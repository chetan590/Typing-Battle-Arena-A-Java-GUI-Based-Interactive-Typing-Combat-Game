package com.typingbattle.ui;

import com.typingbattle.model.CharacterType;
import com.typingbattle.model.Fighter;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Custom Java 2D vector renderer for all 5 fighter classes with full animation states:
 * Idle breathing, Attack lunges, Hurt recoils, Special charges, and Victory poses.
 */
public class FighterRenderer {

    public static void renderFighter(Graphics2D g2, Fighter fighter, int x, int y,
                                     boolean facingRight, long globalTimeMs) {
        if (fighter == null) return;

        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Store fighter coordinates for particle hit targets
        fighter.setX(x);
        fighter.setY(y);

        // Calculate animation offsets
        double animOffsetX = 0;
        double animOffsetY = 0;
        double rotationRad = 0;
        float hurtFlash = 0f;

        Fighter.State state = fighter.getState();
        long elapsed = fighter.getStateElapsedMs();
        long duration = fighter.getStateDurationMs();
        double progress = (duration > 0) ? Math.min(1.0, (double) elapsed / duration) : 0;

        switch (state) {
            case IDLE:
                // Gentle breathing bob
                animOffsetY = Math.sin(globalTimeMs / 220.0) * 4.0;
                break;
            case ATTACKING:
                // Forward dash lunge then return
                double lunge = Math.sin(progress * Math.PI);
                animOffsetX = (facingRight ? 1 : -1) * (lunge * 45.0);
                animOffsetY = -Math.abs(Math.sin(progress * Math.PI)) * 10.0;
                break;
            case HURT:
                // Knockback recoil and flash
                double recoil = (1.0 - progress);
                animOffsetX = (facingRight ? -1 : 1) * (recoil * 28.0);
                animOffsetY = Math.sin(progress * Math.PI * 4) * 3.0; // vibrate
                hurtFlash = (float) (0.6 * (1.0 - progress));
                break;
            case SPECIAL:
                // Levitating in air with pulsing energy
                animOffsetY = -22.0 + Math.sin(globalTimeMs / 100.0) * 6.0;
                break;
            case VICTORY:
                // Victorious bouncing celebration
                animOffsetY = -Math.abs(Math.sin(globalTimeMs / 200.0)) * 12.0;
                break;
            case DEFEATED:
                // Fallen to knees
                animOffsetY = 22.0;
                rotationRad = (facingRight ? 1 : -1) * 0.35;
                break;
        }

        // Apply transforms
        AffineTransform oldTx = g.getTransform();
        g.translate(x + animOffsetX, y + animOffsetY);
        if (rotationRad != 0) {
            g.rotate(rotationRad);
        }
        if (!facingRight) {
            g.scale(-1, 1);
        }

        // Draw character shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(-35, 75, 70, 16);

        // Draw character model
        CharacterType type = fighter.getProfile().getType();
        switch (type) {
            case NINJA:
                drawNinja(g, fighter, globalTimeMs, state, progress);
                drawNinja(g, fighter, globalTimeMs, state);
                break;
            case SAMURAI:
                drawSamurai(g, fighter, globalTimeMs, state, progress);
                drawSamurai(g, fighter, globalTimeMs, state);
                break;
            case MAGE:
                drawMage(g, fighter, globalTimeMs, state, progress);
                drawMage(g, fighter, globalTimeMs, state);
                break;
            case ROBOT_WARRIOR:
                drawRobot(g, fighter, globalTimeMs, state, progress);
                drawRobot(g, fighter, globalTimeMs, state);
                break;
            case SHADOW_FIGHTER:
            default:
                drawShadowFighter(g, fighter, globalTimeMs, state, progress);
                drawShadowFighter(g, fighter, globalTimeMs, state);
                break;
        }

        // Special aura / glowing rings if in SPECIAL state
        if (state == Fighter.State.SPECIAL) {
            drawSpecialAura(g, fighter, globalTimeMs);
        }

        // Hurt flash overlay (white/red)
        if (hurtFlash > 0.05f) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, hurtFlash));
            g.setColor(Color.RED);
            g.fillRect(-45, -70, 90, 150);
        }

        g.setTransform(oldTx);
        g.dispose();
    }

    // ==========================================
    // Character Specific Java 2D Visual Models
    // ==========================================

    private static void drawNinja(Graphics2D g, Fighter f, long t, Fighter.State state, double prog) {
    private static void drawNinja(Graphics2D g, Fighter f, long t, Fighter.State state) {
        Color primary = f.getProfile().getPrimaryColor(); // Emerald Cyan

        // Headband ribbons flowing behind
        g.setColor(primary);
        int ribbonWave = (int) (Math.sin(t / 150.0) * 8);
        g.setStroke(new BasicStroke(4.0f));
        g.drawLine(-18, -48, -45, -45 + ribbonWave);
        g.drawLine(-18, -44, -40, -38 + ribbonWave);

        // Body & Shinobi Vest
        g.setColor(new Color(25, 30, 40));
        g.fillRoundRect(-18, -35, 36, 60, 10, 10);
        g.setColor(primary);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(-12, -35, 12, 10); // Belt strap
        g.drawLine(12, -35, -12, 10);

        // Legs
        g.setColor(new Color(20, 25, 35));
        g.fillRect(-16, 25, 12, 50);
        g.fillRect(4, 25, 12, 50);
        // Shin wraps
        g.setColor(primary);
        g.fillRect(-16, 50, 12, 6);
        g.fillRect(4, 50, 12, 6);

        // Head & Mask
        g.setColor(new Color(20, 25, 35));
        g.fillOval(-20, -65, 40, 40);
        // Face opening & Ninja Eyes
        g.setColor(new Color(245, 215, 185));
        g.fillRect(-10, -53, 22, 10);
        g.setColor(Color.BLACK);
        g.fillRect(-2, -50, 5, 3); // Eye
        // Forehead Protector
        g.setColor(new Color(170, 180, 195));
        g.fillRoundRect(-14, -62, 28, 8, 4, 4);

        // Arms & Weapons (Kunai / Shuriken)
        g.setColor(new Color(25, 30, 40));
        if (state == Fighter.State.ATTACKING) {
            // Extended arm throwing kunai
            g.fillRect(10, -30, 35, 10);
            // Steel Kunai
            g.setColor(Color.WHITE);
            int[] kx = {45, 65, 45};
            int[] ky = {-28, -25, -22};
            g.fillPolygon(kx, ky, 3);
        } else if (state == Fighter.State.VICTORY) {
            // Arms raised high
            g.fillRect(10, -55, 10, 35);
            g.setColor(Color.WHITE);
            g.fillOval(8, -65, 14, 14); // Spinning shuriken
        } else {
            // Guard stance
            g.fillRect(10, -25, 12, 35);
            // Kunai in hand
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(18, 5, 28, -10);
        }
    }

    private static void drawSamurai(Graphics2D g, Fighter f, long t, Fighter.State state, double prog) {
    private static void drawSamurai(Graphics2D g, Fighter f, long t, Fighter.State state) {
        Color primary = f.getProfile().getPrimaryColor(); // Crimson Red

        // Kabuto Helmet Crest (Gold crescent)
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3.5f));
        g.drawArc(-18, -80, 36, 26, 0, 180);

        // Samurai Kabuto Helmet
        g.setColor(new Color(40, 20, 25));
        g.fillOval(-22, -68, 44, 42);
        // Face Mask (Menpo)
        g.setColor(new Color(180, 30, 30));
        g.fillRect(-12, -48, 24, 16);
        // Fierce Eye
        g.setColor(Color.WHITE);
        g.fillRect(-2, -52, 6, 3);

        // Heavy Armor Chestplate (Do)
        g.setColor(new Color(160, 25, 25));
        g.fillRoundRect(-22, -32, 44, 58, 12, 12);
        // Golden Armor Rivets
        g.setColor(new Color(255, 215, 0));
        g.fillRect(-18, -22, 36, 4);
        g.fillRect(-18, -10, 36, 4);
        g.fillRect(-18, 2, 36, 4);

        // Shoulder Armor (Sode)
        g.setColor(new Color(130, 15, 15));
        g.fillRoundRect(-30, -32, 14, 28, 6, 6);
        g.fillRoundRect(16, -32, 14, 28, 6, 6);

        // Armor Skirt (Kusazuri) & Legs
        g.setColor(new Color(100, 20, 25));
        g.fillRect(-20, 26, 40, 20);
        g.setColor(new Color(30, 20, 25));
        g.fillRect(-16, 44, 12, 32);
        g.fillRect(4, 44, 12, 32);

        // Katana Weapon
        if (state == Fighter.State.ATTACKING) {
            // Devastating slash arc
            g.setColor(new Color(255, 255, 255, 220));
            g.setStroke(new BasicStroke(5.0f));
            g.drawArc(0, -60, 90, 90, -45, 90);
            // Steel Blade
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3.0f));
            g.drawLine(15, -15, 75, -45);
        } else if (state == Fighter.State.VICTORY) {
            // Upright katana salute
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3.5f));
            g.drawLine(20, 10, 20, -75);
            // Gold hilt
            g.setColor(new Color(255, 215, 0));
            g.fillRect(16, 5, 8, 14);
        } else {
            // Hand on scabbard ready to draw
            g.setColor(new Color(40, 20, 20));
            g.fillRect(-10, 10, 35, 8); // Scabbard
            g.setColor(new Color(255, 215, 0));
            g.fillRect(22, 6, 12, 6); // Hilt
        }
    }

    private static void drawMage(Graphics2D g, Fighter f, long t, Fighter.State state, double prog) {
    private static void drawMage(Graphics2D g, Fighter f, long t, Fighter.State state) {
        Color primary = f.getProfile().getPrimaryColor(); // Arcane Violet

        // Wizard Cloak & Robe
        g.setColor(new Color(60, 25, 95));
        int[] rx = {-25, 25, 32, -32};
        int[] ry = {-25, -25, 75, 75};
        g.fillPolygon(rx, ry, 4);

        // Glowing Runic Trim
        g.setColor(primary);
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(0, -25, 0, 75);
        g.drawRect(-8, 55, 16, 12);

        // Mage Hood & Mysterious Shadowed Face
        g.setColor(new Color(45, 15, 75));
        int[] hx = {-22, 22, 0};
        int[] hy = {-38, -38, -75};
        g.fillPolygon(hx, hy, 3);
        g.fillOval(-18, -55, 36, 32);

        // Glowing Arcane Eyes
        g.setColor(primary);
        g.fillOval(0, -48, 6, 6);
        g.setColor(Color.WHITE);
        g.fillOval(2, -47, 3, 3);

        // Arcane Staff
        int staffX = 28;
        g.setColor(new Color(110, 70, 45));
        g.setStroke(new BasicStroke(4.0f));
        g.drawLine(staffX, 70, staffX, -50);

        // Glowing Staff Crystal
        int crystalY = (int) (-58 + Math.sin(t / 180.0) * 5);
        g.setColor(primary);
        g.fillOval(staffX - 9, crystalY - 9, 18, 18);
        g.setColor(Color.WHITE);
        g.fillOval(staffX - 4, crystalY - 4, 8, 8);

        // Magic Cast effect
        if (state == Fighter.State.ATTACKING) {
            g.setColor(new Color(200, 120, 255, 200));
            g.fillOval(45, -35, 35, 35);
            g.setColor(Color.WHITE);
            g.fillOval(52, -28, 20, 20);
        }
    }

    private static void drawRobot(Graphics2D g, Fighter f, long t, Fighter.State state, double prog) {
    private static void drawRobot(Graphics2D g, Fighter f, long t, Fighter.State state) {
        Color primary = f.getProfile().getPrimaryColor(); // High-Tech Cyan

        // Heavy Mech Chassis Torso
        g.setColor(new Color(45, 60, 75));
        g.fillRoundRect(-24, -35, 48, 65, 8, 8);
        // Cyber Armor Plates
        g.setColor(new Color(30, 40, 52));
        g.fillRect(-18, -25, 36, 18);
        // Reactor Core
        double pulse = 0.7 + Math.sin(t / 160.0) * 0.3;
        g.setColor(new Color(0, 200, 255, (int) (pulse * 255)));
        g.fillOval(-8, 5, 16, 16);

        // Mech Head & Visor
        g.setColor(new Color(35, 48, 60));
        g.fillRoundRect(-18, -65, 36, 28, 6, 6);
        // Glowing Neon Visor
        g.setColor(primary);
        g.fillRect(-12, -54, 24, 8);
        g.setColor(Color.WHITE);
        g.fillRect(-2, -52, 6, 4);

        // Heavy Piston Legs
        g.setColor(new Color(30, 40, 50));
        g.fillRect(-20, 30, 14, 45);
        g.fillRect(6, 30, 14, 45);
        g.setColor(primary);
        g.fillRect(-20, 55, 14, 5);
        g.fillRect(6, 55, 14, 5);

        // Arm Cannon Weapon
        if (state == Fighter.State.ATTACKING) {
            // Heavy Plasma Cannon extended
            g.setColor(new Color(30, 45, 60));
            g.fillRect(15, -20, 38, 16);
            // Barrel Glow
            g.setColor(primary);
            g.fillOval(45, -22, 12, 20);
            // Laser beam projectile
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(6.0f));
            g.drawLine(55, -12, 100, -12);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.0f));
            g.drawLine(55, -12, 100, -12);
        } else {
            // Heavy Arm resting
            g.setColor(new Color(35, 48, 60));
            g.fillRect(15, -15, 14, 38);
            g.setColor(primary);
            g.fillRect(15, 15, 14, 6);
        }
    }

    private static void drawShadowFighter(Graphics2D g, Fighter f, long t, Fighter.State state, double prog) {
    private static void drawShadowFighter(Graphics2D g, Fighter f, long t, Fighter.State state) {
        Color primary = f.getProfile().getPrimaryColor(); // Void Amber / Dark Gold

        // Floating Shadow Wisps at feet
        g.setColor(new Color(15, 15, 25, 200));
        int wisp = (int) (Math.sin(t / 140.0) * 10);
        g.fillOval(-30 + wisp, 65, 60, 16);

        // Shadow Cowl & Cloak
        g.setColor(new Color(20, 18, 28));
        int[] cx = {-26, 26, 35, -35};
        int[] cy = {-30, -30, 72, 72};
        g.fillPolygon(cx, cy, 4);

        // Dark Hood
        g.setColor(new Color(15, 12, 22));
        g.fillOval(-22, -66, 44, 40);

        // Glowing Void Spectral Eyes
        g.setColor(primary);
        g.fillOval(-2, -50, 8, 6);
        g.setColor(Color.WHITE);
        g.fillOval(0, -49, 4, 3);

        // Dual Void Daggers
        if (state == Fighter.State.ATTACKING) {
            // Cross-slash strike
            g.setColor(primary);
            g.setStroke(new BasicStroke(3.5f));
            g.drawLine(10, -25, 65, -5);
            g.drawLine(10, 5, 65, -35);
            g.setColor(Color.WHITE);
            g.drawLine(15, -20, 60, -8);
        } else {
            // Reverse grip dagger
            g.setColor(primary);
            g.setStroke(new BasicStroke(3.0f));
            g.drawLine(15, 0, 30, 30);
            g.setColor(Color.BLACK);
            g.fillRect(12, -2, 8, 8); // Hilt
        }
    }

    private static void drawSpecialAura(Graphics2D g, Fighter f, long t) {
        Color primary = f.getProfile().getPrimaryColor();
        double pulse = 0.5 + Math.sin(t / 80.0) * 0.4;
        g.setColor(new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), (int) (pulse * 180)));
        g.setStroke(new BasicStroke(4.0f));
        int radius = (int) (65 + Math.sin(t / 120.0) * 12);
        g.drawOval(-radius, -radius - 10, radius * 2, radius * 2);

        // Second outer ring
        g.setColor(new Color(255, 255, 255, 120));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(-radius - 12, -radius - 22, (radius + 12) * 2, (radius + 12) * 2);
    }
}

