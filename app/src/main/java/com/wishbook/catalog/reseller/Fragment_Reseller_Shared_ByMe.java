package com.wishbook.catalog.reseller;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.details.FilterBottomDialog;
import com.wishbook.catalog.home.orders.BottomOrderDateRange;
import com.wishbook.catalog.reseller.adapter.ResellerSharedProductAdapter;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Reseller_Shared_ByMe extends GATrackedFragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {

    private View view;


    @BindView(R.id.recyclerViewPayout)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;


    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;

    @BindView(R.id.linear_view_type)
    LinearLayout linear_view_type;

    @BindView(R.id.linear_date_range_type)
    LinearLayout linear_date_range_type;

    @BindView(R.id.txt_selected_date_value)
            TextView txt_selected_date_value;

    @BindView(R.id.txt_collection_type_value)
            TextView txt_collection_type_value;


    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;


    // Recyclerview variable start
    ArrayList<Response_catalogMini> shared_catalog_arraylist = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    ResellerSharedProductAdapter mAdapter;
    HashMap<String, String> paramsClone = null;

    String default_from_date, default_to_date;

    boolean isCollectionTypeProduct;
    String date_range_type = null;


    public Fragment_Reseller_Shared_ByMe() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_reseller_shared_by_me, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initSwipeRefresh();
        initCall();
        return view;
    }

    private void initView() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date from_date = cal.getTime();
        default_from_date = formatter.format(from_date);
        default_to_date = formatter.format(today);
        txt_selected_date_value.setText("This month");

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new ResellerSharedProductAdapter(getActivity(), shared_catalog_arraylist);
        mRecyclerView.setAdapter(mAdapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        mAdapter.notifyDataSetChanged();

        linear_view_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterBottom("collection_type");
            }
        });

        linear_date_range_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle= new Bundle();
                bundle.putString("all_label_text","All Products");
                BottomOrderDateRange bottomOrderDateRange = BottomOrderDateRange.newInstance(bundle);
                bottomOrderDateRange.setDismissListener(new BottomOrderDateRange.DismissListener() {
                    @Override
                    public void onDismiss(String from_date, String to_date, String displayText) {
                        if (displayText.contains(";")) {
                            date_range_type = displayText;
                            String[] split = displayText.split(";");
                            if (split.length == 2) {
                                txt_selected_date_value.setText("Start " + split[0] + " - " + "End " + split[1]);
                            }
                        } else {
                            date_range_type = displayText;
                            if(displayText.equalsIgnoreCase("All Order")) {
                                displayText = "All Products";
                            }
                            txt_selected_date_value.setText(displayText);
                        }
                        setDateRange(date_range_type);
                        initCall();
                    }
                });
                bottomOrderDateRange.show(getFragmentManager(), "SelectRange");
            }
        });
    }

    private void initCall() {
        paramsClone = new HashMap<>();
        paramsClone.put("limit", String.valueOf(LIMIT));
        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
        if(isCollectionTypeProduct) {
            paramsClone.put("collection", "false");
        } else {
            paramsClone.put("collection", "true");
        }

        if(default_from_date!=null)
            paramsClone.put("from_date", DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, default_from_date));
        if(default_to_date!=null)
            paramsClone.put("to_date", DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, default_to_date));
        getSharedProducts(paramsClone);
    }

    private void getSharedProducts(final HashMap<String, String> params) {
        try {

            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            if (params.containsKey("offset")) {
                if (params.get("offset").equals("0")) {
                    page = 0;
                    shared_catalog_arraylist.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    hasLoadedAllItems = false;
                }
            } else {
                params.put("offset", "0");
                page = 0;
                shared_catalog_arraylist.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                hasLoadedAllItems = false;
            }
            if(params.containsKey("collection") && params.get("collection").equalsIgnoreCase("false")) {
                txt_collection_type_value.setText("Single Pcs");
            } else {
                txt_collection_type_value.setText("Collection");
            }

            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "product-share", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        try {
                            Type type = new TypeToken<ArrayList<Response_catalogMini>>() {
                            }.getType();
                            int offset = 0;
                            try {
                                offset = Integer.parseInt(params.get("offset"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            final ArrayList<Response_catalogMini> response_catalog = Application_Singleton.gson.fromJson(response, type);
                            Loading = false;
                            if (response_catalog.size() > 0) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                linear_empty.setVisibility(View.GONE);
                                shared_catalog_arraylist.addAll(response_catalog);
                                page = (int) Math.ceil((double) shared_catalog_arraylist.size() / LIMIT);
                                if (response_catalog.size() % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (shared_catalog_arraylist.size() <= LIMIT) {
                                    Log.e("TAG", "onServerResponse: Size <= LIMIT=====>" );
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        final int finalOffset = offset;
                                        mRecyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    mAdapter.notifyItemRangeInserted(finalOffset, response_catalog.size());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                hasLoadedAllItems = true;
                                mAdapter.notifyDataSetChanged();
                                if (page == 0) {
                                    linear_empty.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "onServerResponse: Catch Exception ");
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


    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            paramsClone.put("limit", String.valueOf(LIMIT));
            paramsClone.put("offset", String.valueOf(page * LIMIT));
            getSharedProducts(paramsClone);
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

    private void openFilterBottom(final String filtertype) {
        try {
            FilterBottomDialog filterBottomDialog = null;
            Bundle bundle = new Bundle();
            bundle.putString("type", filtertype);
            if (paramsClone != null) {
                if (filtertype.equalsIgnoreCase("collection_type")) {
                    if (paramsClone.containsKey("collection") && paramsClone.get("collection").equalsIgnoreCase("false"))
                        bundle.putString("previous_selected_tag", Constants.COLLECTION_TYPE_PRODUCT);
                    else
                        bundle.putString("previous_selected_tag", Constants.COLLECTION_TYPE_CAT);
                }

                if (paramsClone.containsKey(filtertype)) {
                    bundle.putString("previous_selected_tag", paramsClone.get(filtertype));
                }
            }
            filterBottomDialog = filterBottomDialog.newInstance(bundle);
            filterBottomDialog.show(getFragmentManager(), filterBottomDialog.getTag());
            filterBottomDialog.setFilterBottomSelectListener(new FilterBottomDialog.FilterBottomSelectListener() {
                @Override
                public void onCheck(String check) {

                    if (check != null) {
                        // Handle Code, isNonCatalog because old code work on this flag
                        if (filtertype.equalsIgnoreCase("collection_type")) {
                            if (check != null && check.equalsIgnoreCase(Constants.COLLECTION_TYPE_PRODUCT)) {
                                isCollectionTypeProduct = true;
                                paramsClone.put("collection","false");
                            } else {
                                isCollectionTypeProduct = false;
                                paramsClone.put("collection","true");
                            }
                        }

                        if (paramsClone != null) {
                            paramsClone.put("limit", String.valueOf(LIMIT));
                            paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                            if (check.isEmpty()) {
                                paramsClone.remove(filtertype);
                            } else {
                                paramsClone.put(filtertype, check);
                            }

                            getSharedProducts(paramsClone);
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            if (check != null && check.equalsIgnoreCase(Constants.COLLECTION_TYPE_PRODUCT)) {
                                paramsClone.put("collection","false");
                            } else {
                                paramsClone.put("collection","true");
                            }
                            paramsClone = params;
                            if (check.isEmpty()) {
                                paramsClone.remove(filtertype);
                            } else {
                                paramsClone.put(filtertype, check);
                            }
                            getSharedProducts(paramsClone);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDateRange(String  date_range_type) {
        if (date_range_type != null) {
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            switch (date_range_type.toLowerCase()) {
                case "all order":
                    default_from_date = null;
                    default_to_date = null;
                    break;
                case "today":
                    default_from_date = formatter.format(today);
                    default_to_date = formatter.format(today);
                    break;
                case "yesterday": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(from_date);
                }
                break;
                case "last 7 days": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                }
                break;
                case "this month": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                }

                break;
                default:
                    if(date_range_type.contains(";")) {
                        String[] split = date_range_type.split(";");
                        if(split.length == 2){
                            default_from_date = split[0];
                            default_to_date = split[1];
                        }
                    }
                    break;

            }
        } else {
            txt_selected_date_value.setText("All");
        }
    }
}
