package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 11/11/16.
 */
public class Products
{
    private String total_shares;

    private String total_buyers_viewed;

    private String sku;

    public String getTotal_shares ()
    {
        return total_shares;
    }

    public void setTotal_shares (String total_shares)
    {
        this.total_shares = total_shares;
    }

    public String getTotal_buyers_viewed ()
    {
        return total_buyers_viewed;
    }

    public void setTotal_buyers_viewed (String total_buyers_viewed)
    {
        this.total_buyers_viewed = total_buyers_viewed;
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
        return "ClassPojo [total_shares = "+total_shares+", total_buyers_viewed = "+total_buyers_viewed+", sku = "+sku+"]";
    }
}