package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.PotionGamesX;

public class Game {
    private ArrayList<Lobby> lobbies = new ArrayList<>();

    public void load() {
        lobbies.clear();
        Set<Integer> lobbyIds = new HashSet<>();

        if (Settings.lobbies.contains("pg.lobbies")) {
            for (String key : Settings.lobbies.getConfigurationSection("pg.lobbies").getKeys(false)) {
                try {
                    lobbyIds.add(Integer.parseInt(key));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (PotionGamesX.getInstance() != null) {
            ConfigurationSection lobbySection = PotionGamesX.getInstance().getConfig().getConfigurationSection("pg.lobbies");
            if (lobbySection != null) {
                for (String key : lobbySection.getKeys(false)) {
                    try {
                        lobbyIds.add(Integer.parseInt(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        for (Integer lobbyId : lobbyIds) {
            LobbyConfig lobbyConfig = new LobbyConfig(
                lobbyId,
                Settings.lobbies,
                Settings.countdown,
                Settings.maxPlayers,
                Settings.minPlayers,
                Settings.teamSize,
                Settings.roundTime,
                Settings.activateTeams,
                Settings.activateKits,
                Settings.activateShop,
                Settings.activateAirdrops
            );

            Lobby lobby = new Lobby(lobbyId, lobbyConfig);
            lobby.load();
            lobbies.add(lobby);
        }

        if (lobbies.isEmpty()) {
            PotionGamesX.getInstance().getLogger().info("PotionGamesX: No lobbies configured.");
        }
    }

    public boolean autoJoinLobby(Player player) {
        if (lobbies.isEmpty()) {
            return false;
        }
        if (getPlayerLobby(player) != null || getSpectatorLobby(player) != null) {
            return false;
        }
        boolean joinStarted = PotionGamesX.getInstance().getConfigManager().isJoinStarted();

        // Prefer an open lobby; fall back to spectating a running round
        for (Lobby lobby : lobbies) {
            if (lobby.canJoin()) {
                lobby.addActivePlayer(player);
                return true;
            }
            if (joinStarted && lobby.canSpectate()) {
                lobby.joinAsSpectator(player);
                return true;
            }
        }
        return false;
    }

    public void unregisterPlayer(Player player) {
        if (player == null) {
            return;
        }

        Lobby currentLobby = getLobbyByPlayer(player);
        if (currentLobby != null) {
            currentLobby.removeActivePlayer(player);
            currentLobby.removeSpectatorPlayer(player);
        }
    }

    public Lobby getLobby(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == lobbyId) {
                return lobby;
            }
        }
        return null;
    }

    public Lobby getLobbyByPlayer(Player p) {
        for (Lobby lobby : lobbies) {
            if (lobby.containsPlayer(p)) {
                return lobby;
            }
        }
        return null;
    }

    public boolean addLobby(int lobbyId, Location location) {
        if (lobbyId < 1) {
            PotionGamesX.getInstance().getLogger().warning("PotionGamesX: Invalid lobby id " + lobbyId + ". Lobby IDs must start at 1.");
            return false;
        }
        if (getLobby(lobbyId) != null) {
            PotionGamesX.getInstance().getLogger().warning("PotionGamesX: Lobby " + lobbyId + " already exists.");
            return false;
        }

        Lobby lobby = new Lobby(lobbyId);
        boolean success = lobby.add(location);
        if (success) {
            lobbies.add(lobby);
        }
        return success;
    }

    public boolean removeLobby(int lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby != null) {
            boolean success = lobby.remove();
            if (success) {
                lobbies.remove(lobby);
            }
            return success;
        }
        return false;
    }

    public ArrayList<Lobby> getLobbies() {
        return lobbies;
    }

    public ArrayList<Player> getActivePlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (Lobby lobby : lobbies) {
            players.addAll(lobby.getActivePlayers());
        }
        return players;
    }

    public ArrayList<Player> getSpectatorPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (Lobby lobby : lobbies) {
            players.addAll(lobby.getSpectatorPlayers());
        }
        return players;
    }

    public void removeActivePlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removeActivePlayer(player);
        }
    }

    public void removeSpectatorPlayer(Player player) {
        Lobby lobby = getLobbyByPlayer(player);
        if (lobby != null) {
            lobby.removeSpectatorPlayer(player);
        }
    }

    public int getActivePlayerCount() {
        int count = 0;
        for (Lobby lobby : lobbies) {
            count += lobby.getActivePlayers().size();
        }
        return count;
    }

    public boolean isActivePlayer(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isActivePlayer(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpectatorPlayer(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isSpectatorPlayer(player)) {
                return true;
            }
        }
        return false;
    }

    public void clearAllPlayers() {
        for (Lobby lobby : lobbies) {
            lobby.clearParticipants();
        }
    }

    /**
     * Returns the ID of the lobby the player belongs to, as an active player
     * or as a spectator. Returns null when the player is not part of any lobby.
     */
    public String getPlayerLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isActivePlayer(player) || lobby.isSpectatorPlayer(player)) {
                return Integer.toString(lobby.getId());
            }
        }
        return null;
    }

    public void removePlayerLobby(Player player) {
        for (Lobby lobby : lobbies) {
            lobby.removeActivePlayer(player);
            lobby.removeSpectatorPlayer(player);
        }
    }

    public boolean isInLobby(Player player) {
        return getPlayerLobby(player) != null;
    }

    public ArrayList<Player> getPlayersInLobby(String lobbyId) {
        ArrayList<Player> lobbyPlayers = new ArrayList<>();
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobbyPlayers.addAll(lobby.getActivePlayers());
        }
        return lobbyPlayers;
    }

    public void setSpectatorLobby(Player player, String lobbyId) {
        for (Lobby lobby : lobbies) {
            lobby.removeActivePlayer(player);
            lobby.removeSpectatorPlayer(player);
        }
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobby.addSpectatorPlayer(player);
        }
    }

    public String getSpectatorLobby(Player player) {
        for (Lobby lobby : lobbies) {
            if (lobby.isSpectatorPlayer(player)) {
                return Integer.toString(lobby.getId());
            }
        }
        return null;
    }

    public void removeSpectatorLobby(Player player) {
        for (Lobby lobby : lobbies) {
            lobby.removeSpectatorPlayer(player);
        }
    }

    public boolean isSpectatingInLobby(Player player) {
        return getSpectatorLobby(player) != null;
    }

    public ArrayList<Player> getSpectatorsInLobby(String lobbyId) {
        ArrayList<Player> lobbySpecs = new ArrayList<>();
        Lobby lobby = getLobby(Integer.parseInt(lobbyId));
        if (lobby != null) {
            lobbySpecs.addAll(lobby.getSpectatorPlayers());
        }
        return lobbySpecs;
    }

    /**
     * Clear all shop/loot/kit state.
     */
    public void clearShopItems() {
        PotionGamesX.getInstance().getItemStateManager().clearAll();
    }
}
