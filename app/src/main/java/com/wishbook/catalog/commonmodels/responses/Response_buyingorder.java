package com.wishbook.catalog.commonmodels.responses;

import com.google.gson.annotations.SerializedName;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.Shipments;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 11/04/16.
 */
public class Response_buyingorder implements Serializable {

    String id;
    String order_number;
    String company;
    String total_rate;
    String date;
    String time;
    String processing_status;
    String customer_status;
    String user;
   // ArrayList<Response_Product> items;
    ArrayList<Response_sellingoder_catalog> catalogs;
    String sales_image;
    String purchase_image;
    String seller_company;
    String seller_company_name;
    String note;
    String company_name;
    String total_products;
    String tracking_details;
    String supplier_cancel;
    String buyer_cancel="";
    String payment_details="";
    String payment_date="";
    String dispatch_date="";
    String broker_company="";
    String broker_company_name="";
    String seller_ref="";
    String buyer_table_id="";
    Boolean is_supplier_approved;
    String payment_status ="";
    String buyer_preferred_logistics = "";
    ArrayList<Invoice> invoice;
    String preffered_shipping_provider = "";
    ArrayList<String> images;
    private Object ship_to;
    boolean isBrokerage = false;
    String seller_chat_user;
    String buyer_chat_user;
    String buyer_credit_rating;
    String current_discount;
    String created_at;
    String delivery_date;

    private ArrayList<Rrces> rrces;

    @SerializedName("lastest_shipment")
    private Shipments lastest_shipment;


    private String order_type;

    private double seller_extra_discount_percentage;

    private boolean reseller_order;

    private double display_amount;

    private String order_expected_date;

    public String getOrder_expected_date() {
        return order_expected_date;
    }

    public void setOrder_expected_date(String order_expected_date) {
        this.order_expected_date = order_expected_date;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public double getSeller_extra_discount_percentage() {
        return seller_extra_discount_percentage;
    }

    public void setSeller_extra_discount_percentage(double seller_extra_discount_percentage) {
        this.seller_extra_discount_percentage = seller_extra_discount_percentage;
    }

    public String getBuyer_credit_rating() {
        return buyer_credit_rating;
    }

    public void setBuyer_credit_rating(String buyer_credit_rating) {
        this.buyer_credit_rating = buyer_credit_rating;
    }

    public ArrayList<Invoice> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<Invoice> invoice) {
        this.invoice = invoice;
    }

    public Boolean getIs_supplier_approved() {
        return is_supplier_approved;
    }

    public void setIs_supplier_approved(Boolean is_supplier_approved) {
        this.is_supplier_approved = is_supplier_approved;
    }

    public String getBuyer_table_id() {
        return buyer_table_id;
    }

