package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Brand;
import com.wishbook.catalog.commonmodels.responses.Image;

import java.util.ArrayList;

public class ResponseStoryModel {

    private String id;

    private String image_ppoi;

    private ArrayList<Catalogs_details> catalogs_details;

    private int completion_count;

    private ArrayList<String> catalogs;

    private String name;

    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_ppoi() {
        return image_ppoi;
    }

    public void setImage_ppoi(String image_ppoi) {
        this.image_ppoi = image_ppoi;
    }

    public ArrayList<Catalogs_details> getCatalogs_details() {
        return catalogs_details;
    }

    public void setCatalogs_details(ArrayList<Catalogs_details> catalogs_details) {
        this.catalogs_details = catalogs_details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(ArrayList<String> catalogs) {
        this.catalogs = catalogs;
    }

    public int getCompletion_count() {
        return completion_count;
    }

    public void setCompletion_count(int completion_count) {
        this.completion_count = completion_count;
    }

    public class Catalogs_details {
        private String id;

        private String title;

        private Brand brand;

        private ArrayList<Products> products;

        private String product_id;

        private Thumbnail thumbnail;

        private boolean i_am_selling_this;

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


        public Brand getBrand() {
            return brand;
        }

        public void setBrand(Brand brand) {
            this.brand = brand;
        }

        public ArrayList<Products> getProducts() {
            return products;
        }

        public void setProducts(ArrayList<Products> products) {
            this.products = products;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public boolean isI_am_selling_this() {
            return i_am_selling_this;
        }

        public void setI_am_selling_this(boolean i_am_selling_this) {
            this.i_am_selling_this = i_am_selling_this;
        }
    }

    public class Thumbnail {
        private String thumbnail_medium;

        private String thumbnail_small;

        public String getThumbnail_medium() {
            return thumbnail_medium;
        }

        public void setThumbnail_medium(String thumbnail_medium) {
            this.thumbnail_medium = thumbnail_medium;
        }

        public String getThumbnail_small() {
            return thumbnail_small;
        }

        public void setThumbnail_small(String thumbnail_small) {
            this.thumbnail_small = thumbnail_small;
        }


    }


    public  class Products {
        private String id;

        private Image image;

        public Products(String id, Image image) {
            this.id = id;
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", image = " + image + "]";
        }
    }
}
