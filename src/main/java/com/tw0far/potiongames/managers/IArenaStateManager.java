package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

/**
 * Manager for arena/voting state tracking.
 */
public interface IArenaStateManager extends IManager {

    // ===== LOBBY-SPECIFIC VOTING =====
    Integer getLobbyVoteCount(String lobbyId, String arenaName);
    void addLobbyVote(String lobbyId, String arenaName);
    void removeLobbyVote(String lobbyId, String arenaName);

    void recordPlayerVoteInLobby(String lobbyId, Player player, String arenaName);
    String getPlayerVoteInLobby(String lobbyId, Player player);
    boolean hasPlayerVotedInLobby(String lobbyId, Player player);

    /** Clear votes (counts and per-player records) for one lobby. */
    void resetLobbyVotes(String lobbyId);

    void clearAllLobbyVotes();

    // ===== LOBBY TEAM MANAGEMENT =====
    Integer getLobbyTeamPlayerCount(String lobbyId, Integer teamId);
    void incrementLobbyTeamPlayers(String lobbyId, Integer teamId);
    void decrementLobbyTeamPlayers(String lobbyId, Integer teamId);
    void recordPlayerTeamInLobby(String lobbyId, Player player, String teamId);
    String getPlayerTeamInLobby(String lobbyId, Player player);
    void removePlayerTeamInLobby(String lobbyId, Player player);
    boolean hasPlayerTeamInLobby(String lobbyId, Player player);
    Map<Integer, Integer> getLobbyTeams(String lobbyId);
    void initializeLobbyTeams(String lobbyId, Integer teamCount);
    void setLobbyTeamSize(String lobbyId, Integer teamSize);
    Integer getLobbyTeamSize(String lobbyId);

    // ===== BATCH OPERATIONS =====
    void clearAll();
}
