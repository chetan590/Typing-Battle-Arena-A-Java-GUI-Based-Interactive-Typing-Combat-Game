package com.typingbattle.ui;

import com.typingbattle.engine.CombatEngine;
import com.typingbattle.engine.MultiplayerSession;
import com.typingbattle.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * Screen manager controlling smooth CardLayout view switching
 * and state transitions between all game screens: Login, Player Setup,
 * Main Menu, Campaign Story, Sparring Arena, Cross-Device Multiplayer,
 * Results, Leaderboard, and Analytics.
 */
public class ScreenManager {

    private final JFrame frame;
    private final JPanel cardsPanel;
    private final CardLayout cardLayout;

    // Screen identifiers
    public static final String SCREEN_LOGIN = "LOGIN";
    public static final String SCREEN_SETUP = "SETUP";
    public static final String SCREEN_SPLASH = "SPLASH";
    public static final String SCREEN_MAIN_MENU = "MAIN_MENU";
    public static final String SCREEN_STORY = "STORY";
    public static final String SCREEN_CHAR_SELECT = "CHAR_SELECT";
    public static final String SCREEN_DIFF_SELECT = "DIFF_SELECT";
    public static final String SCREEN_ARENA_1P = "ARENA_1P";
    public static final String SCREEN_ARENA_2P = "ARENA_2P";
    public static final String SCREEN_RESULT = "RESULT";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD";
    public static final String SCREEN_ANALYTICS = "ANALYTICS";
    public static final String SCREEN_MP_LOBBY = "MP_LOBBY";

    // Reusable panels
    private LoginPanel loginPanel;
    private PlayerSetupPanel playerSetupPanel;
    private SplashScreenPanel splashPanel;
    private MainMenuPanel mainMenuPanel;
    private StoryPanel storyPanel;
    private CharacterSelectPanel charSelectPanel;
    private DifficultySelectPanel diffSelectPanel;
    private ArenaPanel arena1PPanel;
    private TwoPlayerArenaPanel arena2PPanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;
    private AnalyticsPanel analyticsPanel;
    private MultiplayerLobbyPanel mpLobbyPanel;

    public ScreenManager(JFrame frame) {
        this.frame = frame;
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);
        this.cardsPanel.setBackground(UITheme.BG_DARK);

        initScreens();
    }

    private void initScreens() {
        loginPanel = new LoginPanel(this);
        playerSetupPanel = new PlayerSetupPanel(this);
        splashPanel = new SplashScreenPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        storyPanel = new StoryPanel(this);
        charSelectPanel = new CharacterSelectPanel(this);
        diffSelectPanel = new DifficultySelectPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);
        analyticsPanel = new AnalyticsPanel(this);
        mpLobbyPanel = new MultiplayerLobbyPanel(this);

        cardsPanel.add(loginPanel, SCREEN_LOGIN);
        cardsPanel.add(playerSetupPanel, SCREEN_SETUP);
        cardsPanel.add(splashPanel, SCREEN_SPLASH);
        cardsPanel.add(mainMenuPanel, SCREEN_MAIN_MENU);
        cardsPanel.add(storyPanel, SCREEN_STORY);
        cardsPanel.add(charSelectPanel, SCREEN_CHAR_SELECT);
        cardsPanel.add(diffSelectPanel, SCREEN_DIFF_SELECT);
        cardsPanel.add(leaderboardPanel, SCREEN_LEADERBOARD);
        cardsPanel.add(analyticsPanel, SCREEN_ANALYTICS);
        cardsPanel.add(mpLobbyPanel, SCREEN_MP_LOBBY);
    }

    public JPanel getContainer() {
        return cardsPanel;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void showScreen(String name) {
        cardLayout.show(cardsPanel, name);
    }

    public void showLogin() {
        showScreen(SCREEN_LOGIN);
    }

    public void showPlayerSetup() {
        playerSetupPanel.syncFromSession();
        showScreen(SCREEN_SETUP);
    }

    public void showSplashScreen() {
        splashPanel.resetAnimation();
        showScreen(SCREEN_SPLASH);
    }

    public void showMainMenu() {
        mainMenuPanel.updatePilotDisplay();
        showScreen(SCREEN_MAIN_MENU);
    }

    public void showStory() {
        storyPanel.refreshStages();
        showScreen(SCREEN_STORY);
    }

    public void showCharacterSelect(boolean forTwoPlayer, StoryStage targetStoryStage) {
        charSelectPanel.setupSelection(forTwoPlayer, targetStoryStage);
        showScreen(SCREEN_CHAR_SELECT);
    }

    public void showDifficultySelect(CharacterProfile p1, CharacterProfile p2) {
        diffSelectPanel.setup(p1, p2);
        showScreen(SCREEN_DIFF_SELECT);
    }

    public void showMultiplayerLobby() {
        mpLobbyPanel.initializeLobby();
        showScreen(SCREEN_MP_LOBBY);
    }

    public void startSinglePlayerBattle(CharacterProfile player, CharacterProfile opponent,
                                        Difficulty difficulty, AIPersonality personality,
                                        String arenaTheme, StoryStage storyStage) {
        if (arena1PPanel != null) {
            arena1PPanel.cleanup();
            cardsPanel.remove(arena1PPanel);
        }

        CombatEngine.BattleMode mode = (storyStage != null)
                ? CombatEngine.BattleMode.STORY_MODE
                : CombatEngine.BattleMode.SINGLE_PLAYER;

        arena1PPanel = new ArenaPanel(this, mode, difficulty, personality, player, opponent, arenaTheme, storyStage);
        cardsPanel.add(arena1PPanel, SCREEN_ARENA_1P);
        showScreen(SCREEN_ARENA_1P);
        arena1PPanel.startBattle();
    }

    public void startTwoPlayerBattle(CharacterProfile p1, CharacterProfile p2) {
        if (arena2PPanel != null) {
            arena2PPanel.cleanup();
            cardsPanel.remove(arena2PPanel);
        }

        arena2PPanel = new TwoPlayerArenaPanel(this, p1, p2);
        cardsPanel.add(arena2PPanel, SCREEN_ARENA_2P);
        showScreen(SCREEN_ARENA_2P);
        arena2PPanel.startBattle();
    }

    public void startOnlineMultiplayerBattle(MultiplayerSession session, CharacterProfile p1, CharacterProfile p2) {
        if (arena2PPanel != null) {
            arena2PPanel.cleanup();
            cardsPanel.remove(arena2PPanel);
        }

        arena2PPanel = new TwoPlayerArenaPanel(this, session, p1, p2);
        cardsPanel.add(arena2PPanel, SCREEN_ARENA_2P);
        showScreen(SCREEN_ARENA_2P);
        arena2PPanel.startBattle();
    }

    public void showResult(CombatEngine engine, StoryStage storyStage) {
        if (resultPanel != null) {
            cardsPanel.remove(resultPanel);
        }

        resultPanel = new ResultPanel(this, engine, storyStage);
        cardsPanel.add(resultPanel, SCREEN_RESULT);
        showScreen(SCREEN_RESULT);
    }

    public void showLeaderboard() {
        leaderboardPanel.refreshData();
        showScreen(SCREEN_LEADERBOARD);
    }

    public void showAnalytics() {
        analyticsPanel.refreshData();
        showScreen(SCREEN_ANALYTICS);
    }
}
