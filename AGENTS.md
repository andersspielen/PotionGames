## Essential Commands
- `mvn clean install` to build the project (required before any tests or execution)
- `mvn test` to run tests (Maven Surefire, JUnit 5)
- `java -jar target/PotionGamesX-*.jar` to launch the game (found in target directory)

## Build/Execution Workflow
- Maven-based project: always run `mvn clean` before `mvn install` to purge build artifacts
- Tests in `src/test` are JUnit 5 unit tests (pure JVM, no Bukkit server needed)
- Game executable path is `target/PotionGamesX-*.jar` (version matches pom.xml)

## Important Rules
- NEVER commit changes unless explicitly asked. Stage with `git add` and wait for user confirmation before committing.

## Repo-Specific Quirks
- Plugin configuration in `src/main/resources/plugin.yml` requires `mvn package` to generate JAR
- CI/CD uses `mvn` for all builds
- No official documentation for deployment; infrastructure flow inferred from `target/classes/config.yml`

## Critical Paths
- Development: `mvn compile` -> `mvn test` -> `java -jar target/PotionGamesX-*.jar`
- Testing: Skip `mvn package` if only running unit tests
- Failure Handling: `mvn clean` always resolves transient build errors

## Release Process
- Release triggers on push to `main` when version changes in pom.xml
- Release body is extracted from CHANGELOG.md:
  - Pre-release versions (contain `-`, e.g. `1.0.0-dev.2`): extracts `## [Unreleased]` section
  - Full releases (no `-`, e.g. `1.0.0`): extracts `## [version]` section
- **Before cutting a full release**, the CHANGELOG.md must be updated:
  1. Rename `## [Unreleased]` to `## [version] - YYYY-MM-DD`
  2. Add a fresh `## [Unreleased]` section at the top for future development
  3. Set pom.xml version to the release version (remove `-dev.X` suffix)

## Session Summary (2026-06-11)

### Completed: Lobby GUI & Inventory Overhaul

**Arena Selector Fixes:**
- Fixed "Random" option displaying 0 votes — now shows actual vote count via `getLobbyVoteCount`
- Random item uses `Messages.RandomItem()` for proper naming

**Kit Selector Fix:**
- Added `handleKitSelection` listener method — now properly applies kit+items on selection

**Inventory Style Unification:**
- Arena selector, kit selector, team selector, stats, leave items all use `DARK_AQUA`, no prefix
- Inventory titles (ArenaSelectorTitle, KitSelector, SelectorTeamTitle) no longer use prefix line

**Messages.java Refactoring:**
- Eliminated all 107 `Messages.raw("key", "default")` calls project-wide
- Replaced with 86 typed `*Text()` methods in `Messages.java` (`Messages.SomethingText()`)
- Existing parameterized methods (`get(key, default, args...)`) left unchanged
- Methods that had both `raw` and parameterized usage were preserved as the parameterized versions are still needed.

### Completed: Dead Code Cleanup

**Removed 5 unused files:**
- `BlockTracker.java`, `PotionChest.java`, `LootTable.java` — never imported or instantiated
- `LobbySettings.java` — entire class dead (zero imports project-wide)
- `ConfigKeys.java` — 36/37 enum values dead; single live use inlined at call site

**Lobby.java cleanup (~30 dead methods removed):**
- Dead voting/team getters (`getVotingMap`, `getTeamsMap`, `getCurrentVote`, etc.) — migration to ArenaStateManager complete
- Dead flag getters/setters (`isForcearena`, `isVoteallowed`, `isKitallowed`, `isBuildAllowed`, `isSingleArena`, etc.) — state managed via LobbyStateManager
- Dead accessors (`getChests`, `getPlacedBlocks`, `getBreakeedBlocks`, `getArenaCount`, `hasArena`, etc.)
- Dead operations (`pauseGame`, `resumeGame`, `activateTeam`, `getTeamMembers`)

**Model cleanup (Arena, Game, LobbyConfig, Participant):**
- `Arena.java` — removed 13 dead methods (stale voting/chest/loot/settings accessors)
- `Game.java` — removed 8 dead orchestration methods + 10 empty loot/shop fields (data lives in ItemStateManager)
- `LobbyConfig.java` — removed 10 dead getters/setters (setters were also buggy — wrong config path)
- `Participant.java` — removed dead `getLobby()`

**Manager cleanup:**
- `ILobbyStateManager` / `LobbyStateManager` — removed 21 dead method declarations
- `IArenaStateManager` / `ArenaStateManager` — removed global voting (dead) + `clearLobbyVotes`, `getWinningArenaForLobby`, `getLobbyTeamAmount`, `markPlayerVotedInLobby`, `clearLobbyTeams`
- `IConfigurationManager` / `ConfigurationManager` — removed ~65 dead getter/setter methods; kept live ones
- `IBlockStateManager` / `BlockStateManager` — removed 28 dead methods, 5 unused fields
- `IPlayerStateManager` / `PlayerStateManager` — removed 22 dead methods, 3 unused fields
- `IDatabaseManager` / `DatabaseManager` — removed 20 dead Player-based convenience methods
- `IManager` — removed `reload()` (zero callers); removed `reload()` from 8 implementations
- `ISetupHandler` / `SetupHandler` — removed 9 dead parameterless overloads
- `Messages.raw()` — removed (zero callers after Text method migration)

**Settings.java cleanup:**
- Removed 19 dead static fields (all written but never read; duplicate of ConfigurationManager values)