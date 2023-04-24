package com.wishbook.catalog.home.catalog.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ActivityFabricSelect extends AppCompatActivity {


    RecyclerView mRecyclerView;
    EditText edit_search;
    private Context mContext;
    private Toolbar toolbar;
    private LinearLayout linear_button;
    private AppCompatButton btn_save;
    private FabricSelectAdapter fabricSelectAdapter;
    private CategorySelectAdapter categorySelectAdapter;
    private String type;
    private String category_id = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ActivityFabricSelect.this;
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
        initView();
        if(getIntent().getStringExtra("category_id")!=null) {
            category_id = getIntent().getStringExtra("category_id");
        }
        if (getIntent().getStringExtra("type") != null) {
            if (getIntent().getStringExtra("type").equals("fabric")) {
                type = "fabric";
                getFabrics();
            } else if(getIntent().getStringExtra("type").equals("style")){
                type = "style";
                getStyle();
            } else {
                type = "work";
                getWorks();
            }
        }
    }

    private void initView() {
        linear_button = (LinearLayout) findViewById(R.id.linear_button);
        linear_button.setVisibility(View.VISIBLE);
        btn_save = (AppCompatButton) findViewById(R.id.btn_save);
        mRecyclerView = (RecyclerView) findViewById(R.id.buyer_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (fabricSelectAdapter != null) {
                    fabricSelectAdapter.filter(charSequence.toString());
                } else if(categorySelectAdapter!=null){
                    categorySelectAdapter.filter(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (type != null) {
                        if (type.equals("fabric")) {
                            Intent fabricIntent = new Intent();
                            fabricIntent.putStringArrayListExtra("fabric", fabricSelectAdapter.getSelectedItem());
                            setResult(RESULT_OK, fabricIntent);
                            finish();
                        } else if (type.equals("style")) {
                            Intent fabricIntent = new Intent();
                            fabricIntent.putStringArrayListExtra("style", categorySelectAdapter.getSelectedItemStringValue());
                            setResult(RESULT_OK, fabricIntent);
                            finish();
                        } else {
                            Intent fabricIntent = new Intent();
                            fabricIntent.putStringArrayListExtra("work", fabricSelectAdapter.getSelectedItem());
                            setResult(RESULT_OK, fabricIntent);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getFabrics() {
        StaticFunctions.showProgressbar(ActivityFabricSelect.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFabricSelect.this);
        String url = URLConstants.companyUrl(ActivityFabricSelect.this, "enumvalues", "") + "?category="+category_id+"&attribute_slug="+type;
        HttpManager.getInstance(ActivityFabricSelect.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                if (enumGroupResponses != null) {
                    if (enumGroupResponses.length > 0) {
                        ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                        if (getIntent().getStringArrayListExtra("selectedfabric") != null) {
                            fabricSelectAdapter = new FabricSelectAdapter(mContext, enumGroupResponses1, getIntent().getStringArrayListExtra("selectedfabric"),type);
                            mRecyclerView.setAdapter(fabricSelectAdapter);
                        } else {
                            fabricSelectAdapter = new FabricSelectAdapter(mContext, enumGroupResponses1,type);
                            mRecyclerView.setAdapter(fabricSelectAdapter);
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getWorks() {
        StaticFunctions.showProgressbar(ActivityFabricSelect.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFabricSelect.this);
        String url = URLConstants.companyUrl(ActivityFabricSelect.this, "enumvalues", "") + "?category="+category_id+"&attribute_slug="+type;
        HttpManager.getInstance(ActivityFabricSelect.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                if (enumGroupResponses != null) {
                    if (enumGroupResponses.length > 0) {
                        ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                        if(getIntent().getStringArrayListExtra("selectedwork") !=null) {
                            fabricSelectAdapter = new FabricSelectAdapter(mContext, enumGroupResponses1, getIntent().getStringArrayListExtra("selectedwork"),type);
                            mRecyclerView.setAdapter(fabricSelectAdapter);
                        }else {
                            fabricSelectAdapter = new FabricSelectAdapter(mContext, enumGroupResponses1, type);
                            mRecyclerView.setAdapter(fabricSelectAdapter);
                        }
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getStyle() {
        StaticFunctions.showProgressbar(ActivityFabricSelect.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFabricSelect.this);
        String url = URLConstants.companyUrl(ActivityFabricSelect.this, "enumvalues", "") + "?category="+category_id+"&attribute_slug="+type;
        HttpManager.getInstance(ActivityFabricSelect.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                if (enumGroupResponses != null) {
                    if (enumGroupResponses.length > 0) {
                        ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                        if(getIntent().getStringArrayListExtra("selectedstyle") !=null) {
                            categorySelectAdapter = new CategorySelectAdapter(mContext, enumGroupResponses1, getIntent().getStringArrayListExtra("selectedstyle"));
                            mRecyclerView.setAdapter(categorySelectAdapter);
                        }else {
                            categorySelectAdapter = new CategorySelectAdapter(mContext, enumGroupResponses1);
                            mRecyclerView.setAdapter(categorySelectAdapter);
                        }
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFabricSelect.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
