package com.tw0far.potiongames.handlers;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;

public class SetupHandler implements ISetupHandler {

    private final PotionGamesX pg;

    public SetupHandler(PotionGamesX pg) {
        this.pg = pg;
    }

    @Override
    public void setup(Player p) {
        PlayerInventory inventory = p.getInventory();
        var ssm = pg.getSetupStateManager();
        ssm.addSetupPlayer(p);
        ssm.savePlayerInventory(p, inventory.getContents());
        ssm.savePlayerArmor(p, inventory.getArmorContents());
        ssm.savePlayerLevel(p, p.getLevel());
        ssm.savePlayerExp(p, p.getExp());
        ssm.savePlayerLocation(p, p.getLocation());
        ssm.savePlayerGameMode(p, p.getGameMode());
        ssm.savePlayerHealth(p, p.getHealth());
        ssm.savePlayerFoodLevel(p, p.getFoodLevel());

        inventory.clear();
        inventory.setArmorContents(new ItemStack[] { null, null, null, null });

        p.setHealth(20);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setExp(0);
        p.setGameMode(GameMode.ADVENTURE);
        pg.clearEffects(p);
        p.setFireTicks(0);
        p.setAllowFlight(true);
        p.setFlying(true);

        ItemStack addlobby = new ItemStack(Material.STICK);
        ItemMeta addlobbymeta = addlobby.getItemMeta();
        if (addlobbymeta != null) {
            addlobbymeta.displayName(Messages.SetupAddDeleteLobbyLabel());
            addlobby.setItemMeta(addlobbymeta);
            inventory.setItem(1, addlobby);
        }

        ItemStack chooselobby = new ItemStack(Material.CLOCK);
        ItemMeta chooselobbymeta = chooselobby.getItemMeta();
        if (chooselobbymeta != null) {
            chooselobbymeta.displayName(Messages.ChooseLobbyLabel());
            chooselobby.setItemMeta(chooselobbymeta);
            inventory.setItem(2, chooselobby);
        }

        ItemStack addarena = new ItemStack(Material.STICK);
        ItemMeta addarenameta = addarena.getItemMeta();
        if (addarenameta != null) {
            addarenameta.displayName(Messages.SetupAddDeleteArenaLabel());
            addarena.setItemMeta(addarenameta);
            inventory.setItem(3, addarena);
        }

        ItemStack choosearena = new ItemStack(Material.CLOCK);
        ItemMeta choosearenameta = choosearena.getItemMeta();
        if (choosearenameta != null) {
            choosearenameta.displayName(Messages.ChooseArenaLabel());
            choosearena.setItemMeta(choosearenameta);
            inventory.setItem(4, choosearena);
        }

        ItemStack addspawn = new ItemStack(Material.STICK);
        ItemMeta addspawnmeta = addspawn.getItemMeta();
        if (addspawnmeta != null) {
            addspawnmeta.displayName(Messages.SetupAddDeleteSpawnLabel());
            addspawn.setItemMeta(addspawnmeta);
            inventory.setItem(5, addspawn);
        }

        ItemStack adddeathmatchspawn = new ItemStack(Material.STICK);
        ItemMeta adddeathmatchspawnmeta = adddeathmatchspawn.getItemMeta();
        if (adddeathmatchspawnmeta != null) {
            adddeathmatchspawnmeta.displayName(Messages.SetupAddDeleteDeathmatchSpawnLabel());
            adddeathmatchspawn.setItemMeta(adddeathmatchspawnmeta);
            inventory.setItem(6, adddeathmatchspawn);
        }

        ItemStack signsetup = new ItemStack(Material.OAK_SIGN);
        ItemMeta signsetupmeta = signsetup.getItemMeta();
        if (signsetupmeta != null) {
            signsetupmeta.displayName(Messages.SetupJoinSignLabel());
            signsetup.setItemMeta(signsetupmeta);
            inventory.setItem(7, signsetup);
        }

        ItemStack leavesetup = new ItemStack(Material.BARRIER);
        ItemMeta leavesetupmeta = leavesetup.getItemMeta();
        if (leavesetupmeta != null) {
            leavesetupmeta.displayName(Messages.SetupLeaveModeLabel());
            leavesetup.setItemMeta(leavesetupmeta);
            inventory.setItem(8, leavesetup);
        }
    }

