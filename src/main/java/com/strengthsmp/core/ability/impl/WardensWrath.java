package com.strengthsmp.core.ability.impl;

import com.strengthsmp.core.ability.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Old-jar-flavored stun ability (reference: StrengthSMP.jar AXE class
 * crits-required-to-stun passive — reimplemented independently). Passive:
 * every 5th critical hit landed applies a brief stun (Slowness + Mining
 * Fatigue at high amplitude, since vanilla has no true "stun"). Stun
 * duration scales with Strength (0.05s per point, capped at +2s on top of
 * the 1s base).
 *
 * "Critical hit" detection is a platform concern (requires checking the
 * attacker's fall state at hit time via EntityDamageByEntityEvent) — this
 * class exposes onCriticalHit() for the platform combat listener to call
 * explicitly, separate from the general onOutgoingDamage() hook, since not
 * every hit is a crit and CombatService's generic damage pipeline has no
 * concept of criticals.
 */
public final class WardensWrath implements Ability {

    private static final int CRITS_REQUIRED_TO_STUN = 5;
    private static final int BASE_STUN_TICKS = 20; // 1s
    private static final double BONUS_STUN_TICKS_PER_STRENGTH = 1.0; // 0.05s per point in ticks
    private static final int BONUS_STUN_CAP_TICKS = 40; // +2s cap
    private static final String CRIT_COUNTER_KEY = "wardenswrath.crits";

    @Override
    public String id() {
        return "wardens_wrath";
    }

    @Override
    public String displayName() {
        return "Warden's Wrath";
    }

    @Override
    public String description() {
        return "Every fifth critical hit stuns your target, slowing them and hampering their swing.";
    }

    @Override
    public AbilityTier tier() {
        return AbilityTier.THREE;
    }

    @Override
    public int activeCooldownTicks() {
        return 0;
    }

    @Override
    public DamageModifier onOutgoingDamage(AbilityContext ctx, AbilityRuntimeState state) {
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

    /**
     * Called by the platform's combat listener when this actor lands a
     * genuine vanilla critical hit (falling, not sprinting, no negative
     * effects — standard crit conditions, not forced). Returns true if a
     * stun was triggered on this hit.
     */
    public boolean onCriticalHit(AbilityContext ctx, AbilityRuntimeState state) {
        int crits = state.incrementCounter(CRIT_COUNTER_KEY);
        if (crits < CRITS_REQUIRED_TO_STUN) {
            return false;
        }
        state.resetCounter(CRIT_COUNTER_KEY);

        int strength = ctx.self().getStrength();
        int bonusTicks = (int) Math.min(BONUS_STUN_CAP_TICKS, strength * BONUS_STUN_TICKS_PER_STRENGTH);
        int stunTicks = BASE_STUN_TICKS + bonusTicks;

        LivingEntity target = ctx.other() != null ? ctx.other().getEntity() : null;
        if (target != null && target.isValid()) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, stunTicks, 4, false, true, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, stunTicks, 3, false, true, true));
        }
        return true;
    }
}
