package com.typingbattle.ui;

import com.typingbattle.engine.SoundEngine;
import com.typingbattle.model.GameSettings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Clean, modern settings modal dialog for configuring word appearance speed,
 * opponent attack velocity, audio volume slider, and word length filtering.
 */
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {

    private final JLabel volumeValLabel;

    public SettingsDialog(Window owner) {
        super(owner, "BATTLE SETTINGS", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setSize(520, 560);
        setLocationRelativeTo(owner);
        setBackground(new Color(0, 0, 0, 0)); // Transparent window for rounded card

        GameSettings settings = GameSettings.getInstance();

        JPanel mainCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 28, 45, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                g2.dispose();
            }
        };
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(new EmptyBorder(20, 26, 20, 26));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("GAMEPLAY SETTINGS");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton closeX = new JButton("X");
        closeX.setFont(UITheme.FONT_SUBHEADER);
        closeX.setForeground(UITheme.TEXT_SECONDARY);
        closeX.setFocusPainted(false);
        closeX.setContentAreaFilled(false);
        closeX.setBorderPainted(false);
        closeX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeX.addActionListener(e -> dispose());
        header.add(closeX, BorderLayout.EAST);

        mainCard.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 0, 16, 0));

        // A. Word Appearance Speed
        content.add(createSectionHeader("A. Word Appearance Speed"));
        JPanel speedRow = new JPanel(new GridLayout(1, 3, 10, 0));
        speedRow.setOpaque(false);
        speedRow.setMaximumSize(new Dimension(460, 38));

        ButtonGroup speedGroup = new ButtonGroup();
        for (GameSettings.WordAppearanceSpeed sp : GameSettings.WordAppearanceSpeed.values()) {
            JToggleButton btn = createToggleOption(sp.getLabel(), settings.getAppearanceSpeed() == sp);
            btn.addActionListener(e -> settings.setAppearanceSpeed(sp));
            speedGroup.add(btn);
            speedRow.add(btn);
        }
        content.add(speedRow);
        content.add(Box.createVerticalStrut(18));

        // B. Opponent Attack Speed
        content.add(createSectionHeader("B. Opponent Attack Speed"));
        JPanel oppRow = new JPanel(new GridLayout(1, 3, 10, 0));
        oppRow.setOpaque(false);
        oppRow.setMaximumSize(new Dimension(460, 38));

        ButtonGroup oppGroup = new ButtonGroup();
        for (GameSettings.OpponentAttackSpeed os : GameSettings.OpponentAttackSpeed.values()) {
            JToggleButton btn = createToggleOption(os.getLabel(), settings.getAttackSpeed() == os);
            btn.addActionListener(e -> settings.setAttackSpeed(os));
            oppGroup.add(btn);
            oppRow.add(btn);
        }
        content.add(oppRow);
        content.add(Box.createVerticalStrut(18));

        // C. Sound Volume
        content.add(createSectionHeader("C. Sound Volume"));
        JPanel volRow = new JPanel(new BorderLayout(15, 0));
        volRow.setOpaque(false);
        volRow.setMaximumSize(new Dimension(460, 36));

        JSlider volSlider = new JSlider(0, 100, (int) (SoundEngine.getInstance().getVolume() * 100));
        volSlider.setOpaque(false);
        volSlider.setFocusable(false);

        int currentPct = (int) (SoundEngine.getInstance().getVolume() * 100);
        volumeValLabel = new JLabel(formatVolumeBar(currentPct) + "  " + currentPct + "%");
        volumeValLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        volumeValLabel.setForeground(UITheme.ACCENT_AMBER);
        volumeValLabel.setPreferredSize(new Dimension(170, 30));

        volSlider.addChangeListener(e -> {
            int val = volSlider.getValue();
            settings.setVolumePercent(val);
            SoundEngine.getInstance().setVolume(val / 100.0f);
            volumeValLabel.setText(formatVolumeBar(val) + "  " + val + "%");
        });

        volRow.add(volSlider, BorderLayout.CENTER);
        volRow.add(volumeValLabel, BorderLayout.EAST);
        content.add(volRow);
        content.add(Box.createVerticalStrut(18));

        // D. Word Length
        content.add(createSectionHeader("D. Word Length (Vocabulary Complexity)"));
        JPanel lenRow = new JPanel(new GridLayout(1, 5, 8, 0));
        lenRow.setOpaque(false);
        lenRow.setMaximumSize(new Dimension(460, 38));

        ButtonGroup lenGroup = new ButtonGroup();
        for (GameSettings.WordLengthFilter wf : GameSettings.WordLengthFilter.values()) {
            JToggleButton btn = createToggleOption(wf.getLabel(), settings.getWordLengthFilter() == wf);
            btn.addActionListener(e -> settings.setWordLengthFilter(wf));
            lenGroup.add(btn);
            lenRow.add(btn);
        }
        content.add(lenRow);
        content.add(Box.createVerticalStrut(6));

        JLabel lenNote = new JLabel("Fixed length generates exclusively words matching your selection.");
        lenNote.setFont(UITheme.FONT_SMALL);
        lenNote.setForeground(UITheme.TEXT_MUTED);
        lenNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lenNote);

        mainCard.add(content, BorderLayout.CENTER);

        // Bottom apply button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottom.setOpaque(false);
        JButton applyBtn = UITheme.createStyledButton("APPLY & SAVE SETTINGS", UITheme.ACCENT_GREEN, 320, 44);
        applyBtn.addActionListener(e -> {
            SoundEngine.getInstance().playWordComplete();
            dispose();
        });
        bottom.add(applyBtn);
        mainCard.add(bottom, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }

    private JLabel createSectionHeader(String title) {
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_SUBHEADER);
        l.setForeground(UITheme.ACCENT_CYAN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    private JToggleButton createToggleOption(String label, boolean isSelected) {
        JToggleButton tb = new JToggleButton(label, isSelected);
        tb.setFont(UITheme.FONT_BODY_BOLD);
        tb.setForeground(isSelected ? Color.WHITE : UITheme.TEXT_SECONDARY);
        tb.setBackground(isSelected ? UITheme.ACCENT_CYAN.darker() : UITheme.BG_CARD);
        tb.setFocusPainted(false);
        tb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tb.setBorder(BorderFactory.createLineBorder(isSelected ? UITheme.ACCENT_CYAN : UITheme.BORDER_COLOR, 1, true));

        tb.addActionListener(e -> {
            Container parent = tb.getParent();
            if (parent != null) {
                for (Component c : parent.getComponents()) {
                    if (c instanceof JToggleButton other) {
                        boolean sel = other.isSelected();
                        other.setForeground(sel ? Color.WHITE : UITheme.TEXT_SECONDARY);
                        other.setBackground(sel ? UITheme.ACCENT_CYAN.darker() : UITheme.BG_CARD);
                        other.setBorder(BorderFactory.createLineBorder(sel ? UITheme.ACCENT_CYAN : UITheme.BORDER_COLOR, 1, true));
                    }
                }
            }
        });
        return tb;
    }

        private String formatVolumeBar(int pct) {
        int totalTicks = 11;
        int knobPos = (int) Math.round((pct / 100.0) * (totalTicks - 1));
        StringBuilder sb = new StringBuilder("[ ");
        for (int i = 0; i < totalTicks; i++) {
            if (i == knobPos) {
                sb.append("O");
            } else {
                sb.append("-");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }
}
