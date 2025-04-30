package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
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

    // m4 booking awy bs ahoo

    public boolean IsRoomAvailable(Room room, Booking booking) {
        String sql = "SELECT COUNT(*) FROM booking WHERE room_number = ? AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = Db.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, room.getNum());
            ps.setDate(2, Date.valueOf(booking.getCheckOutDate()));
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int overlappingBookingsCount = rs.getInt(1);
                // If count is 0, no overlapping bookings exist, so the room is available.
                return overlappingBookingsCount == 0;
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while checking room availability: " + e.getMessage());
            return false;
        }
    }

    public boolean IsRoomAvailable(Room room, Booking booking, int excludeBookingId) {
        String sql = "SELECT COUNT(*) FROM booking " +
                "WHERE room_number = ? " +
                "AND check_in_date < ? " +
                "AND check_out_date > ? " +
                "AND booking_id <> ?";

        try (Connection conn = Db.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, room.getNum());
            ps.setDate(2, Date.valueOf(booking.getCheckOutDate()));
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ps.setInt(4, excludeBookingId);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Booking System wa kda b2a

    public Booking getbooking(int booking_id) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE booking_id = ?");
            ps.setInt(1, booking_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Booking " + booking_id + " Found.");

                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber); // Using your getRoom method

                if (room != null) {
                    return new Booking(rs.getInt("booking_id"), room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate());
                } else {
                    System.err
                            .println("Error: Room with number " + roomNumber + " not found for booking " + booking_id);
                    return null;
                }
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

    public Booking getBooking(Booking booking) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE room_number = ?");
            ps.setInt(1, booking.getRoom().getNum());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber); // Using your getRoom method
                return new Booking(rs.getInt("booking_id"), room, rs.getInt("customer_id"),
                        rs.getInt("receptionist_id"),
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
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber); // Fetch Room *inside* the loop

                if (room != null) {
                    bookings.add(new Booking(rs.getInt("booking_id"), room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate()));
                } else {
                    System.err.println("Warning: Room not found for booking ID " + rs.getInt("booking_id"));
                    // Handle missing room as needed
                }
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
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber); // Fetch Room *inside* the loop

                if (room != null) {
                    bookings.add(new Booking(rs.getInt("booking_id"), room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate()));
                } else {
                    System.err.println("Warning: Room not found for booking ID " + rs.getInt("booking_id"));
                    // Handle missing room as needed
                }
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
            int count = rs.getInt(1);
            System.out.println("Total number of bookings: " + count); // Added print statement
            return count;

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
                int lastId = result.getInt("booking_id");
                System.out.println("The ID of the last booking is: " + lastId);
                return lastId;

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
