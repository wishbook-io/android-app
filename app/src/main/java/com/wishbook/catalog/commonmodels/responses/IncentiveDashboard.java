package com.wishbook.catalog.commonmodels.responses;

public class IncentiveDashboard {

    private double total_weekly_purchase_target_amount;

    private double total_weekly_purchase_actual_amount;

    private double total_weekly_purchase_required_to_reach_target;

    private double total_incentive_amount;


    public double getTotal_weekly_purchase_target_amount() {
        return total_weekly_purchase_target_amount;
    }

    public void setTotal_weekly_purchase_target_amount(double total_weekly_purchase_target_amount) {
        this.total_weekly_purchase_target_amount = total_weekly_purchase_target_amount;
    }

    public double getTotal_weekly_purchase_actual_amount() {
        return total_weekly_purchase_actual_amount;
    }

    public void setTotal_weekly_purchase_actual_amount(double total_weekly_purchase_actual_amount) {
        this.total_weekly_purchase_actual_amount = total_weekly_purchase_actual_amount;
    }

    public double getTotal_weekly_purchase_required_to_reach_target() {
        return total_weekly_purchase_required_to_reach_target;
    }

    public void setTotal_weekly_purchase_required_to_reach_target(double total_weekly_purchase_required_to_reach_target) {
        this.total_weekly_purchase_required_to_reach_target = total_weekly_purchase_required_to_reach_target;
    }

    public double getTotal_incentive_amount() {
        return total_incentive_amount;
    }

    public void setTotal_incentive_amount(double total_incentive_amount) {
        this.total_incentive_amount = total_incentive_amount;
    }
}
