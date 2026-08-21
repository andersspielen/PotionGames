package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.handlers.ReloadHandler;
import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.command.CommandSender;

/**
 * /pg reload - Reload all configurations
 *
 * FIXED: Now properly stops all games, clears memory, closes resources
 */
public class ReloadCommand implements ICommand {
    private final PotionGamesX plugin;
    private final ReloadHandler reloadHandler;

    public ReloadCommand(PotionGamesX plugin) {
        this.plugin = plugin;
        this.reloadHandler = new ReloadHandler(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        try {
            player.sendMessage(CommandDispatcher.createInfo("Starting plugin reload... this may take a moment."));

            // Perform comprehensive reload
            boolean success = reloadHandler.performReload();

            if (success) {
                player.sendMessage(CommandDispatcher.createSuccess("Plugin successfully reloaded!"));
                plugin.getLogger().info("Plugin reloaded by " + player.getName());
            } else {
                player.sendMessage(CommandDispatcher.createError("Plugin reload failed! Check console for details."));
                plugin.getLogger().severe("Plugin reload failed - partial state may remain");
            }

            return true;

        } catch (Exception e) {
            player.sendMessage(CommandDispatcher.createError("Unexpected error during reload!"));
            plugin.getLogger().severe("Reload failed with exception: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public String getUsage() {
        return Messages.HelpReloadUsageText();
    }
}

