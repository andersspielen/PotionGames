package com.tw0far.potiongames.models;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.PotionGamesX;

/**
 * Represents a single arena within a lobby.
 *
 * Encapsulates spawn locations (normal and deathmatch)
 * and per-round voting results.
 */
public class Arena {
    private final String name;
    private final int lobbyId;

    private ArrayList<Location> spawns = new ArrayList<>();
    private ArrayList<Location> deathmatchSpawns = new ArrayList<>();
    private final Random random = new Random();

    public Arena(String name, int lobbyId) {
        this.name = name;
        this.lobbyId = lobbyId;
    }

    public void load() {
        loadSpawns();
        loadDeathmatchSpawns();
    }

    private void loadSpawns() {
        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns";
        if (Settings.lobbies.contains(spawnPath)) {
            for (String key : Settings.lobbies.getConfigurationSection(spawnPath).getKeys(false)) {
                try {
                    Location spawn = Settings.lobbies.getLocation(spawnPath + "." + key);
                    if (spawn != null) {
                        spawns.add(spawn);
                    }
                } catch (Exception ex) {
                    PotionGamesX.getInstance().getLogger().warning("Error loading spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }

    private void loadDeathmatchSpawns() {
        String deathmatchPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch";
        if (Settings.lobbies.contains(deathmatchPath)) {
            for (String key : Settings.lobbies.getConfigurationSection(deathmatchPath).getKeys(false)) {
                try {
                    Location spawn = Settings.lobbies.getLocation(deathmatchPath + "." + key);
                    if (spawn != null) {
                        deathmatchSpawns.add(spawn);
                    }
                } catch (Exception ex) {
                    PotionGamesX.getInstance().getLogger().warning("Error loading deathmatch spawn " + key + " for arena " + name + ": " + ex.getMessage());
                }
            }
        }
    }

    public boolean add() {
        Settings.lobbies.createSection("pg.lobbies." + lobbyId + ".arenas." + name);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public boolean remove() {
        Settings.lobbies.set("pg.lobbies." + lobbyId + ".arenas." + name, null);
        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            return false;
        }
    }

    public ArrayList<Location> getSpawns() {
        return new ArrayList<>(spawns);
    }

    /**
     * Add or replace a spawn at the given 1-based ID.
     * The ID must be within [1, size + 1] to keep config keys and the
     * in-memory list contiguous.
     */
    public boolean addSpawn(int spawnId, Location spawn) {
        if (spawn == null || spawnId < 1 || spawnId > spawns.size() + 1) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Location previousConfig = Settings.lobbies.getLocation(spawnPath);
        Location previousMemory = spawnId <= spawns.size() ? spawns.get(spawnId - 1) : null;

        if (spawnId <= spawns.size()) {
            spawns.set(spawnId - 1, spawn);
        } else {
            spawns.add(spawn);
        }
        Settings.lobbies.set(spawnPath, spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            // Roll back both memory and config so they stay consistent
            if (spawnId <= spawns.size()) {
                spawns.set(spawnId - 1, previousMemory);
            } else {
                spawns.remove(spawns.size() - 1);
            }
            Settings.lobbies.set(spawnPath, previousConfig);
            return false;
        }
    }

    public boolean removeSpawn(int spawnId) {
        int index = spawnId - 1;
        if (index < 0 || index >= spawns.size()) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".spawns." + spawnId;
        Location removed = spawns.remove(index);
        Location previousConfig = Settings.lobbies.getLocation(spawnPath);
        Settings.lobbies.set(spawnPath, null);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            // Roll back both memory and config so they stay consistent
            spawns.add(index, removed);
            Settings.lobbies.set(spawnPath, previousConfig);
            return false;
        }
    }

    public ArrayList<Location> getDeathmatchSpawns() {
        return new ArrayList<>(deathmatchSpawns);
    }

    public Location getRandomDeathmatchSpawn() {
        if (deathmatchSpawns.isEmpty()) {
            return null;
        }
        return deathmatchSpawns.get(random.nextInt(deathmatchSpawns.size()));
    }

    /**
     * Add or replace a deathmatch spawn at the given 1-based ID.
     * The ID must be within [1, size + 1] to keep config keys and the
     * in-memory list contiguous.
     */
    public boolean addDeathmatchSpawn(int spawnId, Location spawn) {
        if (spawn == null || spawnId < 1 || spawnId > deathmatchSpawns.size() + 1) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Location previousConfig = Settings.lobbies.getLocation(spawnPath);
        Location previousMemory = spawnId <= deathmatchSpawns.size() ? deathmatchSpawns.get(spawnId - 1) : null;

        if (spawnId <= deathmatchSpawns.size()) {
            deathmatchSpawns.set(spawnId - 1, spawn);
        } else {
            deathmatchSpawns.add(spawn);
        }
        Settings.lobbies.set(spawnPath, spawn);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            // Roll back both memory and config so they stay consistent
            if (spawnId <= deathmatchSpawns.size()) {
                deathmatchSpawns.set(spawnId - 1, previousMemory);
            } else {
                deathmatchSpawns.remove(deathmatchSpawns.size() - 1);
            }
            Settings.lobbies.set(spawnPath, previousConfig);
            return false;
        }
    }

    public boolean removeDeathmatchSpawn(int spawnId) {
        int index = spawnId - 1;
        if (index < 0 || index >= deathmatchSpawns.size()) {
            return false;
        }

        String spawnPath = "pg.lobbies." + lobbyId + ".arenas." + name + ".deathmatch." + spawnId;
        Location removed = deathmatchSpawns.remove(index);
        Location previousConfig = Settings.lobbies.getLocation(spawnPath);
        Settings.lobbies.set(spawnPath, null);

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
            return true;
        } catch (Exception ex) {
            PotionGamesX.getInstance().getLogger().warning(ex.getMessage());
            // Roll back both memory and config so they stay consistent
            deathmatchSpawns.add(index, removed);
            Settings.lobbies.set(spawnPath, previousConfig);
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void teleport(ArrayList<Participant> participants) {
        if (spawns.isEmpty()) {
            return;
        }
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            Player player = participant.getPlayer();
            if (player != null && player.isOnline()) {
                player.teleport(spawns.get(i % spawns.size()));
            }
        }
    }
}
