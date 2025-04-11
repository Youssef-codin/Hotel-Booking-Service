package com.fivestarhotel;
import java.util.ArrayList;
import java.util.List;

import com.fivestarhotel.users.Customer;

public class BookingManager {
    private List<Booking> bookings;
    private List<Room> rooms;

    public BookingManager() {
        this.bookings = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }



    public Booking bookRoom(int roomNumber, Customer customer) {
            Room room = null;

            for (Room r : rooms) {
                if (r.getNum() == roomNumber) {
                    room = r; 
                    break;
                }
            }

            if (room != null && !room.isOccupied()) {
                
                Booking booking = new Booking(room, customer,Booking.BookingStatus.PENDING);
                bookings.add(booking);
                room.occupy();
                return booking;
            }
            return null;
        }

}