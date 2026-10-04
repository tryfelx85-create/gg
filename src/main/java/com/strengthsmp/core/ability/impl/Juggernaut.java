package com.strengthsmp.core.ability.impl;

import com.strengthsmp.core.ability.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Passive: flat damage reduction percentage, scaling with Strength
 * (0.5% per point, capped at 15%).
 * Active: 3s knockback-immune window (vanilla Resistance won't stop knockback,
 * so this is implemented at the platform layer by tagging runtime state;
 * the platform's PlayerKnockbackEvent/EntityKnockbackEvent handler checks
 * state.isReady(..) == false on this key to cancel knockback). Cooldown fixed
 * at 45s regardless of strength — this is a defensive cooldown, not meant to
 * scale as aggressively as Berserker's.
 */
public final class Juggernaut implements Ability {

    private static final double REDUCTION_PER_STRENGTH = 0.005; // 0.5% per point
    private static final double REDUCTION_CAP = 0.15;
    private static final int COOLDOWN_TICKS = 900; // 45s
    private static final int IMMUNITY_DURATION_TICKS = 60; // 3s
    private static final String CD_KEY = "juggernaut.active";
    public static final String KNOCKBACK_IMMUNE_KEY = "juggernaut.kb_immune";

    @Override
    public String id() {
        return "juggernaut";
    }

    @Override
    public String displayName() {
        return "Juggernaut";
    }

    @Override
    public String description() {
        return "Take reduced damage from all sources. Activate to become immune to knockback briefly.";
    }

    @Override
    public AbilityTier tier() {
        return AbilityTier.TWO;
    }

    @Override
    public int activeCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public DamageModifier onOutgoingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        return DamageModifier.NONE;
    }

    @Override
    public DamageModifier onIncomingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        int strength = ctx.self().getStrength();
        double reduction = Math.min(REDUCTION_CAP, strength * REDUCTION_PER_STRENGTH);
        return DamageModifier.multiplier(1.0 - reduction);
    }

    @Override
    public boolean activate(AbilityContext ctx, AbilityRuntimeState state) {
        long now = System.currentTimeMillis();
        if (!state.isReady(CD_KEY, now)) {
            return false;
        }
        state.setCooldown(CD_KEY, now, COOLDOWN_TICKS * 50L);
        state.setCooldown(KNOCKBACK_IMMUNE_KEY, now, IMMUNITY_DURATION_TICKS * 50L);

        LivingEntity self = ctx.self().getEntity();
        if (self != null && self.isValid()) {
            self.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, IMMUNITY_DURATION_TICKS, 1, false, true, true));
        }
        return true;
    }

    /** Platform knockback listener calls this to decide whether to cancel knockback for the given state. */
    public static boolean isKnockbackImmune(AbilityRuntimeState state) {
        return !state.isReady(KNOCKBACK_IMMUNE_KEY, System.currentTimeMillis());
    }
}
