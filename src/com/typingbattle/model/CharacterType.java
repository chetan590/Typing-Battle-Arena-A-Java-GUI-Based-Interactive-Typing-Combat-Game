package com.typingbattle.model;

/**
 * Character types available in Typing Battle Arena.
 */
public enum CharacterType {
    NINJA("Shadow Ninja", "Swift and elusive master of martial arts and shurikens."),
    SAMURAI("Bushido Samurai", "Disciplined blade warrior with devastating iaido slashes."),
    MAGE("Arcane Mage", "Mystic spellcaster commanding elemental arcane magic."),
    ROBOT_WARRIOR("Cyber Mecha", "High-tech military android equipped with plasma weaponry."),
    SHADOW_FIGHTER("Void Stalker", "Enigmatic combatant weaving darkness and phantom strikes.");

    private final String displayName;
    private final String description;

    CharacterType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

