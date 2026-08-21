package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Arena;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg force [arena] - Force a specific arena
 */
public class ForceCommand implements ICommand {
    private final PotionGamesX plugin;

    public ForceCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "force";
    }

    @Override
    public String getPermission() {
        return "pg.force";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            return false;
        }

        String arena = args[1];

        // Multi-lobby mode
        String lobbyId = plugin.getGame().getPlayerLobby(player); // active or spectator

        if (lobbyId != null) {
            try {
                Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
                if (lobby != null) {
                    Arena targetArena = lobby.getArena(arena);
                    if (targetArena != null) {
                        lobby.setCurrentArena(targetArena);

                        // Broadcast to all players in this lobby
                        for (Player all : plugin.getGame().getPlayersInLobby(lobbyId)) {
                            all.sendMessage(Messages.ArenaForced(arena));
                        }
                    } else {
                        player.sendMessage(Messages.ArenaNotArena(arena));
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpForceUsageText();
    }
}

