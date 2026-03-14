package dev.shardsystem.listeners;

import dev.shardsystem.ShardSystem;
import dev.shardsystem.gui.CategoryGUI;
import dev.shardsystem.gui.ConfirmGUI;
import dev.shardsystem.gui.MainMenuGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIClickListener implements Listener {

    private final ShardSystem plugin;
    private final CategoryGUI catGUI;

    // All known GUI titles – used to cancel ANY click inside our GUIs
    private static final String[] KNOWN_TITLES = {
        MainMenuGUI.TITLE,
        CategoryGUI.CAT_SHARDS,
        CategoryGUI.CAT_SELLWAND,
        CategoryGUI.CAT_RANKS,
        CategoryGUI.CAT_BOOSTERS,
        CategoryGUI.CAT_VAULT,
        CategoryGUI.CAT_CHATTAGS,
        CategoryGUI.CAT_CHATCOLOR,
        CategoryGUI.CAT_MONEY,
        CategoryGUI.CAT_GRADIENTS,
        CategoryGUI.CAT_CRATES,
        CategoryGUI.CAT_GLOWS,
    };

    public GUIClickListener(ShardSystem plugin) {
        this.plugin = plugin;
        this.catGUI = new CategoryGUI(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText()
            .serialize(event.getView().title());

        // ── Always cancel ALL clicks inside our GUIs ──────────────────────────
        // This includes shift-clicks, number keys, dragging – everything
        if (!isOurGUI(title)) return;
        event.setCancelled(true);

        // Also cancel if they try to move items via keyboard/shift
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) return;

        ItemStack clicked = event.getCurrentItem();

        // Ignore clicks on empty slots or glass panes
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        // Ignore clicks in player's own inventory (bottom half)
        int slot = event.getRawSlot();
        if (slot >= event.getView().getTopInventory().getSize()) return;

        // ── Main Menu ──────────────────────────────────────────────────────────
        if (title.equals(MainMenuGUI.TITLE)) {
            handleMainMenu(player, slot);
            return;
        }

        // ── Back button ────────────────────────────────────────────────────────
        if (slot == CategoryGUI.BACK_SLOT && clicked.getType() == Material.ARROW) {
            reopen(player, new MainMenuGUI(plugin).build(player));
            return;
        }

        // ── Confirm GUI ────────────────────────────────────────────────────────
        if (title.startsWith("Confirm: ")) {
            handleConfirm(player, slot, clicked, event.getInventory());
            return;
        }

        // ── Website-only categories ────────────────────────────────────────────
        if (title.equals(CategoryGUI.CAT_SHARDS) ||
            title.equals(CategoryGUI.CAT_SELLWAND) ||
            title.equals(CategoryGUI.CAT_RANKS)) {
            sendWebsiteMessage(player);
            return;
        }

        // ── Purchasable categories → open confirm ──────────────────────────────
        openConfirm(player, clicked, title);
    }

    // ── Main menu navigation ──────────────────────────────────────────────────

    private void handleMainMenu(Player player, int slot) {
        switch (slot) {
            case 20 -> reopen(player, catGUI.buildShards());
            case 21 -> reopen(player, catGUI.buildSellWands());
            case 22 -> reopen(player, catGUI.buildRanks());
            case 23 -> reopen(player, catGUI.buildBoosters());
            case 24 -> reopen(player, catGUI.buildVault());
            case 29 -> reopen(player, catGUI.buildChatTags());
            case 30 -> reopen(player, catGUI.buildChatColors());
            case 31 -> reopen(player, catGUI.buildMoney());
            case 32 -> reopen(player, catGUI.buildGradients());
            case 33 -> reopen(player, catGUI.buildCrates());
            case 49 -> reopen(player, catGUI.buildGlows());
        }
    }

    // ── Confirm ───────────────────────────────────────────────────────────────

    private void openConfirm(Player player, ItemStack item, String category) {
        int price = extractPrice(item);
        if (price <= 0) return;
        player.setMetadata("shop_cat",
            new org.bukkit.metadata.FixedMetadataValue(plugin, category));
        reopen(player, new ConfirmGUI(plugin).build(item, price));
    }

    private void handleConfirm(Player player, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getType() == Material.GREEN_STAINED_GLASS_PANE) {
            ItemStack center = inv.getItem(13);
            ItemStack priceDisplay = inv.getItem(4);
            if (center == null || priceDisplay == null) {
                reopen(player, new MainMenuGUI(plugin).build(player));
                return;
            }

            int price = extractPriceFromName(getPlainName(priceDisplay));
            String category = player.hasMetadata("shop_cat")
                ? player.getMetadata("shop_cat").get(0).asString() : "Shop";
            player.removeMetadata("shop_cat", plugin);

            if (price <= 0) {
                player.sendMessage(Component.text("✗ Could not read price.", NamedTextColor.RED));
                reopen(player, new MainMenuGUI(plugin).build(player));
                return;
            }

            if (!plugin.getShardManager().hasShards(player, price)) {
                player.sendMessage(
                    Component.text("✗ You don't have enough Shards! ", NamedTextColor.RED)
                        .append(Component.text("Need: ", NamedTextColor.GRAY))
                        .append(Component.text(String.format("%,d", price) + " Shards",
                            NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" | You have: ", NamedTextColor.GRAY))
                        .append(Component.text(
                            String.format("%,d", plugin.getShardManager().getShards(player)) + " Shards",
                            NamedTextColor.LIGHT_PURPLE))
                );
                reopen(player, new MainMenuGUI(plugin).build(player));
                return;
            }

            plugin.getShardManager().removeShards(player, price);
            int remaining = plugin.getShardManager().getShards(player);
            String itemName = getPlainName(center);

            // Special: Money → give via Vault
            if (category.equals(CategoryGUI.CAT_MONEY)) {
                double money = parseMoney(itemName);
                if (money > 0 && plugin.getVaultManager().hasVault()) {
                    plugin.getVaultManager().giveMoney(player, money);
                    player.sendMessage(
                        Component.text("✔ You received ", NamedTextColor.GREEN)
                            .append(Component.text("$" + String.format("%,.0f", money),
                                NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                            .append(Component.text(" in-game money!", NamedTextColor.GREEN)));
                } else {
                    player.sendMessage(Component.text(
                        "✔ Purchase logged! Money will be added shortly.", NamedTextColor.GREEN));
                }
            } else {
                player.sendMessage(
                    Component.text("✔ Purchase successful! ", NamedTextColor.GREEN)
                        .append(Component.text(itemName, NamedTextColor.AQUA)));
                player.sendMessage(Component.text(
                    "  Your purchase has been logged and will be processed!",
                    NamedTextColor.GRAY));
            }

            player.sendMessage(
                Component.text("  Remaining balance: ", NamedTextColor.GRAY)
                    .append(Component.text(String.format("%,d", remaining) + " Shards",
                        NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)));

            plugin.getDiscordLogger().logPurchase(
                player.getName(), category, itemName, price, remaining);

            reopen(player, new MainMenuGUI(plugin).build(player));

        } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            player.removeMetadata("shop_cat", plugin);
            reopen(player, new MainMenuGUI(plugin).build(player));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void sendWebsiteMessage(Player player) {
        String url = plugin.getConfig().getString("store-url", "store.runemc.org");
        player.sendMessage(Component.text(""));
        player.sendMessage(
            Component.text("  ✦ RuneMC Store: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(url, NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED)));
        player.sendMessage(Component.text(""));
        player.closeInventory();
    }

    private int extractPrice(ItemStack item) {
        if (item == null) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.lore() == null) return 0;
        for (Component line : meta.lore()) {
            String plain = PlainTextComponentSerializer.plainText().serialize(line);
            if (plain.contains("Shards:")) {
                String num = plain.replaceAll("[^0-9]", "");
                try { return Integer.parseInt(num); } catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    private int extractPriceFromName(String name) {
        // "Cost: 1,500 Shards" → 1500
        String num = name.replaceAll("[^0-9]", "");
        try { return Integer.parseInt(num); } catch (NumberFormatException e) { return 0; }
    }

    private double parseMoney(String name) {
        String num = name.replaceAll("[^0-9]", "");
        try { return Double.parseDouble(num); } catch (NumberFormatException e) { return 0; }
    }

    private String getPlainName(ItemStack item) {
        if (item == null) return "Unknown";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null)
            return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return item.getType().name();
    }

    private boolean isOurGUI(String title) {
        // Check exact matches first
        if (title.startsWith("Confirm: ")) return true;
        for (String known : KNOWN_TITLES) {
            if (title.equals(known)) return true;
        }
        return false;
    }

    private void reopen(Player player, Inventory inv) {
        plugin.getServer().getScheduler().runTask(plugin,
            () -> player.openInventory(inv));
    }
}
