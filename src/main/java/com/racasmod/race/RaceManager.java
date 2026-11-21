package com.racasmod.race;

import com.racasmod.net.Payloads;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Toda a lógica de jogo das raças (servidor). */
public final class RaceManager {
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
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
        applyAttributes(p);
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

    /** Fênix: volta da morte. Retorna true se reviveu. */
    public static boolean tryRevive(ServerPlayerEntity p) {
        Race r = raceOf(p);
        if (r == null) return false;
        Race m = mimicOf(p, r);
        if (!r.revive && (m == null || !m.revive)) return false;
        ServerWorld w = p.getServerWorld();
        long now = w.getTime();
        Long ready = reviveCooldowns.get(p.getUuid());
        if (ready != null && ready > now) return false;
        reviveCooldowns.put(p.getUuid(), now + 6000L);
        p.setHealth(8f);
        p.extinguish();
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
        w.spawnParticles(ParticleTypes.FLAME, p.getX(), p.getY() + 1, p.getZ(), 80, 0.6, 1.0, 0.6, 0.1);
        w.playSound(null, p.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1f, 1f);
        p.sendMessage(Text.literal("Você renasceu das cinzas!").formatted(Formatting.GOLD), true);
        return true;
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
        Ability a = (m != null && m.ability != Ability.NONE) ? m.ability : r.ability;
        if (a == Ability.NONE) {
            p.sendMessage(Text.literal("Sua raça não tem habilidade ativa.").formatted(Formatting.GRAY), true);
            return;
        }
        long now = p.getServerWorld().getTime();
        Long ready = cooldowns.get(p.getUuid());
        if (ready != null && ready > now) {
            long left = (ready - now + 19) / 20;
            p.sendMessage(Text.literal("Recarregando... " + left + "s").formatted(Formatting.RED), true);
            return;
        }
        cooldowns.put(p.getUuid(), now + a.cooldownSeconds * 20L);
        run(p, a);
        p.sendMessage(Text.literal(a.title + "!").formatted(Formatting.GOLD), true);
    }

