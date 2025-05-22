package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
import com.fivestarhotel.users.Admin;

public class App {
    public static void main(String[] args) throws Exception {
        /*
         * WELCOME TO BOOKIT!
         * Step 1: Make sure to run the SQLScript
         *
         * Step 2: make sure to change the username and password of Db.connect() to ur
         * SQLServer username and pass
         *
         * Step 3: Run Db.create.signUpUser(new Admin("admin", "admin",
         * "admin@bookit.com", "Pass_123"));
         * at least once
         *
         * Step 4: Login with admin@gbookit.com and Pass_123
         */

        Db.connect("root", "yoyo8080");

        Db.select.loadRates();
        // Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com",
        // "Pass_123"));
        // run at least once ^

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);
    }
}
