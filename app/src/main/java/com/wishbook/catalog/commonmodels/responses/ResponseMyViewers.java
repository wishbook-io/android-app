package com.wishbook.catalog.commonmodels.responses;

public class ResponseMyViewers {

    private String id;

    private String title;

    private String viewers;

    private String total_viewes;

    private Image image;

    private String brand_name;

    private boolean brand_i_own;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewers() {
        return viewers;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public String getTotal_viewes() {
        return total_viewes;
    }

    public void setTotal_viewes(String total_viewes) {
        this.total_viewes = total_viewes;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public boolean isBrand_i_own() {
        return brand_i_own;
    }

    public void setBrand_i_own(boolean brand_i_own) {
        this.brand_i_own = brand_i_own;
    }
}
