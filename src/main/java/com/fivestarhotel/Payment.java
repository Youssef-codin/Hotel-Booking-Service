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

    // 0 no refunds no due amounts
    // -1 refunded to balance
    // -2 due amount
    public double process(double amountPaid, int customer_id, int billId) {
        double usedFromBalance = Math.min(balance, amountDue);
        amountDue -= usedFromBalance;
        balance -= usedFromBalance;

        double success = applyPayment(amountPaid, customer_id);
        if (success == 0 || success == -1) {
            Db.update.updateCustomerBalance(customer_id, balance);
            Db.update.updateBill(billId, Billing.BillingStatus.PAID);
        } else {
            Db.update.updateCustomerBalance(customer_id, balance);
        }

        return success;
    }

    private double applyPayment(double amountPaid, int customerId) {
        amountDue -= amountPaid;

        if (amountDue == 0) {
            System.out.println("you're good to go.");
            return 0;

        } else if (amountDue < 0) {
            double refund = processHelper(amountDue);
            balance += refund;
            System.out.println("Adding " + refund + " to your balance!");
            return refund;

        } else {
            System.err.println("you still need to pay: " + processHelper(amountDue));
            return -2;
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
