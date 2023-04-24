package com.wishbook.catalog.Utils.gps;

/**
 * Created by root on 18/4/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;

import java.util.HashMap;
import java.util.Map;

public class CallbackLocationFetchingCheckInActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = CallbackLocationFetchingCheckInActivity.class.getSimpleName();


    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int LOCATION_PERMISSION_REQUEST = 10000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Context mContext;

    //variable
    private LottieAnimationView animation_view;
    private TextView location_text;
    private ProgressBar location_progressBar;
    private AppCompatButton location_no_visits_button;
    private AppCompatButton location_retry;
    private boolean isAskedResolution;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishbook_gps_main_layout);
        mContext = this;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(mContext, "You have permission in init", Toast.LENGTH_SHORT).show();
            initGoogleLocation();


        } else {
            String[] permissions = {
                    "android.permission.ACCESS_NETWORK_STATE",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            };
            Toast.makeText(mContext, "You have not permission in init", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) mContext, permissions, LOCATION_PERMISSION_REQUEST);
        }
        //setUpMapIfNeeded();



        //getting components view
        location_no_visits_button = (AppCompatButton) findViewById(R.id.location_btn_visits);
        location_retry = (AppCompatButton) findViewById(R.id.location_btn_retry);

        location_progressBar = (ProgressBar) findViewById(R.id.location_progressbar);

        location_text = (TextView) findViewById(R.id.location_text);

        animation_view = (LottieAnimationView) findViewById(R.id.location_animation);
        isAskedResolution = false;

        location_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //making things start again
                location_progressBar.setVisibility(View.VISIBLE);
                location_retry.setVisibility(View.INVISIBLE);
                location_no_visits_button.setVisibility(View.INVISIBLE);
                location_text.setText("Fetching Location");
                animation_view.setAnimation("location_pin.json");
                animation_view.loop(true);
                animation_view.playAnimation();

                settingsrequest(mGoogleApiClient);
            }
        });

        location_no_visits_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        if(getIntent()!=null && getIntent().getStringExtra("type")!=null ){
            if(getIntent().getStringExtra("type").equals("checkin")){
                location_no_visits_button.setText("CHECK IN");
            } else {
                location_no_visits_button.setText("CHECK OUT");
            }
        }

    }

    private synchronized void initGoogleLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)// 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            //  setUpMapIfNeeded();
            mGoogleApiClient.connect();
        }


        //making things start again
        location_progressBar.setVisibility(View.VISIBLE);
        location_retry.setVisibility(View.INVISIBLE);
        location_no_visits_button.setVisibility(View.INVISIBLE);
        location_text.setText("Fetching Location");
        animation_view.setAnimation("location_pin.json");
        animation_view.loop(true);
        animation_view.playAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        if (mMap != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");
            mMap.addMarker(options);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
        } else {
            Intent intent = getIntent();
            intent.putExtra("latitude", currentLatitude);
            intent.putExtra("longitude", currentLongitude);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        settingsrequest(mGoogleApiClient);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    public void settingsrequest(final GoogleApiClient googleApiCLient) {
        Log.e("settingsrequest", "Comes");


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setMaxWaitTime(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // Log.e("Application","Button Clicked");
                        getLocation(googleApiCLient);

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "Location Resolution Required", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResult: AskResolution==>"+isAskedResolution );
                        if (!isAskedResolution) {
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult()


                                status.startResolutionForResult(CallbackLocationFetchingCheckInActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                                Log.e("Applicationsett", e.toString());
                            }

                        } else {
                            Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "Location Resolution Required Ask Manually", Toast.LENGTH_LONG).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    location_progressBar.setVisibility(View.INVISIBLE);

                                    location_retry.setVisibility(View.VISIBLE);
                                    location_no_visits_button.setVisibility(View.VISIBLE);

                                    location_text.setText("Unable to fetch your location");
                                    animation_view.cancelAnimation();
                                }
                            }, 2000);
                        }


                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "Setting Change Unavailable", Toast.LENGTH_LONG).show();
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //Log.e("Application","Button Clicked2");
                        // Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                location_progressBar.setVisibility(View.INVISIBLE);

                                location_retry.setVisibility(View.VISIBLE);
                                location_no_visits_button.setVisibility(View.VISIBLE);

                                location_text.setText("Unable to fetch your location");
                                animation_view.cancelAnimation();
                            }
                        }, 2000);
                        break;
                }
            }
        });
    }

    private void getLocation(GoogleApiClient googleApiClient) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "You have not give the permission", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    location_progressBar.setVisibility(View.INVISIBLE);

                    location_retry.setVisibility(View.VISIBLE);
                    location_no_visits_button.setVisibility(View.VISIBLE);

                    location_text.setText("Unable to fetch your location");
                    animation_view.cancelAnimation();
                }
            }, 2000);
            return;
        }
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {

            /*if(!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }*/
            // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    handleNewLocation(locationResult.getLocations().get(0));
                }

                @Override
                public void onLocationAvailability(final LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    Log.d("InLocation", "Availibility" + locationAvailability.isLocationAvailable());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!locationAvailability.isLocationAvailable()) {
                                location_progressBar.setVisibility(View.INVISIBLE);

                                location_retry.setVisibility(View.VISIBLE);
                                location_no_visits_button.setVisibility(View.VISIBLE);

                                location_text.setText("Unable to fetch your location");
                                animation_view.cancelAnimation();
                            }
                        }
                    }, 2000);
                }
            }, null);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                isAskedResolution = true;
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            initGoogleLocation();
                            mGoogleApiClient.connect();
                            settingsrequest(mGoogleApiClient);

                            return;
                        }
                        Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "Fetching location please wait", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(CallbackLocationFetchingCheckInActivity.this, "Unable to get your location", Toast.LENGTH_LONG).show();
                        //finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(Application_Singleton.canUseCurrentAcitivity()){
            new MaterialDialog.Builder(mContext)
                    .content("Do you want to continue meeting without location")
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                            Intent intent = getIntent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    })
                    .negativeText("No, Cancel Meeting")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            Toast.makeText(mContext, "After Request Permission Result", Toast.LENGTH_SHORT).show();
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initGoogleLocation();
                    mGoogleApiClient.connect();
                } else {
                    finish();
                }
            }
        }
    }
}