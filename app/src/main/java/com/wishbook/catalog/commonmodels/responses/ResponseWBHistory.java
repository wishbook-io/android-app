package com.wishbook.catalog.commonmodels.responses;

public class ResponseWBHistory {
    private String id;

    private String content_object;

    private String display_text_log;

    private Wb_money_rule wb_money_rule;

    private String created;

    private String object_id;

    private String company;

    private String points;

    private String content_type;

    private String deep_link_log;

    private String expiry_date;

    // client side

    private boolean isHeader;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent_object() {
        return content_object;
    }

    public void setContent_object(String content_object) {
        this.content_object = content_object;
    }

    public Wb_money_rule getWb_money_rule() {
        return wb_money_rule;
    }

    public void setWb_money_rule(Wb_money_rule wb_money_rule) {
        this.wb_money_rule = wb_money_rule;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getDeep_link_log() {
        return deep_link_log;
    }

    public void setDeep_link_log(String deep_link_log) {
        this.deep_link_log = deep_link_log;
    }

    public String getDisplay_text_log() {
        return display_text_log;
    }

    public void setDisplay_text_log(String display_text_log) {
        this.display_text_log = display_text_log;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}
