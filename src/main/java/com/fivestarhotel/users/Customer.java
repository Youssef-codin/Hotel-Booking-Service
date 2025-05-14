package com.fivestarhotel.users;

public class Customer extends User {
    private String phone;
    private String address;
    private double balance;

    public Customer(String customer_fname, String customer_lname, String customer_email,
            String password, String phone, String address, double balance) {
        super(customer_fname, customer_lname, customer_email, password);
        this.phone = phone;
        this.address = address;
        this.balance = balance;
    }

    public Customer(int customer_id, String customer_fname, String customer_lname, String customer_email,
            String password, String phone, String address, double balance) {
        super(customer_id, customer_fname, customer_lname, customer_email, password);
        this.phone = phone;
        this.address = address;
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addy) {
        address = addy;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double bal) {
        balance = bal;

    }
}
