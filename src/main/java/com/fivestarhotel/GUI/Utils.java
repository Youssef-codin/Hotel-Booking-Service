package com.fivestarhotel.GUI;

import javax.swing.*;
import java.awt.*;

public class Utils {
    // Colors
    public static final Color BROWN = new Color(92, 64, 51);
    public static final Color OFF_WHITE = new Color(246, 241, 233);

    // Fonts
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    //Email & Password validation
    public static final String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String passwordRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";


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
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(OFF_WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }

    public static void styleToggleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(40, 40));
        button.setBackground(bgColor);
        button.setForeground(OFF_WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
    }

    public static JPanel createStyledPanel(int padding, Color borderColor) {
        JPanel panel = new JPanel();
        panel.setBackground(OFF_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        ));
        return panel;
    }

    public static void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(BROWN);
        panel.add(label);

        field.setFont(LABEL_FONT);
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BROWN, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        panel.add(field);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}