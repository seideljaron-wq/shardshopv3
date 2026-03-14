package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class DiscordLogger {
    private final ShardSystem plugin;
    private static final int GREEN  = 0x57F287;
    private static final int RED    = 0xED4245;
    private static final int BLUE   = 0x5865F2;
    private static final int YELLOW = 0xFEE75C;
    private static final int PURPLE = 0x9B59B6;

    public DiscordLogger(ShardSystem plugin) { this.plugin = plugin; }

    public void logPurchase(String player, String category, String item, int price, int remaining) {
        send(GREEN, "🛒 Shop Purchase",
            "**" + player + "** bought **" + item + "** from **" + category + "**",
            "Cost", price + " Shards | Remaining: " + remaining + " Shards");
    }
    public void logShardsGiven(String giver, String target, int amount, int newBal) {
        send(GREEN, "💎 Shards Given",
            "**" + giver + "** gave **" + amount + " Shards** to **" + target + "**",
            "New Balance", newBal + " Shards");
    }
    public void logAdminAdded(String exec, String target) {
        send(BLUE, "🔑 Shard Admin Added",
            "**" + target + "** added by **" + exec + "**", null, null);
    }
    public void logAdminRemoved(String exec, String target) {
        send(RED, "🔒 Shard Admin Removed",
            "**" + target + "** removed by **" + exec + "**", null, null);
    }
    public void logShopItemAdded(String admin, String item, int price) {
        send(YELLOW, "➕ Shop Item Added",
            "**" + admin + "** added **" + item + "** for **" + price + " Shards**", null, null);
    }
    public void logShopItemRemoved(String admin, String item) {
        send(RED, "➖ Shop Item Removed",
            "**" + admin + "** removed **" + item + "**", null, null);
    }

    private void send(int color, String title, String desc, String fn, String fv) {
        String url = plugin.getConfig().getString("discord-webhook-url", "");
        if (url.isBlank() || url.contains("YOUR_")) return;
        String field = fn != null
            ? ",\"fields\":[{\"name\":\"" + esc(fn) + "\",\"value\":\"" + esc(fv) + "\",\"inline\":false}]" : "";
        String json = "{\"embeds\":[{\"title\":\"" + esc(title) + "\","
            + "\"description\":\"" + esc(desc) + "\","
            + "\"color\":" + color + ",\"timestamp\":\"" + Instant.now() + "\","
            + "\"footer\":{\"text\":\"ShardSystem v3 \u2022 RuneMC\"}" + field + "}]}";
        final String fu = url, fj = json;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(fu).openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setRequestProperty("User-Agent", "ShardSystem");
                c.setDoOutput(true); c.setConnectTimeout(5000); c.setReadTimeout(5000);
                try (OutputStream os = c.getOutputStream()) { os.write(fj.getBytes(StandardCharsets.UTF_8)); }
                c.getResponseCode(); c.disconnect();
            } catch (Exception e) { plugin.getLogger().warning("[Discord] " + e.getMessage()); }
        });
    }
    private String esc(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }
}
