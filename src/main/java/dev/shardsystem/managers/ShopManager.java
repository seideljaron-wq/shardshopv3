package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.*;

public class ShopManager {

    private final ShardSystem plugin;
    private final Map<Integer, ShopItem> legacyItems = new LinkedHashMap<>();

    static final int[] CONTENT_SLOTS = {11,12,13,14,15,20,21,22,23,24,2,3,4,5,6};
    static final Set<Integer> BORDER_SLOTS = Set.of(0,1,7,8,9,10,16,17,18,19,25,26);

    public ShopManager(ShardSystem plugin) { this.plugin = plugin; loadLegacy(); }

    public static class ShopItem {
        public final ItemStack item;
        public final int price;
        public ShopItem(ItemStack item, int price) { this.item = item; this.price = price; }
    }

    public Map<Integer, ShopItem> getLegacyItems() { return legacyItems; }
    public boolean isBorderSlot(int s) { return BORDER_SLOTS.contains(s); }

    public int addLegacyItem(ItemStack item, int price) {
        for (int slot : CONTENT_SLOTS) {
            if (!legacyItems.containsKey(slot)) {
                legacyItems.put(slot, new ShopItem(item, price));
                saveLegacy();
                return slot;
            }
        }
        return -1;
    }

    public boolean removeLegacyAtSlot(int slot) {
        if (legacyItems.remove(slot) != null) { saveLegacy(); return true; }
        return false;
    }

    private void loadLegacy() {
        legacyItems.clear();
        ConfigurationSection s = plugin.getConfig().getConfigurationSection("shop.items");
        if (s == null) return;
        for (String key : s.getKeys(false)) {
            try {
                int slot = Integer.parseInt(key);
                ItemStack item = s.getItemStack(key + ".item");
                int price      = s.getInt(key + ".price");
                if (item != null) legacyItems.put(slot, new ShopItem(item, price));
            } catch (NumberFormatException ignored) {}
        }
    }

    public void saveLegacy() {
        plugin.getConfig().set("shop.items", null);
        for (Map.Entry<Integer, ShopItem> e : legacyItems.entrySet()) {
            plugin.getConfig().set("shop.items." + e.getKey() + ".item",  e.getValue().item);
            plugin.getConfig().set("shop.items." + e.getKey() + ".price", e.getValue().price);
        }
        plugin.saveConfig();
    }

    public static ItemStack withPriceLore(ItemStack item, int price) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) return clone;
        List<Component> lore = new ArrayList<>();
        if (meta.lore() != null) {
            meta.lore().forEach(l -> {
                String p = PlainTextComponentSerializer.plainText().serialize(l);
                if (!p.startsWith("Price:")) lore.add(l);
            });
        }
        lore.add(Component.text("Price: ", NamedTextColor.GRAY)
            .append(Component.text(price + " Shards", NamedTextColor.LIGHT_PURPLE)));
        meta.lore(lore);
        clone.setItemMeta(meta);
        return clone;
    }

    public static ItemStack stripPriceLore(ItemStack item) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null || meta.lore() == null) return clone;
        List<Component> lore = new ArrayList<>();
        meta.lore().forEach(l -> {
            String p = PlainTextComponentSerializer.plainText().serialize(l);
            if (!p.startsWith("Price:")) lore.add(l);
        });
        meta.lore(lore);
        clone.setItemMeta(meta);
        return clone;
    }

    public static String getDisplayName(ItemStack item) {
        if (item == null) return "Unknown";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null)
            return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        String raw = item.getType().name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String w : raw.split(" "))
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0)))
                .append(w.substring(1).toLowerCase()).append(" ");
        return sb.toString().trim();
    }
}
