package com.typingbattle.engine;

import com.typingbattle.model.CharacterProfile;
import com.typingbattle.model.CharacterType;
import com.typingbattle.model.Difficulty;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages the live state of a networked multiplayer room.
 * Synchronizes player profiles, HP, power gauges, active typing prompts,
 * real-time attack exchanges, match timer, and outcome.
 */
public class MultiplayerSession {

    public enum State {
        LOBBY,       // Waiting for Player 2 to join
        COUNTDOWN,   // 3, 2, 1 fight countdown
        BATTLE,      // Live simultaneous typing combat
        FINISHED     // Match ended with victory/defeat
    }

    private final String roomCode;
    private final int port;
    private final String hostIp;
    private final String joinUrl;

    // Match state
    private volatile State state = State.LOBBY;
    private volatile long battleStartTime = 0;
    private volatile int timeRemainingSeconds = 90;
    private volatile String winnerName = "";
    private volatile String outcomeReason = "";

    // Player 1 (Desktop Host)
    private volatile String p1Name = "Chetan";
    private volatile CharacterType p1Type = CharacterType.NINJA;
    private volatile double p1Hp = 1000.0;
    private volatile double p1MaxHp = 1000.0;
    private volatile double p1Power = 0.0;
    private volatile int p1Score = 0;
    private volatile int p1Combo = 0;
    private volatile int p1WordsCompleted = 0;
    private volatile String p1CurrentWord = "";
    private final Queue<String> p1WordQueue = new ConcurrentLinkedQueue<>();
    private volatile String p1TypedInput = "";
    private volatile boolean p1HasError = false;

    // Player 2 (Remote Client - Mobile / Browser)
    private volatile boolean p2Connected = false;
    private volatile String p2Name = "";
    private volatile CharacterType p2Type = CharacterType.SAMURAI;
    private volatile double p2Hp = 1000.0;
    private volatile double p2MaxHp = 1000.0;
    private volatile double p2Power = 0.0;
    private volatile int p2Score = 0;
    private volatile int p2Combo = 0;
    private volatile int p2WordsCompleted = 0;
    private volatile String p2CurrentWord = "";
    private final Queue<String> p2WordQueue = new ConcurrentLinkedQueue<>();
    private volatile String p2TypedInput = "";
    private volatile boolean p2HasError = false;
    private volatile long p2LastHeartbeat = 0;

    // Event queue for animations (Desktop notifies mobile and vice-versa)
    private final List<String> recentEvents = Collections.synchronizedList(new ArrayList<>());

    public MultiplayerSession(String roomCode, String hostIp, int port, String hostPlayerName, CharacterType hostCombatant) {
        this.roomCode = roomCode;
        this.hostIp = hostIp;
        this.port = port;
        this.p1Name = hostPlayerName;
        this.p1Type = hostCombatant;
        this.joinUrl = "http://" + hostIp + ":" + port + "/join?room=" + roomCode;

        initWordStreams();
    }

    private void initWordStreams() {
        p1CurrentWord = WordBank.getRandomWord(Difficulty.MEDIUM);
        p2CurrentWord = WordBank.getRandomWord(Difficulty.MEDIUM);
        for (int i = 0; i < 5; i++) {
            p1WordQueue.offer(WordBank.getRandomWord(Difficulty.MEDIUM));
            p2WordQueue.offer(WordBank.getRandomWord(Difficulty.MEDIUM));
        }
    }

    public synchronized boolean joinPlayer2(String name, CharacterType type) {
        if (p2Connected && !p2Name.equalsIgnoreCase(name) && state == State.BATTLE) {
            return false; // Room full
        }
        this.p2Name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Challenger";
        this.p2Type = (type != null) ? type : CharacterType.SAMURAI;
        this.p2Connected = true;
        this.p2LastHeartbeat = System.currentTimeMillis();
        addEvent(p2Name + " [" + p2Type.getDisplayName() + "]");
        return true;
    }

    public synchronized void startBattle() {
        this.state = State.BATTLE;
        this.battleStartTime = System.currentTimeMillis();
        this.p1Hp = p1MaxHp;
        this.p2Hp = p2MaxHp;
        this.p1Score = 0;
        this.p2Score = 0;
        this.p1Combo = 0;
        this.p2Combo = 0;
        initWordStreams();
        addEvent("BATTLE STARTED! Clash for the Keyboard Core!");
    }

    /**
     * Executes an attack from Player 1 against Player 2.
     */
    public synchronized void executeP1Attack(int damage, String word) {
        if (state != State.BATTLE) return;

        p2Hp = Math.max(0.0, p2Hp - damage);
        p1Combo++;
        p1WordsCompleted++;
        p1Score += damage * 10 + (p1Combo * 25);
        p1Power = Math.min(100.0, p1Power + 18.0 + word.length() * 1.5);

        // Advance P1 word
        p1CurrentWord = p1WordQueue.poll();
        if (p1CurrentWord == null) p1CurrentWord = WordBank.getRandomWord(Difficulty.MEDIUM);
        p1WordQueue.offer(WordBank.getRandomWord(Difficulty.MEDIUM));
        p1TypedInput = "";
        p1HasError = false;

        addEvent(p1Name + " landed " + word + " dealing " + damage + " DMG!");

        checkBattleOver();
    }

