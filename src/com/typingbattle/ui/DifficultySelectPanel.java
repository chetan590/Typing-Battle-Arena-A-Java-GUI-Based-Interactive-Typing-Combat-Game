package com.typingbattle.ui;

import com.typingbattle.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Difficulty and AI Personality configuration screen for Single Player battles.
 */
@SuppressWarnings("serial")
public class DifficultySelectPanel extends JPanel {

    private final ScreenManager screenManager;
    private CharacterProfile playerProfile;
    private CharacterProfile opponentProfile;

    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private AIPersonality selectedPersonality = AIPersonality.BALANCED;
    private String selectedTheme = "CYBERPUNK";
    private CharacterType selectedOpponentType = CharacterType.SAMURAI;

    private final JPanel diffButtonsPanel;
    private final JComboBox<AIPersonality> personalityCombo;
    private final JComboBox<String> themeCombo;
    private final JComboBox<CharacterType> opponentTypeCombo;

    public DifficultySelectPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 35, 15, 35));

        JButton backBtn = UITheme.createStyledButton("← BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> screenManager.showCharacterSelect(false, null));
        headerPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("BATTLE CONFIGURATION", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 50, 20, 50));

        // 1. Difficulty Cards
        JLabel diffHeading = new JLabel("1. SELECT DIFFICULTY LEVEL");
        diffHeading.setFont(UITheme.FONT_HEADER);
        diffHeading.setForeground(UITheme.ACCENT_AMBER);
        diffHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(diffHeading);
        contentPanel.add(Box.createVerticalStrut(12));

        diffButtonsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        diffButtonsPanel.setOpaque(false);
        diffButtonsPanel.setMaximumSize(new Dimension(950, 110));

        for (Difficulty diff : Difficulty.values()) {
            JPanel card = createDifficultyCard(diff);
            diffButtonsPanel.add(card);
        }
        contentPanel.add(diffButtonsPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // 2. Opponent & Personality Customization Card
        JPanel customCard = UITheme.createCardPanel();
        customCard.setLayout(new GridLayout(3, 2, 20, 15));
        customCard.setMaximumSize(new Dimension(800, 160));

        // Opponent Character
        JLabel oppLabel = new JLabel("Opponent Fighter:");
        oppLabel.setFont(UITheme.FONT_SUBHEADER);
        oppLabel.setForeground(Color.WHITE);
        opponentTypeCombo = new JComboBox<>(CharacterType.values());
        opponentTypeCombo.setFont(UITheme.FONT_BODY);
        opponentTypeCombo.setSelectedItem(CharacterType.SAMURAI);
        opponentTypeCombo.addActionListener(e -> {
            selectedOpponentType = (CharacterType) opponentTypeCombo.getSelectedItem();
            opponentProfile = CharacterProfile.getProfile(selectedOpponentType);
        });

        // AI Personality
        JLabel persLabel = new JLabel("AI Personality:");
        persLabel.setFont(UITheme.FONT_SUBHEADER);
        persLabel.setForeground(Color.WHITE);
        personalityCombo = new JComboBox<>(AIPersonality.values());
        JComboBox<AIPersonality> personalityCombo = new JComboBox<>(AIPersonality.values());
        personalityCombo.setFont(UITheme.FONT_BODY);
        personalityCombo.setSelectedItem(AIPersonality.BALANCED);
        personalityCombo.addActionListener(e -> selectedPersonality = (AIPersonality) personalityCombo.getSelectedItem());

        // Arena Theme
        JLabel themeLabel = new JLabel("Arena Environment:");
        themeLabel.setFont(UITheme.FONT_SUBHEADER);
        themeLabel.setForeground(Color.WHITE);
        String[] themes = {"CYBERPUNK", "DOJO", "MYSTIC_TEMPLE", "VOLCANIC_CORE", "KEYBOARD_SANCTUM"};
        themeCombo = new JComboBox<>(themes);
        JComboBox<String> themeCombo = new JComboBox<>(themes);
        themeCombo.setFont(UITheme.FONT_BODY);
        themeCombo.addActionListener(e -> selectedTheme = (String) themeCombo.getSelectedItem());

        customCard.add(oppLabel);
        customCard.add(opponentTypeCombo);
        customCard.add(persLabel);
        customCard.add(personalityCombo);
        customCard.add(themeLabel);
        customCard.add(themeCombo);

        contentPanel.add(customCard);
        contentPanel.add(Box.createVerticalStrut(25));

        // Start Battle Button
        JButton startBtn = UITheme.createStyledButton("ENTER THE ARENA ⚔", UITheme.ACCENT_CYAN, 340, 52);
        startBtn.setFont(UITheme.FONT_HEADER);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> {
            screenManager.startSinglePlayerBattle(playerProfile, opponentProfile,
                    selectedDifficulty, selectedPersonality, selectedTheme, null);
        });
        contentPanel.add(startBtn);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createDifficultyCard(Difficulty diff) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean selected = (diff == selectedDifficulty);
                g2.setColor(selected ? UITheme.BG_CARD_HOVER : UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(selected ? UITheme.ACCENT_CYAN : UITheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(selected ? 2.5f : 1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel title = new JLabel(diff.getDisplayName());
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html><center style='font-size:10px; color:#94A3B8;'>" + diff.getDescription() + "</center></html>");
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                selectedDifficulty = diff;
                diffButtonsPanel.repaint();
            }
        });

        return card;
    }

    public void setup(CharacterProfile p1, CharacterProfile p2) {
        this.playerProfile = p1;
        this.opponentProfile = (p2 != null) ? p2 : CharacterProfile.getProfile(CharacterType.SAMURAI);
        this.selectedOpponentType = opponentProfile.getType();
        opponentTypeCombo.setSelectedItem(selectedOpponentType);
    }
}

