package com.typingbattle.model;

/**
 * Manages the current player session, authentication status,
 * pilot name, and selected signature combatant across the entire game.
 */
public class SessionManager {

    private static SessionManager instance;

    private boolean loggedIn = false;
    private String loginUsername = "warrior";
    private String playerName = "Chetan";
    private CharacterType preferredCombatant = CharacterType.NINJA;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        if (loginUsername != null && !loginUsername.trim().isEmpty()) {
            this.loginUsername = loginUsername.trim();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty()) {
            this.playerName = playerName.trim();
        }
    }

    public CharacterType getPreferredCombatant() {
        return preferredCombatant;
    }

    public void setPreferredCombatant(CharacterType preferredCombatant) {
        if (preferredCombatant != null) {
            this.preferredCombatant = preferredCombatant;
        }
    }

    public CharacterProfile getPreferredProfile() {
        return CharacterProfile.getProfile(preferredCombatant);
    }

    /**
     * Formats player identifier string matching user requirements.
     * E.g. "Chetan [Hayato the Silent]"
     */
    public String getPlayerDisplayInfo() {
        CharacterProfile profile = getPreferredProfile();
        return playerName + " [" + profile.getName() + "]";
    }

    /**
     * Short display info.
     * E.g. "Chetan (Ninja)"
     */
    public String getPlayerShortInfo() {
        return playerName + " (" + preferredCombatant.getDisplayName() + ")";
    }
}
