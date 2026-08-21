package com.tw0far.potiongames.handlers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Game;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * Comprehensive reload handler that properly stops all game activities before reloading.
 *
 * This fixes the critical bug where reload command:
 * - Doesn't stop active lobbies
 * - Doesn't clear player data
 * - Doesn't close DB connections
 * - Doesn't cancel scheduled tasks
 * - Leaves orphaned resources causing memory leaks
 */
public class ReloadHandler {
    private final PotionGamesX plugin;

    public ReloadHandler(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    /**
     * Perform a complete plugin reload with proper cleanup
     *
     * @return true if reload succeeded
     */
    public boolean performReload() {
        try {
            plugin.getLogger().info("=== Starting Plugin Reload ===");

            // Step 1: Stop all active games and lobbies
            plugin.getLogger().info("1. Stopping all active games and lobbies...");
            stopAllGames();

            // Step 2: Clear all player data
            plugin.getLogger().info("2. Clearing player data...");
            clearPlayerData();

            // Step 3: Cancel all scheduled tasks
            plugin.getLogger().info("3. Canceling scheduled tasks...");
            cancelScheduledTasks();

            // Step 4: Close database connection
            plugin.getLogger().info("4. Closing database connection...");
            closeDatabase();

            // Step 5: Clear all collections to free memory
            plugin.getLogger().info("5. Clearing collections...");
            clearCollections();

            // Step 6: Reload configuration files
            plugin.getLogger().info("6. Reloading configuration files...");
            reloadConfiguration();

            // Step 7: Reconnect to database
            plugin.getLogger().info("7. Reconnecting to database...");
            reconnectDatabase();

            // Step 8: Reload game data
            plugin.getLogger().info("8. Reloading game data...");
            reloadGameData();

            plugin.getLogger().info("=== Plugin Reload Complete ===");
            return true;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Plugin reload failed! Errors:", e);
            return false;
        }
    }

    /**
     * Step 1: Stop all active games and lobbies
     */
    private void stopAllGames() {
        try {
            Game game = plugin.getGame();
            if (game != null) {
                for (Lobby lobby : new ArrayList<>(game.getLobbies())) {
                    if (lobby == null) {
                        continue;
                    }

                    try {
                        lobby.clearParticipants();
                        lobby.clearVoting();
                        lobby.clearTeams();
                        lobby.clearChests();
                        lobby.clearBlockTracking();
                        lobby.setCurrentArena(null);
                        lobby.setState(GameStates.WAITING);
                        lobby.setJoinable(true);
                        lobby.setPaused(false);
                        lobby.setDeathmatch(false);
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.WARNING, "Error stopping lobby " + lobby.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error in stopAllGames", e);
        }
    }

    /**
     * Step 2: Clear all player data and fully restore setup players
     */
    private void clearPlayerData() {
        try {
            List<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player player : allPlayers) {
                try {
                    var ssm = plugin.getSetupStateManager();
                    if (!ssm.isSetupPlayer(player)) {
                        continue;
                    }

                    // Restore inventory and armor
                    ItemStack[] inventory = ssm.getPlayerInventory(player);
                    if (inventory != null) {
                        player.getInventory().setContents(inventory);
                    }
                    ItemStack[] armor = ssm.getPlayerArmor(player);
                    if (armor != null) {
                        player.getInventory().setArmorContents(armor);
                    }

                    // Restore location and attributes saved by SetupHandler.setup()
                    Location savedLocation = ssm.getPlayerLocation(player);
                    if (savedLocation != null) {
                        player.teleport(savedLocation);
                    }
                    Integer level = ssm.getPlayerLevel(player);
                    if (level != null) player.setLevel(level);
                    Float exp = ssm.getPlayerExp(player);
                    if (exp != null) player.setExp(exp);
                    GameMode gameMode = ssm.getPlayerGameMode(player);
                    player.setGameMode(gameMode != null ? gameMode : GameMode.SURVIVAL);
                    Double health = ssm.getPlayerHealth(player);
                    player.setHealth(health != null ? health : 20.0);
                    Integer food = ssm.getPlayerFoodLevel(player);
                    player.setFoodLevel(food != null ? food : 20);

                    ssm.removeSavedInventory(player);
                    ssm.removeSavedArmor(player);
                    ssm.removeSavedLevel(player);
                    ssm.removeSavedExp(player);
                    ssm.removeSavedLocation(player);
                    ssm.removeSavedGameMode(player);
                    ssm.removeSavedHealth(player);
                    ssm.removeSavedFoodLevel(player);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error restoring player " + player.getName(), e);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error in clearPlayerData", e);
        }
    }

    /**
     * Step 3: Cancel all scheduled tasks
     */
    private void cancelScheduledTasks() {
        try {
            // Cancel all plugin tasks
            plugin.getServer().getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
            plugin.getLogger().info("All scheduled tasks canceled");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error canceling tasks", e);
        }
    }

    /**
     * Step 4: Close database connection
     */
    private void closeDatabase() {
        try {
            plugin.getDatabaseManager().closeConnection();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing database", e);
        }
    }

    /**
     * Step 5: Clear all collections to free memory
     * Uses manager delegation to clear all state managers instead of direct HashMap access.
     */
    private void clearCollections() {
        try {
            // Use managers to clear their collections
            plugin.getLobbyStateManager().clearAllLobbies();
            plugin.getArenaStateManager().clearAll();
            plugin.getItemStateManager().clearAll();
            plugin.getGame().clearAllPlayers();
            plugin.getGame().clearShopItems();
            plugin.getSetupStateManager().clearAllSetupPlayers();

            plugin.getLogger().info("All collections cleared");

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error clearing collections", e);
        }
    }

    /**
     * Step 6: Reload configuration files
     */
    private void reloadConfiguration() {
        try {
            plugin.reloadConfig();
            plugin.getConfigManager().reload();
            com.tw0far.potiongames.models.Settings.loadSettings(plugin);
            plugin.getLogger().info("Configuration reloaded");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error reloading configuration", e);
        }
    }

    /**
     * Step 7: Reconnect to database
     */
    private void reconnectDatabase() {
        try {
            plugin.getDatabaseManager().connect();
            plugin.getLogger().info("Database reconnected");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error reconnecting database", e);
        }
    }

    /**
     * Step 8: Reload game data (lobbies, arenas, etc.)
     */
    private void reloadGameData() {
        try {
            if (plugin.getGame() != null) {
                plugin.getGame().load();
                // Restart the per-lobby tick loops (cancelled in step 3)
                for (Lobby lobby : plugin.getGame().getLobbies()) {
                    lobby.startTick();
                }
            }
            // Restart the rank wall updater (cancelled in step 3)
            new com.tw0far.potiongames.bootstrap.RankWallUpdater(plugin).start();
            plugin.getLogger().info("Game data reloaded");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error reloading game data", e);
        }
    }
}
