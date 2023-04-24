package com.wishbook.catalog.home.myBusiness;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.freshchat.consumer.sdk.Freshchat;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.CompanyTypePatch;
import com.wishbook.catalog.commonmodels.responses.ResponseStatistics;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.Fragment_ShareStatus;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Brands;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_WishList;
import com.wishbook.catalog.home.contacts.Fragment_ContactsHolder;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;
import com.wishbook.catalog.home.more.ActivityLanguage;
import com.wishbook.catalog.home.more.Activity_ShareApp;
import com.wishbook.catalog.home.more.Fragment_AboutUs_Policies;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.more.Fragment_BuyerGroups;
import com.wishbook.catalog.home.more.Fragment_BuyersRejected;
import com.wishbook.catalog.home.more.Fragment_ChangePassword;
import com.wishbook.catalog.home.more.Fragment_GST_2;
import com.wishbook.catalog.home.more.Fragment_Home;
import com.wishbook.catalog.home.more.Fragment_MyCompanyProfile;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.more.Fragment_SupplierRejected;
import com.wishbook.catalog.home.more.Fragment_Sync;
import com.wishbook.catalog.home.more.brandDiscount.ActivityBrandwiseDiscountList;
import com.wishbook.catalog.home.more.myearning.Fragment_MyEarningHolder;
import com.wishbook.catalog.home.more.myviewers_2.Fragment_My_Viewers;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orders.Fragment_Order_Holder_Version4;
import com.wishbook.catalog.login.Fragment_Register_New_Version2;
import com.wishbook.catalog.reseller.Fragment_ResellerHolder;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MyBusiness extends GATrackedFragment implements View.OnClickListener {

    @BindView(R.id.relative_rejected_users)
    RelativeLayout relative_rejected_users;

    @BindView(R.id.arrow_img2)
    ImageView arrow_img2;

    @BindView(R.id.linear_rejceted_child)
    LinearLayout linear_rejceted_child;

    @BindView(R.id.setting_rejected_buyer)
    TextView setting_rejected_buyer;

    @BindView(R.id.setting_rejected_supplier)
    TextView setting_rejected_supplier;

    @BindView(R.id.settings_wb_money)
    LinearLayout settings_wb_money;


    @BindView(R.id.setting_rejected_buyer_seller)
    TextView setting_rejected_buyer_seller;

    @BindView(R.id.setting_rejected_supplier_retailer)
    TextView setting_rejected_supplier_retailer;

    @BindView(R.id.settings_my_brands)
    LinearLayout settings_my_brands;

    @BindView(R.id.settings_discount)
    LinearLayout settings_discount;

    @BindView(R.id.setting_my_catalog)
    LinearLayout setting_my_catalog;

    @BindView(R.id.settings_my_buyer_groups)
    TextView settings_my_buyer_groups;

    @BindView(R.id.settings_gst)
    TextView settings_gst;

    @BindView(R.id.setting_register_new_user)
    TextView setting_register_new_user;

    @BindView(R.id.setting_my_followers)
    LinearLayout setting_my_followers;

    @BindView(R.id.setting_scan_qr_code)
    LinearLayout setting_scan_qr_code;

    @BindView(R.id.txt_firstname)
    TextView txt_firstname;

    @BindView(R.id.txt_companyname)
    TextView txt_companyname;

    @BindView(R.id.txt_companytype)
    TextView txt_companytype;

    @BindView(R.id.txt_first_initialize)
    TextView txt_first_initialize;

    @BindView(R.id.setting_my_orders)
    LinearLayout setting_my_orders;

    @BindView(R.id.setting_my_enquiries)
    LinearLayout setting_my_enquiries;

    @BindView(R.id.linear_main_root)
    LinearLayout linear_main_root;

    @BindView(R.id.linear_profile)
    LinearLayout linear_profile;


    @BindView(R.id.setting_my_network)
    LinearLayout setting_my_network;

    @BindView(R.id.setting_add_catalog)
    LinearLayout setting_add_catalog;

    @BindView(R.id.settings_shared_by_me)
    LinearLayout settings_shared_by_me;

    @BindView(R.id.linear_seller_function2)
    LinearLayout linear_seller_function2;

    @BindView(R.id.linear_seller_function3)
    LinearLayout linear_seller_function3;

    @BindView(R.id.linear_seller_function5)
    LinearLayout linear_seller_function5;

    @BindView(R.id.setting_my_wishlist)
    LinearLayout setting_my_wishlist;

    @BindView(R.id.txt_leads_label)
    TextView txt_leads_label;

    @BindView(R.id.setting_brand_i_follow)
    LinearLayout setting_brand_i_follow;

    @BindView(R.id.txt_my_followers_value)
    TextView txt_my_followers_value;

    @BindView(R.id.txt_my_viewers_value)
    TextView txt_my_viewers_value;

    @BindView(R.id.txt_my_wishlist_value)
    TextView txt_my_wishlist_value;

    @BindView(R.id.txt_brand_i_follow_value)
    TextView txt_brand_i_follow_value;

    @BindView(R.id.scroll_view)
    ScrollView scroll_view;

    @BindView(R.id.linear_wishlist_brand_i_follow)
    LinearLayout linear_wishlist_brand_i_follow;

    @BindView(R.id.linear_apply_rating)
    LinearLayout linear_apply_rating;

    @BindView(R.id.txt_apply_credit)
    AppCompatButton txt_apply_credit;

    @BindView(R.id.card_apply_rating)
    CardView card_apply_rating;

    @BindView(R.id.linear_my_viewers)
    LinearLayout linear_my_viewers;

    @BindView(R.id.linear_reseller)
    LinearLayout linear_reseller;

    @BindView(R.id.txt_resale_payout)
    LinearLayout txt_resale_payout;

    @BindView(R.id.txt_resale_customer_address)
    LinearLayout txt_resale_customer_address;

    @BindView(R.id.txt_resale_bank_account)
    LinearLayout txt_resale_bank_account;

    @BindView(R.id.switch_resale_hub)
    Switch switch_resale_hub;

    @BindView(R.id.linear_resale_sub_parts)
    LinearLayout linear_resale_sub_parts;

    @BindView(R.id.linear_resale_sub_parts1)
    LinearLayout linear_resale_sub_parts1;

    @BindView(R.id.settings_reward_points)
    LinearLayout settings_reward_points;

    @BindView(R.id.setting_refer_earn)
    LinearLayout setting_refer_earn;

    @BindView(R.id.setting_incentive)
    LinearLayout setting_incentive;

    @BindView(R.id.cart)
    ImageView cart;

    @BindView(R.id.txt_how_to_sell_layout)
    LinearLayout txt_how_to_sell_layout;

    @BindView(R.id.txt_shared_by_me)
    LinearLayout txt_shared_by_me;

    @BindView(R.id.setting_seller_hub)
    TextView setting_seller_hub;

    @BindView(R.id.badge_cart_count)
    MaterialBadgeTextView cart_count;


    private boolean isRejectedUserExpand = false;

    public static SharedPreferences pref, profilePref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_business, container, false);
        ButterKnife.bind(this, view);
        pref = StaticFunctions.getAppSharedPreferences(getActivity());
        profilePref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        if (UserInfo.getInstance(getActivity()).isGuest()) {
            view.findViewById(R.id.linear_main_root).setVisibility(View.GONE);
            view.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            Log.d("TAG", "onCreateView:  Guest");
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                Log.d("TAG", "onCreateView:  Guest Pending");
                view.findViewById(R.id.linear_dialog_register_root).setVisibility(View.GONE);
                View dialog = view.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(), dialog);
                return view;
            } else {
                Log.d("TAG", "onCreateView:  Guest Login");
                view.findViewById(R.id.linear_main_pending).setVisibility(View.GONE);
                View dialog = view.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog, "my_business_tab");
                return view;
            }
        }
        initView(view);

        // Add temporary
        hideOrderDisableConfig();
        return view;
    }

    /**
     *
     * @param view
     */
    public void initView(View view) {
        cart_count = view.findViewById(R.id.badge_cart_count);
        /**
         * Changes Done By Bhavik Gandhi-April 24
         * Hide due to introduced Seller Hub,duplicate
         */
        //linear_seller_function3.setVisibility(View.GONE);
        //linear_seller_function2.setVisibility(View.GONE);
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                cart_count.setBadgeCount(0);
            } else {
                cart_count.setBadgeCount(cartcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        txt_companytype.setText(UserInfo.getInstance(getActivity()).getAllCompanyType());
        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            setting_seller_hub.setVisibility(View.VISIBLE);
            cart.setVisibility(View.INVISIBLE);
            cart_count.setVisibility(View.INVISIBLE);
            setting_rejected_buyer_seller.setVisibility(View.VISIBLE);
            setting_rejected_supplier_retailer.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.GONE);
            linear_wishlist_brand_i_follow.setVisibility(View.GONE);
            linear_seller_function5.setVisibility(View.GONE);
            txt_leads_label.setText("Leads");

            linear_apply_rating.setVisibility(View.GONE);

        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            setting_seller_hub.setVisibility(View.GONE);
            txt_leads_label.setText("Enquiries");
            setting_rejected_supplier_retailer.setVisibility(View.VISIBLE);
            setting_rejected_buyer_seller.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.GONE);
            settings_my_buyer_groups.setVisibility(View.GONE);


            linear_rejceted_child.setVisibility(View.GONE);

            linear_seller_function2.setVisibility(View.GONE);
            linear_seller_function3.setVisibility(View.GONE);
            linear_seller_function5.setVisibility(View.GONE);

          /*  if (UserInfo.getInstance(getActivity()).isCreditRatingApply()) {
                linear_apply_rating.setVisibility(View.GONE);
            } else {
                linear_apply_rating.setVisibility(View.VISIBLE);
            }*/
        } else {
            txt_leads_label.setText("Leads/Enquiries");
            setting_rejected_supplier_retailer.setVisibility(View.GONE);
            setting_rejected_buyer_seller.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.VISIBLE);
            linear_seller_function5.setVisibility(View.GONE);

           /* if (UserInfo.getInstance(getActivity()).isCreditRatingApply()) {
                linear_apply_rating.setVisibility(View.GONE);
            } else {
                linear_apply_rating.setVisibility(View.VISIBLE);
            }*/
        }

        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);
        if (companyGroupFlag != null && (companyGroupFlag.getRetailer() || companyGroupFlag.getOnline_retailer_reseller())) {
            linear_reseller.setVisibility(View.VISIBLE);
            if (companyGroupFlag.getRetailer()) {
                switch_resale_hub.setChecked(false);
                linear_resale_sub_parts.setVisibility(View.GONE);
                linear_resale_sub_parts1.setVisibility(View.GONE);
            }


            if (companyGroupFlag.getOnline_retailer_reseller()) {
                switch_resale_hub.setChecked(true);
                linear_resale_sub_parts.setVisibility(View.VISIBLE);
                linear_resale_sub_parts1.setVisibility(View.VISIBLE);
            }

        } else {
            linear_reseller.setVisibility(View.GONE);
        }


        if(StaticFunctions.isOnlyReseller(getActivity())) {
            settings_gst.setVisibility(View.GONE);
        }

        /**
         * WB-4199 Hide Register new user and Buyer Group Feature
         * Changes Feb-4 2019 by Bhavik Gandhi
         */
        setting_register_new_user.setVisibility(View.GONE);
        settings_my_buyer_groups.setVisibility(View.GONE);
        setting_rejected_buyer_seller.setVisibility(View.GONE);
        setting_rejected_supplier_retailer.setVisibility(View.GONE);
        relative_rejected_users.setVisibility(View.GONE);
        setClickListener();
        initData();
    }

    /**
     * set data for view
     */
    public void initData() {
        if (PrefDatabaseUtils.getPrefStatistics(getActivity()) != null) {
            ResponseStatistics statistics = new Gson().fromJson(PrefDatabaseUtils.getPrefStatistics(getActivity()), new TypeToken<ResponseStatistics>() {
            }.getType());
            txt_my_followers_value.setText(String.valueOf(statistics.getMy_followers()));
            txt_my_viewers_value.setText(String.valueOf(statistics.getMy_viewers()));
            txt_my_wishlist_value.setText(String.valueOf(statistics.getWishlist() + " Products"));
            txt_brand_i_follow_value.setText(String.valueOf(statistics.getBrand_i_follow() + " Brands"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                cart_count.setBadgeCount(0);
            } else {
                cart_count.setBadgeCount(cartcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profilePref != null) {
            txt_companyname.setText(profilePref.getString("companyname", ""));
            String name = profilePref.getString("firstName", "") + " " + profilePref.getString("lastName", "");
            txt_firstname.setText(name);

            if (name != null && !name.isEmpty()) {
                txt_first_initialize.setText(name.toString().substring(0, 1).toUpperCase());
            }
            /** hide Credit Rating feature
             * changes done by bhavik gandhi April-8 2019
             */
            card_apply_rating.setVisibility(View.GONE);

            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller") && !StaticFunctions.isOnlyReseller(getActivity())) {
               /* if (UserInfo.getInstance(getActivity()).isCreditRatingApply()) {
                    card_apply_rating.setVisibility(View.GONE);
                    linear_apply_rating.setVisibility(View.GONE);
                } else {
                    if (Application_Singleton.IS_COMPANY_RATING_GET) {
                        card_apply_rating.setVisibility(View.VISIBLE);
                        linear_apply_rating.setVisibility(View.VISIBLE);
                    } else {
                        card_apply_rating.setVisibility(View.GONE);
                        linear_apply_rating.setVisibility(View.GONE);
                    }
                }*/
            } else {
                cart_count.setVisibility(View.INVISIBLE);
                cart.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setClickListener() {

        settings_my_buyer_groups.setOnClickListener(this);
        settings_my_brands.setOnClickListener(this);
        settings_gst.setOnClickListener(this);

        setting_register_new_user.setOnClickListener(this);
        setting_rejected_buyer.setOnClickListener(this);
        setting_rejected_supplier.setOnClickListener(this);
        setting_rejected_buyer_seller.setOnClickListener(this);
        setting_rejected_supplier_retailer.setOnClickListener(this);
        settings_wb_money.setOnClickListener(this);

        settings_discount.setOnClickListener(this);
        setting_my_catalog.setOnClickListener(this);
        setting_my_followers.setOnClickListener(this);

        setting_scan_qr_code.setOnClickListener(this);
        setting_my_orders.setOnClickListener(this);
        setting_my_enquiries.setOnClickListener(this);

        setting_my_network.setOnClickListener(this);
        linear_profile.setOnClickListener(this);

        setting_add_catalog.setOnClickListener(this);
        settings_shared_by_me.setOnClickListener(this);

        setting_my_wishlist.setOnClickListener(this);
        setting_brand_i_follow.setOnClickListener(this);

        linear_apply_rating.setOnClickListener(this);
        linear_my_viewers.setOnClickListener(this);

        linear_reseller.setOnClickListener(this);

        txt_resale_payout.setOnClickListener(this);
        txt_resale_bank_account.setOnClickListener(this);
        txt_resale_customer_address.setOnClickListener(this);

        settings_reward_points.setOnClickListener(this);
        setting_refer_earn.setOnClickListener(this);
        setting_incentive.setOnClickListener(this);
        txt_how_to_sell_layout.setOnClickListener(this);
        txt_shared_by_me.setOnClickListener(this);

        setting_seller_hub.setOnClickListener(this);

        switch_resale_hub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    patchCompany(true);
                    linear_resale_sub_parts.setVisibility(View.VISIBLE);
                    linear_resale_sub_parts1.setVisibility(View.VISIBLE);
                    try {
                        Snackbar snackbar = Snackbar
                                .make(getView(), "You can now create resale orders on Wishbook", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setAlpha(1f);
                        snackbar.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                            .content("Are you sure you want to opt-out from Resell on Wishbook?")
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    switch_resale_hub.setChecked(true);

                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    patchCompany(false);
                                    dialog.dismiss();
                                    linear_resale_sub_parts.setVisibility(View.GONE);
                                    linear_resale_sub_parts1.setVisibility(View.GONE);
                                }
                            })
                            .show();
                }
            }
        });

        isRejectedUserExpand = true;
        relative_rejected_users.performClick(); // for hide firsttime
        relative_rejected_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRejectedUserExpand) {
                    isRejectedUserExpand = false;
                    int padding = StaticFunctions.dpToPx(getActivity(), 16);
                    linear_rejceted_child.setVisibility(View.VISIBLE);
                    arrow_img2.setRotation(180);
                    linear_rejceted_child.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.smoothScrollBy(0, 150);
                        }
                    });
                } else {
                    isRejectedUserExpand = true;
                    linear_rejceted_child.setVisibility(View.GONE);
                    arrow_img2.setRotation(0);
                }
            }
        });

        cart.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_help:
                Application_Singleton.CONTAINER_TITLE = "Help";
                Application_Singleton.CONTAINERFRAG = new Fragment_Home();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.HELP);
                startActivity(intent);
                break;
            case R.id.settings_gst:
                Application_Singleton.CONTAINER_TITLE = "KYC and Bank Details";
                Application_Singleton.CONTAINERFRAG = new Fragment_GST_2();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.settings_change_password:
                Application_Singleton.CONTAINER_TITLE = "Change Password";
                Application_Singleton.CONTAINERFRAG = new Fragment_ChangePassword();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.settings_my_buyer_groups:
                if (pref.getString("groupstatus", "0").equals("1")) {
                    Application_Singleton.CONTAINER_TITLE = "Buyer Groups";
                    Application_Singleton.CONTAINERFRAG = new Fragment_BuyerGroups();
                    Intent intent1 = new Intent(getContext(), OpenContainer.class);
                    intent1.putExtra("toolbarCategory", OpenContainer.NEW_BUYER_GROUP);
                    startActivity(intent1);
                } else {

                }
                break;
            case R.id.settings_my_brands:
                if (pref.getString("groupstatus", "0").equals("1") && UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    if (pref.getString("brandadded", "no").equals("no")) {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_AddBrand();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    } else {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    }
                } else {

                    Application_Singleton.CONTAINER_TITLE = "My Brands";
                    Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                }
                break;
            case R.id.settings_profile:
                Application_Singleton.CONTAINER_TITLE = "Profile";
                Application_Singleton.CONTAINERFRAG = new Fragment_Profile();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);


                break;
            case R.id.settings_company_profile:
                Application_Singleton.CONTAINER_TITLE = "Company Profile";
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCompanyProfile();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.settings_register_new_user:
                Application_Singleton.CONTAINER_TITLE = "New Registration";
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                Application_Singleton.CONTAINERFRAG = new Fragment_Register_New_Version2();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.settings_sync:
                Application_Singleton.CONTAINER_TITLE = "Sync";
                Application_Singleton.CONTAINERFRAG = new Fragment_Sync();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                break;

            case R.id.settings_chat:
                Freshchat.showConversations(getActivity().getApplicationContext());
                break;
            case R.id.settings_faq:
                Freshchat.showFAQs(getActivity().getApplicationContext());
                break;
            case R.id.settings_discount:
               /* Application_Singleton.CONTAINER_TITLE = "Discount settings";
                Application_Singleton.CONTAINERFRAG = new Fragment_DiscountSetting();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);*/
                StaticFunctions.switchActivity(getActivity(), ActivityBrandwiseDiscountList.class);
                break;
            case R.id.settings_wb_money:
                navigateMyEarning("wbmoney");
                //StaticFunctions.switchActivity(getActivity(), Activity_Wb_Money.class);
                break;
            case R.id.settings_reward_points:
                navigateMyEarning("reward");
                break;
            case R.id.setting_logout:
                new MaterialDialog.Builder(getActivity())
                        .title("Logout")
                        .content(getResources().getString(R.string.logout_popup))
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                LogoutCommonUtils.logout(getActivity(), false);
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                        .show();
                break;
            case R.id.setting_my_catalog:
                Bundle bundle = new Bundle();
                Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                fragmentCatalogs.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "My Products";
                Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                Intent intent2 = new Intent(getActivity(), OpenContainer.class);
                //intent2.putExtra("toolbarCategory", OpenContainer.ADD_CATALOG);
                startActivity(intent2);
                break;
            case R.id.setting_my_followers:
                Intent intent1 = new Intent(getActivity(), Activity_BuyerSearch.class);
                intent1.putExtra("toolbarTitle", "Search Followers");
                intent1.putExtra("type", "myfollowers");
                startActivity(intent1);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "MyFollowerList_screen", "My business page", null);
                break;
            case R.id.settings_shared_by_me:
                Fragment_ShareStatus fragment_shareStatus = new Fragment_ShareStatus();
                Application_Singleton.CONTAINER_TITLE = "Shared By Me";
                Application_Singleton.CONTAINERFRAG = fragment_shareStatus;
                Intent shared_by_me_intent = new Intent(getActivity(), OpenContainer.class);
                startActivity(shared_by_me_intent);
                break;
            case R.id.linear_profile:
                Application_Singleton.CONTAINER_TITLE = "Profile";
                Application_Singleton.CONTAINERFRAG = new Fragment_Profile();
                Intent profileIntent = new Intent(getActivity(), OpenContainer.class);
                startActivity(profileIntent);
                break;
            case R.id.settings_change_language:
                Intent languageIntent = new Intent(getActivity(), ActivityLanguage.class);
                startActivityForResult(languageIntent, 1);
                break;
            case R.id.setting_register_new_user:
                Application_Singleton.CONTAINER_TITLE = "New Registration";
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                Application_Singleton.CONTAINERFRAG = new Fragment_Register_New_Version2();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.settings_about_us:
                Application_Singleton.CONTAINER_TITLE = "About us & Policies";
                Application_Singleton.CONTAINERFRAG = new Fragment_AboutUs_Policies();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.setting_rejected_buyer:
                Application_Singleton.CONTAINER_TITLE = "Buyers Rejected";
                Application_Singleton.CONTAINERFRAG = new Fragment_BuyersRejected();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.setting_rejected_supplier:
                Application_Singleton.CONTAINER_TITLE = "Suppliers Rejected";
                Application_Singleton.CONTAINERFRAG = new Fragment_SupplierRejected();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.setting_rejected_supplier_retailer:
                Application_Singleton.CONTAINER_TITLE = "Suppliers Rejected";
                Application_Singleton.CONTAINERFRAG = new Fragment_SupplierRejected();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.setting_rejected_buyer_seller:
                Application_Singleton.CONTAINER_TITLE = "Buyers Rejected";
                Application_Singleton.CONTAINERFRAG = new Fragment_BuyersRejected();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.setting_scan_qr_code:
                String[] permissions = {
                        "android.permission.CAMERA",
                };
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 900);
                } else {
                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                    Fragment_MyBusiness.this.startActivityForResult(i, 9000);
                }
                break;

            case R.id.settings_share_app:
                StaticFunctions.switchActivity(getActivity(), Activity_ShareApp.class);
                break;
            case R.id.setting_my_orders: {
                Bundle bundle1 = new Bundle();
                Fragment_Order_Holder_Version4 fragment = new Fragment_Order_Holder_Version4();
                bundle1.putBoolean("showOnlyOrders", true);
                fragment.setArguments(bundle1);
                Application_Singleton.CONTAINER_TITLE = "My Orders";
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }
            break;
            case R.id.setting_my_enquiries: {
                Bundle bundle1 = new Bundle();
                Fragment_Order_Holder_Version4 fragment = new Fragment_Order_Holder_Version4();
                bundle1.putBoolean("showOnlyEnquiries", true);
                fragment.setArguments(bundle1);
                if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                    Application_Singleton.CONTAINER_TITLE = "Leads";
                } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    Application_Singleton.CONTAINER_TITLE = "Enquiries";
                } else {
                    Application_Singleton.CONTAINER_TITLE = "Leads/Enquiries";
                }
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }
            break;
            case R.id.setting_my_network:
                Application_Singleton.CONTAINER_TITLE = "My Network";
                Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.setting_add_catalog:
                if(!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                        && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
                    HashMap<String,String> param =  new HashMap<String,String>();
                    param.put("type","profile_update");
                    new DeepLinkFunction(param,getActivity());
                    return;
                }
                StaticFunctions.switchActivity(getActivity(), Activity_AddCatalog.class);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Add_screen", "My business page", null);
                break;

            case R.id.setting_my_wishlist:
                Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);


                // StaticFunctions.switchActivity(getActivity(), ActivityBrandwiseDiscountList.class);


                break;
            case R.id.setting_brand_i_follow:
                Bundle bundle1 = new Bundle();
                Fragment_Brands fragment = new Fragment_Brands();
                bundle1.putBoolean("isFromBrandsIFollow", true);
                fragment.setArguments(bundle1);
                Application_Singleton.CONTAINER_TITLE = "Brands I Follow";
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.linear_apply_rating:
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("type", "credit_rating");
                hashmap.put("from", Fragment_MyBusiness.class.getSimpleName());
                new DeepLinkFunction(hashmap, getActivity());
                break;

            case R.id.linear_my_viewers:
                Application_Singleton.CONTAINER_TITLE = "My Viewers";
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                Application_Singleton.CONTAINERFRAG = new Fragment_My_Viewers();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "MyViewersList_screen", "My business page", null);
                break;

            case R.id.cart:
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cart_count = preferences.getInt("cartcount", 0);
                    Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.linear_reseller:
                //navigateReseller("payout");
                break;

            case R.id.txt_resale_payout:
                navigateReseller("payout");
                break;
            case R.id.txt_resale_bank_account:
                navigateReseller("bankaccount");
                break;
            case R.id.txt_resale_customer_address:
                navigateReseller("address");
                break;

            case R.id.txt_shared_by_me:
                navigateReseller("sharedbyme");
                break;

            case R.id.setting_incentive:
                navigateMyEarning("incentive");
                break;

            case R.id.setting_refer_earn:
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "refer_earn");
                hashMap.put("from","My Business Page");
                new DeepLinkFunction(hashMap, getActivity());
                break;
            case R.id.txt_how_to_sell_layout: {
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "webview");
                if (LocaleHelper.getLanguage(getActivity()) != null) {
                    if (LocaleHelper.getLanguage(getActivity()).equals("en")) {
                        param.put("page", "https://www.wishbook.io/resell-and-earn.html");
                    } else {
                        param.put("page", "https://www.wishbook.io/resell-and-earn-hi.html");
                    }
                } else {
                    param.put("page", "https://www.wishbook.io/resell-and-earn.html");
                }

                new DeepLinkFunction(param, getActivity());
            }
                break;

            case R.id.setting_seller_hub: {
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "tab");
                param.put("page", "sellerhub");
                new DeepLinkFunction(param, getActivity());
            }
                break;

        }

    }

    /**
     * function for Navigate to Reseller Hub with selected tab
     * @param position - selected tab position
     */
    public void navigateReseller(String position) {
        Bundle reseller_bundle = new Bundle();
        Fragment_ResellerHolder fragment_resellerHolder = new Fragment_ResellerHolder();
        reseller_bundle.putString("from", Fragment_MyBusiness.class.getSimpleName());
        reseller_bundle.putString("position", position);
        fragment_resellerHolder.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "Reseller Hub";
        Application_Singleton.CONTAINERFRAG = fragment_resellerHolder;
        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
    }

    /**
     * function for Navigate to My Earning Screen with Selected tab
     * @param position - specify selected tab position
     */
    private void navigateMyEarning(String position) {
        Bundle reseller_bundle = new Bundle();
        Fragment_MyEarningHolder fragment_myEarningHolder = new Fragment_MyEarningHolder();
        reseller_bundle.putString("from", Fragment_MyBusiness.class.getSimpleName());
        reseller_bundle.putString("position", position);
        fragment_myEarningHolder.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "My Earnings";
        Application_Singleton.CONTAINERFRAG = fragment_myEarningHolder;
        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9000 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringExtra("content") != null) {
                boolean isValid = URLUtil.isValidUrl(data.getStringExtra("content"));
                try {
                    Uri intentUri = Uri.parse(data.getStringExtra("content"));
                    callDeepLinkIntent(intentUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                    Fragment_MyBusiness.this.startActivityForResult(i, 9000);
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    public void callDeepLinkIntent(Uri intentUri) {
        String type = null;
        if (intentUri.getQueryParameter("type") != null) {
            type = intentUri.getQueryParameter("type");
        } else if (intentUri.getQueryParameter("t") != null) {
            type = intentUri.getQueryParameter("t");
        }
        String id = intentUri.getQueryParameter("id");
        if (type != null) {
            Intent intent = new Intent(getActivity(), Activity_Home.class);
            if (type != null) {
                HashMap<String, String> hashMap = SplashScreen.getQueryString(intentUri);
                new DeepLinkFunction(hashMap, getActivity());
            }
        } else {
            String catalog = intentUri.getPath().replaceAll("\\D+", "");
            String supplier = intentUri.getQueryParameter("supplier");
            StaticFunctions.catalogQR(catalog, (AppCompatActivity) getActivity(), supplier);
        }
    }

    /**
     * Call Patch Company-type api for reseller/retailer flag changes
     * @param isReseller
     */
    private void patchCompany(final boolean isReseller) {
        try {
            CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);
            if (companyGroupFlag.getId() != null && !companyGroupFlag.getId().isEmpty()) {
                CompanyTypePatch patchcompanytype = new CompanyTypePatch();
                if (isReseller)
                    patchcompanytype.setOnline_retailer_reseller(isReseller);
                else {
                    patchcompanytype.setOnline_retailer_reseller(isReseller);
                    patchcompanytype.setRetailer(true);
                }
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getActivity());
                HttpManager.getInstance((Activity) getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "companytype", companyGroupFlag.getId()), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchcompanytype), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                      //  if (isAdded() && !isDetached()) {
                            try {
                                if (response != null && !response.isEmpty())
                                    UserInfo.getInstance(getActivity()).setCompanyGroupFlag(response);

                                if (isReseller) {
                                    UserInfo.getInstance(getActivity()).setOnline_retailer_reseller(isReseller);
                                } else {
                                    UserInfo.getInstance(getActivity()).setOnline_retailer_reseller(false);
                                }
                            } catch (Exception e) {

                            }
                      //  }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideOrderDisableConfig() {
        // Add Catalog hide
        if(StaticFunctions.checkOrderDisableConfig(getActivity())) {
            linear_seller_function3.setVisibility(View.GONE);
            setting_refer_earn.setVisibility(View.GONE);
        }

    }

}
