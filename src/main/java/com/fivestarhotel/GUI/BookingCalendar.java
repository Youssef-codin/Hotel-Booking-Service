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

    private JCalendar calendar;
    private JFrame frame;
    private JPanel dayPanel;

    public JCalendar showCalendar(RoomManagement rm, int roomNum) {
        ArrayList<Booking> bookings = Db.select.getBookings(roomNum);

        if (bookings == null || bookings.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No bookings found for room " + roomNum);
            return null;
        }

        calendar = new JCalendar();

        // Add a listener to detect month changes
        calendar.addPropertyChangeListener("calendar", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dayPanel = calendar.getDayChooser().getDayPanel();
                highlightDays(bookings, roomNum);
            }
        });

        // Highlight the initial month
        dayPanel = calendar.getDayChooser().getDayPanel();
        highlightDays(bookings, roomNum);

        // Create a JFrame to display the calendar
        frame = new JFrame("Room " + roomNum + " Bookings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(calendar);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return calendar;
    }

    private void highlightDays(ArrayList<Booking> bookings, int roomNum) {
        clearHighlights();

        Date displayedDate = calendar.getDate();
        LocalDate displayedMonth = displayedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (Booking booking : bookings) {
            LocalDate checkIn = booking.getCheckInDate();
            LocalDate checkOut = booking.getCheckOutDate();

            while (!checkIn.isAfter(checkOut)) {
                // Only highlight dates in the currently displayed month
                if (checkIn.getMonth() == displayedMonth.getMonth() && checkIn.getYear() == displayedMonth.getYear()) {
                    Date currentDate = Date.from(checkIn.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    highlightDate(currentDate, booking, roomNum);
                }
                checkIn = checkIn.plusDays(1);
            }
        }
    }

    private void highlightDate(Date date, Booking booking, int roomNum) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int dayOfMonth = localDate.getDayOfMonth();

        Component[] components = dayPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton dayButton = (JButton) component;

                if (dayButton.getText().equals(String.valueOf(dayOfMonth))) {
                    dayButton.setBackground(Color.RED); // Highlight with red background
                    dayButton.setOpaque(true);
                    dayButton.setForeground(Color.WHITE); // Change text color to white

                    for (ActionListener listener : dayButton.getActionListeners()) {
                        dayButton.removeActionListener(listener);
                    }

                    dayButton.addActionListener(e -> {
                        // Show booking details for this specific booking and room
                        showBookingDetails(localDate, booking, roomNum);
                    });
                }
            }
        }
    }

    private void clearHighlights() {
        // Reset all buttons to their default appearance
        Component[] components = dayPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton dayButton = (JButton) component;
                dayButton.setBackground(null); // Reset background
                dayButton.setOpaque(false);
                dayButton.setForeground(Color.BLACK); // Reset text color
            }
        }
    }

    public void showBookingDetails(LocalDate date, Booking booking, int roomNum) {
        JDialog detailsDialog = new JDialog(frame, "Bookings for Room " + roomNum + " on " + date, true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setBackground(Utils.primaryColor);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        User customer = Db.select.getUserById(UserRoles.CUSTOMER, booking.getCustomer_id());

        panel.add(Utils.createDetailLabel("Booking ID: " + booking.getBooking_id()));
        panel.add(Utils.createDetailLabel("Room Number: " + booking.getRoom().getNum()));
        panel.add(Utils.createDetailLabel("Customer ID: " + customer.getId()));
        panel.add(Utils.createDetailLabel("Customer Name: " + customer.getFullName()));
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
