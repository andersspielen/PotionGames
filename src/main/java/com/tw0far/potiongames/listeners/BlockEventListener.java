package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Handles block-related events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class BlockEventListener implements Listener {
    private final PotionGamesX plugin;

    /** Materials players may freely break/place inside a lobby while build mode is off. */
    private static final Set<Material> ALLOWED_BLOCKS = EnumSet.of(
        Material.COBWEB, Material.FIRE, Material.CAKE,
        Material.SHORT_GRASS, Material.TALL_GRASS, Material.DEAD_BUSH,
        Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES,
        Material.JUNGLE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES,
        Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM);

    public BlockEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        String s = plugin.getGame().getPlayerLobby(p);
        if (s == null || plugin.getLobbyStateManager().isBuildAllowed(s)) {
            return;
        }

        var lobby = plugin.getGame().getLobby(parseIntSafe(s));
        if (lobby == null) {
            return;
        }

        if (ALLOWED_BLOCKS.contains(e.getBlock().getType())) {
            // Track so the arena can be restored at reset (only during a game)
            if (isGameState(plugin.getLobbyStateManager().getGameState(s))) {
                lobby.addBrokenBlock(e.getBlock().getLocation(), e.getBlock().getType());
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        String s = plugin.getGame().getPlayerLobby(p);
        if (s == null || plugin.getLobbyStateManager().isBuildAllowed(s)) {
            return;
        }

        var lobby = plugin.getGame().getLobby(parseIntSafe(s));
        if (lobby == null) {
            return;
        }

        boolean gameState = isGameState(plugin.getLobbyStateManager().getGameState(s));

        if (e.getBlock().getType() == Material.TNT) {
            if (!gameState) {
                return;
            }
            e.setCancelled(true);
            var itemInHand = p.getInventory().getItemInMainHand();
            if (itemInHand.getAmount() > 1) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            } else {
                p.getInventory().setItemInMainHand(null);
            }
            TNTPrimed tnt = (TNTPrimed) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.TNT);
            tnt.setFuseTicks(40);
            return;
        }

        if (ALLOWED_BLOCKS.contains(e.getBlock().getType())) {
            if (gameState) {
                lobby.addPlacedBlock(e.getBlock().getLocation(), e.getBlock().getType());
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        String s = plugin.getGame().getPlayerLobby(p);
        if (s == null || plugin.getLobbyStateManager().isBuildAllowed(s)) {
            return;
        }
        if (!isGameState(plugin.getLobbyStateManager().getGameState(s))) {
            return;
        }

        // Water buckets are cancelled by BucketEventListener; track lava so it can be restored
        if (e.getBucket() == Material.LAVA_BUCKET) {
            var lobby = plugin.getGame().getLobby(parseIntSafe(s));
            var target = e.getBlockClicked().getRelative(e.getBlockFace());
            if (lobby != null) {
                lobby.addLiquidBlock(target.getLocation(), target.getBlockData());
            }
        }
    }

    private boolean isGameState(com.tw0far.potiongames.models.GameStates state) {
        return state == com.tw0far.potiongames.models.GameStates.INGAME
            || state == com.tw0far.potiongames.models.GameStates.DEATHMATCH;
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
