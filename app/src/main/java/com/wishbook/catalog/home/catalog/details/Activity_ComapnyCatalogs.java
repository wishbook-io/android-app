package com.wishbook.catalog.home.catalog.details;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.SellerCatalogStarggedAdater;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_ComapnyCatalogs extends AppCompatActivity implements SellerCatalogStarggedAdater.UpdateCatalogListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_add)
    AppCompatButton btn_add;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;
    SellerCatalogStarggedAdater adater;
    String companyId;

    private static String TAG = Activity_ComapnyCatalogs.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Activity_ComapnyCatalogs.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_comapny_catalogs);
        ButterKnife.bind(this);
        setToolbar();
        initView();
    }

    @Override
    public void getTotalSelected(int count) {
        Log.i("TAG", "getTotalSelected: " + count);
        if (count == 0) {
            btn_add.setVisibility(View.GONE);
        } else {
            btn_add.setVisibility(View.VISIBLE);
        }

    }

    private void setToolbar() {
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        toolbar.setTitle("Catalog");

    }

    private void initView() {
        if (getIntent().getBooleanExtra("isPurchase", false)) {
            // for create purchase order
            if (getIntent().getStringExtra("companyId") != null) {
                companyId = getIntent().getStringExtra("companyId");
                if (getIntent().getBooleanExtra("isPurchase", false)) {
                    getCatalogs(companyId, true);
                }
            }
        } else {
            // for create sales order
            getCatalogs("", false);
        }

        mLayoutManager = new GridLayoutManager(context, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    private void getCatalogs(String companyId, boolean isPurchase) {
        StaticFunctions.showProgressbar(Activity_ComapnyCatalogs.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_ComapnyCatalogs.this);
        String url = null;
        if (isPurchase) {
            url = URLConstants.companyUrl(context, "catalogs", "") + "?company=" + companyId;
        } else {
            url = URLConstants.companyUrl(context, "catalogs", "");
        }
        HttpManager.getInstance(Activity_ComapnyCatalogs.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_ComapnyCatalogs.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(Activity_ComapnyCatalogs.this);
                    Log.v("sync response", response);
                    final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    if (response_catalogMinis.length > 0) {
                        final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalogMinis));
                        if (getIntent().getSerializableExtra("previousCatalog") != null) {
                            Log.i(TAG, " Previous Catalog Not Null: ");
                            ArrayList<Response_catalogMini> preCatalog = (ArrayList<Response_catalogMini>) getIntent().getSerializableExtra("previousCatalog");
                            for (int i = 0; i < preCatalog.size(); i++) {
                                boolean isContains = catalogMinis.contains(preCatalog.get(i));
                                if (isContains) {
                                    catalogMinis.remove(preCatalog.get(i));
                                }
                            }
                        }
                        adater = new SellerCatalogStarggedAdater(context, catalogMinis);
                        adater.setSelectedListener(Activity_ComapnyCatalogs.this);
                        mRecyclerView.setAdapter(adater);
                    } else {
                        Toast.makeText(context, "This Supplier has no catalogs", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_ComapnyCatalogs.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }


    @OnClick(R.id.btn_add)
    public void addCatalog() {
        if (adater != null) {
            Intent sendIntent = new Intent();
            sendIntent.putExtra("selectedCatalog", adater.getSelectedCatalog());
            setResult(Activity.RESULT_OK, sendIntent);
            finish();
        }
    }

}
