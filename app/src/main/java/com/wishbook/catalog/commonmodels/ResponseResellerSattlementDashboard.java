package com.wishbook.catalog.commonmodels;

public class ResponseResellerSattlementDashboard {

    private double total_due;

    private double total_received;

    private double total_earned;


    public double getTotal_due() {
        return total_due;
    }

    public void setTotal_due(double total_due) {
        this.total_due = total_due;
    }

    public double getTotal_received() {
        return total_received;
    }

    public void setTotal_received(double total_received) {
        this.total_received = total_received;
    }

    public double getTotal_earned() {
        return total_earned;
    }

    public void setTotal_earned(double total_earned) {
        this.total_earned = total_earned;
    }
}
