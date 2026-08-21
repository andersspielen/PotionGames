package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg addspawn [lobbynumber] [arenaname] OR /pg addspawn [arenaname]
 * Adds a spawn point to the specified arena at the player's current location
 */
public class AddSpawnCommand implements ICommand {
    private final PotionGamesX plugin;

    public AddSpawnCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "addspawn";
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
        // Multi-lobby system: /pg addspawn <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandAddspawnUsageText());
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
            plugin.getSetupHandler().addSpawn(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandAddspawnUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandAddspawnUsageText();
    }
}

