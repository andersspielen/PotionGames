package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg delarena [lobbynumber] [arenaname] OR /pg delarena [arenaname]
 * Removes an arena from the specified lobby or the default lobby
 */
public class DelArenaCommand implements ICommand {
    private final PotionGamesX plugin;

    public DelArenaCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delarena";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby system: /pg delarena <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandDelarenaUsageText());
            return false;
        }

        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            if (plugin.getGame().getLobby(lobbyId) == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            plugin.getSetupHandler().removeArena(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandDelarenaUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandDelarenaUsageText();
    }
}

