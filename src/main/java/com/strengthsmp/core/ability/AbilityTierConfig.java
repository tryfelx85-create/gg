package com.strengthsmp.core.ability;

import java.util.EnumMap;
import java.util.Map;

/**
 * Admin-overridable minimum-strength thresholds per tier, read from config.yml
 * (e.g. ability-tiers.TWO: 12). Falls back to AbilityTier's built-in default
 * when a tier is not present in config.
 */
public final class AbilityTierConfig {

    private final Map<AbilityTier, Integer> overrides = new EnumMap<>(AbilityTier.class);

    public void setOverride(AbilityTier tier, int minStrength) {
        overrides.put(tier, minStrength);
    }

    public int minStrengthFor(AbilityTier tier) {
        return overrides.getOrDefault(tier, tier.defaultMinStrength());
    }
}
