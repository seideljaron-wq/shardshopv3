package dev.shardsystem;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ShardsExpansion extends PlaceholderExpansion {
    private final ShardSystem plugin;
    public ShardsExpansion(ShardSystem plugin) { this.plugin = plugin; }
    @Override public @NotNull String getIdentifier() { return "shards"; }
    @Override public @NotNull String getAuthor()     { return "ShardSystem"; }
    @Override public @NotNull String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()               { return true; }
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "0";
        if (params.equalsIgnoreCase("amount"))
            return String.valueOf(plugin.getShardManager().getShards(player));
        return null;
    }
}
