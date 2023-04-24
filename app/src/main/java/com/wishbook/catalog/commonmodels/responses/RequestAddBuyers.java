package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class RequestAddBuyers {

    private ArrayList<String> buying_companies;

    public ArrayList<String> getBuying_companies() {
        return buying_companies;
    }

    public void setBuying_companies(ArrayList<String> buying_companies) {
        this.buying_companies = buying_companies;
    }

    @Override
    public String toString() {
        return "ClassPojo [buying_companies = " + buying_companies + "]";
    }
}
