package com.strengthsmp.core.ability;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds every registered Ability by id. Built-in abilities are registered on
 * plugin enable; future abilities (including ones added later without
 * touching this class) register the same way, so the system is extensible
 * per the design requirement.
 */
public final class AbilityRegistry {

    private final Map<String, Ability> byId = new LinkedHashMap<>();

    public void register(Ability ability) {
        String id = ability.id();
        if (byId.containsKey(id)) {
            throw new IllegalStateException("Ability already registered: " + id);
        }
        byId.put(id, ability);
    }

    public Optional<Ability> get(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Collection<Ability> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    /** Abilities an actor at the given strength is allowed to select, gated by tier threshold. */
    public Collection<Ability> availableAt(int strength, AbilityTierConfig tierConfig) {
        return byId.values().stream()
                .filter(a -> strength >= tierConfig.minStrengthFor(a.tier()))
                .toList();
    }
}
