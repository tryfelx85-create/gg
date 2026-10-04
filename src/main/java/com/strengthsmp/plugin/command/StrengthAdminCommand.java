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

public final class StrengthAdminCommand implements CommandExecutor {

    private final StrengthSmpPlugin plugin;

    public StrengthAdminCommand(StrengthSmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /strengthadmin <set|add|remove> <player> <amount>", NamedTextColor.RED));
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Amount must be a whole number.", NamedTextColor.RED));
            return true;
        }

        PlayerCombatActor actor = new PlayerCombatActor(target, plugin.playerDataStore(), plugin.maxStrength());

        switch (action) {
            case "set" -> plugin.strengthService().set(actor, amount);
            case "add" -> plugin.strengthService().add(actor, amount);
            case "remove" -> plugin.strengthService().remove(actor, amount);
            default -> {
                sender.sendMessage(Component.text("Unknown action. Use set, add, or remove.", NamedTextColor.RED));
                return true;
            }
        }

        int result = actor.getStrength();
        sender.sendMessage(Component.text(target.getName() + "'s Strength is now " + result + " / " + actor.getMaxStrength(), NamedTextColor.GREEN));
        return true;
    }
}
