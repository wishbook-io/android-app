package com.wishbook.catalog.home.more.visits;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.pendingTasks.BuyerDetailFrag;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Vigneshkarnika on 10/04/16.
 */
public class Fragment_CheckIn extends DialogFragment {
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Double latitude;
    private Double longitude;
    private GoogleMap map = null;
    private SupportMapFragment mapFragment;

    //new added
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private CustomAutoCompleteTextView buyer_select;
    AutoCompleteCommonAdapter adapter;
    private BuyersList buyer = null;
    private EditText note;
    private Listener listener;


    TextInputLayout input_buyername;
    TextView edit_buyername;
    LinearLayout buyer_container;
    EditText edit_new_buyer;

    private LinearLayout mapcontainer;
    private TextView location_error_text;
    public interface Listener {
        void onGoToMeeting();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void onStop() {

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.checkindailog, container, false);
        //buyer_select = (CustomAutoCompleteTextView) rootView.findViewById(R.id.buyer_select);
        note = (EditText) rootView.findViewById(R.id.note_text);
        mapcontainer = (LinearLayout) rootView.findViewById(R.id.mapcontainerq);
        location_error_text = (TextView) rootView.findViewById(R.id.location_error_text);
        input_buyername = (TextInputLayout) rootView.findViewById(R.id.input_buyername);
        edit_buyername = (TextView) rootView.findViewById(R.id.edit_buyername);
        edit_new_buyer = (EditText) rootView.findViewById(R.id.edit_new_buyer);
        buyer_container = (LinearLayout) rootView.findViewById(R.id.buyer_container);
        buyer_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class),Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
       // getBuyers();

       /* adapter = new AutoCompleteCommonAdapter(getContext(), R.layout.spinneritem, buyerslist,"buyerlist");
        buyer_select.setAdapter(adapter);
        buyer_select.setThreshold(1);
        buyer_select.setLoadingIndicator(
                (android.widget.ProgressBar) rootView.findViewById(R.id.progress_bar));*/


        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
                dismiss();
            }
        });


        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");

            if(latitude==0.0 && longitude==0.0){

                mapcontainer.setVisibility(View.GONE);
                location_error_text.setVisibility(View.VISIBLE);

            }
            else{
                mapcontainer.setVisibility(View.VISIBLE);
                location_error_text.setVisibility(View.GONE);
            }
        }


       /* buyer_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buyer = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buyer_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                buyer = (BuyersList) parent.getItemAtPosition(position);
            }
        });*/


        rootView.findViewById(R.id.btn_gotomeet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BuyersList selectedbuyer = buyer;
                HashMap<String, String> params = new HashMap<String, String>();
                if (buyer != null) {
                    params.put("buying_company_ref", "" + selectedbuyer.getCompany_id());
                }

                if (!note.getText().equals("")) {
                    params.put("note", note.getText().toString());
                }
                if(!edit_new_buyer.getText().toString().trim().isEmpty()){
                    params.put("buyer_name_text",edit_new_buyer.getText().toString().trim());
                }

                params.put("start_datetime", "" + DateUtils.currentUTC());
                params.put("start_lat", "" + latitude);
                params.put("start_long", "" + longitude);
                NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.POST, URLConstants.userUrl(getActivity(), "meetings", ""), params, new NetworkManager.customCallBack() {
                    @Override
                    public void onCompleted(int status, String response) {
                        if (status == NetworkManager.RESPONSESUCCESS) {
                            // Response_meeting response_meeting=(Response_meeting)Application_Singleton.gson.fromJson(response,Response_meeting.class);
                            Activity_Home.pref.edit().putString("currentmeet", response).apply();

                            if (buyer != null) {
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
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();


                                    }

                                    @Override
                                    public void onResponseFailed(ErrorString error) {

                                    }
                                });
                            } else {
                                dismiss();
                                if (listener != null) {
                                    listener.onGoToMeeting();
                                }
                            }


                        } else {

                        }
                    }
                });
            }
        });
        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.mapcontainerq, mapFragment, "map").commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
                    addMarkerToMap();
                }
            }
        });
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                                @Override
                                                public void onConnected(Bundle bundle) {
                                                  addMarkerToMap();
                                                }

                                                @Override
                                                public void onConnectionSuspended(int i) {

                                                }
                                            }

                    )
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                                       @Override
                                                       public void onConnectionFailed(ConnectionResult connectionResult) {

                                                       }
                                                   }

                    )
                    .addApi(LocationServices.API)

                    .build();
        }
        mGoogleApiClient.connect();
        return rootView;
    }

    private void addMarkerToMap() {
       /* mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);*/
        if (map!=null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude)));
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

        }
    }

    private void getBuyers() {
        if (buyerslist.size() < 1) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyerlist", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);
                    buyerslist.clear();
                    BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                    for (BuyersList buyerList : buyers) {
                        buyerslist.add(buyerList);
                    }
                    adapter = new AutoCompleteCommonAdapter(getContext(), R.layout.spinneritem, buyerslist,"buyerlist");
                    buyer_select.setAdapter(adapter);
                    buyer_select.setThreshold(1);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    try {
                        StaticFunctions.showResponseFailedDialog(error);
                    }catch (Exception e){

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data!=null && data.getSerializableExtra("buyer")!=null){
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                edit_buyername.setText(buyer.getCompany_name());
            }
        }
    }
}