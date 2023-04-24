package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 10/10/16.
 */
public class Response_Invoice {

    private String balance_amount;

    private String id;

    private String end_date;

    private String total_credit_used;

    private String status;

    private String company;

    private Invoiceitem_set[] invoiceitem_set;

    private String start_date;

    private String discount;

    private String billed_amount;

    public String getBalance_amount() {
        return balance_amount;
    }

    public void setBalance_amount(String balance_amount) {
        this.balance_amount = balance_amount;
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

    public String getTotal_credit() {
        return total_credit_used;
    }

    public void setTotal_credit(String total_credit) {
        this.total_credit_used = total_credit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Invoiceitem_set[] getInvoiceitem_set() {
        return invoiceitem_set;
    }

    public void setInvoiceitem_set(Invoiceitem_set[] invoiceitem_set) {
        this.invoiceitem_set = invoiceitem_set;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBilled_amount() {
        return billed_amount;
    }

    public void setBilled_amount(String billed_amount) {
        this.billed_amount = billed_amount;
    }

    @Override
    public String toString() {
        return "ClassPojo [balance_amount = " + balance_amount + ", id = " + id + ", end_date = " + end_date + ", total_credit_used = " + total_credit_used + ", status = " + status + ", company = " + company + ", invoiceitem_set = " + invoiceitem_set + ", start_date = " + start_date + ", discount = " + discount + ", billed_amount = " + billed_amount + "]";
    }


}
