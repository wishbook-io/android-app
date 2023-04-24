package com.wishbook.catalog.commonmodels.responses;

public class Current_week
{
    private String order_value;

    private String orders;

    private String visits;

    public String getOrder_value ()
    {
        return order_value;
    }

    public void setOrder_value (String order_value)
    {
        this.order_value = order_value;
    }

    public String getOrders ()
    {
        return orders;
    }

    public void setOrders (String orders)
    {
        this.orders = orders;
    }

    public String getVisits ()
    {
        return visits;
    }

    public void setVisits (String visits)
    {
        this.visits = visits;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [order_value = "+order_value+", orders = "+orders+", visits = "+visits+"]";
    }
}