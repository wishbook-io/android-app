package com.wishbook.catalog.home.orders;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.contacts.Fragment_CatalogEnquiry;
import com.wishbook.catalog.home.contacts.Fragment_Leads;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityEnquiryHolder extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_main)
    RelativeLayout content_main;


    String type = "leads";
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ActivityEnquiryHolder.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_order_holder);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
            position = getIntent().getIntExtra("position", 0);
        }

        setupToolbar();
        spinnerSelect(position);
    }

    public void setupToolbar() {
        if (type.equals("leads"))
            toolbar.setTitle("Leads");
        if (type.equals("enquiry"))
            toolbar.setTitle("Enquiry");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void spinnerSelect(int position) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (type) {
            case "leads":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Leads("all"), "enquiry_received")
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Leads("new"), "enquiry_received")
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Leads("old"), "enquiry_received")
                                .commit();
                        break;
                }

                break;
            case "enquiry":
                switch (position) {
                    case 0: {
                        Bundle bundle1 = new Bundle();
                        Fragment_CatalogEnquiry fragment = new Fragment_CatalogEnquiry("all");
                        bundle1.putString("buying_company_id", UserInfo.getInstance(mContext).getCompany_id());
                        bundle1.putBoolean("isEnquiry" , true);
                        fragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment, "enquiry_sent")
                                .commit();
                    }

                    break;
                    case 1: {
                        Bundle bundle1 = new Bundle();
                        Fragment_CatalogEnquiry fragment = new Fragment_CatalogEnquiry("new");
                        bundle1.putString("buying_company_id", UserInfo.getInstance(mContext).getCompany_id());
                        bundle1.putBoolean("isEnquiry" , true);
                        fragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment, "enquiry_sent")
                                .commit();
                    }
                    break;
                    case 2: {
                        Bundle bundle1 = new Bundle();
                        Fragment_CatalogEnquiry fragment = new Fragment_CatalogEnquiry("old");
                        bundle1.putString("buying_company_id", UserInfo.getInstance(mContext).getCompany_id());
                        bundle1.putBoolean("isEnquiry" , true);
                        fragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment, "enquiry_sent")
                                .commit();
                    }

                    break;
                }
                break;
        }
    }

}
