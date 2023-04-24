package com.wishbook.catalog.commonmodels.postpatchmodels;

import com.wishbook.catalog.commonmodels.Payments;
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;

/**
 * Created by root on 10/8/17.
 */

public class Invoice
{
    private String amount;

    private String total_amount;

    private String status;

    private String id;

    private String total_qty;

    private String order;

    private String taxes;

    private Items[] items;

    private String pending_amount;

    private Payments[] payments;

    public Shipments[] shipments;

    private String invoice_number;

    private String paid_amount;

    private String datetime;

    private String payment_status;

    private String tax_class_1;

    private String tax_class_2;

    private String total_tax_value_1;

    private String seller_discount;

    private String total_tax_value_2;

    private ResponseCouponList wb_coupon;

    private double wb_coupon_discount;

    private String shipping_charges;

    private int wbmoney_points_used;

    private int wbpoints_used;

    private double wbmoney_redeem_amt;

    private boolean reseller_order;
    private double display_amount;

    private String invoice_type;

    private String uniware_invoice_pdf;

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTotal_qty ()
    {
        return total_qty;
    }

    public void setTotal_qty (String total_qty)
    {
        this.total_qty = total_qty;
    }

    public String getOrder ()
    {
        return order;
    }

    public void setOrder (String order)
    {
        this.order = order;
    }

    public Items[] getItems ()
    {
        return items;
    }

    public void setItems (Items[] items)
    {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPending_amount ()
    {
        return pending_amount;
    }

    public void setPending_amount (String pending_amount)
    {
        this.pending_amount = pending_amount;
    }

    public Payments[] getPayments ()
    {
        return payments;
    }

    public void setPayments (Payments[] payments)
    {
        this.payments = payments;
    }

    public Shipments[] getShipments ()
    {
        return shipments;
    }

    public void setShipments (Shipments[] shipments)
    {
        this.shipments = shipments;
    }

    public String getInvoice_number ()
{
    return invoice_number;
}

    public void setInvoice_number (String invoice_number)
    {
        this.invoice_number = invoice_number;
    }

    public String getPaid_amount ()
    {
        return paid_amount;
    }

    public void setPaid_amount (String paid_amount)
    {
        this.paid_amount = paid_amount;
    }

    public String getDatetime ()
    {
        return datetime;
    }

    public void setDatetime (String datetime)
    {
        this.datetime = datetime;
    }

    public String getPayment_status ()
    {
        return payment_status;
    }

    public void setPayment_status (String payment_status)
    {
        this.payment_status = payment_status;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getTax_class_1() {
        return tax_class_1;
    }

    public void setTax_class_1(String tax_class_1) {
        this.tax_class_1 = tax_class_1;
    }

    public String getTax_class_2() {
        return tax_class_2;
    }

    public void setTax_class_2(String tax_class_2) {
        this.tax_class_2 = tax_class_2;
    }

    public String getTotal_tax_value_1() {
        return total_tax_value_1;
    }

    public void setTotal_tax_value_1(String total_tax_value_1) {
        this.total_tax_value_1 = total_tax_value_1;
    }

    public String getSeller_discount() {
        return seller_discount;
    }

    public void setSeller_discount(String seller_discount) {
        this.seller_discount = seller_discount;
    }

    public String getTotal_tax_value_2() {
        return total_tax_value_2;
    }

    public void setTotal_tax_value_2(String total_tax_value_2) {
        this.total_tax_value_2 = total_tax_value_2;
    }

    public ResponseCouponList getWb_coupon() {
        return wb_coupon;
    }

    public void setWb_coupon(ResponseCouponList wb_coupon) {
        this.wb_coupon = wb_coupon;
    }

    public double getWb_coupon_discount() {
        return wb_coupon_discount;
    }

    public void setWb_coupon_discount(double wb_coupon_discount) {
        this.wb_coupon_discount = wb_coupon_discount;
    }

    public String getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(String shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public int getWbmoney_points_used() {
        return wbmoney_points_used;
    }

    public void setWbmoney_points_used(int wbmoney_points_used) {
        this.wbmoney_points_used = wbmoney_points_used;
    }

    public int getWbpoints_used() {
        return wbpoints_used;
    }

    public void setWbpoints_used(int wbpoints_used) {
        this.wbpoints_used = wbpoints_used;
    }

    public double getWbmoney_redeem_amt() {
        return wbmoney_redeem_amt;
    }

    public void setWbmoney_redeem_amt(double wbmoney_redeem_amt) {
        this.wbmoney_redeem_amt = wbmoney_redeem_amt;
    }

    public boolean isReseller_order() {
        return reseller_order;
    }

    public void setReseller_order(boolean reseller_order) {
        this.reseller_order = reseller_order;
    }

    public double getDisplay_amount() {
        return display_amount;
    }

    public void setDisplay_amount(double display_amount) {
        this.display_amount = display_amount;
    }

    public String getInvoice_type() {
        return invoice_type;
    }

    public void setInvoice_type(String invoice_type) {
        this.invoice_type = invoice_type;
    }

    public String getUniware_invoice_pdf() {
        return uniware_invoice_pdf;
    }

    public void setUniware_invoice_pdf(String uniware_invoice_pdf) {
        this.uniware_invoice_pdf = uniware_invoice_pdf;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [amount = "+amount+", id = "+id+", total_qty = "+total_qty+", order = "+order+", pending_amount = "+pending_amount+", payments = "+payments+", shipments = "+shipments+", invoice_number = "+invoice_number+", paid_amount = "+paid_amount+", datetime = "+datetime+", payment_status = "+payment_status+"]";
    }
}