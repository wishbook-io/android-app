package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class Response_catalogMini implements Serializable {
    private String id;
    private String brand;
    private String title;
    private ThumbnailObj thumbnail;
    private String view_permission;
    private String category;
    private String category_name;
    private String company;
    private String total_products;
    private String push_user_id;
    private String full_catalog_orders_only;
    private String sell_full_catalog;
    private String is_disable;
    private String brand_name;
    private String brand_image;
    private Boolean Expanded;
    private String supplier;
    private String supplier_name;
    private int max_sort_order;
    private Boolean is_product_price_null;
    private boolean is_supplier_approved;
    private String price_range;
    private String supplier_chat_user;
    private String is_brand_followed;
    private boolean is_trusted_seller;
    private boolean buyer_disabled;
    private boolean supplier_disabled;
    private String supplier_id;
    private int public_count;
    private boolean is_owner;
    private String is_addedto_wishlist;
    private boolean ready_to_ship;
    private Image image;
    private String catalog_set_type;
    private String catalog_multi_set_type;
    private String product_type;
    private String product_id;
    private String single_piece_price_range;
    private String catalog_title;
    private String available_sizes;
    private String catalog_id;
    private String single_piece_price;
    private String live_stats;
    private double mwp_single_price;
    private double full_discount;
    private double single_discount;

    /**
     * currently data come in product type single,other type data will be zero
     */
    private double price_per_design_with_gst;
    private double shipping_charges;




    private CustomViewModel customViewModel;

    private Shared_details shared_details;

    // client side variable
    private ArrayList<String> available_sizes_array;
    private String resale_price;


    private Response_catalog.Supplier_company_rating supplier_company_rating;
    private Response_catalog.Supplier_details supplier_details;

    public Response_catalog.Supplier_details getSupplier_details() {
        return supplier_details;
    }

    public void setSupplier_details(Response_catalog.Supplier_details supplier_details) {
        this.supplier_details = supplier_details;
    }

    public String getSupplier_chat_user() {
        return supplier_chat_user;
    }

    public void setSupplier_chat_user(String supplier_chat_user) {
        this.supplier_chat_user = supplier_chat_user;
    }

    public boolean getIs_supplier_approved() {
        return is_supplier_approved;
    }

    public void setIs_supplier_approved(boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
    }

    public String getSupplier() {
        return supplier;
    }

    public Response_catalogMini() {

    }

    public String getCategory_name() {
        return category_name;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public int getMax_sort_order() {
        return max_sort_order;
    }

    public void setMax_sort_order(int max_sort_order) {
        this.max_sort_order = max_sort_order;
    }

    public Boolean getIs_product_price_null() {
        return is_product_price_null;
    }

    public void setIs_product_price_null(Boolean is_product_price_null) {
        this.is_product_price_null = is_product_price_null;
    }

    public boolean isReady_to_ship() {
        return ready_to_ship;
    }

    public void setReady_to_ship(boolean ready_to_ship) {
        this.ready_to_ship = ready_to_ship;
    }

    public Boolean getExpanded() {
        return Expanded;
    }

    public void setExpanded(Boolean expanded) {
        Expanded = expanded;
    }

    public String getLive_stats() {
        return live_stats;
    }

    public void setLive_stats(String live_stats) {
        this.live_stats = live_stats;
    }

    public Response_catalogMini(String id, String brand, String title, ThumbnailObj thumbnail, String view_permission, String category, String company, String picasa_url, String picasa_id, String total_products, String push_user_id, String full_catalog_orders_only, String sell_full_catalog, String is_disable) {
        this.id = id;
        this.brand = brand;
        this.title = title;
        this.thumbnail = thumbnail;
        this.view_permission = view_permission;
        this.category = category;
        this.company = company;

        this.total_products = total_products;
        this.push_user_id = push_user_id;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.sell_full_catalog = sell_full_catalog;
        this.is_disable = is_disable;
    }

    public Response_catalogMini(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getBrand_image() {
        return brand_image;
    }

    public void setBrand_image(String brand_image) {
        this.brand_image = brand_image;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ThumbnailObj getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailObj thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getCatalog_set_type() {
        return catalog_set_type;
    }

    public void setCatalog_set_type(String catalog_set_type) {
        this.catalog_set_type = catalog_set_type;
    }

    public String getCatalog_multi_set_type() {
        return catalog_multi_set_type;
    }

    public void setCatalog_multi_set_type(String catalog_multi_set_type) {
        this.catalog_multi_set_type = catalog_multi_set_type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getView_permission() {
        return view_permission;
    }

    public void setView_permission(String view_permission) {
        this.view_permission = view_permission;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }



    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getPush_user_id() {
        return push_user_id;
    }

    public void setPush_user_id(String push_user_id) {
        this.push_user_id = push_user_id;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }

    public String getSell_full_catalog() {
        return sell_full_catalog;
    }

    public void setSell_full_catalog(String sell_full_catalog) {
        this.sell_full_catalog = sell_full_catalog;
    }

    public String getIs_disable() {
        return is_disable;
    }

    public void setIs_disable(String is_disable) {
        this.is_disable = is_disable;
    }

    public String getIs_brand_followed() {
        return is_brand_followed;
    }

    public void setIs_brand_followed(String is_brand_followed) {
        this.is_brand_followed = is_brand_followed;
    }

    public boolean getIs_trusted_seller() {
        return is_trusted_seller;
    }

    public void setIs_trusted_seller(boolean is_trusted_seller) {
        this.is_trusted_seller = is_trusted_seller;
    }

    public boolean isBuyer_disabled() {
        return buyer_disabled;
    }

    public void setBuyer_disabled(boolean buyer_disabled) {
        this.buyer_disabled = buyer_disabled;
    }

    public boolean isSupplier_disabled() {
        return supplier_disabled;
    }

    public void setSupplier_disabled(boolean supplier_disabled) {
        this.supplier_disabled = supplier_disabled;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getPublic_count() {
        return public_count;
    }

    public void setPublic_count(int public_count) {
        this.public_count = public_count;
    }

    public Response_catalog.Supplier_company_rating getSupplier_company_rating() {
        return supplier_company_rating;
    }

    public void setSupplier_company_rating(Response_catalog.Supplier_company_rating supplier_company_rating) {
        this.supplier_company_rating = supplier_company_rating;
    }

    public boolean is_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public String getIs_addedto_wishlist() {
        return is_addedto_wishlist;
    }

    public void setIs_addedto_wishlist(String is_addedto_wishlist) {
        this.is_addedto_wishlist = is_addedto_wishlist;
    }

    public CustomViewModel getCustomViewModel() {
        return customViewModel;
    }

    public void setCustomViewModel(CustomViewModel customViewModel) {
        this.customViewModel = customViewModel;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSingle_piece_price_range() {
        return single_piece_price_range;
    }

    public void setSingle_piece_price_range(String single_piece_price_range) {
        this.single_piece_price_range = single_piece_price_range;
    }

    public Shared_details getShared_details() {
        return shared_details;
    }

    public void setShared_details(Shared_details shared_details) {
        this.shared_details = shared_details;
    }

    public String getCatalog_title() {
        return catalog_title;
    }

    public void setCatalog_title(String catalog_title) {
        this.catalog_title = catalog_title;
    }

    public String getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(String available_sizes) {
        this.available_sizes = available_sizes;
    }

    public ArrayList<String> getAvailable_sizes_array() {
        return available_sizes_array;
    }

    public void setAvailable_sizes_array(ArrayList<String> available_sizes_array) {
        this.available_sizes_array = available_sizes_array;
    }

    public String getCatalog_id() {
        return catalog_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
    }

    public String getSingle_piece_price() {
        return single_piece_price;
    }

    public void setSingle_piece_price(String single_piece_price) {
        this.single_piece_price = single_piece_price;
    }


    public String getResale_price() {
        return resale_price;
    }

    public void setResale_price(String resale_price) {
        this.resale_price = resale_price;
    }

    public double getPrice_per_design_with_gst() {
        return price_per_design_with_gst;
    }

    public void setPrice_per_design_with_gst(double price_per_design_with_gst) {
        this.price_per_design_with_gst = price_per_design_with_gst;
    }

    public double getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(double shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public double getMwp_single_price() {
        return mwp_single_price;
    }

    public void setMwp_single_price(double mwp_single_price) {
        this.mwp_single_price = mwp_single_price;
    }

    public double getFull_discount() {
        return full_discount;
    }

    public void setFull_discount(double full_discount) {
        this.full_discount = full_discount;
    }

    public double getSingle_discount() {
        return single_discount;
    }

    public void setSingle_discount(double single_discount) {
        this.single_discount = single_discount;
    }

    @Override
    public boolean equals(Object o) {
        // for Activity_CompanyCatalog
        return ((Response_catalogMini) o).getId().equals(getId());

    }


}
