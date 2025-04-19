package com.fivestarhotel.BookingSystem;

import java.time.LocalDate;

import com.fivestarhotel.Room;
import com.fivestarhotel.users.Customer;

public class Booking {
    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }

    private Room room;
    private Customer customer;
    private BookingStatus status;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Booking(Room room, Customer customer, BookingStatus status) {
        this.room = room;
        this.customer = customer;
        this.status = status;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
