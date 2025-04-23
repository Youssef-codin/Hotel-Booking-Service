package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Database.Select;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class RoomManagement extends JFrame {
    private final Color BROWN = new Color(92, 64, 51);
    private final Color OFF_WHITE = new Color(246, 241, 233);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    private JPanel roomsPanel;
    private JProgressBar loadingBar;
    private String currentUserRole;
    private int currentUserId;

    public RoomManagement(String userRole, int userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("BookIt - Room Management (" + currentUserRole + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(OFF_WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createRoomsPanel(), BorderLayout.CENTER);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.setBackground(OFF_WHITE);
        loadingBar.setForeground(BROWN);
        mainPanel.add(loadingBar, BorderLayout.SOUTH);

        add(mainPanel);
        loadRooms();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(OFF_WHITE);

        JLabel titleLabel = new JLabel("Room Management - " + currentUserRole, JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BROWN);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        if ("Admin".equals(currentUserRole)) {
            headerPanel.add(createAdminControls(), BorderLayout.EAST);
        }

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, BROWN);
        logoutButton.addActionListener(e -> {
            new BookItLogin().setVisible(true);
            dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createAdminControls() {
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        adminPanel.setBackground(OFF_WHITE);

        JButton addRoomButton = new JButton("Add Room");
        styleButton(addRoomButton, BROWN);
        addRoomButton.addActionListener(e -> showAddRoomDialog());

        JButton removeRoomButton = new JButton("Remove Room");
        styleButton(removeRoomButton, BROWN);
        removeRoomButton.addActionListener(e -> showRemoveRoomDialog());

        adminPanel.add(addRoomButton);
        adminPanel.add(removeRoomButton);

        return adminPanel;
    }

    private JScrollPane createRoomsPanel() {
        roomsPanel = new JPanel(new WrapLayout(WrapLayout.LEADING, 20, 20));
        roomsPanel.setBackground(OFF_WHITE);
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getViewport().setBackground(OFF_WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private void loadRooms() {
        loadingBar.setVisible(true);
        roomsPanel.removeAll();
        roomsPanel.add(new JLabel("Loading rooms..."));
        roomsPanel.revalidate();
        roomsPanel.repaint();

        SwingWorker<List<Room>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Room> doInBackground() throws Exception {
                return new Select().getRooms();
            }

            @Override
            protected void done() {
                try {
                    List<Room> rooms = get();
                    roomsPanel.removeAll();

                    if (rooms == null || rooms.isEmpty()) {
                        roomsPanel.add(new JLabel("No rooms found"));
                    } else {
                        rooms.forEach(room -> addRoomCard(room));
                    }
                } catch (Exception e) {
                    roomsPanel.add(new JLabel("Error loading rooms"));
                } finally {
                    loadingBar.setVisible(false);
                    roomsPanel.revalidate();
                    roomsPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    private void addRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BROWN, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(OFF_WHITE);
        card.setPreferredSize(new Dimension(300, 200));

        // Room info
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(OFF_WHITE);

        JLabel roomNumberLabel = new JLabel("Room #" + room.getNum());
        roomNumberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roomNumberLabel.setForeground(BROWN);

        JLabel floorLabel = new JLabel("Floor: " + ((room.getNum() - 1) / 100 + 1));
        JLabel typeLabel = new JLabel("Type: " + room.getRoomType().toString());
        JLabel statusLabel = new JLabel("Status: " + (room.getStatus() ? "Booked" : "Available"));
        statusLabel.setForeground(room.getStatus() ? Color.RED : new Color(0, 128, 0));

        infoPanel.add(roomNumberLabel);
        infoPanel.add(floorLabel);
        infoPanel.add(typeLabel);
        infoPanel.add(statusLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(createActionButtons(room), BorderLayout.SOUTH);
        roomsPanel.add(card);
    }

    private JPanel createActionButtons(Room room) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(OFF_WHITE);

        if (room.getStatus()) {
            JButton checkOutButton = new JButton("Check Out");
            styleButton(checkOutButton, BROWN);
            checkOutButton.addActionListener(e -> checkOutRoom(room.getNum()));
            buttonPanel.add(checkOutButton);

            if ("Admin".equals(currentUserRole)) {
                JButton setAvailableButton = new JButton("Set Open");
                styleButton(setAvailableButton, BROWN);
                setAvailableButton.addActionListener(e -> setRoomAvailability(room.getNum(), false));
                buttonPanel.add(setAvailableButton);
            }
        } else {
            JButton checkInButton = new JButton("Check In");
            styleButton(checkInButton, BROWN);
            checkInButton.addActionListener(e -> showCheckInDialog(room.getNum()));
            buttonPanel.add(checkInButton);

            if ("Admin".equals(currentUserRole)) {
                JButton setUnavailableButton = new JButton("Set Closed");
                styleButton(setUnavailableButton, BROWN);
                setUnavailableButton.addActionListener(e -> setRoomAvailability(room.getNum(), true));
                buttonPanel.add(setUnavailableButton);
            }
        }

        return buttonPanel;
    }

    private void showAddRoomDialog() {
        JDialog addRoomDialog = new JDialog(this, "Add New Room", true);
        addRoomDialog.setSize(400, 250);
        addRoomDialog.setLocationRelativeTo(this);
        addRoomDialog.getContentPane().setBackground(OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(OFF_WHITE);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(OFF_WHITE);

        JTextField roomNumberField = new JTextField();
        addFormField(formPanel, "Room Number:", roomNumberField);

        JComboBox<RoomType> roomTypeCombo = new JComboBox<>(RoomType.values());
        addFormField(formPanel, "Room Type:", roomTypeCombo);

        JCheckBox bookedCheckbox = new JCheckBox();
        addFormField(formPanel, "Initially Booked:", bookedCheckbox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(OFF_WHITE);

        JButton submitButton = new JButton("Add Room");
        styleButton(submitButton, BROWN);
        submitButton.addActionListener(e -> {
            try {
                if (roomNumberField.getText().trim().isEmpty()) {
                    showError(addRoomDialog, "Room number cannot be empty");
                    return;
                }

                int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
                RoomType roomType = (RoomType) roomTypeCombo.getSelectedItem();
                boolean isBooked = bookedCheckbox.isSelected();

                try (Connection conn = Db.connect()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO room (room_number, room_floor, room_type, room_status) VALUES (?, ?, ?, ?)");
                    ps.setInt(1, roomNumber);
                    ps.setInt(2, (roomNumber - 1) / 100 + 1);
                    ps.setString(3, Room.convertRm(roomType));
                    ps.setBoolean(4, isBooked);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(addRoomDialog,
                                "Room #" + roomNumber + " added successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        addRoomDialog.dispose();
                        loadRooms();
                    }
                }
            } catch (NumberFormatException ex) {
                showError(addRoomDialog, "Please enter a valid room number");
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) {
                    showError(addRoomDialog, "Room #" + roomNumberField.getText() + " already exists");
                } else {
                    showError(addRoomDialog, "Database error: " + ex.getMessage());
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, BROWN);
        cancelButton.addActionListener(e -> addRoomDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> submitButton.doClick());

        addRoomDialog.add(mainPanel);
        addRoomDialog.setVisible(true);
    }

    private void showRemoveRoomDialog() {
        JDialog removeDialog = new JDialog(this, "Remove Room", true);
        removeDialog.setSize(400, 200);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.getContentPane().setBackground(OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(OFF_WHITE);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(OFF_WHITE);

        JTextField roomNumberField = new JTextField();
        addFormField(inputPanel, "Room Number to Remove:", roomNumberField);

        JLabel warningLabel = new JLabel("Warning: This cannot be undone!", JLabel.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(warningLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(OFF_WHITE);

        JButton removeButton = new JButton("Remove");
        styleButton(removeButton, BROWN);
        removeButton.addActionListener(e -> {
            try {
                String roomNumberText = roomNumberField.getText().trim();
                if (roomNumberText.isEmpty()) {
                    showError(removeDialog, "Please enter a room number");
                    return;
                }

                int roomNumber = Integer.parseInt(roomNumberText);
                int confirm = JOptionPane.showConfirmDialog(
                        removeDialog,
                        "Are you sure you want to permanently remove Room #" + roomNumber + "?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = Db.connect()) {
                        PreparedStatement ps = conn.prepareStatement(
                                "DELETE FROM room WHERE room_number = ?");
                        ps.setInt(1, roomNumber);
                        int rowsAffected = ps.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(removeDialog,
                                    "Room #" + roomNumber + " was successfully removed",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            removeDialog.dispose();
                            loadRooms();
                        } else {
                            showError(removeDialog, "Room #" + roomNumber + " not found");
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                showError(removeDialog, "Please enter a valid room number");
            } catch (SQLException ex) {
                showError(removeDialog, "Database error: " + ex.getMessage());
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, BROWN);
        cancelButton.addActionListener(e -> removeDialog.dispose());

        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> removeButton.doClick());

        removeDialog.add(mainPanel);
        removeDialog.setVisible(true);
    }

    private void showCheckInDialog(int roomNumber) {
        JDialog checkInDialog = new JDialog(this, "Check In - Room #" + roomNumber, true);
        checkInDialog.setSize(600, 600);
        checkInDialog.setLocationRelativeTo(this);
        checkInDialog.getContentPane().setBackground(OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(OFF_WHITE);

        JPanel roomInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("Room Information"));
        roomInfoPanel.setBackground(OFF_WHITE);

        Room room = Db.select.getRoom(roomNumber);
        addFormField(roomInfoPanel, "Room Number:", new JLabel(String.valueOf(roomNumber)));
        addFormField(roomInfoPanel, "Room Type:", new JLabel(room.getRoomType().toString()));
        addFormField(roomInfoPanel, "Daily Rate:", new JLabel("$" + Room.getRate(room.getRoomType())));

        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(OFF_WHITE);
        tabbedPane.setFont(LABEL_FONT);

        JPanel existingCustomerPanel = new JPanel(new BorderLayout(10, 10));
        existingCustomerPanel.setBackground(OFF_WHITE);

        JPanel verifyPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        verifyPanel.setBackground(OFF_WHITE);

        JTextField customerIdField = new JTextField();
        JButton verifyButton = new JButton("Verify Customer");
        styleButton(verifyButton, BROWN);

        addFormField(verifyPanel, "Customer ID:", customerIdField);
        verifyPanel.add(new JLabel());
        verifyPanel.add(verifyButton);

        JLabel customerInfoLabel = new JLabel("", JLabel.CENTER);
        customerInfoLabel.setFont(LABEL_FONT);

        existingCustomerPanel.add(verifyPanel, BorderLayout.NORTH);
        existingCustomerPanel.add(customerInfoLabel, BorderLayout.CENTER);

        JPanel newCustomerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        newCustomerPanel.setBackground(OFF_WHITE);

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        addFormField(newCustomerPanel, "First Name:", firstNameField);
        addFormField(newCustomerPanel, "Last Name:", lastNameField);
        addFormField(newCustomerPanel, "Email:", emailField);
        addFormField(newCustomerPanel, "Phone:", phoneField);

        tabbedPane.addTab("Existing Customer", existingCustomerPanel);
        tabbedPane.addTab("New Customer", newCustomerPanel);

        JPanel datesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        datesPanel.setBorder(BorderFactory.createTitledBorder("Booking Dates"));
        datesPanel.setBackground(OFF_WHITE);

        JSpinner checkInSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkInEditor = new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd");
        checkInSpinner.setEditor(checkInEditor);
        checkInSpinner.setValue(new Date());

        JSpinner checkOutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkOutEditor = new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd");
        checkOutSpinner.setEditor(checkOutEditor);
        checkOutSpinner.setValue(new Date(System.currentTimeMillis() + 86400000));

        addFormField(datesPanel, "Check-in Date:", checkInSpinner);
        addFormField(datesPanel, "Check-out Date:", checkOutSpinner);

        JComboBox<String> receptionistCombo = new JComboBox<>();
        if ("Admin".equals(currentUserRole)) {
            try {
                Map<Integer, String> receptionists = getReceptionists();
                receptionists.forEach((id, name) -> receptionistCombo.addItem(name + " (ID: " + id + ")"));
                addFormField(datesPanel, "Receptionist:", receptionistCombo);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(checkInDialog,
                        "Error loading receptionists: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        JButton submitButton = new JButton("Complete Check In");
        styleButton(submitButton, BROWN);

        verifyButton.addActionListener(e -> {
            try {
                if (customerIdField.getText().isEmpty()) {
                    customerInfoLabel.setText("Please enter a customer ID");
                    return;
                }

                int customerId = Integer.parseInt(customerIdField.getText());
                if (customerExists(customerId)) {
                    customerInfoLabel.setText("<html><b>Customer verified</b> - ready to check in</html>");
                    customerInfoLabel.setForeground(new Color(0, 128, 0));
                } else {
                    customerInfoLabel.setText("<html><b>Customer not found</b> - please register new customer</html>");
                    customerInfoLabel.setForeground(Color.RED);
                    tabbedPane.setSelectedIndex(1);
                }
            } catch (NumberFormatException ex) {
                customerInfoLabel.setText("Please enter a valid customer ID");
                customerInfoLabel.setForeground(Color.RED);
            } catch (SQLException ex) {
                customerInfoLabel.setText("Database error: " + ex.getMessage());
                customerInfoLabel.setForeground(Color.RED);
            }
        });

        submitButton.addActionListener(e -> {
            try {
                Date checkInDate = (Date) checkInSpinner.getValue();
                Date checkOutDate = (Date) checkOutSpinner.getValue();

                if (checkOutDate.before(checkInDate)) {
                    JOptionPane.showMessageDialog(checkInDialog,
                            "Check-out date must be after check-in date",
                            "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int customerId;
                if (tabbedPane.getSelectedIndex() == 0) {
                    if (customerIdField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(checkInDialog,
                                "Please verify customer ID first",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    customerId = Integer.parseInt(customerIdField.getText());
                } else {
                    if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(checkInDialog,
                                "First and last name are required",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    customerId = registerNewCustomer(
                            firstNameField.getText(),
                            lastNameField.getText(),
                            emailField.getText(),
                            phoneField.getText()
                    );
                }

                int receptionistId = currentUserId;
                if ("Admin".equals(currentUserRole)) {
                    String selected = (String) receptionistCombo.getSelectedItem();
                    receptionistId = Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
                }

                createBooking(roomNumber, customerId, receptionistId, checkInDate, checkOutDate);

                JOptionPane.showMessageDialog(checkInDialog,
                        "Room #" + roomNumber + " checked in successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                checkInDialog.dispose();
                loadRooms();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(checkInDialog,
                        "Error during check-in: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(OFF_WHITE);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(datesPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);

        checkInDialog.add(mainPanel);
        checkInDialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
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
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setBackground(OFF_WHITE);
            ((JComboBox<?>) field).setBorder(BorderFactory.createLineBorder(BROWN, 1));
        } else if (field instanceof JCheckBox) {
            ((JCheckBox) field).setBackground(OFF_WHITE);
        }
        panel.add(field);
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(color);
        button.setForeground(OFF_WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setMnemonic(KeyEvent.VK_ENTER);
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void setRoomAvailability(int roomNumber, boolean isAvailable) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE room SET room_status = ? WHERE room_number = ?");
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, roomNumber);
            ps.executeUpdate();
            loadRooms();
            JOptionPane.showMessageDialog(this,
                    "Room #" + roomNumber + " status updated",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void checkOutRoom(int roomNumber) {
        try (Connection conn = Db.connect()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE room SET room_status = false WHERE room_number = ?");
                ps.setInt(1, roomNumber);
                ps.executeUpdate();

                ps = conn.prepareStatement(
                        "UPDATE billing SET billing_status = 1 " +
                                "WHERE booking_id IN (SELECT booking_id FROM booking WHERE room_number = ?)");
                ps.setInt(1, roomNumber);
                ps.executeUpdate();

                conn.commit();
                loadRooms();
                JOptionPane.showMessageDialog(this,
                        "Room #" + roomNumber + " checked out successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            showError(this, "Database error: " + ex.getMessage());
        }
    }

    private boolean customerExists(int customerId) throws SQLException {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM customer WHERE customer_id = ?");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private int registerNewCustomer(String firstName, String lastName, String email, String phone) throws SQLException {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customer (customer_fname, customer_lname, customer_email, customer_phone) " +
                            "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Creating customer failed, no ID obtained.");
            }
        }
    }

    private void createBooking(int roomNumber, int customerId, int receptionistId,
                               Date checkInDate, Date checkOutDate) throws SQLException {
        try (Connection conn = Db.connect()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO booking (receptionist_id, customer_id, room_number, " +
                                "check_in_date, check_out_date) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, receptionistId);
                ps.setInt(2, customerId);
                ps.setInt(3, roomNumber);
                ps.setTimestamp(4, new Timestamp(checkInDate.getTime()));
                ps.setTimestamp(5, new Timestamp(checkOutDate.getTime()));
                ps.executeUpdate();

                PreparedStatement updateRoom = conn.prepareStatement(
                        "UPDATE room SET room_status = true WHERE room_number = ?");
                updateRoom.setInt(1, roomNumber);
                updateRoom.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int bookingId = rs.getInt(1);
                        PreparedStatement billingStmt = conn.prepareStatement(
                                "INSERT INTO billing (booking_id, billing_status) VALUES (?, 0)");
                        billingStmt.setInt(1, bookingId);
                        billingStmt.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Map<Integer, String> getReceptionists() throws SQLException {
        Map<Integer, String> receptionists = new HashMap<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT receptionist_id, receptionist_fname, receptionist_lname FROM receptionist");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                receptionists.put(
                        rs.getInt("receptionist_id"),
                        rs.getString("receptionist_fname") + " " + rs.getString("receptionist_lname")
                );
            }
        }
        return receptionists;
    }

    // Custom layout manager for wrapping room cards
    static class WrapLayout extends FlowLayout {
        public WrapLayout() { super(); }
        public WrapLayout(int align) { super(align); }
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                for (Component m : target.getComponents()) {
                    if (m.isVisible()) {
                        Dimension d = m.getPreferredSize();
                        if (rowWidth + d.width > maxWidth) {
                            dim.width = Math.max(dim.width, rowWidth);
                            dim.height += rowHeight + vgap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                dim.width = Math.max(dim.width, rowWidth);
                dim.height += rowHeight;
                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;
                return dim;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Db.connect("root", "mimimi45");
            RoomManagement roomManagement = new RoomManagement("Admin", 1);
            roomManagement.setVisible(true);
        });
    }
}