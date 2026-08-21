package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
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
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        // Multi-lobby mode: get player's lobby and toggle pause for that lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player); // active or spectator

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

