package com.racasmod;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Itens do mod. */
public final class ModItems {
    /** A faca da Kobeni: textura toda estragada de propósito. */
    public static final Item KOBENI_KNIFE = Registry.register(Registries.ITEM,
            Identifier.of(RacasMod.MOD_ID, "faca_da_kobeni"),
            new Item(new Item.Settings().maxCount(1).attributeModifiers(
                    AttributeModifiersComponent.builder()
                            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                    new EntityAttributeModifier(Identifier.ofVanilla("base_attack_damage"), 14.0,
                                            EntityAttributeModifier.Operation.ADD_VALUE),
                                    AttributeModifierSlot.MAINHAND)
                            .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                                    new EntityAttributeModifier(Identifier.ofVanilla("base_attack_speed"), -1.6,
                                            EntityAttributeModifier.Operation.ADD_VALUE),
                                    AttributeModifierSlot.MAINHAND)
                            .build())));

    public static void init() {}

    private ModItems() {}
}
