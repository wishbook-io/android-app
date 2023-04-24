package com.wishbook.catalog.commonmodels;

import java.util.HashMap;

public class WishbookEvent {

    public static String LOGIN_EVENTS_CATEGORY = "LOGIN EVENTS";
    public static String PRODUCT_EVENTS_CATEGORY = "PRODUCT SEARCH / VIEW EVENTS";
    public static String SELLER_EVENTS_CATEGORY = "SELLER EVENTS";
    public static String ECOMMERCE_EVENTS_CATEGORY = "ECOMMERCE EVENTS";
    public static String MARKETING_EVENTS_CATEGORY = "MARKETING / PROMOTION EVENTS";
    public static String UPDATE_EVENT_CATEGORY = "UPDATE EVENTS";

    public static String USER_PROPERTIES = "USER PROPERTIES";
    public static String PRODUCT_ATTRIBUTES = "PRODUCT ATTRIBUTES";
    public static String CART_ATTRIBUTES = "CART ATTRIBUTES";

    public static String HOME_EVENT = "HOME EVENT";



    String event_category;

    String event_names;

    HashMap<String, String> event_properties;

    boolean event_track_branch;

    boolean event_track_clevertap;

    boolean event_track_fabric;

    boolean event_track_ga;

    boolean event_track_ga_ecommerce;

    boolean ga_legacy_event;


    public WishbookEvent() {
    }

    public WishbookEvent(String event_category, String event_names, HashMap<String, String> event_properties, boolean event_track_branch, boolean event_track_clevertap, boolean event_track_fabric, boolean event_track_ga, boolean event_track_ga_ecommerce) {
        this.event_category = event_category;
        this.event_names = event_names;
        this.event_properties = event_properties;
        this.event_track_branch = event_track_branch;
        this.event_track_clevertap = event_track_clevertap;
        this.event_track_fabric = event_track_fabric;
        this.event_track_ga = event_track_ga;
        this.event_track_ga_ecommerce = event_track_ga_ecommerce;
    }

    public String getEvent_category() {
        return event_category;
    }

    public void setEvent_category(String event_category) {
        this.event_category = event_category;
    }

    public String getEvent_names() {
        return event_names;
    }

    public void setEvent_names(String event_names) {
        this.event_names = event_names;
    }

    public HashMap<String, String> getEvent_properties() {
        return event_properties;
    }

    public void setEvent_properties(HashMap<String, String> event_properties) {
        this.event_properties = event_properties;
    }

    public boolean isEvent_track_branch() {
        return event_track_branch;
    }

    public void setEvent_track_branch(boolean event_track_branch) {
        this.event_track_branch = event_track_branch;
    }

    public boolean isEvent_track_clevertap() {
        return event_track_clevertap;
    }

    public void setEvent_track_clevertap(boolean event_track_clevertap) {
        this.event_track_clevertap = event_track_clevertap;
    }

    public boolean isEvent_track_fabric() {
        return event_track_fabric;
    }

    public void setEvent_track_fabric(boolean event_track_fabric) {
        this.event_track_fabric = event_track_fabric;
    }

    public boolean isEvent_track_ga() {
        return event_track_ga;
    }

    public void setEvent_track_ga(boolean event_track_ga) {
        this.event_track_ga = event_track_ga;
    }

    public boolean isEvent_track_ga_ecommerce() {
        return event_track_ga_ecommerce;
    }

    public void setEvent_track_ga_ecommerce(boolean event_track_ga_ecommerce) {
        this.event_track_ga_ecommerce = event_track_ga_ecommerce;
    }

    public boolean isGa_legacy_event() {
        return ga_legacy_event;
    }

    public void setGa_legacy_event(boolean ga_legacy_event) {
        this.ga_legacy_event = ga_legacy_event;
    }
}
