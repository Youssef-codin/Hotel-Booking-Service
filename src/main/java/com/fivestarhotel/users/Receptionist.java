package com.fivestarhotel.users;

public class Receptionist extends User {

    public Receptionist(String receptionist_fname, String receptionist_lname,
            String receptionist_email, String receptionist_password) {
        super(receptionist_fname, receptionist_lname, receptionist_email, receptionist_password);
    }

    public Receptionist(int receptionist_id, String receptionist_fname, String receptionist_lname,
            String receptionist_email, String receptionist_password) {
        super(receptionist_id, receptionist_fname, receptionist_lname, receptionist_email, receptionist_password);
    }
}
