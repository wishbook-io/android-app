package com.wishbook.catalog.home.more;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.WBMoneyAdapter;
import com.wishbook.catalog.commonmodels.ResponseWBMoneyDashboard;
import com.wishbook.catalog.commonmodels.responses.ResponseWBHistory;
import com.wishbook.catalog.home.adapters.SimpleSectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_Wb_Money extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    private Context mContext;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar_back)
    ImageView toolbar_back;

    @BindView(R.id.back_button)
    ImageView back_button;

    @BindView(R.id.txt_redeemed_money)
    TextView txt_redeemed_money;

    @BindView(R.id.txt_received_money)
    TextView txt_received_money;

    @BindView(R.id.txt_available_money)
    TextView txt_available_money;

    @BindView(R.id.txt_empty_text)
    TextView txt_empty_text;

    private LinearLayoutManager mLayoutManager;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    String total_available = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_Wb_Money.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_wbmoney);
        ButterKnife.bind(this);
        initView();
        initSwipeRefresh();
        initListner();
        initCall();
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        txt_empty_text.setText(mContext.getResources().getString(R.string.empty_wb_money));

    }

    private void initListner() {
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appbar.addOnOffsetChangedListener(this);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initCall() {
        getWBMoneyHistory();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        try {
            if (Math.abs(offset) == appBarLayout.getTotalScrollRange()) {
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setTitle(total_available + " WB Money Available");
                toolbar.setTitleTextColor(getResources().getColor(R.color.color_primary));
                setSupportActionBar(toolbar);
                toolbar_back.setVisibility(View.VISIBLE);
                collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));
            } else if (offset == 0) {
                toolbar.setTitle("");
                toolbar.setVisibility(View.GONE);
                toolbar_back.setVisibility(View.GONE);
                collapsing_toolbar.setTitle("");
            } else {
                toolbar.setTitle("");
                toolbar_back.setVisibility(View.GONE);
                collapsing_toolbar.setTitle("");
                toolbar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void getWBMoneyDashboard() {
        StaticFunctions.showProgressbar(Activity_Wb_Money.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Wb_Money.this);
        String url = URLConstants.companyUrl(Activity_Wb_Money.this, "wbmoney-log-dashboard", "");
        HttpManager.getInstance(Activity_Wb_Money.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_Wb_Money.this);
                try {
                    ResponseWBMoneyDashboard dashboard = Application_Singleton.gson.fromJson(response, new TypeToken<ResponseWBMoneyDashboard>() {
                    }.getType());
                    if (dashboard.getTotal_available() != null) {
                        txt_available_money.setText(dashboard.getTotal_available());
                        total_available = dashboard.getTotal_available();
                    }

                    if (dashboard.getTotal_received() != null) {
                        txt_received_money.setText(dashboard.getTotal_received());
                    }

                    if (dashboard.getTotal_redeemed() != null) {
                        txt_redeemed_money.setText(dashboard.getTotal_redeemed());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_Wb_Money.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void getWBMoneyHistory() {
        StaticFunctions.showProgressbar(Activity_Wb_Money.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_Wb_Money.this);
        String url = URLConstants.companyUrl(Activity_Wb_Money.this, "wbmoney-log", "");
        HttpManager.getInstance(Activity_Wb_Money.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(Activity_Wb_Money.this);
                getWBMoneyDashboard();
                try {
                    final ArrayList<ResponseWBHistory> historyArrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseWBHistory>>() {
                    }.getType());
                    if (historyArrayList.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        empty_linear.setVisibility(View.GONE);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext.getResources().getDrawable(R.drawable.divider_drawable),
                                true, true));
                        final WBMoneyAdapter wbMoneyAdapter = new WBMoneyAdapter(mContext, historyArrayList);
                        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
                        for (int i = 0; i < historyArrayList.size(); i++) {
                            String t1 = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, historyArrayList.get(i).getCreated());
                            SimpleSectionedRecyclerViewAdapter.Section temp = new SimpleSectionedRecyclerViewAdapter.Section(i, t1);
                            if (!sections.contains(temp)) {
                                sections.add(temp);
                            }
                        }

                        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
                        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                                SimpleSectionedRecyclerViewAdapter(mContext, R.layout.recycler_sticky_header, R.id.list_item_section_text, wbMoneyAdapter);
                        mSectionedAdapter.setSections(sections.toArray(dummy));
                        mRecyclerView.setAdapter(mSectionedAdapter);
                        wbMoneyAdapter.notifyDataSetChanged();

                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        empty_linear.setVisibility(View.VISIBLE);
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_Wb_Money.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
