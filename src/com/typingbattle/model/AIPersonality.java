package com.typingbattle.model;

/**
 * AI Personality types that dictate opponent behavior, typing cadence, and combat strategy.
 */
public enum AIPersonality {
    AGGRESSIVE("Aggressive Bot",
            "Attacks very frequently with rapid bursts. High pressure, but occasionally prone to typing mistakes.",
            1.25, 0.85, 0.15, 0.0),

    DEFENSIVE("Defensive Bot",
            "Waits longer to prepare devastating heavy counter-strikes. Has a chance to guard against attacks.",
            0.80, 1.30, 0.04, 0.25),

    BALANCED("Balanced Bot",
            "Consistent, rhythmic typing speed and balanced offensive output. Solid, steady combatant.",
            1.00, 1.00, 0.06, 0.05),

    BOSS_AI("Keyboard Core Overlord",
            "Adapts its phases dynamically. Enrages and speeds up at low HP, deploying devastating boss specials.",
            1.15, 1.20, 0.03, 0.15);

    private final String displayName;
    private final String description;
    private final double speedModifier;
    private final double damageModifier;
    private final double errorChance;
    private final double guardChance;

    AIPersonality(String displayName, String description,
                  double speedModifier, double damageModifier,
                  double errorChance, double guardChance) {
        this.displayName = displayName;
        this.description = description;
        this.speedModifier = speedModifier;
        this.damageModifier = damageModifier;
        this.errorChance = errorChance;
        this.guardChance = guardChance;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public double getSpeedModifier() { return speedModifier; }
    public double getDamageModifier() { return damageModifier; }
    public double getErrorChance() { return errorChance; }
    public double getGuardChance() { return guardChance; }
}

