package com.typingbattle.engine;

import com.typingbattle.model.*;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Core battle engine simulating real-time typing combat, AI behavior,
 * damage mechanics, combo multipliers, and visual/audio event dispatches.
 */
public class CombatEngine {

    public enum BattleMode {
        SINGLE_PLAYER,
        TWO_PLAYER,
        STORY_MODE
    }

    private final BattleMode mode;
    private final Difficulty difficulty;
    private final AIPersonality aiPersonality;
    private final Fighter player1;
    private final Fighter opponent; // AI or Player 2
    private final ParticleSystem particleSystem;
    private final SoundEngine soundEngine;
    private final MatchStats player1Stats;
    private final MatchStats player2Stats; // used in Two Player mode

    // Word queues
    private String p1CurrentWord;
    private final Queue<String> p1UpcomingWords = new LinkedList<>();
    private String p1TypedInput = "";
    private boolean p1HasError = false;

    private String p2CurrentWord;
    private final Queue<String> p2UpcomingWords = new LinkedList<>();
    private String p2TypedInput = "";
    private boolean p2HasError = false;

    // AI timing simulation
    private long aiNextCharTimeMs = 0;
    private int aiCharIndex = 0;
    private boolean aiEnraged = false;

    // Match status
    private boolean matchOver = false;
    private boolean player1Victorious = false;

    public CombatEngine(BattleMode mode, Difficulty difficulty, AIPersonality aiPersonality,
                        CharacterProfile p1Profile, CharacterProfile p2Profile,
                        ParticleSystem particleSystem, SoundEngine soundEngine) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.aiPersonality = aiPersonality;
        this.particleSystem = particleSystem;
        this.soundEngine = soundEngine;

        int startingHp = (difficulty == Difficulty.INTENSE) ? 1200 : 1000;
        this.player1 = new Fighter(p1Profile, startingHp, true);
        this.opponent = new Fighter(p2Profile, startingHp, false);

        this.player1Stats = new MatchStats();
        this.player2Stats = (mode == BattleMode.TWO_PLAYER) ? new MatchStats() : null;

