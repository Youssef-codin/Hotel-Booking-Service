package com.fivestarhotel.Database;

import java.sql.*;

import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.BookingSystem.BookingManager;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;
import com.fivestarhotel.users.Customer;

import static com.fivestarhotel.security.Crypto.makeSalt;
import static com.fivestarhotel.security.Crypto.stringToHash;
import com.fivestarhotel.users.User;
import com.fivestarhotel.users.Receptionist;


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
        BookingManager bm = new BookingManager();
        Room room = booking.getRoom();
        bm.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

        if (Db.select.IsRoomAvailable(room,booking)) {
            // Throw custom exception if room is not available
            System.out.println("Room " + booking.getRoom().getNum() + " is available. Proceeding with booking...");
            
            try(Connection conn = Db.connect()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO booking(booking_id, room_number, customer_id, receptionist_id, check_in_date, check_out_date) " +
                        "VALUES (?, ?, ?, ?, ?,?)");
    
                ps.setInt(1, booking.getBooking_id());
                ps.setInt(2, booking.getRoom().getNum());
                ps.setInt(3, booking.getCustomer_id());
                ps.setInt(4, booking.getReceptionist_id());
                ps.setDate(5, Date.valueOf(booking.getCheckInDate()));
                ps.setDate(6, Date.valueOf(booking.getCheckOutDate()));
                int bookingRows = ps.executeUpdate();
                System.out.println("Added " + bookingRows + " booking row(s).");
                Db.update.roomStatus(booking.getRoom().getNum(), true); // Update room status to booked
                
            } catch (SQLException e) {
                System.err.println("Booking failed due to a SQL error. Rolling back transaction.");
                e.printStackTrace();
                
            }
        }
        else{
            System.out.println("Room " + booking.getRoom().getNum() + " is not available for the requested dates.");
        
                }

    }
    public static User signUpUser(User user) {
        try (Connection conn = Db.connect()) {
            String salt = makeSalt();
            String hashedPassword = stringToHash(user.getPassword(), salt);

            if (user instanceof Customer customer) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customer (customer_fname, customer_lname, customer_email, customer_password, customer_salt, customer_phone, customer_address, customer_balance) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, customer.getFname());
                ps.setString(2, customer.getLname());
                ps.setString(3, customer.getEmail());
                ps.setString(4, hashedPassword);
                ps.setString(5, salt);
                ps.setString(6, customer.getPhone());
                ps.setString(7, customer.getAddress());
                ps.setDouble(8, 0.0);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Customer(id, customer.getFname(), customer.getLname(), customer.getEmail(),
                            hashedPassword, customer.getPhone(), customer.getAddress(), 0);
                }

            } else if (user instanceof Receptionist receptionist) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO receptionist (receptionist_fname, receptionist_lname, receptionist_email, receptionist_password, receptionist_salt) " +
                                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, receptionist.getFname());
                ps.setString(2, receptionist.getLname());
                ps.setString(3, receptionist.getEmail());
                ps.setString(4, hashedPassword);
                ps.setString(5, salt);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Receptionist(id, receptionist.getFname(), receptionist.getLname(),
                            receptionist.getEmail(), hashedPassword);
                }
            }

            System.err.println("Unsupported user type or insertion failed.");
            return null;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Email already exists: " + user.getEmail());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}





    

