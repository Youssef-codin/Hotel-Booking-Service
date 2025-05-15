package com.fivestarhotel.GUI;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Database.Db;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class BookingCalendar {

    public JCalendar showCalendar(int roomNum) {
        // Get bookings for the room
        ArrayList<Booking> bookings = Db.select.getBookings(roomNum);

        if (bookings == null || bookings.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No bookings found for room " + roomNum);
            return null;
        }

        // Create a JCalendar
        JCalendar calendar = new JCalendar();

        // Add a listener to detect month changes
        calendar.addPropertyChangeListener("calendar", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Reapply highlights when the month changes
                JPanel dayPanel = calendar.getDayChooser().getDayPanel();
                highlightDays(bookings, dayPanel, calendar);
            }
        });

        // Highlight the initial month
        JPanel dayPanel = calendar.getDayChooser().getDayPanel();
        highlightDays(bookings, dayPanel, calendar);

        // Create a JFrame to display the calendar
        JFrame frame = new JFrame("Room " + roomNum + " Bookings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(calendar);
        frame.setVisible(true);

        return calendar;
    }

    private void highlightDays(ArrayList<Booking> bookings, JPanel dayPanel, JCalendar calendar) {
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
                    highlightDate(dayPanel, currentDate);
                }
                checkIn = checkIn.plusDays(1);
            }
        }
    }

    private void highlightDate(JPanel dayPanel, Date date) {
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
            }
        }
    }
}
