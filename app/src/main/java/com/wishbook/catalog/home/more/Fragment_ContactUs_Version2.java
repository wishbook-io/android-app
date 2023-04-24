package com.wishbook.catalog.home.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_ContactUs_Version2 extends GATrackedFragment {

    private View v;

    @BindView(R.id.txt_buyer_1)
    TextView txt_buyer_1;

    @BindView(R.id.txt_seller_1)
    TextView txt_seller_1;

    @BindView(R.id.txt_upload_team)
    TextView txt_upload_team;

    @BindView(R.id.txt_singlepc_whatsapp_group)
    TextView txt_singlepc_whatsapp_group;

    @BindView(R.id.txt_full_set_whatsapp_group)
    TextView txt_full_set_whatsapp_group;

    @BindView(R.id.txt_fashion_whatsapp_group)
    TextView txt_fashion_whatsapp_group;

    @BindView(R.id.linear_join_whatsapp_group)
    LinearLayout linear_join_whatsapp_group;

    public Fragment_ContactUs_Version2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact_us_version2, container, false);
        ButterKnife.bind(this, v);
        if(PrefDatabaseUtils.getPrefSinglePcWhatsappGroup(getActivity())!=null) {
            txt_singlepc_whatsapp_group.setText(PrefDatabaseUtils.getPrefSinglePcWhatsappGroup(getActivity()));
        }

        if(PrefDatabaseUtils.getPrefFullSetWhatsappGroup(getActivity())!=null) {
            txt_full_set_whatsapp_group.setText(PrefDatabaseUtils.getPrefFullSetWhatsappGroup(getActivity()));
        }

        if(PrefDatabaseUtils.getPrefFashionTrendWhatsappGroup(getActivity())!=null) {
            txt_fashion_whatsapp_group.setText(PrefDatabaseUtils.getPrefFashionTrendWhatsappGroup(getActivity()));
        }

        if (UserInfo.getInstance(getActivity()).getCompanyGroupFlag() != null) {
            CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);
            if (companyGroupFlag != null && (companyGroupFlag.getWholesaler_distributor() || companyGroupFlag.getManufacturer() || companyGroupFlag.getBroker())) {
                linear_join_whatsapp_group.setVisibility(View.GONE);
            }
        }

        try {
            // get value from config and set call value
            String buyer_support,seller_support,catalog_upload_team;
            buyer_support = PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity());
            seller_support = PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity());
            catalog_upload_team = PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity());

            txt_buyer_1.setText(buyer_support);
            txt_seller_1.setText(seller_support);
            txt_upload_team.setText(catalog_upload_team);
        } catch (Exception e){
            e.printStackTrace();
        }



        return v;
    }

    @OnClick({R.id.txt_buyer_1, R.id.txt_seller_1, R.id.txt_upload_team})
    public void callIntent(View v) {
        if (v instanceof TextView) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ((TextView) v).getText()));
            startActivity(intent);
        }

    }

}
