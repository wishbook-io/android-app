package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class OpeningStockManagmentPost {

    private String warehouse;
    private String date;
    private String remark;
    private OpeningAddStock[] openingstockqty_set;

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public OpeningAddStock[] getOpeningstockqty_set() {
        return openingstockqty_set;
    }

    public void setOpeningstockqty_set(OpeningAddStock[] openingstockqty_set) {
        this.openingstockqty_set = openingstockqty_set;
    }
}
