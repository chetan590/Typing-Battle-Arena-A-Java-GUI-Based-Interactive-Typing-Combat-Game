package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.data.MatchRecord;
import com.typingbattle.model.SessionManager;
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
 * Dedicated Global Leaderboard screen dynamically sorting players strictly by score.
 * Features top rank podium highlights, detailed combat metrics, and live session highlighting.
 */
@SuppressWarnings("serial")
public class LeaderboardPanel extends JPanel {

    private final ScreenManager screenManager;
    private final DefaultTableModel tableModel;
    private final JLabel topRanker1;
    private final JLabel topRanker2;
    private final JLabel topRanker3;
    private final JLabel activePilotLabel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, HH:mm");

    public LeaderboardPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(22, 35, 12, 35));

        JButton backBtn = UITheme.createStyledButton("← BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> screenManager.showMainMenu());
        headerPanel.add(backBtn, BorderLayout.WEST);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("GLOBAL HALL OF CHAMPIONS // LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        activePilotLabel = new JLabel("ACTIVE PILOT: Chetan", SwingConstants.CENTER);
        activePilotLabel.setFont(UITheme.FONT_SUBHEADER);
        activePilotLabel.setForeground(UITheme.ACCENT_CYAN);
        activePilotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(activePilotLabel);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(6, 40, 20, 40));

        // Top 3 Podium Cards Row
        JPanel podiumRow = new JPanel(new GridLayout(1, 3, 16, 0));
        podiumRow.setOpaque(false);
        podiumRow.setPreferredSize(new Dimension(900, 85));

        topRanker2 = new JLabel("---");
        topRanker1 = new JLabel("---");
        topRanker3 = new JLabel("---");

        podiumRow.add(createPodiumCard("🥈 2ND PLACE", topRanker2, new Color(203, 213, 225)));
        podiumRow.add(createPodiumCard("👑 1ST PLACE CHAMPION", topRanker1, UITheme.ACCENT_AMBER));
        podiumRow.add(createPodiumCard("🥉 3RD PLACE", topRanker3, new Color(249, 115, 22)));

        contentPanel.add(podiumRow, BorderLayout.NORTH);

        // Leaderboard Table
        String[] columns = {
                "Rank", "Player Name", "Combatant", "Score", "Accuracy",
                "Words Completed", "Best Streak", "Difficulty", "Date / Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(34);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(Color.WHITE);
        table.setGridColor(UITheme.BORDER_COLOR);
        table.setSelectionBackground(UITheme.BG_CARD_HOVER);

        JTableHeader th = table.getTableHeader();
        th.setFont(UITheme.FONT_BODY_BOLD);
        th.setBackground(new Color(25, 33, 48));
        th.setForeground(UITheme.ACCENT_CYAN);
        th.setPreferredSize(new Dimension(100, 38));

        // Custom cell renderer with dynamic rank badges and active user highlight
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.FONT_BODY);

                // Alternating row styling
                if (row % 2 == 0) {
                    lbl.setBackground(UITheme.BG_CARD);
                } else {
                    lbl.setBackground(new Color(24, 34, 49));
                }

                String currentPilot = SessionManager.getInstance().getPlayerName();
                String rowPlayer = String.valueOf(tbl.getValueAt(row, 1));
                if (rowPlayer.equalsIgnoreCase(currentPilot)) {
                    lbl.setBackground(new Color(6, 182, 212, 40));
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setForeground(UITheme.TEXT_PRIMARY);
                }

                // Colorize Score column
                if (col == 3) {
                    lbl.setForeground(UITheme.ACCENT_AMBER);
                    lbl.setFont(UITheme.FONT_BODY_BOLD);
                }

                // Colorize Rank column
                if (col == 0) {
                    if (row == 0) {
                        lbl.setForeground(UITheme.ACCENT_AMBER);
                        lbl.setFont(UITheme.FONT_BODY_BOLD);
                    } else if (row == 1) {
                        lbl.setForeground(new Color(203, 213, 225));
                        lbl.setFont(UITheme.FONT_BODY_BOLD);
                    } else if (row == 2) {
                        lbl.setForeground(new Color(249, 115, 22));
                        lbl.setFont(UITheme.FONT_BODY_BOLD);
                    }
                }

                if (col == 4) {
                    lbl.setForeground(UITheme.ACCENT_GREEN);
                }

                return lbl;
            }
        });

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(95);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);
        table.getColumnModel().getColumn(8).setPreferredWidth(125);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createPodiumCard(String title, JLabel valLabel, Color color) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(color);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valLabel.setFont(UITheme.FONT_SUBHEADER);
        valLabel.setForeground(Color.WHITE);
        valLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        return card;
    }

    public void refreshData() {
        activePilotLabel.setText("ACTIVE PILOT: " + SessionManager.getInstance().getPlayerDisplayInfo());

        DataManager dm = DataManager.getInstance();
        List<MatchRecord> sorted = dm.getLeaderboardSortedByScore();

        // Update Podium Cards
        if (!sorted.isEmpty()) {
            MatchRecord first = sorted.get(0);
            topRanker1.setText(first.getPlayerName() + " — " + String.format("%,d", first.getScore()) + " PTS");
        } else {
            topRanker1.setText("---");
        }

        if (sorted.size() > 1) {
            MatchRecord second = sorted.get(1);
            topRanker2.setText(second.getPlayerName() + " — " + String.format("%,d", second.getScore()) + " PTS");
        } else {
            topRanker2.setText("---");
        }

        if (sorted.size() > 2) {
            MatchRecord third = sorted.get(2);
            topRanker3.setText(third.getPlayerName() + " — " + String.format("%,d", third.getScore()) + " PTS");
        } else {
            topRanker3.setText("---");
        }

        // Fill table rows dynamically
        tableModel.setRowCount(0);
        int rank = 1;
        for (MatchRecord r : sorted) {
            String rankLabel = "#" + rank;
            if (rank == 1) rankLabel = "🥇 1";
            else if (rank == 2) rankLabel = "🥈 2";
            else if (rank == 3) rankLabel = "🥉 3";

            String dateStr = DATE_FORMAT.format(new Date(r.getTimestamp()));
            tableModel.addRow(new Object[]{
                    rankLabel,
                    r.getPlayerName(),
                    r.getPlayerCharacter(),
                    String.format("%,d", r.getScore()),
                    String.format("%.1f%%", r.getAccuracy()),
                    r.getWordsCompleted() + " words",
                    r.getMaxCombo() + " hits",
                    r.getDifficulty(),
                    dateStr
            });
            rank++;
        }
    }
}
