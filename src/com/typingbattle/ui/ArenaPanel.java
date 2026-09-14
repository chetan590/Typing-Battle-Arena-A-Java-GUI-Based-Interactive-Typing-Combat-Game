package com.typingbattle.ui;

import com.typingbattle.engine.*;
import com.typingbattle.model.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Main combat arena panel for Single Player and Story Mode battles.
 * Features real-time typing feedback, dynamic health bars, power gauges,
 * character animation canvas, particle effects, and sound synthesizer integration.
 */
@SuppressWarnings("serial")
public class ArenaPanel extends JPanel {

    private final ScreenManager screenManager;
    private final CombatEngine engine;
    private final StoryStage storyStage;
    private final String arenaTheme;
    private final ParticleSystem particleSystem;
    private final SoundEngine soundEngine;

    // Smooth animated health values
    private double p1DisplayHp;
    private double p2DisplayHp;
    private double p1DisplayPower = 0;

    // UI Components
    private final JTextField typingInput;
    private final JButton specialAttackBtn;
    private final Timer gameLoopTimer;
    private long lastFrameTime;
    private boolean isPaused = false;
    private long matchOverTime = 0;

    public ArenaPanel(ScreenManager screenManager, CombatEngine.BattleMode mode,
                      Difficulty difficulty, AIPersonality personality,
                      CharacterProfile playerProfile, CharacterProfile opponentProfile,
                      String arenaTheme, StoryStage storyStage) {
        this.screenManager = screenManager;
        this.storyStage = storyStage;
        this.arenaTheme = arenaTheme != null ? arenaTheme : "CYBERPUNK";
        this.particleSystem = new ParticleSystem();
        this.soundEngine = SoundEngine.getInstance();

        this.engine = new CombatEngine(mode, difficulty, personality, playerProfile, opponentProfile,
                particleSystem, soundEngine);

        this.p1DisplayHp = engine.getPlayer1().getMaxHp();
        this.p2DisplayHp = engine.getOpponent().getMaxHp();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Top HUD: Health bars, names, combo counter, pause button
        JPanel topHud = createTopHud();
        add(topHud, BorderLayout.NORTH);

        // Bottom Controls: Typing arena box & special attack trigger
        JPanel bottomControls = createBottomControls();
        add(bottomControls, BorderLayout.SOUTH);

        // Input text listener
        typingInput = new JTextField();
        typingInput.setFont(UITheme.FONT_TYPING);
        typingInput.setBackground(new Color(20, 26, 40));
        typingInput.setForeground(Color.WHITE);
        typingInput.setCaretColor(UITheme.ACCENT_CYAN);
        typingInput.setHorizontalAlignment(JTextField.CENTER);
        typingInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        typingInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { handleInputChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { handleInputChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { handleInputChanged(); }
        });

        typingInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Check if player has special attack ready
                    if (engine.getPlayer1().canUseSpecial()) {
                        engine.triggerPlayer1Special();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });

        // Special Attack Button
        specialAttackBtn = UITheme.createStyledButton("⚡ UNLEASH SPECIAL [ENTER]", UITheme.ACCENT_AMBER, 260, 48);
        specialAttackBtn.setEnabled(false);
        specialAttackBtn.addActionListener(e -> {
            engine.triggerPlayer1Special();
            typingInput.requestFocusInWindow();
        });

        JPanel inputWrap = new JPanel(new BorderLayout(15, 0));
        inputWrap.setOpaque(false);
        inputWrap.add(typingInput, BorderLayout.CENTER);
        inputWrap.add(specialAttackBtn, BorderLayout.EAST);

        bottomControls.add(inputWrap, BorderLayout.SOUTH);

