package com.wishbook.catalog.commonmodels.responses;

public class OrderInfo
{
    private String purchaseorder_pending;
    private String purchaseorder_cancelled;
    private String purchaseorder_dispatched;

    private String salesorder_pending;
    private String salesorder_cancelled;
    private String salesorder_dispatched;

    public String getPurchaseorder_pending ()
    {
        return purchaseorder_pending;
    }

    public void setPurchaseorder_pending (String purchaseorder_pending)
    {
        this.purchaseorder_pending = purchaseorder_pending;
    }

    public String getSalesorder_pending ()
    {
        return salesorder_pending;
    }

    public void setSalesorder_pending (String salesorder_pending)
    {
        this.salesorder_pending = salesorder_pending;
    }

    public String getPurchaseorder_cancelled() {
        return purchaseorder_cancelled;
    }

    public void setPurchaseorder_cancelled(String purchaseorder_cancelled) {
        this.purchaseorder_cancelled = purchaseorder_cancelled;
    }

    public String getPurchaseorder_dispatched() {
        return purchaseorder_dispatched;
    }

    public void setPurchaseorder_dispatched(String purchaseorder_dispatched) {
        this.purchaseorder_dispatched = purchaseorder_dispatched;
    }

    public String getSalesorder_cancelled() {
        return salesorder_cancelled;
    }

    public void setSalesorder_cancelled(String salesorder_cancelled) {
        this.salesorder_cancelled = salesorder_cancelled;
    }

    public String getSalesorder_dispatched() {
        return salesorder_dispatched;
    }

    public void setSalesorder_dispatched(String salesorder_dispatched) {
        this.salesorder_dispatched = salesorder_dispatched;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [purchaseorder_pending = "+purchaseorder_pending+", salesorder_pending = "+salesorder_pending+"]";
    }
}