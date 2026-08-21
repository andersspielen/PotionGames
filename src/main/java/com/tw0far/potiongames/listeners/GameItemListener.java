package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.IItemStateManager;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Participant;
import com.tw0far.potiongames.models.Settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Gameplay right-click interactions for players inside a lobby:
 * loot/shop/custom chests, stew and milk, airdrop torch, player finder,
 * the selector GUIs (vote/team/kit), leave item and stats item,
 * plus join/stats signs.
 */
public class GameItemListener implements Listener {
    private final PotionGamesX plugin;

    public GameItemListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // Handle all signs
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Material clickedType = e.getClickedBlock().getType();
            if (clickedType.name().contains("_SIGN")) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                String line1 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
                String line2 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(1));
                String line3 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2));

                // Check Join Signs
                try {
                    int lobbyId = Integer.parseInt(line1);
                    String signKey = "pg.lobbies." + lobbyId + ".joinSign";
                    if (e.getClickedBlock().getLocation().equals(Settings.lobbies.getLocation(signKey))) {
                        e.setCancelled(true);
                        if (plugin.getGame().getPlayerLobby(p) == null && plugin.getGame().getSpectatorLobby(p) == null) {
                            plugin.onJoinLobby(p, line1);
                        }
                        return;
                    }
                } catch (NumberFormatException ignored) {
                    // Not a join sign (line 1 is not a lobby number) - ignore
                }

                // Check Stats Signs
                if (line2 != null && line2.matches("PotionGamesX") && line3 != null && line3.matches("Stats")) {
                    e.setCancelled(true);
                    sendStats(p);
                    return;
                }
            }
        }

        if (!plugin.getGame().isActivePlayer(p) && !plugin.getGame().isInLobby(p)) {
            return;
        }

        if (e.getAction() == Action.PHYSICAL) {
            var clicked = e.getClickedBlock();
            if (clicked != null && clicked.getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleRightClickBlock(e, p);
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            handleGameItems(e, p);
        }
    }

    /** Selector openers and one-shot items that work with either hand-independent checks. */
    private void handleGameItems(PlayerInteractEvent e, Player p) {
        boolean hand = e.getHand() == EquipmentSlot.HAND;
        String s = plugin.getGame().getPlayerLobby(p);

        if (hand) {
            Material held = p.getInventory().getItemInMainHand().getType();
            if (isStew(held)) {
                handleStew(p, s);
            } else if (held == Material.REDSTONE_TORCH) {
                handleAirdropTorch(p, s);
            } else if (held == Material.MILK_BUCKET) {
                handleMilk(p, s);
            } else if (held == Material.COMPASS) {
                handlePlayerFinder(p, s);
            }
        }

        Material mainHand = p.getInventory().getItemInMainHand().getType();
        if (mainHand == Material.PAPER) {
            openArenaSelector(p, s);
        } else if (mainHand == Material.CLOCK) {
            openTeamSelector(p, s);
        } else if (mainHand == Material.ENDER_CHEST) {
            openKitSelector(p, s);
        } else if (mainHand == Material.MAGMA_CREAM) {
            GameStates state = plugin.getLobbyStateManager().getGameState(s);
            if (state == GameStates.WAITING || state == GameStates.PREPARING) {
                plugin.onLeaveLobby(p, s);
            }
        } else if (mainHand == Material.EMERALD) {
            if (p.hasPermission("pg.stats")) {
                sendStats(p);
            }
        }
    }

    private static boolean isStew(Material material) {
        return material == Material.MUSHROOM_STEW
            || material == Material.RABBIT_STEW
            || material == Material.BEETROOT_SOUP;
    }

    private void handleStew(Player p, String s) {
        if (plugin.getLobbyStateManager().getGameState(s) != GameStates.INGAME) {
            return;
        }
        double health = p.getHealth();
        int foodlvl = p.getFoodLevel();
        if (health >= 20 && foodlvl >= 13) {
            p.setFoodLevel(20);
        } else if (foodlvl < 13) {
            p.setFoodLevel(Math.min(20, foodlvl + 7));
        } else if (health >= 13) {
            p.setHealth(20);
        } else {
            p.setHealth(Math.min(20, health + 7));
        }
        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
    }

    private void handleMilk(Player p, String s) {
        if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
            plugin.clearEffects(p);
            p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
        }
    }

    private void handleAirdropTorch(Player p, String s) {
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null || !lobby.isActivateAirdrops()) {
            return;
        }
        if (plugin.getLobbyStateManager().getGameState(s) != GameStates.INGAME) {
            return;
        }
        boolean blocked = false;
        Location loc = p.getEyeLocation().add(0, 1, 0);
        while (loc.getY() <= 320) {
            if (loc.getBlock().getType() != Material.AIR) {
                p.sendMessage(Messages.YouBlockAbove());
                blocked = true;
                break;
            }
            loc.add(0, 1, 0);
        }
        if (blocked) {
            return;
        }
        Location ploc = p.getLocation();
        for (Player all : plugin.getGame().getPlayersInLobby(s)) {
            all.sendMessage(Messages.AirdropFallingAt(ploc.getBlockX() + " " + ploc.getBlockY() + " " + ploc.getBlockZ()));
        }
        p.sendMessage(Messages.AirdropFallingHere());
        BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
        Location spawnLoc = new Location(p.getWorld(), ploc.getX(), ploc.getY() + 100, ploc.getZ());
        p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
        lobby.addPlacedBlock(ploc, b.getMaterial());
        var held = p.getInventory().getItemInMainHand();
        if (held.getAmount() > 1) {
            held.setAmount(held.getAmount() - 1);
        } else {
            p.getInventory().setItemInMainHand(null);
        }
    }

    private void handlePlayerFinder(Player p, String s) {
        if (plugin.getLobbyStateManager().getGameState(s) != GameStates.INGAME) {
            return;
        }
        Player result = null;
        double lastDistance = Double.MAX_VALUE;
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        String playerTeam_p = lobby != null ? lobby.getPlayerTeam(p) : null;
        for (Player cp : p.getWorld().getPlayers()) {
            if (cp == p || plugin.getGame().getPlayerLobby(cp) == null) {
                continue;
            }
            String playerTeam_cp = lobby != null ? lobby.getPlayerTeam(cp) : null;
            if (!Objects.equals(playerTeam_p, playerTeam_cp)) {
                continue;
            }
            double distance = p.getLocation().distance(cp.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                result = cp;
            }
        }
        if (result != null) {
            p.sendActionBar(Messages.PlayerFinderDistance((int) lastDistance));
        } else {
            p.sendActionBar(Messages.NoPlayerFound());
        }
    }

    private void openArenaSelector(Player p, String s) {
        GameStates state = plugin.getLobbyStateManager().getGameState(s);
        if (state != GameStates.WAITING && state != GameStates.PREPARING) {
            return;
        }
        Map<String, Integer> lobbyVotesMap = new HashMap<>();
        String randomVoteKey = Messages.RandomText();
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby != null) {
            for (Arena arena : lobby.getArenas()) {
                lobbyVotesMap.put(arena.getName(), plugin.getArenaStateManager().getLobbyVoteCount(s, arena.getName()));
            }
        }
        ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta randombarriermeta = randombarrier.getItemMeta();
        if (randombarriermeta == null) {
            return;
        }
        randombarriermeta.displayName(Messages.RandomItem());
        List<Component> randomlore = new ArrayList<>();
        randomlore.add(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyVoteCount(s, randomVoteKey))).color(NamedTextColor.AQUA)));
        randombarriermeta.lore(randomlore);
        if (!randombarrier.setItemMeta(randombarriermeta)) {
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
        inv.setItem(0, randombarrier);
        int slot = 1;
        for (String arenaName : lobbyVotesMap.keySet()) {
            if (arenaName.equals(randomVoteKey) || slot >= inv.getSize()) {
                continue;
            }
            ArrayList<Component> arenalore = new ArrayList<>();
            arenalore.add(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(lobbyVotesMap.getOrDefault(arenaName, 0))).color(NamedTextColor.AQUA)));
            ItemStack arenamap = new ItemStack(Material.MAP);
            ItemMeta arenamapmeta = arenamap.getItemMeta();
            if (arenamapmeta == null) {
                continue;
            }
            arenamapmeta.displayName(Component.text(arenaName).color(NamedTextColor.GOLD));
            arenamapmeta.lore(arenalore);
            if (!arenamap.setItemMeta(arenamapmeta)) {
                continue;
            }
            inv.setItem(slot++, arenamap);
        }
        p.openInventory(inv);
    }

    private void openTeamSelector(Player p, String s) {
        GameStates state = plugin.getLobbyStateManager().getGameState(s);
        if (state != GameStates.WAITING && state != GameStates.PREPARING) {
            return;
        }
        Map<Integer, Integer> lobbyTeamsMap = plugin.getArenaStateManager().getLobbyTeams(s);
        Map<Player, String> lobbyTeamPlayerNamesMap = new HashMap<>();
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby != null) {
            lobbyTeamPlayerNamesMap = lobby.getTeamPlayerNamesMap();
        }
        ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta randombarriermeta = randombarrier.getItemMeta();
        if (randombarriermeta == null) {
            return;
        }
        randombarriermeta.displayName(Messages.RandomItem());
        if (!randombarrier.setItemMeta(randombarriermeta)) {
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.SelectorTeamTitle());
        inv.setItem(0, randombarrier);
        int slot = 1;
        for (Integer teamId : lobbyTeamsMap.keySet()) {
            if (slot >= inv.getSize()) {
                break;
            }
            ArrayList<Component> arenalore = new ArrayList<>();
            arenalore.add(0, Component.text(Messages.PlayersText() + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(lobbyTeamsMap.getOrDefault(teamId, 0))).color(NamedTextColor.AQUA)));
            ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta arenamapmeta = arenamap.getItemMeta();
            if (arenamapmeta == null) {
                continue;
            }
            arenamapmeta.displayName(Component.text(Integer.toString(teamId)).color(NamedTextColor.GOLD));
            for (Player temp : lobbyTeamPlayerNamesMap.keySet()) {
                if (temp != null && lobbyTeamPlayerNamesMap.getOrDefault(temp, "").equals(Integer.toString(teamId))) {
                    arenalore.add(Component.text(temp.getName()).color(NamedTextColor.GRAY));
                }
            }
            arenamapmeta.lore(arenalore);
            if (!arenamap.setItemMeta(arenamapmeta)) {
                continue;
            }
            inv.setItem(slot++, arenamap);
        }
        p.openInventory(inv);
    }

    private void openKitSelector(Player p, String s) {
        GameStates state = plugin.getLobbyStateManager().getGameState(s);
        if (state != GameStates.WAITING && state != GameStates.PREPARING) {
            return;
        }
        ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta randombarriermeta = randombarrier.getItemMeta();
        if (randombarriermeta == null) {
            return;
        }
        randombarriermeta.displayName(Messages.RandomItem());
        if (!randombarrier.setItemMeta(randombarriermeta)) {
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.KitSelector());
        inv.setItem(0, randombarrier);
        int kitSlots = Math.min(plugin.getConfigManager().getActiveKits(), inv.getSize() - 1);
        for (int i = 1; i <= kitSlots; i++) {
            String kitName = Settings.kitdata.getString("pg.kits." + i + ".name");
            if (kitName == null) {
                continue;
            }
            ItemStack kitItem = new ItemStack(Material.ARMOR_STAND);
            ItemMeta kitItemMeta = kitItem.getItemMeta();
            if (kitItemMeta == null) {
                continue;
            }
            kitItemMeta.displayName(Component.text(kitName).color(NamedTextColor.GOLD));
            if (!kitItem.setItemMeta(kitItemMeta)) {
                continue;
            }
            inv.setItem(i, kitItem);
        }
        p.openInventory(inv);
    }

    /** Chests, waterlogged tracking and shop access on right-clicked blocks. */
    private void handleRightClickBlock(PlayerInteractEvent e, Player p) {
        if (e.getHand() != EquipmentSlot.HAND || e.getClickedBlock() == null) {
            return;
        }
        var clickedBlock = e.getClickedBlock();

        // Waterlogged block tracking for restoration
        if (clickedBlock.getBlockData() instanceof org.bukkit.block.data.Waterlogged) {
            Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(plugin.getGame().getPlayerLobby(p)));
            if (lobby != null) {
                lobby.addLiquidBlock(clickedBlock.getLocation(), clickedBlock.getBlockData());
            }
        }

        openNormalChest(e, p);
        openCustomChest(e, p);
        handleShopChest(e, p);
    }

    private void openNormalChest(PlayerInteractEvent e, Player p) {
        var normalChestBlock = Settings.chests.get("pg.chestblocks.normal");
        if (normalChestBlock == null
                || !e.getClickedBlock().getType().toString().equals(normalChestBlock.toString())) {
            return;
        }
        Location loc = e.getClickedBlock().getLocation();
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(plugin.getGame().getPlayerLobby(p)));
        if (lobby == null || plugin.getLobbyStateManager().getGameState(Integer.toString(lobby.getId())) != GameStates.INGAME) {
            return;
        }
        if (!lobby.hasChestInventory(loc)) {
            Inventory inv = Bukkit.createInventory(p, 27, Settings.prefix);
            Random rnd = new Random();
            IItemStateManager itemStateManager = plugin.getItemStateManager();
            ChestLootProfile profile = resolveLootProfile(
                    Settings.chests.getConfigurationSection("pg.chestloot.normal"),
                    defaultNormalLootProfile());
            fillLootChest(inv, rnd, profile, itemStateManager);
            lobby.setChestInventory(loc, inv.getContents());
        }
        ItemStack[] items = lobby.getChestInventory(loc);
        if (items == null) {
            return;
        }
        Inventory view = Bukkit.createInventory(null, 27);
        view.setContents(items);
        p.openInventory(view);

        // Bonus random potion effect on first open of an empty-effects player
        if (p.getActivePotionEffects().isEmpty()) {
            Random effect = new Random();
            int tries = effect.nextInt(4); // 0..3 effects
            ArrayList<PotionEffect> potions = new ArrayList<>(plugin.getItemStateManager().getPotions());
            while (tries-- > 0 && !potions.isEmpty()) {
                p.addPotionEffect(potions.get(effect.nextInt(potions.size())));
            }
        }
    }

    private void openCustomChest(PlayerInteractEvent e, Player p) {
        Location loc = e.getClickedBlock().getLocation();
        int chestnumber = 1;
        while (Settings.chests.contains("pg.customchests." + chestnumber)) {
            ConfigurationSection customChest = Settings.chests.getConfigurationSection("pg.customchests." + chestnumber);
            if (customChest != null) {
                Object chestType = customChest.get("chesttype");
                if (chestType != null && e.getClickedBlock().getType().toString().equals(chestType.toString())
                        && customChest.getBoolean("activate")) {
                    String s = plugin.getGame().getPlayerLobby(p);
                    Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
                    if (lobby != null && plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                        if (!lobby.hasChestInventory(loc)) {
                            Inventory inv = Bukkit.createInventory(p, customChest.getInt("chestsize"), Settings.prefix);
                            int chestitem = 1;
                            while (customChest.contains(String.valueOf(chestitem))) {
                                inv.setItem(customChest.getInt(chestitem + ".slot") - 1, customChest.getItemStack(chestitem + ".item"));
                                chestitem++;
                            }
                            Random rnd = new Random();
                            ChestLootProfile profile = resolveLootProfile(customChest, defaultCustomLootProfile());
                            fillLootChest(inv, rnd, profile, plugin.getItemStateManager());
                            lobby.setChestInventory(loc, inv.getContents());
                            p.openInventory(inv);
                        } else {
                            reopenStoredChest(p, lobby, loc);
                        }
                    }
                    break;
                }
            }
            chestnumber++;
        }
    }

    private void handleShopChest(PlayerInteractEvent e, Player p) {
        var shopChestBlock = Settings.chests.get("pg.chestblocks.shop");
        if (shopChestBlock == null
                || !e.getClickedBlock().getType().toString().equals(shopChestBlock.toString())) {
            return;
        }
        Location loc = e.getClickedBlock().getLocation();
        String s = plugin.getGame().getPlayerLobby(p);
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null || !lobby.isActivateShop()) {
            return;
        }
        GameStates state = plugin.getLobbyStateManager().getGameState(s);
        if (state != GameStates.INGAME && state != GameStates.DEATHMATCH) {
            reopenStoredChest(p, lobby, loc);
            return;
        }

        Inventory inv = Bukkit.createInventory(p, 9 * 3, Messages.ShopTitle());
        var itemStateManager = plugin.getItemStateManager();
        List<String> shopItems = new ArrayList<>(itemStateManager.getShopItems());
        Participant participant = lobby.getParticipant(p);
        String kitName = participant != null && participant.getKit() != null ? participant.getKit().getName() : null;
        int slots = Math.min(shopItems.size(), inv.getSize());
        for (int idx = 0; idx < slots; idx++) {
            int coinamount = kitName != null && kitName.equals(itemStateManager.getShopKit(idx))
                ? itemStateManager.getShopSale(idx)
                : itemStateManager.getShopCost(idx);
            ItemStack potionType = itemStateManager.getShopPotionType(idx);
            PotionEffect shopPotion = itemStateManager.getShopPotion(idx);
            if (potionType == null || shopPotion == null) continue;
            ItemStack shopEntry = new ItemStack(potionType);
            ItemMeta shopEntryMeta = shopEntry.getItemMeta();
            if (shopEntryMeta == null) {
                continue;
            }
            shopEntryMeta.displayName(Component.text(shopItems.get(idx)));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text(Messages.DurationText() + ": " + shopPotion.getDuration() / 20));
            lore.add(Component.text(Messages.PriceLabel() + ": " + coinamount + " " + Messages.CoinsText()));
            shopEntryMeta.lore(lore);
            if (!shopEntry.setItemMeta(shopEntryMeta)) {
                continue;
            }
            inv.setItem(idx, shopEntry);
        }
        lobby.setChestInventory(loc, inv.getContents());
        p.openInventory(inv);
    }

    private void reopenStoredChest(Player p, Lobby lobby, Location loc) {
        ItemStack[] items = lobby.getChestInventory(loc);
        if (items == null) {
            return;
        }
        Inventory view = Bukkit.createInventory(null, items.length > 0 ? Math.max(9, ((items.length + 8) / 9) * 9) : 27);
        view.setContents(items);
        p.openInventory(view);
    }

    private void sendStats(Player p) {
        String uuid = p.getUniqueId().toString();
        int wins = plugin.getDatabaseManager().getWins(uuid);
        int losses = plugin.getDatabaseManager().getLosses(uuid);
        int rounds = plugin.getDatabaseManager().getRounds(uuid);
        int kills = plugin.getDatabaseManager().getKills(uuid);
        int deaths = plugin.getDatabaseManager().getDeaths(uuid);
        double kd = plugin.getDatabaseManager().getKD(uuid);
        p.sendMessage(Messages.StatsLabel());
        p.sendMessage(Messages.RoundsLabel(rounds));
        p.sendMessage(Messages.WinsLabel(wins));
        p.sendMessage(Messages.LossesLabel(losses));
        p.sendMessage(Messages.KillsLabel(kills));
        p.sendMessage(Messages.DeathsLabel(deaths));
        p.sendMessage(Messages.KDLabel(kd));
        p.sendMessage(Messages.StatsLabel());
    }

    // ===== Loot filling =====

    private void fillLootChest(Inventory inv, Random rnd, ChestLootProfile profile, IItemStateManager itemStateManager) {
        if (profile.factor <= 0.0) {
            return;
        }

        ArrayList<Integer> emptySlots = new ArrayList<>();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (inv.getItem(slot) == null) {
                emptySlots.add(slot);
            }
        }

        int baseRolls = rnd.nextInt(5) + 2;
        int rolls = Math.max(1, (int) Math.round(baseRolls * profile.factor));

        for (int i = 0; i < rolls && !emptySlots.isEmpty(); i++) {
            LootPool pool = pickLootPool(rnd, profile);
            ItemStack loot = pickLootItem(pool, itemStateManager, rnd);
            if (loot == null) {
                continue;
            }

            int slotIndex = rnd.nextInt(emptySlots.size());
            int slot = emptySlots.remove(slotIndex);
            inv.setItem(slot, loot);
        }
    }

    private LootPool pickLootPool(Random rnd, ChestLootProfile profile) {
        double total = profile.totalWeight();
        if (total <= 0.0) {
            return LootPool.FOOD1;
        }

        double selection = rnd.nextDouble() * total;
        double current = 0.0;
        for (LootPool pool : LootPool.values()) {
            current += profile.weight(pool);
            if (selection <= current) {
                return pool;
            }
        }
        return LootPool.WEAPONS2;
    }

    private ItemStack pickLootItem(LootPool pool, IItemStateManager itemStateManager, Random rnd) {
        return switch (pool) {
            case FOOD1 -> itemStateManager.getRandomFood1();
            case FOOD2 -> itemStateManager.getRandomFood2();
            case ARMOUR1 -> itemStateManager.getRandomArmor(1);
            case ARMOUR2 -> itemStateManager.getRandomArmor(2);
            case ARMOUR3 -> itemStateManager.getRandomArmor(3);
            case ARMOUR4 -> itemStateManager.getRandomArmor(4);
            case ARMOUR5 -> itemStateManager.getRandomArmor(5);
            case WEAPONS1 -> itemStateManager.getRandomWeapon(1);
            case WEAPONS2 -> itemStateManager.getRandomWeapon(2);
        };
    }

    private ChestLootProfile resolveLootProfile(ConfigurationSection chestSection, ChestLootProfile defaultProfile) {
        if (chestSection == null) {
            return defaultProfile;
        }

        ConfigurationSection lootSection = chestSection.getConfigurationSection("loot");
        if (lootSection == null) {
            return defaultProfile;
        }

        ChestLootProfile profile = defaultProfile;
        profile.factor = lootSection.getDouble("factor", profile.factor);
        profile.food1 = lootSection.getDouble("food1", profile.food1);
        profile.food2 = lootSection.getDouble("food2", profile.food2);
        profile.armour1 = lootSection.getDouble("armour1", profile.armour1);
        profile.armour2 = lootSection.getDouble("armour2", profile.armour2);
        profile.armour3 = lootSection.getDouble("armour3", profile.armour3);
        profile.armour4 = lootSection.getDouble("armour4", profile.armour4);
        profile.armour5 = lootSection.getDouble("armour5", profile.armour5);
        profile.weapons1 = lootSection.getDouble("weapons1", profile.weapons1);
        profile.weapons2 = lootSection.getDouble("weapons2", profile.weapons2);
        return profile;
    }

    private ChestLootProfile defaultNormalLootProfile() {
        ChestLootProfile profile = new ChestLootProfile();
        var config = plugin.getConfig();
        if (config != null && config.contains("pg.chestloot.normal.loot")) {
            var lootConfig = config.getConfigurationSection("pg.chestloot.normal.loot");
            if (lootConfig != null) {
                profile.factor = lootConfig.getDouble("factor", 1.0);
                profile.food1 = lootConfig.getDouble("food1", 20.0);
                profile.food2 = lootConfig.getDouble("food2", 10.0);
                profile.armour1 = lootConfig.getDouble("armour1", 15.0);
                profile.armour2 = lootConfig.getDouble("armour2", 15.0);
                profile.armour3 = lootConfig.getDouble("armour3", 7.0);
                profile.armour4 = lootConfig.getDouble("armour4", 5.0);
                profile.armour5 = lootConfig.getDouble("armour5", 3.0);
                profile.weapons1 = lootConfig.getDouble("weapons1", 20.0);
                profile.weapons2 = lootConfig.getDouble("weapons2", 5.0);
                return profile;
            }
        }
        return profile;
    }

    private ChestLootProfile defaultCustomLootProfile() {
        ChestLootProfile profile = defaultNormalLootProfile();
        profile.factor = 0.0;
        return profile;
    }

    /** Weighted loot table for a chest type. */
    static final class ChestLootProfile {
        private double factor = 1.0;
        private double food1 = 20.0;
        private double food2 = 10.0;
        private double armour1 = 15.0;
        private double armour2 = 15.0;
        private double armour3 = 7.0;
        private double armour4 = 5.0;
        private double armour5 = 3.0;
        private double weapons1 = 20.0;
        private double weapons2 = 5.0;

        double totalWeight() {
            return food1 + food2 + armour1 + armour2 + armour3 + armour4 + armour5 + weapons1 + weapons2;
        }

        double weight(LootPool pool) {
            return switch (pool) {
                case FOOD1 -> food1;
                case FOOD2 -> food2;
                case ARMOUR1 -> armour1;
                case ARMOUR2 -> armour2;
                case ARMOUR3 -> armour3;
                case ARMOUR4 -> armour4;
                case ARMOUR5 -> armour5;
                case WEAPONS1 -> weapons1;
                case WEAPONS2 -> weapons2;
            };
        }
    }

    private enum LootPool {
        FOOD1,
        FOOD2,
        ARMOUR1,
        ARMOUR2,
        ARMOUR3,
        ARMOUR4,
        ARMOUR5,
        WEAPONS1,
        WEAPONS2
    }
}
