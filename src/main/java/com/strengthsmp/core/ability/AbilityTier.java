package com.strengthsmp.core.ability;

/**
 * Gates which abilities a player can select, based on their current Strength.
 * Thresholds are the default config values; server owners can override them
 * in config.yml (see AbilityConfig) without changing this enum.
 */
public enum AbilityTier {
    ONE(0),
    TWO(10),
    THREE(20);

    private final int defaultMinStrength;

    AbilityTier(int defaultMinStrength) {
        this.defaultMinStrength = defaultMinStrength;
    }

    public int defaultMinStrength() {
        return defaultMinStrength;
    }
}
