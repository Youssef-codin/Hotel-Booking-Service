package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fivestarhotel.Room;
import com.fivestarhotel.Room.RoomType;

public class Delete {
    public void all(String tableName) {
        if (!tableName.matches(Db.tables)) {
            System.err.println("Table entered is not in the SQL database");

        } else {
            try (Connection conn = Db.connect()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName);
                int columns = ps.executeUpdate();
                System.out.println("deleted" + columns + " columns.");

            } catch (SQLException e) {
                System.err.println("Connection error: Can't connect to server");
            }
        }
    }

    public void rate(RoomType type) {
        try (Connection conn = Db.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM rates WHERE room_type = ?");
            ps.setString(1, Room.convertRm(type));

            int columns = ps.executeUpdate();
            System.out.println("deleted" + columns + " columns.");

        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
        }

    }
}
