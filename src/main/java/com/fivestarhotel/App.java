package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Room.RoomType;

public class App {
    public static void main(String[] args) {

        /*
         * WELCOME TO Db!! You may do "limited" sql functionalities in Java by using
         * Db.!!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER, Use Db.connect() and add
         * your username and password
         * 
         * Db.connect("Enter username here (probably is just root)",
         * "Enter password here");
         *
         * Use Db.select.loadRates(); to load the latest rates from the DB to the
         * program
         * 
         */

        Db.connect("root", "yoyo8080");
        Db.select.getRoom(1);
    }
}
