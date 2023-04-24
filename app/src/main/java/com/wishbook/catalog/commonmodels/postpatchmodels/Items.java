package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 10/8/17.
 */

public class Items
{
    private String total_amount;

    private String amount;

    //private String tax_class_2;

    private String id;

    //private String tax_class_1;

    private Order_item order_item;

    private String rate;

    //private String tax_value_1;

    private String invoice;

    private String qty;


    //private String tax_value_2;

    public String getTotal_amount ()
    {
        return total_amount;
    }

    public void setTotal_amount (String total_amount)
    {
        this.total_amount = total_amount;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    /*public String getTax_class_2 ()
{
    return tax_class_2;
}

    public void setTax_class_2 (String tax_class_2)
    {
        this.tax_class_2 = tax_class_2;
    }*/

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

   /* public String getTax_class_1 ()
{
    return tax_class_1;
}

    public void setTax_class_1 (String tax_class_1)
    {
        this.tax_class_1 = tax_class_1;
    }*/

    public Order_item getOrder_item ()
    {
        return order_item;
    }

    public void setOrder_item (Order_item order_item)
    {
        this.order_item = order_item;
    }

    public String getRate ()
    {
        return rate;
    }

    public void setRate (String rate)
    {
        this.rate = rate;
    }

    /*public String getTax_value_1 ()
    {
        return tax_value_1;
    }

    public void setTax_value_1 (String tax_value_1)
    {
        this.tax_value_1 = tax_value_1;
    }*/

    public String getInvoice ()
    {
        return invoice;
    }

    public void setInvoice (String invoice)
    {
        this.invoice = invoice;
    }

    public String getQty ()
    {
        return qty;
    }

    public void setQty (String qty)
    {
        this.qty = qty;
    }

    /*public String getTax_value_2 ()
    {
        return tax_value_2;
    }

    public void setTax_value_2 (String tax_value_2)
    {
        this.tax_value_2 = tax_value_2;
    }*/


}
			
