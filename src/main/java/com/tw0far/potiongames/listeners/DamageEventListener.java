package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

/**
 * Handles entity damage events.
 * Manages player damage, damage cancellation, and damage tracking.
 */
public class DamageEventListener implements Listener {
    private final PotionGamesX plugin;

    public DamageEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);

        if (!isActive && !isSpectator) {
            return;
        }

        // Spectators cannot take damage
        if (isSpectator) {
            e.setCancelled(true);
            return;
        }

        // Allow damage to active players
        // Damage is processed normally during games
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) e.getEntity();

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) e.getDamager();

        // Only manage PvP when at least one participant belongs to a game lobby
        boolean victimInLobby = plugin.getGame().getPlayerLobby(victim) != null;
        boolean attackerInLobby = plugin.getGame().getPlayerLobby(attacker) != null;
        if (!victimInLobby && !attackerInLobby) {
            return;
        }

        boolean victimActive = plugin.getGame().isActivePlayer(victim);
        boolean attackerActive = plugin.getGame().isActivePlayer(attacker);

        // Both must be active players to fight
        if (!victimActive || !attackerActive) {
            e.setCancelled(true);
            return;
        }

        // Damage allowed - attacker has hit victim
    }
}
