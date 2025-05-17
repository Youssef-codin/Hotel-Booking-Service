package com.fivestarhotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Database.Db;

public class Billing {
    private int billId;
    private int bookingId;
    private BillingStatus status; // e.g., PENDING, PAID

    public enum BillingStatus {
        PENDING,
        PAID
    }

    public Billing(int billId, int bookingId, BillingStatus status) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.status = status;
    }

    public Billing(int bookingId, BillingStatus status) {
        this.bookingId = bookingId;
        this.status = status;
    }

    public static Billing.BillingStatus convertStr(String status) {
        switch (status.toLowerCase()) {
            case "pending" -> {
                return Billing.BillingStatus.PENDING;
            }
            case "paid" -> {
                return Billing.BillingStatus.PAID;
            }
            default -> {
                return null;
            }
        }
    }

    public static boolean convertBill(BillingStatus status) {
        switch (status) {
            case PENDING -> {
                return false;
            }
            case PAID -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public static double calculateAmount(Booking booking) {
        Room room = booking.getRoom();
        if (room == null) {
            System.out.print("Room cannot be null for booking ID: " + booking.getBooking_id());
        }
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (days <= 0) {
            System.out.print("Stay duration must be at least one day.");
        }
        int rate = Room.getRate(room.getRoomType());
        if (rate <= 0) {
            System.out.print("Invalid room rate for type: " + room.getRoomType());
        }
        return days * rate;
    }

    public Booking getBooking() {
        return Db.select.getBooking(bookingId);
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        if (billId <= 0) {
            System.out.print("Bill ID must be positive.");
        }
        this.billId = billId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public BillingStatus getStatus() {
        return status;
    }

    public void setStatus(BillingStatus status) {
        if (!status.equals(Billing.BillingStatus.PENDING) && !status.equals(Billing.BillingStatus.PAID)) {
            throw new IllegalArgumentException("Status must be PENDING or PAID.");
        }
        this.status = status;
    }
}
