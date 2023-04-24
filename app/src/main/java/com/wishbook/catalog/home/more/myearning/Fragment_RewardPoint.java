package com.wishbook.catalog.home.more.myearning;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.KYC_model;
import com.wishbook.catalog.commonmodels.ResponseWBMoneyDashboard;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseWBHistory;
import com.wishbook.catalog.home.adapters.SimpleSectionedRecyclerViewAdapter;
import com.wishbook.catalog.home.more.myearning.adapter.RewardPointAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_RewardPoint extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks {

    private View view;
    private Context mContext;

    ArrayList<KYC_model> kyc_modelArrayList;

    @BindView(R.id.recyclerViewPayout)
    RecyclerView mRecyclerView;


    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;


    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    String total_available = "0";

    private LinearLayoutManager mLayoutManager;
    ArrayList<ResponseWBHistory> historyArrayList;
    ResponseWBMoneyDashboard dashboard;
    HashMap<String, String> paramsClone = null;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
    List<SimpleSectionedRecyclerViewAdapter.Section> sections;
    RewardPointAdapter rewardPointAdapter;


    public Fragment_RewardPoint() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_reward_points, ga_container, true);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        historyArrayList = new ArrayList<>();
        sections = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        initView();
        initSwipeRefresh();
        initCall();
        return view;
    }

    private void initView() {

    }

    private void initCall() {
        getRewardpointDashboard();
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

    public void getRewardpointDashboard() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "wbpoint-dashboard", "");
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
                    rewardPointAdapter = new RewardPointAdapter(mContext, historyArrayList, dashboard);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(rewardPointAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    if (paginate != null) {
                        paginate.unbind();
                    }

                    paginate = Paginate.with(mRecyclerView, Fragment_RewardPoint.this)
                            .setLoadingTriggerThreshold(2)
                            .addLoadingListItem(true)
                            .build();
                    Loading = false;

                    paramsClone = new HashMap<>();
                    paramsClone.put("limit", String.valueOf(LIMIT));
                    paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                    getRewardPointHistory(paramsClone);

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


    public void getRewardPointHistory(final HashMap<String, String> params) {
        if (page == 0) {
            showProgress();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "wbpoint-log", "");

        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                historyArrayList.clear();
                sections.clear();
                if (rewardPointAdapter != null) {
                    rewardPointAdapter.notifyDataSetChanged();
                }
                hasLoadedAllItems = false;
            }
        } else {
            params.put("offset", "0");
            page = 0;
            historyArrayList.clear();
            sections.clear();
            if (rewardPointAdapter != null) {
                rewardPointAdapter.notifyDataSetChanged();
            }
            hasLoadedAllItems = false;
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    if (isAdded() && !isDetached()) {
                        Loading = false;
                        final int offset = Integer.parseInt(params.get("offset"));
                        final ArrayList<ResponseWBHistory> temphistory = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseWBHistory>>() {
                        }.getType());

                        if (temphistory.size() > 0) {
                            historyArrayList.addAll(temphistory);
                            temphistory.clear();
                            page = (int) Math.ceil((double) historyArrayList.size() / LIMIT);
                            if (temphistory.size() % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }
                            for (int i = 0; i < historyArrayList.size(); i++) {
                                String t1 = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, historyArrayList.get(i).getCreated());
                                SimpleSectionedRecyclerViewAdapter.Section tempSection = new SimpleSectionedRecyclerViewAdapter.Section(i, t1);
                                if (!sections.contains(tempSection)) {
                                    sections.add(tempSection);
                                    historyArrayList.get(i).setHeader(true);
                                }
                            }

                            if (historyArrayList.size() <= LIMIT) {
                                rewardPointAdapter.notifyDataSetChanged();
                            } else {
                                try {
                                    mRecyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            rewardPointAdapter.notifyItemRangeChanged(offset, historyArrayList.size());
                                        }
                                    });
                                } catch (Exception e) {
                                    rewardPointAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            hasLoadedAllItems = true;
                            rewardPointAdapter.notifyDataSetChanged();
                        }

                        if (page == 0 && historyArrayList.size() == 0) {
                            ResponseWBHistory emptyLayout = new ResponseWBHistory();
                            emptyLayout.setId("-1");
                            String empty_text = "";
                            if (UserInfo.getInstance(mContext).getCompanyType().equals("seller")) {
                                empty_text = mContext.getResources().getString(R.string.empty_wb_reward_txt_manufacturer);
                            } else if (UserInfo.getInstance(mContext).getCompanyType().equals("buyer")) {
                                empty_text = mContext.getResources().getString(R.string.empty_wb_reward_txt_retailer);
                            } else {
                                empty_text = mContext.getResources().getString(R.string.empty_wb_reward_txt_wholsaler);
                            }
                            emptyLayout.setDisplay_text_log(empty_text);
                            historyArrayList.add(emptyLayout);
                            rewardPointAdapter.notifyDataSetChanged();
                        }
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
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(page * LIMIT));
                getRewardPointHistory(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                getRewardPointHistory(paramsClone);
            }
        }
    }

    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }


}
