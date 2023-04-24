package com.wishbook.catalog.commonmodels;

public class ResponseWBMoneyDashboard {

    private String total_available;

    private String total_redeemed;

    private String total_received;

    public String getTotal_available() {
        return total_available;
    }

    public void setTotal_available(String total_available) {
        this.total_available = total_available;
    }

    public String getTotal_redeemed() {
        return total_redeemed;
    }

    public void setTotal_redeemed(String total_redeemed) {
        this.total_redeemed = total_redeemed;
    }

    public String getTotal_received() {
        return total_received;
    }

    public void setTotal_received(String total_received) {
        this.total_received = total_received;
    }

}
