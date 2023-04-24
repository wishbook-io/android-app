package com.wishbook.catalog.home.orderNew.details;


import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditReference;
import com.wishbook.catalog.home.orderNew.adapters.CreditReferenceAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Credit_Reference_Suppliers extends GATrackedFragment {

    @BindView(R.id.recyclerview_supplier)
    RecyclerView recyclerview_supplier;

    @BindView(R.id.txt_noempty)
    TextView txt_noempty;

    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_refence_suppliers, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerview_supplier.setLayoutManager(mLayoutManager);
        if (getArguments().getString("buying_company_id") != null) {
            getCreditRefences(getArguments().getString("buying_company_id"));
        }

    }

    public void getCreditRefences(String buyingCompanyID) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "credit-reference", buyingCompanyID), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ArrayList<ResponseCreditReference> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseCreditReference>>() {
                    }.getType());
                    if (responseData.size() > 0) {
                        recyclerview_supplier.setVisibility(View.VISIBLE);
                        txt_noempty.setVisibility(View.GONE);
                        mAdapter = new CreditReferenceAdapter(getActivity(), responseData);
                        recyclerview_supplier.setAdapter(mAdapter);
                    } else {
                        // NO DATA AVAILABLE
                        txt_noempty.setVisibility(View.VISIBLE);
                        recyclerview_supplier.setVisibility(View.GONE);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
