package com.typingbattle.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Visual styling theme, modern color palettes, typography, and UI component helpers.
 */
public class UITheme {

    // Palette
    public static final Color BG_DARK = new Color(15, 23, 42);       // Deep slate navy #0F172A
    public static final Color BG_CARD = new Color(30, 41, 59);       // Slate card #1E293B
    public static final Color BG_CARD_HOVER = new Color(51, 65, 85); // Slate hover #334155
    public static final Color BORDER_COLOR = new Color(71, 85, 105); // Slate border #475569

    public static final Color ACCENT_CYAN = new Color(6, 182, 212);  // Neon Cyan #06B6D4
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);// Emerald #10B981
    public static final Color ACCENT_RED = new Color(239, 68, 68);   // Crimson #EF4444
    public static final Color ACCENT_AMBER = new Color(245, 158, 11);// Gold Amber #F59E0B
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);// Arcane Violet #8B5CF6
    public static final Color ACCENT_ORANGE = new Color(249, 115, 22);

    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    // Fonts
    public static final Font FONT_TITLE_LARGE = new Font("SansSerif", Font.BOLD, 36);
    public static final Font FONT_TITLE_MED = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_TYPING = new Font("Monospaced", Font.BOLD, 24);
    public static final Font FONT_TYPING_LARGE = new Font("Monospaced", Font.BOLD, 32);

    /**
     * Creates a styled modern button with rounded borders, gradient hover states,
     * and smooth click response.
     */
    public static JButton createStyledButton(String text, Color accentColor, int width, int height) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background fill
                Color fillTop = isPressed ? accentColor.darker() : (isHovered ? accentColor : BG_CARD);
                Color fillBottom = isPressed ? accentColor.darker().darker() : (isHovered ? accentColor.darker() : BG_CARD.darker());
                GradientPaint gp = new GradientPaint(0, 0, fillTop, 0, h, fillBottom);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 14, 14);

                // Border glow
                g2.setColor(isHovered ? Color.WHITE : accentColor);
                g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 14, 14);

                // Text
                FontMetrics fm = g2.getFontMetrics(getFont());
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

                g2.setFont(getFont());
                g2.setColor(isHovered ? Color.WHITE : TEXT_PRIMARY);
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };

        btn.setFont(FONT_SUBHEADER);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setMaximumSize(new Dimension(width, height));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a styled card container.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        return card;
    }
}

