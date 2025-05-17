package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Database.Db.UserRoles;
import com.fivestarhotel.security.Crypto;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import com.fivestarhotel.users.Admin;
import com.fivestarhotel.users.Receptionist;
import com.fivestarhotel.users.User;
import com.fivestarhotel.security.SecureCredentials;
import com.fivestarhotel.users.Customer;

public class BookItLogin extends JFrame {
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JProgressBar loadingBar;
    private char originalEchoChar;
    private JCheckBox rememberMe;

    public BookItLogin() {
        initializeUI();
        String[] savedCredentials = SecureCredentials.loadCredentials();
        if (savedCredentials != null) {
            emailField.setText(savedCredentials[0]);
            passwordField.setText(savedCredentials[1]);
            rememberMe.setSelected(true);
        }
    }

    public BookItLogin(String email, String password) {
        try {
            if (directAuthenticate(email, password)) {
                emailField.setText(email);
                passwordField.setText(password);
            }
        } catch (Exception e) {
            initializeUI();
        }

    }

    private void initializeUI() {
        setTitle("BookIt - Hotel Management Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.secondaryColor);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createLoginPanel(), BorderLayout.CENTER);

        loadingBar = Utils.createLoadingBar();
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Utils.secondaryColor);

        JLabel titleLabel = new JLabel("BookIt Hotel Management", JLabel.CENTER);
        titleLabel.setFont(Utils.TITLE_FONT);
        titleLabel.setForeground(Utils.primaryColor);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createLoginPanel() {
        try {
            JPanel loginPanel = new JPanel(new GridBagLayout());
            loginPanel.setBackground(Utils.secondaryColor);
            loginPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Utils.primaryColor, 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));

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
                    BorderFactory.createLineBorder(Utils.primaryColor, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            loginPanel.add(emailField, gbc);

            // Password field
            gbc.gridy = 1;
            gbc.gridx = 0;
            loginPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            passwordField = new JPasswordField(20);
            passwordField.setFont(Utils.LABEL_FONT);
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Utils.primaryColor, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            passwordField.addActionListener(e -> authenticateUser());
            loginPanel.add(passwordField, gbc);
            // Store original echo character
            originalEchoChar = passwordField.getEchoChar();

            // Toggle button
            gbc.gridx = 2;
            JButton togglePasswordButton = new JButton("( 0 )");
            Utils.styleToggleButton(togglePasswordButton, Utils.primaryColor);
            togglePasswordButton.addActionListener(e -> togglePasswordVisibility(togglePasswordButton));
            loginPanel.add(togglePasswordButton, gbc);
            // Remember me
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            rememberMe = new JCheckBox("Remember Me");
            rememberMe.setBackground(Utils.secondaryColor);
            loginPanel.add(rememberMe, gbc);
            // Login button
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            JButton loginButton = new JButton("Login");
            Utils.styleButton(loginButton, Utils.primaryColor);
            loginButton.addActionListener(e -> authenticateUser());
            loginPanel.add(loginButton, gbc);

            return loginPanel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticateUser() {
        try {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (!Utils.validateInputs(email, password, this))
                return;

            loadingBar.setVisible(true);
            setEnabled(false);

            // Simulate authentication delay
            Timer timer = new Timer(1500, e -> {
                User user = Db.select.signInUser(email, password);
                if (user instanceof Admin) {
                    System.out.println("Admin login successful");
                    openRoomManagement("Admin", user.getId());

                } else if (user instanceof Receptionist) {
                    System.out.println("Receptionist login successful");
                    openRoomManagement("Receptionist", user.getId());

                } else {
                    Utils.showError(this, "User doesn't exist or incorrect password");
                }

                loadingBar.setVisible(false);
                setEnabled(true);
            });

            if (rememberMe.isSelected()) {
                SecureCredentials.saveCredentials(email, password);
            } else {
                SecureCredentials.clearCredentials();
            }

            timer.setRepeats(false);
            timer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean directAuthenticate(String email, String password) {
        if (email.equals("admin@bookit.com") && password.equals("Admin_123")) {
            openRoomManagement("Admin", 1);
            return true;
        } else if (email.equals("reception@bookit.com") && password.equals("reception123")) {
            openRoomManagement("Receptionist", 2);
            return true;
        }
        return false;
    }

    private void togglePasswordVisibility(JButton togglePasswordButton) {
        if (passwordField.getEchoChar() == originalEchoChar) {
            passwordField.setEchoChar((char) 0);
            togglePasswordButton.setText("( - )");
        } else {
            passwordField.setEchoChar(originalEchoChar);
            togglePasswordButton.setText("( 0 )");
        }
    }

    private void openRoomManagement(String userRole, int userId) {
        System.out.println("Opening Room Management as " + userRole + " with ID: " + userId);
        SwingUtilities.invokeLater(() -> {
            new RoomManagement(userRole, userId).setVisible(true);
            dispose();
        });
    }
}
