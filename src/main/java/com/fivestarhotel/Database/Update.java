package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fivestarhotel.*;
import com.fivestarhotel.Room.RoomType;

public class Update {
    private int updates;
    public void resetIncrement() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE room AUTO_INCREMENT = 1;");
            int rows = ps.executeUpdate();
            System.out.println("updated " + rows + " rows.");

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
    public void rooms(ArrayList<Room> newRooms){
        try (Connection conn = Db.connect()) {
            for (int i = 0; i < newRooms.size(); i++) {
                PreparedStatement ps = conn.prepareStatement("UPDATE room SET room_type = ?, room_status = ? WHERE room_number = ?");
                ps.setString(1, Room.convertRm(newRooms.get(i).getRoomType()));
                ps.setBoolean(2,newRooms.get(i).getStatus());
                ps.setInt(3, newRooms.get(i).getNum());
                updates+= ps.executeUpdate();
            }
            System.out.println("updated " + updates + " rows!");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
