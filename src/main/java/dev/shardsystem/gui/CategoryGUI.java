package dev.shardsystem.gui;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds all 10 category sub-GUIs.
 * Each is a 4-row (36-slot) inventory with borders and centered items.
 */
public class CategoryGUI {

    // Back button slot (always slot 27 = bottom-left in 4 rows, but we use 36-slot so slot 27)
    public static final int BACK_SLOT = 27;

    private final ShardSystem plugin;

    public CategoryGUI(ShardSystem plugin) { this.plugin = plugin; }

    // ── Category titles (used to identify in click listener) ─────────────────
    public static final String CAT_SHARDS    = "✦ Shards";
    public static final String CAT_SELLWAND  = "✦ Sell Wands";
    public static final String CAT_RANKS     = "✦ Ranks";
    public static final String CAT_BOOSTERS  = "✦ Boosters";
    public static final String CAT_VAULT     = "✦ Vault Storage";
    public static final String CAT_CHATTAGS  = "✦ Chat Tags";
    public static final String CAT_CHATCOLOR = "✦ Chat Colors";
    public static final String CAT_MONEY     = "✦ Money";
    public static final String CAT_GRADIENTS = "✦ Gradients";
    public static final String CAT_CRATES    = "✦ Crate Keys";
    public static final String CAT_GLOWS     = "✦ Glows";

    // ── 1. SHARDS ─────────────────────────────────────────────────────────────
    public Inventory buildShards() {
        Inventory inv = make(CAT_SHARDS);
        // 3 items centered: 250, 500, 1000 shards
        // Prices scaled: £1.50=250 shards costs ~250 shards in game (you get what you pay for)
        // But these are things you BUY WITH REAL MONEY, so clicking tells player to visit website
        inv.setItem(12, GuiUtil.websiteItem(Material.AMETHYST_SHARD,
            "250 Shards", "Get 250 Shards added to your balance!", "store.runemc.org"));
        inv.setItem(13, GuiUtil.websiteItem(Material.AMETHYST_CLUSTER,
            "500 Shards", "Get 500 Shards added to your balance!", "store.runemc.org"));
        inv.setItem(14, GuiUtil.websiteItem(Material.BUDDING_AMETHYST,
            "1,000 Shards", "Get 1,000 Shards added to your balance!", "store.runemc.org"));
        back(inv); return inv;
    }

    // ── 2. SELL WANDS ─────────────────────────────────────────────────────────
    public Inventory buildSellWands() {
        Inventory inv = make(CAT_SELLWAND);
        // Sell wands for spawners - website purchase
        inv.setItem(11, GuiUtil.websiteItem(Material.BLAZE_ROD,
            "Basic Sell Wand", "Sell all items in a spawner chest with 1 hit!", "store.runemc.org"));
        inv.setItem(13, GuiUtil.websiteItem(Material.BLAZE_ROD,
            "Pro Sell Wand", "Sell all items with a 10% money booster!", "store.runemc.org"));
        inv.setItem(15, GuiUtil.websiteItem(Material.BLAZE_ROD,
            "Elite Sell Wand", "Sell all items with a 25% money booster!", "store.runemc.org"));
        back(inv); return inv;
    }

    // ── 3. RANKS ──────────────────────────────────────────────────────────────
    public Inventory buildRanks() {
        Inventory inv = make(CAT_RANKS);
        // Based on store prices converted to shards + premium (Immortal=£8 ≈ 8*167=1336 shards)
        inv.setItem(10, GuiUtil.websiteItem(Material.BOOK,
            "Titan Rank", "Get the Titan rank on RuneMC! £2.00", "store.runemc.org"));
        inv.setItem(11, GuiUtil.websiteItem(Material.BOOK,
            "Overlord Rank", "Get the Overlord rank on RuneMC! £3.00", "store.runemc.org"));
        inv.setItem(12, GuiUtil.websiteItem(Material.BOOK,
            "Eternal Rank", "Get the Eternal rank on RuneMC! £4.00", "store.runemc.org"));
        inv.setItem(13, GuiUtil.websiteItem(Material.ENCHANTED_BOOK,
            "Ascended Rank", "Get the Ascended rank on RuneMC! £6.00", "store.runemc.org"));
        inv.setItem(14, GuiUtil.websiteItem(Material.ENCHANTED_BOOK,
            "Immortal Rank", "Get the Immortal rank on RuneMC! £8.00", "store.runemc.org"));
        back(inv); return inv;
    }

