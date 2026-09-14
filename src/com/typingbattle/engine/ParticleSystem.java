package com.typingbattle.engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * High-performance 2D particle engine handling hit sparks, slash trails,
 * floating combat numbers, magic aura bursts, and screen shake.
 */
public class ParticleSystem {

    public static class Particle {
        double x, y;
        double vx, vy;
        Color color;
        float size;
        float alpha = 1.0f;
        float decay;
        int shapeType; // 0 = circle, 1 = star/spark, 2 = line/slash

        public Particle(double x, double y, double vx, double vy, Color color, float size, float decay, int shapeType) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            this.decay = decay;
            this.shapeType = shapeType;
        }

        public boolean update() {
            x += vx;
            y += vy;
            alpha -= decay;
            return alpha <= 0;
        }
    }

    public static class FloatingText {
        double x, y;
        String text;
        Color color;
        Font font;
        float alpha = 1.0f;
        double vy = -1.2;

        public FloatingText(double x, double y, String text, Color color, Font font) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            this.font = font;
        }

        public boolean update() {
            y += vy;
            alpha -= 0.022f;
            return alpha <= 0;
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final List<FloatingText> texts = new ArrayList<>();
    private final Random random = new Random();

    // Screen Shake variables
    private double shakeIntensity = 0;
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;

    public synchronized void triggerScreenShake(double intensity) {
        this.shakeIntensity = Math.max(this.shakeIntensity, intensity);
    }

    public synchronized void spawnHitSparks(double x, double y, Color primaryColor, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 2.0 + random.nextDouble() * 5.0;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            float size = 3.0f + random.nextFloat() * 4.0f;
            float decay = 0.03f + random.nextFloat() * 0.04f;
            int shape = random.nextBoolean() ? 0 : 1;
            particles.add(new Particle(x, y, vx, vy, primaryColor, size, decay, shape));
        }
    }

    public synchronized void spawnSlashEffect(double x, double y, Color color) {
        for (int i = 0; i < 15; i++) {
            double angle = -Math.PI / 4 + (random.nextDouble() - 0.5) * 0.5;
            double speed = 5.0 + random.nextDouble() * 8.0;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, color, 4.0f, 0.05f, 2));
        }
    }

    public synchronized void spawnSpecialBlast(double x, double y, Color color) {
        triggerScreenShake(12.0);
        for (int i = 0; i < 45; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 3.0 + random.nextDouble() * 9.0;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            float size = 5.0f + random.nextFloat() * 8.0f;
            particles.add(new Particle(x, y, vx, vy, color, size, 0.025f, 0));
        }
    }

    public synchronized void spawnFloatingText(double x, double y, String text, Color color, Font font) {
        // slight jitter on spawn
        double jx = x + (random.nextDouble() - 0.5) * 20;
        double jy = y + (random.nextDouble() - 0.5) * 10;
        texts.add(new FloatingText(jx, jy, text, color, font));
    }

    public synchronized void update() {
        // Update particles
        Iterator<Particle> pIt = particles.iterator();
        while (pIt.hasNext()) {
            if (pIt.next().update()) {
                pIt.remove();
            }
        }

        // Update floating texts
        Iterator<FloatingText> tIt = texts.iterator();
        while (tIt.hasNext()) {
            if (tIt.next().update()) {
                tIt.remove();
            }
        }

        // Update screen shake
        if (shakeIntensity > 0.1) {
            shakeOffsetX = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
            shakeOffsetY = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
            shakeIntensity *= 0.88; // decay
        } else {
            shakeIntensity = 0;
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    public synchronized void render(Graphics2D g2) {
        Composite origComposite = g2.getComposite();

        // Render particles
        for (Particle p : particles) {
            if (p.alpha <= 0) continue;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, p.alpha))));
            g2.setColor(p.color);

            int px = (int) p.x;
            int py = (int) p.y;
            int s = (int) p.size;

            if (p.shapeType == 0) {
                g2.fillOval(px - s / 2, py - s / 2, s, s);
            } else if (p.shapeType == 1) {
                // Diamond spark
                int[] xPoints = {px, px + s, px, px - s};
                int[] yPoints = {py - s, py, py + s, py};
                g2.fillPolygon(xPoints, yPoints, 4);
            } else {
                // Slash stroke
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(px - s * 2, py - s, px + s * 2, py + s);
            }
        }

        // Render floating texts with drop shadow
        for (FloatingText ft : texts) {
            if (ft.alpha <= 0) continue;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, ft.alpha))));
            g2.setFont(ft.font);

            int tx = (int) ft.x;
            int ty = (int) ft.y;

            // Black shadow
            g2.setColor(new Color(0, 0, 0, 180));
            g2.drawString(ft.text, tx + 2, ty + 2);

            // Glow / Main text
            g2.setColor(ft.color);
            g2.drawString(ft.text, tx, ty);
        }

        g2.setComposite(origComposite);
    }

    public double getShakeOffsetX() { return shakeOffsetX; }
    public double getShakeOffsetY() { return shakeOffsetY; }

    public synchronized void clear() {
        particles.clear();
        texts.clear();
        shakeIntensity = 0;
        shakeOffsetX = 0;
        shakeOffsetY = 0;
    }
}

