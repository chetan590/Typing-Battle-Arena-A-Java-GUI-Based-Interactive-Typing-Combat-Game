package com.typingbattle.model;

/**
 * State and attributes of a combatant in the battle arena.
 */
public class Fighter {

    public enum State {
        IDLE,
        ATTACKING,
        HURT,
        SPECIAL,
        VICTORY,
        DEFEATED
    }

    private final CharacterProfile profile;
    private final boolean isPlayerOne;
    private final int maxHp;
    private int currentHp;
    private double powerMeter; // 0 to 100
    private int comboCount;
    private int highestCombo;

    private State state = State.IDLE;
    private long stateDurationMs = 0;
    private long stateElapsedMs = 0;

    private boolean guarding = false;
    private double x = 0;
    private double y = 0;

    public Fighter(CharacterProfile profile, int maxHp, boolean isPlayerOne) {
        this.profile = profile;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.powerMeter = 0;
        this.comboCount = 0;
        this.highestCombo = 0;
        this.isPlayerOne = isPlayerOne;
    }

    /**
     * Updates animation timers and returns state to IDLE upon completion.
     */
    public void update(long deltaMs) {
        if (state == State.ATTACKING || state == State.HURT || state == State.SPECIAL) {
            stateElapsedMs += deltaMs;
            if (stateElapsedMs >= stateDurationMs) {
                if (currentHp <= 0) {
                    state = State.DEFEATED;
                } else {
                    state = State.IDLE;
                    guarding = false;
                }
            }
        }
    }

    public void takeDamage(int damage) {
        if (state == State.DEFEATED) return;

        int finalDamage = damage;
        if (guarding) {
            finalDamage = (int) Math.max(1, damage * 0.4);
        }

        currentHp = Math.max(0, currentHp - finalDamage);
        comboCount = 0; // Break combo on hit

        if (currentHp <= 0) {
            state = State.DEFEATED;
            stateDurationMs = Long.MAX_VALUE;
        } else {
            setState(State.HURT, 400);
        }
    }

    public void addPower(double amount) {
        powerMeter = Math.min(100.0, powerMeter + amount);
    }

    public boolean canUseSpecial() {
        return powerMeter >= 100.0 && state != State.DEFEATED;
    }

    public void consumeSpecialPower() {
        powerMeter = 0.0;
    }

    public void incrementCombo() {
        comboCount++;
        if (comboCount > highestCombo) {
            highestCombo = comboCount;
        }
    }

    public void resetCombo() {
        comboCount = 0;
    }

    public void setState(State newState, long durationMs) {
        if (this.state == State.DEFEATED && newState != State.IDLE) {
            return; // Stay defeated
        }
        this.state = newState;
        this.stateDurationMs = durationMs;
        this.stateElapsedMs = 0;
    }

    public void triggerAttack() {
        setState(State.ATTACKING, 450);
    }

    public void triggerSpecial() {
        setState(State.SPECIAL, 900);
    }

    public void triggerVictory() {
        setState(State.VICTORY, Long.MAX_VALUE);
    }

    public void triggerDefeat() {
        setState(State.DEFEATED, Long.MAX_VALUE);
    }

    // Getters and Setters
    public CharacterProfile getProfile() { return profile; }
    public boolean isPlayerOne() { return isPlayerOne; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = Math.min(maxHp, Math.max(0, hp)); }
    public double getHpPercentage() { return (double) currentHp / maxHp; }
    public double getPowerMeter() { return powerMeter; }
    public void setPowerMeter(double power) { this.powerMeter = Math.min(100.0, Math.max(0.0, power)); }
    public int getComboCount() { return comboCount; }
    public int getHighestCombo() { return highestCombo; }
    public State getState() { return state; }
    public long getStateElapsedMs() { return stateElapsedMs; }
    public long getStateDurationMs() { return stateDurationMs; }
    public boolean isGuarding() { return guarding; }
    public void setGuarding(boolean guarding) { this.guarding = guarding; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}

