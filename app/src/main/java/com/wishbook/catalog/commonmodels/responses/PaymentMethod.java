package com.wishbook.catalog.commonmodels.responses;

public class PaymentMethod {

    private String id;

    private String display_name;

    private String name;

    private String payment_type;

    private String amount;

    private boolean is_blocked;

    private boolean is_show;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amountl) {
        this.amount = amount;
    }

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public boolean isIs_show() {
        return is_show;
    }

    public void setIs_show(boolean is_show) {
        this.is_show = is_show;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", display_name = " + display_name + ", name = " + name + ", payment_type = " + payment_type + "]";
    }
}
