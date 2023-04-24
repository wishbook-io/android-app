package com.wishbook.catalog.home.catalog;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.HomeCategoryItemsAdapter;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.SearchActivity;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.adapter.PromotionalProductTabAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Product_Tab extends GATrackedFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.linear_category)
    LinearLayout linear_category;

    @BindView(R.id.linear_collection)
    LinearLayout linear_collection;

    @BindView(R.id.recycler_view_category)
    RecyclerView recycler_view_category;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;


    private View view;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;


    public static String TAG = Fragment_Product_Tab.class.getSimpleName();

    String from = "Product Tab";


    // Toolbar and menu varialble intialize
    public static Toolbar toolbar;
    private FloatingActionButton add_catalog;
    Menu menu;
    String layout_type = Constants.PRODUCT_VIEW_GRID;
    int catalogWishList = 0;


    public Fragment_Product_Tab() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_product_tab, ga_container, true);
        ButterKnife.bind(this, view);
        initView();

        // ####### Set up menu and Toolbar Start ########

        setHasOptionsMenu(true);
        if (UserInfo.getInstance(getActivity()).getDefaultProductView().equalsIgnoreCase(Constants.PRODUCT_VIEW_GRID)) {
            layout_type = Constants.PRODUCT_VIEW_GRID;
            getActivity().invalidateOptionsMenu();
        } else {
            layout_type = Constants.PRODUCT_VIEW_LIST;
            getActivity().invalidateOptionsMenu();
        }
        setupToolbar(view);

        if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            add_catalog.hide();
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            add_catalog.hide();
        }
        initListener();

        if (getArguments() != null && getArguments().getString("from") != null) {
            from = getArguments().getString("from");
        }

        initCall();

        initSwipeRefresh(view);
        toggleFloating(mRecyclerView, getActivity());

        // temp function
        hideOrderDisableConfig();

        return view;
    }

    public void initView() {
        add_catalog = view.findViewById(R.id.add_catalog);
        recycler_view_category.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_category.setHasFixedSize(true);
        recycler_view_category.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            add_catalog.hide();
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            add_catalog.hide();
        }
    }

    public void initListener() {
        add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                        && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("type", "profile_update");
                    new DeepLinkFunction(param, getActivity());
                    return;
                }
                Intent intent = new Intent(getActivity(), Activity_AddCatalog.class);
                intent.putExtra("catalog_type", Constants.PRODUCT_TYPE_CAT);
                startActivity(intent);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Add_screen", "product page", null);

            }
        });
    }

    public void initCall() {
        getHomeCategories();
        getDeepLink();
    }

    public void getHomeCategories() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10&is_home_display=1", null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        try {
                            ResponseHomeCategories[] response_catagories = Application_Singleton.gson.fromJson(response, ResponseHomeCategories[].class);
                            if (response_catagories.length > 0) {
                                linear_category.setVisibility(View.VISIBLE);
                                recycler_view_category.setVisibility(View.VISIBLE);
                                ArrayList<ResponseHomeCategories> responseHomeCategoriesArrayList = new ArrayList<ResponseHomeCategories>(Arrays.asList(response_catagories));
                                HomeCategoryItemsAdapter homeCategoryItemsAdapter = new HomeCategoryItemsAdapter(getActivity(), responseHomeCategoriesArrayList, HomeCategoryItemsAdapter.DEEPLINKCATEGORY);
                                recycler_view_category.setAdapter(homeCategoryItemsAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            linear_category.setVisibility(View.GONE);
                            recycler_view_category.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                linear_category.setVisibility(View.GONE);
                recycler_view_category.setVisibility(View.GONE);
            }
        });

    }

    private void getDeepLink() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HashMap<String, String> param = new HashMap<>();
        param.put("visible_on", "ProductTab");
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.PRODUCT_TAB_PROMOTION, param, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    final Response_Promotion[] deepLinkPromotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (deepLinkPromotion.length > 0) {
                            linear_collection.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            ArrayList<Response_Promotion> dePromotions = new ArrayList<Response_Promotion>(Arrays.asList(deepLinkPromotion));
                            PromotionalProductTabAdapter promotionalProductTabAdapter = new PromotionalProductTabAdapter(getActivity(), dePromotions, PromotionalProductTabAdapter.DEEPLINKPRODUCT);
                            mRecyclerView.setAdapter(promotionalProductTabAdapter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                mRecyclerView.setVisibility(View.GONE);
                linear_collection.setVisibility(View.GONE);
            }
        });
    }


    public void setupToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Products");
        toolbar.inflateMenu(R.menu.menu_product_tab);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        WishbookEvent wishbookEvent = new WishbookEvent();
                        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
                        wishbookEvent.setEvent_names("Products_Search_screen");
                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "product tab");
                        wishbookEvent.setEvent_properties(prop);
                        new WishbookTracker(getActivity(), wishbookEvent);
                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.action_wishlist:
                        Log.d("TAG", "onMenuItemClick: WishList Clik");
                        Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                        Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                        getActivity().startActivity(new Intent(getContext(), OpenContainer.class));
                        break;
                    case R.id.action_cart:
                        try {
                            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            int cart_count = preferences.getInt("cartcount", 0);
                            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                        break;
                }
                return false;
            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_product_tab, menu);
        this.menu = menu;
        cartToolbarUpdate();
        wishListToolbarUpdate();
        isSavedFilterApplied(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.action_wishlist:
                Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                getActivity().startActivity(new Intent(getContext(), OpenContainer.class));
                break;
            case R.id.action_cart:
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cart_count = preferences.getInt("cartcount", 0);
                    Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;
        }
        return true;
    }

    public void toggleFloating(RecyclerView recyclerView, Context context) {
        final FloatingActionButton floatingActionButton = Activity_Home.support_chat_fab;
        try {
            Activity_Home.pref = StaticFunctions.getAppSharedPreferences(context);
            if (Activity_Home.pref != null) {
                if (Activity_Home.pref.getString("groupstatus", "0").equals("2") || UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                    add_catalog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    Activity_Home.support_chat_fab.hide();
                    if (add_catalog != null)
                        add_catalog.hide();
                } else {
                    Activity_Home.support_chat_fab.show();
                    if (add_catalog != null)
                        add_catalog.show();
                }
            }
        });

    }

    public void cartToolbarUpdate() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        } else {
            menu.findItem(R.id.action_cart).setVisible(false);
        }
    }

    private void wishListToolbarUpdate() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            if (menu != null) {
                MenuItem wishlist_item = menu.findItem(R.id.action_wishlist);
                if (wishlist_item != null) {
                    wishlist_item.setVisible(true);
                }
                int wishcount = UserInfo.getInstance(getActivity()).getWishlistCount();
                if (wishcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                }
            }
        } else {
            MenuItem wishlist_item = menu.findItem(R.id.action_wishlist);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            wishlist_item.setVisible(false);
        }
    }

    private void isSavedFilterApplied(boolean isApplied) {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller") && menu != null) {
            MenuItem saved_item = menu.findItem(R.id.action_saved_filter);
            if (!isApplied) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(getActivity(), saved_item, getResources().getDrawable(R.drawable.ic_save_filter_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(getActivity(), saved_item, getResources().getDrawable(R.drawable.ic_save_filter_24dp), ActionItemBadge.BadgeStyles.RED, "1");
            }
        } else {
            if (menu != null) {
                MenuItem saved_item = menu.findItem(R.id.action_saved_filter);
                if (saved_item != null) {
                    saved_item.setVisible(false);
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                // wishlist count
                int wishlistCount = UserInfo.getInstance(getActivity()).getWishlistCount();
                if (catalogWishList == wishlistCount) {
                    // not update wishlist
                    Fragment_WishList.isRequiredUpdateData = false;
                } else {
                    // update data
                    Fragment_WishList.isRequiredUpdateData = true;
                }
                catalogWishList = wishlistCount;
            }
            cartToolbarUpdate();
            wishListToolbarUpdate();
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

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig() {

        // add catalog button hide from catalog level
        if (StaticFunctions.checkOrderDisableConfig(getActivity())) {
            add_catalog.hide();
            add_catalog = null;

        }
    }

}
