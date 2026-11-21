package com.racasmod.race;

import com.racasmod.net.Payloads;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Toda a lógica de jogo das raças (servidor). */
public final class RaceManager {
    private static final Map<UUID, Map<Ability, Long>> cooldowns = new HashMap<>();
    private static final Map<UUID, Integer> selected = new HashMap<>();
    private static final Map<UUID, Long> counterUntil = new HashMap<>();
    private static final Map<UUID, Checkpoint> checkpoints = new HashMap<>();
    private static boolean reflecting = false;
    private static final Identifier TITAN_ID = Identifier.of("racas", "scale_titan");

    private record Checkpoint(RegistryKey<World> world, Vec3d pos) {}

    /** Tarefa agendada para alguns ticks no futuro (golpes em sequência, domínio etc.). */
    private static final class Task {
        int left;
        final Runnable run;
        Task(int left, Runnable run) { this.left = left; this.run = run; }
    }
    private static final List<Task> tasks = new ArrayList<>();
    private static final Map<UUID, Long> reviveCooldowns = new HashMap<>();
    private static final Set<UUID> allowChange = new HashSet<>();

    // ------------------------------------------------------------ consultas
    public static Race raceOf(ServerPlayerEntity p) {
        String id = RaceState.get(p.server).races.get(p.getUuid());
        return id == null ? null : Races.get(id);
    }

    public static Race mimicOf(ServerPlayerEntity p, Race r) {
        if (r == null || !r.mimic) return null;
        String id = RaceState.get(p.server).mimics.get(p.getUuid());
        return id == null ? null : Mimics.get(id);
    }

    public static void allowChange(UUID id) { allowChange.add(id); }

    // ------------------------------------------------------------ escolha
    public static void choose(ServerPlayerEntity p, String id) {
        Race r = Races.get(id);
        if (r == null) return;
        RaceState st = RaceState.get(p.server);
        boolean has = st.races.containsKey(p.getUuid());
        if (has && !allowChange.remove(p.getUuid())) {
            p.sendMessage(Text.literal("Você já escolheu sua raça!").formatted(Formatting.RED), true);
            return;
        }
        set(p, r);
    }

