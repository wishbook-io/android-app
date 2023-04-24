package com.wishbook.catalog.commonmodels;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductMyDetail implements Serializable {
    private boolean i_am_selling_sell_full_catalog;

    private boolean i_am_selling_this;

    private String available_sizes;

    private boolean is_owner;

    private String is_addedto_wishlist;

    private String last_shared_date;

    private String b2c_product_url;

    private boolean currently_selling;

    private String id;

    private ArrayList<ProductMyDetail> products;

    private String catalog_seller_id;

    private double clearance_discount_percentage;

    private double single_piece_price;

    private double full_price;

    private double single_piece_price_fix;

    private double single_piece_price_percentage;


    public boolean isI_am_selling_sell_full_catalog() {
        return i_am_selling_sell_full_catalog;
    }

    public void setI_am_selling_sell_full_catalog(boolean i_am_selling_sell_full_catalog) {
        this.i_am_selling_sell_full_catalog = i_am_selling_sell_full_catalog;
    }

    public boolean isI_am_selling_this() {
        return i_am_selling_this;
    }

    public void setI_am_selling_this(boolean i_am_selling_this) {
        this.i_am_selling_this = i_am_selling_this;
    }

    public String getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(String available_sizes) {
        this.available_sizes = available_sizes;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public String getIs_addedto_wishlist() {
        return is_addedto_wishlist;
    }

    public void setIs_addedto_wishlist(String is_addedto_wishlist) {
        this.is_addedto_wishlist = is_addedto_wishlist;
    }

    public String getLast_shared_date() {
        return last_shared_date;
    }

    public void setLast_shared_date(String last_shared_date) {
        this.last_shared_date = last_shared_date;
    }

    public boolean isCurrently_selling() {
        return currently_selling;
    }

    public void setCurrently_selling(boolean currently_selling) {
        this.currently_selling = currently_selling;
    }

    public ArrayList<ProductMyDetail> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductMyDetail> products) {
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getB2c_product_url() {
        return b2c_product_url;
    }

    public void setB2c_product_url(String b2c_product_url) {
        this.b2c_product_url = b2c_product_url;
    }

    public String getCatalog_seller_id() {
        return catalog_seller_id;
    }

    public void setCatalog_seller_id(String catalog_seller_id) {
        this.catalog_seller_id = catalog_seller_id;
    }

    public double getClearance_discount_percentage() {
        return clearance_discount_percentage;
    }

    public void setClearance_discount_percentage(double clearance_discount_percentage) {
        this.clearance_discount_percentage = clearance_discount_percentage;
    }

    public double getSingle_piece_price() {
        return single_piece_price;
    }

    public void setSingle_piece_price(double single_piece_price) {
        this.single_piece_price = single_piece_price;
    }

    public double getFull_price() {
        return full_price;
    }

    public void setFull_price(double full_price) {
        this.full_price = full_price;
    }

    public double getSingle_piece_price_fix() {
        return single_piece_price_fix;
    }

    public void setSingle_piece_price_fix(double single_piece_price_fix) {
        this.single_piece_price_fix = single_piece_price_fix;
    }

    public double getSingle_piece_price_percentage() {
        return single_piece_price_percentage;
    }

    public void setSingle_piece_price_percentage(double single_piece_price_percentage) {
        this.single_piece_price_percentage = single_piece_price_percentage;
    }

    @Override
    public String toString() {
        return "ProductMyDetail{" +
                "i_am_selling_sell_full_catalog=" + i_am_selling_sell_full_catalog +
                ", i_am_selling_this=" + i_am_selling_this +
                ", available_sizes='" + available_sizes + '\'' +
                ", is_owner=" + is_owner +
                ", is_addedto_wishlist='" + is_addedto_wishlist + '\'' +
                ", last_shared_date='" + last_shared_date + '\'' +
                ", currently_selling=" + currently_selling +
                ", id='" + id + '\'' +
                ", products=" + products +
                '}';
    }

    public class Products implements Serializable {
        private boolean i_am_selling_sell_full_catalog;

        private boolean i_am_selling_this;

        private String available_sizes;

        private boolean is_owner;

        private boolean is_addedto_wishlist;

        private String id;

        private boolean currently_selling;


        public boolean isI_am_selling_sell_full_catalog() {
            return i_am_selling_sell_full_catalog;
        }

        public void setI_am_selling_sell_full_catalog(boolean i_am_selling_sell_full_catalog) {
            this.i_am_selling_sell_full_catalog = i_am_selling_sell_full_catalog;
        }

        public boolean isI_am_selling_this() {
            return i_am_selling_this;
        }

        public void setI_am_selling_this(boolean i_am_selling_this) {
            this.i_am_selling_this = i_am_selling_this;
        }

        public String getAvailable_sizes() {
            return available_sizes;
        }

        public void setAvailable_sizes(String available_sizes) {
            this.available_sizes = available_sizes;
        }

        public boolean isIs_owner() {
            return is_owner;
        }

        public void setIs_owner(boolean is_owner) {
            this.is_owner = is_owner;
        }

        public boolean isIs_addedto_wishlist() {
            return is_addedto_wishlist;
        }

        public void setIs_addedto_wishlist(boolean is_addedto_wishlist) {
            this.is_addedto_wishlist = is_addedto_wishlist;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isCurrently_selling() {
            return currently_selling;
        }

        public void setCurrently_selling(boolean currently_selling) {
            this.currently_selling = currently_selling;
        }
    }

}
