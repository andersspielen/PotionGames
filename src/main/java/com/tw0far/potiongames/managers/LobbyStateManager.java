package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.models.GameStates;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyStateManager implements ILobbyStateManager {
    // Game state mirrored from the Lobby model (see Lobby#setState)
    private final Map<String, GameStates> lobbyStates = new ConcurrentHashMap<>();

    // Build mode per lobby (toggled via /pg build)
    private final Map<String, Boolean> lobbyBuild = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        clearAllLobbies();
    }

    @Override
    public GameStates getGameState(String lobbyId) {
        return lobbyStates.getOrDefault(lobbyId, GameStates.WAITING);
    }

    @Override
    public void setGameState(String lobbyId, GameStates state) {
        lobbyStates.put(lobbyId, state);
    }

    @Override
    public Boolean isBuildAllowed(String lobbyId) {
        return lobbyBuild.getOrDefault(lobbyId, false);
    }

    @Override
    public void setBuildAllowed(String lobbyId, Boolean value) {
        lobbyBuild.put(lobbyId, value);
    }

    @Override
    public void clearAllLobbies() {
        lobbyStates.clear();
        lobbyBuild.clear();
    }
}
