package com.typingbattle.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Custom Java 2D vector backdrop renderer generating atmospheric combat environments:
 * Dojo, Cyberpunk City, Mystic Temple, Volcanic Core, and Keyboard Sanctum.
 */
public class ArenaBackgroundRenderer {

    public static void renderBackground(Graphics2D g2, String theme, int width, int height, long timeMs) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (theme == null) theme = "CYBERPUNK";

        switch (theme) {
            case "DOJO":
                drawDojo(g, width, height, timeMs);
                break;
            case "MYSTIC_TEMPLE":
                drawMysticTemple(g, width, height, timeMs);
                break;
            case "VOLCANIC_CORE":
                drawVolcanicCore(g, width, height, timeMs);
                break;
            case "KEYBOARD_SANCTUM":
                drawKeyboardSanctum(g, width, height, timeMs);
                break;
            case "CYBERPUNK":
            default:
                drawCyberpunk(g, width, height, timeMs);
                break;
        }

        g.dispose();
    }

    private static void drawCyberpunk(Graphics2D g, int w, int h, long t) {
        // Night Sky Gradient
        GradientPaint sky = new GradientPaint(0, 0, new Color(10, 10, 30), 0, h * 0.65f, new Color(30, 20, 55));
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Distant Cyber Skyscrapers
        g.setColor(new Color(20, 15, 40));
        for (int i = 0; i < w; i += 75) {
            int bHeight = 150 + ((i * 37) % 220);
            g.fillRect(i, (int) (h * 0.65) - bHeight, 65, bHeight);

            // Windows
            g.setColor(new Color(0, 230, 255, 60));
            for (int wy = (int) (h * 0.65) - bHeight + 15; wy < (int) (h * 0.65) - 20; wy += 22) {
                if ((i + wy) % 3 == 0) {
                    g.fillRect(i + 12, wy, 8, 12);
                    g.fillRect(i + 32, wy, 8, 12);
                }
            }
            g.setColor(new Color(20, 15, 40));
        }

        // Cyber Grid Arena Floor
        int floorY = (int) (h * 0.65);
        GradientPaint floor = new GradientPaint(0, floorY, new Color(18, 16, 32), 0, h, new Color(8, 6, 16));
        g.setPaint(floor);
        g.fillRect(0, floorY, w, h - floorY);

        // Glowing Neon Ground Lines
        g.setColor(new Color(6, 182, 212, 120)); // Neon Cyan
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(0, floorY, w, floorY);

        // Perspective grid lines
        for (int x = 0; x <= w; x += 110) {
            g.drawLine(x, floorY, (int) (w / 2 + (x - w / 2) * 1.8), h);
        }
        for (int y = floorY + 25; y < h; y += 35) {
            g.drawLine(0, y, w, y);
        }
    }

    private static void drawDojo(Graphics2D g, int w, int h, long t) {
        // Warm dusk sky
        GradientPaint sky = new GradientPaint(0, 0, new Color(45, 20, 20), 0, h * 0.65f, new Color(85, 40, 30));
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Bamboo Silhouettes in background
        g.setColor(new Color(30, 15, 15));
        for (int x = 10; x < w; x += 45) {
            g.setStroke(new BasicStroke(7.0f));
            g.drawLine(x, (int) (h * 0.65), x, (int) (h * 0.2));
            // Bamboo segments
            for (int seg = (int) (h * 0.25); seg < (int) (h * 0.65); seg += 35) {
                g.drawLine(x - 5, seg, x + 5, seg);
            }
        }

        // Wooden Dojo Arena Floor
        int floorY = (int) (h * 0.65);
        GradientPaint floor = new GradientPaint(0, floorY, new Color(90, 50, 25), 0, h, new Color(40, 20, 10));
        g.setPaint(floor);
        g.fillRect(0, floorY, w, h - floorY);

        // Polished Wood Floor Planks
        g.setColor(new Color(60, 30, 15));
        g.setStroke(new BasicStroke(1.5f));
        for (int y = floorY + 20; y < h; y += 28) {
            g.drawLine(0, y, w, y);
        }

        // Glowing Paper Lanterns
        drawLantern(g, 120, 80, t);
        drawLantern(g, w - 120, 80, t + 400);
    }

    private static void drawLantern(Graphics2D g, int x, int y, long t) {
        // String
        g.setColor(Color.BLACK);
        g.drawLine(x, 0, x, y);

        // Glowing Core
        double pulse = 0.8 + Math.sin(t / 200.0) * 0.2;
        g.setColor(new Color(255, 180, 50, (int) (pulse * 80)));
        g.fillOval(x - 30, y - 10, 60, 60);

        // Lantern Body
        g.setColor(new Color(230, 60, 40));
        g.fillRoundRect(x - 16, y, 32, 42, 10, 10);
        g.setColor(new Color(255, 220, 100));
        g.fillRect(x - 8, y + 8, 16, 26);
    }

    private static void drawMysticTemple(Graphics2D g, int w, int h, long t) {
        // Cosmic Violet Starfield
        GradientPaint sky = new GradientPaint(0, 0, new Color(15, 10, 35), 0, h * 0.65f, new Color(45, 15, 70));
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Floating Runic Monoliths
        g.setColor(new Color(70, 40, 100));
        int m1Y = (int) (h * 0.25 + Math.sin(t / 400.0) * 12);
        int m2Y = (int) (h * 0.20 + Math.sin((t + 500) / 400.0) * 12);
        g.fillRoundRect(100, m1Y, 45, 140, 12, 12);
        g.fillRoundRect(w - 145, m2Y, 45, 140, 12, 12);

        // Glowing runes on monoliths
        g.setColor(new Color(190, 100, 255, 180));
        g.drawString("ᚱ", 115, m1Y + 40);
        g.drawString("ᛗ", 115, m1Y + 80);
        g.drawString("ᚹ", w - 130, m2Y + 40);
        g.drawString("ᛟ", w - 130, m2Y + 80);

        // Stone Temple Platform
        int floorY = (int) (h * 0.65);
        GradientPaint floor = new GradientPaint(0, floorY, new Color(35, 25, 55), 0, h, new Color(15, 10, 25));
        g.setPaint(floor);
        g.fillRect(0, floorY, w, h - floorY);

        // Arcane Floor Glyphs
        g.setColor(new Color(160, 70, 245, 100));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(w / 2 - 180, floorY + 10, 360, 100);
        g.drawOval(w / 2 - 130, floorY + 25, 260, 70);
    }

    private static void drawVolcanicCore(Graphics2D g, int w, int h, long t) {
        // Red Molten Haze
        GradientPaint sky = new GradientPaint(0, 0, new Color(30, 10, 10), 0, h * 0.65f, new Color(80, 20, 10));
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Obsidian Spikes in background
        g.setColor(new Color(25, 15, 15));
        int[] sx = {50, 110, 180, 240, 320, 400, 500, w - 200, w - 100, w};
        int[] sy = {(int)(h*0.65), (int)(h*0.3), (int)(h*0.65), (int)(h*0.25), (int)(h*0.65), (int)(h*0.35), (int)(h*0.65), (int)(h*0.28), (int)(h*0.65), (int)(h*0.4)};
        g.fillPolygon(sx, sy, sx.length);

        // Lava Arena Floor
        int floorY = (int) (h * 0.65);
        g.setColor(new Color(20, 10, 10));
        g.fillRect(0, floorY, w, h - floorY);

        // Molten Lava River
        double lavaWave = Math.sin(t / 250.0) * 6;
        GradientPaint lava = new GradientPaint(0, floorY + 30, new Color(255, 120, 0), 0, h, new Color(220, 40, 0));
        g.setPaint(lava);
        g.fillRect(0, (int) (floorY + 35 + lavaWave), w, h - floorY);

        // Floating Rock Platform
        g.setColor(new Color(35, 25, 25));
        g.fillRoundRect(80, floorY - 5, w - 160, 45, 18, 18);
        g.setColor(new Color(255, 100, 0, 180));
        g.drawRoundRect(80, floorY - 5, w - 160, 45, 18, 18);
    }

    private static void drawKeyboardSanctum(Graphics2D g, int w, int h, long t) {
        // Celestial Space Void
        GradientPaint sky = new GradientPaint(0, 0, new Color(5, 5, 20), 0, h * 0.65f, new Color(20, 15, 45));
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Pulsating Legendary KEYBOARD CORE in the center sky
        int coreX = w / 2;
        int coreY = (int) (h * 0.32 + Math.sin(t / 300.0) * 10);

        // Core Outer Glow Aura
        double pulse = 0.6 + Math.sin(t / 120.0) * 0.4;
        g.setColor(new Color(255, 215, 0, (int) (pulse * 90)));
        g.fillOval(coreX - 90, coreY - 90, 180, 180);
        g.setColor(new Color(0, 230, 255, (int) (pulse * 70)));
        g.fillOval(coreX - 65, coreY - 65, 130, 130);

        // Core Golden Relic (Floating Giant Mechanical Keyboard Keycap)
        g.setColor(new Color(25, 28, 45));
        g.fillRoundRect(coreX - 45, coreY - 45, 90, 90, 20, 20);
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3.5f));
        g.drawRoundRect(coreX - 45, coreY - 45, 90, 90, 20, 20);

        // Glowing "CORE" Lettering on the relic
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString("CORE", coreX - fm.stringWidth("CORE") / 2, coreY + 8);

        // Floating orbiting keystroke particles: [A], [W], [S], [D], [SPACE]
        String[] keys = {"[A]", "[W]", "[S]", "[D]", "[ENTER]"};
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        for (int i = 0; i < keys.length; i++) {
            double angle = (t / 600.0) + (i * (Math.PI * 2 / keys.length));
            int kx = (int) (coreX + Math.cos(angle) * 135);
            int ky = (int) (coreY + Math.sin(angle) * 45);

            g.setColor(new Color(20, 25, 45, 220));
            g.fillRoundRect(kx - 16, ky - 12, 32, 24, 6, 6);
            g.setColor(new Color(0, 230, 255));
            g.drawRoundRect(kx - 16, ky - 12, 32, 24, 6, 6);
            g.drawString(keys[i], kx - 11, ky + 5);
        }

        // Crystalline Core Platform Floor
        int floorY = (int) (h * 0.65);
        GradientPaint floor = new GradientPaint(0, floorY, new Color(20, 22, 40), 0, h, new Color(10, 10, 22));
        g.setPaint(floor);
        g.fillRect(0, floorY, w, h - floorY);

        // Golden circuits on the floor
        g.setColor(new Color(255, 215, 0, 130));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(coreX, floorY, coreX, h);
        g.drawLine(coreX - 180, floorY + 30, coreX + 180, floorY + 30);
        g.drawOval(coreX - 120, floorY + 15, 240, 80);
    }
}

