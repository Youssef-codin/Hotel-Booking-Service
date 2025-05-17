package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;

public class App {
    public static void main(String[] args) throws Exception {
        /*
         * WELCOME TO Db!! You may do "limited" sql functionalities in Java by using
         * Db.!!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER, Use Db.connect() and add
         * your username and password
         *
         * Afterwards use Db.select.loadRates(); to load in the rates from the database
         *
         * Use Db.select.loadRates(); to load the latest rates from the DB to the
         * program
         *
         * NEW GUI INTERFACE ADDED, Make sure to add an admin/receptionist account
         * before running the program
         *
         * Make sure to set the proper DB username and password in the GUI main methods
         */
        // TODO: After successful login, transition to:
        // Simplified authentication to use hardcoded credentials:
        // Admin: admin@bookit.com / Admin_123
        Db.connect("root", "6831");

        // Db.create.signUpUser(new Admin("admin", "admin", "admin@bookit.com",
        // "Admin_123"));

        Db.select.loadRates();
        // Db.delete.deleteUser(2, UserRoles.RECEPTIONIST);
        // Db.create.signUpUser(new Receptionist("recep", "recep", "recep@bookit.com",
        // "Recep_123"));
        //
        // Db.create.signUpUser(new Customer("joe", "hany", "joe@gmail.com", "Pass",
        // "01234567890", "12th street", 0));

        // Db.create.addRooms(RoomType.SINGLE, 5);
        // Db.create.addRooms(RoomType.DOUBLE, 3);
        // Db.create.addRooms(RoomType.SUITE, 2);

        // Room room = Db.select.getRoom(1);
        // Booking booking1 = new Booking(room, 1, 1, LocalDate.of(2025, 5, 17),
        // LocalDate.of(2025, 5, 18)); // Example
        // Db.create.addBooking(booking1);

        // Start the application

        BookItLogin loginSystem = new BookItLogin();
        loginSystem.setVisible(true);

    }
}
