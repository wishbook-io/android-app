package com.wishbook.catalog.commonmodels.responses;



public class FollowUsers {

    private String id;

    private String phone_number;

    private String name;

    private boolean is_invited;

    private String followed_brand_names;

    private String buyer_id;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPhone_number ()
    {
        return phone_number;
    }

    public void setPhone_number (String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public boolean getIs_invited ()
    {
        return is_invited;
    }

    public void setIs_invited (boolean is_invited)
    {
        this.is_invited = is_invited;
    }

    public String getFollowed_brand_names ()
    {
        return followed_brand_names;
    }

    public void setFollowed_brand_names (String followed_brand_names)
    {
        this.followed_brand_names = followed_brand_names;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", phone_number = "+phone_number+", name = "+name+", is_invited = "+is_invited+", followed_brand_names = "+followed_brand_names+"]";
    }
}
