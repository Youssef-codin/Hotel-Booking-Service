package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fivestarhotel.*;
import com.fivestarhotel.Room.RoomType;

public class Update {
    public void resetIncrement() {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("ALTER TABLE room AUTO_INCREMENT = 1;");
            int columns = ps.executeUpdate();
            System.out.println("updated " + columns + " columns.");

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");

        }
    }

    public void updateRates(RoomType type, int newRate) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE rates SET room_rate = ? WHERE room_type = ?");
            ps.setInt(1, newRate);
            ps.setString(2, Room.convertRm(type));

            int columns = ps.executeUpdate(); // gets the num of columns updated
            System.out.println("updated " + columns + " columns.");

        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();

        }
    }
}
