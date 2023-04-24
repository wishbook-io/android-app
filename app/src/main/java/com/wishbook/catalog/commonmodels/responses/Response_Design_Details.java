package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 11/11/16.
 */
public class Response_Design_Details
{
    private String catalog_name;

    private String id;

    private String total_products_viewed;

    private Products[] products;

    public String getCatalog_name ()
    {
        return catalog_name;
    }

    public void setCatalog_name (String catalog_name)
    {
        this.catalog_name = catalog_name;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTotal_products_viewed ()
    {
        return total_products_viewed;
    }

    public void setTotal_products_viewed (String total_products_viewed)
    {
        this.total_products_viewed = total_products_viewed;
    }

    public Products[] getProducts ()
    {
        return products;
    }

    public void setProducts (Products[] products)
    {
        this.products = products;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [catalog_name = "+catalog_name+", id = "+id+", total_products_viewed = "+total_products_viewed+", products = "+products+"]";
    }



}

