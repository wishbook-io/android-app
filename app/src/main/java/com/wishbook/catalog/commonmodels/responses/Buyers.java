package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 11/11/16.
 */
public class Buyers
{
    private String total_products_viewed;

    private Downstream_stats downstream_stats;

    private String push_downstream;

    private String buying_company_name;

    public String getTotal_products_viewed ()
    {
        return total_products_viewed;
    }

    public void setTotal_products_viewed (String total_products_viewed)
    {
        this.total_products_viewed = total_products_viewed;
    }

    public Downstream_stats getDownstream_stats ()
    {
        return downstream_stats;
    }

    public void setDownstream_stats (Downstream_stats downstream_stats)
    {
        this.downstream_stats = downstream_stats;
    }

    public String getPush_downstream ()
    {
        return push_downstream;
    }

    public void setPush_downstream (String push_downstream)
    {
        this.push_downstream = push_downstream;
    }

    public String getBuying_company_name ()
    {
        return buying_company_name;
    }

    public void setBuying_company_name (String buying_company_name)
    {
        this.buying_company_name = buying_company_name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_products_viewed = "+total_products_viewed+", downstream_stats = "+downstream_stats+", push_downstream = "+push_downstream+", buying_company_name = "+buying_company_name+"]";
    }
}