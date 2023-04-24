package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 5/12/16.
 */
public class Response_Attendance
{
    private String id;

    private String att_long;

    private String action;

    private String att_lat;

    private String date_time;

    private String user;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAtt_long ()
    {
        return att_long;
    }

    public void setAtt_long (String att_long)
    {
        this.att_long = att_long;
    }

    public String getAction ()
    {
        return action;
    }

    public void setAction (String action)
    {
        this.action = action;
    }

    public String getAtt_lat ()
    {
        return att_lat;
    }

    public void setAtt_lat (String att_lat)
    {
        this.att_lat = att_lat;
    }

    public String getDate_time ()
    {
        return date_time;
    }

    public void setDate_time (String date_time)
    {
        this.date_time = date_time;
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
        return "ClassPojo [id = "+id+", att_long = "+att_long+", action = "+action+", att_lat = "+att_lat+", date_time = "+date_time+", user = "+user+"]";
    }
}