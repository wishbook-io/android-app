package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class ResponseCategoryEvp {

    private String id;

    private String category;

    private boolean is_required;

    private boolean filterable;

    private String attribute;

    private ArrayList<Attribute_values> attribute_values;

    private String attribute_datatype;

    private String attribute_name;

    private String attribute_slug;

    private String attribute_type;

    private String min_value;

    private String max_value;

    private String unit;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getIs_required() {
        return is_required;
    }

    public void setIs_required(boolean is_required) {
        this.is_required = is_required;
    }

    public boolean getFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public ArrayList<Attribute_values> getAttribute_values() {
        return attribute_values;
    }

    public void setAttribute_values(ArrayList<Attribute_values> attribute_values) {
        this.attribute_values = attribute_values;
    }

    public String getAttribute_datatype() {
        return attribute_datatype;
    }

    public void setAttribute_datatype(String attribute_datatype) {
        this.attribute_datatype = attribute_datatype;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public String getAttribute_slug() {
        return attribute_slug;
    }

    public void setAttribute_slug(String attribute_slug) {
        this.attribute_slug = attribute_slug;
    }

    public String getAttribute_type() {
        return attribute_type;
    }

    public void setAttribute_type(String attribute_type) {
        this.attribute_type = attribute_type;
    }

    public String getMin_value() {
        return min_value;
    }

    public void setMin_value(String min_value) {
        this.min_value = min_value;
    }

    public String getMax_value() {
        return max_value;
    }

    public void setMax_value(String max_value) {
        this.max_value = max_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public static ResponseCategoryEvp findEvpfromSlug(ArrayList<ResponseCategoryEvp> responseCategoryEvps,String attribute_slug) {
        if(responseCategoryEvps!=null) {
            for (ResponseCategoryEvp evp:
                 responseCategoryEvps) {
                if(evp.getAttribute_slug()!=null && evp.getAttribute_slug().equalsIgnoreCase(attribute_slug)) {

                    return evp;
                }
            }
        }
        return null;
    }

    public class Attribute_values
    {
        private String id;

        private String value;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getValue ()
        {
            return value;
        }

        public void setValue (String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", value = "+value+"]";
        }
    }
}