    public static void set(ServerPlayerEntity p, Race r) {
        RaceState st = RaceState.get(p.server);
        st.races.put(p.getUuid(), r.id);
        if (!r.mimic) st.mimics.remove(p.getUuid());
        st.markDirty();
        cooldowns.remove(p.getUuid());
        selected.remove(p.getUuid());
        applyAttributes(p);
        giveStarterItem(p);
        p.setHealth(p.getMaxHealth());
        p.getHungerManager().setFoodLevel(20);

        p.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 70, 20));
        p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(r.desc).formatted(Formatting.GRAY)));
        p.networkHandler.sendPacket(new TitleS2CPacket(
                Text.literal(r.name).styled(s -> s.withColor(r.color).withBold(true))));
        p.server.getPlayerManager().broadcast(
                Text.literal(p.getName().getString() + " agora é ")
                        .append(Text.literal(r.name).styled(s -> s.withColor(r.color).withBold(true))), false);
        p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);

        if (r.mimic) ServerPlayNetworking.send(p, new Payloads.OpenMenuPayload(1));
    }

    public static void chooseMimic(ServerPlayerEntity p, String id) {
        Race r = raceOf(p);
        Race m = Mimics.get(id);
        if (r == null || !r.mimic || m == null || m.mimic) return;
        RaceState st = RaceState.get(p.server);
        st.mimics.put(p.getUuid(), m.id);
        st.markDirty();
        applyAttributes(p);
        p.sendMessage(Text.literal("Você copiou o poder de: ")
                .append(Text.literal(m.name).styled(s -> s.withColor(m.color).withBold(true))), true);
        p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 0.6f);
        p.getServerWorld().spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, p.getX(), p.getY() + 1, p.getZ(), 40, 0.4, 0.8, 0.4, 0.05);
    }

    // ------------------------------------------------------------ atributos
    public static void applyAttributes(ServerPlayerEntity p) {
        for (Attr a : Attr.values()) {
            EntityAttributeInstance inst = p.getAttributeInstance(a.entry);
            if (inst == null) continue;
            inst.removeModifier(a.id("r"));
            inst.removeModifier(a.id("m"));
        }
        EntityAttributeInstance sc = p.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (sc != null) sc.removeModifier(TITAN_ID); // some a forma de titã
        Race r = raceOf(p);
        if (r == null) return;
        addMods(p, r, "r");
        Race m = mimicOf(p, r);
        if (m != null) addMods(p, m, "m");
        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
    }

    private static void addMods(ServerPlayerEntity p, Race r, String src) {
        for (Race.AttrMod mod : r.attrs) {
            EntityAttributeInstance inst = p.getAttributeInstance(mod.attr().entry);
            if (inst == null) continue;
            inst.addTemporaryModifier(new EntityAttributeModifier(mod.attr().id(src), mod.value(), mod.attr().op));
        }
    }

    // ------------------------------------------------------------ tick
    public static void tick(ServerPlayerEntity p, int serverTicks) {
        if (serverTicks % 10 != 0 || !p.isAlive() || p.isSpectator()) return;
        Race r = raceOf(p);
        if (r == null) return;
        Race m = mimicOf(p, r);
        When.Ctx c = When.Ctx.of(p);

        if ((r.checkpoint || (m != null && m.checkpoint)) && serverTicks % 400 == 0
                && p.isOnGround() && !p.isOnFire() && p.getHealth() >= p.getMaxHealth() * 0.6f) {
            checkpoints.put(p.getUuid(), new Checkpoint(p.getServerWorld().getRegistryKey(), p.getPos()));
        }

        applyFx(p, r, c);
        if (m != null) applyFx(p, m, c);

        if (serverTicks % 20 == 0 && !p.isCreative()) {
            hurt(p, r, c);
            if (m != null) hurt(p, m, c);
            float hunger = r.hunger + (m != null ? m.hunger : 0f);
            if (hunger > 0f) p.addExhaustion(hunger);
        }
    }

    private static void applyFx(ServerPlayerEntity p, Race r, When.Ctx c) {
        for (Race.Fx f : r.fxs) {
            if (!f.when().test(c)) continue;
            int dur = f.effect() == StatusEffects.NIGHT_VISION ? 260 : 50;
            p.addStatusEffect(new StatusEffectInstance(f.effect(), dur, f.amp(), true, false, true));
        }
    }

    private static void hurt(ServerPlayerEntity p, Race r, When.Ctx c) {
        for (Race.Hurt h : r.hurts) {
            if (!h.when().test(c)) continue;
            if (h.fire()) p.setOnFireFor(3);
            else p.damage(p.getServerWorld().getDamageSources().generic(), h.amount());
        }
    }

    // ------------------------------------------------------------ eventos de combate
    public static void onDamageDealt(LivingEntity victim, ServerPlayerEntity attacker, float damage) {
        Race r = raceOf(attacker);
        if (r == null) return;
        Race m = mimicOf(attacker, r);
        float steal = r.lifesteal + (m != null ? m.lifesteal : 0f);
        if (steal > 0f && damage > 0f) attacker.heal(damage * steal);
    }

    /** Volta da morte (Fênix, Subaru, Alucard, Kobeni...). Retorna true se reviveu. */
    public static boolean tryRevive(ServerPlayerEntity p) {
        Race r = raceOf(p);
        if (r == null) return false;
        Race m = mimicOf(p, r);
        Race src = r.revive ? r : (m != null && m.revive ? m : null);
        if (src == null) return false;
        ServerWorld w = p.getServerWorld();
        long now = w.getTime();
        Long ready = reviveCooldowns.get(p.getUuid());
        if (ready != null && ready > now) return false;
        reviveCooldowns.put(p.getUuid(), now + src.reviveTicks);
        p.setHealth(Math.min(src.reviveHealth, p.getMaxHealth()));
        p.extinguish();
        p.fallDistance = 0;
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
        String msg = "Você renasceu das cinzas!";
        if (src.checkpoint) {
            Checkpoint cp = checkpoints.get(p.getUuid());
            if (cp != null && cp.world().equals(w.getRegistryKey())) {
                p.requestTeleport(cp.pos().x, cp.pos().y, cp.pos().z);
            }
            msg = "Retorno pela Morte!";
        }
        w.spawnParticles(ParticleTypes.FLAME, p.getX(), p.getY() + 1, p.getZ(), 80, 0.6, 1.0, 0.6, 0.1);
        w.playSound(null, p.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1f, 1f);
        p.sendMessage(Text.literal(msg).formatted(Formatting.GOLD), true);
        return true;
    }

    /** Dano recebido: Contra-Ataque Total (Meliodas) e esquiva (Kobeni). Retorna false para cancelar o dano. */
    public static boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (reflecting || !(entity instanceof ServerPlayerEntity p)) return true;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;
        Race r = raceOf(p);
        if (r == null) return true;
        Race m = mimicOf(p, r);
        ServerWorld w = p.getServerWorld();

        Long until = counterUntil.get(p.getUuid());
        if (until != null && until > w.getTime() && source.getAttacker() instanceof LivingEntity att && att != p) {
            reflecting = true;
            try {
                att.damage(w.getDamageSources().playerAttack(p), Math.max(amount, 1f) * 2f);
            } finally {
                reflecting = false;
            }
            w.spawnParticles(ParticleTypes.CRIT, p.getX(), p.getY() + 1, p.getZ(), 25, 0.5, 0.8, 0.5, 0.2);
            return false;
        }

        float dodge = r.dodge + (m != null ? m.dodge : 0f);
        if (dodge > 0f && p.getRandom().nextFloat() < dodge) {
            w.spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 1, p.getZ(), 12, 0.4, 0.6, 0.4, 0.05);
            p.sendMessage(Text.literal("Desviou!").formatted(Formatting.LIGHT_PURPLE), true);
            return false;
        }
        return true;
    }

    /** Executa as tarefas agendadas (chamado todo tick do servidor). */
    public static void runTasks() {
        if (tasks.isEmpty()) return;
        List<Runnable> ready = new ArrayList<>();
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (--t.left <= 0) {
                ready.add(t.run);
                it.remove();
            }
        }
        for (Runnable r : ready) {
            try { r.run(); } catch (RuntimeException ignored) { }
        }
    }

    private static void later(int ticks, Runnable r) {
        tasks.add(new Task(Math.max(1, ticks), r));
    }

    /** Entrega o item inicial da raça (ex.: a faca da Kobeni) se o jogador ainda não tiver. */
    public static void giveStarterItem(ServerPlayerEntity p) {
        Race r = raceOf(p);
        if (r == null || r.giveItem == null) return;
        Item item = r.giveItem;
        for (int i = 0; i < p.getInventory().size(); i++) {
            if (p.getInventory().getStack(i).isOf(item)) return;
        }
        p.giveItemStack(new net.minecraft.item.ItemStack(item));
    }

    // ------------------------------------------------------------ habilidades
    public static void action(ServerPlayerEntity p, int action) {
        Race r = raceOf(p);
        if (r == null) return;
        if (action == 1) {
            if (r.mimic) ServerPlayNetworking.send(p, new Payloads.OpenMenuPayload(1));
            else p.sendMessage(Text.literal("Só o Herobrine copia poderes!").formatted(Formatting.RED), true);
            return;
        }
        useAbility(p, r);
    }

    private static void useAbility(ServerPlayerEntity p, Race r) {
        Race m = mimicOf(p, r);
        List<Ability> list = (m != null && m.ability != Ability.NONE) ? m.abilities() : r.abilities();
        if (list.isEmpty()) {
            p.sendMessage(Text.literal("Sua raça não tem habilidade ativa.").formatted(Formatting.GRAY), true);
            return;
        }
        // Agachado + R: troca a habilidade selecionada
        if (p.isSneaking() && list.size() > 1) {
            int next = (selected.getOrDefault(p.getUuid(), 0) + 1) % list.size();
            selected.put(p.getUuid(), next);
            Ability n = list.get(next);
            p.sendMessage(Text.literal("Habilidade: " + n.title + " (" + n.cooldownSeconds + "s)")
                    .formatted(Formatting.AQUA), true);
            return;
        }
        Ability a = list.get(Math.min(selected.getOrDefault(p.getUuid(), 0), list.size() - 1));
        long now = p.getServerWorld().getTime();
        Map<Ability, Long> cd = cooldowns.computeIfAbsent(p.getUuid(), k -> new EnumMap<>(Ability.class));
        Long ready = cd.get(a);
        if (ready != null && ready > now) {
            long left = (ready - now + 19) / 20;
            p.sendMessage(Text.literal(a.title + ": recarregando... " + left + "s").formatted(Formatting.RED), true);
            return;
        }
        cd.put(a, now + a.cooldownSeconds * 20L);
        if (!run(p, a)) {
            cd.remove(a); // errou o alvo: não gasta a recarga
            p.sendMessage(Text.literal(a.title + ": nenhum alvo!").formatted(Formatting.GRAY), true);
            return;
        }
        p.sendMessage(Text.literal(a.title + "!").formatted(Formatting.GOLD), true);
    }

    /** Executa a habilidade. Retorna false se não havia alvo (a recarga não é gasta). */
    private static boolean run(ServerPlayerEntity p, Ability a) {
        ServerWorld w = p.getServerWorld();
        Vec3d look = p.getRotationVec(1f);

        switch (a) {
            // ================================================== habilidades gerais
            case TELEPORT -> {
                HitResult h = p.raycast(16, 1f, false);
                Vec3d t = h.getPos();
                if (h.getType() == HitResult.Type.BLOCK) t = t.subtract(look.multiply(0.7));
                double fy = t.y - 1.6;
                for (int dy = 0; dy <= 4; dy++) {
                    Box moved = p.getBoundingBox().offset(t.x - p.getX(), fy + dy - p.getY(), t.z - p.getZ());
                    if (w.isSpaceEmpty(p, moved)) {
                        poof(w, p.getPos());
                        p.requestTeleport(t.x, fy + dy, t.z);
                        poof(w, p.getPos());
                        sound(w, p, SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f);
                        break;
                    }
                }
            }
            case DASH -> {
                p.setVelocity(look.x * 1.7, 0.25, look.z * 1.7);
                p.velocityModified = true;
                p.fallDistance = 0;
                w.spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 0.5, p.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
                sound(w, p, SoundEvents.ENTITY_BAT_TAKEOFF, 1f);
            }
            case LEAP -> {
                Vec3d v = p.getVelocity();
                p.setVelocity(v.x + look.x * 0.4, 1.1, v.z + look.z * 0.4);
                p.velocityModified = true;
                p.fallDistance = 0;
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 4, true, false, false));
                sound(w, p, SoundEvents.ENTITY_SLIME_JUMP, 1f);
            }
            case EXPLODE -> {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 4, true, false, false));
                w.createExplosion(p, p.getX(), p.getY(), p.getZ(), 3.0f, World.ExplosionSourceType.NONE);
            }
            case FIRE_BREATH -> {
                for (int i = 1; i <= 8; i++) {
                    Vec3d pt = p.getEyePos().add(look.multiply(i));
                    w.spawnParticles(ParticleTypes.FLAME, pt.x, pt.y, pt.z, 4, 0.25, 0.25, 0.25, 0.02);
                }
                for (LivingEntity e : targets(p, 8)) {
                    if (inCone(p, e, 0.7)) {
                        e.setOnFireFor(6);
                        e.damage(w.getDamageSources().playerAttack(p), 4f);
                    }
                }
                sound(w, p, SoundEvents.ENTITY_BLAZE_SHOOT, 1f);
            }
            case SHOCK -> {
                LivingEntity best = nearestInCone(p, 32, 0.97);
                Vec3d pos = best != null ? best.getPos() : p.raycast(40, 1f, false).getPos();
                bolt(w, pos);
            }
            case FREEZE -> {
                for (LivingEntity e : targets(p, 6)) {
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 3));
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
                    w.spawnParticles(ParticleTypes.SNOWFLAKE, e.getX(), e.getY() + 1, e.getZ(), 25, 0.4, 0.6, 0.4, 0.02);
                }
                w.spawnParticles(ParticleTypes.SNOWFLAKE, p.getX(), p.getY() + 1, p.getZ(), 60, 3, 0.5, 3, 0.05);
                sound(w, p, SoundEvents.BLOCK_GLASS_BREAK, 0.8f);
            }
            case HEAL -> {
                p.heal(8f);
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
                for (ServerPlayerEntity other : w.getPlayers(x -> x != p && x.squaredDistanceTo(p) <= 64)) {
                    other.heal(6f);
                    w.spawnParticles(ParticleTypes.HEART, other.getX(), other.getY() + 1.5, other.getZ(), 8, 0.3, 0.3, 0.3, 0.02);
           
