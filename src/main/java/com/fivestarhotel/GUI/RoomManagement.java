package com.fivestarhotel.GUI;

import javax.swing.*;
import java.awt.*;

public class RoomManagement extends JFrame {
    private final Color PRIMARY_COLOR = new Color(92, 64, 51);
    private final Color BACKGROUND_COLOR = new Color(246, 241, 233);
    private JPanel roomsPanel;

    public RoomManagement(String userRole, int userId) {
        setupWindow(userRole);
        initUI(userRole);
    }

    private void setupWindow(String userRole) {
        setTitle("BookIt - Room Management (" + userRole + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void initUI(String userRole) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(createHeaderPanel(userRole), BorderLayout.NORTH);
        mainPanel.add(createRoomListPanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel(String userRole) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Room Management - " + userRole, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        if (userRole.equals("Admin")) {
            headerPanel.add(createAdminControls(), BorderLayout.EAST);
        }

        return headerPanel;
    }

    private JPanel createAdminControls() {
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        adminPanel.setBackground(BACKGROUND_COLOR);

        JButton addButton = new JButton("Add Room");
        JButton removeButton = new JButton("Remove Room");
        styleButton(addButton);
        styleButton(removeButton);

        adminPanel.add(addButton);
        adminPanel.add(removeButton);

        return adminPanel;
    }

    private JScrollPane createRoomListPanel() {
        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setBackground(BACKGROUND_COLOR);

        // Sample rooms - replace with actual data loading
        roomsPanel.add(createRoomCard(101, "Single", false));
        roomsPanel.add(createRoomCard(201, "Double", true));
        roomsPanel.add(createRoomCard(301, "Suite", false));

        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        return scrollPane;
    }

    private JPanel createRoomCard(int number, String type, boolean booked) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(BACKGROUND_COLOR);

        // Room info
        card.add(createRoomInfoPanel(number, type, booked), BorderLayout.CENTER);

        // Action button
        JButton actionButton = new JButton(booked ? "Check Out" : "Check In");
        styleButton(actionButton);
        card.add(actionButton, BorderLayout.EAST);

        return card;
    }

    private JPanel createRoomInfoPanel(int number, String type, boolean booked) {
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(BACKGROUND_COLOR);

        JLabel numberLabel = new JLabel("Room #" + number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberLabel.setForeground(PRIMARY_COLOR);

        JLabel statusLabel = new JLabel("Status: " + (booked ? "Booked" : "Available"));
        statusLabel.setForeground(booked ? Color.RED : new Color(0, 128, 0));

        infoPanel.add(numberLabel);
        infoPanel.add(new JLabel("Type: " + type));
        infoPanel.add(new JLabel("Floor: " + ((number - 1) / 100 + 1)));
        infoPanel.add(statusLabel);

        return infoPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
    }

    // Main method for direct testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Test with admin privileges
            RoomManagement management = new RoomManagement("Admin", 1);
            management.setVisible(true);

            // Alternatively test as receptionist:
            // new RoomManagement("Receptionist", 2).setVisible(true);
        });
    }
}