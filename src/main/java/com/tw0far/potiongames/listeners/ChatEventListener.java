package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Handles player chat events.
 * Manages team/spectator chat separation, message filtering, and setup mode input.
 */
public class ChatEventListener implements Listener {
    private final PotionGamesX plugin;

    public ChatEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        Player p = e.getPlayer();

        // Handle setup mode chat input first
        if (plugin.getSetupStateManager().isSetupPlayer(p)) {
            String messageText = PlainTextComponentSerializer.plainText().serialize(e.message());
            handleSetupChatInput(e, p, messageText);
            return;
        }

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);

        if (!isActive && !isSpectator) {
            return;
        }

        // Multi-lobby mode: get lobby ID and filter chat per lobby
        String lobbyId = null;
        if (isActive) {
            lobbyId = plugin.getGame().getPlayerLobby(p);
        } else if (isSpectator) {
            lobbyId = plugin.getGame().getSpectatorLobby(p);
        }

        if (lobbyId != null && !plugin.getConfigManager().isAllowOutsideChat()) {
            // Filter recipients: only players in the same lobby (plus console)
            e.viewers().clear();
            e.viewers().add(Bukkit.getConsoleSender());

            // Add active players in same lobby
            for (Player active : plugin.getGame().getPlayersInLobby(lobbyId)) {
                e.viewers().add(active);
            }

            // Add spectators in same lobby
            for (Player spec : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                e.viewers().add(spec);
            }

            // Add colored name prefix based on team
            boolean activateTeams;
            try {
                int id = Integer.parseInt(lobbyId);
                Lobby lobby = plugin.getGame().getLobby(id);
                activateTeams = lobby != null ? lobby.isActivateTeams() : plugin.getConfigManager().isActivateTeams();
            } catch (NumberFormatException e2) {
                activateTeams = plugin.getConfigManager().isActivateTeams();
            }
            if (activateTeams) {
                Lobby chatLobby = plugin.getGame().getLobbyByPlayer(p);
                String teamName = chatLobby != null ? chatLobby.getPlayerTeam(p) : null;
                if (teamName != null) {
                    Component currentMessage = e.message();
                    if (currentMessage != null) {
                        Component prefixed = Component.text("[" + teamName + "] ").color(NamedTextColor.YELLOW).append(currentMessage);
                        if (prefixed != null) {
                            e.message(prefixed);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle chat input during setup mode
     */
    private void handleSetupChatInput(AsyncChatEvent e, Player p, String input) {
        // No pending input mode: let normal chat through instead of swallowing it
        if (!plugin.getSetupStateManager().isAddlobby()
                && !plugin.getSetupStateManager().isDellobby()
                && !plugin.getSetupStateManager().isAddarena()
                && !plugin.getSetupStateManager().isDelarena()) {
            return;
        }

        // Cancel the chat message from being sent
        e.setCancelled(true);

        Integer selectedLobby = plugin.getSetupStateManager().getSelectedLobby(p);

        // Handle add lobby
        if (plugin.getSetupStateManager().isAddlobby()) {
            // Input is the lobby ID to add
            try {
                int lobbyId = Integer.parseInt(input);
                plugin.getSetupHandler().addLobby(p, lobbyId);
                plugin.getSetupStateManager().setAddlobby(false);
            } catch (NumberFormatException ex) {
                p.sendMessage(Component.text("Invalid lobby ID. Must be a number.").color(NamedTextColor.RED));
            }
        }
        // Handle delete lobby
        else if (plugin.getSetupStateManager().isDellobby()) {
            // Input is the lobby ID to delete
            try {
                int lobbyId = Integer.parseInt(input);
                plugin.getSetupHandler().removeLobby(p, lobbyId);
                plugin.getSetupStateManager().setDellobby(false);
            } catch (NumberFormatException ex) {
                p.sendMessage(Component.text("Invalid lobby ID. Must be a number.").color(NamedTextColor.RED));
            }
        }
        // Handle add arena
        else if (plugin.getSetupStateManager().isAddarena()) {
            // Input is the arena name to add
            if (selectedLobby != null) {
                plugin.getSetupHandler().addArena(p, input, selectedLobby);
                plugin.getSetupStateManager().setAddarena(false);
            } else {
                p.sendMessage(Component.text("Please select a lobby first!").color(NamedTextColor.RED));
            }
        }
        // Handle delete arena
        else if (plugin.getSetupStateManager().isDelarena()) {
            // Input is the arena name to delete
            if (selectedLobby != null) {
                plugin.getSetupHandler().removeArena(p, input, selectedLobby);
                plugin.getSetupStateManager().setDelarena(false);
            } else {
                p.sendMessage(Component.text("Please select a lobby first!").color(NamedTextColor.RED));
            }
        }
    }
}
