package com.typingbattle.ui;

import com.typingbattle.data.DataManager;
import com.typingbattle.model.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Dedicated Combat Analytics Dashboard displaying career metrics,
 * accuracy precision, word volume breakdowns, performance by difficulty,
 * and high-definition Java 2D time-series charts.
 */
@SuppressWarnings("serial")
public class AnalyticsPanel extends JPanel {

    private final ScreenManager screenManager;

    // Stat metric value labels
    private final JLabel gamesVal;
    private final JLabel scoreVal;
    private final JLabel accuracyVal;
    private final JLabel streakVal;
    private final JLabel wordsVal;
    private final JLabel operatorLabel;

    // Custom Java 2D chart canvases
    private final JPanel performanceTimeChart;
    private final JPanel difficultyBarChart;
    private final JPanel wordBreakdownBar;

    public AnalyticsPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(22, 35, 12, 35));

        JButton backBtn = UITheme.createStyledButton("BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> screenManager.showMainMenu());
        headerPanel.add(backBtn, BorderLayout.WEST);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TYPING COMBAT ANALYTICS & DIAGNOSTICS", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        operatorLabel = new JLabel("PILOT: Chetan   |   CAREER SUMMARY", SwingConstants.CENTER);
        operatorLabel.setFont(UITheme.FONT_SUBHEADER);
        operatorLabel.setForeground(UITheme.ACCENT_CYAN);
        operatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(operatorLabel);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Scrollable / Grid Content
        JPanel mainContent = new JPanel();
        mainContent.setOpaque(false);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(new EmptyBorder(6, 40, 20, 40));

        // 1. Metric Cards Row
        JPanel metricRow = new JPanel(new GridLayout(1, 4, 15, 0));
        metricRow.setOpaque(false);
        metricRow.setMaximumSize(new Dimension(1050, 85));

        gamesVal = new JLabel("0 G (0W - 0L)");
        scoreVal = new JLabel("0 PTS (Avg 0)");
        accuracyVal = new JLabel("0.0% / 0 WPM");
        streakVal = new JLabel("0 Hits / 350ms");

        metricRow.add(createCard("COMBAT RECORD", gamesVal, UITheme.ACCENT_CYAN));
        metricRow.add(createCard("TOTAL / AVG SCORE", scoreVal, UITheme.ACCENT_AMBER));
        metricRow.add(createCard("PRECISION & SPEED", accuracyVal, UITheme.ACCENT_GREEN));
        metricRow.add(createCard("BEST STREAK / REACTION", streakVal, UITheme.ACCENT_PURPLE));

        mainContent.add(metricRow);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Charts Row (2 visual charts side by side)
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(1050, 260));

        // Left Chart: Performance Over Time (Score Progression)
        JPanel timeCard = UITheme.createCardPanel();
        timeCard.setLayout(new BorderLayout(0, 10));
        JLabel timeTitle = new JLabel("PERFORMANCE OVER TIME (RECENT SCORES)");
        timeTitle.setFont(UITheme.FONT_SUBHEADER);
        timeTitle.setForeground(Color.WHITE);
        timeCard.add(timeTitle, BorderLayout.NORTH);

        performanceTimeChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderTimeChart((Graphics2D) g);
            }
        };
        performanceTimeChart.setOpaque(false);
        timeCard.add(performanceTimeChart, BorderLayout.CENTER);
        chartsRow.add(timeCard);

        // Right Chart: Performance By Difficulty
        JPanel diffCard = UITheme.createCardPanel();
        diffCard.setLayout(new BorderLayout(0, 10));
        JLabel diffTitle = new JLabel("PERFORMANCE BY DIFFICULTY");
        diffTitle.setFont(UITheme.FONT_SUBHEADER);
        diffTitle.setForeground(Color.WHITE);
        diffCard.add(diffTitle, BorderLayout.NORTH);

        difficultyBarChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderDifficultyChart((Graphics2D) g);
            }
        };
        difficultyBarChart.setOpaque(false);
        diffCard.add(difficultyBarChart, BorderLayout.CENTER);
        chartsRow.add(diffCard);

        mainContent.add(chartsRow);
        mainContent.add(Box.createVerticalStrut(18));

        // 3. Bottom Card: Word Volume & Accuracy Breakdown
        JPanel bottomCard = UITheme.createCardPanel();
        bottomCard.setLayout(new BorderLayout(0, 10));
        bottomCard.setMaximumSize(new Dimension(1050, 110));

        JLabel bottomTitle = new JLabel("TOTAL WORDS TYPED BREAKDOWN");
        bottomTitle.setFont(UITheme.FONT_SUBHEADER);
        bottomTitle.setForeground(Color.WHITE);
        bottomCard.add(bottomTitle, BorderLayout.NORTH);

        wordsVal = new JLabel("Words: 0 Total   |   Correct: 0   |   Mistakes: 0");
        wordsVal.setFont(UITheme.FONT_BODY_BOLD);
        wordsVal.setForeground(UITheme.TEXT_SECONDARY);
        bottomCard.add(wordsVal, BorderLayout.CENTER);

        wordBreakdownBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderWordBreakdown((Graphics2D) g);
            }
        };
        wordBreakdownBar.setOpaque(false);
        wordBreakdownBar.setPreferredSize(new Dimension(800, 20));
        bottomCard.add(wordBreakdownBar, BorderLayout.SOUTH);

        mainContent.add(bottomCard);

        add(mainContent, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createCard(String title, JLabel valLabel, Color color) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_SECONDARY);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valLabel.setFont(UITheme.FONT_SUBHEADER);
        valLabel.setForeground(color);
        valLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(6));
        card.add(valLabel);
        return card;
    }

    public void refreshData() {
        DataManager dm = DataManager.getInstance();
        SessionManager sm = SessionManager.getInstance();

        operatorLabel.setText("PILOT: " + sm.getPlayerDisplayInfo() + "   |   CAREER SUMMARY");

        int games = dm.getTotalGamesPlayed();
        int wins = dm.getWins();
        int losses = dm.getLosses();
        gamesVal.setText(games + " Matches (" + wins + "W - " + losses + "L, " + dm.getWinRatePercent() + "%)");

        scoreVal.setText(String.format("%,d", dm.getTotalScore()) + " PTS (Avg " + String.format("%,d", dm.getAverageScore()) + ")");
        accuracyVal.setText(dm.getAverageAccuracy() + "% Acc  |  " + dm.getAverageWpm() + " WPM Avg");
        streakVal.setText(dm.getBestStreak() + " Hits Max  |  " + dm.getAverageReactionTimeMs() + "ms Avg");

        wordsVal.setText("Total Words Typed: " + dm.getTotalWordsTyped()
                + "   -   Correct: " + dm.getTotalCorrectWords()
                + "   -   Mistakes: " + dm.getTotalIncorrectWords());

        repaint();
    }

    private void renderTimeChart(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = performanceTimeChart.getWidth();
        int h = performanceTimeChart.getHeight();
        if (w <= 20 || h <= 20) return;

        List<Integer> scores = DataManager.getInstance().getRecentScores(10);
        if (scores.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString("No match data recorded yet. Enter combat to begin telemetry.", 30, h / 2);
            g2.dispose();
            return;
        }

        int maxScore = Math.max(10000, scores.stream().mapToInt(Integer::intValue).max().orElse(10000));
        int padLeft = 50;
        int padRight = 30;
        int padTop = 20;
        int padBottom = 30;

        int chartW = w - padLeft - padRight;
        int chartH = h - padTop - padBottom;

        // Draw horizontal grid lines
        g2.setColor(new Color(40, 50, 70));
        g2.setFont(UITheme.FONT_SMALL);
        for (int i = 0; i <= 4; i++) {
            int gy = padTop + chartH - (i * chartH / 4);
            g2.drawLine(padLeft, gy, w - padRight, gy);
            int scoreLabel = maxScore * i / 4;
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString(scoreLabel / 1000 + "k", 12, gy + 4);
            g2.setColor(new Color(40, 50, 70));
        }

        // Draw plot line
        int n = scores.size();
        int[] xs = new int[n];
        int[] ys = new int[n];

        for (int i = 0; i < n; i++) {
            xs[i] = padLeft + (n == 1 ? chartW / 2 : (i * chartW / (n - 1)));
            int s = scores.get(i);
            ys[i] = padTop + chartH - (int) (((double) s / maxScore) * chartH);
        }

        // Gradient under line
        Polygon poly = new Polygon();
        poly.addPoint(xs[0], padTop + chartH);
        for (int i = 0; i < n; i++) {
            poly.addPoint(xs[i], ys[i]);
        }
        poly.addPoint(xs[n - 1], padTop + chartH);

        GradientPaint fillGp = new GradientPaint(0, padTop, new Color(6, 182, 212, 100), 0, padTop + chartH, new Color(6, 182, 212, 10));
        g2.setPaint(fillGp);
        g2.fillPolygon(poly);

        // Glowing stroke line
        g2.setColor(UITheme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(2.8f));
        for (int i = 0; i < n - 1; i++) {
            g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }

        // Nodes
        for (int i = 0; i < n; i++) {
            g2.setColor(Color.WHITE);
            g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
            g2.setColor(UITheme.ACCENT_CYAN);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(xs[i] - 5, ys[i] - 5, 10, 10);

            // Print score above node
            g2.setColor(UITheme.ACCENT_AMBER);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            String valStr = String.valueOf(scores.get(i));
            g2.drawString(valStr, xs[i] - 12, ys[i] - 9);
        }

        g2.dispose();
    }

    private void renderDifficultyChart(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = difficultyBarChart.getWidth();
        int h = difficultyBarChart.getHeight();
        if (w <= 20 || h <= 20) return;

        Map<String, int[]> stats = DataManager.getInstance().getStatsByDifficulty();
        String[] labels = {"Easy", "Medium", "Hard", "Intense"};
        Color[] barColors = {UITheme.ACCENT_GREEN, UITheme.ACCENT_CYAN, UITheme.ACCENT_AMBER, UITheme.ACCENT_RED};

        int padLeft = 40;
        int padBottom = 25;
        int padTop = 20;
        int chartH = h - padTop - padBottom;
        int numBars = labels.length;
        int barWidth = Math.min(48, (w - padLeft - 30) / (numBars * 2));

        // Find max average score
        int maxAvg = 1;
        for (String l : labels) {
            int[] data = stats.get(l);
            if (data != null && data[0] > 0) {
                int avg = data[2] / data[0];
                if (avg > maxAvg) maxAvg = avg;
            }
        }
        maxAvg = Math.max(8000, maxAvg);

        for (int i = 0; i < numBars; i++) {
            String l = labels[i];
            int[] data = stats.get(l);
            int count = (data != null) ? data[0] : 0;
            int avg = (count > 0) ? data[2] / count : 0;

            int bx = padLeft + i * ((w - padLeft - 20) / numBars) + 15;
            int barHeight = (int) (((double) avg / maxAvg) * chartH);
            int by = padTop + chartH - barHeight;

            // Bar fill
            g2.setColor(barColors[i]);
            g2.fillRoundRect(bx, by, barWidth, barHeight, 6, 6);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(bx, by, barWidth, barHeight, 6, 6);

            // Labels
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(Color.WHITE);
            g2.drawString(l, bx - 2, h - 8);

            // Count badge
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString(count + " plays", bx - 4, by - 6);
        }

        g2.dispose();
    }

    private void renderWordBreakdown(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = wordBreakdownBar.getWidth();
        int h = wordBreakdownBar.getHeight();
        if (w <= 20 || h <= 5) return;

        DataManager dm = DataManager.getInstance();
        int correct = dm.getTotalCorrectWords();
        int mistakes = dm.getTotalIncorrectWords();
        int total = Math.max(1, correct + mistakes);

        double correctPct = (double) correct / total;
        int correctW = (int) (w * correctPct);

        // Correct bar (Green)
        g2.setColor(UITheme.ACCENT_GREEN);
        g2.fillRoundRect(0, 0, correctW, h, 6, 6);

        // Mistake bar (Red)
        g2.setColor(UITheme.ACCENT_RED);
        g2.fillRoundRect(correctW, 0, w - correctW, h, 6, 6);

        // Border
        g2.setColor(UITheme.BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);

        g2.dispose();
    }
}