        // 60 FPS Game Loop Timer
        lastFrameTime = System.currentTimeMillis();
        gameLoopTimer = new Timer(16, e -> gameTick());
    }

    public void startBattle() {
        soundEngine.startBattleMusic();
        gameLoopTimer.start();
        SwingUtilities.invokeLater(typingInput::requestFocusInWindow);
    }

    public void cleanup() {
        gameLoopTimer.stop();
        soundEngine.stopBattleMusic();
        particleSystem.clear();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            typingInput.requestFocusInWindow();
        }
        repaint();
    }

    private void handleInputChanged() {
        if (isPaused || engine.isMatchOver()) return;
        String text = typingInput.getText().trim();
        engine.handlePlayer1Input(text);

        // Clear input field if word was finished
        if (engine.getP1TypedInput().isEmpty() && text.length() > 0) {
            SwingUtilities.invokeLater(() -> typingInput.setText(""));
        }

        // Update special attack button state
        specialAttackBtn.setEnabled(engine.getPlayer1().canUseSpecial());
    }

    private void gameTick() {
        if (isPaused) return;

        long now = System.currentTimeMillis();
        long delta = now - lastFrameTime;
        lastFrameTime = now;

        engine.update(delta);

        // Smooth interpolate health bars
        double targetP1Hp = engine.getPlayer1().getCurrentHp();
        double targetP2Hp = engine.getOpponent().getCurrentHp();
        p1DisplayHp += (targetP1Hp - p1DisplayHp) * 0.12;
        p2DisplayHp += (targetP2Hp - p2DisplayHp) * 0.12;

        // Smooth power meter
        double targetPower = engine.getPlayer1().getPowerMeter();
        p1DisplayPower += (targetPower - p1DisplayPower) * 0.15;
        specialAttackBtn.setEnabled(engine.getPlayer1().canUseSpecial());

        // Check if match just ended
        if (engine.isMatchOver()) {
            if (matchOverTime == 0) {
                matchOverTime = now;
            } else if (now - matchOverTime > 1800) {
                // Battle finished, proceed to result panel
                cleanup();
                screenManager.showResult(engine, storyStage);
                return;
            }
        }

        repaint();
    }

    private JPanel createTopHud() {
        JPanel hud = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Semi-transparent dark gradient top banner
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 15, 25, 230), 0, getHeight(), new Color(15, 23, 42, 180));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        hud.setOpaque(false);
        hud.setPreferredSize(new Dimension(1000, 100));
        hud.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        // Player 1 Left HUD
        JPanel p1Panel = new JPanel();
        p1Panel.setOpaque(false);
        p1Panel.setLayout(new BoxLayout(p1Panel, BoxLayout.Y_AXIS));

        JLabel p1Name = new JLabel(engine.getPlayer1().getProfile().getName() + " (YOU)");
        p1Name.setFont(UITheme.FONT_SUBHEADER);
        p1Name.setForeground(engine.getPlayer1().getProfile().getPrimaryColor());

        p1Panel.add(p1Name);
        p1Panel.add(Box.createVerticalStrut(4));
        hud.add(p1Panel, BorderLayout.WEST);

        // Center Pause / Combo info
        JPanel centerHud = new JPanel();
        centerHud.setOpaque(false);
        centerHud.setLayout(new BoxLayout(centerHud, BoxLayout.Y_AXIS));

        JButton pauseBtn = UITheme.createStyledButton("⏸ PAUSE", UITheme.TEXT_SECONDARY, 95, 28);
        pauseBtn.setFont(UITheme.FONT_SMALL);
        pauseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pauseBtn.addActionListener(e -> togglePause());

        JLabel diffLabel = new JLabel("DIFFICULTY: " + engine.getDifficulty().getDisplayName().toUpperCase());
        diffLabel.setFont(UITheme.FONT_SMALL);
        diffLabel.setForeground(UITheme.TEXT_MUTED);
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerHud.add(pauseBtn);
        centerHud.add(Box.createVerticalStrut(4));
        centerHud.add(diffLabel);
        hud.add(centerHud, BorderLayout.CENTER);

        // Opponent Right HUD
        JPanel p2Panel = new JPanel();
        p2Panel.setOpaque(false);
        p2Panel.setLayout(new BoxLayout(p2Panel, BoxLayout.Y_AXIS));

        String oppTitle = (storyStage != null)
                ? engine.getOpponent().getProfile().getName() + " [BOSS]"
                : engine.getOpponent().getProfile().getName() + " (" + engine.getAiPersonality().getDisplayName() + ")";

        JLabel p2Name = new JLabel(oppTitle, SwingConstants.RIGHT);
        p2Name.setFont(UITheme.FONT_SUBHEADER);
        p2Name.setForeground(engine.getOpponent().getProfile().getPrimaryColor());
        p2Name.setAlignmentX(Component.RIGHT_ALIGNMENT);

        p2Panel.add(p2Name);
        p2Panel.add(Box.createVerticalStrut(4));
        hud.add(p2Panel, BorderLayout.EAST);

        return hud;
    }

    private JPanel createBottomControls() {
        JPanel controls = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42, 190), 0, getHeight(), new Color(10, 15, 25, 240));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        controls.setOpaque(false);
        controls.setPreferredSize(new Dimension(1000, 180));
        controls.setBorder(BorderFactory.createEmptyBorder(15, 50, 18, 50));
        return controls;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        long now = System.currentTimeMillis();

        // Screen Shake offset
        int shakeX = (int) particleSystem.getShakeOffsetX();
        int shakeY = (int) particleSystem.getShakeOffsetY();
        g2.translate(shakeX, shakeY);

        // 1. Draw Arena Backdrop
        ArenaBackgroundRenderer.renderBackground(g2, arenaTheme, w, h, now);

        // 2. Draw Characters (Player on Left, Opponent on Right)
        int fighterGroundY = (int) (h * 0.65);
        int p1X = (int) (w * 0.28);
        int p2X = (int) (w * 0.72);

        FighterRenderer.renderFighter(g2, engine.getPlayer1(), p1X, fighterGroundY, true, now);
        FighterRenderer.renderFighter(g2, engine.getOpponent(), p2X, fighterGroundY, false, now);

        // 3. Draw Top HUD Health & Power Bars
        renderHealthAndPowerGauges(g2, w);

        // 4. Draw Center Combat Words & Typing Feedback
        renderTypingWordCard(g2, w, h);

        // 5. Render Particle System (Sparks, Slashes, Floating Damage Text)
        particleSystem.render(g2);

        // 6. Draw Pause Overlay if active
        if (isPaused) {
            renderPauseOverlay(g2, w, h);
        }

        g2.dispose();
    }

    private void renderHealthAndPowerGauges(Graphics2D g2, int w) {
        int barW = 320;
        int barH = 22;
        int topY = 45;

        // Player 1 Health Bar (Left)
        int p1X = 30;
        drawDynamicHealthBar(g2, p1X, topY, barW, barH, p1DisplayHp, engine.getPlayer1().getMaxHp(), true);

        // Player 1 Power Meter below Health Bar
        int powerH = 10;
        int powerY = topY + barH + 6;
        g2.setColor(new Color(20, 30, 45));
        g2.fillRoundRect(p1X, powerY, barW, powerH, 6, 6);

        int powerFillW = (int) (barW * (p1DisplayPower / 100.0));
        Color powerCol = (engine.getPlayer1().canUseSpecial()) ? UITheme.ACCENT_AMBER : UITheme.ACCENT_CYAN;
        g2.setColor(powerCol);
        g2.fillRoundRect(p1X, powerY, powerFillW, powerH, 6, 6);
        g2.setColor(UITheme.BORDER_COLOR);
        g2.drawRoundRect(p1X, powerY, barW, powerH, 6, 6);

        // Opponent Health Bar (Right)
        int p2X = w - 30 - barW;
        drawDynamicHealthBar(g2, p2X, topY, barW, barH, p2DisplayHp, engine.getOpponent().getMaxHp(), false);

        // Opponent Power Bar
        g2.setColor(new Color(20, 30, 45));
        g2.fillRoundRect(p2X, powerY, barW, powerH, 6, 6);
        int oppPowerFill = (int) (barW * (engine.getOpponent().getPowerMeter() / 100.0));
        g2.setColor(UITheme.ACCENT_PURPLE);
        g2.fillRoundRect(p2X, powerY, oppPowerFill, powerH, 6, 6);
        g2.setColor(UITheme.BORDER_COLOR);
        g2.drawRoundRect(p2X, powerY, barW, powerH, 6, 6);

        // Player 1 Combo Counter
        int combo = engine.getPlayer1().getComboCount();
        if (combo >= 2) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.setColor(UITheme.ACCENT_AMBER);
            String comboStr = "COMBO " + combo + "x!";
            g2.drawString(comboStr, p1X, powerY + 32);
        }
    }

    private void drawDynamicHealthBar(Graphics2D g2, int x, int y, int w, int h, double hp, int maxHp, boolean leftToRight) {
        // Bar Background
        g2.setColor(new Color(30, 20, 25));
        g2.fillRoundRect(x, y, w, h, 8, 8);

        // Health percentage
        double pct = Math.max(0.0, Math.min(1.0, hp / maxHp));
        int fillW = (int) (w * pct);

        // Dynamic Color gradient from Green -> Yellow -> Orange -> Red
        Color barColor;
        if (pct > 0.60) {
            barColor = UITheme.ACCENT_GREEN;
        } else if (pct > 0.35) {
            barColor = UITheme.ACCENT_AMBER;
        } else if (pct > 0.18) {
            barColor = UITheme.ACCENT_ORANGE;
        } else {
            barColor = UITheme.ACCENT_RED;
        }

        g2.setColor(barColor);
        if (leftToRight) {
            g2.fillRoundRect(x, y, fillW, h, 8, 8);
        } else {
            g2.fillRoundRect(x + (w - fillW), y, fillW, h, 8, 8);
        }

        // Border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, w, h, 8, 8);

        // HP numeric text
        g2.setFont(UITheme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        String hpText = (int) Math.round(hp) + " / " + maxHp + " HP";
        int tx = x + (w - fm.stringWidth(hpText)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(Color.BLACK);
        g2.drawString(hpText, tx + 1, ty + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(hpText, tx, ty);
    }

    private void renderTypingWordCard(Graphics2D g2, int w, int h) {
        int cardW = 540;
        int cardH = 75;
        int cardX = (w - cardW) / 2;
        int cardY = h - 265;

        // Card Backdrop
        g2.setColor(new Color(20, 28, 45, 230));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 16, 16);
        g2.setColor(engine.isP1HasError() ? UITheme.ACCENT_RED : UITheme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 16, 16);

        // Active Word Character Highlighting
        String targetWord = engine.getP1CurrentWord();
        String typed = engine.getP1TypedInput();
        boolean hasError = engine.isP1HasError();

        g2.setFont(UITheme.FONT_TYPING_LARGE);
        FontMetrics fm = g2.getFontMetrics();
        int wordWidth = fm.stringWidth(targetWord);
        int startX = cardX + (cardW - wordWidth) / 2;
        int textY = cardY + (cardH - fm.getHeight()) / 2 + fm.getAscent() - 6;

        int curX = startX;
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            String ch = String.valueOf(c);
            int chW = fm.stringWidth(ch);

            if (i < typed.length()) {
                if (hasError && i == typed.length() - 1) {
                    // Current mistyped character in bright RED
                    g2.setColor(UITheme.ACCENT_RED);
                } else {
                    // Correctly typed character in vibrant GREEN
                    g2.setColor(UITheme.ACCENT_GREEN);
                }
            } else {
                // Upcoming untyped characters in WHITE
                g2.setColor(Color.WHITE);
            }

            g2.drawString(ch, curX, textY);
            curX += chW;
        }

        // Preview upcoming next words
        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        StringBuilder nextPreview = new StringBuilder("Upcoming: ");
        for (String nw : engine.getP1UpcomingWords()) {
            nextPreview.append(nw).append("   ");
        }
        g2.drawString(nextPreview.toString(), cardX + 25, cardY + cardH - 12);
    }

    private void renderPauseOverlay(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        g2.setFont(UITheme.FONT_TITLE_MED);
        FontMetrics fm = g2.getFontMetrics();
        String paused = "GAME PAUSED";
        int tx = (w - fm.stringWidth(paused)) / 2;
        int ty = h / 2 - 30;

        g2.setColor(UITheme.ACCENT_AMBER);
        g2.drawString(paused, tx, ty);

        g2.setFont(UITheme.FONT_SUBHEADER);
        g2.setColor(Color.WHITE);
        String resume = "Press ESC or Pause button to Resume";
        g2.drawString(resume, (w - g2.getFontMetrics().stringWidth(resume)) / 2, ty + 40);
    }
}

