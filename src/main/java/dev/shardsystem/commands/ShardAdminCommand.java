package dev.shardsystem.commands;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class ShardAdminCommand implements CommandExecutor, TabCompleter {
    private final ShardSystem plugin;
    public ShardAdminCommand(ShardSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean console = !(sender instanceof Player);
        boolean allowed = console || ((Player)sender).isOp() || sender.hasPermission("shards.admin");
        if (!allowed) { sender.sendMessage(Component.text("✗ No permission.", NamedTextColor.RED)); return true; }
        if (args.length != 2) { sender.sendMessage(Component.text("Usage: /shard-admin <add|remove> <player>", NamedTextColor.YELLOW)); return true; }

        String exec = console ? "Console" : sender.getName();
        Player online = Bukkit.getPlayerExact(args[1]);
        @SuppressWarnings("deprecation") OfflinePlayer target = online != null ? online : Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && online == null) { sender.sendMessage(Component.text("✗ Player not found.", NamedTextColor.RED)); return true; }
        String name = target.getName() != null ? target.getName() : args[1];

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (plugin.getAdminManager().isAdmin(target.getUniqueId())) { sender.sendMessage(Component.text("⚠ Already an admin.", NamedTextColor.YELLOW)); return true; }
                plugin.getAdminManager().addAdmin(target.getUniqueId());
                sender.sendMessage(Component.text("✔ ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.LIGHT_PURPLE)).append(Component.text(" added as Shard Admin.", NamedTextColor.GREEN)));
                if (online != null) online.sendMessage(Component.text("✔ You are now a Shard Admin.", NamedTextColor.GREEN));
                plugin.getDiscordLogger().logAdminAdded(exec, name);
            }
            case "remove" -> {
                if (!plugin.getAdminManager().isAdmin(target.getUniqueId())) { sender.sendMessage(Component.text("⚠ Not an admin.", NamedTextColor.YELLOW)); return true; }
                plugin.getAdminManager().removeAdmin(target.getUniqueId());
                sender.sendMessage(Component.text("✔ ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.LIGHT_PURPLE)).append(Component.text(" removed from Shard Admins.", NamedTextColor.GREEN)));
                if (online != null) online.sendMessage(Component.text("✗ Your Shard Admin status was revoked.", NamedTextColor.RED));
                plugin.getDiscordLogger().logAdminRemoved(exec, name);
            }
            default -> sender.sendMessage(Component.text("Usage: /shard-admin <add|remove> <player>", NamedTextColor.YELLOW));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return List.of("add","remove");
        if (args.length == 2) { List<String> n = new ArrayList<>(); Bukkit.getOnlinePlayers().forEach(p -> n.add(p.getName())); return n; }
        return List.of();
    }
}
