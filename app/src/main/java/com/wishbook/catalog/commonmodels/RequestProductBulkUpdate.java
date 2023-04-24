package com.wishbook.catalog.commonmodels;


import java.util.ArrayList;

public class RequestProductBulkUpdate {

    private ArrayList<Products> products;

    public ArrayList<Products> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Products> products) {
        this.products = products;
    }


    public class Products {
        private String available_sizes;

        private String id;

        public String getAvailable_sizes() {
            return available_sizes;
        }

        public void setAvailable_sizes(String available_sizes) {
            this.available_sizes = available_sizes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "ClassPojo [available_sizes = " + available_sizes + ", id = " + id + "]";
        }
    }
}
