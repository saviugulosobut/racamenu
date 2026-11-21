package com.racasmod.client;

import com.racasmod.net.Payloads;
import com.racasmod.race.Mimics;
import com.racasmod.race.Races;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class RacasClient implements ClientModInitializer {
    private static KeyBinding abilityKey;
    private static KeyBinding mimicKey;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Payloads.OpenMenuPayload.ID, (payload, ctx) -> {
            switch (payload.type()) {
                case 1 -> ctx.client().setScreen(new ChoiceScreen(Mimics.all(), true, true));
                case 2 -> ctx.client().setScreen(new ChoiceScreen(Races.all(), false, true));
                default -> ctx.client().setScreen(new ChoiceScreen(Races.all(), false, false));
            }
        });

        abilityKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.racas.ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.racas"));
        mimicKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.racas.mimic", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.racas"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (abilityKey.wasPressed()) {
                if (mc.player != null) ClientPlayNetworking.send(new Payloads.ActionPayload(0));
            }
            while (mimicKey.wasPressed()) {
                if (mc.player != null) ClientPlayNetworking.send(new Payloads.ActionPayload(1));
            }
        });
    }
}
