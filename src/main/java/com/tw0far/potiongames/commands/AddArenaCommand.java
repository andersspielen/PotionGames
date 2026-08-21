package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

/**
 * /pg addarena [lobbynumber] [arenaname] OR /pg addarena [arenaname]
 * Adds an arena to the specified lobby or the default lobby
 */
public class AddArenaCommand implements ICommand {
    private final PotionGamesX plugin;

    public AddArenaCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "addarena";
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
        // Multi-lobby system: /pg addarena <lobbynumber> <arenaname>
        if (args.length < 3) {
            player.sendMessage(Messages.CommandAddarenaUsageText());
            return false;
        }

        try {
            int lobbyId = Integer.parseInt(args[1]);
            String arenaName = args[2];
            if (plugin.getGame().getLobby(lobbyId) == null) {
                player.sendMessage(Messages.LobbyDoesNotExist());
                return false;
            }
            plugin.getSetupHandler().addArena(player, arenaName, lobbyId);
            return true;
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.CommandAddarenaUsageText());
            return false;
        }
    }

    @Override
    public String getUsage() {
        return Messages.CommandAddarenaUsageText();
    }
}

