package com.wishbook.catalog.home.orderNew.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.FollowUsers;
import com.wishbook.catalog.commonmodels.responses.RequestAddBuyers;
import com.wishbook.catalog.home.contacts.add.Fragment_AddBuyer;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.adapters.BuyerSearchAdapter;
import com.wishbook.catalog.home.orderNew.adapters.FollowersAdapter;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Activity_BuyerSearch extends AppCompatActivity implements
        BuyerSearchAdapter.UpdateBuyerListener, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView buyer_recyclerview;
    Spinner spinner_state;
    Spinner spinner_city;

    private Context mContext;
    private Toolbar toolbar;
    private EditText edit_search;
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private LinearLayout linear_button;
    private AppCompatButton btn_save;
    private boolean isMultipleSelect = false;
    BuyerSearchAdapter buyerSearchAdapter;
    FollowersAdapter followersAdapter;
    private String bottomAction = "";
    private ArrayList<NameValues> previouslyAdded = new ArrayList<>();
    private String selling_company_id, supplier_id;
    private String type;
    LinearLayout linear_filter_city;

    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private String stateId = "";
    private String cityId = "";
    private String State = "";
    private String City = "";


    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;


    // required arguments toolbarTitle,isMultipleSelect,bottom_action,type,previousArray (Arraylist nameValues)
    // pass data -->"buyer" List<NameValue> when multiselect otherwise Buyerlist object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_BuyerSearch.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_buyer_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search.requestFocus();
        if (getIntent().getStringExtra("toolbarTitle") != null) {
            edit_search.setHint(getIntent().getStringExtra("toolbarTitle"));
        }
        initView();
    }

    private void initView() {
        buyer_recyclerview = (RecyclerView) findViewById(R.id.buyer_recyclerview);
        linear_button = (LinearLayout) findViewById(R.id.linear_button);
        btn_save = (AppCompatButton) findViewById(R.id.btn_save);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_state = (Spinner) findViewById(R.id.spinner_state);
        linear_filter_city = (LinearLayout) findViewById(R.id.linear_filter_city);
        if (getIntent().getBooleanExtra("isMultipleSelect", false)) {
            isMultipleSelect = true;
            if (getIntent().getStringExtra("bottom_action") != null) {
                if (getIntent().getStringExtra("bottom_action").equals("delete")) {
                    bottomAction = "delete";
                    btn_save.setText("Remove");
                }
                if (getIntent().getStringExtra("bottom_action").equals("save")) {
                    bottomAction = "save";
                    btn_save.setText("Save");
                }
                if (getIntent().getStringExtra("bottom_action").equals("add")) {
                    bottomAction = "add";
                    btn_save.setText("Add");
                }
            } else {
                linear_button.setVisibility(View.VISIBLE);
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        buyer_recyclerview.setLayoutManager(mLayoutManager);
        if (getIntent().getSerializableExtra("previouslyArray") != null) {
            previouslyAdded = (ArrayList<NameValues>) getIntent().getSerializableExtra("previouslyArray");
        }


        initSwipeRefresh();
        initCall(false);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomAction.equals("delete")) {
                    if (type.equals("connected_buyers")) {
                        ArrayList<String> companies = new ArrayList<String>();
                        for (int i = 0; i < buyerSearchAdapter.getSelectedBuyers().size(); i++) {
                            companies.add(buyerSearchAdapter.getSelectedBuyers().get(i).getPhone());
                        }
                        if (supplier_id != null) {
                            removeConnectedBuyers(supplier_id, companies);
                        }
                    }
                } else {
                    Intent searchIntent = new Intent();
                    List<NameValues> buyers = buyerSearchAdapter.getSelectedBuyers();
                    searchIntent.putExtra("buyer", (Serializable) buyers);
                    setResult(Activity.RESULT_OK, searchIntent);
                    finish();
                }
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (type.equals("myfollowers")) {
                    if (followersAdapter != null) {
                        followersAdapter.filter(charSequence.toString());
                    }
                } else {
                    if (buyerSearchAdapter != null) {
                        buyerSearchAdapter.filter(charSequence.toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initCall(boolean isRefresh) {
        if (getIntent().getStringExtra("type") != null) {
            if (getIntent().getStringExtra("type").equals("supplier")) {
                type = "supplier";
                linear_filter_city.setVisibility(View.VISIBLE);
                if(isRefresh) {
                    getSuppliers(true);
                } else {
                    getstates();
                    getSuppliers(false);
                }
            } else if (getIntent().getStringExtra("type").equals("buyer")) {
                type = "buyer";
                linear_filter_city.setVisibility(View.VISIBLE);
                if(isRefresh) {
                    getBuyers("buyerlist",true);
                } else {
                    getstates();
                    getBuyers("buyerlist",false);
                }
            } else if (getIntent().getStringExtra("type").equals("connected_buyers")) {
                type = "connected_buyers";
                if (getIntent().getStringExtra("selling_comapny_id") != null) {
                    selling_company_id = getIntent().getStringExtra("selling_comapny_id");
                    getConnectedBrokerage(selling_company_id);
                }
                if (getIntent().getStringExtra("supplier_id") != null) {
                    supplier_id = getIntent().getStringExtra("supplier_id");
                    Log.i("TAG", "initView: Supplier ID" + supplier_id);
                }
            } else if (getIntent().getStringExtra("type").equals("myfollowers")) {
                type = "myfollowers";
                getBrandsFollow();
            }
        } else {
            linear_filter_city.setVisibility(View.VISIBLE);
            type = "buyer";
            if(isRefresh) {
                getBuyers("buyerlist",true);
            } else {
                getstates();
                getBuyers("buyerlist",false);
            }

           /* if(Activity_Home.pref!=null) {
                if (!Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                    getBuyers("buyerlist",false);
                }
            }*/

        }
    }

    private void getBuyers(String url, final boolean isCityFilter) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        if (url.equals("buyerlist_no_deputed")) {
            params.put("without_deputed", "true");
        }
        params.put("status", "approved");
        params.put("search", "");
        if (!cityId.equals("")) {
            params.put("city", cityId);
        }
        if (!stateId.equals("")) {
            params.put("state", stateId);
        }
        StaticFunctions.showProgressbar(Activity_BuyerSearch.this);
        HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "buyerlist", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                if (!isCityFilter) {
                    BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                    if (buyers.length > 0) {
                        ArrayList<BuyersList> buyersLists = new ArrayList<BuyersList>(Arrays.asList(buyers));
                        //here getPhone() means buyingComanyId
                        for (int i = 0; i < buyersLists.size(); i++) {
                            for (int j = 0; j < previouslyAdded.size(); j++) {
                                if (previouslyAdded.get(j).getPhone().equals(buyersLists.get(i).getCompany_id())) {
                                    buyersLists.remove(i);
                                }
                            }
                        }
                        buyerSearchAdapter = new BuyerSearchAdapter(mContext, R.layout.spinneritem, buyersLists, "buyerlist", isMultipleSelect, false);
                        buyer_recyclerview.setAdapter(buyerSearchAdapter);
                        buyerSearchAdapter.setSelectedListener(Activity_BuyerSearch.this);
                    } else {
                        new MaterialDialog.Builder(Activity_BuyerSearch.this)
                                .title(getResources().getString(R.string.no_buyer_title))
                                .content(getResources().getString(R.string.no_buyer_coonected_error))
                                .positiveText(getResources().getString(R.string.alert_yes))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        showAddBuyerDialog();
                                    }
                                })
                                .negativeText(getResources().getString(R.string.alert_no))
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                } else {
                    BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                    buyerSearchAdapter.getAllItems().clear();
                    if (buyers.length > 0) {
                        ArrayList<BuyersList> buyersLists = new ArrayList<BuyersList>(Arrays.asList(buyers));
                        //here getPhone() means buyingComanyId
                        for (int i = 0; i < buyersLists.size(); i++) {
                            for (int j = 0; j < previouslyAdded.size(); j++) {
                                if (previouslyAdded.get(j).getPhone().equals(buyersLists.get(i).getCompany_id())) {
                                    buyersLists.remove(i);
                                }
                            }
                        }
                        buyerSearchAdapter.getAllItems().addAll(buyersLists);
                        buyerSearchAdapter.notifyDataSetChanged();
                        buyerSearchAdapter.refreshArrayListSearch();
                    } else {
                        buyerSearchAdapter.notifyDataSetChanged();
                        buyerSearchAdapter.refreshArrayListSearch();
                    }

                }




            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getSuppliers(final boolean isFilter) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        params.put("status", "approved");
        params.put("search", "");
        if (!cityId.equals("")) {

            params.put("city", cityId);
        }
        if (!stateId.equals("")) {
            params.put("state", stateId);
        }
        StaticFunctions.showProgressbar(Activity_BuyerSearch.this);
        HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "suppliers_list", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                if(!isFilter){
                    BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                    if (buyers.length > 0) {
                        ArrayList<BuyersList> buyersLists = new ArrayList<BuyersList>(Arrays.asList(buyers));
                        buyerSearchAdapter = new BuyerSearchAdapter(mContext, R.layout.spinneritem, buyersLists, "suppliers_list", isMultipleSelect, true);
                        buyer_recyclerview.setAdapter(buyerSearchAdapter);
                        buyerSearchAdapter.setSelectedListener(Activity_BuyerSearch.this);
                    }
                } else {
                    BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                    buyerSearchAdapter.getAllItems().clear();
                    if (buyers.length > 0) {
                        ArrayList<BuyersList> buyersLists = new ArrayList<BuyersList>(Arrays.asList(buyers));
                        buyerSearchAdapter.getAllItems().addAll(buyersLists);
                        buyerSearchAdapter.notifyDataSetChanged();
                        buyerSearchAdapter.refreshArrayListSearch();
                    } else {
                        buyerSearchAdapter.notifyDataSetChanged();
                        buyerSearchAdapter.refreshArrayListSearch();
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getConnectedBrokerage(String seller_company_id) {
        Log.i("TAG", "getConnectedBrokerage: Seller ID" + seller_company_id);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        StaticFunctions.showProgressbar(Activity_BuyerSearch.this);
        HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_BuyerSearch.this, "get_connected_buyers_broker", seller_company_id), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                //onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                BuyersList[] buyers = Application_Singleton.gson.fromJson(response, BuyersList[].class);
                if (buyers.length > 0) {
                    ArrayList<BuyersList> buyersLists = new ArrayList<BuyersList>(Arrays.asList(buyers));
                    buyerSearchAdapter = new BuyerSearchAdapter(mContext, R.layout.spinneritem, buyersLists, "buyerlist", isMultipleSelect, false);
                    buyer_recyclerview.setAdapter(buyerSearchAdapter);
                    buyerSearchAdapter.setSelectedListener(Activity_BuyerSearch.this);
                } else {
                    new MaterialDialog.Builder(Activity_BuyerSearch.this)
                            .title(getResources().getString(R.string.no_buyer_title))
                            .content(getResources().getString(R.string.no_buyer_coonected_error))
                            .positiveText(getResources().getString(R.string.connect_buyers))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    startActivityForResult(new Intent(mContext, Activity_BuyerSearch.class)
                                                    .putExtra("isMultipleSelect", true)
                                                    .putExtra("bottom_action", "save"),
                                            Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                Log.i("TAG", "onResponseFailed: Connected Brokerage");
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void getTotalSelected(int count) {
        Log.i("TAG", "getTotalSelected: " + count);
        if (isMultipleSelect && (bottomAction.equals("delete") || bottomAction.equals("save") || bottomAction.equals("add"))) {
            if (count == 0) {
                linear_button.setVisibility(View.GONE);
            } else {
                linear_button.setVisibility(View.VISIBLE);
            }
        }
    }

    public void removeConnectedBuyers(String supplierId, ArrayList<String> companies) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(Activity_BuyerSearch.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(Activity_BuyerSearch.this, "remove_connected_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                setResult(Activity.RESULT_OK);
                finish();
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getBrandsFollow() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        StaticFunctions.showProgressbar(Activity_BuyerSearch.this);
        HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(this, "my_followers", ""), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                FollowUsers[] followUserses = new Gson().fromJson(response, FollowUsers[].class);
                if (followUserses.length > 0) {
                    ArrayList<FollowUsers> buyersLists = new ArrayList<FollowUsers>(Arrays.asList(followUserses));
                    followersAdapter = new FollowersAdapter(mContext, buyersLists);
                    buyer_recyclerview.setAdapter(followersAdapter);
                } else {
                    new MaterialDialog.Builder(Activity_BuyerSearch.this)
                            .title("No Followers")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_BuyerSearch.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void showAddBuyerDialog() {
        Fragment_AddBuyer addBuyerFragment = new Fragment_AddBuyer();
        addBuyerFragment.setListener(new Fragment_AddBuyer.Listener() {
            @Override
            public void onDismiss() {
                Log.i("TAG", "onDismiss: Dialog Called");
                if (type.equals("buyer")) {
                    getBuyers("buyerlist",false);
                }

            }
        });
        addBuyerFragment.show(getSupportFragmentManager(), "addbuyer");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<NameValues> selectedBuyers = (List<NameValues>) data.getSerializableExtra("buyer");
            ArrayList<String> companies = new ArrayList<>();
            //getPhone equals comapnyid
            for (NameValues s : selectedBuyers) {
                companies.add(s.getPhone());
            }
            isAllowCache =false;
            addBuyers(supplier_id, companies);
        }
    }

    public void addBuyers(final String supplierId, ArrayList<String> companies) {
        Log.i("TAG", "addBuyers Request: ==>" + companies.size() + "\n Supplier id" + supplierId);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_BuyerSearch.this);
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(Activity_BuyerSearch.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(mContext, "add_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                getConnectedBrokerage(selling_company_id);
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                Log.i("TAG", "onResponseFailed: ===Add Buyers");
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getstates() {
        HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GET, URLConstants.companyUrl(mContext, "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                if (allstates != null) {

                    Response_States blankState = new Response_States("-1","Select State");
                    List<Response_States> newstate = new ArrayList<Response_States>(Arrays.asList(allstates));
                    newstate.add(0, new Response_States("", "Select State"));
                    allstates = newstate.toArray(new Response_States[newstate.size()]);

                    SpinAdapter_State spinAdapter_state = new SpinAdapter_State(Activity_BuyerSearch.this, R.layout.spinneritem,R.id.spintext, allstates);
                    spinner_state.setAdapter(spinAdapter_state);
                }
                if (allstates != null) {
                    if (State != null) {
                        for (int i = 0; i < allstates.length; i++) {
                            if (State.equals(allstates[i].getState_name())) {
                                spinner_state.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(mContext)
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
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    Log.i("TAG", "onItemSelected: State==>" + stateId);
                    if (type.equals("buyer")) {
                        if (buyerSearchAdapter != null) {
                          getBuyers("buyerlist",true);
                        } else {
                            if(position!=0){
                                getBuyers("buyerlist",false);
                            }
                        }
                    } else if(type.equals("supplier")){
                        if (buyerSearchAdapter != null) {
                            getSuppliers(true);
                        }
                    }

                    HttpManager.getInstance(Activity_BuyerSearch.this).request(HttpManager.METHOD.GET, URLConstants.companyUrl(mContext, "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (allcities != null) {


                                List<Response_Cities> newstate = new ArrayList<Response_Cities>(Arrays.asList(allcities));
                                newstate.add(0, new Response_Cities("", ",","Select City", ""));
                                allcities = newstate.toArray(new Response_Cities[newstate.size()]);

                                SpinAdapter_City spinAdapter_city = new SpinAdapter_City(Activity_BuyerSearch.this, R.layout.spinneritem, allcities);
                                spinner_city.setAdapter(spinAdapter_city);
                                if (State != null && State != "") {
                                    if (City != null) {
                                        for (int i = 0; i < allcities.length; i++) {
                                            if (City.equals(allcities[i].getCity_name())) {
                                                spinner_city.setSelection(i);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            new MaterialDialog.Builder(mContext)
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
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = allcities[position].getId();
                Log.i("TAG", "onItemSelected: city==>" + cityId);
                if (type.equals("buyer")) {
                    if (buyerSearchAdapter != null) {
                        getBuyers("buyerlist",true);
                    } else {
                        if(allcities.length > 0){
                            if(!allcities[position].getCity_name().equals("Select City")) {
                                getBuyers("buyerlist",false);
                            }
                        }
                    }
                } else if(type.equals("supplier")) {
                    if (buyerSearchAdapter != null) {
                        getSuppliers(true);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void initSwipeRefresh() {
        swipe_container = findViewById(R.id.swipe_container);
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
                edit_search.setText("");
                initCall(true);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }
}
