package com.typingbattle.ui;

import com.typingbattle.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Player Setup Screen allowing the user to enter their pilot name and
 * choose their preferred combatant before entering the Main Menu.
 * Features live animated 2D vector preview and comprehensive stats.
 */
@SuppressWarnings("serial")
public class PlayerSetupPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JTextField nameField;
    private CharacterType selectedType = CharacterType.NINJA;

    private final JPanel previewCanvas;
    private final JLabel combatantNameLabel;
    private final JLabel combatantTitleLabel;
    private final JTextArea bioArea;
    private final JLabel standardAttackLabel;
    private final JLabel specialAttackLabel;
    private final JProgressBar attackBar;
    private final JProgressBar speedBar;
    private final JProgressBar defenseBar;
    private final List<JButton> combatantButtons = new ArrayList<>();

    private Fighter previewFighter;
    private final Timer animTimer;

    public PlayerSetupPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(30, 20, 10, 20));

        JLabel titleLabel = new JLabel("PILOT & COMBATANT INITIALIZATION");
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("STEP 1: ENTER NAME   |   STEP 2: CHOOSE COMBATANT");
        subLabel.setFont(UITheme.FONT_SUBHEADER);
        subLabel.setForeground(UITheme.ACCENT_CYAN);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center: 2-column split (Left: Name input + fighter buttons, Right: Live preview + stats)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 40, 20, 40));

        // LEFT CARD: Setup Controls
        JPanel leftCard = UITheme.createCardPanel();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel step1Label = new JLabel("1. ENTER YOUR PILOT NAME:");
        step1Label.setFont(UITheme.FONT_HEADER);
        step1Label.setForeground(UITheme.ACCENT_AMBER);
        step1Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCard.add(step1Label);
        leftCard.add(Box.createVerticalStrut(8));

        nameField = new JTextField(SessionManager.getInstance().getPlayerName());
        nameField.setFont(UITheme.FONT_SUBHEADER);
        nameField.setBackground(new Color(20, 26, 40));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(UITheme.ACCENT_CYAN);
        nameField.setMaximumSize(new Dimension(420, 44));
        nameField.setPreferredSize(new Dimension(420, 44));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        leftCard.add(nameField);
        leftCard.add(Box.createVerticalStrut(20));

        JLabel step2Label = new JLabel("2. SELECT PREFERRED COMBATANT:");
        step2Label.setFont(UITheme.FONT_HEADER);
        step2Label.setForeground(UITheme.ACCENT_AMBER);
        step2Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCard.add(step2Label);
        leftCard.add(Box.createVerticalStrut(10));

        // Combatant buttons list
        for (CharacterType type : CharacterType.values()) {
            CharacterProfile profile = CharacterProfile.getProfile(type);
            JButton btn = UITheme.createStyledButton(profile.getName() + " (" + type.getDisplayName() + ")",
                    profile.getPrimaryColor(), 420, 46);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> {
                selectedType = type;
                updateDisplay();
            });
            combatantButtons.add(btn);
            leftCard.add(btn);
            leftCard.add(Box.createVerticalStrut(8));
        }

        centerPanel.add(leftCard);

        // RIGHT CARD: Live Animated Preview & Stats
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(0, 10));
        rightCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Live preview canvas
        previewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Platform circle
                g2.setColor(new Color(20, 26, 46));
                g2.fillOval(w / 2 - 80, h - 35, 160, 26);
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(w / 2 - 80, h - 35, 160, 26);

                if (previewFighter != null) {
                    FighterRenderer.renderFighter(g2, previewFighter, w / 2, h - 70, true, System.currentTimeMillis());
                }

                g2.dispose();
            }
        };
        previewCanvas.setOpaque(false);
        previewCanvas.setPreferredSize(new Dimension(380, 160));
        rightCard.add(previewCanvas, BorderLayout.NORTH);

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        combatantNameLabel = new JLabel("Name");
        combatantNameLabel.setFont(UITheme.FONT_HEADER);
        combatantNameLabel.setForeground(Color.WHITE);

        combatantTitleLabel = new JLabel("Title");
        combatantTitleLabel.setFont(UITheme.FONT_BODY_BOLD);
        combatantTitleLabel.setForeground(UITheme.ACCENT_AMBER);

        bioArea = new JTextArea();
        bioArea.setOpaque(false);
        bioArea.setEditable(false);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(UITheme.FONT_BODY);
        bioArea.setForeground(UITheme.TEXT_SECONDARY);
        bioArea.setMaximumSize(new Dimension(420, 45));

        standardAttackLabel = new JLabel("Standard Strike: ");
        standardAttackLabel.setFont(UITheme.FONT_BODY);
        standardAttackLabel.setForeground(UITheme.TEXT_PRIMARY);

        specialAttackLabel = new JLabel("Special Move: ");
        specialAttackLabel.setFont(UITheme.FONT_BODY_BOLD);
        specialAttackLabel.setForeground(UITheme.ACCENT_AMBER);

        attackBar = createStatBar(UITheme.ACCENT_RED);
        speedBar = createStatBar(UITheme.ACCENT_CYAN);
        defenseBar = createStatBar(UITheme.ACCENT_GREEN);

        detailsPanel.add(combatantNameLabel);
        detailsPanel.add(combatantTitleLabel);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(bioArea);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(standardAttackLabel);
        detailsPanel.add(specialAttackLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(createStatRow("Attack Power", attackBar));
        detailsPanel.add(createStatRow("Speed Multiplier", speedBar));
        detailsPanel.add(createStatRow("Armor Defense", defenseBar));

        rightCard.add(detailsPanel, BorderLayout.CENTER);

        // Confirm Button
        JButton confirmBtn = UITheme.createStyledButton("SAVE PROFILE & ENTER GAME", UITheme.ACCENT_GREEN, 380, 48);
        confirmBtn.setFont(UITheme.FONT_HEADER);
        confirmBtn.addActionListener(e -> onConfirm());
        rightCard.add(confirmBtn, BorderLayout.SOUTH);

        centerPanel.add(rightCard);
        add(centerPanel, BorderLayout.CENTER);

        // 60 FPS animation timer
        animTimer = new Timer(16, e -> previewCanvas.repaint());
        animTimer.start();

        updateDisplay();
    }

    private JProgressBar createStatBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 150);
        bar.setForeground(color);
        bar.setBackground(new Color(30, 40, 55));
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(170, 8));
        return bar;
    }

    private JPanel createStatRow(String label, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(380, 20));

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(110, 18));

        row.add(l, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private void updateDisplay() {
        CharacterProfile profile = CharacterProfile.getProfile(selectedType);
        combatantNameLabel.setText(profile.getName());
        combatantNameLabel.setForeground(profile.getPrimaryColor());
        combatantTitleLabel.setText(profile.getTitle());
        bioArea.setText(profile.getLore());
        standardAttackLabel.setText("Standard Strike: " + profile.getStandardAttackName());
        specialAttackLabel.setText("Special Move: " + profile.getSpecialAttackName());

        attackBar.setValue((int) (profile.getAttackMultiplier() * 100));
        speedBar.setValue((int) (profile.getSpeedMultiplier() * 100));
        defenseBar.setValue((int) (profile.getDefenseMultiplier() * 100));

        previewFighter = new Fighter(profile, 1000, true);
    }

    public void syncFromSession() {
        nameField.setText(SessionManager.getInstance().getPlayerName());
        selectedType = SessionManager.getInstance().getPreferredCombatant();
        updateDisplay();
    }

    private void onConfirm() {
        String enteredName = nameField.getText().trim();
        if (enteredName.isEmpty()) {
            enteredName = "Chetan";
        }
        SessionManager.getInstance().setPlayerName(enteredName);
        SessionManager.getInstance().setPreferredCombatant(selectedType);

        screenManager.showMainMenu();
    }
}
