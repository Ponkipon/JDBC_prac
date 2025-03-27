package gui;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf; // If using FlatLaf
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class ThemeManager {
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}