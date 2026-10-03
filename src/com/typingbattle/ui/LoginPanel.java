package com.typingbattle.ui;

import com.typingbattle.engine.SoundEngine;
import com.typingbattle.model.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * High-tech dummy authentication terminal serving as the first gateway screen.
 * Perfectly aligned with consistent centering and pure English typography.
 */
@SuppressWarnings("serial")
public class LoginPanel extends JPanel {

    private final ScreenManager screenManager;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(35, 20, 15, 20));

        JLabel titleLabel = new JLabel("TYPING BATTLE ARENA");
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("NEURAL TERMINAL // ACCESS GATEWAY");
        subLabel.setFont(UITheme.FONT_SUBHEADER);
        subLabel.setForeground(UITheme.ACCENT_CYAN);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Login Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel loginCard = UITheme.createCardPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setPreferredSize(new Dimension(440, 420));
        loginCard.setMaximumSize(new Dimension(440, 420));
        loginCard.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel cardTitle = new JLabel("PILOT AUTHENTICATION");
        cardTitle.setFont(UITheme.FONT_HEADER);
        cardTitle.setForeground(Color.WHITE);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(cardTitle);
        loginCard.add(Box.createVerticalStrut(20));

        // Username section container for exact alignment
        JPanel userBox = new JPanel();
        userBox.setOpaque(false);
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));
        userBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        userBox.setMaximumSize(new Dimension(360, 68));

        JLabel userLabel = new JLabel("Operator / Pilot ID");
        userLabel.setFont(UITheme.FONT_SMALL);
        userLabel.setForeground(UITheme.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField("warrior");
        styleInput(usernameField);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        userBox.add(userLabel);
        userBox.add(Box.createVerticalStrut(4));
        userBox.add(usernameField);
        loginCard.add(userBox);
        loginCard.add(Box.createVerticalStrut(12));

        // Password section container for exact alignment
        JPanel passBox = new JPanel();
        passBox.setOpaque(false);
        passBox.setLayout(new BoxLayout(passBox, BoxLayout.Y_AXIS));
        passBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        passBox.setMaximumSize(new Dimension(360, 68));

        JLabel passLabel = new JLabel("Security Passcode");
        passLabel.setFont(UITheme.FONT_SMALL);
        passLabel.setForeground(UITheme.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField("arena123");
        styleInput(passwordField);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        passBox.add(passLabel);
        passBox.add(Box.createVerticalStrut(4));
        passBox.add(passwordField);
        loginCard.add(passBox);
        loginCard.add(Box.createVerticalStrut(12));

        // Status Label
        statusLabel = new JLabel("System Ready. Enter credentials or use Quick Demo.");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(statusLabel);
        loginCard.add(Box.createVerticalStrut(18));

        // Login Action Buttons
        JButton loginBtn = UITheme.createStyledButton("AUTHENTICATE", UITheme.ACCENT_GREEN, 360, 46);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> attemptLogin());
        loginCard.add(loginBtn);
        loginCard.add(Box.createVerticalStrut(10));

        JButton demoBtn = UITheme.createStyledButton("QUICK DEMO LOGIN", UITheme.ACCENT_CYAN, 360, 42);
        demoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        demoBtn.addActionListener(e -> {
            usernameField.setText("Chetan");
            passwordField.setText("arena123");
            attemptLogin();
        });
        loginCard.add(demoBtn);

        // Enter key listener
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        centerPanel.add(loginCard);
        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 20, 25, 20));
        JLabel tipLabel = new JLabel("Typing Battle Arena v2.0 - Zero External Dependencies - High-Resolution Engine");
        tipLabel.setFont(UITheme.FONT_SMALL);
        tipLabel.setForeground(UITheme.TEXT_MUTED);
        footer.add(tipLabel);
        add(footer, BorderLayout.SOUTH);
    }

    private void styleInput(JTextField field) {
        field.setFont(UITheme.FONT_BODY);
        field.setBackground(new Color(20, 26, 40));
        field.setForeground(Color.WHITE);
        field.setCaretColor(UITheme.ACCENT_CYAN);
        field.setMaximumSize(new Dimension(360, 40));
        field.setPreferredSize(new Dimension(360, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    private void attemptLogin() {
        String u = usernameField.getText().trim();
        if (u.isEmpty()) {
            u = "Chetan";
        }
        statusLabel.setForeground(UITheme.ACCENT_GREEN);
        statusLabel.setText("ACCESS GRANTED. INITIALIZING PILOT SETUP...");
        SoundEngine.getInstance().playWordComplete();

        SessionManager.getInstance().setLoggedIn(true);
        SessionManager.getInstance().setLoginUsername(u);
        SessionManager.getInstance().setPlayerName(u);

        Timer t = new Timer(350, evt -> {
            ((Timer) evt.getSource()).stop();
            screenManager.showPlayerSetup();
        });
        t.setRepeats(false);
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient backdrop
        GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(25, 22, 45));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Decorative background geometric rings
        g2.setColor(new Color(6, 182, 212, 18));
        g2.drawOval(-80, -80, 360, 360);
        g2.drawOval(getWidth() - 280, getHeight() - 280, 460, 460);

        // Grid lines
        g2.setColor(new Color(255, 255, 255, 5));
        for (int x = 0; x < getWidth(); x += 60) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 60) {
            g2.drawLine(0, y, getWidth(), y);
        }

        g2.dispose();
    }
}

