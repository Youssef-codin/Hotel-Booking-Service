package com.fivestarhotel.users;

public class Admin extends User {

    public Admin(int admin_id, String admin_fname, String admin_lname, String admin_email, String admin_password) {
        super(admin_id, admin_fname, admin_lname, admin_email, admin_password);
    }

    @Override
    public void showDashboard() {
        System.out.println("Admin Dashboard Loaded.");
    }

    public void manageRooms() {
    }

    public void viewRoomLogs() {
    }

    public void updateRoomStatus() {
    }
}
