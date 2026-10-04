package com.strengthsmp.core.ability;

/**
 * Definition of a single ability. One instance per ability type is registered
 * in AbilityRegistry and shared by every actor (player or bot) who has it
 * equipped — abilities must therefore be stateless; any per-actor state
 * (cooldowns, combo counters, stacks) belongs in AbilityRuntimeState,
 * looked up by actor id, never stored as a field on the Ability itself.
 *
 * Each ability owns its own strength-scaling curve: there is no shared
 * formula in CombatService. This keeps abilities independently tunable,
 * per the design decision that scaling is ability-specific, not global.
 */
public interface Ability {

    /** Stable id used in persistence and commands, e.g. "berserker". */
    String id();

    String displayName();

    String description();

    AbilityTier tier();

    /** Cooldown of the active component, in ticks. 0 if the ability has no active component. */
    int activeCooldownTicks();

    /**
     * Passive outgoing-damage modifier, evaluated on every hit this actor lands.
     * Returns a multiplier (1.0 = no change) or additive bonus — see
     * {@link DamageModifier} for which. Implementations scale their effect
     * using ctx.self().getStrength() internally.
     */
    DamageModifier onOutgoingDamage(AbilityContext ctx, AbilityRuntimeState state);

    /**
     * Passive incoming-damage modifier, evaluated whenever this actor takes damage.
     */
    DamageModifier onIncomingDamage(AbilityContext ctx, AbilityRuntimeState state);

    /**
     * Invoked when the actor activates their ability (command, item, sneak+offhand —
     * binding is decided by the platform layer, not here). Returns false if the
     * ability could not activate (on cooldown, conditions not met); the caller
     * is responsible for notifying the actor.
     */
    boolean activate(AbilityContext ctx, AbilityRuntimeState state);
}
