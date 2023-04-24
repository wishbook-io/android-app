package com.wishbook.catalog.home.catalog.share;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.wishbook.catalog.GATrackedFragment;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_buyergroups;
import com.wishbook.catalog.commonmodels.BuyerSeg;

public class Fragment_ShareProductSelection extends GATrackedFragment {


    private MaterialDialog progressDialog;
    private SharedPreferences pref;
    private Spinner spinner_buyergroups;
    private EditText input_message;
    private TextView errortext;


    public Fragment_ShareProductSelection() {
        // Required empty public constructor
    }


    public static Fragment_ShareProductSelection newInstance() {

        return new Fragment_ShareProductSelection();
    }
    public ArrayList<BuyerSeg> processBuyerGrRes(Exception e, String result) {
        if(e==null){
            Type listOfProductObj = new TypeToken<ArrayList<BuyerSeg>>() {
            }.getType();
            return Application_Singleton.gson.fromJson(result,listOfProductObj);
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_catalog, container, false);
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        spinner_buyergroups=(Spinner)v.findViewById(R.id.spinner_buyergroups);
        errortext=(TextView)v.findViewById(R.id.errortext);
        errortext.setVisibility(View.GONE);
        input_message=(EditText)v.findViewById(R.id.input_message);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        if (StaticFunctions.isOnline(getActivity())) {
            if(DataPasser.shareSelectionSelected!=null){
                progressDialog = StaticFunctions.showProgress(getActivity());
                progressDialog.show();
                NetworkProcessor.with(getActivity())
                        .load( URLConstants.companyUrl(getActivity(),"buyergroups","")).addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
                        .asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.v("res", "" + result);
                        if(e==null) {
                            ArrayList<BuyerSeg> response_buyerGroups = processBuyerGrRes(e, result);
                            if (response_buyerGroups != null) {
                                if (response_buyerGroups.size() > 0) {
                                    final SpinAdapter_buyergroups spinAdapter_buyergroups=new SpinAdapter_buyergroups(getActivity(),R.layout.spinneritem,response_buyerGroups);
                                    spinner_buyergroups.setAdapter(spinAdapter_buyergroups);
                                    spinner_buyergroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            BuyerSeg buyerSeg=spinAdapter_buyergroups.getItem(position);
                                            if(buyerSeg.getActive_buyers().equals("0")){
                                               errortext.setVisibility(View.VISIBLE);
                                            }else{
                                                errortext.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        } else {

        }
        toolbar.setVisibility(View.GONE);
       /* v.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticFunctions.isOnline(getActivity())) {

                    JsonArray catarray=new JsonArray();
                    JsonArray selarray=new JsonArray();
                    selarray.add(new JsonPrimitive(DataPasser.shareSelectionSelected.getId()));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("push_downstream","yes");
                    jsonObject.addProperty("message",""+input_message.getText().toString());
                    jsonObject.addProperty("buyer_segmentation",""+((BuyerSeg) (spinner_buyergroups.getSelectedItem())).getId());
                    jsonObject.add("catalog", catarray);
                    jsonObject.add("selection", selarray);
                    NetworkProcessor.with(getActivity())
                            .load( URLConstants.SHARECATALOG_URL).addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
                            .setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Log.v("res", "" + result);
                            progressDialog.dismiss();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
                }
            }
        });*/
        return v;
    }

}
