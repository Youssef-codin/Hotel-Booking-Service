package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;
import com.fivestarhotel.users.Customer;
import com.fivestarhotel.users.User;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;


public class RoomManagement extends JFrame {
    private JTextField accountIdField, searchField, customerIdField, firstNameField, lastNameField, emailField, phoneField;
    private JDialog addRoomDialog, removeDialog, checkInDialog;
    private JSpinner checkInSpinner, checkOutSpinner;
    private int currentUserId, searchNumber;
    private String currentUserRole;
    private JPanel roomsPanel, headerPanel, accountsPanel;
    private JProgressBar loadingBar;
    private JCheckBox bookedCheckbox;
    private JComboBox<RoomType> roomTypes;
    private JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"Admin", "Receptionist", "Customer"});;
    private JLabel customerInfoLabel;
    private JTabbedPane tabbedPane;
    private JButton addButton, removeButton;


    //Lists
    private ArrayList<Room> allRooms = Db.select.getRooms();
    private ArrayList<Room> allLogs;
    private ArrayList<Room> activeLogs;
    private ArrayList<User> allAdminAccounts = Db.select.getAllUsers(Db.UserRoles.ADMIN);
    private ArrayList<User> activeAdminAccounts = Db.select.getAllUsers(Db.UserRoles.ADMIN);;
    private ArrayList<User> allRecepAccounts = Db.select.getAllUsers(Db.UserRoles.RECEPTIONIST);
    private ArrayList<User> activeRecepAccounts = Db.select.getAllUsers(Db.UserRoles.RECEPTIONIST);
    private ArrayList<User> allCustAccounts = Db.select.getAllUsers(Db.UserRoles.CUSTOMER);
    private ArrayList<User> activeCustAccounts = Db.select.getAllUsers(Db.UserRoles.CUSTOMER);


    

    public RoomManagement(String userRole, int userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        initializeUI();
    }

    private void initializeUI() {
        Utils.UiStyleTabs();
        setTitle("BookIt - Room Management (" + currentUserRole + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.OFF_WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        if(currentUserRole.matches("Admin")){
            mainPanel.add(tabbedInterface(), BorderLayout.CENTER);
        }else{
            mainPanel.add(createRoomsScrollPane(), BorderLayout.CENTER);
        }


        loadingBar = Utils.createLoadingBar();
        mainPanel.add(loadingBar, BorderLayout.SOUTH);

        add(mainPanel);
        loadRooms();
    }




    private JPanel createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Utils.OFF_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 0),
                BorderFactory.createLineBorder(Utils.BROWN, 0)
        ));

        JButton logoutButton = Utils.createActionButton("<-", e -> returnToLogin());
        Utils.styleButton(logoutButton, Utils.BROWN, 50, 40);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        leftPanel.setOpaque(false);
        leftPanel.add(logoutButton);
        headerPanel.add(leftPanel, BorderLayout.WEST);

        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.setPreferredSize(new Dimension(120, 40)); // match height

        JButton searchButton = Utils.createActionButton("Search", e -> SearchButton());
        Utils.styleButton(searchButton, Utils.BROWN, 85, 40);
        searchField.addActionListener(e -> searchButton.doClick());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        if(currentUserRole.matches("Admin")){
            headerPanel.add(createAdminControls(false), BorderLayout.EAST);
        }


        return headerPanel;
    }

    private JTabbedPane tabbedInterface(){
        tabbedPane = new JTabbedPane();

        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Utils.OFF_WHITE);

        tabbedPane.addChangeListener(e -> updateHeaderButtons());

        tabbedPane.addTab("Available Rooms", createRoomsScrollPane());
        tabbedPane.addTab("Accounts", createAccountsPanel());
        tabbedPane.addTab("Room Logs", new JPanel());

        return tabbedPane;
    }

    private void filter(ArrayList from, ArrayList to) {
        to.clear(); // Clear existing entries
        if (searchNumber == 0 || searchNumber > from.size()) {
            to.addAll(from);
        } else if (searchNumber < 0) {
            Utils.showError(this, "Invalid Number!");
        } else {
            to.add(from.get(searchNumber - 1));
        }
    }

    private void SearchButton() {
        try {
            searchNumber = Integer.parseInt(searchField.getText().trim());
            if (tabbedPane.getSelectedIndex() == 0) {
                roomsPanel.removeAll();
                loadRoom(allRooms.get(searchNumber - 1));
            } else if (tabbedPane.getSelectedIndex() == 1) {
                filter(allAdminAccounts, activeAdminAccounts);
                filter(allRecepAccounts, activeRecepAccounts);
                filter(allCustAccounts, activeCustAccounts);
                refreshAccounts();
            }
        } catch (NumberFormatException a) {
            activeAdminAccounts = new ArrayList<>(allAdminAccounts);
            activeRecepAccounts = new ArrayList<>(allRecepAccounts);
            activeCustAccounts = new ArrayList<>(allCustAccounts);
            refreshAccounts();
            loadRooms();
        }
    }

    private JPanel createAdminControls(boolean switcher) {
        JPanel adminPanel = new JPanel(new GridLayout(1, 2,10,0));
        adminPanel.setBackground(Utils.OFF_WHITE);
        if(!switcher) {
            addButton = Utils.createActionButton("Room +", e -> showAddRoomDialog());
            removeButton= Utils.createActionButton("Room -", e -> showRemoveRoomDialog());
        }else{
            addButton = Utils.createActionButton("Account +", e -> showAddAccountDialog());
            removeButton = Utils.createActionButton("Account -", e -> showRemoveAccountDialog());
        }
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

    private JScrollPane createAccountScrollPane(String title, ArrayList<User> users) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Utils.OFF_WHITE);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        // Add spacing between components
        loadAccounts(users, sectionPanel);

        JScrollPane scrollPane = new JScrollPane(sectionPanel);
        scrollPane.getViewport().setBackground(Utils.OFF_WHITE);
        return scrollPane;
    }

    private void loadRooms() {
        loadingBar.setVisible(true);
        roomsPanel.removeAll();
        roomsPanel.add(new JLabel("Loading rooms..."));
        roomsPanel.revalidate();
        roomsPanel.repaint();

        // Simulate loading delay
        Timer timer = new Timer(1000, e -> {
            roomsPanel.removeAll();

            if (allRooms.isEmpty()) {
                roomsPanel.add(new JLabel("No rooms found"));
            } else {
                allRooms.forEach(room -> addRoomCard(room));
            }

            loadingBar.setVisible(false);
            roomsPanel.revalidate();
            roomsPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void loadRoom(Room room) {
        loadingBar.setVisible(true);
        roomsPanel.removeAll();
        roomsPanel.add(new JLabel("Loading room..."));
        roomsPanel.revalidate();
        roomsPanel.repaint();

        // Simulate loading delay
        Timer timer = new Timer(1000, e -> {
            roomsPanel.removeAll();
            addRoomCard(room);
            loadingBar.setVisible(false);
            roomsPanel.revalidate();
            roomsPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }



    private void loadAccounts(ArrayList<User> users, JPanel sectionPanel) {
        sectionPanel.removeAll();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.add(new JLabel("Loading Accounts..."));

        Timer timer = new Timer(1000, e -> {
            sectionPanel.removeAll();
            if (users.isEmpty()) {
                sectionPanel.add(new JLabel("No account found"));
            } else {
                users.forEach(user -> {
                    sectionPanel.add(addAccountCard(user));
                    sectionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                });
            }
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
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

    private JPanel addAccountCard(User user) {
        JPanel card = Utils.createStyledPanel(10, Utils.BROWN, true);
        card.setLayout(new BorderLayout());

        // Make card fill available width
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fixed height
        card.setPreferredSize(new Dimension(card.getPreferredSize().width, 40));

        JLabel infoLabel = new JLabel(
                user.getId() + "  |     " + user.getFullName()
        );

        // Add padding
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JButton detailsButton = Utils.createActionButton("i", e -> showUserDetails(user));
        Utils.styleButton(detailsButton, Utils.BROWN, 20, 20);

        card.add(infoLabel, BorderLayout.CENTER);
        card.add(detailsButton, BorderLayout.EAST);

        return card;
    }



    private void returnToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new BookItLogin().setVisible(true));
    }

    private void refreshRoomUI() {
        roomsPanel.revalidate();
        roomsPanel.repaint();
    }


    private void showAddRoomDialog() {
        addRoomDialog = new JDialog(this, "Add New Room", true);
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

        roomTypes = new JComboBox<>(RoomType.values());
        Utils.addFormField(formPanel, "Room Type:", roomTypes);

        bookedCheckbox = new JCheckBox();
        Utils.addFormField(formPanel, "Initially Booked:", bookedCheckbox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.OFF_WHITE);

        JButton submitButton = Utils.createActionButton("Add Room", e -> {addRoomAction(roomNumberField);});
        JButton cancelButton = Utils.createActionButton("Cancel",e -> addRoomDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> submitButton.doClick());

        addRoomDialog.add(mainPanel);
        addRoomDialog.setVisible(true);
    }

    private void addRoomAction(JTextField roomNumberField) {
        if (roomNumberField.getText().trim().isEmpty()) {
            Utils.showError(addRoomDialog, "Room number cannot be empty");
        }

        try {
            int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
            RoomType roomType = (RoomType) roomTypes.getSelectedItem();
            boolean isBooked = bookedCheckbox.isSelected();

            allRooms.add(new Room(roomNumber, roomType, isBooked));
            JOptionPane.showMessageDialog(addRoomDialog,
                    "Room #" + roomNumber + " added successfully! (Mock implementation)",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            addRoomDialog.dispose();
            loadRooms();
        } catch (NumberFormatException ex) {
            Utils.showError(addRoomDialog, "Please enter a valid room number");
        }
    }


    private JPanel createAccountsPanel() {
        accountsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        accountsPanel.setBackground(Utils.OFF_WHITE);

        accountsPanel.add(createAccountScrollPane("Admins", activeAdminAccounts));
        accountsPanel.add(createAccountScrollPane("Receptionists", activeRecepAccounts));
        accountsPanel.add(createAccountScrollPane("Customers", activeCustAccounts));
        accountsPanel.revalidate();
        accountsPanel.repaint();
        return accountsPanel;
    }




    private void showUserDetails(User user) {
        JDialog detailsDialog = new JDialog(this, "User Details", true);
        detailsDialog.setSize(300, 250);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createDetailLabel("ID: " + user.getId()));
        panel.add(createDetailLabel("Name: " + user.getFullName()));
        panel.add(createDetailLabel("Email: " + user.getEmail()));
        if(user instanceof Customer) {
            panel.add(createDetailLabel("Phone: " + ((Customer) user).getPhone()));
            panel.add(createDetailLabel("Address: " + ((Customer) user).getAddress()));
            panel.add(createDetailLabel("Balance: " + ((Customer) user).getBalance()));
        }
        detailsDialog.add(panel);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    // Modified header button management
    private void updateHeaderButtons() {
        Component[] components = headerPanel.getComponents();
        headerPanel.remove(components[components.length - 1]);
        if(tabbedPane.getSelectedIndex() == 1) { // Accounts tab
            headerPanel.add(createAdminControls(true), BorderLayout.EAST);
        } else {
            headerPanel.add(createAdminControls(false), BorderLayout.EAST);
        }

        headerPanel.revalidate();
        headerPanel.repaint();
    }


    private void showAddAccountDialog() {
        JDialog addAccountDialog = new JDialog(this, "Add New Account", true);
        addAccountDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addAccountDialog.getContentPane().setBackground(Utils.OFF_WHITE);
        addAccountDialog.setSize(450, 380);
        addAccountDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Utils.OFF_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Utils.OFF_WHITE);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Utils.addFormField(formPanel, "Account Type:", accountTypeCombo);
        Utils.addFormField(formPanel, "Full Name:", nameField);
        Utils.addFormField(formPanel, "Email:", emailField);
        Utils.addFormField(formPanel, "Password:", passwordField);

        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneFieldLocal = new JTextField();

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressFieldLocal = new JTextField();

        phoneLabel.setFont(Utils.LABEL_FONT);
        phoneLabel.setForeground(Utils.BROWN);
        addressLabel.setFont(Utils.LABEL_FONT);
        addressLabel.setForeground(Utils.BROWN);

        phoneFieldLocal.setFont(Utils.LABEL_FONT);
        phoneFieldLocal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        addressFieldLocal.setFont(Utils.LABEL_FONT);
        addressFieldLocal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.BROWN, 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));

        formPanel.add(phoneLabel);
        formPanel.add(phoneFieldLocal);
        formPanel.add(addressLabel);
        formPanel.add(addressFieldLocal);

        phoneLabel.setVisible(false);
        phoneFieldLocal.setVisible(false);
        addressLabel.setVisible(false);
        addressFieldLocal.setVisible(false);

        accountTypeCombo.addActionListener(e -> {
            String selectedType = (String) accountTypeCombo.getSelectedItem();
            boolean isCustomer = "Customer".equals(selectedType);

            phoneLabel.setVisible(isCustomer);
            phoneFieldLocal.setVisible(isCustomer);
            addressLabel.setVisible(isCustomer);
            addressFieldLocal.setVisible(isCustomer);

            addAccountDialog.pack(); // Adjust dialog size to fit new content
            // Ensure dialog is not smaller than a minimum reasonable size after pack
            addAccountDialog.setMinimumSize(new Dimension(400, (isCustomer ? 420 : 350)));
            // Re-center dialog if its size changed
            addAccountDialog.setLocationRelativeTo(RoomManagement.this);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.OFF_WHITE);

        JButton submitButton = Utils.createActionButton("Add", e -> {
            String accountType = (String) accountTypeCombo.getSelectedItem();
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Utils.showError(addAccountDialog, "Full Name, Email, and Password are required.");
                return;
            }

            String message = "GUI: Would add Account:\nType: " + accountType +
                    "\nName: " + fullName + "\nEmail: " + email;

            if ("Customer".equals(accountType)) {
                String phone = phoneFieldLocal.getText().trim();
                String address = addressFieldLocal.getText().trim();
                if (phone.isEmpty() || address.isEmpty()) {
                    Utils.showError(addAccountDialog, "Phone and Address are required for Customer accounts.");
                    return;
                }
                message += "\nPhone: " + phone + "\nAddress: " + address;
            }

            JOptionPane.showMessageDialog(addAccountDialog,
                    message, "Account Action (GUI Only)", JOptionPane.INFORMATION_MESSAGE);

            addAccountDialog.dispose();
            refreshAccounts();
        });

        JButton cancelButton = Utils.createActionButton("Cancel", e -> {
            addAccountDialog.dispose(); // Close the dialog
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addAccountDialog.add(mainPanel);
        addAccountDialog.pack();
        addAccountDialog.setMinimumSize(new Dimension(400, 350));
        addAccountDialog.setLocationRelativeTo(this);
        addAccountDialog.setVisible(true);
    }

    private void showRemoveAccountDialog() {
        JDialog removeDialog = new JDialog(this, "Remove Account", true);
        removeDialog.setSize(400, 200);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.OFF_WHITE);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Utils.OFF_WHITE);
        accountIdField = new JTextField();

        Utils.addFormField(inputPanel, "Account Type:", accountTypeCombo);
        Utils.addFormField(inputPanel, "Account ID:", accountIdField);

        JLabel warningLabel = new JLabel("Warning: This action cannot be undone!", JLabel.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.OFF_WHITE);

        JButton removeButton = Utils.createActionButton("Remove", e -> {removeAccountButton(accountTypeCombo);});

        JButton cancelButton = Utils.createActionButton("Cancel", e -> removeDialog.dispose());

        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(warningLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        accountIdField.addActionListener(e -> removeButton.doClick());

        removeDialog.add(mainPanel);
        removeDialog.setVisible(true);
    }

    private void refreshAccounts() {
        accountsPanel.removeAll();
        accountsPanel.add(createAccountScrollPane("Admins", activeAdminAccounts));
        accountsPanel.add(createAccountScrollPane("Receptionists", activeRecepAccounts));
        accountsPanel.add(createAccountScrollPane("Customers", activeCustAccounts));
        accountsPanel.revalidate();
        accountsPanel.repaint();
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
                panel.add(Utils.createActionButton("Set Open", e -> Utils.showError(panel,"hi")));//showSetAvailableMessage(room)));
            }
        } else {
            panel.add(Utils.createActionButton("Check In", e -> showCheckInDialog(room)));
            if ("Admin".equals(currentUserRole)) {
                panel.add(Utils.createActionButton("Set Closed", e -> Utils.showError(panel,"hi")));//showSetUnavailableMessage(room)));
            }
        }
        return panel;
    }


    private void removeAccountButton(JComboBox<String> accountTypeCombo){
        String accountType = (String) accountTypeCombo.getSelectedItem();
        String accountId = accountIdField.getText().trim();

        if (accountId.isEmpty()) {
            Utils.showError(removeDialog, "Please enter an account ID");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(
                    removeDialog,
                    "Delete " + accountType + " account #" + accountId + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // In real implementation: Db.delete.removeUser(Integer.parseInt(accountId), accountType)
                JOptionPane.showMessageDialog(removeDialog,
                        accountType + " account #" + accountId + " removed (Mock implementation)",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                removeDialog.dispose();
                refreshAccounts(); // Refresh the accounts display
            }
        } catch (NumberFormatException ex) {
            Utils.showError(removeDialog, "Please enter a valid numeric ID");
        }
    }

    private void showRemoveRoomDialog() {
        removeDialog = new JDialog(this, "Remove Room", true);
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

        JButton removeButton = Utils.createActionButton("Remove", e -> removeButtonAction(roomNumberField));
        JButton cancelButton = Utils.createActionButton("Cancel", e -> removeDialog.dispose());

        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> removeButton.doClick());

        removeDialog.add(mainPanel);
        removeDialog.setVisible(true);
    }

    private void removeButtonAction(JTextField roomNumberField){
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
                boolean removed = allRooms.removeIf(room -> room.getNum() == roomNumber);
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
    };

    private void showCheckInDialog(Room room) {
        checkInDialog = new JDialog(this, "Check In - Room #" + room.getNum(), true);
        checkInDialog.setSize(600, 600);
        checkInDialog.setLocationRelativeTo(this);
        checkInDialog.getContentPane().setBackground(Utils.OFF_WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.OFF_WHITE);

        JPanel roomInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("Room Information"));
        roomInfoPanel.setBackground(Utils.OFF_WHITE);

        Utils.addFormField(roomInfoPanel, "Room Number:", new JLabel(String.valueOf(room.getNum())));
        Utils.addFormField(roomInfoPanel, "Room Type:", new JLabel(Room.convertRm(room.getRoomType())));
        Utils.addFormField(roomInfoPanel, "Daily Rate:", new JLabel(String.valueOf(Room.getRate(room.getRoomType()))));

        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Utils.OFF_WHITE);
        tabbedPane.setFont(Utils.LABEL_FONT);

        JPanel existingCustomerPanel = new JPanel(new BorderLayout(10, 10));
        existingCustomerPanel.setBackground(Utils.OFF_WHITE);

        JPanel verifyPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        verifyPanel.setBackground(Utils.OFF_WHITE);

        customerIdField = new JTextField();
        JButton verifyButton = new JButton("Verify Customer");
        Utils.styleButton(verifyButton, Utils.BROWN);

        Utils.addFormField(verifyPanel, "Customer ID:", customerIdField);
        verifyPanel.add(new JLabel());
        verifyPanel.add(verifyButton);

        customerInfoLabel = new JLabel("", JLabel.CENTER);
        customerInfoLabel.setFont(Utils.LABEL_FONT);

        existingCustomerPanel.add(verifyPanel, BorderLayout.NORTH);
        existingCustomerPanel.add(customerInfoLabel, BorderLayout.CENTER);

        JPanel newCustomerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        newCustomerPanel.setBackground(Utils.OFF_WHITE);

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        Utils.addFormField(newCustomerPanel, "First Name:", firstNameField);
        Utils.addFormField(newCustomerPanel, "Last Name:", lastNameField);
        Utils.addFormField(newCustomerPanel, "Email:", emailField);
        Utils.addFormField(newCustomerPanel, "Phone:", phoneField);

        tabbedPane.addTab("Existing Customer", existingCustomerPanel);
        tabbedPane.addTab("New Customer", newCustomerPanel);

        JPanel datesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        datesPanel.setBorder(BorderFactory.createTitledBorder("Booking Dates"));
        datesPanel.setBackground(Utils.OFF_WHITE);

        checkInSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkInEditor = new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd");
        checkInSpinner.setEditor(checkInEditor);
        checkInSpinner.setValue(new Date());

        checkOutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkOutEditor = new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd");
        checkOutSpinner.setEditor(checkOutEditor);
        checkOutSpinner.setValue(new Date(System.currentTimeMillis() + 86400000));

        Utils.addFormField(datesPanel, "Check-in Date:", checkInSpinner);
        Utils.addFormField(datesPanel, "Check-out Date:", checkOutSpinner);

        if ("Admin".equals(currentUserRole)) {
            JComboBox<String> receptionistCombo = new JComboBox<>(new String[]{"Receptionist 1 (ID: 1)", "Receptionist 2 (ID: 2)"});
            Utils.addFormField(datesPanel, "Receptionist:", receptionistCombo);
        }

        JButton submitButton = Utils.createActionButton("Complete Check In", e -> {completeCheckButtonAction(room);});

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Utils.OFF_WHITE);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(datesPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);

        checkInDialog.add(mainPanel);
        checkInDialog.setVisible(true);
    }


    private void completeCheckButtonAction(Room room){
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
                    "Room #" + room.getNum() + " checked in successfully! (Mock implementation)",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            checkInDialog.dispose();
            loadRooms();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(checkInDialog,
                    "Error during check-in: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        //Insert Db.connect(user,pass) here if you want to test
        Db.connect("root", "mimimi45");
        SwingUtilities.invokeLater(() -> {
            RoomManagement roomManagement = new RoomManagement("Admin", 1);
            roomManagement.setVisible(true);
        });
    }
}