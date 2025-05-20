package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
import com.fivestarhotel.users.Admin;

public class App {
    public static void main(String[] args) throws Exception {
        /*
         * WELCOME TO BOOKIT!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER (make sure you run the
         * SQLScript), Use Db.connect() and add
         * your username and password
         *
         * Login with admin@gbookit.com and Pass_123
         */

        Db.connect("root", "yoyo8080");
        Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com", "Pass_123"));
        Db.select.loadRates();

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);
    }
}
