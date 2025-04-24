package com.fivestarhotel.BookingSystem;

import java.time.LocalDate;

import com.fivestarhotel.Room;

public class Booking {
    // public enum BookingStatus {
    //     Booked,
    //     NotBooked
    // }

    private int booking_id;
    private Room room;
    private int  customer_id;
    private int receptionist_id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Booking(int booking_id,Room room, int customer_id, int receptionist_id, LocalDate checkInDate, LocalDate checkOutDate) {
        this.booking_id = booking_id;
        this.room = room;
        this.customer_id = customer_id;
        this.receptionist_id = receptionist_id;
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
        return "Booking{" + ",\n" +
               "bookingId=" + booking_id + ",\n" +
               "room=" + (room != null ? room.getNum() : null) + ",\n" + // Print only the room number
               "customerId=" + customer_id + ",\n" +
               "receptionistId=" + receptionist_id + ",\n" +
               "checkInDate=" + checkInDate + ",\n" +
               "checkOutDate=" + checkOutDate + ",\n" +
               '}';
    }
   
}
