package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import com.typingbattle.engine.CombatEngine;
import com.typingbattle.model.MatchStats;
import com.typingbattle.model.SessionManager;
import com.typingbattle.model.StoryStage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Educational Performance Analysis and Match Results Screen.
 * Displays WPM, Accuracy %, Combo streaks, Rank grade (S-D),
 * dynamic combat score, personalized learning recommendations,
 * and Story Mode campaign progression.
 */
@SuppressWarnings("serial")
public class ResultPanel extends JPanel {

    private final ScreenManager screenManager;
    private final CombatEngine engine;
    private final StoryStage storyStage;
    private int calculatedScore = 0;

    public ResultPanel(ScreenManager screenManager, CombatEngine engine, StoryStage storyStage) {
        this.screenManager = screenManager;
        this.engine = engine;
        this.storyStage = storyStage;

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Save match record to persistent storage and compute score
        saveMatchAnalytics();

        // 1. Header Banner: Outcome & Player Info
        JPanel headerPanel = createHeaderBanner();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Performance Analysis Dashboard Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(5, 40, 15, 40));

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
        String playerName = SessionManager.getInstance().getPlayerName();

        // Calculate dynamic competitive score
        calculatedScore = (int) (stats.getWpm() * 110 + stats.getAccuracy() * 25 + stats.getHighestCombo() * 85
                + (stats.getCorrectWords() * 30) + (won ? 2500 : 500));

        String modeName;
        if (engine.getMode() == CombatEngine.BattleMode.STORY_MODE && storyStage != null) {
            modeName = "Story Act " + storyStage.getStageNumber();
            if (won) {
                // Unlock next stage up to 8 acts
                DataManager.getInstance().unlockStoryStage(storyStage.getStageNumber() + 1);
            }
        } else if (engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER) {
            modeName = "Two Player Duel";
        } else {
            modeName = "Single Player (" + engine.getDifficulty().getDisplayName() + ")";
        }

        int completed = stats.getCorrectWords();
        int mistakes = stats.getErrorKeystrokes();
        int totalWordsTyped = completed + (mistakes > 0 ? (mistakes / 3 + 1) : 0);
        int reactionTime = Math.max(160, 60000 / Math.max(1, stats.getWpm() * 5));

        MatchRecord record = new MatchRecord(
                System.currentTimeMillis(),
                playerName,
                engine.getPlayer1().getProfile().getName(),
                engine.getOpponent().getProfile().getName(),
                modeName,
                stats.getWpm(),
                stats.getAccuracy(),
                rank.getLabel(),
                won,
                stats.getHighestCombo(),
                calculatedScore,
                completed,
                totalWordsTyped,
                mistakes,
                engine.getDifficulty().getDisplayName(),
                reactionTime
        );

