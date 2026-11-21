package com.racasmod.race;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Condições em que um efeito (vantagem ou fraqueza) fica ativo. */
public enum When {
    ALWAYS,
    DAY,          // dia (overworld)
    NIGHT,        // noite (e também Nether/End)
    SUNLIT,       // sob o sol direto
    SUNLIT_BARE,  // sob o sol direto e SEM capacete
    WET,          // na água ou na chuva
    DRY,          // fora da água e da chuva
    IN_WATER,     // dentro da água
    COLD,         // bioma frio
    HOT,          // bioma quente ou Nether
    NETHER,
    OVERWORLD,
    UNDERGROUND,  // em cavernas
    LOW_HP,       // menos de 50% de vida
    SNEAK,        // agachado
    BURNING,      // pegando fogo
    GRASS;        // em cima de grama

    public boolean test(Ctx c) {
        return switch (this) {
            case ALWAYS -> true;
            case DAY -> c.day();
            case NIGHT -> !c.day();
            case SUNLIT -> c.sunlit();
            case SUNLIT_BARE -> c.sunlitBare();
            case WET -> c.wet();
            case DRY -> !c.wet();
            case IN_WATER -> c.inWater();
            case COLD -> c.cold();
            case HOT -> c.hot();
            case NETHER -> c.nether();
            case OVERWORLD -> !c.nether();
            case UNDERGROUND -> c.underground();
            case LOW_HP -> c.lowHp();
            case SNEAK -> c.sneaking();
            case BURNING -> c.burning();
            case GRASS -> c.grass();
        };
    }

    public record Ctx(boolean day, boolean sunlit, boolean sunlitBare, boolean wet, boolean inWater,
                      boolean cold, boolean hot, boolean nether, boolean underground,
                      boolean lowHp, boolean sneaking, boolean burning, boolean grass) {

        public static Ctx of(ServerPlayerEntity p) {
            ServerWorld w = p.getServerWorld();
            BlockPos eye = BlockPos.ofFloored(p.getX(), p.getEyeY(), p.getZ());
            BlockPos feet = p.getBlockPos();
            boolean nether = w.getRegistryKey() == World.NETHER;
            boolean day = w.isDay();
            boolean sky = w.isSkyVisible(eye);
            boolean inWater = p.isTouchingWater();
            boolean sunlit = day && sky && !w.isRaining() && !inWater;
            boolean bare = sunlit && p.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
            boolean wet = inWater || w.hasRain(eye);
            float temp = w.getBiome(feet).value().getTemperature();
            boolean cold = temp < 0.2f;
            boolean hot = nether || temp >= 1.5f;
            boolean underground = !sky && p.getY() < 55;
            boolean lowHp = p.getHealth() < p.getMaxHealth() * 0.5f;
            boolean grass = w.getBlockState(feet.down()).isOf(Blocks.GRASS_BLOCK);
            return new Ctx(day, sunlit, bare, wet, inWater, cold, hot, nether, underground,
                    lowHp, p.isSneaking(), p.isOnFire(), grass);
        }
    }
}
