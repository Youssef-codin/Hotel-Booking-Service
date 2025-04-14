package com.fivestarhotel.Database;

import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Select {
    public Room getRoom(int roomNumber) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room WHERE room_number = ?");
            ps.setInt(1, roomNumber);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                System.out.println("Room " + roomNumber + " Found.");
                return new Room(result.getInt("room_number"),
                        Room.convertStr(result.getString("room_type")));
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

    public ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM room");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                rooms.add(new Room(result.getInt("room_number"), Room.convertStr(result.getString("room_type"))));
            }
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return null;
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
}
