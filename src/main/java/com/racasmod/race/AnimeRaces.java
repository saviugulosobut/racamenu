package com.racasmod.race;

import static com.racasmod.race.Ability.*;
import static com.racasmod.race.When.*;
import static net.minecraft.entity.effect.StatusEffects.*;

import com.racasmod.ModItems;
import java.util.function.Consumer;
import net.minecraft.item.Items;

/**
 * Pacote 3: 19 personagens de anime (todos OP, cada um com uma fraqueza).
 * hp: +2 = 1 coração | speed/jump: 0.2 = +20% | fx(QUANDO, EFEITO, nível-1)
 * Registradas por Races.ensure().
 */
final class AnimeRaces {
    static void register(Consumer<Race> add) {

        // ---------- Chainsaw Man
        add.accept(Race.of("kobeni", "Kobeni", ModItems.KOBENI_KNIFE, 0xE57373,
                "Tremendo de medo e pedindo desculpa o tempo todo... e nunca perdeu uma luta. Será que o pânico é só fingimento?")
                .hp(-4).scale(-0.1).speed(0.2).jump(0.2).step(0.5).luck(3).dmg(2).dodge(0.35f).hunger(0.3f)
                .fx(LOW_HP, SPEED, 2).fx(LOW_HP, STRENGTH, 1)
                .ability(DESPERATE_STAB).also(PANIC_RUN).give(ModItems.KOBENI_KNIFE)
                .perk("Assassina de elite disfarçada de garota assustada: +2 de dano")
                .perk("Facada Desesperada (R): dano alto no ponto fatal, 20% de chance de matar na hora")
                .perk("Ágil como um macaco: +20% de velocidade e salto, sobe blocos")
                .perk("35% de chance de desviar de qualquer dano")
                .perk("Encurralada (pouca vida): Velocidade III e Força II")
                .perk("Começa com a Faca da Kobeni")
                .flaw("-2 corações (corpo pequeno)")
                .flaw("Gulosa: fome mais rápida"));

        // ---------- Death Note
        add.accept(Race.of("light", "Light Yagami", Items.WRITABLE_BOOK, 0x7B1FA2,
                "Eu sou a justiça. Escreva o nome, encare o rosto, e ele morre.")
                .hp(-4).dmg(-1).luck(2)
                .ability(DEATH_NOTE)
                .perk("Death Note (R): o alvo que você encara morre de ataque cardíaco em 40 segundos")
                .perk("Sorte +2 (gênio)")
                .flaw("-2 corações e -1 de dano: sem o caderno, é só um estudante"));

        // ---------- Tensei Slime
        add.accept(Race.of("rimuru", "Rimuru Tempest", Items.SLIME_BALL, 0x4FC3F7,
                "O slime que virou Rei Demônio. Devora, copia e regenera de tudo.")
                .hp(10).speed(0.1).armor(4).fall(-1).dmg(-1).lifesteal(0.1f).hunger(0.2f)
                .fx(ALWAYS, REGENERATION, 0).fx(ALWAYS, WATER_BREATHING, 0).fx(IN_WATER, DOLPHINS_GRACE, 0)
                .revive(6000, 10f)
                .ability(PREDATOR).also(WATER_BLADE, STICKY_THREAD, BLACK_LIGHTNING, HELL_FLARE, GRAVITY_COLLAPSE, BEELZEBUTH)
                .perk("7 habilidades: Predador, Lâmina d'Água, Fio Pegajoso, Relâmpago Negro, Chama do Inferno, Colapso Gravitacional e Beelzebuth")
                .perk("Agache + R troca de habilidade")
                .perk("+5 corações, regeneração, sem dano de queda, rouba 10% de vida")
                .perk("Volta da morte uma vez a cada 5 minutos")
                .flaw("-1 de dano (corpo de slime)")
                .flaw("Fome mais rápida (o Predador devora tudo)"));

        // ---------- Re:Zero
        add.accept(Race.of("shaula", "Shaula", Items.SPECTRAL_ARROW, 0xFF7043,
                "Bruxa-fera aprendiz do Sábio. Guardiã da Torre há 400 anos.")
                .hp(6).dmg(3).step(0.5).speed(0.05).hunger(0.3f)
                .ability(HELLS_SNIPE).also(INFINITE_SNIPE)
                .perk("Força descomunal: +3 de dano (levanta até um dragão-da-terra com carruagem)")
                .perk("Corpo de bruxa-fera: sobe blocos com facilidade")
                .perk("Hell's Snipe (R): agulha de luz quente ligada ao alvo por mana")
                .perk("Infinite Hell's Snipe: centenas de agulhas num raio enorme")
                .flaw("Gasta fome (mana)"));

        add.accept(Race.of("subaru", "Natsuki Subaru", Items.CLOCK, 0x1E88E5,
                "Morreu? Volta. Aprende. Tenta de novo.")
                .hp(-2).luck(1).fx(LOW_HP, WEAKNESS, 0)
                .revive(3000, 10f).checkpoint()
                .ability(MAO_INVISIVEL)
                .perk("Retorno pela Morte: ao morrer, volta ao último ponto salvo (salva a cada 20s)")
                .perk("Mão Invisível (R): esmaga todos ao redor")
                .flaw("-1 coração")
                .flaw("Com pouca vida: Fraqueza"));

        // ---------- Attack on Titan
        add.accept(Race.of("eren", "Eren Yeager", Items.NETHERITE_SWORD, 0x558B2F,
                "Humano e Titã. Continue avançando.")
                .hp(4).dmg(1).speed(0.05).kb(0.2).hunger(0.25f)
                .fx(LOW_HP, STRENGTH, 1)
                .ability(TITAN_FORM).also(RUMBLING)
                .perk("Titã (R): morde a mão (perde 2 corações) e fica gigante por 30s, com Força V, Resistência III e absorção")
                .perk("Rumbling: marcha dos titãs devasta uma área enorme")
                .perk("Agache + R troca entre Titã e Rumbling")
                .perk("Forma humana: com pouca vida ganha Força II")
                .flaw("Fome mais rápida (titã devora)"));

        // ---------- Dragon Ball
        add.accept(Race.of("goku", "Goku", Items.BLAZE_POWDER, 0xFF9800,
                "Guerreiro Saiyajin. Cada luta o deixa mais forte.")
                .hp(6).dmg(2).speed(0.1).hunger(0.6f)
                .fx(LOW_HP, STRENGTH, 1)
                .ability(KAMEHAMEHA).also(SUPER_SAIYAJIN)
                .perk("Kamehameha (R): raio devastador de 30 blocos")
                .perk("Super Saiyajin: Força IV, Velocidade III e Resistência II por 30s")
                .perk("Zenkai: com pouca vida ganha Força II")
                .flaw("Fome gigantesca (Saiyajin come muito)"));

        // ---------- One Punch Man
        add.accept(Race.of("saitama", "Saitama", Items.EGG, 0xFFEB3B,
                "Treino: 100 flexões, 100 abdominais, 100 agachamentos e 10 km. Todo dia.")
                .hp(6).dmg(6).jump(0.1).hunger(0.4f)
                .ability(SERIOUS_PUNCH)
                .perk("+6 de dano em todo golpe")
                .perk("Soco Sério (R): um soco que obliterará tudo à frente")
                .flaw("Fome mais rápida (precisa de mercado em promoção)"));

        // ---------- Jujutsu Kaisen
        add.accept(Race.of("sukuna", "Ryomen Sukuna", Items.WITHER_SKELETON_SKULL, 0xB71C1C,
                "O Rei das Maldições. Corta tudo, inclusive o que não deveria.")
                .hp(4).dmg(3).speed(0.1).lifesteal(0.1f).hunger(0.3f)
                .ability(DISMANTLE).also(CLEAVE, MALEVOLENT_SHRINE)
                .perk("Desmantelar: cortes invisíveis em linha reta (4s)")
                .perk("Clivar: corte que se ajusta à vida do alvo")
                .perk("Santuário Malevolente: cortes sem parar numa área enorme")
                .perk("Rouba 10% do dano em vida")
                .flaw("Gasta fome (energia amaldiçoada)"));

        // ---------- Eminence in Shadow
        add.accept(Race.of("cid", "Cid Kagenou", Items.AMETHYST_SHARD, 0x311B92,
                "Nas sombras, tudo é possível. I AM ATOMIC.")
                .hp(2).dmg(1).fx(NIGHT, STRENGTH, 1).fx(NIGHT, SPEED, 1).fx(DAY, WEAKNESS, 0)
                .ability(ATOMIC).also(VANISH)
                .perk("À noite: Força II e Velocidade II")
                .perk("I AM ATOMIC (R): explosão atômica gigante ao redor")
                .perk("Sumiço: invisível e rápido por 8s")
                .flaw("De dia: Fraqueza (fingindo ser um NPC)"));

        // ---------- Maou Gakuin
        add.accept(Race.of("anos", "Anos Voldigoad", Items.DRAGON_EGG, 0x6A1B9A,
                "O Rei Demônio Tirano. Destrói o que encara.")
                .hp(10).armor(4).hunger(0.3f)
                .ability(DESTRUCTION_EYE).also(BARRIER)
                .perk("Olho da Destruição (R): destrói quem você encarar")
                .perk("Barreira: Resistência e Absorção por 6s")
                .perk("+5 corações e +4 de armadura")
                .flaw("Magia gasta fome"));

        // ---------- Overlord
        add.accept(Race.of("ainz", "Ainz Ooal Gown", Items.SKELETON_SKULL, 0x4A148C,
                "O Soberano Supremo. A morte é apenas o começo.")
                .hp(4).armor(6).dmg(-2).fx(ALWAYS, NIGHT_VISION, 0)
                .ability(GRASP_HEART).also(BARRIER)
                .perk("Agarrar Coração (R): mata na hora tudo que for fraco por perto")
                .perk("Barreira: Resistência e Absorção por 6s")
                .perk("+6 de armadura (imune a ataques fracos) e visão no escuro")
                .flaw("Mago: -2 de dano corpo a corpo"));

        // ---------- SAO
        add.accept(Race.of("kirito", "Kirito", Items.IRON_SWORD, 0x212121,
                "O Espadachim Negro. Duas lâminas, zero pausa.")
                .hp(-4).dmg(2).speed(0.2)
                .ability(STARBURST).also(DASH)
                .perk("Starburst Stream (R): combo de 16 golpes ultrarrápidos")
                .perk("Dash para entrar na luta")
                .perk("+2 de dano e 20% mais rápido")
                .flaw("-2 corações"));

        // ---------- Nanatsu no Taizai
        add.accept(Race.of("escanor", "Escanor", Items.SUNFLOWER, 0xFFA000,
                "O Leão do Orgulho. Ao meio-dia, ninguém o supera.")
                .hp(6).fx(DAY, STRENGTH, 3).fx(DAY, RESISTANCE, 1).fx(NIGHT, WEAKNESS, 1).fx(NIGHT, SLOWNESS, 0)
                .ability(CRUEL_SUN).also(DIVINE_AXE)
                .perk("De dia: Força IV e Resistência II")
                .perk("Cruel Sun (R): bola de sol que calcina tudo")
                .perk("Machado Divino Rhitta: corte colossal à frente")
                .flaw("De noite: Fraqueza II e Lentidão"));

        add.accept(Race.of("meliodas", "Meliodas", Items.FIRE_CHARGE, 0x212121,
                "O Dragão da Ira. Tudo que você faz, volta em dobro.")
                .hp(4).dmg(2).speed(0.1).fx(NIGHT, STRENGTH, 1).fx(DAY, WEAKNESS, 0)
                .ability(FULL_COUNTER).also(HELLBLAZE)
                .perk("Contra-Ataque Total (R): reflete em dobro todo dano por 4s")
                .perk("Hellblaze: chamas negras queimam tudo ao redor")
                .perk("À noite: Força II (poder demoníaco)")
                .flaw("De dia: Fraqueza (poder demoníaco selado)"));

        // ---------- Hellsing
        add.accept(Race.of("alucard", "Alucard", Items.CROSSBOW, 0xB71C1C,
                "O vampiro supremo de Hellsing. Imune ao sol, devorador de almas.")
                .hp(10).dmg(1).lifesteal(0.3f).hunger(0.4f).fx(ALWAYS, REGENERATION, 1)
                .revive(4800, 10f)
                .ability(HELLHOUNDS).also(CROMWELL)
                .perk("Baskerville (R): o cão infernal devora os inimigos")
                .perk("Cromwell: quebra o selo e libera o poder total por 20s")
                .perk("Regeneração II e rouba 30% do dano em vida")
                .perk("Imune ao sol")
                .perk("Volta da morte a cada 4 minutos (almas absorvidas)")
                .flaw("Sede de sangue: gasta fome"));

        // ---------- JoJo
        add.accept(Race.of("dio", "DIO", Items.GOLD_INGOT, 0xFDD835,
                "MUDA MUDA MUDA! O mundo é dele. ZA WARUDO!")
                .hp(4).dmg(2).speed(0.1).lifesteal(0.15f).fx(NIGHT, STRENGTH, 1).burn(SUNLIT_BARE)
                .ability(TIME_STOP).also(KNIVES, ROAD_ROLLER)
                .perk("Parar o Tempo (R): congela todos ao redor por 5s")
                .perk("Chuva de Facas e Road Roller!")
                .perk("Rouba 15% do dano em vida e Força II à noite")
                .flaw("Pega fogo sob o sol (um capacete protege)"));

        // ---------- Fate
        add.accept(Race.of("gilgamesh", "Gilgamesh", Items.GOLDEN_SWORD, 0xFFD600,
                "O Rei dos Heróis. O tesouro do mundo é dele.")
                .hp(4).armor(4).luck(5).dmg(-1).speed(-0.05)
                .ability(GATE_OF_BABYLON).also(ENKIDU, ENUMA_ELISH)
                .perk("Portão da Babilônia (R): chuva de armas douradas")
                .perk("Enkidu: correntes prendem todos por perto")
                .perk("Enuma Elish: a espada que rasga o mundo")
                .perk("Sorte +5 e +4 de armadura")
                .flaw("-1 de dano e 5% mais lento (orgulhoso demais para correr)"));

        // ---------- Berserk
        add.accept(Race.of("guts", "Guts", Items.IRON_AXE, 0x424242,
                "O Espadachim Negro. Uma espada grande demais para ser espada.")
                .hp(6).dmg(3).armor(2).kb(0.5).speed(-0.05)
                .ability(DRAGON_SLAYER).also(BERSERKER_ARMOR, ARM_CANNON)
                .perk("Mata-Dragão (R): golpe absurdo num só alvo")
                .perk("Armadura Berserker: Força III e Resistência III por 20s, mas custa 2,5 corações")
                .perk("Canhão do Braço: disparo explosivo à frente")
                .perk("+3 de dano e resistência a empurrão")
                .flaw("5% mais lento (a espada pesa)"));
    }

    private AnimeRaces() {}
}
