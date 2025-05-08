package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.BookingSystem.BookingManager;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType; // Ensure this is the correct package for the Booking class

public class Update {

    public void resetIncrement() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE room AUTO_INCREMENT = 1;");
            int rows = ps.executeUpdate();
            System.out.println("updated " + rows + " rows.");

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");

        }
    }

    public void updateRates(RoomType type, int newRate) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE rates SET room_rate = ? WHERE room_type = ?");
            ps.setInt(1, newRate);
            ps.setString(2, Room.convertRm(type));

            int rows = ps.executeUpdate(); // gets the num of rows updated
            System.out.println("updated " + rows + " rows.");

        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();

        }
    }
    

    public void rooms(ArrayList<Room> newRooms) {

        int updates = 0;
        try (Connection conn = Db.connect()) {
            for (int i = 0; i < newRooms.size(); i++) {
                PreparedStatement ps = conn
                        .prepareStatement("UPDATE room SET room_type = ?, room_status = ? WHERE room_number = ?");
                ps.setString(1, Room.convertRm(newRooms.get(i).getRoomType()));
                ps.setBoolean(2, newRooms.get(i).getStatus());
                ps.setInt(3, newRooms.get(i).getNum());
                updates += ps.executeUpdate();
            }
            System.out.println("updated " + updates + " rows!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void roomStatus(int roomNum, boolean status) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE room SET room_status = ? WHERE room_number = ?");
            ps.setBoolean(1, status);
            ps.setInt(2, roomNum);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Room not found.");
            }

            System.out.println("updated " + rows + " rows!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
        }
    }



    //Booking update method b2a wa kda


    


    // if a customer wants to extend their stay, they can use this method to update the check out date
    public void bookingCheckoutdate(Booking booking){
        BookingManager bm = new BookingManager();
        Room room = booking.getRoom();
        bm.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

        if (Db.select.IsRoomAvailable(room,booking,booking.getBooking_id())){
            System.out.println("Room " + booking.getRoom().getNum() + " is available. Proceeding with booking...");
            try (Connection conn = Db.connect()) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE booking SET check_out_date = ? WHERE booking_id = ?");
                ps.setDate(1, Date.valueOf(booking.getCheckOutDate()));
                ps.setInt(2, booking.getBooking_id());
    
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.err.println("Booking ID not found.");
                } else {
                    System.out.println("updated " + rows + " rows!");
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Room " + booking.getRoom().getNum() + " is not available for the requested dates.");
        }
        
    }


    public void booking(Booking booking ) {
        BookingManager bm = new BookingManager();
        Room room = booking.getRoom();
        bm.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());
        try (Connection conn = Db.connect()) {
           
            if (Db.select.IsRoomAvailable(room,booking)){
                System.out.println("Room " + booking.getRoom().getNum() + " is available. Proceeding with booking...");
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET room_number = ?, customer_id = ?, receptionist_id = ?, check_in_date = ?, check_out_date = ? WHERE booking_id = ?");
                ps.setInt(1, booking.getRoom().getNum());
                ps.setInt(2, booking.getCustomer_id());
                ps.setInt(3, booking.getReceptionist_id());
                ps.setDate(4, Date.valueOf(booking.getCheckInDate())); // Format: yyyy-MM-dd
                ps.setDate(5, Date.valueOf(booking.getCheckOutDate())); // Format: yyyy-MM-dd
                ps.setInt(6, booking.getBooking_id());
                Db.update.roomStatus(booking.getRoom().getNum(), true); // Update room status to booked

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.err.println("Booking ID not found.");
                } else {
                    System.out.println("updated " + rows + " rows!");
                }
            }else{
            System.out.println("Room " + booking.getRoom().getNum() + " is not available for the requested dates.");
        
    }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateCustomerBalance(int customerId, double newBalance) {
        if (newBalance < 0) {
            System.err.println("Balance cannot be negative.");
            return;
        }

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE customer SET customer_balance = ? WHERE customer_id = ?"
            );
            ps.setDouble(1, newBalance);
            ps.setInt(2, customerId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Customer ID not found: " + customerId);
            } else {
                System.out.println("Updated balance for customer ID: " + customerId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error during balance update: " + e.getMessage());
        }
    }



}
