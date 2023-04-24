package com.wishbook.catalog.home.contacts.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.orders.adapters.PurchaseOrderAdapterNew;
import com.wishbook.catalog.home.orders.adapters.SalesOrderAdapterNew;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_Details_Orders extends GATrackedFragment {

    private View v;
    private RecyclerView mRecyclerView;
    //private SalesOrderAdapter salesOrderAdapter;
    private SalesOrderAdapterNew salesOrderAdapter;
    private ArrayList<Response_sellingorder> list = new ArrayList<>();
    public Fragment_Details_Orders() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bdetorders, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        if (bundle != null) {

            if(bundle.getString("type","buyer").equals("buyer"))
            {
                String orders = bundle.getString("orders", "[]");
                Type type = new TypeToken<Response_sellingorder[]>() {
                }.getType();
               Response_sellingorder[] response_sellingorders = new Gson().fromJson(orders, type);
                list = new ArrayList<Response_sellingorder>(Arrays.asList(response_sellingorders));
                //salesOrderAdapter = new SalesOrderAdapter(getActivity(),list);
                salesOrderAdapter = new SalesOrderAdapterNew(getActivity(),list);
                mRecyclerView.setAdapter(salesOrderAdapter);


            }
            else if(bundle.getString("type","supplier").equals("supplier"))
            {
                String orders = bundle.getString("orders", "[]");
                Type type = new TypeToken<ArrayList<Response_buyingorder>>() {
                }.getType();
                ArrayList<Response_buyingorder> response_buyingorders = new Gson().fromJson(orders, type);
                PurchaseOrderAdapterNew buyingOrderAdapter = new PurchaseOrderAdapterNew(getActivity(),response_buyingorders);
                mRecyclerView.setAdapter(buyingOrderAdapter);
            }
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
