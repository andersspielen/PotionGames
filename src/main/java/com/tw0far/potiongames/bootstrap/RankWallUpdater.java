package com.tw0far.potiongames.bootstrap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.sign.Side;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;

/**
 * Periodically refreshes the stats wall (top-3 heads + signs).
 * Database access runs asynchronously; only the final block and sign
 * updates are applied on the global region scheduler.
 */
public class RankWallUpdater {
    private static final long PERIOD_SECONDS = 60;

    private record RankEntry(String uuid, String name, int place, int wins, double kd) { }

    private final PotionGamesX plugin;

    public RankWallUpdater(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public ScheduledTask start() {
        return plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> {
            try {
                List<RankEntry> entries = queryTopPlayers();
                plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> applyRankWall(entries));
            } catch (Exception ex) {
                // Never let an exception cancel this repeating task
                plugin.getComponentLogger().info(Messages.RankwallCouldNotUpdate());
            }
        }, 5, PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    private boolean isConfigured() {
        return plugin.getConfig().contains("pg.RankWall.headp1")
            && plugin.getConfig().contains("pg.RankWall.headp2")
            && plugin.getConfig().contains("pg.RankWall.headp3")
            && plugin.getConfig().contains("pg.RankWall.signp1")
            && plugin.getConfig().contains("pg.RankWall.signp2")
            && plugin.getConfig().contains("pg.RankWall.signp3");
    }

    /** Runs on the async scheduler - pure JDBC, no world access. */
    private List<RankEntry> queryTopPlayers() throws SQLException {
        List<RankEntry> entries = new ArrayList<>();
        if (!isConfigured()) {
            return entries;
        }
        try (ResultSet rs = plugin.getDatabaseManager().query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
            if (rs == null) {
                return entries;
            }
            int place = 0;
            while (rs.next()) {
                place++;
                String uuid = rs.getString("UUID");
                if (uuid == null) {
                    continue;
                }
                String name = resolveName(uuid);
                entries.add(new RankEntry(uuid, name, place,
                        plugin.getDatabaseManager().getWins(uuid),
                        plugin.getDatabaseManager().getKD(uuid)));
            }
        }
        return entries;
    }

    private String resolveName(String uuid) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            String name = player.getName();
            return name != null ? name : "Unknown";
        } catch (IllegalArgumentException e) {
            return "Unknown";
        }
    }

    /** Runs on the global region scheduler - world access only, no blocking calls. */
    private void applyRankWall(List<RankEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        Location[] heads = {
            plugin.getConfig().getLocation("pg.RankWall.headp1"),
            plugin.getConfig().getLocation("pg.RankWall.headp2"),
            plugin.getConfig().getLocation("pg.RankWall.headp3"),
        };
        Location[] signs = {
            plugin.getConfig().getLocation("pg.RankWall.signp1"),
            plugin.getConfig().getLocation("pg.RankWall.signp2"),
            plugin.getConfig().getLocation("pg.RankWall.signp3"),
        };

        for (RankEntry entry : entries) {
            int idx = entry.place() - 1;
            UUID uuid;
            try {
                uuid = UUID.fromString(entry.uuid());
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            Location headLoc = heads[idx];
            if (headLoc != null && headLoc.getBlock().getState() instanceof Skull skull) {
                PlayerProfile profile = Bukkit.createProfile(uuid);
                skull.setProfile(ResolvableProfile.resolvableProfile(profile));
                skull.update();
            }

            Location signLoc = signs[idx];
            if (signLoc != null && signLoc.getBlock().getState() instanceof Sign sign) {
                sign.getSide(Side.FRONT).line(0, Messages.SignPlace(entry.place()));
                sign.getSide(Side.FRONT).line(1, Component.text(entry.name()));
                sign.getSide(Side.FRONT).line(2, Messages.SignWins(entry.wins()));
                sign.getSide(Side.FRONT).line(3, Messages.SignKD(entry.kd()));
                sign.update();
            }
        }
    }
}
