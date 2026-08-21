package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * /pg top [type] - Show top players leaderboard
 * Types: kills, deaths, wins, kd (kill/death ratio)
 * Permission: pg.top
 */
public class TopCommand implements ICommand {
    private static final String DIVIDER = "──────────────────────────────────";

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
    public boolean execute(CommandSender sender, String[] args) {
        if (!plugin.getDatabaseManager().isConnected()) {
            sender.sendMessage(Component.text("Statistics database is not connected.").color(NamedTextColor.RED));
            return true;
        }

        String type = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "kills";
        String column;
        String label;
        switch (type) {
            case "kills" -> { column = "KILLS"; label = "Kills"; }
            case "deaths" -> { column = "DEATHS"; label = "Deaths"; }
            case "wins" -> { column = "WINS"; label = "Wins"; }
            case "kd" -> { column = "KD"; label = "K/D Ratio"; }
            default -> {
                sender.sendMessage(Component.text("Unknown leaderboard type: " + type).color(NamedTextColor.RED));
                sender.sendMessage(Component.text("Use /pg top [kills|deaths|wins|kd]").color(NamedTextColor.GRAY));
                return true;
            }
        }

        final String queryType = type;
        final String query = "SELECT UUID, " + column + " FROM Stats ORDER BY " + column + " DESC LIMIT 10";

        sender.sendMessage(Component.text(DIVIDER).color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("Top 10 Players by " + label).color(NamedTextColor.GOLD));

        // Query runs asynchronously; results are delivered on the sender's thread
        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            List<String> lines = new ArrayList<>();
            try (ResultSet resultSet = plugin.getDatabaseManager().query(query)) {
                if (resultSet == null) {
                    lines.add("!Unable to read leaderboard data right now.");
                } else {
                    int rank = 1;
                    boolean hasEntries = false;
                    while (resultSet.next()) {
                        hasEntries = true;
                        String playerName = resolvePlayerName(resultSet.getString("UUID"));
                        lines.add(rank + ". " + playerName + " - " + formatValue(queryType, resultSet));
                        rank++;
                    }
                    if (!hasEntries) {
                        lines.add("No leaderboard entries found yet.");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to build leaderboard for type " + queryType + ": " + e.getMessage());
                lines.add("!Failed to load leaderboard data.");
            }

            Runnable deliver = () -> {
                for (String line : lines) {
                    if (line.startsWith("!")) {
                        sender.sendMessage(Component.text(line.substring(1)).color(NamedTextColor.RED));
                    } else {
                        sender.sendMessage(Component.text(line).color(NamedTextColor.GRAY));
                    }
                }
                sender.sendMessage(Component.text(DIVIDER).color(NamedTextColor.DARK_GRAY));
            };
            deliverOnSenderThread(sender, deliver);
        });
        return true;
    }

    private void deliverOnSenderThread(CommandSender sender, Runnable deliver) {
        if (sender instanceof org.bukkit.entity.Player player) {
            player.getScheduler().execute(plugin, deliver, () -> { }, 1L);
        } else {
            deliver.run();
        }
    }

    private String resolvePlayerName(String uuidValue) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuidValue));
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
            case "deaths" -> Integer.toString(resultSet.getInt("DEATHS"));
            case "wins" -> Integer.toString(resultSet.getInt("WINS"));
            case "kd" -> String.format(Locale.ROOT, "%.3f", resultSet.getDouble("KD"));
            default -> Integer.toString(resultSet.getInt("KILLS"));
        };
    }

    @Override
    public String getUsage() {
        return "/pg top [kills|deaths|wins|kd]";
    }
}
