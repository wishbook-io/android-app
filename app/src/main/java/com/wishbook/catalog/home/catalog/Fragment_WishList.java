package com.wishbook.catalog.home.catalog;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Fragment_WishList extends GATrackedFragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {

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
    AppCompatButton trusted_filter;
    AppCompatButton nearme_filter;

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;

    private RelativeLayout relative_filter;
    Menu menu;
    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    public static boolean isRequiredUpdateData;


    public Fragment_WishList() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_wishlist, ga_container, true);
        setHasOptionsMenu(true);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        try {
            ((AppCompatActivity) getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            v.findViewById(R.id.swipe_container).setVisibility(View.GONE);
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(),dialog);
            } else {
                View dialog = v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog, null);
            }
            return v;
        }
        relative_filter = v.findViewById(R.id.relative_filter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs,Constants.PRODUCT_VIEW_GRID);
        adapter.setWishListProduct(true);
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


        initCall(true);
        // focus particular item
        if (getArguments() != null) {
            lastFirstVisiblePosition = getArguments().getInt(Application_Singleton.adapterFocusPosition);
        }
        CatalogHolder.toggleFloating(mRecyclerView,getActivity());
        initSwipeRefresh(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        try {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                menu.findItem(R.id.cart).setVisible(false);
            } else {
                menu.findItem(R.id.cart).setVisible(true);
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cartcount = preferences.getInt("cartcount", 0);
                    if (cartcount == 0) {
                        BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                    } else {
                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
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
    public void onPause() {
        super.onPause();
        try {
            menu.findItem(R.id.cart).setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initCall(boolean isLoadCacheData) {

        if (Application_Singleton.deep_link_filter != null) {
            Application_Singleton.deep_link_filter = null;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(LIMIT));
        params.put("offset", String.valueOf(INITIAL_OFFSET));
        showCatalogs(params);
    }


    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void showCatalogs(HashMap<String, String> params) {
        String url = null;
        url = URLConstants.userUrl(getActivity(), "wishlist-catalog", "");
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
        final HashMap<String, String> finalParams = params;

        if (isAdded() && !isDetached()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
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
                        if (isAdded() && !isDetached() && adapter != null) {
                            final int offset = Integer.parseInt(finalParams.get("offset"));
                            Loading = false;
                            Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                            if (response_catalog.length > 0 ) {
                                allCatalogs = (ArrayList<Response_catalogMini>) HttpManager.removeDuplicateIssue(offset, allCatalogs, dataUpdated, LIMIT);
                                for (int i = 0; i < response_catalog.length; i++) {
                                    allCatalogs.add(response_catalog[i]);
                                }
                                page = (int) Math.ceil((double) allCatalogs.size() / LIMIT);
                                if (response_catalog.length % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (allCatalogs.size() <= LIMIT) {
                                    adapter.setWishListProduct(true);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        mRecyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyItemRangeChanged(offset, allCatalogs.size() - 1);
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
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(page * LIMIT));
                showCatalogs(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                showCatalogs(paramsClone);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(isRequiredUpdateData){
            isAllowCache = false;
            initCall(false);
        }*/

        try {
            ((AppCompatActivity) getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                menu.findItem(R.id.cart).setVisible(false);
            } else {
                menu.findItem(R.id.cart).setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }
}
