package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 15/4/17.
 */
public class CommonSummaryList {
    private String header;
    private int BuyerSupplier;
    private int pendingRequest;
    private int Enquiries;
    private String OrdersPending;
    private String OrdersDispatched;
    private String OrdersCancelled;

    public CommonSummaryList(String header, int buyerSupplier, int pendingRequest, int enquiries) {
        this.header = header;
        BuyerSupplier = buyerSupplier;
        this.pendingRequest = pendingRequest;
        Enquiries = enquiries;
    }

    public CommonSummaryList(String header, String ordersPending, String ordersDispatched, String ordersCancelled) {
        this.header = header;
        OrdersPending = ordersPending;
        OrdersDispatched = ordersDispatched;
        OrdersCancelled = ordersCancelled;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOrdersPending() {
        return OrdersPending;
    }

    public void setOrdersPending(String ordersPending) {
        OrdersPending = ordersPending;
    }

    public String getOrdersDispatched() {
        return OrdersDispatched;
    }

    public void setOrdersDispatched(String ordersDispatched) {
        OrdersDispatched = ordersDispatched;
    }

    public String getOrdersCancelled() {
        return OrdersCancelled;
    }

    public void setOrdersCancelled(String ordersCancelled) {
        OrdersCancelled = ordersCancelled;
    }

    public int getBuyerSupplier() {
        return BuyerSupplier;
    }

    public void setBuyerSupplier(int buyerSupplier) {
        BuyerSupplier = buyerSupplier;
    }

    public int getPendingRequest() {
        return pendingRequest;
    }

    public void setPendingRequest(int pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    public int getEnquiries() {
        return Enquiries;
    }

    public void setEnquiries(int enquiries) {
        Enquiries = enquiries;
    }
}
