package dev.shardsystem;

import dev.shardsystem.commands.ShardAdminCommand;
import dev.shardsystem.commands.ShardGiveCommand;
import dev.shardsystem.commands.ShardShopCommand;
import dev.shardsystem.listeners.GUIClickListener;
import dev.shardsystem.managers.AdminManager;
import dev.shardsystem.managers.DiscordLogger;
import dev.shardsystem.managers.ShardManager;
import dev.shardsystem.managers.ShopManager;
import dev.shardsystem.managers.VaultManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ShardSystem extends JavaPlugin {

    private static ShardSystem instance;
    private ShardManager shardManager;
    private AdminManager adminManager;
    private ShopManager shopManager;
    private DiscordLogger discordLogger;
    private VaultManager vaultManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Hook Vault
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                getLogger().info("Vault economy hooked!");
            }
        }

        shardManager  = new ShardManager(this);
        adminManager  = new AdminManager(this);
        shopManager   = new ShopManager(this);
        discordLogger = new DiscordLogger(this);
        vaultManager  = new VaultManager(this);

        ShardAdminCommand adminCmd = new ShardAdminCommand(this);
        getCommand("shard-admin").setExecutor(adminCmd);
        getCommand("shard-admin").setTabCompleter(adminCmd);

        ShardGiveCommand giveCmd = new ShardGiveCommand(this);
        getCommand("shard-give").setExecutor(giveCmd);
        getCommand("shard-give").setTabCompleter(giveCmd);

        ShardShopCommand shopCmd = new ShardShopCommand(this);
        getCommand("shardshop").setExecutor(shopCmd);
        getCommand("shardshop").setTabCompleter(shopCmd);

        getServer().getPluginManager().registerEvents(new GUIClickListener(this), this);

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ShardsExpansion(this).register();
            getLogger().info("PlaceholderAPI hooked – %shards_amount% registered.");
        }

        getLogger().info("ShardSystem v3 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ShardSystem v3 disabled.");
    }

    public static ShardSystem getInstance()     { return instance; }
    public ShardManager getShardManager()       { return shardManager; }
    public AdminManager getAdminManager()       { return adminManager; }
    public ShopManager getShopManager()         { return shopManager; }
    public DiscordLogger getDiscordLogger()     { return discordLogger; }
    public VaultManager getVaultManager()       { return vaultManager; }
    public Economy getEconomy()                 { return economy; }

    public boolean isShopAdmin(org.bukkit.entity.Player p) {
        return p.isOp()
            || p.hasPermission("shards.shop.manage")
            || adminManager.isAdmin(p.getUniqueId());
    }
}
