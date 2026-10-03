package com.typingbattle.ui;

import com.typingbattle.engine.*;
import com.typingbattle.model.CharacterProfile;
import com.typingbattle.model.Difficulty;
import com.typingbattle.model.Fighter;
import com.typingbattle.model.SessionManager;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Two Player Arena supporting both:
 * 1) Cross-device Network Multiplayer (Desktop vs Mobile/Browser via QR Code sync)
 * 2) Local Split-Screen Duel (two players on one keyboard).
 *
 * Implements full real-time synchronization, clear fighter visibility,
 * and high-definition Java 2D vector rendering.
 */
@SuppressWarnings("serial")
public class TwoPlayerArenaPanel extends JPanel {

    private final ScreenManager screenManager;
    private final CombatEngine engine;
    private final ParticleSystem particleSystem;
    private final SoundEngine soundEngine;
    private final MultiplayerSession networkSession; // Null if local duel

    private double p1DisplayHp;
    private double p2DisplayHp;

    private final JTextField p1Input;
    private final JTextField p2Input;
    private final JButton p1SpecialBtn;
    private final JButton p2SpecialBtn;

    private final Timer gameLoopTimer;
    private long lastFrameTime;
    private long matchOverTime = 0;
    private int lastSeenP2WordCount = 0;

    public TwoPlayerArenaPanel(ScreenManager screenManager, CharacterProfile p1Profile, CharacterProfile p2Profile) {
        this(screenManager, null, p1Profile, p2Profile);
    }

