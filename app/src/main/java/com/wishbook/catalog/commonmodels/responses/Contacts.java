package com.wishbook.catalog.commonmodels.responses;

public class Contacts
{
    private String pending_buyer;

    private String pending_supplier;

    private String request;

    private String approved_buyer;

    private String approved_supplier;

    private String total_supplier_enquiry;

    private String total_buyer_enquiry;


    public String getPending_supplier() {
        return pending_supplier;
    }

    public void setPending_supplier(String pending_supplier) {
        this.pending_supplier = pending_supplier;
    }

    public String getTotal_supplier_enquiry() {
        return total_supplier_enquiry;
    }

    public void setTotal_supplier_enquiry(String total_supplier_enquiry) {
        this.total_supplier_enquiry = total_supplier_enquiry;
    }

    public String getTotal_buyer_enquiry() {
        return total_buyer_enquiry;
    }

    public void setTotal_buyer_enquiry(String total_buyer_enquiry) {
        this.total_buyer_enquiry = total_buyer_enquiry;
    }

    public String getPending_buyer ()
    {
        return pending_buyer;
    }

    public void setPending_buyer (String pending_buyer)
    {
        this.pending_buyer = pending_buyer;
    }

    public String getRequest ()
    {
        return request;
    }

    public void setRequest (String request)
    {
        this.request = request;
    }

    public String getApproved_buyer ()
    {
        return approved_buyer;
    }

    public void setApproved_buyer (String approved_buyer)
    {
        this.approved_buyer = approved_buyer;
    }

    public String getApproved_supplier ()
    {
        return approved_supplier;
    }

    public void setApproved_supplier (String approved_supplier)
    {
        this.approved_supplier = approved_supplier;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [pending_buyer = "+pending_buyer+", request = "+request+", approved_buyer = "+approved_buyer+", approved_supplier = "+approved_supplier+"]";
    }
}
