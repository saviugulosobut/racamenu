package com.racasmod.race;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

/** Define uma raça (ou um poder de mob copiado pelo Herobrine). */
public final class Race {
    public record AttrMod(Attr attr, double value) {}
    public record Fx(When when, RegistryEntry<StatusEffect> effect, int amp) {}
    public record Hurt(When when, float amount, boolean fire) {}

    public final String id, name, desc;
    public final Item icon;
    public final ItemStack stack;
    public final int color;
    public final List<String> perks = new ArrayList<>();
    public final List<String> flaws = new ArrayList<>();
    public final List<AttrMod> attrs = new ArrayList<>();
    public final List<Fx> fxs = new ArrayList<>();
    public final List<Hurt> hurts = new ArrayList<>();
    public Ability ability = Ability.NONE;
    public float lifesteal, hunger;
    public boolean revive, mimic;

    private Race(String id, String name, Item icon, int color, String desc) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.stack = new ItemStack(icon);
        this.color = color;
        this.desc = desc;
    }

    public static Race of(String id, String name, Item icon, int color, String desc) {
        return new Race(id, name, icon, color, desc);
    }

    // ---- textos do menu
    public Race perk(String s) { perks.add(s); return this; }
    public Race flaw(String s) { flaws.add(s); return this; }

    // ---- atributos
    public Race a(Attr attr, double v) { attrs.add(new AttrMod(attr, v)); return this; }
    public Race hp(double v) { return a(Attr.HEALTH, v); }
    public Race speed(double v) { return a(Attr.SPEED, v); }
    public Race dmg(double v) { return a(Attr.DAMAGE, v); }
    public Race armor(double v) { return a(Attr.ARMOR, v); }
    public Race kb(double v) { return a(Attr.KNOCKBACK, v); }
    public Race jump(double v) { return a(Attr.JUMP, v); }
    public Race scale(double v) { return a(Attr.SCALE, v); }
    public Race fall(double v) { return a(Attr.FALL, v); }
    public Race reach(double v) { return a(Attr.REACH, v); }
    public Race mine(double v) { return a(Attr.MINE, v); }
    public Race step(double v) { return a(Attr.STEP, v); }
    public Race luck(double v) { return a(Attr.LUCK, v); }

    // ---- efeitos condicionais (vantagens e fraquezas)
    public Race fx(When when, RegistryEntry<StatusEffect> effect, int amp) {
        fxs.add(new Fx(when, effect, amp));
        return this;
    }
    public Race burn(When when) { hurts.add(new Hurt(when, 0f, true)); return this; }
    public Race hurt(When when, float amount) { hurts.add(new Hurt(when, amount, false)); return this; }

    // ---- extras
    public Race ability(Ability a) { this.ability = a; return this; }
    public Race lifesteal(float f) { this.lifesteal = f; return this; }
    public Race hunger(float f) { this.hunger = f; return this; }
    public Race revive() { this.revive = true; return this; }
    public Race mimic() { this.mimic = true; return this; }
}
