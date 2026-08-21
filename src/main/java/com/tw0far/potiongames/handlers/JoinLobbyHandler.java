package com.tw0far.potiongames.handlers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

public class JoinLobbyHandler {
    private final PotionGamesX plugin;

    public JoinLobbyHandler(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void onJoinLobby(Player player, String lobbyId) {
        if (plugin.getGame().getPlayerLobby(player) != null || plugin.getGame().getSpectatorLobby(player) != null) {
            return;
        }
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            if (lobby == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return;
            }

            if (lobby.canJoin()) {
                lobby.join(player);
                return;
            }

            // Late join: watch a running round as spectator when allowed
            if (plugin.getConfigManager().isJoinStarted() && lobby.canSpectate()) {
                lobby.joinAsSpectator(player);
                return;
            }

            if (lobby.canSpectate()) {
                player.sendMessage(Messages.GameAlreadyStarted());
            } else {
                player.sendMessage(Messages.LobbyFull());
            }
        } catch (NumberFormatException ignored) {
        }
    }
}
