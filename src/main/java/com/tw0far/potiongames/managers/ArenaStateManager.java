package com.tw0far.potiongames.managers;

import org.bukkit.entity.Player;
import java.util.*;

public class ArenaStateManager implements IArenaStateManager {

    // Per-lobby voting
    private final Map<String, Map<String, Integer>> lobbyvotes = new HashMap<>();
    private final Map<String, Map<Player, String>> lobbyvoteplayernames = new HashMap<>();

    // Per-lobby team state
    private final Map<String, Map<Integer, Integer>> lobbyteams = new HashMap<>();
    private final Map<String, Map<Player, String>> lobbyteamplayernames = new HashMap<>();
    private final Map<String, Integer> lobbyteamSize = new HashMap<>();

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        clearAll();
    }

    // ===== LOBBY-SPECIFIC VOTING =====
    @Override
    public Integer getLobbyVoteCount(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.get(lobbyId);
        if (lobbyVotes == null) {
            return 0;
        }
        return lobbyVotes.getOrDefault(arenaName, 0);
    }

    @Override
    public void addLobbyVote(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.computeIfAbsent(lobbyId, k -> new HashMap<>());
        Integer current = lobbyVotes.getOrDefault(arenaName, 0);
        lobbyVotes.put(arenaName, current + 1);
    }

    @Override
    public void removeLobbyVote(String lobbyId, String arenaName) {
        Map<String, Integer> lobbyVotes = lobbyvotes.get(lobbyId);
        if (lobbyVotes != null) {
            Integer current = lobbyVotes.getOrDefault(arenaName, 0);
            if (current > 0) {
                lobbyVotes.put(arenaName, current - 1);
            }
        }
    }

    @Override
    public void recordPlayerVoteInLobby(String lobbyId, Player player, String arenaName) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.computeIfAbsent(lobbyId, k -> new HashMap<>());
        lobbyPlayerVotes.put(player, arenaName);
    }

    @Override
    public String getPlayerVoteInLobby(String lobbyId, Player player) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.get(lobbyId);
        if (lobbyPlayerVotes == null) {
            return null;
        }
        return lobbyPlayerVotes.get(player);
    }

    @Override
    public boolean hasPlayerVotedInLobby(String lobbyId, Player player) {
        Map<Player, String> lobbyPlayerVotes = lobbyvoteplayernames.get(lobbyId);
        if (lobbyPlayerVotes == null) {
            return false;
        }
        return lobbyPlayerVotes.containsKey(player);
    }

    @Override
    public void clearAllLobbyVotes() {
        lobbyvotes.clear();
        lobbyvoteplayernames.clear();
    }

    @Override
    public void resetLobbyVotes(String lobbyId) {
        lobbyvotes.remove(lobbyId);
        lobbyvoteplayernames.remove(lobbyId);
    }

    // ===== LOBBY TEAM MANAGEMENT =====
    @Override
    public Integer getLobbyTeamPlayerCount(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.get(lobbyId);
        if (teams == null) {
            return 0;
        }
        return teams.getOrDefault(teamId, 0);
    }

    @Override
    public void incrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.computeIfAbsent(lobbyId, k -> new HashMap<>());
        Integer current = teams.getOrDefault(teamId, 0);
        teams.put(teamId, current + 1);
    }

    @Override
    public void decrementLobbyTeamPlayers(String lobbyId, Integer teamId) {
        Map<Integer, Integer> teams = lobbyteams.get(lobbyId);
        if (teams != null) {
            Integer current = teams.getOrDefault(teamId, 0);
            if (current > 0) {
                teams.put(teamId, current - 1);
            }
        }
    }

    @Override
    public void recordPlayerTeamInLobby(String lobbyId, Player player, String teamId) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.computeIfAbsent(lobbyId, k -> new HashMap<>());
        teamPlayers.put(player, teamId);
    }

    @Override
    public String getPlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers == null) {
            return null;
        }
        return teamPlayers.get(player);
    }

    @Override
    public void removePlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers != null) {
            teamPlayers.remove(player);
        }
    }

    @Override
    public boolean hasPlayerTeamInLobby(String lobbyId, Player player) {
        Map<Player, String> teamPlayers = lobbyteamplayernames.get(lobbyId);
        if (teamPlayers == null) {
            return false;
        }
        return teamPlayers.containsKey(player);
    }

    @Override
    public Map<Integer, Integer> getLobbyTeams(String lobbyId) {
        return lobbyteams.getOrDefault(lobbyId, new HashMap<>());
    }

    @Override
    public void initializeLobbyTeams(String lobbyId, Integer teamCount) {
        Map<Integer, Integer> teams = lobbyteams.computeIfAbsent(lobbyId, k -> new HashMap<>());
        teams.clear();
        for (int i = 1; i <= teamCount; i++) {
            teams.put(i, 0);
        }
    }

    @Override
    public void setLobbyTeamSize(String lobbyId, Integer teamSize) {
        lobbyteamSize.put(lobbyId, teamSize);
    }

    @Override
    public Integer getLobbyTeamSize(String lobbyId) {
        return lobbyteamSize.getOrDefault(lobbyId, 1);
    }

    // ===== BATCH OPERATIONS =====
    @Override
    public void clearAll() {
        clearAllLobbyVotes();
        clearAllLobbyTeams();
    }

    private void clearAllLobbyTeams() {
        lobbyteams.clear();
        lobbyteamplayernames.clear();
        lobbyteamSize.clear();
    }
}
