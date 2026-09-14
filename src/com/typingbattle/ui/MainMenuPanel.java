package com.typingbattle.ui;

import com.typingbattle.engine.SoundEngine;
import javax.swing.*;
import java.awt.*;

/**
 * Main Menu Screen featuring navigation options: Story Mode,
 * Single Player, Two Player, Leaderboard, Audio Controls, and Exit.
 */
@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JButton audioBtn;

    public MainMenuPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(45, 20, 20, 20));

        JLabel titleLabel = new JLabel("TYPING BATTLE ARENA");
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("SELECT COMBAT MODE");
        subLabel.setFont(UITheme.FONT_SUBHEADER);
        subLabel.setForeground(UITheme.ACCENT_CYAN);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Buttons Center Box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        JPanel menuCard = UITheme.createCardPanel();
        menuCard.setLayout(new BoxLayout(menuCard, BoxLayout.Y_AXIS));
        menuCard.setPreferredSize(new Dimension(440, 420));
        menuCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JButton storyBtn = UITheme.createStyledButton("📖  STORY MODE (CAMPAIGN)", UITheme.ACCENT_AMBER, 380, 50);
        storyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        storyBtn.addActionListener(e -> screenManager.showStory());

        JButton singleBtn = UITheme.createStyledButton("⚔  SINGLE PLAYER (VS AI)", UITheme.ACCENT_CYAN, 380, 50);
        singleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        singleBtn.addActionListener(e -> screenManager.showCharacterSelect(false, null));

        JButton twoPlayerBtn = UITheme.createStyledButton("👥  TWO PLAYER (LOCAL DUEL)", UITheme.ACCENT_PURPLE, 380, 50);
        twoPlayerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        twoPlayerBtn.addActionListener(e -> screenManager.showCharacterSelect(true, null));

        JButton statsBtn = UITheme.createStyledButton("📊  LEADERBOARD & ANALYTICS", UITheme.ACCENT_GREEN, 380, 50);
        statsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsBtn.addActionListener(e -> screenManager.showLeaderboard());

        audioBtn = UITheme.createStyledButton("🔊  AUDIO: ON", UITheme.TEXT_SECONDARY, 380, 45);
        audioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        audioBtn.addActionListener(e -> {
            SoundEngine.getInstance().toggleMute();
            updateAudioButton();
        });

        JButton exitBtn = UITheme.createStyledButton("✕  EXIT GAME", UITheme.ACCENT_RED, 380, 45);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> System.exit(0));

        menuCard.add(storyBtn);
        menuCard.add(Box.createVerticalStrut(14));
        menuCard.add(singleBtn);
        menuCard.add(Box.createVerticalStrut(14));
        menuCard.add(twoPlayerBtn);
        menuCard.add(Box.createVerticalStrut(14));
        menuCard.add(statsBtn);
        menuCard.add(Box.createVerticalStrut(14));
        menuCard.add(audioBtn);
        menuCard.add(Box.createVerticalStrut(14));
        menuCard.add(exitBtn);

        centerPanel.add(menuCard);
        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));
        JLabel tipLabel = new JLabel("Tip: Typing without mistakes builds higher combos and charges your Special Attack meter!");
        tipLabel.setFont(UITheme.FONT_BODY);
        tipLabel.setForeground(UITheme.TEXT_MUTED);
        footer.add(tipLabel);
        add(footer, BorderLayout.SOUTH);
    }

    private void updateAudioButton() {
        boolean muted = SoundEngine.getInstance().isMuted();
        audioBtn.setText(muted ? "🔇  AUDIO: MUTED" : "🔊  AUDIO: ON");
        audioBtn.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Subtle gradient backdrop
        GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(30, 25, 50));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Decorative background geometric rings
        g2.setColor(new Color(6, 182, 212, 15));
        g2.drawOval(-100, -100, 400, 400);
        g2.drawOval(getWidth() - 300, getHeight() - 300, 500, 500);

        g2.dispose();
    }
}

