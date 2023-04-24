package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 10/10/16.
 */
public class Invoiceitem_set {
    private String amount;

    private String id;

    private String end_date;

    private String rate;

    private String invoice;

    private String company;

    private String qty;

    private String start_date;

    private String item_type;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    @Override
    public String toString() {
        return "ClassPojo [amount = " + amount + ", id = " + id + ", end_date = " + end_date + ", rate = " + rate + ", invoice = " + invoice + ", company = " + company + ", qty = " + qty + ", start_date = " + start_date + ", item_type = " + item_type + "]";
    }
}
