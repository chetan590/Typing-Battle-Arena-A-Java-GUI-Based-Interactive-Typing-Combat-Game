package com.typingbattle.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * High-impact animated splash screen with glowing typography,
 * orbiting Keyboard Core, and start prompts.
 */
@SuppressWarnings("serial")
public class SplashScreenPanel extends JPanel {

    private final ScreenManager screenManager;
    private final Timer animTimer;
    private long startTime;

    public SplashScreenPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.startTime = System.currentTimeMillis();
        setBackground(UITheme.BG_DARK);
        setFocusable(true);

        // 60 FPS animation timer
        animTimer = new Timer(16, e -> repaint());
        animTimer.start();

        // Mouse and keyboard interactions to continue
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                advanceToMainMenu();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                advanceToMainMenu();
            }
        });
    }

    public void resetAnimation() {
        this.startTime = System.currentTimeMillis();
        requestFocusInWindow();
    }

    private void advanceToMainMenu() {
        screenManager.showMainMenu();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        long t = System.currentTimeMillis() - startTime;

        // Background dark gradient
        GradientPaint bg = new GradientPaint(0, 0, new Color(10, 15, 30), 0, h, new Color(15, 23, 42));
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);

        // Ambient cyber grid at bottom
        g2.setColor(new Color(6, 182, 212, 40));
        for (int x = 0; x < w; x += 60) {
            g2.drawLine(x, h - 120, x, h);
        }
        for (int y = h - 120; y < h; y += 30) {
            g2.drawLine(0, y, w, y);
        }

        // Center Floating KEYBOARD CORE Icon
        int coreX = w / 2;
        int coreY = (int) (h * 0.38 + Math.sin(t / 250.0) * 12);

        // Glowing core rings
        double pulse = 0.6 + Math.sin(t / 140.0) * 0.4;
        g2.setColor(new Color(6, 182, 212, (int) (pulse * 90)));
        g2.fillOval(coreX - 75, coreY - 75, 150, 150);
        g2.setColor(new Color(245, 158, 11, (int) (pulse * 70)));
        g2.fillOval(coreX - 55, coreY - 55, 110, 110);

        // Relic Keyboard Keycap
        g2.setColor(new Color(30, 41, 59));
        g2.fillRoundRect(coreX - 42, coreY - 42, 84, 84, 18, 18);
        g2.setColor(UITheme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRoundRect(coreX - 42, coreY - 42, 84, 84, 18, 18);

        // Keycap Letter
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        FontMetrics fmKey = g2.getFontMetrics();
        g2.drawString("⚔", coreX - fmKey.stringWidth("⚔") / 2, coreY + 12);

        // Orbiting Keystroke Glyphs
        String[] orbitKeys = {"[W]", "[A]", "[S]", "[D]"};
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        for (int i = 0; i < orbitKeys.length; i++) {
            double angle = (t / 500.0) + (i * Math.PI / 2.0);
            int ox = (int) (coreX + Math.cos(angle) * 105);
            int oy = (int) (coreY + Math.sin(angle) * 45);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.fillRoundRect(ox - 16, oy - 12, 32, 24, 6, 6);
            g2.setColor(UITheme.ACCENT_AMBER);
            g2.drawRoundRect(ox - 16, oy - 12, 32, 24, 6, 6);
            g2.setColor(Color.WHITE);
            g2.drawString(orbitKeys[i], ox - 11, oy + 5);
        }

        // Glowing Game Title
        g2.setFont(UITheme.FONT_TITLE_LARGE);
        FontMetrics fmTitle = g2.getFontMetrics();
        String title = "TYPING BATTLE ARENA";
        int tx = (w - fmTitle.stringWidth(title)) / 2;
        int ty = (int) (h * 0.62);

        // Title drop glow
        g2.setColor(new Color(6, 182, 212, 160));
        g2.drawString(title, tx + 3, ty + 3);
        g2.setColor(Color.WHITE);
        g2.drawString(title, tx, ty);

        // Subtitle
        g2.setFont(UITheme.FONT_SUBHEADER);
        g2.setColor(UITheme.ACCENT_AMBER);
        FontMetrics fmSub = g2.getFontMetrics();
        String sub = "The Keyboard Core Chronicles • A Real-Time Typing Combat Game";
        g2.drawString(sub, (w - fmSub.stringWidth(sub)) / 2, ty + 35);

        // Pulsating "Click or Press Any Key to Start" prompt
        float promptAlpha = (float) (0.4 + Math.sin(t / 200.0) * 0.4);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.1f, Math.min(1.0f, promptAlpha))));
        g2.setFont(UITheme.FONT_BODY_BOLD);
        g2.setColor(UITheme.TEXT_PRIMARY);
        FontMetrics fmPrompt = g2.getFontMetrics();
        String prompt = "▶  PRESS ANY KEY OR CLICK TO ENTER  ◀";
        g2.drawString(prompt, (w - fmPrompt.stringWidth(prompt)) / 2, (int) (h * 0.82));

        // Footer version info
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        String footer = "Java GUI Combat Engine • Pure Java SE • Version 1.0";
        g2.drawString(footer, (w - g2.getFontMetrics().stringWidth(footer)) / 2, h - 25);

        g2.dispose();
    }
}

