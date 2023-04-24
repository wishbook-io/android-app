package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 15/05/16.
 */
public class SummaryModel {
    String headerTitle;
    ArrayList<Summary_Sub> summary_subs;


    public SummaryModel(String headerTitle, ArrayList<Summary_Sub> summary_subs) {
        this.headerTitle = headerTitle;
        this.summary_subs = summary_subs;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<Summary_Sub> getSummary_subs() {
        return summary_subs;
    }

    public void setSummary_subs(ArrayList<Summary_Sub> summary_subs) {
        this.summary_subs = summary_subs;
    }
}
