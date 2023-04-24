package com.wishbook.catalog.commonmodels.responses;


import com.wishbook.catalog.commonmodels.postpatchmodels.BuyerSegmentation;

import java.util.ArrayList;

public class ResponseBrandDiscountExpand {

    private String cash_discount;

    private String id;

    private ArrayList<Brand> brands;

    private String credit_discount;

    private double single_pcs_discount;

    private String discount_type;

    private String name;

    private boolean all_brands;

    private ArrayList<BuyerSegmentation> buyer_segmentations;

    private String selling_company;


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

    public ArrayList<Brand> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<Brand> brands) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getAll_brands() {
        return all_brands;
    }

    public void setAll_brands(boolean all_brands) {
        this.all_brands = all_brands;
    }

    public ArrayList<BuyerSegmentation> getBuyer_segmentations() {
        return buyer_segmentations;
    }

    public void setBuyer_segmentations(ArrayList<BuyerSegmentation> buyer_segmentations) {
        this.buyer_segmentations = buyer_segmentations;
    }

    public String getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(String selling_company) {
        this.selling_company = selling_company;
    }

    public double getSingle_pcs_discount() {
        return single_pcs_discount;
    }

    public void setSingle_pcs_discount(double single_pcs_discount) {
        this.single_pcs_discount = single_pcs_discount;
    }
}
