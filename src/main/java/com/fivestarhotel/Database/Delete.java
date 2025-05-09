package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

public class Delete {
    public void all(String tableName) {
        if (!tableName.matches(Db.tables)) {
            System.err.println("Table entered is not in the SQL database");

        } else {
            try (Connection conn = Db.connect()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName);
                int rows = ps.executeUpdate();
                System.out.println("deleted " + rows + " rows.");

            } catch (SQLException e) {
                System.err.println("Connection error: Can't connect to server");
            }
        }
    }

    public void room(int roomNum) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM room WHERE room_number = ?");
            ps.setInt(1, roomNum);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Room number not found, didn't delete anything");
            } else {
                System.out.println("deleted " + rows + " rows.");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
        }
    }

    public void rate(RoomType type) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM rates WHERE room_type = ?");
            ps.setString(1, Room.convertRm(type));

            int rows = ps.executeUpdate();
            System.out.println("deleted " + rows + " rows.");

        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
        }

    }


    //Booking delete method wa kda b2a

    
    public void booking(int bookingId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM booking WHERE booking_id = ?");
            ps.setInt(1, bookingId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Booking ID not found, didn't delete anything");
            } else {
                System.out.println("deleted " + rows + " rows.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
        }
    }
}
