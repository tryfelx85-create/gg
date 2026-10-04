package com.strengthsmp.core.strength;

import com.strengthsmp.core.actor.CombatActor;

/**
 * Central read/write point for Strength. All gain/loss — PvP, playtime,
 * objectives, admin commands — goes through add()/remove() here rather than
 * each caller clamping independently, so the 0..max bound is enforced in
 * exactly one place.
 *
 * Progression-locked actors (bot profiles) must be filtered out by the
 * caller (e.g. PlaytimeService, PvpListener) before reaching this service —
 * it does not itself check isProgressionLocked(), since some callers
 * (an explicit admin command) should legitimately be able to override even
 * a locked actor.
 */
public final class StrengthService {

    public int get(CombatActor actor) {
        return actor.getStrength();
    }

    public void set(CombatActor actor, int value) {
        actor.setStrength(value);
    }

    public void add(CombatActor actor, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Use remove() for negative changes");
        }
        actor.setStrength(actor.getStrength() + amount);
    }

    public void remove(CombatActor actor, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        actor.setStrength(actor.getStrength() - amount);
    }
}
