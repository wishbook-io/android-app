package com.wishbook.catalog.reseller;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wishbook.catalog.commonmodels.KYC_model;
import com.wishbook.catalog.commonmodels.ResponseResellerSattlementDashboard;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseResellerSattelmentHistory;
import com.wishbook.catalog.home.adapters.SimpleSectionedRecyclerViewAdapter;
import com.wishbook.catalog.reseller.adapter.ResellerSattlementHistoryAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Reseller_Payout extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;

    ArrayList<KYC_model> kyc_modelArrayList;

    @BindView(R.id.txt_payout_notified)
    TextView txt_payout_notified;

    @BindView(R.id.txt_received_money)
    TextView txt_received_money;

    @BindView(R.id.txt_due_money)
    TextView txt_due_money;

    @BindView(R.id.txt_total_earn)
    TextView txt_total_earn;


    @BindView(R.id.recyclerViewPayout)
    RecyclerView mRecyclerView;


    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_payout_empty)
    LinearLayout empty_linear;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    String total_available = "0";

    private LinearLayoutManager mLayoutManager;


    public Fragment_Reseller_Payout() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_reseller_payouts, ga_container, true);
        ButterKnife.bind(this, view);
        getBankDetails();
        initView();
        initSwipeRefresh();
        initCall();
        return view;
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initCall() {
        getResellerSattlementHistory();
    }

    private void getBankDetails() {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "bank_details", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        try {
                            hideProgress();
                            Type type = new TypeToken<ArrayList<KYC_model>>() {
                            }.getType();
                            kyc_modelArrayList = Application_Singleton.gson.fromJson(response, type);
                            if (kyc_modelArrayList != null && kyc_modelArrayList.size() > 0) {
                                UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(kyc_modelArrayList));
                                if (kyc_modelArrayList.get(0).getAccount_number().length() < 9) {
                                    txt_payout_notified.setVisibility(View.VISIBLE);
                                    txt_payout_notified.setText("Bank Details not correct");
                                }

                                if (kyc_modelArrayList.get(0).getIfsc_code().length() != 11) {
                                    txt_payout_notified.setVisibility(View.VISIBLE);
                                    txt_payout_notified.setText("Bank Details not correct");
                                }
                            } else {
                                txt_payout_notified.setText("Bank Details not correct");
                            }

                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void getResellerSattlementDashboard() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "reseller-settlement-dashboard", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    ResponseResellerSattlementDashboard dashboard = Application_Singleton.gson.fromJson(response, new TypeToken<ResponseResellerSattlementDashboard>() {
                    }.getType());

                    if (dashboard.getTotal_earned() > 0) {
                        txt_total_earn.setText("\u20B9 " +String.valueOf(dashboard.getTotal_earned()));
                        total_available = String.valueOf(dashboard.getTotal_earned());
                    }

                    if (dashboard.getTotal_received() > 0) {
                        txt_received_money.setText(String.valueOf(dashboard.getTotal_received()));
                    }

                    if (dashboard.getTotal_due() > 0) {
                        txt_due_money.setText( String.valueOf(dashboard.getTotal_due()));
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


    public void getResellerSattlementHistory() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "reseller-settlement", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                getResellerSattlementDashboard();
                try {
                    final ArrayList<ResponseResellerSattelmentHistory> historyArrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseResellerSattelmentHistory>>() {
                    }.getType());
                    if (historyArrayList.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        empty_linear.setVisibility(View.GONE);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.divider_drawable),
                                true, true));
                        final ResellerSattlementHistoryAdapter adapter = new ResellerSattlementHistoryAdapter(getActivity(), historyArrayList);
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
                                SimpleSectionedRecyclerViewAdapter(getActivity(), R.layout.recycler_sticky_header, R.id.list_item_section_text, adapter);
                        mSectionedAdapter.setSections(sections.toArray(dummy));
                        mRecyclerView.setAdapter(mSectionedAdapter);
                        adapter.notifyDataSetChanged();

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


}
