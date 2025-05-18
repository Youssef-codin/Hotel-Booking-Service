package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
import com.fivestarhotel.users.Admin;

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
        Db.connect("root", "");
        Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com", "Pass_123"));
        Db.select.loadRates();

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);

    }
}
