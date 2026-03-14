package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ShardManager {

    private static final String OBJ = "shards";
    private final ShardSystem plugin;
    private final Scoreboard scoreboard;
    private Objective objective;

    public ShardManager(ShardSystem plugin) {
        this.plugin = plugin;
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        objective  = scoreboard.getObjective(OBJ);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                OBJ, Criteria.DUMMY,
                net.kyori.adventure.text.Component.text("Shards"),
                RenderType.INTEGER);
        }
    }

    public int getShards(OfflinePlayer p) {
        Score s = objective.getScore(p.getName());
        return s.isScoreSet() ? s.getScore() : 0;
    }
    public void setShards(OfflinePlayer p, int v)   { objective.getScore(p.getName()).setScore(Math.max(0, v)); }
    public void addShards(OfflinePlayer p, int v)   { setShards(p, getShards(p) + v); }
    public boolean removeShards(OfflinePlayer p, int v) {
        int cur = getShards(p);
        if (cur < v) return false;
        setShards(p, cur - v);
        return true;
    }
    public boolean hasShards(OfflinePlayer p, int v) { return getShards(p) >= v; }
}
