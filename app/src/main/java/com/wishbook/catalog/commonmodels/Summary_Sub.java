package com.wishbook.catalog.commonmodels;

import androidx.fragment.app.Fragment;

import com.wishbook.catalog.commonadapters.AllSectionAdapter;
import com.wishbook.catalog.commonadapters.CommonAnalyticsAdapter;
import com.wishbook.catalog.commonadapters.CommonSummaryAdapter;
import com.wishbook.catalog.commonadapters.HomeSupplierSuggestionRatingAdapter;
import com.wishbook.catalog.commonadapters.ShareBYMeSummaryAdapter;
import com.wishbook.catalog.commonadapters.SuggestionContactsAdapter;

/**
 * Created by Vigneshkarnika on 15/05/16.
 */
public class Summary_Sub {
    String text;
    String redirectorText;
    Fragment redirector;
    String uniqueID;
    String redirectorText1;
    Fragment redirector1;
    String inviteAs;

    String analytics_value;
    String analytics_label;


    SuggestionContactsAdapter suggestionContactsAdapter;
    CommonSummaryAdapter commonSummaryAdapter;
    ShareBYMeSummaryAdapter shareStatusAdapter;
    CommonAnalyticsAdapter commonAnalyticsAdapter;
    HomeSupplierSuggestionRatingAdapter homeSupplierSuggestionRatingAdapter;

    AllSectionAdapter allSectionAdapter;

    public Summary_Sub() {
    }

    public Summary_Sub(String text, String redirectorText, Fragment redirector, String uniqueID) {
        this.text = text;
        this.redirectorText = redirectorText;
        this.redirector = redirector;
        this.uniqueID = uniqueID;
        this.redirectorText1=null;
        this.redirector1=null;
    }
    public Summary_Sub(String text,Fragment redirector, String uniqueID) {
        this.text = text;
        this.redirectorText = null;
        this.redirector = redirector;
        this.uniqueID = uniqueID;
        this.redirectorText1=null;
        this.redirector1=null;
    }

    public Summary_Sub(ShareBYMeSummaryAdapter shareStatusAdapter) {
        this.shareStatusAdapter = shareStatusAdapter;
    }

    public ShareBYMeSummaryAdapter getShareStatusAdapter() {
        return shareStatusAdapter;
    }

    public void setShareStatusAdapter(ShareBYMeSummaryAdapter shareStatusAdapter) {
        this.shareStatusAdapter = shareStatusAdapter;
    }

    public Summary_Sub(SuggestionContactsAdapter suggestionContactsAdapter) {
        this.suggestionContactsAdapter = suggestionContactsAdapter;
    }


    public Summary_Sub(CommonSummaryAdapter commonSummaryAdapter) {
        this.commonSummaryAdapter = commonSummaryAdapter;
    }

    public Summary_Sub(CommonAnalyticsAdapter commonAnalyticsAdapter) {
        this.commonAnalyticsAdapter = commonAnalyticsAdapter;
    }

    public Summary_Sub(HomeSupplierSuggestionRatingAdapter homeSupplierSuggestionRatingAdapter) {
        this.homeSupplierSuggestionRatingAdapter = homeSupplierSuggestionRatingAdapter;
    }


    public HomeSupplierSuggestionRatingAdapter getHomeSupplierSuggestionRatingAdapter() {
        return homeSupplierSuggestionRatingAdapter;
    }

    public void setHomeSupplierSuggestionRatingAdapter(HomeSupplierSuggestionRatingAdapter homeSupplierSuggestionRatingAdapter) {
        this.homeSupplierSuggestionRatingAdapter = homeSupplierSuggestionRatingAdapter;
    }

    public Summary_Sub(AllSectionAdapter allSectionAdapter) {
        this.allSectionAdapter = allSectionAdapter;
    }

    public SuggestionContactsAdapter getSuggestionContactsAdapter() {
        return suggestionContactsAdapter;
    }

    public void setSuggestionContactsAdapter(SuggestionContactsAdapter suggestionContactsAdapter) {
        this.suggestionContactsAdapter = suggestionContactsAdapter;
    }

    public CommonSummaryAdapter getCommonSummaryAdapter() {
        return commonSummaryAdapter;
    }

    public void setCommonSummaryAdapter(CommonSummaryAdapter commonSummaryAdapter) {
        this.commonSummaryAdapter = commonSummaryAdapter;
    }

    public Summary_Sub(String text, String redirectorText, Fragment redirector, String uniqueID, String redirectorText1, Fragment redirector1) {
        this.text = text;
        this.redirectorText = redirectorText;
        this.redirector = redirector;
        this.uniqueID = uniqueID;
        this.redirectorText1 = redirectorText1;
        this.redirector1 = redirector1;
    }

    public String getInviteAs() {
        return inviteAs;
    }

    public void setInviteAs(String inviteAs) {
        this.inviteAs = inviteAs;
    }


    public String getRedirectorText1() {
        return redirectorText1;
    }

    public void setRedirectorText1(String redirectorText1) {
        this.redirectorText1 = redirectorText1;
    }

    public Fragment getRedirector1() {
        return redirector1;
    }

    public void setRedirector1(Fragment redirector1) {
        this.redirector1 = redirector1;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRedirectorText() {
        return redirectorText;
    }

    public void setRedirectorText(String redirectorText) {
        this.redirectorText = redirectorText;
    }

    public Fragment getRedirector() {
        return redirector;
    }

    public void setRedirector(Fragment redirector) {
        this.redirector = redirector;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getAnalytics_value() {
        return analytics_value;
    }

    public void setAnalytics_value(String analytics_value) {
        this.analytics_value = analytics_value;
    }

    public String getAnalytics_label() {
        return analytics_label;
    }

    public void setAnalytics_label(String analytics_label) {
        this.analytics_label = analytics_label;
    }

    public CommonAnalyticsAdapter getCommonAnalyticsAdapter() {
        return commonAnalyticsAdapter;
    }

    public void setCommonAnalyticsAdapter(CommonAnalyticsAdapter commonAnalyticsAdapter) {
        this.commonAnalyticsAdapter = commonAnalyticsAdapter;
    }

    public AllSectionAdapter getAllSectionAdapter() {
        return allSectionAdapter;
    }

    public void setAllSectionAdapter(AllSectionAdapter allSectionAdapter) {
        this.allSectionAdapter = allSectionAdapter;
    }
}
