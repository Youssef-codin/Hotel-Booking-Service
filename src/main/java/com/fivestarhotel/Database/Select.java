package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import com.fivestarhotel.Billing;
import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.Database.Db.UserRoles;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;
import static com.fivestarhotel.security.Crypto.stringToHash;
import com.fivestarhotel.users.Admin;
import com.fivestarhotel.users.Customer;
import com.fivestarhotel.users.Receptionist;
import com.fivestarhotel.users.User;

public class Select {

    public Room getRoom(int roomNumber) {

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room WHERE room_number = ?");
            ps.setInt(1, roomNumber);

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                System.out.println("Room " + roomNumber + " Found.");
                return new Room(result.getInt("room_number"),
                        Room.convertStr(result.getString("room_type")), result.getBoolean("room_status"),
                        result.getBoolean("room_checkedin"));

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
                        result.getBoolean("room_status"), result.getBoolean("room_checkedin")));
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
                        result.getBoolean("room_status"), result.getBoolean("room_checkedin")));
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
                        result.getBoolean("room_status"), result.getBoolean("room_checkedin")));
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
                        result.getBoolean("room_status"), result.getBoolean("room_checkedin")));
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
                        Room.convertStr(result.getString("room_type")), result.getBoolean("room_status"),
                        result.getBoolean("room_checkedin")));
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

    public int lastUserNum(UserRoles role) {
        String table = role.toString().toLowerCase();

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * from " + table + " ORDER BY " + table + "_id DESC LIMIT 1");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt(table + "_id");
            } else {
                System.err.println("Last id not found.");
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
                switch (type) {
                    case SINGLE -> {
                        return 750;
                    }
                    case DOUBLE -> {
                        return 1200;
                    }
                    case SUITE -> {
                        return 2000;
                    }
                    default -> {
                        return 0; // this case should never happen, but if it happens the app will probably
                                  // crash
                    }
                }

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

            }
            Room.setRate(type, rate);

        }
    }

    // m4 booking awy bs ahoo

    public boolean IsRoomAvailable(Booking booking) {
        String sql = "SELECT COUNT(*) FROM booking WHERE room_number = ? AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = Db.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, booking.getRoom().getNum());
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
                "AND check_in_date < ? " + // Existing booking starts before new checkout
                "AND check_out_date > ? " + // Existing booking ends after new checkin
                "AND booking_id <> ?"; // Exclude the current booking being updated

        try (Connection conn = Db.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, room.getNum());
            ps.setDate(2, Date.valueOf(booking.getCheckOutDate()));
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));

            ps.setInt(4, excludeBookingId); // Exclude this booking ID

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0; // True if no overlaps (other than current booking)

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Booking System wa kda b2a

    public Booking getBooking(int booking_id) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE booking_id = ?");
            ps.setInt(1, booking_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber);

                if (room != null) {
                    return new Booking(rs.getInt("booking_id"), room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getBoolean("booking_checkedin"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate());
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

    public Booking getBooking(int room_number, LocalDate check_in_date) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM booking WHERE room_number = ? AND check_in_date = ?");
            ps.setInt(1, room_number);
            ps.setDate(2, Date.valueOf(check_in_date));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room room = getRoom(room_number);
                return new Booking(rs.getInt("booking_id"),
                        room, rs.getInt("customer_id"),
                        rs.getInt("receptionist_id"),
                        rs.getBoolean("booking_checkedin"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate());
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

    public ArrayList<Booking> getBookingByName(String name) {
        ArrayList<Booking> bookings = new ArrayList<>();

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from booking where customer_id IN (select customer_id from customer where customer_fname like ? or customer_lname like ?)");
            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber);

                bookings.add(new Booking(rs.getInt("booking_id"),
                        room, rs.getInt("customer_id"),
                        rs.getInt("receptionist_id"),
                        rs.getBoolean("booking_checkedin"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate()));
            }

            return bookings;

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
                Room room = getRoom(roomNumber);

                if (room != null) {
                    bookings.add(new Booking(rs.getInt("booking_id"),
                            room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getBoolean("booking_checkedin"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate()));
                } else {
                    System.err.println("Warning: Room not found for booking ID " + rs.getInt("booking_id"));
                }
            }
            return bookings;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
        }
    }

    public ArrayList<Booking> getBookings(int roomNum) {
        ArrayList<Booking> bookings = new ArrayList<>();

        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM booking WHERE room_number = ?");
            ps.setInt(1, roomNum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                Room room = getRoom(roomNumber);

                if (room != null) {
                    bookings.add(new Booking(rs.getInt("booking_id"),
                            room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getBoolean("booking_checkedin"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate()));
                } else {
                    System.err.println("Warning: Room not found for booking ID " + rs.getInt("booking_id"));
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
                    bookings.add(new Booking(rs.getInt("booking_id"),
                            room, rs.getInt("customer_id"),
                            rs.getInt("receptionist_id"),
                            rs.getBoolean("booking_checkedin"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate()));
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

    public User signInUser(String email, String password) {
        try (Connection conn = Db.connect()) {

            PreparedStatement customerPs = conn.prepareStatement(
                    "SELECT * FROM customer WHERE customer_email = ?");
            customerPs.setString(1, email);
            ResultSet customerRs = customerPs.executeQuery();

            if (customerRs.next()) {
                String salt = customerRs.getString("customer_salt");
                String storedPassword = customerRs.getString("customer_password");

                if (storedPassword.equals(stringToHash(password, salt))) {
                    return new Customer(
                            customerRs.getInt("customer_id"),
                            customerRs.getString("customer_fname"),
                            customerRs.getString("customer_lname"),
                            customerRs.getString("customer_email"),
                            storedPassword,
                            customerRs.getString("customer_phone"),
                            customerRs.getString("customer_address"),
                            customerRs.getDouble("customer_balance"));
                } else {
                    System.err.println("Incorrect password for customer: " + email);
                    return null;
                }
            }

            PreparedStatement adminPs = conn.prepareStatement(
                    "SELECT * FROM admin WHERE admin_email = ?");
            adminPs.setString(1, email);
            ResultSet adminRs = adminPs.executeQuery();

            if (adminRs.next()) {
                String salt = adminRs.getString("admin_salt");
                String storedPassword = adminRs.getString("admin_password");

                if (storedPassword.equals(stringToHash(password, salt))) {
                    return new Admin(
                            adminRs.getInt("admin_id"),
                            adminRs.getString("admin_fname"),
                            adminRs.getString("admin_lname"),
                            adminRs.getString("admin_email"),
                            storedPassword);
                } else {
                    System.err.println("Incorrect password for admin: " + email);
                    return null;
                }
            }

            PreparedStatement recepPs = conn.prepareStatement(
                    "SELECT * FROM receptionist WHERE receptionist_email = ?");
            recepPs.setString(1, email);
            ResultSet recepRs = recepPs.executeQuery();

            if (recepRs.next()) {
                String salt = recepRs.getString("receptionist_salt");
                String storedPassword = recepRs.getString("receptionist_password");

                if (storedPassword.equals(stringToHash(password, salt))) {
                    return new Receptionist(
                            recepRs.getInt("receptionist_id"),
                            recepRs.getString("receptionist_fname"),
                            recepRs.getString("receptionist_lname"),
                            recepRs.getString("receptionist_email"),
                            storedPassword);
                } else {
                    System.err.println("Incorrect password for receptionist: " + email);
                    return null;
                }
            }

            System.err.println("User not found: " + email);
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error during sign-in: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during password hashing: " + e.getMessage());
            return null;
        }

    }

    // Billing wa kda

    public Billing getBill(int billId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing WHERE billing_id = ?");
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Billing(
                        rs.getInt("billing_id"),
                        rs.getInt("booking_id"),
                        Billing.convertStr(rs.getString("billing_status")));
            } else {
                System.err.println("Bill not found for ID: " + billId);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public int lastBillId() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * from billing ORDER BY billing_id DESC LIMIT 1");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("billing_id");
            } else {
                System.err.println("Last bill not found.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return 0;
        }
    }

    public Billing getBillCustomer(int customerId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing WHERE customer_id = ?");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Billing(
                        rs.getInt("billing_id"),
                        rs.getInt("booking_id"),
                        Billing.convertStr(rs.getString("billing_status")));
            } else {
                System.err.println("Bill not found for customer ID: " + customerId);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public Billing getBillBooking(int bookingId) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing WHERE booking_id = ?");
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Billing(
                        rs.getInt("billing_id"),
                        rs.getInt("booking_id"),
                        Billing.convertStr(rs.getString("billing_status")));
            } else {
                System.err.println("Bill not found for booking ID: " + bookingId);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Billing> getBillsByCustomer(int customerId) {
        ArrayList<Billing> bills = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing WHERE customer_id = ?");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bills.add(new Billing(
                        rs.getInt("billing_id"),
                        rs.getInt("booking_id"),
                        Billing.convertStr(rs.getString("billing_status"))));
            }
            return bills;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Billing> getAllBills() {
        ArrayList<Billing> bills = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bills.add(new Billing(
                        rs.getInt("billing_id"),
                        rs.getInt("booking_id"),
                        Billing.convertStr(rs.getString("billing_status"))));
            }
            return bills;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public User getUserById(UserRoles role, int userId) {
        String sql = switch (role) {
            case ADMIN -> "SELECT * FROM admin WHERE admin_id = ?";
            case RECEPTIONIST -> "SELECT * FROM receptionist WHERE receptionist_id = ?";
            case CUSTOMER -> "SELECT * FROM customer WHERE customer_id = ?";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };

        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (role.equals(UserRoles.ADMIN)) {
                    return new Admin(rs.getInt("admin_id"), rs.getString("admin_fname"),
                            rs.getString("admin_lname"), rs.getString("admin_email"),
                            rs.getString("admin_password"));
                } else if (role.equals(UserRoles.RECEPTIONIST)) {
                    return new Receptionist(rs.getInt("receptionist_id"), rs.getString("receptionist_fname"),
                            rs.getString("receptionist_lname"), rs.getString("receptionist_email"),
                            rs.getString("receptionist_password"));
                } else if (role.equals(UserRoles.CUSTOMER)) {
                    return new Customer(rs.getInt("customer_id"), rs.getString("customer_fname"),
                            rs.getString("customer_lname"), rs.getString("customer_email"),
                            rs.getString("customer_password"), rs.getString("customer_phone"),
                            rs.getString("customer_address"), rs.getDouble("customer_balance"));
                }
            }

            System.err.println(role + " with ID " + userId + " not found.");
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<User> getUsersByName(UserRoles role, String name) {
        ArrayList<User> users = new ArrayList<>();
        String sql = switch (role) {
            case ADMIN -> "SELECT * FROM admin WHERE admin_fname LIKE ? OR admin_lname LIKE ?";
            case RECEPTIONIST ->
                "SELECT * FROM receptionist WHERE receptionist_fname LIKE ? OR receptionist_lname LIKE ?";
            case CUSTOMER -> "SELECT * FROM customer WHERE customer_fname LIKE ? OR customer_lname LIKE ?";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };

        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (role.equals(UserRoles.ADMIN)) {
                    users.add(new Admin(rs.getInt("admin_id"), rs.getString("admin_fname"),
                            rs.getString("admin_lname"), rs.getString("admin_email"),
                            rs.getString("admin_password")));
                } else if (role.equals(UserRoles.RECEPTIONIST)) {
                    users.add(new Receptionist(rs.getInt("receptionist_id"), rs.getString("receptionist_fname"),
                            rs.getString("receptionist_lname"), rs.getString("receptionist_email"),
                            rs.getString("receptionist_password")));
                } else if (role.equals(UserRoles.CUSTOMER)) {
                    users.add(new Customer(rs.getInt("customer_id"), rs.getString("customer_fname"),
                            rs.getString("customer_lname"), rs.getString("customer_email"),
                            rs.getString("customer_password"), rs.getString("customer_phone"),
                            rs.getString("customer_address"), rs.getDouble("customer_balance")));
                }
            }

            return users;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getRoomsByCustomer(int customerId) {
        ArrayList<String> rooms = new ArrayList<>();
        String sql = "SELECT b.room_number, c.customer_fname, c.customer_lname " +
                "FROM booking b " +
                "JOIN customer c ON b.customer_id = c.customer_id " +
                "WHERE b.customer_id = ?";

        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                String customerName = rs.getString("customer_fname") + " " + rs.getString("customer_lname");
                rooms.add("Room Number: " + roomNumber + " (Customer: " + customerName + ")");
            }

            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public User getUserByEmail(UserRoles role, String email) {
        User user = null;

        System.out.println(role.toString());
        String sql = switch (role) {
            case ADMIN -> "SELECT * FROM admin WHERE admin_email = ?";
            case RECEPTIONIST -> "SELECT * FROM receptionist WHERE receptionist_email = ?";
            case CUSTOMER -> "SELECT * FROM customer WHERE customer_email = ?";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };

        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (role.equals(UserRoles.ADMIN)) {
                    user = new Admin(rs.getInt("admin_id"), rs.getString("admin_fname"),
                            rs.getString("admin_lname"), rs.getString("admin_email"),
                            rs.getString("admin_password"));
                } else if (role.equals(UserRoles.RECEPTIONIST)) {
                    user = new Receptionist(rs.getInt("receptionist_id"), rs.getString("receptionist_fname"),
                            rs.getString("receptionist_lname"), rs.getString("receptionist_email"),
                            rs.getString("receptionist_password"));
                } else if (role.equals(UserRoles.CUSTOMER)) {
                    user = new Customer(rs.getInt("customer_id"), rs.getString("customer_fname"),
                            rs.getString("customer_lname"), rs.getString("customer_email"),
                            rs.getString("customer_password"), rs.getString("customer_phone"),
                            rs.getString("customer_address"), rs.getDouble("customer_balance"));
                }
            }

            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<User> getAllUsers(UserRoles role) {
        ArrayList<User> users = new ArrayList<>();
        String sql = switch (role) {
            case ADMIN -> "SELECT * FROM admin";
            case RECEPTIONIST -> "SELECT * FROM receptionist";
            case CUSTOMER -> "SELECT * FROM customer";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };

        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (role.equals(UserRoles.ADMIN)) {
                    users.add(new Admin(rs.getInt("admin_id"), rs.getString("admin_fname"),
                            rs.getString("admin_lname"), rs.getString("admin_email"),
                            rs.getString("admin_password")));
                } else if (role.equals(UserRoles.RECEPTIONIST)) {
                    users.add(new Receptionist(rs.getInt("receptionist_id"), rs.getString("receptionist_fname"),
                            rs.getString("receptionist_lname"), rs.getString("receptionist_email"),
                            rs.getString("receptionist_password")));
                } else if (role.equals(UserRoles.CUSTOMER)) {
                    users.add(new Customer(rs.getInt("customer_id"), rs.getString("customer_fname"),
                            rs.getString("customer_lname"), rs.getString("customer_email"),
                            rs.getString("customer_password"), rs.getString("customer_phone"),
                            rs.getString("customer_address"), rs.getDouble("customer_balance")));
                }
            }

            return users;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Customer getCustomerByRoom(int roomNumber) {
        String sql = "SELECT c.* FROM customer c " +
                "JOIN booking b ON c.customer_id = b.customer_id " +
                "WHERE b.room_number = ?";
        try (Connection conn = Db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getInt("customer_id"), rs.getString("customer_fname"),
                        rs.getString("customer_lname"), rs.getString("customer_email"),
                        rs.getString("customer_password"), rs.getString("customer_phone"),
                        rs.getString("customer_address"), rs.getDouble("customer_balance"));
            } else {
                System.err.println("No customer found for room number " + roomNumber);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Check if a room exists in the database

    public boolean doesRoomExist(int roomNum) {
        String sql = "SELECT COUNT(*) FROM room WHERE room_number = ?";
        try (Connection conn = Db.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomNum);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if room exists
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while checking room existence: " + e.getMessage());
            return false;
        }
    }

    public boolean doesRoomExist(Room room) {
        return doesRoomExist(room.getNum());
    }

}
