package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by prane on 23-03-2016.
 */
public class Response_ShareStatus {
    String id;
    String date;
    String time;
    String push_type;
    String push_downstream;
    String status;
    String message;
    String buyer_segmentation;
    String buyer_segmentation_name;
    String company;
    String total_users;
    String total_viewed;
    String total_products;
    String push_amount;
    String user;
    String title;
    String brand_name;
    String image;
    String sms;
    String whatsapp;
    String email;
    String total_products_viewed;

    /*public Response_ShareStatus(String id, String date, String time, String push_type, String push_downstream, String status, String message, String buyer_segmentation, String buyer_segmentation_name, String company, String total_users, String total_viewed, String total_products, String push_amount, String user, String title, String image, String sms, String whatsapp, String email, String total_products_viewed) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.push_type = push_type;
        this.push_downstream = push_downstream;
        this.status = status;
        this.message = message;
        this.buyer_segmentation = buyer_segmentation;
        this.buyer_segmentation_name = buyer_segmentation_name;
        this.company = company;
        this.total_users = total_users;
        this.total_viewed = total_viewed;
        this.total_products = total_products;
        this.push_amount = push_amount;
        this.user = user;
        this.title = title;
        this.image = image;
        this.sms = sms;
        this.whatsapp = whatsapp;
        this.email = email;
        this.total_products_viewed = total_products_viewed;
    }*/

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

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getPush_downstream() {
        return push_downstream;
    }

    public void setPush_downstream(String push_downstream) {
        this.push_downstream = push_downstream;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBuyer_segmentation() {
        return buyer_segmentation;
    }

    public void setBuyer_segmentation(String buyer_segmentation) {
        this.buyer_segmentation = buyer_segmentation;
    }

    public String getBuyer_segmentation_name() {
        return buyer_segmentation_name;
    }

    public void setBuyer_segmentation_name(String buyer_segmentation_name) {
        this.buyer_segmentation_name = buyer_segmentation_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTotal_users() {
        return total_users;
    }

    public void setTotal_users(String total_users) {
        this.total_users = total_users;
    }

    public String getTotal_viewed() {
        return total_viewed;
    }

    public void setTotal_viewed(String total_viewed) {
        this.total_viewed = total_viewed;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public String getPush_amount() {
        return push_amount;
    }

    public void setPush_amount(String push_amount) {
        this.push_amount = push_amount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotal_products_viewed() {
        return total_products_viewed;
    }

    public void setTotal_products_viewed(String total_products_viewed) {
        this.total_products_viewed = total_products_viewed;
    }
}
