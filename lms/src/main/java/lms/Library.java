package lms;

import lms.ui.LibraryUI;

import javax.swing.*;

public class Library {

    public static void main(String[] args) {
        // Optional modern look (works if FlatLaf is on the classpath, otherwise ignored)
        try {
            Class<?> laf = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            laf.getMethod("setup").invoke(null);

            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI();
            ui.setVisible(true);
        });
    }
}