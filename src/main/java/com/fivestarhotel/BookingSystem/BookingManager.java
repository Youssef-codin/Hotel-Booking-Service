package com.fivestarhotel.BookingSystem;

import java.time.LocalDate;

public class BookingManager {

    public BookingManager() {

    }

    public boolean validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate == null || checkOutDate == null) {
            System.out.println("Check-in and check-out dates cannot be null.");
            return false;
        }

        if (checkInDate.isBefore(today)) {
            System.out.println("Check-in date (" + checkInDate + ") cannot be before today (" + today + ").");
            return false;
        }

        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            System.out.println("Check-out date (" + checkOutDate + ") must be after the check-in date (" + checkInDate
                    + "). Minimum stay is one night.");
            return false;
        }

        return true;
    }
}
