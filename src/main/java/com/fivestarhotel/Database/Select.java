package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

public class Select {
    public Room getRoom(int roomNumber) {

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room WHERE room_number = ?");
            ps.setInt(1, roomNumber);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                System.out.println("Room " + roomNumber + " Found.");
                return new Room(result.getInt("room_number"),
                        Room.convertStr(result.getString("room_type")), result.getBoolean("room_status"));

            } else {
                System.err.println("Query-Error: Room Not Found");
                return null;

            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;

        }
    }

    public ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"), Room.convertStr(result.getString("room_type")),
                        result.getBoolean("room_status")));
            }

            return rooms;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Room> getRooms(RoomType type) {
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room WHERE room_type = ?");
            ps.setString(1, Room.convertRm(type));
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"), Room.convertStr(result.getString("room_type")),
                        result.getBoolean("room_status")));
            }

            return rooms;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Room> getRooms(boolean status) {

        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room WHERE room_status = ?");
            ps.setBoolean(1, status);

            ResultSet result = ps.executeQuery();
            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"), Room.convertStr(result.getString("room_type")),
                        result.getBoolean("room_status")));
            }

            return rooms;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Room> getRooms(RoomType type, boolean status) {

        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM room WHERE room_status =  ? AND room_type = ?");
            ps.setBoolean(1, status);
            ps.setString(2, Room.convertRm(type));
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"), Room.convertStr(result.getString("room_type")),
                        result.getBoolean("room_status")));
            }

            return rooms;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Room> getRooms(int from, int to) {
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM room WHERE room_number BETWEEN ? AND ?");
            ps.setInt(1, from);
            ps.setInt(2, to);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"),
                        Room.convertStr(result.getString("room_type"))));
            }
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public int countRooms() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM room");

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return -1;

        }
    }

    public int countRooms(RoomType type) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM room WHERE room_type = ?");
            ps.setString(1, Room.convertRm(type));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return -1;

        }
    }

    public int lastRoomNum() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * from room ORDER BY room_number DESC LIMIT 1");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("room_number");
            } else {
                System.err.println("Last room not found.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return 0;
        }
    }

    public int getRate(RoomType type) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM rates WHERE room_type = ?");
            ps.setString(1, Room.convertRm(type));

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.err.println("Room type doesn't have a set rate");
                return -1;

            } else {
                return rs.getInt("room_rate");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return -1;
        }
    }

    public void loadRates() {

        for (RoomType type : RoomType.values()) {
            int rate = Db.select.getRate(type);
            if (rate == -1) {
                System.err.println("Rate not found for type: " + type);

            } else {
                Room.setRate(type, rate);

            }
        }
    }


    //Booking System wa kda b2a



    public Booking getbooking(int booking_id) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE booking_id = ?");
            ps.setInt(1, booking_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Booking " + booking_id + " Found.");
                return new Booking(rs.getInt("booking_id"), rs.getInt("room_number"),
                        rs.getInt("customer_id"), rs.getInt("receptionist_id"),
                        rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate());
            } else {
                System.err.println("Query-Error: Booking Not Found");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;

        }
    }

    public Booking getBooking(int roomNumber) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE room_number = ?");
            ps.setInt(1, roomNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Booking(rs.getInt("booking_id"), rs.getInt("room_number"),
                        rs.getInt("customer_id"), rs.getInt("receptionist_id"),
                        rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate());
            } else {
                System.err.println("Query-Error: Booking Not Found");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;

        }
    }

    public ArrayList<Booking> getBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                bookings.add(new Booking(result.getInt("booking_id"), result.getInt("room_number"),
                        result.getInt("customer_id"), result.getInt("receptionist_id"),
                        result.getDate("check_in_date").toLocalDate(), result.getDate("check_out_date").toLocalDate()));
            }
            return bookings;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Booking> getBookings(int from, int to) {
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE booking_id BETWEEN ? AND ?");
            ps.setInt(1, from);
            ps.setInt(2, to);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                bookings.add(new Booking(result.getInt("booking_id"), result.getInt("room_number"),
                        result.getInt("customer_id"), result.getInt("receptionist_id"),
                        result.getDate("check_in_date").toLocalDate(), result.getDate("check_out_date").toLocalDate()));
            }
            return bookings;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }
    


    public int countBookings() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM booking");

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return -1;

        }
    }
    public int lastBookingId() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * from booking ORDER BY booking_id DESC LIMIT 1");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("booking_id");
            } else {
                System.err.println("Last booking not found.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return 0;
        }
    }
   


        



    
}
