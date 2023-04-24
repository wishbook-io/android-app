package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

public class RequestPendingOrderItems {

    private String action_type;

    private ArrayList<Items> items;

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }


    // sub class
    public class Items {
        private String size;

        private String product_id;

        private int ready_to_ship_qty;

        private int unavailable_qty;

        private String id;

        private ArrayList<Integer> order_item_ids;

        private String seller_company_id;

        private String expected_date;


        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public int getReady_to_ship_qty() {
            return ready_to_ship_qty;
        }

        public void setReady_to_ship_qty(int ready_to_ship_qty) {
            this.ready_to_ship_qty = ready_to_ship_qty;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public int getUnavailable_qty() {
            return unavailable_qty;
        }

        public void setUnavailable_qty(int unavailable_qty) {
            this.unavailable_qty = unavailable_qty;
        }

        public String getExpected_date() {
            return expected_date;
        }

        public void setExpected_date(String expected_date) {
            this.expected_date = expected_date;
        }
    }
}
