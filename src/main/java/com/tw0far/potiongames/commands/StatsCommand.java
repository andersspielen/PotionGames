package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * /pg stats [player] - Show player statistics
 */
public class StatsCommand implements ICommand {
    private final PotionGamesX plugin;

    public StatsCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "pg.stats";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            // Show stats for command sender
            int wins = plugin.getDatabaseManager().getWins(player.getUniqueId().toString());
            int losses = plugin.getDatabaseManager().getLosses(player.getUniqueId().toString());
            int rounds = plugin.getDatabaseManager().getRounds(player.getUniqueId().toString());
            int kills = plugin.getDatabaseManager().getKills(player.getUniqueId().toString());
            int deaths = plugin.getDatabaseManager().getDeaths(player.getUniqueId().toString());
            double kd = plugin.getDatabaseManager().getKD(player.getUniqueId().toString());

            player.sendMessage(Messages.StatsLabel());
            player.sendMessage(Messages.RoundsLabel(rounds));
            player.sendMessage(Messages.WinsLabel(wins));
            player.sendMessage(Messages.LossesLabel(losses));
            player.sendMessage(Messages.KillsLabel(kills));
            player.sendMessage(Messages.DeathsLabel(deaths));
            player.sendMessage(Messages.KDLabel(kd));
            player.sendMessage(Messages.StatsLabel());
        } else {
            // Show stats for specified player (args[0] is the subcommand itself)
            Player target = Bukkit.getPlayer(args[1]);
            player.sendMessage(Messages.StatsLabel());
            if (target != null) {
                int wins = plugin.getDatabaseManager().getWins(target.getUniqueId().toString());
                int losses = plugin.getDatabaseManager().getLosses(target.getUniqueId().toString());
                int rounds = plugin.getDatabaseManager().getRounds(target.getUniqueId().toString());
                int kills = plugin.getDatabaseManager().getKills(target.getUniqueId().toString());
                int deaths = plugin.getDatabaseManager().getDeaths(target.getUniqueId().toString());
                double kd = plugin.getDatabaseManager().getKD(target.getUniqueId().toString());

                player.sendMessage(Messages.RoundsLabel(rounds));
                player.sendMessage(Messages.WinsLabel(wins));
                player.sendMessage(Messages.LossesLabel(losses));
                player.sendMessage(Messages.KillsLabel(kills));
                player.sendMessage(Messages.DeathsLabel(deaths));
                player.sendMessage(Messages.KDLabel(kd));
            } else {
                player.sendMessage(Messages.NoPlayerFound());
            }
            player.sendMessage(Messages.StatsLabel());
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpStatsUsageText();
    }
}

