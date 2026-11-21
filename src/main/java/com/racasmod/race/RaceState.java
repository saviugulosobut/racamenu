package com.racasmod.race;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

/** Salva a raça de cada jogador no mundo. */
public class RaceState extends PersistentState {
    public final Map<UUID, String> races = new HashMap<>();
    public final Map<UUID, String> mimics = new HashMap<>();
    public boolean timeSet = false;

    private static final PersistentState.Type<RaceState> TYPE =
            new PersistentState.Type<>(RaceState::new, RaceState::load, null);

    public static RaceState load(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        RaceState s = new RaceState();
        NbtCompound r = nbt.getCompound("races");
        for (String k : r.getKeys()) s.races.put(UUID.fromString(k), r.getString(k));
        NbtCompound m = nbt.getCompound("mimics");
        for (String k : m.getKeys()) s.mimics.put(UUID.fromString(k), m.getString(k));
        s.timeSet = nbt.getBoolean("timeSet");
        return s;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        NbtCompound r = new NbtCompound();
        races.forEach((k, v) -> r.putString(k.toString(), v));
        NbtCompound m = new NbtCompound();
        mimics.forEach((k, v) -> m.putString(k.toString(), v));
        nbt.put("races", r);
        nbt.put("mimics", m);
        nbt.putBoolean("timeSet", timeSet);
        return nbt;
    }

    public static RaceState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, "racas_state");
    }
}
