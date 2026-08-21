package com.tw0far.potiongames.managers;

public interface IConfigurationManager extends IManager {

    /**
     * Reload all values from the plugin's current configuration.
     */
    void reload();

    String getLanguage();
    int getCountdown();
    int getRoundTime();
    int getMinPlayers();
    int getTeamSize();
    int getMaxPlayers();
    int getActiveKits();
    int getWinningReward();
    int getKillReward();

    boolean isActivateTeams();
    boolean isActivateKits();
    boolean isActivateShop();
    boolean isActivateAirdrops();
    boolean isActivateMysql();
    boolean isActivateScoreboard();
    boolean isActivateDeathmatch();

    void setActivateTeams(boolean activate);
    void setActivateKits(boolean activate);
    void setActivateShop(boolean activate);
    void setActivateAirdrops(boolean activate);
    void setActivateMysql(boolean activate);
    void setActivateScoreboard(boolean activate);
    void setActivateDeathmatch(boolean activate);

    boolean isStartOnJoin();
    boolean isFriendlyFire();
    boolean isGameServer();
    boolean isEnableRewards();

    // Gameplay toggles
    boolean isJoinStarted();
    boolean isAllowOutsideChat();
    boolean isBroadcastStarting();
    boolean isCompassOnSpawn();
    boolean isChangeGamerules();

    void setGameServer(boolean gameServer);

    String getHost();
    String getPort();
    String getDatabase();
    String getUser();
}
