package com.wishbook.catalog.commonmodels;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.Utils.StaticFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vigneshkarnika on 06/07/16.
 */
public class UserInfo {
    SharedPreferences pref;
    private Boolean manufacturer;
    private Boolean wholesaler_distributor;
    private Boolean retailer;
    private boolean online_retailer_reseller;
    private Boolean broker;
    private boolean isGuestUser;
    private boolean user_approval_status;
    private String freshchat_user_id;
    private int cart_count;

    public int getCart_count() {
        return cart_count;
    }

    public void setCart_count(int cart_count) {
        this.cart_count = cart_count;
    }

    public String getFreshchat_user_id() {
        return freshchat_user_id;
    }

    public void setFreshchat_user_id(String freshchat_user_id) {
        this.freshchat_user_id = freshchat_user_id;
    }
    public Boolean getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Boolean manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getWholesaler_distributor() {
        return wholesaler_distributor;
    }

    public void setWholesaler_distributor(Boolean wholesaler_distributor) {
        this.wholesaler_distributor = wholesaler_distributor;
    }

    public Boolean getRetailer() {
        return retailer;
    }

    public void setRetailer(Boolean retailer) {
        this.retailer = retailer;
    }

    public boolean getOnline_retailer_reseller() {
        return pref.getBoolean("online_retailer_reseller", false);
    }

    public void setOnline_retailer_reseller(boolean online_retailer_reseller) {
        pref.edit().putBoolean("online_retailer_reseller", online_retailer_reseller).apply();
    }

    public Boolean getBroker() {
        return pref.getBoolean("broker", false);
        //return broker;
    }

    public void setBroker(Boolean broker) {
        pref.edit().putBoolean("broker", broker).apply();
        this.broker = broker;
    }


    public boolean isGuest() {
        return pref.getBoolean("is_guest", false);
    }

    public void setGuest(boolean broker) {
        pref.edit().putBoolean("is_guest", broker).apply();
    }

    public boolean isUser_approval_status() {
        return pref.getBoolean("user_approval_status", true);
    }

    public void setUser_approval_status(boolean user_approval_status) {
        pref.edit().putBoolean("user_approval_status", user_approval_status).apply();
    }

    public boolean isSuper_User(){
        return pref.getBoolean("is_super_user",false);
    }

    public void setSuper_User(boolean is_super_user) {
        pref.edit().putBoolean("is_super_user", is_super_user).apply();
    }

    public String getLastLoggedIn(){
        return pref.getString("last_logged_in_date",null);
    }


    public boolean isAskSmsDialogShown() {
        return pref.getBoolean("isAskSmsDialogshown",false);
    }

    public void setAskSmsDialogShown(boolean value) {
        pref.edit().putBoolean("isAskSmsDialogshown", value).apply();
    }

    public void setLastLoggedIn() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            String lastLogged = sdf.format(cal.getTime());
            pref.edit().putString("last_logged_in_date", lastLogged).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getBranchRefLink() {
        return pref.getString("branch_ref_link", "");
    }

    public void setBranchRefLink(String branch_ref_link) {
        pref.edit().putString("branch_ref_link", branch_ref_link).apply();
    }

    public String getBranchUserReferralLink() {
        return pref.getString("branch_user_ref_link", "");
    }

    public void setBranchUserReferralLink(String branch_ref_link) {
        pref.edit().putString("branch_user_ref_link", branch_ref_link).apply();
    }

    public int getGuestUserSession() {
        return pref.getInt("guest_session_count", 0);
    }

    public void setGuestUserSession(int count) {
        pref.edit().putInt("guest_session_count", count).apply();
    }

    public boolean isPasswordSet() {
        return pref.getBoolean("isPasswordSet",true);
    }

    public void setPasswordset(boolean isPassword) {
        pref.edit().putBoolean("isPasswordSet",isPassword).apply();
    }

    public UserInfo(Context context) {
        pref = StaticFunctions.getAppSharedPreferences(context);
    }

    public static UserInfo getInstance(Context context) {
        if (context == null){
            return new UserInfo(Application_Singleton.getInstance().getApplicationContext());
        }
        return new UserInfo(context);
    }

    public String getDeputed_to() {
        return pref.getString("deputed_to", "");
    }

    public void setDeputed_to(String deputed_to) {
        pref.edit().putString("deputed_to", deputed_to).apply();
    }

    public String getDeputed_to_name() {
        return pref.getString("deputed_to_name", "");
    }

    public void setDeputed_to_name(String deputed_to_name) {
        pref.edit().putString("deputed_to_name", deputed_to_name).apply();
    }


    public String getKey() {
        return pref.getString("key", "");
    }

    public void setKey(String key) {
        pref.edit().putString("key", key).apply();
    }

    public String getCompany_type() {
        return pref.getString("company_type", "buyer");
    }

    public void setCompany_type(String company_type) {
        pref.edit().putString("company_type", company_type).apply();
    }


    public String getContactStatus() {
        return pref.getString("contact_status", "init");
    }

