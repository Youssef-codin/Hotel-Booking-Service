package com.fivestarhotel.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update {
    public void resetIncrement() {
        try {
            assert Db.connection != null;
            PreparedStatement ps = Db.connection.prepareStatement("ALTER TABLE room AUTO_INCREMENT = 1;");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Connection error: Can't connect to server");
        }
    }

    public void sql(String tableName, String columnName, String primaryColumnName, int primaryKeyValue,
            int updateValue) {
        try {
            assert Db.connection != null;
            PreparedStatement ps = Db.connection.prepareStatement(
                    "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + primaryColumnName + " = ?");
            ps.setInt(1, updateValue);
            ps.setInt(2, primaryKeyValue);
            int update = ps.executeUpdate();
            System.out.println("Updated " + update + " successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error Occurred");
        }
    }
}
