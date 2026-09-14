# Typing Battle Arena — A Java GUI-Based Interactive Typing Combat Game

![Java Version](https://img.shields.io/badge/Java-8%2B%20%2F%2011%2B%20%2F%2017%2B%20%2F%2021%2B-blue.svg)
![GUI Framework](https://img.shields.io/badge/GUI-Java%20Swing%20%26%20Java%202D-orange.svg)
![Audio Engine](https://img.shields.io/badge/Audio-Procedural%20Java%20Synthesizer-success.svg)
![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20Java%20SE)-brightgreen.svg)
[![Java Version](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%20%7C%2021%2B-blue.svg)](https://www.oracle.com/java/)
[![GUI Framework](https://img.shields.io/badge/GUI-Java%20Swing%20%26%20Java%202D-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Audio Engine](https://img.shields.io/badge/Audio-Procedural%20Java%20Synthesizer-success.svg)](https://docs.oracle.com/javase/tutorial/sound/)
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero%20(Pure%20Java%20SE)-brightgreen.svg)](https://docs.oracle.com/en/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Typing Battle Arena** is an action-packed, interactive fighting game built with **Java Swing and Java 2D Graphics** that transforms typing speed, accuracy, and reflexes into real-time martial and magical combat. 
---

Unlike conventional fighting games where players press attack buttons, every offensive and defensive move is performed by typing displayed battle words quickly and accurately.
## 📖 Project Overview

**Typing Battle Arena** is an action-packed, interactive fighting game built entirely in standard **Java (Swing & Java 2D Graphics)** that fuses typing speed, accuracy, and reflex challenges with real-time martial and magical combat.

Unlike traditional fighting games where attacks are executed via gamepad buttons or simple keystrokes, every attack, combo streak, and special move in Typing Battle Arena is launched by typing displayed words accurately and swiftly. The game features rich visual effects, animated characters, adaptive AI personalities, local two-player duel mechanics, an educational typing analytics dashboard with S–D rank grading, and a 5-act narrative campaign mode to reclaim the legendary **Keyboard Core**.

---

## 🌟 Key Highlights & Features
## 🌟 Key Features

### 1. 📖 Story Campaign Mode (The Keyboard Core)
- **Lore**: The world has been plunged into silence by rogue warlords who stole the legendary **Keyboard Core** artifact.
- **5 Progressive Acts**:
  - **Act I: Whispering Shadows** — *Bamboo Perimeter Dojo* (Scout Genji, Balanced)
  - **Act II: Cybernetic Overdrive** — *Neo-Tokyo High-Rise* (Enforcer Unit-7X, Aggressive)
  - **Act III: Arcane Inscriptions** — *Sunken Eldorian Sanctuary* (Sorceress Morwenna, Defensive)
  - **Act IV: Molten Bushido** — *The Iron Citadel Foundry* (Lord Raiden, Aggressive)
  - **Act V: Reclaim the Keyboard Core** — *The Core Sanctum Chamber* (Overlord Malakor, Multi-Phase Boss AI)
- Features pre-battle dialogues, stage briefings, boss phase transformations, and victory epilogues.
- **Action-Packed Real-Time Typing Combat**: Continuous, fluid battle mechanics where faster typing velocity deals quicker damage and prevents incoming enemy counters.
- **Dynamic Letter-by-Letter Visual Feedback**:
  - <span style="color:#10B981">**Green**</span>: Accurately typed letters.
  - <span style="color:#EF4444">**Red**</span>: Mistyped letters triggering an instant error buzz and combo penalty.
  - **White**: Upcoming target characters.
- **Dynamic Interpolated Health Bars**: Color transitions smoothly from Green ➔ Yellow ➔ Orange ➔ Red as combatant health is depleted.
- **Combo Multiplier Mechanics**:
  - `3+ Hits`: **COMBO x1.25**
  - `5+ Hits`: **SUPER COMBO x1.50**
  - `10+ Hits`: **ULTIMATE COMBO x2.00**
- **Power Meter & Cinematic Special Attacks**: Every successful word charges your power meter. At 100%, press <kbd>Enter</kbd> or click the Special button to unleash an explosive character ultimate with screen shake and particle bursts.
- **Pure Java SE Procedural Sound Synthesizer**: Generates authentic retro arcade sound effects (key clicks, blade swings, impact crunches, magic charges, victory fanfares) and an energetic synth battle soundtrack on the fly via `javax.sound.sampled` with zero external audio assets.
- **Educational Performance Analytics**: Post-battle diagnostics report Words Per Minute (WPM), Accuracy (%), peak combo, error counts, personalized coaching advice, and an **S / A / B / C / D Rank Grade**.
- **Persistent Local Records**: Match history, high scores, best WPM records, and story unlocks are automatically saved locally.

### 2. ⚔ Game Modes
- **Story Mode (Campaign)**: Journey through the 5 acts with persistent unlocks.
- **Single Player (Versus AI)**: Custom sparring matches with selectable difficulty, AI personality, and arena backdrop.
- **Two Player (Local Duel)**: Head-to-head combat on the same system with independent split-screen typing inputs, separate word streams, and dual power gauges!
---

### 3. 🥋 Selectable Combatants
Each fighter features unique Java 2D animations (idle breathing, attack lunges, hurt recoil, special charging, and victory poses):
- **Hayato the Silent (Shadow Ninja)**: Blinding agility, shuriken flurry, and Shadow Execution.
- **Kenji Crimson Blade (Bushido Samurai)**: High damage, precise iaido slashes, and Dragon Cleave.
- **Aurelia Spellweaver (Arcane Mage)**: Runic bursts, elemental shielding, and Cataclysmic Comet.
- **Unit VX-9000 (Cyber Mecha)**: Heavily armored android with plasma cannons and Orbital Core Blasts.
- **Malakor the Eclipse (Void Stalker)**: Dark tendrils, high burst attack, and Abyssal Singularity.
## 🎮 Game Modes

### 4. 🤖 Adaptive AI Personalities
- **Aggressive Bot**: Rapid burst typing, high damage pressure, slightly higher mistake chance.
- **Defensive Bot**: Methodical cadence, heavy counter-strikes, and chance to guard/block damage.
- **Balanced Bot**: Smooth, rhythmic typing pace for standard battles.
- **Boss AI**: Multi-phase boss. Enrages below 50% HP with a 35% typing velocity boost and charges ultimate attacks.
### 1. 📖 Story Mode (The Keyboard Core Campaign)
The world has been plunged into silence by rogue warlords who seized the legendary **Keyboard Core**. Advance across 5 acts, engaging in pre-battle cutscenes and defeating sector guardians:
- **Act I: Whispering Shadows** — *Bamboo Perimeter Dojo* (Guardian: Rogue Scout Genji)
- **Act II: Cybernetic Overdrive** — *Neo-Tokyo High-Rise* (Guardian: Security Enforcer Unit-7X)
- **Act III: Arcane Inscriptions** — *Sunken Eldorian Sanctuary* (Guardian: Sorceress Morwenna)
- **Act IV: Molten Bushido** — *The Iron Citadel Foundry* (Guardian: Lord Raiden Crimson-Blade)
- **Act V: Reclaim the Keyboard Core** — *The Core Sanctum Chamber* (Final Boss: Overlord Malakor the Void)

### 5. 💥 Combat Mechanics & Visual Feedback
- **Real-Time Character Highlighting**:
  - <span style="color:#10B981">**Green**</span>: Correct character typed.
  - <span style="color:#EF4444">**Red**</span>: Mistyped character with error buzz.
  - **White**: Upcoming characters.
- **Dynamic Health Bars**: Real-time smooth animation transitioning through Green ➔ Yellow ➔ Orange ➔ Red.
- **Combo Multipliers**:
  - `3+ Hits`: COMBO x1.25
  - `5+ Hits`: SUPER COMBO x1.5
  - `10+ Hits`: ULTIMATE COMBO x2.0
- **Power Meter & Special Attack**: Every correct word charges the power meter. At 100%, unleash an explosive special move using `Enter` or the Special button.
- **Particle Engine**: Hit sparks, blade slashes, magic blasts, floating combat text (`"-140"`, `"CRITICAL!"`), and screen shake.
- **Procedural Sound Synthesizer**: Pure Java SE `javax.sound.sampled` generating authentic retro arcade SFX and upbeat synth battle music without missing file errors.
### 2. ⚔ Single Player (Versus AI)
Customizable quick sparring matches:
- Select your fighter and opponent.
- Choose your difficulty (**Easy**, **Medium**, **Hard**, **Intense**).
- Choose from 4 unique AI personalities.
- Select your arena backdrop (**Dojo**, **Cyberpunk City**, **Mystic Temple**, **Volcanic Core**, **Keyboard Sanctum**).

### 6. 📊 Educational Performance Analytics & Ranking
At the end of every match, players receive a deep breakdown:
- **Typing Velocity (WPM)**: Words Per Minute (`(correctChars / 5.0) / minutes`).
- **Accuracy (%)**: `(correctKeystrokes / totalKeystrokes) * 100`.
- **Combat Ranks**:
  - 🏆 **S Rank**: Legendary Typist (WPM ≥ 65, Acc ≥ 95%, Combo ≥ 6)
  - 🥇 **A Rank**: Master Combatant (WPM ≥ 50, Acc ≥ 90%)
  - 🥈 **B Rank**: Skilled Fighter (WPM ≥ 35, Acc ≥ 82%)
  - 🥉 **C Rank**: Apprentice (WPM ≥ 20, Acc ≥ 70%)
  - 🔰 **D Rank**: Trainee (Below 20 WPM or Acc < 70%)
- **Personalized Skill Coaching**: Provides targeted advice on balancing speed and accuracy, recommending suitable difficulties.
- **Persistent Leaderboard**: Saves high scores, best WPM, and match logs to local storage (`~/.typing_battle_arena/`).
### 3. 👥 Two Player (Local Duel)
Head-to-head competitive typing on the same system! Player 1 and Player 2 have separate split-screen typing prompts, independent word streams, combo counters, health bars, and individual special attack triggers.

---

## 🚀 How to Run the Game
## 🥋 Selectable Characters

### Prerequisites
- Any standard Java Development Kit (JDK 8, 11, 17, 21 or later).
- No external libraries, build tools, or frameworks required!
Each fighter is brought to life with custom Java 2D vector animation states (idle breathing, attack dashes, hurt recoils, special levitation, and victory celebration):

### Option 1: 1-Click Launch (Windows)
Double-click `run.bat` or open PowerShell in the project directory and run:
```cmd
run.bat
| Character | Class | Signature Attack | Special Move | Primary Trait |
| :--- | :--- | :--- | :--- | :--- |
| **Hayato the Silent** | Shadow Ninja | Shuriken Flurry | *Shadow Execution* | High Speed (1.25x), Swift Strikes |
| **Kenji Crimson Blade** | Bushido Samurai | Iaido Slash | *Dragon Cleave* | Heavy Attack Power (1.20x) |
| **Aurelia Spellweaver** | Arcane Mage | Arcane Surge | *Cataclysmic Comet* | Balanced Magic & Balanced Stats |
| **Unit VX-9000** | Cyber Mecha | Plasma Burst | *Orbital Core Blast* | High Defense Armor (1.20x) |
| **Malakor the Eclipse** | Void Stalker | Void Tendrils | *Abyssal Singularity* | Burst Attack Power (1.25x) |

---

## 🤖 AI Personalities

- **Aggressive Bot**: Rapid burst typing, high pressure cadence, slightly higher typo probability.
- **Defensive Bot**: Slower, methodical keystrokes dealing heavy counter-damage and chance to mitigate attacks.
- **Balanced Bot**: Steady, rhythmic typing cadence suited for standard sparring.
- **Boss AI**: Dynamic multi-phase boss. Enrages below 50% HP with a 35% speed surge and unleashes devastating specials.

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

---

## 📁 Project Structure

```
Typing-Battle-Arena/
├── .env.example                       # Environment configuration template
├── .gitignore                         # Comprehensive Git exclusion rules
├── README.md                          # Full project documentation
├── compile.bat                        # Windows batch compiler
├── run.bat                            # Windows 1-click execution script
├── test.bat                           # Headless automated test script
├── bin/                               # Compiled bytecode output directory
└── src/
    └── com/typingbattle/
        ├── Main.java                  # Main entry point & look-and-feel setup
        ├── data/
        │   ├── DataManager.java       # Local data persistence & high score manager
        │   └── MatchRecord.java       # Serializable match record model
        ├── engine/
        │   ├── CombatEngine.java      # Combat loop, typing validation, damage equations
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
            ├── ArenaBackgroundRenderer.java # Backdrops (Dojo, Cyberpunk, Sanctum, etc.)
            ├── ArenaPanel.java        # 1P & Story battle arena GUI
            ├── CharacterSelectPanel.java    # Character selector with live 2D animation
            ├── DifficultySelectPanel.java   # Difficulty & bot personality configuration
            ├── FighterRenderer.java   # Custom Java 2D vector graphic character animations
            ├── LeaderboardPanel.java  # Career stats & match history table
            ├── MainFrame.java         # Main window frame & lifecycle listener
            ├── MainMenuPanel.java     # Mode selection & audio settings
            ├── ResultPanel.java       # Match results & educational analytics card
            ├── ScreenManager.java     # CardLayout controller managing transitions
            ├── SplashScreenPanel.java # Glowing animated splash screen
            ├── StoryPanel.java        # Story map & chapter progression
            ├── TwoPlayerArenaPanel.java     # Split-screen local dual battle GUI
            └── UITheme.java           # Cyberpunk dark theme palettes & UI helpers
```

### Option 2: Compile & Run via Terminal
---

## ⚙ Installation and Setup

### 1. Clone the Repository
```bash
# Compile all classes
javac -encoding UTF-8 -d bin src/com/typingbattle/model/*.java src/com/typingbattle/engine/*.java src/com/typingbattle/data/*.java src/com/typingbattle/ui/*.java src/com/typingbattle/test/*.java src/com/typingbattle/*.java
git clone https://github.com/chetan590/Typing-Battle-Arena-A-Java-GUI-Based-Interactive-Typing-Combat-Game.git
cd Typing-Battle-Arena-A-Java-GUI-Based-Interactive-Typing-Combat-Game
```

# Run the game
java -cp bin com.typingbattle.Main
### 2. Environment Variables Setup (Optional)
Copy `.env.example` to `.env` or set environment variables in your terminal:
```bash
# Optional: Override custom save directory (Default: ~/.typing_battle_arena)
export TYPING_BATTLE_DATA_DIR="./data"

# Optional: Launch with audio muted (Default: false)
export TYPING_BATTLE_MUTE="false"
```

### Option 3: Run Automated Test Suite
---

## 🔨 How to Compile and Run

### On Windows (1-Click Batch Files)
- **To Compile & Run**: Double-click `run.bat` or run:
  ```cmd
  run.bat
  ```
- **To Compile Only**:
  ```cmd
  compile.bat
  ```
- **To Run Automated Tests**:
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

# 4. Run automated test suite
java -ea -cp bin com.typingbattle.test.GameTestSuite
```

---

## 🎮 Game Controls
## 🎯 Gameplay Instructions & Controls

| Action | Control |
| :--- | :--- |
| **Typing Attack** | Type the displayed target word in the input box |
| **Special Attack** | Press <kbd>Enter</kbd> (or click Special button) when Power Meter reaches 100% |
| **Pause Battle** | Press <kbd>Esc</kbd> or click Pause button |
| **Mute / Unmute Audio** | Click the Audio button in the Main Menu or Settings |
| Action | Input / Control | Description |
| :--- | :--- | :--- |
| **Attack** | Type displayed word | Validating letters executes regular strikes against your opponent. |
| **Special Attack** | <kbd>Enter</kbd> (or click button) | Available when the glowing **Power Meter** reaches 100%. |
| **Pause / Resume** | <kbd>Esc</kbd> (or click Pause) | Pauses or resumes the ongoing battle. |
| **Toggle Audio** | Audio Button | Toggles sound effects and synthesized background music on/off. |
| **Two Player P1** | Left Input Box | Player 1 types their target words in the left combat box. |
| **Two Player P2** | Right Input Box | Player 2 types their target words in the right combat box. |

### Combat Scoring & Ranks
Ranks are calculated from your Words Per Minute (WPM), accuracy percentage, and peak combo streak:
- 👑 **S Rank**: WPM ≥ 65, Accuracy ≥ 95%, Combo ≥ 6 (Legendary Typist)
- 🥇 **A Rank**: WPM ≥ 50, Accuracy ≥ 90% (Master Combatant)
- 🥈 **B Rank**: WPM ≥ 35, Accuracy ≥ 82% (Skilled Fighter)
- 🥉 **C Rank**: WPM ≥ 20, Accuracy ≥ 70% (Apprentice)
- 🔰 **D Rank**: Below 20 WPM or Accuracy < 70% (Trainee)

---

## 📂 Project Architecture
## 💡 Important Implementation Details

```
src/com/typingbattle/
├── Main.java                          # Entry point, Swing EDT initialization
├── model/
│   ├── CharacterType.java             # Archetype definitions (Ninja, Samurai, etc.)
│   ├── CharacterProfile.java          # Fighter stats, color palettes, attack names
│   ├── Difficulty.java                # Easy, Medium, Hard, Intense configurations
│   ├── AIPersonality.java             # Aggressive, Defensive, Balanced, Boss AI
│   ├── Fighter.java                   # Real-time HP, power meter, animations, states
│   ├── MatchStats.java                # WPM math, accuracy %, S-D rank calculation
│   └── StoryStage.java                # 5-act campaign chapters, dialogues, and lore
├── engine/
│   ├── WordBank.java                  # Categorized vocabulary repository
│   ├── ParticleSystem.java            # Hit sparks, floating combat text, screen shake
│   ├── SoundEngine.java               # Procedural audio waveform synthesizer
│   └── CombatEngine.java              # Combat loop, typing validation, damage formulas
├── data/
│   ├── MatchRecord.java               # Serializable match statistics record
│   └── DataManager.java               # High score & story progress file persistence
├── ui/
│   ├── UITheme.java                   # Dark cyberpunk color theme, typography, buttons
│   ├── FighterRenderer.java           # Custom Java 2D vector graphic animations
│   ├── ArenaBackgroundRenderer.java   # Backdrops (Dojo, Cyber City, Temple, Core)
│   ├── ScreenManager.java             # CardLayout navigation controller
│   ├── SplashScreenPanel.java         # Animated title & floating Keyboard Core
│   ├── MainMenuPanel.java             # Mode navigation & audio controls
│   ├── StoryPanel.java                # Campaign map & stage dialogues
│   ├── CharacterSelectPanel.java      # Live animated preview & stats radars
│   ├── DifficultySelectPanel.java     # Difficulty & bot configuration
│   ├── ArenaPanel.java                # 1P / Story combat arena & real-time HUD
│   ├── TwoPlayerArenaPanel.java       # Local split-screen duel arena
│   ├── ResultPanel.java               # Performance report, ranks, and recommendations
│   └── LeaderboardPanel.java          # High score table & career statistics
└── test/
    └── GameTestSuite.java             # Headless unit verification suite
```
1. **Zero External Dependencies**: The entire project uses pure standard Java SE libraries (`javax.swing`, `java.awt`, `javax.sound.sampled`, `java.io`, `java.util`). No Gradle/Maven wrapper or external JARs are required.
2. **Procedural Audio Synthesis**: Instead of fragile audio files that can fail to load across platforms, `SoundEngine` directly synthesizes 8-bit/16-bit PCM waveforms at 22,050 Hz.
3. **Smooth Health Bar Draining**: Rather than instantly jumping on hit, health bars interpolate smoothly at ~60 FPS with easing formulas (`hp += (target - hp) * 0.12`).
4. **Resilient Data Storage**: Match history and story unlock progression are persisted to disk gracefully. If writing to disk is restricted, it seamlessly operates with memory state.

---

## 🔮 Future Improvements

- [ ] Online multiplayer duels using Java sockets (`java.net`).
- [ ] Custom word bank imports (allow users to practice specific vocabulary files or code snippets).
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

