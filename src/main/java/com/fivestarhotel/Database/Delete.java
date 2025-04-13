package com.fivestarhotel.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Delete {
    public void all(String tableName) {
        try {
            assert Db.connection != null;
            PreparedStatement ps = Db.connection.prepareStatement("DELETE FROM " + tableName);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");
        }
    }

    public void sql(String tableName, String columnName, int roomNumber) {
        try {
            assert Db.connection != null;
            PreparedStatement ps = Db.connection
                    .prepareStatement("DELETE FROM " + tableName + " WHERE " + columnName + " = ?");
            ps.setInt(1, roomNumber);
            int deletes = ps.executeUpdate();
            System.out.println("Deleted " + deletes + " row");

        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");
        }
    }
}
