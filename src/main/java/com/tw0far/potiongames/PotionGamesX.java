package com.tw0far.potiongames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.tw0far.potiongames.bootstrap.BootstrapInitializer;
import com.tw0far.potiongames.bootstrap.ChestLootInitializer;
import com.tw0far.potiongames.bootstrap.EnableBootstrapInitializer;
import com.tw0far.potiongames.commands.CommandDispatcher;
import com.tw0far.potiongames.handlers.JoinLobbyHandler;
import com.tw0far.potiongames.handlers.ISetupHandler;
import com.tw0far.potiongames.handlers.SetupHandler;
import com.tw0far.potiongames.listeners.*;
import com.tw0far.potiongames.managers.*;
import com.tw0far.potiongames.models.Game;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.UpdateChecker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PotionGamesX extends JavaPlugin {

    private static PotionGamesX instance;
    private final Game game = new Game();
    private IConfigurationManager configManager;
    private ILobbyStateManager lobbyStateManager;
    private IArenaStateManager arenaStateManager;
    private IItemStateManager itemStateManager;
    private ISetupStateManager setupStateManager;
    private IDatabaseManager databaseManager;
    private ISetupHandler setupHandler;
    private JoinLobbyHandler joinLobbyHandler;
    public static PotionGamesX getInstance() { return instance; }
    public Game getGame() { return game; }
    public IConfigurationManager getConfigManager() { return configManager; }
    public ILobbyStateManager getLobbyStateManager() { return lobbyStateManager; }
    public IArenaStateManager getArenaStateManager() { return arenaStateManager; }
    public IItemStateManager getItemStateManager() { return itemStateManager; }
    public ISetupStateManager getSetupStateManager() { return setupStateManager; }
    public IDatabaseManager getDatabaseManager() { return databaseManager; }
    public ISetupHandler getSetupHandler() { return setupHandler; }

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private final ItemStack coin = new ItemStack(Material.GOLD_NUGGET);
    private final ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        // Register BungeeCord channel for server switching
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        instance = this;
        setupHandler = new SetupHandler(this);
        joinLobbyHandler = new JoinLobbyHandler(this);
        saveDefaultConfig();

        // Initialize configuration manager
        configManager = new ConfigurationManager(this);
        configManager.onEnable();

        // Initialize state managers
        lobbyStateManager = new LobbyStateManager();
        lobbyStateManager.onEnable();
        arenaStateManager = new ArenaStateManager();
        arenaStateManager.onEnable();
        itemStateManager = new ItemStateManager();
        itemStateManager.onEnable();
        setupStateManager = new SetupStateManager();
        setupStateManager.onEnable();

        // Initialize database manager
        databaseManager = new DatabaseManager(this, configManager);
        databaseManager.onEnable();

        PluginManager pm = Bukkit.getPluginManager();

        // Register event listeners (refactored from monolithic Events.java)
        // Player-related events
        pm.registerEvents(new PlayerEventListener(this), this);
        pm.registerEvents(new SpectatorEventListener(this), this);
        pm.registerEvents(new ChatEventListener(this), this);
        pm.registerEvents(new ItemDropEventListener(this), this);

        // Block-related events
        pm.registerEvents(new BlockEventListener(this), this);
        pm.registerEvents(new BlockFadeEventListener(this), this);
        pm.registerEvents(new LeavesDecayEventListener(this), this);
        pm.registerEvents(new BlockFlowEventListener(this), this);
        pm.registerEvents(new BucketEventListener(this), this);

        // Combat-related events
        pm.registerEvents(new CombatEventListener(this), this);
        pm.registerEvents(new DamageEventListener(this), this);
        pm.registerEvents(new DeathEventListener(this), this);

        // Environmental events
        pm.registerEvents(new WeatherEventListener(this), this);
        pm.registerEvents(new CreatureSpawnEventListener(this), this);
        pm.registerEvents(new SignChangeEventListener(), this);

        // Inventory events
        pm.registerEvents(new InventoryEventListener(this), this);

        // Register new command dispatcher (refactored from monolithic Commands.java)
        CommandDispatcher dispatcher = new CommandDispatcher(this);
        dispatcher.register();
        var pgCommand = getCommand("pg");
        if (pgCommand != null) {
            pgCommand.setExecutor(dispatcher);
            pgCommand.setTabCompleter(dispatcher);
        }

        new BootstrapInitializer(this).initialize();
        new ChestLootInitializer(this).seed();

        new EnableBootstrapInitializer(this).initialize();

        // Auto-join first lobby if enabled
        if (configManager.isGameServer() || configManager.isStartOnJoin()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                game.autoJoinLobby(player);
            }
        }

        if (configManager.isActivateMysql() && !databaseManager.isConnected()) {
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            getComponentLogger().info(Messages.PluginStarted());
        }
        new UpdateChecker(this, 87633).getVersion(version -> {
            if (version == null) {
                getComponentLogger().info(Messages.UpdateCheckerError());
                return;
            }
            if (getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                getComponentLogger().info(Messages.UpdateNotAvailable().append(Component.text(" " + getPluginMeta().getVersion()).color(NamedTextColor.GRAY)));
            } else {
                getComponentLogger().info(Messages.UpdateAvailable(getPluginMeta().getVersion(), version));
            }
        });
        if (configManager.isEnableRewards()) {
            if (!setupEconomy()) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getPluginMeta().getName()));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

    }



    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getPluginMeta().getName(), getPluginMeta().getVersion()));

        // Remove all players from lobbies while managers are still alive,
        // restoring inventories/locations before anything is torn down.
        for (Player player : Bukkit.getOnlinePlayers()) {
            String lobbyId = game.getPlayerLobby(player);
            if (lobbyId != null) {
                onLeaveLobby(player, lobbyId);
            }
        }

        boolean gameServer = configManager != null && (configManager.isGameServer() || configManager.isStartOnJoin());
        if (gameServer) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kick(Messages.ServerStopped());
            }
        }

        // Shut down managers and database last
        if (lobbyStateManager != null) lobbyStateManager.onDisable();
        if (arenaStateManager != null) arenaStateManager.onDisable();
        if (itemStateManager != null) itemStateManager.onDisable();
        if (configManager != null) configManager.onDisable();
        if (databaseManager != null) databaseManager.onDisable();

        getComponentLogger().info(Messages.PluginStopped());
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        var rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public void onLeave(Player p) {
        game.removeActivePlayer(p);
        game.removeSpectatorPlayer(p);
    }

    public void clearEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public void onJoinLobby(Player p, String s) {
        joinLobbyHandler.onJoinLobby(p, s);
    }

    public void onLeaveLobby(Player p, String s) {
        try {
            Lobby lobby = game.getLobby(Integer.parseInt(s));
            if (lobby != null) {
                lobby.leave(p);
            }
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "[PotionGamesX] Invalid lobby ID on leave", e);
        }
        game.removePlayerLobby(p);
        game.removeSpectatorLobby(p);
    }

    public ItemStack getCoin() { return coin; }
    public ItemStack getBottle() { return bottle; }

}



