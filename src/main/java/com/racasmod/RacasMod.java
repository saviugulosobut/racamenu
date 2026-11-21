package com.racasmod;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.racasmod.net.Payloads;
import com.racasmod.race.Race;
import com.racasmod.race.RaceManager;
import com.racasmod.race.RaceState;
import com.racasmod.race.Races;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class RacasMod implements ModInitializer {
    public static final String MOD_ID = "racas";

    /** Horário em que o mundo novo começa: 13000 = anoitecer. */
    public static final long START_TIME = 13000L;

    private final Map<UUID, Integer> pendingMenu = new HashMap<>();

    @Override
    public void onInitialize() {
        Races.all(); // garante que tudo carrega

        // ---- pacotes
        PayloadTypeRegistry.playC2S().register(Payloads.ChoosePayload.ID, Payloads.ChoosePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(Payloads.MimicPayload.ID, Payloads.MimicPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(Payloads.ActionPayload.ID, Payloads.ActionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.OpenMenuPayload.ID, Payloads.OpenMenuPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(Payloads.ChoosePayload.ID,
                (payload, ctx) -> RaceManager.choose(ctx.player(), payload.id()));
        ServerPlayNetworking.registerGlobalReceiver(Payloads.MimicPayload.ID,
                (payload, ctx) -> RaceManager.chooseMimic(ctx.player(), payload.id()));
        ServerPlayNetworking.registerGlobalReceiver(Payloads.ActionPayload.ID,
                (payload, ctx) -> RaceManager.action(ctx.player(), payload.action()));

        // ---- mundo novo começa de noite
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            RaceState st = RaceState.get(server);
            if (!st.timeSet) {
                server.getOverworld().setTimeOfDay(START_TIME);
                st.timeSet = true;
                st.markDirty();
            }
        });

        // ---- entrar no mundo: aplica atributos / abre o menu se ainda não escolheu
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity p = handler.getPlayer();
            RaceManager.applyAttributes(p);
            if (RaceManager.raceOf(p) == null) pendingMenu.put(p.getUuid(), 40);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> RaceManager.applyAttributes(newPlayer));

        // ---- tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int t = server.getTicks();
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                RaceManager.tick(p, t);
                Integer left = pendingMenu.get(p.getUuid());
                if (left != null) {
                    if (left <= 1) {
                        pendingMenu.remove(p.getUuid());
                        if (RaceManager.raceOf(p) == null) ServerPlayNetworking.send(p, new Payloads.OpenMenuPayload(0));
                    } else {
                        pendingMenu.put(p.getUuid(), left - 1);
                    }
                }
            }
        });

        // ---- combate
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (!blocked && source.getAttacker() instanceof ServerPlayerEntity attacker) {
                RaceManager.onDamageDealt(entity, attacker, damageTaken);
            }
        });
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity p && RaceManager.tryRevive(p)) return false;
            return true;
        });

        // ---- comandos (op): /raca menu | /raca set <raça>
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("raca")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.literal("menu").executes(ctx -> {
                            ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                            RaceManager.allowChange(p.getUuid());
                            ServerPlayNetworking.send(p, new Payloads.OpenMenuPayload(2));
                            return 1;
                        }))
                        .then(CommandManager.literal("set").then(CommandManager.argument("id", StringArgumentType.word())
                                .suggests((c, b) -> CommandSource.suggestMatching(Races.ids(), b))
                                .executes(ctx -> {
                                    ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                                    Race r = Races.get(StringArgumentType.getString(ctx, "id"));
                                    if (r == null) {
                                        ctx.getSource().sendError(Text.literal("Raça não encontrada."));
                                        return 0;
                                    }
                                    RaceManager.set(p, r);
                                    return 1;
                                })))));
    }
  }
