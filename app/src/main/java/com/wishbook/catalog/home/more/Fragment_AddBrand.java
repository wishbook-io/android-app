package com.wishbook.catalog.home.more;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.MultiSpinner;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brands;
import com.wishbook.catalog.commonmodels.AddBrandDistributor;
import com.wishbook.catalog.commonmodels.PatchFlagBrand;
import com.wishbook.catalog.home.models.Response_Brands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_AddBrand extends GATrackedFragment{


    private SharedPreferences pref;
    private MultiSpinner simpleSpinner;
    private SpinAdapter_brands spinAdapter_brands;
    private ArrayList<String> ids = new ArrayList<>();
     ImageView imageView;

    public Fragment_AddBrand() {
        // Required empty public constructor
    }


    public static Fragment_AddBrand newInstance() {

        return new Fragment_AddBrand();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_brand, container, false);
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        imageView = (ImageView) v.findViewById(R.id.imageView2);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle(getResources().getString(R.string.add_your_brand_title));
        // add your brand
        final EditText input_brand=(EditText)v.findViewById(R.id.input_brand);
        simpleSpinner = (MultiSpinner) v.findViewById(R.id.simpleMultiSpinner);


       HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS,URLConstants.companyUrl(getActivity(),"brands","")+"?showall=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                if (response_brands.length > 0) {
                    if (response_brands[0].getId() != null) {

                      spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, response_brands);
                      //  spinner_brands.setAdapter(spinAdapter_brands);
                        simpleSpinner.setAdapter(spinAdapter_brands,false,onSelectedListener);
                        simpleSpinner.setText("Select Brand");
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });




        v.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {
                        "android.permission.CAMERA",
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                };
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 900);
                } else {
                    PhotoTakerUtils.openTaker(getActivity(), "brandpreview.jpg", false,new PhotoTakerUtils.OnCropFinishListener() {
                        @Override
                        public void OnCropFinsh(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                            File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                            File output = new File(mDirectory, "brandpreview.jpg");
                            if (output.exists()) {
                                Log.v("selected image exits", output.getAbsolutePath());
                            }
                        }
                    });
                }
            }
        });
        toolbar.setVisibility(View.GONE);
        v.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                File output = new File(mDirectory, "brandpreview.jpg");
                Log.v("selected image", output.getAbsolutePath());
                if (output.exists() && !input_brand.getText().toString().matches("")) {
                    File outputrenamed = new File(mDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                    try {
                        StaticFunctions.copy(output, outputrenamed);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("name", input_brand.getText().toString());
                    params.put("company", pref.getString("company_id", ""));
                    Log.v("selected image exits", output.getAbsolutePath());




                     /*   NetworkManager.getInstance().HttpRequestwithFile(getActivity(), URLConstants.BRANDADD, params, "image", "image/jpg", outputrenamed, new NetworkManager.customCallBack() {
                        @Override
                        public void onCompleted(int status, String response) {
                            if (status == NetworkManager.RESPONSESUCCESS) {

if (simpleSpinner.getSelected().length > 0 ) {
                            AddDistibuter();
                        }


                            } else {
                                Toast.makeText(getActivity(), "request failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/

                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOADWITHPROGRESS, URLConstants.companyUrl(getActivity(),"brands",""), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            //Model Changed by Abu
                            if (ids.size()> 0) {
                                AddDistibuter();
                                Log.d("SPINNER",simpleSpinner.getSelected().toString());

                            } else {
                                PatchFlagBrand patch = new PatchFlagBrand("yes");
                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                Gson gson = new Gson();
                                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.COMPANY_URL + pref.getString("company_id", "") + '/', gson.fromJson(gson.toJson(patch), JsonObject.class), headers, new HttpManager.customCallBack() {
                                    @Override
                                    public void onCacheResponse(String response) {

                                    }

                                    @Override
                                    public void onServerResponse(String response, boolean dataUpdated) {
                                        Toast.makeText(getActivity(),"Brand added successfully", Toast.LENGTH_SHORT).show();
                                        // StaticFunctions.switchActivity(getActivity(), SplashScreen.class);
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else
                {
                    if(simpleSpinner.getSelected()!=null){
                        if (simpleSpinner.getSelected().length > 0 ) {
                            AddDistibuter();
                        }
                    }
                }

            }
        });

        return v;

    }
    private void AddDistibuter(){
            pref.edit().putString("brandadded", "yes").apply();
            AddBrandDistributor addBrandDistributor=new AddBrandDistributor(pref.getString("company_id", ""),ids);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            Gson gson = new Gson();
            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(),"brands_distributor","") , gson.fromJson(gson.toJson(addBrandDistributor), JsonObject.class), headers,false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    //  StaticFunctions.switchActivity(getActivity(), SplashScreen.class);
                    PatchFlagBrand patch=new PatchFlagBrand("yes");
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    Gson gson = new Gson();
                    HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS,URLConstants.COMPANY_URL+ pref.getString("company_id","") + '/',gson.fromJson(gson.toJson(patch), JsonObject.class),headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Toast.makeText(getActivity(),"Brand added successfully", Toast.LENGTH_SHORT).show();
                        //    StaticFunctions.switchActivity(getActivity(), SplashScreen.class);
                            getActivity().finish();
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                    // progressDialog.dismiss();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    PhotoTakerUtils.openTaker(getActivity(), "brandpreview.jpg",false, new PhotoTakerUtils.OnCropFinishListener() {
                        @Override
                        public void OnCropFinsh(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                            File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                            File output = new File(mDirectory, "brandpreview.jpg");
                            if (output.exists()) {
                                Log.v("selected image exits", output.getAbsolutePath());
                            }
                        }
                    });
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                ids.add(spinAdapter_brands.getItem(i).getId().toString());
                }
            }
            Log.d("Ids",ids.toString());


        }
    };





}
