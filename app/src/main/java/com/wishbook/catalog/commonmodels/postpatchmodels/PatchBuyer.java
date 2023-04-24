package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by tech2 on 29/7/17.
 */
public class PatchBuyer {
    String id;
    String payment_duration;
    String discount;
    String cash_discount;
    String group_type;
    String credit_limit;
    String broker_company;
    String brokerage_fees;

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }




    public PatchBuyer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }
}
