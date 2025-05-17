package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fivestarhotel.Billing;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;
import com.fivestarhotel.Database.Db.UserRoles;

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

    // 0 done
    // -1 room not found
    // -2 SQLError: make sure you remove all bookings before removing the room
    public int room(int roomNum) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM room WHERE room_number = ?");
            ps.setInt(1, roomNum);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Room number not found, didn't delete anything");
                return -1;
            } else {
                System.out.println("deleted " + rows + " rows.");
                return 0;

            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return -2;
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

    // Booking delete method wa kda b2a

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

    // billing delete method wa kda b2a

    // public void bill(int billId) {
    // try (Connection conn = Db.connect()) {
    // PreparedStatement ps = conn.prepareStatement("DELETE FROM billing WHERE
    // bill_id = ?");
    // ps.setInt(1, billId);
    // int rows = ps.executeUpdate();
    // if (rows == 0) {
    // System.err.println("Bill ID not found, didn't delete anything");
    // } else {
    // System.out.println("deleted " + rows + " rows.");
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // System.err.println(e.getErrorCode());
    // }
    // }

    public void bill(int booking_id) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM billing WHERE booking_id = ?");
            ps.setInt(1, booking_id);
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

    public void bill(Billing bill) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM billing WHERE bill_id = ?");
            ps.setInt(1, bill.getBillId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Bill ID not found, didn't delete anything");
            } else {
                System.out.println("deleted " + rows + " rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
        }
    }

    public void deleteUser(int userID, UserRoles userRole) {
        String tableName;

        switch (userRole) {
            case CUSTOMER -> tableName = "customer";
            case RECEPTIONIST -> tableName = "receptionist";
            case ADMIN -> tableName = "admin";
            default -> throw new IllegalArgumentException("Unknown user role: " + userRole);
        }

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("DELETE FROM " + tableName + " WHERE " + tableName + "_id = ?");
            ps.setInt(1, userID);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("User deleted successfully from " + tableName + " table.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
