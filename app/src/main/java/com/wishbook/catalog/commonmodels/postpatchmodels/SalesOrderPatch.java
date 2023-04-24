package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class SalesOrderPatch {


    int id;


    ArrayList<Integer> salesorder;

    public SalesOrderPatch(int id, ArrayList<Integer> salesorder) {
        this.id = id;
        this.salesorder = salesorder;
    }

    public ArrayList<Integer> getSalesorder() {
        return salesorder;
    }

    public void setSalesorder(ArrayList<Integer> salesorder) {
        this.salesorder = salesorder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
