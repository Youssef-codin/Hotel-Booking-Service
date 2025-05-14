package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
import com.fivestarhotel.users.Admin;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        /*
         * WELCOME TO Db!! You may do "limited" sql functionalities in Java by using
         * Db.!!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER, Use Db.connect() and add
         * your username and password
         *
         * Afterwards use Db.select.loadRates(); to load in the rates from the database
         *
         * Db.connect("Enter username here (probably is just root)",
         * "Enter password here");
         *
         * Use Db.select.loadRates(); to load the latest rates from the DB to the
         * program
         *
         * NEW GUI INTERFACE ADDED, Make sure to add an admin/receptionist account
         * before running the program
         *
         * Make sure to set the proper DB username and password in the GUI main methods
         */
        // TODO: After successful login, transition to:
        // Simplified authentication to use hardcoded credentials:
        // Admin: admin@bookit.com / Admin_123
        Db.connect("root", "");
        Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com", "Admin_123"));

        // Start the application
        SwingUtilities.invokeLater(() -> {
            BookItLogin loginSystem = new BookItLogin();
            loginSystem.setVisible(true);
        });
    }
}
