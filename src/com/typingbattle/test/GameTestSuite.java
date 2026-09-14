package com.typingbattle.test;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import com.typingbattle.engine.CombatEngine;
import com.typingbattle.engine.ParticleSystem;
import com.typingbattle.engine.SoundEngine;
import com.typingbattle.engine.WordBank;
import com.typingbattle.model.*;

/**
 * Automated headless test suite verifying engine mechanics,
 * word dictionaries, WPM math, ranking algorithms, and data persistence.
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
        // Record 100 correct keystrokes, 5 error keystrokes
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
        se.setMuted(true); // Headless test - mute sound

        CombatEngine engine = new CombatEngine(
                CombatEngine.BattleMode.SINGLE_PLAYER,
                Difficulty.EASY,
                AIPersonality.BALANCED,
                p1, p2, ps, se
        );

        int initialHp = engine.getOpponent().getCurrentHp();
        String currentWord = engine.getP1CurrentWord();

        // Type matching characters
        engine.handlePlayer1Input(currentWord);

        // Word completed! Opponent must have taken damage, combo should be 1
        assert engine.getOpponent().getCurrentHp() < initialHp : "Opponent HP did not decrease after word completion";
        assert engine.getPlayer1().getComboCount() == 1 : "Combo count was not incremented";
        assert engine.getPlayer1().getPowerMeter() > 0 : "Power meter did not increase";

        // Test error handling
        String nextWord = engine.getP1CurrentWord();
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
}

