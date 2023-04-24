package com.wishbook.catalog.commonmodels.responses;

public class SalesInfo
{
    private Current_week current_week;

    private String name;

    private Yesterday yesterday;

    private Today today;

    public Current_week getCurrent_week ()
    {
        return current_week;
    }

    public void setCurrent_week (Current_week current_week)
    {
        this.current_week = current_week;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Yesterday getYesterday ()
    {
        return yesterday;
    }

    public void setYesterday (Yesterday yesterday)
    {
        this.yesterday = yesterday;
    }

    public Today getToday ()
    {
        return today;
    }

    public void setToday (Today today)
    {
        this.today = today;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [current_week = "+current_week+", name = "+name+", yesterday = "+yesterday+", today = "+today+"]";
    }
}