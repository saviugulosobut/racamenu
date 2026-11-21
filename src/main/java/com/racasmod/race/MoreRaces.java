package com.racasmod.race;

import static com.racasmod.race.Ability.*;
import static com.racasmod.race.When.*;
import static net.minecraft.entity.effect.StatusEffects.*;

import java.util.function.Consumer;
import net.minecraft.item.Items;

/** Pacote 2: +20 raças. Registradas por Races.ensure(). */
final class MoreRaces {
    static void register(Consumer<Race> add) {

        // ---------- Pacote 2: +20 raças ----------
        add.accept(Race.of("samurai", "Samurai", Items.DIAMOND_SWORD, 0xC62828,
                "Honra acima de tudo. Lâmina rápida, corpo disciplinado.")
                .dmg(2).speed(0.1).hp(-2).fx(LOW_HP, STRENGTH, 1).fx(LOW_HP, RESISTANCE, 0).ability(DASH)
                .perk("+2 de dano e 10% mais rápido")
                .perk("Com pouca vida: Força II e Resistência")
                .perk("Dash cortante (R)")
                .flaw("-1 coração"));

        add.accept(Race.of("pirate", "Pirata", Items.SPYGLASS, 0x6D4C41,
                "Senhor dos mares e dos ganchos.")
                .luck(2).dmg(1).fx(IN_WATER, DOLPHINS_GRACE, 0).fx(IN_WATER, STRENGTH, 0).fx(UNDERGROUND, SLOWNESS, 0).ability(PULL)
                .perk("Gancho puxa os inimigos até você (R)")
                .perk("Na água: nada rápido e ganha Força")
                .perk("+1 de dano e sorte")
                .flaw("Em cavernas: Lentidão (saudade do mar)"));

        add.accept(Race.of("cyborg", "Ciborgue", Items.OBSERVER, 0x00B0FF,
                "Metade homem, metade máquina, 100% descarga elétrica.")
                .armor(4).dmg(1).speed(0.05).hp(2).hurt(WET, 1.0f).fx(NIGHT, NIGHT_VISION, 0).ability(SHOCK)
                .perk("+4 de armadura, +1 coração e +1 de dano")
                .perk("Visão noturna à noite")
                .perk("Descarga elétrica onde você olha (R)")
                .flaw("Curto-circuito: água e chuva machucam"));

        add.accept(Race.of("alien", "Alienígena", Items.AMETHYST_SHARD, 0x69F0AE,
                "Veio de outro mundo. Pula como ninguém e cega quem chega perto.")
                .scale(-0.1).jump(0.5).fall(-1).hp(-2).hunger(0.2f).fx(NIGHT, NIGHT_VISION, 0).fx(NIGHT, SPEED, 0).ability(BLIND)
                .perk("Pulo enorme e zero dano de queda")
                .perk("À noite: visão noturna e Velocidade")
                .perk("Clarão cega os inimigos por perto (R)")
                .flaw("-1 coração")
                .flaw("Fome mais rápida"));

        add.accept(Race.of("titan", "Titã", Items.OBSIDIAN, 0x5D4037,
                "Uma montanha que anda. Cada passo faz o chão tremer.")
                .scale(0.5).hp(16).dmg(3).armor(2).kb(0.6).speed(-0.15).hunger(0.5f).ability(STOMP)
                .perk("+8 corações, +3 de dano e +2 de armadura")
                .perk("Pisão arremessa os inimigos pro alto (R)")
                .flaw("15% mais lento")
                .flaw("Fome gigante"));

        add.accept(Race.of("assassin", "Assassino", Items.NETHERITE_SWORD, 0x78909C,
                "Um golpe, um silêncio.")
                .dmg(3).hp(-6).speed(0.12).fx(SNEAK, INVISIBILITY, 0).fx(NIGHT, STRENGTH, 0).fx(SUNLIT, WEAKNESS, 0).ability(VANISH)
                .perk("+3 de dano e 12% mais rápido")
                .perk("Agachado: invisível; à noite: Força")
                .perk("Sumiço (R)")
                .flaw("Frágil: -3 corações")
                .flaw("Ao sol: Fraqueza"));

        add.accept(Race.of("paladin", "Paladino", Items.GOLDEN_SWORD, 0xFFD54F,
                "Guerreiro da luz. Brilha sob o sol e protege os aliados.")
                .armor(4).hp(4).dmg(1).speed(-0.05).hunger(0.2f).fx(SUNLIT, REGENERATION, 0).fx(SUNLIT, STRENGTH, 0)
                .fx(UNDERGROUND, WEAKNESS, 0).ability(BARRIER)
                .perk("+2 corações, +4 de armadura e +1 de dano")
                .perk("Ao sol: Regeneração e Força")
                .perk("Barreira sagrada (R)")
                .flaw("5% mais lento")
                .flaw("Em cavernas: Fraqueza")
                .flaw("Fome mais rápida"));

        add.accept(Race.of("nomad", "Nômade do Deserto", Items.CACTUS, 0xFFB74D,
                "Filho das dunas. Rápido no calor, perdido no frio.")
                .speed(0.1).hp(2).fx(HOT, SPEED, 1).fx(HOT, RESISTANCE, 0).fx(COLD, SLOWNESS, 1).fx(COLD, WEAKNESS, 0).ability(DASH)
                .perk("No calor (deserto/Nether): Velocidade II e Resistência")
                .perk("+1 coração e 10% mais rápido")
                .perk("Dash (R)")
                .flaw("No frio: Lentidão II e Fraqueza"));

        add.accept(Race.of("scorpion", "Escorpião", Items.FERMENTED_SPIDER_EYE, 0xBF360C,
                "Casca dura, ferrão letal.")
                .dmg(1).hp(-2).armor(2).step(0.3).fx(HOT, STRENGTH, 0).fx(WET, SLOWNESS, 0).ability(VENOM)
                .perk("+2 de armadura e sobe blocos")
                .perk("No calor: Força")
                .perk("Veneno em cone (R)")
                .flaw("-1 coração")
                .flaw("Na água/chuva: Lentidão"));

        add.accept(Race.of("banshee", "Banshee", Items.SOUL_LANTERN, 0x80DEEA,
                "Seu grito arrepia até os mortos.")
                .hp(-4).speed(0.15).fall(-0.5).fx(NIGHT, SPEED, 1).fx(NIGHT, NIGHT_VISION, 0).fx(SUNLIT, WEAKNESS, 1).ability(SCREAM)
                .perk("15% mais rápida e quase sem dano de queda")
                .perk("À noite: Velocidade II e visão noturna")
                .perk("Grito empurra e fere (R)")
                .flaw("Frágil: -2 corações")
                .flaw("Ao sol: Fraqueza II"));

        add.accept(Race.of("parasite", "Parasita", Items.SPORE_BLOSSOM, 0xAD1457,
                "Vive da vida dos outros.")
                .lifesteal(0.4f).hp(-4).dmg(1).hunger(0.3f).fx(WET, SLOWNESS, 0).ability(LEECH)
                .perk("Rouba 40% do dano que causa em vida")
                .perk("Drenar vida cura você (R)")
                .flaw("Frágil: -2 corações")
                .flaw("Fome mais rápida")
                .flaw("Na água/chuva: Lentidão"));

        add.accept(Race.of("pyromancer", "Piromante", Items.BLAZE_POWDER, 0xFF5722,
                "Quanto mais queima, mais forte fica.")
                .hp(-2).fx(ALWAYS, FIRE_RESISTANCE, 0).fx(BURNING, STRENGTH, 1).fx(BURNING, SPEED, 1).hurt(WET, 1.0f).ability(EXPLODE)
                .perk("Imune a fogo")
                .perk("Em chamas: Força II e Velocidade II")
                .perk("Explosão sem quebrar blocos (R)")
                .flaw("-1 coração")
                .flaw("Água e chuva machucam"));

        add.accept(Race.of("windwalker", "Andarilho dos Ventos", Items.BREEZE_ROD, 0xB2EBF2,
                "Leve como a brisa. Quase voa.")
                .speed(0.25).jump(0.5).fall(-1).hp(-6).dmg(-1).ability(LEAP)
                .perk("25% mais rápido e pulo enorme")
                .perk("Zero dano de queda")
                .perk("Super salto (R)")
                .flaw("Frágil: -3 corações")
                .flaw("Dano fraco"));

        add.accept(Race.of("bear", "Urso", Items.HONEYCOMB, 0xA1887F,
                "Pesado, forte e sempre com fome.")
                .hp(12).dmg(2).kb(0.4).speed(-0.05).hunger(0.5f).fx(COLD, RESISTANCE, 0).fx(COLD, STRENGTH, 0).ability(RAGE)
                .perk("+6 corações e +2 de dano")
                .perk("No frio: Força e Resistência")
                .perk("Fúria (R)")
                .flaw("5% mais lento")
                .flaw("Fome de urso"));

        add.accept(Race.of("feline", "Felino", Items.COD, 0xFFCC80,
                "Sempre cai de pé. Caça de noite.")
                .speed(0.2).jump(0.3).fall(-1).step(0.3).hp(-4).fx(NIGHT, NIGHT_VISION, 0).fx(WET, SLOWNESS, 0).ability(LEAP)
                .perk("20% mais rápido, pula alto e sobe blocos")
                .perk("Zero dano de queda; visão noturna à noite")
                .perk("Super salto (R)")
                .flaw("Frágil: -2 corações")
                .flaw("Na água/chuva: Lentidão"));

        add.accept(Race.of("terran", "Elemental da Terra", Items.GRASS_BLOCK, 0x689F38,
                "A própria montanha em forma de gente.")
                .armor(3).hp(6).kb(0.5).speed(-0.1).fx(UNDERGROUND, RESISTANCE, 0).fx(GRASS, REGENERATION, 0).ability(STOMP)
                .perk("+3 corações e +3 de armadura")
                .perk("Em cavernas: Resistência; na grama: Regeneração")
                .perk("Pisão (R)")
                .flaw("10% mais lento"));

        add.accept(Race.of("magma", "Homem-Magma", Items.MAGMA_BLOCK, 0xE65100,
                "Rocha derretida com mau humor.")
                .armor(2).dmg(2).hp(6).speed(-0.1).fx(ALWAYS, FIRE_RESISTANCE, 0).fx(NETHER, STRENGTH, 0).hurt(WET, 2.0f).ability(FIRE_BREATH)
                .perk("Imune a fogo, +3 corações e +2 de dano")
                .perk("No Nether: Força")
                .perk("Sopro de fogo (R)")
                .flaw("10% mais lento")
                .flaw("Água e chuva ferem muito"));

        add.accept(Race.of("shaman", "Xamã", Items.TOTEM_OF_UNDYING, 0x00BFA5,
                "Fala com os espíritos. Eles trazem você de volta.")
                .hp(-2).luck(1).dmg(-1).revive().fx(LOW_HP, ABSORPTION, 1).fx(GRASS, REGENERATION, 0).ability(BARRIER)
                .perk("Ao morrer, renasce (recarga de 5 min)")
                .perk("Com pouca vida: Absorção; na grama: Regeneração")
                .perk("Barreira espiritual (R)")
                .flaw("-1 coração")
                .flaw("Dano fraco"));

        add.accept(Race.of("gladiator", "Gladiador", Items.SHIELD, 0xFF8F00,
                "Nasceu na arena. Só cai de pé.")
                .armor(4).dmg(2).hp(4).kb(0.3).hunger(0.3f).fx(LOW_HP, RESISTANCE, 1).fx(LOW_HP, STRENGTH, 0).ability(SCREAM)
                .perk("+2 corações, +4 de armadura e +2 de dano")
                .perk("Com pouca vida: Resistência II e Força")
                .perk("Grito de guerra (R)")
                .flaw("Fome mais rápida"));

        add.accept(Race.of("trickster", "Trapaceiro", Items.FIREWORK_STAR, 0xE040FB,
                "Aparece, some e leva o que quiser.")
                .luck(3).hp(-4).speed(0.1).dmg(-1).fx(SNEAK, INVISIBILITY, 0).fx(SNEAK, SPEED, 1).ability(TELEPORT)
                .perk("Teleporte onde você olha (R)")
                .perk("Agachado: invisível e Velocidade II")
                .perk("Muita sorte")
                .flaw("Frágil: -2 corações")
                .flaw("Dano fraco"));
    }

    private MoreRaces() {}
                           }
