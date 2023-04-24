package com.wishbook.catalog.home.inventory;

/**
 * Created by root on 18/11/16.
 */

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_Inventory;
import com.wishbook.catalog.home.inventory.barcode.Fragment_Barcode;
import com.wishbook.catalog.home.inventory.inwardstock.Fragment_Inward_Stock;
import com.wishbook.catalog.home.models.Response_meeting_report;

import java.util.ArrayList;
import java.util.HashMap;


public class Fragment_Inventory extends GATrackedFragment {

    private Toolbar toolbar;

    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private Response_meeting_report[] response_meeting_reports;
    private ArrayList<Response_meeting_report> response_meeting_reports_list;
    private RelativeLayout barcode_container;
    private RelativeLayout inward_container;
    private RelativeLayout barcode_outward;
    private RelativeLayout opening_stock_inward;
    private RelativeLayout opening_stock_outward;
    private RelativeLayout opening_stock_container;

    private TextView in_stock,blocked,open_sales,open_purchase;

    public Fragment_Inventory() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.inventory_main, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        barcode_container= (RelativeLayout) v.findViewById(R.id.barcode_container);
        opening_stock_inward= (RelativeLayout) v.findViewById(R.id.opening_stock_inward);
        opening_stock_outward= (RelativeLayout) v.findViewById(R.id.opening_stock_outward);
        opening_stock_container= (RelativeLayout) v.findViewById(R.id.opening_stock_container);

        open_purchase= (TextView) v.findViewById(R.id.open_purchase);
        in_stock= (TextView) v.findViewById(R.id.in_stock);
        open_sales= (TextView) v.findViewById(R.id.open_sales);
        blocked= (TextView) v.findViewById(R.id.blocked);

        getAllInventories();

        toolbar.setTitle("Inventory");
        barcode_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.CONTAINER_TITLE = "Barcode";
                Application_Singleton.CONTAINERFRAG = new Fragment_Barcode();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                getActivity().startActivity(intent);
            }
        });



        opening_stock_inward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //brandWiseData
                //Add
                //Inward
                Fragment_Inward_Stock fragment_inward_stock = new Fragment_Inward_Stock();
                Bundle bundle = new Bundle();
                bundle.putString("inventory_type","Add");
                bundle.putString("local_inventory_json","brandWiseData");
                bundle.putString("inventory_name","Inward");
                fragment_inward_stock.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Inward Stock";
                Application_Singleton.CONTAINERFRAG = fragment_inward_stock;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.putExtra("toolbarCategory",OpenContainer.SCAN_BARCODE_SALESMAN);
                getActivity().startActivity(intent);
            }
        });


        opening_stock_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //brandWiseDataStock
                //Opening Stock
                Fragment_Inward_Stock fragment_inward_stock = new Fragment_Inward_Stock();
                Bundle bundle = new Bundle();
                bundle.putString("local_inventory_json","brandWiseDataStock");
                bundle.putString("inventory_name","Opening Stock");
                fragment_inward_stock.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Opening Stock";
                Application_Singleton.CONTAINERFRAG = fragment_inward_stock;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.putExtra("toolbarCategory",OpenContainer.SCAN_BARCODE_SALESMAN);
                getActivity().startActivity(intent);
            }
        });


        opening_stock_outward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //brandWiseDataRemove
                //Remove
                //Outward
                Fragment_Inward_Stock fragment_inward_stock = new Fragment_Inward_Stock();
                Bundle bundle = new Bundle();
                bundle.putString("inventory_type","Remove");
                bundle.putString("local_inventory_json","brandWiseDataRemove");
                bundle.putString("inventory_name","Outward");
                fragment_inward_stock.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Outward Stock";
                Application_Singleton.CONTAINERFRAG = fragment_inward_stock;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                intent.putExtra("toolbarCategory",OpenContainer.SCAN_BARCODE_SALESMAN);
                getActivity().startActivity(intent);
            }
        });
        return v;
    }

    private void getAllInventories() {
        //getting all inventories dashboard from server
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.INVENTORY_DASHBOARD, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    final Response_Inventory response_inventories = Application_Singleton.gson.fromJson(response, Response_Inventory.class);
                    open_sales.setText(response_inventories.getOpen_sale());
                    open_purchase.setText(response_inventories.getOpen_purchase());
                    in_stock.setText(response_inventories.getIn_stock() + "");
                    blocked.setText(response_inventories.getBlocked());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }
}

