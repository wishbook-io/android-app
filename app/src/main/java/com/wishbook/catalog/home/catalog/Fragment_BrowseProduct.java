package com.wishbook.catalog.home.catalog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.paginate.Paginate;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseSavedFilters;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.SearchActivity;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.adapter.PromotionalProductTabAdapter;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.details.FilterBottomDialog;
import com.wishbook.catalog.home.catalog.details.SavedFilterBottomDialog;
import com.wishbook.catalog.home.catalog.details.SortBottomDialog;
import com.wishbook.catalog.home.catalog.view_action.ViewMultipleSelectionBottomBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;

public class Fragment_BrowseProduct extends GATrackedFragment implements
        Paginate.Callbacks,
        SortBottomDialog.SortBySelectListener,
        SavedFilterBottomDialog.SavedFilterSelectListener,
        SwipeRefreshLayout.OnRefreshListener, BrowseCatalogsAdapter.ProductBottomBarListener {

    private View view;
    private BrowseCatalogsAdapter adapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private TextView filter_image;
    HashMap<String, String> paramsClone = null;

    ArrayList<Response_catalogMini> allCatalogs = new ArrayList<>();
    int lastFirstVisiblePosition = 0;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    private boolean isPreCatalog;
    private boolean isReadyCatalog;

    private String filtertype = null;
    private String filtervalue = null;
    private boolean isFromBrandFilter = false;


    private MaterialBadgeTextView badge_filter_count;

    private LinearLayout linear_filter,
            linear_searchview, linear_sub_root, linear_progress, linear_search_sort;

    private SearchView search_view;

    private RelativeLayout relative_filter;


    private SwipeRefreshLayout swipe_container;
    private boolean isFilter, isSearchFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    ResponseSavedFilters responseSavedFilters;


    public static String TAG = Fragment_BrowseProduct.class.getSimpleName();

    HashMap<String, String> searchHashMap = null;

    boolean isNonCatalog, isToolbarMenuHide;
    boolean isSinglePcView;

    String from = "Product Tab";

    private LinearLayout linear_catalog_type, linear_category, linear_availability, linear_view_type;
    private LinearLayout linear_sort_bar;
    private TextView txt_filter_availability, txt_filter_category, txt_filter_catalog, txt_collection_type_value, txt_availability_label;
    private LinearLayout linear_promotional_category;
    private LinearLayout linear_multiselect_bar, linear_whatsapp_share;

    private TextView txt_add_to_cart_multiple, txt_other_share_multiple, txt_select_none;

    RelativeLayout relative_filter_container;


    // Toolbar and menu variable initialize
    public static Toolbar toolbar;
    private FloatingActionButton add_catalog;
    Menu menu;
    String layout_type = Constants.PRODUCT_VIEW_GRID;
    int catalogWishList = 0;
    boolean isShowSortBar = true;

    // Sort Bar
    TextView txt_sort_trending, txt_sort_latest, txt_sort_price;
    ImageView img_sort_price;

    RecyclerView recycler_view_category;
    PermissionGrantedListener permissionGrantedListener;
    String searchFilter = null;


    public Fragment_BrowseProduct() {

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
        inflater.inflate(R.layout.fragment_browse_product, ga_container, true);
        ButterKnife.bind(this, view);
        searchFilter = null;
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(view.findViewById(R.id.list_empty1));


        recycler_view_category = view.findViewById(R.id.recycler_view_category);
        recycler_view_category.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_category.setHasFixedSize(true);
        initView();
        initSortBarView(view);


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

        try {
            if (Activity_Home.pref != null) {
                if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                    add_catalog.hide();
                } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    add_catalog.hide();
                }
            } else {
                add_catalog.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initListener();

        if (getArguments() != null && getArguments().getString("from") != null) {
            from = getArguments().getString("from");
        }


        badge_filter_count = (MaterialBadgeTextView) view.findViewById(R.id.badge_filter_count);
        badge_filter_count.setBadgeCount(0, true);

        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFilterScreen();
            }
        });

        relative_filter_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFilterScreen();
            }
        });


        // focus particular item
        if (getArguments() != null) {
            if (getArguments().getString("focus_position") != null) {
                lastFirstVisiblePosition = Integer.parseInt(getArguments().getString("focus_position"));
            } else {
                lastFirstVisiblePosition = getArguments().getInt(Application_Singleton.adapterFocusPosition);
            }
        }

        if (getArguments() != null) {
            if (getArguments().getBoolean("isfrom_brand_filter")) {
                isFromBrandFilter = getArguments().getBoolean("isfrom_brand_filter");
            }
        }

        if (Application_Singleton.deep_link_filter_non_catalog != null) {
            isNonCatalog = true;
        }


        if (UserInfo.getInstance(getActivity()).getDefaultProductView().equalsIgnoreCase(Constants.PRODUCT_VIEW_GRID)) {
            changeViewType(Constants.PRODUCT_VIEW_GRID);
        } else {
            changeViewType(Constants.PRODUCT_VIEW_LIST);
        }
        changeCatalog();

        toggleFloating(mRecyclerView, getActivity());
        initSwipeRefresh(view);

        getCategory();

        return view;
    }


    public void initView() {
        relative_filter = view.findViewById(R.id.relative_filter);
        relative_filter_container = view.findViewById(R.id.relative_filter_container);
        filter_image = (TextView) view.findViewById(R.id.filter_image);


        linear_search_sort = view.findViewById(R.id.linear_search_sort);
        search_view = view.findViewById(R.id.search_view);
        linear_filter = view.findViewById(R.id.linear_filter);

        linear_searchview = view.findViewById(R.id.linear_searchview);
        linear_searchview.setVisibility(View.GONE);

        linear_progress = view.findViewById(R.id.linear_progress);
        linear_sort_bar = view.findViewById(R.id.linear_sort_bar);


        linear_sub_root = view.findViewById(R.id.linear_sub_root);

        linear_category = view.findViewById(R.id.linear_category);
        linear_catalog_type = view.findViewById(R.id.linear_catalog_type);
        linear_availability = view.findViewById(R.id.linear_availability);
        linear_view_type = view.findViewById(R.id.linear_view_type);
        txt_filter_availability = view.findViewById(R.id.txt_filter_availability);
        txt_filter_category = view.findViewById(R.id.txt_filter_category);
        txt_filter_catalog = view.findViewById(R.id.txt_filter_catalog);
        txt_collection_type_value = view.findViewById(R.id.txt_collection_type_value);
        txt_availability_label = view.findViewById(R.id.txt_availability_label);


        linear_multiselect_bar = view.findViewById(R.id.linear_multiselect_bar);
        txt_other_share_multiple = view.findViewById(R.id.txt_other_share_multiple);
        linear_whatsapp_share = view.findViewById(R.id.linear_whatsapp_share);
        txt_add_to_cart_multiple = view.findViewById(R.id.txt_add_to_cart_multiple);
        txt_select_none = view.findViewById(R.id.txt_select_none);


        linear_promotional_category = view.findViewById(R.id.linear_promotional_category);
        add_catalog = view.findViewById(R.id.add_catalog);

        try {
            if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
                add_catalog.hide();
            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                add_catalog.hide();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            add_catalog.hide();
        } catch (Exception e) {
            e.printStackTrace();
            add_catalog.hide();
        }

    }


    public void initListener() {
        linear_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterBottom("category");
            }
        });

        linear_catalog_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterBottom("product_type");
            }
        });

        linear_availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterBottom("sell_full_catalog");
            }
        });

        linear_view_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterBottom("collection_type");
            }
        });

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
                if (isNonCatalog) {
                    intent.putExtra("catalog_type", Constants.PRODUCT_TYPE_NON);
                } else {
                    intent.putExtra("catalog_type", Constants.PRODUCT_TYPE_CAT);
                }
                startActivity(intent);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Add_screen", "product page", null);

            }
        });
    }

    public void initCall(boolean isLoadCacheData) {
        gettingFilterIfAny();
        if (Application_Singleton.deep_link_filter != null) {
            Log.e(TAG, "Init Call DeepLink  Catalog");
            paramsClone = Application_Singleton.deep_link_filter;

            if (paramsClone.containsKey("ready_to_ship")) {
                if (paramsClone.get("ready_to_ship").equals("true")) {
                    isReadyCatalog = true;
                } else {
                    isPreCatalog = true;
                }
            }


            if (paramsClone.containsKey("generic")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title")) {
                    hideToolbar(paramsClone.get("page_title"));
                }

                relative_filter.setVisibility(View.GONE);
            }

            if (paramsClone.containsKey("most_viewed")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title"))
                    hideToolbar(paramsClone.get("page_title"));
                else
                    hideToolbar(getResources().getString(R.string.most_viewed_catalog_title));
                relative_filter.setVisibility(View.GONE);
            }

            if (paramsClone.containsKey("most_ordered")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title"))
                    hideToolbar(paramsClone.get("page_title"));
                else
                    hideToolbar(getResources().getString(R.string.most_sold_catalog_title));
                relative_filter.setVisibility(View.GONE);
            }

            if (paramsClone.containsKey("collection") &&
                    paramsClone.get("collection").equalsIgnoreCase("false")) {
                isSinglePcView = true;
            }

            if (paramsClone.containsKey("focus_position")) {
                lastFirstVisiblePosition = Integer.parseInt(paramsClone.get("focus_position"));
            }

            if (isFromBrandFilter) {
                if (paramsClone.containsKey("page_title"))
                    hideToolbar(paramsClone.get("page_title"));
                else
                    hideToolbar(getResources().getString(R.string.most_sold_catalog_title));
            }


            Application_Singleton.deep_link_filter = null;
            showCatalogs(paramsClone);
            setCatalogTypeBoolean("isFilter", true);
        } else if (Application_Singleton.deep_link_filter_non_catalog != null) {
            Log.e(TAG, "Init Call DeepLink Non Catalog");
            paramsClone = Application_Singleton.deep_link_filter_non_catalog;
            if (paramsClone.containsKey("focus_position")) {
                lastFirstVisiblePosition = Integer.parseInt(paramsClone.get("focus_position"));
            }
            if (paramsClone.containsKey("ready_to_ship")) {
                if (paramsClone.get("ready_to_ship").equals("true")) {
                    isReadyCatalog = true;
                } else {
                    isPreCatalog = true;
                }
            }

            if (paramsClone.containsKey("generic")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title")) {
                    hideToolbar(paramsClone.get("page_title"));
                }
                relative_filter.setVisibility(View.GONE);
            }

            if (paramsClone.containsKey("most_viewed")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title"))
                    hideToolbar(paramsClone.get("page_title"));
                else
                    hideToolbar(getResources().getString(R.string.most_viewed_catalog_title));
                relative_filter.setVisibility(View.GONE);
            }
            if (paramsClone.containsKey("most_ordered")) {
                isShowSortBar = false;
                linear_sort_bar.setVisibility(View.GONE);
                if (paramsClone.containsKey("page_title"))
                    hideToolbar(paramsClone.get("page_title"));
                else
                    hideToolbar(getResources().getString(R.string.most_sold_catalog_title));
                relative_filter.setVisibility(View.GONE);
            }
            if (paramsClone.containsKey("collection") &&
                    paramsClone.get("collection").equalsIgnoreCase("false")) {
                isSinglePcView = true;
            }
            if (paramsClone.containsKey("focus_position")) {
                lastFirstVisiblePosition = Integer.parseInt(paramsClone.get("focus_position"));
            }
            Application_Singleton.deep_link_filter_non_catalog = null;
            showCatalogs(paramsClone);
            setCatalogTypeBoolean("isFilter", true);
        } else if (isFilter) {
            Log.e("TAG", "initCall:  isFilter");
            if (paramsClone.containsKey("collection") &&
                    paramsClone.get("collection").equalsIgnoreCase("false")) {
                isSinglePcView = true;
            }
            showCatalogs(paramsClone);
        } else if (isSearchFilter) {
            Log.e(TAG, "Init Call Search Filter");
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "public");
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            params.put("q", searchFilter);
            showCatalogs(params);
        } else {
            Log.e(TAG, "Init Call Else");
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "public");
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            params.put("ready_to_ship", "true");
            isReadyCatalog = true;
            setCatalogTypeBoolean("isFilter", false);
            if (Application_Singleton.isPublicTrusted) {
                params.put("trusted_seller", "true");
                Application_Singleton.isPublicTrusted = false;
            }
            showCatalogs(params);
        }

    }

    private void OpenFilterScreen() {
        Intent intent = new Intent(getActivity(), ActivityFilter.class);
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
        } else {
            HashMap<String, String> param = new HashMap<>();
            if (isNonCatalog) {
                param.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
            } else {
                param.put("product_type", Constants.PRODUCT_TYPE_CAT);
            }

            intent.putExtra("previousParam", param);
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
        if (isFromBrandFilter) {
            intent.putExtra("from_public", false);
            intent.putExtra("from_brand", true);
        }

        intent.putExtra("isNonCatalog", isNonCatalog);
        startActivityForResult(intent, 1);
    }

    public void setupToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (getActivity() instanceof OpenContainer) {
            ((OpenContainer) getActivity()).toolbar.setVisibility(View.GONE);
            ((OpenContainer) getActivity()).setSupportActionBar(toolbar);
            ((OpenContainer) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void hideToolbar(String title) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(title);
        toolbar.getMenu().clear();
        isToolbarMenuHide = true;
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!isToolbarMenuHide) {
            if (layout_type == Constants.PRODUCT_VIEW_LIST) {
                menu.findItem(R.id.action_view_type).setIcon(getResources().getDrawable(R.drawable.ic_view_grid_black_24dp));
            } else if (layout_type == Constants.PRODUCT_VIEW_GRID) {
                menu.findItem(R.id.action_view_type).setIcon(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu: ========>");
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        if (!isToolbarMenuHide) {
            inflater.inflate(R.menu.menu_catalog_holder_version2, menu);
            this.menu = menu;
            cartToolbarUpdate();
            wishListToolbarUpdate();
            isSavedFilterApplied(false);
        }

        if (isSearchFilter) {
            if(this.menu!=null && this.menu.findItem(R.id.action_saved_filter)!=null)
                this.menu.findItem(R.id.action_saved_filter).setVisible(false);
            Log.e(TAG, "onCreateOptionsMenu: Search Filter True ========>");
        } else {
            Log.e(TAG, "onCreateOptionsMenu: SearchFilter False ========>");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
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
                if (isSearchFilter) {
                    intent.putExtra("searchfilter", searchFilter);
                }
                startActivity(intent);
                if (isSearchFilter) {
                    getActivity().finish();
                }
                break;
            case R.id.action_view_type:
                if (layout_type == Constants.PRODUCT_VIEW_LIST) {
                    layout_type = Constants.PRODUCT_VIEW_GRID;
                    UserInfo.getInstance(getActivity()).setDefaultProductView(Constants.PRODUCT_VIEW_GRID);
                } else if (layout_type == Constants.PRODUCT_VIEW_GRID) {
                    layout_type = Constants.PRODUCT_VIEW_LIST;
                    UserInfo.getInstance(getActivity()).setDefaultProductView(Constants.PRODUCT_VIEW_LIST);
                }
                changeViewType(layout_type);
                Log.e(TAG, "onOptionsItemSelected: Grid ======");
                getActivity().invalidateOptionsMenu();
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
            case R.id.action_saved_filter:
                openSavedFilterBottom();
                break;
        }
        return true;
    }

    public void toggleFloating(RecyclerView recyclerView, Context context) {
        final FloatingActionButton floatingActionButton = Activity_Home.support_chat_fab;
        FloatingActionButton add_float = add_catalog;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && linear_sort_bar.getVisibility() == View.VISIBLE) {
                    //floatingActionButton.hide();
                    linear_sort_bar.setVisibility(View.GONE);
                        /*if (finalAdd_float != null)
                            finalAdd_float.hide();*/
                } else if (dy < 0 && linear_sort_bar.getVisibility() != View.VISIBLE) {
                    if (isShowSortBar) {
                        linear_sort_bar.setVisibility(View.VISIBLE);
                    }
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
        } else {
            MenuItem wishlist_item = menu.findItem(R.id.action_wishlist);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            wishlist_item.setVisible(false);
        }
    }

    private void isSavedFilterApplied(boolean isApplied) {
        try {
            if (!isSearchFilter && searchFilter == null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            // e.printStackTrace();
        }
    }

    private void showCatalogs(HashMap<String, String> params) {

        String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
        HttpManager.METHOD methodType = HttpManager.METHOD.GETWITHPROGRESS;
        if (params == null) {
            params = new HashMap<>();
        }


        //// Start Set Runtime parameter start
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                Log.e(TAG, "showCatalogs: Clear");
                page = 0;
                allCatalogs.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;

            }
        } else {
            Log.e(TAG, "showCatalogs: Clear 2");
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


        if (!params.containsKey("view_type")) {
            params.put("view_type", "public");
        }

        // to decide is add product_type parameter
        boolean isAddProdutType = true;
        if(isSearchFilter) {
            if(params.containsKey("product_type")){
                isAddProdutType = true;
            } else {
                isAddProdutType = false;
            }
        }


        if(isAddProdutType) {
            if (!params.containsKey("product_type")) {
                if (isNonCatalog) {
                    params.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
                } else {
                    params.put("product_type", Constants.CATALOG_TYPE_CAT);
                }
            }
        }

        params.put("collection", String.valueOf(!isSinglePcView));


        if (responseSavedFilters != null) {
            isSavedFilterApplied(true);
        } else {
            isSavedFilterApplied(false);
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

        if (isSearchFilter && searchFilter != null) {
            params.put("q", searchFilter);
        }


        if (!params.containsKey("ordering") && UserInfo.getInstance(getActivity()).getDefaultSortPref() != null) {
            params.put("ordering", UserInfo.getInstance(getActivity()).getDefaultSortPref());
        }

        if (params.containsKey("offset") && params.get("offset").equalsIgnoreCase("0")) {
            if (adapter != null) {
                //Log.e(TAG, "showCatalogs:  Adapter not null ==> isSinglePcView"+ isSinglePcView);
                adapter.setCollectionTypeProduct(isSinglePcView);
                if (params.containsKey("sell_full_catalog") && params.get("sell_full_catalog").equalsIgnoreCase("true")) {
                    adapter.setFullCatalogFilter(true);
                } else {
                    adapter.setFullCatalogFilter(false);
                }

                adapter.notifyDataSetChanged();
            }
        }


        //// End of Runtime parameter

        final HashMap<String, String> finalParams = removeClientKey(params);
        setFilterBar(finalParams);
        sendFilterResultAnalytics(params);


        // #### Api Calling Start

        if (isAdded() && !isDetached()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
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
                            final Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                            if (response_catalog.length > 0) {
                                //checking if data updated on 2nd page or more
                                //allCatalogs = (ArrayList<Response_catalogMini>) HttpManager.removeDuplicateIssue(offset, allCatalogs, dataUpdated, LIMIT);
                                for (int i = 0; i < response_catalog.length; i++) {
                                    allCatalogs.add(response_catalog[i]);
                                }

                                page = (int) Math.ceil((double) allCatalogs.size() / LIMIT);

                                if (response_catalog.length % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (allCatalogs.size() <= LIMIT) {
                                    Log.e(TAG, "Size < LIMIT");
                                    adapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        final int finalOffset = offset;
                                        mRecyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    //Log.e(TAG, "run: =====> Final Offset"+ finalOffset  +"\n Size===>"+(allCatalogs.size()-1) );
                                                    adapter.notifyItemRangeInserted(finalOffset, response_catalog.length);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        adapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //Log.e(TAG, "Exception : Notify DataSet Changes" + adapter.getItemCount());
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

        // #### Api Calling End

    }

    public void checkStorePermission(Context context) {
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 1599);
        } else {
            if (permissionGrantedListener != null) {
                permissionGrantedListener.writePermissionGranted();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        if (paramsClone != null) {
            paramsClone.clear();
        }
        Application_Singleton.deep_link_filter = null;
        Application_Singleton.deep_link_filter_non_catalog = null;
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
                    from = "Filter";
                    if (params != null) {
                        if (params.size() > 0) {
                            StaticFunctions.printHashmap(params);
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
                            } else {
                                setCatalogTypeBoolean("isTrusted", false);
                            }

                            if (params.containsKey("product_availability") && params.get("product_availability").equalsIgnoreCase("both")) {
                                setCatalogTypeBoolean("isPreorder", true);
                                setCatalogTypeBoolean("isReadyCatalog", true);
                            } else {
                                if (params.containsKey("ready_to_ship")) {
                                    setCatalogTypeBoolean("isPreorder", params.get("ready_to_ship").equals("true") ? false : true);
                                    setCatalogTypeBoolean("isReadyCatalog", params.get("ready_to_ship").equals("true") ? true : false);
                                } else {
                                    setCatalogTypeBoolean("isPreorder", false);
                                    setCatalogTypeBoolean("isReadyCatalog", false);
                                }
                            }


                            if (isReadyCatalog) {
                                params.put("ready_to_ship", "true");
                            }

                            if (isPreCatalog) {
                                params.put("ready_to_ship", "false");
                            }


                            if (params.containsKey("product_type")) {
                                if (params.get("product_type").equalsIgnoreCase("catalog")) {
                                    isNonCatalog = false;
                                } else if (params.get("product_type").contains("noncatalog")) {
                                    isNonCatalog = true;
                                }
                            }

                            params.put("view_type", "public");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            paramsClone = params;
                            setCatalogTypeBoolean("isFilter", true);
                            showCatalogs(params);

                        } else {
                            if (paramsClone != null) {
                                paramsClone.clear();
                                paramsClone = null;
                            }

                            allCatalogs.clear();
                            setCatalogTypeBoolean("isTrusted", false);
                            params.put("view_type", "public");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            showCatalogs(params);
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
                    OpenFilterScreen();
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    showCatalogs(params);
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
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    showCatalogs(params);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1599) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (permissionGrantedListener != null) {
                        permissionGrantedListener.writePermissionGranted();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Write External Storage Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    public interface PermissionGrantedListener {
        void writePermissionGranted();
    }

    public void setPermissionGrantedListener(PermissionGrantedListener permissionGrantedListener) {
        this.permissionGrantedListener = permissionGrantedListener;
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
                    showCatalogs(searchHashMap);
                }
            } else {
                if (isNonCatalog) {
                    if (paramsClone == null) {
                        HashMap<String, String> params = new HashMap<>();
                        if (isPreCatalog) {
                            params.put("ready_to_ship", "false");
                        }
                        if (isReadyCatalog) {
                            params.put("ready_to_ship", "true");
                        }
                        params.put("view_type", "public");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(params);
                    } else {
                        paramsClone.put("limit", String.valueOf(LIMIT));
                        paramsClone.put("offset", String.valueOf(page * LIMIT));
                        showCatalogs(selectCloneHashMap());
                    }
                } else {
                    if (paramsClone == null) {
                        HashMap<String, String> params = new HashMap<>();
                        if (isPreCatalog) {
                            params.put("ready_to_ship", "false");
                        }
                        if (isReadyCatalog) {
                            params.put("ready_to_ship", "true");
                        }
                        params.put("view_type", "public");
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
        sortBottomDialog.setSortBySelectListener(Fragment_BrowseProduct.this);
    }

    private void initSortBarView(View view) {
        txt_sort_latest = view.findViewById(R.id.txt_sort_latest);
        txt_sort_price = view.findViewById(R.id.txt_sort_price);
        txt_sort_trending = view.findViewById(R.id.txt_sort_trending);
        img_sort_price = view.findViewById(R.id.img_sort_price);


        txt_sort_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txt_sort_latest.isSelected())
                    setSortFilter("latest");
            }
        });

        txt_sort_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortFilter("price");
            }
        });

        txt_sort_trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txt_sort_trending.isSelected())
                    setSortFilter("trending");
            }
        });
    }


    private void setSortFilter(String type) {
        HashMap<String, String> params = new HashMap<>();
        switch (type) {
            case "latest":
                params.put("ordering", "-id");
                txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                img_sort_price.setVisibility(View.GONE);
                txt_sort_price.setText("PRICE");
                txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));

                txt_sort_trending.setSelected(false);
                txt_sort_latest.setSelected(true);
                txt_sort_price.setSelected(false);
                UserInfo.getInstance(getActivity()).setDefaultSortPref("-id");
                break;
            case "trending":
                params.put("ordering", "-popularity");
                txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                img_sort_price.setVisibility(View.GONE);
                txt_sort_price.setText("PRICE");
                txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));

                txt_sort_trending.setSelected(true);
                txt_sort_latest.setSelected(false);
                txt_sort_price.setSelected(false);
                UserInfo.getInstance(getActivity()).setDefaultSortPref("-popularity");
                break;
            case "price":
                if (!txt_sort_price.isSelected()) {
                    params.put("ordering", "price");
                    txt_sort_price.setSelected(true);
                    txt_sort_price.setText("PRICE");
                    img_sort_price.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_price_high_to_low_24dp));
                    txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    UserInfo.getInstance(getActivity()).setDefaultSortPref("price");
                } else {
                    params.put("ordering", "-price");
                    txt_sort_price.setSelected(false);
                    txt_sort_price.setText("PRICE");
                    img_sort_price.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_price_low_to_high_24dp));
                    txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    UserInfo.getInstance(getActivity()).setDefaultSortPref("-price");
                }
                txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));

                txt_sort_trending.setSelected(false);
                txt_sort_latest.setSelected(false);
                break;


        }
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
        lastFirstVisiblePosition = 0;
        showCatalogs(selectCloneHashMap());
    }

    private void openFilterBottom(final String filtertype) {
        try {
            FilterBottomDialog filterBottomDialog = null;
            Bundle bundle = new Bundle();
            bundle.putString("type", filtertype);
            if (isSearchFilter) {
                bundle.putBoolean("is_product_type_both", true);
            }
            if (paramsClone != null) {
                if (filtertype.equalsIgnoreCase("collection_type")) {
                    if (paramsClone.get("collection").equalsIgnoreCase("false"))
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
                        if (filtertype.equalsIgnoreCase("product_type")) {
                            if (check != null && check.equalsIgnoreCase("catalog")) {
                                isNonCatalog = false;
                            } else if (check != null && check.contains("noncatalog")) {
                                isNonCatalog = true;
                            }
                        }
                        if (filtertype.equalsIgnoreCase("collection_type")) {
                            if (check != null && check.equalsIgnoreCase(Constants.COLLECTION_TYPE_PRODUCT)) {
                                isSinglePcView = true;
                            } else {
                                isSinglePcView = false;
                            }
                        }

                        if (paramsClone != null) {
                            paramsClone.put("view_type", "public");
                            paramsClone.put("limit", String.valueOf(LIMIT));
                            paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
                            if (check.isEmpty()) {
                                paramsClone.remove(filtertype);
                            } else {
                                paramsClone.put(filtertype, check);
                            }

                            setCatalogTypeBoolean("isFilter", true);
                            lastFirstVisiblePosition = 0;
                            showCatalogs(selectCloneHashMap());
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("view_type", "public");
                            params.put("limit", String.valueOf(LIMIT));
                            params.put("offset", String.valueOf(INITIAL_OFFSET));
                            paramsClone = params;
                            if (check.isEmpty()) {
                                paramsClone.remove(filtertype);
                            } else {
                                paramsClone.put(filtertype, check);
                            }
                            setCatalogTypeBoolean("isFilter", true);
                            lastFirstVisiblePosition = 0;
                            showCatalogs(selectCloneHashMap());
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openSavedFilterBottom() {
        SavedFilterBottomDialog savedFilterBottomDialog = null;
        if (responseSavedFilters != null) {
            savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(responseSavedFilters.getId(), isNonCatalog);
        } else {
            savedFilterBottomDialog = SavedFilterBottomDialog.newInstance(null, isNonCatalog);
        }
        savedFilterBottomDialog.setTargetFragment(this, 1);
        savedFilterBottomDialog.show(getFragmentManager(), savedFilterBottomDialog.getTag());
        savedFilterBottomDialog.setSavedFilterSelectListener(Fragment_BrowseProduct.this);
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
                showCatalogs(selectCloneHashMap());
            } else {
                // To Do Task
                if (!search_view.getQuery().toString().trim().isEmpty()) {
                    params.put("view_type", "public");
                    params.put("limit", String.valueOf(LIMIT));
                    params.put("offset", String.valueOf(INITIAL_OFFSET));
                    params.put("q", search_view.getQuery().toString().trim());
                    searchHashMap = params;
                    showCatalogs(params);
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
                if (paramsClone != null) {
                    if (paramsClone.containsKey("offset")) {
                        paramsClone.put("offset", "0");
                    }
                }
                lastFirstVisiblePosition = 0;
                initCall(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }


    @Override
    public void onSavedChecked(String check) {

    }


    public void changeCatalog() {
        if (paramsClone != null) {
            if (paramsClone.containsKey("offset")) {
                paramsClone.put("offset", "0");
            }
        }
        if (getArguments() != null && getArguments().getString("searchfilter") != null) {
            setCatalogTypeBoolean("isSearchFilter", true);
            searchFilter = getArguments().getString("searchfilter");
            toolbar.setTitle(searchFilter);
            Log.e(TAG, "changeCatalog: Invalidate OptionM?enu");
            getActivity().invalidateOptionsMenu();
        }
        initCall(false);
    }


    public void changeViewType(String layout_type) {
        this.layout_type = layout_type;
        if (layout_type == Constants.PRODUCT_VIEW_LIST) {
            // List View
            try {
                if (mRecyclerView.getLayoutManager() != null) {
                    if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        lastFirstVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    } else {
                        lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            adapter.setCollectionTypeProduct(isSinglePcView);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(adapter);
            adapter.setProductBottomBarListener(this);
            mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        } else {
            // Grid View
            if (mRecyclerView.getLayoutManager() != null) {
                if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    lastFirstVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                } else {
                    lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                }
            }


            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs, layout_type);
            adapter.setCollectionTypeProduct(isSinglePcView);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(adapter);
            adapter.setProductBottomBarListener(this);
            mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        }
        bindAgainPaginate();
    }


    public HashMap<String, String> selectCloneHashMap() {
        return paramsClone;
    }


    public ResponseSavedFilters selectSavedCatalog() {
        return responseSavedFilters;
    }


    public void setCatalogTypeBoolean(String type, boolean value) {
        switch (type) {
            case "isFilter":
                isFilter = value;
                break;
            case "isSearchFilter":
                isSearchFilter = value;
                break;
            case "isPreorder":
                isPreCatalog = value;
                break;
            case "isReadyCatalog":
                isReadyCatalog = value;
                break;
        }
    }

    public void bindAgainPaginate() {
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(4)
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

            if (param.containsKey("generic")) {
                param.remove("generic");
            }

            if (param.containsKey("page_title")) {
                param.remove("page_title");
            }

            if (param.containsKey("page")) {
                param.remove("page");
            }
        }

        return param;
    }


    public void setFilterBar(HashMap<String, String> params) {
        if (params.containsKey("category")) {
            if (PrefDatabaseUtils.getCategory(getActivity()) != null) {
                txt_filter_category.setText(getCategoryFromId(params));
            } else {
                txt_filter_category.setText("Select");
            }
        } else {
            txt_filter_category.setText("Select");
        }
        if (params.containsKey("product_type")) {
            if (params.get("product_type") != null && params.get("product_type").toString().equalsIgnoreCase("catalog")) {
                txt_filter_catalog.setText("Catalog");
            } else if (params.get("product_type") != null && params.get("product_type").toString().contains("noncatalog")) {
                txt_filter_catalog.setText("Non-Catalog");
            }
        } else {
            if(isSearchFilter) {
                txt_filter_catalog.setText("Both");
            } else {
                txt_filter_catalog.setText("Catalog");
            }
        }


        if (params.containsKey("collection") && params.get("collection").equalsIgnoreCase("false")) {
            txt_collection_type_value.setText("Single Pcs");
            txt_filter_availability.setText("Single Pcs.");
            disableAvailabilityFilterUI();
        } else {
            enableAvailabilityFilterUI();
            txt_collection_type_value.setText("Collection");
            if (params.containsKey("sell_full_catalog")) {
                String full_catalog = params.get("sell_full_catalog");
                if (full_catalog.equalsIgnoreCase("true")) {
                    txt_filter_availability.setText("Full Catalog");
                } else {
                    txt_filter_availability.setText("Single Pcs.");
                }
            } else {
                txt_filter_availability.setText("Both");
            }
        }


        setFilterBadge(params);
    }

    public void setFilterBadge(HashMap<String, String> params) {
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
                if (params.get("ordering").equals("-id")) {
                    txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                    txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                    txt_sort_price.setText("PRICE");
                    img_sort_price.setVisibility(View.GONE);
                } else if (params.get("ordering").equals("price") || params.get("ordering").equals("-price")) {
                    txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                    txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    if (params.get("ordering").equals("price")) {
                        // Low To High
                        txt_sort_price.setText("PRICE");
                        img_sort_price.setVisibility(View.VISIBLE);
                        img_sort_price.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_price_high_to_low_24dp));
                    } else {
                        // High to Low
                        txt_sort_price.setText("PRICE");
                        img_sort_price.setVisibility(View.VISIBLE);
                        img_sort_price.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_price_low_to_high_24dp));
                    }
                    txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                } else if (params.get("ordering").equals("-popularity")) {
                    txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                    txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                    txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    txt_sort_price.setText("PRICE");
                    img_sort_price.setVisibility(View.GONE);
                }
            } else {
                txt_sort_latest.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                txt_sort_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                txt_sort_price.setText("PRICE");
                img_sort_price.setVisibility(View.GONE);
                txt_sort_trending.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
            }
        }

        badge_filter_count.setBadgeCount(filtercount, true);
    }

    private void getCategory() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                        if (ct != null) {
                            if (ct.length > 0) {
                                ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                                for (int i = 0; i < ct.length; i++) {
                                    enumGroupResponses.add(new EnumGroupResponse(String.valueOf(ct[i].getId()), ct[i].getcategory_name()));
                                }
                                PrefDatabaseUtils.setCategory(getActivity(), Application_Singleton.gson.toJson(enumGroupResponses));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                // StaticFunctions.hideProgressbar(getActivity());
            }
        });
    }

    private void getCategoryDeepLink(String category_id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HashMap<String, String> param = new HashMap<>();
        param.put("visible_on", "ProductList");
        param.put("category", category_id);
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
                            linear_promotional_category.setVisibility(View.VISIBLE);
                            recycler_view_category.setVisibility(View.VISIBLE);
                            ArrayList<Response_Promotion> dePromotions = new ArrayList<Response_Promotion>(Arrays.asList(deepLinkPromotion));
                            PromotionalProductTabAdapter promotionalProductTabAdapter = new PromotionalProductTabAdapter(getActivity(), dePromotions, PromotionalProductTabAdapter.DEEPLINKPRODUCT);
                            recycler_view_category.setAdapter(promotionalProductTabAdapter);
                        } else {
                            linear_promotional_category.setVisibility(View.GONE);
                            recycler_view_category.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                linear_promotional_category.setVisibility(View.GONE);
                recycler_view_category.setVisibility(View.GONE);
            }
        });
    }

    private String getCategoryFromId(HashMap<String, String> params) {
        ArrayList<EnumGroupResponse> data = new Gson().fromJson(PrefDatabaseUtils.getCategory(getActivity()), new TypeToken<ArrayList<EnumGroupResponse>>() {
        }.getType());
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equalsIgnoreCase(params.get("category"))) {
                return data.get(i).getValue();
            }
        }
        return "";
    }

    public void enableAvailabilityFilterUI() {
        linear_availability.setClickable(true);
        txt_availability_label.setTextColor(ContextCompat.getColor(getContext(), R.color.purchase_medium_gray));
        txt_filter_availability.setTextColor(ContextCompat.getColor(getContext(), R.color.purchase_dark_gray));
    }

    public void disableAvailabilityFilterUI() {
        linear_availability.setClickable(false);
        txt_availability_label.setTextColor(ContextCompat.getColor(getContext(), R.color.purchase_light_gray));
        txt_filter_availability.setTextColor(ContextCompat.getColor(getContext(), R.color.purchase_light_gray));
    }


    @Override
    public void showHide(final ArrayList<Response_catalogMini> response_catalogMiniArrayList) {
        if (isSinglePcView && response_catalogMiniArrayList.size() > 0) {
            linear_multiselect_bar.setVisibility(View.VISIBLE);
            txt_select_none.setVisibility(View.VISIBLE);
            txt_select_none.setPaintFlags(txt_select_none.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txt_select_none.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter != null) {
                        adapter.seleResponse_catalogMiniArrayList.clear();
                        adapter.notifyDataSetChanged();
                        showHide(adapter.seleResponse_catalogMiniArrayList);
                    }
                }
            });
            final ViewMultipleSelectionBottomBar viewMultipleSelectionBottomBar = new ViewMultipleSelectionBottomBar(getActivity(), response_catalogMiniArrayList, Fragment_BrowseProduct.this);
            txt_other_share_multiple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewMultipleSelectionBottomBar.otherShare();

                }
            });

            linear_whatsapp_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewMultipleSelectionBottomBar.whatsappShare();
                }
            });

            txt_add_to_cart_multiple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewMultipleSelectionBottomBar.addToCart();
                }
            });

            viewMultipleSelectionBottomBar.setTaskCompleteListener(new ViewMultipleSelectionBottomBar.TaskCompleteListener() {
                @Override
                public void onSuccess() {
                    if (adapter != null) {
                        adapter.seleResponse_catalogMiniArrayList.clear();
                        adapter.notifyDataSetChanged();
                        showHide(adapter.seleResponse_catalogMiniArrayList);
                    }
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccessAddToCart(CartProductModel response) {
                    if (adapter != null) {
                        adapter.seleResponse_catalogMiniArrayList.clear();
                        adapter.notifyDataSetChanged();
                        showHide(adapter.seleResponse_catalogMiniArrayList);
                        Toast.makeText(getContext(), "Products Added to cart", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof OpenContainer) {
                            cartToolbarUpdate();
                            if (menu != null) {
                                MenuItem menuItem = menu.findItem(R.id.action_cart);
                                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
                                if (menuItem != null)
                                    menuItem.getActionView().startAnimation(shake);
                            }

                        }
                    }

                }
            });
        } else {
            linear_multiselect_bar.setVisibility(View.GONE);
            txt_select_none.setVisibility(View.GONE);
        }
    }
}
