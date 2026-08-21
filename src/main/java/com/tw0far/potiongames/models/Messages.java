package com.tw0far.potiongames.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;

public class Messages {
    private static String get(String key, String def) {
        String lang = Settings.language != null ? Settings.language : "en_US";
        String primaryPath = lang + ".messages." + key;
        String value = Settings.messages.getString(primaryPath, null);
        if (value == null) {
            value = Settings.messages.getString("en_US.messages." + key, def);
        }
        return value != null ? value : def;
    }

    private static Component prefix(Component message) {
        return Settings.prefix.append(message);
    }

    public static void seed() {
        String lang = "en_US";
        addDefault(lang, "lobby.added", "Lobby %d added successfully.");
        addDefault(lang, "lobby.added_failed", "Failed to add lobby %d.");
        addDefault(lang, "lobby.removed", "Lobby %d removed successfully.");
        addDefault(lang, "lobby.removed_failed", "Failed to remove lobby %d.");
        addDefault(lang, "arena.added", "Arena %s added successfully. (Lobby: %d)");
        addDefault(lang, "arena.removed", "Arena %s removed successfully. (Lobby: %d)");
        addDefault(lang, "spawn.added", "Spawn %d added successfully. (Arena: %s, Lobby: %d)");
        addDefault(lang, "spawn.removed", "Spawn %d removed successfully. (Arena: %s, Lobby: %d)");
        addDefault(lang, "airdrop.falling_at", "Airdrop is falling at");
        addDefault(lang, "airdrop.falling_here", "Airdrop is falling at your location!");
        addDefault(lang, "arena.could_not_load", "Could not load an arena!");
        addDefault(lang, "arena.forced", "has been forced as arena!");
        addDefault(lang, "arena.not_arena", "is not an arena!");
        addDefault(lang, "arena.not_exists", "This arena does not exist!");
        addDefault(lang, "arena.selector", "Arena-Selector");
        addDefault(lang, "build.toggle", "Build");
        addDefault(lang, "coin.single", "Coin");
        addDefault(lang, "coins.label", "Coins");
        addDefault(lang, "commands.label", "Commands");
        addDefault(lang, "deathmatch.started", "Deathmatch started!");
        addDefault(lang, "deathmatch.starting_in", "Deathmatch is starting in");
        addDefault(lang, "deaths.label", "Deaths");
        addDefault(lang, "duration.label", "Duration");
        addDefault(lang, "error.generic", "An error occurred");
        addDefault(lang, "file.save_failed", "File loading / saving fail! For more information see console.");
        addDefault(lang, "game.already_started", "The game has already started!");
        addDefault(lang, "game.started", "The game has been started!");
        addDefault(lang, "game.not_enough_players", "Not enough players to start the game!");
        addDefault(lang, "game.starts_in", "The game starts in");
        addDefault(lang, "game.waiting", "Waiting for players! [%d/%d]");
        addDefault(lang, "help.use_pg_help", "Use /pg help for help!");
        addDefault(lang, "join.already_in_lobby", "You are already in a lobby!");
        addDefault(lang, "kd.label", "K/D");
        addDefault(lang, "kill.reward", "For killing a player you get");
        addDefault(lang, "killed.by", "was killed by");
        addDefault(lang, "died", "died");
        addDefault(lang, "kills.label", "Kills");
        addDefault(lang, "kit.now_have", "You now have the kit");
        addDefault(lang, "kit.selector", "Kit-Selector");
        addDefault(lang, "leave.label", "Leave");
        addDefault(lang, "lobby.does_not_exist", "This lobby does not exist!");
        addDefault(lang, "arena.does_not_exist", "This arena does not exist!");
        addDefault(lang, "lobby.full", "This lobby is full!");
        addDefault(lang, "lobby.starting_broadcast", "Lobby %s is starting! Join with /pg join %s");
        addDefault(lang, "lobby.success_set", "Lobby successfully set!");
        addDefault(lang, "lost.label", "Losses");
        addDefault(lang, "no_player_found", "No player found!");
        addDefault(lang, "pause.toggle", "Pause");
        addDefault(lang, "sign.place", "Place");
        addDefault(lang, "sign.wins", "Wins");
        addDefault(lang, "sign.kd", "K/D");
        addDefault(lang, "player.finder", "Player-Finder");
        addDefault(lang, "choose.arena", "Choose Arena");
        addDefault(lang, "choose.lobby", "Choose Lobby");
        addDefault(lang, "lobby.list", "Lobby List");
        addDefault(lang, "choose.lobby_first", "Choose a lobby first!");
        addDefault(lang, "choose.arena_first", "Choose an arena first!");
        addDefault(lang, "permission.no_use", "You don't have permission to use this!");
        addDefault(lang, "spawn.none_to_remove", "No spawns to remove!");
        addDefault(lang, "deathmatch_spawn.none_to_remove", "No deathmatch spawns to remove!");
        addDefault(lang, "setup.add_delete_lobby", "Add(Left)/Del(Right) Lobby");
        addDefault(lang, "setup.add_delete_arena", "Add(Left)/Del(Right) Arena");
        addDefault(lang, "setup.add_delete_spawn", "Add(Left)/Del(Right) Spawn");
        addDefault(lang, "setup.add_delete_deathmatch_spawn", "Add(Left)/Del(Right) Deathmatch-Spawn");
        addDefault(lang, "setup.join_sign", "Set Join-Sign");
        addDefault(lang, "setup.leave_mode", "Leave Setup-Mode");
        addDefault(lang, "sign.prefix", "[PG]");
        addDefault(lang, "lobby.selected", "Lobby %d selected!");
        addDefault(lang, "arena.selected", "Arena %s selected!");
        addDefault(lang, "lobby.invalid_selection", "Invalid lobby selection!");
        addDefault(lang, "players.label", "Players");
        addDefault(lang, "plugin.started", "Plugin started successfully!");
        addDefault(lang, "plugin.stopped", "Plugin stopped successfully!");
        addDefault(lang, "price.label", "Price");
        addDefault(lang, "random.label", "Random");
        addDefault(lang, "rankwall.could_not_update", "Could not update Rank-Wall!");
        addDefault(lang, "round.nobody_won", "Nobody won this round!");
        addDefault(lang, "round.seconds_remaining", "seconds remaining to end this round!");
        addDefault(lang, "round.minutes_remaining", "minutes remaining to end this round!");
        addDefault(lang, "rounds.label", "Rounds");
        addDefault(lang, "selector.team", "Team-Selector");
        addDefault(lang, "server.stopped", "Server stopped!");
        addDefault(lang, "shop.label", "Shop");
        addDefault(lang, "sign.set", "Sign successfully set!");
        addDefault(lang, "stats.label", "Stats");
        addDefault(lang, "team.already_full", "This team is already full!");
        addDefault(lang, "team.now_in", "You are now in team");
        addDefault(lang, "teleport.to_lobby_in", "Teleporting to lobby in");
        addDefault(lang, "type.arena_name_add", "Type arena name in chat to add it!");
        addDefault(lang, "update.available", "There is a new update available. %s -> %s");
        addDefault(lang, "update.checker_error", "Update-Checker-Error");
        addDefault(lang, "update.not_available", "There is not a new update available.");
        addDefault(lang, "vote.label", "Votes");
        addDefault(lang, "will_be_played", "will be played!");
        addDefault(lang, "winner.has_won_the_game", "has won the game!");
        addDefault(lang, "won.label", "Wins");
        addDefault(lang, "you.block_above", "You have a block above you!");
        addDefault(lang, "won.reward", "For winning the round you get");
        addDefault(lang, "player.finder_distance", "Blocks away from next player");
        addDefault(lang, "you.not_empty_bottle", "You don't have an empty bottle!");
        addDefault(lang, "you.not_enough_coins", "You don't have enough coins!");

        Settings.messages.options().copyDefaults(true);
        try {
            Settings.messages.save(Settings.messagesFile);
        } catch (IOException ignored) {
        }
    }

