package com.wishbook.catalog.home.more.visits.FIrstLevelExpandableView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.wishbook.catalog.home.more.visits.SecondLevelExpandableView.Cities;

import java.util.List;

/**
 * Created by root on 28/10/16.
 */
public class StateListItem implements ParentListItem {


    private String state_id;

    private List<Cities> cities;

    private String state;

    private String total_buyers;


    public StateListItem(String state_id, List<Cities> cities, String state, String total_buyers) {
        this.state_id = state_id;
        this.cities = cities;
        this.state = state;
        this.total_buyers = total_buyers;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public List<Cities> getCities() {
        return cities;
    }

    public void setCities(List<Cities> cities) {
        this.cities = cities;
    }

    public String getTotal_buyers() {
        return total_buyers;
    }

    public void setTotal_buyers(String total_buyers) {
        this.total_buyers = total_buyers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public List<Cities> getChildItemList() {
        return cities;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
