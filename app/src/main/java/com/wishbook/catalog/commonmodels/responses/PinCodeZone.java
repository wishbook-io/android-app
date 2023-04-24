package com.wishbook.catalog.commonmodels.responses;

public class PinCodeZone {

    private String pincode;

    private String zone;

    private String city;

    private String state;

    private boolean is_servicable;

    private String created;

    private boolean cod_available;

    private String modified;

    private String id;


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isIs_servicable() {
        return is_servicable;
    }

    public void setIs_servicable(boolean is_servicable) {
        this.is_servicable = is_servicable;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isCod_available() {
        return cod_available;
    }

    public void setCod_available(boolean cod_available) {
        this.cod_available = cod_available;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
