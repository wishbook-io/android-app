package com.wishbook.catalog.home.more;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.home.Activity_Home;

import java.util.HashMap;

public class Fragment_GST extends GATrackedFragment {

    private View v;
    private EditText input_gst;
    private AppCompatButton btn_add_change;
    private SharedPreferences pref;
    private Toolbar toolbar;
    Boolean gstAlreadyAdded = false;
    String id = "";
    private EditText input_gst2;

    public Fragment_GST() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.gst_layout, container, false);
        toolbar=(Toolbar)v.findViewById(R.id.appbar);
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);


      //  oldPswdEt = (EditText) v.findViewById(R.id.old_pswd);
        input_gst = (EditText) v.findViewById(R.id.input_gst);
        input_gst2 = (EditText) v.findViewById(R.id.input_gst2);


        btn_add_change = (AppCompatButton) v.findViewById(R.id.btn_add_change);

        btn_add_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_gst2.getText().toString().length()==15) {
                    if (gstAlreadyAdded && !id.equals("")) {
                        patchGst(input_gst2.getText().toString(), id);
                    } else {
                        postGst(input_gst2.getText().toString());
                    }
                }else{
                    StaticFunctions.showToast(getActivity(), "GST no. should be of 15 characters");
                }
            }
        });

        getGST();

        toolbar.setVisibility(View.GONE);
        return v;
    }

    private void patchGst(String s, String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        PostKycGst postKycGst = new PostKycGst();
        postKycGst.setGstin(s);
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(),"company_kyc","") + id + '/', gson.fromJson(gson.toJson(postKycGst), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.showToast(getActivity(), "GST no. Changed Successfully");
                // progressDialog.dismiss();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getGST() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""),null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                PostKycGst[] resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst[].class);
                btn_add_change.setText("Add GST no.");
                if(resPostKycGst.length>0 && resPostKycGst[0]!=null ){
                    input_gst.setText(resPostKycGst[0].getGstin());
                    input_gst2.setText(resPostKycGst[0].getGstin());
                    gstAlreadyAdded=true;
                    id = resPostKycGst[0].getId();
                    btn_add_change.setText("Change GST no.");
                }else{
                    btn_add_change.setText("Add GST no.");
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void postGst(String gst) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        params.put("gstin", gst);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);

                if(resPostKycGst!=null) {
                    gstAlreadyAdded = true;
                    id = resPostKycGst.getId();
                }
                // transfer_button.setVisibility(View.GONE);
                Toast.makeText(getContext(), "GST number added successfully", Toast.LENGTH_SHORT).show();
                Activity_Home.pref.edit().putBoolean("kyc_gstin_popup", true).apply();
                if (resPostKycGst.getGstin() != null)
                    Activity_Home.pref.edit().putString("kyc_gstin", resPostKycGst.getGstin()).apply();

                getActivity().finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {

                new MaterialDialog.Builder(getActivity())
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                Log.d("ERRROR", error.toString());
            }
        });
    }


}
