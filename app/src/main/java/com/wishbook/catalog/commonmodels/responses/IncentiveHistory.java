package com.wishbook.catalog.commonmodels.responses;

public class IncentiveHistory {

    private double purchase_actual_amount;

    private String id;

    private String created;

    private String company;

    private double incentive_amount;

    private String to_date;

    private String tier_applicable;

    private double purchase_target_amount;

    private String from_date;

    private String modified;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getIncentive_amount() {
        return incentive_amount;
    }

    public void setIncentive_amount(double incentive_amount) {
        this.incentive_amount = incentive_amount;
    }


    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getTier_applicable() {
        return tier_applicable;
    }

    public void setTier_applicable(String tier_applicable) {
        this.tier_applicable = tier_applicable;
    }



    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public double getPurchase_actual_amount() {
        return purchase_actual_amount;
    }

    public void setPurchase_actual_amount(double purchase_actual_amount) {
        this.purchase_actual_amount = purchase_actual_amount;
    }

    public double getPurchase_target_amount() {
        return purchase_target_amount;
    }

    public void setPurchase_target_amount(double purchase_target_amount) {
        this.purchase_target_amount = purchase_target_amount;
    }
}
