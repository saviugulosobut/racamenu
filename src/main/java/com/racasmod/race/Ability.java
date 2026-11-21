package com.racasmod.race;

/** Habilidades ativas (tecla R). */
public enum Ability {
    NONE("Nenhuma", "", 0),
    TELEPORT("Teleporte", "Teleporta até 16 blocos para onde você olha.", 8),
    DASH("Dash", "Avança numa velocidade absurda para frente.", 5),
    LEAP("Super Salto", "Dá um salto enorme (sem dano de queda).", 6),
    EXPLODE("Explosão", "Explode ao redor sem quebrar blocos.", 15),
    FIRE_BREATH("Sopro de Fogo", "Queima tudo que estiver na sua frente.", 10),
    SHOCK("Raio", "Invoca um raio onde você olha.", 12),
    FREEZE("Congelar", "Desacelera e prende os inimigos por perto.", 12),
    HEAL("Cura", "Cura você e os jogadores por perto.", 20),
    RAGE("Fúria", "Força, Velocidade e Resistência por 10s.", 25),
    VANISH("Sumiço", "Fica invisível e rápido por 8s.", 20),
    SCREAM("Grito", "Empurra e fere os inimigos por perto.", 12),
    WITHER_GAZE("Olhar Sombrio", "Aplica Wither nos inimigos à frente.", 15),
    STOMP("Pisão", "Esmaga o chão: fere e lança os inimigos pro alto.", 14),
    BARRIER("Barreira", "Escudo de Resistência e Absorção por 6s.", 18),
    VENOM("Veneno", "Envenena os inimigos à sua frente.", 12),
    PULL("Gancho", "Puxa os inimigos próximos até você.", 10),
    BLIND("Clarão", "Cega os inimigos por perto.", 16),
    LEECH("Drenar Vida", "Suga a vida dos inimigos à frente e cura você.", 14),

    // ---------- habilidades dos personagens de anime
    MAO_INVISIVEL("Mão Invisível", "Mãos invisíveis esmagam todos ao redor (Subaru).", 8),
    ATOMIC("I AM ATOMIC", "Explosão atômica gigante ao redor (Cid).", 25),
    KAMEHAMEHA("Kamehameha", "Raio de energia devastador à frente (Goku).", 12),
    SUPER_SAIYAJIN("Super Saiyajin", "Transformação: Força, Velocidade e Resistência enormes por 30s (Goku).", 60),
    SERIOUS_PUNCH("Soco Sério", "Um único soco que obliterará tudo à frente (Saitama).", 10),
    DISMANTLE("Desmantelar", "Cortes invisíveis em linha reta (Sukuna).", 4),
    CLEAVE("Clivar", "Corte que se ajusta à resistência do alvo (Sukuna).", 5),
    MALEVOLENT_SHRINE("Santuário Malevolente", "Expansão de Domínio: cortes sem parar numa área enorme (Sukuna).", 45),
    HELLS_SNIPE("Hell's Snipe", "Liga uma agulha de luz quente ao alvo com mana e a dispara (Shaula).", 3),
    INFINITE_SNIPE("Infinite Hell's Snipe", "Círculos mágicos disparam centenas de agulhas em todos num raio enorme (Shaula).", 20),
    TITAN_FORM("Titã", "Morde a mão (perde vida) e vira titã: força e dureza absurdas por 30s (Eren).", 45),
    RUMBLING("Rumbling", "Marcha dos Titãs: tremor devastador numa área gigante (Eren).", 60),
    DEATH_NOTE("Death Note", "Escreve o nome do alvo que você encara: ataque cardíaco em 40 segundos (Light).", 15),
    DESTRUCTION_EYE("Olho da Destruição", "Destrói quem você encarar (Anos).", 10),
    GRASP_HEART("Agarrar Coração", "Mata na hora tudo que for fraco por perto (Ainz).", 20),
    TIME_STOP("Parar o Tempo", "Congela todos ao redor por alguns segundos.", 40),
    STARBURST("Starburst Stream", "Combo de 16 golpes ultrarrápidos (Kirito).", 12),
    CRUEL_SUN("Cruel Sun", "Bola de sol que calcina tudo (Escanor).", 15),
    DIVINE_AXE("Machado Divino Rhitta", "Corte colossal à frente (Escanor).", 10),
    FULL_COUNTER("Contra-Ataque Total", "Reflete em dobro todo dano recebido por 4s (Meliodas).", 20),
    HELLBLAZE("Hellblaze", "Chamas negras queimam tudo ao redor (Meliodas).", 12),
    HELLHOUNDS("Baskerville", "Invoca o cão infernal Baskerville, que devora os inimigos (Alucard).", 15),
    CROMWELL("Cromwell", "Quebra o selo: Força, Velocidade e Regeneração enormes por 20s (Alucard).", 40),
    KNIVES("Chuva de Facas", "Facas atingem todos à frente (DIO).", 6),
    ROAD_ROLLER("Road Roller!", "Salta e despenca esmagando tudo (DIO).", 25),
    PANIC_RUN("Fuga em Pânico", "Corre e pula como um macaco por 8s (Kobeni).", 15),
    DESPERATE_STAB("Facada Desesperada", "Parece em pânico, mas mira o ponto fatal: dano alto e, às vezes, morte na hora (Kobeni).", 5),
    GATE_OF_BABYLON("Portão da Babilônia", "Chuva de armas douradas (Gilgamesh).", 15),
    ENKIDU("Enkidu", "Correntes puxam e prendem todos por perto (Gilgamesh).", 18),
    ENUMA_ELISH("Enuma Elish", "A espada que rasga o mundo: dano colossal (Gilgamesh).", 40),
    DRAGON_SLAYER("Mata-Dragão", "Golpe absurdo num só alvo (Guts).", 8),
    BERSERKER_ARMOR("Armadura Berserker", "Ignora a dor: Força e Resistência enormes por 20s, mas custa vida (Guts).", 40),
    ARM_CANNON("Canhão do Braço", "Disparo explosivo à frente (Guts).", 10),

    // ---------- habilidades do Rimuru
    PREDATOR("Predador", "Devora o alvo à frente: fere, cura você e dá absorção (Rimuru).", 6),
    WATER_BLADE("Lâmina d'Água", "Cortes de água em linha reta (Rimuru).", 4),
    STICKY_THREAD("Fio de Aço Pegajoso", "Prende os inimigos à frente com fios (Rimuru).", 10),
    BLACK_LIGHTNING("Relâmpago Negro", "Raios caem sobre os inimigos à frente (Rimuru).", 14),
    HELL_FLARE("Chama do Inferno", "Esfera de chamas negras explode onde você olha (Rimuru).", 18),
    GRAVITY_COLLAPSE("Colapso Gravitacional", "Puxa e esmaga os inimigos num ponto (Rimuru).", 20),
    BEELZEBUTH("Beelzebuth", "Devora tudo ao redor: fere todos e cura você (Rimuru).", 35);

    public final String title;
    public final String desc;
    public final int cooldownSeconds;

    Ability(String title, String desc, int cooldownSeconds) {
        this.title = title;
        this.desc = desc;
        this.cooldownSeconds = cooldownSeconds;
    }
}