    // ── 4. BOOSTERS ───────────────────────────────────────────────────────────
    // Left side: Spawner Booster. Right side: Loot+XP Booster. 2x 1h left, 2x 30min right
    public Inventory buildBoosters() {
        Inventory inv = make(CAT_BOOSTERS);
        // Spawner Boosters (left)
        inv.setItem(10, GuiUtil.shopItem(Material.EMERALD, "Spawner Booster 1h",
            "Doubles spawner rates for 1 hour!", "Buy Booster", 800, "Duration: 60 minutes"));
        inv.setItem(19, GuiUtil.shopItem(Material.EMERALD, "Spawner Booster 1h",
            "Doubles spawner rates for 1 hour!", "Buy Booster", 800, "Duration: 60 minutes"));
        // Loot + XP Boosters (right)
        inv.setItem(14, GuiUtil.shopItem(Material.EXPERIENCE_BOTTLE, "Loot & XP Booster 1h",
            "Doubles loot drops and XP gain for 1 hour!", "Buy Booster", 750, "Duration: 60 minutes"));
        inv.setItem(23, GuiUtil.shopItem(Material.EXPERIENCE_BOTTLE, "Loot & XP Booster 30min",
            "Doubles loot drops and XP gain for 30 minutes!", "Buy Booster", 400, "Duration: 30 minutes"));
        back(inv); return inv;
    }

    // ── 5. VAULT STORAGE ──────────────────────────────────────────────────────
    public Inventory buildVault() {
        Inventory inv = make(CAT_VAULT);
        inv.setItem(11, GuiUtil.shopItem(Material.CHEST, "Vault Extension [+1 Row]",
            "Extend the size of all your vaults by 1 row.", "Buy Storage", 1500,
            "Note: Does not grant a new vault."));
        inv.setItem(13, GuiUtil.shopItem(Material.CHEST, "Vault Extension [+2 Rows]",
            "Extend the size of all your vaults by 2 rows.", "Buy Storage", 2500,
            "Best value upgrade!"));
        inv.setItem(15, GuiUtil.shopItem(Material.ENDER_CHEST, "Extra Vault",
            "Unlock an additional vault slot!", "Buy Vault", 5000,
            "You can have multiple vaults!"));
        back(inv); return inv;
    }

    // ── 6. CHAT TAGS ──────────────────────────────────────────────────────────
    public Inventory buildChatTags() {
        Inventory inv = make(CAT_CHATTAGS);
        // RuneMC themed tags
        inv.setItem(10, GuiUtil.shopItem(Material.PLAYER_HEAD, "[#1]",
            "Show off as #1 in chat!", "Buy Tag", 500, "Tag: [#1]"));
        inv.setItem(11, GuiUtil.shopItem(Material.GOLD_INGOT, "[Rich]",
            "Flex your wealth in chat!", "Buy Tag", 400, "Tag: [Rich]"));
        inv.setItem(12, GuiUtil.shopItem(Material.DIAMOND, "[Elite]",
            "Stand out as an elite player!", "Buy Tag", 600, "Tag: [Elite]"));
        inv.setItem(13, GuiUtil.shopItem(Material.NETHER_STAR, "[Legend]",
            "Become a legend on RuneMC!", "Buy Tag", 800, "Tag: [Legend]"));
        inv.setItem(14, GuiUtil.shopItem(Material.BLAZE_POWDER, "[Blaze]",
            "Burn bright in chat!", "Buy Tag", 450, "Tag: [Blaze]"));
        inv.setItem(15, GuiUtil.shopItem(Material.ENDER_EYE, "[Phantom]",
            "Haunt the chat as a Phantom!", "Buy Tag", 500, "Tag: [Phantom]"));
        inv.setItem(16, GuiUtil.shopItem(Material.BEACON, "[God]",
            "Ascend to godhood in chat!", "Buy Tag", 1200, "Tag: [God]"));
        inv.setItem(19, GuiUtil.shopItem(Material.SLIME_BALL, "[Slime]",
            "Bounce around in chat!", "Buy Tag", 300, "Tag: [Slime]"));
        inv.setItem(20, GuiUtil.shopItem(Material.FLINT_AND_STEEL, "[Inferno]",
            "Set the chat on fire!", "Buy Tag", 550, "Tag: [Inferno]"));
        inv.setItem(21, GuiUtil.shopItem(Material.TOTEM_OF_UNDYING, "[Immortal]",
            "Show your immortality!", "Buy Tag", 1000, "Tag: [Immortal]"));
        inv.setItem(22, GuiUtil.shopItem(Material.IRON_SWORD, "[Warrior]",
            "Fight your way through chat!", "Buy Tag", 350, "Tag: [Warrior]"));
        inv.setItem(23, GuiUtil.shopItem(Material.GOLDEN_APPLE, "[MVP]",
            "Be the Most Valuable Player!", "Buy Tag", 700, "Tag: [MVP]"));
        inv.setItem(24, GuiUtil.shopItem(Material.AMETHYST_SHARD, "[RuneMC]",
            "Rep the RuneMC brand!", "Buy Tag", 900, "Tag: [RuneMC]"));
        inv.setItem(25, GuiUtil.shopItem(Material.WITHER_SKELETON_SKULL, "[Wither]",
            "Bring darkness to the chat!", "Buy Tag", 650, "Tag: [Wither]"));
        back(inv); return inv;
    }

