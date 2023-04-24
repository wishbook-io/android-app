package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.postpatchmodels.Order_item;

import java.io.Serializable;
import java.util.ArrayList;


public class Response_Product {
    String id;
    String product;
    String product_image;
    String product_title;
    String quantity;
    String rate;
    String sales_order;
    String packing_type;
    String note;
    String product_type;
    String no_of_pcs;
    int pending_quantity;
    int  invoiced_qty;
    int dispatched_qty;
    int canceled_qty;
    int ready_to_ship_qty;
    int delivered_qty;
    int returned_qty;
    int unavailable_qty;
    boolean isChecked;
    boolean show_cancellation_option;
    String cancellation_option_reason;

    private ArrayList<Item_ratings> item_ratings;

    // client side variable
    int RWQ_qty;

    public Response_Product(String id, String product, String product_image, String product_title, String quantity, String rate, String sales_order) {
        this.id = id;
        this.product = product;
        this.product_image = product_image;
        this.product_title = product_title;
        this.quantity = quantity;
        this.rate = rate;
        this.sales_order = sales_order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getSales_order() {
        return sales_order;
    }

    public void setSales_order(String sales_order) {
        this.sales_order = sales_order;
    }

    public String getPacking_type() {
        return packing_type;
    }

    public void setPacking_type(String packing_type) {
        this.packing_type = packing_type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getNo_of_pcs() {
        return no_of_pcs;
    }

    public void setNo_of_pcs(String no_of_pcs) {
        this.no_of_pcs = no_of_pcs;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPending_quantity() {
        return pending_quantity;
    }

    public void setPending_quantity(int pending_quantity) {
        this.pending_quantity = pending_quantity;
    }

    public int getInvoiced_qty() {
        return invoiced_qty;
    }

    public void setInvoiced_qty(int invoiced_qty) {
        this.invoiced_qty = invoiced_qty;
    }

    public int getDispatched_qty() {
        return dispatched_qty;
    }

    public void setDispatched_qty(int dispatched_qty) {
        this.dispatched_qty = dispatched_qty;
    }

    public int getCanceled_qty() {
        return canceled_qty;
    }

    public void setCanceled_qty(int canceled_qty) {
        this.canceled_qty = canceled_qty;
    }

    public int getReady_to_ship_qty() {
        return ready_to_ship_qty;
    }

    public void setReady_to_ship_qty(int ready_to_ship_qty) {
        this.ready_to_ship_qty = ready_to_ship_qty;
    }

    public int getDelivered_qty() {
        return delivered_qty;
    }

    public void setDelivered_qty(int delivered_qty) {
        this.delivered_qty = delivered_qty;
    }

    public int getReturned_qty() {
        return returned_qty;
    }

    public void setReturned_qty(int returned_qty) {
        this.returned_qty = returned_qty;
    }

    public int getUnavailable_qty() {
        return unavailable_qty;
    }

    public void setUnavailable_qty(int unavailable_qty) {
        this.unavailable_qty = unavailable_qty;
    }

    public boolean isShow_cancellation_option() {
        return show_cancellation_option;
    }

    public void setShow_cancellation_option(boolean show_cancellation_option) {
        this.show_cancellation_option = show_cancellation_option;
    }

    public String getCancellation_option_reason() {
        return cancellation_option_reason;
    }

    public void setCancellation_option_reason(String cancellation_option_reason) {
        this.cancellation_option_reason = cancellation_option_reason;
    }

    public ArrayList<Item_ratings> getItem_ratings() {
        return item_ratings;
    }

    public void setItem_ratings(ArrayList<Item_ratings> item_ratings) {
        this.item_ratings = item_ratings;
    }

    public int getRWQ_qty() {
        return RWQ_qty;
    }

    public void setRWQ_qty(int RWQ_qty) {
        this.RWQ_qty = RWQ_qty;
    }

    public class Item_ratings implements Serializable
    {
        private String product;

        private String order_item;

        private String review;

        private String rating;

        private String last_name;

        private String id;

        private String user;

        private String first_name;

        public String getProduct ()
        {
            return product;
        }

        public void setProduct (String product)
        {
            this.product = product;
        }

        public String getOrder_item ()
        {
            return order_item;
        }

        public void setOrder_item (String order_item)
        {
            this.order_item = order_item;
        }

        public String getReview ()
        {
            return review;
        }

        public void setReview (String review)
        {
            this.review = review;
        }

        public String getRating ()
        {
            return rating;
        }

        public void setRating (String rating)
        {
            this.rating = rating;
        }

        public String getLast_name ()
        {
            return last_name;
        }

        public void setLast_name (String last_name)
        {
            this.last_name = last_name;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getUser ()
        {
            return user;
        }

        public void setUser (String user)
        {
            this.user = user;
        }

        public String getFirst_name ()
        {
            return first_name;
        }

        public void setFirst_name (String first_name)
        {
            this.first_name = first_name;
        }

    }
}
