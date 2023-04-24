package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by tech4 on 4/10/17.
 */

public class SellerDiscount {

    private String cash_discount;

    private String id;

    private String buyer_type;

    private String credit_limit;

    private String company;

    private String price_list;

    private String payment_duration;

    private String discount;


    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyer_type() {
        return buyer_type;
    }

    public void setBuyer_type(String buyer_type) {
        this.buyer_type = buyer_type;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice_list() {
        return price_list;
    }

    public void setPrice_list(String price_list) {
        this.price_list = price_list;
    }

    public String getPayment_duration() {
        return payment_duration;
    }

    public void setPayment_duration(String payment_duration) {
        this.payment_duration = payment_duration;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
