package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 17/3/17.
 */
public class Send_Enquiry
{
    private String buyer_type;

    private Enquiry_Details details;

    public String getBuyer_type ()
    {
        return buyer_type;
    }

    public void setBuyer_type (String buyer_type)
    {
        this.buyer_type = buyer_type;
    }

    public Enquiry_Details getDetails ()
    {
        return details;
    }

    public void setDetails (Enquiry_Details details)
    {
        this.details = details;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [buyer_type = "+buyer_type+", details = "+details+"]";
    }
}
