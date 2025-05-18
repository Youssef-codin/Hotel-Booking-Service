package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
<<<<<<< HEAD
=======
import com.fivestarhotel.users.Admin;

import java.util.ArrayList;
>>>>>>> edd958b1fff09e15e47dac765047b127c5307066

public class App {
    public static void main(String[] args) throws Exception {
        /*
         * WELCOME TO BOOKIT!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER, Use Db.connect() and add
         * your username and password
         *
         * Afterwards use Db.select.loadRates(); to load in the rates from the database
         *
         * Use Db.select.loadRates(); to load the latest rates from the DB to the
         * program
         *
         * Login with admin@gbookit.com and Pass_123
         */
<<<<<<< HEAD
        // TODO: After successful login, transition to:
        // Simplified authentication to use hardcoded credentials:
        // Admin: admin@bookit.com / Admin_123
        Db.connect("root", "6831");

        // Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com",
        // "Admin_123"));

=======
        Db.connect("root", "");
        Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com", "Pass_123"));
>>>>>>> edd958b1fff09e15e47dac765047b127c5307066
        Db.select.loadRates();

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);
    }
}
