package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Leaderboard & Performance History screen displaying career stats,
 * best WPM, highest combo records, and historical match logs.
 */
@SuppressWarnings("serial")
public class LeaderboardPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JLabel bestWpmVal;
    private final JLabel bestComboVal;
    private final JLabel matchesVal;
    private final JLabel winRateVal;
    private final JLabel storyProgressVal;
    private final DefaultTableModel tableModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, HH:mm");

    public LeaderboardPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 35, 15, 35));

        JButton backBtn = UITheme.createStyledButton("← BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> screenManager.showMainMenu());
        headerPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("CAREER STATS & MATCH RECORDS", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 18));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 45, 25, 45));

        // Top 5 Metric Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 5, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(900, 80));

        bestWpmVal = new JLabel("0 WPM");
        bestComboVal = new JLabel("0 Hits");
        matchesVal = new JLabel("0");
        winRateVal = new JLabel("0 %");
        storyProgressVal = new JLabel("Act 1 / 5");

        statsRow.add(createStatCard("RECORD WPM", bestWpmVal, UITheme.ACCENT_CYAN));
        statsRow.add(createStatCard("MAX COMBO", bestComboVal, UITheme.ACCENT_AMBER));
        statsRow.add(createStatCard("MATCHES", matchesVal, UITheme.TEXT_PRIMARY));
        statsRow.add(createStatCard("VICTORY RATE", winRateVal, UITheme.ACCENT_GREEN));
        statsRow.add(createStatCard("STORY UNLOCK", storyProgressVal, UITheme.ACCENT_PURPLE));

        contentPanel.add(statsRow, BorderLayout.NORTH);

        // Historical Records Table
        String[] columns = {"Date/Time", "Combat Mode", "Fighter", "Opponent", "WPM", "Accuracy", "Rank", "Outcome"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(Color.WHITE);
        table.setGridColor(UITheme.BORDER_COLOR);
        table.setSelectionBackground(UITheme.BG_CARD_HOVER);

        JTableHeader th = table.getTableHeader();
        th.setFont(UITheme.FONT_BODY_BOLD);
        th.setBackground(new Color(25, 33, 48));
        th.setForeground(UITheme.ACCENT_CYAN);
        th.setPreferredSize(new Dimension(100, 36));

        // Center renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valLabel, Color valColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_SECONDARY);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valLabel.setFont(UITheme.FONT_HEADER);
        valLabel.setForeground(valColor);
        valLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        return card;
    }

    public void refreshData() {
        DataManager dm = DataManager.getInstance();

        bestWpmVal.setText(dm.getBestWpm() + " WPM");
        bestComboVal.setText(dm.getHighestCombo() + " Hits");
        matchesVal.setText(String.valueOf(dm.getTotalMatches()));

        int total = dm.getTotalMatches();
        int vic = dm.getTotalVictories();
        int rate = (total > 0) ? (int) Math.round(((double) vic / total) * 100) : 0;
        winRateVal.setText(rate + " %");

        int unlocked = dm.getStoryUnlockedStage();
        storyProgressVal.setText(unlocked >= 5 ? "Core Restored! ★" : "Act " + unlocked + " / 5");

        // Refresh table
        tableModel.setRowCount(0);
        List<MatchRecord> records = dm.getRecords();
        for (MatchRecord r : records) {
            String dateStr = DATE_FORMAT.format(new Date(r.getTimestamp()));
            String outcome = r.isVictory() ? "VICTORY" : "DEFEAT";
            tableModel.addRow(new Object[]{
                    dateStr,
                    r.getMode(),
                    r.getPlayerCharacter(),
                    r.getOpponentCharacter(),
                    r.getWpm() + " WPM",
                    r.getAccuracy() + " %",
                    r.getRank(),
                    outcome
            });
        }
    }
}