    public void setBuyer_table_id(String buyer_table_id) {
        this.buyer_table_id = buyer_table_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Response_buyingorder(String id) {
        this.id = id;
    }

    public ArrayList<Response_sellingoder_catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(ArrayList<Response_sellingoder_catalog> catalogs) {
        this.catalogs = catalogs;
    }


    public String getTracking_details() {
        return tracking_details;
    }

    public void setTracking_details(String tracking_details) {
        this.tracking_details = tracking_details;
    }

    public String getSupplier_cancel() {
        return supplier_cancel;
    }

    public void setSupplier_cancel(String supplier_cancel) {
        this.supplier_cancel = supplier_cancel;
    }

    public String getBuyer_cancel() {
        return buyer_cancel;
    }

    public void setBuyer_cancel(String buyer_cancel) {
        this.buyer_cancel = buyer_cancel;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getDispatch_date() {
        return dispatch_date;
    }

    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getBroker_company_name() {
        return broker_company_name;
    }

    public void setBroker_company_name(String broker_company_name) {
        this.broker_company_name = broker_company_name;
    }

    public String getSeller_ref() {
        return seller_ref;
    }

    public void setSeller_ref(String seller_ref) {
        this.seller_ref = seller_ref;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }



    public String getTotal_rate() {
        return total_rate;
    }

    public void setTotal_rate(String total_rate) {
        this.total_rate = total_rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    public String getCustomer_status() {
        return customer_status;
    }

    public void setCustomer_status(String customer_status) {
        this.customer_status = customer_status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCurrent_discount() {
        return current_discount;
    }

    public void setCurrent_discount(String current_discount) {
        this.current_discount = current_discount;
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

    /*

    public ArrayList<Response_Product> getItems() {
        return items;
    }

    public void setItems(ArrayList<Response_Product> items) {
        this.items = items;
    }
*/

    public String getSales_image() {
        return sales_image;
    }

    public void setSales_image(String sales_image) {
        this.sales_image = sales_image;
    }

    public String getPurchase_image() {
        return purchase_image;
    }

    public void setPurchase_image(String purchase_image) {
        this.purchase_image = purchase_image;
    }


    public String getSeller_company() {
        return seller_company;
    }

    public void setSeller_company(String seller_company) {
        this.seller_company = seller_company;
    }

    public String getSeller_company_name() {
        return seller_company_name;
    }

    public void setSeller_company_name(String seller_company_name) {
        this.seller_company_name = seller_company_name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getBuyer_preferred_logistics() {
        return buyer_preferred_logistics;
    }

    public void setBuyer_preferred_logistics(String buyer_preferred_logistics) {
        this.buyer_preferred_logistics = buyer_preferred_logistics;
    }

    public String getPreffered_shipping_provider() {
        return preffered_shipping_provider;
    }

    public void setPreffered_shipping_provider(String preffered_shipping_provider) {
        this.preffered_shipping_provider = preffered_shipping_provider;
    }

    public Object getShip_to() {
        return ship_to;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setShip_to(Object ship_to) {
        this.ship_to = ship_to;
    }

    public boolean isBrokerage() {
        return isBrokerage;
    }

    public void setBrokerage(boolean brokerage) {
        isBrokerage = brokerage;
    }

    public String getSeller_chat_user() {
        return seller_chat_user;
    }

    public void setSeller_chat_user(String seller_chat_user) {
        this.seller_chat_user = seller_chat_user;
    }

    public String getBuyer_chat_user() {
        return buyer_chat_user;
    }

    public void setBuyer_chat_user(String buyer_chat_user) {
        this.buyer_chat_user = buyer_chat_user;
    }

    public ArrayList<Rrces> getRrces() {
        return rrces;
    }

    public void setRrces(ArrayList<Rrces> rrces) {
        this.rrces = rrces;
    }

    public Shipments getLastest_shipment() {
        return lastest_shipment;
    }

    public void setLastest_shipment(Shipments lastest_shipment) {
        this.lastest_shipment = lastest_shipment;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public class Ship_to
    {
        private String pincode;

        private String id;

        private String name;

        private String state;

        private String street_address;

        private String user;

        private String country;

        private String city;

        public String getPincode ()
        {
            return pincode;
        }

        public void setPincode (String pincode)
        {
            this.pincode = pincode;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getState ()
        {
            return state;
        }

        public void setState (String state)
        {
            this.state = state;
        }

        public String getStreet_address ()
        {
            return street_address;
        }

        public void setStreet_address (String street_address)
        {
            this.street_address = street_address;
        }

        public String getUser ()
        {
            return user;
        }

        public void setUser (String user)
        {
            this.user = user;
        }

        public String getCountry ()
        {
            return country;
        }

        public void setCountry (String country)
        {
            this.country = country;
        }

        public String getCity ()
        {
            return city;
        }

        public void setCity (String city)
        {
            this.city = city;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [pincode = "+pincode+", id = "+id+", name = "+name+", state = "+state+", street_address = "+street_address+", user = "+user+", country = "+country+", city = "+city+"]";
        }
    }


}