    public void setContactStatus(String status) {
        pref.edit().putString("contact_status", status).apply();
    }

    public String getWishBookContactStatus() {
        return pref.getString("wishbook_contact_status", "init");
    }

    public void setWishBookContactStatus(String status) {
        pref.edit().putString("wishbook_contact_status", status).apply();
    }

    public String getLanguage() {
        return pref.getString("profile_language", "hi");
    }

    public void setLanguage(String languageId) {
        pref.edit().putString("profile_language", languageId).apply();
    }


    public String getUserStats(){
        return pref.getString("user_stats","");
    }

    public void setUserStats(String stats) {
        pref.edit().putString("user_stats", stats).apply();
    }


    public int getWishlistCount() {
        return pref.getInt("wishlist_count", 0);
    }

    public void setWishlistCount(int wishlistCount) {
        pref.edit().putInt("wishlist_count", wishlistCount).apply();
    }

    public boolean isLanguageSet() {
        return pref.getBoolean("language_set", false);
    }

    public void setLanguageSet(boolean isSet) {
        pref.edit().putBoolean("language_set", isSet).apply();
    }

    public boolean isCreditRatingApply() {
        return pref.getBoolean("credit_rating", false);
    }

    public void setCreditRating(boolean isSet) {
        pref.edit().putBoolean("credit_rating", isSet).apply();
    }


    public boolean isBranchReferral() {
        return pref.getBoolean("isbranch_referral", false);
    }

    public void setBranchReferral(boolean isSet) {
        pref.edit().putBoolean("isbranch_referral", isSet).apply();
    }


    public String getCreditScore() {
        return pref.getString("credit_rating_score", "null");
    }

    public void setCreditScore(String creditScore) {
        pref.edit().putString("credit_rating_score", creditScore).apply();
    }

    public String getUserWishlistProduct() {
        return pref.getString("user_wishlist_product", "");
    }

    public void setUserWishlistProduct(String userWishlistProduct) {
        pref.edit().putString("user_wishlist_product", userWishlistProduct).apply();
    }


    public boolean isCompanyProfileSet() {
        return pref.getBoolean("is_profile_set", true);
    }

    public void setCompanyProfileSet(boolean is_profile_set) {
        pref.edit().putBoolean("is_profile_set", is_profile_set).apply();
    }


    public String getSupplierApprovalStatus() {
        return pref.getString("company_seller_detail", null);
    }

    public void setSupplierApprovalStatus(String approvalStatus){
        pref.edit().putString("company_seller_detail",approvalStatus).apply();
    }

    public boolean isApprovedMsgShow() {
        return pref.getBoolean("seller_approved", false);
    }

    public void setApprovedMsgShow(boolean isShow){
        pref.edit().putBoolean("seller_approved", isShow).apply();
    }


    public String getBankDetails(){
        return pref.getString("user_bank_details", null);
    }

    public void setBankDetails(String bankDetails) {
        pref.edit().putString("user_bank_details", bankDetails).apply();
    }

    public String getKyc(){
        return pref.getString("user_kyc_details", null);
    }

    public void setKyc(String kycDetails) {
        pref.edit().putString("user_kyc_details", kycDetails).apply();
    }


    public String getCompanyType() {
        return pref.getString("companytype", "");
    }

    public void setCompanyType(String companytype) {
        pref.edit().putString("companytype", companytype).apply();
    }

    public String getTotalBrandFollowers() {
        return pref.getString("total_brand_followers", "");
    }

    public void setTotalBrandFollowers(String brandFollowers) {
        pref.edit().putString("total_brand_followers", brandFollowers).apply();
    }

    public String getTotalMyCatalogs() {
        return pref.getString("total_my_catalogs", "");
    }

    public void setTotalMyCatalogs(String totalMyCatalogs) {
        pref.edit().putString("total_my_catalogs", totalMyCatalogs).apply();
    }

    public String getUserId() {
        return pref.getString("userId", "");
    }

    public void setUserId(String userId) {
        pref.edit().putString("userId", userId).apply();
    }

    public String getUserName() {
        return pref.getString("userName", "");
    }

    public void setUserName(String userName) {
        pref.edit().putString("userName", userName).apply();
    }

    public String getFirstName() {
        return pref.getString("firstName", "");
    }

    public void setFirstName(String firstName) {
        pref.edit().putString("firstName", firstName).apply();
    }

    public String getLastName() {
        return pref.getString("lastName", "");
    }

    public void setLastName(String lastName) {
        pref.edit().putString("lastName", lastName).apply();
    }

    public String getPassword() {
        return pref.getString("password", "");
    }

    public void setPassword(String password) {
        pref.edit().putString("password", password).apply();
    }

    public String getEmail() {
        return pref.getString("email", "");
    }

    public void setEmail(String email) {
        pref.edit().putString("email", email).apply();
    }

    public String getProfileimage() {
        return pref.getString("profileimage", "");
    }

    public void setProfileimage(String profileimage) {
        pref.edit().putString("profileimage", profileimage).apply();
    }

