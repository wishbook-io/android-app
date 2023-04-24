package com.wishbook.catalog.home.contacts.details;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.MyContactAdapter1;
import com.wishbook.catalog.commonmodels.Contact;
import com.wishbook.catalog.commonmodels.dbmodel.ResponseCache;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class Activity_ConnectionList extends AppCompatActivity implements MyContactAdapter1.SelectedListener {


    private Context mContext;
    private Toolbar toolbar;
    private EditText edit_search;

    private LinearLayout linear_button;
    private AppCompatButton btn_save;
    RecyclerView buyer_recyclerview;
    private List<Contact> contactList = new ArrayList<>();
    MyContactAdapter1 myContactAdapter;

    private ArrayList<Response_Buyer> response_buyers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_ConnectionList.this;
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
        findViewById(R.id.spinner_city).setVisibility(View.GONE);
        findViewById(R.id.spinner_state).setVisibility(View.GONE);
        findViewById(R.id.linear_filter_city).setVisibility(View.GONE);

        btn_save.setText("ADD");
        btn_save.setBackgroundColor(getResources().getColor(R.color.color_primary));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        buyer_recyclerview.setLayoutManager(mLayoutManager);
        buyer_recyclerview.setItemAnimator(new DefaultItemAnimator());
        buyer_recyclerview.addItemDecoration(new DividerItemDecoration(mContext, null));

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(myContactAdapter!=null) {
                    myContactAdapter.filter(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent();
                setResult(Activity.RESULT_OK, searchIntent);
                finish();
            }
        });
        getBuyersList();

    }

    private void getBuyersList() {

        Log.v("onCreate", "called approved buyers");

        HttpManager.METHOD methodType;
        methodType = HttpManager.METHOD.GETWITHPROGRESS;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_ConnectionList.this);
        HttpManager.getInstance(Activity_ConnectionList.this).request(methodType, URLConstants.companyUrl(mContext, "buyers_approved", ""), null, headers, true, new HttpManager.customCallBack() {
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
                    response_buyers = new ArrayList<Response_Buyer>(Arrays.asList(response_buyer));
                    String cacheContacts = getCacheifExists("wishBook_contacts");
                    for (int i = 0; i < response_buyers.size(); i++) {
                        Contact contact = new Contact(response_buyers.get(i).getBuying_person_name(), response_buyers.get(i).getBuying_company_phone_number(), false, true);
                        contactList.add(contact);
                    }
                    myContactAdapter = new MyContactAdapter1(Activity_ConnectionList.this, contactList, null, "buyer");
                    buyer_recyclerview.setAdapter(myContactAdapter);
                    myContactAdapter.setSelectedListener(Activity_ConnectionList.this);
                    if (cacheContacts != null) {
                        //  cacheContacts
                        Gson gson = new Gson();
                        Type type1 = new TypeToken<List<Contact>>() {
                        }.getType();
                        contactList.addAll((Collection<? extends Contact>) gson.fromJson(cacheContacts, type1));
                        myContactAdapter = new MyContactAdapter1(Activity_ConnectionList.this, contactList, null, "buyer");
                        buyer_recyclerview.setAdapter(myContactAdapter);
                        myContactAdapter.setSelectedListener(Activity_ConnectionList.this);
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private String getCacheifExists(String md5) {
        ResponseCache res = new Gson().fromJson(PrefDatabaseUtils.getPrefWishbookContacts(Activity_ConnectionList.this), new TypeToken<ResponseCache>() {
        }.getType());
        if (res != null) {
            return res.getResponse();
        }
        return null;
    }

    @Override
    public void getTotalSelected(int count) {
        Log.i("TAG", "getTotalSelected: " + count);
        if (count == 0) {
            linear_button.setVisibility(View.GONE);
        } else {
            linear_button.setVisibility(View.VISIBLE);
        }
    }
}
