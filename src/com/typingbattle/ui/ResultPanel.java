package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import com.typingbattle.engine.CombatEngine;
import com.typingbattle.model.MatchStats;
import com.typingbattle.model.StoryStage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Educational Performance Analysis and Match Results Screen.
 * Displays WPM, Accuracy %, Combo streaks, Rank grade (S-D),
 * personalized learning recommendations, and story progression.
 */
@SuppressWarnings("serial")
public class ResultPanel extends JPanel {

    private final ScreenManager screenManager;
    private final CombatEngine engine;
    private final StoryStage storyStage;

    public ResultPanel(ScreenManager screenManager, CombatEngine engine, StoryStage storyStage) {
        this.screenManager = screenManager;
        this.engine = engine;
        this.storyStage = storyStage;

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Save match record to persistent storage
        saveMatchAnalytics();

        // 1. Header Banner: Outcome
        JPanel headerPanel = createHeaderBanner();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Performance Analysis Dashboard Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 40, 15, 40));

        JPanel statsCard = createStatsDashboard();
        centerPanel.add(statsCard);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom: Navigation Action Buttons
        JPanel buttonBar = createActionButtons();
        add(buttonBar, BorderLayout.SOUTH);
    }

    private void saveMatchAnalytics() {
        MatchStats stats = engine.getPlayer1Stats();
        MatchStats.Rank rank = stats.calculateRank();
        boolean won = engine.isPlayer1Victorious();

        String modeName;
        if (engine.getMode() == CombatEngine.BattleMode.STORY_MODE && storyStage != null) {
            modeName = "Story Act " + storyStage.getStageNumber();
            if (won) {
                // Unlock next stage
                DataManager.getInstance().unlockStoryStage(storyStage.getStageNumber() + 1);
            }
        } else if (engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER) {
            modeName = "Two Player Duel";
        } else {
            modeName = "Single Player (" + engine.getDifficulty().getDisplayName() + ")";
        }

        MatchRecord record = new MatchRecord(
                System.currentTimeMillis(),
                modeName,
                engine.getPlayer1().getProfile().getName(),
                engine.getOpponent().getProfile().getName(),
                stats.getWpm(),
                stats.getAccuracy(),
                rank.getLabel(),
                won,
                stats.getHighestCombo()
        );

        DataManager.getInstance().recordMatch(record);
    }

    private JPanel createHeaderBanner() {
        JPanel banner = new JPanel();
        banner.setOpaque(false);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(25, 20, 10, 20));

        boolean p1Won = engine.isPlayer1Victorious();
        String outcomeTitle;
        Color titleColor;

        if (engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER) {
            outcomeTitle = p1Won ? "PLAYER 1 WINS THE DUEL!" : "PLAYER 2 WINS THE DUEL!";
            titleColor = p1Won ? engine.getPlayer1().getProfile().getPrimaryColor()
                               : engine.getOpponent().getProfile().getPrimaryColor();
        } else {
            outcomeTitle = p1Won ? "★ VICTORY ACHIEVED ★" : "☠ DEFEAT ☠";
            titleColor = p1Won ? UITheme.ACCENT_GREEN : UITheme.ACCENT_RED;
        }

        JLabel titleLabel = new JLabel(outcomeTitle);
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        banner.add(titleLabel);

        if (storyStage != null) {
            banner.add(Box.createVerticalStrut(8));
            String epilogue = p1Won ? storyStage.getVictoryEpilogue() : "The villain retains the Keyboard Core...";
            JLabel epilogueLabel = new JLabel("<html><center style='color:#F59E0B; max-width:650px;'>" + epilogue + "</center></html>");
            epilogueLabel.setFont(UITheme.FONT_BODY);
            epilogueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            banner.add(epilogueLabel);
        }

        return banner;
    }

    private JPanel createStatsDashboard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(25, 0));
        card.setPreferredSize(new Dimension(840, 360));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        MatchStats stats = engine.getPlayer1Stats();
        MatchStats.Rank rank = stats.calculateRank();

        // Left: Rank Badge Card
        JPanel rankBadge = new JPanel();
        rankBadge.setOpaque(false);
        rankBadge.setLayout(new BoxLayout(rankBadge, BoxLayout.Y_AXIS));
        rankBadge.setPreferredSize(new Dimension(200, 300));

        JLabel rankTitle = new JLabel("COMBAT RANK");
        rankTitle.setFont(UITheme.FONT_SUBHEADER);
        rankTitle.setForeground(UITheme.TEXT_SECONDARY);
        rankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Huge Rank Letter (e.g. "S")
        JLabel rankChar = new JLabel(rank.name());
        rankChar.setFont(new Font("SansSerif", Font.BOLD, 95));
        rankChar.setForeground(Color.decode(rank.getHexColor()));
        rankChar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rankDesc = new JLabel("<html><center style='font-size:11px; color:#E2E8F0;'>"
                + rank.getDescription() + "</center></html>");
        rankDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        rankBadge.add(Box.createVerticalGlue());
        rankBadge.add(rankTitle);
        rankBadge.add(rankChar);
        rankBadge.add(rankDesc);
        rankBadge.add(Box.createVerticalGlue());

        card.add(rankBadge, BorderLayout.WEST);

        // Right: Detailed Metrics Grid & Educational Training Tip
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("PERFORMANCE & TYPING ANALYSIS");
        header.setFont(UITheme.FONT_HEADER);
        header.setForeground(Color.WHITE);
        detailsPanel.add(header);
        detailsPanel.add(Box.createVerticalStrut(15));

        // 2x3 metrics grid
        JPanel grid = new JPanel(new GridLayout(3, 2, 18, 12));
        grid.setOpaque(false);

        grid.add(createMetricItem("Typing Velocity", stats.getWpm() + " WPM", UITheme.ACCENT_CYAN));
        grid.add(createMetricItem("Accuracy Precision", stats.getAccuracy() + " %", UITheme.ACCENT_GREEN));
        grid.add(createMetricItem("Peak Combo Streak", stats.getHighestCombo() + " Hits", UITheme.ACCENT_AMBER));
        grid.add(createMetricItem("Combat Duration", (int) stats.getDurationSeconds() + " sec", UITheme.TEXT_PRIMARY));
        grid.add(createMetricItem("Correct Keystrokes", String.valueOf(stats.getCorrectKeystrokes()), UITheme.ACCENT_GREEN));
        grid.add(createMetricItem("Mistakes Made", String.valueOf(stats.getErrorKeystrokes()), UITheme.ACCENT_RED));

        detailsPanel.add(grid);
        detailsPanel.add(Box.createVerticalStrut(18));

        // Educational Recommendation Box
        JPanel tipBox = new JPanel(new BorderLayout());
        tipBox.setBackground(new Color(20, 30, 48));
        tipBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel tipText = new JLabel("<html><b>Skill Analysis:</b> " + stats.getRecommendation() + "</html>");
        tipText.setFont(UITheme.FONT_BODY);
        tipText.setForeground(UITheme.TEXT_PRIMARY);
        tipBox.add(tipText, BorderLayout.CENTER);

        detailsPanel.add(tipBox);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMetricItem(String label, String value, Color valColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_SUBHEADER);
        val.setForeground(valColor);

        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private JPanel createActionButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bar.setOpaque(false);

        // Next Stage Button if in Story Mode and Won and not last stage
        if (engine.getMode() == CombatEngine.BattleMode.STORY_MODE && storyStage != null && engine.isPlayer1Victorious()) {
            if (storyStage.getStageNumber() < 5) {
                JButton nextStageBtn = UITheme.createStyledButton("NEXT ACT ▶", UITheme.ACCENT_CYAN, 170, 44);
                nextStageBtn.addActionListener(e -> {
                    StoryStage nextStage = StoryStage.getAllStages()[storyStage.getStageNumber()];
                    screenManager.showCharacterSelect(false, nextStage);
                });
                bar.add(nextStageBtn);
            }
        }

        JButton rematchBtn = UITheme.createStyledButton("⚔ REMATCH", UITheme.ACCENT_GREEN, 160, 44);
        rematchBtn.addActionListener(e -> {
            if (engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER) {
                screenManager.startTwoPlayerBattle(engine.getPlayer1().getProfile(), engine.getOpponent().getProfile());
            } else {
                screenManager.startSinglePlayerBattle(
                        engine.getPlayer1().getProfile(),
                        engine.getOpponent().getProfile(),
                        engine.getDifficulty(),
                        engine.getAiPersonality(),
                        storyStage != null ? storyStage.getArenaTheme() : "CYBERPUNK",
                        storyStage
                );
            }
        });
        bar.add(rematchBtn);

        JButton changeFighterBtn = UITheme.createStyledButton("CHOOSE FIGHTER", UITheme.ACCENT_PURPLE, 180, 44);
        changeFighterBtn.addActionListener(e -> {
            boolean is2P = engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER;
            screenManager.showCharacterSelect(is2P, storyStage);
        });
        bar.add(changeFighterBtn);

        JButton menuBtn = UITheme.createStyledButton("MAIN MENU", UITheme.TEXT_SECONDARY, 150, 44);
        menuBtn.addActionListener(e -> screenManager.showMainMenu());
        bar.add(menuBtn);

        return bar;
    }
}

