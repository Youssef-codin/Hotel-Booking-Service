package com.fivestarhotel.BookingSystem;

import java.time.LocalDate;

import com.fivestarhotel.Room;

public class Booking {

    private int booking_id;
    private Room room;
    private int customer_id;
    private int receptionist_id;
    private boolean checkedIn;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Booking(int booking_id, Room room, int customer_id, int receptionist_id, boolean checkedIn,
            LocalDate checkInDate,
            LocalDate checkOutDate) {
        this.booking_id = booking_id;
        this.room = room;
        this.customer_id = customer_id;
        this.receptionist_id = receptionist_id;
        this.checkedIn = checkedIn;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Booking(Room room, int customer_id, int receptionist_id, LocalDate checkInDate, LocalDate checkOutDate) {

        this.room = room;
        this.customer_id = customer_id;
        this.receptionist_id = receptionist_id;
        this.checkedIn = false;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setReceptionist_id(int receptionist_id) {
        this.receptionist_id = receptionist_id;
    }

    public int getReceptionist_id() {
        return receptionist_id;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "booking_id=" + booking_id +
                ", room=" + (room != null ? room.getNum() : "null") +
                ", customer_id=" + customer_id +
                ", receptionist_id=" + receptionist_id +
                ", checkedIn=" + checkedIn +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
}
