package com.wishbook.catalog.home.more.visits.SecondLevelExpandableView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by root on 28/10/16.
 */
public class Cities implements ParentListItem
{
    private String id;

    private List<Buyers> buyers;

    private String name;



    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public List<Buyers> getBuyers ()
    {
        return buyers;
    }

    public void setBuyers (List<Buyers> buyers)
    {
        this.buyers = buyers;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public List<Buyers> getChildItemList() {
        return buyers;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}