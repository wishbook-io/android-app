package com.wishbook.catalog.commonmodels.postpatchmodels;

import java.util.ArrayList;

/**
 * Created by root on 14/7/17.
 */
public class ResendBuyerID {
    ArrayList<String> buyers;

    public ResendBuyerID(ArrayList<String> ids) {
        this.buyers = ids;
    }

    public ArrayList<String> getIds() {
        return buyers;
    }

    public void setIds(ArrayList<String> ids) {
        this.buyers = ids;
    }
}
