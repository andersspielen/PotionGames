# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

#### Gameplay
- Team-aware win condition: the round ends when a single team remains; every surviving member earns a win and reward
- Late-join spectating: with `pg.joinStarted`, players who join a running round become spectators instead of being dropped in as fighters
- `pg.broadcastStarting`: broadcasts a "lobby is starting" message to the whole server when a countdown begins
- `pg.compassOnSpawn`: hands the player-finder compass to everyone at round start
- `pg.changeGamerules`: freezes daylight/weather in the arena world during rounds and restores the previous values afterwards
- `pg.allowOutsideChat`: lets players outside a lobby watch lobby chat (default off, previous behavior)
- Console support for `/pg reload|status|top|broadcast|version|database|gameserver|config|help|kick`
- `permissions` section in plugin.yml with sane defaults (`pg.join`/`pg.leave`/`pg.stats` for everyone, admin nodes op-only)
- Unit tests (JUnit 5): arena spawn ID math and per-lobby config resolution/normalization

#### Configuration
- Consolidated config structure (config.yml, chests.yml, kits.yml, messages.yml, shop.yml)

### Fixed

- Game state is now propagated from the `Lobby` model to listeners; previously deaths/kills/rewards were never recorded and in-game block rules never applied
- Shop GUI was unreachable (title mismatch + wrong state gate); purchases now count real coin items and can no longer loop forever
- Random team assignment froze the server when all teams were full; switching teams validates capacity first so nobody ends up teamless
- Arena spawn add/remove kept config keys and memory in sync (1-based contiguous IDs) with rollback on save failure
- Joining via signs/GUIs mid-game or into full lobbies is now rejected (or turned into spectator late join)
- Rank wall updates no longer die on bad blocks/DB errors; DB queries run async and only block/sign writes happen on the region thread
- PreparedStatements are closed with their ResultSets (cursor leak on MySQL)
- Update-check failures are reported instead of silently doing nothing (or throwing NPE)
- TNT bonus damage applies once inside active games (was an extra flat health drain anywhere on the server)
- Lobby maps are protected outside running games; placed/broken blocks and liquids are actually restored at reset
- `/pg reload` fully restores setup players (level/exp/gamemode/location/health) and restarts tick loops + rank wall
- `/pg database` reconnects immediately instead of requiring a reload
- `/pg stats <player>` looks up the target correctly (was parsing "stats")
- `/pg pause` actually pauses the tick loop again
- Status screen shows real game states and live database connectivity
- `/pg top` and the stats signs/emerald readout no longer block the main thread while querying the database
- Player records are created asynchronously on join instead of during the join event

### Changed

- Java 21 → 25 across all build configs and CI workflows
- `maven-resources-plugin` pinned to 3.5.0 for Eclipse M2E compat
- release.yml: reads release body from `CHANGELOG.md` instead of auto-generated notes
- Inventory GUI: arena selector, kit selector, team selector, stats, leave items unified to DARK_AQUA
- Inventory titles: ArenaSelectorTitle, KitSelector, SelectorTeamTitle no longer use prefix line
- `InventoryEventListener` split into `GameInventoryListener`, `GameItemListener` and `SetupInteractionListener`
- Six clone commands (headp1-3/signp1-3) consolidated into one parameterized `StatsWallCommand`
- Setup-mode chat input no longer swallows normal chat while no input mode is active
- Votes have a single source of truth (`ArenaStateManager` via `Lobby#recordVote`); the duplicate vote record on participants was removed
- Removed redundant spectator fallbacks in `/pg join|force|start|pause|build` (`getPlayerLobby` already covers spectators)

### Removed

- Hardcoded chest loot (now config-driven)
- Hardcoded shop data (now in config)
- Monolithic event/command classes
- `BlockTracker.java`, `PotionChest.java`, `LootTable.java`
- `LobbySettings.java` (entire class unused)
- `ConfigKeys.java` (36/37 dead enum values inlined)
- Dead config knobs that had no code reader: `logging.*`, `performance.*`, `security.*`, `activePotions`
- `BlockStateManager` / `PlayerStateManager` (duplicated state owned by `Lobby`/`Game`; tracking now lives where it is used)
- ~45 dead message methods and their orphaned translation keys
- 5 no-op listener classes (consume/food/respawn/teleport/explosion)
- Never-functional move-freeze flag (cost a lobby lookup on every move event)
- Write-only voting/team maps in `Lobby` and mirror flags in `LobbyStateManager`
- BAD_OMEN entries 21-27 from `shop.yml` and `BootstrapInitializer.java` shop seeding
