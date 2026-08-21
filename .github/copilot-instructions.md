# Copilot Instructions for PotionGamesX

## Overview

PotionGamesX is a Minecraft **Paper 26.2+** (Java 25+) minigames plugin similar to SurvivalGames, but with potions, loot chests, a potion shop, teams and a deathmatch phase. It supports multiple lobbies with arena voting and persists player statistics via SQLite or MySQL.

## Build & Test

```bash
mvn clean package    # JAR lands in target/PotionGamesX-<version>.jar
mvn test             # JUnit 5 unit tests (src/test/java) - no Bukkit server needed
```

- Java 25+, Maven 3.8+
- Paper API is resolved from `https://repo.papermc.io` by Maven
- Optional at runtime: VaultAPI 1.7.1 (economy rewards), Multiverse-Core (multi-world)

## Architecture

### models/ - domain classes that own their state

- **Game.java** - all lobbies; membership lookups (`getPlayerLobby`, `getSpectatorLobby`,
  `isActivePlayer`, `isSpectatorPlayer`), auto-join, lobby add/remove.
  Note: `getPlayerLobby(Player)` returns the lobby ID for active players *and* spectators.
- **Lobby.java** - per-lobby state machine (`WAITING → PREPARING → INGAME → DEATHMATCH/ENDING → RESET`),
  tick loop (`runGameTick`), join gating (`canJoin`, `canSpectate`, `joinAsSpectator`),
  voting (`recordVote` delegates to ArenaStateManager), team distribution,
  block/liquid tracking for round restoration (`addPlacedBlock`, `addBrokenBlock`,
  `addLiquidBlock`), pause flag, gamerule freeze/restore.
- **Arena.java** - spawns + deathmatch spawns. IDs are 1-based and contiguous;
  `addSpawn(id, loc)` replaces in place when `id <= size`, appends when `id == size + 1`,
  rejects gaps. Both memory list and YAML keys are rolled back on save failure.
- **Participant.java** - per-player wrapper inside a Lobby (kit, saved inventory/state).
- **LobbyConfig.java** - resolves per-lobby settings from `lobbies.yml` with global defaults;
  normalizes `roundTime` (≤300 treated as minutes → seconds).
- **Messages.java** - typed message factory: one static method per message
  (`Component` variants like `Messages.LobbyFull()` plus `*Text()` String variants).
  Seeds its keys into `messages.yml` on startup; falls back to `en_US`.
- **Settings.java** - static file handles (`lobbies`, `chests`, `kitdata`, `messages`, `shopdata`)
  and global defaults loaded from config.yml.
- Kit / GameStates / PlayerState / ParticipantType - small support types.

### managers/ - interfaces + implementations

| Manager | Responsibility |
|---------|----------------|
| `ConfigurationManager` | Cached snapshot of config.yml; `reload()` re-reads (called by `/pg reload`) |
| `DatabaseManager` | SQLite/MySQL stats persistence; all APIs take UUID **strings** |
| `LobbyStateManager` | Only two concerns: mirrored game state (`getGameState`/`setGameState`, written by `Lobby#setState`) and build-mode flag |
| `ArenaStateManager` | **Single source of truth** for votes (`getLobbyVoteCount`, `recordPlayerVoteInLobby`, `resetLobbyVotes`) and GUI team counts |
| `ItemStateManager` | Loot pools, shop item parallel lists, kits |
| `SetupStateManager` | Setup-mode players, saved inventories, transient input flags |

There is intentionally no BlockStateManager or PlayerStateManager anymore -
block tracking lives on `Lobby`, membership lives on `Game`.

### listeners/

- `GameInventoryListener` - clicks in the vote/team/kit/shop GUIs
- `GameItemListener` - gameplay right-clicks: loot/custom/shop chests, stew, milk,
  airdrop torch, player finder, selector openers, leave/stats items, join/stats signs
- `SetupInteractionListener` - setup hotbar tools + lobby/arena selection GUIs
- `PlayerEventListener`, `ChatEventListener`, `DeathEventListener`, `CombatEventListener`,
  `DamageEventListener`, `BlockEventListener`, `BucketEventListener`, `BlockFadeEventListener`,
  `BlockFlowEventListener`, `LeavesDecayEventListener`, `WeatherEventListener`,
  `CreatureSpawnEventListener`, `ItemDropEventListener`, `SignChangeEventListener`,
  `SpectatorEventListener`

All are registered in `PotionGamesX.onEnable()`.

### commands/

Every subcommand of `/pg` is one class implementing `ICommand`
(`execute(CommandSender sender, String[] args)`); guard with
`instanceof Player` where a real player is required. Registered in
`CommandDispatcher.registerCommands()`. Console may run the subset listed in
`CommandDispatcher.CONSOLE_ALLOWED`. The six stats-wall commands
(headp1-3/signp1-3) are instances of the parameterized `StatsWallCommand`.

### handlers/ & bootstrap/

- `JoinLobbyHandler` - join gating (reject full/running lobbies; spectator late join via `pg.joinStarted`)
- `SetupHandler` - setup wizard state give/restore
- `ReloadHandler` - full reload: restore setup players, cancel tasks, reconnect DB, restart ticks/rank wall
- `EnableBootstrapInitializer` - per-boot seeding (build flags off, team maps, kit players)
- `BootstrapInitializer`, `ChestLootInitializer`, `RankWallUpdater` (async DB query,
  world writes on the global region scheduler)

## Key conventions

1. **Delegation over raw maps** - use `Game`/`Lobby` accessors; never reach into manager maps.
2. **State flow**: only `Lobby.setState` changes game state; it mirrors to
   `LobbyStateManager.setGameState` so ID-based listener lookups stay cheap.
3. **Votes**: write through `Lobby.recordVote(player, arenaName)` only -
   it owns switch handling against `ArenaStateManager`. Winner selection reads counts back.
4. **Threading (Folia-safe schedulers)**:
   - DB/network work → `Bukkit.getAsyncScheduler().runNow(...)`
   - Back to a player thread → `player.getScheduler().execute(plugin, run, retired, delayTicks)`
   - World/sign/skull writes → `getServer().getGlobalRegionScheduler()`
5. **Config**: read through `IConfigurationManager`; add new knobs to both the interface
   and `loadAllConfig()`, and document them in README.md.
6. **Messages**: add a seed line + a typed method in `Messages.java`; never hardcode
   player-facing English strings.
7. **Spawns**: always use the 1-based contiguous ID scheme on `Arena`;
   see `ArenaSpawnTest` for the exact contract.

## Testing

Unit tests live in `src/test/java` (JUnit 5). Pattern for headless tests:
swap the static `Settings.lobbies` handle for an in-memory `YamlConfiguration`
(see `ArenaSpawnTest`). Keep pure logic (ID math, config resolution) out of
Bukkit runtime classes so it stays testable.

Manual smoke checklist before releases (needs a Paper server):
join → vote → start → chest/shop/stew → deathmatch → reset → rejoin,
plus `/pg reload` mid-setup.
