package com.wishbook.catalog.home.more.visits.FIrstLevelExpandableView;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.home.more.visits.SecondLevelExpandableView.AdapterCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 28/10/16.
 */

public class BuyerSelect_Dialog extends DialogFragment implements AdapterCallback{

    private RecyclerView mRecyclerView;
    private StateExpandableAdapter stateAdapter;
    private StateListItem[] listItem;
    Dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)  {

        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.buyer_select_list);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        /*dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));*/

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        getBuyersList();




        return dialog;
    }

    private void getBuyersList() {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"buyers_expand_false_statewise",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

                listItem = Application_Singleton.gson.fromJson(response, StateListItem[].class);
                List<StateListItem> items = new ArrayList<StateListItem>(Arrays.asList(listItem));
                stateAdapter = new StateExpandableAdapter(getActivity(),items,dialog);
                stateAdapter.setCallbackAdapter(BuyerSelect_Dialog.this);
                mRecyclerView.setAdapter(stateAdapter);



            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }


    public void getData(HashMap<String, String> params)
    {
        Intent i = new Intent()
                .putExtra("parameters", params);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

}
