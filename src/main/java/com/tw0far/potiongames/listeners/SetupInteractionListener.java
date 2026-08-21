package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Setup-mode interactions: the setup hotbar tools and the
 * lobby/arena selection GUIs.
 */
public class SetupInteractionListener implements Listener {
    private final PotionGamesX plugin;

    public SetupInteractionListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    private boolean isNamedItem(Player player, Material material, Component displayName) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != material || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && Objects.equals(meta.displayName(), displayName);
    }

    private Lobby resolveSetupLobby(Player player) {
        Integer selectedLobby = plugin.getSetupStateManager().getSelectedLobby(player);
        if (selectedLobby != null) {
            return plugin.getGame().getLobby(selectedLobby);
        }

        String activeLobbyId = plugin.getGame().getPlayerLobby(player);
        if (activeLobbyId != null) {
            return plugin.getGame().getLobby(InteractionSupport.parseIntSafe(activeLobbyId));
        }

        return null;
    }

    private Arena resolveSetupArena(Player player, Lobby lobby) {
        if (lobby == null) {
            return null;
        }

        String selectedArena = plugin.getSetupStateManager().getSelectedArena(player);
        if (selectedArena != null) {
            Arena arena = lobby.getArena(selectedArena);
            if (arena != null) {
                return arena;
            }
        }

        return lobby.getCurrentArena();
    }

    private void openChooseArenaInventory(Player player, Lobby lobby) {
        if (lobby == null) {
            player.sendMessage(Messages.ChooseLobbyFirst());
            return;
        }

        List<Arena> arenas = lobby.getArenas();
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ChooseArenaTitle());

        int slot = 0;
        for (Arena arena : arenas) {
            if (slot >= inv.getSize()) {
                break;
            }
            ItemStack item = new ItemStack(Material.MAP);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
            meta.displayName(Component.text(arena.getName()).color(NamedTextColor.AQUA));
            if (!item.setItemMeta(meta)) {
                continue;
            }
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    /** Clicks inside the lobby list / choose-lobby / choose-arena GUIs. */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        handleLobbyListClick(e, p);
        handleChooseLobbyClick(e, p);
        handleChooseArenaClick(e, p);
    }

    private void handleLobbyListClick(InventoryClickEvent e, Player p) {
        if (!e.getView().title().equals(Messages.LobbyListTitle())) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname != null) {
            p.closeInventory();
            plugin.onJoinLobby(p, displayname);
        }
    }

    private void handleChooseLobbyClick(InventoryClickEvent e, Player p) {
        if (!e.getView().title().equals(Messages.ChooseLobbyTitle())) {
            return;
        }
        e.setCancelled(true);
        String lobbyName = InteractionSupport.plainName(e.getCurrentItem());
        if (lobbyName == null) {
            return;
        }
        p.closeInventory();
        int lobbyId = InteractionSupport.parseIntSafe(lobbyName);
        Lobby lobby = lobbyId > 0 ? plugin.getGame().getLobby(lobbyId) : null;
        if (lobby != null) {
            plugin.getSetupStateManager().setSelectedLobby(p, lobbyId);
            plugin.getSetupStateManager().removeSelectedArena(p);
            p.sendMessage(Messages.LobbySelected(lobbyId));
        } else {
            p.sendMessage(Messages.LobbyDoesNotExist());
        }
    }

    private void handleChooseArenaClick(InventoryClickEvent e, Player p) {
        Component expectedTitle = Settings.prefix.append(Component.text(Messages.ChooseArenaText()).color(NamedTextColor.DARK_AQUA));
        if (!e.getView().title().equals(expectedTitle)) {
            return;
        }
        e.setCancelled(true);
        String arenaName = InteractionSupport.plainName(e.getCurrentItem());
        if (arenaName == null) {
            return;
        }
        p.closeInventory();
        Lobby lobby = resolveSetupLobby(p);
        if (lobby == null) {
            p.sendMessage(Messages.ChooseLobbyFirst());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena != null) {
            plugin.getSetupStateManager().setSelectedArena(p, arenaName);
            p.sendMessage(Messages.ArenaSelected(arenaName));
        } else {
            p.sendMessage(Messages.ArenaNotExists());
        }
    }

    /** Setup hotbar tools (sticks, choose clocks, join-sign item, barrier). */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (e.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) {
            return;
        }
        Player p = e.getPlayer();

        Material held = p.getInventory().getItemInMainHand().getType();
        if (held == Material.STICK) {
            if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteLobbyLabel())) {
                if (!p.hasPermission("pg.setup")) {
                    return;
                }
                var world = p.getLocation().getWorld();
                if (world == null) return;
                plugin.getConfig().set("pg.Lobby.world", world.getName());
                plugin.getConfig().set("pg.Lobby.coords", p.getLocation());
                plugin.saveConfig();
                p.sendMessage(Messages.LobbySuccessSet());

                p.getInventory().clear();
                plugin.getSetupStateManager().setAddlobby(true);
                e.setCancelled(true);
            } else if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteArenaLabel())) {
                if (!p.hasPermission("pg.setup")) {
                    return;
                }
                p.getInventory().clear();
                plugin.getSetupStateManager().setAddarena(true);
                e.setCancelled(true);
                p.sendMessage(Messages.TypeArenaNameAdd());
            } else if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteSpawnLabel())) {
                handleSetupSpawnAction(e, p, true);
            } else if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteDeathmatchSpawnLabel())) {
                handleSetupDeathmatchSpawnAction(e, p, true);
            }
            return;
        }

        if (held == Material.CLOCK) {
            if (isNamedItem(p, Material.CLOCK, Messages.ChooseLobbyLabel())) {
                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ChooseLobbyTitle());
                for (int slot = 1; slot <= 27; slot++) {
                    if (Settings.lobbies.contains("pg.lobbies." + slot)) {
                        ItemStack arenamap = new ItemStack(Material.MAP);
                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                        if (arenamapmeta == null) {
                            continue;
                        }
                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                        arenamapmeta.lore(new ArrayList<>());
                        if (!arenamap.setItemMeta(arenamapmeta)) {
                            continue;
                        }
                        inv.setItem(slot - 1, arenamap);
                    }
                }
                p.openInventory(inv);
            } else if (isNamedItem(p, Material.CLOCK, Messages.ChooseArenaLabel())) {
                openChooseArenaInventory(p, resolveSetupLobby(p));
            }
            return;
        }

        if (held == Material.OAK_SIGN) {
            ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
            if (meta != null && meta.displayName() != null && meta.displayName().equals(Messages.SetupJoinSignLabel())) {
                plugin.getSetupHandler().setJoinSign(p);
                e.setCancelled(true);
            }
            return;
        }

        if (held == Material.BARRIER) {
            if (isNamedItem(p, Material.BARRIER, Messages.SetupLeaveModeLabel())) {
                plugin.getSetupHandler().exitSetup(p);
                e.setCancelled(true);
            }
        }
    }

    private boolean handleSetupSpawnAction(PlayerInteractEvent e, Player player, boolean addSpawn) {
        if (!player.hasPermission("pg.setup")) {
            player.sendMessage(Messages.PermissionNoUse());
            return true;
        }

        Lobby lobby = resolveSetupLobby(player);
        Arena arena = resolveSetupArena(player, lobby);
        if (lobby == null) {
            player.sendMessage(Messages.ChooseLobbyFirst());
            return true;
        }
        if (arena == null) {
            player.sendMessage(Messages.ChooseArenaFirst());
            return true;
        }

        if (addSpawn) {
            plugin.getSetupHandler().addSpawn(player, arena.getName(), lobby.getId());
        } else {
            if (arena.getSpawns().isEmpty()) {
                player.sendMessage(Messages.NoSpawnsToRemove());
                return true;
            }
            plugin.getSetupHandler().removeSpawn(player, arena.getName(), lobby.getId());
        }
        e.setCancelled(true);
        return true;
    }

    private boolean handleSetupDeathmatchSpawnAction(PlayerInteractEvent e, Player player, boolean addSpawn) {
        if (!player.hasPermission("pg.setup")) {
            player.sendMessage(Messages.PermissionNoUse());
            return true;
        }

        Lobby lobby = resolveSetupLobby(player);
        Arena arena = resolveSetupArena(player, lobby);
        if (lobby == null) {
            player.sendMessage(Messages.ChooseLobbyFirst());
            return true;
        }
        if (arena == null) {
            player.sendMessage(Messages.ChooseArenaFirst());
            return true;
        }

        if (addSpawn) {
            plugin.getSetupHandler().addDeathmatchSpawn(player, arena.getName(), lobby.getId());
        } else {
            if (arena.getDeathmatchSpawns().isEmpty()) {
                player.sendMessage(Messages.NoDeathmatchSpawnsToRemove());
                return true;
            }
            plugin.getSetupHandler().removeDeathmatchSpawn(player, arena.getName(), lobby.getId());
        }
        e.setCancelled(true);
        return true;
    }
}
