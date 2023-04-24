package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PushObjectShare {
    int change_price_fix;
    int change_price_add;
    String push_type;
    String push_downstream;
    String message;
    String status;
    String sms;
    String buyer_segmentation;
    String full_catalog_orders_only;
    String[] catalog;
    String[] selection;
    String single_company_push;
    private String exp_desp_date;
    private int change_price_add_amount;


    public PushObjectShare() {
        this.change_price_fix = 0;
        this.change_price_add = 0;
        this.push_type = "";
        this.push_downstream = "";
        this.message = "";
        this.status = "";
        this.sms = "";
        this.single_company_push = "";
        this.buyer_segmentation = "";
        this.full_catalog_orders_only = "";
        this.catalog = new String[]{};
    }

    public String getExp_desp_date() {
        return exp_desp_date;
    }

    public void setExp_desp_date(String exp_desp_date) {
        this.exp_desp_date = exp_desp_date;
    }

    public String getSingle_company_push() {
        return single_company_push;
    }

    public void setSingle_company_push(String single_company_push) {
        this.single_company_push = single_company_push;
    }

    public String[] getSelection() {
        return selection;
    }

    public void setSelection(String[] selection) {
        this.selection = selection;
    }

    public int getChange_price_fix() {
        return change_price_fix;
    }

    public void setChange_price_fix(int change_price_fix) {
        this.change_price_fix = change_price_fix;
    }

    public int getChange_price_add() {
        return change_price_add;
    }

    public void setChange_price_add(int change_price_add) {
        this.change_price_add = change_price_add;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getPush_downstream() {
        return push_downstream;
    }

    public void setPush_downstream(String push_downstream) {
        this.push_downstream = push_downstream;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getBuyer_segmentation() {
        return buyer_segmentation;
    }

    public void setBuyer_segmentation(String buyer_segmentation) {
        this.buyer_segmentation = buyer_segmentation;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }

    public String[] getCatalog() {
        return catalog;
    }

    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    public int getChange_price_add_amount() {
        return change_price_add_amount;
    }

    public void setChange_price_add_amount(int change_price_add_amount) {
        this.change_price_add_amount = change_price_add_amount;
    }
}
