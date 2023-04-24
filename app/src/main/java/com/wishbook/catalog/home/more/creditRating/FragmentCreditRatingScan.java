package com.wishbook.catalog.home.more.creditRating;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestCreditRating;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentCreditRatingScan extends GATrackedFragment {

    @BindView(R.id.btn_scan)
    AppCompatButton btn_scan;

    @BindView(R.id.btn_manually)
    AppCompatButton btn_manually;

    String from ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_scan_intro, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        if(getArguments()!=null && getArguments().getString("from")!=null) {
            from = getArguments().getString("from");
        }
        getCompanyRating();
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {
                        "android.permission.CAMERA",
                };
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 900);
                } else {
                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                    FragmentCreditRatingScan.this.startActivityForResult(i, 9000);
                }
            }
        });
        btn_manually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from",from);
                FragmentCreditRating fragment = new FragmentCreditRating();
                fragment.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                getActivity().finish();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9000 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringExtra("content") != null) {
                Bundle bundle1 = new Bundle();
                FragmentCreditRating fragment = new FragmentCreditRating();
                bundle1.putString("content", data.getStringExtra("content"));
                bundle1.putString("from",from);
                fragment.setArguments(bundle1);
                Application_Singleton.CONTAINERFRAG = fragment;
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                getActivity().finish();
            }
        }
    }

    private void getCompanyRating() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "solo-propreitorship-kyc", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        final RequestCreditRating[] creditRatings = new Gson().fromJson(response, RequestCreditRating[].class);
                        if (creditRatings.length > 0) {
                            UserInfo.getInstance(getActivity()).setCreditRating(true);
                        } else {
                            UserInfo.getInstance(getActivity()).setCreditRating(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                    FragmentCreditRatingScan.this.startActivityForResult(i, 9000);
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }
}
