package com.wishbook.catalog.commonmodels.responses;



public class ConfigResponse {

    private String id;

    private String visible_on_frontend;

    private String display_text;

    private String value;

    private String key;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getVisible_on_frontend ()
    {
        return visible_on_frontend;
    }

    public void setVisible_on_frontend (String visible_on_frontend)
    {
        this.visible_on_frontend = visible_on_frontend;
    }

    public String getDisplay_text ()
    {
        return display_text;
    }

    public void setDisplay_text (String display_text)
    {
        this.display_text = display_text;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getKey ()
    {
        return key;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", visible_on_frontend = "+visible_on_frontend+", display_text = "+display_text+", value = "+value+", key = "+key+"]";
    }

}
