package com.tw0far.potiongames.handlers;

import org.bukkit.entity.Player;

public interface ISetupHandler {
    void setup(Player p);
    void addLobby(Player p, int lobbyId);
    void removeLobby(Player p, int lobbyId);
    void setJoinSign(Player p);
    void setJoinSign(Player p, int lobbyId);
    void addArena(Player p, String arenaName, int lobbyId);
    void removeArena(Player p, String arenaName, int lobbyId);
    void addSpawn(Player p, String arenaName, int lobbyId);
    void removeSpawn(Player p, String arenaName, int lobbyId);
    void addDeathmatchSpawn(Player p, String arenaName, int lobbyId);
    void removeDeathmatchSpawn(Player p, String arenaName, int lobbyId);
    void exitSetup(Player p);
}
