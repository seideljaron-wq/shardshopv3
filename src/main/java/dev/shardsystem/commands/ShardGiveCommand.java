package dev.shardsystem.commands;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class ShardGiveCommand implements CommandExecutor, TabCompleter {
    private final ShardSystem plugin;
    public ShardGiveCommand(ShardSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean console = !(sender instanceof Player);
        boolean allowed = console || ((Player)sender).isOp() || sender.hasPermission("shards.give")
            || (!console && plugin.getAdminManager().isAdmin(((Player)sender).getUniqueId()));
        if (!allowed) { sender.sendMessage(Component.text("✗ No permission.", NamedTextColor.RED)); return true; }
        if (args.length != 2) { sender.sendMessage(Component.text("Usage: /shard-give <player> <amount>", NamedTextColor.YELLOW)); return true; }

        int amount;
        try { amount = Integer.parseInt(args[1]); if (amount <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { sender.sendMessage(Component.text("✗ Amount must be a positive integer.", NamedTextColor.RED)); return true; }

        Player online = Bukkit.getPlayerExact(args[0]);
        OfflinePlayer target = online;
        if (target == null) { @SuppressWarnings("deprecation") OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]); if (op.hasPlayedBefore()) target = op; }
        if (target == null) { sender.sendMessage(Component.text("✗ Player '" + args[0] + "' not found.", NamedTextColor.RED)); return true; }

        String name = target.getName() != null ? target.getName() : args[0];
        String giver = console ? "Console" : sender.getName();
        plugin.getShardManager().addShards(target, amount);
        int bal = plugin.getShardManager().getShards(target);

        sender.sendMessage(Component.text("✔ Given ", NamedTextColor.GREEN)
            .append(Component.text(String.format("%,d", amount) + " Shards", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" to ", NamedTextColor.GREEN))
            .append(Component.text(name, NamedTextColor.AQUA))
            .append(Component.text(" | Balance: ", NamedTextColor.GRAY))
            .append(Component.text(String.format("%,d", bal) + " Shards", NamedTextColor.LIGHT_PURPLE)));

        if (online != null) online.sendMessage(
            Component.text("✔ You received ", NamedTextColor.GREEN)
                .append(Component.text(String.format("%,d", amount), NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" Shards!", NamedTextColor.GREEN)));

        plugin.getDiscordLogger().logShardsGiven(giver, name, amount, bal);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) { List<String> n = new ArrayList<>(); Bukkit.getOnlinePlayers().forEach(p -> n.add(p.getName())); return n; }
        if (args.length == 2) return List.of("10","50","100","500","1000");
        return List.of();
    }
}
