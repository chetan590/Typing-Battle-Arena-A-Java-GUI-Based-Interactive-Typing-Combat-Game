package com.typingbattle.model;

/**
 * Global game configuration settings affecting word presentation pace,
 * opponent attack velocity, audio volume, and vocabulary word length filtering.
 */
public class GameSettings {

    private static GameSettings instance;

    public enum WordAppearanceSpeed {
        SLOW("Slow", 350, "Measured word pacing with smooth transition delay"),
        NORMAL("Normal", 120, "Standard rhythmic battle cadence"),
        FAST("Fast", 0, "Instantaneous word refresh for rapid typing");

        private final String label;
        private final int delayMs;
        private final String description;

        WordAppearanceSpeed(String label, int delayMs, String description) {
            this.label = label;
            this.delayMs = delayMs;
            this.description = description;
        }

        public String getLabel() { return label; }
        public int getDelayMs() { return delayMs; }
        public String getDescription() { return description; }
    }

    public enum OpponentAttackSpeed {
        SLOW("Slow", 0.70, "Gentler AI typing pace and delayed counter-strikes"),
        NORMAL("Normal", 1.00, "Balanced AI reflexes and combat cadence"),
        FAST("Fast", 1.35, "Blistering enemy attack frequency and rapid bursts");

        private final String label;
        private final double multiplier;
        private final String description;

        OpponentAttackSpeed(String label, double multiplier, String description) {
            this.label = label;
            this.multiplier = multiplier;
            this.description = description;
        }

        public String getLabel() { return label; }
        public double getMultiplier() { return multiplier; }
        public String getDescription() { return description; }
    }

    public enum WordLengthFilter {
        LENGTH_3("3 Letters", 3, 3),
        LENGTH_4("4 Letters", 4, 4),
        LENGTH_5("5 Letters", 5, 5),
        LENGTH_6("6 Letters", 6, 6),
        MIXED("Mixed", 0, 0);

        private final String label;
        private final int minLength;
        private final int maxLength;

        WordLengthFilter(String label, int minLength, int maxLength) {
            this.label = label;
            this.minLength = minLength;
            this.maxLength = maxLength;
        }

        public String getLabel() { return label; }
        public int getMinLength() { return minLength; }
        public int getMaxLength() { return maxLength; }
        public boolean isFixedLength() { return this != MIXED; }
    }

    private WordAppearanceSpeed appearanceSpeed = WordAppearanceSpeed.NORMAL;
    private OpponentAttackSpeed attackSpeed = OpponentAttackSpeed.NORMAL;
    private WordLengthFilter wordLengthFilter = WordLengthFilter.MIXED;
    private int volumePercent = 80; // 0 to 100

    private GameSettings() {}

    public static synchronized GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public WordAppearanceSpeed getAppearanceSpeed() {
        return appearanceSpeed;
    }

    public void setAppearanceSpeed(WordAppearanceSpeed appearanceSpeed) {
        if (appearanceSpeed != null) {
            this.appearanceSpeed = appearanceSpeed;
        }
    }

    public OpponentAttackSpeed getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(OpponentAttackSpeed attackSpeed) {
        if (attackSpeed != null) {
            this.attackSpeed = attackSpeed;
        }
    }

    public WordLengthFilter getWordLengthFilter() {
        return wordLengthFilter;
    }

    public void setWordLengthFilter(WordLengthFilter wordLengthFilter) {
        if (wordLengthFilter != null) {
            this.wordLengthFilter = wordLengthFilter;
        }
    }

    public int getVolumePercent() {
        return volumePercent;
    }

    public void setVolumePercent(int volumePercent) {
        this.volumePercent = Math.max(0, Math.min(100, volumePercent));
    }
}
