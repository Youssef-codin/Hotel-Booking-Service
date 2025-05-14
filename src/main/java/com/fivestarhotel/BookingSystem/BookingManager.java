package com.fivestarhotel.BookingSystem;

import java.time.LocalDate;

public class BookingManager {

    public BookingManager() {

    }

    public void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate == null || checkOutDate == null) {
            System.out.println("Check-in and check-out dates cannot be null.");
        }

        // Check if check-in date is not before today
        if (checkInDate.isBefore(today)) {
            System.out.println("Check-in date (" + checkInDate + ") cannot be before today (" + today + ").");
        }

        // Check if check-out date is not before check-in date
        // Also implies a minimum stay of one night
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            System.out.println("Check-out date (" + checkOutDate + ") must be after the check-in date (" + checkInDate
                    + "). Minimum stay is one night.");
        }

    }
}
