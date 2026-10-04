package com.strengthsmp.plugin.command;

import com.strengthsmp.core.actor.PlayerCombatActor;
import com.strengthsmp.plugin.StrengthSmpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StrengthCommand implements CommandExecutor {

    private final StrengthSmpPlugin plugin;

    public StrengthCommand(StrengthSmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player self) {
            target = self;
        } else {
            sender.sendMessage(Component.text("Console must specify a player: /strength <player>", NamedTextColor.RED));
            return true;
        }

        PlayerCombatActor actor = new PlayerCombatActor(target, plugin.playerDataStore(), plugin.maxStrength());
        int strength = actor.getStrength();
        String abilityId = actor.getAbilityId();
        String abilityName = abilityId == null ? "none"
                : plugin.abilityRegistry().get(abilityId).map(a -> a.displayName()).orElse(abilityId);

        sender.sendMessage(Component.text(target.getName() + "'s Strength: ", NamedTextColor.GRAY)
                .append(Component.text(strength + " / " + actor.getMaxStrength(), NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("Ability: ", NamedTextColor.GRAY)
                .append(Component.text(abilityName, NamedTextColor.AQUA)));
        return true;
    }
}