    private static void addDefault(String lang, String key, String def) {
        String path = lang + ".messages." + key;
        if (Settings.messages.get(path) == null) {
            Settings.messages.addDefault(path, def);
        }
    }

    public static final Component LobbyAdded(int lobbyId) {
        String pattern = get("lobby.added", "Lobby %d added successfully.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component LobbyAddedFailed(int lobbyId) {
        String pattern = get("lobby.added_failed", "Failed to add lobby %d.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.RED));
    }

    public static final Component LobbyRemoved(int lobbyId) {
        String pattern = get("lobby.removed", "Lobby %d removed successfully.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component LobbyRemovedFailed(int lobbyId) {
        String pattern = get("lobby.removed_failed", "Failed to remove lobby %d.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.RED));
    }

    public static final Component ArenaAdded(String arenaName, int lobbyId) {
        String pattern = get("arena.added", "Arena %s added successfully. (Lobby: %d)");
        return prefix(Component.text(String.format(pattern, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component ArenaRemoved(String arenaName, int lobbyId) {
        String pattern = get("arena.removed", "Arena %s removed successfully. (Lobby: %d)");
        return prefix(Component.text(String.format(pattern, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component SpawnAdded(int spawnId, String arenaName, int lobbyId) {
        String pattern = get("spawn.added", "Spawn %d added successfully. (Arena: %s, Lobby: %d)");
        return prefix(Component.text(String.format(pattern, spawnId, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component SpawnRemoved(int spawnId, String arenaName, int lobbyId) {
        String pattern = get("spawn.removed", "Spawn %d removed successfully. (Arena: %s, Lobby: %d)");
        return prefix(Component.text(String.format(pattern, spawnId, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static Component AirdropFallingAt(String location) {
        return prefix(Component.text(get("airdrop.falling_at", "Airdrop is falling at") + " " + location).color(NamedTextColor.GOLD));
    }

    public static Component AirdropFallingHere() {
        return prefix(Component.text(get("airdrop.falling_here", "Airdrop is falling at your location!")).color(NamedTextColor.GOLD));
    }

    public static Component ArenaCouldNotLoad() {
        return prefix(Component.text(get("arena.could_not_load", "Could not load an arena!")).color(NamedTextColor.RED));
    }

    public static Component ArenaForced(String arenaName) {
        return prefix(Component.text(arenaName + " " + get("arena.forced", "has been forced as arena!")).color(NamedTextColor.GREEN));
    }

    public static Component ArenaNotArena(String arenaName) {
        return prefix(Component.text(arenaName + " " + get("arena.not_arena", "is not an arena!")).color(NamedTextColor.RED));
    }

    public static Component ArenaNotExists() {
        return prefix(Component.text(get("arena.not_exists", "This arena does not exist!")).color(NamedTextColor.RED));
    }

    public static Component ArenaSelector() {
        return prefix(Component.text("--------------" + get("arena.selector", "Arena-Selector") + "--------------").color(NamedTextColor.GRAY));
    }

    public static String ArenaSelectorText() {
        return get("arena.selector", "Arena-Selector");
    }

    public static Component ArenaSelectorTitle() {
        return Component.text(ArenaSelectorText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component ArenaSelectorItem() {
        return Component.text(ArenaSelectorText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component BuildToggle(boolean enabled) {
        NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        return prefix(Component.text(get("build.toggle", "Build") + ": " + enabled).color(color));
    }

    public static Component CoinSingle() {
        return prefix(Component.text(get("coin.single", "Coin")).color(NamedTextColor.GOLD));
    }

    public static String CoinsText() {
        return get("coins.label", "Coins");
    }

    public static Component CommandsLabel() {
        return prefix(Component.text("--------------" + get("commands.label",  "Commands") + "--------------").color(NamedTextColor.GRAY));
    }

    public static Component DeathmatchStarted() {
        return prefix(Component.text(get("deathmatch.started", "Deathmatch started!")).color(NamedTextColor.RED));
    }

    public static Component DeathmatchStartingIn(int time) {
        return prefix(Component.text(get("deathmatch.starting_in", "Deathmatch is starting in") + " " + time).color(NamedTextColor.GOLD));
    }

    public static Component DeathsLabel(int deaths) {
        return prefix(Component.text(get("deaths.label", "Deaths") + ": ").color(NamedTextColor.GREEN).append(Component.text(deaths).color(NamedTextColor.AQUA)));
    }

    public static String DurationText() {
        return get("duration.label", "Duration");
    }

    public static Component ErrorGeneric() {
        return prefix(Component.text(get("error.generic", "An error occurred")).color(NamedTextColor.RED));
    }

    public static Component FileSaveFailed() {
        return prefix(Component.text(get("file.save_failed", "File loading / saving fail! For more information see console.")).color(NamedTextColor.RED));
    }

    public static Component GameAlreadyStarted() {
        return prefix(Component.text(get("game.already_started", "The game has already started!")).color(NamedTextColor.RED));
    }

    public static Component GameStarted() {
        return prefix(Component.text(get("game.started", "The game has been started!")).color(NamedTextColor.GREEN));
    }

    public static Component GameNotEnoughPlayers() {
        return prefix(Component.text(get("game.not_enough_players", "Not enough players to start the game!")).color(NamedTextColor.RED));
    }

    public static Component GameStartsIn(int time) {
        return prefix(Component.text(get("game.starts_in", "The game starts in") + ": ").color(NamedTextColor.GREEN).append(Component.text(time).color(NamedTextColor.AQUA)));
    }

    public static Component GameWaiting(int players, int minPlayers) {
        return Component.text(String.format(get("game.waiting", "Waiting for players! [%d/%d]"), players, minPlayers)).color(NamedTextColor.GRAY);
    }

    public static Component HelpUsePgHelp() {
        return prefix(Component.text(get("help.use_pg_help", "Use /pg help for help!")).color(NamedTextColor.AQUA));
    }

    public static Component JoinAlreadyInLobby() {
        return prefix(Component.text(get("join.already_in_lobby", "You are already in a lobby!")).color(NamedTextColor.RED));
    }

    public static Component KDLabel(double kd) {
        return prefix(Component.text(get("kd.label", "K/D") + ": ").color(NamedTextColor.GREEN).append(Component.text(kd).color(NamedTextColor.AQUA)));
    }

    public static Component KillReward(int amount) {
        return prefix(Component.text(get("kill.reward", "For killing a player you get") + " " + amount).color(NamedTextColor.GOLD));
    }

    public static Component KillsLabel(int kills) {
        return prefix(Component.text(get("kills.label", "Kills") + ": ").color(NamedTextColor.GREEN).append(Component.text(kills).color(NamedTextColor.AQUA)));
    }

    public static Component KitNowHave() {
        return prefix(Component.text(get("kit.now_have", "You now have the kit")).color(NamedTextColor.GREEN));
    }

    public static Component KitNowHave(String kitName) {
        return prefix(Component.text(get("kit.now_have", "You now have the kit") + " " + kitName).color(NamedTextColor.GREEN));
    }

    public static String KitSelectorText() {
        return get("kit.selector", "Kit-Selector");
    }

    public static Component KitSelector() {
        return Component.text(KitSelectorText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component KitSelectorItem() {
        return Component.text(KitSelectorText()).color(NamedTextColor.DARK_AQUA);
    }

    public static String LeaveText() {
        return get("leave.label", "Leave");
    }

    public static Component LeaveItem() {
        return Component.text(LeaveText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component LobbyDoesNotExist() {
        return prefix(Component.text(get("lobby.does_not_exist", "This lobby does not exist!")).color(NamedTextColor.RED));
    }

    public static Component LobbyFull() {
        return prefix(Component.text(get("lobby.full", "This lobby is full!")).color(NamedTextColor.RED));
    }

    public static Component LobbyStartingBroadcast(String lobbyId) {
        String pattern = get("lobby.starting_broadcast", "Lobby %s is starting! Join with /pg join %s");
        return prefix(Component.text(String.format(pattern, lobbyId, lobbyId)).color(NamedTextColor.GRAY));
    }

    public static Component ArenaDoesNotExist() {
        return prefix(Component.text(get("arena.does_not_exist", "This arena does not exist!")).color(NamedTextColor.RED));
    }

    public static Component LobbySuccessSet() {
        return prefix(Component.text(get("lobby.success_set", "Lobby successfully set!")).color(NamedTextColor.GREEN));
    }

    public static Component LossesLabel(int losses) {
        return prefix(Component.text(get("lost.label", "Losses") + ": ").color(NamedTextColor.GREEN).append(Component.text(losses).color(NamedTextColor.AQUA)));
    }

    public static Component NoPlayerFound() {
        return prefix(Component.text(get("no_player_found", "No player found!")).color(NamedTextColor.YELLOW));
    }

    public static Component PauseToggle(boolean paused) {
        NamedTextColor color = paused ? NamedTextColor.GREEN : NamedTextColor.RED;
        return prefix(Component.text(get("pause.toggle", "Pause") + ": " + paused).color(color));
    }

    public static Component SignPlace(int place) {
        return Component.text(get("sign.place", "Place") + ": #" + place);
    }

    public static Component SignWins(int wins) {
        return Component.text(get("sign.wins", "Wins") + ": " + wins);
    }

    public static Component SignKD(double kd) {
        return Component.text(get("sign.kd", "K/D") + ": " + kd);
    }

    public static Component PlayerFinder() {
        return prefix(Component.text(get("player.finder", "Player-Finder")).color(NamedTextColor.DARK_AQUA));
    }

    public static String PlayerFinderText() {
        return get("player.finder", "Player-Finder");
    }

    public static Component ChooseArenaTitle() {
        return prefix(Component.text(get("choose.arena", "Choose Arena")).color(NamedTextColor.DARK_AQUA));
    }

    public static Component ChooseArenaLabel() {
        return Component.text(get("choose.arena", "Choose Arena")).color(NamedTextColor.DARK_AQUA);
    }

    public static String ChooseArenaText() {
        return get("choose.arena", "Choose Arena");
    }

    public static Component ChooseLobbyTitle() {
        return prefix(Component.text(get("choose.lobby", "Choose Lobby")).color(NamedTextColor.DARK_AQUA));
    }

    public static Component ChooseLobbyLabel() {
        return Component.text(get("choose.lobby", "Choose Lobby")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component LobbyListTitle() {
        return Component.text(get("lobby.list", "Lobby List")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component ChooseLobbyFirst() {
        return prefix(Component.text(get("choose.lobby_first", "Choose a lobby first!")).color(NamedTextColor.YELLOW));
    }

    public static Component ChooseArenaFirst() {
        return prefix(Component.text(get("choose.arena_first", "Choose an arena first!")).color(NamedTextColor.YELLOW));
    }

    public static Component PermissionNoUse() {
        return prefix(Component.text(get("permission.no_use", "You don't have permission to use this!")).color(NamedTextColor.RED));
    }

    public static Component NoSpawnsToRemove() {
        return prefix(Component.text(get("spawn.none_to_remove", "No spawns to remove!")).color(NamedTextColor.YELLOW));
    }

    public static Component NoDeathmatchSpawnsToRemove() {
        return prefix(Component.text(get("deathmatch_spawn.none_to_remove", "No deathmatch spawns to remove!")).color(NamedTextColor.YELLOW));
    }

    public static Component SetupAddDeleteLobbyLabel() {
        return Component.text(get("setup.add_delete_lobby", "Add(Left)/Del(Right) Lobby")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SetupAddDeleteArenaLabel() {
        return Component.text(get("setup.add_delete_arena", "Add(Left)/Del(Right) Arena")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SetupAddDeleteSpawnLabel() {
        return Component.text(get("setup.add_delete_spawn", "Add(Left)/Del(Right) Spawn")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SetupAddDeleteDeathmatchSpawnLabel() {
        return Component.text(get("setup.add_delete_deathmatch_spawn", "Add(Left)/Del(Right) Deathmatch-Spawn")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SetupJoinSignLabel() {
        return Component.text(get("setup.join_sign", "Set Join-Sign")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SetupLeaveModeLabel() {
        return Component.text(get("setup.leave_mode", "Leave Setup-Mode")).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SignPrefix() {
        return Component.text(get("sign.prefix", "[PG]")).color(NamedTextColor.GOLD);
    }

    public static Component LobbySelected(int lobbyId) {
        return prefix(Component.text(String.format(get("lobby.selected", "Lobby %d selected!"), lobbyId)).color(NamedTextColor.GREEN));
    }

    public static Component ArenaSelected(String arenaName) {
        return prefix(Component.text(String.format(get("arena.selected", "Arena %s selected!"), arenaName)).color(NamedTextColor.GREEN));
    }

    public static Component InvalidLobbySelection() {
        return prefix(Component.text(get("lobby.invalid_selection", "Invalid lobby selection!")).color(NamedTextColor.RED));
    }

    public static String PlayersText() {
        return get("players.label", "Players");
    }

    public static Component PluginStarted() {
        return prefix(Component.text(get("plugin.started", "Plugin started successfully!")).color(NamedTextColor.GREEN));
    }

    public static Component PluginStopped() {
        return prefix(Component.text(get("plugin.stopped", "Plugin stopped successfully!")).color(NamedTextColor.RED));
    }

    public static Component PriceLabel() {
        return prefix(Component.text(get("price.label", "Price")).color(NamedTextColor.GOLD));
    }

    public static String RandomText() {
        return get("random.label", "Random");
    }

    public static Component RandomItem() {
        return Component.text(RandomText()).color(NamedTextColor.DARK_PURPLE);
    }

    public static Component RankwallCouldNotUpdate() {
        return prefix(Component.text(get("rankwall.could_not_update", "Could not update Rank-Wall!")).color(NamedTextColor.RED));
    }

    public static Component RoundNobodyWon() {
        return prefix(Component.text(get("round.nobody_won", "Nobody won this round!")).color(NamedTextColor.YELLOW));
    }

    public static Component RoundSecondsRemaining(int seconds) {
        return prefix(Component.text(seconds + " " + get("round.seconds_remaining", "seconds remaining to end this round!")).color(NamedTextColor.GOLD));
    }

    public static Component RoundMinutesRemaining(int minutes) {
        return prefix(Component.text(minutes + " " + get("round.minutes_remaining", "minutes remaining to end this round!")).color(NamedTextColor.GOLD));
    }

    public static Component RoundsLabel(int rounds) {
        return prefix(Component.text(get("rounds.label", "Rounds") + ": ").color(NamedTextColor.GREEN).append(Component.text(rounds).color(NamedTextColor.AQUA)));
    }

    public static Component SelectorTeam() {
        return prefix(Component.text(SelectorTeamText()).color(NamedTextColor.AQUA));
    }

    public static Component SelectorTeamTitle() {
        return Component.text(SelectorTeamText()).color(NamedTextColor.DARK_AQUA);
    }

    public static String SelectorTeamText() {
        return get("selector.team", "Team-Selector");
    }

    public static Component SelectorTeamItem() {
        return Component.text(SelectorTeamText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component ServerStopped() {
        return prefix(Component.text(get("server.stopped", "Server stopped!")).color(NamedTextColor.RED));
    }

    public static String ShopText() {
        return get("shop.label", "Shop");
    }

    public static Component ShopTitle() {
        return Component.text(ShopText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component SignSet() {
        return prefix(Component.text(get("sign.set", "Sign successfully set!")).color(NamedTextColor.GREEN));
    }

    public static String StatsText() {
        return get("stats.label", "Stats");
    }

    public static Component StatsLabel() {
        return prefix(Component.text("--------------" + StatsText() + "--------------").color(NamedTextColor.GRAY));
    }

    public static Component StatsItem() {
        return Component.text(StatsText()).color(NamedTextColor.DARK_AQUA);
    }

    public static Component TeamAlreadyFull() {
        return prefix(Component.text(get("team.already_full", "This team is already full!")).color(NamedTextColor.RED));
    }

    public static Component TeamNowIn(String teamName) {
        return prefix(Component.text(get("team.now_in", "You are now in team") + " " + teamName).color(NamedTextColor.GREEN));
    }

    public static String TeamNowInText() {
        return get("team.now_in", "You are now in team");
    }

    public static Component TeleportToLobbyIn(int time) {
        return prefix(Component.text(get("teleport.to_lobby_in", "Teleporting to lobby in") + " " + time).color(NamedTextColor.YELLOW));
    }

    public static Component TypeArenaNameAdd() {
        return prefix(Component.text(get("type.arena_name_add", "Type arena name in chat to add it!")).color(NamedTextColor.YELLOW));
    }

    public static Component UpdateAvailable(String currentVersion, String newVersion) {
        return prefix(Component.text(String.format(get("update.available", "There is a new update available. %s -> %s"), currentVersion, newVersion)).color(NamedTextColor.GREEN));
    }

    public static Component UpdateCheckerError() {
        return prefix(Component.text(get("update.checker_error", "Update-Checker-Error")).color(NamedTextColor.RED));
    }

    public static Component UpdateNotAvailable() {
        return prefix(Component.text(get("update.not_available", "There is not a new update available.")).color(NamedTextColor.GRAY));
    }

    public static String VoteText() {
        return get("vote.label", "Votes");
    }

    public static Component WillBePlayed(String arena) {
        return prefix(Component.text(arena + " " + get("will_be_played", "will be played!")).color(NamedTextColor.GOLD));
    }

    public static Component WinnerHasWonTheGame(String winner) {
        return prefix(Component.text(winner + " " + get("winner.has_won_the_game", "has won the game!")).color(NamedTextColor.GOLD));
    }

    public static Component WinsLabel(int wins) {
        return prefix(Component.text(get("won.label", "Wins") + ": ").color(NamedTextColor.GREEN).append(Component.text(wins).color(NamedTextColor.AQUA)));
    }

    public static Component YouBlockAbove() {
        return prefix(Component.text(get("you.block_above", "You have a block above you!")).color(NamedTextColor.YELLOW));
    }

    public static Component WinReward(int amount) {
        return prefix(Component.text(get("won.reward", "For winning the round you get") + " " + amount).color(NamedTextColor.GOLD));
    }

    public static Component PlayerFinderDistance(int distance) {
        return prefix(Component.text(get("player.finder_distance", "Blocks away from next player") + ": ").color(NamedTextColor.GREEN).append(Component.text(distance).color(NamedTextColor.AQUA)));
    }

    public static Component YouNotEmptyBottle() {
        return prefix(Component.text(get("you.not_empty_bottle", "You don't have an empty bottle!")).color(NamedTextColor.RED));
    }

    public static Component YouNotEnoughCoins() {
        return prefix(Component.text(get("you.not_enough_coins", "You don't have enough coins!")).color(NamedTextColor.RED));
    }

    public static String BucketWaterNotAllowedText() { return get("bucket.water_not_allowed", "Water buckets are not allowed during games!"); }
    public static String CommandAddarenaUsageText() { return get("command.addarena.usage", "Usage: /pg addarena <lobbynumber> <arenaname>"); }
    public static String CommandAdddeathmatchUsageText() { return get("command.adddeathmatch.usage", "Usage: /pg adddeathmatch <lobbynumber> <arenaname>"); }
    public static String CommandAddlobbyUsageText() { return get("command.addlobby.usage", "Usage: /pg addlobby <lobbynumber>"); }
    public static String CommandAddspawnUsageText() { return get("command.addspawn.usage", "Usage: /pg addspawn <lobbynumber> <arenaname>"); }
    public static String CommandDelarenaUsageText() { return get("command.delarena.usage", "Usage: /pg delarena <lobbynumber> <arenaname>"); }
    public static String CommandDeldeathmatchUsageText() { return get("command.deldeathmatch.usage", "Usage: /pg deldeathmatch <lobbynumber> <arenaname>"); }
    public static String CommandDellobbyUsageText() { return get("command.dellobby.usage", "Usage: /pg dellobby <lobbynumber>"); }
    public static String CommandDelspawnUsageText() { return get("command.delspawn.usage", "Usage: /pg delspawn <lobbynumber> <arenaname>"); }
    public static String CommandExecutionErrorText() { return get("command.execution_error", "An error occurred while executing this command!"); }
    public static String CommandJoinsignUsageText() { return get("command.joinsign.usage", "Usage: /pg joinsign <lobbynumber>"); }
    public static String CommandUnknownText() { return get("command.unknown", "Unknown command! Use /pg help for help."); }
    public static String DiedText() { return get("died", "died"); }
    public static String HeadLookBlock1Text() { return get("head.look_block_1", "Look at a block to set the 1st place head."); }
    public static String HeadLookBlock2Text() { return get("head.look_block_2", "Look at a block to set the 2nd place head."); }
    public static String HeadLookBlock3Text() { return get("head.look_block_3", "Look at a block to set the 3rd place head."); }
    public static String HeadSet1Text() { return get("head.set_1", "1st place head set."); }
    public static String HeadSet2Text() { return get("head.set_2", "2nd place head set."); }
    public static String HeadSet3Text() { return get("head.set_3", "3rd place head set."); }
    public static String HelpAddarenaText() { return get("help.addarena", "/pg addarena [lobbynumber] [arenaname] - Add an arena"); }
    public static String HelpAdddeathmatchText() { return get("help.adddeathmatch", "/pg adddeathmatch [lobbynumber] [arenaname] - Add a deathmatch spawn"); }
    public static String HelpAddlobbyText() { return get("help.addlobby", "/pg addlobby [lobbynumber] - Add a lobby"); }
    public static String HelpAddspawnText() { return get("help.addspawn", "/pg addspawn [lobbynumber] [arenaname] - Add a spawn"); }
    public static String HelpBroadcastText() { return get("help.broadcast", "/pg broadcast <message> - Send announcement to all players"); }
    public static String HelpBuildText() { return get("help.build", "/pg build - Activate build mode"); }
    public static String HelpBuildUsageText() { return get("help.build_usage", "/pg build - Enable/disable build mode (requires pg.build)"); }
    public static String HelpConfigText() { return get("help.config", "/pg config - View current configuration"); }
    public static String HelpDatabaseText() { return get("help.database", "/pg database - Toggle database mode (MySQL/SQLite)"); }
    public static String HelpDatabaseUsageText() { return get("help.database_usage", "/pg database - Toggle database mode MySQL/SQLite (requires pg.database)"); }
    public static String HelpDebugText() { return get("help.debug", "/pg debug - Toggle debug logging mode"); }
    public static String HelpDellobbyText() { return get("help.dellobby", "/pg dellobby [lobbynumber] - Remove a lobby"); }
    public static String HelpForceText() { return get("help.force", "/pg force [arenaname] - Force an arena"); }
    public static String HelpForceUsageText() { return get("help.force_usage", "/pg force <arena_name> - Force a specific arena (requires pg.force)"); }
    public static String HelpGameserverText() { return get("help.gameserver", "/pg gameserver - Toggle gameserver mode (online/offline)"); }
    public static String HelpGameserverUsageText() { return get("help.gameserver_usage", "/pg gameserver - Toggle gameserver mode (requires pg.gameserver)"); }
    public static String HelpHeadText() { return get("help.head", "/pg headp1(2;3) - Add Player Head to Stats-Wall"); }
    public static String HelpHeadp1UsageText() { return get("help.headp1_usage", "/pg headp1 (Look at 1st place head)"); }
    public static String HelpHeadp2UsageText() { return get("help.headp2_usage", "/pg headp2 (Look at 2nd place head)"); }
    public static String HelpHeadp3UsageText() { return get("help.headp3_usage", "/pg headp3 (Look at 3rd place head)"); }
    public static String HelpHelpUsageText() { return get("help.help_usage", "/pg help - Show this help message"); }
    public static String HelpJoinText() { return get("help.join", "/pg join # - Join a game"); }
    public static String HelpJoinUsageText() { return get("help.join_usage", "/pg join <lobby_number> - Join a specific lobby"); }
    public static String HelpJoinsignText() { return get("help.joinsign", "/pg joinsign [lobbynumber] - Add Join-Sign"); }
    public static String HelpKickText() { return get("help.kick", "/pg kick <player> - Remove player from lobby"); }
    public static String HelpLeaveText() { return get("help.leave", "/pg leave - Leave the game"); }
    public static String HelpLeaveUsageText() { return get("help.leave_usage", "/pg leave - Leave the current game"); }
    public static String HelpListText() { return get("help.list", "/pg list - List of all lobbies"); }
    public static String HelpListUsageText() { return get("help.list_usage", "/pg list"); }
    public static String HelpPauseText() { return get("help.pause", "/pg pause - Pause timer/countdown"); }
    public static String HelpPauseUsageText() { return get("help.pause_usage", "/pg pause - Pause/resume the game (requires pg.pause)"); }
    public static String HelpReloadText() { return get("help.reload", "/pg reload - Reload all configs"); }
    public static String HelpReloadUsageText() { return get("help.reload_usage", "/pg reload - Reload all configurations and stop all games (requires pg.setup)"); }
    public static String HelpDelarenaText() { return get("help.delarena", "/pg delarena [lobbynumber] [arenaname] - Remove an arena"); }
    public static String HelpDelspawnText() { return get("help.delspawn", "/pg delspawn [lobbynumber] [arenaname] - Remove last spawn"); }
    public static String HelpDeldeathmatchText() { return get("help.deldeathmatch", "/pg deldeathmatch [lobbynumber] [arenaname] - Remove last deathmatch spawn"); }
    public static String HelpSetupText() { return get("help.setup", "/pg setup - Set up plugin"); }
    public static String HelpSetupUsageText() { return get("help.setup_usage", "/pg setup - Start setup mode (requires pg.setup)"); }
    public static String HelpSignText() { return get("help.sign", "/pg signp1(2;3) - Add Player Sign to Stats-Wall"); }
    public static String HelpSignp1UsageText() { return get("help.signp1_usage", "/pg signp1 (Look at 1st place sign)"); }
    public static String HelpSignp2UsageText() { return get("help.signp2_usage", "/pg signp2 (Look at 2nd place sign)"); }
    public static String HelpSignp3UsageText() { return get("help.signp3_usage", "/pg signp3 (Look at 3rd place sign)"); }
    public static String HelpStartText() { return get("help.start", "/pg start - Set lobby countdown to 10"); }
    public static String HelpStartUsageText() { return get("help.start_usage", "/pg start - Set lobby countdown to 10 seconds (requires pg.start)"); }
    public static String HelpStatsText() { return get("help.stats", "/pg stats [player] - Show player stats"); }
    public static String HelpStatsUsageText() { return get("help.stats_usage", "/pg stats [player] - Show player statistics"); }
    public static String HelpStatusText() { return get("help.status", "/pg status - Show server status and active lobbies"); }
    public static String HelpTopText() { return get("help.top", "/pg top [type] - Show leaderboard (kills/deaths/wins/kd)"); }
    public static String HelpVersionText() { return get("help.version", "/pg version - Show your and latest version of plugin"); }
    public static String HelpVersionUsageText() { return get("help.version_usage", "/pg version - Check plugin version and updates (requires pg.version)"); }
    public static String HeadLookBlockText(int place) {
        return get("head.look_block_" + place, "Look at a block to set the " + place + (place == 1 ? "st" : place == 2 ? "nd" : "rd") + " place head.");
    }

    public static String HeadSetText(int place) {
        return get("head.set_" + place, place + (place == 1 ? "st" : place == 2 ? "nd" : "rd") + " place head set.");
    }

    public static String SignLookSignText(int place) {
        return get("sign.look_sign_" + place, "Look at a sign to set the " + place + (place == 1 ? "st" : place == 2 ? "nd" : "rd") + " place sign.");
    }

    public static String SignSetText(int place) {
        return get("sign.set_" + place, place + (place == 1 ? "st" : place == 2 ? "nd" : "rd") + " place sign set.");
    }

    public static String HelpHeadpUsageText(int place) {
        return get("help.headp" + place + "_usage", "/pg headp" + place + " (Look at the " + place + "st place head)");
    }

    public static String HelpSignpUsageText(int place) {
        return get("help.signp" + place + "_usage", "/pg signp" + place + " (Look at the " + place + "st place sign)");
    }
    public static String KilledByText() { return get("killed.by", "was killed by"); }
    public static String ListNoLobbiesText() { return get("list.no_lobbies", "No lobbies available."); }
    public static String SetupDisabledText() { return get("setup.disabled", "Setup mode disabled."); }
    public static String SetupEnabledText() { return get("setup.enabled", "Setup mode enabled."); }
    public static String SetupNoBlockInRangeText() { return get("setup.no_block_in_range", "No block in range to set as join sign."); }
    public static String SetupNoDeathmatchSpawnsText() { return get("setup.no_deathmatch_spawns", "No deathmatch spawns are configured for that arena."); }
    public static String SetupNoSpawnsText() { return get("setup.no_spawns", "No spawns are configured for that arena."); }
    public static String SignLookSign1Text() { return get("sign.look_sign_1", "Look at a sign to set the 1st place sign."); }
    public static String SignLookSign2Text() { return get("sign.look_sign_2", "Look at a sign to set the 2nd place sign."); }
    public static String SignLookSign3Text() { return get("sign.look_sign_3", "Look at a sign to set the 3rd place sign."); }
    public static String SignSet1Text() { return get("sign.set_1", "1st place sign set."); }
    public static String SignSet2Text() { return get("sign.set_2", "2nd place sign set."); }
    public static String SignSet3Text() { return get("sign.set_3", "3rd place sign set."); }

}

