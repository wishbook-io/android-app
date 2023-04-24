package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 11/8/17.
 */

public class OrderInvoiceCreate {
    ArrayList<InvoiceItemSet> invoiceitem_set;
    String order;

    public ArrayList<InvoiceItemSet> getInvoiceitem_set() {
        return invoiceitem_set;
    }

    public void setInvoiceitem_set(ArrayList<InvoiceItemSet> invoiceitem_set) {
        this.invoiceitem_set = invoiceitem_set;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
