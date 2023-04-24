package com.wishbook.catalog.home.more.invoice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_Invoice;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.more.adapters.InvoiceAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 10/10/16.
 */
public class Fragment_Invoice extends Fragment
{
    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private InvoiceAdapter invoiceAdapter;
    private ArrayList<Response_sellingorder> list = new ArrayList<>();
    private ArrayList<String> filterStatusList = new ArrayList<>();

    public Fragment_Invoice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_invoice, container, false);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        getInvoice();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getInvoice() {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"invoice",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Log.v("res", "" + response);
                Gson gson = new Gson();
                Response_Invoice[] response_invoice = gson.fromJson(response, Response_Invoice[].class);
                invoiceAdapter = new InvoiceAdapter(getActivity(),response_invoice);
                mRecyclerView.setAdapter(invoiceAdapter);
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }
}
