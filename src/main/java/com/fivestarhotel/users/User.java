package com.fivestarhotel.users;

public abstract class User {
    private int id;
    private String fname;
    private String lname;
    private String email;
    private String password;

    public User(int id, String fname, String lname, String email, String password) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getFname() { return fname; }
    public String getLname() { return lname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public abstract void showDashboard();
}
