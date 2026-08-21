package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.models.GameStates;

/**
 * Manager for per-lobby runtime flags that are not part of the Lobby model.
 *
 * Game state is mirrored here by {@code Lobby#setState} so listeners get a
 * fast, ID-based lookup; build mode is toggled via /pg build.
 */
public interface ILobbyStateManager extends IManager {

    // Game State (mirrored from Lobby model)
    GameStates getGameState(String lobbyId);
    void setGameState(String lobbyId, GameStates state);

    // Build Mode
    Boolean isBuildAllowed(String lobbyId);
    void setBuildAllowed(String lobbyId, Boolean value);

    // Batch Operations
    void clearAllLobbies();
}
