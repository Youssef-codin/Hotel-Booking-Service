package com.fivestarhotel;

import com.fivestarhotel.Database.Create;
import com.fivestarhotel.Database.Db;
import com.fivestarhotel.users.Receptionist;
import com.fivestarhotel.users.User;
import com.fivestarhotel.users.Customer;

public class App {
    public static void main(String[] args)  {

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
         */

        Db.connect("root","root");
        User user = Db.select.signInUser("john.doe@example.com", "StrongP@ssw0rd!");
        if (user != null) {
            System.out.println("Welcome, " + user.getFname() + " " + user.getLname() + "!");
        } else {
            System.out.println("Sign-in failed. Please check your credentials.");
        }

        System.out.println("Sign-in process complete.");


    }
}

