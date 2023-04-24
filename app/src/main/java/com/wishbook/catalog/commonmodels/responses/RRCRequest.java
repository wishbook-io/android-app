package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;
import java.util.ArrayList;

public class RRCRequest implements Serializable {

    private String reject_reason;

    private String request_status;

    private String return_type;

    private String return_courier;

    private String total_discount;

    private String request_type;

    private ArrayList<Rrc_items> rrc_items;

    private ArrayList<Rrc_images> rrc_images;

    private String created;

    private String total_qty;

    private String total_rate;

    private String request_reason_text;

    private String total_tax;

    private String return_awb_no;

    private String replacement_order_id;

    private String uniware_reverse_pickup_code;

    private String uniware_return_invoice_id;

    private String invoice_id;

    private String modified;

    private String id;

    private String invoice;

    private Refund refund;

    private String return_shipment_status;

    private String order;

    private ArrayList<String> images;


    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getReturn_type() {
        return return_type;
    }

    public void setReturn_type(String return_type) {
        this.return_type = return_type;
    }

    public String getReturn_courier() {
        return return_courier;
    }

    public void setReturn_courier(String return_courier) {
        this.return_courier = return_courier;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public ArrayList<Rrc_items> getRrc_items() {
        return rrc_items;
    }

    public void setRrc_items(ArrayList<Rrc_items> rrc_items) {
        this.rrc_items = rrc_items;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(String total_qty) {
        this.total_qty = total_qty;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public void setTotal_rate(String total_rate) {
        this.total_rate = total_rate;
    }

    public String getRequest_reason_text() {
        return request_reason_text;
    }

    public void setRequest_reason_text(String request_reason_text) {
        this.request_reason_text = request_reason_text;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(String total_tax) {
        this.total_tax = total_tax;
    }

    public String getReturn_awb_no() {
        return return_awb_no;
    }

    public void setReturn_awb_no(String return_awb_no) {
        this.return_awb_no = return_awb_no;
    }

    public String getReplacement_order_id() {
        return replacement_order_id;
    }

    public void setReplacement_order_id(String replacement_order_id) {
        this.replacement_order_id = replacement_order_id;
    }

    public String getUniware_reverse_pickup_code() {
        return uniware_reverse_pickup_code;
    }

    public void setUniware_reverse_pickup_code(String uniware_reverse_pickup_code) {
        this.uniware_reverse_pickup_code = uniware_reverse_pickup_code;
    }

    public String getUniware_return_invoice_id() {
        return uniware_return_invoice_id;
    }

    public void setUniware_return_invoice_id(String uniware_return_invoice_id) {
        this.uniware_return_invoice_id = uniware_return_invoice_id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public String getReturn_shipment_status() {
        return return_shipment_status;
    }

    public void setReturn_shipment_status(String return_shipment_status) {
        this.return_shipment_status = return_shipment_status;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<Rrc_images> getRrc_images() {
        return rrc_images;
    }

    public void setRrc_images(ArrayList<Rrc_images> rrc_images) {
        this.rrc_images = rrc_images;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }
}
