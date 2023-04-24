package com.wishbook.catalog.commonmodels;

/**
 * Created by root on 17/3/17.
 */
public class Enquiry_Details {

    private String catalog;

    private String references;

    private String transfer_id;

    public String getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(String transfer_id) {
        this.transfer_id = transfer_id;
    }

    public String getRefrences() {
        return references;
    }

    public void setRefrences(String refrences) {
        this.references = refrences;
    }

    public String getCatalog ()
    {
        return catalog;
    }

    public void setCatalog (String catalog)
    {
        this.catalog = catalog;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [catalog = "+catalog+"]";
    }
}
