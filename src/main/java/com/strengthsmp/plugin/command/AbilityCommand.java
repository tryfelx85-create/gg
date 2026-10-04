package com.strengthsmp.plugin.command;

import com.strengthsmp.core.ability.Ability;
import com.strengthsmp.core.actor.PlayerCombatActor;
import com.strengthsmp.plugin.StrengthSmpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Minimal command-line ability picker for this first buildable pass.
 * The design calls for a GUI with kit-variant selection; that comes with
 * the Kit module. This command exists so the ability system is actually
 * reachable/testable on a live server right now.
 */
public final class AbilityCommand implements CommandExecutor {

    private final StrengthSmpPlugin plugin;

    public AbilityCommand(StrengthSmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Players only.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            PlayerCombatActor actor = new PlayerCombatActor(player, plugin.playerDataStore(), plugin.maxStrength());
            String current = actor.getAbilityId();
            if (current == null) {
                player.sendMessage(Component.text("You have no ability equipped. Use /ability list to see options.", NamedTextColor.YELLOW));
            } else {
                Optional<Ability> ability = plugin.abilityRegistry().get(current);
                player.sendMessage(Component.text("Current ability: ", NamedTextColor.GRAY)
                        .append(Component.text(ability.map(Ability::displayName).orElse(current), NamedTextColor.AQUA)));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            PlayerCombatActor actor = new PlayerCombatActor(player, plugin.playerDataStore(), plugin.maxStrength());
            int strength = actor.getStrength();
            player.sendMessage(Component.text("Available abilities (Strength " + strength + "):", NamedTextColor.GOLD));
            for (Ability ability : plugin.abilityRegistry().availableAt(strength, plugin.abilityTierConfig())) {
                player.sendMessage(Component.text("- " + ability.id() + ": ", NamedTextColor.AQUA)
                        .append(Component.text(ability.displayName() + " (" + ability.tier() + ") - " + ability.description(), NamedTextColor.WHITE)));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("set") && args.length == 2) {
            String abilityId = args[1].toLowerCase();
            Optional<Ability> ability = plugin.abilityRegistry().get(abilityId);
            if (ability.isEmpty()) {
                player.sendMessage(Component.text("Unknown ability id: " + abilityId, NamedTextColor.RED));
                return true;
            }
            PlayerCombatActor actor = new PlayerCombatActor(player, plugin.playerDataStore(), plugin.maxStrength());
            int minRequired = plugin.abilityTierConfig().minStrengthFor(ability.get().tier());
            if (actor.getStrength() < minRequired) {
                player.sendMessage(Component.text("You need " + minRequired + " Strength to pick this ability.", NamedTextColor.RED));
                return true;
            }
            actor.setAbilityId(abilityId);
            player.sendMessage(Component.text("Ability set to " + ability.get().displayName() + ".", NamedTextColor.GREEN));
            return true;
        }

        player.sendMessage(Component.text("Usage: /ability [list | set <id>]", NamedTextColor.RED));
        return true;
    }
}
