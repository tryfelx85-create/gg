package com.strengthsmp.core.ability.impl;

import com.strengthsmp.core.ability.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Passive: lifesteal a percentage of outgoing melee damage as healing, scaling
 * with Strength (1% per point, capped at 25%).
 * Active: 4s rage burst — temporary attack-speed buff. Cooldown scales down
 * slightly with Strength (faster at high strength, floor at 20s).
 */
public final class Berserker implements Ability {

    private static final double LIFESTEAL_PER_STRENGTH = 0.01; // 1% per strength point
    private static final double LIFESTEAL_CAP = 0.25;
    private static final int BASE_COOLDOWN_TICKS = 600; // 30s
    private static final int MIN_COOLDOWN_TICKS = 400;  // 20s floor
    private static final String CD_KEY = "berserker.active";

    @Override
    public String id() {
        return "berserker";
    }

    @Override
    public String displayName() {
        return "Berserker";
    }

    @Override
    public String description() {
        return "Heal a portion of the damage you deal. Activate to enter a brief rage, swinging faster.";
    }

    @Override
    public AbilityTier tier() {
        return AbilityTier.ONE;
    }

    @Override
    public int activeCooldownTicks() {
        return BASE_COOLDOWN_TICKS;
    }

    @Override
    public DamageModifier onOutgoingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        int strength = ctx.self().getStrength();
        double lifestealPct = Math.min(LIFESTEAL_CAP, strength * LIFESTEAL_PER_STRENGTH);
        double healAmount = ctx.baseDamage() * lifestealPct;

        LivingEntity self = ctx.self().getEntity();
        if (self != null && self.isValid() && healAmount > 0) {
            double maxHealth = attributeValue(self, Attribute.MAX_HEALTH, 20.0);
            self.setHealth(Math.min(maxHealth, self.getHealth() + healAmount));
        }
        return DamageModifier.NONE;
    }

    @Override
    public DamageModifier onIncomingDamage(AbilityContext ctx, AbilityRuntimeState state) {
        return DamageModifier.NONE;
    }

    @Override
    public boolean activate(AbilityContext ctx, AbilityRuntimeState state) {
        long now = System.currentTimeMillis();
        if (!state.isReady(CD_KEY, now)) {
            return false;
        }
        int strength = ctx.self().getStrength();
        int cooldownTicks = Math.max(MIN_COOLDOWN_TICKS, BASE_COOLDOWN_TICKS - (strength * 4));
        state.setCooldown(CD_KEY, now, cooldownTicks * 50L);

        LivingEntity self = ctx.self().getEntity();
        if (self != null && self.isValid()) {
            self.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 80, 1, false, true, true));
            self.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0, false, true, true));
        }
        return true;
    }

    private static double attributeValue(LivingEntity entity, Attribute attribute, double fallback) {
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance != null ? instance.getValue() : fallback;
    }
}
