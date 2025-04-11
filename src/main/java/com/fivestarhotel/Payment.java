package com.fivestarhotel;


public class Payment{
    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        ONLINE_PAYMENT
    }
    private final double amount;
    private final PaymentMethod method;
    private boolean isProcessed;

    public Payment(double amount, PaymentMethod method){
        this.amount = amount;
        this.method = method;
        this.isProcessed = false;
    }

    public void process() {
        if (!isProcessed) {
            System.out.println("Processing payment...");
            isProcessed = true;
        }
}