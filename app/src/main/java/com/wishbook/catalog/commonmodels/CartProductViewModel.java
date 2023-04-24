package com.wishbook.catalog.commonmodels;

public class CartProductViewModel {


    private String sort_order;
    private String public_price;
    private String eav;
    private Image image;
    private String catalog;
    private String price;
    private String title;
    private String sku;
    private String id;
    private String fabric;
    private String work;

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getPublic_price() {
        return public_price;
    }

    public void setPublic_price(String public_price) {
        this.public_price = public_price;
    }

    public String getEav() {
        return eav;
    }

    public void setEav(String eav) {
        this.eav = eav;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Image {
        private String thumbnail_small;
        private String thumbnail_medium;
        private String full_size;

        public String getThumbnail_small() {
            return thumbnail_small;
        }

        public void setThumbnail_small(String thumbnail_small) {
            this.thumbnail_small = thumbnail_small;
        }

        public String getThumbnail_medium() {
            return thumbnail_medium;
        }

        public void setThumbnail_medium(String thumbnail_medium) {
            this.thumbnail_medium = thumbnail_medium;
        }

        public String getFull_size() {
            return full_size;
        }

        public void setFull_size(String full_size) {
            this.full_size = full_size;
        }
    }
}
