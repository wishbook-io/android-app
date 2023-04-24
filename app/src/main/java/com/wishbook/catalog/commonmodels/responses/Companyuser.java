package com.wishbook.catalog.commonmodels.responses;

public class Companyuser
{
    private String brand_added_flag;

    private String id;

    private String username;

    private String company;

    private String company_type;

    private String user;

    private String companyname;

    public String getBrand_added_flag ()
    {
        return brand_added_flag;
    }

    public void setBrand_added_flag (String brand_added_flag)
    {
        this.brand_added_flag = brand_added_flag;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    public String getCompany ()
    {
        return company;
    }

    public void setCompany (String company)
    {
        this.company = company;
    }

    public String getCompany_type ()
    {
        return company_type;
    }

    public void setCompany_type (String company_type)
    {
        this.company_type = company_type;
    }

    public String getUser ()
    {
        return user;
    }

    public void setUser (String user)
    {
        this.user = user;
    }

    public String getCompanyname ()
    {
        return companyname;
    }

    public void setCompanyname (String companyname)
    {
        this.companyname = companyname;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [brand_added_flag = "+brand_added_flag+", id = "+id+", username = "+username+", company = "+company+", company_type = "+company_type+", user = "+user+", companyname = "+companyname+"]";
    }
}

	