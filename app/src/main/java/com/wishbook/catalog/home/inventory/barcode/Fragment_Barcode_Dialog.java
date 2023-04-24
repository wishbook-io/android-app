package com.wishbook.catalog.home.inventory.barcode;

/**
 * Created by root on 19/11/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_warehouse;
import com.wishbook.catalog.commonmodels.responses.Response_warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Fragment_Barcode_Dialog extends DialogFragment implements ZXingScannerView.ResultHandler {



    private Spinner spinner_warehouse;
    private AppCompatButton btn_discard;
    private AppCompatButton btn_scan;
    private AppCompatButton btn_save;
    private ArrayList<Response_warehouse> response_warehouses = new ArrayList<>();
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private TextView barcode;
    private String id=null;
    private ZXingScannerView mScannerView;
    public static String countrysel="+91";

    @Override
    public void handleResult(Result result) {

    }

    public interface Listener{
        void onDismiss();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.barcode_dialog);

        barcode = (TextView) dialog.findViewById(R.id.barcode);

        spinner_warehouse=(Spinner)dialog.findViewById(R.id.spinner_warehouse);
        getwarehouse();

        btn_discard=(AppCompatButton)dialog.findViewById(R.id.btn_discard);
        btn_save=(AppCompatButton)dialog.findViewById(R.id.btn_save);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_scan=(AppCompatButton)dialog.findViewById(R.id.btn_scan);

        if(getArguments()!=null)
        {
            id=getArguments().getString("id");
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(barcode.getText().toString()!="" && spinner_warehouse.getSelectedItem()!=null && id!=null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("barcode", barcode.getText().toString());
                    params.put("warehouse", ((Response_warehouse) spinner_warehouse.getSelectedItem()).getId());
                    params.put("product", id);
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.BARCODE_SAVE, params, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                Toast.makeText(getActivity(), "Barcode Save Successfully", Toast.LENGTH_LONG).show();
                                dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });
                }

            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {

                    Intent i = new Intent(getActivity(),SimpleScannerActivity.class);
                    startActivityForResult(i, 1);
                }
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void getwarehouse() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"warehouse",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    Response_warehouse[] response_warehous = Application_Singleton.gson.fromJson(response, Response_warehouse[].class);
                    response_warehouses = new ArrayList<Response_warehouse>(Arrays.asList(response_warehous));
                    SpinAdapter_warehouse spinAdapter_warehouse = new SpinAdapter_warehouse(getActivity(), R.layout.spinneritem, response_warehouses);
                    spinner_warehouse.setAdapter(spinAdapter_warehouse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("sync response", error.getErrormessage());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                btn_save.setVisibility(View.VISIBLE);
            barcode.setText(data.getStringExtra("content").toString());
            }
        }
    }

}