    // ── 7. CHAT COLORS ────────────────────────────────────────────────────────
    public Inventory buildChatColors() {
        Inventory inv = make(CAT_CHATCOLOR);
        // All 16 Minecraft colors, 150 shards each
        record ColorEntry(Material mat, String name, int price) {}
        List<ColorEntry> colors = List.of(
            new ColorEntry(Material.RED_DYE,         "Red",           150),
            new ColorEntry(Material.ORANGE_DYE,      "Orange",        150),
            new ColorEntry(Material.YELLOW_DYE,      "Yellow",        150),
            new ColorEntry(Material.LIME_DYE,        "Lime",          150),
            new ColorEntry(Material.GREEN_DYE,       "Green",         150),
            new ColorEntry(Material.CYAN_DYE,        "Cyan",          150),
            new ColorEntry(Material.LIGHT_BLUE_DYE,  "Light Blue",    150),
            new ColorEntry(Material.BLUE_DYE,        "Blue",          150),
            new ColorEntry(Material.PURPLE_DYE,      "Purple",        150),
            new ColorEntry(Material.MAGENTA_DYE,     "Magenta",       150),
            new ColorEntry(Material.PINK_DYE,        "Pink",          150),
            new ColorEntry(Material.WHITE_DYE,       "White",         150),
            new ColorEntry(Material.LIGHT_GRAY_DYE,  "Light Gray",    150),
            new ColorEntry(Material.GRAY_DYE,        "Gray",          150),
            new ColorEntry(Material.BLACK_DYE,       "Black",         150),
            new ColorEntry(Material.BROWN_DYE,       "Brown",         150)
        );

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29};
        for (int i = 0; i < colors.size() && i < slots.length; i++) {
            ColorEntry c = colors.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(c.mat(), c.name() + " Chat Color",
                "Change your chat message color to " + c.name() + "!",
                "Buy Color", c.price(), "Requires ChatColors plugin"));
        }
        back(inv); return inv;
    }

    // ── 8. MONEY ──────────────────────────────────────────────────────────────
    public Inventory buildMoney() {
        Inventory inv = make(CAT_MONEY);
        // Varied money amounts – priced fairly vs shard value
        record MoneyEntry(int money, int shards) {}
        List<MoneyEntry> entries = List.of(
            new MoneyEntry(5_000,    200),
            new MoneyEntry(10_000,   380),
            new MoneyEntry(25_000,   900),
            new MoneyEntry(50_000,  1700),
            new MoneyEntry(100_000, 3200),
            new MoneyEntry(250_000, 7500)
        );
        int[] slots = {10,11,12,13,14,15};
        for (int i = 0; i < entries.size(); i++) {
            MoneyEntry e = entries.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(Material.PAPER,
                String.format("$%,d Money", e.money()),
                String.format("Receive $%,d in-game money!", e.money()),
                "Buy Money", e.shards(),
                String.format("$%,.0f per Shard", (double)e.money()/e.shards())));
        }
        back(inv); return inv;
    }

    // ── 9. GRADIENTS ──────────────────────────────────────────────────────────
    public Inventory buildGradients() {
        Inventory inv = make(CAT_GRADIENTS);
        record GradEntry(Material mat, String name, String desc, int price) {}
        List<GradEntry> grads = List.of(
            new GradEntry(Material.RED_DYE,       "Sunset Gradient",    "Red → Orange → Yellow",  1200),
            new GradEntry(Material.BLUE_DYE,      "Ocean Gradient",     "Dark Blue → Cyan → White",1200),
            new GradEntry(Material.PURPLE_DYE,    "Galaxy Gradient",    "Purple → Blue → Pink",    1500),
            new GradEntry(Material.GREEN_DYE,     "Forest Gradient",    "Dark Green → Lime",       1000),
            new GradEntry(Material.PINK_DYE,      "Cherry Gradient",    "Pink → Rose → White",     1200),
            new GradEntry(Material.ORANGE_DYE,    "Lava Gradient",      "Red → Orange → Yellow",  1300),
            new GradEntry(Material.WHITE_DYE,     "Ice Gradient",       "White → Light Blue → Cyan",1100),
            new GradEntry(Material.MAGENTA_DYE,   "Neon Gradient",      "Purple → Magenta → Pink", 1800),
            new GradEntry(Material.GOLD_INGOT,    "Gold Gradient",      "Yellow → Gold → Orange",  1400),
            new GradEntry(Material.COAL,          "Shadow Gradient",    "Black → Gray → Dark Gray",1000)
        );
        int[] slots = {10,11,12,13,14,19,20,21,22,23};
        for (int i = 0; i < grads.size() && i < slots.length; i++) {
            GradEntry g = grads.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(g.mat(), g.name(),
                g.desc(), "Buy Gradient", g.price(), "Applied to your rank/name tag"));
        }
        back(inv); return inv;
    }

    // ── 10. CRATE KEYS ────────────────────────────────────────────────────────
    public Inventory buildCrates() {
        Inventory inv = make(CAT_CRATES);
        record CrateEntry(Material mat, String name, String desc, int price) {}
        List<CrateEntry> crates = List.of(
            new CrateEntry(Material.PURPLE_CANDLE,  "Common Crate Key",  "Open Common crates for basic rewards!",       250),
            new CrateEntry(Material.LIME_CANDLE,    "Rare Crate Key",    "Open Rare crates for better rewards!",        500),
            new CrateEntry(Material.ORANGE_CANDLE,  "Epic Crate Key",    "Open Epic crates for great rewards!",         900),
            new CrateEntry(Material.YELLOW_CANDLE,  "Legendary Key",     "Open Legendary crates for amazing rewards!", 1500),
            new CrateEntry(Material.RED_CANDLE,     "Mythic Crate Key",  "Open Mythic crates for the rarest rewards!", 3000),
            new CrateEntry(Material.TRIAL_KEY,      "RuneMC Special Key","Open the exclusive RuneMC Special crate!",   5000)
        );
        int[] slots = {10,11,12,13,14,15};
        for (int i = 0; i < crates.size(); i++) {
            CrateEntry c = crates.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(c.mat(), c.name(),
                c.desc(), "Buy Key", c.price(), null));
        }
        back(inv); return inv;
    }

    // ── 11. GLOWS ─────────────────────────────────────────────────────────────
    public Inventory buildGlows() {
        Inventory inv = make(CAT_GLOWS);
        record GlowEntry(Material mat, String name, int price) {}
        List<GlowEntry> glows = List.of(
            new GlowEntry(Material.RED_DYE,        "Red Glow",        300),
            new GlowEntry(Material.ORANGE_DYE,     "Orange Glow",     300),
            new GlowEntry(Material.YELLOW_DYE,     "Yellow Glow",     300),
            new GlowEntry(Material.LIME_DYE,       "Lime Glow",       300),
            new GlowEntry(Material.GREEN_DYE,      "Green Glow",      300),
            new GlowEntry(Material.CYAN_DYE,       "Cyan Glow",       300),
            new GlowEntry(Material.LIGHT_BLUE_DYE, "Light Blue Glow", 300),
            new GlowEntry(Material.BLUE_DYE,       "Blue Glow",       300),
            new GlowEntry(Material.PURPLE_DYE,     "Purple Glow",     300),
            new GlowEntry(Material.PINK_DYE,       "Pink Glow",       300),
            new GlowEntry(Material.WHITE_DYE,      "White Glow",      300),
            new GlowEntry(Material.GRAY_DYE,       "Gray Glow",       300),
            new GlowEntry(Material.BLACK_DYE,      "Black Glow",      350),
            new GlowEntry(Material.NETHER_STAR,    "Rainbow Glow",    800),
            new GlowEntry(Material.AMETHYST_SHARD, "Crystal Glow",    600)
        );
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28};
        for (int i = 0; i < glows.size() && i < slots.length; i++) {
            GlowEntry g = glows.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(g.mat(), g.name(),
                "Glow with " + g.name().replace(" Glow","") + " around your player!",
                "Buy Glow", g.price(), "Requires Glow plugin"));
        }
        back(inv); return inv;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Inventory make(String title) {
        Inventory inv = Bukkit.createInventory(null, 36,
            Component.text(title, NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        GuiUtil.fillAll(inv);
        return inv;
    }

    private void back(Inventory inv) {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta  = back.getItemMeta();
        meta.displayName(Component.text("← Back to Main Menu", NamedTextColor.RED)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        back.setItemMeta(meta);
        inv.setItem(BACK_SLOT, back);
    }
}
