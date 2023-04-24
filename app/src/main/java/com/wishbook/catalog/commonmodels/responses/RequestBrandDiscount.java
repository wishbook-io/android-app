package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class RequestBrandDiscount {

    private String id;

    private String name;

    private String cash_discount;

    private String single_pcs_discount;

    private ArrayList<String> brands;

    private String credit_discount;

    private String discount_type;

    private ArrayList<String> buyer_segmentations;

    private boolean all_brands;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public ArrayList<String> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<String> brands) {
        this.brands = brands;
    }

    public String getCredit_discount() {
        return credit_discount;
    }

    public void setCredit_discount(String credit_discount) {
        this.credit_discount = credit_discount;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public ArrayList<String> getBuyer_segmentations() {
        return buyer_segmentations;
    }

    public void setBuyer_segmentations(ArrayList<String> buyer_segmentations) {
        this.buyer_segmentations = buyer_segmentations;
    }

    public boolean isAll_brands() {
        return all_brands;
    }

    public void setAll_brands(boolean all_brands) {
        this.all_brands = all_brands;
    }

    public String getSingle_pcs_discount() {
        return single_pcs_discount;
    }

    public void setSingle_pcs_discount(String single_pcs_discount) {
        this.single_pcs_discount = single_pcs_discount;
    }
}
