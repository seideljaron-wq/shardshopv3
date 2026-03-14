package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import java.util.*;

public class AdminManager {
    private final ShardSystem plugin;
    private final List<UUID> admins = new ArrayList<>();

    public AdminManager(ShardSystem plugin) { this.plugin = plugin; load(); }

    private void load() {
        admins.clear();
        for (String s : plugin.getConfig().getStringList("admins")) {
            try { admins.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
        }
    }
    public boolean isAdmin(UUID u)  { return admins.contains(u); }
    public void addAdmin(UUID u)    { if (!admins.contains(u)) { admins.add(u); save(); } }
    public boolean removeAdmin(UUID u) { boolean r = admins.remove(u); if (r) save(); return r; }
    private void save() {
        List<String> l = new ArrayList<>();
        admins.forEach(u -> l.add(u.toString()));
        plugin.getConfig().set("admins", l);
        plugin.saveConfig();
    }
}
