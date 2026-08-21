package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg delspawn [lobbynumber] [arenaname] OR /pg delspawn [arenaname]
 * Removes the last added spawn point from the specified arena
 */
public class DelSpawnCommand implements ICommand {
    private final PotionGamesX plugin;

    public DelSpawnCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delspawn";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        // Multi-lobby system: /pg delspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandDelspawnUsageText());
            return false;
        }

        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            Lobby lobby = plugin.getGame().getLobby(lobbyId);
            if (lobby == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            plugin.getSetupHandler().removeSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandDelspawnUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandDelspawnUsageText();
    }
}

