package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class ResponseCouponList implements Serializable {
    private String code;

    private String created;

    private String discount_amount;

    private String valid_from;

    private String num_uses;

    private String discount_percentage;

    private String type;

    private String discount_type;

    private String min_order_value;

    private String valid_till;

    private String modified;

    private String max_discount;

    private String campaign;

    private String num_used;

    private String id;

    private String user;

    private Display_text display_text;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getNum_uses() {
        return num_uses;
    }

    public void setNum_uses(String num_uses) {
        this.num_uses = num_uses;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public String getMin_order_value() {
        return min_order_value;
    }

    public void setMin_order_value(String min_order_value) {
        this.min_order_value = min_order_value;
    }

    public String getValid_till() {
        return valid_till;
    }

    public void setValid_till(String valid_till) {
        this.valid_till = valid_till;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getMax_discount() {
        return max_discount;
    }

    public void setMax_discount(String max_discount) {
        this.max_discount = max_discount;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getNum_used() {
        return num_used;
    }

    public void setNum_used(String num_used) {
        this.num_used = num_used;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Display_text getDisplay_text() {
        return display_text;
    }

    public void setDisplay_text(Display_text display_text) {
        this.display_text = display_text;
    }

    // Start Inner Class

    public class Display_text implements Serializable {
        private String hi;

        private String en;

        public String getHi() {
            return hi;
        }

        public void setHi(String hi) {
            this.hi = hi;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        @Override
        public String toString() {
            return "ClassPojo [hi = " + hi + ", en = " + en + "]";
        }
    }

}
