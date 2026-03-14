package dev.shardsystem.gui;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmGUI {

    private final ShardSystem plugin;

    public ConfirmGUI(ShardSystem plugin) { this.plugin = plugin; }

    public Inventory build(ItemStack shopItem, int price) {
        String name = getPlainName(shopItem);
        Inventory inv = Bukkit.createInventory(null, 27,
            "Confirm: " + name);

        GuiUtil.fillAll(inv);

        // Left = cancel (red), right = confirm (green)
        ItemStack cancel = pane(Material.RED_STAINED_GLASS_PANE,
            Component.text("✗ Cancel", NamedTextColor.RED));
        ItemStack confirm = pane(Material.GREEN_STAINED_GLASS_PANE,
            Component.text("✔ Confirm Purchase", NamedTextColor.GREEN));

        for (int s : new int[]{0,1,2,9,10,11,18,19,20}) inv.setItem(s, cancel);
        for (int s : new int[]{6,7,8,15,16,17,24,25,26}) inv.setItem(s, confirm);

        // Center item (slot 13) – clean, no price lore
        inv.setItem(13, ShopManager.stripPriceLore(shopItem.clone()));

        // Price info at slot 4
        ItemStack priceItem = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = priceItem.getItemMeta();
        meta.displayName(Component.text("Cost: " + String.format("%,d", price) + " Shards",
            NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text("Your balance: ", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));
        priceItem.setItemMeta(meta);
        inv.setItem(4, priceItem);

        return inv;
    }

    private ItemStack pane(Material mat, Component name) {
        ItemStack p = new ItemStack(mat);
        ItemMeta m  = p.getItemMeta();
        m.displayName(name.decoration(TextDecoration.ITALIC, false));
        p.setItemMeta(m);
        return p;
    }

    private String getPlainName(ItemStack item) {
        if (item == null) return "Item";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null)
            return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return item.getType().name();
    }
}
