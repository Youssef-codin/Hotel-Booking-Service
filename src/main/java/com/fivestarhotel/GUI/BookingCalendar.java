package com.fivestarhotel.GUI;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Database.Db.UserRoles;
import com.fivestarhotel.users.Customer;
import com.fivestarhotel.users.User;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class BookingCalendar {

    private RoomManagement rm;
    private JCalendar calendar;
    JFrame frame;

    public JCalendar showCalendar(RoomManagement rm, int roomNum) {
        // Get bookings for the room
        ArrayList<Booking> bookings = Db.select.getBookings(roomNum);
        this.rm = rm;

        if (bookings == null || bookings.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No bookings found for room " + roomNum);
            return null;
        }

        // Create a JCalendar
        calendar = new JCalendar();

        // Add a listener to detect month changes
        calendar.addPropertyChangeListener("calendar", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Reapply highlights when the month changes
                JPanel dayPanel = calendar.getDayChooser().getDayPanel();
                highlightDays(bookings, dayPanel, calendar, roomNum);
            }
        });

        // Highlight the initial month
        JPanel dayPanel = calendar.getDayChooser().getDayPanel();
        highlightDays(bookings, dayPanel, calendar, roomNum);

        // Create a JFrame to display the calendar
        frame = new JFrame("Room " + roomNum + " Bookings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(calendar);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return calendar;
    }

    private void highlightDays(ArrayList<Booking> bookings, JPanel dayPanel, JCalendar calendar, int roomNum) {
        // Clear any previous highlights
        clearHighlights(dayPanel);

        // Get the currently displayed month and year
        Date displayedDate = calendar.getDate();
        LocalDate displayedMonth = displayedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (Booking booking : bookings) {
            LocalDate checkIn = booking.getCheckInDate();
            LocalDate checkOut = booking.getCheckOutDate();

            while (!checkIn.isAfter(checkOut)) {
                // Only highlight dates in the currently displayed month
                if (checkIn.getMonth() == displayedMonth.getMonth() && checkIn.getYear() == displayedMonth.getYear()) {
                    Date currentDate = Date.from(checkIn.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    highlightDate(dayPanel, currentDate, booking, roomNum);
                }
                checkIn = checkIn.plusDays(1);
            }
        }
    }

    private void highlightDate(JPanel dayPanel, Date date, Booking booking, int roomNum) {

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int dayOfMonth = localDate.getDayOfMonth();

        Component[] components = dayPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton dayButton = (JButton) component;

                if (dayButton.getText().equals(String.valueOf(dayOfMonth))) {
                    // Highlight the button
                    dayButton.setBackground(Color.RED); // Highlight with red background
                    dayButton.setOpaque(true);
                    dayButton.setForeground(Color.WHITE); // Change text color to white

                    dayButton.addActionListener(e -> onDateClicked(localDate, booking, roomNum));
                }
            }
        }
    }

    private void clearHighlights(JPanel dayPanel) {
        // Reset all buttons to their default appearance
        Component[] components = dayPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton dayButton = (JButton) component;
                dayButton.setBackground(null); // Reset background
                dayButton.setOpaque(false);
                dayButton.setForeground(Color.BLACK); // Reset text color
                // Remove any existing ActionListeners
                for (ActionListener listener : dayButton.getActionListeners()) {
                    dayButton.removeActionListener(listener);
                }
            }
        }
    }

    private void onDateClicked(LocalDate date, Booking booking, int roomNum) {
        showBookingDetails(booking);

    }

    public void showBookingDetails(Booking booking) {
        JDialog detailsDialog = new JDialog(frame, "Booking Details", true);
        detailsDialog.setSize(300, 250);
        detailsDialog.setBackground(Utils.BROWN);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        User customer = Db.select.getUserById(UserRoles.CUSTOMER, booking.getCustomer_id());

        panel.add(Utils.createDetailLabel("ID: " + booking.getBooking_id()));
        panel.add(Utils.createDetailLabel("Room number: " + booking.getRoom().getNum()));
        panel.add(Utils.createDetailLabel("Customer Id: " + customer.getId()));
        panel.add(Utils.createDetailLabel("Customer name: " + customer.getFullName()));
        panel.add(Utils.createDetailLabel("Check-in Date: " + booking.getCheckInDate()));
        panel.add(Utils.createDetailLabel("Check-out Date: " + booking.getCheckOutDate()));
        panel.add(Utils.createDetailLabel("Balance: " + ((Customer) customer).getBalance()));

        detailsDialog.add(panel);
        detailsDialog.setLocationRelativeTo(frame);
        detailsDialog.setVisible(true);
    }

    public JCalendar getCalendar() {
        return calendar;
    }
}
