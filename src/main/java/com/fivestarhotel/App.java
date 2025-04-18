package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Room.RoomType;
import com.fivestarhotel.users.Customer;

import java.util.ArrayList;

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
         */

        Customer c = new Customer(101, "joe", "loe", "joemo@gmail.com", "1234", "012304", "1234 street", 500);
        // new Customer(customer_id, customer_fname, customer_lname, customer_email,
        // password, phone, address, balance)
        Payment p = new Payment(1000, c);
        p.process(100);
    }
}
