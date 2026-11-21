package com.racasmod.net;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class Payloads {

    /** Cliente -> servidor: escolheu uma raça. */
    public record ChoosePayload(String id) implements CustomPayload {
        public static final CustomPayload.Id<ChoosePayload> ID = new CustomPayload.Id<>(Identifier.of("racas", "choose"));
        public static final PacketCodec<RegistryByteBuf, ChoosePayload> CODEC =
                PacketCodec.tuple(PacketCodecs.STRING, ChoosePayload::id, ChoosePayload::new);
        @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    /** Cliente -> servidor: Herobrine escolheu um mob para copiar. */
    public record MimicPayload(String id) implements CustomPayload {
        public static final CustomPayload.Id<MimicPayload> ID = new CustomPayload.Id<>(Identifier.of("racas", "mimic"));
        public static final PacketCodec<RegistryByteBuf, MimicPayload> CODEC =
                PacketCodec.tuple(PacketCodecs.STRING, MimicPayload::id, MimicPayload::new);
        @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    /** Cliente -> servidor: 0 = usar habilidade, 1 = abrir menu de mobs (Herobrine). */
    public record ActionPayload(int action) implements CustomPayload {
        public static final CustomPayload.Id<ActionPayload> ID = new CustomPayload.Id<>(Identifier.of("racas", "action"));
        public static final PacketCodec<RegistryByteBuf, ActionPayload> CODEC =
                PacketCodec.tuple(PacketCodecs.VAR_INT, ActionPayload::action, ActionPayload::new);
        @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    /** Servidor -> cliente: 0 = menu de raças (obrigatório), 1 = menu de mobs, 2 = menu de raças (pode fechar). */
    public record OpenMenuPayload(int type) implements CustomPayload {
        public static final CustomPayload.Id<OpenMenuPayload> ID = new CustomPayload.Id<>(Identifier.of("racas", "open_menu"));
        public static final PacketCodec<RegistryByteBuf, OpenMenuPayload> CODEC =
                PacketCodec.tuple(PacketCodecs.VAR_INT, OpenMenuPayload::type, OpenMenuPayload::new);
        @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    private Payloads() {}
}
