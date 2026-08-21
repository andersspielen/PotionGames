package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

/**
 * Handles block fade/decay events.
 * Prevents natural block decay (leaves, ice, etc.) during games.
 */
public class BlockFadeEventListener implements Listener {
    private final PotionGamesX plugin;

    public BlockFadeEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
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
