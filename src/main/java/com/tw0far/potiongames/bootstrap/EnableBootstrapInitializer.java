package com.tw0far.potiongames.bootstrap;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.PotionSerialization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.function.Consumer;

public final class EnableBootstrapInitializer {
    private final PotionGamesX plugin;

    public EnableBootstrapInitializer(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        syncShop();
        syncKits();
        configureCoin(plugin);
        initializeLobbies();
    }

    private void syncShop() {
        IItemStateManager ism = plugin.getItemStateManager();

        for (int i = 0; i < ism.getShopItemsRaw().size(); i++) {
            String potionPath = "pg.potions." + (i + 1);
            Settings.shopdata.addDefault(potionPath, ism.getShopItemsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".name", ism.getShopItemsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".shoppotion", ism.getShopPotionsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".shoppotiontype", ism.getShopPotionTypesRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".kit", ism.getShopKitsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".cost", ism.getShopCostsRaw().get(i));
            Settings.shopdata.addDefault(potionPath + ".sale", ism.getShopSalesRaw().get(i));
        }
        Settings.shopdata.options().copyDefaults(true);

        for (int i = 0; i < ism.getShopItemsRaw().size(); i++) {
            String potionPath = "pg.potions." + (i + 1);
            String name = Settings.shopdata.getString(potionPath + ".name");
            if (name != null) ism.getShopItemsRaw().set(i, name);

            PotionEffect effect = PotionSerialization.deserializePotionEffect(Settings.shopdata.get(potionPath + ".shoppotion"), ism.getShopItemsRaw().get(i));
            if (effect != null) ism.getShopPotionsRaw().set(i, effect);

            ItemStack item = PotionSerialization.deserializeItemStack(Settings.shopdata.get(potionPath + ".shoppotiontype"));
            if (item != null) ism.getShopPotionTypesRaw().set(i, item);

            String kit = Settings.shopdata.getString(potionPath + ".kit");
            if (kit != null) ism.getShopKitsRaw().set(i, kit);

            if (Settings.shopdata.get(potionPath + ".cost") instanceof Integer cost) ism.getShopCostsRaw().set(i, cost);
            if (Settings.shopdata.get(potionPath + ".sale") instanceof Integer sale) ism.getShopSalesRaw().set(i, sale);
        }

        try {
            Settings.shopdata.save(Settings.shopFile);
            Settings.lobbies.save(Settings.lobbiesFile);
        } catch (IOException ex) {
            sendError(ex);
        }
    }

    private void syncKits() {
        IItemStateManager ism = plugin.getItemStateManager();
        ism.getKitplayersRaw().put(Messages.RandomText(), 0);
        for (String all : ism.getKitsRaw()) {
            ism.getKitplayersRaw().put(all, 0);
        }

        int kititem = 1;
        for (int i = 0; i < ism.getKitsRaw().size(); i++) {
            String kitPath = "pg.kits." + kititem;
            if (Settings.kitdata.get(kitPath) == null) {
                Settings.kitdata.addDefault(kitPath, ism.getKitsRaw().get(kititem - 1));
                Settings.kitdata.addDefault(kitPath + ".name", ism.getKitsRaw().get(kititem - 1));
                Settings.kitdata.options().copyDefaults(true);
            } else {
                ism.getKitsRaw().set(kititem - 1, Settings.kitdata.getString(kitPath + ".name"));
            }
            kititem++;
        }

        try {
            Settings.kitdata.save(Settings.kitsFile);
        } catch (IOException ex) {
            sendError(ex);
        }
    }

    private void configureCoin(PotionGamesX plugin) {
        ItemStack coin = plugin.getCoin();
        ItemMeta coinmeta = coin.getItemMeta();
        if (coinmeta == null) {
            plugin.getLogger().warning("[PotionGamesX] Coin ItemMeta is null, skipping coin configuration");
            return;
        }
        coinmeta.displayName(Messages.CoinSingle());
        coin.setItemMeta(coinmeta);
    }

    private void initializeLobbies() {
        new RankWallUpdater(plugin).start();

        var ism = plugin.getItemStateManager();
        ism.getKitplayersRaw().put(Messages.RandomText(), 0);
        for (String kit : ism.getKitsRaw()) {
            ism.getKitplayersRaw().put(kit, 0);
        }

        for (var gameLobby : plugin.getGame().getLobbies()) {
            int lobby = gameLobby.getId();
            String s = Integer.toString(lobby);
            var asm = plugin.getArenaStateManager();

            // Build mode starts disabled each boot; the Lobby model owns all other
            // per-lobby settings (loaded via LobbyConfig from lobbies.yml).
            plugin.getLobbyStateManager().setBuildAllowed(s, false);

            // Team selector state: team size and one empty slot per team.
            syncLobbyConfig(s, "teamSize", 2,
                    value -> { int teamSize = Math.max(1, value);
                               int maxPlayers = Math.max(1, Settings.lobbies.getInt("pg.lobbies." + s + ".maxPlayers", 24));
                               int minPlayers = Math.max(1, Settings.lobbies.getInt("pg.lobbies." + s + ".minPlayers", 12));
                               int capacity = Math.max(maxPlayers, minPlayers);
                               asm.setLobbyTeamSize(s, teamSize);
                               asm.initializeLobbyTeams(s, Math.max(1, capacity / teamSize)); });

            plugin.getLobbyStateManager().setGameState(s, GameStates.WAITING);
            gameLobby.startTick();
        }
    }

    private <T> void syncLobbyConfig(String lobbyId, String key, T defaultValue, Consumer<T> setter) {
        String path = "pg.lobbies." + lobbyId + "." + key;
        if (Settings.lobbies.get(path) == null) {
            Settings.lobbies.addDefault(path, defaultValue);
            Settings.lobbies.options().copyDefaults(true);
            try {
                Settings.lobbies.save(Settings.lobbiesFile);
            } catch (IOException ex) {
                sendError(ex);
            }
            return;
        }
        @SuppressWarnings("unchecked")
        T value = (T) Settings.lobbies.get(path);
        setter.accept(value);
    }

    private void sendError(IOException ex) {
        plugin.getComponentLogger().info(Settings.prefix.append(
                Messages.FileSaveFailed().append(Component.text(": " + ex.getMessage()).color(NamedTextColor.RED))
        ));
    }
}