    public String getMobile() {
        return pref.getString("mobile", "");
    }

    public void setMobile(String mobile) {
        pref.edit().putString("mobile", mobile).apply();
    }

    public String getGroupstatus() {
        return pref.getString("groupstatus", "");
    }

    public void setGroupstatus(String groupstatus) {
        pref.edit().putString("groupstatus", groupstatus).apply();
    }

    public String getUserCompanyAddress() {
        return pref.getString("companyAddress", "");
    }

    public void setUserCompanyAddress(String address) {
        pref.edit().putString("companyAddress", address).apply();
    }


    public String getCompanyCity() {
        return pref.getString("companyCity", "");
    }

    public void setCompanyCity(String address) {
        pref.edit().putString("companyCity", address).apply();
    }

    public String getCompanyCityName() {
        return pref.getString("companyCityName", "");
    }

    public void setCompanyCityName(String cityName) {
        pref.edit().putString("companyCityName", cityName).apply();
    }

    public String getCompanyStateName() {
        return pref.getString("companyStateName", "");
    }

    public void setCompanyStateName(String cityName) {
        pref.edit().putString("companyStateName", cityName).apply();
    }

    public String getCompanyGroupFlag() {
        return pref.getString("companyGroupFlag", "");
    }

    public void setCompanyGroupFlag(String groupFlag) {
        pref.edit().putString("companyGroupFlag", groupFlag).apply();
    }


    public boolean isOrderDisabled() {
        return pref.getBoolean("order_disabled", false);
    }

    public void setOrderDisabled(boolean orderDisabled) {
        pref.edit().putBoolean("order_disabled", orderDisabled).apply();
    }


    public String getBrandadded() {
        return pref.getString("brandadded", "");
    }

    public void setBrandadded(String brandadded) {
        pref.edit().putString("brandadded", brandadded).apply();
    }

    public String getCompany_id() {
        return pref.getString("company_id", "");
    }

    public void setCompany_id(String company_id) {
        pref.edit().putString("company_id", company_id).apply();
    }

    public String getCompanyname() {
        return pref.getString("companyname", "");
    }

    public void setCompanyname(String companyname) {
        pref.edit().putString("companyname", companyname).apply();
    }

    public int getlasttabselected() {
        return pref.getInt("tab", 1);
    }

    public void settabselected(int tab) {
        pref.edit().putInt("tab", tab).apply();
    }

    public int getlastsubtabselected() {
        return pref.getInt("subtab", 1);
    }

    public void selastsubtabselected(int subtab) {
        pref.edit().putInt("subtab", subtab).apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        pref.edit().putBoolean("isLoggedIn", isLoggedIn).apply();
    }

    public String getcontacts() {
        return pref.getString("contacts", "");
    }

    public String getDefaultProductView() {
        return pref.getString("defaultProductView", Constants.PRODUCT_VIEW_GRID);
    }

    public void setDefaultProductView(String view) {
        pref.edit().putString("defaultProductView", view).apply();
    }

    public String getDefaultSortPref() {
        return pref.getString("defaultSortProductPref", "-id");
    }

    public void setDefaultSortPref(String sortPref) {
        pref.edit().putString("defaultSortProductPref", sortPref).apply();
    }

    public void setContacts(String contacts) {
        pref.edit().putString("contacts", contacts).apply();
    }

    public String getResaleDefaultMargin() {
        return pref.getString("resale_default_margin", null);
    }

    public void setResaleDefaultMargin(String margin) {
        pref.edit().putString("resale_default_margin", margin).commit();
    }

    public String getwishbookcontacts() {
        return pref.getString("wcontacts", "");
    }

    public ArrayList<MyContacts> getwishSuggestioncontacts() {
        ArrayList<MyContacts> contactsList = new ArrayList<>();
        MyContacts[] contacts = new Gson().fromJson(pref.getString("wscontacts", ""), MyContacts[].class);
        if (contacts != null && !contacts.equals("")) {
            for (MyContacts contacts1 : contacts) {
                contactsList.add(contacts1);

            }
        } else {
            contactsList = new ArrayList<>();
        }
        return contactsList;
    }

    public void setwishContacts(String wcontacts) {
        pref.edit().putString("wcontacts", wcontacts).apply();
    }

    public void setwishSuggestionContacts(String wcontacts) {
        pref.edit().putString("wscontacts", wcontacts).apply();
    }

    public String getAllCompanyType() {
        return pref.getString("allComapnytype", "");
    }

    public void setAllCompanyType(String companyType) {
        pref.edit().putString("allComapnytype", companyType).apply();
    }


    //buyers_assigned_to_salesman
    public Boolean getBuyerSplitSalesperson() {
        return pref.getBoolean("buyers_assigned_to_salesman", false);
    }

    public void setBuyerSplitSalesperson(Boolean buyerSplitSalesperson) {
        pref.edit().putBoolean("buyers_assigned_to_salesman", buyerSplitSalesperson).apply();
    }


}
