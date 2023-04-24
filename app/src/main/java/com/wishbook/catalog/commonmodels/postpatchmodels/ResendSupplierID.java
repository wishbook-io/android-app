package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class ResendSupplierID {
    ArrayList<String> suppliers;

    public ResendSupplierID(ArrayList<String> suppliers) {
        this.suppliers = suppliers;
    }

    public ArrayList<String> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(ArrayList<String> suppliers) {
        this.suppliers = suppliers;
    }
}
