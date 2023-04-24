package com.wishbook.catalog.commonmodels;

import java.io.Serializable;
import java.util.List;

public class CartProductModel {


    public String id;
    private String order_number;
    private List<Items> items;
    private boolean finalize;
    private String total_cart_items;
    private boolean reseller_order;

    private boolean add_quantity;
    private boolean add_size;

    public boolean isAdd_size() {
        return add_size;
    }

    public void setAdd_size(boolean add_size) {
        this.add_size = add_size;
    }

    public boolean isAdd_quantity() {
        return add_quantity;
    }

    public void setAdd_quantity(boolean add_quantity) {
        this.add_quantity = add_quantity;
    }
    public String getTotal_cart_items() {
        return total_cart_items;
    }

    public void setTotal_cart_items(String total_cart_items) {
        this.total_cart_items = total_cart_items;
    }

    public boolean isFinalize() {
        return finalize;
    }

    public void setFinalize(boolean finalize) {
        this.finalize = finalize;
    }

    public CartProductModel(List<Items> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public boolean isReseller_order() {
        return reseller_order;
    }

    public void setReseller_order(boolean reseller_order) {
        this.reseller_order = reseller_order;
    }

    public static class Items implements Serializable {
        private String rate;
        private String quantity;
        private String product;
        private boolean is_full_catalog;
        private String selling_company;
        private String note;
        private double display_amount;

        public Items() {
        }

        public Items(String rate, String quantity, String product, boolean is_full_catalog, String note) {
            this.rate = rate;
            this.quantity = quantity;
            this.product = product;
            this.is_full_catalog = is_full_catalog;
            this.note = note;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getSelling_company() {
            return selling_company;
        }

        public void setSelling_company(String selling_company) {
            this.selling_company = selling_company;
        }

        public boolean isIs_full_catalog() {
            return is_full_catalog;
        }

        public void setIs_full_catalog(boolean is_full_catalog) {
            this.is_full_catalog = is_full_catalog;
        }


        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public double getDisplay_amount() {
            return display_amount;
        }

        public void setDisplay_amount(double display_amount) {
            this.display_amount = display_amount;
        }
    }


}
