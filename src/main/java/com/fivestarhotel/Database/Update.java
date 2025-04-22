package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fivestarhotel.BookingSystem.Booking; // Ensure this is the correct package for the Booking class
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

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


    public void booking(int bookingId, int roomNum, int customerId, int receptionistId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET room_number = ?, customer_id = ?, receptionist_id = ? WHERE booking_id = ?");
            ps.setInt(1, roomNum);
            ps.setInt(2, customerId);
            ps.setInt(3, receptionistId);
            ps.setInt(4, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void booking(int bookingId, int roomNum) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET room_number = ? WHERE booking_id = ?");
            ps.setInt(1, roomNum);
            ps.setInt(2, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void booking(int bookingId, int roomNum, int customerId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET room_number = ?, customer_id = ? WHERE booking_id = ?");
            ps.setInt(1, roomNum);
            ps.setInt(2, customerId);
            ps.setInt(3, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void booking(int bookingId, String checkInDate,String checkOutDate){
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET check_in_date = ?, check_out_date = ? WHERE booking_id = ?");
            ps.setString(1, checkInDate);
            ps.setString(2, checkOutDate);
            ps.setInt(3, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // if a customer wants to extend their stay, they can use this method to update the check out date
    public void booking(int bookingId,String checkOutDate){
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET check_out_date = ? WHERE booking_id = ?");
            ps.setString(1, checkOutDate);
            ps.setInt(2, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void booking(Booking booking ) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE booking SET room_number = ?, customer_id = ?, receptionist_id = ?, check_in_date = ?, check_out_date = ? WHERE booking_id = ?");
            ps.setInt(1, booking.getRoom().getNum());
            ps.setInt(2, booking.getCustomer_id());
            ps.setInt(3, booking.getReceptionist_id());
            ps.setDate(4, Date.valueOf(booking.getCheckInDate())); // Format: yyyy-MM-dd
            ps.setDate(5, Date.valueOf(booking.getCheckOutDate())); // Format: yyyy-MM-dd
            ps.setInt(6, booking.getBooking_id());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found.");
            } else {
                System.out.println("updated " + rows + " rows!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
