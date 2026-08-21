package com.tw0far.potiongames.models;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class Participant {
    private Lobby lobby;
    private Kit kit;
    private Player player;
    private PlayerState savedState;

    public Participant(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.player = player;
    }

    public void teleportToLobby() {
        if (savedState == null) {
            savedState = new PlayerState(player);
        }
        clearPlayer();
        player.teleport(lobby.getSpawn());
        player.setGameMode(GameMode.ADVENTURE);

        PlayerInventory inv = player.getInventory();

        ItemStack map = new ItemStack(Material.PAPER);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.displayName(Messages.ArenaSelectorItem());
        map.setItemMeta(mapMeta);
        inv.setItem(0, map);

        ItemStack kitItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta kitMeta = kitItem.getItemMeta();
        kitMeta.displayName(Messages.KitSelectorItem());
        kitItem.setItemMeta(kitMeta);
        inv.setItem(1, kitItem);

        ItemStack teamItem = new ItemStack(Material.CLOCK);
        ItemMeta teamMeta = teamItem.getItemMeta();
        teamMeta.displayName(Messages.SelectorTeamItem());
        teamItem.setItemMeta(teamMeta);
        inv.setItem(2, teamItem);

        ItemStack statsItem = new ItemStack(Material.EMERALD);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.displayName(Messages.StatsItem());
        statsItem.setItemMeta(statsMeta);
        inv.setItem(7, statsItem);

        ItemStack leaveItem = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.displayName(Messages.LeaveItem());
        leaveItem.setItemMeta(leaveMeta);
        inv.setItem(8, leaveItem);
    }

    public void teleportToOriginalLocation() {
        savedState.restore(player);
        savedState = null;
    }

    private void clearPlayer() {
        kit = null;
        player.getInventory().clear();
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    public void sendActionBar(Component message) {
        player.sendActionBar(message);
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }
}
