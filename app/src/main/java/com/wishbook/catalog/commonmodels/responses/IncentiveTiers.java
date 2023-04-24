package com.wishbook.catalog.commonmodels.responses;

public class IncentiveTiers {

    private String incentive_percentage;

    private String to_amount;

    private String created;

    private String from_amount;

    private String modified;

    private String id;

    private String cycle;


    public String getIncentive_percentage() {
        return incentive_percentage;
    }

    public void setIncentive_percentage(String incentive_percentage) {
        this.incentive_percentage = incentive_percentage;
    }

    public String getTo_amount() {
        return to_amount;
    }

    public void setTo_amount(String to_amount) {
        this.to_amount = to_amount;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFrom_amount() {
        return from_amount;
    }

    public void setFrom_amount(String from_amount) {
        this.from_amount = from_amount;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
}
