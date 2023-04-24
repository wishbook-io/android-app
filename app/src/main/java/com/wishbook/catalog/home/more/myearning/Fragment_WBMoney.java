package com.wishbook.catalog.home.more.myearning;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
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

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_WBMoney extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private Context mContext;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;


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

    @BindView(R.id.nested_wb_money)
    NestedScrollView nested_wb_money;

    private LinearLayoutManager mLayoutManager;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    String total_available = "0";


    public Fragment_WBMoney() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_wb_money, ga_container, true);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        nested_wb_money.scrollTo(0,0);
        initView();
        initSwipeRefresh(view);
        initCall();
        return view;
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        txt_empty_text.setText(mContext.getResources().getString(R.string.empty_wb_money));

    }

    private void initCall() {
        getWBMoneyHistory();
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
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void getWBMoneyDashboard() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "wbmoney-log-dashboard", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
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
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getWBMoneyHistory() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "wbmoney-log", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
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
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
