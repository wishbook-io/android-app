package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

/**
 * Created by Vigneshkarnika on 17/04/16.
 */
public class CheckInmodel {
    Response_Buyer buyer;
    String checkinstatus;
    long time = 0;

    public CheckInmodel(Response_Buyer buyer, String checkinstatus, long time) {
        this.buyer = buyer;
        this.checkinstatus = checkinstatus;
        this.time = time;
    }

    public Response_Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Response_Buyer buyer) {
        this.buyer = buyer;
    }

    public String getCheckinstatus() {
        return checkinstatus;
    }

    public void setCheckinstatus(String checkinstatus) {
        this.checkinstatus = checkinstatus;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
