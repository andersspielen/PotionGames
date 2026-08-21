package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * /pg leave - Leave current game
 */
public class LeaveCommand implements ICommand {
    private final PotionGamesX plugin;

    public LeaveCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "pg.leave";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        Lobby lobby = plugin.getGame().getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.leave(player);
        }

        if (plugin.getConfigManager().isGameServer()) {
            String hub = plugin.getConfig().getString("pg.bungeeServer", "hub");
            if (plugin.getConfigManager().isStartOnJoin()) {
                player.kick(Component.text("Connecting to " + hub + "...").color(NamedTextColor.GREEN));
            } else {
                sendToServer(player, hub);
            }
        }

        return true;
    }

    private void sendToServer(Player player, String server) {
        if (player == null || !player.isOnline()) return;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(PotionGamesX.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.kick(Component.text("Connecting to " + server + "...").color(NamedTextColor.GREEN));
        }
    }

    @Override
    public String getUsage() {
        return Messages.HelpLeaveUsageText();
    }
}

