package com.wishbook.catalog.home.more;

import android.Manifest;
import android.app.Activity;
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

import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_AddBrandOwn extends GATrackedFragment {


    private SharedPreferences pref;
    private ArrayList<String> ids = new ArrayList<>();
    ImageView imageView;
    Bitmap img_bitmap = null;

    public Fragment_AddBrandOwn() {
        // Required empty public constructor
    }


    public static Fragment_AddBrandOwn newInstance() {

        return new Fragment_AddBrandOwn();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_brand_own, container, false);
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        imageView = (ImageView) v.findViewById(R.id.imageView2);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        final EditText input_brand = (EditText) v.findViewById(R.id.input_brand);


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
                    PhotoTakerUtils.openTaker(getActivity(), "brandpreview.jpg",false, new PhotoTakerUtils.OnCropFinishListener() {
                        @Override
                        public void OnCropFinsh(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                            img_bitmap = bitmap;
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
                if(img_bitmap!=null) {
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

                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOADWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", ""), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                //Model Changed by Abu
                                Toast.makeText(getActivity(), "Brands Added Successfully", Toast.LENGTH_LONG).show();
                                getActivity().setResult(Activity.RESULT_OK);
                                getActivity().finish();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(),"Image can't be empty",Toast.LENGTH_SHORT).show();
                }



            }
        });

        return v;

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
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    PhotoTakerUtils.openTaker(getActivity(), "brandpreview.jpg", false,new PhotoTakerUtils.OnCropFinishListener() {
                        @Override
                        public void OnCropFinsh(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                            img_bitmap = bitmap;
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
}
