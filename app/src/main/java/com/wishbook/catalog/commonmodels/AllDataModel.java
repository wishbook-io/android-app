package com.wishbook.catalog.commonmodels;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vigneshkarnika on 16/05/16.
 */
public class AllDataModel {
    String headerTitle;
    ArrayList imageUrls;
    Fragment fragment;
    HashMap<String, String> params;
    int numberofGridsize =3;
    String type;

    public AllDataModel(String headerTitle, ArrayList imageUrls, Fragment fragment) {
        this.headerTitle = headerTitle;
        this.imageUrls = imageUrls;
        this.fragment = fragment;
    }



    public AllDataModel(String headerTitle, ArrayList imageUrls, Fragment fragment, int numberofGridsize) {
        this.headerTitle = headerTitle;
        this.imageUrls = imageUrls;
        this.fragment = fragment;
        this.numberofGridsize = numberofGridsize;
    }

    public AllDataModel(String headerTitle, ArrayList imageUrls, Fragment fragment, int numberofGridsize, HashMap<String, String> params) {
        this.headerTitle = headerTitle;
        this.imageUrls = imageUrls;
        this.fragment = fragment;
        this.numberofGridsize = numberofGridsize;
        this.params = params;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getNumberofGridsize() {
        return numberofGridsize;
    }

    public void setNumberofGridsize(int numberofGridsize) {
        this.numberofGridsize = numberofGridsize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
