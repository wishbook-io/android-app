package com.wishbook.catalog.commonmodels.responses;

public class ResponseSellerDashBoard {

    private String avg_order_delay;

    private String live_non_catalogs;

    private String live_catalogs;

    private String cancellation_rate;

    private String live_sets;

    private String pending_orders;

    private String facility_type;

    private String leads;

    private String sales_orders;


    public String getAvg_order_delay() {
        return avg_order_delay;
    }

    public void setAvg_order_delay(String avg_order_delay) {
        this.avg_order_delay = avg_order_delay;
    }

    public String getLive_non_catalogs() {
        return live_non_catalogs;
    }

    public void setLive_non_catalogs(String live_non_catalogs) {
        this.live_non_catalogs = live_non_catalogs;
    }

    public String getLive_catalogs() {
        return live_catalogs;
    }

    public void setLive_catalogs(String live_catalogs) {
        this.live_catalogs = live_catalogs;
    }

    public String getCancellation_rate() {
        return cancellation_rate;
    }

    public void setCancellation_rate(String cancellation_rate) {
        this.cancellation_rate = cancellation_rate;
    }

    public String getLive_sets() {
        return live_sets;
    }

    public void setLive_sets(String live_sets) {
        this.live_sets = live_sets;
    }

    public String getPending_orders() {
        return pending_orders;
    }

    public void setPending_orders(String pending_orders) {
        this.pending_orders = pending_orders;
    }

    public String getFacility_type() {
        return facility_type;
    }

    public void setFacility_type(String facility_type) {
        this.facility_type = facility_type;
    }

    public String getLeads() {
        return leads;
    }

    public void setLeads(String leads) {
        this.leads = leads;
    }

    public String getSales_orders() {
        return sales_orders;
    }

    public void setSales_orders(String sales_orders) {
        this.sales_orders = sales_orders;
    }
}
