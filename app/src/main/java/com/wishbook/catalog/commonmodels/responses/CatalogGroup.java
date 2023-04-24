package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by tech4 on 26/10/17.
 */

public class CatalogGroup {

    private String product_catalog;

    private String pieces;

    private String total;

    private String pkg_type;

    public CatalogGroup(String product_catalog, String pieces) {
        this.product_catalog = product_catalog;
        this.pieces = pieces;
    }

    public String getProduct_catalog() {
        return product_catalog;
    }

    public void setProduct_catalog(String product_catalog) {
        this.product_catalog = product_catalog;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPkg_type() {
        return pkg_type;
    }

    public void setPkg_type(String pkg_type) {
        this.pkg_type = pkg_type;
    }

    @Override
    public boolean equals(Object o) {
        return ((CatalogGroup) o).getProduct_catalog().equals(getProduct_catalog());

    }
}
