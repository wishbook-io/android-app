package com.wishbook.catalog.Utils.multipleimageselect.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Vignesh on 4/18/2015.
 */
public class Image implements Parcelable {
    public long id;
    public String name;
    public String path;
    public String fabric;
    public String work;
    public String price;
    public int sort_order;
    public String public_price;
    public boolean isSelected;
    public String photo_id;
    public String thumbnail_small;
    public ArrayList<String> available_sizes;
    public boolean isDisabled;





    ArrayList<Image> more_images;

    // Client side field
    private boolean isAddMarginPriceSelected;
    private String add_margin;
    public boolean isDefaultCatalogImage;

    public Image() {
    }

    public Image(long id, String name, String path, String price, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.price=price;
        this.isSelected = isSelected;
    }



    public Image(String photo_id, String thumbnail_small) {
        this.photo_id = photo_id;
        this.thumbnail_small = thumbnail_small;
    }

    public Image(ArrayList<String> available_sizes) {
        this.available_sizes = available_sizes;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    public String getPublic_price() {
        return public_price;
    }

    public void setPublic_price(String public_price) {
        this.public_price = public_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getThumbnail_small() {
        return thumbnail_small;
    }

    public void setThumbnail_small(String thumbnail_small) {
        this.thumbnail_small = thumbnail_small;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isAddMarginPriceSelected() {
        return isAddMarginPriceSelected;
    }

    public void setAddMarginPriceSelected(boolean addMarginPriceSelected) {
        isAddMarginPriceSelected = addMarginPriceSelected;
    }

    public String getAdd_margin() {
        return add_margin;
    }

    public void setAdd_margin(String add_margin) {
        this.add_margin = add_margin;
    }

    public ArrayList<String> getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(ArrayList<String> available_sizes) {
        this.available_sizes = available_sizes;
    }

    public ArrayList<Image> getMore_images() {
        return more_images;
    }

    public void setMore_images(ArrayList<Image> more_images) {
        this.more_images = more_images;
    }

    public boolean isDefaultCatalogImage() {
        return isDefaultCatalogImage;
    }

    public void setDefaultCatalogImage(boolean defaultCatalogImage) {
        isDefaultCatalogImage = defaultCatalogImage;
    }


    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(price);
    }


    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        price = in.readString();
    }
}
