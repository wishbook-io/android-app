package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseBrandDiscount {

    private ArrayList<String> brands;

    private String single_pcs_discount;

    private String from_date;

    private String created;

    private String selling_all_catalog;

    private String selling_all_catalog_as_single;

    private String selling_company;

    private String discount_type;

    private String[] buyer_segmentations;

    private String credit_discount;

    private String cash_discount;

    private String name;

    private String modified;

    private boolean all_brands;

    private String id;

    public ArrayList<String> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<String> brands) {
        this.brands = brands;
    }

    public String getSingle_pcs_discount() {
        return single_pcs_discount;
    }

    public void setSingle_pcs_discount(String single_pcs_discount) {
        this.single_pcs_discount = single_pcs_discount;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getSelling_all_catalog() {
        return selling_all_catalog;
    }

    public void setSelling_all_catalog(String selling_all_catalog) {
        this.selling_all_catalog = selling_all_catalog;
    }

    public String getSelling_all_catalog_as_single() {
        return selling_all_catalog_as_single;
    }

    public void setSelling_all_catalog_as_single(String selling_all_catalog_as_single) {
        this.selling_all_catalog_as_single = selling_all_catalog_as_single;
    }

    public String getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(String selling_company) {
        this.selling_company = selling_company;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public String[] getBuyer_segmentations() {
        return buyer_segmentations;
    }

    public void setBuyer_segmentations(String[] buyer_segmentations) {
        this.buyer_segmentations = buyer_segmentations;
    }

    public String getCredit_discount() {
        return credit_discount;
    }

    public void setCredit_discount(String credit_discount) {
        this.credit_discount = credit_discount;
    }

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public boolean isAll_brands() {
        return all_brands;
    }

    public void setAll_brands(boolean all_brands) {
        this.all_brands = all_brands;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClassPojo [brands = " + brands + ", single_pcs_discount = " + single_pcs_discount + ", from_date = " + from_date + ", created = " + created + ", selling_all_catalog = " + selling_all_catalog + ", selling_all_catalog_as_single = " + selling_all_catalog_as_single + ", selling_company = " + selling_company + ", discount_type = " + discount_type + ", buyer_segmentations = " + buyer_segmentations + ", credit_discount = " + credit_discount + ", cash_discount = " + cash_discount + ", name = " + name + ", modified = " + modified + ", all_brands = " + all_brands + ", id = " + id + "]";
    }
}
