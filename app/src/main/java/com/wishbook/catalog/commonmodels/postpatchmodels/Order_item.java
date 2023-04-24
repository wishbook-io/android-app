package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by root on 10/8/17.
 */

public class Order_item
{
    private String dispatched_qty;

    private String canceled_qty;

    private String invoiced_qty;

    private String product;

    private String id;

    private String rate;

    private String product_title;

    private String product_category;

    private String pending_quantity;

    private String sales_order;

    private String product_sku;

    private String product_image;

    private String product_catalog;

    private String quantity;

    String packing_type;

    private ArrayList<Item_ratings> item_ratings;

    public String getDispatched_qty ()
    {
        return dispatched_qty;
    }

    public void setDispatched_qty (String dispatched_qty)
    {
        this.dispatched_qty = dispatched_qty;
    }

    public String getCanceled_qty ()
    {
        return canceled_qty;
    }

    public void setCanceled_qty (String canceled_qty)
    {
        this.canceled_qty = canceled_qty;
    }

    public String getInvoiced_qty ()
    {
        return invoiced_qty;
    }

    public void setInvoiced_qty (String invoiced_qty)
    {
        this.invoiced_qty = invoiced_qty;
    }

    public String getProduct ()
    {
        return product;
    }

    public void setProduct (String product)
    {
        this.product = product;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getRate ()
    {
        return rate;
    }

    public void setRate (String rate)
    {
        this.rate = rate;
    }

    public String getProduct_title ()
    {
        return product_title;
    }

    public void setProduct_title (String product_title)
    {
        this.product_title = product_title;
    }

    public String getProduct_category ()
    {
        return product_category;
    }

    public void setProduct_category (String product_category)
    {
        this.product_category = product_category;
    }

    public String getPending_quantity ()
    {
        return pending_quantity;
    }

    public void setPending_quantity (String pending_quantity)
    {
        this.pending_quantity = pending_quantity;
    }

    public String getSales_order ()
    {
        return sales_order;
    }

    public void setSales_order (String sales_order)
    {
        this.sales_order = sales_order;
    }

    public String getProduct_sku ()
    {
        return product_sku;
    }

    public void setProduct_sku (String product_sku)
    {
        this.product_sku = product_sku;
    }

    public String getProduct_image ()
    {
        return product_image;
    }

    public void setProduct_image (String product_image)
    {
        this.product_image = product_image;
    }

    public String getQuantity ()
    {
        return quantity;
    }

    public void setQuantity (String quantity)
    {
        this.quantity = quantity;
    }

    public String getProduct_catalog() {
        return product_catalog;
    }

    public void setProduct_catalog(String product_catalog) {
        this.product_catalog = product_catalog;
    }

    public String getPacking_type() {
        return packing_type;
    }

    public void setPacking_type(String packing_type) {
        this.packing_type = packing_type;
    }

    public ArrayList<Item_ratings> getItem_ratings() {
        return item_ratings;
    }

    public void setItem_ratings(ArrayList<Item_ratings> item_ratings) {
        this.item_ratings = item_ratings;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [dispatched_qty = "+dispatched_qty+", canceled_qty = "+canceled_qty+", invoiced_qty = "+invoiced_qty+", product = "+product+", id = "+id+", rate = "+rate+", product_title = "+product_title+", product_category = "+product_category+", pending_quantity = "+pending_quantity+", sales_order = "+sales_order+", product_sku = "+product_sku+", product_image = "+product_image+", quantity = "+quantity+"]";
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
