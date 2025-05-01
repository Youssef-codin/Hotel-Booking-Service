package com.fivestarhotel.GUI;

import javax.swing.*;
import java.awt.*;

public class BookItLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JProgressBar loadingBar;
    private char originalEchoChar;

    public BookItLogin() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("BookIt - Hotel Management Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.OFF_WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createLoginPanel(), BorderLayout.CENTER);

        // Loading indicator
        JProgressBar loadingBar = Utils.createLoadingBar();
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Utils.OFF_WHITE);

        JLabel titleLabel = new JLabel("BookIt Hotel Management", JLabel.CENTER);
        titleLabel.setFont(Utils.TITLE_FONT);
        titleLabel.setForeground(Utils.BROWN);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Utils.OFF_WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(Utils.LABEL_FONT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        loginPanel.add(emailField, gbc);

        // Password field
        gbc.gridy = 1;
        gbc.gridx = 0;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPanel passwordContainer = new JPanel(new BorderLayout(5, 0));
        passwordContainer.setBackground(Utils.OFF_WHITE);
        passwordField = new JPasswordField(20);
        passwordField.setFont(Utils.LABEL_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(1, 5, 1, 5)
        ));
        passwordField.addActionListener(e -> authenticateUser());

        // Store original echo character
        originalEchoChar = passwordField.getEchoChar();

        // Toggle button
        JButton togglePasswordButton = new JButton("(0)");
        Utils.styleToggleButton(togglePasswordButton, Utils.BROWN);
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility(togglePasswordButton));

        passwordContainer.add(passwordField, BorderLayout.CENTER);
        passwordContainer.add(togglePasswordButton, BorderLayout.EAST);
        loginPanel.add(passwordContainer, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        Utils.styleButton(loginButton, Utils.BROWN);
        loginButton.addActionListener(e -> authenticateUser());
        loginPanel.add(loginButton, gbc);

        return loginPanel;
    }

    private void authenticateUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (!validateInputs(email, password)) return;

        loadingBar.setVisible(true);
        setEnabled(false); // Disable UI during authentication

        // Simulate authentication delay
        Timer timer = new Timer(1500, e -> {
            if (email.equals("admin@bookit.com") && password.equals("admin123")) {
                System.out.println("Admin login successful");
                openRoomManagement("Admin", 1);
            } else if (email.equals("reception@bookit.com") && password.equals("reception123")) {
                System.out.println("Receptionist login successful");
                openRoomManagement("Receptionist", 2);
            } else {
                Utils.showError(this,"User doesn't exist");
            }

            loadingBar.setVisible(false);
            setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Utils.showError(this,"Please enter both email and password");
            return false;
        }

        if (!email.matches(Utils.emailRegex)) {
            Utils.showError(this,"Please enter a valid email address");
            return false;
        }

        if (!password.matches(Utils.passwordRegex)) {
            Utils.showError(this,"Password must be 6-20 characters with at least one uppercase letter, number, and symbol.");
            return false;
        }

        return true;
    }

    private void togglePasswordVisibility(JButton togglePasswordButton) {
        if (passwordField.getEchoChar() == originalEchoChar) {
            passwordField.setEchoChar((char) 0);
            togglePasswordButton.setText("Hide");
        } else {
            passwordField.setEchoChar(originalEchoChar);
            togglePasswordButton.setText("Show");
        }
    }

    private void openRoomManagement(String userRole, int userId) {
        System.out.println("Opening Room Management as " + userRole + " with ID: " + userId);
        SwingUtilities.invokeLater(() -> {
            new RoomManagement(userRole, userId).setVisible(true);
            dispose();
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookItLogin loginSystem = new BookItLogin();
            loginSystem.setVisible(true);
        });
    }
}