package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class PatchSeller {
    String id;
    String fix_amount;
    String percentage_amount;
    String brokerage_fees;


    public PatchSeller() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFix_amount() {
        return fix_amount;
    }

    public void setFix_amount(String fix_amount) {
        this.fix_amount = fix_amount;
    }

    public String getPercentage_amount() {
        return percentage_amount;
    }

    public void setPercentage_amount(String percentage_amount) {
        this.percentage_amount = percentage_amount;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }
}
