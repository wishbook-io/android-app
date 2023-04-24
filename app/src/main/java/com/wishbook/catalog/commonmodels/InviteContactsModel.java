package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

public class InviteContactsModel
{
    private String group_type;

    private String request_type;

    private ArrayList<ArrayList<String>> invitee;

    public String getGroup_type ()
    {
        return group_type;
    }

    public void setGroup_type (String group_type)
    {
        this.group_type = group_type;
    }

    public String getRequest_type ()
    {
        return request_type;
    }

    public void setRequest_type (String request_type)
    {
        this.request_type = request_type;
    }

    public ArrayList<ArrayList<String>> getInvitee() {
        return invitee;
    }

    public void setInvitee(ArrayList<ArrayList<String>> invitee) {
        this.invitee = invitee;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [group_type = "+group_type+", request_type = "+request_type+", invitee = "+invitee+"]";
    }
}