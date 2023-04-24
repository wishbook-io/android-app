package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class RequestAddSuppliers {

    private ArrayList<String> selling_companies;

    public ArrayList<String> getSelling_companies() {
        return selling_companies;
    }

    public void setSelling_companies(ArrayList<String> selling_companies) {
        this.selling_companies = selling_companies;
    }

    @Override
    public String toString() {
        return "ClassPojo [buying_companies = " + selling_companies + "]";
    }
}
