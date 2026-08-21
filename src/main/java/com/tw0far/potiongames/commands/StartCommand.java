package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.GameStates;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg start - Set game countdown to 10 seconds
 */
public class StartCommand implements ICommand {
    private final PotionGamesX plugin;

    public StartCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getPermission() {
        return "pg.start";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        // Multi-lobby mode
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }

        if (lobbyId != null) {
            try {
                Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                if (lobby != null) {
                    // Check if enough players
                    if (lobby.getPlayerCount() >= lobby.getMinPlayers()) {
                        if (lobby.getState() == GameStates.WAITING) {
                            lobby.setCountdown(10);
                            lobby.startCountdown();
                            // Broadcast to all players in this lobby
                            for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                                all.sendMessage(Messages.GameStarted());
                            }
                        } else {
                            player.sendMessage(Messages.GameAlreadyStarted());
                        }
                    } else {
                        player.sendMessage(Messages.GameNotEnoughPlayers());
                    }
                }
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.HelpUsePgHelp());
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpStartUsageText();
    }
}

