package com.typingbattle.model;

/**
 * Detailed performance analytics tracking typing metrics, combat stats,
 * and educational ranking evaluations.
 */
public class MatchStats {

    public enum Rank {
        S("S RANK", "Legendary Typist! Outstanding speed & flawless precision.", "#FFD700"), // Gold
        A("A RANK", "Master Combatant! High typing velocity and great accuracy.", "#00E6B4"), // Emerald Cyan
        B("B RANK", "Skilled Fighter! Solid typing rhythm and reliable strikes.", "#3B82F6"), // Royal Blue
        C("C RANK", "Apprentice! Good effort, focus on accuracy to build combos.", "#F59E0B"), // Amber
        D("D RANK", "Trainee! Keep practicing typing basics to overcome faster foes.", "#EF4444"); // Crimson

        private final String label;
        private final String description;
        private final String hexColor;

        Rank(String label, String description, String hexColor) {
            this.label = label;
            this.description = description;
            this.hexColor = hexColor;
        }

        public String getLabel() { return label; }
        public String getDescription() { return description; }
        public String getHexColor() { return hexColor; }
    }

    private final long startTimeMs;
    private long endTimeMs;
    private int totalKeystrokes = 0;
    private int correctKeystrokes = 0;
    private int errorKeystrokes = 0;
    private int totalWordsAttempted = 0;
    private int correctWords = 0;
    private int failedWords = 0;
    private int totalWordCharacters = 0;
    private int highestCombo = 0;
    private int specialAttacksUsed = 0;
    private int totalDamageDealt = 0;
    private boolean playerWon = false;

    public MatchStats() {
        this.startTimeMs = System.currentTimeMillis();
    }

    public void recordKeystroke(boolean correct) {
        totalKeystrokes++;
        if (correct) {
            correctKeystrokes++;
        } else {
            errorKeystrokes++;
        }
    }

    public void recordWord(boolean completedSuccessfully, int wordLength, int combo) {
        totalWordsAttempted++;
        if (completedSuccessfully) {
            correctWords++;
            totalWordCharacters += wordLength;
            if (combo > highestCombo) {
                highestCombo = combo;
            }
        } else {
            failedWords++;
        }
    }

    public void recordSpecialAttack() {
        specialAttacksUsed++;
    }

    public void recordDamage(int damage) {
        totalDamageDealt += damage;
    }

    public void completeMatch(boolean victory) {
        this.endTimeMs = System.currentTimeMillis();
        this.playerWon = victory;
    }

    public double getDurationSeconds() {
        long end = (endTimeMs > 0) ? endTimeMs : System.currentTimeMillis();
        return Math.max(1.0, (end - startTimeMs) / 1000.0);
    }

    /**
     * Calculates Words Per Minute (WPM) using standard 5 characters = 1 word metric.
     */
    public int getWpm() {
        double minutes = getDurationSeconds() / 60.0;
        if (minutes <= 0.001) return 0;
        double wpm = (correctKeystrokes / 5.0) / minutes;
        return (int) Math.round(Math.max(0, wpm));
    }

    /**
     * Calculates Typing Accuracy percentage.
     */
    public double getAccuracy() {
        if (totalKeystrokes == 0) return 100.0;
        double acc = ((double) correctKeystrokes / totalKeystrokes) * 100.0;
        return Math.min(100.0, Math.max(0.0, Math.round(acc * 10.0) / 10.0));
    }

    /**
     * Evaluates performance rank (S, A, B, C, D) using speed, accuracy, and combo metrics.
     */
    public Rank calculateRank() {
        int wpm = getWpm();
        double acc = getAccuracy();

        if (wpm >= 65 && acc >= 95.0 && highestCombo >= 6) {
            return Rank.S;
        } else if (wpm >= 50 && acc >= 90.0) {
            return Rank.A;
        } else if (wpm >= 35 && acc >= 82.0) {
            return Rank.B;
        } else if (wpm >= 20 && acc >= 70.0) {
            return Rank.C;
        } else {
            return Rank.D;
        }
    }

    /**
     * Recommends the optimal difficulty level or training advice.
     */
    public String getRecommendation() {
        int wpm = getWpm();
        double acc = getAccuracy();

        if (acc < 80.0) {
            return "Recommendation: Slow down slightly and prioritize accuracy! Clean typing prevents combo resets and generates max attack power.";
        } else if (wpm >= 70 && acc >= 92.0) {
            return "Recommendation: Impressive reflex speed! You are fully primed to conquer Intense Difficulty!";
        } else if (wpm >= 45) {
            return "Recommendation: Great flow! Try Hard Difficulty to expand your combat vocabulary and quicken your strikes.";
        } else if (wpm >= 30) {
            return "Recommendation: Solid progress! Practice Medium Difficulty to push your rhythm above 45 WPM.";
        } else {
            return "Recommendation: Keep sparring on Easy Difficulty to build muscle memory and finger positioning.";
        }
    }

    // Getters
    public int getTotalKeystrokes() { return totalKeystrokes; }
    public int getCorrectKeystrokes() { return correctKeystrokes; }
    public int getErrorKeystrokes() { return errorKeystrokes; }
    public int getTotalWordsAttempted() { return totalWordsAttempted; }
    public int getCorrectWords() { return correctWords; }
    public int getFailedWords() { return failedWords; }
    public int getTotalWordCharacters() { return totalWordCharacters; }
    public int getHighestCombo() { return highestCombo; }
    public int getSpecialAttacksUsed() { return specialAttacksUsed; }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public boolean isPlayerWon() { return playerWon; }
    public long getStartTimeMs() { return startTimeMs; }
    public long getEndTimeMs() { return endTimeMs; }
}

