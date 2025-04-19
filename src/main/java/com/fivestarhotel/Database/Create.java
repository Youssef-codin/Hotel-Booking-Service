package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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


    public void addBooking(int bookingId, int roomNumber, int customer_id,int receptionist_id, String checkInDate, String checkOutDate) {
    Connection conn = null;
    try {
        conn = Db.connect();
        conn.setAutoCommit(false); 

        PreparedStatement check = conn.prepareStatement(
            "SELECT room_status FROM room WHERE room_number = ?"
        );
        check.setInt(1, roomNumber);
        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            System.out.println("Room number " + roomNumber + " does not exist.");
            return;
        }

        boolean isBooked = rs.getBoolean("room_status");
        if (isBooked) {
            System.out.println("Room " + roomNumber + " is already booked.");
            return;
        }

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO booking(booking_id, room_number, customer_id, receptionist_id, check_in_date, check_out_date) " +
            "VALUES (?, ?, ?, ?, ?,?)"
        );
        ps.setInt(1, bookingId);
        ps.setInt(2, roomNumber);
        ps.setInt(3, customer_id);
        ps.setInt(4, receptionist_id);
        ps.setDate(5, Date.valueOf(checkInDate)); // Format: yyyy-MM-dd
        ps.setDate(6, Date.valueOf(checkOutDate)); // Format: yyyy-MM-dd
        int bookingRows = ps.executeUpdate();
        System.out.println("Added " + bookingRows + " booking row(s).");

        PreparedStatement ps2 = conn.prepareStatement(
            "UPDATE room SET room_status = ? WHERE room_number = ?"
        );
        ps2.setBoolean(1, true);
        ps2.setInt(2, roomNumber);
        int roomRows = ps2.executeUpdate();
        System.out.println("Updated " + roomRows + " room row(s).");

        conn.commit(); // Commit transaction
        System.out.println("Booking completed successfully.");

    } catch (SQLException e) {
        System.err.println("Booking failed. Rolling back transaction.");
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
