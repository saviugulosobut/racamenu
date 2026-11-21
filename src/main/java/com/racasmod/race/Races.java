package com.racasmod.race;

import static com.racasmod.race.Ability.*;
import static com.racasmod.race.When.*;
import static net.minecraft.entity.effect.StatusEffects.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Items;

/**
 * Todas as raças. Para criar uma nova, copie um bloco e mude os valores.
 * hp: +2 = 1 coração | speed/jump: 0.2 = +20% | scale: -0.2 = 80% do tamanho
 * fx(QUANDO, EFEITO, nível-1) | burn/hurt = dano em certa condição
 */
public final class Races {
    private static final Map<String, Race> MAP = new LinkedHashMap<>();

    private static void add(Race r) { MAP.put(r.id, r); }

    public static Race get(String id) { ensure(); return id == null ? null : MAP.get(id); }
    public static List<Race> all() { ensure(); return new ArrayList<>(MAP.values()); }
    public static List<String> ids() { ensure(); return new ArrayList<>(MAP.keySet()); }

    private static void ensure() {
        if (!MAP.isEmpty()) return;

        add(Race.of("vampire", "Vampiro", Items.REDSTONE, 0xB0102A,
                "Criatura da noite. Domina as trevas, mas o sol é o seu pior inimigo.")
                .fx(NIGHT, STRENGTH, 1).fx(NIGHT, SPEED, 1).fx(NIGHT, NIGHT_VISION, 0)
                .fx(SUNLIT, WEAKNESS, 1).burn(SUNLIT_BARE).lifesteal(0.25f).ability(DASH)
                .perk("À noite: Força II, Velocidade II e visão noturna")
                .perk("Rouba 25% do dano que causa em vida")
                .flaw("Pega fogo sob o sol (um capacete protege)")
                .flaw("Sob o sol: Fraqueza II"));

        add(Race.of("herobrine", "Herobrine", Items.PLAYER_HEAD, 0x00E5FF,
                "A lenda. Escolha um mob e copie o poder dele. Troque quando quiser com a tecla M.")
                .mimic().hp(-6).fx(ALWAYS, NIGHT_VISION, 0).fx(ALWAYS, STRENGTH, 0).fx(NIGHT, SPEED, 0)
                .ability(SHOCK).hunger(0.3f)
                .perk("Copia o poder de um mob (Creeper, Enderman, Blaze, Warden...)")
                .perk("Visão noturna, Força I e raios nas mãos")
                .flaw("Sozinho é frágil: -3 corações")
                .flaw("Poder emprestado gasta fome"));

        add(Race.of("werewolf", "Lobisomem", Items.BONE, 0x8D6E3F,
                "Fera da lua cheia. Terrível à noite, fraco de dia.")
                .fx(NIGHT, STRENGTH, 1).fx(NIGHT, SPEED, 1).fx(NIGHT, NIGHT_VISION, 0).fx(NIGHT, JUMP_BOOST, 0)
                .fx(DAY, WEAKNESS, 0).fx(DAY, SLOWNESS, 0).ability(LEAP)
                .perk("À noite: Força II, Velocidade II, salto e visão noturna")
                .flaw("De dia: Fraqueza e Lentidão"));

        add(Race.of("merfolk", "Sereiano", Items.NAUTILUS_SHELL, 0x26C6DA,
                "Filho do oceano. Rei da água, perdido em terra seca.")
                .fx(ALWAYS, WATER_BREATHING, 0).fx(IN_WATER, DOLPHINS_GRACE, 0).fx(IN_WATER, NIGHT_VISION, 0)
                .fx(IN_WATER, STRENGTH, 0).fx(DRY, SLOWNESS, 0).ability(HEAL)
                .perk("Respira debaixo d'água e nada como golfinho")
                .perk("Na água: Força e visão noturna")
                .flaw("Fora da água e da chuva: Lentidão"));

        add(Race.of("enderian", "Enderiano", Items.ENDER_PEARL, 0x8E24AA,
                "Alto, estranho e misterioso. Some e aparece onde quiser.")
                .scale(0.15).hurt(WET, 1.0f).ability(TELEPORT).step(0.3)
                .perk("Teleporte onde você olha (R)")
                .perk("Mais alto e sobe blocos")
                .flaw("Água e chuva machucam"));

        add(Race.of("angel", "Anjo", Items.FEATHER, 0xFFF3B0,
                "Pairando sobre o mundo. Leve como pena, frágil como vidro.")
                .fx(ALWAYS, SLOW_FALLING, 0).fall(-1).speed(0.1).hp(-4).ability(LEAP)
                .fx(NETHER, WEAKNESS, 1).fx(NETHER, SLOWNESS, 0)
                .perk("Queda lenta e zero dano de queda")
                .flaw("Frágil: -2 corações")
                .flaw("No Nether: Fraqueza II e Lentidão"));

        add(Race.of("demon", "Demônio", Items.MAGMA_CREAM, 0xD84315,
                "Nascido no fogo. Nada o queima, mas a água o consome.")
                .fx(ALWAYS, FIRE_RESISTANCE, 0).fx(NETHER, STRENGTH, 1).dmg(1).hurt(WET, 1.0f).ability(FIRE_BREATH)
                .perk("Imune a fogo e lava")
                .perk("No Nether: Força II")
                .flaw("Água e chuva queimam"));

        add(Race.of("dwarf", "Anão", Items.IRON_PICKAXE, 0xB8860B,
                "Mestre das minas. Pequeno, teimoso e rápido com a picareta.")
                .scale(-0.2).mine(0.5).speed(-0.1).kb(0.3)
                .fx(UNDERGROUND, NIGHT_VISION, 0).fx(UNDERGROUND, HASTE, 1).fx(SUNLIT, MINING_FATIGUE, 0)
                .perk("Minera 50% mais rápido; nas cavernas ganha Pressa II")
                .perk("Visão noturna nas cavernas e resiste a knockback")
                .flaw("10% mais lento")
                .flaw("Ao sol: Fadiga de mineração"));

        add(Race.of("elf", "Elfo", Items.BOW, 0x66BB6A,
                "Ágil e sortudo. Vive em harmonia com a floresta.")
                .speed(0.15).jump(0.2).luck(1).hp(-2).fx(NIGHT, NIGHT_VISION, 0).fx(GRASS, REGENERATION, 0)
                .fx(NETHER, WEAKNESS, 0).ability(DASH)
                .perk("Rápido, pula alto e tem sorte")
                .perk("Na grama: Regeneração")
                .flaw("-1 coração")
                .flaw("No Nether: Fraqueza"));

        add(Race.of("orc", "Orc", Items.IRON_AXE, 0x558B2F,
                "Brutal e resistente. Vive para a batalha.")
                .dmg(2).hp(6).kb(0.2).speed(-0.08).hunger(0.4f).ability(RAGE)
                .perk("+3 corações e +2 de dano")
                .flaw("8% mais lento")
                .flaw("Fome mais rápida"));

        add(Race.of("golem", "Golem", Items.IRON_BLOCK, 0xB0BEC5,
                "Uma muralha ambulante. Quase imparável, quase imóvel.")
                .armor(6).hp(10).kb(1.0).speed(-0.25).scale(0.15).hunger(0.3f).ability(SCREAM)
                .perk("+5 corações, +6 de armadura e imune a knockback")
                .flaw("25% mais lento")
                .flaw("Fome mais rápida"));

        add(Race.of("fairy", "Fada", Items.GLOWSTONE_DUST, 0xFF80AB,
                "Minúscula e brilhante. Voa baixinho e cura os amigos.")
                .scale(-0.5).hp(-8).speed(0.2).jump(0.3).fall(-1).dmg(-1).fx(ALWAYS, SLOW_FALLING, 0).ability(HEAL)
                .perk("Metade do tamanho, rápida e leve como pena")
                .perk("Cura os aliados (R)")
                .flaw("Muito frágil: -4 corações")
                .flaw("Dano fraco"));

        add(Race.of("giant", "Gigante", Items.BONE_BLOCK, 0x8D6E63,
                "Enorme demais para qualquer porta.")
                .scale(0.8).hp(20).dmg(3).reach(1.5).step(0.4).hunger(0.6f).ability(SCREAM)
                .perk("+10 corações, +3 de dano e alcance gigante")
                .flaw("Enorme: não passa em portas")
                .flaw("Fome gigante"));

        add(Race.of("phoenix", "Fênix", Items.FIRE_CHARGE, 0xFF6D00,
                "Renasce das próprias cinzas.")
                .fx(ALWAYS, FIRE_RESISTANCE, 0).fx(BURNING, REGENERATION, 1).fx(COLD, SLOWNESS, 0)
                .hurt(WET, 1.0f).revive().ability(HEAL)
                .perk("Ao morrer, renasce (recarga de 5 min)")
                .perk("Imune a fogo; em chamas se regenera rápido")
                .flaw("Água e chuva machucam")
                .flaw("No frio: Lentidão"));

        add(Race.of("yeti", "Yeti", Items.PACKED_ICE, 0x81D4FA,
                "Rei das montanhas geladas.")
                .hp(4).kb(0.3).fx(COLD, STRENGTH, 1).fx(COLD, RESISTANCE, 0).fx(COLD, SPEED, 0)
                .fx(HOT, SLOWNESS, 1).fx(HOT, WEAKNESS, 0).hurt(HOT, 1.0f).ability(FREEZE)
                .perk("No frio: Força II, Resistência e Velocidade")
                .perk("Congela os inimigos (R)")
                .flaw("No calor (deserto/Nether): sofre dano e fica fraco"));

        add(Race.of("ghost", "Fantasma", Items.PHANTOM_MEMBRANE, 0xB0BEC5,
                "Invisível na escuridão.")
                .fx(NIGHT, INVISIBILITY, 0).speed(0.1).hp(-6).dmg(-1).fx(SUNLIT, WEAKNESS, 1).fx(SUNLIT, SLOWNESS, 0)
                .ability(VANISH)
                .perk("À noite: invisível")
                .flaw("Muito frágil: -3 corações")
                .flaw("Ao sol: Fraqueza II e Lentidão"));

        add(Race.of("dragonborn", "Draconato", Items.DRAGON_BREATH, 0x7B1FA2,
                "Sangue de dragão nas veias.")
                .fx(ALWAYS, FIRE_RESISTANCE, 0).dmg(2).hp(4).armor(2).hunger(0.4f).fx(COLD, SLOWNESS, 0).ability(FIRE_BREATH)
                .perk("Sopro de fogo (R), imune a fogo")
                .perk("+2 corações, +2 de armadura e +2 de dano")
                .flaw("Sangue frio: Lentidão em biomas frios")
                .flaw("Fome mais rápida"));

        add(Race.of("kitsune", "Kitsune", Items.GOLDEN_CARROT, 0xFF9800,
                "Raposa espiritual, esperta e veloz.")
                .speed(0.2).jump(0.25).luck(2).hp(-4).fx(NIGHT, NIGHT_VISION, 0).fx(WET, SLOWNESS, 0).ability(DASH)
                .perk("Muito rápida, pula alto e tem muita sorte")
                .flaw("-2 corações")
                .flaw("Na chuva: Lentidão"));

        add(Race.of("undead", "Morto-Vivo", Items.ROTTEN_FLESH, 0x4E7A3A,
                "Levantou do túmulo. Não sente dor, mas odeia o sol.")
                .burn(SUNLIT_BARE).fx(NIGHT, STRENGTH, 0).fx(LOW_HP, REGENERATION, 0).kb(0.2).speed(-0.05).ability(RAGE)
                .perk("À noite: Força; com pouca vida se regenera")
                .flaw("Pega fogo sob o sol (um capacete protege)")
                .flaw("5% mais lento"));

        add(Race.of("necromancer", "Necromante", Items.WITHER_SKELETON_SKULL, 0x37474F,
                "Mestre da morte e da sombra.")
                .fx(NIGHT, REGENERATION, 0).fx(NIGHT, RESISTANCE, 0).hp(-2).fx(SUNLIT, WEAKNESS, 0).fx(SUNLIT, SLOWNESS, 0)
                .ability(WITHER_GAZE)
                .perk("À noite: Regeneração e Resistência")
                .perk("Olhar sombrio aplica Wither (R)")
                .flaw("-1 coração")
                .flaw("Ao sol: Fraqueza e Lentidão"));

        add(Race.of("slime", "Slime", Items.SLIME_BALL, 0x7CB342,
                "Mole, saltitante e indestrutível contra quedas.")
                .jump(0.8).fall(-1).hp(2).dmg(-1).hurt(BURNING, 1.0f).ability(LEAP)
                .perk("Pulo gigante e zero dano de queda")
                .flaw("Fogo causa dano extra")
                .flaw("Corpo mole: -1 de dano"));

        add(Race.of("arachnid", "Aracnídeo", Items.SPIDER_EYE, 0x8B1A1A,
                "Caçador de teias e cavernas.")
                .speed(0.1).jump(0.2).step(0.4).hp(-4).fx(NIGHT, NIGHT_VISION, 0).fx(COLD, SLOWNESS, 0).ability(FREEZE)
                .perk("Sobe blocos, rápido e enxerga no escuro")
                .perk("Teia prende os inimigos (R)")
                .flaw("-2 corações")
                .flaw("No frio: Lentidão"));

        add(Race.of("piglin", "Piglin", Items.GOLD_INGOT, 0xE57373,
                "Em casa no Nether, forasteiro no resto do mundo.")
                .fx(NETHER, STRENGTH, 1).fx(NETHER, RESISTANCE, 0).fx(NETHER, SPEED, 0).fx(OVERWORLD, WEAKNESS, 0)
                .luck(1).ability(RAGE)
                .perk("No Nether: Força II, Resistência e Velocidade")
                .flaw("Fora do Nether: Fraqueza"));

        add(Race.of("berserker", "Berserker", Items.NETHERITE_AXE, 0xD32F2F,
                "Quanto mais ferido, mais perigoso.")
                .dmg(1).hp(-2).hunger(0.5f).fx(LOW_HP, STRENGTH, 2).fx(LOW_HP, SPEED, 1).fx(LOW_HP, RESISTANCE, 0).ability(RAGE)
                .perk("Com pouca vida: Força III, Velocidade II e Resistência")
                .flaw("Fome insaciável")
                .flaw("-1 coração"));

        add(Race.of("ninja", "Ninja", Items.IRON_SWORD, 0x546E7A,
                "Silencioso. Quando você ouve, já é tarde.")
                .fx(SNEAK, INVISIBILITY, 0).speed(0.15).fall(-0.7).jump(0.2).hp(-4).ability(DASH)
                .perk("Agachado: invisível")
                .perk("Rápido, ágil e quase sem dano de queda")
                .flaw("Frágil: -2 corações"));

        add(Race.of("mage", "Mago", Items.ENCHANTED_BOOK, 0x3F51B5,
                "Poder arcano a um estalar de dedos.")
                .hp(-4).luck(1).dmg(-1).fx(SNEAK, RESISTANCE, 1).ability(SHOCK)
                .perk("Raio onde você olha (R)")
                .perk("Agachado: escudo arcano (Resistência II)")
                .flaw("-2 corações")
                .flaw("Corpo a corpo fraco"));

        add(Race.of("druid", "Druida", Items.OAK_SAPLING, 0x2E7D32,
                "Guardião da natureza.")
                .hp(2).fx(GRASS, REGENERATION, 1).fx(GRASS, SPEED, 0).hurt(BURNING, 1.0f).fx(HOT, WEAKNESS, 0).ability(HEAL)
                .perk("Na grama: Regeneração II e Velocidade")
                .perk("Cura os aliados (R)")
                .flaw("Fogo causa dano extra")
                .flaw("Em lugares quentes: Fraqueza"));

        add(Race.of("thunder", "Filho do Trovão", Items.LIGHTNING_ROD, 0xFFEB3B,
                "Nasceu da tempestade.")
                .speed(0.15).hp(-2).fx(WET, STRENGTH, 1).fx(WET, SPEED, 1).fx(SUNLIT, WEAKNESS, 0).ability(SHOCK)
                .perk("Na chuva/água: Força II e Velocidade II")
                .perk("Chama raios (R)")
                .flaw("Céu limpo de dia: Fraqueza")
                .flaw("-1 coração"));

        // ---------- Estilo "youtuber" (genéricas, dá pra personalizar) ----------
        add(Race.of("troll", "Troll", Items.TNT, 0x00C853,
                "Vive para zoar os amigos.")
                .fx(SNEAK, SPEED, 2).fall(-0.5).hp(-4).dmg(-1).ability(SCREAM)
                .perk("Agachado: Velocidade III")
                .perk("Empurrão troll (R)")
                .flaw("-2 corações")
                .flaw("Dano fraco"));

        add(Race.of("speedrunner", "Speedrunner", Items.CLOCK, 0x03A9F4,
                "Cada segundo conta.")
                .speed(0.3).step(0.4).fall(-0.5).hp(-4).hunger(0.4f).ability(DASH)
                .perk("30% mais rápido e quase sem dano de queda")
                .flaw("-2 corações")
                .flaw("Fome mais rápida"));

        add(Race.of("builder", "Construtor", Items.BRICKS, 0xFF7043,
                "Constrói rápido e de longe.")
                .reach(2).mine(0.5).fx(ALWAYS, HASTE, 0).dmg(-1).hp(-2).speed(-0.05).ability(LEAP)
                .perk("Alcance +2 blocos, mineração e pressa")
                .flaw("Luta mal: -1 de dano")
                .flaw("-1 coração"));

        add(Race.of("legend", "Lenda do Canal", Items.NETHER_STAR, 0xFFD700,
                "Todo mundo te conhece. Todo mundo te vê.")
                .hp(4).dmg(1).speed(0.05).luck(1).hunger(0.4f).fx(ALWAYS, GLOWING, 0).ability(RAGE)
                .perk("+2 corações, +1 de dano, sorte")
                .flaw("Brilha: todo mundo te vê de longe")
                .flaw("Fome mais rápida (os fãs cobram)"));
    }

    private Races() {}
            }
