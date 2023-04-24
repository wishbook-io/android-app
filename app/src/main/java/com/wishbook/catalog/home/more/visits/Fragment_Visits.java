package com.wishbook.catalog.home.more.visits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.gps.CallbackLocationFetchingCheckInActivity;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonadapters.MeetingAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Fragment_Visits extends GATrackedFragment implements SearchView.OnQueryTextListener, Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {


    private final static int CHECKIN_REQUEST_CODE = 1;
    private final static int CHECKOUT_REQUEST_CODE = 2;
    private final static int NEWBUYER_REQUEST_CODE = 600;
    private final static int NEWBUYER_RESULT_CODE = 601;
    public static TextView details_met_date;
    public static TextView details_met_time;
    public static TextView details_met_status;
    public static TextView details_met_duration;
    public static TextView details_met_buyername;
    public static TextView details_met_noOforder;
    public static TextView details_met_noOfpcs;
    public static RelativeLayout descontent;
    public static String buyer_ref = "null";
    public static LinearLayout text_duration;
    public static LinearLayout visit_duration_layout;
    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    AutoCompleteCommonAdapter adapter;
    int lastFirstVisiblePosition = 0;
    TextView edit_buyername;
    SharedPreferences pref;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    ArrayList<Response_meeting> data = new ArrayList<>();
    HashMap<String, String> paramsClone = null;
    Paginate paginate;
    int page;
    private View v;
    private RecyclerView mRecyclerView;
    private MeetingAdapter meetingAdapter = null;
    private Response_meeting response_meeting;
    private ArrayList<Response_meeting> filtered_response_meetings = new ArrayList<>();
    private AppCompatButton checkinbut;
    private boolean gps_enabled;
    private SearchView searchView;
    private CustomAutoCompleteTextView buyer_select;
    private BuyersList buyer = null;
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private SwipeRefreshLayout swipe_container;
    private ArrayList<Response_meeting> response_meetings = new ArrayList<>();

    public Fragment_Visits() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_visits, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        checkinbut = ((AppCompatButton) v.findViewById(R.id.btn_checkin));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        details_met_date = (TextView) v.findViewById(R.id.visit_date);
        details_met_buyername = (TextView) v.findViewById(R.id.visit_buyer_name);
        details_met_duration = (TextView) v.findViewById(R.id.visit_duration);
        text_duration = (LinearLayout) v.findViewById(R.id.duration_text_layout);
        visit_duration_layout = (LinearLayout) v.findViewById(R.id.visit_duration_layout);
        details_met_noOforder = (TextView) v.findViewById(R.id.visit_order_no);
        details_met_noOfpcs = (TextView) v.findViewById(R.id.visit_pcs_no);
        details_met_status = (TextView) v.findViewById(R.id.visit_status);
        details_met_time = (TextView) v.findViewById(R.id.visit_time);
        descontent = (RelativeLayout) v.findViewById(R.id.descontent);
        searchView = (SearchView) v.findViewById(R.id.group_search);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        meetingAdapter = new MeetingAdapter((AppCompatActivity) getActivity(), data, Fragment_Visits.this);
        mRecyclerView.setAdapter(meetingAdapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;

        paramsClone = new HashMap<>();
        paramsClone.put("limit", String.valueOf(LIMIT));
        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
        fetchMeetingData(true);
        initSwipeRefresh(v);


        // change for sometime crashes
        pref = StaticFunctions.getAppSharedPreferences(getActivity());
        if (pref != null) {
            if (pref.getString("currentmeet", "na") != null) {
                if (!pref.getString("currentmeet", "na").equals("na")) {
                    checkinbut.setText("CHECKOUT");
                }
            }
        }
        toolbar.setVisibility(View.GONE);
        checkinbut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkinbut.getText().toString().equals("CHECKOUT")) {
                    Intent intent = new Intent(getActivity(), CallbackLocationFetchingCheckInActivity.class);
                    intent.putExtra("type", "checkout");
                    startActivityForResult(intent, CHECKOUT_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(getActivity(), CallbackLocationFetchingCheckInActivity.class);
                    intent.putExtra("type", "checkin");
                    startActivityForResult(intent, CHECKIN_REQUEST_CODE);
                }
            }
        });


        return v;
    }

    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                paramsClone = new HashMap<>();
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                fetchMeetingData(false);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                fetchMeetingData(false);

            }
        }
    }


    private void patchMeeting(HashMap<String, String> params, final MaterialDialog dialog) {

        Log.i("TAG", "patchMeeting: ");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "meetings_with_id", response_meeting.getId()), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Activity_Home.pref.edit().putString("currentmeet", "na").apply();
                Activity_Home.pref.edit().putString("currentmeetbuyer", "na").apply();


                Application_Singleton.selectedBuyer.clear();
                onRefresh();
                checkinbut.setText("CHECK IN");
               /* NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.userUrl(getActivity(), "meetings", ""), null, new NetworkManager.customCallBack() {
                    @Override
                    public void onCompleted(int status, String response) {
                        if (status == NetworkManager.RESPONSESUCCESS) {
                            Log.e("TAG", "onCompleted: Called===>" );
                            Type type = new TypeToken<ArrayList<Response_Buyer>>() {
                            }.getType();
                            response_meetings = Application_Singleton.gson.fromJson(response, type);
                            meetingAdapter = new MeetingAdapter((AppCompatActivity) getActivity(), response_meetings, Fragment_Visits.this);
                            mRecyclerView.setAdapter(meetingAdapter);
                            checkinbut.setText("CHECK IN");
                        }
                    }
                });*/
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
       /* NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), "PATCH", URLConstants.userUrl(getActivity(), "meetings_with_id", response_meeting.getId()), params, new NetworkManager.customCallBack() {
            @Override
            public void onCompleted(int status, String response) {
                if (status == NetworkManager.RESPONSESUCCESS) {
                    // Response_meeting response_meeting=(Response_meeting)Application_Singleton.gson.fromJson(response,Response_meeting.class);
                    Activity_Home.pref.edit().putString("currentmeet", "na").apply();
                    Activity_Home.pref.edit().putString("currentmeetbuyer", "na").apply();


                    Application_Singleton.selectedBuyer.clear();


                    if (dialog != null) {
                        dialog.dismiss();
                    }

                } else {

                }
            }
        });*/
    }

    private void patchBuyerWithMeeting(String meetingId, BuyersList buyer) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "" + meetingId);
        params.put("buying_company_name", buyer.getCompany_name());
        params.put("buying_company_ref", buyer.getCompany_id());
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "meetings_with_id", meetingId), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Application_Singleton.MEETING_ID = null;
                fetchMeetingData(false);

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                Application_Singleton.MEETING_ID = null;
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (response_meetings != null) {
            if (response_meetings.size() > 0) { // Check if the Original List and Constraint aren't null.
                filtered_response_meetings.clear();
                newText = newText.toLowerCase();
                for (int i = 0; i < response_meetings.size(); i++) {
                    try {

                        if (response_meetings.get(i).getBuying_company_name() != null) {
                            String status = response_meetings.get(i).getStatus().toLowerCase();
                            if (response_meetings.get(i).getBuying_company_name().toLowerCase().contains(newText.toLowerCase())

                                    || response_meetings.get(i).getBuyer_name_text().toLowerCase().contains(newText.toLowerCase())
                                    || response_meetings.get(i).getNote().toLowerCase().contains(newText.toLowerCase())
                                    || response_meetings.get(i).getBuying_company_ref().toLowerCase().contains(newText.toLowerCase())

                                    || response_meetings.get(i).getStart_datetime().contains(newText)
                                    || response_meetings.get(i).getEnd_datetime().contains(newText)
                                    || status.contains(newText)) {
                                filtered_response_meetings.add(response_meetings.get(i));
                            }
                        } else {
                            String status = response_meetings.get(i).getStatus().toLowerCase();
                            if (response_meetings.get(i).getBuyer_name_text().toLowerCase().contains(newText.toLowerCase())
                                    || response_meetings.get(i).getStart_datetime().contains(newText)
                                    || response_meetings.get(i).getEnd_datetime().contains(newText)

                                    || response_meetings.get(i).getBuyer_name_text().toLowerCase().contains(newText.toLowerCase())
                                    || response_meetings.get(i).getNote().toLowerCase().contains(newText.toLowerCase())
                                    || response_meetings.get(i).getBuying_company_ref().toLowerCase().contains(newText.toLowerCase())
                                    || status.contains(newText)) {
                                Log.d("QUERYSEARCHNOTE", response_meetings.get(i).getNote());
                                filtered_response_meetings.add(response_meetings.get(i));
                            }
                        }
                    } catch (Exception e) {

                    }
                }


                //Response_meeting[] filteredArray = filtered_response_meetings.toArray(new Response_meeting[filtered_response_meetings.size()]);
                meetingAdapter = new MeetingAdapter((AppCompatActivity) getActivity(), filtered_response_meetings, Fragment_Visits.this);
                mRecyclerView.setAdapter(meetingAdapter);

            }
        }
        return false;
    }

    public void fetchMeetingData(final boolean isAllowCache) {
        final HashMap<String, String> params = paramsClone;
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                data.clear();
                if (meetingAdapter != null)
                    meetingAdapter.notifyDataSetChanged();
                hasLoadedAllItems = false;
            }
        } else {
            params.put("offset", "0");
            page = 0;
            data.clear();
            if (meetingAdapter != null)
                meetingAdapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }
        final int offset = Integer.parseInt(params.get("offset"));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(), "meetings", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Loading = false;

                Type type = new TypeToken<ArrayList<Response_meeting>>() {
                }.getType();
                response_meetings = Application_Singleton.gson.fromJson(response, type);

                if (response_meetings.size() > 0) {
                    data.addAll(response_meetings);
                    page = (int) Math.ceil((double) data.size() / LIMIT);
                    if (response_meetings.size() % LIMIT != 0) {
                        hasLoadedAllItems = true;
                    }
                    if (data.size() <= LIMIT) {
                        meetingAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    meetingAdapter.notifyItemRangeChanged(offset, data.size() - 1);
                                }
                            });

                        } catch (Exception e) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    hasLoadedAllItems = true;
                    meetingAdapter.notifyDataSetChanged();
                }
                try {
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

                }

                for (int i = 0; i < response_meetings.size(); i++)
                    if (response_meetings.get(i).getStatus().equals("pending")) {
                        final String text = Application_Singleton.gson.toJson(response_meetings.get(i));


                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        if (response_meetings.get(i).getBuying_company_name() != null && !response_meetings.get(i).getBuying_company_name().equals("") && !response_meetings.get(i).getBuying_company_name().equals("null")) {
                            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + "?company=" + response_meetings.get(i).getBuying_company_ref(), null, headers, isAllowCache, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {
                                    Log.v("cached response", response);
                                    onServerResponse(response, false);
                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    Log.v("sync response", response);


                                    Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                                    if (response_buyer.length > 0) {
                                        final String buyer = Application_Singleton.gson.toJson(response_buyer[0]);

                                        Activity_Home.pref.edit().putString("currentmeet", text).apply();
                                        Activity_Home.pref.edit().putString("currentmeetbuyer", buyer).apply();
                                        checkinbut.setText("CHECKOUT");
                                    }


                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {

                                }
                            });
                        } else {
                            Activity_Home.pref.edit().putString("currentmeet", text).apply();
                            checkinbut.setText("CHECKOUT");
                        }

                    }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Fragement Visit request code" + requestCode + "\n Result code=>" + resultCode);
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
                }
                //if there is no meeting in pending
                if (Activity_Home.pref.getString("currentmeet", "na").equals("na")) {
                    try {
                        Handler handler = new Handler();
                        final Double finalLatitude = latitude;
                        final Double finalLongitude = longitude;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //initiate checkin dialog
                                initiateCheckIn(finalLatitude, finalLongitude);
                            }
                        }, 500);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Location fetched successfully please recheckin", Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                Toast.makeText(getActivity(), "Error fetching your location", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == CHECKOUT_REQUEST_CODE) {
            //where there is some callback from locationActivity
            if (data != null) {
                Bundle bundle = data.getExtras();
                Double latitude = 0.0;
                Double longitude = 0.0;
                if (bundle != null) {
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                }
                makeMeeting(latitude, longitude);

            } else {
                Toast.makeText(getActivity(), "Error fetching your location", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                edit_buyername.setText(buyer.getCompany_name());
            }
        } else if (requestCode == Application_Singleton.ADD_NEW_BUYER_REQUEST_CODE && resultCode == Application_Singleton.ADD_NEW_BUYER_RESPONSE_CODE) {
            fetchMeetingData(false);
        } else if (requestCode == Application_Singleton.EXISTING_BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                BuyersList buyer = (BuyersList) data.getSerializableExtra("buyer");
                patchBuyerWithMeeting(Application_Singleton.MEETING_ID, buyer);
            }
        }
    }

    private void makeMeeting(final Double latitude, final Double longitude) {
        try {
            response_meeting = Application_Singleton.gson.fromJson(Activity_Home.pref.getString("currentmeet", "na"), Response_meeting.class);
            if (response_meeting.getBuying_company_name() != null && !response_meeting.getBuying_company_name().equals("") && !response_meeting.getBuying_company_name().equals("null")) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", "" + response_meeting.getId());
                params.put("end_datetime", DateUtils.currentUTC());
                params.put("end_lat", "" + latitude);
                params.put("end_long", "" + longitude);
                params.put("status", "done");
                patchMeeting(params, null);
            } else {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("Do you want to add buyer ?").positiveText("Yes").negativeText("No").customView(R.layout.buyer_select_view, true).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (buyer != null) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("id", "" + response_meeting.getId());
                            params.put("buying_company_name", buyer.getCompany_name());
                            params.put("buying_company_ref", buyer.getCompany_id());
                            params.put("end_datetime", DateUtils.currentUTC());
                            params.put("end_lat", "" + latitude);
                            params.put("end_long", "" + longitude);
                            params.put("status", "done");
                            patchMeeting(params, dialog);
                        } else {
                            Toast.makeText(getActivity(), "Please select the buyer", Toast.LENGTH_LONG).show();
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("id", "" + response_meeting.getId());
                        params.put("end_datetime", DateUtils.currentUTC());
                        params.put("end_lat", "" + latitude);
                        params.put("end_long", "" + longitude);
                        params.put("status", "done");
                        patchMeeting(params, dialog);
                    }
                }).build();


                TextInputLayout input_buyername = (TextInputLayout) dialog.getCustomView().findViewById(R.id.input_buyername);
                edit_buyername = (TextView) dialog.getCustomView().findViewById(R.id.edit_buyername);
                LinearLayout buyer_container = (LinearLayout) dialog.getCustomView().findViewById(R.id.buyer_container);
                buyer_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                    }
                });


                /*buyer_select = (CustomAutoCompleteTextView) dialog.getCustomView().findViewById(R.id.buyer_select);

                buyer_select.addTextChangedListener(new TextWatcher() {
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
                });

                adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, buyerslist,"buyerlist");
                buyer_select.setAdapter(adapter);
                buyer_select.setThreshold(1);
                buyer_select.setLoadingIndicator(
                        (android.widget.ProgressBar) dialog.getCustomView().findViewById(R.id.progress_bar));*/
                dialog.show();
               /* HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                params.put("status", "approved");
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "buyerlist", ""), params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        //change by abu
                        buyerslist.clear();
                        BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                        for (BuyersList buyerList : buyers) {
                            buyerslist.add(buyerList);
                        }
                        try {
                            adapter = new AutoCompleteCommonAdapter(getContext(), R.layout.spinneritem, buyerslist);
                            dialog.show();
                            buyer_select.setAdapter(adapter);
                            buyer_select.setThreshold(1);

                        }catch (Exception e){

                        }
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
                    }
                });*/

            }


        } catch (Exception e) {

        }
    }

    private void initiateCheckIn(Double latitude, Double longitude) {
        Fragment_CheckIn checkinDialogFrag = new Fragment_CheckIn();
        Bundle innerBundle = new Bundle();
        innerBundle.putDouble("latitude", latitude);
        innerBundle.putDouble("longitude", longitude);
        checkinDialogFrag.setArguments(innerBundle);
        checkinDialogFrag.setListener(new Fragment_CheckIn.Listener() {
            @Override
            public void onGoToMeeting() {
                NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.userUrl(getActivity(), "meetings", ""), null, new NetworkManager.customCallBack() {
                    @Override
                    public void onCompleted(int status, String response) {
                        if (status == NetworkManager.RESPONSESUCCESS) {
                            Type type = new TypeToken<ArrayList<Response_meeting>>() {
                            }.getType();
                            response_meetings = Application_Singleton.gson.fromJson(response, type);
                            meetingAdapter = new MeetingAdapter((AppCompatActivity) getActivity(), response_meetings, Fragment_Visits.this);
                            mRecyclerView.setAdapter(meetingAdapter);
                            checkinbut.setText("CHECKOUT");
                        }
                    }
                });
            }
        });
        checkinDialogFrag.setCancelable(false);
        checkinDialogFrag.show(getChildFragmentManager(), "checkin");
    }


    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {
            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                searchView.setQuery("", false);
                paramsClone = new HashMap<>();
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                data.clear();
                page = 0;
                response_meetings.clear();
                meetingAdapter = new MeetingAdapter((AppCompatActivity) getActivity(), data, Fragment_Visits.this);
                mRecyclerView.setAdapter(meetingAdapter);
                fetchMeetingData(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }


}