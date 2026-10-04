package com.strengthsmp.core.actor;

import org.bukkit.entity.LivingEntity;

import java.util.UUID;

/**
 * Unifies real players and bot-controlled entities under one identity for the
 * Strength/Ability/Combat systems. Core services (StrengthService, AbilityService,
 * CombatService) operate only on this interface and never know whether the
 * underlying entity is a Player or an NMS fake player.
 */
public interface CombatActor {

    UUID getActorId();

    /** The live Bukkit entity backing this actor (Player, or bot's entity). */
    LivingEntity getEntity();

    /** Current strength value (0..max, clamped by StrengthService on write). */
    int getStrength();

    void setStrength(int value);

    int getMaxStrength();

    /** Currently equipped ability id, or null if none equipped. */
    String getAbilityId();

    void setAbilityId(String abilityId);

    /**
     * True for actors whose Strength/ability must never change from passive
     * progression (playtime ticks, objectives, PvP gain/loss) — i.e. bots with
     * a fixed profile. StrengthService and ObjectiveService must skip these.
     */
    boolean isProgressionLocked();

    boolean isValid();
}
