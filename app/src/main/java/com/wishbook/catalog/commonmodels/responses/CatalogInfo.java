package com.wishbook.catalog.commonmodels.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CatalogInfo
{
    private String total_shared_with_me;

    private String shared_catalog;

    private Popular[] popular;

    private Last_shared last_shared;

    private String uploaded_catalog;

    private String shared_received_catalog;

    private String my_catalog_total_views;

    @SerializedName("my_most_viewed_catalogs")
    private ArrayList<Lastest_catalog> my_most_viewed_catalogs;

    @SerializedName("lastest_catalog")
    private Lastest_catalog lastest_catalog;

    private String catalogs_under_most_viewed;


    public String getTotal_shared_with_me ()
    {
        return total_shared_with_me;
    }

    public void setTotal_shared_with_me (String total_shared_with_me)
    {
        this.total_shared_with_me = total_shared_with_me;
    }

    public String getShared_catalog ()
    {
        return shared_catalog;
    }

    public void setShared_catalog (String shared_catalog)
    {
        this.shared_catalog = shared_catalog;
    }

    public Popular[] getPopular ()
    {
        return popular;
    }

    public void setPopular (Popular[] popular)
    {
        this.popular = popular;
    }

    public Last_shared getLast_shared ()
    {
        return last_shared;
    }

    public void setLast_shared (Last_shared last_shared)
    {
        this.last_shared = last_shared;
    }

    public String getUploaded_catalog ()
    {
        return uploaded_catalog;
    }

    public void setUploaded_catalog (String uploaded_catalog)
    {
        this.uploaded_catalog = uploaded_catalog;
    }

    public String getShared_received_catalog ()
    {
        return shared_received_catalog;
    }

    public void setShared_received_catalog (String shared_received_catalog)
    {
        this.shared_received_catalog = shared_received_catalog;
    }

    public String getMy_catalog_total_views() {
        return my_catalog_total_views;
    }

    public void setMy_catalog_total_views(String my_catalog_total_views) {
        this.my_catalog_total_views = my_catalog_total_views;
    }

    public ArrayList<Lastest_catalog> getMy_most_viewed_catalogs() {
        return my_most_viewed_catalogs;
    }

    public void setMy_most_viewed_catalogs(ArrayList<Lastest_catalog> my_most_viewed_catalogs) {
        this.my_most_viewed_catalogs = my_most_viewed_catalogs;
    }

    public Lastest_catalog getLastest_catalog() {
        return lastest_catalog;
    }

    public void setLastest_catalog(Lastest_catalog lastest_catalog) {
        this.lastest_catalog = lastest_catalog;
    }

    public String getCatalogs_under_most_viewed() {
        return catalogs_under_most_viewed;
    }

    public void setCatalogs_under_most_viewed(String catalogs_under_most_viewed) {
        this.catalogs_under_most_viewed = catalogs_under_most_viewed;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_shared_with_me = "+total_shared_with_me+", shared_catalog = "+shared_catalog+", popular = "+popular+", last_shared = "+last_shared+", uploaded_catalog = "+uploaded_catalog+", shared_received_catalog = "+shared_received_catalog+"]";
    }


    public class Lastest_catalog
    {
        private String id;

        private String title;

        private String views;

        private String image;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getTitle ()
        {
            return title;
        }

        public void setTitle (String title)
        {
            this.title = title;
        }

        public String getViews ()
        {
            return views;
        }

        public void setViews (String views)
        {
            this.views = views;
        }

        public String getImage ()
        {
            return image;
        }

        public void setImage (String image)
        {
            this.image = image;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", title = "+title+", views = "+views+", image = "+image+"]";
        }
    }
}