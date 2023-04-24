package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.home.models.ThumbnailObj;

public class CatalogMinified implements Comparable<CatalogMinified> {
    private String id;
    private String brand;
    private String brand_image;
    private String title;
    private String full_catalog_orders_only;
    private ThumbnailObj thumbnail;
    private String type;
    private String brand_name;
    private String total_products;
    private String is_disable;
    private String is_viewed;
    private String exp_desp_date;
    private String view_permission;
    private int max_sort_order;
    private boolean is_product_price_null;
    private String price_range;
    private String supplier;
    private String supplier_name;
    private String supplier_chat_user;
    private boolean is_supplier_approved;
    private String is_brand_followed;
    private boolean is_trusted_seller;
    private boolean supplier_disabled;
    private boolean buyer_disabled;
    private String supplier_id;
    private int near_by_sellers;
    String push_user_id;
    boolean fromPublic;
    boolean fromReceived;
    private String created_at;
    private String single_piece_price;

    private String is_addedto_wishlist;



    private Eavdata eavdata;

    private int public_count;

    private boolean is_owner;

    private String product_type;
    private String catalog_title;
    private String no_of_pcs_per_design;
    private String price_per_design;

    private Response_catalog.Supplier_details supplier_details;

    private Response_catalog.Supplier_company_rating supplier_company_rating;

    public boolean isFromPublic() {
        return fromPublic;
    }

    public void setFromPublic(boolean fromPublic) {
        this.fromPublic = fromPublic;
    }

    public boolean isFromReceived() {
        return fromReceived;
    }

    public void setFromReceived(boolean fromReceived) {
        this.fromReceived = fromReceived;
    }

    public boolean getIs_supplier_approved() {
        return is_supplier_approved;
    }

    public void setIs_supplier_approved(boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
    }

    public CatalogMinified(Boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
    }

    public String getSupplier() {
        return supplier;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public boolean getIs_product_price_null() {
        return is_product_price_null;
    }

    public void setIs_product_price_null(boolean is_product_price_null) {
        this.is_product_price_null = is_product_price_null;
    }

    public String getView_permission() {
        return view_permission;
    }

    public void setView_permission(String view_permission) {
        this.view_permission = view_permission;
    }

    public String getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(String is_viewed) {
        this.is_viewed = is_viewed;
    }

    public String getSingle_piece_price() {
        return single_piece_price;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getCatalog_title() {
        return catalog_title;
    }

    public void setCatalog_title(String catalog_title) {
        this.catalog_title = catalog_title;
    }

    public String getNo_of_pcs_per_design() {
        return no_of_pcs_per_design;
    }

    public void setNo_of_pcs_per_design(String no_of_pcs_per_design) {
        this.no_of_pcs_per_design = no_of_pcs_per_design;
    }

    public String getPrice_per_design() {
        return price_per_design;
    }

    public void setPrice_per_design(String price_per_design) {
        this.price_per_design = price_per_design;
    }

    public void setSingle_piece_price(String single_piece_price) {
        this.single_piece_price = single_piece_price;
    }

    public CatalogMinified(String id, String type, String full_catalog_orders_only, boolean buyer_disabled) {
        this.id = id;
        this.type = type;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.buyer_disabled = buyer_disabled;
    }
    public CatalogMinified(String id, String type, String full_catalog_orders_only,boolean buyer_disabled,String is_viewed,String brand_name,String exp_desp_date) {
        this.id = id;
        this.type = type;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.buyer_disabled = buyer_disabled;
        this.exp_desp_date=exp_desp_date;
    }

    public String getSupplier_chat_user() {
        return supplier_chat_user;
    }

    public void setSupplier_chat_user(String supplier_chat_user) {
        this.supplier_chat_user = supplier_chat_user;
    }

    public String getExp_desp_date() {
        return exp_desp_date;
    }

    public void setExp_desp_date(String exp_desp_date) {
        this.exp_desp_date = exp_desp_date;
    }

    public CatalogMinified(String id, String type, String full_catalog_orders_only, boolean buyer_disabled, String title) {
        this.id = id;
        this.type = type;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.buyer_disabled = buyer_disabled;
        this.title=title;
    }

    public CatalogMinified(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public CatalogMinified(String id, String type, boolean buyer_disabled,String title,String brand_name,String view_permission) {
        this.id = id;
        this.type = type;
        this.buyer_disabled=buyer_disabled;
        this.title=title;
        this.brand_name=brand_name;
        this.view_permission=view_permission;
    }

    public CatalogMinified(String id, String type, boolean buyer_disabled) {
        this.id = id;
        this.type = type;
        this.buyer_disabled=buyer_disabled;

    }
    public CatalogMinified(){

    }

    public String getIs_disable() {
        return is_disable;
    }

    public void setIs_disable(String is_disable) {
        this.is_disable = is_disable;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getBrand_image() {
        return brand_image;
    }

    public void setBrand_image(String brand_image) {
        this.brand_image = brand_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_catalog_orders_only() {
        return full_catalog_orders_only;
    }

    public void setFull_catalog_orders_only(String full_catalog_orders_only) {
        this.full_catalog_orders_only = full_catalog_orders_only;
    }

    public ThumbnailObj getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailObj thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
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

    public boolean getSupplier_disabled() {
        return supplier_disabled;
    }

    public void setSupplier_disabled(boolean supplier_disabled) {
        this.supplier_disabled = supplier_disabled;
    }

    public boolean getBuyer_disabled() {
        return buyer_disabled;
    }

    public void setBuyer_disabled(boolean buyer_disabled) {
        this.buyer_disabled = buyer_disabled;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Eavdata getEavdata() {
        return eavdata;
    }

    public void setEavdata(Eavdata eavdata) {
        this.eavdata = eavdata;
    }

    public int getNear_by_sellers() {
        return near_by_sellers;
    }

    public void setNear_by_sellers(int near_by_sellers) {
        this.near_by_sellers = near_by_sellers;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", brand_image = " + brand_image + ", title = " + title + ", full_catalog_orders_only = " + full_catalog_orders_only + ",  type = " + type + ", brand_name = " + brand_name + ", total_products = " + total_products + "]";
    }


    public boolean is_product_price_null() {
        return is_product_price_null;
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

    public Response_catalog.Supplier_details getSupplier_details() {
        return supplier_details;
    }

    public void setSupplier_details(Response_catalog.Supplier_details supplier_details) {
        this.supplier_details = supplier_details;
    }

    public boolean is_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public String getPush_user_id() {
        return push_user_id;
    }

    public void setPush_user_id(String push_user_id) {
        this.push_user_id = push_user_id;
    }

    public String getIs_addedto_wishlist() {
        return is_addedto_wishlist;
    }

    public void setIs_addedto_wishlist(String is_addedto_wishlist) {
        this.is_addedto_wishlist = is_addedto_wishlist;
    }

    @Override
    public int compareTo(CatalogMinified another) {
        return this.getIs_viewed().toString().compareTo(another.getIs_viewed().toString());
    }
}
