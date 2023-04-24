package com.wishbook.catalog.reseller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.reseller.adapter.ResellerAddressAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Reseller_Address extends GATrackedFragment {

    @BindView(R.id.recyclerViewAddress)
    RecyclerView recyclerViewAddress;

    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;

    @BindView(R.id.float_add_address)
    FloatingActionButton fab_add_new_address;

    private LinearLayoutManager linearLayoutManager;


    private View view;

    private ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList;

    public Fragment_Reseller_Address() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_reseller_address, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public void initView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        fab_add_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResellerAddressDialog();
            }
        });

        initCall();

    }

    public void initCall() {
        getAllAddress(UserInfo.getInstance(getActivity()).getCompany_id());
    }

    public void getAllAddress(String buyerComapnyId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", "") + "?company=" + buyerComapnyId, null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        hideProgress();
                        Type type = new TypeToken<ArrayList<ShippingAddressResponse>>() {
                        }.getType();
                        shippingAddressResponseArrayList = Application_Singleton.gson.fromJson(response, type);
                        if (shippingAddressResponseArrayList.size() > 0) {
                            linear_empty.setVisibility(View.GONE);
                            recyclerViewAddress.setVisibility(View.VISIBLE);
                            ResellerAddressAdapter resellerAddressAdapter = new ResellerAddressAdapter(getActivity(), shippingAddressResponseArrayList, recyclerViewAddress);
                            recyclerViewAddress.setAdapter(resellerAddressAdapter);
                        } else {
                            linear_empty.setVisibility(View.VISIBLE);
                            recyclerViewAddress.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showResellerAddressDialog() {
        ResellerAddDialog addDialog = ResellerAddDialog.newInstance(null);
        addDialog.show(getFragmentManager(), "Add Address");
        addDialog.setResellerAddAddressListener(new ResellerAddDialog.ResellerAddAddressListener() {
            @Override
            public void onAdd(boolean check) {
                initCall();
            }
        });
    }


}
