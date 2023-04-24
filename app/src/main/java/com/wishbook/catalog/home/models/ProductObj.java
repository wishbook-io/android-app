package com.wishbook.catalog.home.models;

import com.google.gson.annotations.SerializedName;
import com.wishbook.catalog.commonmodels.responses.Photos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class ProductObj implements Comparator<ProductObj>,Serializable {

    private String id;
    private String sku;
    private String title;
    private String fabric;
    private String work;
    CatalogObj catalog;
    private ThumbnailObj image;
    private String final_price;
    private String selling_price;
    private String price;
    private String public_price;

    private String push_user_product_id;
    private String product_likes;
    private String product_like_id;
    private String barcode;
    private String single_piece_price;
    private int  no_of_pcs;
    private String product_type;
    private boolean supplier_disabled;
    private boolean selling;

    private double mwp_price;
    private  double clearance_discount;
    private double single_discount;
    private double full_discount;
    private double mwp_single_price;


    // Client side field
    private boolean isAddMarginPriceSelected;
    private String add_margin;
    private String product_id;
    private boolean isChecked;
    // Client field end

    @SerializedName("available_sizes_array")
    private ArrayList<String> available_sizes;

    // set boolean to String , chnages required bulk-update-product-seller url
    private boolean is_enable;

    @SerializedName("available_sizes")
    private String available_size_string;

    private String resell_price;

    private String shared_on;
    private String actual_price;
    private boolean sell_full_catalog;
    private String public_price_with_gst;
    private String shipping_charges2;
    private String single_piece_price_with_gst;

    private boolean enabled;
    private ArrayList<Photos> photos;

    // client side variable
    private String note;
    private String catalog_id;
    private String quantity;

    public ProductObj() {
    }

    public ProductObj(String id, String price) {
        this.id = id;
        this.public_price = price;
    }

    /**
     * For Add To Cart Basic constructor
     * @param id
     * @param price
     */
    public ProductObj(String id, String price,String note) {
        this.id = id;
        this.public_price = price;
        this.note = note;
    }

    public ProductObj(@Nullable String id, @Nullable String sku, @Nullable String title, @Nullable String fabric, @Nullable String work, @Nullable CatalogObj catalog, @Nullable ThumbnailObj image, @Nullable String final_price, @Nullable String selling_price, @Nullable String price, @Nullable String push_user_product_id, @Nullable String product_likes, @Nullable String product_like_id) {
        this.id = id;
        this.sku = sku;
        this.title = title;
        this.fabric = fabric;
        this.work = work;
        this.catalog = catalog;
        this.image =  image;
        this.final_price = final_price;
        this.selling_price = selling_price;
        this.price = price;
        this.push_user_product_id = push_user_product_id;
        this.product_likes = product_likes;
        this.product_like_id = product_like_id;
    }




    public String getSingle_piece_price() {
        return single_piece_price;
    }

    public void setSingle_piece_price(String single_piece_price) {
        this.single_piece_price = single_piece_price;
    }
    @Nullable
    public String getPublic_price() {
        return public_price;
    }

    public void setPublic_price(@Nullable String public_price) {
        this.public_price = public_price;
    }
    @Nullable
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(@Nullable String barcode) {
        this.barcode = barcode;
    }
    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
    @Nullable
    public String getSku() {
        return sku;
    }

    public void setSku(@Nullable String sku) {
        this.sku = sku;
    }
    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }
    @Nullable
    public String getFabric() {
        return fabric;
    }

    public void setFabric(@Nullable String fabric) {
        this.fabric = fabric;
    }
    @Nullable
    public String getWork() {
        return work;
    }

    public void setWork(@Nullable String work) {
        this.work = work;
    }
    @Nullable
    public CatalogObj getCatalog() {
        return catalog;
    }

    public void setCatalog(@Nullable CatalogObj catalog) {
        this.catalog = catalog;
    }
    @Nullable
    public ThumbnailObj getImage() {
        return image;
    }

    public void setImage(@Nullable ThumbnailObj image) {
        this.image = image;
    }

    @Nullable
    public String getFinal_price() {
        return final_price;
    }

    public void setFinal_price(@Nullable String final_price) {
        this.final_price = final_price;
    }

    @Nullable
    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(@Nullable String selling_price) {
        this.selling_price = selling_price;
    }
    @Nullable
    public String getPrice() {
        return price;
    }

    public void setPrice(@Nullable String price) {
        this.price = price;
    }
    @Nullable
    public String getPush_user_product_id() {
        return push_user_product_id;
    }

    public void setPush_user_product_id(@Nullable String push_user_product_id) {
        this.push_user_product_id = push_user_product_id;
    }
    @Nullable
    public String getProduct_likes() {
        return product_likes;
    }

    public void setProduct_likes(@Nullable String product_likes) {
        this.product_likes = product_likes;
    }
    @Nullable
    public String getProduct_like_id() {
        return product_like_id;
    }

    public void setProduct_like_id(@Nullable String product_like_id) {
        this.product_like_id = product_like_id;
    }

    public int getNo_of_pcs() {
        return no_of_pcs;
    }

    public void setNo_of_pcs(int no_of_pcs) {
        this.no_of_pcs = no_of_pcs;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public boolean isSupplier_disabled() {
        return supplier_disabled;
    }

    public void setSupplier_disabled(boolean supplier_disabled) {
        this.supplier_disabled = supplier_disabled;
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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public ArrayList<String> getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(ArrayList<String> available_sizes) {
        this.available_sizes = available_sizes;
    }

    public String getAvailable_size_string() {
        return available_size_string;
    }

    public void setAvailable_size_string(String available_size_string) {
        this.available_size_string = available_size_string;
    }

    public boolean isIs_enable() {
        return is_enable;
    }

    public void setIs_enable(boolean is_enable) {
        this.is_enable = is_enable;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getResell_price() {
        return resell_price;
    }

    public void setResell_price(String resell_price) {
        this.resell_price = resell_price;
    }

    public String getShared_on() {
        return shared_on;
    }

    public void setShared_on(String shared_on) {
        this.shared_on = shared_on;
    }

    public boolean isSell_full_catalog() {
        return sell_full_catalog;
    }

    public void setSell_full_catalog(boolean sell_full_catalog) {
        this.sell_full_catalog = sell_full_catalog;
    }

    public String getPublic_price_with_gst() {
        return public_price_with_gst;
    }

    public void setPublic_price_with_gst(String public_price_with_gst) {
        this.public_price_with_gst = public_price_with_gst;
    }

    public String getShipping_charges() {
        return shipping_charges2;
    }

    public void setShipping_charges(String shipping_charges2) {
        this.shipping_charges2 = shipping_charges2;
    }

    public String getSingle_piece_price_with_gst() {
        return single_piece_price_with_gst;
    }

    public void setSingle_piece_price_with_gst(String single_piece_price_with_gst) {
        this.single_piece_price_with_gst = single_piece_price_with_gst;
    }

    public String getActual_price() {
        return actual_price;
    }

    public void setActual_price(String actual_price) {
        this.actual_price = actual_price;
    }

    public boolean isSelling() {
        return selling;
    }

    public void setSelling(boolean selling) {
        this.selling = selling;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCatalog_id() {
        return catalog_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photos> photos) {
        this.photos = photos;
    }

    public double getMwp_price() {
        return mwp_price;
    }

    public void setMwp_price(double mwp_price) {
        this.mwp_price = mwp_price;
    }

    public double getClearance_discount() {
        return clearance_discount;
    }

    public void setClearance_discount(double clearance_discount) {
        this.clearance_discount = clearance_discount;
    }

    public double getMwp_single_price() {
        return mwp_single_price;
    }

    public void setMwp_single_price(double mwp_single_price) {
        this.mwp_single_price = mwp_single_price;
    }

    public double getSingle_discount() {
        return single_discount;
    }

    public void setSingle_discount(double single_discount) {
        this.single_discount = single_discount;
    }

    public double getFull_discount() {
        return full_discount;
    }

    public void setFull_discount(double full_discount) {
        this.full_discount = full_discount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (((ProductObj) obj).getId().equals(getId()));
    }

    @Override
    public int compare(@Nullable ProductObj lhs, @Nullable ProductObj rhs) {
        return lhs.getFinal_price().compareTo(rhs.getFinal_price());
    }
}
