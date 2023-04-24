package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 28/05/16.
 */
public class SyncModel {

String syncUrl;
boolean syncstatus;
String syncTitle;

    public SyncModel(String syncUrl, boolean syncstatus, String syncTitle) {
        this.syncUrl = syncUrl;
        this.syncstatus = syncstatus;
        this.syncTitle = syncTitle;
    }

    public String getSyncUrl() {
        return syncUrl;
    }

    public void setSyncUrl(String syncUrl) {
        this.syncUrl = syncUrl;
    }

    public boolean isSyncstatus() {
        return syncstatus;
    }

    public void setSyncstatus(boolean syncstatus) {
        this.syncstatus = syncstatus;
    }

    public String getSyncTitle() {
        return syncTitle;
    }

    public void setSyncTitle(String syncTitle) {
        this.syncTitle = syncTitle;
    }
}
