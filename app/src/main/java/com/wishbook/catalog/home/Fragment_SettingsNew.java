package com.wishbook.catalog.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.freshchat.consumer.sdk.Freshchat;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;
import com.wishbook.catalog.home.more.ActivityLanguage;
import com.wishbook.catalog.home.more.Activity_ShareApp;
import com.wishbook.catalog.home.more.Fragment_AboutUs_Policies;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.more.Fragment_BuyerGroups;
import com.wishbook.catalog.home.more.Fragment_BuyersRejected;
import com.wishbook.catalog.home.more.Fragment_ChangePassword;
import com.wishbook.catalog.home.more.Fragment_DiscountSetting;
import com.wishbook.catalog.home.more.Fragment_GST_2;
import com.wishbook.catalog.home.more.Fragment_Home;
import com.wishbook.catalog.home.more.Fragment_MyCompanyProfile;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.more.Fragment_SupplierRejected;
import com.wishbook.catalog.home.more.Fragment_Sync;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.login.Fragment_Register_New_Version2;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_SettingsNew extends GATrackedFragment implements View.OnClickListener {


    @BindView(R.id.settings_profile)
    TextView settings_profile;

    @BindView(R.id.settings_company_profile)
    LinearLayout settings_company_profile;

    @BindView(R.id.settings_change_password)
    TextView settings_change_password;

    @BindView(R.id.settings_my_brands)
    TextView settings_my_brands;

    @BindView(R.id.settings_my_buyer_groups)
    TextView settings_my_buyer_groups;

    @BindView(R.id.settings_chat)
    TextView settings_chat;

    @BindView(R.id.settings_faq)
    TextView settings_faq;

    @BindView(R.id.settings_discount)
    LinearLayout settings_discount;

    @BindView(R.id.settings_gst)
    TextView settings_gst;

    @BindView(R.id.setting_logout)
    TextView setting_logout;

    @BindView(R.id.settings_change_language)
    LinearLayout settings_change_language;

    @BindView(R.id.setting_register_new_user)
    TextView setting_register_new_user;


    @BindView(R.id.settings_about_us)
    TextView settings_about_us;

    @BindView(R.id.txt_profile_name)
    TextView txt_profile_name;
    @BindView(R.id.txt_company_name)
    TextView txt_company_name;
    @BindView(R.id.txt_user_id)
    TextView txt_user_id;
    @BindView(R.id.txt_email_id)
    TextView txt_email_id;
    @BindView(R.id.txt_contact_no)
    TextView txt_contact_no;

    @BindView(R.id.txt_my_followers_value)
    TextView txt_my_followers_value;
    @BindView(R.id.txt_my_catalog_value)
    TextView txt_my_catalog_value;
    @BindView(R.id.txt_company_type)
    TextView txt_company_type;


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

    private boolean isRejectedUserExpand = false;


    @BindView(R.id.card_followers)
    CardView card_followers;
    @BindView(R.id.linear_view_profile)
    LinearLayout linear_view_profile;

    @BindView(R.id.setting_rejected_buyer_seller)
    TextView setting_rejected_buyer_seller;

    @BindView(R.id.setting_rejected_supplier_retailer)
    TextView setting_rejected_supplier_retailer;

    @BindView(R.id.settings_share_app)
    LinearLayout settings_share_app;

    @BindView(R.id.setting_scan_qr_code)
    TextView setting_scan_qr_code;

    @BindView(R.id.linear_card_followers)
    LinearLayout linear_card_followers;

    @BindView(R.id.settings_shared_by_me)
    TextView settings_shared_by_me;

    public static SharedPreferences pref;
    public static SharedPreferences profilePref;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_new, container, false);
        //setting toolbar
        ButterKnife.bind(this, view);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_logout:
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
                }
                return false;
            }
        });

        if (UserInfo.getInstance(getActivity()).isGuest()) {
            toolbar.inflateMenu(R.menu.menu_guest_profile);
            toolbar.setVisibility(View.VISIBLE);
            view.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            Log.d("TAG", "onCreateView:  Guest");
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                Log.d("TAG", "onCreateView:  Guest Pending");
                View dialog = view.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(),dialog);
                return view;
            } else {
                Log.d("TAG", "onCreateView:  Guest Login");
                View dialog = view.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog,null);
                return view;
            }


        } else {
           toolbar.setVisibility(View.GONE);
        }


        settings_change_password.setOnClickListener(this);
        settings_my_buyer_groups.setOnClickListener(this);
        settings_my_brands.setOnClickListener(this);
        settings_profile.setOnClickListener(this);
        settings_company_profile.setOnClickListener(this);
        settings_chat.setOnClickListener(this);
        settings_faq.setOnClickListener(this);
        settings_gst.setOnClickListener(this);
        settings_discount.setOnClickListener(this);
        setting_logout.setOnClickListener(this);
        txt_my_followers_value.setOnClickListener(this);
        txt_my_catalog_value.setOnClickListener(this);
        linear_view_profile.setOnClickListener(this);
        settings_change_language.setOnClickListener(this);
        settings_about_us.setOnClickListener(this);
        setting_scan_qr_code.setOnClickListener(this);

        setting_register_new_user.setOnClickListener(this);
        setting_rejected_buyer.setOnClickListener(this);
        setting_rejected_supplier.setOnClickListener(this);
        setting_rejected_buyer_seller.setOnClickListener(this);
        setting_rejected_supplier_retailer.setOnClickListener(this);

        settings_shared_by_me.setOnClickListener(this);

        settings_share_app.setOnClickListener(this);


        relative_rejected_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isRejectedUserExpand) {
                    isRejectedUserExpand = false;
                    int padding = StaticFunctions.dpToPx(getActivity(), 16);
                    linear_rejceted_child.setVisibility(View.VISIBLE);
                    arrow_img2.setRotation(180);
                } else {
                    isRejectedUserExpand = true;
                    linear_rejceted_child.setVisibility(View.GONE);
                    arrow_img2.setRotation(0);
                }
            }
        });
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linear_card_followers.getLayoutParams();
        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            setting_rejected_buyer_seller.setVisibility(View.VISIBLE);
            setting_rejected_supplier_retailer.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.GONE);
            settings_shared_by_me.setVisibility(View.VISIBLE);

        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            setting_rejected_supplier_retailer.setVisibility(View.VISIBLE);
            setting_rejected_buyer_seller.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.GONE);

            params.setMargins(StaticFunctions.dpToPx(getActivity(), 50), 0, StaticFunctions.dpToPx(getActivity(), 50), 0);
            linear_card_followers.setLayoutParams(params);
            settings_shared_by_me.setVisibility(View.GONE);

            settings_my_brands.setVisibility(View.GONE);
            settings_my_buyer_groups.setVisibility(View.GONE);
            settings_discount.setVisibility(View.GONE);
            setting_register_new_user.setVisibility(View.GONE);
        } else {
            setting_rejected_supplier_retailer.setVisibility(View.GONE);
            setting_rejected_buyer_seller.setVisibility(View.GONE);
            relative_rejected_users.setVisibility(View.VISIBLE);

            settings_shared_by_me.setVisibility(View.VISIBLE);

        }

        relative_rejected_users.performClick(); // for hide firsttime

        pref = StaticFunctions.getAppSharedPreferences(getActivity());


        profilePref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);

        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            card_followers.setVisibility(View.GONE);
        } else {
            card_followers.setVisibility(View.VISIBLE);
        }

        if (profilePref != null) {
            txt_company_name.setText(profilePref.getString("companyname", ""));
            String phoneNumber1 = profilePref.getString("mobile", "");
            if (phoneNumber1.length() >= 10) {
                String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                txt_contact_no.setText(phoneNumbertrim);
            }
            txt_email_id.setText(profilePref.getString("email", ""));
            String name = profilePref.getString("firstName", "") + " " + profilePref.getString("lastName", "");
            txt_profile_name.setText(name);
            txt_user_id.setText("(+" + profilePref.getString("userName", "") + ")");

            txt_my_catalog_value.setText(profilePref.getString("total_my_catalogs", "0"));
            txt_my_followers_value.setText(profilePref.getString("total_brand_followers", "0"));
        }

        txt_company_type.setText(UserInfo.getInstance(getActivity()).getAllCompanyType());

        return view;
    }

    @Override
    public void onResume() {
        if (UserInfo.getInstance(getActivity()).isPasswordSet()) {
            if (settings_change_password != null) {
                settings_change_password.setText(getResources().getString(R.string.change_password));
            }
        } else {
            if (settings_change_password != null) {
                settings_change_password.setText(getResources().getString(R.string.set_password));
            }
        }
        super.onResume();

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
                Application_Singleton.CONTAINER_TITLE = "GST";
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
                Application_Singleton.CONTAINER_TITLE = "Discount settings";
                Application_Singleton.CONTAINERFRAG = new Fragment_DiscountSetting();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
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
            case R.id.txt_my_catalog_value:
                Bundle bundle = new Bundle();
                Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                //bundle.putInt("toolbarCategory", OpenContainer.ADD_CATALOG);
                fragmentCatalogs.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "My Catalog";
                Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                Intent intent2 = new Intent(getActivity(), OpenContainer.class);
                intent2.putExtra("toolbarCategory", OpenContainer.ADD_CATALOG);
                startActivity(intent2);
                //StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
            case R.id.txt_my_followers_value:
                Intent intent1 = new Intent(getActivity(), Activity_BuyerSearch.class);
                intent1.putExtra("toolbarTitle", "Search Followers");
                intent1.putExtra("type", "myfollowers");
                startActivity(intent1);
                break;
            case R.id.settings_shared_by_me:
                Fragment_ShareStatus fragment_shareStatus = new Fragment_ShareStatus();
                Application_Singleton.CONTAINER_TITLE = "Shared By Me";
                Application_Singleton.CONTAINERFRAG = fragment_shareStatus;
                Intent shared_by_me_intent = new Intent(getActivity(), OpenContainer.class);
                startActivity(shared_by_me_intent);
                break;
            case R.id.linear_view_profile:
                Application_Singleton.CONTAINER_TITLE = "Profile";
                Application_Singleton.CONTAINERFRAG = new Fragment_Profile();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
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
                    Intent i = new Intent(getActivity(),SimpleScannerActivity.class);
                    Fragment_SettingsNew.this.startActivityForResult(i, 9000);
                }
                break;

            case R.id.settings_share_app:
                /*Application_Singleton.CONTAINER_TITLE = "";
                Application_Singleton.TOOLBARSTYLE = "TRANSPARENT";
                Application_Singleton.CONTAINERFRAG = new Fragment_ShareApp();*/
                StaticFunctions.switchActivity(getActivity(), Activity_ShareApp.class);
                break;
        }

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
                    Intent i = new Intent(getActivity(),SimpleScannerActivity.class);
                    Fragment_SettingsNew.this.startActivityForResult(i, 9000);
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
}
