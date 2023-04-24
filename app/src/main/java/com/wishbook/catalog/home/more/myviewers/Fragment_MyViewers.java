package com.wishbook.catalog.home.more.myviewers;


import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseMyViewers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MyViewers extends GATrackedFragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {

    private View view;

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    HashMap<String, String> paramsClone = null;
    ArrayList<ResponseMyViewers> allViewers = new ArrayList<>();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    MyViewersAdapter adapter;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    public Fragment_MyViewers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_my_viewers_list, ga_container, true);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new MyViewersAdapter(getActivity(), allViewers);
        mRecyclerView.setAdapter(adapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;

        initCall();
        initSwipeRefresh(view);
        return view;
    }


    public void initCall() {
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(LIMIT));
        params.put("offset", String.valueOf(INITIAL_OFFSET));
        getMyViewers(params);
    }

    public void getMyViewers(final HashMap<String, String> params) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());


        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                allViewers.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;

            }
        } else {
            params.put("offset", "0");
            page = 0;
            allViewers.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "my-viewers", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    Loading = false;
                    final int offset = Integer.parseInt(params.get("offset"));
                    Type type = new TypeToken<ArrayList<ResponseMyViewers>>() {
                    }.getType();
                    ArrayList<ResponseMyViewers> temp = Application_Singleton.gson.fromJson(response, type);
                    if (temp.size() > 0) {
                        allViewers.addAll(temp);
                        page = (int) Math.ceil((double) allViewers.size() / LIMIT);
                        if (temp.size() % LIMIT != 0) {
                            hasLoadedAllItems = true;
                        }
                        if (allViewers.size() <= LIMIT) {
                            adapter.notifyDataSetChanged();
                        } else {
                            try {
                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemRangeChanged(offset, allViewers.size() - 1);
                                    }
                                });

                            } catch (Exception e) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        hasLoadedAllItems = true;
                        adapter.notifyDataSetChanged();
                    }
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
                getMyViewers(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                getMyViewers(paramsClone);
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


}
