package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg build - Enable/disable build mode
 */
public class BuildCommand implements ICommand {
    private final PotionGamesX plugin;

    public BuildCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "build";
    }

    @Override
    public String getPermission() {
        return "pg.build";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        // Multi-lobby mode: get player's lobby and toggle build mode for that lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            lobbyId = plugin.getGame().getSpectatorLobby(player);
        }

        if (lobbyId != null) {
            // Toggle build mode
            boolean currentBuild = plugin.getLobbyStateManager().isBuildAllowed(lobbyId);
            plugin.getLobbyStateManager().setBuildAllowed(lobbyId, !currentBuild);

            // Broadcast to all players in this lobby
            boolean buildEnabled = plugin.getLobbyStateManager().isBuildAllowed(lobbyId);
            for (Player p : plugin.getGame().getPlayersInLobby(lobbyId)) {
                p.sendMessage(Messages.BuildToggle(buildEnabled));
            }
            for (Player p : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                p.sendMessage(Messages.BuildToggle(buildEnabled));
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpBuildUsageText();
    }
}

