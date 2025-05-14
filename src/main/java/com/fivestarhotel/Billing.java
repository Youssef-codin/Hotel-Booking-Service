package com.fivestarhotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Database.Db;

public class Billing {
    private int billId;
    private int bookingId;
    private int customerId;
    private double amount;
    private BillingStatus status; // e.g., PENDING, PAID
    private LocalDate createdDate;

    public enum BillingStatus {
        PENDING,
        PAID
    }

    public Billing(int billId, int bookingId, int customerId, double amount, BillingStatus status,
            LocalDate createdDate) {
        // validateIds(bookingId, customerId);
        this.billId = billId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Billing(int bookingId, int customerId, double amount, BillingStatus status, LocalDate createdDate) {
        // validateIds(bookingId, customerId);
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
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

    public static String convertBill(BillingStatus status) {
        switch (status) {
            case PENDING -> {
                return "pending";
            }
            case PAID -> {
                return "paid";
            }
            default -> {
                return null;
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

    // private void validateIds(int bookingId, int customerId) {
    // if (bookingId <= 0) {
    // throw new IllegalArgumentException("Booking ID must be positive.");
    // }
    // if (customerId <= 0) {
    // throw new IllegalArgumentException("Customer ID must be positive.");
    // }
    // }

    public Booking getBooking() {
        return Db.select.getbooking(bookingId);
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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            System.out.print("Amount cannot be negative.");
        }
        this.amount = amount;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        if (createdDate == null) {
            System.out.print("Created date cannot be null.");
        }
        this.createdDate = createdDate;
    }
}
