package com.tw0far.potiongames.models;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies per-lobby setting resolution with global defaults and the
 * minutes-to-seconds normalization for round times.
 */
class LobbyConfigTest {

    private YamlConfiguration emptyYml() {
        return new YamlConfiguration();
    }

    @Test
    void fallsBackToGlobalDefaultsWhenNothingIsSet() {
        LobbyConfig config = new LobbyConfig(
                1, emptyYml(),
                45,   // countdown
                20,   // maxPlayers
                3,    // minPlayers
                4,    // teamSize
                15,   // roundTime (minutes)
                true, true, false, true);

        assertEquals(45, config.getCountdown());
        assertEquals(20, config.getMaxPlayers());
        assertEquals(3, config.getMinPlayers());
        assertEquals(4, config.getTeamSize());
        // 15 minutes are normalized to seconds
        assertEquals(15 * 60, config.getRoundTime());
        assertTrue(config.isActivateTeams());
        assertFalse(config.isActivateShop());
    }

    @Test
    void lobbyOverridesWinOverGlobals() {
        YamlConfiguration yml = emptyYml();
        yml.set("pg.lobbies.2.maxPlayers", 8);
        yml.set("pg.lobbies.2.settings.activateTeams", false);

        LobbyConfig config = new LobbyConfig(
                2, yml,
                45, 20, 3, 4, 15,
                true, true, true, true);

        assertEquals(8, config.getMaxPlayers());
        assertFalse(config.isActivateTeams());
        assertEquals(3, config.getMinPlayers()); // untouched default
    }

    @Test
    void roundTimeValuesAtOrBelow300AreTreatedAsMinutes() {
        YamlConfiguration yml = emptyYml();
        yml.set("pg.lobbies.3.roundTime", 5);
        LobbyConfig config = new LobbyConfig(3, yml, 60, 24, 2, 2, 30, true, true, true, true);
        assertEquals(300, config.getRoundTime());
    }

    @Test
    void roundTimeValuesAbove300AreTreatedAsSeconds() {
        YamlConfiguration yml = emptyYml();
        yml.set("pg.lobbies.4.roundTime", 400);
        LobbyConfig config = new LobbyConfig(4, yml, 60, 24, 2, 2, 30, true, true, true, true);
        assertEquals(400, config.getRoundTime());
    }

    @Test
    void legacyLobbyKeysStillWork() {
        YamlConfiguration yml = emptyYml();
        yml.set("pg.lobbies.5.teamSize", 6);

        LobbyConfig config = new LobbyConfig(5, yml, 60, 24, 2, 2, 30, true, true, true, true);
        assertEquals(6, config.getTeamSize());
    }
}
