package com.wishbook.catalog.commonmodels.responses;


public class BuyerDiscountRequest {

    private String id;

    private String cash_discount = "0.0";

    private String buyer_type= "0.0";

    private String credit_limit ="0.0";

    private String price_list;

    private String payment_duration;

    private String discount;

    public BuyerDiscountRequest() {
    }

    public BuyerDiscountRequest(String id, String cash_discount, String buyer_type, String payment_duration, String discount) {
        this.id = id;
        this.cash_discount = cash_discount;
        this.buyer_type = buyer_type;
        this.payment_duration = payment_duration;
        this.discount = discount;
    }

    public BuyerDiscountRequest(String cash_discount, String buyer_type, String payment_duration, String discount) {
        this.cash_discount = cash_discount;
        this.buyer_type = buyer_type;
        this.payment_duration = payment_duration;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
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

    @Override
    public String toString() {
        return "BuyerDiscountRequest{" +
                "id='" + id + '\'' +
                ", cash_discount='" + cash_discount + '\'' +
                ", buyer_type='" + buyer_type + '\'' +
                ", credit_limit='" + credit_limit + '\'' +
                ", price_list='" + price_list + '\'' +
                ", payment_duration='" + payment_duration + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}
