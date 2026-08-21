package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * /pg help - Show help message
 */
public class HelpCommand implements ICommand {
    private final PotionGamesX plugin;

    public HelpCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return null; // Everyone can use
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        player.sendMessage(Messages.CommandsLabel());

        if (player.hasPermission("pg.setup")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpSetupText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpAddlobbyText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDellobbyText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpAddarenaText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpAddspawnText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpAdddeathmatchText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDelarenaText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDelspawnText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDeldeathmatchText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpHeadText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpSignText()).color(NamedTextColor.GRAY)));
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpJoinsignText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.reload")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpReloadText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.build")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpBuildText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.pause")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpPauseText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.force")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpForceText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.start")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpStartText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.join")) {
            if (!plugin.getConfigManager().isStartOnJoin()) {
                player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpJoinText()).color(NamedTextColor.GRAY)));
                player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpListText()).color(NamedTextColor.GRAY)));
            }
        }

        if (!plugin.getConfigManager().isStartOnJoin()) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpLeaveText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.stats")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpStatsText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.version")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpVersionText()).color(NamedTextColor.GRAY)));
        }

        // Admin commands
        if (player.hasPermission("pg.admin") || player.hasPermission("pg.gameserver")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpGameserverText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.database")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDatabaseText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.config")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpConfigText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.status")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpStatusText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.debug")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpDebugText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.broadcast")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpBroadcastText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.kick")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpKickText()).color(NamedTextColor.GRAY)));
        }

        if (player.hasPermission("pg.admin") || player.hasPermission("pg.top")) {
            player.sendMessage(Settings.prefix.append(Component.text(Messages.HelpTopText()).color(NamedTextColor.GRAY)));
        }

        player.sendMessage(Messages.CommandsLabel());
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpHelpUsageText();
    }
}

