package com.wishbook.catalog.login.models;

/**
 * Created by root on 31/8/16.
 */
public class TermsConditionModel {
    private String Sub_Heading;
    private String Sub_Description;

    public String getSub_Heading() {
        return Sub_Heading;
    }

    public void setSub_Heading(String sub_Heading) {
        Sub_Heading = sub_Heading;
    }

    public String getSub_Description() {
        return Sub_Description;
    }

    public void setSub_Description(String sub_Description) {
        Sub_Description = sub_Description;
    }

    public TermsConditionModel(String sub_Heading, String sub_Description) {
        Sub_Heading = sub_Heading;
        Sub_Description = sub_Description;
    }
}
