package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class DeliveryInfo {

    private boolean delivery;

    private ArrayList<ResponseShipment> shipping_methods;

    private boolean cod_available;

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public ArrayList<ResponseShipment> getShipping_methods() {
        return shipping_methods;
    }

    public void setShipping_methods(ArrayList<ResponseShipment> shipping_methods) {
        this.shipping_methods = shipping_methods;
    }

    public boolean isCod_available() {
        return cod_available;
    }

    public void setCod_available(boolean cod_available) {
        this.cod_available = cod_available;
    }
}
