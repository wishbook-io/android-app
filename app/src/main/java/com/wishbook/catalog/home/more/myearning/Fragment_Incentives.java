package com.wishbook.catalog.home.more.myearning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.IncentiveDashboard;
import com.wishbook.catalog.commonmodels.responses.IncentiveHistory;
import com.wishbook.catalog.commonmodels.responses.IncentiveTiers;
import com.wishbook.catalog.home.more.myearning.adapter.IncentiveAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Incentives extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;


    @BindView(R.id.recyclerViewIncentives)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_total_target)
    TextView txt_total_target;

    @BindView(R.id.txt_total_sales)
    TextView txt_total_sales;

    @BindView(R.id.txt_target_required)
    TextView txt_target_required;

    @BindView(R.id.txt_total_incentive)
    TextView txt_total_incentive;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_payout_empty)
    LinearLayout empty_linear;

    @BindView(R.id.txt_tc)
    TextView txt_tc;

    @BindView(R.id.nested_incentive)
    NestedScrollView nested_incentive;

    @BindView(R.id.linear_incentive_tier_container)
    LinearLayout linear_incentive_tier_container;

    @BindView(R.id.linear_incentive_root)
            LinearLayout linear_incentive_root;


    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;


    private LinearLayoutManager mLayoutManager;


    public Fragment_Incentives() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_incentives, ga_container, true);
        ButterKnife.bind(this, view);
        nested_incentive.scrollTo(0, 0);
        initView();
        initSwipeRefresh();
        initCall();
        return view;
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        txt_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTcBottomDialog();
            }
        });
    }

    private void initCall() {
        getIncentiveHistory();
        getIncentiveTiers();
    }


    public void initSwipeRefresh() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


    public void getIncentiveDashboard() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "incentive-dashboard", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    IncentiveDashboard dashboard = Application_Singleton.gson.fromJson(response, new TypeToken<IncentiveDashboard>() {
                    }.getType());

                    txt_total_incentive.setText("\u20B9 " + String.valueOf(dashboard.getTotal_incentive_amount()));
                    txt_total_sales.setText(String.valueOf(dashboard.getTotal_weekly_purchase_actual_amount()));
                    txt_target_required.setText(String.valueOf(dashboard.getTotal_weekly_purchase_required_to_reach_target()));
                    txt_total_target.setText(String.valueOf(dashboard.getTotal_weekly_purchase_target_amount()));

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


    public void getIncentiveHistory() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "incentive-history", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                getIncentiveDashboard();
                try {
                    final ArrayList<IncentiveHistory> historyArrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<IncentiveHistory>>() {
                    }.getType());
                    if (historyArrayList.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        empty_linear.setVisibility(View.GONE);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        final IncentiveAdapter incentiveAdapter = new IncentiveAdapter(getActivity(), historyArrayList);
                        mRecyclerView.setAdapter(incentiveAdapter);
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


    public void openTcBottomDialog() {
        Bundle reseller_bundle = new Bundle();
        reseller_bundle.putString("type", "incentive");
        RewardTcBottomSheetFragment rewardTcBottomSheetFragment = new RewardTcBottomSheetFragment();
        rewardTcBottomSheetFragment.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "Incentive program T&C";
        Application_Singleton.CONTAINERFRAG = rewardTcBottomSheetFragment;
        startActivity(new Intent(getActivity(), OpenContainer.class));
    }

    public void getIncentiveTiers() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "incentive-tiers", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {

                    final ArrayList<IncentiveTiers> incentiveTiers = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<IncentiveTiers>>() {
                    }.getType());
                    if(incentiveTiers.size() > 0) {
                        linear_incentive_root.setVisibility(View.VISIBLE);
                        addRow(incentiveTiers,linear_incentive_tier_container);
                    } else {
                        linear_incentive_root.setVisibility(View.GONE);
                    }

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

    private void addRow(ArrayList<IncentiveTiers> incentiveTiers, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < incentiveTiers.size(); i++) {
            View v = vi.inflate(R.layout.incentive_tier_row, null);
            TextView label = (TextView) v.findViewById(R.id.txt_weekly_order_label);
            TextView value = (TextView) v.findViewById(R.id.txt_weekly_order_value);
            if(incentiveTiers.get(i).getTo_amount()!=null && Double.parseDouble(incentiveTiers.get(i).getTo_amount()) > 0) {
                label.setText(("\u20B9"+incentiveTiers.get(i).getFrom_amount() + " to "+incentiveTiers.get(i).getTo_amount()));
            } else {
                label.setText(("\u20B9"+incentiveTiers.get(i).getFrom_amount() + " and "+ "above"));
            }

            value.setText(incentiveTiers.get(i).getIncentive_percentage() + "%");
            root.addView(v);
        }
    }


}

