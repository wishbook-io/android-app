package com.wishbook.catalog.home.more.myviewers_2;

import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.wishbook.catalog.commonmodels.responses.Model_My_Viewers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_My_Viewers extends GATrackedFragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    @BindView(R.id.recycler_my_viewers)
    RecyclerView recycler_my_viewers;
    @BindView(R.id.empty_text)
    TextView empyt_text;
    Adapter_My_Viewers adapter;

    ArrayList<Model_My_Viewers> data = new ArrayList<>();
    ArrayList<Model_My_Viewers> previousdata;
    long CONTSANT_TIME = 3000;
    HashMap<String, String> paramsClone = null;
    int count;
    int lastFirstVisiblePosition = 0;
    ArrayList<Model_My_Viewers> temp;
    Paginate paginate;
    int page;
    Handler handler;
    Runnable runnable;

    public Fragment_My_Viewers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_my_viewers, ga_container, true);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_my_viewers.setLayoutManager(layoutManager);
        adapter = new Adapter_My_Viewers(getActivity(), data);
        recycler_my_viewers.setAdapter(adapter);

        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(recycler_my_viewers, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        initCall();
        initSwipeRefresh(view);
        return view;
    }


    public void initCall() {
        paramsClone = new HashMap<>();
        paramsClone.put("limit", String.valueOf(LIMIT));
        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
        getMyViewers(paramsClone);
        requestNewdata();
    }


    public void initSwipeRefresh(View view) {

    }

    @Override
    public void onRefresh() {

    }


    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                paramsClone = new HashMap<>();
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                getMyViewers(paramsClone);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                getMyViewers(paramsClone);
            }
        }
    }

    public void getMyViewers(final HashMap<String, String> params) {
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                data.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;
            }
        } else {
            params.put("offset", "0");
            page = 0;
            data.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }
        Log.d("VIEWERS_OFFSET", "" + params.get("offset"));
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "my-viewers-live", ""), params, headers, false, new HttpManager.customCallBack() {
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
                        final int offset = Integer.parseInt(params.get("offset"));
                        Type type = new TypeToken<ArrayList<Model_My_Viewers>>() {
                        }.getType();
                        temp = Application_Singleton.gson.fromJson(response, type);

                        if (temp.size() > 0) {
                            data.addAll(temp);
                            page = (int) Math.ceil((double) data.size() / LIMIT);

                            if (temp.size() % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }
                            if (data.size() <= LIMIT) {
                                adapter.notifyDataSetChanged();
                                RecyclerSectionItemDecoration sectionItemDecoration =
                                        new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                                                true,
                                                new RecyclerSectionItemDecoration.SectionCallback() {
                                                    @Override
                                                    public boolean isSection(int position) {
                                                        try {
                                                            String t1 = "" + getCategory(DateUtils.getTimeAgo(data.get(position).getCreated_at(), getActivity()));
                                                            String t2 = "" + getCategory(DateUtils.getTimeAgo(data.get(position - 1).getCreated_at(), getActivity()));
                                                            return position == 0 || t1.charAt(0) != t2.charAt(0);
                                                        } catch (Exception e) {
                                                            return true;
                                                        }

                                                    }

                                                    @Override
                                                    public CharSequence getSectionHeader(int position) {
                                                        try {
                                                            if (getCategory(DateUtils.getTimeAgo(data.get(position).getCreated_at(), getActivity())).equals("1")) {
                                                                return "Last Week";
                                                            }
                                                            if (getCategory(DateUtils.getTimeAgo(data.get(position).getCreated_at(), getActivity())).equals("2")) {
                                                                return "Last Month";
                                                            }
                                                            if (getCategory(DateUtils.getTimeAgo(data.get(position).getCreated_at(), getActivity())).equals("3")) {
                                                                return "Last Year";
                                                            }
                                                            return getCategory(DateUtils.getTimeAgo(data.get(position).getCreated_at(), getActivity()));
                                                        } catch (Exception e) {
                                                            return "";
                                                        }
                                                    }
                                                });
                                recycler_my_viewers.addItemDecoration(sectionItemDecoration);
                            } else {
                                try {
                                    recycler_my_viewers.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemRangeChanged(offset, data.size() - 1);
                                        }
                                    });

                                } catch (Exception e) {
                                    adapter.notifyDataSetChanged();
                                }
                            }


                        } else {
                            if (data.size() == 0) {
                                empyt_text.setVisibility(View.VISIBLE);
                                hasLoadedAllItems = true;
                                adapter.notifyDataSetChanged();
                            }
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
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }
        super.onDestroy();
    }


    private void requestNewdata() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewRecentData();
            }
        }, CONTSANT_TIME);
    }

    private void getNewRecentData() {
        if (isAdded() && !isDetached()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "my-viewers-live", "") + "?seconds=3", null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        Log.d("RESPONSE", response);
                        hideProgress();

                        if (isAdded() && !isDetached()) {
                            Type type = new TypeToken<ArrayList<Model_My_Viewers>>() {
                            }.getType();
                            ArrayList<Model_My_Viewers> temp = Application_Singleton.gson.fromJson(response, type);
                            if (temp.size() > 0) {
                                for (int i = 0; i < temp.size(); i++) {
                                    try {
                                        data.add(i, temp.get(i));
                                        adapter.notifyItemInserted(i);
                                        recycler_my_viewers.scrollToPosition(0);
                                        adapter.notifyItemChanged(i + 1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            requestNewdata();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                    requestNewdata();
                    hideProgress();
                }
            });
        }
    }

    @Override
    public void onPause() {
        lastFirstVisiblePosition = ((LinearLayoutManager) recycler_my_viewers.getLayoutManager()).findLastVisibleItemPosition();
        Log.d("Pause", String.valueOf(lastFirstVisiblePosition));
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        recycler_my_viewers.post(new Runnable() {
            public void run() {
                recycler_my_viewers.getLayoutManager().scrollToPosition(0);
            }
        });


    }

    public String getCategory(String value) {
        if (value.contains("minutes") || value.contains("hours") || value.contains("hour")
                || value.contains("Just") || value.contains("minute")) {
            return "Recent";
        }
        if (value.contains("Yesterday")) {
            return "Yesterday";
        }
        if (value.contains("days")) {
            String numberOnly = value.replaceAll("[^0-9]", "");
            try {
                int days = Integer.parseInt(numberOnly);
                if (days <= 7) {
                    return "1";
                } else if (days <= 30) {
                    return "2";
                } else {
                    return "3";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }


}
