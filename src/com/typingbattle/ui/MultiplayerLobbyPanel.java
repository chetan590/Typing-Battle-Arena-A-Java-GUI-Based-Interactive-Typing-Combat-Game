package com.typingbattle.ui;

import com.typingbattle.engine.*;
import com.typingbattle.model.CharacterProfile;
import com.typingbattle.model.CharacterType;
import com.typingbattle.model.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.net.URI;

/**
 * Multiplayer Matchmaking Lobby generating a unique room session,
 * displaying a scannable QR Code for mobile/tablet companion devices,
 * secondary room code join link, and live player connection synchronization.
 */
@SuppressWarnings("serial")
public class MultiplayerLobbyPanel extends JPanel {

    private final ScreenManager screenManager;
    private MultiplayerSession session;
    private Timer pollTimer;

    // UI elements
    private final JLabel roomCodeLabel;
    private final JLabel p1NameLabel;
    private final JLabel p1FighterLabel;
    private final JLabel p2StatusLabel;
    private final JLabel p2NameLabel;
    private final JLabel p2FighterLabel;
    private final JLabel joinUrlLabel;
    private final JPanel qrCanvas;
    private final JButton startBattleBtn;
    private BufferedImage qrImage;

    public MultiplayerLobbyPanel(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(22, 35, 12, 35));

