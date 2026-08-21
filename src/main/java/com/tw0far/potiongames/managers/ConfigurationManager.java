package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationManager implements IConfigurationManager {
    private final PotionGamesX plugin;

    private String language = "en_US";
    private int countdown = 60;
    private int roundTime = 30;
    private int minPlayers = 2;
    private int teamSize = 2;
    private int maxPlayers = 24;
    private int activePotions = 19;
    private int activeKits = 6;
    private int winningReward = 100;
    private int killReward = 10;

    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private boolean activateMysql = false;
    private boolean activateScoreboard = true;
    private boolean activateDeathmatch = true;
    private boolean enableRewards = false;

    private boolean startOnJoin = false;
    private boolean friendlyFire = false;
    private boolean gameServer = false;

    private String host = "localhost";
    private String port = "3306";
    private String database = "PotionGamesX";
    private String user = "root";

    public ConfigurationManager(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        loadAllConfig();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void reload() {
        loadAllConfig();
    }

    private void loadAllConfig() {
        FileConfiguration config = plugin.getConfig();
        language = config.getString("pg.language", "en_US");
        countdown = config.getInt("pg.defaults.countdown", 60);
        roundTime = config.getInt("pg.defaults.roundTime", 30);
        minPlayers = config.getInt("pg.defaults.minPlayers", 2);
        teamSize = config.getInt("pg.defaults.teamSize", 2);
        maxPlayers = config.getInt("pg.defaults.maxPlayers", 24);
        activePotions = config.getInt("pg.activePotions", 19);
        activeKits = config.getInt("pg.activeKits", 6);
        winningReward = config.getInt("pg.winningReward", 100);
        killReward = config.getInt("pg.killReward", 10);

        activateTeams = config.getBoolean("pg.defaults.activateTeams", true);
        activateKits = config.getBoolean("pg.defaults.activateKits", true);
        activateShop = config.getBoolean("pg.defaults.activateShop", true);
        activateAirdrops = config.getBoolean("pg.defaults.activateAirdrops", true);
        activateMysql = config.getBoolean("pg.activateMySQL", false);
        activateScoreboard = config.getBoolean("pg.activateScoreboard", true);
        activateDeathmatch = config.getBoolean("pg.activateDeathmatch", true);
        enableRewards = config.getBoolean("pg.enableRewards", false);

        startOnJoin = config.getBoolean("pg.startOnJoin", false);
        friendlyFire = config.getBoolean("pg.friendlyFire", false);
        gameServer = config.getBoolean("pg.gameServer", false);

        host = config.getString("pg.mysql.host", "localhost");
        port = config.getString("pg.mysql.port", "3306");
        database = config.getString("pg.mysql.database", "PotionGamesX");
        user = config.getString("pg.mysql.user", "root");
    }

    @Override
    public String getLanguage() { return language; }

    @Override
    public int getCountdown() { return countdown; }

    @Override
    public int getRoundTime() { return roundTime; }

    @Override
    public int getMinPlayers() { return minPlayers; }

    @Override
    public int getTeamSize() { return teamSize; }

    @Override
    public int getMaxPlayers() { return maxPlayers; }

    @Override
    public int getActivePotions() { return activePotions; }

    @Override
    public int getActiveKits() { return activeKits; }

    @Override
    public int getWinningReward() { return winningReward; }

    @Override
    public int getKillReward() { return killReward; }

    @Override
    public boolean isActivateTeams() { return activateTeams; }

    @Override
    public boolean isActivateKits() { return activateKits; }

    @Override
    public boolean isActivateShop() { return activateShop; }

    @Override
    public boolean isActivateAirdrops() { return activateAirdrops; }

    @Override
    public boolean isActivateMysql() { return activateMysql; }

    @Override
    public boolean isActivateScoreboard() { return activateScoreboard; }

    @Override
    public boolean isActivateDeathmatch() { return activateDeathmatch; }

    @Override
    public void setActivateTeams(boolean activate) { this.activateTeams = activate; }

    @Override
    public void setActivateKits(boolean activate) { this.activateKits = activate; }

    @Override
    public void setActivateShop(boolean activate) { this.activateShop = activate; }

    @Override
    public void setActivateAirdrops(boolean activate) { this.activateAirdrops = activate; }

    @Override
    public void setActivateMysql(boolean activate) { this.activateMysql = activate; }

    @Override
    public void setActivateScoreboard(boolean activate) { this.activateScoreboard = activate; }

    @Override
    public void setActivateDeathmatch(boolean activate) { this.activateDeathmatch = activate; }

    @Override
    public boolean isStartOnJoin() { return startOnJoin; }

    @Override
    public boolean isFriendlyFire() { return friendlyFire; }

    @Override
    public boolean isGameServer() { return gameServer; }

    @Override
    public boolean isEnableRewards() { return enableRewards; }

    @Override
    public void setGameServer(boolean gameServer) { this.gameServer = gameServer; }

    @Override
    public String getHost() { return host; }

    @Override
    public String getPort() { return port; }

    @Override
    public String getDatabase() { return database; }

    @Override
    public String getUser() { return user; }
}
