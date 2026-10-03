package com.typingbattle.test;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import com.typingbattle.engine.CombatEngine;
import com.typingbattle.engine.MultiplayerSession;
import com.typingbattle.engine.ParticleSystem;
import com.typingbattle.engine.SoundEngine;
import com.typingbattle.engine.WordBank;
import com.typingbattle.model.*;

/**
 * Automated headless test suite verifying engine mechanics,
 * word dictionaries, WPM math, ranking algorithms, settings, session, and networking.
 */
public class GameTestSuite {

    public static void main(String[] args) {
        System.out.println(">>> RUNNING TYPING BATTLE ARENA TEST SUITE <<<\n");
        int passed = 0;
        int failed = 0;

        try {
            testWordBank();
            System.out.println("  [PASS] WordBank dictionary validation");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] WordBank dictionary validation: " + t.getMessage());
            failed++;
        }

        try {
            testMatchStatsAndRanks();
            System.out.println("  [PASS] MatchStats & Rank calculation algorithms");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] MatchStats & Rank calculation: " + t.getMessage());
            failed++;
        }

        try {
            testCombatEngineMechanics();
            System.out.println("  [PASS] CombatEngine input validation, combos & damage");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] CombatEngine mechanics: " + t.getMessage());
            failed++;
        }

        try {
            testDataPersistence();
            System.out.println("  [PASS] DataManager records & Story unlocks");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] DataManager persistence: " + t.getMessage());
            failed++;
        }

        try {
            testSoundEngine();
            System.out.println("  [PASS] SoundEngine procedural tone synthesizer");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] SoundEngine synthesizer: " + t.getMessage());
            failed++;
        }

        try {
            testGameSettingsAndWordFiltering();
            System.out.println("  [PASS] GameSettings & WordBank length filtering (3, 4, 5, 6, Mixed)");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] GameSettings & WordBank filter: " + t.getMessage());
            failed++;
        }

        try {
            testSessionManagerAndLeaderboardSorting();
            System.out.println("  [PASS] SessionManager pilot info & dynamic score-sorted leaderboard");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] SessionManager & Leaderboard: " + t.getMessage());
            failed++;
        }

        try {
            testStoryActsProgression();
            System.out.println("  [PASS] Expanded Campaign Story (Acts I through VIII progression)");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] Story Acts Progression: " + t.getMessage());
            failed++;
        }

        try {
            testMultiplayerSessionState();
            System.out.println("  [PASS] Cross-device MultiplayerSession room creation & synchronization");
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] MultiplayerSession state: " + t.getMessage());
            failed++;
        }

        System.out.println("\n>>> TEST SUITE SUMMARY: " + passed + " PASSED, " + failed + " FAILED <<<");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testWordBank() {
        for (Difficulty d : Difficulty.values()) {
            String word = WordBank.getRandomWord(d);
            assert word != null && !word.isEmpty() : "Null/empty word for difficulty " + d;
            assert word.length() >= d.getMinWordLength() - 1 : "Word too short for difficulty " + d + ": " + word;
        }
        String special = WordBank.getSpecialWord();
        assert special != null && !special.isEmpty() : "Special word must not be null";
    }

    private static void testMatchStatsAndRanks() {
        MatchStats stats = new MatchStats();
        for (int i = 0; i < 100; i++) stats.recordKeystroke(true);
        for (int i = 0; i < 5; i++) stats.recordKeystroke(false);

        double acc = stats.getAccuracy();
        assert acc > 90.0 && acc < 100.0 : "Accuracy calculation incorrect: " + acc;

        stats.recordWord(true, 5, 8);
        stats.completeMatch(true);

        MatchStats.Rank rank = stats.calculateRank();
        assert rank != null : "Rank should not be null";

        String tip = stats.getRecommendation();
        assert tip != null && tip.startsWith("Recommendation:") : "Recommendation tip formatted incorrectly";
    }

    private static void testCombatEngineMechanics() {
        CharacterProfile p1 = CharacterProfile.getProfile(CharacterType.NINJA);
        CharacterProfile p2 = CharacterProfile.getProfile(CharacterType.SAMURAI);
        ParticleSystem ps = new ParticleSystem();
        SoundEngine se = SoundEngine.getInstance();
        se.setMuted(true);

        CombatEngine engine = new CombatEngine(
                CombatEngine.BattleMode.SINGLE_PLAYER,
                Difficulty.EASY,
                AIPersonality.BALANCED,
                p1, p2, ps, se
        );

        int initialHp = engine.getOpponent().getCurrentHp();
        String currentWord = engine.getP1CurrentWord();

        engine.handlePlayer1Input(currentWord);

        assert engine.getOpponent().getCurrentHp() < initialHp : "Opponent HP did not decrease after word completion";
        assert engine.getPlayer1().getComboCount() == 1 : "Combo count was not incremented";
        assert engine.getPlayer1().getPowerMeter() > 0 : "Power meter did not increase";

        engine.handlePlayer1Input("ZZZ_INVALID");
        assert engine.isP1HasError() : "Engine should flag error on mistyped prefix";
        assert engine.getPlayer1().getComboCount() == 0 : "Combo must reset on mistake";
    }

    private static void testDataPersistence() {
        DataManager dm = DataManager.getInstance();
        int initialUnlocked = dm.getStoryUnlockedStage();
        assert initialUnlocked >= 1 : "Initial unlocked stage must be >= 1";

        dm.unlockStoryStage(3);
        assert dm.getStoryUnlockedStage() >= 3 : "Story unlock stage failed to update";

        MatchRecord record = new MatchRecord(
                System.currentTimeMillis(),
                "Single Player Test",
                "Ninja",
                "Samurai",
                55,
                94.5,
                "A RANK",
                true,
                7
        );
        dm.recordMatch(record);
        assert dm.getRecords().size() > 0 : "Record list must contain saved matches";
        assert dm.getBestWpm() >= 55 : "Best WPM record failed to update";
    }

    private static void testSoundEngine() {
        SoundEngine se = SoundEngine.getInstance();
        se.setMuted(true);
        se.playKeyClick();
        se.playWordComplete();
        se.playError();
        se.playHit();
        se.playSlash();
        se.playSpecialBlast();
    }

    private static void testGameSettingsAndWordFiltering() {
        GameSettings settings = GameSettings.getInstance();
        settings.setAppearanceSpeed(GameSettings.WordAppearanceSpeed.FAST);
        assert settings.getAppearanceSpeed() == GameSettings.WordAppearanceSpeed.FAST : "Word appearance speed failed";

        settings.setAttackSpeed(GameSettings.OpponentAttackSpeed.SLOW);
        assert settings.getAttackSpeed() == GameSettings.OpponentAttackSpeed.SLOW : "Opponent attack speed failed";

        settings.setVolumePercent(75);
        assert settings.getVolumePercent() == 75 : "Volume setting failed";

        // Test Word Length Filter strictly
        settings.setWordLengthFilter(GameSettings.WordLengthFilter.LENGTH_3);
        for (int i = 0; i < 20; i++) {
            String word = WordBank.getRandomWord(Difficulty.MEDIUM);
            assert word.length() == 3 : "Expected 3-letter word, got: " + word;
        }

        settings.setWordLengthFilter(GameSettings.WordLengthFilter.LENGTH_4);
        for (int i = 0; i < 20; i++) {
            String word = WordBank.getRandomWord(Difficulty.MEDIUM);
            assert word.length() == 4 : "Expected 4-letter word, got: " + word;
        }

        settings.setWordLengthFilter(GameSettings.WordLengthFilter.LENGTH_5);
        for (int i = 0; i < 20; i++) {
            String word = WordBank.getRandomWord(Difficulty.MEDIUM);
            assert word.length() == 5 : "Expected 5-letter word, got: " + word;
        }

        settings.setWordLengthFilter(GameSettings.WordLengthFilter.LENGTH_6);
        for (int i = 0; i < 20; i++) {
            String word = WordBank.getRandomWord(Difficulty.MEDIUM);
            assert word.length() == 6 : "Expected 6-letter word, got: " + word;
        }

        settings.setWordLengthFilter(GameSettings.WordLengthFilter.MIXED);
    }

    private static void testSessionManagerAndLeaderboardSorting() {
        SessionManager session = SessionManager.getInstance();
        session.setPlayerName("Chetan");
        session.setPreferredCombatant(CharacterType.SAMURAI);
        session.setLoggedIn(true);

        assert "Chetan".equals(session.getPlayerName()) : "Player name mismatch";
        assert session.getPreferredCombatant() == CharacterType.SAMURAI : "Combatant mismatch";
        assert session.getPlayerDisplayInfo().contains("Chetan") : "Player info must include name";

        DataManager dm = DataManager.getInstance();
        dm.recordMatch(new MatchRecord(System.currentTimeMillis(), "Chetan", "Samurai", "Robot",
                "Hard", 70, 98.0, "S RANK", true, 10, 8500, 40, 42, 2, "Hard", 420));
        dm.recordMatch(new MatchRecord(System.currentTimeMillis(), "Rival", "Ninja", "Mage",
                "Hard", 60, 95.0, "A RANK", true, 8, 9200, 35, 36, 1, "Hard", 450));

        java.util.List<MatchRecord> sorted = dm.getLeaderboardSortedByScore();
        assert sorted.size() >= 2 : "Sorted leaderboard should have at least 2 records";
        for (int i = 0; i < sorted.size() - 1; i++) {
            assert sorted.get(i).getScore() >= sorted.get(i + 1).getScore() :
                    "Leaderboard not dynamically sorted descending: " + sorted.get(i).getScore() + " < " + sorted.get(i + 1).getScore();
        }
    }

    private static void testStoryActsProgression() {
        StoryStage[] stages = StoryStage.getAllStages();
        assert stages.length == 8 : "Campaign must contain 8 continuous acts, found: " + stages.length;
        for (int i = 0; i < stages.length; i++) {
            StoryStage st = stages[i];
            assert st.getStageNumber() == i + 1 : "Stage index mismatch for: " + st.getStageTitle();
            assert st.getStageTitle() != null && !st.getStageTitle().isEmpty() : "Stage title empty";
            assert st.getIntroDialogue() != null && !st.getIntroDialogue().isEmpty() : "Dialogue missing for: " + st.getStageTitle();
        }
    }

    private static void testMultiplayerSessionState() {
        MultiplayerSession session = new MultiplayerSession("A7K92", "127.0.0.1", 8080, "Chetan", CharacterType.NINJA);
        assert "A7K92".equals(session.getRoomCode()) : "Room code mismatch";
        assert !session.isP2Connected() : "P2 should not be connected yet";

        boolean joined = session.joinPlayer2("GuestPilot", CharacterType.SAMURAI);
        assert joined && session.isP2Connected() : "P2 should be connected";

        session.startBattle();
        assert session.getState() == MultiplayerSession.State.BATTLE : "Session should be in BATTLE state";
        assert session.getP1CurrentWord() != null && !session.getP1CurrentWord().isEmpty() : "P1 word must be non-empty";

        session.executeP1Attack(25, "combat");
        assert session.getP2Hp() < 1000.0 : "P2 HP should decrease after P1 attack";
    }
}
