package com.fivestarhotel.GUI;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Database.Db.UserRoles;
import com.fivestarhotel.Billing;
import com.fivestarhotel.Payment;
import com.fivestarhotel.Room;
import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Room.RoomType;
import com.fivestarhotel.users.Admin;
import com.fivestarhotel.users.Customer;
import com.fivestarhotel.users.Receptionist;
import com.fivestarhotel.users.User;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class RoomManagement extends JFrame {
    private JTextField accountIdField, searchField, customerIdField, firstNameField, lastNameField, emailField,
            phoneField, payField, singleRateField, doubleRateField, suiteRateField, addressField, passwordField;
    private JDialog addRoomDialog;
    private JDialog removeDialog;
    private JDialog bookingDialog;
    private JSpinner checkInSpinner, checkOutSpinner;
    private int searchNumber;
    private int currentUserId;
    private String currentUserRole;
    private JPanel roomsPanel, headerPanel, accountsPanel, adminPanel, recepPanel, custPanel, anchorPanel,
            bookedRoomsPanel, customerAccountsPanel;
    private JProgressBar loadingBar;

    private JComboBox<RoomType> roomTypes;
    private JComboBox<String> accountTypeCombo = new JComboBox<>(new String[] { "Admin", "Receptionist", "Customer" });
    private JLabel customerInfoLabel;
    private JTabbedPane tabbedPane1, tabbedPane2;
    private JButton addButton, removeButton, setRateButton;
    private JScrollPane adminSection, recepSection, custSection;
    private BookingCalendar calendar = new BookingCalendar();
    private int verifiedCustomerId;
    private JTextField roomNumberField;
    private JCheckBox addManyCheckbox;

    JComboBox<String> receptionistCombo;

    // Lists
    private ArrayList<Room> allRooms;
    private ArrayList<Room> allBookedRooms = Db.select.getRooms(true);
    private ArrayList<User> allAdminAccounts = Db.select.getAllUsers(Db.UserRoles.ADMIN);
    private ArrayList<User> allRecepAccounts = Db.select.getAllUsers(Db.UserRoles.RECEPTIONIST);
    private ArrayList<User> allCustAccounts = Db.select.getAllUsers(Db.UserRoles.CUSTOMER);

    public RoomManagement(String userRole, int userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        initializeUI();
    }

    private void initializeUI() {
        Utils.setUIStyles();
        setTitle("BookIt - Room Management (" + currentUserRole + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Utils.secondaryColor);

        anchorPanel = new JPanel(new BorderLayout(10, 10));
        anchorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        anchorPanel.setBackground(Utils.secondaryColor);

        anchorPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        if (currentUserRole.matches("Admin")) {
            anchorPanel.add(tabbedInterface(), BorderLayout.CENTER);
        } else {
            anchorPanel.add(recepTabbedInterface(), BorderLayout.CENTER);
        }

        loadingBar = Utils.createLoadingBar();
        anchorPanel.add(loadingBar, BorderLayout.SOUTH);

        add(anchorPanel);
    }

    private JPanel createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Utils.secondaryColor);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 0),
                BorderFactory.createLineBorder(Utils.primaryColor, 0)));

        JButton logoutButton = Utils.createActionButton("<-", e -> returnToLogin());
        Utils.styleButton(logoutButton, Utils.primaryColor, 50, 40);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(logoutButton);
        headerPanel.add(leftPanel, BorderLayout.WEST);

        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.primaryColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setPreferredSize(new Dimension(120, 40)); // match height

        JButton searchButton = Utils.createActionButton("Search", e -> searchAction());
        Utils.styleButton(searchButton, Utils.primaryColor, 85, 40);
        searchField.addActionListener(e -> searchButton.doClick());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        if (currentUserRole.matches("Admin")) {
            headerPanel.add(createAdminControls(false), BorderLayout.EAST);
        }

        return headerPanel;
    }

    private JTabbedPane tabbedInterface() {
        tabbedPane1 = new JTabbedPane();

        tabbedPane1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane1.setBackground(Utils.secondaryColor);
        tabbedPane1.addChangeListener(e -> updateHeaderButtons());

        tabbedPane1.addTab("Rooms", createRoomsScrollPane());
        tabbedPane1.addTab("Bookings", createBookedRoomsScrollPane());
        tabbedPane1.addTab("Accounts", createAccountsPanel());

        return tabbedPane1;
    }

    private JTabbedPane recepTabbedInterface() {
        tabbedPane1 = new JTabbedPane();

        tabbedPane1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane1.setBackground(Utils.secondaryColor);

        tabbedPane1.addTab("Rooms", createRoomsScrollPane());
        tabbedPane1.addTab("Bookings", createBookedRoomsScrollPane());
        tabbedPane1.addTab("Customers", createCustomerPanel());

        return tabbedPane1;
    }

    private void searchAction() {
        try {
            searchNumber = Integer.parseInt(searchField.getText().trim());
            if (searchNumber >= 0) {
                switch (tabbedPane1.getSelectedIndex()) {
                    case 0 -> {
                        if (allRooms.size() >= searchNumber) {
                            Room room = Db.select.getRoom(searchNumber);
                            if (!(room == null)) {
                                loadRoom(room);
                            }

                        } else {
                            loadRooms();
                        }
                    }
                    case 1 -> {
                        if (allBookedRooms.size() >= searchNumber) {
                            Room room = Db.select.getRoom(searchNumber);

                            if (room != null) {
                                loadBookedRoom(room);
                            }

                        } else {
                            loadBookedRooms();
                        }
                    }

                    case 2 -> {
                        if (currentUserRole.matches("Admin")) {
                            if (!allAdminAccounts.isEmpty() && allAdminAccounts.size() >= searchNumber) {
                                User admin = Db.select.getUserById(UserRoles.ADMIN, searchNumber);
                                if (!(admin == null)) {
                                    loadAccount(admin, adminPanel);
                                }
                            } else {
                                allAdminAccounts = Db.select.getAllUsers(UserRoles.ADMIN);
                                loadAccounts(adminPanel, allAdminAccounts);
                            }

                            if (!allRecepAccounts.isEmpty() && allRecepAccounts.size() >= searchNumber) {
                                User recep = Db.select.getUserById(UserRoles.RECEPTIONIST, searchNumber);
                                if (!(recep == null)) {
                                    loadAccount(recep, recepPanel);
                                }
                            } else {
                                allRecepAccounts = Db.select.getAllUsers(UserRoles.RECEPTIONIST);
                                loadAccounts(recepPanel, allRecepAccounts);
                            }
                        }
                        if (!allCustAccounts.isEmpty()
                                && allCustAccounts.get(allCustAccounts.size() - 1).getId() >= searchNumber
                                && allCustAccounts.get(0).getId() <= searchNumber) {
                            User cust = Db.select.getUserById(UserRoles.CUSTOMER, searchNumber);
                            if (currentUserRole.matches("Admin")) {
                                if (!(cust == null)) {
                                    loadAccount(cust, custPanel);
                                }
                            } else {
                                if (!(cust == null)) {
                                    loadAccount(cust, custPanel);
                                }
                            }
                        } else {
                            allCustAccounts = Db.select.getAllUsers(UserRoles.CUSTOMER);
                            loadAccounts(custPanel, allCustAccounts);
                        }
                    }
                }
            } else {
                if (!(currentUserRole.matches("Admin"))) {
                    loadCustomerSection();
                }
                loadRooms();
                loadBookedRooms();
            }

        } catch (NumberFormatException a) {
            String searchName = searchField.getText().trim();
            switch (tabbedPane1.getSelectedIndex()) {
                case 1 -> {
                    ArrayList<Booking> bookings = Db.select.getBookingByName(searchName);
                    if (!bookings.isEmpty()) {
                        loadBookedRooms(bookings);
                    } else {
                        System.out.println("empty 210");
                        loadBookedRooms();
                    }
                }
                case 2 -> {
                    if (currentUserRole.matches("Admin")) {
                        ArrayList<User> admins = Db.select.getUsersByName(UserRoles.ADMIN, searchName);
                        if (admins != null) {
                            loadAccounts(adminPanel, admins);
                        } else {
                            allAdminAccounts = Db.select.getAllUsers(UserRoles.ADMIN);
                            loadAccounts(adminPanel, allAdminAccounts);
                        }
                        ArrayList<User> receps = Db.select.getUsersByName(UserRoles.RECEPTIONIST, searchName);
                        if (receps != null) {
                            loadAccounts(recepPanel, receps);
                        } else {
                            allRecepAccounts = Db.select.getAllUsers(UserRoles.RECEPTIONIST);
                            loadAccounts(recepPanel, allRecepAccounts);
                        }
                    }
                    ArrayList<User> custs = Db.select.getUsersByName(UserRoles.CUSTOMER, searchName);
                    if (custs != null) {
                        loadAccounts(custPanel, custs);
                        if (!(currentUserRole.matches("Admin"))) {
                            loadCustomerSection();
                            loadCustomerSection();
                        }
                    } else {
                        allCustAccounts = Db.select.getAllUsers(UserRoles.CUSTOMER);
                        loadAccounts(custPanel, allCustAccounts);
                    }
                }
            }

            if (!(currentUserRole.matches("Admin"))) {
                loadCustomerSection();
            }
            loadRooms();
        }
    }

    private JPanel createAdminControls(boolean switcher) {
        JPanel adminPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        adminPanel.setBackground(Utils.secondaryColor);
        if (!switcher) {
            addButton = Utils.createActionButton("Room +", e -> showAddRoomDialog());
            removeButton = Utils.createActionButton("Room -", e -> showRemoveRoomDialog());
            setRateButton = Utils.createActionButton("Rates", e -> showRatesDialog());
        } else {
            addButton = Utils.createActionButton("Account +", e -> showAddAccountDialog());
            removeButton = Utils.createActionButton("Account -", e -> showRemoveAccountDialog());
        }

        adminPanel.add(addButton);
        adminPanel.add(removeButton);
        adminPanel.add(setRateButton);

        return adminPanel;
    }

    private JScrollPane createRoomsScrollPane() {
        roomsPanel = new JPanel(new WrapLayout(WrapLayout.LEADING, 20, 20));
        roomsPanel.setBackground(Utils.secondaryColor);
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getViewport().setBackground(Utils.secondaryColor);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        loadRooms();

        return scrollPane;
    }

    private JScrollPane createBookedRoomsScrollPane() {
        bookedRoomsPanel = new JPanel(new WrapLayout(WrapLayout.LEADING, 20, 20));
        bookedRoomsPanel.setBackground(Utils.secondaryColor);
        bookedRoomsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(bookedRoomsPanel);
        scrollPane.getViewport().setBackground(Utils.secondaryColor);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        loadBookedRooms();

        return scrollPane;
    }

    private JScrollPane createAccountScrollPane(String title, ArrayList<User> users, JPanel sectionPanel) {
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Utils.secondaryColor);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        // Add spacing between components
        loadAccounts(sectionPanel, users);

        JScrollPane scrollPane = new JScrollPane(sectionPanel);
        scrollPane.getViewport().setBackground(Utils.secondaryColor);
        return scrollPane;
    }

    private void loadRooms() {
        roomsPanel.removeAll();
        roomsPanel.revalidate();
        roomsPanel.repaint();
        allRooms = Db.select.getRooms();

        SwingUtilities.invokeLater(() -> {
            roomsPanel.removeAll();

            if (allRooms.isEmpty()) {
                roomsPanel.add(new JLabel("No rooms found"));
            } else {
                allRooms.forEach(room -> addRoomCard(room));
            }

            roomsPanel.revalidate();
            roomsPanel.repaint();
        });
    }

    private void loadBookedRooms() {
        bookedRoomsPanel.removeAll();
        bookedRoomsPanel.revalidate();
        bookedRoomsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            bookedRoomsPanel.removeAll();

            if (allBookedRooms.isEmpty()) {
                bookedRoomsPanel.add(new JLabel("No rooms found"));
            } else {
                for (Room room : allBookedRooms) {
                    ArrayList<Booking> bookings = Db.select.getBookings(room.getNum());

                    for (Booking booking : bookings) {
                        addBookedRoomCard(booking);

                    }
                }
            }

            bookedRoomsPanel.revalidate();
            bookedRoomsPanel.repaint();
        });
    }

    private void loadBookedRooms(ArrayList<Booking> bookings) {
        bookedRoomsPanel.removeAll();
        bookedRoomsPanel.revalidate();
        bookedRoomsPanel.repaint();

        if (bookings.isEmpty()) {
            bookedRoomsPanel.removeAll();
            bookedRoomsPanel.add(new JLabel("No rooms found"));
        } else {
            for (Booking booking : bookings) {
                addBookedRoomCard(booking);
            }
        }

        bookedRoomsPanel.revalidate();
        bookedRoomsPanel.repaint();
    }

    private void loadBookedRoom(Room room) {
        bookedRoomsPanel.removeAll();
        bookedRoomsPanel.revalidate();
        bookedRoomsPanel.repaint();

        bookedRoomsPanel.removeAll();
        for (Booking booking : Db.select.getBookings(room.getNum())) {
            booking.getRoom().getNum();
            addBookedRoomCard(booking);
        }

        bookedRoomsPanel.revalidate();
        bookedRoomsPanel.repaint();
    }

    private void loadRoom(Room room) {
        roomsPanel.removeAll();
        roomsPanel.revalidate();
        roomsPanel.repaint();

        roomsPanel.removeAll();
        addRoomCard(room);
        roomsPanel.revalidate();
        roomsPanel.repaint();

    }

    private void loadAccounts(JPanel sectionPanel, ArrayList<User> users) {
        sectionPanel.removeAll();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.add(new JLabel("Loading Accounts..."));
        Timer timer = new Timer(100, e -> {
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

    private void loadAccount(User user, JPanel sectionPanel) {
        sectionPanel.removeAll();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.add(new JLabel("Loading Account..."));

        Timer timer = new Timer(100, e -> {
            sectionPanel.removeAll();
            sectionPanel.add(addAccountCard(user));
            sectionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void addRoomCard(Room room) {
        JPanel card = Utils.createStyledPanel(15, Utils.primaryColor, true);
        card.setPreferredSize(new Dimension(300, 200));

        // Room Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Utils.secondaryColor);

        JLabel numberLabel = new JLabel("Room #" + room.getNum());
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberLabel.setForeground(Utils.primaryColor);

        infoPanel.add(numberLabel);
        infoPanel.add(new JLabel("Floor: " + ((room.getNum() - 1) / 100 + 1)));
        infoPanel.add(new JLabel("Type: " + room.getRoomType()));
        infoPanel.add(createStatusLabel(room.isCheckedIn()));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(createRoomButtons(room), BorderLayout.SOUTH);
        roomsPanel.add(card);
    }

    private void addBookedRoomCard(Booking booking) {
        Room room = booking.getRoom();

        JPanel card = Utils.createStyledPanel(15, Utils.primaryColor, true);
        card.setPreferredSize(new Dimension(300, 290));

        // Room Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Utils.secondaryColor);

        JLabel numberLabel = new JLabel("Room #" + room.getNum());
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberLabel.setForeground(Utils.primaryColor);

        User user = Db.select.getUserById(UserRoles.CUSTOMER, booking.getCustomer_id());

        infoPanel.add(numberLabel);
        infoPanel.add(new JLabel("Customer: " + user.getFullName()));
        infoPanel.add(new JLabel("Floor: " + ((room.getNum() - 1) / 100 + 1)));
        infoPanel.add(new JLabel("Type: " + room.getRoomType()));
        infoPanel.add(createStatusLabel(booking.isCheckedIn()));

        System.out.println("checked in " + booking.isCheckedIn());

        card.add(infoPanel, BorderLayout.CENTER);

        infoPanel.add(new JLabel("Check-in: " + booking.getCheckInDate()));
        infoPanel.add(new JLabel("Check-out: " + booking.getCheckOutDate()));
        card.add(createBookedRoomButtons(booking), BorderLayout.SOUTH);
        bookedRoomsPanel.add(card);

    }

    private JPanel addAccountCard(User user) {
        JPanel card = Utils.createStyledPanel(10, Utils.primaryColor, true);
        card.setLayout(new BorderLayout());

        // Make card fill available width
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fixed height
        card.setPreferredSize(new Dimension(card.getPreferredSize().width, 40));

        JLabel infoLabel = new JLabel(
                user.getId() + "  |     " + user.getFullName());

        // Add padding
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JButton detailsButton = Utils.createActionButton("i", e -> showUserDetails(user));
        Utils.styleButton(detailsButton, Utils.primaryColor, 20, 20);

        card.add(infoLabel, BorderLayout.CENTER);
        card.add(detailsButton, BorderLayout.EAST);

        return card;
    }

    private void returnToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new BookItLogin().setVisible(true));
    }

    private void showRatesDialog() {
        JDialog ratesDialog = new JDialog();
        ratesDialog.setSize(400, 250);
        ratesDialog.setLocationRelativeTo(this);
        ratesDialog.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Utils.secondaryColor);

        singleRateField = new JTextField();
        singleRateField.setText(String.valueOf(Db.select.getRate(RoomType.SINGLE)));
        Utils.addFormField(formPanel, "Single Rate", singleRateField);
        doubleRateField = new JTextField();
        doubleRateField.setText(String.valueOf(Db.select.getRate(RoomType.DOUBLE)));
        Utils.addFormField(formPanel, "Double Rate", doubleRateField);
        suiteRateField = new JTextField();
        suiteRateField.setText(String.valueOf(Db.select.getRate(RoomType.SUITE)));
        Utils.addFormField(formPanel, "suite Rate", suiteRateField);

        JButton changeRatesButton = Utils.createActionButton("Change Rates", e -> changeRatesAction());

        mainPanel.add(formPanel);
        formPanel.add(Box.createHorizontalGlue());
        formPanel.add(changeRatesButton);
        ratesDialog.add(mainPanel);
        ratesDialog.setVisible(true);
    }

    private void changeRatesAction() {
        String single = singleRateField.getText();
        String doubleRate = doubleRateField.getText();
        String suite = suiteRateField.getText();

        if (single.isEmpty() || doubleRate.isEmpty() || suite.isEmpty()) {
            Utils.showError(null, "Please don't leave any fields empty.");
            return;
        }

        Db.update.rates(RoomType.SINGLE, Integer.parseInt(single));
        Db.update.rates(RoomType.DOUBLE, Integer.parseInt(doubleRate));
        Db.update.rates(RoomType.SUITE, Integer.parseInt(suite));

        Room.setRate(RoomType.SINGLE, Integer.parseInt(single));
        Room.setRate(RoomType.DOUBLE, Integer.parseInt(doubleRate));
        Room.setRate(RoomType.SUITE, Integer.parseInt(suite));

        JOptionPane.showMessageDialog(addRoomDialog,
                "Room rates changed Successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        loadRooms();
        loadBookedRooms();
    }

    private boolean addRoomAction(JTextField roomNumberField) {
        if (roomNumberField.getText().trim().isEmpty()) {
            switch (JOptionPane.showConfirmDialog(addRoomDialog, "No entered number, add room anyway?")) {
                case 0 -> {
                    RoomType roomType = (RoomType) roomTypes.getSelectedItem();
                    Db.create.addRoom(roomType);
                    JOptionPane.showMessageDialog(
                            addRoomDialog,
                            "Room added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    addRoomDialog.dispose();
                    allRooms = Db.select.getRooms();
                    loadRooms();
                }
                case 2 -> {
                    addRoomDialog.dispose();
                    allRooms = Db.select.getRooms();
                    loadRooms();
                }
            }
            return true;
        }

        try {
            int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
            RoomType roomType = (RoomType) roomTypes.getSelectedItem();
            if (addManyCheckbox.isSelected()) {
                Db.create.addRooms(roomType, roomNumber);
                JOptionPane.showMessageDialog(addRoomDialog,
                        roomNumber + " Rooms added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                addRoomDialog.dispose();
                allRooms = Db.select.getRooms();
                loadRooms();
            } else {
                if (Db.create.addRoom(roomNumber, roomType)) {
                    JOptionPane.showMessageDialog(addRoomDialog,
                            "Room #" + roomNumber + " added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    addRoomDialog.dispose();
                    allRooms = Db.select.getRooms();
                    loadRooms();
                } else {
                    Utils.showError(addRoomDialog, "Room already exists");
                }
            }
        } catch (NumberFormatException ex) {
            Utils.showError(addRoomDialog, "Please enter a valid room number");
        }
        return true;
    }

    private JPanel createAccountsPanel() {
        accountsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        adminPanel = new JPanel();
        recepPanel = new JPanel();
        custPanel = new JPanel();
        accountsPanel.setBackground(Utils.secondaryColor);

        // Reload account lists from database
        allAdminAccounts = Db.select.getAllUsers(Db.UserRoles.ADMIN);
        allRecepAccounts = Db.select.getAllUsers(Db.UserRoles.RECEPTIONIST);
        allCustAccounts = Db.select.getAllUsers(Db.UserRoles.CUSTOMER);

        adminSection = createAccountScrollPane("Admins", allAdminAccounts, adminPanel);
        recepSection = createAccountScrollPane("Receptionists", allRecepAccounts, recepPanel);
        custSection = createAccountScrollPane("Customers", allCustAccounts, custPanel);

        accountsPanel.add(adminSection);
        accountsPanel.add(recepSection);
        accountsPanel.add(custSection);

        return accountsPanel;
    }

    private JPanel createCustomerPanel() {
        customerAccountsPanel = new JPanel(new GridLayout(1, 1, 10, 0));
        custPanel = new JPanel();
        customerAccountsPanel.setBackground(Utils.secondaryColor);
        allCustAccounts = Db.select.getAllUsers(Db.UserRoles.CUSTOMER);
        custSection = createAccountScrollPane("Customers", allCustAccounts, custPanel);
        customerAccountsPanel.add(custSection);

        return customerAccountsPanel;
    }

    private void refreshAccounts() {
        accountsPanel.removeAll();
        accountsPanel.add(createAccountScrollPane("Admins", Db.select.getAllUsers(Db.UserRoles.ADMIN), adminPanel));
        accountsPanel.add(
                createAccountScrollPane("Receptionists", Db.select.getAllUsers(Db.UserRoles.RECEPTIONIST), recepPanel));
        accountsPanel
                .add(createAccountScrollPane("Customers", Db.select.getAllUsers(Db.UserRoles.CUSTOMER), custPanel));
        accountsPanel.revalidate();
        accountsPanel.repaint();
    }

    private void showUserDetails(User user) {
        JDialog detailsDialog = new JDialog(this, "User Details", true);
        detailsDialog.setSize(300, 250);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 0));
        panel.setBackground(Utils.secondaryColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(Utils.createDetailLabel("ID: " + user.getId()));
        panel.add(Utils.createDetailLabel("Name: " + user.getFullName()));
        panel.add(Utils.createDetailLabel("Email: " + user.getEmail()));
        if (user instanceof Customer) {
            panel.add(Utils.createDetailLabel("Phone: " + ((Customer) user).getPhone()));
            panel.add(Utils.createDetailLabel("Address: " + ((Customer) user).getAddress()));
            panel.add(Utils.createDetailLabel("Balance: " + ((Customer) user).getBalance()));
        }
        detailsDialog.add(panel);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }

    private void updateHeaderButtons() {
        Component[] components = headerPanel.getComponents();
        headerPanel.remove(components[components.length - 1]);
        if (tabbedPane1.getSelectedIndex() == 2) { // Accounts tab
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
        addAccountDialog.getContentPane().setBackground(Utils.secondaryColor);
        addAccountDialog.setSize(450, 380);
        addAccountDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Utils.secondaryColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Utils.secondaryColor);

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Utils.addFormField(formPanel, "Account Type:", accountTypeCombo);
        Utils.addFormField(formPanel, "First Name:", firstNameField);
        Utils.addFormField(formPanel, "Last Name:", lastNameField);
        Utils.addFormField(formPanel, "Email:", emailField);
        Utils.addFormField(formPanel, "Password:", passwordField);

        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneFieldLocal = new JTextField();
        phoneLabel.setFont(Utils.LABEL_FONT);
        phoneLabel.setForeground(Utils.primaryColor);
        phoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        phoneFieldLocal.setFont(Utils.LABEL_FONT);
        phoneFieldLocal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.primaryColor, 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressFieldLocal = new JTextField();
        addressLabel.setFont(Utils.LABEL_FONT);
        addressLabel.setForeground(Utils.primaryColor);
        addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        addressFieldLocal.setFont(Utils.LABEL_FONT);
        addressFieldLocal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utils.primaryColor, 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));

        formPanel.add(phoneLabel);
        formPanel.add(phoneFieldLocal);
        formPanel.add(addressLabel);
        formPanel.add(addressFieldLocal);

        phoneLabel.setVisible(false);
        phoneFieldLocal.setVisible(false);
        addressLabel.setVisible(false);
        addressFieldLocal.setVisible(false);

        accountTypeCombo.addActionListener(e -> {
            addAccountDialog.pack();
            addAccountDialog.setMinimumSize(new Dimension(400, 350));
            addAccountDialog.setLocationRelativeTo(RoomManagement.this);
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
        buttonPanel.setBackground(Utils.secondaryColor);

        JButton submitButton = Utils.createActionButton("Add", e -> {
            String accountType = (String) accountTypeCombo.getSelectedItem();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String phone = phoneFieldLocal.getText().trim();
            String address = addressFieldLocal.getText().trim();

            // Use the validateInputs method for complete validation
            if (!Utils.validateInputs(firstName, lastName, email, password, phone, address, accountType,
                    addAccountDialog)) {
                return;
            }

            if (accountType.equals("Admin")) {
                if (Db.create.signUpUser(new Admin(firstName, lastName, email, password)) == null) {
                    Utils.showError(bookingDialog, "User already exists!");
                    return;
                }
            } else if (accountType.equals("Receptionist")) {
                if (Db.create.signUpUser(new Receptionist(firstName, lastName, email, password)) == null) {
                    Utils.showError(bookingDialog, "User already exists!");
                    return;
                }
            } else {
                if (Db.create
                        .signUpUser(new Customer(firstName, lastName, email, password, phone, address, 0)) == null) {
                    Utils.showError(bookingDialog, "User already exists!");
                    return;
                }
            }

            JOptionPane.showMessageDialog(addAccountDialog, "Account created successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            addAccountDialog.dispose();
            loadAccountSections();
            refreshAccounts();
        });

        JButton cancelButton = Utils.createActionButton("Cancel", e -> {
            addAccountDialog.dispose();
        });
        passwordField.addActionListener(e -> submitButton.doClick());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addAccountDialog.add(mainPanel);
        addAccountDialog.pack();
        addAccountDialog.setMinimumSize(new Dimension(400, 350));
        addAccountDialog.setLocationRelativeTo(this);
        addAccountDialog.setVisible(true);

        refreshAccounts();
    }

    private void showRemoveAccountDialog() {
        JDialog removeDialog = new JDialog(this, "Remove Account", true);
        removeDialog.setSize(400, 200);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Utils.secondaryColor);
        accountIdField = new JTextField();

        Utils.addFormField(inputPanel, "Account Type:", accountTypeCombo);
        Utils.addFormField(inputPanel, "Account ID:", accountIdField);

        JLabel warningLabel = new JLabel("Warning: This action cannot be undone!", JLabel.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.secondaryColor);

        JButton removeButton = Utils.createActionButton("Remove", e -> {
            removeAccountButton(accountTypeCombo);
        });

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

    private void loadAccountSections() {
        accountsPanel.removeAll();
        accountsPanel.add(adminSection);
        accountsPanel.add(recepSection);
        accountsPanel.add(custSection);
        accountsPanel.revalidate();
        accountsPanel.repaint();
    }

    private void loadCustomerSection() {
        customerAccountsPanel.removeAll();
        customerAccountsPanel.add(custSection);
        customerAccountsPanel.revalidate();
        customerAccountsPanel.repaint();
    }

    private JLabel createStatusLabel(boolean isCheckedIn) {
        JLabel label = new JLabel("Status: " + (isCheckedIn ? "Occupied" : "un-Occupied"));
        label.setForeground(isCheckedIn ? Color.RED : new Color(0, 128, 0));
        return label;
    }

    private JPanel createRoomButtons(Room room) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(Utils.secondaryColor);

        panel.add(Utils.createActionButton("Book", e -> showBookingDialog(room)));
        panel.add(Utils.createActionButton("Bookings", e -> calendar.showCalendar(room.getNum())));

        return panel;
    }

    private JPanel createBookedRoomButtons(Booking booking) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(Utils.secondaryColor);
        if (!booking.isCheckedIn()) {
            panel.add(Utils.createActionButton("Check in", e -> checkInAction(booking)));
            panel.add(Utils.createActionButton("Cancel", e -> cancelBookingDialoge(booking)));

        } else {
            panel.add(Utils.createActionButton("Check out", e -> showCheckOutDialog(booking)));
            panel.add(Utils.createActionButton("Cancel", e -> cancelBookingDialoge(booking)));

        }
        return panel;
    }

    private void cancelBookingDialoge(Booking booking) {
        int confirm = JOptionPane.showConfirmDialog(
                removeDialog,
                "Are you sure you want to Cancel this booking?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && !booking.getRoom().isCheckedIn()) {
            Db.delete.bill(booking.getBooking_id());
            Db.delete.booking(booking.getBooking_id());
            if (booking.isCheckedIn()) {
                Db.update.roomStatus(booking.getRoom().getNum(), false);
                Db.update.roomCheckIn(booking.getRoom().getNum(), false);
            }

            JOptionPane.showMessageDialog(null, "Successfully cancelled booking.");
            System.out.println("Successfully cancelled booking!");
        } else if (booking.getRoom().isCheckedIn()) {
            Utils.showError(null, "Check-out the person in the room before deleting the booking.");
        }

        loadRooms();
        loadBookedRooms();
    }

    private void checkInAction(Booking booking) {
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckedIn(true);

        Db.update.booking(booking);
        Db.update.roomCheckIn(booking.getRoom().getNum(), true);

        JOptionPane.showMessageDialog(null, "Successfully Checked in!", "Check in status",
                JOptionPane.INFORMATION_MESSAGE);

        loadRooms();
        loadBookedRooms();
    }

    private void showCheckOutDialog(Booking booking) {
        Room room = booking.getRoom();

        JDialog checkOutPanel = new JDialog(this, "Room #" + room.getNum(), true);
        checkOutPanel.setSize(400, 600);
        checkOutPanel.setLocationRelativeTo(this);
        checkOutPanel.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel roomInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("Room Information"));
        roomInfoPanel.setBackground(Utils.secondaryColor);

        Utils.addFormField(roomInfoPanel, "Room Number:", new JLabel(String.valueOf(room.getNum())));
        Utils.addFormField(roomInfoPanel, "Room Type:", new JLabel(Room.convertRm(room.getRoomType())));
        Utils.addFormField(roomInfoPanel, "Daily Rate:", new JLabel(String.valueOf(Room.getRate(room.getRoomType()))));

        JPanel bookingInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        bookingInfoPanel.setBorder(BorderFactory.createTitledBorder("Booking Information"));
        bookingInfoPanel.setBackground(Utils.secondaryColor);

        JPanel payPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        payPanel.setBorder(BorderFactory.createTitledBorder("Pay"));
        payPanel.setBackground(Utils.secondaryColor);

        double amount = Billing.calculateAmount(booking);
        Customer customer = (Customer) Db.select.getUserById(UserRoles.CUSTOMER, booking.getCustomer_id());

        Utils.addFormField(bookingInfoPanel, "Customer ID: ", new JLabel(String.valueOf(customer.getId())));
        Utils.addFormField(bookingInfoPanel, "Balance: ", new JLabel(String.valueOf(customer.getBalance())));
        Utils.addFormField(bookingInfoPanel, "Check-in Date: ", new JLabel(String.valueOf(booking.getCheckInDate())));
        Utils.addFormField(bookingInfoPanel, "Check-out Date: ", new JLabel(String.valueOf(booking.getCheckOutDate())));
        Utils.addFormField(bookingInfoPanel, "Bill: ", new JLabel(String.valueOf(Billing.calculateAmount(booking))));

        payField = new JTextField();
        Utils.addFormField(payPanel, "Pay here: ", payField);
        payPanel.add(Box.createHorizontalGlue());
        payPanel.add(Utils.createActionButton("Check-out", e -> processCheckOut(amount, booking)));

        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);
        mainPanel.add(bookingInfoPanel, BorderLayout.CENTER);
        mainPanel.add(payPanel, BorderLayout.SOUTH);
        checkOutPanel.add(mainPanel);
        checkOutPanel.setVisible(true);

    }

    private void showAddRoomDialog() {
        addRoomDialog = new JDialog(this, "Add New Room", true);
        addRoomDialog.setSize(400, 250);
        addRoomDialog.setLocationRelativeTo(this);
        addRoomDialog.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Utils.secondaryColor);

        roomNumberField = new JTextField();
        Utils.addFormField(formPanel, "Room number: ", roomNumberField);

        roomTypes = new JComboBox<>(RoomType.values());
        Utils.addFormField(formPanel, "Room Type:", roomTypes);

        addManyCheckbox = new JCheckBox();
        Utils.addFormField(formPanel, "Add Many Rooms:", addManyCheckbox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.secondaryColor);

        JButton submitButton = Utils.createActionButton("Add Room", e -> addRoomAction(roomNumberField));
        JButton cancelButton = Utils.createActionButton("Cancel", e -> addRoomDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> submitButton.doClick());

        addRoomDialog.add(mainPanel);
        addRoomDialog.setVisible(true);
    }

    private void processCheckOut(double billDue, Booking booking) {
        User user = Db.select.getUserById(UserRoles.CUSTOMER, booking.getCustomer_id());
        Customer customer = (Customer) user;
        Payment payment = new Payment(billDue, customer);

        if (payField.getText().isEmpty()) {
            return;
        }

        double payAmount = Double.parseDouble(payField.getText());

        double processed = payment.process(payAmount, customer.getId(),
                Db.select.getBillBooking(booking.getBooking_id()).getBillId());
        if (processed == 0) {
            JOptionPane.showMessageDialog(null,
                    "Payment successfully processed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("PAID");
            Db.delete.bill(booking.getBooking_id());
            Db.delete.booking(booking.getBooking_id());
            Db.update.roomStatus(booking.getRoom().getNum(), false);
            Db.update.roomCheckIn(booking.getRoom().getNum(), false);

        } else if (processed == -2) {
            Utils.showError(null, "Payment was not complete.");

        } else {
            JOptionPane.showMessageDialog(null,
                    "Payment successfully processed, refunded " + processed + " into you account.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            Db.delete.bill(booking.getBooking_id());
            Db.delete.booking(booking.getBooking_id());
            Db.update.roomStatus(booking.getRoom().getNum(), false);
            Db.update.roomCheckIn(booking.getRoom().getNum(), false);
        }

        allCustAccounts = Db.select.getAllUsers(UserRoles.CUSTOMER);
        loadAccounts(custPanel, allCustAccounts);
        loadRooms();
        loadBookedRooms();
    }

    // private JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]
    // {"Admin", "Receptionist", "Customer" });
    private void removeAccountButton(JComboBox<String> accountTypeCombo) {
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
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = false;

                switch (accountType) {
                    case "Customer" -> deleted = Db.delete.deleteUser(Integer.parseInt(accountId), UserRoles.CUSTOMER);
                    case "Receptionist" ->
                        deleted = Db.delete.deleteUser(Integer.parseInt(accountId), UserRoles.RECEPTIONIST);
                    case "Admin" -> deleted = Db.delete.deleteUser(Integer.parseInt(accountId), UserRoles.ADMIN);
                }

                if (deleted) {
                    JOptionPane.showMessageDialog(removeDialog,
                            accountType + " account #" + accountId + " removed",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Utils.showError(null,
                            "Error: User not found or check out the customer before trying to delete them");
                }

                allCustAccounts = Db.select.getAllUsers(UserRoles.CUSTOMER);
                loadAccounts(custPanel, allCustAccounts);
                allAdminAccounts = Db.select.getAllUsers(UserRoles.ADMIN);
                loadAccounts(adminPanel, allAdminAccounts);
                allRecepAccounts = Db.select.getAllUsers(UserRoles.RECEPTIONIST);
                loadAccounts(recepPanel, allRecepAccounts);
            }
        } catch (NumberFormatException ex) {
            Utils.showError(removeDialog, "Please enter a valid numeric ID");
        }
    }

    private void showRemoveRoomDialog() {
        removeDialog = new JDialog(this, "Remove Room", true);
        removeDialog.setSize(400, 200);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(Utils.secondaryColor);

        JTextField roomNumberField = new JTextField();
        Utils.addFormField(inputPanel, "Room Number to Remove:", roomNumberField);

        JLabel warningLabel = new JLabel("Warning: This cannot be undone!", JLabel.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(warningLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Utils.secondaryColor);

        JButton removeButton = Utils.createActionButton("Remove", e -> removeButtonAction(roomNumberField));
        JButton cancelButton = Utils.createActionButton("Cancel", e -> removeDialog.dispose());

        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        roomNumberField.addActionListener(e -> removeButton.doClick());

        removeDialog.add(mainPanel);
        removeDialog.setVisible(true);
    }

    // 0 done
    // -1 room not found
    // -2 SQLError: make sure you remove all bookings before removing the room
    private void removeButtonAction(JTextField roomNumberField) {
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
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {

                int dbRemoved = Db.delete.room(roomNumber);
                boolean removed = allRooms.removeIf(room -> room.getNum() == roomNumber);
                if (removed && dbRemoved == 0) {

                    JOptionPane.showMessageDialog(removeDialog,
                            "Room #" + roomNumber + " was successfully removed",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    removeDialog.dispose();
                    loadRooms();
                } else if (dbRemoved == -1) {
                    Utils.showError(removeDialog, "Room #" + roomNumber + " not found");
                } else {
                    Utils.showError(removeDialog, "SQLError Room#" + roomNumber
                            + "make sure you remove all bookings before removing the room");

                }
            }

        } catch (NumberFormatException ex) {
            Utils.showError(removeDialog, "Please enter a valid room number");
        }
    }

    private void showBookingDialog(Room room) {
        bookingDialog = new JDialog(this, "Book - Room #" + room.getNum(), true);
        bookingDialog.setSize(600, 600);
        bookingDialog.setLocationRelativeTo(this);
        bookingDialog.getContentPane().setBackground(Utils.secondaryColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Utils.secondaryColor);

        JPanel roomInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("Room Information"));
        roomInfoPanel.setBackground(Utils.secondaryColor);

        Utils.addFormField(roomInfoPanel, "Room Number:", new JLabel(String.valueOf(room.getNum())));
        Utils.addFormField(roomInfoPanel, "Room Type:", new JLabel(Room.convertRm(room.getRoomType())));
        Utils.addFormField(roomInfoPanel, "Daily Rate:", new JLabel(String.valueOf(Room.getRate(room.getRoomType()))));

        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);

        tabbedPane2 = new JTabbedPane();
        tabbedPane2.setBackground(Utils.secondaryColor);
        tabbedPane2.setFont(Utils.LABEL_FONT);

        JPanel existingCustomerPanel = new JPanel(new BorderLayout(10, 10));
        existingCustomerPanel.setBackground(Utils.secondaryColor);

        JPanel verifyPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        verifyPanel.setBackground(Utils.secondaryColor);

        customerIdField = new JTextField();
        JButton verifyButton = Utils.createActionButton("Verify Customer", e -> verifyCustomerAction());
        Utils.styleButton(verifyButton, Utils.primaryColor);
        customerIdField.addActionListener(e -> verifyButton.doClick());
        Utils.addFormField(verifyPanel, "Customer ID:", customerIdField);
        verifyPanel.add(new JLabel());
        verifyPanel.add(verifyButton);

        customerInfoLabel = new JLabel("", JLabel.CENTER);
        customerInfoLabel.setFont(Utils.LABEL_FONT);

        existingCustomerPanel.add(verifyPanel, BorderLayout.NORTH);
        existingCustomerPanel.add(customerInfoLabel, BorderLayout.CENTER);

        JPanel newCustomerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        newCustomerPanel.setBackground(Utils.secondaryColor);

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();

        Utils.addFormField(newCustomerPanel, "First Name:", firstNameField);
        Utils.addFormField(newCustomerPanel, "Last Name:", lastNameField);
        Utils.addFormField(newCustomerPanel, "Email:", emailField);
        Utils.addFormField(newCustomerPanel, "Password:", passwordField);
        Utils.addFormField(newCustomerPanel, "Phone:", phoneField);
        Utils.addFormField(newCustomerPanel, "Address", addressField);

        tabbedPane2.addTab("Existing Customer", existingCustomerPanel);
        tabbedPane2.addTab("New Customer", newCustomerPanel);

        // Modified tab change listener to preserve verification
        // Modified tab change listener in showBookingDialog method
        tabbedPane2.addChangeListener(e -> {
            if (tabbedPane2.getSelectedIndex() == 0) { // Existing customer tab
                // Clear new customer fields
                firstNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                phoneField.setText("");
                addressField.setText("");

                // Display verification status if customer was previously verified
                if (verifiedCustomerId != -1) {
                    customerInfoLabel.setText("<html><b>Customer verified</b> - ready to check in</html>");
                    customerInfoLabel.setForeground(new Color(0, 128, 0));
                }
            } else { // New customer tab
                // Reset the verification status
                verifiedCustomerId = -1;
                customerInfoLabel.setText("");
                customerIdField.setText("");
            }
        });

        JPanel datesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        datesPanel.setBorder(BorderFactory.createTitledBorder("Booking Dates"));
        datesPanel.setBackground(Utils.secondaryColor);

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

        }
        if ("Admin".equals(currentUserRole)) {
            // Populate receptionist combo (existing logic)
            ArrayList<User> receptionists = Db.select.getAllUsers(UserRoles.RECEPTIONIST);
            String[] recepStrings = new String[receptionists.size()];

            for (int i = 0; i < receptionists.size(); i++) {
                recepStrings[i] = "Receptionist ID: " + receptionists.get(i).getId();
            }

            receptionistCombo = new JComboBox<>(recepStrings);
            Utils.addFormField(datesPanel, "Receptionist:", receptionistCombo);
        } else {
            // For receptionists, auto-fill their ID
            receptionistCombo = new JComboBox<>(new String[] { "Receptionist ID: " + currentUserId });
            receptionistCombo.setEnabled(false); // Disable editing
            Utils.addFormField(datesPanel, "Receptionist:", receptionistCombo);
        }

        JButton submitButton = Utils.createActionButton("Finish Booking", e -> {
            finishBookingAction(room);
        });

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Utils.secondaryColor);
        centerPanel.add(tabbedPane2, BorderLayout.CENTER);
        centerPanel.add(datesPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);
        bookingDialog.add(mainPanel);
        bookingDialog.setVisible(true);
    }

    private int verifyCustomerAction() {
        String customerIdText = customerIdField.getText();
        if (customerIdField.getText().isEmpty()) {
            customerInfoLabel.setText("Please enter a customer ID");
            customerInfoLabel.setForeground(Color.BLACK);
            return -1;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdText);
        } catch (NumberFormatException e) {
            customerInfoLabel.setText("<html><b>Invalid input</b> - Please enter a valid numerical customer ID</html>");
            customerInfoLabel.setForeground(Color.RED);
            return -1;
        }

        if (customerId <= 0 && customerIdField.getText().isEmpty()) {
            customerInfoLabel.setText("<html><b>Invalid input</b> - please enter a valid customer ID</html>");
            customerInfoLabel.setForeground(Color.RED);
            return -1;
        } else if (customerId > 0) {

            User user = Db.select.getUserById(UserRoles.CUSTOMER, customerId);
            if (user != null) {
                customerInfoLabel.setText("<html><b>Customer verified</b> - ready to check in</html>");
                customerInfoLabel.setForeground(new Color(0, 128, 0));
                verifiedCustomerId = customerId;
                return customerId;
            }
            customerInfoLabel.setText("<html><b>Customer not found</b> - please register new customer</html>");
            customerInfoLabel.setForeground(Color.RED);
            return -1;
        } else {
            customerInfoLabel.setText("<html><b>Invalid input</b> - please enter a valid customer ID</html>");
            customerInfoLabel.setForeground(Color.RED);
            return -1;
        }
    }

    private void finishBookingAction(Room room) {
        int customerId;
        int selectedTab = tabbedPane2.getSelectedIndex();
        System.out.println(selectedTab);

        if (selectedTab == 0) { // Existing Customer Tab
            // Use verifiedCustomerId if available
            if (verifiedCustomerId == -1) {
                JOptionPane.showMessageDialog(bookingDialog,
                        "Please verify customer ID first",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            customerId = verifiedCustomerId;
        } else { // New Customer Tab
            // Validate new customer fields
            if (!Utils.validateInputs(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    passwordField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    "Customer", this)) {
                return; // Exit if validation fails
            }

            // Create new customer
            Customer newCustomer = new Customer(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    passwordField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    0);

            User createdUser = Db.create.signUpUser(newCustomer);
            if (createdUser == null) {
                Utils.showError(bookingDialog, "User already exists!");
                return;
            }
            customerId = createdUser.getId();
        }

        // If we reach here, we have a valid customerId
        // Proceed with booking
        Date checkInDate = (Date) checkInSpinner.getValue();
        Date checkOutDate = (Date) checkOutSpinner.getValue();

        LocalDate checkInLocalDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (checkOutDate.before(checkInDate)) {
            JOptionPane.showMessageDialog(bookingDialog,
                    "Check-out date must be after check-in date",
                    "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedRecep = (String) receptionistCombo.getSelectedItem();
        String[] parts = selectedRecep.split(": ");
        int receptionist_id = Integer.parseInt(parts[1]);

        Booking booking = new Booking(room, customerId, receptionist_id, checkInLocalDate, checkOutLocalDate);
        int bookingResult = Db.create.addBooking(booking);

        // -3 sql error
        // -2 invalid input
        // -1 room not avaliable at requested dates
        // 0 successful
        switch (bookingResult) {
            case 0:
                JOptionPane.showMessageDialog(bookingDialog,
                        "Room #" + room.getNum() + " booked successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                bookingDialog.dispose();
                loadRooms();
                loadBookedRooms();
                break;
            case -1:
                JOptionPane.showMessageDialog(bookingDialog,
                        "Booking failed: Room not available at requested dates.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case -2:
                JOptionPane.showMessageDialog(bookingDialog,
                        "Booking failed: Invalid date inputs.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case -3:
                JOptionPane.showMessageDialog(bookingDialog,
                        "Booking failed: Database error.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
        allBookedRooms = Db.select.getRooms(true);
    }

    public static void main(String[] args) {
        // Insert Db.connect(user,pass) here if you want to test
        Db.connect("root", "yoyo8080");
        // run at least once
        Db.select.loadRates();
        SwingUtilities.invokeLater(() -> {
            RoomManagement roomManagement = new RoomManagement("Admin", 1);
            roomManagement.setVisible(true);
        });
    }
}
