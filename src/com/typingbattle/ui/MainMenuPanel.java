package com.typingbattle.ui;

import com.typingbattle.engine.SoundEngine;
import com.typingbattle.model.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Menu Screen featuring navigation options: Story Mode, Single Player,
 * Two Player Multiplayer (with QR Code), separate Leaderboard, separate Analytics,
 * Settings modal, and active Pilot Profile management.
 */
@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JButton audioBtn;
    private final JLabel pilotInfoLabel;

    public MainMenuPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(25, 20, 10, 20));

        JLabel titleLabel = new JLabel("TYPING BATTLE ARENA");
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pilot Info Banner (Requirement 1)
        JPanel pilotBanner = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pilotBanner.setOpaque(false);

        pilotInfoLabel = new JLabel("PILOT: Chetan [Hayato the Silent]");
        pilotInfoLabel.setFont(UITheme.FONT_SUBHEADER);
        pilotInfoLabel.setForeground(UITheme.ACCENT_CYAN);
        pilotBanner.add(pilotInfoLabel);

        JButton editProfileBtn = UITheme.createStyledButton("âœŽ EDIT PROFILE", UITheme.TEXT_SECONDARY, 120, 28);
        editProfileBtn.setFont(UITheme.FONT_SMALL);
        editProfileBtn.addActionListener(e -> screenManager.showPlayerSetup());
        pilotBanner.add(editProfileBtn);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(pilotBanner);

        add(headerPanel, BorderLayout.NORTH);

        // Buttons Center Box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        JPanel menuCard = UITheme.createCardPanel();
        menuCard.setLayout(new BoxLayout(menuCard, BoxLayout.Y_AXIS));
        menuCard.setPreferredSize(new Dimension(450, 480));
        menuCard.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        JButton storyBtn = UITheme.createStyledButton("ðŸ“–  STORY MODE (8 ACT CAMPAIGN)", UITheme.ACCENT_AMBER, 390, 44);
        storyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        storyBtn.addActionListener(e -> screenManager.showStory());

        JButton singleBtn = UITheme.createStyledButton("âš”  SINGLE PLAYER (VS AI)", UITheme.ACCENT_CYAN, 390, 44);
        singleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        singleBtn.addActionListener(e -> screenManager.showCharacterSelect(false, null));

        // Two Player Multiplayer (Requirement 5)
        JButton twoPlayerBtn = UITheme.createStyledButton("ðŸ‘¥  TWO PLAYERS (QR / MULTIPLAYER)", UITheme.ACCENT_PURPLE, 390, 44);
        twoPlayerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        twoPlayerBtn.addActionListener(e -> screenManager.showMultiplayerLobby());

        // Separate Leaderboard (Requirement 4)
        JButton leaderboardBtn = UITheme.createStyledButton("ðŸ†  LEADERBOARD", UITheme.ACCENT_GREEN, 390, 44);
        leaderboardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardBtn.addActionListener(e -> screenManager.showLeaderboard());

        // Separate Analytics (Requirement 4)
        JButton analyticsBtn = UITheme.createStyledButton("ðŸ“ˆ  COMBAT ANALYTICS", UITheme.ACCENT_CYAN, 390, 44);
        analyticsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        analyticsBtn.addActionListener(e -> screenManager.showAnalytics());

        // Settings (Requirement 3)
        JButton settingsBtn = UITheme.createStyledButton("âš™  GAMEPLAY SETTINGS", UITheme.ACCENT_AMBER, 390, 42);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(screenManager.getFrame());
            dialog.setVisible(true);
        });

        audioBtn = UITheme.createStyledButton("ðŸ”Š  AUDIO: ON", UITheme.TEXT_SECONDARY, 390, 40);
        audioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        audioBtn.addActionListener(e -> {
            SoundEngine.getInstance().toggleMute();
            updateAudioButton();
        });

        JButton exitBtn = UITheme.createStyledButton("âœ•  EXIT GAME", UITheme.ACCENT_RED, 390, 40);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> System.exit(0));

        menuCard.add(storyBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(singleBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(twoPlayerBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(leaderboardBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(analyticsBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(settingsBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(audioBtn);
        menuCard.add(Box.createVerticalStrut(9));
        menuCard.add(exitBtn);

        centerPanel.add(menuCard);
        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));
        JLabel tipLabel = new JLabel("Tip: Scan the Two-Player QR Code on any phone to engage in real-time cross-device combat!");
        tipLabel.setFont(UITheme.FONT_BODY);
        tipLabel.setForeground(UITheme.TEXT_MUTED);
        footer.add(tipLabel);
        add(footer, BorderLayout.SOUTH);
    }

    public void updatePilotDisplay() {
        pilotInfoLabel.setText("PILOT: " + SessionManager.getInstance().getPlayerDisplayInfo());
        pilotInfoLabel.repaint();
    }

    private void updateAudioButton() {
        boolean muted = SoundEngine.getInstance().isMuted();
        audioBtn.setText(muted ? "ðŸ”‡  AUDIO: MUTED" : "ðŸ”Š  AUDIO: ON");
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
