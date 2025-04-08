package com.fivestarhotel.users;



public class Customer extends User {
    private String phone;
    private String address;

    public Customer(int customer_id, String customer_fname, String customer_lname, String customer_email, String password, String phone, String address) {
        super(customer_id, customer_fname, customer_lname, customer_email, password);
        this.phone = phone;
        this.address = address;
    }

    @Override
    public void showDashboard() {
        System.out.println("Customer Dashboard Loaded.");
    }

    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public void bookRoom() {}
    public void makePayment() {}
    public void viewBilling() {}
}