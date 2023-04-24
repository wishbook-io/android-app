package com.wishbook.catalog.home.contacts.details;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BuyerGroupListAdapter;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.PatchBuyersGroup;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.catalog.details.Activity_ComapnyCatalogs;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityBuyerGroupList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;

    @BindView(R.id.edit_search)
    EditText edit_search;

    @BindView(R.id.linear_add_participant)
    LinearLayout linear_add_participant;

    @BindView(R.id.linear_edit_name)
    LinearLayout linear_edit_name;

    @BindView(R.id.edit_group_name)
    EditText edit_group_name;

    @BindView(R.id.btn_save_name)
    TextView btn_save_name;

    @BindView(R.id.btn_close)
    ImageView btn_close;

    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;

    BuyerGroupListAdapter buyerGroupListAdapter;
    ArrayList<Response_Buyer> responseArrayList;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    boolean isCustomGroup = false;

    private static String TAG = Activity_ComapnyCatalogs.class.getSimpleName();
    private boolean isAnayChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ActivityBuyerGroupList.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_buyer_group_list);
        ButterKnife.bind(this);
        setToolbar();
        initView();
        initSwipeRefresh();
    }

    private void setToolbar() {
        if (getIntent().getStringExtra("name") != null) {
            toolbar.setTitle(getIntent().getStringExtra("name"));
            edit_search.setHint(String.format(getResources().getString(R.string.buyer_group_search), getIntent().getStringExtra("name")));
        }

        if (getIntent().getBooleanExtra("isCustomGroup", false)) {
            isCustomGroup = true;
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnayChange){
                    setResult(Activity.RESULT_OK);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);


    }


    private void initView() {
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        responseArrayList = new ArrayList<>();
        if (getIntent().getStringExtra("id") != null) {
            getAllBuyersFromGroup(getIntent().getStringExtra("id"));
        }
        if (isCustomGroup) {
            linear_add_participant.setVisibility(View.VISIBLE);
        } else {
            linear_add_participant.setVisibility(View.GONE);
        }
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (buyerGroupListAdapter != null) {
                    buyerGroupListAdapter.filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        linear_add_participant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ActivityBuyerGroupList.this, Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("bottom_action", "add"),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
    }


    private void getAllBuyersFromGroup(final String buyerSegmentationId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBuyerGroupList.this);
        StaticFunctions.showProgressbar(ActivityBuyerGroupList.this);
        HttpManager.getInstance(ActivityBuyerGroupList.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(ActivityBuyerGroupList.this, "buyers_group_list", buyerSegmentationId), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                Response_Buyer[] buyers = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);

                if (buyers.length > 0) {
                    responseArrayList = new ArrayList<Response_Buyer>(Arrays.asList(buyers));
                    if (responseArrayList.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        empty_linear.setVisibility(View.GONE);
                        buyerGroupListAdapter = new BuyerGroupListAdapter(ActivityBuyerGroupList.this, responseArrayList,isCustomGroup,buyerSegmentationId,toolbar.getTitle().toString());
                        mRecyclerView.setAdapter(buyerGroupListAdapter);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        empty_linear.setVisibility(View.VISIBLE);
                        edit_search.setVisibility(View.GONE);
                    }

                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    empty_linear.setVisibility(View.VISIBLE);
                    edit_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<NameValues> selectedBuyers = (List<NameValues>) data.getSerializableExtra("buyer");
            if (selectedBuyers != null && selectedBuyers.size() > 0) {
                patchBuyers(getIntent().getStringExtra("id"), selectedBuyers,null);
            }
            StaticFunctions.selectedBuyers.clear();
        }
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
                getAllBuyersFromGroup(getIntent().getStringExtra("id"));
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void patchBuyers(String buyerSegmentationId, List<NameValues> selectedBuyers, final String name) {
        PatchBuyersGroup patchBuyersGroup = new PatchBuyersGroup();
        ArrayList<Integer> company = new ArrayList<>();
        if(responseArrayList!=null){
            for (int i = 0; i < responseArrayList.size(); i++) {
                company.add(Integer.parseInt(responseArrayList.get(i).getBuying_company()));
            }
        }
        if(selectedBuyers!=null){
            for (int i = 0; i < selectedBuyers.size(); i++) {
                company.add(Integer.parseInt(selectedBuyers.get(i).getPhone()));
            }
        }
        if(company.size() > 0) {
            patchBuyersGroup.setBuyer(company);
        }

        if(name==null){
            patchBuyersGroup.setSegmentation_name(getIntent().getStringExtra("name"));
        } else {
            patchBuyersGroup.setSegmentation_name(name);
        }


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBuyerGroupList.this);
        StaticFunctions.showProgressbar(ActivityBuyerGroupList.this);
        HttpManager.getInstance(ActivityBuyerGroupList.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(ActivityBuyerGroupList.this, "buyergroups", "") + buyerSegmentationId + "/", Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchBuyersGroup), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                edit_search.setText("");
                if(name!=null){
                    toolbar.setTitle(name);
                }
                hideEditName();
                isAnayChange = true;
                getAllBuyersFromGroup(getIntent().getStringExtra("id"));
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                new MaterialDialog.Builder(ActivityBuyerGroupList.this)
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


    public void deleteBuyers(String buyerSegmentationId, ArrayList<Response_Buyer> selectedBuyers, final String name) {
        PatchBuyersGroup patchBuyersGroup = new PatchBuyersGroup();
        ArrayList<Integer> company = new ArrayList<>();
        if(selectedBuyers!=null){
            for (int i = 0; i < selectedBuyers.size(); i++) {
                company.add(Integer.parseInt(selectedBuyers.get(i).getBuying_company()));
            }
        }
        //if(company.size() > 0) {
            patchBuyersGroup.setBuyer(company);
       // }

        if(name==null){
            patchBuyersGroup.setSegmentation_name(getIntent().getStringExtra("name"));
        } else {
            patchBuyersGroup.setSegmentation_name(name);
        }


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityBuyerGroupList.this);
        StaticFunctions.showProgressbar(ActivityBuyerGroupList.this);
        HttpManager.getInstance(ActivityBuyerGroupList.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(ActivityBuyerGroupList.this, "buyergroups", "") + buyerSegmentationId + "/", Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchBuyersGroup), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                edit_search.setText("");
                if(name!=null){
                    toolbar.setTitle(name);
                }
                hideEditName();
                isAnayChange = true;
                getAllBuyersFromGroup(getIntent().getStringExtra("id"));
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityBuyerGroupList.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if(isCustomGroup){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit_group, menu);
        } else {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_group_edit) {
            openEditName();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openEditName() {
        int x = linear_edit_name.getRight();
        int y = linear_edit_name.getBottom();
        linear_edit_name.setVisibility(View.GONE);
        int startRadius = 0;
        int endRadius = (int) Math.hypot(linear_edit_name.getWidth(), linear_edit_name.getHeight());
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(linear_edit_name, x, y, startRadius, endRadius);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    edit_group_name.setText(toolbar.getTitle());
                    edit_group_name.setSelection(toolbar.getTitle().length());
                    edit_group_name.requestFocus();
                    KeyboardUtils.showKeyboard(edit_group_name,context);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.setDuration(300);
            anim.start();
            linear_edit_name.setVisibility(View.VISIBLE);
        } else {
            edit_group_name.setText(toolbar.getTitle());
            edit_group_name.setSelection(toolbar.getTitle().length());
            linear_edit_name.setVisibility(View.VISIBLE);
        }



        edit_search.setVisibility(View.GONE);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linear_edit_name.getVisibility() == View.VISIBLE){
                    hideEditName();
                    edit_group_name.clearFocus();
                    KeyboardUtils.hideKeyboard(ActivityBuyerGroupList.this);
                }
            }
        });

        btn_save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_group_name.getText().toString().isEmpty()){
                    Toast.makeText(context,"Please enter valid name",Toast.LENGTH_SHORT).show();
                    return;
                }
                patchBuyers(getIntent().getStringExtra("id"),null,edit_group_name.getText().toString());
                isAnayChange = true;
            }
        });
    }

    public void hideEditName() {
        toolbar.setVisibility(View.VISIBLE);
        linear_edit_name.setVisibility(View.GONE);
        if(responseArrayList!=null && responseArrayList.size() > 0) {
            edit_search.setVisibility(View.VISIBLE);
        }
    }
}
