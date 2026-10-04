package com.strengthsmp.core.ability;

/**
 * Result of an ability's damage hook. CombatService applies all additive
 * modifiers first (summed), then all multipliers (multiplied together),
 * so stacking multiple abilities/sources behaves predictably:
 * final = (base + sum(additive)) * product(multiplier)
 */
public final class DamageModifier {

    public static final DamageModifier NONE = new DamageModifier(0.0, 1.0);

    private final double additive;
    private final double multiplier;

    private DamageModifier(double additive, double multiplier) {
        this.additive = additive;
        this.multiplier = multiplier;
    }

    public static DamageModifier additive(double amount) {
        return new DamageModifier(amount, 1.0);
    }

    public static DamageModifier multiplier(double factor) {
        return new DamageModifier(0.0, factor);
    }

    public static DamageModifier of(double additive, double multiplier) {
        return new DamageModifier(additive, multiplier);
    }

    public double additiveAmount() {
        return additive;
    }

    public double multiplierAmount() {
        return multiplier;
    }
}
