package com.strengthsmp.core.ability.impl;

import com.strengthsmp.core.ability.*;

/**
 * Old-jar-flavored combo ability (reference: StrengthSMP.jar SWORD class passive/ultimate —
 * reimplemented independently, not copied). Passive: every 3rd consecutive hit on the
 * same target lands as a bonus-damage strike; bonus scales with Strength
 * (0.2 per point, capped at +4.0). Combo resets if no hit lands within 30 ticks (1.5s),
 * mirrored from the reference's combo-reset-ticks concept but re-expressed as this
 * ability's own constant, not a shared config value.
 *
 * No active component — this ability is fully passive, so activate() always fails.
 */
public final class Executioner implements Ability {

    private static final int COMBO_HITS_REQUIRED = 3;
    private static final int COMBO_RESET_MILLIS = 1500;
    private static final double BONUS_PER_STRENGTH = 0.2;
    private static final double BONUS_CAP = 4.0;
    private static final String COMBO_COUNTER_KEY = "executioner.combo";
    private static final String COMBO_LAST_HIT_KEY = "executioner.last_hit";

    @Override
    public String id() {
        return "executioner";
    }

    @Override
    public String displayName() {
        return "Executioner";
    }

    @Override
    public String description() {
        return "Every third consecutive hit on a target deals bonus damage.";
    }

    @Override
    public AbilityTier tier() {
        return AbilityTier.TWO;
    }

    @Override
    public int activeCooldownTicks() {
        return 0;
    }

    @Override
    public DamageModifier onOutgoingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        long now = System.currentTimeMillis();

        if (state.millisSinceEvent(COMBO_LAST_HIT_KEY, now) > COMBO_RESET_MILLIS) {
            state.resetCounter(COMBO_COUNTER_KEY);
        }
        state.markEvent(COMBO_LAST_HIT_KEY, now);

        int hits = state.incrementCounter(COMBO_COUNTER_KEY);

        if (hits >= COMBO_HITS_REQUIRED) {
            state.resetCounter(COMBO_COUNTER_KEY);
            int strength = ctx.self().getStrength();
            double bonus = Math.min(BONUS_CAP, strength * BONUS_PER_STRENGTH);
            return DamageModifier.additive(bonus);
        }
        return DamageModifier.NONE;
    }

    @Override
    public DamageModifier onIncomingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        return DamageModifier.NONE;
    }

    @Override
    public boolean activate(AbilityContext ctx, AbilityRuntimeState state) {
        return false; // fully passive ability
    }
}
