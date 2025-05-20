package com.fivestarhotel.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Utils {
    // Colors
    protected static final Color primaryColor = new Color(92, 64, 51);
    protected static final Color secondaryColor = new Color(246, 241, 233);
    protected static final Color selectionColor = Utils.primaryColor.brighter();

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
        bar.setBackground(secondaryColor);
        bar.setForeground(primaryColor);
        return bar;
    }

    public static void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setBackground(bgColor);
        button.setForeground(secondaryColor);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }

    public static void styleButton(JButton button, Color bgColor, int buttonWidth, int buttonHeight) {
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setBackground(bgColor);
        button.setForeground(secondaryColor);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }

    public static void styleToggleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(toggleButtonWidth, toggleButtonHeight));
        button.setBackground(bgColor);
        button.setForeground(secondaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        button.setFocusPainted(false);
    }

    public static JPanel createStyledPanel(int padding, Color borderColor, boolean border) {
        JPanel panel = new JPanel();
        panel.setBackground(secondaryColor);
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
        label.setForeground(primaryColor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label);

        field.setFont(LABEL_FONT);
        if (field instanceof JTextField) {
            (field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        }
        panel.add(field);
    }

    public static void setUIStyles() {
        // Base colors
        Color selectionColor = Utils.primaryColor.brighter();

        // Basic component defaults
        UIManager.put("control", secondaryColor);
        UIManager.put("info", secondaryColor);
        UIManager.put("nimbusBase", Utils.primaryColor);
        UIManager.put("nimbusFocus", Utils.primaryColor);
        UIManager.put("nimbusSelectionBackground", selectionColor);

        // Text components
        UIManager.put("TextField.foreground", primaryColor);
        UIManager.put("TextField.caretForeground", primaryColor);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));

        UIManager.put("PasswordField.foreground", primaryColor);

        // Buttons
        UIManager.put("Button.select", selectionColor);

        // Checkboxes & Radio buttons
        UIManager.put("CheckBox.background", secondaryColor);
        UIManager.put("CheckBox.foreground", primaryColor);
        UIManager.put("RadioButton.background", secondaryColor);
        UIManager.put("RadioButton.foreground", primaryColor);

        // Combo boxes

        UIManager.put("ComboBox.foreground", primaryColor);
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));
        UIManager.put("ComboBox.buttonBackground", secondaryColor);
        UIManager.put("ComboBox.selectionBackground", selectionColor);

        // Lists
        UIManager.put("List.foreground", primaryColor);
        UIManager.put("List.selectionBackground", selectionColor);

        // Tables

        UIManager.put("Table.foreground", primaryColor);
        UIManager.put("Table.gridColor", Utils.primaryColor);
        UIManager.put("Table.selectionBackground", selectionColor);

        // Scroll panes
        UIManager.put("ScrollPane.background", secondaryColor);
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));
        UIManager.put("ScrollBar.thumb", selectionColor);
        UIManager.put("ScrollBar.track", secondaryColor);

        // Progress bars
        UIManager.put("ProgressBar.background", secondaryColor);
        UIManager.put("ProgressBar.foreground", Utils.primaryColor);
        UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));

        // Menus
        UIManager.put("Menu.background", secondaryColor);
        UIManager.put("Menu.foreground", primaryColor);
        UIManager.put("MenuBar.background", secondaryColor);
        UIManager.put("MenuBar.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));

        // Tooltips
        UIManager.put("ToolTip.background", secondaryColor);
        UIManager.put("ToolTip.foreground", primaryColor);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));

        // Option panes
        UIManager.put("OptionPane.background", secondaryColor);
        UIManager.put("OptionPane.messageForeground", primaryColor);
        UIManager.put("OptionPane.border", BorderFactory.createLineBorder(Utils.primaryColor, 1));

        // Tabs (your existing implementation)
        UIManager.put("TabbedPane.selected", secondaryColor);
        UIManager.put("TabbedPane.contentAreaColor", secondaryColor);
        UIManager.put("TabbedPane.focus", Utils.primaryColor);
        UIManager.put("TabbedPane.darkShadow", Utils.primaryColor);
        UIManager.put("TabbedPane.light", Utils.primaryColor);
        UIManager.put("TabbedPane.highlight", Utils.primaryColor);
        UIManager.put("TabbedPane.shadow", Utils.primaryColor);
        UIManager.put("TabbedPane.unselectedBackground", secondaryColor);
        UIManager.put("TabbedPane.foreground", Utils.primaryColor);
        UIManager.put("TabbedPane.selectedForeground", Utils.primaryColor);
        UIManager.put("TabbedPane.background", secondaryColor);
        UIManager.put("TabbedPane.borderHightlightColor", Utils.primaryColor);

        // Labels
        UIManager.put("Label.foreground", primaryColor);

        // Panels
        UIManager.put("Panel.background", secondaryColor);

        // Separators
        UIManager.put("Separator.foreground", Utils.primaryColor);

        // File chooser
        UIManager.put("FileChooser.background", secondaryColor);
        UIManager.put("FileChooser.foreground", primaryColor);

        // Disabled components
        UIManager.put("Button.disabledText", Utils.primaryColor.darker());
        UIManager.put("Label.disabledForeground", Utils.primaryColor.darker());
        UIManager.put("TextField.inactiveForeground", Utils.primaryColor.darker());

        // Internal frames
        UIManager.put("InternalFrame.background", secondaryColor);
        UIManager.put("InternalFrame.titleForeground", primaryColor);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean validateInputs(String email, String password, String fullName, String phone, String address, String accountType, Component parent) {
        // Email and Password Validation
        if (email.isEmpty() || password.isEmpty()) {
            Utils.showError(parent, "Please enter both email and password");
            return false;
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            Utils.showError(parent, "Please enter a valid email address");
            return false;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$")) {
            Utils.showError(parent, "Password must be 6-20 characters with at least one uppercase letter, number, and symbol.");
            return false;
        }

        // Full Name Validation
        if (fullName.isEmpty()) {
            Utils.showError(parent, "Full Name is required.");
            return false;
        }

        // Customer-Specific Validation
        if ("Customer".equals(accountType)) {
            if (phone.isEmpty() || address.isEmpty()) {
                Utils.showError(parent, "Phone and Address are required for Customer accounts.");
                return false;
            }
            if (!phone.matches("^\\d{10,15}$")) {
                Utils.showError(parent, "Please enter a valid phone number (10-15 digits).");
                return false;
            }
        }

        // Length Checks for Name
        if (fullName.length() > 50) {
            Utils.showError(parent, "Full Name must not exceed 50 characters.");
            return false;
        }

        return true;
    }
    public static boolean validateInputs(String email, String password) {
        if (email == null || email.isEmpty()) {
            showError(null, "Email is required.");
            return false;
        }
        if (password == null || password.isEmpty()) {
            showError(null, "Password is required.");
            return false;
        }
        return true;
    }



    public static JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(primaryColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return label;
    }

    public static JButton createActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        styleButton(button, primaryColor);
        button.addActionListener(action);
        return button;
    }
}
