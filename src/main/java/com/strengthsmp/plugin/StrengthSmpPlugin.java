package com.strengthsmp.plugin;

import com.strengthsmp.core.ability.AbilityRegistry;
import com.strengthsmp.core.ability.AbilityTierConfig;
import com.strengthsmp.core.ability.impl.Berserker;
import com.strengthsmp.core.ability.impl.Executioner;
import com.strengthsmp.core.ability.impl.Juggernaut;
import com.strengthsmp.core.ability.impl.WardensWrath;
import com.strengthsmp.core.persistence.PlayerDataStore;
import com.strengthsmp.core.strength.StrengthService;
import com.strengthsmp.plugin.command.AbilityCommand;
import com.strengthsmp.plugin.command.StrengthAdminCommand;
import com.strengthsmp.plugin.command.StrengthCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point. Wires persistence -> services -> commands. This is the first
 * buildable slice of StrengthSMP: Strength + the 4 built abilities, with
 * simple YAML persistence. Combat damage integration, kits, lobby/SMP
 * modules, bots etc. are future additions on top of this skeleton — see
 * the project README / design doc for the full roadmap.
 */
public final class StrengthSmpPlugin extends JavaPlugin {

    private static final int DEFAULT_MAX_STRENGTH = 25;

    private PlayerDataStore playerDataStore;
    private StrengthService strengthService;
    private AbilityRegistry abilityRegistry;
    private AbilityTierConfig abilityTierConfig;
    private int maxStrength;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxStrength = getConfig().getInt("strength.max", DEFAULT_MAX_STRENGTH);

        playerDataStore = new PlayerDataStore(getDataFolder(), getLogger());
        strengthService = new StrengthService();

        abilityRegistry = new AbilityRegistry();
        abilityRegistry.register(new Berserker());
        abilityRegistry.register(new Juggernaut());
        abilityRegistry.register(new Executioner());
        abilityRegistry.register(new WardensWrath());

        abilityTierConfig = new AbilityTierConfig();
        applyTierOverridesFromConfig();

        getCommand("strength").setExecutor(new StrengthCommand(this));
        getCommand("strengthadmin").setExecutor(new StrengthAdminCommand(this));
        getCommand("ability").setExecutor(new AbilityCommand(this));

        getLogger().info("StrengthSMP enabled. Max strength=" + maxStrength
                + ", abilities registered=" + abilityRegistry.all().size());
    }

    @Override
    public void onDisable() {
        getLogger().info("StrengthSMP disabled.");
    }

    private void applyTierOverridesFromConfig() {
        if (!getConfig().isConfigurationSection("ability-tiers")) {
            return;
        }
        for (com.strengthsmp.core.ability.AbilityTier tier : com.strengthsmp.core.ability.AbilityTier.values()) {
            String path = "ability-tiers." + tier.name();
            if (getConfig().contains(path)) {
                abilityTierConfig.setOverride(tier, getConfig().getInt(path));
            }
        }
    }

    public PlayerDataStore playerDataStore() {
        return playerDataStore;
    }

    public StrengthService strengthService() {
        return strengthService;
    }

    public AbilityRegistry abilityRegistry() {
        return abilityRegistry;
    }

    public AbilityTierConfig abilityTierConfig() {
        return abilityTierConfig;
    }

    public int maxStrength() {
        return maxStrength;
    }
}
