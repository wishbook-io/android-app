package com.wishbook.catalog.commonmodels.responses;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class NameValueParent<T> implements ParentListItem {

    private String id;
    private String name;

    private boolean isChecked;

    private boolean isDisable;


    private List<T> feedItemList;

    private Object adpter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<T> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<T> feedItemList) {
        this.feedItemList = feedItemList;
    }


    @Override
    public List<T> getChildItemList() {
        return feedItemList;
    }

    public void setChildItemList(List<T> list) {
        this.feedItemList = list;
    }

    public Object getAdpter() {
        return adpter;
    }

    public void setAdpter(Object adpter) {
        this.adpter = adpter;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
