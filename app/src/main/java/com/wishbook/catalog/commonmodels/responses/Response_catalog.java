package com.wishbook.catalog.commonmodels.responses;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vigneshkarnika on 26/03/16.
 */
public class Response_catalog implements ParentListItem {
    String id;
    Response_Brands brand;
    String title;
    ThumbnailObj thumbnail;
    ThumbnailObj image;
    String view_permission;
    String category;
    ArrayList<ProductObj> products;
    ArrayList<Photos> photos;
    String company;
    String total_products;
    String push_user_id;
    public String full_catalog_orders_only;
    String sell_full_catalog;
    boolean is_disable;
    ArrayList<ProductObj> objs;
    Boolean isExpanded;
    private String price_range;
    private String dispatch_date;

    String supplier;
    String supplier_chat_user;
    String supplier_name;
    private Boolean is_product_price_null;
    private boolean is_trusted_seller;
    private String supplier_id;
    private boolean buyer_disabled;


    private Eavdata eavdataOld;



    @SerializedName("eavdata")
    private JsonObject eavdatajson;


    private Supplier_details supplier_details;
    private Supplier_company_rating supplier_company_rating;


    private boolean is_owner;
    private boolean is_seller;
    private boolean is_currently_selling;
    private boolean is_public;
    boolean is_supplier_approved;
    private boolean supplier_disabled;
    private String single_piece_price_range;
    private String catalog_type;
    private boolean ready_to_ship;

    private String catalog_set_type;
    private String catalog_multi_set_type;
    private String catalog_id;
    private String product_type;
    private String catalog_title;
    private String no_of_pcs_per_design;
    private String price_per_design;
    private int  no_of_pcs;
    private String set_type_details;
    private String price_per_design_with_gst;
    private String shipping_charges;
    private double surface_shipping_charges;

    private String available_sizes;
    private boolean selling;
    private boolean i_am_selling_sell_full_catalog;
    private String bundle_product_id;

    private Product_rating product_rating;

    private String public_price;

    private String mwp_price_range;

    private double clearance_discount;

    private String photoshoot_type;

    private double mwp_single_price;

    private double full_discount;

    private double single_discount;

    public String getPublic_price() {
        return public_price;
    }

    public void setPublic_price(String public_price) {
        this.public_price = public_price;
    }

    public Product_rating getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(Product_rating product_rating) {
        this.product_rating = product_rating;
    }

    public boolean isSupplier_disabled() {
        return supplier_disabled;
    }

    public void setSupplier_disabled(boolean supplier_disabled) {
        this.supplier_disabled = supplier_disabled;
    }

    public String getDispatch_date() {
        return dispatch_date;
    }

    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    private String is_addedto_wishlist;
    private String created_at;
    private String enquiry_id;

    private String single_piece_price;

    private String single_piece_price_percentage;


    private String category_name;

    private ArrayList<MultipleSuppliers> suppliers;

    public String getSingle_piece_price() {
        return single_piece_price;
    }

    public void setSingle_piece_price(String single_piece_price) {
        this.single_piece_price = single_piece_price;
    }

    public String getSingle_piece_price_percentage() {
        return single_piece_price_percentage;
    }

