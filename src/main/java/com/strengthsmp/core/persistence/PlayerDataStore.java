package com.strengthsmp.core.persistence;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Minimal YAML-file-per-player persistence. One file per UUID under
 * plugins/StrengthSMP/playerdata/<uuid>.yml. Deliberately simple for this
 * first buildable pass — swap for a database-backed implementation later
 * without changing any caller, since everything goes through this class.
 */
public final class PlayerDataStore {

    private final File dataFolder;
    private final Logger logger;

    public PlayerDataStore(File pluginDataFolder, Logger logger) {
        this.dataFolder = new File(pluginDataFolder, "playerdata");
        this.logger = logger;
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logger.warning("Could not create playerdata directory at " + dataFolder.getAbsolutePath());
        }
    }

    private File fileFor(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }

    public int getStrength(UUID uuid, int defaultValue) {
        YamlConfiguration yaml = load(uuid);
        return yaml.getInt("strength", defaultValue);
    }

    public void setStrength(UUID uuid, int value) {
        YamlConfiguration yaml = load(uuid);
        yaml.set("strength", value);
        save(uuid, yaml);
    }

    public String getAbilityId(UUID uuid) {
        YamlConfiguration yaml = load(uuid);
        return yaml.getString("ability", null);
    }

    public void setAbilityId(UUID uuid, String abilityId) {
        YamlConfiguration yaml = load(uuid);
        yaml.set("ability", abilityId);
        save(uuid, yaml);
    }

    private YamlConfiguration load(UUID uuid) {
        File file = fileFor(uuid);
        return YamlConfiguration.loadConfiguration(file);
    }

    private void save(UUID uuid, YamlConfiguration yaml) {
        try {
            yaml.save(fileFor(uuid));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save player data for " + uuid, e);
        }
    }
}
