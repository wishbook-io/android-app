package com.wishbook.catalog.home.catalog.details;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.ProductslistSelectionAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Selection_Detail;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Fragment_ProductsInSelection extends GATrackedFragment {

    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private Boolean delete_button_not_show = false;
    public Fragment_ProductsInSelection() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_selectionpro, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        toolbar.setVisibility(View.GONE);

        if (getArguments() != null) {
            if (getArguments().getString("fromNotification", "").equals("yes")) {
                delete_button_not_show = true;
            }
        }


        if (StaticFunctions.isOnline(getActivity())) {
            if(DataPasser.selectedID!=null) {
//                final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
//                        R.style.AppTheme_Dark_Dialog);
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Please wait...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                showProgress();
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"selections_expand_false","")+DataPasser.selectedID+"/?expand=true" , null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        hideProgress();
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        hideProgress();
                        Log.v("sync response", response);
                        Gson gson = new Gson();
                        Response_Selection_Detail response_productss = gson.fromJson(response, Response_Selection_Detail.class);
                        ProductObj[] response_products =response_productss.getProducts();
                        if (response_products.length > 0) {
                            if (response_products[0].getId() != null) {
                                ProductslistSelectionAdapter productslistAdapter = new ProductslistSelectionAdapter((AppCompatActivity) getActivity(), new ArrayList<ProductObj>(Arrays.asList(response_products)), DataPasser.selectedID,delete_button_not_show);
                                mRecyclerView.setAdapter(productslistAdapter);
                            }
                        }


                   // progressDialog.dismiss();
                }

                @Override
                    public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    //progressDialog.dismiss();
                    }
                });


            }
        }
        return v;
    }
}
