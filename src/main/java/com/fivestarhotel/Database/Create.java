package com.fivestarhotel.Database;

import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
