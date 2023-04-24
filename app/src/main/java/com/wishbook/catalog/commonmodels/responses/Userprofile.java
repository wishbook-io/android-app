package com.wishbook.catalog.commonmodels.responses;

public class Userprofile
{
    private String phone_number;

    private String alternate_email;

    private String tnc_agreed;

    private String phone_number_verified;

    private String user_image;

    private String country;

    String notification_disabled;

    public String getPhone_number ()
    {
        return phone_number;
    }

    public void setPhone_number (String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getAlternate_email ()
    {
        return alternate_email;
    }

    public void setAlternate_email (String alternate_email)
    {
        this.alternate_email = alternate_email;
    }

    public String getTnc_agreed ()
    {
        return tnc_agreed;
    }

    public void setTnc_agreed (String tnc_agreed)
    {
        this.tnc_agreed = tnc_agreed;
    }

    public String getPhone_number_verified ()
    {
        return phone_number_verified;
    }

    public void setPhone_number_verified (String phone_number_verified)
    {
        this.phone_number_verified = phone_number_verified;
    }

    public String getUser_image ()
    {
        return user_image;
    }

    public void setUser_image (String user_image)
    {
        this.user_image = user_image;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }

    public String getNotification_disabled() {
        return notification_disabled;
    }

    public void setNotification_disabled(String notification_disabled) {
        this.notification_disabled = notification_disabled;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [phone_number = "+phone_number+", alternate_email = "+alternate_email+", tnc_agreed = "+tnc_agreed+", phone_number_verified = "+phone_number_verified+", user_image = "+user_image+", country = "+country+"]";
    }
}
