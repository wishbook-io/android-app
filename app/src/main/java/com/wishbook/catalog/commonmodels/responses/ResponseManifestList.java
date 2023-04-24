package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseManifestList {

    private String shipping_service_name;

    private String shipping_service;

    private String shipping_provider_name;

    private ArrayList<SellerInvoice_images> images;

    private String shipping_provider;

    private String created;

    private ArrayList<String> shipments;

    private String signed_copy;

    private String company_name;

    private String modified;

    private String company;

    private String id;

    private ArrayList<Items> items;

    private String status;

    private boolean isEditMode;

    private boolean isExpanded;


    public String getShipping_service_name() {
        return shipping_service_name;
    }

    public void setShipping_service_name(String shipping_service_name) {
        this.shipping_service_name = shipping_service_name;
    }

    public String getShipping_service() {
        return shipping_service;
    }

    public void setShipping_service(String shipping_service) {
        this.shipping_service = shipping_service;
    }

    public String getShipping_provider_name() {
        return shipping_provider_name;
    }

    public void setShipping_provider_name(String shipping_provider_name) {
        this.shipping_provider_name = shipping_provider_name;
    }

    public ArrayList<SellerInvoice_images> getImages() {
        return images;
    }

    public void setImages(ArrayList<SellerInvoice_images> images) {
        this.images = images;
    }

    public String getShipping_provider() {
        return shipping_provider;
    }

    public void setShipping_provider(String shipping_provider) {
        this.shipping_provider = shipping_provider;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public ArrayList<String> getShipments() {
        return shipments;
    }

    public void setShipments(ArrayList<String> shipments) {
        this.shipments = shipments;
    }

    public String getSigned_copy() {
        return signed_copy;
    }

    public void setSigned_copy(String signed_copy) {
        this.signed_copy = signed_copy;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    /// Nested Classed


    public class Items {
        private String amount;

        private String order_number;

        private String tax_value_1;

        private String discount;

        private String awb;

        private String tax_class_2;

        private String tax_class_1;


        private String rate;

        private String total_amount;

        private String order_item;

        private String tax_value_2;

        private String qty;

        private String buyer_order_number;

        private String id;

        private String invoice;

        private String sku;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getOrder_number() {
            return order_number;
        }

        public void setOrder_number(String order_number) {
            this.order_number = order_number;
        }

        public String getTax_value_1() {
            return tax_value_1;
        }

        public void setTax_value_1(String tax_value_1) {
            this.tax_value_1 = tax_value_1;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getAwb() {
            return awb;
        }

        public void setAwb(String awb) {
            this.awb = awb;
        }

        public String getTax_class_2() {
            return tax_class_2;
        }

        public void setTax_class_2(String tax_class_2) {
            this.tax_class_2 = tax_class_2;
        }

        public String getTax_class_1() {
            return tax_class_1;
        }

        public void setTax_class_1(String tax_class_1) {
            this.tax_class_1 = tax_class_1;
        }


        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getOrder_item() {
            return order_item;
        }

        public void setOrder_item(String order_item) {
            this.order_item = order_item;
        }

        public String getTax_value_2() {
            return tax_value_2;
        }

        public void setTax_value_2(String tax_value_2) {
            this.tax_value_2 = tax_value_2;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getBuyer_order_number() {
            return buyer_order_number;
        }

        public void setBuyer_order_number(String buyer_order_number) {
            this.buyer_order_number = buyer_order_number;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getInvoice() {
            return invoice;
        }

        public void setInvoice(String invoice) {
            this.invoice = invoice;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

    }

}
