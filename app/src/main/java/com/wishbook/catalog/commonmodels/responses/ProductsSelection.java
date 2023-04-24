package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.SellingCompany;

/**
 * Created by root on 30/1/17.
 */
public class ProductsSelection {
    private String work;

    private String id;

    private String title;

    private String price;

    private String catalog;

    private String eav;

    private Image image;

    private String fabric;

    private String sku;

    private int selling_company_id;

    private SellingCompany[] selling_company;

    private String quantity;


    public int getSelling_company_id() {
        return selling_company_id;
    }

    public void setSelling_company_id(int selling_company_id) {
        this.selling_company_id = selling_company_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getWork ()
    {
        return work;
    }

    public void setWork (String work)
    {
        this.work = work;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getPrice ()
    {
        return price;
    }

    public void setPrice (String price)
    {
        this.price = price;
    }

    public String getCatalog ()
    {
        return catalog;
    }

    public void setCatalog (String catalog)
    {
        this.catalog = catalog;
    }

    public String getEav ()
    {
        return eav;
    }

    public void setEav (String eav)
    {
        this.eav = eav;
    }

    public Image getImage ()
    {
        return image;
    }

    public void setImage (Image image)
    {
        this.image = image;
    }

    public String getFabric ()
    {
        return fabric;
    }

    public void setFabric (String fabric)
    {
        this.fabric = fabric;
    }

    public String getSku ()
    {
        return sku;
    }

    public void setSku (String sku)
    {
        this.sku = sku;
    }

    public SellingCompany[] getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(SellingCompany[] selling_company) {
        this.selling_company = selling_company;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [work = "+work+", id = "+id+", title = "+title+", price = "+price+", catalog = "+catalog+", eav = "+eav+", image = "+image+", fabric = "+fabric+", sku = "+sku+", selling_company = "+selling_company+"]";
    }
}
