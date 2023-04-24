package com.wishbook.catalog.home.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseLeads;
import com.wishbook.catalog.home.contacts.adapter.LeadsAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Leads extends GATrackedFragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks {

    private View v;
    @BindView(R.id.recycler_view)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.filter)
    LinearLayout linear_filter;

    @BindView(R.id.search_icon)
    ImageView search_icon;

    @BindView(R.id.img_searchclose)
    ImageView img_searchclose;

    @BindView(R.id.linear_searchview)
    LinearLayout linear_searchview;

    @BindView(R.id.relative_top_bar)
    RelativeLayout relative_top_bar;

    private SearchView searchView;


    private LeadsAdapter mAdapter;
    Spinner spinner;
    private ArrayList<ResponseLeads> responseLeads = new ArrayList<>();

    //Pagination Variable
    public static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page = 0;


    private String searchText = "";
    public String filter;
    HashMap<String, String> paramsClone = null;

    // Swipe Variable
    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;


    public Fragment_Leads() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment_Leads(String filter) {
        this.filter = filter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_leads, ga_container, true);
        ButterKnife.bind(this, v);
        WishbookTracker.sendScreenEvents(getActivity(),WishbookEvent.SELLER_EVENTS_CATEGORY,"Leads_BuyerList_Screen","",null);
        if (filter != null) {
            linear_filter.setVisibility(View.VISIBLE);
        } else {
            linear_filter.setVisibility(View.GONE);
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        mAdapter = new LeadsAdapter(getActivity(), responseLeads ,Fragment_Leads.this);
        recyclerView.setAdapter(mAdapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
      //  initCall();
        initSearch();
        initTopBar();
        initSwipeRefresh(v);

        return v;
    }


    public void initCall() {
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(LIMIT));
        params.put("offset", String.valueOf(INITIAL_OFFSET));
        paramsClone  = params;
        getLeads(params);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
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
                if(linear_searchview.getVisibility() == View.VISIBLE) {
                    searchText = "";
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                     }

                    if(paramsClone!=null){
                        getLeads(paramsClone);
                    } else {

                    }

                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void initSearch() {
        searchView = (SearchView) v.findViewById(R.id.search_view);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
    }

    public void initTopBar() {
        spinner = v.findViewById(R.id.spinner);
        search_icon = v.findViewById(R.id.search_icon);
        img_searchclose = v.findViewById(R.id.img_searchclose);
        linear_searchview = v.findViewById(R.id.linear_searchview);
        relative_top_bar = v.findViewById(R.id.relative_top_bar);


        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relative_top_bar.setVisibility(View.GONE);
                linear_searchview.setVisibility(View.VISIBLE);
                searchView.setFocusable(true);
                searchView.setIconifiedByDefault(false);
                searchView.requestFocus();
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
                searchView.setQuery("", false);

            }
        });
        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
            }
        });

        if (filter != null) {
            if(filter.equals("all"))
                spinner.setSelection(0);
            else if (filter.equals("new")) {
                spinner.setSelection(1);
            } else if (filter.equals("old")) {
                spinner.setSelection(2);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if( i == 0){
                    filter = "all";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    paramsClone  = params;
                    getLeads(paramsClone);
                } else if (i == 1) {
                    filter = "new";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    params.put("status", "created");
                    paramsClone  = params;
                    getLeads(paramsClone);
                } else if (i == 2) {
                    filter = "old";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    params.put("status", "resolved");
                    paramsClone  = params;
                    getLeads(paramsClone);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final Boolean[] canRun = {true};
        searchText = newText.toLowerCase();
        if (newText.length() >= 2) {
            if (canRun[0]) {
                canRun[0] = false;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canRun[0] = true;
                        HashMap<String, String> params = new HashMap<>();
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(INITIAL_OFFSET));
                        if(filter.equals("all")){
                        } else if(filter.equals("new")) {
                            params.put("status","created");
                        } else if(filter.equals("old")){
                            params.put("status","resolved");
                        }
                        params.put("buying_company_name",searchText);
                        if(paramsClone!=null){
                            paramsClone.clear();
                            paramsClone = params;
                            getLeads(paramsClone);
                        } else {
                            paramsClone = params;
                            getLeads(paramsClone);
                        }

                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            if(filter!=null) {
                if(filter.equals("all")){
                } else if(filter.equals("new")) {
                    params.put("status","created");
                } else if(filter.equals("old")){
                    params.put("status","resolved");
                }
            }
            params.put("buying_company_name",searchText);
            if(paramsClone!=null){
                paramsClone.clear();
                paramsClone = params;
                getLeads(paramsClone);
            } else {
                paramsClone = params;
                getLeads(paramsClone);
            }
        }

        return false;
    }


    public void getLeads(final HashMap<String, String> params) {
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                responseLeads.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                hasLoadedAllItems = false;

            }
        } else {
            params.put("offset", "0");
            page = 0;
            responseLeads.clear();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }
        final HashMap<String, String> finalParams = params;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyer-leads", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Loading = false;
                        int offset = 0;
                        try {
                            offset = Integer.parseInt(finalParams.get("offset"));
                            Type type = new TypeToken<ArrayList<ResponseLeads>>() {
                            }.getType();
                            ArrayList<ResponseLeads> temp = Application_Singleton.gson.fromJson(response, type);
                            if (temp.size() > 0) {
                                responseLeads.addAll(temp);
                                page = (int) Math.ceil((double) responseLeads.size() / LIMIT);
                                if (temp.size() % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (responseLeads.size() <= LIMIT) {
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        final int finalOffset = offset;
                                        recyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.notifyItemRangeChanged(finalOffset, responseLeads.size() - 1);
                                            }
                                        });

                                    } catch (Exception e) {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                hasLoadedAllItems = true;
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
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
                getLeads(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
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
