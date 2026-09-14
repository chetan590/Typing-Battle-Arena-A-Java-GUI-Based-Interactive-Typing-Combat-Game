package com.typingbattle;

import com.typingbattle.ui.MainFrame;
import javax.swing.*;

/**
 * Main application entry point for Typing Battle Arena.
 */
public class Main {

    public static void main(String[] args) {
        // Optimize font anti-aliasing and rendering for Java 2D / Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Attempt modern system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Standard fallback
        }

        // Launch UI safely on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            System.out.println("==================================================");
            System.out.println("   TYPING BATTLE ARENA — COMBAT ENGINE STARTED    ");
            System.out.println("==================================================");
            System.out.println("Reclaim the Keyboard Core through typing mastery!");

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

