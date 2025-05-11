package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.users.Customer;

public class Payment {

    private double balance;
    private double amountDue;

    public Payment(double bill, Customer customer) {
        this.amountDue = bill;
        this.balance = customer.getBalance();

    }

    public boolean process(double amountPaid, Customer customer, int billId) {
        double usedFromBalance = Math.min(balance, amountDue);
        amountDue -= usedFromBalance;
        balance -= usedFromBalance;

        boolean success = applyPayment(amountPaid);
        if (success) {
            Db.update.updateCustomerBalance(customer.getId(), balance);
            Db.update.updateBillStatus(billId, Billing.BillingStatus.PAID);
        }
        return success;
    }

    public boolean directPay(double amountPaid) {
        return applyPayment(amountPaid);
    }

    private boolean applyPayment(double amountPaid) {
        amountDue -= amountPaid;

        if (amountDue == 0) {
            System.out.println("you're good to go.");
            return true;

        } else if (amountDue < 0) {
            double refund = processHelper(amountDue);
            balance += refund;
            // update db
            System.out.println("Adding " + refund + " to your balance!");
            return true;

        } else {
            System.err.println("you still need to pay: " + processHelper(amountDue));
            return false;
        }
    }

    private double processHelper(double i) {
        return Math.round(Math.abs(i) * 100.0) / 100.0;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }
}
