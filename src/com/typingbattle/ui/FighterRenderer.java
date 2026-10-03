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
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

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
                drawNinja(g, fighter, globalTimeMs, state);
                break;
            case SAMURAI:
                drawSamurai(g, fighter, globalTimeMs, state);
                break;
            case MAGE:
                drawMage(g, fighter, globalTimeMs, state);
                break;
            case ROBOT_WARRIOR:
                drawRobot(g, fighter, globalTimeMs, state);
                break;
            case SHADOW_FIGHTER:
            default:
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

        // Head & Mask
        g.setColor(new Color(20, 24, 32));
        g.fillOval(-16, -65, 32, 32);
        // Shinobi Headband
        g.setColor(primary);
        g.fillRect(-16, -58, 32, 8);
        g.setColor(Color.WHITE);
        g.fillOval(-3, -56, 6, 4); // Metal clan plate

        // Glowing Ninja Eyes
        g.setColor(primary);
        g.fillRect(-4, -48, 8, 4);
        g.setColor(Color.WHITE);
        g.fillRect(0, -47, 4, 2);

        // Legs & Tabi Boots
        g.setColor(new Color(20, 24, 32));
        g.fillRect(-15, 25, 12, 45);
        g.fillRect(3, 25, 12, 45);
        g.setColor(primary);
        g.fillRect(-15, 52, 12, 6); // Leg wraps
        g.fillRect(3, 52, 12, 6);

        // Arms & Katana / Shuriken
        g.setColor(new Color(25, 30, 40));
        if (state == Fighter.State.ATTACKING) {
            // Extended Katana Strike
            g.fillRect(10, -25, 30, 10);
            g.setColor(Color.LIGHT_GRAY);
            g.setStroke(new BasicStroke(3.5f));
            g.drawLine(35, -20, 80, -20); // Katana blade
            g.setColor(primary);
            g.drawLine(70, -20, 80, -20); // Energy tip
        } else {
            // Guard stance
            g.fillRect(10, -25, 12, 35);
            // Kunai in hand
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(18, 5, 28, -10);
        }
    }

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

        // Heavy Samurai Armor Plates (Do)
        g.setColor(new Color(35, 18, 22));
        g.fillRoundRect(-22, -32, 44, 62, 8, 8);
        g.setColor(primary);
        g.fillRect(-20, -25, 40, 6);
        g.fillRect(-20, -12, 40, 6);
        g.fillRect(-20, 1, 40, 6);

        // Gold Crest on chest
        g.setColor(new Color(255, 215, 0));
        g.fillOval(-6, -6, 12, 12);

        // Armored Sode (Shoulder Guards)
        g.setColor(primary);
        g.fillRoundRect(-32, -30, 12, 28, 4, 4);
        g.fillRoundRect(20, -30, 12, 28, 4, 4);

        // Hakama Armor Pants & Boots
        g.setColor(new Color(30, 15, 20));
        g.fillRect(-18, 30, 15, 45);
        g.fillRect(3, 30, 15, 45);

        // Katana Weapon
        if (state == Fighter.State.ATTACKING) {
            // Sweeping Dragon Slash
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4.0f));
            g.drawLine(15, -10, 85, -45);
            g.setColor(primary);
            g.setStroke(new BasicStroke(2.0f));
            g.drawLine(15, -10, 85, -45);
            // Fiery blade trail
            g.setColor(new Color(255, 100, 0, 180));
            g.drawArc(10, -60, 80, 80, 0, 90);
        } else {
            // Hand on scabbard ready to draw
            g.setColor(new Color(40, 20, 20));
            g.fillRect(-10, 10, 35, 8); // Scabbard
            g.setColor(new Color(255, 215, 0));
            g.fillRect(22, 6, 12, 6); // Hilt
        }
    }

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

        // Mage Hood
        g.setColor(new Color(45, 15, 75));
        int[] hx = {-20, 20, 0};
        int[] hy = {-40, -40, -85};
        g.fillPolygon(hx, hy, 3);
        // Face Shadow inside hood
        g.setColor(new Color(15, 10, 25));
        g.fillOval(-14, -58, 28, 28);

        // Glowing Magic Eyes
        g.setColor(primary);
        g.fillOval(-2, -50, 7, 5);
        g.setColor(Color.WHITE);
        g.fillOval(0, -49, 3, 3);

        // Mystic Staff in Hand
        g.setColor(new Color(120, 80, 40));
        g.setStroke(new BasicStroke(3.5f));
        g.drawLine(25, -60, 25, 70);

        // Glowing Orb atop Staff
        double orbGlow = 0.6 + Math.sin(t / 140.0) * 0.4;
        g.setColor(new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), (int) (orbGlow * 255)));
        g.fillOval(17, -76, 16, 16);
        g.setColor(Color.WHITE);
        g.fillOval(21, -72, 8, 8);

        if (state == Fighter.State.ATTACKING) {
            // Arcane beam cast
            g.setColor(primary);
            g.setStroke(new BasicStroke(8.0f));
            g.drawLine(35, -20, 95, -20);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3.0f));
            g.drawLine(35, -20, 95, -20);
            // Energy burst circle
            g.setColor(new Color(255, 255, 255, 180));
            g.fillOval(45, -35, 35, 35);
            g.setColor(Color.WHITE);
            g.fillOval(52, -28, 20, 20);
        }
    }

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
