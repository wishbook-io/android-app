package com.wishbook.catalog.commonmodels;

import com.wishbook.catalog.commonmodels.responses.Eavdata;
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartCatalogModel implements  Serializable{


    private ArrayList<Catalogs> catalogs;
    private String cart_status;
    private String created_type;
    private String seller_discount;
    private String payment_status;
    private String total_qty;
    private String modified;
    private String created;
    private String order_type;
    private String shipping_charges;
    private String shipping_method;
    private String preffered_shipping_provider;
    private String brokerage_fees;
    private String buying_company;
    private String user;
    private ArrayList<Items> items;
    private String id;
    private int total;
    private double pending_amount;
    private String taxes;
    private String amount;
    private double total_amount;
    private int wbmoney_points_used;
    private int wbpoints_used;
    private double wbmoney_redeem_amt;
    private double now_pay_amount;
    private String ship_to;
    private String total_cart_items;
    private boolean reseller_order;
    private double display_amount;
    private double wb_coupon_discount;
    private ResponseCouponList wb_coupon;

    public int getWbpoints_used() {
        return wbpoints_used;
    }

    public void setWbpoints_used(int wbpoints_used) {
        this.wbpoints_used = wbpoints_used;
    }

    public String getTotal_cart_items() {
        return total_cart_items;
    }

    public void setTotal_cart_items(String total_cart_items) {
        this.total_cart_items = total_cart_items;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public double getPending_amount() {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(pending_amount));
    }

    public void setPending_amount(double pending_amount) {
        this.pending_amount = pending_amount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Catalogs> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(ArrayList<Catalogs> catalogs) {
        this.catalogs = catalogs;
    }

    public String getCart_status() {
        return cart_status;
    }

    public void setCart_status(String cart_status) {
        this.cart_status = cart_status;
    }

    public String getCreated_type() {
        return created_type;
    }

    public void setCreated_type(String created_type) {
        this.created_type = created_type;
    }

    public String getSeller_discount() {
        return seller_discount;
    }

    public void setSeller_discount(String seller_discount) {
        this.seller_discount = seller_discount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(String total_qty) {
        this.total_qty = total_qty;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getShipping_charges() {
        return shipping_charges;
    }

    public void setShipping_charges(String shipping_charges) {
        this.shipping_charges = shipping_charges;
    }

    public String getPreffered_shipping_provider() {
        return preffered_shipping_provider;
    }

    public void setPreffered_shipping_provider(String preffered_shipping_provider) {
        this.preffered_shipping_provider = preffered_shipping_provider;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public String getBuying_company() {
        return buying_company;
    }

    public void setBuying_company(String buying_company) {
        this.buying_company = buying_company;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotal_amount() {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(total_amount));
    }

    public String getShip_to() {
        return ship_to;
    }

    public void setShip_to(String ship_to) {
        this.ship_to = ship_to;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }


    public int getWb_money_used() {
        return wbmoney_points_used;
    }

    public void setWb_money_used(int wb_money_applied) {
        this.wbmoney_points_used = wb_money_applied;
    }

    public double getWbmoney_redeem_amt() {
        return wbmoney_redeem_amt;
    }

    public void setWbmoney_redeem_amt(double wbmoney_redeem_amt) {
        this.wbmoney_redeem_amt = wbmoney_redeem_amt;
    }

    public double getNow_pay_amount() {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(total_amount));
    }

    public void setNow_pay_amount(double now_pay_amount) {
        this.now_pay_amount = now_pay_amount;
    }

    public boolean isReseller_order() {
        return reseller_order;
    }

    public void setReseller_order(boolean reseller_order) {
        this.reseller_order = reseller_order;
    }

    public double getDisplay_amount() {
        return display_amount;
    }

    public void setDisplay_amount(double display_amount) {
        this.display_amount = display_amount;
    }

    public double getWb_coupon_discount() {
        return wb_coupon_discount;
    }

    public void setWb_coupon_discount(double wb_coupon_discount) {
        this.wb_coupon_discount = wb_coupon_discount;
    }

    public ResponseCouponList getWb_coupon() {
        return wb_coupon;
    }

    public void setWb_coupon(ResponseCouponList wb_coupon) {
        this.wb_coupon = wb_coupon;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
    }

    public class Catalogs  implements  Serializable{
        private String price_range;
        private Eavdata catalog_eavdata;
        private ArrayList<Sellers> sellers;
        private String selling_company_id;
        private String catalog_id;
        private ArrayList<Products> products;
        private boolean is_full_catalog;
        private String catalog_title;
        private String selling_company_name;
        private String catalog_brand;
        private String total_products;
        private boolean trusted_seller;
        private int total_price;
        private String catalog_image;
        private Object cartProductAdapter;
        private boolean expanded;
        private int grand_total;
        private String catalog_amount;
        private String catalog_total_amount;
        private String catalog_shipping_charges;
        private String catalog_discount;
        private String catalog_discount_percent;
        private String catalog_tax_class_1_percentage;
        private String catalog_tax_class_2_percentage;
        private String catalog_tax_value_2;
        private String catalog_tax_value_1;
        private String tax_class_1, tax_class_2;
        private String dispatch_date;
        private boolean ready_to_ship;
        private String catalog_type;
        private String product_id;
        private String catalog_category;
        private double catalog_display_amount;

        private double single_discount;
        private double full_discount;


        public String getTax_class_1() {
            return tax_class_1;
        }

        public void setTax_class_1(String tax_class_1) {
            this.tax_class_1 = tax_class_1;
        }

        public String getTax_class_2() {
            return tax_class_2;
        }

        public void setTax_class_2(String tax_class_2) {
            this.tax_class_2 = tax_class_2;
        }

        public Eavdata getCatalog_eavdata() {
            return catalog_eavdata;
        }

        public void setCatalog_eavdata(Eavdata catalog_eavdata) {
            this.catalog_eavdata = catalog_eavdata;
        }

        public String getCatalog_tax_value_1() {
            return catalog_tax_value_1;
        }

        public void setCatalog_tax_value_1(String catalog_tax_value_1) {
            this.catalog_tax_value_1 = catalog_tax_value_1;
        }

        public boolean isIs_full_catalog() {
            return is_full_catalog;
        }

        public boolean isTrusted_seller() {
            return trusted_seller;
        }

        public int getTotal_price() {
            return total_price;
        }

        public void setTotal_price(int total_price) {
            this.total_price = total_price;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public String getCatalog_amount() {
            return catalog_amount;
        }

        public void setCatalog_amount(String catalog_amount) {
            this.catalog_amount = catalog_amount;
        }

        public String getCatalog_total_amount() {
            return catalog_total_amount;
        }

        public void setCatalog_total_amount(String catalog_total_amount) {
            this.catalog_total_amount = catalog_total_amount;
        }

        public String getCatalog_shipping_charges() {
            return catalog_shipping_charges;
        }

        public void setCatalog_shipping_charges(String catalog_shipping_charges) {
            this.catalog_shipping_charges = catalog_shipping_charges;
        }

        public String getCatalog_discount() {
            return catalog_discount;
        }

        public void setCatalog_discount(String catalog_discount) {
            this.catalog_discount = catalog_discount;
        }

        public String getCatalog_discount_percent() {
            return catalog_discount_percent;
        }

        public void setCatalog_discount_percent(String catalog_discount_percent) {
            this.catalog_discount_percent = catalog_discount_percent;
        }

        public String getCatalog_tax_class_1_percentage() {
            return catalog_tax_class_1_percentage;
        }

        public void setCatalog_tax_class_1_percentage(String catalog_tax_class_1_percentage) {
            this.catalog_tax_class_1_percentage = catalog_tax_class_1_percentage;
        }

        public String getCatalog_tax_class_2_percentage() {
            return catalog_tax_class_2_percentage;
        }

        public void setCatalog_tax_class_2_percentage(String catalog_tax_class_2_percentage) {
            this.catalog_tax_class_2_percentage = catalog_tax_class_2_percentage;
        }

        public String getCatalog_tax_value_2() {
            return catalog_tax_value_2;
        }

        public void setCatalog_tax_value_2(String catalog_tax_value_2) {
            this.catalog_tax_value_2 = catalog_tax_value_2;
        }

        public String getCatalog_type() {
            return catalog_type;
        }

        public void setCatalog_type(String catalog_type) {
            this.catalog_type = catalog_type;
        }

        public int getGrand_total() {
            return grand_total;
        }

        public void setGrand_total(int grand_total) {
            this.grand_total = grand_total;
        }

        public boolean getExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public Object getCartProductAdapter() {
            return cartProductAdapter;
        }

        public void setCartProductAdapter(Object cartProductAdapter) {
            this.cartProductAdapter = cartProductAdapter;
        }

        public String getPrice_range() {
            return price_range;
        }

        public void setPrice_range(String price_range) {
            this.price_range = price_range;
        }

        public ArrayList<Sellers> getSellers() {
            return sellers;
        }

        public void setSellers(ArrayList<Sellers> sellers) {
            this.sellers = sellers;
        }

        public String getSelling_company_id() {
            return selling_company_id;
        }

        public void setSelling_company_id(String selling_company_id) {
            this.selling_company_id = selling_company_id;
        }

        public String getCatalog_id() {
            return catalog_id;
        }

        public void setCatalog_id(String catalog_id) {
            this.catalog_id = catalog_id;
        }

        public ArrayList<Products> getProducts() {
            return products;
        }

        public void setProducts(ArrayList<Products> products) {
            this.products = products;
        }

        public boolean getIs_full_catalog() {
            return is_full_catalog;
        }

        public void setIs_full_catalog(boolean is_full_catalog) {
            this.is_full_catalog = is_full_catalog;
        }

        public String getCatalog_title() {
            return catalog_title;
        }

        public void setCatalog_title(String catalog_title) {
            this.catalog_title = catalog_title;
        }

        public String getSelling_company_name() {
            return selling_company_name;
        }

        public void setSelling_company_name(String selling_company_name) {
            this.selling_company_name = selling_company_name;
        }

        public String getCatalog_brand() {
            return catalog_brand;
        }

        public void setCatalog_brand(String catalog_brand) {
            this.catalog_brand = catalog_brand;
        }

        public String getTotal_products() {
            return total_products;
        }

        public void setTotal_products(String total_products) {
            this.total_products = total_products;
        }

        public boolean getTrusted_seller() {
            return trusted_seller;
        }

        public void setTrusted_seller(boolean trusted_seller) {
            this.trusted_seller = trusted_seller;
        }

        public String getCatalog_image() {
            return catalog_image;
        }

        public void setCatalog_image(String catalog_image) {
            this.catalog_image = catalog_image;
        }

        public String getDispatch_date() {
            return dispatch_date;
        }

        public void setDispatch_date(String dispatch_date) {
            this.dispatch_date = dispatch_date;
        }

        public boolean isReady_to_ship() {
            return ready_to_ship;
        }

        public void setReady_to_ship(boolean ready_to_ship) {
            this.ready_to_ship = ready_to_ship;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getCatalog_category() {
            return catalog_category;
        }

        public void setCatalog_category(String catalog_category) {
            this.catalog_category = catalog_category;
        }

        public double getCatalog_display_amount() {
            return catalog_display_amount;
        }

        public void setCatalog_display_amount(double catalog_display_amount) {
            this.catalog_display_amount = catalog_display_amount;
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
    }

    public class Sellers implements Serializable {
        private String company_name;
        private String company_id;
        private boolean trusted_seller;
        private String state_name;
        private String city_name;
        private ArrayList<ResponseSellerPolicy> seller_policy;
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public ArrayList<ResponseSellerPolicy> getSeller_policy() {
            return seller_policy;
        }

        public void setSeller_policy(ArrayList<ResponseSellerPolicy> seller_policy) {
            this.seller_policy = seller_policy;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public boolean getTrusted_seller() {
            return trusted_seller;
        }

        public void setTrusted_seller(boolean trusted_seller) {
            this.trusted_seller = trusted_seller;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }


        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }
    }

    public class Products implements Serializable {
        double discount_percent;
        private String product;
        private String cart;
        private boolean is_full_catalog;
        private String discount;
        private String tax_value_2;
        private String tax_value_1;
        private String rate;
        private int quantity;
        private String selling_company;
        private String product_category;
        private String product_image;
        private String product_image_medium;
        private String product_sku;
        private String product_catalog;
        private String product_title;
        private String note;
        private String product_type;
        private int no_of_pcs;
        private String product_id;

        private String total_amount;
        private double mwp_single_price;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getProduct_image_medium() {
            return product_image_medium;
        }

        public void setProduct_image_medium(String product_image_medium) {
            this.product_image_medium = product_image_medium;
        }

        private String id;
        private int count;
        private int total_price;
        private String amount;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public double getDiscount_percent() {
            return discount_percent;
        }

        public void setDiscount_percent(double discount_percent) {
            this.discount_percent = discount_percent;
        }

        public int getTotal_price() {
            return total_price;
        }

        public void setTotal_price(int price) {
            this.total_price = price;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getCart() {
            return cart;
        }

        public void setCart(String cart) {
            this.cart = cart;
        }

        public boolean getIs_full_catalog() {
            return is_full_catalog;
        }

        public void setIs_full_catalog(boolean is_full_catalog) {
            this.is_full_catalog = is_full_catalog;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getTax_value_2() {
            return tax_value_2;
        }

        public void setTax_value_2(String tax_value_2) {
            this.tax_value_2 = tax_value_2;
        }

        public String getTax_value_1() {
            return tax_value_1;
        }

        public void setTax_value_1(String tax_value_1) {
            this.tax_value_1 = tax_value_1;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getSelling_company() {
            return selling_company;
        }

        public void setSelling_company(String selling_company) {
            this.selling_company = selling_company;
        }

        public String getProduct_category() {
            return product_category;
        }

        public void setProduct_category(String product_category) {
            this.product_category = product_category;
        }

        public String getProduct_image() {
            return product_image;
        }

        public void setProduct_image(String product_image) {
            this.product_image = product_image;
        }

        public String getProduct_sku() {
            return product_sku;
        }

        public void setProduct_sku(String product_sku) {
            this.product_sku = product_sku;
        }

        public String getProduct_catalog() {
            return product_catalog;
        }

        public void setProduct_catalog(String product_catalog) {
            this.product_catalog = product_catalog;
        }

        public String getProduct_type() {
            return product_type;
        }

        public void setProduct_type(String product_type) {
            this.product_type = product_type;
        }

        public String getProduct_title() {
            return product_title;
        }

        public void setProduct_title(String product_title) {
            this.product_title = product_title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public int getNo_of_pcs() {
            return no_of_pcs;
        }

        public void setNo_of_pcs(int no_of_pcs) {
            this.no_of_pcs = no_of_pcs;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public double getMwp_single_price() {
            return mwp_single_price;
        }

        public void setMwp_single_price(double mwp_single_price) {
            this.mwp_single_price = mwp_single_price;
        }
    }

    public class Items  implements  Serializable{
        private String product;
        private String cart;
        private boolean is_full_catalog;
        private String discount;
        private String tax_value_2;
        private String tax_value_1;
        private String rate;
        private String quantity;
        private String selling_company;
        private String product_category;
        private String product_image;
        private String product_sku;
        private String product_catalog;
        private String product_title;
        private String id;

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getCart() {
            return cart;
        }

        public void setCart(String cart) {
            this.cart = cart;
        }

        public boolean getIs_full_catalog() {
            return is_full_catalog;
        }

        public void setIs_full_catalog(boolean is_full_catalog) {
            this.is_full_catalog = is_full_catalog;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getTax_value_2() {
            return tax_value_2;
        }

        public void setTax_value_2(String tax_value_2) {
            this.tax_value_2 = tax_value_2;
        }

        public String getTax_value_1() {
            return tax_value_1;
        }

        public void setTax_value_1(String tax_value_1) {
            this.tax_value_1 = tax_value_1;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getSelling_company() {
            return selling_company;
        }

        public void setSelling_company(String selling_company) {
            this.selling_company = selling_company;
        }

        public String getProduct_category() {
            return product_category;
        }

        public void setProduct_category(String product_category) {
            this.product_category = product_category;
        }

        public String getProduct_image() {
            return product_image;
        }

        public void setProduct_image(String product_image) {
            this.product_image = product_image;
        }

        public String getProduct_sku() {
            return product_sku;
        }

        public void setProduct_sku(String product_sku) {
            this.product_sku = product_sku;
        }

        public String getProduct_catalog() {
            return product_catalog;
        }

        public void setProduct_catalog(String product_catalog) {
            this.product_catalog = product_catalog;
        }

        public String getProduct_title() {
            return product_title;
        }

        public void setProduct_title(String product_title) {
            this.product_title = product_title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
