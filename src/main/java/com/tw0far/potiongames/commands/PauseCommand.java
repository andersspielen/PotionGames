package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg pause - Pause the game timer
 */
public class PauseCommand implements ICommand {
    private final PotionGamesX plugin;

    public PauseCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getPermission() {
        return "pg.pause";
    }


    @Override
    public boolean execute(Player player, String[] args) {
        // Multi-lobby mode: get player's lobby and toggle pause for that lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }

        if (lobbyId != null) {
            try {
                Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                if (lobby != null) {
                    lobby.setPaused(!lobby.isPaused());

                    boolean paused = lobby.isPaused();
                    for (Player p : plugin.getGame().getPlayersInLobby(lobbyId)) {
                        p.sendMessage(Messages.PauseToggle(paused));
                    }
                    for (Player p : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                        p.sendMessage(Messages.PauseToggle(paused));
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpPauseUsageText();
    }
}

