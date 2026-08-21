package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Kit;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Participant;
import com.tw0far.potiongames.models.Settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Inventory clicks for the in-game GUIs: arena voting, team selection,
 * kit selection and the shop.
 */
public class GameInventoryListener implements Listener {
    private final PotionGamesX plugin;

    public GameInventoryListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        String s = plugin.getGame().getPlayerLobby(p);
        if (s != null) {
            GameStates lobbyState = plugin.getLobbyStateManager().getGameState(s);
            boolean canBuild = plugin.getLobbyStateManager().isBuildAllowed(s);
            if ((lobbyState == GameStates.WAITING || lobbyState == GameStates.PREPARING) && !canBuild) {
                handleArenaVoting(e, p, s);
                handleTeamSelection(e, p, s);
                handleKitSelection(e, p, s);
            }
            handleShop(e, p, s);
        }
    }

    private void handleArenaVoting(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.ArenaSelectorTitle())) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }

        // Switching vote - remove old vote first
        if (plugin.getArenaStateManager().hasPlayerVotedInLobby(s, p)) {
            String previousVote = plugin.getArenaStateManager().getPlayerVoteInLobby(s, p);
            if (previousVote != null) {
                plugin.getArenaStateManager().removeLobbyVote(s, previousVote);
            }
        }
        p.closeInventory();
        plugin.getArenaStateManager().addLobbyVote(s, displayname);
        plugin.getArenaStateManager().recordPlayerVoteInLobby(s, p, displayname);
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby != null) {
            lobby.recordVote(p, displayname);
        }

        // Send feedback messages
        p.sendMessage(Messages.ArenaSelector());
        p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
        p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyVoteCount(s, displayname))).color(NamedTextColor.AQUA)));
        p.sendMessage(Messages.ArenaSelector());
    }

    private void handleTeamSelection(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.SelectorTeamTitle())) {
            return;
        }
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null || !lobby.isActivateTeams()) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }
        int maxteamplayers = plugin.getArenaStateManager().getLobbyTeamSize(s);

        if (!plugin.getArenaStateManager().hasPlayerTeamInLobby(s, p)) {
            assignTeam(p, s, displayname, maxteamplayers);
        } else {
            switchTeam(p, s, displayname, maxteamplayers);
        }
    }

    private boolean isNumericTeamId(String displayname) {
        try {
            Integer.parseInt(displayname);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void assignTeam(Player p, String s, String displayname, int maxteamplayers) {
        if (displayname.equals(Messages.RandomText())) {
            assignRandomTeam(p, s, maxteamplayers);
        } else if (isNumericTeamId(displayname)) {
            assignSpecificTeam(p, s, displayname, maxteamplayers);
        }
    }

    private void assignRandomTeam(Player p, String s, int maxteamplayers) {
        Map<Integer, Integer> lobbyTeamsMap = plugin.getArenaStateManager().getLobbyTeams(s);

        if (lobbyTeamsMap == null || lobbyTeamsMap.isEmpty()) {
            return;
        }

        // Collect teams with free capacity; abort gracefully when all are full
        List<Integer> available = new ArrayList<>();
        for (Integer teamId : lobbyTeamsMap.keySet()) {
            Integer teamPlayers = plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, teamId);
            if (teamPlayers != null && teamPlayers < maxteamplayers) {
                available.add(teamId);
            }
        }
        if (available.isEmpty()) {
            p.closeInventory();
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Messages.TeamAlreadyFull());
            p.sendMessage(Messages.SelectorTeam());
            return;
        }

        Random rnd = new Random();
        int rndTeam = available.get(rnd.nextInt(available.size()));
        p.closeInventory();
        plugin.getArenaStateManager().incrementLobbyTeamPlayers(s, rndTeam);
        plugin.getArenaStateManager().recordPlayerTeamInLobby(s, p, Integer.toString(rndTeam));

        p.sendMessage(Messages.SelectorTeam());
        p.sendMessage(Settings.prefix.append(Component.text(Messages.TeamNowInText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
        p.sendMessage(Settings.prefix.append(Component.text(Messages.PlayersText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
        p.sendMessage(Messages.SelectorTeam());

        applyTeamScoreboardPrefix(p, Integer.toString(rndTeam));
    }

    private void assignSpecificTeam(Player p, String s, String displayname, int maxteamplayers) {
        int teamId = Integer.parseInt(displayname);
        Integer currentPlayers = plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, teamId);

        if (currentPlayers != null && currentPlayers < maxteamplayers) {
            p.closeInventory();
            plugin.getArenaStateManager().incrementLobbyTeamPlayers(s, teamId);
            plugin.getArenaStateManager().recordPlayerTeamInLobby(s, p, displayname);

            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Settings.prefix.append(Component.text(Messages.TeamNowInText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
            p.sendMessage(Settings.prefix.append(Component.text(Messages.PlayersText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, teamId))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
            p.sendMessage(Messages.SelectorTeam());

            applyTeamScoreboardPrefix(p, displayname);
        } else {
            p.closeInventory();
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Messages.TeamAlreadyFull());
            p.sendMessage(Messages.SelectorTeam());
        }
    }

    private void applyTeamScoreboardPrefix(Player p, String teamName) {
        if (plugin.getConfigManager().isActivateScoreboard()) {
            var team = p.getScoreboard().getTeam("team");
            if (team != null) {
                team.prefix(Component.text(teamName).color(NamedTextColor.DARK_AQUA));
            }
        }
    }

    private void switchTeam(Player p, String s, String displayname, int maxteamplayers) {
        Map<Integer, Integer> lobbyTeamsMap = plugin.getArenaStateManager().getLobbyTeams(s);

        // Validate the target team BEFORE leaving the old one so a full team
        // cannot leave the player teamless.
        boolean randomRequested = displayname.equals(Messages.RandomText());
        if (!randomRequested) {
            if (!isNumericTeamId(displayname)) {
                return;
            }
            int targetTeam = Integer.parseInt(displayname);
            Integer targetPlayers = plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, targetTeam);
            if (targetPlayers == null || targetPlayers >= maxteamplayers) {
                p.closeInventory();
                p.sendMessage(Messages.SelectorTeam());
                p.sendMessage(Messages.TeamAlreadyFull());
                p.sendMessage(Messages.SelectorTeam());
                return;
            }
        } else if (lobbyTeamsMap.isEmpty()) {
            return;
        }

        String previousTeam = plugin.getArenaStateManager().getPlayerTeamInLobby(s, p);
        if (previousTeam != null && isNumericTeamId(previousTeam)) {
            plugin.getArenaStateManager().decrementLobbyTeamPlayers(s, Integer.parseInt(previousTeam));
            plugin.getArenaStateManager().removePlayerTeamInLobby(s, p);
        }

        if (randomRequested) {
            assignRandomTeam(p, s, maxteamplayers);
        } else {
            assignSpecificTeam(p, s, displayname, maxteamplayers);
        }
    }

    private void handleKitSelection(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.KitSelector())) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null) return;
        Participant participant = lobby.getParticipant(p);
        if (participant == null) return;
        int activeKits = plugin.getConfigManager().getActiveKits();
        if (displayname.equals(Messages.RandomText())) {
            if (activeKits < 1) return;
            int randomKitIndex = new Random().nextInt(activeKits) + 1;
            String kitName = Settings.kitdata.getString("pg.kits." + randomKitIndex + ".name");
            if (kitName != null) {
                participant.setKit(new Kit(randomKitIndex, kitName));
                p.closeInventory();
                p.sendMessage(Messages.KitNowHave(kitName));
            }
        } else {
            for (int i = 1; i <= activeKits; i++) {
                String kitName = Settings.kitdata.getString("pg.kits." + i + ".name");
                if (kitName != null && kitName.equals(displayname)) {
                    participant.setKit(new Kit(i, kitName));
                    p.closeInventory();
                    p.sendMessage(Messages.KitNowHave(kitName));
                    break;
                }
            }
        }
    }

    private void handleShop(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.ShopTitle())) {
            return;
        }
        // The shop GUI is only open during a running game; always protect its items
        e.setCancelled(true);
        if (plugin.getLobbyStateManager().getGameState(s) != GameStates.INGAME
                && plugin.getLobbyStateManager().getGameState(s) != GameStates.DEATHMATCH) {
            return;
        }
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }

        // Count the player's actual coins and bottles
        ItemStack bottleItem = plugin.getBottle();
        ItemStack coinItem = plugin.getCoin();
        int bottles = 0;
        int coins = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;
            if (bottleItem != null && item.getType() == bottleItem.getType()) bottles += item.getAmount();
            if (coinItem != null && item.getType() == coinItem.getType()) coins += item.getAmount();
        }

        var itemStateManager = plugin.getItemStateManager();
        List<String> shopItems = new ArrayList<>(itemStateManager.getShopItems());
        for (int idx = 0; idx < shopItems.size(); idx++) {
            if (!displayname.equals(shopItems.get(idx))) {
                continue;
            }

            Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
            String kitName = lobby != null && lobby.getParticipant(p) != null && lobby.getParticipant(p).getKit() != null
                ? lobby.getParticipant(p).getKit().getName() : null;
            int coinamount = kitName != null && kitName.equals(itemStateManager.getShopKit(idx))
                ? itemStateManager.getShopSale(idx)
                : itemStateManager.getShopCost(idx);

            if (bottles < 1) {
                p.sendMessage(Messages.YouNotEmptyBottle());
                break;
            }
            if (coins < coinamount) {
                p.sendMessage(Messages.YouNotEnoughCoins());
                break;
            }

            ItemStack potionType = itemStateManager.getShopPotionType(idx);
            PotionEffect shopPotion = itemStateManager.getShopPotion(idx);
            if (potionType == null || shopPotion == null) {
                break;
            }
            coins -= coinamount;

            ItemStack potionItem = new ItemStack(potionType);
            PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
            if (potionMeta == null) {
                break;
            }
            potionMeta.addCustomEffect(new PotionEffect(shopPotion.getType(), shopPotion.getDuration(), shopPotion.getAmplifier(), shopPotion.isAmbient(), shopPotion.hasParticles(), shopPotion.hasIcon()), true);
            potionMeta.displayName(Component.text(shopItems.get(idx)));
            potionItem.setItemMeta(potionMeta);
            p.getInventory().addItem(potionItem);
            for (int k = 0; k < coinamount; k++) {
                p.getInventory().removeItem(plugin.getCoin());
            }
            p.getInventory().removeItem(plugin.getBottle());
            break;
        }
    }
}
