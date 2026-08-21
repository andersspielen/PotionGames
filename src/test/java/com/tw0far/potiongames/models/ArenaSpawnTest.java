package com.tw0far.potiongames.models;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that the in-memory spawn list and the YAML config keys stay
 * consistent for the 1-based spawn ID scheme (see Arena#addSpawn /
 * Arena#removeSpawn).
 */
class ArenaSpawnTest {
    private static final String SPAWN_PATH = "pg.lobbies.1.arenas.test.spawns.";

    @TempDir
    Path tempDir;

    private File previousLobbiesFile;
    private FileConfiguration previousLobbies;

    @BeforeEach
    void setUp() throws IOException {
        previousLobbies = Settings.lobbies;
        previousLobbiesFile = Settings.lobbiesFile;
        Settings.lobbies = new org.bukkit.configuration.file.YamlConfiguration();
        Settings.lobbiesFile = File.createTempFile("lobbies", ".yml", tempDir.toFile());
    }

    @AfterEach
    void tearDown() {
        Settings.lobbies = previousLobbies;
        Settings.lobbiesFile = previousLobbiesFile;
    }

    private Location loc(double x) {
        return new Location(null, x, 64, 0);
    }

    @Test
    void addedSpawnsUseContiguousOneBasedKeys() {
        Arena arena = new Arena("test", 1);

        assertTrue(arena.addSpawn(1, loc(1)));
        assertTrue(arena.addSpawn(2, loc(2)));
        assertTrue(arena.addSpawn(3, loc(3)));

        assertEquals(3, arena.getSpawns().size());
        assertNotNull(Settings.lobbies.getLocation(SPAWN_PATH + "1"));
        assertNotNull(Settings.lobbies.getLocation(SPAWN_PATH + "2"));
        assertNotNull(Settings.lobbies.getLocation(SPAWN_PATH + "3"));
        assertNull(Settings.lobbies.getLocation(SPAWN_PATH + "4"));
    }

    @Test
    void removingMiddleSpawnKeepsListAndConfigAligned() {
        Arena arena = new Arena("test", 1);
        arena.addSpawn(1, loc(1));
        arena.addSpawn(2, loc(2));
        arena.addSpawn(3, loc(3));

        assertTrue(arena.removeSpawn(2));

        assertEquals(2, arena.getSpawns().size());
        // The removed key must be gone from the config as well
        assertNull(Settings.lobbies.getLocation(SPAWN_PATH + "2"));
        assertEquals(loc(1).getX(), arena.getSpawns().get(0).getX());
        assertEquals(loc(3).getX(), arena.getSpawns().get(1).getX());

        // Next add must reuse the freed slot instead of overwriting spawn 3
        assertTrue(arena.addSpawn(3, loc(9)));
        assertEquals(3, arena.getSpawns().size());
        assertEquals(loc(9).getX(), arena.getSpawns().get(2).getX());
        assertEquals(loc(9).getX(), Settings.lobbies.getLocation(SPAWN_PATH + "3").getX());
        assertNull(Settings.lobbies.getLocation(SPAWN_PATH + "4"));
    }

    @Test
    void replacingAnExistingSpawnOverwritesInPlace() {
        Arena arena = new Arena("test", 1);
        arena.addSpawn(1, loc(1));
        arena.addSpawn(2, loc(2));

        assertTrue(arena.addSpawn(1, loc(7)));

        assertEquals(2, arena.getSpawns().size());
        assertEquals(loc(7).getX(), arena.getSpawns().get(0).getX());
        assertEquals(loc(2).getX(), arena.getSpawns().get(1).getX());
    }

    @Test
    void outOfRangeIdsAreRejected() {
        Arena arena = new Arena("test", 1);

        assertFalse(arena.addSpawn(0, loc(1)));
        assertFalse(arena.addSpawn(2, loc(1))); // gap: size is 0, only id 1 is valid
        assertNull(Settings.lobbies.getLocation(SPAWN_PATH + "0"));
        assertNull(Settings.lobbies.getLocation(SPAWN_PATH + "2"));

        assertTrue(arena.addSpawn(1, loc(1)));
        assertFalse(arena.removeSpawn(5));
        assertFalse(arena.removeSpawn(0));
    }
}
