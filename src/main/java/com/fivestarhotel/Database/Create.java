package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;


public class Create {

    public boolean addRoom(RoomType roomType) {
        try (Connection conn = Db.connect()) {
            int lastRoomNum = Db.select.lastRoomNum();
            int updates = 0;

            if (lastRoomNum >= 1) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO room(room_floor, room_type, room_status) Value (?,?,?)");
                ps.setInt(1, (lastRoomNum / 100) + 1);
                ps.setString(2, Room.convertRm(roomType));
                ps.setBoolean(3, false);
                updates = ps.executeUpdate();

            } else if (lastRoomNum == 0) {
                Db.update.resetIncrement();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO room(room_number, room_floor, room_type, room_status) Values(?,?,?,?)");
                ps.setInt(1, 1);
                ps.setInt(2, (lastRoomNum / 100) + 1);
                ps.setString(3, Room.convertRm(roomType));
                ps.setBoolean(4, false);
                updates = ps.executeUpdate();

            }
            System.out.println("Added " + updates + " rows");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return false;
        }
    }

    public void addRoom(int roomNumber, RoomType roomType) {

        int updates = 0;
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO room(room_number, room_floor, room_type, room_status) Values(?,?,?,?)");
            ps.setInt(1, roomNumber);
            ps.setInt(2, (roomNumber / 100) + 1);
            ps.setString(3, Room.convertRm(roomType));
            ps.setBoolean(4, false);
            updates = ps.executeUpdate();
            System.out.println("Inserted " + updates + " rows");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Duplicate primary key detected: use override or empty index.");
            }
        }
    }

    public void overrideRoom(int roomNumber, RoomType roomType) {

        int updates = 0;
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM room WHERE room_number = ?");
            ps.setInt(1, roomNumber);
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "INSERT INTO room(room_number, room_floor, room_type, room_status) Values(?,?,?,?)");
            ps.setInt(1, roomNumber);
            ps.setInt(2, (roomNumber / 100) + 1);
            ps.setString(3, Room.convertRm(roomType));
            ps.setBoolean(4, false);
            updates = ps.executeUpdate();
            System.out.println("overrode " + updates + " rows");

        } catch (SQLException e) {
            System.err.println("SQL: Error");
        }
    }

    public boolean addRooms(RoomType roomType, int amount) {

        int updates = 0;
        try (Connection conn = Db.connect()) {
            for (int i = 0; i < amount; i++) {
                int lastRoomNum = Db.select.lastRoomNum();
                if (lastRoomNum >= 1) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO room(room_floor, room_type, room_status) Values(?,?,?)");
                    ps.setInt(1, (lastRoomNum / 100) + 1);
                    ps.setString(2, Room.convertRm(roomType));
                    ps.setBoolean(3, false);
                    updates += ps.executeUpdate();

                } else if (lastRoomNum == 0) {
                    Db.update.resetIncrement();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO room(room_number, room_floor, room_type, room_status) Values(?,?,?,?)");
                    ps.setInt(1, 1);
                    ps.setInt(2, (lastRoomNum / 100) + 1);
                    ps.setString(3, Room.convertRm(roomType));
                    ps.setBoolean(4, false);
                    updates += ps.executeUpdate();

                }
            }
            System.out.println("Added " + updates + " rows");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return false;
        }
    }

    public boolean addBookedRooms(RoomType roomType, int amount) {

        int updates = 0;
        try (Connection conn = Db.connect()) {
            for (int i = 0; i < amount; i++) {
                int lastRoomNum = Db.select.lastRoomNum();
                if (lastRoomNum >= 1) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO room(room_floor, room_type, room_status) Values(?,?,?)");
                    ps.setInt(1, (lastRoomNum / 100) + 1);
                    ps.setString(2, Room.convertRm(roomType));
                    ps.setBoolean(3, true);
                    updates += ps.executeUpdate();

                } else if (lastRoomNum == 0) {
                    Db.update.resetIncrement();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO room(room_number, room_floor, room_type, room_status) Values(?,?,?,?)");
                    ps.setInt(1, 1);
                    ps.setInt(2, (lastRoomNum / 100) + 1);
                    ps.setString(3, Room.convertRm(roomType));
                    ps.setBoolean(4, true);
                    updates += ps.executeUpdate();

                }
            }
            System.out.println("Added " + updates + " rows");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return false;
        }
    }

    public void addRate(RoomType type, int newRate) {

        String room_type = Room.convertRm(type);

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rates(room_type, room_rate) values (?, ?)");
            ps.setString(1, room_type);
            ps.setInt(2, newRate);

            int rows = ps.executeUpdate();
            System.out.println("added " + rows + " rows.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Room type already has a rate, try updating instead");

            } else {
                e.printStackTrace();
            }
        }
    }

    // Booking stuff wa kda b2a 


    public void addBooking(Booking booking) {
        Connection conn = null;
        try {
            conn = Db.connect();
            conn.setAutoCommit(false);
    
            int availabilityStatus = Db.select.checkBooking(booking.getRoom());
    
            if (availabilityStatus == 0) { // Room is available
                Db.update.roomStatus(booking.getRoom().getNum(), true); // Update room status to booked
    
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO booking(booking_id, room_number, customer_id, receptionist_id, check_in_date, check_out_date) " +
                        "VALUES (?, ?, ?, ?, ?,?)"
                );
                ps.setInt(1, booking.getBooking_id());
                ps.setInt(2, booking.getRoom().getNum());
                ps.setInt(3, booking.getCustomer_id());
                ps.setInt(4, booking.getReceptionist_id());
                ps.setDate(5, Date.valueOf(booking.getCheckInDate()));
                ps.setDate(6, Date.valueOf(booking.getCheckOutDate()));
                int bookingRows = ps.executeUpdate();
                System.out.println("Added " + bookingRows + " booking row(s).");
                conn.commit();
            } else if (availabilityStatus == -1) {
                System.err.println("Error: Cannot book. Room number " + booking.getRoom().getNum() + " does not exist.");
                // You might want to throw an exception or handle this differently
            } else if (availabilityStatus == -2) {
                System.err.println("Error: Cannot book. Room " + booking.getRoom().getNum() + " is already booked.");
                // You might want to throw an exception or handle this differently
            } else { // availabilityStatus == -3 (Database error)
                System.err.println("Error: Could not check room availability due to a database error. Booking failed.");
                // You might want to log this error more extensively
            }
    
        } catch (SQLException e) {
            System.err.println("Booking failed due to a SQL error. Rolling back transaction.");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }




}
    

