package com.wishbook.catalog.home.contacts.details;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingCheckInActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.pendingTasks.BuyerDetailFrag;

import java.util.HashMap;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Vigneshkarnika on 10/04/16.
 */
public class Fragment_CheckInWithBuyer extends DialogFragment {
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap map = null;
    private SupportMapFragment mapFragment;
    String id = null;
    String company_name = "";
    String broker_company = "";
    String company_id = "";
    private Double check_in_latitude;
    private Double check_in_longitude;
    //new added

    private final static int LOCATION_PERMISSION_REQUEST = 10000;

    private final static int CHECKIN_REQUEST_CODE = 1;

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.checkindailogwithbuyer, container, false);

        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
                dismiss();
            }
        });
        check_in_latitude = 0.0;
        check_in_longitude = 0.0;

        if (getArguments() != null) {
            id = getArguments().getString("id");
            company_id = getArguments().getString("company_id");
            company_name = getArguments().getString("company_name");
            broker_company = getArguments().getString("broker_company");
        }


        rootView.findViewById(R.id.btn_gotomeet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_in_latitude != null && check_in_longitude!=null ) {
                    if (id != null) {
                        final BuyersList selectedbuyer = new BuyersList();
                        selectedbuyer.setCompany_id(company_id);
                        selectedbuyer.setBroker_id(broker_company);
                        selectedbuyer.setId(id);
                        selectedbuyer.setCompany_name(company_name);
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("buying_company_ref", "" + selectedbuyer.getCompany_id());
                        params.put("start_datetime", "" + DateUtils.currentUTC());
                        params.put("start_lat", "" + check_in_latitude);
                        params.put("start_long", "" + check_in_longitude);
                        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.POST, URLConstants.userUrl(getActivity(), "meetings", ""), params, new NetworkManager.customCallBack() {
                            @Override
                            public void onCompleted(int status, String response) {
                                if (status == NetworkManager.RESPONSESUCCESS) {
                                    // Response_meeting response_meeting=(Response_meeting)Application_Singleton.gson.fromJson(response,Response_meeting.class);
                                    Activity_Home.pref.edit().putString("currentmeet", response).apply();
                                    Activity_Home.pref.edit().putString("currentmeetbuyer", Application_Singleton.gson.toJson(selectedbuyer)).apply();


                                    //added by Abu
                                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + "?company=" + selectedbuyer.getCompany_id(), null, headers, true, new HttpManager.customCallBack() {
                                        @Override
                                        public void onCacheResponse(String response) {
                                            Log.v("cached response", response);
                                            onServerResponse(response, false);
                                        }

                                        @Override
                                        public void onServerResponse(String response, boolean dataUpdated) {
                                            Log.v("sync response", response);

                                            Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);

                                            Application_Singleton.navselectedBuyer = response_buyer[0];
                                            dismiss();

                                            BuyerDetailFrag buyerDetailFrag = new BuyerDetailFrag();
                                            Application_Singleton.CONTAINERFRAG = buyerDetailFrag;
                                            Application_Singleton.CONTAINER_TITLE = "Visits";
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean("fromBuyerDirectly", true);
                                            buyerDetailFrag.setArguments(bundle);
                                            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);


                                        }

                                        @Override
                                        public void onResponseFailed(ErrorString error) {

                                        }
                                    });


                                } else {

                                }
                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), "Error occured in selecting buyer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Unable to fetch your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.mapcontainerq, mapFragment, "map").commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                   addMarkerToMap();
            }
        });

        Intent intent = new Intent(getActivity(), CallbackLocationFetchingCheckInActivity.class);
        intent.putExtra("type", "checkin");
        startActivityForResult(intent, CHECKIN_REQUEST_CODE);

        return rootView;


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Fragement Check in With Buyer request code" + requestCode + "\n Result code=>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECKIN_REQUEST_CODE) {
            //where there is some callback from locationActivity
            if (data != null) {
                Bundle bundle = data.getExtras();
                Double latitude = 0.0;
                Double longitude = 0.0;
                //if bundle has something i.e. location
                if (bundle != null) {
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    check_in_latitude = latitude;
                    check_in_longitude = longitude;
                   addMarkerToMap();
                }
            }
        }
    }

    private void addMarkerToMap() {
        if (map!=null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(check_in_latitude, check_in_longitude)));
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(check_in_latitude, check_in_longitude), 13));
        }
    }


}