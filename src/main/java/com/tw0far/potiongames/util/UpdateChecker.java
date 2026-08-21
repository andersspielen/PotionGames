package com.tw0far.potiongames.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public record UpdateChecker(JavaPlugin plugin, int resourceId) {
    /**
     * Fetches the latest version from the SpigotMC API asynchronously.
     * The consumer is always invoked: with the latest version on success,
     * or with null when the lookup fails.
     */
    public void getVersion(final Consumer<String> consumer) {
        String url = "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId;
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            try (InputStream inputStream = new URI(url).toURL().openStream(); Scanner scanner = new Scanner(inputStream)) {
                consumer.accept(scanner.hasNext() ? scanner.next() : null);
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
                consumer.accept(null);
            }
        });
    }
}
