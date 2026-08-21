package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Handles water/lava flow events.
 * Prevents water and lava from flowing during games.
 */
public class BlockFlowEventListener implements Listener {
    private final PotionGamesX plugin;

    public BlockFlowEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFlow(BlockFromToEvent e) {
        for (Lobby lobby : plugin.getGame().getLobbies()) {
            if (isGameRunning(lobby)
                    && lobby.getSpawn() != null
                    && lobby.getSpawn().getWorld().equals(e.getBlock().getWorld())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    private boolean isGameRunning(Lobby lobby) {
        return lobby.getState() == GameStates.INGAME || lobby.getState() == GameStates.DEATHMATCH;
    }
}
