package com.typingbattle.model;

/**
 * Game difficulty levels affecting word complexity, AI typing speed, and damage scaling.
 */
public enum Difficulty {
    EASY("Easy", "Short & common words. Slower enemy attacks. Perfect for newcomers.",
         3, 5, 25, 35, 0.15, 1.2, 0.8),
    MEDIUM("Medium", "Moderate length words. Balanced gameplay & attack rhythm.",
           5, 7, 45, 55, 0.08, 1.0, 1.0),
    HARD("Hard", "Longer & challenging words. Swift opponent attacks.",
         7, 10, 65, 80, 0.04, 0.95, 1.2),
    INTENSE("Intense", "Complex technical vocabulary. Blistering AI reflexes & high damage.",
            8, 14, 85, 110, 0.02, 0.85, 1.45);

    private final String displayName;
    private final String description;
    private final int minWordLength;
    private final int maxWordLength;
    private final int minAiWpm;
    private final int maxAiWpm;
    private final double aiErrorRate;
    private final double playerDamageScale;
    private final double aiDamageScale;

    Difficulty(String displayName, String description,
               int minWordLength, int maxWordLength,
               int minAiWpm, int maxAiWpm,
               double aiErrorRate,
               double playerDamageScale, double aiDamageScale) {
        this.displayName = displayName;
        this.description = description;
        this.minWordLength = minWordLength;
        this.maxWordLength = maxWordLength;
        this.minAiWpm = minAiWpm;
        this.maxAiWpm = maxAiWpm;
        this.aiErrorRate = aiErrorRate;
        this.playerDamageScale = playerDamageScale;
        this.aiDamageScale = aiDamageScale;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getMinWordLength() { return minWordLength; }
    public int getMaxWordLength() { return maxWordLength; }
    public int getMinAiWpm() { return minAiWpm; }
    public int getMaxAiWpm() { return maxAiWpm; }
    public double getAiErrorRate() { return aiErrorRate; }
    public double getPlayerDamageScale() { return playerDamageScale; }
    public double getAiDamageScale() { return aiDamageScale; }
}

