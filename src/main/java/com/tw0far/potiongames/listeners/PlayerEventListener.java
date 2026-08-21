package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


/**
 * Handles player-specific events (join, quit, move).
 * Extracted from monolithic Events.java.
 */
public class PlayerEventListener implements Listener {
    private final PotionGamesX plugin;

    public PlayerEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // DB write runs off the join thread
        org.bukkit.Bukkit.getAsyncScheduler().runNow(plugin, task ->
            plugin.getDatabaseManager().createPlayer(p.getUniqueId().toString()));
        if (p.hasPermission("pg.update")) {
            new UpdateChecker(plugin, 87633).getVersion(version -> {
                if (version != null && !plugin.getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                    p.sendMessage(Messages.UpdateAvailable(plugin.getPluginMeta().getVersion(), version));
                }
            });
        }

        // Auto-join first lobby if configured
        if (plugin.getConfigManager().isGameServer() || plugin.getConfigManager().isStartOnJoin()) {
            plugin.getGame().autoJoinLobby(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String lobbyId = null;
        if (plugin.getGame().isInLobby(p)) {
            lobbyId = plugin.getGame().getPlayerLobby(p);
        } else if (plugin.getGame().isSpectatingInLobby(p)) {
            lobbyId = plugin.getGame().getSpectatorLobby(p);
        }
        if (lobbyId != null) {
            plugin.onLeaveLobby(p, lobbyId);
            e.quitMessage(null);
        }
    }
}
