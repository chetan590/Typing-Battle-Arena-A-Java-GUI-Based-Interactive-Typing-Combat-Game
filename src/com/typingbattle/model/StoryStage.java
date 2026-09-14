package com.typingbattle.model;

/**
 * Story Mode Stage definition representing a chapter in reclaiming the Keyboard Core.
 */
public class StoryStage {
    private final int stageNumber;
    private final String stageTitle;
    private final String locationName;
    private final CharacterType bossType;
    private final String bossName;
    private final AIPersonality personality;
    private final Difficulty difficulty;
    private final String introDialogue;
    private final String victoryEpilogue;
    private final String arenaTheme;

    public StoryStage(int stageNumber, String stageTitle, String locationName,
                      CharacterType bossType, String bossName,
                      AIPersonality personality, Difficulty difficulty,
                      String introDialogue, String victoryEpilogue, String arenaTheme) {
        this.stageNumber = stageNumber;
        this.stageTitle = stageTitle;
        this.locationName = locationName;
        this.bossType = bossType;
        this.bossName = bossName;
        this.personality = personality;
        this.difficulty = difficulty;
        this.introDialogue = introDialogue;
        this.victoryEpilogue = victoryEpilogue;
        this.arenaTheme = arenaTheme;
    }

    public static StoryStage[] getAllStages() {
        return new StoryStage[] {
            new StoryStage(
                1,
                "Act I: Whispering Shadows",
                "Bamboo Perimeter Dojo",
                CharacterType.NINJA,
                "Rogue Infiltrator Genji",
                AIPersonality.BALANCED,
                Difficulty.EASY,
                "Halt! None trespass toward the Keyboard Core! Show me if your fingers have the swiftness of wind!",
                "The scout collapses into smoke. 'You are fast... but the Cyber Enforcers await in the metropolis...'",
                "DOJO"
            ),
            new StoryStage(
                2,
                "Act II: Cybernetic Overdrive",
                "Neo-Tokyo High-Rise",
                CharacterType.ROBOT_WARRIOR,
                "Security Enforcer Unit-7X",
                AIPersonality.AGGRESSIVE,
                Difficulty.MEDIUM,
                "WARNING: Unregistered typist detected. Initiating high-frequency plasma purge!",
                "Sparks erupt from the droid chassis. 'Error 404... Defensive firewalls breached...'",
                "CYBERPUNK"
            ),
            new StoryStage(
                3,
                "Act III: Arcane Inscriptions",
                "Sunken Eldorian Sanctuary",
                CharacterType.MAGE,
                "Sorceress Morwenna",
                AIPersonality.DEFENSIVE,
                Difficulty.MEDIUM,
                "The Keyboard Core pulses with primordial runes. A mortal intellect cannot withstand my spells!",
                "Her arcane barrier shatters into glittering stardust. 'Incredible... the lexicon bends to your will!'",
                "MYSTIC_TEMPLE"
            ),
            new StoryStage(
                4,
                "Act IV: Molten Bushido",
                "The Iron Citadel Foundry",
                CharacterType.SAMURAI,
                "Lord Raiden Crimson-Blade",
                AIPersonality.AGGRESSIVE,
                Difficulty.HARD,
                "A thousand keystrokes tempered in fire! Draw your keys and let our combat decide fate!",
                "Lord Raiden kneels and bows his katana. 'A magnificent duel. Claim your destiny in the core chamber!'",
                "VOLCANIC_CORE"
            ),
            new StoryStage(
                5,
                "Act V: Reclaim the Keyboard Core",
                "The Core Sanctum Chamber",
                CharacterType.SHADOW_FIGHTER,
                "Overlord Malakor the Void",
                AIPersonality.BOSS_AI,
                Difficulty.INTENSE,
                "At last, the final contender! The Keyboard Core is mine! I shall delete every word in reality!",
                "The darkness dissipates! The legendary Keyboard Core glows with radiant light—peace is restored to the realm!",
                "KEYBOARD_SANCTUM"
            )
        };
    }

    public int getStageNumber() { return stageNumber; }
    public String getStageTitle() { return stageTitle; }
    public String getLocationName() { return locationName; }
    public CharacterType getBossType() { return bossType; }
    public String getBossName() { return bossName; }
    public AIPersonality getPersonality() { return personality; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getIntroDialogue() { return introDialogue; }
    public String getVictoryEpilogue() { return victoryEpilogue; }
    public String getArenaTheme() { return arenaTheme; }
}

