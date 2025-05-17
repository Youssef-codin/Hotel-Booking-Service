package com.fivestarhotel;

import java.util.ArrayList;

import com.fivestarhotel.Database.Db;

public class BillingSystem {

    public static void viewAllBills() {
        ArrayList<Billing> bills = Db.select.getAllBills();
        if (bills == null || bills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            System.out.println("All Bills:");
            for (Billing bill : bills) {
                System.out.println("Bill ID: " + bill.getBillId() +
                        ", Booking ID: " + bill.getBookingId() +
                        ", Status: " + bill.getStatus());
            }
        }
    }

    public static void viewCusBills(int customerId) {
        ArrayList<Billing> bills = Db.select.getBillsByCustomer(customerId);
        if (bills == null || bills.isEmpty()) {
            System.out.println("No bills found for customer ID: " + customerId);
        } else {
            System.out.println("Bills for customer ID: " + customerId);
            for (Billing bill : bills) {
                System.out.println("Bill ID: " + bill.getBillId() +
                        ", Booking ID: " + bill.getBookingId() +
                        ", Status: " + bill.getStatus());
            }
        }
    }

    public static void viewCusBills(Billing bill) {
        int customerId = bill.getBooking().getCustomer_id();
        ArrayList<Billing> bills = Db.select.getBillsByCustomer(customerId);
        if (bills == null || bills.isEmpty()) {
            System.out.println("No bills found for customer ID: " + customerId);

        } else {
            System.out.println("Bills for customer ID: " + customerId);
            for (Billing b : bills) {
                System.out.println("Bill ID: " + b.getBillId() +
                        ", Booking ID: " + b.getBookingId() +
                        ", Status: " + b.getStatus());
            }
        }
    }
}
