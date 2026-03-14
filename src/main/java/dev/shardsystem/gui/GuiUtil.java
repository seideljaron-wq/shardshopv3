package dev.shardsystem.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiUtil {

    public static ItemStack glass() {
        ItemStack p = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = p.getItemMeta(); m.displayName(Component.empty()); p.setItemMeta(m);
        return p;
    }

    public static ItemStack blackGlass() {
        ItemStack p = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta m = p.getItemMeta(); m.displayName(Component.empty()); p.setItemMeta(m);
        return p;
    }

    public static ItemStack buildItem(Material mat, Component name, Component... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        for (Component l : loreLines)
            lore.add(l.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack shopItem(Material mat, String name, String description,
                                     String priceLabel, int price, String extraInfo) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();

        meta.displayName(Component.text(name, NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());

        // Description
        if (description != null && !description.isEmpty()) {
            lore.add(Component.text("Information", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
            // Word-wrap long descriptions
            for (String line : wrapText(description, 30)) {
                lore.add(Component.text(line, NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            }
            lore.add(Component.empty());
        }

        // Price
        lore.add(Component.text(priceLabel + " ", NamedTextColor.LIGHT_PURPLE)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("  ✦ Shards: ", NamedTextColor.WHITE)
            .append(Component.text(String.format("%,d", price), NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD))
            .decoration(TextDecoration.ITALIC, false));

        if (extraInfo != null && !extraInfo.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text(extraInfo, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        lore.add(Component.text("▶ ", NamedTextColor.YELLOW)
            .append(Component.text("CLICK", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                .decorate(TextDecoration.UNDERLINED))
            .append(Component.text(" to Purchase", NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack websiteItem(Material mat, String name, String description, String url) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (description != null) {
            lore.add(Component.text("Information", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
            for (String line : wrapText(description, 30))
                lore.add(Component.text(line, NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
        }
        lore.add(Component.text("Purchase Shards", NamedTextColor.LIGHT_PURPLE)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("  ♪ ", NamedTextColor.YELLOW)
            .append(Component.text(url, NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED))
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("▶ ", NamedTextColor.YELLOW)
            .append(Component.text("CLICK", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                .decorate(TextDecoration.UNDERLINED))
            .append(Component.text(" to see website in chat", NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void fillBorders(org.bukkit.inventory.Inventory inv) {
        ItemStack g = glass();
        for (int i = 0; i < inv.getSize(); i++) {
            int row = i / 9, col = i % 9;
            if (row == 0 || row == inv.getSize()/9 - 1 || col == 0 || col == 8)
                inv.setItem(i, g);
        }
    }

    public static void fillAll(org.bukkit.inventory.Inventory inv) {
        ItemStack g = glass();
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, g);
    }

    private static List<String> wrapText(String text, int maxLen) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            if (cur.length() + w.length() + 1 > maxLen) {
                if (cur.length() > 0) { lines.add(cur.toString()); cur = new StringBuilder(); }
            }
            if (cur.length() > 0) cur.append(" ");
            cur.append(w);
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }
}