        initWordQueues();
        scheduleNextAiKeystroke();
    }

    private void initWordQueues() {
        p1CurrentWord = WordBank.getRandomWord(difficulty);
        p2CurrentWord = WordBank.getRandomWord(difficulty);

        for (int i = 0; i < 4; i++) {
            p1UpcomingWords.offer(WordBank.getRandomWord(difficulty));
            p2UpcomingWords.offer(WordBank.getRandomWord(difficulty));
        }
    }

    /**
     * Main simulation tick called from the UI game loop (~60 FPS).
     */
    public void update(long deltaMs) {
        if (matchOver) return;

        player1.update(deltaMs);
        opponent.update(deltaMs);
        particleSystem.update();

        // Check HP termination
        if (player1.getCurrentHp() <= 0 || opponent.getCurrentHp() <= 0) {
            endMatch();
            return;
        }

        // Process AI if in single player / story mode
        if (mode != BattleMode.TWO_PLAYER) {
            updateAi(deltaMs);
        }
    }

    /**
     * Simulates AI typing behavior matching selected personality and difficulty.
     */
    private void updateAi(long deltaMs) {
        long now = System.currentTimeMillis();

        // Check Boss Phase
        if (aiPersonality == AIPersonality.BOSS_AI && !aiEnraged && opponent.getHpPercentage() <= 0.50) {
            aiEnraged = true;
            particleSystem.spawnFloatingText(opponent.getX(), opponent.getY() - 40,
                    "PHASE 2: CORE OVERDRIVE!", new Color(255, 60, 60), new Font("SansSerif", Font.BOLD, 18));
            particleSystem.spawnSpecialBlast(opponent.getX(), opponent.getY(), new Color(255, 50, 50));
            soundEngine.playSpecialReady();
        }

        if (now < aiNextCharTimeMs) {
            return;
        }

        // AI Typing Step
        if (aiCharIndex < p2CurrentWord.length()) {
            // Chance of AI mistake based on difficulty & personality
            double errRate = difficulty.getAiErrorRate() * aiPersonality.getErrorChance() * (aiEnraged ? 0.5 : 1.0);
            if (Math.random() < errRate) {
                // Mistake pause
                aiNextCharTimeMs = now + 250;
                return;
            }

            aiCharIndex++;
            scheduleNextAiKeystroke();
        } else {
            // AI completed the word! Launch attack
            performAiAttack();
            p2CurrentWord = p2UpcomingWords.poll();
            p2UpcomingWords.offer(WordBank.getRandomWord(difficulty));
            aiCharIndex = 0;
            scheduleNextAiKeystroke();
        }
    }

    private void scheduleNextAiKeystroke() {
        int minWpm = difficulty.getMinAiWpm();
        int maxWpm = difficulty.getMaxAiWpm();
        int baseWpm = minWpm + (int) (Math.random() * (maxWpm - minWpm + 1));

        double multiplier = aiPersonality.getSpeedModifier();
        if (aiEnraged) multiplier *= 1.35;

        int finalWpm = (int) (baseWpm * multiplier);
        // Average ms per character = 60,000 / (WPM * 5)
        int msPerChar = Math.max(35, 60000 / Math.max(1, finalWpm * 5));
        // Add realistic human jitter (+/- 25%)
        int jitter = (int) ((Math.random() - 0.5) * msPerChar * 0.5);
        aiNextCharTimeMs = System.currentTimeMillis() + Math.max(20, msPerChar + jitter);
    }

    private void performAiAttack() {
        if (matchOver) return;

        opponent.triggerAttack();
        soundEngine.playHit();
        particleSystem.spawnSlashEffect(player1.getX(), player1.getY(), opponent.getProfile().getPrimaryColor());

        // Calculate damage
        int baseDmg = (int) (p2CurrentWord.length() * 15 * opponent.getProfile().getAttackMultiplier()
                * difficulty.getAiDamageScale() * aiPersonality.getDamageModifier());

        // Check if opponent can unleash Special Attack
        opponent.addPower(18.0);
        if (opponent.canUseSpecial()) {
            opponent.triggerSpecial();
            opponent.consumeSpecialPower();
            baseDmg += 140;
            soundEngine.playSpecialBlast();
            particleSystem.spawnSpecialBlast(player1.getX(), player1.getY(), opponent.getProfile().getPrimaryColor());
            particleSystem.spawnFloatingText(opponent.getX(), opponent.getY() - 45,
                    opponent.getProfile().getSpecialAttackName() + "!", Color.YELLOW, new Font("SansSerif", Font.BOLD, 18));
        }

        player1.takeDamage(baseDmg);
        particleSystem.spawnHitSparks(player1.getX(), player1.getY(), Color.RED, 12);
        particleSystem.spawnFloatingText(player1.getX(), player1.getY() - 25,
                "-" + baseDmg, Color.RED, new Font("SansSerif", Font.BOLD, 16));
        particleSystem.triggerScreenShake(6.0);
    }

    /**
     * Handles real-time input change from Player 1's text box.
     */
    public synchronized void handlePlayer1Input(String input) {
        if (matchOver) return;

        p1TypedInput = input;
        int inputLen = input.length();

        if (inputLen == 0) {
            p1HasError = false;
            return;
        }

        // Check if prefix matches
        if (p1CurrentWord.startsWith(input)) {
            p1HasError = false;
            soundEngine.playKeyClick();
            player1Stats.recordKeystroke(true);

            // Complete word check
            if (input.equals(p1CurrentWord)) {
                executePlayer1WordSuccess();
            }
        } else {
            p1HasError = true;
            soundEngine.playError();
            player1Stats.recordKeystroke(false);
            player1.resetCombo();
        }
    }

    private void executePlayer1WordSuccess() {
        player1.incrementCombo();
        soundEngine.playWordComplete();
        player1.triggerAttack();

        int combo = player1.getComboCount();
        player1Stats.recordWord(true, p1CurrentWord.length(), combo);

        // Calculate combo multiplier
        double comboMult = 1.0;
        String comboText = null;
        if (combo >= 10) {
            comboMult = 2.0;
            comboText = "ULTIMATE COMBO x" + combo + "!";
        } else if (combo >= 5) {
            comboMult = 1.5;
            comboText = "SUPER COMBO x" + combo + "!";
        } else if (combo >= 3) {
            comboMult = 1.25;
            comboText = "COMBO x" + combo + "!";
        }

        if (comboText != null) {
            particleSystem.spawnFloatingText(player1.getX(), player1.getY() - 40,
                    comboText, Color.CYAN, new Font("SansSerif", Font.BOLD, 17));
        }

        // Damage calculation
        int damage = (int) (p1CurrentWord.length() * 18
                * player1.getProfile().getAttackMultiplier()
                * difficulty.getPlayerDamageScale()
                * comboMult);

        opponent.takeDamage(damage);
        player1Stats.recordDamage(damage);

        // Visual effects
        particleSystem.spawnHitSparks(opponent.getX(), opponent.getY(), player1.getProfile().getPrimaryColor(), 15);
        particleSystem.spawnSlashEffect(opponent.getX(), opponent.getY(), player1.getProfile().getPrimaryColor());
        particleSystem.spawnFloatingText(opponent.getX(), opponent.getY() - 25,
                "-" + damage, Color.GREEN, new Font("SansSerif", Font.BOLD, 17));
        soundEngine.playSlash();
        particleSystem.triggerScreenShake(4.0);

        // Charge power meter
        double powerGain = 16.0 + (p1CurrentWord.length() * 1.5);
        player1.addPower(powerGain);
        if (player1.canUseSpecial()) {
            soundEngine.playSpecialReady();
            particleSystem.spawnFloatingText(player1.getX(), player1.getY() - 55,
                    "SPECIAL READY! [Press Enter / Button]", Color.YELLOW, new Font("SansSerif", Font.BOLD, 15));
        }

        // Advance to next word
        p1TypedInput = "";
        p1HasError = false;
        p1CurrentWord = p1UpcomingWords.poll();
        p1UpcomingWords.offer(WordBank.getRandomWord(difficulty));
    }

    /**
     * Executes Player 1 Special Attack if power meter is 100%.
     */
    public synchronized boolean triggerPlayer1Special() {
        if (matchOver || !player1.canUseSpecial()) {
            return false;
        }

        player1.triggerSpecial();
        player1.consumeSpecialPower();
        player1Stats.recordSpecialAttack();

        int specialDmg = (int) (260 * player1.getProfile().getAttackMultiplier() * difficulty.getPlayerDamageScale());
        opponent.takeDamage(specialDmg);
        player1Stats.recordDamage(specialDmg);

        soundEngine.playSpecialBlast();
        particleSystem.spawnSpecialBlast(opponent.getX(), opponent.getY(), player1.getProfile().getPrimaryColor());
        particleSystem.spawnFloatingText(opponent.getX(), opponent.getY() - 45,
                "★ " + player1.getProfile().getSpecialAttackName().toUpperCase() + " ★ -" + specialDmg,
                Color.ORANGE, new Font("SansSerif", Font.BOLD, 19));

        return true;
    }

    /**
     * Two Player Mode: Handles Player 2's input.
     */
    public synchronized void handlePlayer2Input(String input) {
        if (matchOver || mode != BattleMode.TWO_PLAYER) return;

        p2TypedInput = input;
        int inputLen = input.length();

        if (inputLen == 0) {
            p2HasError = false;
            return;
        }

        if (p2CurrentWord.startsWith(input)) {
            p2HasError = false;
            soundEngine.playKeyClick();
            if (player2Stats != null) player2Stats.recordKeystroke(true);

            if (input.equals(p2CurrentWord)) {
                executePlayer2WordSuccess();
            }
        } else {
            p2HasError = true;
            soundEngine.playError();
            if (player2Stats != null) player2Stats.recordKeystroke(false);
            opponent.resetCombo();
        }
    }

    private void executePlayer2WordSuccess() {
        opponent.incrementCombo();
        soundEngine.playWordComplete();
        opponent.triggerAttack();

        int combo = opponent.getComboCount();
        if (player2Stats != null) player2Stats.recordWord(true, p2CurrentWord.length(), combo);

        double comboMult = 1.0;
        if (combo >= 5) comboMult = 1.5;
        else if (combo >= 3) comboMult = 1.25;

        int damage = (int) (p2CurrentWord.length() * 18 * opponent.getProfile().getAttackMultiplier() * comboMult);
        player1.takeDamage(damage);
        if (player2Stats != null) player2Stats.recordDamage(damage);

        particleSystem.spawnHitSparks(player1.getX(), player1.getY(), opponent.getProfile().getPrimaryColor(), 15);
        particleSystem.spawnSlashEffect(player1.getX(), player1.getY(), opponent.getProfile().getPrimaryColor());
        particleSystem.spawnFloatingText(player1.getX(), player1.getY() - 25, "-" + damage, Color.GREEN, new Font("SansSerif", Font.BOLD, 17));
        soundEngine.playSlash();
        particleSystem.triggerScreenShake(4.0);

        opponent.addPower(18.0);
        if (opponent.canUseSpecial()) {
            soundEngine.playSpecialReady();
        }

        p2TypedInput = "";
        p2HasError = false;
        p2CurrentWord = p2UpcomingWords.poll();
        p2UpcomingWords.offer(WordBank.getRandomWord(difficulty));
    }

    public synchronized boolean triggerPlayer2Special() {
        if (matchOver || mode != BattleMode.TWO_PLAYER || !opponent.canUseSpecial()) return false;

        opponent.triggerSpecial();
        opponent.consumeSpecialPower();
        if (player2Stats != null) player2Stats.recordSpecialAttack();

        int specialDmg = (int) (260 * opponent.getProfile().getAttackMultiplier());
        player1.takeDamage(specialDmg);
        if (player2Stats != null) player2Stats.recordDamage(specialDmg);

        soundEngine.playSpecialBlast();
        particleSystem.spawnSpecialBlast(player1.getX(), player1.getY(), opponent.getProfile().getPrimaryColor());
        particleSystem.spawnFloatingText(player1.getX(), player1.getY() - 45,
                "★ " + opponent.getProfile().getSpecialAttackName().toUpperCase() + " ★ -" + specialDmg,
                Color.ORANGE, new Font("SansSerif", Font.BOLD, 19));

        return true;
    }

    private void endMatch() {
        matchOver = true;
        player1Victorious = player1.getCurrentHp() > 0;

        if (player1Victorious) {
            player1.triggerVictory();
            opponent.triggerDefeat();
            soundEngine.playVictory();
            player1Stats.completeMatch(true);
            if (player2Stats != null) player2Stats.completeMatch(false);
        } else {
            player1.triggerDefeat();
            opponent.triggerVictory();
            soundEngine.playDefeat();
            player1Stats.completeMatch(false);
            if (player2Stats != null) player2Stats.completeMatch(true);
        }
    }

    // Getters
    public BattleMode getMode() { return mode; }
    public Difficulty getDifficulty() { return difficulty; }
    public AIPersonality getAiPersonality() { return aiPersonality; }
    public Fighter getPlayer1() { return player1; }
    public Fighter getOpponent() { return opponent; }
    public ParticleSystem getParticleSystem() { return particleSystem; }
    public SoundEngine getSoundEngine() { return soundEngine; }
    public MatchStats getPlayer1Stats() { return player1Stats; }
    public MatchStats getPlayer2Stats() { return player2Stats; }
    public String getP1CurrentWord() { return p1CurrentWord; }
    public Queue<String> getP1UpcomingWords() { return p1UpcomingWords; }
    public String getP1TypedInput() { return p1TypedInput; }
    public boolean isP1HasError() { return p1HasError; }
    public String getP2CurrentWord() { return p2CurrentWord; }
    public Queue<String> getP2UpcomingWords() { return p2UpcomingWords; }
    public String getP2TypedInput() { return p2TypedInput; }
    public boolean isP2HasError() { return p2HasError; }
    public int getAiCharIndex() { return aiCharIndex; }
    public boolean isMatchOver() { return matchOver; }
    public boolean isPlayer1Victorious() { return player1Victorious; }
}