        JButton backBtn = UITheme.createStyledButton("BACK", UITheme.TEXT_SECONDARY, 110, 38);
        backBtn.addActionListener(e -> {
            stopPolling();
            screenManager.showMainMenu();
        });
        headerPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("TWO-PLAYER MULTIPLAYER ARENA", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE_MED);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton localDuelBtn = UITheme.createStyledButton("LOCAL KEYBOARD DUEL", UITheme.ACCENT_PURPLE, 210, 38);
        localDuelBtn.addActionListener(e -> {
            stopPolling();
            screenManager.showCharacterSelect(true, null);
        });
        headerPanel.add(localDuelBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center: 2-column split (Left: QR Code & Room Info, Right: Player Cards & Battle Start)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 45, 20, 45));

        // LEFT CARD: QR Code & Room Connection Info
        JPanel qrCard = UITheme.createCardPanel();
        qrCard.setLayout(new BoxLayout(qrCard, BoxLayout.Y_AXIS));
        qrCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        roomCodeLabel = new JLabel("ROOM: -----");
        roomCodeLabel.setFont(UITheme.FONT_TITLE_MED);
        roomCodeLabel.setForeground(UITheme.ACCENT_AMBER);
        roomCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCard.add(roomCodeLabel);
        qrCard.add(Box.createVerticalStrut(8));

        JLabel scanPrompt = new JLabel("Scan with Mobile, Tablet, or Laptop Camera");
        scanPrompt.setFont(UITheme.FONT_SMALL);
        scanPrompt.setForeground(UITheme.TEXT_SECONDARY);
        scanPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCard.add(scanPrompt);
        qrCard.add(Box.createVerticalStrut(10));

        // QR Code canvas
        qrCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                if (qrImage != null) {
                    int imgW = qrImage.getWidth();
                    int imgH = qrImage.getHeight();
                    int x = (w - imgW) / 2;
                    int y = (h - imgH) / 2;

                    // Neon frame
                    g2.setColor(UITheme.ACCENT_CYAN);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(x - 4, y - 4, imgW + 8, imgH + 8, 12, 12);

                    g2.drawImage(qrImage, x, y, null);
                } else {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.drawString("Generating QR Code...", w / 2 - 60, h / 2);
                }
                g2.dispose();
            }
        };
        qrCanvas.setOpaque(false);
        qrCanvas.setPreferredSize(new Dimension(240, 240));
        qrCanvas.setMaximumSize(new Dimension(240, 240));
        qrCanvas.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCard.add(qrCanvas);
        qrCard.add(Box.createVerticalStrut(12));

        joinUrlLabel = new JLabel("http://127.0.0.1:8080/join?room=-----");
        joinUrlLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        joinUrlLabel.setForeground(UITheme.ACCENT_CYAN);
        joinUrlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCard.add(joinUrlLabel);
        qrCard.add(Box.createVerticalStrut(10));

        // URL Action Buttons
        JPanel urlBtnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        urlBtnRow.setOpaque(false);

        JButton copyBtn = UITheme.createStyledButton("COPY LINK", UITheme.TEXT_SECONDARY, 140, 36);
        copyBtn.setFont(UITheme.FONT_SMALL);
        copyBtn.addActionListener(e -> {
            if (session != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(session.getJoinUrl()), null);
                JOptionPane.showMessageDialog(this, "Join URL copied to clipboard!\n" + session.getJoinUrl());
            }
        });
        urlBtnRow.add(copyBtn);

        JButton openBrowserBtn = UITheme.createStyledButton("OPEN IN BROWSER", UITheme.TEXT_SECONDARY, 160, 36);
        openBrowserBtn.setFont(UITheme.FONT_SMALL);
        openBrowserBtn.addActionListener(e -> {
            if (session != null) {
                try {
                    Desktop.getDesktop().browse(new URI(session.getJoinUrl()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not open browser: " + ex.getMessage());
                }
            }
        });
        urlBtnRow.add(openBrowserBtn);

        qrCard.add(urlBtnRow);
        centerPanel.add(qrCard);

        // RIGHT CARD: Player Rosters & Battle Launcher
        JPanel playersCard = UITheme.createCardPanel();
        playersCard.setLayout(new BoxLayout(playersCard, BoxLayout.Y_AXIS));
        playersCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lobbyTitle = new JLabel("MULTIPLAYER COMBAT ROSTER");
        lobbyTitle.setFont(UITheme.FONT_HEADER);
        lobbyTitle.setForeground(Color.WHITE);
        lobbyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        playersCard.add(lobbyTitle);
        playersCard.add(Box.createVerticalStrut(18));

        // Player 1 Card (Host)
        JPanel p1Box = UITheme.createCardPanel();
        p1Box.setLayout(new GridLayout(3, 1, 0, 4));
        p1Box.setMaximumSize(new Dimension(440, 90));
        p1Box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        JLabel p1Tag = new JLabel("PLAYER 1 (DESKTOP HOST)");
        p1Tag.setFont(UITheme.FONT_SMALL);
        p1Tag.setForeground(UITheme.ACCENT_CYAN);

        p1NameLabel = new JLabel("Chetan");
        p1NameLabel.setFont(UITheme.FONT_SUBHEADER);
        p1NameLabel.setForeground(Color.WHITE);

        p1FighterLabel = new JLabel("Combatant: Hayato the Silent [Ninja]  -  Ready");
        p1FighterLabel.setFont(UITheme.FONT_SMALL);
        p1FighterLabel.setForeground(UITheme.ACCENT_GREEN);

        p1Box.add(p1Tag);
        p1Box.add(p1NameLabel);
        p1Box.add(p1FighterLabel);
        playersCard.add(p1Box);
        playersCard.add(Box.createVerticalStrut(15));

        // Player 2 Card (Remote Companion)
        JPanel p2Box = UITheme.createCardPanel();
        p2Box.setLayout(new GridLayout(3, 1, 0, 4));
        p2Box.setMaximumSize(new Dimension(440, 90));
        p2Box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        JLabel p2Tag = new JLabel("PLAYER 2 (MOBILE / BROWSER CLIENT)");
        p2Tag.setFont(UITheme.FONT_SMALL);
        p2Tag.setForeground(UITheme.ACCENT_PURPLE);

        p2NameLabel = new JLabel("Waiting for Player 2 to join...");
        p2NameLabel.setFont(UITheme.FONT_SUBHEADER);
        p2NameLabel.setForeground(UITheme.TEXT_MUTED);

        p2FighterLabel = new JLabel("Combatant: Pending selection");
        p2FighterLabel.setFont(UITheme.FONT_SMALL);
        p2FighterLabel.setForeground(UITheme.TEXT_MUTED);

        p2Box.add(p2Tag);
        p2Box.add(p2NameLabel);
        p2Box.add(p2FighterLabel);
        playersCard.add(p2Box);
        playersCard.add(Box.createVerticalStrut(16));

        p2StatusLabel = new JLabel("Listening on local network for scan connection...");
        p2StatusLabel.setFont(UITheme.FONT_SMALL);
        p2StatusLabel.setForeground(UITheme.ACCENT_AMBER);
        p2StatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playersCard.add(p2StatusLabel);
        playersCard.add(Box.createVerticalStrut(20));

        // Start Battle Button
        startBattleBtn = UITheme.createStyledButton("START MULTIPLAYER CLASH", UITheme.ACCENT_GREEN, 380, 50);
        startBattleBtn.setFont(UITheme.FONT_HEADER);
        startBattleBtn.setEnabled(false);
        startBattleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBattleBtn.addActionListener(e -> launchMultiplayerArena());
        playersCard.add(startBattleBtn);

        centerPanel.add(playersCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void initializeLobby() {
        SessionManager sm = SessionManager.getInstance();
        String p1Name = sm.getPlayerName();
        CharacterType p1Type = sm.getPreferredCombatant();

        // Create unique multiplayer room
        this.session = MultiplayerServer.getInstance().createRoom(p1Name, p1Type);

        roomCodeLabel.setText("ROOM: " + session.getRoomCode());
        joinUrlLabel.setText(session.getJoinUrl());

        p1NameLabel.setText(p1Name);
        p1FighterLabel.setText("Combatant: " + CharacterProfile.getProfile(p1Type).getName() + "  -  Ready");

        p2NameLabel.setText("Waiting for Player 2 to join...");
        p2NameLabel.setForeground(UITheme.TEXT_MUTED);
        p2FighterLabel.setText("Combatant: Pending selection");
        p2FighterLabel.setForeground(UITheme.TEXT_MUTED);
        p2StatusLabel.setText("Waiting for Player 2 to scan QR code...");
        startBattleBtn.setEnabled(false);

        // Generate Scannable QR Code
        this.qrImage = QRCodeGenerator.generateQRCode(session.getJoinUrl(), 210);
        qrCanvas.repaint();

        startPolling();
    }

    private void startPolling() {
        stopPolling();
        pollTimer = new Timer(250, e -> {
            if (session == null) return;

            if (session.isP2Connected()) {
                p2NameLabel.setText(session.getP2Name());
                p2NameLabel.setForeground(Color.WHITE);
                CharacterProfile p2Profile = CharacterProfile.getProfile(session.getP2Type());
                p2FighterLabel.setText("Combatant: " + p2Profile.getName() + "  -  Connected & Ready");
                p2FighterLabel.setForeground(UITheme.ACCENT_GREEN);
                p2StatusLabel.setText("PLAYER 2 READY! PRESS BUTTON TO INITIATE CLASH");
                p2StatusLabel.setForeground(UITheme.ACCENT_GREEN);

                if (!startBattleBtn.isEnabled()) {
                    startBattleBtn.setEnabled(true);
                    SoundEngine.getInstance().playWordComplete();
                }
            } else {
                p2NameLabel.setText("Waiting for Player 2 to join...");
                p2NameLabel.setForeground(UITheme.TEXT_MUTED);
                p2FighterLabel.setText("Combatant: Pending selection");
                p2FighterLabel.setForeground(UITheme.TEXT_MUTED);
                p2StatusLabel.setText("Waiting for Player 2 to scan QR code...");
                startBattleBtn.setEnabled(false);
            }
        });
        pollTimer.start();
    }

    private void stopPolling() {
        if (pollTimer != null) {
            pollTimer.stop();
            pollTimer = null;
        }
    }

    private void launchMultiplayerArena() {
        if (session == null || !session.isP2Connected()) return;
        stopPolling();
        session.startBattle();

        CharacterProfile p1Prof = CharacterProfile.getProfile(session.getP1Type());
        CharacterProfile p2Prof = CharacterProfile.getProfile(session.getP2Type());

        screenManager.startOnlineMultiplayerBattle(session, p1Prof, p2Prof);
    }
}
