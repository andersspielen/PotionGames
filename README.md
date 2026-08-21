# PotionGamesX

> A Minecraft minigames plugin for Paper servers!

[![CI Build](https://github.com/lennartabeln/PotionGamesX/actions/workflows/ci.yml/badge.svg)](https://github.com/lennartabeln/PotionGamesX/actions/workflows/ci.yml)
[![CodeQL](https://github.com/lennartabeln/PotionGamesX/actions/workflows/codeql.yml/badge.svg)](https://github.com/lennartabeln/PotionGamesX/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/lennartabeln/PotionGamesX)](https://github.com/lennartabeln/PotionGamesX/releases/latest)

PotionGamesX is a minigames plugin that works like SurvivalGames but with potions and effects!

## Features

* Custom loot tables with probability-weighted chests
* Custom potion effects from looting chests
* Custom chest types (Normal, Target, Netherite, Composter)
* Airdrops (activate with redstone torch)
* Loot coins and glass bottles to use in the shop
* Potion shop with kit-specific sale prices
* Multiple lobbies with arena voting each round
* Teams with custom team sizes — last team standing wins
* Spectators: eliminated players (and late joiners via `joinStarted`) watch the round
* Deathmatch mode for final battles
* Player statistics (SQLite or MySQL)
* Top 3 stats wall display
* Join signs with live player count updates
* BungeeCord / proxy support
* GUI or sign to join lobbies
* Rewards system (Vault)
* Class-based architecture with manager delegation

## Requirements

- **Java 25+**
- **Paper 26.2**
- **Maven 3.8+** (for building)
- **Multiverse-Core** (soft dependency)
- **Vault** (optional, for economy rewards)

## Installation

1. Download the latest release from the [releases page](https://github.com/lennartabeln/PotionGamesX/releases)
2. Place the `.jar` in your server's `plugins/` folder
3. Install Multiverse-Core if using multi-world setups
4. Restart your server
5. Configure via `plugins/PotionGamesX/config.yml`
6. Run `/pg setup` to set up the plugin

## Setup

### Quick Start
1. Create a lobby: `/pg setup`
2. Add arenas and spawns through the setup wizard
3. Add chests to your arenas
4. Configure settings in `config.yml`

### Lobby Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg join <id>` | Join a lobby | `pg.join` |
| `/pg leave` | Leave current lobby | — |
| `/pg list` | Open lobby GUI | `pg.join` |
| `/pg start` | Start countdown | `pg.start` |
| `/pg build` | Toggle build mode | `pg.build` |
| `/pg pause` | Pause/resume the game timer | `pg.pause` |
| `/pg force <arena>` | Force an arena | `pg.force` |
| `/pg stats [player]` | Show player stats | `pg.stats` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg setup` | Interactive setup wizard | `pg.setup` |
| `/pg config` | View current configuration | `pg.config` |
| `/pg status` | Show server status and lobbies | `pg.status` |
| `/pg debug` | Toggle debug logging | `pg.debug` |
| `/pg broadcast` | Send server announcement | `pg.broadcast` |
| `/pg kick <player>` | Remove player from lobby | `pg.kick` |
| `/pg top` | Show leaderboards | `pg.top` |
| `/pg gameserver` | Toggle game/hub mode | `pg.gameserver` |
| `/pg database` | Switch MySQL/SQLite | `pg.database` |
| `/pg reload` | Reload config files | `pg.setup` |
| `/pg version` | Show plugin version | `pg.update` |

Most admin commands can also be run from the server console (`reload`, `status`, `top`, `broadcast`, `version`, `database`, `gameserver`, `config`, `help`, `kick`).

### Advanced Setup Commands

Alternative to the `/pg setup` wizard — individual commands for manual setup:

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg addlobby <id>` | Create lobby | `pg.setup` |
| `/pg dellobby <id>` | Remove lobby | `pg.setup` |
| `/pg addarena <lobby> <name>` | Add arena | `pg.setup` |
| `/pg delarena <lobby> <name>` | Remove arena | `pg.setup` |
| `/pg addspawn <lobby> <arena>` | Add spawn | `pg.setup` |
| `/pg delspawn <lobby> <arena>` | Remove last spawn | `pg.setup` |
| `/pg adddeathmatch <lobby> <arena>` | Add deathmatch spawn | `pg.setup` |
| `/pg deldeathmatch <lobby> <arena>` | Remove deathmatch spawn | `pg.setup` |
| `/pg joinsign <lobby>` | Set join sign | `pg.setup` |
| `/pg headp1\|p2\|p3` | Set stats wall head | `pg.setup` |
| `/pg signp1\|p2\|p3` | Set stats wall sign | `pg.setup` |

### Chest Types

* **End Portal Frame**: Normal chest with probability-weighted loot (food, armour, weapons)
* **Target**: Special chest with Flame Bow and Arrows
* **Netherite Block**: Special chest with Netherite Ingots (Smithing Table upgrades diamond → netherite)
* **Composter**: Potion shop (build a beacon under it to show on the map)

### Signs

* **Join Sign**: Place a sign, look at it, use `/pg joinsign <lobby>`
* **Stats Sign**: Place a sign, write `PotionGamesX` on line 2 and `Stats` on line 3

### Stats Wall

1. Place 3 player heads on blocks next to each other
2. Place 3 signs at the front
3. Look at head #1 and run `/pg headp1` (repeat for p2, p3)
4. Look at sign #1 and run `/pg signp1` (repeat for p2, p3)

## Configuration

Config files in `plugins/PotionGamesX/`:

| File | Purpose |
|------|---------|
| **config.yml** | Global settings |
| **lobbies.yml** | Lobbies, arenas, spawn locations |
| **chests.yml** | Chest loot item definitions |
| **shop.yml** | Shop item definitions |
| **kits.yml** | Kit definitions |
| **messages.yml** | Localized text messages |

Settings use a lobby-first model:
- `pg.defaults.*` stores global defaults for every lobby
- `pg.lobbies.<id>.settings.*` stores per-lobby overrides
- Falls back to defaults if per-lobby setting is missing

Notable global toggles (`config.yml`):
| Setting | Effect |
|---------|--------|
| `joinStarted` | Late joiners spectate a running round instead of being rejected |
| `broadcastStarting` | Broadcast a server-wide message when a lobby countdown starts |
| `compassOnSpawn` | Give everyone the player-finder compass at round start |
| `changeGamerules` | Freeze daylight/weather in the arena world during rounds |
| `allowOutsideChat` | Players outside lobbies can see lobby chat |

## Release History

### v1.0 — PotionGamesX (Rework)
Complete rework of the original PotionGames, now rebuilt as PotionGamesX.
* Full class-based OOP refactor with manager delegation
* Manager classes and separated event listeners / command classes
* Configuration-driven design
* Consolidated config structure (config.yml, chests.yml, kits.yml, messages.yml, shop.yml)
* GitHub CI/CD pipeline (build, test, CodeQL, automated releases)

### Original PotionGames
Previous versions of the original PotionGames plugin before the rework:

* **v7.0** — Support for Minecraft 1.13+
* **v6.0** — Airdrops
* **v5.2** — Sounds
* **v5.1** — Rewards for kills and winning (Vault)
* **v5.0** — Deathmatch
* **v4.9.5** — Team-Mode
* **v4.9** — Scoreboard
* **v4.8** — One arena lobbies
* **v4.7** — Primed TNT to loot table
* **v4.6** — `/pg reload` command
* **v4.5** — `/pg version` command
* **v4.4** — Join lobby via GUI
* **v4.3** — Support for 1.17
* **v4.2** — Bungee Hub-Server setting
* **v4.1** — Option to change all chest blocks
* **v4.0** — Option to add own blocks with their own loot table
* **v3.1** — Own settings per lobby
* **v3.0** — Inventory setup
* **v2.3** — Round-Time
* **v2.2** — Own settings per lobby
* **v2.1** — Join signs with live updates
* **v2.0** — Multi-Arena-System
* **v1.0** — Option to change chest items and effects
* **v0.9** — Option to change database type (MySQL/SQLite)
* **v0.8.5** — Option to deactivate MySQL/stats
* **v0.8** — Option to change kits
* **v0.7** — Option to change shop items
* **v0.6** — Option to turn Teams, Kits and Shop on/off
* **v0.5** — Teams
* **v0.4** — Kits
* **v0.3** — Shop
* **v0.2** — ArenaVote-System
* **v0.1** — SurvivalGames-System with chests giving potion effects

## License

Licensed under the [MIT License](LICENSE).

## Issues / Ideas

[Report bugs / request features here!](https://github.com/lennartabeln/PotionGamesX/issues)
