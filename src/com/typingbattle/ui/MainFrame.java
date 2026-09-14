package com.typingbattle.ui;

import com.typingbattle.engine.SoundEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Main application window frame for Typing Battle Arena.
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private final ScreenManager screenManager;

    public MainFrame() {
        super("Typing Battle Arena — Interactive Combat Game");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 740);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        // Custom window icon
        setIconImage(createAppIcon());

        // Initialize screen controller
        screenManager = new ScreenManager(this);
        setContentPane(screenManager.getContainer());

        // Safe window cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SoundEngine.getInstance().stopBattleMusic();
            }
        });

        // Launch splash screen
        screenManager.showSplashScreen();
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Slate background
        g2.setColor(new Color(15, 23, 42));
        g2.fillRoundRect(2, 2, 60, 60, 16, 16);

        // Neon border
        g2.setColor(new Color(6, 182, 212));
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRoundRect(2, 2, 60, 60, 16, 16);

        // Crossed swords symbol
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        g2.setColor(new Color(245, 158, 11));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("⚔", (64 - fm.stringWidth("⚔")) / 2, 44);

        g2.dispose();
        return icon;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }
}

