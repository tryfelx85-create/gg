package com.strengthsmp.core.ability;

import com.strengthsmp.core.actor.CombatActor;

/**
 * Snapshot passed into ability hooks for a single combat interaction.
 * Immutable; abilities read from it and return effect values, they do not
 * mutate world state directly except through the returned modifiers.
 */
public final class AbilityContext {

    private final CombatActor self;
    private final CombatActor other;
    private final double incomingOrOutgoingDamage;

    public AbilityContext(CombatActor self, CombatActor other, double incomingOrOutgoingDamage) {
        this.self = self;
        this.other = other;
        this.incomingOrOutgoingDamage = incomingOrOutgoingDamage;
    }

    public CombatActor self() {
        return self;
    }

    /** The other party in the interaction (attacker if self is defender, and vice versa). May be null for non-combat triggers. */
    public CombatActor other() {
        return other;
    }

    public double baseDamage() {
        return incomingOrOutgoingDamage;
    }
}
