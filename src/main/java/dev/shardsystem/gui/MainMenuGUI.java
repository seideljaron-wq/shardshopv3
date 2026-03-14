package dev.shardsystem.gui;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainMenuGUI {

    public static final String TITLE = "✦ RuneMC Shard Shop ✦";

    // Category layout in a 5-row (45-slot) inventory
    // Row 1+5: borders
    // Row 2: cats 1-4 centered (slots 11,13,15,21 → adjusted)
    // Row 3: cats 5-7
    // Row 4: cats 8-10
    // Layout: center 3 groups of items
    //  Row 2 center: 11,12,13,14,15 → use 10,12,14,16 for 4 items
    //  Row 3: 19,21,23 for 3 items  → use 19,22,25
    //  Row 4: 28,31,34 for 3 items

    // Slots for the 10 categories:
    public static final int[] CAT_SLOTS = {
        11, 13, 15, 17,   // row 2: shards, sell-wand, ranks, boosters
        28, 30, 32,       // row 4: vault, chattags, chatcolors
        37, 39, 41        // row 5: money, gradients, crates  (row index in 6-row)
    };

    // Actually let's use a cleaner 6-row layout
    // Row 1: border
    // Row 2: cats 1-5  (shards=12, sellwand=14, ranks=16, boosters=?, ...)
    // Let me do 4-row with centered items

    private final ShardSystem plugin;

    public MainMenuGUI(ShardSystem plugin) { this.plugin = plugin; }

    public Inventory build(Player player) {
        // 54-slot (6 rows) main menu
        Inventory inv = Bukkit.createInventory(null, 54,
            Component.text(TITLE, NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        GuiUtil.fillAll(inv);

        int shards = plugin.getShardManager().getShards(player);

        // Balance display at top center (slot 4)
        inv.setItem(4, buildBalanceItem(shards));

        // 10 Categories placed in 2 rows of 5 (slots 20-24 and 29-33)
        inv.setItem(20, buildCatItem(Material.AMETHYST_SHARD,      "✦ Shards",         "Buy Shards",             NamedTextColor.LIGHT_PURPLE));
        inv.setItem(21, buildCatItem(Material.BLAZE_ROD,           "⚔ Sell Wands",      "Sell wands & more",      NamedTextColor.GOLD));
        inv.setItem(22, buildCatItem(Material.NETHERITE_CHESTPLATE,"♛ Ranks",           "Buy ranks",              NamedTextColor.RED));
        inv.setItem(23, buildCatItem(Material.SUNFLOWER,           "⚡ Boosters",        "XP & Loot boosters",     NamedTextColor.YELLOW));
        inv.setItem(24, buildCatItem(Material.BARREL,              "📦 Vault Storage",   "Expand your vault",      NamedTextColor.GOLD));
        inv.setItem(29, buildCatItem(Material.NAME_TAG,            "🏷 Chat Tags",        "Custom chat tags",       NamedTextColor.AQUA));
        inv.setItem(30, buildCatItem(Material.CYAN_DYE,            "🎨 Chat Colors",      "Custom chat colors",     NamedTextColor.DARK_AQUA));
        inv.setItem(31, buildCatItem(Material.PAPER,               "💰 Money",           "Buy in-game money",      NamedTextColor.GREEN));
        inv.setItem(32, buildCatItem(Material.ORANGE_DYE,          "🌈 Gradients",        "Gradient name colors",   NamedTextColor.GOLD));
        inv.setItem(33, buildCatItem(Material.TRIPWIRE_HOOK,       "🗝 Crate Keys",       "Buy crate keys",         NamedTextColor.LIGHT_PURPLE));

        // Row 5: Glow (centered)
        inv.setItem(49, buildCatItem(Material.LEATHER_CHESTPLATE,  "✨ Glows",           "Player glow effects",    NamedTextColor.WHITE));

        return inv;
    }

    private ItemStack buildBalanceItem(int shards) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text("Your Balance", NamedTextColor.LIGHT_PURPLE)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text("  ✦ Shards: ", NamedTextColor.WHITE)
                .append(Component.text(String.format("%,d", shards), NamedTextColor.LIGHT_PURPLE)
                    .decorate(TextDecoration.BOLD))
                .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Visit ", NamedTextColor.GRAY)
                .append(Component.text("store.runemc.org", NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED))
                .append(Component.text(" to buy more!", NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildCatItem(Material mat, String name, String desc, NamedTextColor color) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text(name, color)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text(desc, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("► Click to open", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }
}
