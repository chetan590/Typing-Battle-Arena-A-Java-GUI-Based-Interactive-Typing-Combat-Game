package com.typingbattle.engine;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pure Java procedural sound synthesizer using javax.sound.sampled.
 * Generates arcade-quality combat sound effects and rhythmic synth music
 * without relying on external audio files or native libraries.
 */
public class SoundEngine {

    private static SoundEngine instance;
    private final ExecutorService sfxPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "SoundFX-Thread");
        t.setDaemon(true);
        return t;
    });

    private boolean muted = false;
    private Thread musicThread = null;
    private volatile boolean musicRunning = false;
    private static final float SAMPLE_RATE = 22050f;

    private SoundEngine() {}
    private SoundEngine() {
        String envMute = System.getenv("TYPING_BATTLE_MUTE");
        if (envMute != null && ("true".equalsIgnoreCase(envMute) || "1".equals(envMute))) {
            this.muted = true;
        }
    }

    public static synchronized SoundEngine getInstance() {
        if (instance == null) {
            instance = new SoundEngine();
        }
        return instance;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            stopBattleMusic();
        }
    }

    public void toggleMute() {
        setMuted(!muted);
    }

    /**
     * Plays a synthesized audio buffer asynchronously.
     */
    private void playToneBuffer(byte[] audioData) {
        if (muted) return;
        sfxPool.submit(() -> {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, false, false);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
                AudioInputStream ais = new AudioInputStream(bais, format, audioData.length);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception ignored) {
                // Audio unavailable or busy - gracefully ignore
            }
        });
    }

    /**
     * Mechanical keyboard key click sound.
     */
    public void playKeyClick() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.02); // 20ms
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double decay = 1.0 - ((double) i / samples);
            // High click pop + tiny noise
            double wave = Math.sin(2.0 * Math.PI * 1800 * i / SAMPLE_RATE) * 0.7
                        + (Math.random() * 2.0 - 1.0) * 0.3;
            buffer[i] = (byte) (128 + wave * decay * 50);
        }
        playToneBuffer(buffer);
    }

    /**
     * Melodic positive chime when a word is completed successfully.
     */
    public void playWordComplete() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.16); // 160ms
        byte[] buffer = new byte[samples];
        int half = samples / 2;
        for (int i = 0; i < samples; i++) {
            double freq = (i < half) ? 587.33 : 880.00; // D5 -> A5
            double decay = 1.0 - ((double) (i % half) / half);
            double wave = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buffer[i] = (byte) (128 + wave * decay * 70);
        }
        playToneBuffer(buffer);
    }

    /**
     * Low discordant buzzer when typing error occurs.
     */
    public void playError() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.12);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double decay = 1.0 - ((double) i / samples);
            // Square wave discordant buzzer
            double wave1 = Math.sin(2.0 * Math.PI * 130 * i / SAMPLE_RATE) > 0 ? 1.0 : -1.0;
            double wave2 = Math.sin(2.0 * Math.PI * 165 * i / SAMPLE_RATE) > 0 ? 1.0 : -1.0;
            buffer[i] = (byte) (128 + (wave1 * 0.5 + wave2 * 0.5) * decay * 60);
        }
        playToneBuffer(buffer);
    }

    /**
     * Heavy physical hit impact with bass punch and crunch noise.
     */
    public void playHit() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.18);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double freq = 220.0 * (1.0 - progress * 0.7); // Pitch drops
            double decay = Math.pow(1.0 - progress, 1.8);
            double sine = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            double noise = (Math.random() * 2.0 - 1.0) * 0.5;
            buffer[i] = (byte) (128 + (sine * 0.6 + noise * 0.4) * decay * 95);
        }
        playToneBuffer(buffer);
    }

    /**
     * Swift blade whoosh sound.
     */
    public void playSlash() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.15);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double decay = Math.sin(progress * Math.PI); // envelope
            double freq = 400.0 + progress * 800.0;
            double wave = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE) * 0.4
                        + (Math.random() * 2.0 - 1.0) * 0.6;
            buffer[i] = (byte) (128 + wave * decay * 80);
        }
        playToneBuffer(buffer);
    }

    /**
     * Rising power meter or special attack ready alert.
     */
    public void playSpecialReady() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.35);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double freq = 300.0 + Math.pow(progress, 2) * 1200.0;
            double decay = 1.0 - progress * 0.2;
            double wave = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buffer[i] = (byte) (128 + wave * decay * 85);
        }
        playToneBuffer(buffer);
    }

    /**
     * Massive explosive blast for ultimate/special moves.
     */
    public void playSpecialBlast() {
        if (muted) return;
        int samples = (int) (SAMPLE_RATE * 0.5);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double freq = 120.0 * (1.0 - progress * 0.6);
            double decay = Math.pow(1.0 - progress, 1.2);
            double bass = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            double noise = (Math.random() * 2.0 - 1.0);
            buffer[i] = (byte) (128 + (bass * 0.6 + noise * 0.4) * decay * 110);
        }
        playToneBuffer(buffer);
    }

    /**
     * Heroic victory fanfare arpeggio (C5 -> E5 -> G5 -> C6).
     */
    public void playVictory() {
        if (muted) return;
        sfxPool.submit(() -> {
            try {
                double[] notes = {523.25, 659.25, 783.99, 1046.50};
                for (double note : notes) {
                    if (muted) break;
                    playSingleTone(note, 0.16, 85);
                    Thread.sleep(130);
                }
            } catch (InterruptedException ignored) {}
        });
    }

    /**
     * Defeat gong / cadence.
     */
    public void playDefeat() {
        if (muted) return;
        sfxPool.submit(() -> {
            try {
                double[] notes = {440.00, 392.00, 349.23, 293.66};
                for (double note : notes) {
                    if (muted) break;
                    playSingleTone(note, 0.22, 75);
                    Thread.sleep(180);
                }
            } catch (InterruptedException ignored) {}
        });
    }

    private void playSingleTone(double freq, double durationSec, int volume) {
        int samples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double decay = 1.0 - ((double) i / samples);
            double wave = Math.sin(2.0 * Math.PI * freq * i / SAMPLE_RATE);
            buffer[i] = (byte) (128 + wave * decay * volume);
        }
        playToneBuffer(buffer);
    }

    /**
     * Procedural background battle synthesizer playing an energetic synth arpeggio.
     */
    public synchronized void startBattleMusic() {
        if (musicRunning || muted) return;
        musicRunning = true;
        musicThread = new Thread(() -> {
            // High-octane arcade synth notes: Am pentatonic groove
            double[] bassLine = {110.0, 110.0, 130.81, 146.83, 110.0, 164.81, 146.83, 130.81};
            int step = 0;
            while (musicRunning && !muted) {
                try {
                    double note = bassLine[step % bassLine.length];
                    playSingleTone(note, 0.12, 35); // Gentle volume for background
                    step++;
                    Thread.sleep(175); // ~170 BPM tempo
                } catch (InterruptedException e) {
                    break;
                }
            }
            musicRunning = false;
        }, "BattleMusic-Thread");
        musicThread.setDaemon(true);
        musicThread.start();
    }

    public synchronized void stopBattleMusic() {
        musicRunning = false;
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
    }
}

