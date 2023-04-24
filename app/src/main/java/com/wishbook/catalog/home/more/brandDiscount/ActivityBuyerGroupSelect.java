package com.wishbook.catalog.home.more.brandDiscount;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.Brand;
import com.wishbook.catalog.commonmodels.responses.NameValueParent;
import com.wishbook.catalog.home.more.brandDiscount.adapter.DiscountBrandChildListAdapter;
import com.wishbook.catalog.home.more.brandDiscount.adapter.DiscountBrandListParentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActivityBuyerGroupSelect extends AppCompatActivity {


    RecyclerView mRecyclerView;
    EditText edit_search;
    private Context mContext;
    private Toolbar toolbar;
    private LinearLayout linear_button;
    private AppCompatButton btn_save;
    private String type;
    private ArrayList<NameValueParent> discountList = new ArrayList<>();
    DiscountBrandListParentAdapter discountBrandsListAdapter;
    private static String TAG = ActivityBuyerGroupSelect.class.getSimpleName();
    private ArrayList<NameValues> previouslyBrand;
    private ArrayList<NameValues> previouslyBuyer;
    private ArrayList<NameValues> appliedBrands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ActivityBuyerGroupSelect.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_select_brand_discount);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search.requestFocus();

        initView();

        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
        }

        if (type != null && type.equals("brand")) {
            edit_search.setHint("Search brands by name");
            if (getIntent().getSerializableExtra("previouslySelected") != null) {
                previouslyBrand = (ArrayList<NameValues>) getIntent().getSerializableExtra("previouslySelected");
            }
            if (getIntent().getSerializableExtra("applyDiscountBrands") != null) {
                appliedBrands = (ArrayList<NameValues>) getIntent().getSerializableExtra("applyDiscountBrands");
            }


            NameValueParent brandOwn = new NameValueParent();
            brandOwn.setName("All brands");
            brandOwn.setId("-1");
            if(getIntent().getBooleanExtra("isAllbrandApplied",false)){
                brandOwn.setDisable(true);
            }
            List<Brand> brands = new ArrayList<>();
            if (previouslyBrand != null) {
                for (int i = 0; i < previouslyBrand.size(); i++) {
                    if (previouslyBrand.get(i).getPhone().equals("-1")) {
                        brandOwn.setChecked(true);
                        break;
                    }
                }
            }
            brandOwn.setChildItemList(brands);
            discountList.add(brandOwn);
            discountBrandsListAdapter = new DiscountBrandListParentAdapter(mContext, discountList);
            mRecyclerView.setAdapter(discountBrandsListAdapter);
            getOwnBrands();
        }
    }

    private void initView() {
        linear_button = (LinearLayout) findViewById(R.id.linear_button);
        linear_button.setVisibility(View.VISIBLE);
        btn_save = (AppCompatButton) findViewById(R.id.btn_save);
        mRecyclerView = (RecyclerView) findViewById(R.id.buyer_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (discountBrandsListAdapter != null) {
                    discountBrandsListAdapter.filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type != null) {
                    if (type.equals("brand")) {
                        Intent brandIntent = new Intent();
                        brandIntent.putExtra("brand", discountBrandsListAdapter.getAllCheckedFilter());
                        setResult(RESULT_OK, brandIntent);
                        finish();
                    } else {
                        Intent buyerIntent = new Intent();
                        buyerIntent.putExtra("buyer", discountBrandsListAdapter.getAllCheckedFilter());
                        setResult(RESULT_OK, buyerIntent);
                        finish();
                    }
                }

            }
        });
    }

    private void getOwnBrands() {
        StaticFunctions.showProgressbar(ActivityBuyerGroupSelect.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBuyerGroupSelect.this);
        HttpManager.getInstance(ActivityBuyerGroupSelect.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "brands_dropdown", "") + "?mycompany=true", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
                Brand[] brands = Application_Singleton.gson.fromJson(response, Brand[].class);
                if (brands.length > 0) {
                    ArrayList<Brand> brands1 = new ArrayList(Arrays.asList(brands));
                    NameValueParent brandOwn = new NameValueParent();
                    brandOwn.setName("Brands you own");
                    brandOwn.setId("1");

                    if (appliedBrands != null) {
                        ArrayList<Brand> temp = new ArrayList(Arrays.asList(brands));
                        for (int i = 0; i < temp.size(); i++) {
                            for (int j = 0; j < appliedBrands.size(); j++) {
                                if (temp.get(i).getId().equals(appliedBrands.get(j).getPhone())) {
                                    brands1.get(i).setDisable(true);
                                    brands1.get(i).setDiscount_rules(appliedBrands.get(j).getDiscount_rules());
                                }
                            }
                        }
                    }

                    if (previouslyBrand != null) {
                        for (int i = 0; i < brands1.size(); i++) {
                            for (int j = 0; j < previouslyBrand.size(); j++) {
                                if (brands1.get(i).getId().equals(previouslyBrand.get(j).getPhone()) || previouslyBrand.get(j).getPhone().equals("-1")) {
                                    brands1.get(i).setChecked(true);
                                }
                            }
                        }
                    }
                    brandOwn.setChildItemList(brands1);
                    brandOwn.setAdpter(new DiscountBrandChildListAdapter(mContext, brands1, brands1, discountList.size() - 1));
                    discountList.add(brandOwn);
                    getISellBrands();
                } else {
                    getISellBrands();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
            }
        });
    }

    private void getISellBrands() {
        StaticFunctions.showProgressbar(ActivityBuyerGroupSelect.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBuyerGroupSelect.this);
        HttpManager.getInstance(ActivityBuyerGroupSelect.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "brands_i_sell_dropdown", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
                Brand[] brands = Application_Singleton.gson.fromJson(response, Brand[].class);
                if (brands.length > 0) {
                    ArrayList<Brand> brands1 = new ArrayList(Arrays.asList(brands));
                    NameValueParent brandOwn = new NameValueParent();
                    brandOwn.setName("Brands you sell");
                    brandOwn.setId("2");
                    if (appliedBrands != null) {
                        ArrayList<Brand> temp = new ArrayList(Arrays.asList(brands));
                        for (int i = 0; i < temp.size(); i++) {
                            for (int j = 0; j < appliedBrands.size(); j++) {
                                if (temp.get(i).getId().equals(appliedBrands.get(j).getPhone())) {
                                    brands1.get(i).setDisable(true);
                                    brands1.get(i).setDiscount_rules(appliedBrands.get(j).getDiscount_rules());
                                }
                            }
                        }
                    }

                    if (previouslyBrand != null) {
                        for (int i = 0; i < brands1.size(); i++) {
                            for (int j = 0; j < previouslyBrand.size(); j++) {
                                if (brands1.get(i).getId().equals(previouslyBrand.get(j).getPhone()) || previouslyBrand.get(j).getPhone().equals("-1")) {
                                    brands1.get(i).setChecked(true);
                                }
                            }
                        }
                    }
                    brandOwn.setChildItemList(brands1);
                    brandOwn.setAdpter(new DiscountBrandChildListAdapter(mContext, brands1, brands1, discountList.size() - 1));
                    discountList.add(brandOwn);
                    discountBrandsListAdapter = new DiscountBrandListParentAdapter(mContext, discountList);
                    mRecyclerView.setAdapter(discountBrandsListAdapter);
                } else {
                    discountBrandsListAdapter = new DiscountBrandListParentAdapter(mContext, discountList);
                    mRecyclerView.setAdapter(discountBrandsListAdapter);
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupSelect.this);
            }
        });
    }



}