    public TwoPlayerArenaPanel(ScreenManager screenManager, MultiplayerSession networkSession,
                              CharacterProfile p1Profile, CharacterProfile p2Profile) {
        this.screenManager = screenManager;
        this.networkSession = networkSession;
        this.particleSystem = new ParticleSystem();
        this.soundEngine = SoundEngine.getInstance();

        this.engine = new CombatEngine(
                CombatEngine.BattleMode.TWO_PLAYER,
                Difficulty.MEDIUM,
                null,
                p1Profile,
                p2Profile,
                particleSystem,
                soundEngine
        );

        this.p1DisplayHp = engine.getPlayer1().getMaxHp();
        this.p2DisplayHp = engine.getOpponent().getMaxHp();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Top HUD
        JPanel topHud = createTopHud();
        add(topHud, BorderLayout.NORTH);

        // Bottom Split Input Area
        JPanel bottomArea = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomArea.setOpaque(false);
        bottomArea.setPreferredSize(new Dimension(1000, 160));
        bottomArea.setBorder(BorderFactory.createEmptyBorder(10, 35, 18, 35));

        // Player 1 Input Box
        JPanel p1InputBox = UITheme.createCardPanel();
        p1InputBox.setLayout(new BorderLayout(8, 8));
        String p1Header = "PLAYER 1: " + SessionManager.getInstance().getPlayerName() + " [" + p1Profile.getName() + "]";
        JLabel p1Title = new JLabel(p1Header, SwingConstants.CENTER);
        p1Title.setFont(UITheme.FONT_SUBHEADER);
        p1Title.setForeground(engine.getPlayer1().getProfile().getPrimaryColor());
        p1InputBox.add(p1Title, BorderLayout.NORTH);

        p1Input = new JTextField();
        p1Input.setFont(UITheme.FONT_TYPING);
        p1Input.setBackground(new Color(20, 26, 40));
        p1Input.setForeground(Color.WHITE);
        p1Input.setCaretColor(UITheme.ACCENT_CYAN);
        p1Input.setHorizontalAlignment(JTextField.CENTER);
        p1Input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        p1InputBox.add(p1Input, BorderLayout.CENTER);

        p1SpecialBtn = UITheme.createStyledButton("SPECIAL ATTACK", UITheme.ACCENT_AMBER, 180, 36);
        p1SpecialBtn.setEnabled(false);
        p1SpecialBtn.addActionListener(e -> {
            engine.triggerPlayer1Special();
            if (networkSession != null) networkSession.triggerP1Special();
            p1Input.requestFocusInWindow();
        });
        p1InputBox.add(p1SpecialBtn, BorderLayout.SOUTH);

        // Player 2 Input Box
        JPanel p2InputBox = UITheme.createCardPanel();
        p2InputBox.setLayout(new BorderLayout(8, 8));

        String p2NameStr = (networkSession != null && networkSession.isP2Connected())
                ? networkSession.getP2Name() : "Player 2";
        String p2Header = "PLAYER 2: " + p2NameStr + " [" + p2Profile.getName() + "]"
                + (networkSession != null ? " (MOBILE)" : "");
        JLabel p2Title = new JLabel(p2Header, SwingConstants.CENTER);
        p2Title.setFont(UITheme.FONT_SUBHEADER);
        p2Title.setForeground(engine.getOpponent().getProfile().getPrimaryColor());
        p2InputBox.add(p2Title, BorderLayout.NORTH);

        p2Input = new JTextField();
        p2Input.setFont(UITheme.FONT_TYPING);
        p2Input.setBackground(new Color(20, 26, 40));
        p2Input.setForeground(Color.WHITE);
        p2Input.setCaretColor(UITheme.ACCENT_PURPLE);
        p2Input.setHorizontalAlignment(JTextField.CENTER);
        p2Input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_PURPLE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        if (networkSession != null) {
            p2Input.setEditable(false);
            p2Input.setText("[Remote Mobile Client Connected]");
            p2Input.setForeground(UITheme.TEXT_MUTED);
        }
        p2InputBox.add(p2Input, BorderLayout.CENTER);

        p2SpecialBtn = UITheme.createStyledButton("SPECIAL ATTACK", UITheme.ACCENT_AMBER, 180, 36);
        p2SpecialBtn.setEnabled(false);
        p2SpecialBtn.addActionListener(e -> {
            engine.triggerPlayer2Special();
            p2Input.requestFocusInWindow();
        });
        p2InputBox.add(p2SpecialBtn, BorderLayout.SOUTH);

        bottomArea.add(p1InputBox);
        bottomArea.add(p2InputBox);
        add(bottomArea, BorderLayout.SOUTH);

        // Input Listeners
        p1Input.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { handleP1Change(); }
            @Override public void removeUpdate(DocumentEvent e) { handleP1Change(); }
            @Override public void changedUpdate(DocumentEvent e) { handleP1Change(); }
        });

        p1Input.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && engine.getPlayer1().canUseSpecial()) {
                    engine.triggerPlayer1Special();
                    if (networkSession != null) networkSession.triggerP1Special();
                }
            }
        });

        if (networkSession == null) {
            p2Input.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { handleP2Change(); }
                @Override public void removeUpdate(DocumentEvent e) { handleP2Change(); }
                @Override public void changedUpdate(DocumentEvent e) { handleP2Change(); }
            });

            p2Input.addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && engine.getOpponent().canUseSpecial()) {
                        engine.triggerPlayer2Special();
                    }
                }
            });
        }

        lastFrameTime = System.currentTimeMillis();
        gameLoopTimer = new Timer(16, e -> gameTick());
    }

    public void startBattle() {
        gameLoopTimer.start();
        SwingUtilities.invokeLater(p1Input::requestFocusInWindow);
    }

    public void cleanup() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        soundEngine.stopBattleMusic();
    }

    private void handleP1Change() {
        String txt = p1Input.getText().toLowerCase().trim();
        engine.handlePlayer1Input(txt);

        if (networkSession != null) {
            networkSession.setP1TypedInput(txt);
            if (txt.equals(engine.getP1CurrentWord().toLowerCase())) {
                int dmg = Math.max(25, txt.length() * 16);
                networkSession.executeP1Attack(dmg, txt);
            }
        }

        if (engine.getP1TypedInput().isEmpty() && !txt.isEmpty()) {
            SwingUtilities.invokeLater(() -> p1Input.setText(""));
        }
    }

    private void handleP2Change() {
        if (networkSession != null) return;
        String txt = p2Input.getText().toLowerCase().trim();
        engine.handlePlayer2Input(txt);
        if (engine.getP2TypedInput().isEmpty() && !txt.isEmpty()) {
            SwingUtilities.invokeLater(() -> p2Input.setText(""));
        }
    }

    private void gameTick() {
        long now = System.currentTimeMillis();
        long delta = now - lastFrameTime;
        lastFrameTime = now;

        // Synchronize with network session if active
        if (networkSession != null) {
            networkSession.updateTimer(now);

            // Check if remote player 2 landed attacks
            int currentP2Words = networkSession.getP2WordsCompleted();
            if (currentP2Words > lastSeenP2WordCount) {
                int diff = currentP2Words - lastSeenP2WordCount;
                lastSeenP2WordCount = currentP2Words;
                for (int i = 0; i < diff; i++) {
                    engine.getOpponent().triggerAttack();
                    int dmg = 35;
                    engine.getPlayer1().takeDamage(dmg);
                    particleSystem.spawnSlashEffect(engine.getPlayer1().getX(), engine.getPlayer1().getY(),
                            engine.getOpponent().getProfile().getPrimaryColor());
                    particleSystem.spawnHitSparks(engine.getPlayer1().getX(), engine.getPlayer1().getY(), Color.RED, 12);
                    soundEngine.playHit();
                    particleSystem.triggerScreenShake(4.0);
                }
            }

            p2Input.setText("Remote Typed: " + networkSession.getP2TypedInput());

            if (networkSession.getState() == MultiplayerSession.State.FINISHED && matchOverTime == 0) {
                matchOverTime = now;
            }
        }

        engine.update(delta);

        p1DisplayHp += (engine.getPlayer1().getCurrentHp() - p1DisplayHp) * 0.12;
        p2DisplayHp += (engine.getOpponent().getCurrentHp() - p2DisplayHp) * 0.12;

        p1SpecialBtn.setEnabled(engine.getPlayer1().canUseSpecial());
        p2SpecialBtn.setEnabled(engine.getOpponent().canUseSpecial());

        if (engine.isMatchOver() || (networkSession != null && networkSession.getState() == MultiplayerSession.State.FINISHED)) {
            if (matchOverTime == 0) {
                matchOverTime = now;
            } else if (now - matchOverTime > 1800) {
                cleanup();
                screenManager.showResult(engine, null);
                return;
            }
        }

        repaint();
    }

    private JPanel createTopHud() {
        JPanel hud = new JPanel(new BorderLayout());
        hud.setOpaque(false);
        hud.setPreferredSize(new Dimension(1000, 75));
        hud.setBorder(BorderFactory.createEmptyBorder(12, 35, 10, 35));

        String p1Display = "P1: " + SessionManager.getInstance().getPlayerName()
                + " [" + engine.getPlayer1().getProfile().getName() + "]";
        JLabel p1Name = new JLabel(p1Display);
        p1Name.setFont(UITheme.FONT_SUBHEADER);
        p1Name.setForeground(engine.getPlayer1().getProfile().getPrimaryColor());
        hud.add(p1Name, BorderLayout.WEST);

        String centerText = (networkSession != null) ? "ONLINE MULTIPLAYER ARENA" : "LOCAL 2-PLAYER DUEL";
        JLabel center = new JLabel(centerText, SwingConstants.CENTER);
        center.setFont(UITheme.FONT_HEADER);
        center.setForeground(Color.WHITE);
        hud.add(center, BorderLayout.CENTER);

        String p2Display = (networkSession != null && networkSession.isP2Connected())
                ? "P2: " + networkSession.getP2Name() + " [" + engine.getOpponent().getProfile().getName() + "]"
                : "P2: " + engine.getOpponent().getProfile().getName();
        JLabel p2Name = new JLabel(p2Display, SwingConstants.RIGHT);
        p2Name.setFont(UITheme.FONT_SUBHEADER);
        p2Name.setForeground(engine.getOpponent().getProfile().getPrimaryColor());
        hud.add(p2Name, BorderLayout.EAST);

        return hud;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        long now = System.currentTimeMillis();

        int shakeX = (int) particleSystem.getShakeOffsetX();
        int shakeY = (int) particleSystem.getShakeOffsetY();
        g2.translate(shakeX, shakeY);

        // Arena backdrop
        ArenaBackgroundRenderer.renderBackground(g2, "CYBERPUNK", w, h, now);

        // Characters positioned with clear elevation and unobstructed visibility (Requirement 7)
        int groundY = (int) (h * 0.46);
        int p1X = (int) (w * 0.22);
        int p2X = (int) (w * 0.78);

        FighterRenderer.renderFighter(g2, engine.getPlayer1(), p1X, groundY, true, now);
        FighterRenderer.renderFighter(g2, engine.getOpponent(), p2X, groundY, false, now);

        // Top health bars
        renderHealthBars(g2, w);

        // Dual active word cards - LOWERED into bottom console area so fighters are 100% visible
        int cardY = h - 170;
        int cardW = Math.min(380, (w / 2) - 50);
        int cardH = 58;

        String p1Word = engine.getP1CurrentWord();
        String p1Typed = engine.getP1TypedInput();
        boolean p1Err = engine.isP1HasError();

        String p2Word = (networkSession != null && networkSession.isP2Connected())
                ? networkSession.getP2CurrentWord() : engine.getP2CurrentWord();
        String p2Typed = (networkSession != null && networkSession.isP2Connected())
                ? networkSession.getP2TypedInput() : engine.getP2TypedInput();
        boolean p2Err = (networkSession != null && networkSession.isP2Connected())
                ? networkSession.isP2HasError() : engine.isP2HasError();

        renderPlayerWord(g2, p1Word, p1Typed, p1Err, 35, cardY, cardW, cardH, true);
        renderPlayerWord(g2, p2Word, p2Typed, p2Err, w - 35 - cardW, cardY, cardW, cardH, false);

        // Particles
        particleSystem.render(g2);

        g2.dispose();
    }

    private void renderHealthBars(Graphics2D g2, int w) {
        int barW = 320;
        int barH = 20;
        int y = 45;

        // P1 bar
        drawBar(g2, 35, y, barW, barH, p1DisplayHp, engine.getPlayer1().getMaxHp(), true);
        // P2 bar
        drawBar(g2, w - 35 - barW, y, barW, barH, p2DisplayHp, engine.getOpponent().getMaxHp(), false);
    }

    private void drawBar(Graphics2D g2, int x, int y, int w, int h, double hp, int maxHp, boolean leftToRight) {
        g2.setColor(new Color(30, 20, 25));
        g2.fillRoundRect(x, y, w, h, 6, 6);

        double pct = Math.max(0, Math.min(1.0, hp / maxHp));
        int fillW = (int) (w * pct);

        Color c = pct > 0.5 ? UITheme.ACCENT_GREEN : (pct > 0.25 ? UITheme.ACCENT_AMBER : UITheme.ACCENT_RED);
        g2.setColor(c);
        if (leftToRight) {
            g2.fillRoundRect(x, y, fillW, h, 6, 6);
        } else {
            g2.fillRoundRect(x + (w - fillW), y, fillW, h, 6, 6);
        }

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, w, h, 6, 6);
    }

    private void renderPlayerWord(Graphics2D g2, String targetWord, String typed, boolean error,
                                  int x, int y, int w, int h, boolean isP1) {
        if (targetWord == null) targetWord = "READY";
        if (typed == null) typed = "";

        g2.setColor(new Color(20, 25, 45, 235));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(error ? UITheme.ACCENT_RED : (isP1 ? UITheme.ACCENT_CYAN : UITheme.ACCENT_PURPLE));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        g2.setFont(UITheme.FONT_TYPING);
        FontMetrics fm = g2.getFontMetrics();
        int totalW = fm.stringWidth(targetWord);
        int curX = x + (w - totalW) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            String ch = String.valueOf(c);
            int chW = fm.stringWidth(ch);

            if (i < typed.length()) {
                g2.setColor(error && i == typed.length() - 1 ? UITheme.ACCENT_RED : UITheme.ACCENT_GREEN);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(ch, curX, textY);
            curX += chW;
        }
    }
}
