package com.wishbook.catalog.commonmodels.postpatchmodels;

/**
 * Created by root on 14/7/17.
 */
public class InventoryManagmentPost {

    private String warehouse;
    private String date;
    private String remark;
    private InventoryAddStock[] inventoryadjustmentqty_set;

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

    public InventoryAddStock[] getInventoryadjustmentqty_set() {
        return inventoryadjustmentqty_set;
    }

    public void setInventoryadjustmentqty_set(InventoryAddStock[] inventoryadjustmentqty_set) {
        this.inventoryadjustmentqty_set = inventoryadjustmentqty_set;
    }
}
