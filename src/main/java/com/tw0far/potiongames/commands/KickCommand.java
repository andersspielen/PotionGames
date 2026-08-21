package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /pg kick <player> - Remove player from lobby
 * Permission: pg.kick (or pg.admin)
 */
public class KickCommand implements ICommand {
    private final PotionGamesX plugin;

    public KickCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getPermission() {
        return "pg.kick";
    }


    @Override
    public boolean execute(CommandSender player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /pg kick <player>").color(NamedTextColor.RED));
            return true;
        }

        String targetName = args[1];
        Player target = plugin.getServer().getPlayer(targetName);

        if (target == null) {
            player.sendMessage(Component.text("Player not found: " + targetName).color(NamedTextColor.RED));
            return true;
        }

        String lobbyId = plugin.getGame().getPlayerLobby(target);
        if (lobbyId == null) {
            player.sendMessage(Component.text(target.getName() + " is not in a lobby").color(NamedTextColor.RED));
            return true;
        }

        plugin.getGame().unregisterPlayer(target);
        target.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());

        Component kickMsg = Component.text(target.getName()).color(NamedTextColor.YELLOW)
            .append(Component.text(" has been kicked from lobby ").color(NamedTextColor.GRAY))
            .append(Component.text(lobbyId).color(NamedTextColor.AQUA));

        player.sendMessage(kickMsg);

        Component targetMsg = Component.text("You have been kicked from the lobby by an admin").color(NamedTextColor.RED);
        target.sendMessage(targetMsg);

        return true;
    }

    @Override
    public String getUsage() {
        return "/pg kick <player>";
    }
}
