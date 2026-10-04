package com.strengthsmp.core.actor;

import com.strengthsmp.core.persistence.PlayerDataStore;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * CombatActor backed by a real online Player. Strength reads/writes go
 * straight through to PlayerDataStore so there is no separate in-memory
 * copy that could drift from disk — simplicity over caching for this
 * first buildable pass.
 */
public final class PlayerCombatActor implements CombatActor {

    private final Player player;
    private final PlayerDataStore dataStore;
    private final int maxStrength;

    public PlayerCombatActor(Player player, PlayerDataStore dataStore, int maxStrength) {
        this.player = player;
        this.dataStore = dataStore;
        this.maxStrength = maxStrength;
    }

    @Override
    public UUID getActorId() {
        return player.getUniqueId();
    }

    @Override
    public LivingEntity getEntity() {
        return player;
    }

    @Override
    public int getStrength() {
        return dataStore.getStrength(player.getUniqueId(), 0);
    }

    @Override
    public void setStrength(int value) {
        int clamped = Math.max(0, Math.min(maxStrength, value));
        dataStore.setStrength(player.getUniqueId(), clamped);
    }

    @Override
    public int getMaxStrength() {
        return maxStrength;
    }

    @Override
    public String getAbilityId() {
        return dataStore.getAbilityId(player.getUniqueId());
    }

    @Override
    public void setAbilityId(String abilityId) {
        dataStore.setAbilityId(player.getUniqueId(), abilityId);
    }

    @Override
    public boolean isProgressionLocked() {
        return false;
    }

    @Override
    public boolean isValid() {
        return player.isOnline();
    }
}
