package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Database.Select;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class BookItLogin extends JFrame {
    private final Color BROWN = new Color(92, 64, 51);
    private final Color OFF_WHITE = new Color(246, 241, 233);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    private JTextField emailField;
    private JPasswordField passwordField;
    private JProgressBar loadingBar;

    public BookItLogin() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("BookIt - Hotel Management Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(OFF_WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createLoginPanel(), BorderLayout.CENTER);

        // Loading indicator
        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.setBackground(OFF_WHITE);
        loadingBar.setForeground(BROWN);
        mainPanel.add(loadingBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(OFF_WHITE);

        JLabel titleLabel = new JLabel("Welcome To BookIt!", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BROWN);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(OFF_WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BROWN, 1),
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
        emailField.setFont(LABEL_FONT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BROWN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        loginPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(LABEL_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BROWN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.addActionListener(e -> authenticateUser());
        loginPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, BROWN);
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

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            private String userRole = null;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    if (checkAdminCredentials(email, password)) {
                        userRole = "Admin";
                        return true;
                    } else if (checkReceptionistCredentials(email, password)) {
                        userRole = "Receptionist";
                        return true;
                    }
                    return false;
                } catch (SQLException ex) {
                    throw ex;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) { // Authentication successful
                        openRoomManagement(userRole);
                    } else {
                        showError("Invalid email or password");
                    }
                } catch (Exception ex) {
                    showError("Database error: " + ex.getMessage());
                } finally {
                    loadingBar.setVisible(false);
                    setEnabled(true); // Re-enable UI
                }
            }
        };
        worker.execute();
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return false;
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Please enter a valid email address");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private boolean checkAdminCredentials(String email, String password) throws SQLException {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM admin WHERE admin_email = ? AND admin_password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private boolean checkReceptionistCredentials(String email, String password) throws SQLException {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM receptionist WHERE receptionist_email = ? AND receptionist_password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private void openRoomManagement(String userRole) {
        try (Connection conn = Db.connect()) {
            String query = userRole.equals("Admin")
                    ? "SELECT admin_id FROM admin WHERE admin_email = ?"
                    : "SELECT receptionist_id FROM receptionist WHERE receptionist_email = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, emailField.getText());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(1);
                SwingUtilities.invokeLater(() -> {
                    new RoomManagement(userRole, userId).setVisible(true);
                    dispose();
                });
            } else {
                showError("Error: Could not retrieve user ID");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(color);
        button.setForeground(OFF_WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setMnemonic(KeyEvent.VK_ENTER);
    }
}