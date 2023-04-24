package com.wishbook.catalog.home.more;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingCheckInActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AttendanceAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Attendance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 5/12/16.
 */
public class Fragment_Attendance extends GATrackedFragment {

    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.toggle)
    SwitchCompat toggle;
    private Boolean gps_enabled = false;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView recyclerView;
    // private SearchView searchView;
    private AttendanceAdapter mAdapter;
    private Location mLastLocation;
    private Double latitude, longitude;
    String datetime;
    private ProgressDialog progressDialog;
    private Boolean automatic = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");


        ButterKnife.bind(this, view);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle.isChecked()) {
                    status.setText("Checked in");
                    String statuss = "Checkin";
                    postdata(statuss);

                } else {
                    status.setText("Not Checked in");
                    String statuss = "Checkout";
                    postdata(statuss);
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        Intent intent = new Intent(getActivity(), CallbackLocationFetchingCheckInActivity.class);
        startActivityForResult(intent, 1);

        getData();


        return view;
    }

    private void getData() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(), "attendance", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Response_Attendance[] response_attendances = Application_Singleton.gson.fromJson(response, Response_Attendance[].class);
                if (response_attendances != null) {

                    if (response_attendances.length > 0) {
                        String[] dateArray = response_attendances[0].getDate_time().split("T");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date date = new Date();
                        String dateToCheck = dateFormat.format(date).toString();
                        if (dateToCheck.equals(dateArray[0])) {
                            if (response_attendances[0].getAction().equals("Checkin")) {
                                toggle.setChecked(true);
                                status.setText("Checked in");
                            }
                        }


                        mAdapter = new AttendanceAdapter((AppCompatActivity) getActivity(), response_attendances);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    private void getFromLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            // notify user
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.LightDialogTheme);
            dialog.setMessage("Gps not enabled");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Open Location Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        } else {
            Log.d("SEEsadad", "");
            progressDialog.show();
            getGoogleApi();
        }
    }

    private void getGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d("Getting Location", "");
                            getLocation(0);

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void postdata(String status) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();

        //  params.put("date_time",getformatedDate());
        params.put("date_time", DateUtils.currentUTC());
        params.put("action", status);

        if (latitude != null && longitude != null) {
            params.put("att_lat", String.valueOf(latitude));
            params.put("att_long", String.valueOf(longitude));
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.userUrl(getActivity(), "attendance", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

                Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
                getData();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void onStop() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private void getLocation(int count) {
     /*   if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        count++;

        Log.d("SEE", "");
        while (count < 5) {
            count++;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.d("Fetched Location", "");

            if (mLastLocation != null) {
                progressDialog.dismiss();
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
               progressDialog.dismiss();
                count=5;

            }
            else
            {

            }

        }
        progressDialog.dismiss();

    }

    private String getformatedDate() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
     //   System.out.println(dateFormat.format(date));

        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
      //  System.out.println(dateFormat1.format(cal.getTime()));
        System.out.println(dateFormat.format(date).toString() +"T"+dateFormat1.format(cal.getTime()).toString()+"Z");
        return dateFormat.format(date).toString() +"T"+dateFormat1.format(cal.getTime()).toString()+"Z";
       /* DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = new Date();
        System.out.println(dateFormat.format(date).toString());
        return dateFormat.format(date).toString();*/
    }

    @Override
    public void onResume() {
        super.onResume();
      // getFromLocation();
     /*   Intent i = new Intent(getActivity(), GPSMainActivity.class);
        startActivity(i);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data!=null) {
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.get("latitude") != null && bundle.get("longitude") != null) {
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                }

            }

        }
    }


}
