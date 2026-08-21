package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

/**
 * /pg top [type] - Show top players leaderboard
 * Types: kills, deaths, wins, kd (kill/death ratio)
 * Permission: pg.top
 */
public class TopCommand implements ICommand {
    private final PotionGamesX plugin;

    public TopCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getPermission() {
        return "pg.top";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        if (!plugin.getDatabaseManager().isConnected()) {
            player.sendMessage(Component.text("Statistics database is not connected.").color(NamedTextColor.RED));
            return true;
        }

        String type = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "kills";
        String column;
        String label;

        switch (type) {
            case "kills" -> {
                column = "KILLS";
                label = "Kills";
            }
            case "deaths" -> {
                column = "DEATHS";
                label = "Deaths";
            }
            case "wins" -> {
                column = "WINS";
                label = "Wins";
            }
            case "kd" -> {
                column = "KD";
                label = "K/D Ratio";
            }
            default -> {
                player.sendMessage(Component.text("Unknown leaderboard type: " + type).color(NamedTextColor.RED));
                player.sendMessage(Component.text("Use /pg top [kills|deaths|wins|kd]").color(NamedTextColor.GRAY));
                return true;
            }
        }

        player.sendMessage(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.text("Top 10 Players by " + label).color(NamedTextColor.GOLD));

        String query = "SELECT UUID, " + column + " FROM Stats ORDER BY " + column + " DESC LIMIT 10";
        try (ResultSet resultSet = plugin.getDatabaseManager().query(query)) {
            if (resultSet == null) {
                player.sendMessage(Component.text("Unable to read leaderboard data right now.").color(NamedTextColor.RED));
                return true;
            }

            int rank = 1;
            boolean hasEntries = false;
            while (resultSet.next()) {
                hasEntries = true;
                String uuidValue = resultSet.getString("UUID");
                String playerName = resolvePlayerName(uuidValue);
                String value = formatValue(type, resultSet);
                player.sendMessage(Component.text(rank + ". " + playerName + " - " + value).color(NamedTextColor.GRAY));
                rank++;
            }

            if (!hasEntries) {
                player.sendMessage(Component.text("No leaderboard entries found yet.").color(NamedTextColor.GRAY));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to build leaderboard for type " + type + ": " + e.getMessage());
            player.sendMessage(Component.text("Failed to load leaderboard data.").color(NamedTextColor.RED));
        }

        player.sendMessage(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY));
        return true;
    }

    private String resolvePlayerName(String uuidValue) {
        try {
            UUID uuid = UUID.fromString(uuidValue);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (name != null && !name.isBlank()) {
                return name;
            }
        } catch (IllegalArgumentException ignored) {
        }

        if (uuidValue == null || uuidValue.isBlank()) {
            return "Unknown";
        }
        return uuidValue.length() > 8 ? uuidValue.substring(0, 8) : uuidValue;
    }

    private String formatValue(String type, ResultSet resultSet) throws SQLException {
        return switch (type) {
            case "kd" -> String.format(Locale.ROOT, "%.3f", resultSet.getDouble("KD"));
            case "deaths" -> Integer.toString(resultSet.getInt("DEATHS"));
            case "wins" -> Integer.toString(resultSet.getInt("WINS"));
            default -> Integer.toString(resultSet.getInt("KILLS"));
        };
    }

    @Override
    public String getUsage() {
        return "/pg top [kills|deaths|wins|kd]";
    }
}