        DataManager.getInstance().recordMatch(record);
    }

    private JPanel createHeaderBanner() {
        JPanel banner = new JPanel();
        banner.setOpaque(false);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(18, 20, 8, 20));

        boolean p1Won = engine.isPlayer1Victorious();
        String playerName = SessionManager.getInstance().getPlayerName();
        String outcomeTitle;
        Color titleColor;

        if (engine.getMode() == CombatEngine.BattleMode.TWO_PLAYER) {
            outcomeTitle = p1Won ? (playerName.toUpperCase() + " (P1) WINS THE DUEL!")
                                 : "PLAYER 2 WINS THE DUEL!";
            titleColor = p1Won ? engine.getPlayer1().getProfile().getPrimaryColor()
                               : engine.getOpponent().getProfile().getPrimaryColor();
        } else {
            outcomeTitle = p1Won ? ("â˜… " + playerName.toUpperCase() + " VICTORIOUS â˜…")
                                 : ("â˜  " + playerName.toUpperCase() + " DEFEATED â˜ ");
            titleColor = p1Won ? UITheme.ACCENT_GREEN : UITheme.ACCENT_RED;
        }

        JLabel titleLabel = new JLabel(outcomeTitle);
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        banner.add(titleLabel);

        // Player Info & Score Subtitle
        JLabel subLabel = new JLabel("OPERATOR: " + SessionManager.getInstance().getPlayerDisplayInfo()
                + "   |   FINAL SCORE: " + String.format("%,d", calculatedScore) + " PTS");
        subLabel.setFont(UITheme.FONT_SUBHEADER);
        subLabel.setForeground(UITheme.ACCENT_AMBER);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        banner.add(Box.createVerticalStrut(4));
        banner.add(subLabel);

        if (storyStage != null) {
            banner.add(Box.createVerticalStrut(6));
            String epilogue = p1Won ? storyStage.getVictoryEpilogue() : "The villain retains the Keyboard Core...";
            JLabel epilogueLabel = new JLabel("<html><center style='color:#F59E0B; max-width:700px;'>" + epilogue + "</center></html>");
            epilogueLabel.setFont(UITheme.FONT_BODY);
            epilogueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            banner.add(epilogueLabel);
        }

        return banner;
    }

    private JPanel createStatsDashboard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(25, 0));
        card.setPreferredSize(new Dimension(860, 360));
        card.setBorder(new EmptyBorder(16, 25, 16, 25));

        MatchStats stats = engine.getPlayer1Stats();
        MatchStats.Rank rank = stats.calculateRank();

        // Left: Rank Badge Card
        JPanel rankBadge = new JPanel();
        rankBadge.setOpaque(false);
        rankBadge.setLayout(new BoxLayout(rankBadge, BoxLayout.Y_AXIS));
        rankBadge.setPreferredSize(new Dimension(210, 300));

        JLabel rankTitle = new JLabel("COMBAT RANK");
        rankTitle.setFont(UITheme.FONT_SUBHEADER);
        rankTitle.setForeground(UITheme.TEXT_SECONDARY);
        rankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rankChar = new JLabel(rank.name());
        rankChar.setFont(new Font("SansSerif", Font.BOLD, 92));
        rankChar.setForeground(Color.decode(rank.getHexColor()));
        rankChar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreValLabel = new JLabel(String.format("%,d PTS", calculatedScore));
        scoreValLabel.setFont(UITheme.FONT_HEADER);
        scoreValLabel.setForeground(UITheme.ACCENT_AMBER);
        scoreValLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rankDesc = new JLabel("<html><center style='font-size:11px; color:#E2E8F0;'>"
                + rank.getDescription() + "</center></html>");
        rankDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        rankBadge.add(Box.createVerticalGlue());
        rankBadge.add(rankTitle);
        rankBadge.add(rankChar);
        rankBadge.add(scoreValLabel);
        rankBadge.add(Box.createVerticalStrut(4));
        rankBadge.add(rankDesc);
        rankBadge.add(Box.createVerticalGlue());

        card.add(rankBadge, BorderLayout.WEST);

        // Right: Detailed Metrics Grid & Educational Training Tip
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("PERFORMANCE & ACCURACY DIAGNOSTICS");
        header.setFont(UITheme.FONT_HEADER);
        header.setForeground(Color.WHITE);
        detailsPanel.add(header);
        detailsPanel.add(Box.createVerticalStrut(14));

        // 2x3 metrics grid
        JPanel grid = new JPanel(new GridLayout(3, 2, 18, 10));
        grid.setOpaque(false);

        grid.add(createMetricItem("Typing Speed", stats.getWpm() + " WPM", UITheme.ACCENT_CYAN));
        grid.add(createMetricItem("Accuracy Precision", stats.getAccuracy() + " %", UITheme.ACCENT_GREEN));
        grid.add(createMetricItem("Peak Combo Streak", stats.getHighestCombo() + " Hits", UITheme.ACCENT_AMBER));
        grid.add(createMetricItem("Battle Duration", (int) stats.getDurationSeconds() + " sec", UITheme.TEXT_PRIMARY));
        grid.add(createMetricItem("Words Completed", String.valueOf(stats.getCorrectWords()), UITheme.ACCENT_GREEN));
        grid.add(createMetricItem("Mistyped Errors", String.valueOf(stats.getErrorKeystrokes()), UITheme.ACCENT_RED));

        detailsPanel.add(grid);
        detailsPanel.add(Box.createVerticalStrut(14));

        // Educational Recommendation Box
        JPanel tipBox = new JPanel(new BorderLayout());
        tipBox.setBackground(new Color(20, 30, 48));
        tipBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel tipText = new JLabel("<html><b>Tactical Debrief:</b> " + stats.getRecommendation() + "</html>");
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
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 16));
        bar.setOpaque(false);

        // Next Stage Button if in Story Mode and Won and not last stage
        if (engine.getMode() == CombatEngine.BattleMode.STORY_MODE && storyStage != null && engine.isPlayer1Victorious()) {
            if (storyStage.getStageNumber() < StoryStage.getAllStages().length) {
                JButton nextStageBtn = UITheme.createStyledButton("NEXT ACT", UITheme.ACCENT_CYAN, 170, 44);
                nextStageBtn.addActionListener(e -> {
                    StoryStage nextStage = StoryStage.getAllStages()[storyStage.getStageNumber()];
                    screenManager.showCharacterSelect(false, nextStage);
                });
                bar.add(nextStageBtn);
            }
        }

        JButton rematchBtn = UITheme.createStyledButton("REMATCH", UITheme.ACCENT_GREEN, 160, 44);
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

        JButton leaderboardBtn = UITheme.createStyledButton("LEADERBOARD", UITheme.ACCENT_AMBER, 170, 44);
        leaderboardBtn.addActionListener(e -> screenManager.showLeaderboard());
        bar.add(leaderboardBtn);

        JButton analyticsBtn = UITheme.createStyledButton("ANALYTICS", UITheme.ACCENT_PURPLE, 150, 44);
        analyticsBtn.addActionListener(e -> screenManager.showAnalytics());
        bar.add(analyticsBtn);

        JButton menuBtn = UITheme.createStyledButton("MAIN MENU", UITheme.TEXT_SECONDARY, 140, 44);
        menuBtn.addActionListener(e -> screenManager.showMainMenu());
        bar.add(menuBtn);

        return bar;
    }
}
