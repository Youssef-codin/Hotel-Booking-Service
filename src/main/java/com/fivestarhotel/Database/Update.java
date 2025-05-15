package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fivestarhotel.Billing;
import com.fivestarhotel.BookingSystem.Booking;
import com.fivestarhotel.BookingSystem.BookingManager;
import com.fivestarhotel.Database.Db.UserRoles;
import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType; // Ensure this is the correct package for the Booking class

public class Update {

    public void resetIncrementRooms() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE room AUTO_INCREMENT = 1;");
            ps.executeUpdate();
            System.out.println("reset booking auto-increment");

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");

        }
    }

    public void resetIncrementUsers(UserRoles role) {
        String table = role.toString().toLowerCase();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE " + table + " AUTO_INCREMENT = 1;");
            ps.executeUpdate();
            System.out.println("reset users auto-increment");

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");

        }
    }

    public void resetIncrementBooking() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE booking AUTO_INCREMENT = 1;");
            ps.executeUpdate();
            System.out.println("reset booking auto-increment");

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

    // Booking update method b2a wa kda

    // if a customer wants to extend their stay, they can use this method to update
    // the check out date
    public void bookingCheckoutdate(Booking booking) {
        BookingManager bm = new BookingManager();
        Room room = booking.getRoom();
        bm.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

        if (Db.select.IsRoomAvailable(room, booking, booking.getBooking_id())) {
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
        } else {
            System.out.println("Room " + booking.getRoom().getNum() + " is not available for the requested dates.");
        }

    }

    public void booking(Booking booking) {
        BookingManager bm = new BookingManager();
        bm.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());
        try (Connection conn = Db.connect()) {

            if (Db.select.IsRoomAvailable(booking)) {
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

                // Handle billing
                Billing existingBill = Db.select.getBillBooking(booking.getBooking_id());
                if (existingBill != null) {
                    // Update existing bill
                    if (existingBill.getStatus() == Billing.BillingStatus.PAID) {
                        existingBill.setStatus(Billing.BillingStatus.PENDING);
                        updateBillStatus(existingBill);
                    }

                } else {
                    // Create new bill
                    Billing newBill = new Billing(booking.getBooking_id(), Billing.BillingStatus.PENDING);
                    Db.create.addBill(newBill);
                    System.out.println("Created new bill with amount");
                }

            } else {
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
                    "UPDATE customer SET customer_balance = ? WHERE customer_id = ?");
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

    // billing

    public void updateBillStatus(int billId, Billing.BillingStatus status) {
        if (status != Billing.BillingStatus.PAID && status != Billing.BillingStatus.PENDING) {
            System.err.println("Invalid status: " + status);
            return;
        }
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE billing SET billing_status = ? WHERE bill_id = ?");
            ps.setBoolean(1, Billing.convertBill(status));
            ps.setInt(2, billId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Bill not found for ID: " + billId);
            } else {
                System.out.println("Updated bill status for ID: " + billId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public void updateBillStatus(Billing billing) {
        if (billing.getStatus() != Billing.BillingStatus.PAID && billing.getStatus() != Billing.BillingStatus.PENDING) {
            System.err.println("Invalid status: " + billing.getStatus());
            return;
        }
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE billing SET billing_status = ? WHERE bill_id = ?");
            ps.setBoolean(1, Billing.convertBill(billing.getStatus()));
            ps.setInt(2, billing.getBillId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.err.println("Bill not found for ID: " + billing.getBillId());
            } else {
                System.out.println("Updated bill status for ID: " + billing.getBillId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public void updateBill(Billing billing) {
        updateBillStatus(billing);
    }
}
