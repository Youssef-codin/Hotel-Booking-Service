package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.Room.RoomType;

import java.util.ArrayList;

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
        Db.connect("root", "1234");
        ArrayList<Room> rooms = Db.select.getRooms();
        ArrayList<Room> updatedRooms = new ArrayList<Room>();
        for (int i = 0; i < Db.select.lastRoomNum(); i++) {
            Room room = rooms.get(i);
            room.setStatus(true);
            updatedRooms.add(room);
        }
        Db.update.rooms(updatedRooms);
    }
}
