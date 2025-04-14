package com.fivestarhotel.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String URL = "jdbc:mysql://localhost:3306/HMSDB";
    private static String user = "root";
    private static String password = "1234";
    public static Select select = new Select();
    public static Create create = new Create();
    public static Update update = new Update();
    public static Delete delete = new Delete();
    private static int numConn = 0;

    protected static String tables = "admin|customer|receptionist|room|booking|billing|room_log";

    public static void connect(String newUser, String newPassword) {
        user = newUser;
        password = newPassword;
        try {
            DriverManager.getConnection(URL, user, password);
            System.out.println("Connection Successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() {
        if (!(numConn > 0)) {
            System.out.println("Previously set username and password have been used, change them if necessary.");
        }
        try {
            numConn++;
            return DriverManager.getConnection(URL, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1045) {
                System.err.println("Invalid username or password, error code: " + e.getErrorCode()
                        + "\nfix: add username and password parameters when connecting for the first time");
            }
            return null;
        }
    }
}
