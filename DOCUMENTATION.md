# PotionGamesX - Official Documentation

> A modern Minecraft minigames plugin for Paper servers with potions, loot, and team-based gameplay.

## 📋 Documentation Index

- **[README.md](README.md)** - Main plugin features and installation
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and release notes

## 🚀 Quick Start

### Build
```bash
mvn -DskipTests clean package
```

### Deploy
1. Copy `target/PotionGamesX-*.jar` to server `plugins/`
2. Restart server
3. Configure in `plugins/PotionGamesX/config.yml`
4. Use `/pg help` for commands

## 📦 What's Included

### Core Features
- ✅ Multi-lobby minigames system (up to 27 lobbies)
- ✅ Customizable loot chests with probability-weighted pools
- ✅ Team-based gameplay with custom team sizes
- ✅ Potion shop system with 27+ potions
- ✅ Player kits with 26+ configurations
- ✅ Deathmatch mode for final battles
- ✅ Player statistics (SQLite/MySQL)
- ✅ Top 3 player display wall

### Modern Architecture
- ✅ Class-based OOP (no monolithic patterns)
- ✅ Manager-based state delegation
- ✅ Individual command classes (not monolithic)
- ✅ Separated event listeners
- ✅ Configuration-driven design
- ✅ 0 code warnings, production-ready

### Admin Commands
- `/pg config` - View configuration
- `/pg status` - Show server status and lobbies
- `/pg gameserver` - Toggle offline/online mode
- `/pg database` - Switch MySQL/SQLite
- `/pg debug` - Toggle debug logging
- `/pg broadcast` - Send announcements
- `/pg kick` - Remove players from lobbies
- `/pg top` - Show leaderboards

## 🛠️ Configuration

All config files in `plugins/PotionGamesX/`:

| File | Purpose |
|------|---------|
| **config.yml** | Global settings |
| **lobbies.yml** | Lobbies, arenas, spawn locations |
| **chests.yml** | Chest loot item definitions |
| **shop.yml** | Shop item definitions |
| **kits.yml** | Kit definitions |
| **messages.yml** | Localized text messages |

## 📊 Build Status

| Metric | Status |
|--------|--------|
| Java | 25+ required |
| Paper | 26.1.x required |
| Code Warnings | ✅ 0 |
| Code Errors | ✅ 0 |
| JAR Size | 0.22 MB |
| Build Time | ~6s |

## 🔗 Requirements

- **Java 25+**
- **Maven 3.8+**
- **Paper 26.1.x**
- **VaultAPI 1.7.1** (optional, for economy)
- **Multiverse-Core** (soft dependency)

## 🏗️ Project Structure

```
src/main/java/com/tw0far/potiongames/
├── models/              # Domain models (Game, Lobby, etc)
├── managers/            # State managers (Config, Database, etc)
├── commands/            # Individual command classes
├── listeners/           # Separated event handlers
├── handlers/            # Complex workflows
├── bootstrap/           # Startup initialization
├── util/                # Utilities and helpers
└── PotionGamesX.java    # Main plugin class
```

## 📝 Key Files

| File | Purpose |
|------|---------|
| pom.xml | Maven build configuration |
| plugin.yml | Minecraft plugin metadata |

## 🚨 Support

For issues, features, or questions:
1. Check console logs for error messages
2. Verify config files are valid YAML
3. Open an issue on the [GitHub repository](https://github.com/lennartabeln/PotionGamesX/issues)

## 📜 License

Licensed under the [MIT License](LICENSE).
