package com.wishbook.catalog.commonmodels.responses;

public class ResponseShipment {

    private boolean only_wishbook_shipping;

    private String weight;

    private String shipping_method_duration;

    private String shipping_method_name;

    private String is_available_reseller;

    private String shipping_method_id;

    private String shipping_method_delivery_duration;

    private double shipping_charge;

    private boolean is_default;


    public boolean getOnly_wishbook_shipping() {
        return only_wishbook_shipping;
    }

    public void setOnly_wishbook_shipping(boolean only_wishbook_shipping) {
        this.only_wishbook_shipping = only_wishbook_shipping;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getShipping_method_duration() {
        return shipping_method_duration;
    }

    public void setShipping_method_duration(String shipping_method_duration) {
        this.shipping_method_duration = shipping_method_duration;
    }

    public String getShipping_method_name() {
        return shipping_method_name;
    }

    public void setShipping_method_name(String shipping_method_name) {
        this.shipping_method_name = shipping_method_name;
    }

    public String getIs_available_reseller() {
        return is_available_reseller;
    }

    public void setIs_available_reseller(String is_available_reseller) {
        this.is_available_reseller = is_available_reseller;
    }

    public String getShipping_method_id() {
        return shipping_method_id;
    }

    public void setShipping_method_id(String shipping_method_id) {
        this.shipping_method_id = shipping_method_id;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(double shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getShipping_method_delivery_duration() {
        return shipping_method_delivery_duration;
    }

    public void setShipping_method_delivery_duration(String shipping_method_delivery_duration) {
        this.shipping_method_delivery_duration = shipping_method_delivery_duration;
    }

    public boolean isOnly_wishbook_shipping() {
        return only_wishbook_shipping;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }
}
