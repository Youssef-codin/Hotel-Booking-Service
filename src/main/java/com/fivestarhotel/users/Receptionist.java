package com.fivestarhotel.users;

public class Receptionist extends User {

    public Receptionist(int receptionist_id, String receptionist_fname, String receptionist_lname,
            String receptionist_email, String receptionist_password) {
        super(receptionist_id, receptionist_fname, receptionist_lname, receptionist_email, receptionist_password);
    }

    @Override
    public void showDashboard() {
        System.out.println("Receptionist Dashboard Loaded.");
    }

    public void checkInGuest() {
    }

    public void checkOutGuest() {
    }

    public void viewBookings() {
    }

}
