package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;

/**
 * Handles combat-related events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class CombatEventListener implements Listener {
    private final PotionGamesX plugin;

    public CombatEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    private String getPlayerTeam(String lobbyId, Player player) {
        try {
            Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(lobbyId));
            if (lobby != null) return lobby.getPlayerTeam(player);
        } catch (NumberFormatException ignored) { }
        return null;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }

        String victimLobby = plugin.getGame().getPlayerLobby(p);
        boolean inActiveGame = victimLobby != null && isGameState(plugin.getLobbyStateManager().getGameState(victimLobby));

        // Lightning/firework damage is disabled inside active games
        if (!e.isCancelled()
                && inActiveGame
                && (e.getDamager() instanceof LightningStrike || e.getDamager() instanceof Firework)) {
            e.setCancelled(true);
            return;
        }

        // TNT deals bonus damage inside active games
        if (!e.isCancelled() && inActiveGame && e.getDamager() instanceof TNTPrimed) {
            e.setDamage(e.getDamage() + 4.0);
        }

        if (!plugin.getConfigManager().isFriendlyFire() && !e.isCancelled()
                && e.getDamager() instanceof Player d) {
            // Both must be in same lobby for friendly fire check
            String dLobby = plugin.getGame().getPlayerLobby(d);

            if (victimLobby != null && victimLobby.equals(dLobby)
                    && isGameState(plugin.getLobbyStateManager().getGameState(victimLobby))) {
                String pTeam = getPlayerTeam(victimLobby, p);
                String dTeam = getPlayerTeam(dLobby, d);
                if (Objects.equals(pTeam, dTeam)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private boolean isGameState(GameStates state) {
        return state == GameStates.INGAME || state == GameStates.DEATHMATCH;
    }
}
