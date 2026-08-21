package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg gameserver - Toggle gameserver mode
 */
public class GameServerCommand implements ICommand {
    private final PotionGamesX plugin;

    public GameServerCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gameserver";
    }

    @Override
    public String getPermission() {
        return "pg.gameserver";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        // Get current state
        boolean currentState = plugin.getConfigManager().isGameServer();
        boolean newState = !currentState;

        // Update config manager
        plugin.getConfigManager().setGameServer(newState);

        // Also update the plugin's config file so it persists on reload
        plugin.getConfig().set("pg.gameServer", newState);
        plugin.saveConfig();

        // Send message to player
        String statusText = newState ? "enabled" : "disabled";
        Component message = Component.text("GameServer mode is now ")
            .color(NamedTextColor.AQUA)
            .append(Component.text(statusText).color(newState ? NamedTextColor.GREEN : NamedTextColor.RED));

        player.sendMessage(message);

        // Broadcast to all ops
        for (Player op : plugin.getServer().getOnlinePlayers()) {
            if (op == null) continue;
            if (op.isOp() && !op.equals(player)) {
                op.sendMessage(Component.text(player.getName() + " turned " + statusText + " GameServer mode!")
                    .color(NamedTextColor.YELLOW));
            }
        }

        plugin.getLogger().info("GameServer mode toggled to " + newState + " by " + player.getName());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpGameserverUsageText();
    }
}

