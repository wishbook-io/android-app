package com.wishbook.catalog.home.catalog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonadapters.CatalogsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.adapter.ScreenQualityGroupAdapter;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.details.SortBottomDialog;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Catalogs extends GATrackedFragment implements
        Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener, SortBottomDialog.SortBySelectListener,
        SearchView.OnQueryTextListener, CatalogsAdapter.UpdateListPageListener {

    private View v;
    private CatalogsAdapter adapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private String filtertype = null;
    private String filtervalue = null;
    private TextView filter_image;
    HashMap<String, String> paramsClone = null;
    ArrayList<Response_catalogMini> allCatalogs = new ArrayList<>();
    ArrayList<Response_catalogMini> allCatalogsFiltered = new ArrayList<>();
    ArrayList<Response_catalog> allSrcreenCatalogs = new ArrayList<>();
    int lastFirstVisiblePosition = 0;
    AppCompatButton trusted_filter;
    AppCompatButton nearme_filter;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    private MaterialBadgeTextView badge_filter_count, badge_sort_count;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    String pageType;
    RelativeLayout relative_filter_1;
    Spinner spinner;
    ImageView search_icon, img_my_fragment_searchclose;
    RelativeLayout linear_my_catalog_searchview;
    RelativeLayout relative_top_bar;
    private SearchView searchView;
    TextView txt_seller_near_me_subtext;
    private LinearLayout linear_sort, linear_search, linear_filter;

    ScreenQualityGroupAdapter screenQualityGroupAdapter;

    // ### Seller Approval Layout variable #####

    @BindView(R.id.linear_approval_status)
    LinearLayout linear_approval_status;
    @BindView(R.id.txt_approval_stage)
    TextView txt_approval_stage;
    @BindView(R.id.linear_chat_call)
    LinearLayout linear_chat_call;
    @BindView(R.id.btn_call_wb_support)
    AppCompatButton btn_call_wb_support;
    @BindView(R.id.btn_chat_wb_support)
    AppCompatButton btn_chat_wb_support;

    @BindView(R.id.float_button_add_catalog)
    FloatingActionButton float_button_add_catalog;

    // ### My Products Filter Popup #### //
    @BindView(R.id.img_filter)
    ImageView img_filter;

    @BindView(R.id.txt_filter)
    TextView txt_filter;

    boolean isFilterPopupshow;
    PopupMenu popupMenu;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }


    public Fragment_Catalogs() {
    }

    @SuppressLint("ValidFragment")
    public Fragment_Catalogs(String type) {
        this.pageType = type;
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
        ButterKnife.bind(this, v);
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        filter_image = (TextView) v.findViewById(R.id.filter_image);
        badge_filter_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_filter_count);
        badge_filter_count.setBadgeCount(0, true);
        badge_sort_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_sort_count);
        badge_sort_count.setBadgeCount(0, true);
        trusted_filter = (AppCompatButton) v.findViewById(R.id.trusted_filter);
        trusted_filter.setVisibility(View.GONE);
        nearme_filter = v.findViewById(R.id.nearme_filter);
        nearme_filter.setVisibility(View.GONE);
        linear_filter = v.findViewById(R.id.filter);
        relative_filter_1 = v.findViewById(R.id.relative_filter_1);
        spinner = v.findViewById(R.id.spinner);
        txt_seller_near_me_subtext = v.findViewById(R.id.txt_seller_near_me_subtext);

        if (pageType != null) {
            if (pageType.equals(CatalogHolder.MYCATALOGS)) {
                linear_filter.setVisibility(View.GONE);
                relative_filter_1.setVisibility(View.VISIBLE);

                // Show Add Floating Button for My Product tab and hide support chat

                // temp condition
                if(StaticFunctions.checkOrderDisableConfig(getActivity())) {
                    float_button_add_catalog.setVisibility(View.GONE);
                } else {
                    float_button_add_catalog.setVisibility(View.VISIBLE);
                }
                try {
                    getActivity().findViewById(R.id.support_chat_fab).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFilter();
            }
        });

        linear_sort = v.findViewById(R.id.linear_sort);
        linear_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortBottom();
            }
        });

        final Bundle filter1 = getArguments();
        if (filter1 != null) {
            filtertype = filter1.getString("filtertype");
            filtervalue = filter1.getString("filtervalue");

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        if (filtertype == null | filtervalue == null) {
            adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs,
                    this,
                    true,
                    0);
            mRecyclerView.setAdapter(adapter);
            adapter.setUpdateListPageListener(this);
        } else {
            adapter = new CatalogsAdapter((AppCompatActivity) getActivity(), allCatalogsFiltered,
                    this,
                    false,
                    0);
            mRecyclerView.setAdapter(adapter);
        }

        mRecyclerView.setAdapter(adapter);


        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        adapter.notifyDataSetChanged();

        initSwipeRefresh(v);
        initCall();
        initTopBar();
        initListener();
        if (pageType != null && pageType.equalsIgnoreCase(CatalogHolder.MYCATALOGS)) {
            initSellerApproval();
        }

        CatalogHolder.toggleFloating(mRecyclerView, getActivity());
        if (getArguments() != null) {
            lastFirstVisiblePosition = getArguments().getInt(Application_Singleton.adapterFocusPosition);
        }
        return v;
    }

    private void initCall() {
        Log.e("TAG", "initCall:  isFilter==>" + isFilter);
        if ((filtertype == null | filtervalue == null) && !isFilter) {
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "mycatalogs");
            params.put("product_type", Constants.CATALOG_TYPE_CAT);
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            if (getArguments() != null && getArguments().getString("product_type") != null) {
                if (getArguments().getString("product_type").equalsIgnoreCase(Constants.PRODUCT_TYPE_CAT)) {
                    spinner.setSelection(0);
                }

                if (getArguments().getString("product_type").equalsIgnoreCase(Constants.PRODUCT_TYPE_NON)) {
                    spinner.setSelection(1);
                }

                if (getArguments().getString("product_type").equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    spinner.setSelection(2);
                }

            } else {
                showCatalogs(params, true);
            }
            //showCatalogs(params, true);
        } else if (isFilter) {
            if (filtertype == null | filtervalue == null) {
                if (spinner.getSelectedItem().toString().equals(Constants.DISPLAY_SCREEN)) {

                    showScreenCatalogs(paramsClone, true);
                } else {
                    showCatalogs(paramsClone, true);
                }
            } else {
                showCatalogsFiltered(paramsClone, true);
            }
        } else {
            if (Application_Singleton.deep_link_filter != null) {
                paramsClone = Application_Singleton.deep_link_filter;
                if (paramsClone != null) {
                }
                if (paramsClone.containsKey("limitdisplay")) {
                    paramsClone.put("limit", paramsClone.get("limitdisplay"));
                }
                showCatalogsFiltered(paramsClone, true);
                Application_Singleton.deep_link_filter = null;
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put(filtertype, filtervalue);
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(INITIAL_OFFSET));
                showCatalogsFiltered(params, true);
            }

        }
    }

    private void toFilter() {
        Intent intent = new Intent(getActivity(), ActivityFilter.class);
        if (paramsClone != null) {
            intent.putExtra("previousParam", paramsClone);
        }
        intent.putExtra("from_public", false);
        if (filtertype != null) {
            if (filtertype.equals("brand")) {
                intent.putExtra("from_brand", true);
            } else if (filtertype.equals("category")) {
                intent.putExtra("from_category", true);
                intent.putExtra("categoryID", filtervalue);
            }
        }
        startActivityForResult(intent, 1);
    }

    private void initListener() {

        txt_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMyProductFilterPopup(img_filter);
            }
        });
        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMyProductFilterPopup(img_filter);
            }
        });

        float_button_add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateAddCatalog();
            }
        });
    }

    private void openMyProductFilterPopup(View view) {
        if (isFilterPopupshow && popupMenu != null) {
            popupMenu.dismiss();
            isFilterPopupshow = false;
            return;
        }
        popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_enable) {
                    if (paramsClone != null) {
                        if (paramsClone.containsKey("buyer_disabled"))
                            paramsClone.remove("buyer_disabled");
                        isFilter = true;
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                        showCatalogs(paramsClone, true);
                    }
                } else if (item.getItemId() == R.id.action_disable) {
                    if (paramsClone != null) {
                        paramsClone.put("buyer_disabled", "true");
                        isFilter = true;
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                        showCatalogs(paramsClone, true);
                    }
                }
                isFilterPopupshow = false;
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                isFilterPopupshow = false;
            }
        });

        popupMenu.inflate(R.menu.menu_my_catalog_popup);
        if (!isFilterPopupshow) {
            popupMenu.show();
            isFilterPopupshow = true;
        }

    }


    private void navigateAddCatalog() {
        String catalog_type = "catalog";
        String spinner_value = spinner.getSelectedItem().toString();
        if (spinner_value != null) {
            if (spinner_value.equalsIgnoreCase("Non-Catalog")) {
                catalog_type = "noncatalog";
            } else if (spinner_value.equalsIgnoreCase(Constants.DISPLAY_SCREEN)) {
                catalog_type = "screen";
            } else {
                catalog_type = "catalog";
            }
        }
        final String finalCatalog_type = catalog_type;

        if (!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("type", "profile_update");
            new DeepLinkFunction(param, getActivity());
            return;
        }

        Intent intent = new Intent(getActivity(), Activity_AddCatalog.class);
        intent.putExtra("catalog_type", finalCatalog_type);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        Log.d("Pause", String.valueOf(lastFirstVisiblePosition));
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.post(new Runnable() {
            public void run() {
                mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }
        });
        // ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);


    }

    private void showCatalogs(HashMap<String, String> params, Boolean progress) {
        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
                //     showProgress();
            } else {
                methodType = HttpManager.METHOD.GET;
            }
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (params == null) {
            params = new HashMap<>();
        }
        if (params.get("offset").equals("0")) {
            page = 0;
            allCatalogs.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            hasLoadedAllItems = false;
        }

        if (params.size() > 0) {
            // to ignore for filter count
            int filtercount = params.size();
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

            if (params.containsKey("catalog_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("product_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ordering")) {
                if (!params.get("ordering").equals("-id")) {
                    badge_sort_count.setHighLightMode();
                } else {
                    badge_sort_count.setBadgeCount(0, true);
                }
            } else {
                badge_sort_count.setBadgeCount(0, true);
            }

            badge_filter_count.setBadgeCount(filtercount, true);
        }

        if (params.containsKey("buyer_disabled")) {
            if (params.get("buyer_disabled").equalsIgnoreCase("true")) {
                txt_filter.setText("(Disabled)");
            }
        } else {
            txt_filter.setText("(Enable)");
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        final HashMap<String, String> finalParams = params;

        String url = URLConstants.companyUrl(getActivity(), "catalogs", "");

        HttpManager.getInstance(getActivity()).request(methodType, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
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

                        if (offset == 0) {
                            mRecyclerView.setAdapter(adapter);
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
                                    adapter.notifyItemRangeChanged(offset, allCatalogs.size() - 1);
                                } catch (Exception e) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            hasLoadedAllItems = true;
                            adapter.notifyDataSetChanged();
                        }

                        if (adapter.getItemCount() <= LIMIT) {
                            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);

                        }
                    }
                } catch (
                        Exception e)

                {
                    e.printStackTrace();
                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });


    }

    private void showScreenCatalogs(HashMap<String, String> params, Boolean progress) {
        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
                //     showProgress();
            } else {
                methodType = HttpManager.METHOD.GET;
            }
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (params == null) {
            params = new HashMap<>();
        }

        if (params.get("offset").equals("0")) {
            page = 0;
            allSrcreenCatalogs.clear();
            if (screenQualityGroupAdapter != null) {
                screenQualityGroupAdapter.notifyDataSetChanged();
            }
            hasLoadedAllItems = false;
            showProgress();
        }

        int filtercount = setCommonFilterCount(params);
        badge_filter_count.setBadgeCount(filtercount, true);

        if (params.containsKey("buyer_disabled")) {
            if (params.get("buyer_disabled").equalsIgnoreCase("true")) {
                txt_filter.setText("(Disabled)");
            }
        } else {
            txt_filter.setText("(Enable)");
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        final HashMap<String, String> finalParams = params;

        String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
        if (spinner.getSelectedItem().equals(Constants.DISPLAY_SCREEN)) {
            url = URLConstants.companyUrl(getActivity(), "screen-catalog-grouping", "");
            finalParams.put("catalog_type", "noncatalog");
            finalParams.put("set_type", "multi_set");
            finalParams.remove("product_type");
        }


        HttpManager.getInstance(getActivity()).request(methodType, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
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
                        Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
                        if (response_catalog.length > 0) {
                            allSrcreenCatalogs = (ArrayList<Response_catalog>) HttpManager.removeDuplicateIssue(offset, allSrcreenCatalogs, dataUpdated, LIMIT);
                            for (int i = 0; i < response_catalog.length; i++) {
                                allSrcreenCatalogs.add(response_catalog[i]);
                            }
                            if (offset == 0) {
                                screenQualityGroupAdapter = new ScreenQualityGroupAdapter(getActivity(), allSrcreenCatalogs);
                                mRecyclerView.setAdapter(screenQualityGroupAdapter);
                            }
                            page = (int) Math.ceil((double) allCatalogs.size() / LIMIT);
                            if (response_catalog.length % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }
                            if (allSrcreenCatalogs.size() <= LIMIT) {
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            } else {
                                try {
                                    mRecyclerView.getAdapter().notifyItemRangeChanged(offset, allSrcreenCatalogs.size() - 1);
                                } catch (Exception e) {
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }
                        } else {
                            hasLoadedAllItems = true;
                            if (screenQualityGroupAdapter != null) {
                                screenQualityGroupAdapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(screenQualityGroupAdapter);
                            } else {
                                screenQualityGroupAdapter = new ScreenQualityGroupAdapter(getActivity(), allSrcreenCatalogs);
                                mRecyclerView.setAdapter(screenQualityGroupAdapter);
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

    private void showCatalogsFiltered(final HashMap<String, String> params, Boolean progress) {


        HttpManager.METHOD methodType;
        if (progress) {
            methodType = HttpManager.METHOD.GETWITHPROGRESS;
            //showProgress();
        } else {
            methodType = HttpManager.METHOD.GET;
        }


        if (params.get("offset").equals("0")) {
            page = 0;
            allCatalogsFiltered.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            hasLoadedAllItems = false;
        }


        int filtercount = setCommonFilterCount(params);
        if (filtertype != null) {
            if (filtertype.equals("brand")) {
                filtercount = filtercount - 1;
            } else if (filtertype.equals("category")) {
                filtercount = filtercount - 1;
            }
        }

        if (!params.containsKey("view_type")) {
            params.put("view_type", "public");
        }

        if (params.containsKey("min_price") && params.containsKey("max_price")) {
            filtercount = filtercount - 1;
        }

        badge_filter_count.setBadgeCount(filtercount, true);

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(methodType, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                int offset = 10;
                try {
                    offset = Integer.parseInt(params.get("offset"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Loading = false;

                Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                if (isAdded() && !isDetached()) {
                    if (response_catalog.length > 0) {

                        //checking if data updated on 2nd page or more
                        allCatalogsFiltered = (ArrayList<Response_catalogMini>) HttpManager.removeDuplicateIssue(offset, allCatalogsFiltered, dataUpdated, LIMIT);
                        for (int i = 0; i < response_catalog.length; i++) {
                            allCatalogsFiltered.add(response_catalog[i]);
                        }
                        page = (int) Math.ceil((double) allCatalogsFiltered.size() / LIMIT);

                        if (response_catalog.length % LIMIT != 0) {
                            hasLoadedAllItems = true;
                        }
                        if (allCatalogsFiltered.size() <= LIMIT) {
                            adapter.notifyDataSetChanged();
                        } else {
                            try {
                                adapter.notifyItemRangeChanged(offset, allCatalogsFiltered.size() - 1);
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
            }
        });

    }


    private int setCommonFilterCount(HashMap<String, String> params) {

        int filtercount = 0;

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

            if (params.containsKey("catalog_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("product_type")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("ordering")) {
                if (!params.get("ordering").equals("-id")) {
                    badge_sort_count.setHighLightMode();
                } else {
                    badge_sort_count.setBadgeCount(0, true);
                }
            } else {
                badge_sort_count.setBadgeCount(0, true);
            }


        }

        return filtercount;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                    StaticFunctions.printHashmap(params);
                    if (params != null) {
                        if (paramsClone != null) {
                            paramsClone.clear();
                        }

                        isFilter = true;
                        if (filtertype == null | filtervalue == null) {
                            params.put("view_type", "mycatalogs");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            paramsClone = params;
                            showCatalogs(params, true);
                        } else {
                            params.put(filtertype, filtervalue);
                            if (filtertype.equals("category")) {
                                params.put("view_type", "public");
                            }
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            paramsClone = params;
                            showCatalogsFiltered(params, true);
                        }
                    }

                }
                break;
            case 1211:
                if (resultCode == Activity.RESULT_OK) {
                    if (paramsClone != null && paramsClone.size() > 0) {
                        isFilter = true;
                        if (filtertype == null | filtervalue == null) {
                            paramsClone.put("limit", String.valueOf(LIMIT));
                            paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogs(paramsClone, false);
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            paramsClone.put("limit", String.valueOf(LIMIT));
                            paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogsFiltered(paramsClone, false);
                        }
                    } else {
                        if (filtertype == null | filtervalue == null) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("view_type", "mycatalogs");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogs(params, false);
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(filtertype, filtervalue);
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogsFiltered(params, false);
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                Log.e("TAG", "onLoadMore: Called IF");
                if (filtertype == null | filtervalue == null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("view_type", "mycatalogs");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(page * LIMIT));
                    if (spinner.getSelectedItem().toString().equals(Constants.DISPLAY_SCREEN)) {
                        showScreenCatalogs(params, true);
                    } else {
                        showCatalogs(params, true);
                    }
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(filtertype, filtervalue);
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(page * LIMIT));
                    showCatalogsFiltered(params, false);
                }

            } else {
                Log.e("TAG", "onLoadMore: Called");
                if (paramsClone != null) {
                    if (filtertype == null | filtervalue == null) {
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(page * LIMIT));
                        if (spinner.getSelectedItem().toString().equals(Constants.DISPLAY_SCREEN)) {
                            showScreenCatalogs(paramsClone, true);
                        } else {
                            showCatalogs(paramsClone, true);
                        }
                    } else {
                        HashMap<String, String> params = new HashMap<>();
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(page * LIMIT));
                        showCatalogsFiltered(paramsClone, false);
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
                swipe_container.setRefreshing(false);
                if (linear_my_catalog_searchview.getVisibility() == View.VISIBLE) {
                    linear_my_catalog_searchview.setVisibility(View.GONE);
                    relative_top_bar.setVisibility(View.VISIBLE);
                    searchView.clearFocus();
                    spinner.setSelection(1);
                }
                KeyboardUtils.hideKeyboard(getActivity());
                if(paramsClone!=null) {
                    if(paramsClone.containsKey("offset")){
                        paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                    }
                }
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private void openSortBottom() {
        SortBottomDialog sortBottomDialog = null;
        if (paramsClone != null) {
            if (paramsClone.containsKey("ordering")) {
                sortBottomDialog = sortBottomDialog.newInstance(paramsClone.get("ordering"));
            } else {
                sortBottomDialog = SortBottomDialog.newInstance(null);
            }
        } else {
            sortBottomDialog = SortBottomDialog.newInstance(null);
        }
        sortBottomDialog.show(getFragmentManager(), sortBottomDialog.getTag());
        sortBottomDialog.setSortBySelectListener(Fragment_Catalogs.this);
    }

    @Override
    public void onCheck(String check) {
        if (check != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("ordering", check);
            isFilter = true;
            if (filtertype == null | filtervalue == null) {
                params.put("view_type", "mycatalogs");
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(INITIAL_OFFSET));
                paramsClone = params;
                showCatalogs(paramsClone, false);
            } else {
                params.put(filtertype, filtervalue);
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(INITIAL_OFFSET));
                paramsClone = params;
                showCatalogsFiltered(paramsClone, false);
            }
        }
    }

    public void initTopBar() {
        spinner = v.findViewById(R.id.spinner);
        search_icon = v.findViewById(R.id.search_icon);
        img_my_fragment_searchclose = v.findViewById(R.id.img_my_fragment_searchclose);
        linear_my_catalog_searchview = (RelativeLayout) v.findViewById(R.id.linear_my_catalog_searchview);
        relative_top_bar = v.findViewById(R.id.relative_top_bar);
        LinearLayout search_linear = v.findViewById(R.id.search_linear);
        search_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relative_top_bar.setVisibility(View.GONE);
                linear_my_catalog_searchview.setVisibility(View.VISIBLE);
                searchView.setFocusable(true);
                searchView.setIconifiedByDefault(false);
                searchView.requestFocus();
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
                searchView.setQuery("", false);
            }
        });
        img_my_fragment_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_my_catalog_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
                searchView.clearFocus();
                spinner.setSelection(0);
                KeyboardUtils.hideKeyboard(getActivity());
            }
        });
        searchView = (SearchView) v.findViewById(R.id.search_view_my_catalog);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bindAgainPaginate();
                HashMap<String, String> params = new HashMap<>();
                if (i == 0) {
                    params.put("catalog_type", Constants.CATALOG_TYPE_CAT);
                    params.put("product_type", Constants.CATALOG_TYPE_CAT);
                    img_filter.setVisibility(View.VISIBLE);
                    txt_filter.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    params.put("catalog_type", Constants.CATALOG_TYPE_NON);
                    params.put("product_type", Constants.CATALOG_TYPE_NON);
                    img_filter.setVisibility(View.VISIBLE);
                    txt_filter.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    params.put("product_type", Constants.PRODUCT_TYPE_SCREEN);
                    img_filter.setVisibility(View.GONE);
                    txt_filter.setVisibility(View.GONE);
                }


                if (paramsClone != null) {
                    paramsClone.clear();
                    isFilter = true;
                }
                if (filtertype == null | filtervalue == null) {
                    params.put("view_type", "mycatalogs");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    if (i != 2) {
                        String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
                        if (supplier_approval_status != null && !supplier_approval_status.equals(Constants.SELLER_APPROVAL_APPROVED)) {
                            isFilter = true;
                            params.put("buyer_disabled", "true");
                        }
                    }

                    paramsClone = params;

                    if (i == 2) {
                        // For Non-Catalog
                        showScreenCatalogs(params, true);
                    } else {
                        // For Other
                        showCatalogs(params, true);
                    }
                } else {
                    params.put(filtertype, filtervalue);
                    if (filtertype.equals("category")) {
                        params.put("view_type", "public");
                    }
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    paramsClone = params;
                    Log.e("TAG", "onItemSelected: Show Catalogs");
                    showCatalogsFiltered(params, true);
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
        HashMap<String, String> params = new HashMap<>();
        params.put("search", newText);
        if (paramsClone != null) {
            paramsClone.clear();
            isFilter = true;
        }
        if (filtertype == null | filtervalue == null) {
            params.put("view_type", "mycatalogs");
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            paramsClone = params;
            showCatalogs(params, true);
        } else {
            params.put(filtertype, filtervalue);
            if (filtertype.equals("category")) {
                params.put("view_type", "public");
            }
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            paramsClone = params;
            showCatalogsFiltered(params, true);
        }
        return false;
    }


    public void initSellerApproval() {
        String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
        if (supplier_approval_status != null) {
            if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_PENDING)) {
                linear_approval_status.setVisibility(View.VISIBLE);
                txt_approval_stage.setText(Html.fromHtml(getResources().getString(R.string.upload_seller_approval_stage2)));
                linear_chat_call.setVisibility(View.VISIBLE);
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_APPROVED)) {
                if (!UserInfo.getInstance(getActivity()).isApprovedMsgShow()) {
                    linear_approval_status.setVisibility(View.VISIBLE);
                    txt_approval_stage.setText(Html.fromHtml(getResources().getString(R.string.upload_seller_approval_stage4)));
                    linear_chat_call.setVisibility(View.GONE);
                    UserInfo.getInstance(getActivity()).setApprovedMsgShow(true);
                } else {
                    linear_approval_status.setVisibility(View.GONE);
                }
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_REJECTED)) {
                linear_approval_status.setVisibility(View.VISIBLE);
                txt_approval_stage.setText(Html.fromHtml(getResources().getString(R.string.upload_seller_approval_stage3)), TextView.BufferType.NORMAL);
                linear_chat_call.setVisibility(View.VISIBLE);
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD)) {
                linear_approval_status.setVisibility(View.VISIBLE);
                txt_approval_stage.setText(Html.fromHtml(getResources().getString(R.string.upload_seller_approval_stage1)), TextView.BufferType.NORMAL);
                linear_chat_call.setVisibility(View.GONE);
            }
        }


        btn_call_wb_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("callwbsupport", "call", "sellerApproval");
                new ChatCallUtils(getActivity(), ChatCallUtils.CHAT_CALL_TYPE, null);
            }
        });

        btn_chat_wb_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("chatwbsupport", "chat", "sellerApproval");
                String msg = "Asking Supplier Approval:  Supplier Ref No #" + UserInfo.getInstance(getActivity()).getCompany_id();
                new ChatCallUtils(getActivity(), ChatCallUtils.WB_CHAT_TYPE, msg);
            }
        });
    }


    @Override
    public void updateList(int position) {
        isAllowCache = false;
        lastFirstVisiblePosition = position;
        initCall();
    }

    public void bindAgainPaginate() {
        Loading = false;
        hasLoadedAllItems = false;
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
    }
}

