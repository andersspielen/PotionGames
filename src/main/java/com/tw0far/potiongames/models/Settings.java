package com.tw0far.potiongames.models;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.tw0far.potiongames.PotionGamesX;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Settings {
    public static File lobbiesFile = null;
    public static File chestsFile = null;
    public static File kitsFile = null;
    public static File messagesFile = null;
    public static File shopFile = null;
    public static FileConfiguration lobbies;
    public static FileConfiguration chests;
    public static FileConfiguration kitdata;
    public static FileConfiguration messages;
    public static FileConfiguration shopdata;
    public static Component prefix = Component.text("[").color(NamedTextColor.DARK_GRAY).append(Component.text("Potion").color(NamedTextColor.DARK_PURPLE)).append(Component.text("Games").color(NamedTextColor.GOLD)).append(Component.text("]").color(NamedTextColor.DARK_GRAY)).append(Component.text(" ").color(NamedTextColor.GRAY));
    public static boolean activateTeams = true;
    public static boolean activateKits = true;
    public static boolean activateShop = true;
    public static boolean activateAirdrops = true;
    public static int maxPlayers = 24;
    public static int minPlayers = 2;
    public static int teamSize = 2;
    public static int roundTime = 30;
    public static int countdown = 60;
    public static String language = "en_US";


    public static void loadConfigurations() {
        // Arena/lobby/spawn data from lobbies.yml
        Settings.lobbies = YamlConfiguration.loadConfiguration(Settings.lobbiesFile);
        // Chest loot data from chests.yml
        Settings.chests = YamlConfiguration.loadConfiguration(Settings.chestsFile);
        // Messages from messages.yml
        Settings.messages = YamlConfiguration.loadConfiguration(Settings.messagesFile);
        // Kit data from kits.yml
        Settings.kitdata = YamlConfiguration.loadConfiguration(Settings.kitsFile);
        // Shop data from shop.yml
        Settings.shopdata = YamlConfiguration.loadConfiguration(Settings.shopFile);
    }

    public static void loadSettings(PotionGamesX pg) {
        FileConfiguration cfg = pg.getConfig();
        countdown = cfg.getInt("pg.defaults.countdown", 60);
        activateTeams = cfg.getBoolean("pg.defaults.activateTeams", true);
        activateKits = cfg.getBoolean("pg.defaults.activateKits", true);
        activateShop = cfg.getBoolean("pg.defaults.activateShop", true);
        activateAirdrops = cfg.getBoolean("pg.defaults.activateAirdrops", true);
        maxPlayers = cfg.getInt("pg.defaults.maxPlayers", 24);
        minPlayers = cfg.getInt("pg.defaults.minPlayers", 2);
        teamSize = cfg.getInt("pg.defaults.teamSize", 2);
        roundTime = cfg.getInt("pg.defaults.roundTime", 30) * 60;
        language = cfg.getString("pg.language", "en_US");
    }
}
