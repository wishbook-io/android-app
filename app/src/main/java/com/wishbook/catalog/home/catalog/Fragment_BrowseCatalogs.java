package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonadapters.SearchSuggestionAdapter;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseSavedFilters;
import com.wishbook.catalog.commonmodels.responses.ResponseSubPredefinedCategory;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.details.SavedFilterBottomDialog;
import com.wishbook.catalog.home.catalog.details.SortBottomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Fragment_BrowseCatalogs extends GATrackedFragment implements
        Paginate.Callbacks, SortBottomDialog.SortBySelectListener, SavedFilterBottomDialog.SavedFilterSelectListener, SwipeRefreshLayout.OnRefreshListener,
        CatalogHolderVersion2.CatalogTypeChangeListener {

    private View v;
    //private RelativeLayout filter;
    private BrowseCatalogsAdapter adapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private TextView filter_image;
    private ProgressDialog progressDialog;
    HashMap<String, String> paramsClone = null;

    ArrayList<Response_catalogMini> allCatalogs = new ArrayList<>();
    ArrayList<Response_catalogMini> allCatalogsFiltered = new ArrayList<>();
    int lastFirstVisiblePosition = 0;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    private boolean isTrusted, isNearMe;
    private boolean isPreCatalog;
    private boolean isReadyCatalog;

    private String filtertype = null;
    private String filtervalue = null;

    private TextView txt_seller_near_me_subtext;
    private MaterialBadgeTextView badge_filter_count, badge_sort_count, badge_saved_count, badge_search_sort_count;

    private LinearLayout linear_sort, linear_search, linear_filter,
            linear_saved_filter, linear_searchview, linear_predefined_filter,
            linear_sub_predefined_filter, linear_sub_root, linear_progress, linear_search_sort;

    private HorizontalScrollView horizontal_subfilter;

    private ImageView img_searchclose;

    private SearchView search_view;

    private RelativeLayout relative_filter;

    private TextView txt_parent_category_name;


    private SwipeRefreshLayout swipe_container;
    private boolean isFilter, isFilterNon, isSearchFilter, isSearchFilterNon;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    private RecyclerView mSuggestionRecyclerView;

    ResponseSavedFilters responseSavedFilters;

    AppCompatButton pre_order_filter, ready_to_dispatch_filter;

    public static String TAG = Fragment_BrowseCatalogs.class.getSimpleName();

    HashMap<String, String> searchHashMap = null;

    boolean isNonCatalog;

    boolean isScreen;

    String layout_type = Constants.PRODUCT_VIEW_GRID;

    String from = "Product Tab";


    public Fragment_BrowseCatalogs() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_catalogs, ga_container, true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        //clear_text = (TextView) v.findViewById(R.id.clear_filter);
        relative_filter = v.findViewById(R.id.relative_filter);
        filter_image = (TextView) v.findViewById(R.id.filter_image);

        linear_sort = v.findViewById(R.id.linear_sort);
        linear_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortBottom();
            }
        });

        linear_search_sort = v.findViewById(R.id.linear_search_sort);


        linear_search = v.findViewById(R.id.linear_search);
        search_view = v.findViewById(R.id.search_view);
        linear_filter = v.findViewById(R.id.linear_filter);

        linear_saved_filter = v.findViewById(R.id.linear_saved_filter);
        linear_saved_filter.setVisibility(View.VISIBLE);

        linear_searchview = v.findViewById(R.id.linear_searchview);
        img_searchclose = v.findViewById(R.id.img_searchclose);
        linear_searchview.setVisibility(View.GONE);

        linear_predefined_filter = v.findViewById(R.id.linear_predefined_filter);
        linear_predefined_filter.setVisibility(View.VISIBLE);

        linear_progress = v.findViewById(R.id.linear_progress);

        linear_sub_predefined_filter = v.findViewById(R.id.linear_sub_predefined_filter);
        horizontal_subfilter = v.findViewById(R.id.horizontal_subfilter);
        linear_sub_root = v.findViewById(R.id.linear_sub_root);
        txt_parent_category_name = v.findViewById(R.id.txt_parent_category_name);

        ready_to_dispatch_filter = v.findViewById(R.id.ready_to_dispatch_filter);
        ready_to_dispatch_filter.setVisibility(View.VISIBLE);
        pre_order_filter = v.findViewById(R.id.pre_order_filter);
        pre_order_filter.setVisibility(View.VISIBLE);

        linear_saved_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSavedFilterBottom();
            }
        });
        linear_search.setVisibility(View.GONE);

        mSuggestionRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerSuggestion);
        mSuggestionRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mSuggestionRecyclerView.setLayoutManager(linearLayoutManager);


        if (getArguments() != null && getArguments().getString("from") != null) {
            from = getArguments().getString("from");
        }

        linear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchView();
            }
        });


        if (getParentFragment() instanceof CatalogHolderVersion2) {
            Log.e("TAG", "onCreateView: Parent Called");
            ((CatalogHolderVersion2) getParentFragment()).setCatalogTypeChangeListener(this);
        } else {
            Log.e("TAG", "onCreateView: Parent Not Called");
        }


        badge_filter_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_filter_count);
        badge_filter_count.setBadgeCount(0, true);

        badge_sort_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_sort_count);
        badge_sort_count.setBadgeCount("", false);

        badge_search_sort_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_search_sort_count);
        badge_search_sort_count.setBadgeCount("", false);

        badge_saved_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_saved_count);
        badge_saved_count.setBadgeCount("", false);

        txt_seller_near_me_subtext = v.findViewById(R.id.txt_seller_near_me_subtext);
        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFilter();
            }
        });


        if (UserInfo.getInstance(getActivity()).getDefaultProductView().equalsIgnoreCase(Constants.PRODUCT_VIEW_LIST)) {
            layout_type = Constants.PRODUCT_VIEW_LIST;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            mRecyclerView.setAdapter(adapter);
        } else {
            layout_type = Constants.PRODUCT_VIEW_GRID;
            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            mRecyclerView.setAdapter(adapter);
        }

        if (paginate != null) {
            paginate.unbind();
        }

        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        adapter.notifyDataSetChanged();

        // focus particular item
        if (getArguments() != null) {
            if (getArguments().getString("focus_position") != null) {
                lastFirstVisiblePosition = Integer.parseInt(getArguments().getString("focus_position"));
            } else {
                lastFirstVisiblePosition = getArguments().getInt(Application_Singleton.adapterFocusPosition);
            }
        }


        if (Application_Singleton.deep_link_filter_non_catalog != null) {
            CatalogHolderVersion2.setCatalog_type_spinner(Constants.CATALOG_TYPE_NON);
        }
        changeCatalog();
        if (UserInfo.getInstance(getActivity()).getDefaultProductView().equalsIgnoreCase(Constants.PRODUCT_VIEW_GRID)) {
            changeViewType(Constants.PRODUCT_VIEW_GRID);
        } else {
            changeViewType(Constants.PRODUCT_VIEW_LIST);
        }

        catalogAvailabilityListner();
        CatalogHolderVersion2.toggleFloating(mRecyclerView, getActivity());
        initSwipeRefresh(v);
        return v;
    }

    public void initCall(boolean isLoadCacheData) {
        gettingFilterIfAny();
        if (Application_Singleton.deep_link_filter != null) {
            Log.e(TAG, "Init Call DeepLink  Catalog");
            paramsClone = Application_Singleton.deep_link_filter;
            if (paramsClone.containsKey("trusted_seller")) {
                if (paramsClone.get("trusted_seller").equals("true")) {
                    updateFilter(true);
                    isTrusted = true;
                }
            }
            if (paramsClone.containsKey("near_me")) {
                if (paramsClone.get("near_me").equals("true")) {
                    updateNearme(true);
                    isNearMe = true;
                }
            }
            if (paramsClone.containsKey("ready_to_ship")) {
                if (paramsClone.get("ready_to_ship").equals("true")) {
                    updateReadyShipFilter(true);
                    isReadyCatalog = true;
                } else {
                    updatePreOrderFilter(true);
                    isPreCatalog = true;
                }
            }
            if (paramsClone.containsKey("most_viewed")) {
                relative_filter.setVisibility(View.GONE);
            }
            if (paramsClone.containsKey("most_ordered")) {
                relative_filter.setVisibility(View.GONE);
            }
            showCatalogs(paramsClone, isLoadCacheData, false);
            setCatalogTypeBoolean("isFilter", true);
            Application_Singleton.deep_link_filter = null;
        } else if (Application_Singleton.deep_link_filter_non_catalog != null) {
            Log.e(TAG, "Init Call DeepLink Non Catalog");
            paramsClone = Application_Singleton.deep_link_filter_non_catalog;
            if (paramsClone.containsKey("trusted_seller")) {
                if (paramsClone.get("trusted_seller").equals("true")) {
                    updateFilter(true);
                    isTrusted = true;
                }
            }
            if (paramsClone.containsKey("near_me")) {
                if (paramsClone.get("near_me").equals("true")) {
                    updateNearme(true);
                    isNearMe = true;
                }
            }

            if (paramsClone.containsKey("ready_to_ship")) {
                if (paramsClone.get("ready_to_ship").equals("true")) {
                    updateReadyShipFilter(true);
                    isReadyCatalog = true;
                } else {
                    updatePreOrderFilter(true);
                    isPreCatalog = true;
                }
            }

            if (paramsClone.containsKey("most_viewed")) {
                relative_filter.setVisibility(View.GONE);
            }
            if (paramsClone.containsKey("most_ordered")) {
                relative_filter.setVisibility(View.GONE);
            }
            showCatalogs(paramsClone, isLoadCacheData, false);
            setCatalogTypeBoolean("isFilter", true);
            Application_Singleton.deep_link_filter_non_catalog = null;
        } else if (isFilter) {
            Log.e("TAG", "initCall:  isFilter");
            updateFilter(isTrusted);
            updatePreOrderFilter(isPreCatalog);
            updateReadyShipFilter(isReadyCatalog);
            showCatalogs(paramsClone, isLoadCacheData, false);
        } else if (isSearchFilter) {
            Log.e(TAG, "Init Call Search Filter");
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "public");
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            params.put("q", search_view.getQuery().toString().trim());
            showCatalogs(params, isLoadCacheData, true);
        } else {
            Log.e(TAG, "Init Call Else");
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "public");
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            params.put("ready_to_ship", "true");

            isReadyCatalog = true;
            updatePreOrderFilter(isPreCatalog);
            updateReadyShipFilter(isReadyCatalog);
            setCatalogTypeBoolean("isFilter", false);
            if (Application_Singleton.isPublicTrusted) {
                params.put("trusted_seller", "true");
                updateFilter(true);
                isTrusted = true;
                Application_Singleton.isPublicTrusted = false;
            }
            showCatalogs(params, isLoadCacheData, false);
        }

    }

    private void toFilter() {
        Intent intent = new Intent(getActivity(), ActivityFilter.class);

        if (CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON)) {
            if (paramsClone != null) {
                if (isReadyCatalog && isPreCatalog) {
                    paramsClone.put("product_availability", "both");
                } else {
                    if (!isReadyCatalog && !isPreCatalog) {
                        paramsClone.put("product_availability", "both");
                    } else {
                        paramsClone.remove("product_availability");
                    }
                }
                intent.putExtra("previousParam", paramsClone);
            }
        } else {
            if (paramsClone != null) {
                if (isReadyCatalog && isPreCatalog) {
                    paramsClone.put("product_availability", "both");
                } else {
                    if (!isReadyCatalog && !isPreCatalog) {
                        paramsClone.put("product_availability", "both");
                    } else {
                        paramsClone.remove("product_availability");
                    }

                }


                intent.putExtra("previousParam", paramsClone);
            }
        }

        intent.putExtra("from_public", true);
        if (isNonCatalog) {
            if (responseSavedFilters != null) {
                intent.putExtra("selected_saved_filter", responseSavedFilters);
            }
        } else {
            if (responseSavedFilters != null) {
                intent.putExtra("selected_saved_filter", responseSavedFilters);
            }
        }


        intent.putExtra("isNonCatalog", isNonCatalog);
        startActivityForResult(intent, 1);
    }




    @Override
    public void onPause() {
        super.onPause();
        lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        Log.d("Position", String.valueOf(lastFirstVisiblePosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
    }

    private void showCatalogs(HashMap<String, String> params, Boolean progress, boolean isSearch) {

        String url = null;
        if (isScreen) {
            url = URLConstants.SCREEN_LIST;
        } else {
            url = URLConstants.companyUrl(getActivity(), "catalogs", "");
        }


        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                Log.d(getClass().getName(), "Visible");
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
                // showProgress();
            } else {
                Log.d(getClass().getName(), "Invisible");
                methodType = HttpManager.METHOD.GET;
            }
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (params == null) {
            params = new HashMap<>();
        }
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                Log.e("TAG", "showCatalogs: Clear");
                page = 0;
                allCatalogs.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;

            }
        } else {
            Log.e("TAG", "showCatalogs: Clear 2");
            params.put("offset", "0");
            page = 0;
            allCatalogs.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }


        //adding supplier company filter if needed
        if (filtertype != null && filtervalue != null) {
            params.put(filtertype, filtervalue);
        }

        if (params.containsKey("near_me")) {
            txt_seller_near_me_subtext.setVisibility(View.VISIBLE);
        } else {
            txt_seller_near_me_subtext.setVisibility(View.GONE);
        }
        int filtercount = 0;
        if (!params.containsKey("view_type")) {
            params.put("view_type", "public");
        }


        if (isNonCatalog) {
            // params.put("catalog_type", Constants.CATALOG_TYPE_NON);
            params.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
        } else {
            // params.put("catalog_type", Constants.CATALOG_TYPE_CAT);
            params.put("product_type", Constants.CATALOG_TYPE_CAT);
        }

        if (!isNonCatalog) {
            if (responseSavedFilters != null) {
                badge_saved_count.setHighLightMode();
            } else {
                badge_saved_count.setBadgeCount(0, true);
            }
        } else {
            if (responseSavedFilters != null) {
                badge_saved_count.setHighLightMode();
            } else {
                badge_saved_count.setBadgeCount(0, true);
            }
        }


        if (isReadyCatalog && isPreCatalog) {
            if (params.containsKey("ready_to_ship")) {
                params.remove("ready_to_ship");
                params.put("product_availability", "both");
            }
        } else if (isReadyCatalog) {
            params.put("ready_to_ship", "true");
        } else if (isPreCatalog) {
            params.put("ready_to_ship", "false");
        }

        if (params.size() > 0) {
            // to ignore for filter count
            filtercount = params.size();
            if (params.containsKey("view_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ctype")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("limit")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("offset")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ordering")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("min_price") && params.containsKey("max_price")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("saved_filter_id")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("catalog_type")) {
                filtercount = filtercount - 1;
            }


            if (params.containsKey("product_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("from")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("product_availability")) {
                if (!isReadyCatalog && !isPreCatalog) {
                    filtercount = filtercount - 1;
                }
            }


            if (params.containsKey("ordering")) {
                if (!params.get("ordering").equals("-id")) {
                    if (linear_searchview.getVisibility() == View.VISIBLE) {
                        badge_search_sort_count.setHighLightMode();
                    } else {
                        badge_sort_count.setHighLightMode();
                    }
                } else {
                    if (linear_searchview.getVisibility() == View.VISIBLE) {
                        badge_search_sort_count.setBadgeCount(0, true);
                    } else {
                        badge_sort_count.setBadgeCount(0, true);
                    }
                }
            } else {
                if (linear_searchview.getVisibility() == View.VISIBLE) {
                    badge_search_sort_count.setBadgeCount(0, true);
                } else {
                    badge_sort_count.setBadgeCount(0, true);
                }
            }
        }

        final HashMap<String, String> finalParams = removeClientKey(params);
        sendFilterResultAnalytics(params);
        badge_filter_count.setBadgeCount(filtercount, true);


        if (isAdded() && !isDetached()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(methodType, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            int offset = 0;
                            try {
                                offset = Integer.parseInt(finalParams.get("offset"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Loading = false;
                            Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                            if (response_catalog.length > 0) {
                                //checking if data updated on 2nd page or more
                                allCatalogs = (ArrayList<Response_catalogMini>) HttpManager.removeDuplicateIssue(offset, allCatalogs, dataUpdated, LIMIT);
                                for (int i = 0; i < response_catalog.length; i++) {
                                    allCatalogs.add(response_catalog[i]);
                                }

                                page = (int) Math.ceil((double) allCatalogs.size() / LIMIT);

                                if (response_catalog.length % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (allCatalogs.size() <= LIMIT) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        final int finalOffset = offset;
                                        mRecyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyItemRangeChanged(finalOffset, allCatalogs.size() - 1);
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("TAG", "onServerResponse: Notify DataSet Changes" + adapter.getItemCount());
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                hasLoadedAllItems = true;
                                adapter.notifyDataSetChanged();
                            }

                            if (adapter.getItemCount() <= LIMIT) {
                                try {
                                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                                } catch (Exception e) {
                                    e.printStackTrace();
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

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            CatalogHolderVersion2.setCatalog_type_spinner(Constants.CATALOG_TYPE_CAT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                    if (bundle.getSerializable("selected_saved_filter") != null) {
                        if (isNonCatalog) {
                            responseSavedFilters = (ResponseSavedFilters) bundle.getSerializable("selected_saved_filter");
                        } else {
                            responseSavedFilters = (ResponseSavedFilters) bundle.getSerializable("selected_saved_filter");
                        }
                    } else {

                        if (isNonCatalog) {
                            responseSavedFilters = null;
                        } else {
                            responseSavedFilters = null;
                        }

                    }
                    boolean isNonCatalog = CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON);
                    from = "Filter";
                    if (params != null) {
                        if (params.size() > 0) {
                            if (selectCloneHashMap() != null) {
                                if (selectCloneHashMap().containsKey("ordering")) {
                                    params.put("ordering", selectCloneHashMap().get("ordering"));
                                }
                            }
                            if (selectCloneHashMap() != null) {
                                selectCloneHashMap().clear();
                            }
                            if (params.containsKey("trusted_seller")) {
                                setCatalogTypeBoolean("isTrusted", true);
                                updateFilter(true);
                            } else {
                                setCatalogTypeBoolean("isTrusted", false);
                                updateFilter(false);
                            }

                            if (params.containsKey("product_availability") && params.get("product_availability").equalsIgnoreCase("both")) {
                                setCatalogTypeBoolean("isPreorder", true);
                                setCatalogTypeBoolean("isReadyCatalog", true);
                                updatePreOrderFilter(true);
                                updateReadyShipFilter(true);
                            } else {
                                if (params.containsKey("ready_to_ship")) {
                                    setCatalogTypeBoolean("isPreorder", params.get("ready_to_ship").equals("true") ? false : true);
                                    setCatalogTypeBoolean("isReadyCatalog", params.get("ready_to_ship").equals("true") ? true : false);
                                    updatePreOrderFilter(params.get("ready_to_ship").equals("true") ? false : true);
                                    updateReadyShipFilter(params.get("ready_to_ship").equals("true") ? true : false);
                                } else {
                                    setCatalogTypeBoolean("isPreorder", false);
                                    setCatalogTypeBoolean("isReadyCatalog", false);
                                    updatePreOrderFilter(false);
                                    updateReadyShipFilter(false);
                                }
                            }


                            if (!isNonCatalog ? isTrusted : isTrusted) {
                                params.put("trusted_seller", "true");
                            }

                            if (!isNonCatalog ? isNearMe : isNearMe) {
                                params.put("near_me", "true");
                            }

                            if (!isNonCatalog ? isReadyCatalog : isReadyCatalog) {
                                params.put("ready_to_ship", "true");
                            }

                            if (!isNonCatalog ? isPreCatalog : isPreCatalog) {
                                params.put("ready_to_ship", "false");
                            }


                            if (params.containsKey("near_me")) {
                                setCatalogTypeBoolean("isNearMe", true);
                                updateNearme(true);
                            }

                            params.put("view_type", "public");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));

                            if (!isNonCatalog)
                                paramsClone = params;
                            else
                                paramsClone = params;
                            setCatalogTypeBoolean("isFilter", true);
                            showCatalogs(params, true, false);
                            getMainPredefinedFilter(getActivity());
                            /*if (isNonCatalog)
                                getMainPredefinedFilter(getActivity());
                            else
                                getMainPredefinedFilter1(getActivity());*/

                        } else {
                            if (!isNonCatalog) {
                                if (paramsClone != null) {
                                    paramsClone.clear();
                                    paramsClone = null;
                                }
                            } else {
                                if (paramsClone != null) {
                                    paramsClone.clear();
                                    paramsClone = null;
                                }
                            }

                            allCatalogs.clear();
                            setCatalogTypeBoolean("isTrusted", false);
                            params.put("view_type", "public");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogs(params, true, false);
                            updateFilter(false);
                            getMainPredefinedFilter(getActivity());
                        /*    if (isNonCatalog)
                                getMainPredefinedFilter(getActivity());
                            else
                                getMainPredefinedFilter1(getActivity());*/
                            linear_sub_root.setVisibility(View.GONE);

                        }
                    }
                } else if (resultCode == 9000) {
                    // Delete Saved Filter Apply and open Again Filter
                    HashMap<String, String> params = new HashMap<>();
                    if (isNonCatalog) {
                        responseSavedFilters = null;
                    } else {
                        responseSavedFilters = null;
                    }
                    if (paramsClone != null) {
                        paramsClone.clear();
                        paramsClone = null;
                    }

                    allCatalogs.clear();
                    isTrusted = false;
                    toFilter();
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    showCatalogs(params, true, false);
                    getMainPredefinedFilter(getActivity());
                 /*   if (isNonCatalog)
                        getMainPredefinedFilter(getActivity());
                    else
                        getMainPredefinedFilter1(getActivity());*/
                    updateFilter(false);
                } else if (resultCode == 8000) {
                    // Delete Saved Filter Apply
                    HashMap<String, String> params = new HashMap<>();
                    if (isNonCatalog) {
                        responseSavedFilters = null;
                    } else {
                        responseSavedFilters = null;
                    }

                    if (paramsClone != null) {
                        paramsClone.clear();
                        paramsClone = null;
                    }
                    allCatalogs.clear();
                    isTrusted = false;
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    showCatalogs(params, true, false);
                    getMainPredefinedFilter(getActivity());
                   /* if (isNonCatalog)
                        getMainPredefinedFilter(getActivity());
                    else
                        getMainPredefinedFilter1(getActivity());*/
                    updateFilter(false);

                }


                break;
        }
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (linear_searchview.getVisibility() == View.VISIBLE) {
                if (searchHashMap == null) {

                } else {
                    searchHashMap.put("limit", String.valueOf(LIMIT));
                    searchHashMap.put("offset", String.valueOf(page * LIMIT));
                    showCatalogs(searchHashMap, false, true);
                }
            } else {
                if (isNonCatalog) {
                    if (paramsClone == null) {
                        HashMap<String, String> params = new HashMap<>();
                        if (isTrusted) {
                            params.put("trusted_seller", "true");
                        }
                        if (isNearMe) {
                            params.put("near_me", "true");
                        }
                        if (isPreCatalog) {
                            params.put("ready_to_ship", "false");
                        }
                        if (isReadyCatalog) {
                            params.put("ready_to_ship", "true");
                        }
                        params.put("view_type", "public");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(params, false, false);
                    } else {
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(selectCloneHashMap(), false, false);
                    }
                } else {
                    if (paramsClone == null) {
                        HashMap<String, String> params = new HashMap<>();
                        if (isTrusted) {
                            params.put("trusted_seller", "true");
                        }
                        if (isNearMe) {
                            params.put("near_me", "true");
                        }
                        if (isPreCatalog) {
                            params.put("ready_to_ship", "false");
                        }
                        if (isReadyCatalog) {
                            params.put("ready_to_ship", "true");
                        }
                        params.put("view_type", "public");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(params, false, false);
                    } else {
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(paramsClone, false, false);
                    }
                }

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

    private void gettingFilterIfAny() {
        if (getArguments() != null) {
            filtertype = getArguments().getString("filtertype", null);
            filtervalue = getArguments().getString("filtervalue", null);
        }
    }

    private void updateFilter(boolean isFilter) {
       /* if (isFilter) {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            trusted_filter.setTextColor(Color.WHITE);
        } else {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            trusted_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }*/
    }

    private void updatePreOrderFilter(boolean isFilter) {
        setCatalogTypeBoolean("isPreorder", isFilter);
        setCatalogTypeBoolean("isFilter", isFilter);
        if (isFilter) {
            pre_order_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            pre_order_filter.setTextColor(Color.WHITE);
        } else {
            pre_order_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            pre_order_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }
    }

    private void updateReadyShipFilter(boolean isFilter) {
        setCatalogTypeBoolean("isReadyCatalog", isFilter);
        setCatalogTypeBoolean("isFilter", true);
        if (isFilter) {
            ready_to_dispatch_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            ready_to_dispatch_filter.setTextColor(Color.WHITE);
        } else {
            ready_to_dispatch_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            ready_to_dispatch_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }
    }

    private void catalogAvailabilityListner() {

        pre_order_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean reverseFlag;
                if (isPreCatalog) {
                    reverseFlag = false;
                } else {
                    reverseFlag = true;
                }

                updatePreOrderFilter(reverseFlag);
                HashMap<String, String> params = new HashMap<>();
                if (selectCloneHashMap() != null) {
                    params = selectCloneHashMap();
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (!reverseFlag)
                        params.remove("ready_to_ship");
                    else
                        params.put("ready_to_ship", String.valueOf(!reverseFlag));

                    if (isNonCatalog)
                        paramsClone = params;
                    else
                        paramsClone = params;
                    Application_Singleton.trackEvent("Public", "Pre-Order Filter", "Pre-Order Filter");
                } else {
                    if (!reverseFlag)
                        params.remove("ready_to_ship");
                    else
                        params.put("ready_to_ship", String.valueOf(!reverseFlag));

                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (isNonCatalog)
                        paramsClone = params;
                    else
                        paramsClone = params;
                }
                showCatalogs(params, false, false);
            }
        });


        ready_to_dispatch_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean reverseFlag;
                if (isReadyCatalog) {
                    reverseFlag = false;
                } else {
                    reverseFlag = true;
                }
                updateReadyShipFilter(reverseFlag);
                HashMap<String, String> params = new HashMap<>();
                if (selectCloneHashMap() != null) {
                    params = selectCloneHashMap();
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (!reverseFlag)
                        params.remove("ready_to_ship");
                    else
                        params.put("ready_to_ship", String.valueOf(reverseFlag));

                    if (isNonCatalog)
                        paramsClone = params;
                    else
                        paramsClone = params;
                    Application_Singleton.trackEvent("Public", "Ready to ship Filter", "Ready to ship Filter");
                } else {
                    if (!reverseFlag)
                        params.remove("ready_to_ship");
                    else
                        params.put("ready_to_ship", String.valueOf(reverseFlag));

                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (isNonCatalog)
                        paramsClone = params;
                    else
                        paramsClone = params;
                }
                showCatalogs(params, false, false);
            }
        });


    }

    private void updateCategoryFilter(boolean isFilter, AppCompatButton button) {
        if (isFilter) {
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            button.setTextColor(getResources().getColor(R.color.color_primary));
        }
    }

    private void updateSubCategoryFilter(boolean isFilter, AppCompatButton button) {
        if (isFilter) {
            button.setTextColor(getResources().getColor(R.color.color_primary));
        } else {
            button.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }
    }

    private void updateNearme(boolean isFilter) {
     /*   if (isFilter) {
            nearme_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            nearme_filter.setTextColor(Color.WHITE);
        } else {
            nearme_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            nearme_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }*/
    }

    private void openSortBottom() {
        SortBottomDialog sortBottomDialog = null;
        boolean isNonCatalog = CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON);
        if (isNonCatalog) {
            if (paramsClone != null) {
                if (paramsClone.containsKey("ordering")) {
                    sortBottomDialog = sortBottomDialog.newInstance(paramsClone.get("ordering"));
                } else {
                    sortBottomDialog = SortBottomDialog.newInstance(null);
                }
            } else {
                sortBottomDialog = SortBottomDialog.newInstance(null);
            }
        } else {
            if (paramsClone != null) {
                if (paramsClone.containsKey("ordering")) {
                    sortBottomDialog = sortBottomDialog.newInstance(paramsClone.get("ordering"));
                } else {
                    sortBottomDialog = SortBottomDialog.newInstance(null);
                }
            } else {
                sortBottomDialog = SortBottomDialog.newInstance(null);
            }
        }
        sortBottomDialog.show(getFragmentManager(), sortBottomDialog.getTag());
        sortBottomDialog.setSortBySelectListener(Fragment_BrowseCatalogs.this);
    }

    private void openSearchSortBottom(HashMap<String, String> searchHashMap) {
        SortBottomDialog sortBottomDialog = null;
        if (searchHashMap != null) {
            if (searchHashMap.containsKey("ordering")) {
                sortBottomDialog = sortBottomDialog.newInstance(searchHashMap.get("ordering"));
            } else {
                sortBottomDialog = SortBottomDialog.newInstance(null);
            }
        } else {
            sortBottomDialog = SortBottomDialog.newInstance(null);
        }
        sortBottomDialog.show(getFragmentManager(), sortBottomDialog.getTag());
        sortBottomDialog.setSortBySelectListener(Fragment_BrowseCatalogs.this);
    }


    private void openSavedFilterBottom() {
        SavedFilterBottomDialog savedFilterBottomDialog = null;
        if (isNonCatalog) {
            if (responseSavedFilters != null) {
                savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(responseSavedFilters.getId(), isNonCatalog);

            } else {
                savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(null, isNonCatalog);
            }
        } else {
            if (responseSavedFilters != null) {
                savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(responseSavedFilters.getId(), isNonCatalog);

            } else {
                savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(null, isNonCatalog);
            }
        }

        savedFilterBottomDialog.setTargetFragment(this, 1);
        savedFilterBottomDialog.show(getFragmentManager(), savedFilterBottomDialog.getTag());
        savedFilterBottomDialog.setSavedFilterSelectListener(Fragment_BrowseCatalogs.this);
    }

    private void openSearchView() {
        linear_filter.setVisibility(View.GONE);
        horizontal_subfilter.setVisibility(View.GONE);
        search_view.setVisibility(View.VISIBLE);
        linear_searchview.setVisibility(View.VISIBLE);
        linear_sub_root.setVisibility(View.GONE);
        linear_progress.setVisibility(View.GONE);
        // search_view.setIconifiedByDefault(false);
        search_view.setFocusable(true);
        search_view.setIconified(false);
        search_view.requestFocusFromTouch();

        badge_search_sort_count.setBadgeCount(0, true);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) search_view.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        linear_search_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchHashMap != null) {
                    openSearchSortBottom(searchHashMap);
                } else {
                    Toast.makeText(getActivity(), "search can't be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        KeyboardUtils.showKeyboard(search_view, getActivity());
/*
        TextView searchText = (TextView) search_view.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if(searchText!=null){
            searchText.setFilters(new InputFilter[] {
                    new SpecialCharacterInputFilter()
            });
        }*/

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                if (searchHashMap != null && searchHashMap.containsKey("ordering")) {
                    hashMap.put("ordering", searchHashMap.get("ordering"));
                }
                if (paramsClone != null) {
                    paramsClone.clear();
                    paramsClone = null;
                }
                allCatalogs.clear();
                if (isNonCatalog) {
                    responseSavedFilters = null;
                } else {
                    responseSavedFilters = null;
                }
                responseSavedFilters = null;
                isTrusted = false;
                isFilter = false;
                isSearchFilter = true;
                hashMap.put("view_type", "public");
                hashMap.put("limit", String.valueOf(LIMIT));
                hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
                hashMap.put("q", search_view.getQuery().toString().trim());
                searchHashMap = hashMap;
                showCatalogs(searchHashMap, true, true);
                mSuggestionRecyclerView.setVisibility(View.GONE);
                search_view.clearFocus();
                if (search_view.getQuery() != null)
                    Application_Singleton.trackEvent("Public Search", "Search", search_view.getQuery().toString().trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (search_view.hasFocus()) {
                    if (!newText.isEmpty()) {
                        String[] wordsList = newText.split(" ");
                        if (wordsList.length > 0) {
                            //getSearchSuggestion(getActivity(), wordsList[wordsList.length - 1]);
                            getSearchSuggestion(getActivity(), newText);
                        }
                    } else {
                        mSuggestionRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    mSuggestionRecyclerView.setVisibility(View.GONE);

                }

                return false;
            }
        });

        search_view.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    linear_search_sort.setVisibility(View.GONE);
                } else {
                    linear_search_sort.setVisibility(View.VISIBLE);
                }
            }
        });
        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_view.setQuery("", false);
                search_view.clearFocus();
                KeyboardUtils.hideKeyboard(getActivity());
                linear_searchview.setVisibility(View.GONE);
                linear_filter.setVisibility(View.VISIBLE);
                mSuggestionRecyclerView.setVisibility(View.GONE);
                search_view.setVisibility(View.GONE);
                if (searchHashMap != null) {
                    searchHashMap.clear();
                }
                isFilter = false;
                isSearchFilter = false;
                // show Public Catalog after search clear
                HashMap<String, String> params = new HashMap<>();
                params.put("view_type", "public");
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(INITIAL_OFFSET));
                updateFilter(false);
                updateNearme(false);
                showCatalogs(params, true, false);
                getMainPredefinedFilter(getActivity());
            /*    if (isNonCatalog)
                    getMainPredefinedFilter(getActivity());
                else
                    getMainPredefinedFilter1(getActivity());*/
            }
        });


    }

    @Override
    public void onCheck(String check) {
        if (check != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("ordering", check);
            if (linear_searchview.getVisibility() == View.GONE) {
                if (selectCloneHashMap() != null) {
                    selectCloneHashMap().put("offset", String.valueOf(INITIAL_OFFSET));
                    selectCloneHashMap().putAll(params);
                } else {
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (isNonCatalog)
                        paramsClone = params;
                    else
                        paramsClone = params;
                }
                setCatalogTypeBoolean("isFilter", true);
                showCatalogs(selectCloneHashMap(), true, false);
            } else {
                // To Do Task
                if (!search_view.getQuery().toString().trim().isEmpty()) {
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    params.put("q", search_view.getQuery().toString().trim());
                    searchHashMap = params;
                    showCatalogs(params, true, true);
                } else {
                    Toast.makeText(getActivity(), "Search can't be empty", Toast.LENGTH_SHORT).show();
                }

            }

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
                swipe_container.setRefreshing(false);
                initCall(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


    @Override
    public void onSavedChecked(String check) {

    }


    private void getSearchSuggestion(final Activity activity, final String query) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs_suggestion", "") + "?q=" + query, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                String[] suggestionString = Application_Singleton.gson.fromJson(response, String[].class);
                if (suggestionString.length > 0) {
                    if (!search_view.getQuery().toString().isEmpty()) {
                        mSuggestionRecyclerView.setVisibility(View.VISIBLE);
                        final ArrayList<String> strings = new ArrayList<String>(Arrays.asList(suggestionString));
                        SearchSuggestionAdapter adapter = new SearchSuggestionAdapter(getActivity(), strings,false);
                        mSuggestionRecyclerView.setAdapter(adapter);
                        adapter.setSearchItemClick(new SearchSuggestionAdapter.AdapterNotifyListener() {
                            @Override
                            public void itemClick(int position) {
                                if (!search_view.getQuery().toString().isEmpty()) {
                                    String[] wordsList = search_view.getQuery().toString().split(" ");
                                    search_view.clearFocus();
                                    try {
                                        //search_view.setQuery(search_view.getQuery().toString().replace(wordsList[wordsList.length - 1], strings.get(position) + " "), true);
                                        search_view.setQuery(strings.get(position), true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mSuggestionRecyclerView.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } else {
                    mSuggestionRecyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    private void getMainPredefinedFilter(final Activity activity) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, URLConstants.PREDEFINED_FILTER_URL, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    CategoryTree[] categoryTrees = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, StaticFunctions.dpToPx(getActivity(), 40));
                    if (categoryTrees.length > 0) {
                        linear_predefined_filter.removeAllViews();
                        final AppCompatButton[] appCompatButton = new AppCompatButton[categoryTrees.length];

                        for (int i = 0; i < categoryTrees.length; i++) {
                            appCompatButton[i] = new AppCompatButton(getActivity());
                            appCompatButton[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
                            appCompatButton[i].setText(categoryTrees[i].getcategory_name());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                appCompatButton[i].setStateListAnimator(null);
                            }
                            appCompatButton[i].setSupportAllCaps(false);
                            appCompatButton[i].setTextColor(getResources().getColor(R.color.color_primary));
                            appCompatButton[i].setGravity(Gravity.CENTER);
                            lp.setMargins(StaticFunctions.dpToPx(activity, 10), 0, 0, 0);
                            appCompatButton[i].setLayoutParams(lp);
                            appCompatButton[i].setSelected(false);


                            if (selectCloneHashMap() != null && selectCloneHashMap().containsKey("category")) {
                                if (categoryTrees[i].getId().toString().equals(selectCloneHashMap().get("category"))) {
                                    Log.d("MainFilter", "select main filter + " + categoryTrees[i].getcategory_name());
                                    appCompatButton[i].setSelected(true);
                                    //horizontal_subfilter.setVisibility(View.VISIBLE);
                                    //linear_sub_root.setVisibility(View.VISIBLE);
                                    updateCategoryFilter(true, appCompatButton[i]);
                                } else {
                                    horizontal_subfilter.setVisibility(View.GONE);
                                    linear_sub_root.setVisibility(View.GONE);
                                }
                            } else {
                                horizontal_subfilter.setVisibility(View.GONE);
                                linear_sub_root.setVisibility(View.GONE);
                            }

                            appCompatButton[i].setTag(categoryTrees[i].getId());
                            final int finalI = i;
                            appCompatButton[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (appCompatButton[finalI].isSelected()) {
                                        appCompatButton[finalI].setSelected(false);
                                        updateCategoryFilter(false, appCompatButton[finalI]);
                                        horizontal_subfilter.setVisibility(View.GONE);
                                        linear_sub_root.setVisibility(View.GONE);
                                        if (paramsClone != null) {
                                            if (paramsClone.containsKey("category")) {
                                                paramsClone.remove("category");
                                            }
                                        }
                                        setCatalogTypeBoolean("isFilter", false);
                                        initCall(true);
                                    } else {
                                        for (int j = 0; j < appCompatButton.length; j++) {
                                            appCompatButton[j].setSelected(false);
                                            updateCategoryFilter(false, appCompatButton[j]);
                                        }
                                        Application_Singleton.trackEvent("Predefined Main Filter", "Click", appCompatButton[finalI].getText().toString());
                                        appCompatButton[finalI].setSelected(true);
                                        setCatalogTypeBoolean("isFilter", true);
                                        updateCategoryFilter(true, appCompatButton[finalI]);
                                        getSubPredefinedFilter(activity, appCompatButton[finalI].getTag().toString(), appCompatButton[finalI].getText().toString());
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("view_type", "public");
                                        hashMap.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                        hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
                                        hashMap.put("category", appCompatButton[finalI].getTag().toString());
                                        if (selectCloneHashMap() != null) {
                                            if (selectCloneHashMap().containsKey("trusted_seller")) {
                                                hashMap.put("trusted_seller", "true");
                                            }
                                            if (selectCloneHashMap().containsKey("near_me")) {
                                                hashMap.put("near_me", "true");
                                            }
                                        }
                                        if (hashMap.containsKey("trusted_seller")) {
                                            if (hashMap.get("trusted_seller").equals("true")) {
                                                updateFilter(true);
                                                setCatalogTypeBoolean("isTrusted", true);
                                            }
                                        }
                                        if (hashMap.containsKey("near_me")) {
                                            if (hashMap.get("near_me").equals("true")) {
                                                updateNearme(true);
                                                setCatalogTypeBoolean("isNearMe", true);
                                            }
                                        }


                                        if (selectCloneHashMap() != null) {
                                            selectCloneHashMap().clear();
                                            selectCloneHashMap().putAll(hashMap);
                                        } else {
                                            if (isNonCatalog) {
                                                paramsClone = hashMap;
                                            } else {
                                                paramsClone = hashMap;
                                            }
                                        }

                                        showCatalogs(selectCloneHashMap(), false, false);
                                    }

                                }
                            });
                            linear_predefined_filter.addView(appCompatButton[i]);
                        }


                    } else {
                        linear_predefined_filter.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getSubPredefinedFilter(final Activity activity, String catagoryId, final String categoryName) {
        Log.e(TAG, "getSubPredefinedFilter: ===>" + catagoryId);
        linear_progress.setVisibility(View.VISIBLE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PREDEFINED_SUB_FILTER_URL + "?category=" + catagoryId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    linear_progress.setVisibility(View.GONE);
                    final ResponseSubPredefinedCategory[] subFilter = Application_Singleton.gson.fromJson(response, ResponseSubPredefinedCategory[].class);
                    Log.e(TAG, "Response ==getSubPredefinedFilter: ===>" + response);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (subFilter.length > 0) {
                        linear_sub_root.setVisibility(View.VISIBLE);
                        horizontal_subfilter.setVisibility(View.VISIBLE);
                        txt_parent_category_name.setText(categoryName);
                        linear_sub_predefined_filter.removeAllViews();
                        final AppCompatButton[] appCompatButtons = new AppCompatButton[subFilter.length];
                        for (int i = 0; i < subFilter.length; i++) {
                            appCompatButtons[i] = new AppCompatButton(getActivity());
                            appCompatButtons[i].setBackgroundDrawable(null);
                            appCompatButtons[i].setText(subFilter[i].getName());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                appCompatButtons[i].setStateListAnimator(null);
                            }
                            appCompatButtons[i].setSupportAllCaps(false);
                            appCompatButtons[i].setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                            appCompatButtons[i].setGravity(Gravity.CENTER);
                            lp.setMargins(StaticFunctions.dpToPx(activity, 10), 0, 0, 0);
                            appCompatButtons[i].setSelected(false);
                            appCompatButtons[i].setTag(subFilter[i].getUrl());
                            appCompatButtons[i].setLayoutParams(lp);
                            final int finalI = i;
                            appCompatButtons[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (appCompatButtons[finalI].isSelected()) {
                                        appCompatButtons[finalI].setSelected(false);
                                        updateSubCategoryFilter(false, appCompatButtons[finalI]);
                                        initCall(false);
                                        setCatalogTypeBoolean("isFilter", false);
                                        initCall(true);
                                    } else {
                                        for (int j = 0; j < appCompatButtons.length; j++) {
                                            appCompatButtons[j].setSelected(false);
                                            updateSubCategoryFilter(false, appCompatButtons[j]);
                                        }
                                        setCatalogTypeBoolean("isFilter", true);
                                        appCompatButtons[finalI].setSelected(true);
                                        Application_Singleton.trackEvent("Predefined Sub Filter", "Click", appCompatButtons[finalI].getText().toString());
                                        updateSubCategoryFilter(true, appCompatButtons[finalI]);
                                        Uri intentUri = Uri.parse(appCompatButtons[finalI].getTag().toString());
                                        HashMap<String, String> hashMap = SplashScreen.getQueryString(intentUri);
                                        hashMap.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                        hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
                                        hashMap.put("view_type", "public");
                                        if (selectCloneHashMap() != null) {
                                            if (selectCloneHashMap().containsKey("trusted_seller")) {
                                                hashMap.put("trusted_seller", "true");
                                            }
                                            if (paramsClone.containsKey("near_me")) {
                                                hashMap.put("near_me", "true");
                                            }
                                        }
                                        if (hashMap.containsKey("trusted_seller")) {
                                            if (hashMap.get("trusted_seller").equals("true")) {
                                                updateFilter(true);
                                                setCatalogTypeBoolean("isTrusted", true);
                                            }
                                        }
                                        if (hashMap.containsKey("near_me")) {
                                            if (hashMap.get("near_me").equals("true")) {
                                                updateNearme(true);
                                                setCatalogTypeBoolean("isNearMe", true);
                                            }
                                        }

                                        if (selectCloneHashMap() != null) {
                                            selectCloneHashMap().clear();
                                            selectCloneHashMap().putAll(hashMap);
                                        } else {
                                            if (isNonCatalog) {
                                                paramsClone = hashMap;
                                            } else {
                                                paramsClone = hashMap;
                                            }
                                        }
                                       /* allCatalogs.clear();
                                        adapter.notifyDataSetChanged();*/
                                        showCatalogs(selectCloneHashMap(), false, false);
                                    }

                                }
                            });
                            linear_sub_predefined_filter.addView(appCompatButtons[i]);
                        }


                    } else {
                        linear_sub_root.setVisibility(View.VISIBLE);
                        horizontal_subfilter.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                linear_progress.setVisibility(View.GONE);

            }
        });
    }


    private void getMainPredefinedFilter1(final Activity activity) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, URLConstants.PREDEFINED_FILTER_URL, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    CategoryTree[] categoryTrees = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, StaticFunctions.dpToPx(getActivity(), 40));
                    if (categoryTrees.length > 0) {
                        linear_predefined_filter.removeAllViews();
                        final AppCompatButton[] appCompatButton = new AppCompatButton[categoryTrees.length];

                        for (int i = 0; i < categoryTrees.length; i++) {
                            appCompatButton[i] = new AppCompatButton(getActivity());
                            appCompatButton[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
                            appCompatButton[i].setText(categoryTrees[i].getcategory_name());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                appCompatButton[i].setStateListAnimator(null);
                            }
                            appCompatButton[i].setSupportAllCaps(false);
                            appCompatButton[i].setTextColor(getResources().getColor(R.color.color_primary));
                            appCompatButton[i].setGravity(Gravity.CENTER);
                            lp.setMargins(StaticFunctions.dpToPx(activity, 10), 0, 0, 0);
                            appCompatButton[i].setLayoutParams(lp);
                            appCompatButton[i].setSelected(false);


                            if (selectCloneHashMap() != null && selectCloneHashMap().containsKey("category")) {
                                if (categoryTrees[i].getId().toString().equals(selectCloneHashMap().get("category"))) {
                                    Log.d("MainFilter", "select main filter Non Catalog + " + categoryTrees[i].getcategory_name());
                                    appCompatButton[i].setSelected(true);
                                    updateCategoryFilter(true, appCompatButton[i]);
                                } else {
                                    horizontal_subfilter.setVisibility(View.GONE);
                                    linear_sub_root.setVisibility(View.GONE);
                                }
                            } else {
                                horizontal_subfilter.setVisibility(View.GONE);
                                linear_sub_root.setVisibility(View.GONE);
                            }

                            appCompatButton[i].setTag(categoryTrees[i].getId());
                            final int finalI = i;
                            appCompatButton[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (appCompatButton[finalI].isSelected()) {
                                        appCompatButton[finalI].setSelected(false);
                                        updateCategoryFilter(false, appCompatButton[finalI]);
                                        horizontal_subfilter.setVisibility(View.GONE);
                                        linear_sub_root.setVisibility(View.GONE);
                                        setCatalogTypeBoolean("isFilter", false);
                                        initCall(true);
                                    } else {
                                        for (int j = 0; j < appCompatButton.length; j++) {
                                            appCompatButton[j].setSelected(false);
                                            updateCategoryFilter(false, appCompatButton[j]);
                                        }
                                        Application_Singleton.trackEvent("Predefined Main Filter", "Click", appCompatButton[finalI].getText().toString());
                                        appCompatButton[finalI].setSelected(true);
                                        setCatalogTypeBoolean("isFilter", true);
                                        updateCategoryFilter(true, appCompatButton[finalI]);
                                        getSubPredefinedFilter1(activity, appCompatButton[finalI].getTag().toString(), appCompatButton[finalI].getText().toString());
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("view_type", "public");
                                        hashMap.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                        hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
                                        hashMap.put("category", appCompatButton[finalI].getTag().toString());
                                        if (selectCloneHashMap() != null) {
                                            if (selectCloneHashMap().containsKey("trusted_seller")) {
                                                hashMap.put("trusted_seller", "true");
                                            }
                                            if (selectCloneHashMap().containsKey("near_me")) {
                                                hashMap.put("near_me", "true");
                                            }
                                        }
                                        if (hashMap.containsKey("trusted_seller")) {
                                            if (hashMap.get("trusted_seller").equals("true")) {
                                                updateFilter(true);
                                                setCatalogTypeBoolean("isTrusted", true);
                                            }
                                        }
                                        if (hashMap.containsKey("near_me")) {
                                            if (hashMap.get("near_me").equals("true")) {
                                                updateNearme(true);
                                                setCatalogTypeBoolean("isNearMe", true);
                                            }
                                        }


                                        if (selectCloneHashMap() != null) {
                                            selectCloneHashMap().clear();
                                            selectCloneHashMap().putAll(hashMap);
                                        } else {
                                            if (isNonCatalog) {
                                                paramsClone = hashMap;
                                            } else {
                                                paramsClone = hashMap;
                                            }
                                        }

                                        showCatalogs(selectCloneHashMap(), false, false);
                                    }

                                }
                            });
                            linear_predefined_filter.addView(appCompatButton[i]);
                        }


                    } else {
                        linear_predefined_filter.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getSubPredefinedFilter1(final Activity activity, String catagoryId, final String categoryName) {
        linear_progress.setVisibility(View.VISIBLE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PREDEFINED_SUB_FILTER_URL + "?category=" + catagoryId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    linear_progress.setVisibility(View.GONE);
                    final ResponseSubPredefinedCategory[] subFilter = Application_Singleton.gson.fromJson(response, ResponseSubPredefinedCategory[].class);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (subFilter.length > 0) {
                        linear_sub_root.setVisibility(View.VISIBLE);
                        horizontal_subfilter.setVisibility(View.VISIBLE);
                        txt_parent_category_name.setText(categoryName);
                        linear_sub_predefined_filter.removeAllViews();
                        final AppCompatButton[] appCompatButtons = new AppCompatButton[subFilter.length];
                        for (int i = 0; i < subFilter.length; i++) {
                            appCompatButtons[i] = new AppCompatButton(getActivity());
                            appCompatButtons[i].setBackgroundDrawable(null);
                            appCompatButtons[i].setText(subFilter[i].getName());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                appCompatButtons[i].setStateListAnimator(null);
                            }
                            appCompatButtons[i].setSupportAllCaps(false);
                            appCompatButtons[i].setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                            appCompatButtons[i].setGravity(Gravity.CENTER);
                            lp.setMargins(StaticFunctions.dpToPx(activity, 10), 0, 0, 0);
                            appCompatButtons[i].setSelected(false);
                            appCompatButtons[i].setTag(subFilter[i].getUrl());
                            appCompatButtons[i].setLayoutParams(lp);
                            final int finalI = i;
                            appCompatButtons[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (appCompatButtons[finalI].isSelected()) {
                                        appCompatButtons[finalI].setSelected(false);
                                        updateSubCategoryFilter(false, appCompatButtons[finalI]);
                                        initCall(false);
                                        setCatalogTypeBoolean("isFilter", false);
                                        initCall(true);
                                    } else {
                                        for (int j = 0; j < appCompatButtons.length; j++) {
                                            appCompatButtons[j].setSelected(false);
                                            updateSubCategoryFilter(false, appCompatButtons[j]);
                                        }
                                        setCatalogTypeBoolean("isFilter", true);
                                        appCompatButtons[finalI].setSelected(true);
                                        Application_Singleton.trackEvent("Predefined Sub Filter", "Click", appCompatButtons[finalI].getText().toString());
                                        updateSubCategoryFilter(true, appCompatButtons[finalI]);
                                        Uri intentUri = Uri.parse(appCompatButtons[finalI].getTag().toString());
                                        HashMap<String, String> hashMap = SplashScreen.getQueryString(intentUri);
                                        hashMap.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                        hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
                                        hashMap.put("view_type", "public");
                                        if (selectCloneHashMap() != null) {
                                            if (selectCloneHashMap().containsKey("trusted_seller")) {
                                                hashMap.put("trusted_seller", "true");
                                            }
                                            if (paramsClone != null && paramsClone.containsKey("near_me")) {
                                                hashMap.put("near_me", "true");
                                            }
                                        }
                                        if (hashMap.containsKey("trusted_seller")) {
                                            if (hashMap.get("trusted_seller").equals("true")) {
                                                updateFilter(true);
                                                setCatalogTypeBoolean("isTrusted", true);
                                            }
                                        }
                                        if (hashMap.containsKey("near_me")) {
                                            if (hashMap.get("near_me").equals("true")) {
                                                updateNearme(true);
                                                setCatalogTypeBoolean("isNearMe", true);
                                            }
                                        }

                                        if (selectCloneHashMap() != null) {
                                            selectCloneHashMap().clear();
                                            selectCloneHashMap().putAll(hashMap);
                                        } else {
                                            if (isNonCatalog) {
                                                paramsClone = hashMap;
                                            } else {
                                                paramsClone = hashMap;
                                            }
                                        }
                                       /* allCatalogs.clear();
                                        adapter.notifyDataSetChanged();*/
                                        showCatalogs(selectCloneHashMap(), false, false);
                                    }

                                }
                            });
                            linear_sub_predefined_filter.addView(appCompatButtons[i]);
                        }


                    } else {
                        linear_sub_root.setVisibility(View.VISIBLE);
                        horizontal_subfilter.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                linear_progress.setVisibility(View.GONE);

            }
        });
    }


    @Override
    public void changeCatalog() {
        // Change Catalog Type so clear
        //isAllowCache = false;
        if (CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON)) {
            isNonCatalog = true;
            isScreen = false;
        } else if (CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_SCREEN)) {
            isNonCatalog = true;
            isScreen = true;
        } else {
            isScreen = false;
            isNonCatalog = false;
        }

        if (isNonCatalog && isScreen) {
            ready_to_dispatch_filter.setVisibility(View.GONE);
            pre_order_filter.setVisibility(View.GONE);
        } else {
            ready_to_dispatch_filter.setVisibility(View.VISIBLE);
            pre_order_filter.setVisibility(View.VISIBLE);
        }
        if (paramsClone != null) {
            if (paramsClone.containsKey("offset")) {
                paramsClone.put("offset", "0");
            }
        }
        getMainPredefinedFilter(getActivity());
        initCall(false);

    }

    @Override
    public void changeViewType(String layout_type) {
        this.layout_type = layout_type;
        if (layout_type == Constants.PRODUCT_VIEW_LIST) {
            // List View
            try {
                lastFirstVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        } else {
            // Grid View
            try {
                lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }

            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        }
        bindAgainPaginate();
    }


    public HashMap<String, String> selectCloneHashMap() {
        /*if (CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON)) {
            return paramsClone;
        }*/
        return paramsClone;
    }


    public ResponseSavedFilters selectSavedCatalog() {
       /* if (CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON)) {
            return responseSavedFilters;
        }*/
        return responseSavedFilters;
    }


    public void setCatalogTypeBoolean(String type, boolean value) {
        boolean isNonCatalog = CatalogHolderVersion2.getSpinnerValue().equalsIgnoreCase(Constants.CATALOG_TYPE_NON);
        switch (type) {
            case "isFilter":
                isFilter = value;
               /* if (!isNonCatalog) isFilter = value;
                else isFilterNon = value;*/
                break;
            case "isTrusted":
                isTrusted = value;
              /*  if (!isNonCatalog) isTrusted = value;
                else isTrusted = value;*/
                break;
            case "isNearMe":
                isNearMe = value;
              /*  if (!isNonCatalog) isNearMe = value;
                else isNearMe = value;*/
            case "isSearchFilter":
                isSearchFilter = value;
               /* if (!isNonCatalog) isSearchFilter = value;
                else isSearchFilterNon = value;*/
                break;
            case "isPreorder":
                isPreCatalog = value;
               /* if (!isNonCatalog) isPreCatalog = value;
                else isPreCatalog = value;*/
                break;
            case "isReadyCatalog":
                isReadyCatalog = value;
               /* if (!isNonCatalog) isReadyCatalog = value;
                else isReadyCatalog = value;*/
                break;
        }
    }

    public void bindAgainPaginate() {
        if (paginate != null) {
            paginate.unbind();
        }

        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
    }


    public void sendFilterResultAnalytics(HashMap<String, String> filter_para) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Products_FilterResults");
        HashMap<String, String> prop = new HashMap<>();
        if (filter_para != null) {
            for (Map.Entry<String, String> entry : filter_para.entrySet()) {
                prop.put("filter_attribute_" + entry.getKey(), entry.getValue());
            }
        }
        if (from != null) {
            prop.put("source", from);
        }


        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    /**
     * Before sending final param to server remove client key
     */
    public HashMap<String, String> removeClientKey(HashMap<String, String> param) {
        if (param != null) {
            if (param.containsKey("from")) {
                param.remove("from");
            }

            if (param.containsKey("focus_position")) {
                param.remove("focus_position");
            }

            if (param.containsKey("product_availability")) {
                param.remove("product_availability");
            }
        }

        return param;
    }


}

