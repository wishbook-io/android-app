package com.wishbook.catalog.home.models;

/**
 * Created by root on 17/8/16.
 */
public class SelectionsProducts
{
    private String id;

    private String push_user_id;

    private String is_disable;

    private String name;

    private String buyable;

    private String image;

    private String[] products;

    private String user;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPush_user_id ()
{
    return push_user_id;
}

    public void setPush_user_id (String push_user_id)
    {
        this.push_user_id = push_user_id;
    }

    public String getIs_disable ()
    {
        return is_disable;
    }

    public void setIs_disable (String is_disable)
    {
        this.is_disable = is_disable;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getBuyable ()
    {
        return buyable;
    }

    public void setBuyable (String buyable)
    {
        this.buyable = buyable;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String[] getProducts ()
    {
        return products;
    }

    public void setProducts (String[] products)
    {
        this.products = products;
    }

    public String getUser ()
    {
        return user;
    }

    public void setUser (String user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", push_user_id = "+push_user_id+", is_disable = "+is_disable+", name = "+name+", buyable = "+buyable+", image = "+image+", products = "+products+", user = "+user+"]";
    }
}