# Code Fighters

A security awareness arcade 2D fighting game built with **libGDX** and **Android Studio** for a school project. Battle against real-world malware as a stylized software developer, learn about cybersecurity threats, and unlock enemy data in the gallery.

---

## Game Concept

In a digital world where malware threatens systems worldwide, software developers step up as the last line of defense. Play as the **Team Lead** and fight against infamous malware like **WannaCry** in fast-paced 2D combat.

Each character and enemy is designed around real-world software development and cybersecurity concepts, making this both a game and an educational tool.

---

## Features

- **2D Arcade Combat**: Light attack combos, special moves, blocking, and hit-stop effects
- **Character Selection**: Choose your fighter with unique stats and abilities
- **Enemy AI**: WannaCry tracks, approaches, and attacks independently
- **Gallery System**: Unlock enemy profiles and combo lists by defeating malware
- **Educational Content**: Learn about real malware and the developers who fight them
- **Touch & Keyboard Controls**: Play on Android with on-screen buttons or use keyboard (A/D, J, K, L)

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java | Core game logic |
| libGDX | Game framework (cross-platform) |
| Android Studio | IDE and build system |
| Gradle | Build automation |
| ShapeRenderer | Procedural graphics (placeholder sprites) |

---

## Controls

### Keyboard (Desktop / Emulator)
| Key | Action |
|-----|--------|
| A / Left Arrow | Move left |
| D / Right Arrow | Move right |
| J | Light attack (3-hit combo) |
| K | Special move (Projectile) |
| L | Block |

### Touch (Android)
| Button | Action |
|--------|--------|
| LEFT | Move left |
| RIGHT | Move right |
| ATK | Light attack |
| SP | Special move |
| BLK | Block |

---

## Game Screens

1. **Main Menu** - Start fight or view gallery
2. **Character Select** - Choose your fighter
3. **Fight Screen** - Combat arena with health bars, combo counter, and touch controls
4. **Win Screen** - Victory display with options to continue
5. **Lose Screen** - Defeat display with retry option
6. **Gallery** - View unlocked enemy profiles and combo lists

---

## Current Build

### Playable Characters
- **Team Lead (You)** - Balanced fighter with combo attacks and projectile special

### Enemies
- **WannaCry** - Aggressive ransomware with glitch effects and melee attacks

### Unlockables
Defeat WannaCry to unlock its profile and combo data in the Gallery.

---

## Project Structure

```
CodeFighters/
├── android/              # Android-specific module
│   ├── AndroidManifest.xml
│   └── AndroidLauncher.java
├── core/                 # Core game module (shared)
│   └── src/com/codefighters/
│       ├── CodeFightersGame.java      # Main game class
│       ├── MenuScreen.java            # Main menu
│       ├── CharacterSelectScreen.java # Fighter selection
│       ├── FightScreen.java           # Combat logic
│       ├── WinScreen.java             # Victory screen
│       ├── LoseScreen.java            # Defeat screen
│       ├── GalleryScreen.java         # Unlockable content
│       ├── CharacterData.java         # Fighter stats
│       ├── EnemyData.java             # Enemy stats
│       └── GameProgress.java          # Progress tracking
├── build.gradle          # Project build config
└── settings.gradle       # Module settings
```

---

## How to Build and Run

### Requirements
- Android Studio (latest version)
- JDK 17 or higher
- Android SDK with API level 21+

### Steps
1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/CodeFighters.git
   ```
2. Open the project in Android Studio
3. Sync Gradle files (Android Studio will prompt you)
4. Connect an Android device or start an emulator
5. Click the **Run** button (green triangle)

---

## Development Roadmap

- [x] Core game loop (menu, fight, win/lose)
- [x] Basic combat system (move, attack, block, special)
- [x] Enemy AI (tracking, attacking)
- [x] Combo system (3-hit light attack chain)
- [x] Gallery unlock system
- [x] Touch controls for Android
- [ ] Sound effects and music
- [ ] Additional characters (Professor, teammates)
- [ ] Additional enemies (more malware)
- [ ] Save system with Preferences
- [ ] Custom sprites (replace ShapeRenderer placeholders)
- [ ] Polish: screen shake, hit flash, particle effects

---

## Credits

- **Game Design & Programming**: [Your Name]
- **Framework**: [libGDX](https://libgdx.com/)
- **Development Environment**: [Android Studio](https://developer.android.com/studio)
- **School Project**: Computer Science Department

---

## License

This project was created for educational purposes as part of a school assignment.
