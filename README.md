# Typing Battle Arena — A Java GUI-Based Interactive Typing Combat Game

[![Java Version](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%20%7C%2021%2B-blue.svg)](https://www.oracle.com/java/)
[![GUI Framework](https://img.shields.io/badge/GUI-Java%20Swing%20%26%20Java%202D-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Audio Engine](https://img.shields.io/badge/Audio-Procedural%20Java%20Synthesizer-success.svg)](https://docs.oracle.com/javase/tutorial/sound/)
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20Java%20SE)-brightgreen.svg)](https://docs.oracle.com/en/java/)

**Typing Battle Arena** is an action-packed, interactive fighting game built entirely in standard **Java (Swing & Java 2D Graphics)** that fuses typing speed, accuracy, and reflex challenges with real-time martial and magical combat.

Unlike traditional fighting games where attacks are executed via gamepad buttons or simple keystrokes, every attack, combo streak, and special move in Typing Battle Arena is launched by typing displayed words accurately and swiftly. The game features rich visual effects, animated characters, adaptive AI personalities, local two-player duel mechanics, an educational typing analytics dashboard with S–D rank grading, and a 5-act narrative campaign mode to reclaim the legendary **Keyboard Core**.

---

## 📖 Project Overview

The world has been plunged into silence by rogue warlords who stole the legendary **Keyboard Core** artifact. Battles are won not with buttons, but with speed and precision — every keystroke you land accurately becomes a strike against your opponent.

---

## 🌟 Key Features

- **Action-Packed Real-Time Typing Combat** — Continuous, fluid battle mechanics where faster typing velocity deals quicker damage and prevents incoming enemy counters.
- **Dynamic Letter-by-Letter Visual Feedback**:
  - 🟢 **Green**: Accurately typed letters.
  - 🔴 **Red**: Mistyped letters, triggering an instant error buzz and combo penalty.
  - ⚪ **White**: Upcoming target characters.
- **Dynamic Interpolated Health Bars** — Smooth color transitions from Green ➔ Yellow ➔ Orange ➔ Red as combatant health depletes.
- **Combo Multiplier Mechanics**:
  - `3+ Hits`: **COMBO x1.25**
  - `5+ Hits`: **SUPER COMBO x1.50**
  - `10+ Hits`: **ULTIMATE COMBO x2.00**
- **Power Meter & Cinematic Special Attacks** — Every successful word charges your power meter. At 100%, press <kbd>Enter</kbd> or click the Special button to unleash an explosive character ultimate with screen shake and particle bursts.
- **Particle Engine** — Hit sparks, blade slashes, magic blasts, floating combat text (`"-140"`, `"CRITICAL!"`), and screen shake.
- **Pure Java SE Procedural Sound Synthesizer** — Generates authentic retro arcade sound effects (key clicks, blade swings, impact crunches, magic charges, victory fanfares) and an energetic synth battle soundtrack on the fly via `javax.sound.sampled`, with zero external audio assets.
- **Educational Performance Analytics** — Post-battle diagnostics report Words Per Minute (WPM), Accuracy (%), peak combo, error counts, personalized coaching advice, and an **S / A / B / C / D Rank Grade**.
- **Persistent Local Records** — Match history, high scores, best WPM records, and story unlocks are automatically saved locally.

---

## 🎮 Game Modes

### 1. 📖 Story Mode (The Keyboard Core Campaign)
Journey through 5 progressive acts, each with pre-battle dialogues, stage briefings, boss phase transformations, and victory epilogues:

| Act | Title | Location | Guardian | AI Style |
| :--- | :--- | :--- | :--- | :--- |
| I | Whispering Shadows | Bamboo Perimeter Dojo | Rogue Scout Genji | Balanced |
| II | Cybernetic Overdrive | Neo-Tokyo High-Rise | Security Enforcer Unit-7X | Aggressive |
| III | Arcane Inscriptions | Sunken Eldorian Sanctuary | Sorceress Morwenna | Defensive |
| IV | Molten Bushido | The Iron Citadel Foundry | Lord Raiden Crimson-Blade | Aggressive |
| V | Reclaim the Keyboard Core | The Core Sanctum Chamber | Overlord Malakor the Void | Multi-Phase Boss AI |

### 2. ⚔ Single Player (Versus AI)
Customizable quick sparring matches:
- Select your fighter and opponent.
- Choose your difficulty (**Easy**, **Medium**, **Hard**, **Intense**).
- Choose from 4 unique AI personalities.
- Select your arena backdrop (**Dojo**, **Cyberpunk City**, **Mystic Temple**, **Volcanic Core**, **Keyboard Sanctum**).

### 3. 👥 Two Player (Local Duel)
Head-to-head competitive typing on the same system! Player 1 and Player 2 have separate split-screen typing prompts, independent word streams, combo counters, health bars, and individual special attack triggers.

---

## 🥋 Selectable Characters

Each fighter is brought to life with custom Java 2D vector animations (idle breathing, attack dashes, hurt recoils, special charging, and victory poses):

| Character | Class | Signature Attack | Special Move | Primary Trait |
| :--- | :--- | :--- | :--- | :--- |
| **Hayato the Silent** | Shadow Ninja | Shuriken Flurry | *Shadow Execution* | High Speed (1.25x), Swift Strikes |
| **Kenji Crimson Blade** | Bushido Samurai | Iaido Slash | *Dragon Cleave* | Heavy Attack Power (1.20x) |
| **Aurelia Spellweaver** | Arcane Mage | Arcane Surge | *Cataclysmic Comet* | Balanced Magic & Stats |
| **Unit VX-9000** | Cyber Mecha | Plasma Burst | *Orbital Core Blast* | High Defense Armor (1.20x) |
| **Malakor the Eclipse** | Void Stalker | Void Tendrils | *Abyssal Singularity* | Burst Attack Power (1.25x) |

---

## 🤖 Adaptive AI Personalities

- **Aggressive Bot** — Rapid burst typing, high damage pressure, slightly higher mistake chance.
- **Defensive Bot** — Methodical cadence, heavy counter-strikes, and chance to guard/block damage.
- **Balanced Bot** — Smooth, rhythmic typing pace for standard battles.
- **Boss AI** — Multi-phase boss. Enrages below 50% HP with a 35% typing velocity boost and charges devastating ultimate attacks.

---

## 📊 Combat Scoring & Ranks

Ranks are calculated from Words Per Minute (WPM), Accuracy (%), and peak combo streak:

- 👑 **S Rank** — Legendary Typist: WPM ≥ 65, Accuracy ≥ 95%, Combo ≥ 6
- 🥇 **A Rank** — Master Combatant: WPM ≥ 50, Accuracy ≥ 90%
- 🥈 **B Rank** — Skilled Fighter: WPM ≥ 35, Accuracy ≥ 82%
- 🥉 **C Rank** — Apprentice: WPM ≥ 20, Accuracy ≥ 70%
- 🔰 **D Rank** — Trainee: Below 20 WPM or Accuracy < 70%

**Formulas:**
- **WPM** = `(correctChars / 5.0) / minutes`
- **Accuracy (%)** = `(correctKeystrokes / totalKeystrokes) * 100`

Post-match, players also receive personalized skill coaching (balancing speed vs. accuracy, recommended difficulty) and results are saved to a persistent local leaderboard (`~/.typing_battle_arena/`).

---

## 🛠 Technologies Used

- **Language**: Java (JDK 8+, 11+, 17+, 21+)
- **GUI Framework**: Java Swing (`JFrame`, `JPanel`, `CardLayout`, `JProgressBar`, `JTextField`, `JTable`)
- **Graphics Engine**: Java 2D (`Graphics2D`, `GradientPaint`, `AffineTransform`, `BasicStroke`, `AlphaComposite`)
- **Audio Engine**: Pure Java SE `javax.sound.sampled` PCM waveform synthesis
- **Multithreading**: Swing Timers (~60 FPS render loops), daemon audio threads, `java.util.concurrent.ExecutorService`
- **Data Persistence**: Standard Java Object Serialization & File I/O (`java.io`)

---

## 💻 System Requirements

- **Operating System**: Windows 10/11, macOS, or Linux
- **Java Runtime**: JDK/JRE 8 or higher (tested with JDK 8, 11, 17, 21)
- **Memory**: 512 MB RAM minimum
- **Display**: Minimum screen resolution of 1024x720
- **Dependencies**: None — pure Java SE, no external libraries or build tools required

---

## 📂 Project Structure

```
Typing-Battle-Arena/
├── .env.example                       # Environment configuration template
├── .gitignore                         # Comprehensive Git exclusion rules
├── README.md                          # Full project documentation
├── compile.bat                        # Windows batch compiler
├── run.bat                            # Windows 1-click execution script
├── test.bat                           # Headless automated test script
├── bin/                                # Compiled bytecode output directory
└── src/
    └── com/typingbattle/
        ├── Main.java                  # Main entry point & look-and-feel setup
        ├── data/
        │   ├── DataManager.java       # Local data persistence & high score manager
        │   └── MatchRecord.java       # Serializable match record model
        ├── engine/
        │   ├── CombatEngine.java      # Combat loop, typing validation, damage formulas
        │   ├── ParticleSystem.java    # 2D particles, sparks, text floaters, screen shake
        │   ├── SoundEngine.java       # Procedural audio waveform synthesizer & BGM
        │   └── WordBank.java          # Categorized vocabulary repository
        ├── model/
        │   ├── AIPersonality.java     # Bot behavior patterns (Aggressive, Defensive, etc.)
        │   ├── CharacterProfile.java  # Fighter stats, color palettes, and attack data
        │   ├── CharacterType.java     # Fighter class enumerations
        │   ├── Difficulty.java        # Difficulty scales, word lengths, AI WPM bounds
        │   ├── Fighter.java           # Live fighter state, HP, power meter, animations
        │   ├── MatchStats.java        # Keystroke analytics, WPM calculation, S-D ranks
        │   └── StoryStage.java        # Campaign acts, boss stats, dialogues, and lore
        ├── test/
        │   └── GameTestSuite.java     # Automated headless verification test suite
        └── ui/
            ├── ArenaBackgroundRenderer.java  # Backdrops (Dojo, Cyberpunk, Sanctum, etc.)
            ├── ArenaPanel.java               # 1P & Story battle arena GUI
            ├── CharacterSelectPanel.java     # Character selector with live 2D animation
            ├── DifficultySelectPanel.java    # Difficulty & bot personality configuration
            ├── FighterRenderer.java          # Custom Java 2D vector graphic animations
            ├── LeaderboardPanel.java         # Career stats & match history table
            ├── MainFrame.java                # Main window frame & lifecycle listener
            ├── MainMenuPanel.java            # Mode selection & audio settings
            ├── ResultPanel.java              # Match results & educational analytics card
            ├── ScreenManager.java            # CardLayout controller managing transitions
            ├── SplashScreenPanel.java        # Glowing animated splash screen
            ├── StoryPanel.java               # Story map & chapter progression
            ├── TwoPlayerArenaPanel.java      # Split-screen local dual battle GUI
            └── UITheme.java                  # Cyberpunk dark theme palettes & UI helpers
```

---

## ⚙ Installation and Setup

### 1. Clone the Repository
```bash
git clone https://github.com/chetan590/Typing-Battle-Arena-A-Java-GUI-Based-Interactive-Typing-Combat-Game.git
cd Typing-Battle-Arena-A-Java-GUI-Based-Interactive-Typing-Combat-Game
```

### 2. Environment Variables Setup (Optional)
Copy `.env.example` to `.env` or set environment variables in your terminal:
```bash
# Optional: Override custom save directory (Default: ~/.typing_battle_arena)
export TYPING_BATTLE_DATA_DIR="./data"

# Optional: Launch with audio muted (Default: false)
export TYPING_BATTLE_MUTE="false"
```

---

## 🔨 How to Compile and Run

### On Windows (1-Click Batch Files)
- **Compile & Run**:
  ```cmd
  run.bat
  ```
- **Compile Only**:
  ```cmd
  compile.bat
  ```
- **Run Automated Tests**:
  ```cmd
  test.bat
  ```

### On Linux / macOS (Terminal)
```bash
# 1. Create bin directory
mkdir -p bin

# 2. Compile all source files
javac -encoding UTF-8 -d bin $(find src -name "*.java")

# 3. Launch the game
java -cp bin com.typingbattle.Main

# 4. Run the automated test suite
java -ea -cp bin com.typingbattle.test.GameTestSuite
```

---

## 🎯 Gameplay Instructions & Controls

| Action | Input / Control | Description |
| :--- | :--- | :--- |
| **Attack** | Type displayed word | Correctly typed letters execute regular strikes against your opponent. |
| **Special Attack** | <kbd>Enter</kbd> (or click button) | Available when the glowing **Power Meter** reaches 100%. |
| **Pause / Resume** | <kbd>Esc</kbd> (or click Pause) | Pauses or resumes the ongoing battle. |
| **Toggle Audio** | Audio Button | Toggles sound effects and synthesized background music on/off. |
| **Two Player – P1** | Left Input Box | Player 1 types their target words in the left combat box. |
| **Two Player – P2** | Right Input Box | Player 2 types their target words in the right combat box. |

---

## 💡 Important Implementation Details

1. **Zero External Dependencies** — The entire project uses pure standard Java SE libraries (`javax.swing`, `java.awt`, `javax.sound.sampled`, `java.io`, `java.util`). No Gradle/Maven wrapper or external JARs are required.
2. **Procedural Audio Synthesis** — Instead of fragile audio files that can fail to load across platforms, `SoundEngine` directly synthesizes 8-bit/16-bit PCM waveforms at 22,050 Hz.
3. **Smooth Health Bar Draining** — Rather than instantly jumping on hit, health bars interpolate smoothly at ~60 FPS with easing formulas (`hp += (target - hp) * 0.12`).
4. **Resilient Data Storage** — Match history and story unlock progression are persisted to disk gracefully. If writing to disk is restricted, the game seamlessly falls back to in-memory state.

---

## 🔮 Future Improvements

- [ ] Online multiplayer duels using Java sockets (`java.net`).
- [ ] Custom word bank imports (practice with specific vocabulary files or code snippets).
- [ ] Additional fighting characters and seasonal boss encounters.
- [ ] Exportable PDF / CSV typing performance certificates.

---

## 🤝 Contribution Guidelines

Contributions are welcome! Follow these steps:
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/amazing-feature`.
3. Commit your changes: `git commit -m "Add amazing feature"`.
4. Push to the branch: `git push origin feature/amazing-feature`.
5. Open a Pull Request.

---
