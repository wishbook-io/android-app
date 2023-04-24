package com.wishbook.catalog.home.address;

import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.address.adapter.BottomSheetAddressAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDeliveryAddressBottomDialog extends BottomSheetDialogFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;


    @BindView(R.id.txt_bottom_sheet_title)
    TextView txt_bottom_sheet_title;

    @BindView(R.id.relative_bottom_progress)
    RelativeLayout relative_bottom_progress;

    @BindView(R.id.txt_add_new_delivery)
    TextView txt_add_new_delivery;

    String previous_selected_id = null;
    boolean isReadOnly;


    /// #### Start Global variable initialize #######
    LinearLayoutManager layoutManager;
    private ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList;

    public static ManageDeliveryAddressBottomDialog newInstance(Bundle bundle) {
        ManageDeliveryAddressBottomDialog f = new ManageDeliveryAddressBottomDialog();
        if(bundle!=null) {
            f.setArguments(bundle);
        }
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manage_delivery_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        initRecyclerview();
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        getAllDeliveryAddress();
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(getArguments()!=null && getArguments().getBoolean("isReadOnly")) {
            isReadOnly = true;
        }

        if(getArguments()!=null &&  getArguments().getString("selected_delivery_address")!=null) {
            previous_selected_id = getArguments().getString("selected_delivery_address");
        }

        return v;
    }

    public void initRecyclerview() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        txt_add_new_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryAddressAddDialog deliveryAddressAddDialog = DeliveryAddressAddDialog.newInstance(null);
                deliveryAddressAddDialog.show(getFragmentManager(), "Add Address");
                deliveryAddressAddDialog.setDeliveryAddAddressListener(new DeliveryAddressAddDialog.DeliveryAddAddressListener() {
                    @Override
                    public void onAdd(String address_id) {
                        getAllDeliveryAddress();
                    }
                });
            }
        });
    }


    public void getAllDeliveryAddress() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "address", "") + "?company=" + UserInfo.getInstance(getActivity()).getCompany_id();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Type type = new TypeToken<ArrayList<ShippingAddressResponse>>() {
                        }.getType();
                        shippingAddressResponseArrayList = Application_Singleton.gson.fromJson(response, type);
                        if (shippingAddressResponseArrayList.size() > 0) {
                            empty_linear.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setNestedScrollingEnabled(false);
                            BottomSheetAddressAdapter addressAdapter = new BottomSheetAddressAdapter(getActivity(), shippingAddressResponseArrayList,
                                    ManageDeliveryAddressBottomDialog.this, previous_selected_id,isReadOnly);
                            recyclerView.setAdapter(addressAdapter);

                        } else {
                            // show empty view;
                            empty_linear.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                empty_linear.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }


    public void showProgress() {
        relative_bottom_progress.setVisibility(View.VISIBLE);
    }

    public void showEmptyLinear() {
        empty_linear.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void hideProgress() {
        relative_bottom_progress.setVisibility(View.GONE);
    }


}

