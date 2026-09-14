package com.typingbattle.ui;

import com.typingbattle.engine.CombatEngine;
import com.typingbattle.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * Screen manager controlling smooth CardLayout view switching
 * and state transitions between all game screens.
 */
public class ScreenManager {

    private final JFrame frame;
    private final JPanel cardsPanel;
    private final CardLayout cardLayout;

    // Screen identifiers
    public static final String SCREEN_SPLASH = "SPLASH";
    public static final String SCREEN_MAIN_MENU = "MAIN_MENU";
    public static final String SCREEN_STORY = "STORY";
    public static final String SCREEN_CHAR_SELECT = "CHAR_SELECT";
    public static final String SCREEN_DIFF_SELECT = "DIFF_SELECT";
    public static final String SCREEN_ARENA_1P = "ARENA_1P";
    public static final String SCREEN_ARENA_2P = "ARENA_2P";
    public static final String SCREEN_RESULT = "RESULT";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD";

    // Reusable panels
    private SplashScreenPanel splashPanel;
    private MainMenuPanel mainMenuPanel;
    private StoryPanel storyPanel;
    private CharacterSelectPanel charSelectPanel;
    private DifficultySelectPanel diffSelectPanel;
    private ArenaPanel arena1PPanel;
    private TwoPlayerArenaPanel arena2PPanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;

    public ScreenManager(JFrame frame) {
        this.frame = frame;
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);
        this.cardsPanel.setBackground(UITheme.BG_DARK);

        initScreens();
    }

    private void initScreens() {
        splashPanel = new SplashScreenPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
        storyPanel = new StoryPanel(this);
        charSelectPanel = new CharacterSelectPanel(this);
        diffSelectPanel = new DifficultySelectPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        cardsPanel.add(splashPanel, SCREEN_SPLASH);
        cardsPanel.add(mainMenuPanel, SCREEN_MAIN_MENU);
        cardsPanel.add(storyPanel, SCREEN_STORY);
        cardsPanel.add(charSelectPanel, SCREEN_CHAR_SELECT);
        cardsPanel.add(diffSelectPanel, SCREEN_DIFF_SELECT);
        cardsPanel.add(leaderboardPanel, SCREEN_LEADERBOARD);
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

    public void showSplashScreen() {
        splashPanel.resetAnimation();
        showScreen(SCREEN_SPLASH);
    }

    public void showMainMenu() {
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
}

