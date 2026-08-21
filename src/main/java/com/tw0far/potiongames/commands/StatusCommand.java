package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * /pg status - Show active lobbies, player counts, and database connection status
 */
public class StatusCommand implements ICommand {
    private final PotionGamesX plugin;

    public StatusCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPermission() {
        return "pg.status";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        var game = plugin.getGame();
        var lobbies = game.getLobbies();

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        player.sendMessage(Component.text("PotionGamesX Status").color(NamedTextColor.GOLD));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Server:").color(NamedTextColor.GOLD)));

        player.sendMessage(formatStatusLine("  Players Active", game.getActivePlayers().size() + ""));
        player.sendMessage(formatStatusLine("  Players Spectating", game.getSpectatorPlayers().size() + ""));
        player.sendMessage(formatStatusLine("  Total Players", (game.getActivePlayers().size() + game.getSpectatorPlayers().size()) + ""));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Lobbies:").color(NamedTextColor.GOLD)));

        if (lobbies.isEmpty()) {
            player.sendMessage(Component.text("  No active lobbies").color(NamedTextColor.GRAY));
        } else {
            for (Lobby lobby : lobbies) {
                int lobbyId = lobby.getId();
                int activePlayers = lobby.getActivePlayers().size();
                int spectators = lobby.getSpectatorPlayers().size();
                String state = lobby.getState().name();

                Component lobbyInfo = Component.text("  Lobby: ").color(NamedTextColor.GRAY)
                    .append(Component.text(lobbyId + "").color(NamedTextColor.AQUA))
                    .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(activePlayers + "/" + spectators).color(NamedTextColor.YELLOW))
                    .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(state).color(getStateColor(state)));

                player.sendMessage(lobbyInfo);
            }
        }

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Database:").color(NamedTextColor.GOLD)));

        boolean mysqlActive = plugin.getConfigManager().isActivateMysql();
        boolean connected = plugin.getDatabaseManager().isConnected();
        player.sendMessage(formatStatusLine("  Mode", mysqlActive ? "MySQL" : "SQLite"));
        player.sendMessage(Component.text("  Connection").color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(connected ? "✓ Connected" : "✗ Disconnected").color(connected ? NamedTextColor.GREEN : NamedTextColor.RED)));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        return true;
    }

    private Component formatStatusLine(String key, String value) {
        return Component.text(key).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(value).color(NamedTextColor.GREEN));
    }

    private NamedTextColor getStateColor(String state) {
        return switch (state) {
            case "WAITING" -> NamedTextColor.BLUE;
            case "PREPARING" -> NamedTextColor.AQUA;
            case "INGAME" -> NamedTextColor.GREEN;
            case "DEATHMATCH" -> NamedTextColor.RED;
            case "ENDING" -> NamedTextColor.YELLOW;
            case "RESET" -> NamedTextColor.GRAY;
            default -> NamedTextColor.WHITE;
        };
    }

    @Override
    public String getUsage() {
        return "/pg status";
    }
}
