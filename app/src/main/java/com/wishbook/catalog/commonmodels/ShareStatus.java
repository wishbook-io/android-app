package com.wishbook.catalog.commonmodels;

/**
 * Created by Vigneshkarnika on 19/03/16.
 */
public class ShareStatus {
    String image;
    String catalogName;
    String noofproducts;
    String noofpeople;
    String noofopened;
    String sharestatus;

    public ShareStatus(String image, String catalogName, String noofproducts, String noofpeople, String noofopened, String sharestatus) {
        this.image = image;
        this.catalogName = catalogName;
        this.noofproducts = noofproducts;
        this.noofpeople = noofpeople;
        this.noofopened = noofopened;
        this.sharestatus = sharestatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getNoofproducts() {
        return noofproducts;
    }

    public void setNoofproducts(String noofproducts) {
        this.noofproducts = noofproducts;
    }

    public String getNoofpeople() {
        return noofpeople;
    }

    public void setNoofpeople(String noofpeople) {
        this.noofpeople = noofpeople;
    }

    public String getNoofopened() {
        return noofopened;
    }

    public void setNoofopened(String noofopened) {
        this.noofopened = noofopened;
    }

    public String getSharestatus() {
        return sharestatus;
    }

    public void setSharestatus(String sharestatus) {
        this.sharestatus = sharestatus;
    }
}
