package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.UpdateChecker;
import org.bukkit.entity.Player;

/**
 * /pg version - Show version info and check for updates
 */
public class VersionCommand implements ICommand {
    private final PotionGamesX plugin;

    public VersionCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "pg.update";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        new UpdateChecker(plugin, 87633).getVersion(version -> {
            if (version == null) {
                player.sendMessage(Messages.UpdateCheckerError());
                return;
            }
            String currentVersion = plugin.getPluginMeta().getVersion();
            if (currentVersion.equalsIgnoreCase(version)) {
                player.sendMessage(Messages.UpdateNotAvailable());
            } else {
                player.sendMessage(Messages.UpdateAvailable(currentVersion, version));
            }
        });
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpVersionUsageText();
    }
}

