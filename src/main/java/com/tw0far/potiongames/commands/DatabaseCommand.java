package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg database - Toggle database mode (MySQL vs SQLite)
 */
public class DatabaseCommand implements ICommand {
    private final PotionGamesX plugin;

    public DatabaseCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "database";
    }

    @Override
    public String getPermission() {
        return "pg.database";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        // Get current state
        boolean currentState = plugin.getConfigManager().isActivateMysql();
        boolean newState = !currentState;

        // Update config manager
        plugin.getConfigManager().setActivateMysql(newState);

        // Also update the plugin's config file so it persists on reload
        plugin.getConfig().set("pg.activateMySQL", newState);
        plugin.saveConfig();

        // Apply immediately: reconnect with the new database mode
        try {
            plugin.getDatabaseManager().closeConnection();
            plugin.getDatabaseManager().connect();
        } catch (Exception ex) {
            plugin.getLogger().warning("Database reconnect failed: " + ex.getMessage());
        }
        boolean connected = plugin.getDatabaseManager().isConnected();

        // Send message to player
        String dbType = newState ? "MySQL" : "SQLite";
        Component message = Component.text("Database mode switched to ")
            .color(NamedTextColor.AQUA)
            .append(Component.text(dbType).color(NamedTextColor.GREEN))
            .append(Component.text(connected ? " (connected)" : " (connection failed!)")
                .color(connected ? NamedTextColor.GREEN : NamedTextColor.RED));

        player.sendMessage(message);

        // Broadcast to all ops
        for (Player op : plugin.getServer().getOnlinePlayers()) {
            if (op == null) continue;
            if (op.isOp() && !op.equals(player)) {
                op.sendMessage(Component.text(player.getName() + " switched database mode to " + dbType + "!")
                    .color(NamedTextColor.YELLOW));
            }
        }

        plugin.getLogger().info("Database mode switched to " + dbType + " by " + player.getName());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpDatabaseUsageText();
    }
}

