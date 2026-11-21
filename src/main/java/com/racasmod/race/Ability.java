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
    LEECH("Drenar Vida", "Suga a vida dos inimigos à frente e cura você.", 14);

    public final String title;
    public final String desc;
    public final int cooldownSeconds;

    Ability(String title, String desc, int cooldownSeconds) {
        this.title = title;
        this.desc = desc;
        this.cooldownSeconds = cooldownSeconds;
    }
}
