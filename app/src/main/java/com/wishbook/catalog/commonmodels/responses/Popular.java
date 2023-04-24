package com.wishbook.catalog.commonmodels.responses;

public class Popular
{
    private String title;

    private String order_quantity;

    private String likes;

    private String sku;

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getOrder_quantity ()
    {
        return order_quantity;
    }

    public void setOrder_quantity (String order_quantity)
    {
        this.order_quantity = order_quantity;
    }

    public String getLikes ()
    {
        return likes;
    }

    public void setLikes (String likes)
    {
        this.likes = likes;
    }

    public String getSku ()
    {
        return sku;
    }

    public void setSku (String sku)
    {
        this.sku = sku;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [title = " + title + ", order_quantity = " + order_quantity + ", likes = " + likes + ", Design No = " + sku + "]";
    }
}