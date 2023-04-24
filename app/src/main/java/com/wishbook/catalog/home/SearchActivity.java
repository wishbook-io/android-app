package com.wishbook.catalog.home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AllSectionAdapter;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonadapters.SearchSuggestionAdapter;
import com.wishbook.catalog.commonmodels.AllDataModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements
        Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.suggestion_recyclerview)
    RecyclerView mSuggestionRecyclerView;

    @BindView(R.id.linear_local_search_container)
    LinearLayout linear_local_search_container;

    @BindView(R.id.recyclerview_local_search)
    RecyclerView recyclerview_local_search;

    @BindView(R.id.catalog_recyclerview)
    RecyclerView catalog_recyclerview;

    @BindView(R.id.btn_search_img)
    AppCompatButton btn_search_img;

    @BindView(R.id.recycler_view_shared_by_me)
    RecyclerView recycler_view_shared_by_me;

    @BindView(R.id.relative_search_container)
    RelativeLayout relative_search_container;

    @BindView(R.id.img_search)
    ImageView img_search;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.relative_progress)
    RelativeLayout relative_progress;

    @BindView(R.id.search_view)
    SearchView search_view;

    @BindView(R.id.txt_empty_msg)
    TextView txt_empty_msg;


    private Context mContext;
    HashMap<String, String> searchHashMap = null;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    ArrayList<Response_catalogMini> allCatalogs = new ArrayList<>();

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    String catalog_type;

    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogRRCImageUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;


    //private RelativeLayout filter;
    private BrowseCatalogsAdapter adapter;


    /**
     * Required catalog_type Parameter for catalog and non-catalog
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = SearchActivity.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(150);
            getWindow().getSharedElementReturnTransition().setDuration(0)
                    .setInterpolator(new DecelerateInterpolator());
        }
        setToolBar();
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) search_view.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchTextView.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        search_view.setFocusable(true);
        search_view.setIconified(false);
        search_view.requestFocusFromTouch();
        initView();
        initSwipeRefresh();

        if (getIntent().getStringExtra("searchfilter") != null) {
            search_view.setQuery(getIntent().getStringExtra("searchfilter").toString(), false);
        }
    }

    private void setToolBar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initView() {
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(LIMIT));
        params.put("offset", String.valueOf(INITIAL_OFFSET));
        params.put("collection", "true");
        initSharedByMe();
        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(mContext).getCompanyGroupFlag(), CompanyGroupFlag.class);
        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {
            getSharedProducts(params);
        } else {
            recycler_view_shared_by_me.setVisibility(View.GONE);
        }


        catalog_type = Constants.PRODUCT_TYPE_CAT + "," + Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        catalog_recyclerview.setLayoutManager(mLayoutManager);
        catalog_recyclerview.setVisibility(View.VISIBLE);
        txt_empty_msg.setVisibility(View.GONE);


        mSuggestionRecyclerView = (RecyclerView) findViewById(R.id.suggestion_recyclerview);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        mSuggestionRecyclerView.setLayoutManager(mLayoutManager1);

        if (getIntent().getStringExtra("catalog_type") != null) {
            if (getIntent().getStringExtra("catalog_type").equalsIgnoreCase(Constants.CATALOG_TYPE_NON)) {
                catalog_type = Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN;
            } else if (getIntent().getStringExtra("catalog_type").equalsIgnoreCase(Constants.CATALOG_TYPE_CAT)) {
                catalog_type = Constants.PRODUCT_TYPE_CAT;
            } else {
                catalog_type = Constants.PRODUCT_TYPE_CAT + "," + Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN;
            }
        } else {
            catalog_type = Constants.PRODUCT_TYPE_CAT + "," + Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN;
        }


        search_view.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                storeSearchHistory(query);
                sendSearchEvent();
                KeyboardUtils.hideKeyboard(SearchActivity.this);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                if (search_view.getQuery() != null)
                    Application_Singleton.trackEvent("Public Search", "Search", search_view.getQuery().toString().trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (search_view.hasFocus()) {
                    if (!newText.isEmpty()) {
                        Log.e("TAG", "onQueryTextChange:=======> IS Not Empty");
                        hideLocalSearchView();
                        String[] wordsList = newText.split(" ");
                        if (wordsList.length > 0) {
                            // getSearchSuggestion(SearchActivity.this, wordsList[wordsList.length - 1]);
                            getSearchSuggestion(SearchActivity.this, newText);
                        }
                    } else {
                        Log.e("TAG", "onQueryTextChange:=======> IS Empty");
                        showSearchLocalView();
                        mSuggestionRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    mSuggestionRecyclerView.setVisibility(View.GONE);
                }

                return false;
            }
        });


        adapter = new BrowseCatalogsAdapter(SearchActivity.this, allCatalogs);
        catalog_recyclerview.setAdapter(adapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(catalog_recyclerview, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        adapter.notifyDataSetChanged();

        if(isAllowImageSearch(this)) {
            img_search.setVisibility(View.VISIBLE);
            btn_search_img.setVisibility(View.VISIBLE);
            img_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlbumActivity();
                }
            });

            btn_search_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlbumActivity();
                }
            });
        } else {
            img_search.setVisibility(View.GONE);
            btn_search_img.setVisibility(View.GONE);
        }


        showSearchLocalView();
    }

    private void initSharedByMe() {
        recycler_view_shared_by_me.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_view_shared_by_me.setHasFixedSize(true);
        recycler_view_shared_by_me.setNestedScrollingEnabled(false);

        recyclerview_local_search.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerview_local_search.setHasFixedSize(true);
        recyclerview_local_search.setNestedScrollingEnabled(false);
    }

    private void showSearchLocalView() {
        ArrayList<String> local_history;
        local_history = getSearchHistory();
        Log.e("TAG", "showSearchLocalView: ");
        if (local_history != null && local_history.size() > 0) {
            relative_search_container.setVisibility(View.GONE);
            Log.e("TAG", "showSearchLocalView: History Size > 0 ");
            linear_local_search_container.setVisibility(View.VISIBLE);
            recyclerview_local_search.setVisibility(View.VISIBLE);
            SearchSuggestionAdapter searchSuggestionAdapter = new SearchSuggestionAdapter(mContext, local_history, true);
            recyclerview_local_search.setAdapter(searchSuggestionAdapter);
            searchSuggestionAdapter.setSearchItemClick(new SearchSuggestionAdapter.AdapterNotifyListener() {
                @Override
                public void itemClick(int position) {
                    //if (!search_view.getQuery().toString().isEmpty()) {
                    String[] wordsList = search_view.getQuery().toString().split(" ");
                    search_view.clearFocus();
                    try {
                        //  search_view.setQuery(search_view.getQuery().toString().replace(wordsList[wordsList.length - 1], strings.get(position) + " "), true);
                        search_view.setQuery(local_history.get(position), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mSuggestionRecyclerView.setVisibility(View.GONE);
                    // }
                }
            });
        } else {
            relative_search_container.setVisibility(View.GONE);
        }
    }

    private void hideLocalSearchView() {
        linear_local_search_container.setVisibility(View.GONE);
        relative_search_container.setVisibility(View.VISIBLE);
    }

    private void getSearchSuggestion(final Activity activity, final String query) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        String url = URLConstants.companyUrl(SearchActivity.this, "catalogs_suggestion", "") + "?q=" + query;
        if (catalog_type != null) {
            url = URLConstants.companyUrl(SearchActivity.this, "catalogs_suggestion", "") + "?q=" + query + "&product_type=" + catalog_type;
        }
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, url, null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    String[] suggestionString = Application_Singleton.gson.fromJson(response, String[].class);
                    if (suggestionString.length > 0) {
                        if (!search_view.getQuery().toString().isEmpty()) {
                            mSuggestionRecyclerView.setVisibility(View.VISIBLE);
                            final ArrayList<String> strings = new ArrayList<String>(Arrays.asList(suggestionString));
                            SearchSuggestionAdapter adapter = new SearchSuggestionAdapter(SearchActivity.this, strings, false);
                            mSuggestionRecyclerView.setAdapter(adapter);
                            adapter.setSearchItemClick(new SearchSuggestionAdapter.AdapterNotifyListener() {
                                @Override
                                public void itemClick(int position) {
                                    if (!search_view.getQuery().toString().isEmpty()) {
                                        String[] wordsList = search_view.getQuery().toString().split(" ");
                                        search_view.clearFocus();
                                        try {
                                            //  search_view.setQuery(search_view.getQuery().toString().replace(wordsList[wordsList.length - 1], strings.get(position) + " "), true);
                                            search_view.setQuery(strings.get(position), true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        mSuggestionRecyclerView.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            showSearchLocalView();
                        }
                    } else {
                        mSuggestionRecyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void showCatalogs(HashMap<String, String> params, Boolean progress, boolean isSearch) {
        relative_progress.setVisibility(View.VISIBLE);
        txt_empty_msg.setVisibility(View.GONE);
        String url = null;
        url = URLConstants.companyUrl(mContext, "catalogs", "");
        HttpManager.METHOD methodType;
        if (progress) {
            methodType = HttpManager.METHOD.GETWITHPROGRESS;
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (params == null) {
            params = new HashMap<>();
        }
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                allCatalogs.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;

            }
        } else {
            params.put("offset", "0");
            page = 0;
            allCatalogs.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
            hasLoadedAllItems = false;
        }
        if (catalog_type != null) {
            params.put("product_type", catalog_type);
        }

        final HashMap<String, String> finalParams = params;

        if (!isFinishing()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(SearchActivity.this);
            HttpManager.getInstance(SearchActivity.this).request(methodType, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    // hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {

                    WishbookEvent wishbookEvent = new WishbookEvent();
                    wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
                    wishbookEvent.setEvent_names("Products_SearchResults");
                    HashMap<String, String> prop = new HashMap<>();
                    prop.put("num_results", "0");
                    prop.put("keyword", search_view.getQuery().toString());
                    wishbookEvent.setEvent_properties(prop);
                    new WishbookTracker(mContext, wishbookEvent);


                    try {
                        if (!isFinishing()) {
                            relative_progress.setVisibility(View.GONE);
                            catalog_recyclerview.setVisibility(View.VISIBLE);
                            txt_empty_msg.setVisibility(View.GONE);
                            int offset = 0;
                            try {
                                offset = Integer.parseInt(finalParams.get("offset"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.v("sync response", response);
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
                                        catalog_recyclerview.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyItemRangeChanged(finalOffset, allCatalogs.size() - 1);
                                            }
                                        });

                                    } catch (Exception e) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (adapter.getItemCount() == 0) {
                                    catalog_recyclerview.setVisibility(View.GONE);
                                    txt_empty_msg.setVisibility(View.VISIBLE);
                                } else {
                                    catalog_recyclerview.setVisibility(View.VISIBLE);
                                    txt_empty_msg.setVisibility(View.GONE);
                                }
                                hasLoadedAllItems = true;
                                adapter.notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    //hideProgress();
                }
            });
        }

    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (searchHashMap == null) {
                hasLoadedAllItems = true;
            } else {
                searchHashMap.put("limit", String.valueOf(LIMIT));
                searchHashMap.put("offset", String.valueOf(page * LIMIT));
                // showCatalogs(searchHashMap, false, true);
            }
        } else {
            hasLoadedAllItems = true;
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
                if (!search_view.getQuery().toString().isEmpty())
                    sendSearchEvent();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        try {
            hideUploadDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    public void storeSearchHistory(String searchSting) {
        if (!searchSting.trim().isEmpty()) {
            ArrayList<String> localSearchHistory = null;
            if (PrefDatabaseUtils.getSearchHistory(this) != null) {
                localSearchHistory = new Gson().fromJson(PrefDatabaseUtils.getSearchHistory(this), new TypeToken<ArrayList<String>>() {
                }.getType());

            } else {
                localSearchHistory = new ArrayList<>();
            }
            if (!localSearchHistory.contains(searchSting)) {
                localSearchHistory.add(0, searchSting);
            }

            PrefDatabaseUtils.setSearchHistory(this, new Gson().toJson(localSearchHistory));
            if (localSearchHistory.size() > Constants.STORE_SEARCH_LOCAL_HISTORY) {
                ArrayList<String> temp_history = new ArrayList<>(localSearchHistory);
                Collections.reverse(temp_history);
                int removeLength = localSearchHistory.size() - Constants.STORE_SEARCH_LOCAL_HISTORY;
                for (int i = 0; i < removeLength; i++) {
                    try {
                        int last_index = (localSearchHistory.size() - 1);
                        localSearchHistory.remove(last_index);
                        PrefDatabaseUtils.setSearchHistory(this, new Gson().toJson(localSearchHistory));
                    } catch (Exception e) {
                        Log.e("RealmDelete", e.getMessage());
                    }
                }
            }
        }


    }

    public ArrayList<String> getSearchHistory() {
        ArrayList<String> localSearchHistory = null;
        if (PrefDatabaseUtils.getSearchHistory(this) != null) {
            localSearchHistory = new Gson().fromJson(PrefDatabaseUtils.getSearchHistory(this), new TypeToken<ArrayList<String>>() {
            }.getType());
        }

        return localSearchHistory;
    }

    private void getSharedProducts(final HashMap<String, String> params) {
        try {
            Log.e("TAG", "getSharedProducts:=====> ");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, "product-share", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (!isFinishing()) {
                        try {

                            ArrayList<Response_catalogMini> response_arrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                            }.getType());
                            if (response_arrayList.size() > 0) {
                                HashMap<String, String> deep_link_param = new HashMap<>();
                                deep_link_param.put("type", "tab");
                                deep_link_param.put("page", "reseller/sharedbyme");
                                ArrayList<AllDataModel> prebook_dataList = new ArrayList<>();
                                prebook_dataList.add(0, new AllDataModel("Products Already Shared by You", response_arrayList, null, 3, deep_link_param));
                                AllSectionAdapter preBookAdapter = new AllSectionAdapter((AppCompatActivity) mContext, prebook_dataList);
                                recycler_view_shared_by_me.setVisibility(View.VISIBLE);
                                recycler_view_shared_by_me.setAdapter(preBookAdapter);
                            } else {
                                recycler_view_shared_by_me.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAlbumActivity() {
        Intent intent = new Intent(mContext, AlbumSelectActivity.class);
        intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 1);
        intent.putExtra("isoneshotselection", true);
        startActivityForResult(intent, Application_Singleton.MULTIIMAGE_SELECT_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.MULTIIMAGE_SELECT_REQUEST_CODE && resultCode == this.RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp != null & temp.size() > 0) {
                callUploadSearchImages(temp.get(0).getPath());
            }
        }
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard((Activity) mContext);
        View currentFocus = (SearchActivity.this).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    public void callUploadSearchImages(String filePath) {
        showUploadDialog();
        HashMap params = new HashMap();
        File outputrenamed = new File(filePath);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        HttpManager.getInstance((Activity) mContext).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(mContext, "imgsearch", ""), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideUploadDialog();
                try {
                    if (!isFinishing()) {
                        final Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        if (response_catalog.length > 0) {
                            if (response_catalog[0].getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                Bundle bundle = new Bundle();
                                bundle.putString("product_id", response_catalog[0].getId());
                                bundle.putString("from", "Image Search");
                                new NavigationUtils().navigateSingleProductDetailPage(mContext, bundle);
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("product_id", response_catalog[0].getId());
                                bundle.putString("from", "Image Search");
                                new NavigationUtils().navigateDetailPage(mContext, bundle);
                            }
                        } else {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("Not Found")
                                    .content("Your uploaded image result not found")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(final ErrorString error) {
                hideUploadDialog();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void showUploadDialog() {
        try {
            //initializing loader
            dialogRRCImageUpload = new MaterialDialog.Builder(mContext).title("Uploading ..").build();
            dialogRRCImageUpload.setCancelable(false);
            dialogRRCImageUpload.show();
            BeforeTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            handlerPostData = new Handler();
            runnablePostData = new Runnable() {
                public void run() {
                    if (handlerPostData != null) {
                        long AfterTime = System.currentTimeMillis();
                        long TotalRxAfterTest = TrafficStats.getTotalTxBytes();
                        double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                        if ((rxDiff != 0)) {
                            double rxBPS = (rxDiff / 1024); // total rx bytes per second.
                            dialogRRCImageUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                            TotalTxBeforeTest = TotalRxAfterTest;
                        }

                        handlerPostData.postDelayed(this, 1000);
                    }
                }
            };
            handlerPostData.postDelayed(runnablePostData, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideUploadDialog() {
        if (dialogRRCImageUpload != null) {
            handlerPostData.removeCallbacks(runnablePostData);
            dialogRRCImageUpload.dismiss();
        }

    }


    public boolean isAllowImageSearch(Context context) {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("PRODUCT_IMAGE_SEARCH_FEATURE_IN_APP")) {
                    try {
                        if(Integer.parseInt(data.get(i).getValue()) == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        return true;
    }

    ////// ############  Send Wishbook Analyses Event  ###################//
    public void sendSearchEvent() {


        try {
            WishbookEvent wishbookEvent = new WishbookEvent();
            wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
            wishbookEvent.setEvent_names("Products_SearchResults");
            HashMap<String, String> prop = new HashMap<>();
            prop.put("keyword", search_view.getQuery().toString());
            wishbookEvent.setEvent_properties(prop);
            new WishbookTracker(mContext, wishbookEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }



        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (searchHashMap != null && searchHashMap.containsKey("ordering")) {
            hashMap.put("ordering", searchHashMap.get("ordering"));
        }
        allCatalogs.clear();
        hashMap.put("view_type", "public");
        hashMap.put("limit", String.valueOf(LIMIT));
        hashMap.put("offset", String.valueOf(INITIAL_OFFSET));
        hashMap.put("q", search_view.getQuery().toString().trim());
        searchHashMap = hashMap;


        //showCatalogs(searchHashMap, true, true);


        mSuggestionRecyclerView.setVisibility(View.GONE);
        search_view.clearFocus();


        Bundle bundle = new Bundle();
        Fragment_BrowseProduct fragment_browseProduct = new Fragment_BrowseProduct();
        bundle.putString("searchfilter", search_view.getQuery().toString().trim());
        fragment_browseProduct.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = search_view.getQuery().toString().trim();
        Application_Singleton.CONTAINERFRAG = fragment_browseProduct;
        Intent intent2 = new Intent(this, OpenContainer.class);
        startActivity(intent2);
        finish();
    }
}
