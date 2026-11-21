 package com.racasmod.race;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/** Atributos que as raças podem modificar. */
public enum Attr {
    HEALTH(EntityAttributes.GENERIC_MAX_HEALTH, EntityAttributeModifier.Operation.ADD_VALUE),
    SPEED(EntityAttributes.GENERIC_MOVEMENT_SPEED, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
    DAMAGE(EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADD_VALUE),
    ARMOR(EntityAttributes.GENERIC_ARMOR, EntityAttributeModifier.Operation.ADD_VALUE),
    KNOCKBACK(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, EntityAttributeModifier.Operation.ADD_VALUE),
    JUMP(EntityAttributes.GENERIC_JUMP_STRENGTH, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
    SCALE(EntityAttributes.GENERIC_SCALE, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
    FALL(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
    REACH(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, EntityAttributeModifier.Operation.ADD_VALUE),
    MINE(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
    STEP(EntityAttributes.GENERIC_STEP_HEIGHT, EntityAttributeModifier.Operation.ADD_VALUE),
    LUCK(EntityAttributes.GENERIC_LUCK, EntityAttributeModifier.Operation.ADD_VALUE);

    public final RegistryEntry<EntityAttribute> entry;
    public final EntityAttributeModifier.Operation op;

    Attr(RegistryEntry<EntityAttribute> entry, EntityAttributeModifier.Operation op) {
        this.entry = entry;
        this.op = op;
    }

    /** Id único do modificador: um para a raça ("r") e outro para o poder copiado ("m"). */
    public Identifier id(String src) {
        return Identifier.of("racas", name().toLowerCase() + "_" + src);
    }
}
