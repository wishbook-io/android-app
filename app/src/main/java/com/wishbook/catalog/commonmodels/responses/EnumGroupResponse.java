package com.wishbook.catalog.commonmodels.responses;


public class EnumGroupResponse {
    private String id;

    private String value;

    private String tag;

    public EnumGroupResponse(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public EnumGroupResponse(String id, String value, String tag) {
        this.id = id;
        this.value = value;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", value = " + value + "]";
    }

}