    @Override
    public void addLobby(Player p, int lobbyId) {
        boolean success = pg.getGame().addLobby(lobbyId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.LobbyAdded(lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeLobby(Player p, int lobbyId) {
        boolean success = pg.getGame().removeLobby(lobbyId);
        if (success) {
            p.sendMessage(Messages.LobbyRemoved(lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void setJoinSign(Player p) {
        setJoinSign(p, 1);
    }

    @Override
    public void setJoinSign(Player p, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Block target = p.getTargetBlockExact(5);
        if (target == null) {
            p.sendMessage(Settings.prefix.append(Component.text(Messages.SetupNoBlockInRangeText()).color(NamedTextColor.RED)));
            return;
        }
        boolean success = lobby.setJoinSign(target.getLocation());
        if (success) {
            p.sendMessage(Messages.SignSet());
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addArena(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        boolean success = lobby.addArena(arenaName);
        if (success) {
            p.sendMessage(Messages.ArenaAdded(arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeArena(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        boolean success = lobby.removeArena(arenaName);
        if (success) {
            p.sendMessage(Messages.ArenaRemoved(arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getSpawns().size() + 1;
        boolean success = arena.addSpawn(spawnId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.SpawnAdded(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getSpawns().size();
        if (spawnId < 1) {
            p.sendMessage(Settings.prefix.append(Component.text(Messages.SetupNoSpawnsText()).color(NamedTextColor.RED)));
            return;
        }
        boolean success = arena.removeSpawn(spawnId);
        if (success) {
            p.sendMessage(Messages.SpawnRemoved(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addDeathmatchSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getDeathmatchSpawns().size() + 1;
        boolean success = arena.addDeathmatchSpawn(spawnId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.SpawnAdded(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeDeathmatchSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getDeathmatchSpawns().size();
        if (spawnId < 1) {
            p.sendMessage(Settings.prefix.append(Component.text(Messages.SetupNoDeathmatchSpawnsText()).color(NamedTextColor.RED)));
            return;
        }
        boolean success = arena.removeDeathmatchSpawn(spawnId);
        if (success) {
            p.sendMessage(Messages.SpawnRemoved(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void exitSetup(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { null, null, null, null });

        ItemStack[] savedInv = pg.getSetupStateManager().getPlayerInventory(p);
        if (savedInv != null) {
            p.getInventory().setContents(savedInv);
        }

        ItemStack[] savedArmor = pg.getSetupStateManager().getPlayerArmor(p);
        if (savedArmor != null) {
            p.getInventory().setArmorContents(savedArmor);
        }
        p.teleport(pg.getSetupStateManager().getPlayerLocation(p));
        Integer savedLevel = pg.getSetupStateManager().getPlayerLevel(p);
        if (savedLevel != null) p.setLevel(savedLevel);
        Float savedExp = pg.getSetupStateManager().getPlayerExp(p);
        if (savedExp != null) p.setExp(savedExp);
        GameMode gm = pg.getSetupStateManager().getPlayerGameMode(p);
        p.setGameMode(gm != null ? gm : GameMode.SURVIVAL);

        Double health = pg.getSetupStateManager().getPlayerHealth(p);
        p.setHealth(health != null ? health : 20.0);
        Integer food = pg.getSetupStateManager().getPlayerFoodLevel(p);
        p.setFoodLevel(food != null ? food : 20);
        pg.clearEffects(p);
        p.setFireTicks(0);
        p.setAllowFlight(false);
        p.setFlying(false);

        pg.getSetupStateManager().removeSavedInventory(p);
        pg.getSetupStateManager().removeSavedArmor(p);
        pg.getSetupStateManager().removeSavedLocation(p);
        pg.getSetupStateManager().removeSavedLevel(p);
        pg.getSetupStateManager().removeSavedExp(p);
        pg.getSetupStateManager().removeSavedGameMode(p);
        pg.getSetupStateManager().removeSavedHealth(p);
        pg.getSetupStateManager().removeSavedFoodLevel(p);
        pg.getSetupStateManager().removeSetupPlayer(p);
    }
}