    /**
     * Executes an attack from Player 2 (e.g. from Mobile/Web client) against Player 1.
     */
    public synchronized void executeP2Attack(int damage, String word) {
        if (state != State.BATTLE) return;

        p1Hp = Math.max(0.0, p1Hp - damage);
        p2Combo++;
        p2WordsCompleted++;
        p2Score += damage * 10 + (p2Combo * 25);
        p2Power = Math.min(100.0, p2Power + 18.0 + word.length() * 1.5);

        // Advance P2 word
        p2CurrentWord = p2WordQueue.poll();
        if (p2CurrentWord == null) p2CurrentWord = WordBank.getRandomWord(Difficulty.MEDIUM);
        p2WordQueue.offer(WordBank.getRandomWord(Difficulty.MEDIUM));
        p2TypedInput = "";
        p2HasError = false;

        addEvent(p2Name + " landed " + word + " dealing " + damage + " DMG!");

        checkBattleOver();
    }

    public synchronized void triggerP1Special() {
        if (state != State.BATTLE || p1Power < 100.0) return;
        int specialDmg = 260;
        p1Power = 0.0;
        p2Hp = Math.max(0.0, p2Hp - specialDmg);
        p1Score += 3500;
        addEvent(p1Name + " unleashed SPECIAL ATTACK dealing " + specialDmg + " DMG!");
        checkBattleOver();
    }

    public synchronized void triggerP2Special() {
        if (state != State.BATTLE || p2Power < 100.0) return;
        int specialDmg = 260;
        p2Power = 0.0;
        p1Hp = Math.max(0.0, p1Hp - specialDmg);
        p2Score += 3500;
        addEvent(p2Name + " unleashed SPECIAL ATTACK dealing " + specialDmg + " DMG!");
        checkBattleOver();
    }

    public synchronized void checkBattleOver() {
        if (state != State.BATTLE) return;

        if (p1Hp <= 0 && p2Hp <= 0) {
            state = State.FINISHED;
            winnerName = "DRAW";
            outcomeReason = "MUTUAL KNOCKOUT";
        } else if (p1Hp <= 0) {
            state = State.FINISHED;
            winnerName = p2Name;
            outcomeReason = p2Name + " achieved KO Victory!";
        } else if (p2Hp <= 0) {
            state = State.FINISHED;
            winnerName = p1Name;
            outcomeReason = p1Name + " achieved KO Victory!";
        }
    }

    public void updateTimer(long now) {
        if (state == State.BATTLE) {
            long elapsedSec = (now - battleStartTime) / 1000;
            timeRemainingSeconds = Math.max(0, 90 - (int) elapsedSec);
            if (timeRemainingSeconds <= 0 && state == State.BATTLE) {
                // Time Over: Highest HP wins
                state = State.FINISHED;
                if (p1Hp > p2Hp) {
                    winnerName = p1Name;
                    outcomeReason = "TIME OVER - " + p1Name + " won by Health Advantage!";
                } else if (p2Hp > p1Hp) {
                    winnerName = p2Name;
                    outcomeReason = "TIME OVER - " + p2Name + " won by Health Advantage!";
                } else {
                    winnerName = "DRAW";
                    outcomeReason = "TIME OVER - Even Health Draw!";
                }
            }
        }
    }

    public synchronized void handleP2Disconnect() {
        p2Connected = false;
        addEvent("Player 2 disconnected.");
    }

    public synchronized void recordHeartbeat() {
        p2LastHeartbeat = System.currentTimeMillis();
    }

    public boolean isP2TimedOut() {
        return p2Connected && (System.currentTimeMillis() - p2LastHeartbeat > 10000);
    }

    private void addEvent(String evt) {
        recentEvents.add(evt);
        if (recentEvents.size() > 8) {
            recentEvents.remove(0);
        }
    }

    // Getters and setters
    public String getRoomCode() { return roomCode; }
    public String getJoinUrl() { return joinUrl; }
    public String getHostIp() { return hostIp; }
    public int getPort() { return port; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public String getP1Name() { return p1Name; }
    public CharacterType getP1Type() { return p1Type; }
    public double getP1Hp() { return p1Hp; }
    public double getP1MaxHp() { return p1MaxHp; }
    public double getP1Power() { return p1Power; }
    public int getP1Score() { return p1Score; }
    public int getP1Combo() { return p1Combo; }
    public int getP1WordsCompleted() { return p1WordsCompleted; }
    public String getP1CurrentWord() { return p1CurrentWord; }
    public String getP1TypedInput() { return p1TypedInput; }
    public void setP1TypedInput(String input) { this.p1TypedInput = input; }
    public boolean isP1HasError() { return p1HasError; }
    public void setP1HasError(boolean err) { this.p1HasError = err; }
    public void setP1Combo(int combo) { this.p1Combo = combo; }

    public boolean isP2Connected() { return p2Connected; }
    public String getP2Name() { return p2Name; }
    public CharacterType getP2Type() { return p2Type; }
    public double getP2Hp() { return p2Hp; }
    public double getP2MaxHp() { return p2MaxHp; }
    public double getP2Power() { return p2Power; }
    public int getP2Score() { return p2Score; }
    public int getP2Combo() { return p2Combo; }
    public int getP2WordsCompleted() { return p2WordsCompleted; }
    public String getP2CurrentWord() { return p2CurrentWord; }
    public String getP2TypedInput() { return p2TypedInput; }
    public void setP2TypedInput(String input) { this.p2TypedInput = input; }
    public boolean isP2HasError() { return p2HasError; }
    public void setP2HasError(boolean err) { this.p2HasError = err; }
    public void setP2Combo(int combo) { this.p2Combo = combo; }

    public int getTimeRemainingSeconds() { return timeRemainingSeconds; }
    public String getWinnerName() { return winnerName; }
    public String getOutcomeReason() { return outcomeReason; }
    public List<String> getRecentEvents() { return new ArrayList<>(recentEvents); }
}
