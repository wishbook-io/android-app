package com.wishbook.catalog.commonmodels.postpatchmodels;

import com.wishbook.catalog.home.orders.add.ProductItems;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class PurchaseBackOrder {
    String order_number;
    String company;
    String selections;
    ArrayList<String> backorders;
    ArrayList<ProductItems> items;

    public PurchaseBackOrder() {
    }

    public PurchaseBackOrder(String order_number, String company, String selections, ArrayList<ProductItems> items) {
        this.order_number = order_number;
        this.company = company;
        this.selections = selections;
        this.items = items;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSelections() {
        return selections;
    }

    public void setSelections(String selections) {
        this.selections = selections;
    }

    public ArrayList<ProductItems> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductItems> items) {
        this.items = items;
    }

    public ArrayList<String> getBackorders() {
        return backorders;
    }

    public void setBackorders(ArrayList<String> backorders) {
        this.backorders = backorders;
    }
}
