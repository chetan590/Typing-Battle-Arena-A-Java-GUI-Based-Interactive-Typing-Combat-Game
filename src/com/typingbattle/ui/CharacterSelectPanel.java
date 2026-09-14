package com.typingbattle.ui;

import com.typingbattle.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Character Selection Screen with live animated character previews,
 * stats breakdowns, special move overviews, and dual-player support.
 */
@SuppressWarnings("serial")
public class CharacterSelectPanel extends JPanel {

    private final ScreenManager screenManager;
    private boolean forTwoPlayer = false;
    private StoryStage targetStoryStage = null;
    private int playerPicking = 1; // 1 or 2

    private CharacterType p1SelectedType = CharacterType.NINJA;
    private CharacterType p2SelectedType = CharacterType.SAMURAI;
    private CharacterType currentlyHoveredType = CharacterType.NINJA;

    private final JPanel previewCanvas;
    private final JLabel titleLabel;
    private final JLabel nameLabel;
    private final JLabel titleSubLabel;
    private final JTextArea bioArea;
    private final JLabel standardAttackLabel;
    private final JLabel specialAttackLabel;
    private final JProgressBar attackBar;
    private final JProgressBar speedBar;
    private final JProgressBar defenseBar;

    private final Timer animTimer;
    private Fighter previewFighter;