    public void setSingle_piece_price_percentage(String single_piece_price_percentage) {
        this.single_piece_price_percentage = single_piece_price_percentage;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public Response_catalog(String id, Response_Brands brand, String title, ThumbnailObj thumbnail, String view_permission, String category, ArrayList<ProductObj> products, String company, String picasa_url, String picasa_id, String total_products, String push_user_id, String full_catalog_orders_only, String sell_full_catalog, boolean is_disable) {
        this.id = id;
        this.brand = brand;
        this.title = title;
        this.thumbnail = thumbnail;
        this.view_permission = view_permission;
        this.category = category;
        this.products = products;
        this.company = company;

        this.total_products = total_products;
        this.push_user_id = push_user_id;
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.sell_full_catalog = sell_full_catalog;
        this.is_disable = is_disable;
    }

    public Response_catalog() {
    }

    public String getCatalog_type() {
        return catalog_type;
    }

    public void setCatalog_type(String catalog_type) {
        this.catalog_type = catalog_type;
    }

    public boolean is_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public boolean is_seller() {
        return is_seller;
    }

    public void setIs_seller(boolean is_seller) {
        this.is_seller = is_seller;
    }

    public boolean is_currently_selling() {
        return is_currently_selling;
    }

    public void setIs_currently_selling(boolean is_currently_selling) {
        this.is_currently_selling = is_currently_selling;
    }

    public boolean is_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    public ArrayList<ProductObj> getObjs() {
        return objs;
    }

    public void setObjs(ArrayList<ProductObj> objs) {
        this.objs = objs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Response_Brands getBrand() {
        return brand;
    }

    public void setBrand(Response_Brands brand) {
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

    public ThumbnailObj getImage() {
        return image;
    }

    public void setImage(ThumbnailObj image) {
        this.image = image;
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

    public ArrayList<ProductObj> getProducts() {
        return products;
    }

    public ProductObj[] getProduct() {
        ProductObj[] productObjs = new ProductObj[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productObjs[i] = products.get(i);
        }
        return productObjs;
    }


    public void setProducts(ArrayList<ProductObj> products) {
        this.products = products;
    }

    public ArrayList<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photos> photos) {
        this.photos = photos;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getCatalog_id() {
        return catalog_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
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

    public boolean getIs_disable() {
        return is_disable;
    }

    public void setIs_disable(boolean is_disable) {
        this.is_disable = is_disable;
    }

    public String getSingle_piece_price_range() {
        return single_piece_price_range;
    }

    public void setSingle_piece_price_range(String single_piece_price_range) {
        this.single_piece_price_range = single_piece_price_range;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
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

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier_chat_user() {
        return supplier_chat_user;
    }

    public void setSupplier_chat_user(String supplier_chat_user) {
        this.supplier_chat_user = supplier_chat_user;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public Boolean getIs_product_price_null() {
        return is_product_price_null;
    }

    public boolean is_trusted_seller() {
        return is_trusted_seller;
    }

    public void setIs_trusted_seller(boolean is_trusted_seller) {
        this.is_trusted_seller = is_trusted_seller;
    }

    public void setIs_product_price_null(Boolean is_product_price_null) {
        this.is_product_price_null = is_product_price_null;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
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

    public String getPrice_per_design_with_gst() {
        return price_per_design_with_gst;
    }

    public void setPrice_per_design_with_gst(String price_per_design_with_gst) {
        this.price_per_design_with_gst = price_per_design_with_gst;
    }

    public String getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(String shipping_charges) {
        this.shipping_charges= shipping_charges;
    }

    public double getSurface_shipping_charges() {
        return surface_shipping_charges;
    }

    public void setSurface_shipping_charges(double surface_shipping_charges) {
        this.surface_shipping_charges = surface_shipping_charges;
    }

    @Override
    public List<ProductObj> getChildItemList() {
        return objs;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public boolean isBuyer_disabled() {
        return buyer_disabled;
    }

    public void setBuyer_disabled(boolean buyer_disabled) {
        this.buyer_disabled = buyer_disabled;
    }

    public Eavdata getEavdata() {
        if(eavdatajson!=null) {
            setEavdata(Application_Singleton.gson.fromJson(eavdatajson, Eavdata.class));
        }
        return eavdataOld;
    }

    public JsonObject getEavdatajson() {
        return eavdatajson;
    }

    public void setEavdata(Eavdata eavdata) {
        this.eavdataOld = eavdata;
    }

    public void setEavdatajson(JsonObject jsonObject) {
        this.eavdatajson = jsonObject;

    }

    public Supplier_details getSupplier_details() {
        return supplier_details;
    }

    public void setSupplier_details(Supplier_details supplier_details) {
        this.supplier_details = supplier_details;
    }

    public Supplier_company_rating getSupplier_company_rating() {
        return supplier_company_rating;
    }

    public void setSupplier_company_rating(Supplier_company_rating supplier_company_rating) {
        this.supplier_company_rating = supplier_company_rating;
    }

    public ArrayList<MultipleSuppliers> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(ArrayList<MultipleSuppliers> suppliers) {
        this.suppliers = suppliers;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIs_addedto_wishlist() {
        return is_addedto_wishlist;
    }

    public void setIs_addedto_wishlist(String is_addedto_wishlist) {
        this.is_addedto_wishlist = is_addedto_wishlist;
    }

    public String getEnquiry_id() {
        return enquiry_id;
    }

    public void setEnquiry_id(String enquiry_id) {
        this.enquiry_id = enquiry_id;
    }

    public boolean isReady_to_ship() {
        return ready_to_ship;
    }

    public void setReady_to_ship(boolean ready_to_ship) {
        this.ready_to_ship = ready_to_ship;
    }

    public int getNo_of_pcs() {
        return no_of_pcs;
    }

    public void setNo_of_pcs(int no_of_pcs) {
        this.no_of_pcs = no_of_pcs;
    }

    public String getSet_type_details() {
        return set_type_details;
    }

    public void setSet_type_details(String set_type_details) {
        this.set_type_details = set_type_details;
    }

    public String getAvailable_sizes() {
        return available_sizes;
    }

    public void setAvailable_sizes(String available_sizes) {
        this.available_sizes = available_sizes;
    }

    public boolean isSelling() {
        return selling;
    }

    public void setSelling(boolean selling) {
        this.selling = selling;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public boolean isI_am_selling_sell_full_catalog() {
        return i_am_selling_sell_full_catalog;
    }

    public void setI_am_selling_sell_full_catalog(boolean i_am_selling_sell_full_catalog) {
        this.i_am_selling_sell_full_catalog = i_am_selling_sell_full_catalog;
    }

    public String getBundle_product_id() {
        return bundle_product_id;
    }

    public void setBundle_product_id(String bundle_product_id) {
        this.bundle_product_id = bundle_product_id;
    }

    public String getMwp_price_range() {
        return mwp_price_range;
    }

    public void setMwp_price_range(String mwp_price_range) {
        this.mwp_price_range = mwp_price_range;
    }

    public double getClearance_discount() {
        return clearance_discount;
    }

    public void setClearance_discount(double clearance_discount) {
        this.clearance_discount = clearance_discount;
    }

    public String getPhotoshoot_type() {
        return photoshoot_type;
    }

    public void setPhotoshoot_type(String photoshoot_type) {
        this.photoshoot_type = photoshoot_type;
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

    // Start Sub Class
    public class Supplier_details implements Serializable {
        private String city_name;

        private String state_name;

        private int near_by_sellers;

        private boolean i_am_selling_this;

        private boolean i_am_selling_sell_full_catalog;

        private int total_suppliers;

        private ArrayList<ResponseSellerPolicy> seller_policy;

        public int getNear_by_sellers() {
            return near_by_sellers;
        }

        public void setNear_by_sellers(int near_by_sellers) {
            this.near_by_sellers = near_by_sellers;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }


        public boolean isI_am_selling_this() {
            return i_am_selling_this;
        }

        public void setI_am_selling_this(boolean i_am_selling_this) {
            this.i_am_selling_this = i_am_selling_this;
        }

        public int getTotal_suppliers() {
            return total_suppliers;
        }

        public void setTotal_suppliers(int total_suppliers) {
            this.total_suppliers = total_suppliers;
        }

        public ArrayList<ResponseSellerPolicy> getSeller_policy() {
            return seller_policy;
        }

        public void setSeller_policy(ArrayList<ResponseSellerPolicy> seller_policy) {
            this.seller_policy = seller_policy;
        }

        public boolean isI_am_selling_sell_full_catalog() {
            return i_am_selling_sell_full_catalog;
        }

        public void setI_am_selling_sell_full_catalog(boolean i_am_selling_sell_full_catalog) {
            this.i_am_selling_sell_full_catalog = i_am_selling_sell_full_catalog;
        }

        @Override
        public String toString() {
            return "ClassPojo [city_name = " + city_name + ", state_name = " + state_name + "]";
        }
    }

    public class Supplier_company_rating implements Serializable {
        private String id;

        private String total_seller_rating;

        private String company;

        private String buyer_score;

        private String total_buyer_rating;

        private String seller_score;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTotal_seller_rating() {
            return total_seller_rating;
        }

        public void setTotal_seller_rating(String total_seller_rating) {
            this.total_seller_rating = total_seller_rating;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getBuyer_score() {
            return buyer_score;
        }

        public void setBuyer_score(String buyer_score) {
            this.buyer_score = buyer_score;
        }

        public String getTotal_buyer_rating() {
            return total_buyer_rating;
        }

        public void setTotal_buyer_rating(String total_buyer_rating) {
            this.total_buyer_rating = total_buyer_rating;
        }

        public String getSeller_score() {
            return seller_score;
        }

        public void setSeller_score(String seller_score) {
            this.seller_score = seller_score;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", total_seller_rating = " + total_seller_rating + ", company = " + company + ", buyer_score = " + buyer_score + ", total_buyer_rating = " + total_buyer_rating + ", seller_score = " + seller_score + "]";
        }
    }

    public class Product_rating implements Serializable
    {
        private String avg_rating;

        private String total_review;

        private String total_rating;

        private ArrayList<Image> review_photos;

        public String getAvg_rating() {
            return avg_rating;
        }

        public void setAvg_rating(String avg_rating) {
            this.avg_rating = avg_rating;
        }

        public String getTotal_review ()
        {
            return total_review;
        }

        public void setTotal_review (String total_review)
        {
            this.total_review = total_review;
        }

        public String getTotal_rating ()
        {
            return total_rating;
        }

        public void setTotal_rating (String total_rating)
        {
            this.total_rating = total_rating;
        }

        public ArrayList<Image> getReview_photos() {
            return review_photos;
        }

        public void setReview_photos(ArrayList<Image> review_photos) {
            this.review_photos = review_photos;
        }
    }
}
