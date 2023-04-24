package com.wishbook.catalog.home.orders.add;

import com.wishbook.catalog.commonmodels.responses.ProductsSelection;

import java.util.ArrayList;

/**
 * Created by root on 2/2/17.
 */
public class SelectionPurchaseOrder{
    String order_number;
    String company;
    String selections;
   ArrayList<ProductsSelection> products;

    public SelectionPurchaseOrder() {
    }

    public SelectionPurchaseOrder(String order_number, String company, String selections,  ArrayList<ProductsSelection> products) {
        this.order_number = order_number;
        this.company = company;
        this.selections = selections;
        this.products = products;
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

    public ArrayList<ProductsSelection> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductsSelection> products) {
        this.products = products;
    }
}
