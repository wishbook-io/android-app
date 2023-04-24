package com.wishbook.catalog.commonmodels.responses;

public class Last_shared
{
    private String people_viewed;

    private String name;

    private String product_viewed;

    public String getPeople_viewed ()
    {
        return people_viewed;
    }

    public void setPeople_viewed (String people_viewed)
    {
        this.people_viewed = people_viewed;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getProduct_viewed ()
    {
        return product_viewed;
    }

    public void setProduct_viewed (String product_viewed)
    {
        this.product_viewed = product_viewed;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [people_viewed = "+people_viewed+", name = "+name+", product_viewed = "+product_viewed+"]";
    }
}