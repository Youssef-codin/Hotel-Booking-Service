package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;

public class RoomManagement extends JFrame {
    private JPanel roomsPanel;
    private JProgressBar loadingBar;
    private String currentUserRole;
    private int currentUserId;
    private JTextField roomSearch; //extracted variable
    //private JTextField roomSearch = new JTextField(5); //alternative way to do this so u dont have to create the object below

    //room data
    private ArrayList<Room> allRooms = Db.select.getRooms();
    private ArrayList<Room> mockRooms = Db.select.getRooms();

    public RoomManagement(String userRole, int userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("BookIt - Room Management (" + currentUserRole + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.OFF_WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createRoomsScrollPane(), BorderLayout.CENTER);

        loadingBar = Utils.createLoadingBar();
        mainPanel.add(loadingBar, BorderLayout.SOUTH);

        add(mainPanel);
        loadRooms();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Utils.OFF_WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 38, 0, 50));
        if ("Admin".equals(currentUserRole)) {
            headerPanel.add(createAdminControls(), BorderLayout.EAST);
        }
        JPanel searchPanel = Utils.createStyledPanel(0, Utils.BROWN, false);
        JLabel titleLabel = new JLabel("Room Management - " + currentUserRole, JLabel.CENTER);
        titleLabel.setFont(Utils.TITLE_FONT);
        titleLabel.setForeground(Utils.BROWN);
        searchPanel.add(titleLabel);

        //extracted variable
        roomSearch = new JTextField(5);
        roomSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        JButton searchButton = Utils.createActionButton("S", e -> extractedAction());

        Utils.styleButton(searchButton, Utils.BROWN, 40,40);
        roomSearch.addActionListener(e -> searchButton.doClick());
        searchPanel.add(roomSearch);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        JButton logoutButton = Utils.createActionButton("logout",e -> returnToLogin());
        headerPanel.add(logoutButton, BorderLayout.WEST);

        return headerPanel;
    }

    private void extractedAction() { //basically, expand the scope of the variables used (like the roomSearch textField) then u can just do this
        try {
            int roomNumber = Integer.parseInt(roomSearch.getText().trim());
            mockRooms = new ArrayList<>();
            if(roomNumber == 0 || roomNumber > allRooms.size()){
                mockRooms.addAll(allRooms);
            }else if(roomNumber < 0){
                Utils.showError(this, "Invalid Number!");
            }else {
                mockRooms.add(allRooms.get(roomNumber-1));
            }
        } catch (NumberFormatException a){
            mockRooms = Db.select.getRooms();
        }
    }

    private JPanel createAdminControls() {
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        adminPanel.setBackground(Utils.OFF_WHITE);

        JButton addButton = Utils.createActionButton("Add Room", e -> showAddRoomDialog());
        JButton removeButton = Utils.createActionButton("Remove Room", e -> showRemoveRoomDialog());

        adminPanel.add(addButton);
        adminPanel.add(removeButton);
        return adminPanel;
    }



    private JScrollPane createRoomsScrollPane() {
        roomsPanel = new JPanel(new WrapLayout(WrapLayout.LEADING, 20, 20));
        roomsPanel.setBackground(Utils.OFF_WHITE);
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getViewport().setBackground(Utils.OFF_WHITE);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadRooms() {
        loadingBar.setVisible(true);
        roomsPanel.removeAll();
        roomsPanel.add(new JLabel("Loading rooms..."));
        refreshUI();

        // Simulated loading
        new Timer(1000, e -> {
            roomsPanel.removeAll();
            if (mockRooms.isEmpty()) {
                roomsPanel.add(new JLabel("No rooms found"));
            } else {
                mockRooms.forEach(this::addRoomCard);
            }
            loadingBar.setVisible(false);
            refreshUI();
        }).start();
    }

    private void addRoomCard(Room room) {
        JPanel card = Utils.createStyledPanel(15, Utils.BROWN, true);
        card.setPreferredSize(new Dimension(300, 200));

        // Room Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Utils.OFF_WHITE);

        JLabel numberLabel = new JLabel("Room #" + room.getNum());
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberLabel.setForeground(Utils.BROWN);

        infoPanel.add(numberLabel);
        infoPanel.add(new JLabel("Floor: " + ((room.getNum()-1)/100 +1)));
        infoPanel.add(new JLabel("Type: " + room.getRoomType()));
        infoPanel.add(createStatusLabel(room.getStatus()));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(createActionButtons(room), BorderLayout.SOUTH);
        roomsPanel.add(card);
    }

    private JLabel createStatusLabel(boolean isBooked) {
        JLabel label = new JLabel("Status: " + (isBooked ? "Booked" : "Available"));
        label.setForeground(isBooked ? Color.RED : new Color(0, 128, 0));
        return label;
    }

    private JPanel createActionButtons(Room room) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(Utils.OFF_WHITE);

        if (room.getStatus()) {
            panel.add(Utils.createActionButton("Check Out", e -> Utils.showError(panel,"hi")));//showCheckoutMessage(room)));
            if ("Admin".equals(currentUserRole)) {
                panel.add(Utils.createActionButton("Set Available", e -> Utils.showError(panel,"hi")));//showSetAvailableMessage(room)));
            }
        } else {
            panel.add(Utils.createActionButton("Check In", e -> showCheckInDialog(room.getNum())));
            if ("Admin".equals(currentUserRole)) {
                panel.add(Utils.createActionButton("Set Unavailable", e -> Utils.showError(panel,"hi")));//showSetUnavailableMessage(room)));
            }
        }
        return panel;
    }

    private void returnToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new BookItLogin().setVisible(true));
    }

    private void refreshUI() {
        roomsPanel.revalidate();
        roomsPanel.repaint();
    }

    private void showAddRoomDialog() {
        JDialog addRoomDialog = new JDialog(this, "Add New Room", true);
        addRoomDialog.setSize(400, 250);
        addRoomDialog.setLocationRelativeTo(this);
        addRoomDialog.getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.OFF_WHITE);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Utils.OFF_WHITE);

        JTextField roomNumberField = new JTextField();
        Utils.addFormField(formPanel, "Room Number:", roomNumberField);

        JComboBox<RoomType> roomTypeCombo = new JComboBox<>(RoomType.values());
        Utils.addFormField(formPanel, "Room Type:", roomTypeCombo);

        JCheckBox bookedCheckbox = new JCheckBox();
        Utils.addFormField(formPanel, "Initially Booked:", bookedCheckbox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.OFF_WHITE);

        JButton submitButton = Utils.createActionButton("Add Room",e -> {
            if (roomNumberField.getText().trim().isEmpty()) {
                Utils.showError(addRoomDialog, "Room number cannot be empty");
                return;
            }

            try {
                int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
                RoomType roomType = (RoomType) roomTypeCombo.getSelectedItem();
                boolean isBooked = bookedCheckbox.isSelected();

                mockRooms.add(new Room(roomNumber, roomType, isBooked));
                JOptionPane.showMessageDialog(addRoomDialog,
                        "Room #" + roomNumber + " added successfully! (Mock implementation)",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                addRoomDialog.dispose();
                loadRooms();
            } catch (NumberFormatException ex) {
                Utils.showError(addRoomDialog, "Please enter a valid room number");
            }
        });
        JButton cancelButton = Utils.createActionButton("Cancel",e -> addRoomDialog.dispose());

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
        removeDialog.getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.OFF_WHITE);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(Utils.OFF_WHITE);

        JTextField roomNumberField = new JTextField();
        Utils.addFormField(inputPanel, "Room Number to Remove:", roomNumberField);

        JLabel warningLabel = new JLabel("Warning: This cannot be undone!", JLabel.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(warningLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.OFF_WHITE);

        JButton removeButton = Utils.createActionButton("Remove", e -> {
            try {
                String roomNumberText = roomNumberField.getText().trim();
                if (roomNumberText.isEmpty()) {
                    Utils.showError(removeDialog, "Please enter a room number");
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
                    boolean removed = mockRooms.removeIf(room -> room.getNum() == roomNumber);
                    if (removed) {
                        JOptionPane.showMessageDialog(removeDialog,
                                "Room #" + roomNumber + " was successfully removed (Mock implementation)",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        removeDialog.dispose();
                        loadRooms();
                    } else {
                        Utils.showError(removeDialog, "Room #" + roomNumber + " not found");
                    }
                }
            } catch (NumberFormatException ex) {
                Utils.showError(removeDialog, "Please enter a valid room number");
            }
        });

        JButton cancelButton = Utils.createActionButton("Cancel", e -> removeDialog.dispose());

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
        checkInDialog.getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.OFF_WHITE);

        JPanel roomInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("Room Information"));
        roomInfoPanel.setBackground(Utils.OFF_WHITE);

        Utils.addFormField(roomInfoPanel, "Room Number:", new JLabel(String.valueOf(roomNumber)));
        Utils.addFormField(roomInfoPanel, "Room Type:", new JLabel("MOCK_TYPE"));
        Utils.addFormField(roomInfoPanel, "Daily Rate:", new JLabel("$MOCK_RATE"));

        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Utils.OFF_WHITE);
        tabbedPane.setFont(Utils.LABEL_FONT);

        JPanel existingCustomerPanel = new JPanel(new BorderLayout(10, 10));
        existingCustomerPanel.setBackground(Utils.OFF_WHITE);

        JPanel verifyPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        verifyPanel.setBackground(Utils.OFF_WHITE);

        JTextField customerIdField = new JTextField();
        JButton verifyButton = new JButton("Verify Customer");
        Utils.styleButton(verifyButton, Utils.BROWN);

        Utils.addFormField(verifyPanel, "Customer ID:", customerIdField);
        verifyPanel.add(new JLabel());
        verifyPanel.add(verifyButton);

        JLabel customerInfoLabel = new JLabel("", JLabel.CENTER);
        customerInfoLabel.setFont(Utils.LABEL_FONT);

        existingCustomerPanel.add(verifyPanel, BorderLayout.NORTH);
        existingCustomerPanel.add(customerInfoLabel, BorderLayout.CENTER);

        JPanel newCustomerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        newCustomerPanel.setBackground(Utils.OFF_WHITE);

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        Utils.addFormField(newCustomerPanel, "First Name:", firstNameField);
        Utils.addFormField(newCustomerPanel, "Last Name:", lastNameField);
        Utils.addFormField(newCustomerPanel, "Email:", emailField);
        Utils.addFormField(newCustomerPanel, "Phone:", phoneField);

        tabbedPane.addTab("Existing Customer", existingCustomerPanel);
        tabbedPane.addTab("New Customer", newCustomerPanel);

        JPanel datesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        datesPanel.setBorder(BorderFactory.createTitledBorder("Booking Dates"));
        datesPanel.setBackground(Utils.OFF_WHITE);

        JSpinner checkInSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkInEditor = new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd");
        checkInSpinner.setEditor(checkInEditor);
        checkInSpinner.setValue(new Date());

        JSpinner checkOutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkOutEditor = new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd");
        checkOutSpinner.setEditor(checkOutEditor);
        checkOutSpinner.setValue(new Date(System.currentTimeMillis() + 86400000));

        Utils.addFormField(datesPanel, "Check-in Date:", checkInSpinner);
        Utils.addFormField(datesPanel, "Check-out Date:", checkOutSpinner);

        if ("Admin".equals(currentUserRole)) {
            JComboBox<String> receptionistCombo = new JComboBox<>(new String[]{"Receptionist 1 (ID: 1)", "Receptionist 2 (ID: 2)"});
            Utils.addFormField(datesPanel, "Receptionist:", receptionistCombo);
        }

        JButton submitButton = Utils.createActionButton("Complete Check In", e -> {
            try {
                if (customerIdField.getText().isEmpty()) {
                    customerInfoLabel.setText("Please enter a customer ID");
                    return;
                }

                int customerId = Integer.parseInt(customerIdField.getText());
                if (customerId > 0) {
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
            }

            try {
                Date checkInDate = (Date) checkInSpinner.getValue();
                Date checkOutDate = (Date) checkOutSpinner.getValue();

                if (checkOutDate.before(checkInDate)) {
                    JOptionPane.showMessageDialog(checkInDialog,
                            "Check-out date must be after check-in date",
                            "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tabbedPane.getSelectedIndex() == 0) {
                    if (customerIdField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(checkInDialog,
                                "Please verify customer ID first",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(checkInDialog,
                                "First and last name are required",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                JOptionPane.showMessageDialog(checkInDialog,
                        "Room #" + roomNumber + " checked in successfully! (Mock implementation)",
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
        centerPanel.setBackground(Utils.OFF_WHITE);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(datesPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);

        checkInDialog.add(mainPanel);
        checkInDialog.setVisible(true);
    }


    public static void main(String[] args) {
        //Insert Db.connect(user,pass) here if you want to test
        Db.connect("root", "yoyo8080");
        SwingUtilities.invokeLater(() -> {
            RoomManagement roomManagement = new RoomManagement("Receptionist", 1);
            roomManagement.setVisible(true);
        });
    }
}