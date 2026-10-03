package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.model.SessionManager;
import com.typingbattle.model.StoryStage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Story Mode Campaign Screen displaying all 8 narrative acts,
 * progression unlocks, stage briefs, and story lore.
 */
@SuppressWarnings("serial")
public class StoryPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JPanel stagesGrid;
    private final JLabel pilotLabel;

    public StoryPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(22, 30, 10, 30));

        JButton backBtn = UITheme.createStyledButton("BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> screenManager.showMainMenu());
        topPanel.add(backBtn, BorderLayout.WEST);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("THE KEYBOARD CORE CAMPAIGN", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pilotLabel = new JLabel("PILOT: Chetan [Ninja]   -   8 ACT SAGA", SwingConstants.CENTER);
        pilotLabel.setFont(UITheme.FONT_SUBHEADER);
        pilotLabel.setForeground(UITheme.ACCENT_CYAN);
        pilotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pilotLabel);
        topPanel.add(titleBox, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        topPanel.add(spacer, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Story Intro Banner
        JPanel bannerPanel = new JPanel();
        bannerPanel.setOpaque(false);
        bannerPanel.setBorder(new EmptyBorder(0, 40, 10, 40));

        JPanel bannerCard = UITheme.createCardPanel();
        bannerCard.setLayout(new BoxLayout(bannerCard, BoxLayout.Y_AXIS));
        bannerCard.setPreferredSize(new Dimension(860, 68));

        JLabel storyLore1 = new JLabel("The world has been plunged into silence. Rogue warlords fractured the legendary Keyboard Core artifact.");
        storyLore1.setFont(UITheme.FONT_BODY_BOLD);
        storyLore1.setForeground(UITheme.ACCENT_AMBER);
        storyLore1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel storyLore2 = new JLabel("Battle through all 8 narrative acts, defeat guardians with your typing martial arts, and restore the lexicon!");
        storyLore2.setFont(UITheme.FONT_BODY);
        storyLore2.setForeground(UITheme.TEXT_SECONDARY);
        storyLore2.setAlignmentX(Component.CENTER_ALIGNMENT);

        bannerCard.add(storyLore1);
        bannerCard.add(Box.createVerticalStrut(4));
        bannerCard.add(storyLore2);
        bannerPanel.add(bannerCard);

        // Stages List Container - Grid with 0 rows for dynamic count
        stagesGrid = new JPanel();
        stagesGrid.setOpaque(false);
        stagesGrid.setLayout(new GridLayout(0, 1, 10, 10));
        stagesGrid.setBorder(new EmptyBorder(10, 50, 25, 50));

        JScrollPane scrollPane = new JScrollPane(stagesGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Put scrollable stages in bottom
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(bannerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        refreshStages();
    }

    public void refreshStages() {
        stagesGrid.removeAll();
        StoryStage[] stages = StoryStage.getAllStages();
        int unlockedStage = DataManager.getInstance().getStoryUnlockedStage();

        pilotLabel.setText("PILOT: " + SessionManager.getInstance().getPlayerDisplayInfo()
                + "   -   ACT " + unlockedStage + " / " + stages.length + " UNLOCKED");

        for (StoryStage stage : stages) {
            boolean isUnlocked = stage.getStageNumber() <= unlockedStage;
            stagesGrid.add(createStageCard(stage, isUnlocked));
        }

        stagesGrid.revalidate();
        stagesGrid.repaint();
    }

    private JPanel createStageCard(StoryStage stage, boolean unlocked) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(800, 68));

        // Left Badge
        JPanel leftBadge = new JPanel(new GridBagLayout());
        leftBadge.setOpaque(false);
        leftBadge.setPreferredSize(new Dimension(85, 45));

        JLabel numLabel = new JLabel(unlocked ? "ACT " + stage.getStageNumber() : "LOCKED");
        numLabel.setFont(UITheme.FONT_HEADER);
        numLabel.setForeground(unlocked ? UITheme.ACCENT_CYAN : UITheme.TEXT_MUTED);
        leftBadge.add(numLabel);
        card.add(leftBadge, BorderLayout.WEST);

        // Middle Information
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(stage.getStageTitle() + " - " + stage.getLocationName());
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(unlocked ? Color.WHITE : UITheme.TEXT_MUTED);

        JLabel bossInfo = new JLabel("Guardian: " + stage.getBossName() + " (" + stage.getBossType().getDisplayName()
                + ")  -  Difficulty: " + stage.getDifficulty().getDisplayName());
        bossInfo.setFont(UITheme.FONT_BODY);
        bossInfo.setForeground(unlocked ? UITheme.ACCENT_AMBER : UITheme.TEXT_MUTED);

        infoPanel.add(title);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(bossInfo);
        card.add(infoPanel, BorderLayout.CENTER);

        // Right Action Button
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);

        if (unlocked) {
            JButton fightBtn = UITheme.createStyledButton("ENTER BATTLE", UITheme.ACCENT_CYAN, 170, 40);
            fightBtn.addActionListener(e -> showStageIntroDialog(stage));
            actionPanel.add(fightBtn);
        } else {
            JLabel lockedLabel = new JLabel("LOCKED");
            lockedLabel.setFont(UITheme.FONT_BODY_BOLD);
            lockedLabel.setForeground(UITheme.TEXT_MUTED);
            actionPanel.add(lockedLabel);
        }

        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    private void showStageIntroDialog(StoryStage stage) {
        JDialog dialog = new JDialog(screenManager.getFrame(), stage.getStageTitle(), true);
        dialog.setSize(600, 370);
        dialog.setLocationRelativeTo(screenManager.getFrame());
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel(stage.getStageTitle() + " - " + stage.getLocationName());
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.ACCENT_AMBER);
        panel.add(title, BorderLayout.NORTH);

        JTextArea dialogArea = new JTextArea();
        dialogArea.setOpaque(false);
        dialogArea.setEditable(false);
        dialogArea.setLineWrap(true);
        dialogArea.setWrapStyleWord(true);
        dialogArea.setFont(UITheme.FONT_BODY);
        dialogArea.setForeground(Color.WHITE);
        dialogArea.setText("\n" + stage.getBossName() + " approaches the battlefield...\n\n\""
                + stage.getIntroDialogue() + "\"\n\nPrepare your fingers. Your typing precision will decide this battle!");
        panel.add(dialogArea, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton proceedBtn = UITheme.createStyledButton("CHOOSE FIGHTER", UITheme.ACCENT_CYAN, 180, 42);
        proceedBtn.addActionListener(e -> {
            dialog.dispose();
            screenManager.showCharacterSelect(false, stage);
        });
        btnPanel.add(proceedBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