    private static void run(ServerPlayerEntity p, Ability a) {
        ServerWorld w = p.getServerWorld();
        Vec3d look = p.getRotationVec(1f);

        switch (a) {
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
                Vec3d pos = null;
                LivingEntity best = null;
                double bestDist = 1e9;
                for (LivingEntity e : targets(p, 32)) {
                    double d = e.squaredDistanceTo(p);
                    if (inCone(p, e, 0.97) && d < bestDist) { best = e; bestDist = d; }
                }
                if (best != null) pos = best.getPos();
                else pos = p.raycast(40, 1f, false).getPos();
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(w);
                if (bolt != null) {
                    bolt.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
                    w.spawnEntity(bolt);
                }
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
                for (ServerPlayerEntity o : w.getPlayers(o -> o != p && o.squaredDistanceTo(p) <= 64)) {
                    o.heal(6f);
                    w.spawnParticles(ParticleTypes.HEART, o.getX(), o.getY() + 1.5, o.getZ(), 8, 0.3, 0.3, 0.3, 0.02);
                }
                w.spawnParticles(ParticleTypes.HEART, p.getX(), p.getY() + 1.5, p.getZ(), 10, 0.4, 0.4, 0.4, 0.02);
                sound(w, p, SoundEvents.ENTITY_PLAYER_LEVELUP, 1f);
            }
            case RAGE -> {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0));
                w.spawnParticles(ParticleTypes.ANGRY_VILLAGER, p.getX(), p.getY() + 1.8, p.getZ(), 10, 0.4, 0.3, 0.4, 0.02);
                sound(w, p, SoundEvents.ENTITY_RAVAGER_ROAR, 1f);
            }
            case VANISH -> {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 160, 0));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 160, 0));
                poof(w, p.getPos());
                sound(w, p, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, 1f);
            }
            case SCREAM -> {
                for (LivingEntity e : targets(p, 6)) {
                    Vec3d dir = e.getPos().subtract(p.getPos()).normalize();
                    e.damage(w.getDamageSources().playerAttack(p), 4f);
                    e.addVelocity(dir.x * 1.2, 0.5, dir.z * 1.2);
                    e.velocityModified = true;
                }
                w.spawnParticles(ParticleTypes.SONIC_BOOM, p.getX(), p.getY() + 1, p.getZ(), 1, 0, 0, 0, 0);
                sound(w, p, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 1f);
            }
            case WITHER_GAZE -> {
                for (LivingEntity e : targets(p, 10)) {
                    if (inCone(p, e, 0.7)) {
                        e.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 120, 1));
                        w.spawnParticles(ParticleTypes.SMOKE, e.getX(), e.getY() + 1, e.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
                    }
                }
                sound(w, p, SoundEvents.ENTITY_WITHER_AMBIENT, 0.8f);
            }
            case STOMP -> {
                for (LivingEntity e : targets(p, 5)) {
                    e.damage(w.getDamageSources().playerAttack(p), 5f);
                    e.addVelocity(0, 0.9, 0);
                    e.velocityModified = true;
                }
                w.spawnParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY() + 0.2, p.getZ(), 6, 1.5, 0.1, 1.5, 0.0);
                w.spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 0.2, p.getZ(), 40, 2.0, 0.1, 2.0, 0.1);
                sound(w, p, SoundEvents.ENTITY_RAVAGER_ROAR, 0.6f);
            }
            case BARRIER -> {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 3));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 120, 2));
                w.spawnParticles(ParticleTypes.END_ROD, p.getX(), p.getY() + 1, p.getZ(), 30, 0.5, 0.8, 0.5, 0.02);
                sound(w, p, SoundEvents.ITEM_TOTEM_USE, 1.4f);
            }
            case VENOM -> {
                for (LivingEntity e : targets(p, 10)) {
                    if (inCone(p, e, 0.7)) {
                        e.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 140, 1));
                        w.spawnParticles(ParticleTypes.WITCH, e.getX(), e.getY() + 1, e.getZ(), 15, 0.3, 0.6, 0.3, 0.02);
                    }
                }
                sound(w, p, SoundEvents.ENTITY_SLIME_JUMP, 0.6f);
            }
            case PULL -> {
                for (LivingEntity e : targets(p, 12)) {
                    Vec3d dir = p.getPos().subtract(e.getPos()).normalize();
                    e.addVelocity(dir.x * 1.3, 0.3, dir.z * 1.3);
                    e.velocityModified = true;
                    w.spawnParticles(ParticleTypes.CRIT, e.getX(), e.getY() + 1, e.getZ(), 10, 0.3, 0.5, 0.3, 0.1);
                }
                sound(w, p, SoundEvents.ENTITY_BAT_TAKEOFF, 0.7f);
            }
            case BLIND -> {
                for (LivingEntity e : targets(p, 8)) {
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0));
                }
                w.spawnParticles(ParticleTypes.END_ROD, p.getX(), p.getY() + 1, p.getZ(), 40, 1.5, 0.8, 1.5, 0.05);
                sound(w, p, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, 1f);
            }
            case LEECH -> {
                float healed = 0f;
                for (LivingEntity e : targets(p, 8)) {
                    if (inCone(p, e, 0.7)) {
                        e.damage(w.getDamageSources().playerAttack(p), 4f);
                        healed += 2f;
                        w.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, e.getX(), e.getY() + 1, e.getZ(), 12, 0.3, 0.6, 0.3, 0.03);
                    }
                }
                if (healed > 0f) p.heal(Math.min(healed, 8f));
                sound(w, p, SoundEvents.ENTITY_WITHER_AMBIENT, 1.4f);
            }
            default -> { }
        }
    }

    // ------------------------------------------------------------ helpers
    private static List<LivingEntity> targets(ServerPlayerEntity p, double range) {
        Box box = p.getBoundingBox().expand(range);
        return p.getServerWorld().getEntitiesByClass(LivingEntity.class, box,
                e -> e != p && e.isAlive()
                        && (e instanceof Monster || (e instanceof PlayerEntity pl && !pl.isSpectator()))
                        && e.squaredDistanceTo(p) <= range * range);
    }

    private static boolean inCone(ServerPlayerEntity p, LivingEntity e, double minDot) {
        Vec3d to = e.getPos().add(0, e.getHeight() / 2, 0).subtract(p.getEyePos());
        if (to.lengthSquared() < 1.0E-4) return true;
        return to.normalize().dotProduct(p.getRotationVec(1f)) >= minDot;
    }

    private static void poof(ServerWorld w, Vec3d pos) {
        w.spawnParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1, pos.z, 30, 0.4, 0.8, 0.4, 0.2);
    }

    private static void sound(ServerWorld w, ServerPlayerEntity p, SoundEvent s, float pitch) {
        w.playSound(null, p.getBlockPos(), s, SoundCategory.PLAYERS, 1f, pitch);
    }

    private RaceManager() {}
                  }
