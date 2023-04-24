package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseSellerPendingOrderItems {

    private String product_catalog_title;

    private String product;

    private String created;

    private String product_title;

    private String pending_qty;

    private String update_date;

    private String expected_date;

    private String size;

    private String ready_to_ship_qty;

    private String modified;

    private String unavailable_qty;

    private String company;

    private String id;

    private String product_brand;

    private Extra_json extra_json;

    private String product_sku;

    private String product_image;


    public String getProduct_catalog_title() {
        return product_catalog_title;
    }

    public void setProduct_catalog_title(String product_catalog_title) {
        this.product_catalog_title = product_catalog_title;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getPending_qty() {
        return pending_qty;
    }

    public void setPending_qty(String pending_qty) {
        this.pending_qty = pending_qty;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getExpected_date() {
        return expected_date;
    }

    public void setExpected_date(String expected_date) {
        this.expected_date = expected_date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getReady_to_ship_qty() {
        return ready_to_ship_qty;
    }

    public void setReady_to_ship_qty(String ready_to_ship_qty) {
        this.ready_to_ship_qty = ready_to_ship_qty;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getUnavailable_qty() {
        return unavailable_qty;
    }

    public void setUnavailable_qty(String unavailable_qty) {
        this.unavailable_qty = unavailable_qty;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_brand = product_brand;
    }

    public Extra_json getExtra_json() {
        return extra_json;
    }

    public void setExtra_json(Extra_json extra_json) {
        this.extra_json = extra_json;
    }

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    // Subclass of extrajson
    public class Extra_json {
        private String pending_since;

        private Qty qty;

        private boolean is_full_catalog;

        private ArrayList<Integer> order_item_ids;

        private String seller_company_id;

        public String getPending_since() {
            return pending_since;
        }

        public void setPending_since(String pending_since) {
            this.pending_since = pending_since;
        }

        public Qty getQty() {
            return qty;
        }

        public void setQty(Qty qty) {
            this.qty = qty;
        }

        public boolean isIs_full_catalog() {
            return is_full_catalog;
        }

        public void setIs_full_catalog(boolean is_full_catalog) {
            this.is_full_catalog = is_full_catalog;
        }

        public ArrayList<Integer> getOrder_item_ids() {
            return order_item_ids;
        }

        public void setOrder_item_ids(ArrayList<Integer> order_item_ids) {
            this.order_item_ids = order_item_ids;
        }

        public String getSeller_company_id() {
            return seller_company_id;
        }

        public void setSeller_company_id(String seller_company_id) {
            this.seller_company_id = seller_company_id;
        }

    }

    // subclass of QTY
    public class Qty {
        private int ready_to_ship_qty;

        private int unavailable_qty;

        private int order_qty;

        private int pending_quantity;


        public int getReady_to_ship_qty() {
            return ready_to_ship_qty;
        }

        public void setReady_to_ship_qty(int ready_to_ship_qty) {
            this.ready_to_ship_qty = ready_to_ship_qty;
        }

        public int getUnavailable_qty() {
            return unavailable_qty;
        }

        public void setUnavailable_qty(int unavailable_qty) {
            this.unavailable_qty = unavailable_qty;
        }

        public int getOrder_qty() {
            return order_qty;
        }

        public void setOrder_qty(int order_qty) {
            this.order_qty = order_qty;
        }

        public int getPending_quantity() {
            return pending_quantity;
        }

        public void setPending_quantity(int pending_quantity) {
            this.pending_quantity = pending_quantity;
        }
    }


}
