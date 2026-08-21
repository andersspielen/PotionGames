package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;

/**
 * Handles item drop events.
 * Prevents dropping items during lobby/game phases.
 */
public class ItemDropEventListener implements Listener {
    private final PotionGamesX plugin;

    public ItemDropEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();

        // Players inside a game may drop items (PvP); spectators and waiting
        // players may not, so selector items stay in their inventories.
        if (!plugin.getGame().isActivePlayer(p)
                && plugin.getGame().getPlayerLobby(p) != null) {
            e.setCancelled(true);
        }
    }
}
