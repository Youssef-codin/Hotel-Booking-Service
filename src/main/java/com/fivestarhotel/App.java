package com.fivestarhotel;

import com.fivestarhotel.Database.Db;

public class App {
    public static void main(String[] args) {
        Db.connect("root", "mimimi45");
        Db.update.sql("room", "room_floor", "room_number", 3, 5);
    }
}
