package com.fivestarhotel.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Utils {
    // Colors
    protected static final Color BROWN = new Color(92, 64, 51);
    protected static final Color OFF_WHITE = new Color(246, 241, 233);

    // Fonts
    protected static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    protected static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    protected static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    // Sizes
    protected static final int buttonWidth = 120;
    protected static final int buttonHeight = 40;
    protected static final int toggleButtonWidth = 30;
    protected static final int toggleButtonHeight = 30;

    // UI Components
    public static JProgressBar createLoadingBar() {
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setVisible(false);
        bar.setBackground(OFF_WHITE);
        bar.setForeground(BROWN);
        return bar;
    }

    public static void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setBackground(bgColor);
        button.setForeground(OFF_WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }

    public static void styleButton(JButton button, Color bgColor, int buttonWidth, int buttonHeight) {
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setBackground(bgColor);
        button.setForeground(OFF_WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }

    public static void styleToggleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(toggleButtonWidth, toggleButtonHeight));
        button.setBackground(bgColor);
        button.setForeground(OFF_WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        button.setFocusPainted(false);
    }

    public static JPanel createStyledPanel(int padding, Color borderColor, boolean border) {
        JPanel panel = new JPanel();
        panel.setBackground(OFF_WHITE);
        if (border) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        } else {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 0),
                    BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        }

        return panel;
    }

    public static void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(BROWN);
        panel.add(label);

        field.setFont(LABEL_FONT);
        if (field instanceof JTextField) {
            (field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BROWN, 1),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        }
        panel.add(field);
    }

    public static void UiStyleTabs() {
        UIManager.put("TabbedPane.selected", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.contentAreaColor", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.focus", Utils.BROWN);
        UIManager.put("TabbedPane.darkShadow", Utils.BROWN);
        UIManager.put("TabbedPane.light", Utils.BROWN);
        UIManager.put("TabbedPane.highlight", Utils.BROWN);
        UIManager.put("TabbedPane.shadow", Utils.BROWN);
        UIManager.put("TabbedPane.unselectedBackground", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.foreground", Utils.BROWN);
        UIManager.put("TabbedPane.selectedForeground", Utils.BROWN);
        UIManager.put("TabbedPane.selected", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.foreground", Utils.BROWN);
        UIManager.put("TabbedPane.background", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.unselectedBackground", Utils.OFF_WHITE);
        UIManager.put("TabbedPane.shadow", Utils.BROWN);
        UIManager.put("TabbedPane.darkShadow", Utils.BROWN);
        UIManager.put("TabbedPane.highlight", Utils.BROWN);
        UIManager.put("TabbedPane.light", Utils.BROWN);
        UIManager.put("TabbedPane.borderHightlightColor", Utils.BROWN); // yes, typo in API
        UIManager.put("TabbedPane.focus", Utils.BROWN);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean validateInputs(String email, String password, Component parent) {
        if (email.isEmpty() || password.isEmpty()) {
            Utils.showError(parent, "Please enter both email and password");
            return false;
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            Utils.showError(parent, "Please enter a valid email address");
            return false;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[-_+$#%&]).{6,20}$")) {
            Utils.showError(parent,
                    "Password must be 6-20 characters with at least one uppercase letter, number, and symbol.");
            return false;
        }

        return true;
    }

    public static JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(BROWN);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return label;
    }

    public static JButton createActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        styleButton(button, BROWN);
        button.addActionListener(action);
        return button;
    }
}
