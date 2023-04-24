package com.wishbook.catalog.home.orders;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.sellerhub.Fragment_SellerInvoice_List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityOrderHolder extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_main)
    RelativeLayout content_main;


    String type = "purchase";
    int position = 0;
    boolean isFromPlaceOrder = false;

    String from = ActivityOrderHolder.class.getSimpleName();
    String source = "";
    String date_range_type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ActivityOrderHolder.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_order_holder);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
            position = getIntent().getIntExtra("position", 0);
            isFromPlaceOrder = getIntent().getBooleanExtra("isFromPlaceOrder", false);
            if (isFromPlaceOrder) {
                Application_Singleton singleton = new Application_Singleton();
                singleton.trackScreenView("PurchaseOrder/List/Success", mContext);
                StaticFunctions.getAllCODVerficationPending(mContext);
                //StaticFunctions.showBankDetailsDialog(0, ActivityOrderHolder.this);
            }

        }


        if (getIntent() != null && getIntent().getStringExtra("from") != null) {
            from = getIntent().getStringExtra("from");
        }

        if (getIntent() != null && getIntent().getStringExtra("date_range_type") != null) {
            date_range_type = getIntent().getStringExtra("date_range_type");
        }

        setupToolbar();
        spinnerSelect(position);
    }

    public void setupToolbar() {
        if (type.equals("purchase"))
            toolbar.setTitle("Purchase Order");
        if (type.equals("sale"))
            toolbar.setTitle("Sales Order");
        if (type.equals("broker"))
            toolbar.setTitle("Brokerage Order");
        if (type.equals("manifest"))
            toolbar.setTitle("Manifests");
        if (type.equals("seller_invoice"))
            toolbar.setTitle("Seller Invoices");
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
        Fragment fragment = null;
        switch (type) {
            case "purchase":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_Purchase_Orders("placed", isFromPlaceOrder, date_range_type), Fragment_Tab_Purchase_Orders.class.getSimpleName())
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_Purchase_Orders("pending", isFromPlaceOrder, date_range_type), Fragment_Tab_Purchase_Orders.class.getSimpleName())
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_Purchase_Orders("dispatch", isFromPlaceOrder, date_range_type), Fragment_Tab_Purchase_Orders.class.getSimpleName())
                                .commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_Purchase_Orders("cancel", isFromPlaceOrder, date_range_type), Fragment_Tab_Purchase_Orders.class.getSimpleName())
                                .commit();
                        break;
                }

                break;
            case "sale":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_SalesOrders("placed", true, from, date_range_type), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_SalesOrders("pending", true, from, date_range_type), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_SalesOrders("dispatch", false, from, date_range_type), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Tab_SalesOrders("cancel", false, from, date_range_type), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                }

                break;
            case "broker":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Frgment_Tab_Brokerage_Orders("placed", true), Frgment_Tab_Brokerage_Orders.class.getSimpleName())
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Frgment_Tab_Brokerage_Orders("pending", true), Frgment_Tab_Brokerage_Orders.class.getSimpleName())
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Frgment_Tab_Brokerage_Orders("dispatch", false), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Frgment_Tab_Brokerage_Orders("cancel", false), Fragment_Tab_SalesOrders.class.getSimpleName())
                                .commit();
                        break;
                }

                break;

            case "manifest":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Manifest_List("CREATED", true), Fragment_Manifest_List.class.getSimpleName())
                                .commit();
                        break;

                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_Manifest_List("CLOSED", true), Fragment_Manifest_List.class.getSimpleName())
                                .commit();
                        break;
                }
                break;

            case "seller_invoice":
                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_SellerInvoice_List("Created", true), Fragment_SellerInvoice_List.class.getSimpleName())
                                .commit();
                        break;

                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, new Fragment_SellerInvoice_List("Invoice Uploaded", true), Fragment_SellerInvoice_List.class.getSimpleName())
                                .commit();
                        break;
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: ActivityOrderHolder request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
