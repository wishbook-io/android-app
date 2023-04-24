
package com.wishbook.catalog.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.freshchat.consumer.sdk.Freshchat;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.more.Fragment_BuyerGroups;
import com.wishbook.catalog.home.more.Fragment_ChangePassword;
import com.wishbook.catalog.home.more.Fragment_DiscountSetting;
import com.wishbook.catalog.home.more.Fragment_GST_2;
import com.wishbook.catalog.home.more.Fragment_Home;
import com.wishbook.catalog.home.more.Fragment_MyCompanyProfile;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.more.Fragment_Sync;
import com.wishbook.catalog.login.Fragment_Register_New_Version2;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 20/4/17.
 */
public class Fragment_Settings extends GATrackedFragment implements View.OnClickListener {

    @BindView(R.id.settings_profile)
    LinearLayout settings_profile;

    @BindView(R.id.settings_company_profile)
    LinearLayout settings_company_profile;

    @BindView(R.id.settings_change_password)
    LinearLayout settings_change_password;

    @BindView(R.id.settings_help)
    LinearLayout setting_help;

    @BindView(R.id.settings_my_brands)
    LinearLayout settings_my_brands;

    @BindView(R.id.settings_my_buyer_groups)
    LinearLayout settings_my_buyer_groups;

    @BindView(R.id.settings_sync)
    LinearLayout settings_sync;

    @BindView(R.id.settings_register_new_user)
    LinearLayout settings_register_new_user;

    @BindView(R.id.settings_chat)
    LinearLayout settings_chat;

    @BindView(R.id.settings_faq)
    LinearLayout settings_faq;

    @BindView(R.id.settings_discount)
    LinearLayout settings_discount;

  @BindView(R.id.settings_gst)
  LinearLayout settings_gst;


    public static SharedPreferences pref;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //setting toolbar
        try {
            toolbar.setTitle(StringUtils.capitalize(UserInfo.getInstance(getContext()).getFirstName() +" "+ UserInfo.getInstance(getContext()).getLastName()));
        }catch (NullPointerException e){
            toolbar.setTitle("Profile");
        }

        ButterKnife.bind(this,view);


        setting_help.setOnClickListener(this);
        settings_change_password.setOnClickListener(this);
        settings_my_buyer_groups.setOnClickListener(this);
        settings_my_brands.setOnClickListener(this);
        settings_profile.setOnClickListener(this);
        settings_register_new_user.setOnClickListener(this);
        settings_sync.setOnClickListener(this);
        settings_company_profile.setOnClickListener(this);
        settings_chat.setOnClickListener(this);
        settings_faq.setOnClickListener(this);
        settings_gst.setOnClickListener(this);
        settings_discount.setOnClickListener(this);
        pref = StaticFunctions.getAppSharedPreferences(getActivity());


        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            settings_my_brands.setVisibility(View.GONE);
            settings_my_buyer_groups.setVisibility(View.GONE);
            setting_help.setVisibility(View.GONE);
        }
        else {
            settings_my_brands.setVisibility(View.VISIBLE);
            settings_my_buyer_groups.setVisibility(View.VISIBLE);
            setting_help.setVisibility(View.GONE);
        }



        return view;
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
                if (pref.getString("groupstatus", "0").equals("1")  && UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    if (pref.getString("brandadded", "no").equals("no")) {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_AddBrand();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    } else {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    }
                }
                else {

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
        }

    }
}
