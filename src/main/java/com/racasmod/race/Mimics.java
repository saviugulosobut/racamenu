package com.racasmod.race;

import static com.racasmod.race.Ability.*;
import static com.racasmod.race.When.*;
import static net.minecraft.entity.effect.StatusEffects.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Items;

/** Poderes de mobs que o Herobrine pode copiar. */
public final class Mimics {
    private static final Map<String, Race> MAP = new LinkedHashMap<>();

    private static void add(Race r) { MAP.put(r.id, r); }

    public static Race get(String id) { ensure(); return id == null ? null : MAP.get(id); }
    public static List<Race> all() { ensure(); return new ArrayList<>(MAP.values()); }

    private static void ensure() {
        if (!MAP.isEmpty()) return;

        add(Race.of("creeper", "Creeper", Items.GUNPOWDER, 0x4CAF50, "Sssss... BOOM.")
                .ability(EXPLODE)
                .perk("Explosão ao seu redor (R), sem quebrar blocos"));

        add(Race.of("enderman", "Enderman", Items.ENDER_EYE, 0x7B1FA2, "Teleporte e longas pernas.")
                .ability(TELEPORT).scale(0.1).hurt(WET, 1.0f)
                .perk("Teleporte onde você olha (R)")
                .flaw("Água e chuva machucam"));

        add(Race.of("blaze", "Blaze", Items.BLAZE_ROD, 0xFFA000, "Chamas vivas do Nether.")
                .ability(FIRE_BREATH).fx(ALWAYS, FIRE_RESISTANCE, 0).hurt(WET, 1.0f)
                .perk("Sopro de fogo (R) e imunidade a fogo")
                .flaw("Água e chuva machucam"));

        add(Race.of("skeleton", "Esqueleto", Items.SKELETON_SKULL, 0xE0E0E0, "Ossos rápidos e sem dó.")
                .ability(DASH).speed(0.15).jump(0.1).burn(SUNLIT_BARE)
                .perk("Dash (R), mais rápido e pula mais")
                .flaw("Pega fogo sob o sol (capacete protege)"));

        add(Race.of("spider", "Aranha", Items.COBWEB, 0xB71C1C, "Teias e escuridão.")
                .ability(FREEZE).fx(NIGHT, NIGHT_VISION, 0).step(0.4).speed(0.1)
                .perk("Teia prende inimigos (R), sobe blocos e vê no escuro"));

        add(Race.of("zombie", "Zumbi", Items.ZOMBIE_HEAD, 0x4E7A3A, "Não para, não sente.")
                .ability(RAGE).fx(LOW_HP, REGENERATION, 0).kb(0.2).burn(SUNLIT_BARE)
                .perk("Fúria (R) e regeneração com pouca vida")
                .flaw("Pega fogo sob o sol (capacete protege)"));

        add(Race.of("wither_skeleton", "Esqueleto Wither", Items.WITHER_SKELETON_SKULL, 0x37474F, "A morte em pessoa.")
                .ability(WITHER_GAZE).dmg(2).fx(ALWAYS, FIRE_RESISTANCE, 0)
                .perk("Olhar sombrio (R), +2 de dano e imune a fogo"));

        add(Race.of("iron_golem", "Golem de Ferro", Items.IRON_BLOCK, 0xB0BEC5, "Proteção pesada.")
                .ability(SCREAM).armor(6).hp(10).kb(1.0).speed(-0.15)
                .perk("+5 corações, +6 de armadura, sem knockback")
                .flaw("15% mais lento"));

        add(Race.of("ghast", "Ghast", Items.GHAST_TEAR, 0xECEFF1, "Flutua e chora fogo.")
                .ability(FIRE_BREATH).fx(ALWAYS, SLOW_FALLING, 0).hp(-2)
                .perk("Fogo (R) e queda lenta")
                .flaw("-1 coração"));

        add(Race.of("phantom", "Phantom", Items.PHANTOM_MEMBRANE, 0x5C6BC0, "Caçador dos céus noturnos.")
                .ability(LEAP).fx(ALWAYS, SLOW_FALLING, 0).fx(NIGHT, NIGHT_VISION, 0).fx(NIGHT, SPEED, 1).burn(SUNLIT_BARE)
                .perk("Super salto (R), queda lenta; à noite: Velocidade II")
                .flaw("Pega fogo sob o sol (capacete protege)"));

        add(Race.of("guardian", "Guardião", Items.PRISMARINE_SHARD, 0x26A69A, "Raio do oceano.")
                .ability(SHOCK).fx(ALWAYS, WATER_BREATHING, 0).fx(IN_WATER, DOLPHINS_GRACE, 0).fx(DRY, SLOWNESS, 0)
                .perk("Raio (R), respira na água e nada rápido")
                .flaw("Fora da água: Lentidão"));

        add(Race.of("witch", "Bruxa", Items.SPLASH_POTION, 0x6A1B9A, "Poções e sorte.")
                .ability(HEAL).luck(2).fx(LOW_HP, ABSORPTION, 1)
                .perk("Cura (R), sorte e escudo com pouca vida"));

        add(Race.of("ravager", "Ravager", Items.RAVAGER_SPAWN_EGG, 0x795548, "Um touro de guerra.")
                .ability(SCREAM).dmg(4).hp(8).kb(0.5).speed(-0.1)
                .perk("Grito (R), +4 de dano e +4 corações")
                .flaw("10% mais lento"));

        add(Race.of("warden", "Warden", Items.SCULK_SHRIEKER, 0x006064, "O terror das profundezas.")
                .ability(SCREAM).hp(16).dmg(5).fx(ALWAYS, NIGHT_VISION, 0).speed(-0.1).hunger(0.6f)
                .perk("+8 corações, +5 de dano e grito sônico (R)")
                .flaw("Lento e faminto"));
    }

    private Mimics() {}
}
