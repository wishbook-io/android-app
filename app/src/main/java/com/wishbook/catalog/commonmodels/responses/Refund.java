package com.wishbook.catalog.commonmodels.responses;

import java.io.Serializable;

public class Refund implements Serializable {

    private String transaction_id;

    private String refund_status;

    private String requested_by;

    private String refund_mode;

    private String modified_by;

    private String refund_amount;

    private String refund_cause;

    private Refund_date refund_date;

    private String wb_refunded;

    private String other_refunded;

    private String reward_refunded;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }

    public String getRequested_by() {
        return requested_by;
    }

    public void setRequested_by(String requested_by) {
        this.requested_by = requested_by;
    }

    public String getRefund_mode() {
        return refund_mode;
    }

    public void setRefund_mode(String refund_mode) {
        this.refund_mode = refund_mode;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getRefund_cause() {
        return refund_cause;
    }

    public void setRefund_cause(String refund_cause) {
        this.refund_cause = refund_cause;
    }

    public Refund_date getRefund_date() {
        return refund_date;
    }

    public void setRefund_date(Refund_date refund_date) {
        this.refund_date = refund_date;
    }

    public String getWb_refunded() {
        return wb_refunded;
    }

    public void setWb_refunded(String wb_refunded) {
        this.wb_refunded = wb_refunded;
    }

    public String getOther_refunded() {
        return other_refunded;
    }

    public void setOther_refunded(String other_refunded) {
        this.other_refunded = other_refunded;
    }

    public String getReward_refunded() {
        return reward_refunded;
    }

    public void setReward_refunded(String reward_refunded) {
        this.reward_refunded = reward_refunded;
    }

    // Inner Class
    public class Refund_date implements Serializable
    {
        private String process_date;

        private String initiated_date;

        public String getProcess_date ()
        {
            return process_date;
        }

        public void setProcess_date (String process_date)
        {
            this.process_date = process_date;
        }

        public String getInitiated_date ()
        {
            return initiated_date;
        }

        public void setInitiated_date (String initiated_date)
        {
            this.initiated_date = initiated_date;
        }

    }
}
