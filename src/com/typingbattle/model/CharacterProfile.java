package com.typingbattle.model;

import java.awt.Color;

/**
 * Detailed attributes and combat statistics for a character.
 */
public class CharacterProfile {
    private final CharacterType type;
    private final String name;
    private final String title;
    private final String lore;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final double attackMultiplier;
    private final double speedMultiplier;
    private final double defenseMultiplier;
    private final String standardAttackName;
    private final String specialAttackName;
    private final String victoryQuote;
    private final String defeatQuote;

    public CharacterProfile(CharacterType type, String name, String title, String lore,
                            Color primaryColor, Color secondaryColor,
                            double attackMultiplier, double speedMultiplier, double defenseMultiplier,
                            String standardAttackName, String specialAttackName,
                            String victoryQuote, String defeatQuote) {
        this.type = type;
        this.name = name;
        this.title = title;
        this.lore = lore;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.attackMultiplier = attackMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.defenseMultiplier = defenseMultiplier;
        this.standardAttackName = standardAttackName;
        this.specialAttackName = specialAttackName;
        this.victoryQuote = victoryQuote;
        this.defeatQuote = defeatQuote;
    }

    public static CharacterProfile getProfile(CharacterType type) {
        switch (type) {
            case NINJA:
                return new CharacterProfile(
                        CharacterType.NINJA,
                        "Hayato the Silent",
                        "Master of the Whispering Wind",
                        "Trained in the mountain monastery of Kirigakure, Hayato attacks with blinding typing reflexes.",
                        new Color(0, 230, 180), // Emerald Cyan
                        new Color(20, 60, 50),
                        1.05, 1.25, 0.90,
                        "Shuriken Flurry",
                        "Shadow Execution",
                        "Silence has fallen once again.",
                        "The shadow has dispersed..."
                );
            case SAMURAI:
                return new CharacterProfile(
                        CharacterType.SAMURAI,
                        "Kenji Crimson Blade",
                        "Lone Ronin of the Scarlet Sun",
                        "Follower of the ancient Bushido code, wielding a katana sharpened by thousand keystrokes.",
                        new Color(255, 75, 75), // Crimson Red
                        new Color(70, 20, 20),
                        1.20, 1.00, 1.05,
                        "Iaido Slash",
                        "Dragon Cleave",
                        "My blade never wavers in the face of chaos.",
                        "My sword... has failed me."
                );
            case MAGE:
                return new CharacterProfile(
                        CharacterType.MAGE,
                        "Aurelia Spellweaver",
                        "Grand Archmage of Eldoria",
                        "Channeler of primordial arcane leylines, transmuting typed glyphs into cosmic power.",
                        new Color(175, 75, 255), // Arcane Purple
                        new Color(50, 20, 70),
                        1.15, 1.05, 0.95,
                        "Arcane Surge",
                        "Cataclysmic Comet",
                        "The arcana reigns supreme!",
                        "The magic fades away..."
                );
            case ROBOT_WARRIOR:
                return new CharacterProfile(
                        CharacterType.ROBOT_WARRIOR,
                        "Unit VX-9000",
                        "Apex Combat Automaton",
                        "Forged in cybernetic foundries to safeguard the digital cores with plasma artillery.",
                        new Color(0, 190, 255), // High-Tech Blue
                        new Color(15, 45, 70),
                        1.10, 0.95, 1.20,
                        "Plasma Burst",
                        "Orbital Core Blast",
                        "Target neutralized. Combat parameters optimal.",
                        "System critical... powering down."
                );
            case SHADOW_FIGHTER:
            default:
                return new CharacterProfile(
                        CharacterType.SHADOW_FIGHTER,
                        "Malakor the Eclipse",
                        "Void Revenant",
                        "A specter born from corrupt keystrokes, wielding tendrils of shadowy anti-matter.",
                        new Color(255, 170, 0), // Dark Gold / Void Amber
                        new Color(50, 35, 10),
                        1.25, 1.10, 0.85,
                        "Void Tendrils",
                        "Abyssal Singularity",
                        "All shall be consumed by the dark abyss!",
                        "The void... beckons me back."
                );
        }
    }

    public CharacterType getType() { return type; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getLore() { return lore; }
    public Color getPrimaryColor() { return primaryColor; }
    public Color getSecondaryColor() { return secondaryColor; }
    public double getAttackMultiplier() { return attackMultiplier; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getDefenseMultiplier() { return defenseMultiplier; }
    public String getStandardAttackName() { return standardAttackName; }
    public String getSpecialAttackName() { return specialAttackName; }
    public String getVictoryQuote() { return victoryQuote; }
    public String getDefeatQuote() { return defeatQuote; }
}

