package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 11/11/16.
 */
public class Downstream_stats
{
    private String total_products_viewed;

    private String total_buyers;

    public String getTotal_products_viewed ()
    {
        return total_products_viewed;
    }

    public void setTotal_products_viewed (String total_products_viewed)
    {
        this.total_products_viewed = total_products_viewed;
    }

    public String getTotal_buyers ()
    {
        return total_buyers;
    }

    public void setTotal_buyers (String total_buyers)
    {
        this.total_buyers = total_buyers;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_products_viewed = "+total_products_viewed+", total_buyers = "+total_buyers+"]";
    }
}