    public CharacterSelectPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 35, 15, 35));

        JButton backBtn = UITheme.createStyledButton("← BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> {
            if (forTwoPlayer && playerPicking == 2) {
                playerPicking = 1;
                updateDisplay();
            } else if (targetStoryStage != null) {
                screenManager.showStory();
            } else {
                screenManager.showMainMenu();
            }
        });
        headerPanel.add(backBtn, BorderLayout.WEST);

        titleLabel = new JLabel("CHOOSE YOUR FIGHTER", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center split: Left = Character selection grid, Right = Live Preview & Stats Card
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 35, 25, 35));

        // LEFT: Fighter selector cards
        JPanel listCard = UITheme.createCardPanel();
        listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
        listCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        CharacterType[] types = CharacterType.values();
        for (CharacterType type : types) {
            CharacterProfile profile = CharacterProfile.getProfile(type);
            JButton btn = UITheme.createStyledButton(profile.getName() + " (" + type.getDisplayName() + ")",
                    profile.getPrimaryColor(), 420, 52);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> {
                currentlyHoveredType = type;
                if (forTwoPlayer) {
                    if (playerPicking == 1) {
                        p1SelectedType = type;
                    } else {
                        p2SelectedType = type;
                    }
                } else {
                    p1SelectedType = type;
                }
                updateDisplay();
            });
            listCard.add(btn);
            listCard.add(Box.createVerticalStrut(10));
        }

        centerPanel.add(listCard);

        // RIGHT: Live animated preview & stats card
        JPanel statsCard = UITheme.createCardPanel();
        statsCard.setLayout(new BorderLayout(0, 10));

        // Live preview canvas
        previewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Arena platform circle
                g2.setColor(new Color(20, 25, 45));
                g2.fillOval(w / 2 - 80, h - 35, 160, 25);
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(w / 2 - 80, h - 35, 160, 25);

                if (previewFighter != null) {
                    FighterRenderer.renderFighter(g2, previewFighter, w / 2, h - 70, true, System.currentTimeMillis());
                }

                g2.dispose();
            }
        };
        previewCanvas.setOpaque(false);
        previewCanvas.setPreferredSize(new Dimension(380, 180));
        statsCard.add(previewCanvas, BorderLayout.NORTH);

        // Info & stats panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("Name");
        nameLabel.setFont(UITheme.FONT_HEADER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSubLabel = new JLabel("Title");
        titleSubLabel.setFont(UITheme.FONT_BODY_BOLD);
        titleSubLabel.setForeground(UITheme.ACCENT_AMBER);
        titleSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bioArea = new JTextArea();
        bioArea.setOpaque(false);
        bioArea.setEditable(false);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(UITheme.FONT_BODY);
        bioArea.setForeground(UITheme.TEXT_SECONDARY);
        bioArea.setMaximumSize(new Dimension(420, 50));
        bioArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        standardAttackLabel = new JLabel("Standard Strike: ");
        standardAttackLabel.setFont(UITheme.FONT_BODY);
        standardAttackLabel.setForeground(UITheme.TEXT_PRIMARY);

        specialAttackLabel = new JLabel("Special Move: ");
        specialAttackLabel.setFont(UITheme.FONT_BODY_BOLD);
        specialAttackLabel.setForeground(UITheme.ACCENT_AMBER);

        attackBar = createStatBar(UITheme.ACCENT_RED);
        speedBar = createStatBar(UITheme.ACCENT_CYAN);
        defenseBar = createStatBar(UITheme.ACCENT_GREEN);

        detailsPanel.add(nameLabel);
        detailsPanel.add(titleSubLabel);
        detailsPanel.add(Box.createVerticalStrut(6));
        detailsPanel.add(bioArea);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(standardAttackLabel);
        detailsPanel.add(specialAttackLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createStatRow("Attack Power", attackBar));
        detailsPanel.add(createStatRow("Speed Multiplier", speedBar));
        detailsPanel.add(createStatRow("Armor Defense", defenseBar));

        statsCard.add(detailsPanel, BorderLayout.CENTER);

        // Confirm / Select button at bottom of stats
        JButton confirmBtn = UITheme.createStyledButton("CONFIRM SELECTION ▶", UITheme.ACCENT_GREEN, 380, 45);
        confirmBtn.addActionListener(e -> onConfirmSelection());
        statsCard.add(confirmBtn, BorderLayout.SOUTH);

        centerPanel.add(statsCard);
        add(centerPanel, BorderLayout.CENTER);

        // 60 FPS animation timer for live character preview
        animTimer = new Timer(16, e -> previewCanvas.repaint());
        animTimer.start();

        updateDisplay();
    }

    private JProgressBar createStatBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 150);
        bar.setForeground(color);
        bar.setBackground(new Color(30, 40, 55));
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(180, 10));
        return bar;
    }

    private JPanel createStatRow(String label, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(380, 22));

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(110, 18));

        row.add(l, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    public void setupSelection(boolean forTwoPlayer, StoryStage targetStoryStage) {
        this.forTwoPlayer = forTwoPlayer;
        this.targetStoryStage = targetStoryStage;
        this.playerPicking = 1;
        this.currentlyHoveredType = p1SelectedType;
        updateDisplay();
    }

    private void updateDisplay() {
        if (forTwoPlayer) {
            titleLabel.setText(playerPicking == 1 ? "PLAYER 1: SELECT FIGHTER" : "PLAYER 2: SELECT FIGHTER");
        } else if (targetStoryStage != null) {
            titleLabel.setText("STORY MODE: SELECT YOUR FIGHTER");
        } else {
            titleLabel.setText("SELECT YOUR COMBATANT");
        }

        CharacterType activeType = currentlyHoveredType;
        CharacterProfile profile = CharacterProfile.getProfile(activeType);

        nameLabel.setText(profile.getName());
        nameLabel.setForeground(profile.getPrimaryColor());
        titleSubLabel.setText(profile.getTitle());
        bioArea.setText(profile.getLore());
        standardAttackLabel.setText("Standard Strike: " + profile.getStandardAttackName());
        specialAttackLabel.setText("Special Move: " + profile.getSpecialAttackName());

        attackBar.setValue((int) (profile.getAttackMultiplier() * 100));
        speedBar.setValue((int) (profile.getSpeedMultiplier() * 100));
        defenseBar.setValue((int) (profile.getDefenseMultiplier() * 100));

        previewFighter = new Fighter(profile, 1000, true);
    }

    private void onConfirmSelection() {
        if (forTwoPlayer) {
            if (playerPicking == 1) {
                playerPicking = 2;
                currentlyHoveredType = p2SelectedType;
                updateDisplay();
            } else {
                // Both players selected! Start 2-Player Battle
                CharacterProfile p1 = CharacterProfile.getProfile(p1SelectedType);
                CharacterProfile p2 = CharacterProfile.getProfile(p2SelectedType);
                screenManager.startTwoPlayerBattle(p1, p2);
            }
        } else if (targetStoryStage != null) {
            // Story Mode: Start battle against target stage's boss!
            CharacterProfile player = CharacterProfile.getProfile(p1SelectedType);
            CharacterProfile boss = CharacterProfile.getProfile(targetStoryStage.getBossType());
            screenManager.startSinglePlayerBattle(player, boss,
                    targetStoryStage.getDifficulty(),
                    targetStoryStage.getPersonality(),
                    targetStoryStage.getArenaTheme(),
                    targetStoryStage);
        } else {
            // Single Player Quick Battle: Proceed to difficulty and AI personality selection
            CharacterProfile player = CharacterProfile.getProfile(p1SelectedType);
            // Default opponent can be chosen or randomized
            CharacterProfile bot = CharacterProfile.getProfile(CharacterType.SAMURAI);
            screenManager.showDifficultySelect(player, bot);
        }
    }
}

