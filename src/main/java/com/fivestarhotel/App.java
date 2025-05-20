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
         * Step 2: Run Db.create.signUpUser(new Admin("admin", "admin",
         * "admin@bookit.com", "Pass_123"));
         * at least once
         *
         * Step 3: Login with admin@gbookit.com and Pass_123
         */

        Db.connect("root", "root");
        Db.select.loadRates();
        // run at least once
        Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com", "Pass_123"));

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);
    }
}
