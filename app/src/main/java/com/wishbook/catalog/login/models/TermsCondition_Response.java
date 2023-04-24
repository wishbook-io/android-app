package com.wishbook.catalog.login.models;

/**
 * Created by root on 31/8/16.
 */
public class TermsCondition_Response
{
    private String main_description;

    private Sub_description[] sub_description;

    public String getMain_description ()
    {
        return main_description;
    }

    public void setMain_description (String main_description)
    {
        this.main_description = main_description;
    }

    public Sub_description[] getSub_description ()
    {
        return sub_description;
    }

    public void setSub_description (Sub_description[] sub_description)
    {
        this.sub_description = sub_description;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [main_description = "+main_description+", sub_description = "+sub_description+"]";
    }
    public class Sub_description
    {
        private String description;

        private String heading;

        public String getDescription ()
        {
            return description;
        }

        public void setDescription (String description)
        {
            this.description = description;
        }

        public String getHeading ()
        {
            return heading;
        }

        public void setHeading (String heading)
        {
            this.heading = heading;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [description = "+description+", heading = "+heading+"]";
        }
    }
}