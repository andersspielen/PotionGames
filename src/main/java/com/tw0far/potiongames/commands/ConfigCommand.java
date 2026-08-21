package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * /pg config - View current plugin configuration settings
 */
public class ConfigCommand implements ICommand {
    private final PotionGamesX plugin;

    public ConfigCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getPermission() {
        return "pg.config";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        var config = plugin.getConfigManager();

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        player.sendMessage(Component.text("PotionGamesX Configuration").color(NamedTextColor.GOLD));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Core:").color(NamedTextColor.GOLD)));

        player.sendMessage(formatConfigLine("  Language", config.getLanguage()));
        player.sendMessage(formatConfigLine("  Game Server Mode", config.isGameServer() ? "ONLINE" : "OFFLINE"));
        player.sendMessage(formatConfigLine("  Database Mode", config.isActivateMysql() ? "MySQL" : "SQLite"));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Defaults:").color(NamedTextColor.GOLD)));

        player.sendMessage(formatConfigLine("  Countdown", config.getCountdown() + "s"));
        player.sendMessage(formatConfigLine("  Round Time", config.getRoundTime() + " min"));
        player.sendMessage(formatConfigLine("  Max Players", config.getMaxPlayers() + ""));
        player.sendMessage(formatConfigLine("  Min Players", config.getMinPlayers() + ""));
        player.sendMessage(formatConfigLine("  Team Size", config.getTeamSize() + ""));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Features:").color(NamedTextColor.GOLD)));

        player.sendMessage(formatConfigLine("  Teams", config.isActivateTeams() ? "✓" : "✗"));
        player.sendMessage(formatConfigLine("  Kits", config.isActivateKits() ? "✓" : "✗"));
        player.sendMessage(formatConfigLine("  Shop", config.isActivateShop() ? "✓" : "✗"));
        player.sendMessage(formatConfigLine("  Airdrops", config.isActivateAirdrops() ? "✓" : "✗"));
        player.sendMessage(formatConfigLine("  Deathmatch", config.isActivateDeathmatch() ? "✓" : "✗"));
        player.sendMessage(formatConfigLine("  Scoreboard", config.isActivateScoreboard() ? "✓" : "✗"));

        player.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("════════════════════════════════════════").color(NamedTextColor.DARK_GRAY)));

        return true;
    }

    private Component formatConfigLine(String key, String value) {
        return Component.text(key).color(NamedTextColor.GRAY)
            .append(Component.text(": ").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(value).color(NamedTextColor.GREEN));
    }

    @Override
    public String getUsage() {
        return "/pg config";
    }
}
