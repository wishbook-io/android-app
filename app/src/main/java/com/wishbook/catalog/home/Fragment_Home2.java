package com.wishbook.catalog.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.facebook.common.internal.ImmutableList;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jay.widget.StickyHeadersGridLayoutManager;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AllSectionAdapter;
import com.wishbook.catalog.commonadapters.CommonAnalyticsAdapter;
import com.wishbook.catalog.commonadapters.HomeSupplierSuggestionRatingAdapter;
import com.wishbook.catalog.commonadapters.HomeWishListAdapter;
import com.wishbook.catalog.commonadapters.SuggestionContactsAdapter;
import com.wishbook.catalog.commonmodels.AllDataModel;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.KYC_model;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.PrefStoryCompletion;
import com.wishbook.catalog.commonmodels.RequestCreditRating;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.wishbook.catalog.commonmodels.ResponseStoryModel;
import com.wishbook.catalog.commonmodels.SummaryModel;
import com.wishbook.catalog.commonmodels.Summary_Sub;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.commonmodels.responses.HomeBanner;
import com.wishbook.catalog.commonmodels.responses.HomeWrapperModel;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;
import com.wishbook.catalog.commonmodels.responses.ResponseCouponList;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_DashBoard;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.UserStats;
import com.wishbook.catalog.home.adapters.RecentViewedAdapter;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_WishList;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.home_groupie.HeaderItemDecoration;
import com.wishbook.catalog.home.home_groupie.InfiniteScrollListener;
import com.wishbook.catalog.home.home_groupie.StickyGroupAdapter;
import com.wishbook.catalog.home.home_groupie.StickyHeaderItem;
import com.wishbook.catalog.home.home_groupie.decoration.CarouselItemDecoration;
import com.wishbook.catalog.home.home_groupie.group.CarouselGroup;
import com.wishbook.catalog.home.home_groupie.view.BannerItem;
import com.wishbook.catalog.home.home_groupie.view.BrowseCatalogItem;
import com.wishbook.catalog.home.home_groupie.view.CarouselFilterItem;
import com.wishbook.catalog.home.home_groupie.view.CarouselProductWithSeeMoreItem;
import com.wishbook.catalog.home.home_groupie.view.CarouselWithBackGroundItem;
import com.wishbook.catalog.home.home_groupie.view.CouponCarouselItem;
import com.wishbook.catalog.home.home_groupie.view.EmptyItem;
import com.wishbook.catalog.home.home_groupie.view.FooterSpaceItem;
import com.wishbook.catalog.home.home_groupie.view.HeaderGradientItem;
import com.wishbook.catalog.home.home_groupie.view.HeaderItem;
import com.wishbook.catalog.home.home_groupie.view.HomeCategoryItem;
import com.wishbook.catalog.home.home_groupie.view.HomeWishListItem;
import com.wishbook.catalog.home.home_groupie.view.LoadingItem;
import com.wishbook.catalog.home.home_groupie.view.ProductItemAdapter;
import com.wishbook.catalog.home.home_groupie.view.PublicBrandCarouselItem;
import com.wishbook.catalog.home.home_groupie.view.RotateBannerItem;
import com.wishbook.catalog.home.home_groupie.view.VideoFeedBackItem;
import com.wishbook.catalog.home.home_groupie.view.WishBookStoryItem;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.notifications.Fragment_Notifications;
import com.wishbook.catalog.home.notifications.models.NotificationModel;
import com.wishbook.catalog.services.LocalService;
import com.wishbook.catalog.stories.StoryActivity;
import com.wishbook.catalog.stories.adapter.HomeStoriesAdapter;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.in.moneysmart.common.MoneySmartInit;
import co.in.moneysmart.common.util.PermissionsCheckHelper;
import rx.Subscriber;

public class Fragment_Home2 extends GATrackedFragment {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private TextView txt_home_search;

    ArrayList<CarouselGroup> storyGroup;
    GroupAdapter storyGroupAdapter;
    Section storySection;
    Group loadingGroup;

    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Handler handler;


    private StickyGroupAdapter groupAdapter;
    private GridLayoutManager layoutManager;
    private Section infiniteLoadingSection;

    private View view;
    private int gray;
    private int betweenPadding;
    private int transparent;
    int page = 1;
    boolean isEndOfPageDetect;
    boolean flag_recent = false;

    public static final String INSET_TYPE_KEY = "inset_type";
    public static final String FULL_BLEED = "full_bleed";
    public static final String INSET = "inset";

    private static String TAG = Fragment_Home2.class.getSimpleName();
    ArrayList<HomeWrapperModel> homeWrapperModelArrayList;
    StickyHeaderItem home_sticky_filter;


    private ArrayList<SummaryModel> data = new ArrayList<>();
    private ArrayList<Summary_Sub> contactsData = new ArrayList<>();
    private ArrayList<Summary_Sub> catalogsData = new ArrayList<>();
    private ArrayList<Summary_Sub> brands = new ArrayList<>();


    Response_BuyerGroupType[] responseBuyerGroupTypes;


    LinearLayout recent_catalog_heading;
    private SharedPreferences pref;
    Dialog_CompanyType companyType = null;
    private UserInfo userinfo;
    private List<MyContacts> contactList = new ArrayList<>();
    private List<MyContacts> wishbookcontactList = new ArrayList<>();
    private ArrayList<MyContacts> supplierSuggestionList = new ArrayList<>();
    HashMap<String, String> paramsClone = null;
    HashMap<Integer, ArrayList<Group>> page_group = null;


    //added

    @BindView(R.id.appbar)
    public Toolbar toolbar;

    RecyclerView recycler_view_recent;


    RecyclerView recycler_view_wishlist;

    RecyclerView recycler_view_non_catalog;

    ActionBarDrawerToggle mDrawerToggle;


    private Menu menu;
    private Subscriber<Integer> subscriber;

    HomeWishListAdapter homeWishListAdapter;
    HomeStoriesAdapter homeStoriesAdapter;
    ArrayList<ResponseStoryModel> storyModels;

    // MoneySmart Need Flags,Variable
    private static int ASK_BACKGROUD_PROCESS = 500;
    private static int ASK_SERVICE_SMS = 600;
    private boolean isRequiredAutoStart, isRequiredServiceSms;
    private SharedPreferences moneySmart;


    Handler company_rating_handler;
    Runnable company_rating_runnable;

    int wishlist_section_position;


    LinearLayoutManager linearLayoutManagerHorizontal;
    // MoneySmart End


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_groupie, ga_container, true);
        flag_recent = false;
        ButterKnife.bind(this, view);
        initTopHomeSearch(view);
        initOtherComponentListener();
        setHasOptionsMenu(true);
        data = new ArrayList<>();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setDrwaerToggle();
        toolbar.setTitle("Wishbook");
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        gray = ContextCompat.getColor(getContext(), R.color.material_bg);
        transparent = ContextCompat.getColor(getActivity(), R.color.transparent);
        betweenPadding = getResources().getDimensionPixelSize(R.dimen.home_padding);
        groupAdapter = new StickyGroupAdapter();
        groupAdapter.setSpanCount(12);

        layoutManager = new StickyHeadersGridLayoutManager<StickyGroupAdapter>(getActivity(), groupAdapter.getSpanCount());
        layoutManager.setSpanSizeLookup(groupAdapter.getSpanSizeLookup());


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new HeaderItemDecoration(gray, betweenPadding));
        //mRecyclerView.addItemDecoration(new InsetItemDecoration(gray, betweenPadding));
        mRecyclerView.setAdapter(groupAdapter);

        mRecyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.e(TAG, "onLoadMore: Current Page " + page);
                if (!hasLoadedAllItems) {
                    if (!Loading) {
                        if (loadingGroup == null) {
                            loadingGroup = new LoadingItem(getActivity(), Fragment_Home2.this);
                        }
                        groupAdapter.add(loadingGroup);
                        Loading = true;
                        getHomeData((page + 1), false);
                    }
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && Activity_Home.support_chat_fab.getVisibility() == View.VISIBLE) {
                    Activity_Home.support_chat_fab.hide();
                } else if (dy < 0 && Activity_Home.support_chat_fab.getVisibility() != View.VISIBLE) {
                    Activity_Home.support_chat_fab.show();
                }
            }
        });


        try {
            getActivity().startService(new Intent(getActivity(), LocalService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGroupsType();
        get15DaysCronStockCheck();
        getGST();
        getBankDetails();
        getUserStatstic();
        StaticFunctions.getAllCODVerficationPending(getActivity());
        getCartData((AppCompatActivity) getActivity());
        getHomeData(page, false);
        return view;
    }

    public void initTopHomeSearch(View view) {
        txt_home_search = view.findViewById(R.id.txt_home_search);
        txt_home_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WishbookEvent wishbookEvent = new WishbookEvent();
                wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
                wishbookEvent.setEvent_names("Products_Search_screen");
                HashMap<String, String> prop = new HashMap<>();
                prop.put("source", "search bar");
                wishbookEvent.setEvent_properties(prop);
                new WishbookTracker(getActivity(), wishbookEvent);


                Intent intent = new Intent(getContext(), SearchActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(),
                                txt_home_search,
                                ViewCompat.getTransitionName(txt_home_search));
                startActivity(intent, options.toBundle());
            }
        });

    }

    public void initOtherComponentListener() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            company_rating_runnable = new Runnable() {
                @Override
                public void run() {
                    getCompanyRating();
                }
            };
            handler = new Handler();
            handler.postDelayed(company_rating_runnable, PermissionsCheckHelper.DELAY_TO_CHECK_IF_ACTIVITY_EXISTS);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment_Summary", "onResume: Start");
        Activity_Home.support_chat_fab.show();

        if (menu != null) {
            setApplozicMenu();
        }
        registerUnReadCountReceiver();


        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {

            getManufactureDashBoard();


        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {


            //Retailer
            getDashBoard(null);


        } else {
            //Wholesaler
            callDashboardWholesaler();
        }

        getWishListProductList();
        try {
            if (homeStoriesAdapter != null && storyModels.size() > 0) {
                if (PrefDatabaseUtils.getPrefStoryCompletion(getActivity()) != null) {
                    ArrayList<PrefStoryCompletion> responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefStoryCompletion(getActivity()), new TypeToken<ArrayList<PrefStoryCompletion>>() {
                    }.getType());
                    for (int i = 0; i < storyModels.size(); i++) {
                        for (int j = 0; j < responseData.size(); j++) {
                            if (storyModels.get(i).getId().equals(responseData.get(j).getStory_id())) {
                                storyModels.get(i).setCompletion_count(responseData.get(j).getCompletion_count());
                            }
                        }
                    }
                    homeStoriesAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
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

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (getActivity() instanceof Activity_Home) {
            ((Activity_Home) getActivity()).setMenuAfterCompany();
        }

        toolbar.setTitle("Wishbook");

    }


    private void startNotificationService() {
        if (isAdded() && !isDetached()) {
            final ArrayList<NotificationModel> allnotifications = getUnreadNotificationList();
            if (allnotifications != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (allnotifications.size() == 0) {
                                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), badgeStyleTransparent, "");

                            } else {
                                ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), ActionItemBadge.BadgeStyles.RED, allnotifications.size());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

            } else {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), Application_Singleton.getCurrentActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), badgeStyleTransparent, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            subscriber = new Subscriber<Integer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(final Integer integer) {
                    try {
                        //onResume();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAdded() && !isDetached()) {
                                    ArrayList<NotificationModel> allnotifications = getUnreadNotificationList();
                                    try {
                                        if (allnotifications != null) {
                                            if (allnotifications.size() == 0) {
                                                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                                ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), badgeStyleTransparent, "");
                                            } else {
                                                ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), ActionItemBadge.BadgeStyles.RED, allnotifications.size());
                                            }


                                        } else {
                                            BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                            ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), badgeStyleTransparent, "");

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                        ActionItemBadge.update(getActivity(), toolbar.getMenu().findItem(R.id.action_notification), getActivity().getResources().getDrawable(R.drawable.ic_notifications_24dp), badgeStyleTransparent, "");

                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            LocalService.notificationCounter.subscribe(subscriber);

        }
    }

    public ArrayList<NotificationModel> getUnreadNotificationList() {
        if (getActivity() != null && PrefDatabaseUtils.getGCMNotificationData(getActivity()) != null) {
            ArrayList<NotificationModel> data = new Gson().fromJson(PrefDatabaseUtils.getGCMNotificationData(getActivity()), new TypeToken<ArrayList<NotificationModel>>() {
            }.getType());
            ArrayList<NotificationModel> unreadData = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getRead() != null && !data.get(i).getRead()) {
                    // get All Notification that is not read by user
                    unreadData.add(data.get(i));
                }
            }
            return unreadData;

        } else {
            ArrayList<NotificationModel> data = new ArrayList<>();
            return data;
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onResume: Start");
        super.onDestroyView();
        if (company_rating_handler != null && company_rating_runnable != null) {
            company_rating_handler.removeCallbacks(company_rating_runnable);
        }
        unregisterUnReadCountReceiver();
    }

    // Region #################  Home Page Other API Calling Start ################  //


    private void getDashBoard(final Response_DashBoard response_seller) {
        // showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "buyer_dashboard", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    data.clear();
                    brands = new ArrayList<>();
                    if (isAdded() && !isDetached()) {


                        userinfo = UserInfo.getInstance(getActivity());
                        String Status = "";
                        Response_DashBoard response_dashBoards = new Gson().fromJson(response, Response_DashBoard.class);


                        // Log.d("brand",response_dashBoards.getProfile().toString());
                        if (userinfo.getGroupstatus().equals("1")) {
                            JSONObject dashboard = null, profile = null;
                            String company_type_filled = null;
                            Boolean is_profile_set = null;
                            String kyc_gstin = null;
                            try {
                                dashboard = new JSONObject(response);
                                profile = dashboard.getJSONObject("profile");
                                company_type_filled = profile.getString("company_type_filled");
                                is_profile_set = profile.getBoolean("is_profile_set");
                                kyc_gstin = profile.getString("kyc_gstin");
                                if (!Application_Singleton.isChangeCreditScrore) {
                                    if (profile.has("credit_rating_score")) {
                                        UserInfo.getInstance(getActivity()).setCreditScore(profile.getString("credit_rating_score"));
                                    }
                                } else {
                                    Log.e(TAG, "onServerResponse: First Time----->" + Application_Singleton.isAskedFirstTimeAfterApply + "\n Credit Rating Apply");
                                    if (UserInfo.getInstance(getActivity()).isCreditRatingApply() && !Application_Singleton.isAskedFirstTimeAfterApply) {
                                        initMoneySmart();
                                    }
                                }


                                Activity_Home.pref.edit().putString("entered_gst", kyc_gstin).apply();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                                if (profile.has("brand")) {
                                    brands.add(new Summary_Sub("no brands", new Fragment_AddBrand(), "Add Brand"));
                                    data.add(new SummaryModel("Brands", brands));

                                }

                                //CATALOGS
                                if (response_seller != null && response_seller.getCatalogs() != null && response_seller.getCatalogs().getUploaded_catalog() != null) {
                                    catalogsData = new ArrayList<>();
                                    String uploadedcatalogs = response_seller.getCatalogs().getUploaded_catalog().equals("0") ? "no" : response_seller.getCatalogs().getUploaded_catalog();
                                    catalogsData.add(new Summary_Sub(uploadedcatalogs + " enable", new Fragment_AddCatalog(), "Add Catalog"));
                                    data.add(new SummaryModel("Products", catalogsData));
                                }
                            }
                        }

                        Log.d("companytype", UserInfo.getInstance(getActivity()).getCompanyType());

                        if (!UserInfo.getInstance(getActivity()).isGuest()) {


                            //CONTACTS SUGGESTION
                            wishbookcontactList.clear();
                            supplierSuggestionList = new ArrayList<>();
                            Type type = new TypeToken<ArrayList<MyContacts>>() {
                            }.getType();
                            ArrayList<MyContacts> localList = UserInfo.getInstance(getActivity()).getwishSuggestioncontacts();
                            if (localList != null) {
                                for (MyContacts temp : localList) {
                                    if (temp != null
                                            && temp.getType() != null
                                            && temp.getType().equals("supplier")
                                            && temp.getIs_visible()
                                            && temp.getCredit_reference_id() == null
                                            && temp.getCompany_id() != null
                                            && !temp.getCompany_id().equals(UserInfo.getInstance(getActivity()).getCompany_id())) {
                                        supplierSuggestionList.add(temp);
                                    }
                                }
                            }

                            for (MyContacts contacts1 : localList) {
                                if (contacts1 != null && contacts1.getIs_visible()
                                        && contacts1.getConnected_as() != null
                                        && !contacts1.getConnected_as().equals("supplier")
                                        && !contacts1.getConnected_as().equals("buyer")
                                        && contacts1.getType() != null
                                        && !contacts1.getType().equals("supplier")
                                ) {
                                    wishbookcontactList.add(contacts1);
                                }
                            }

                            final List<String> displayOrder = ImmutableList.of("broker", "buyer", "supplier", "");

                            Collections.sort(wishbookcontactList, new Comparator<MyContacts>() {
                                @Override
                                public int compare(MyContacts myContacts, MyContacts t1) {
                                    if (myContacts != null && myContacts.getType() == null && t1.getType() == null) {
                                        return 0;
                                    } else {
                                        return Integer.valueOf(
                                                displayOrder.indexOf(myContacts.getType()))
                                                .compareTo(
                                                        Integer.valueOf(
                                                                displayOrder.indexOf(t1.getType())));
                                    }
                                }
                            });
                            contactsData = new ArrayList<Summary_Sub>();
                            ArrayList<Summary_Sub> temp1 = new ArrayList<>();
                            SuggestionContactsAdapter suggestionAdapter = new SuggestionContactsAdapter((AppCompatActivity) getActivity(), wishbookcontactList, responseBuyerGroupTypes);
                            contactsData.add(0, new Summary_Sub(suggestionAdapter));

                            if (wishbookcontactList.size() > 0) {
                                data.add(0, new SummaryModel("Contacts", contactsData));

                            }
                            if (supplierSuggestionList.size() > 0) {
                                HomeSupplierSuggestionRatingAdapter homeSupplierSuggestionRatingAdapter = new HomeSupplierSuggestionRatingAdapter(getActivity(), supplierSuggestionList, Fragment_Home2.this);
                                temp1.add(0, new Summary_Sub(homeSupplierSuggestionRatingAdapter));
                                data.add(new SummaryModel("Request feedback & improve credit rating", temp1));
                            }

                        }

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

    private void callDashboardWholesaler() {
        synchronized (this) {
            getManufactureDashBoard();
        }
    }

    private void getManufactureDashBoard() {
        // showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "seller_dashboard", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                //hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    // hideProgress();
                    Log.v("sync response", response);
                    data.clear();
                    brands = new ArrayList<>();
                    if (isAdded() && !isDetached()) {

                        userinfo = UserInfo.getInstance(getActivity());
                        String Status = "";
                        Response_DashBoard response_dashBoards_seller = new Gson().fromJson(response, Response_DashBoard.class);

                        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                            try {
                                JSONObject dashboard = null, profile = null;
                                dashboard = new JSONObject(response);
                                profile = dashboard.getJSONObject("profile");
                                if (profile != null) {
                                    if (profile.has("company_seller_detail")) {
                                        String seller_approval_status = profile.getString("company_seller_detail");
                                        if (seller_approval_status != null && !seller_approval_status.isEmpty() && !seller_approval_status.equals("null"))
                                            UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(seller_approval_status);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            getDashBoard(response_dashBoards_seller);
                            return;
                        }
                        Log.d("summray", response);
                        Log.d("profile", response_dashBoards_seller.getProfile().toString());


                        if (userinfo.getGroupstatus().equals("1")) {
                            JSONObject dashboard = null, profile = null;
                            String company_type_filled = null;
                            Boolean is_profile_set = null;
                            String kyc_gstin = null;
                            try {
                                dashboard = new JSONObject(response);
                                profile = dashboard.getJSONObject("profile");
                                company_type_filled = profile.getString("company_type_filled");
                                is_profile_set = profile.getBoolean("is_profile_set");
                                kyc_gstin = profile.getString("kyc_gstin");

                                if (profile != null) {
                                    if (profile.has("company_seller_detail")) {
                                        String seller_approval_status = profile.getString("company_seller_detail");
                                        if (seller_approval_status != null && !seller_approval_status.isEmpty() && !seller_approval_status.equals("null"))
                                            UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(seller_approval_status);
                                    }
                                }
                                Activity_Home.pref = StaticFunctions.getAppSharedPreferences(getActivity());
                                Activity_Home.pref.edit().putString("entered_gst", kyc_gstin).apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (profile.has("brand")) {
                                brands.add(new Summary_Sub("no brands", new Fragment_AddBrand(), "Add Brand"));
                                data.add(new SummaryModel("Brands", brands));

                            }


                            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                                // Show Only For Manufacturer

                                /**
                                 * Changed by Gaurav Sir 20-sep
                                 */
                               /* if (response_dashBoards_seller.getCatalogs().getMy_catalog_total_views() != null) {
                                    // show analytics (Last 7 days view)
                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getCatalogs().getMy_catalog_total_views());
                                    analyticsData.setAnalytics_label(getResources().getString(R.string.view_all_catalogs));


                                    ArrayList<Summary_Sub> summary_subs = new ArrayList<Summary_Sub>();
                                    summary_subs.add(analyticsData);
                                    CommonAnalyticsAdapter commonAnalyticsAdapter = new CommonAnalyticsAdapter(getActivity(), summary_subs, CommonAnalyticsAdapter.LEFTANALYTICS);

                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(commonAnalyticsAdapter));
                                    data.add(new SummaryModel("Analytics", summary_subs1));

                                }*/


                                if (response_dashBoards_seller.getCatalogs().getMy_most_viewed_catalogs() != null
                                        && response_dashBoards_seller.getCatalogs().getMy_most_viewed_catalogs().size() > 0) {
                                    // show Catalog List (Most Viewed Catalog)
                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getCatalogs().getMy_catalog_total_views());
                                    analyticsData.setAnalytics_label(getResources().getString(R.string.view_all_catalogs));


                                    ArrayList<Summary_Sub> summary_subs = new ArrayList<Summary_Sub>();
                                    summary_subs.add(analyticsData);


                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("view_type", "mycatalogs");
                                    params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                    params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                    params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                                    params.put("most_viewed", "true");
                                    List<AllDataModel> list = new ArrayList<AllDataModel>();
                                    list.add(new AllDataModel("Your Most Viewed Product", response_dashBoards_seller.getCatalogs().getMy_most_viewed_catalogs(), new Fragment_Catalogs(), 3, params));
                                    ;
                                    AllSectionAdapter allSectionAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), list);
                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(allSectionAdapter));
                                    data.add(new SummaryModel("Analytics", summary_subs1));

                                }


                                if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                                    //CATALOGS
                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getCatalogs().getUploaded_catalog());


                                    ArrayList<Summary_Sub> summary_subs = new ArrayList<Summary_Sub>();
                                    summary_subs.add(analyticsData);
                                    CommonAnalyticsAdapter commonAnalyticsAdapter = new CommonAnalyticsAdapter(getActivity(), summary_subs, CommonAnalyticsAdapter.CATALOGANALYTICS);

                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(commonAnalyticsAdapter));
                                    data.add(new SummaryModel("My Product", summary_subs1));


                                }


                                /**
                                 * Changed by Gaurav Sir 20-sep
                                 */
                            /*    if (response_dashBoards_seller.getCatalogs().getLastest_catalog() != null) {

                                    // Show Latest Catalog Viewd

                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getTotal_followers());
                                    analyticsData.setAnalytics_label(getResources().getString(R.string.total_followers));


                                    ArrayList<CatalogInfo.Lastest_catalog> summary_subs = new ArrayList<CatalogInfo.Lastest_catalog>();
                                    summary_subs.add(response_dashBoards_seller.getCatalogs().getLastest_catalog());
                                    CommonAnalyticsAdapter commonAnalyticsAdapter = new CommonAnalyticsAdapter(getActivity(), summary_subs, CommonAnalyticsAdapter.LATESTANALYTICS);

                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(commonAnalyticsAdapter));
                                    data.add(new SummaryModel("Analytics", summary_subs1));

                                }*/


                                if (response_dashBoards_seller.getCatalogs().getCatalogs_under_most_viewed() != null) {

                                    // show analytics data (Catalog Under Top 10)
                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getCatalogs().getCatalogs_under_most_viewed());
                                    analyticsData.setAnalytics_label(getResources().getString(R.string.top_most_catalog_7days));
                                    analyticsData.setRedirectorText(CommonAnalyticsAdapter.TOPCATALOG);


                                    ArrayList<Summary_Sub> summary_subs = new ArrayList<Summary_Sub>();
                                    summary_subs.add(analyticsData);
                                    CommonAnalyticsAdapter commonAnalyticsAdapter = new CommonAnalyticsAdapter(getActivity(), summary_subs, CommonAnalyticsAdapter.LEFTANALYTICS1);

                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(commonAnalyticsAdapter));
                                    data.add(new SummaryModel("Analytics", summary_subs1));

                                }


                                if (response_dashBoards_seller.getTotal_followers() != null) {

                                    // show Analytics data Total Followers
                                    Summary_Sub analyticsData = new Summary_Sub();
                                    analyticsData.setAnalytics_value(response_dashBoards_seller.getTotal_followers());
                                    analyticsData.setAnalytics_label(getResources().getString(R.string.total_followers));
                                    analyticsData.setRedirectorText(CommonAnalyticsAdapter.MYFOLLOWERS);


                                    ArrayList<Summary_Sub> summary_subs = new ArrayList<Summary_Sub>();
                                    summary_subs.add(analyticsData);
                                    CommonAnalyticsAdapter commonAnalyticsAdapter = new CommonAnalyticsAdapter(getActivity(), summary_subs, CommonAnalyticsAdapter.RIGHTANALYTICS);

                                    ArrayList<Summary_Sub> summary_subs1 = new ArrayList<Summary_Sub>();
                                    summary_subs1.add(new Summary_Sub(commonAnalyticsAdapter));
                                    data.add(new SummaryModel("Analytics", summary_subs1));

                                }
                            /*//Pending Products in Selection
                            String previousProds = Activity_Home.pref.getString("selectedProds", null);

                            Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
                            }.getType();


                            selection = new ArrayList<>();
                            if (previousProds != null) {
                                ArrayList<ProductObj> preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
                                selection.add(new Summary_Sub(preseletedprods.size() + " products", "View", new Fragment_ProductSelections(), "Selected Products"));
                                if (preseletedprods.size() > 0) {
                                    data.add(new SummaryModel("Selected Products", selection));
                                }
                            }*/
                    /*if (is_profile_set != null) {
                        if ((!is_profile_set && !Activity_Home.pref.getBoolean("is_profile_set", false) && !isCompanyPopUpShown) || (!company_type_filled.equals("true") && !Activity_Home.pref.getBoolean("is_profile_set", false) && !isCompanyPopUpShown)) {
                            companyType = new Dialog_CompanyType();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", company_type_filled);
                            companyType.show(getActivity().getSupportFragmentManager(), "Company Type");
                            isCompanyPopUpShown = true;
                        }
                    }*/

                   /* if (!(kyc_gstin != null && !kyc_gstin.equals("null"))) {
                        if (!Activity_Home.pref.getBoolean("kyc_gstin_popup", false) && !isKYCPopUpShown) {
                            isKYCPopUpShown = true;
                            showGSTPopup();
                        }

                    }*/

                                Log.d("companytype", UserInfo.getInstance(getActivity()).getCompanyType());

                                //CONTACTS SUGGESTION
                                wishbookcontactList.clear();
                                ArrayList<MyContacts> localList = UserInfo.getInstance(getActivity()).getwishSuggestioncontacts();

                                for (MyContacts contacts1 : localList) {
                                    if (contacts1.getIs_visible() && !contacts1.getConnected_as().equals("supplier") && !contacts1.getConnected_as().equals("buyer")) {
                                        wishbookcontactList.add(contacts1);
                                    }
                                }
                                final List<String> displayOrder = ImmutableList.of("broker", "buyer", "supplier", "");

                                Collections.sort(wishbookcontactList, new Comparator<MyContacts>() {
                                    @Override
                                    public int compare(MyContacts myContacts, MyContacts t1) {
                                        if (myContacts.getType() == null && t1.getType() == null) {
                                            return 0;
                                        } else {
                                            return Integer.valueOf(
                                                    displayOrder.indexOf(myContacts.getType()))
                                                    .compareTo(
                                                            Integer.valueOf(
                                                                    displayOrder.indexOf(t1.getType())));
                                        }
                                    }
                                });

                                contactsData = new ArrayList<Summary_Sub>();
                                SuggestionContactsAdapter suggestionAdapter = new SuggestionContactsAdapter((AppCompatActivity) getActivity(), wishbookcontactList, responseBuyerGroupTypes);
                                contactsData.add(0, new Summary_Sub(suggestionAdapter));
                                if (wishbookcontactList.size() > 0) {
                                    try {
                                        if (data.get(7).getHeaderTitle().equals("Contacts")) {
                                            data.remove(7);
                                            data.add(7, new SummaryModel("Contacts", contactsData));
                                        } else {
                                            data.add(7, new SummaryModel("Contacts", contactsData));
                                        }
                                    } catch (Exception e) {
                                        data.add(new SummaryModel("Contacts", contactsData));
                                    }
                                }

                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });


    }

    private void getGroupsType() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    if (isAdded() && !isDetached()) {
                        responseBuyerGroupTypes = Application_Singleton.gson.fromJson(response, Response_BuyerGroupType[].class);
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


    public void getUserStatstic() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.userUrl(getActivity(), "stats", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        UserStats userStats = Application_Singleton.gson.fromJson(response, UserStats.class);
                        UserInfo.getInstance(getActivity()).setUserStats(Application_Singleton.gson.toJson(userStats));
                        sendUserAttributes(getActivity());
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                recycler_view_wishlist.setVisibility(View.GONE);
            }
        });
    }


    public void getWishListProductList() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.userUrl(getActivity(), "wishlist-product", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ArrayList<String> wislistProducts = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<String>>() {
                    }.getType());
                    if (wislistProducts.size() > 0) {
                        PrefDatabaseUtils.setPrefWishlistProductData(getContext(), Application_Singleton.gson.toJson(wislistProducts));
                    }


                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    // ################# Home Page Other API Calling End ############################  //


    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (UserInfo.getInstance(getActivity()) != null) {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                menu.findItem(R.id.action_wishlist).setVisible(true);
                int wishcount = UserInfo.getInstance(getActivity()).getWishlistCount();
                if (wishcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                }
            } else {
                menu.findItem(R.id.action_wishlist).setVisible(false);
            }
        }
        if (UserInfo.getInstance(getActivity()) != null) {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
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
            } else {
                menu.findItem(R.id.cart).setVisible(false);
            }
        }
        menu.findItem(R.id.action_unread_applozic).setVisible(false);
        setApplozicMenu();
        menu.findItem(R.id.action_notification).setVisible(true);
    }


    // <editor-fold desc="OveerideMenu Setup">
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification, menu);
        this.menu = menu;
        menu.findItem(R.id.action_unread_applozic).setVisible(false);
        startNotificationService();
        setApplozicMenu();
        super.onCreateOptionsMenu(menu, inflater);

    }

    //</editor-fold>

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_notification) {
            Fragment_Notifications supplier = new Fragment_Notifications();
            Application_Singleton.CONTAINER_TITLE = "Notifications";
            Application_Singleton.CONTAINERFRAG = supplier;
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
        } else if (item.getItemId() == R.id.action_unread_applozic) {
            if (UserInfo.getInstance(getActivity()).isGuest()) {
                StaticFunctions.ShowRegisterDialog(getActivity(), "Home Page");
                return false;
            }
            Intent intent = new Intent(getContext(), ConversationActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_wishlist) {
            Log.d(TAG, "onOptionsItemSelected: WishList");
            Application_Singleton.CONTAINER_TITLE = "My Wishlist";
            Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.cart) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cart_count = preferences.getInt("cartcount", 0);
                Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
    }

    public void setDrwaerToggle() {
        mDrawerToggle = ((Activity_Home) getActivity()).mDrawerToggle;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((Activity_Home) getActivity()).mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NAVIGATIONDRAWER", "CLICKED");
                ((Activity_Home) getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_hamburger_24dp);
    }


    private void getCompanyRating() {
        if (isAdded() && !isDetached()) {
            //showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "solo-propreitorship-kyc", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {

                        if (isAdded() && !isDetached()) {
                            final RequestCreditRating[] creditRatings = new Gson().fromJson(response, RequestCreditRating[].class);
                            if (creditRatings.length > 0) {
                                UserInfo.getInstance(getActivity()).setCreditRating(true);
                                Application_Singleton.IS_COMPANY_RATING_GET = true;
                                if (!Application_Singleton.isAskedFirstTimeAfterApply) {
                                    initMoneySmart();
                                }

                            } else {
                                UserInfo.getInstance(getActivity()).setCreditRating(false);
                                Application_Singleton.IS_COMPANY_RATING_GET = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                    Log.v("error response", error.getErrormessage());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.e(TAG, "onActivityResult Fragment_Home2: ");
            if (requestCode == Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE
                    && resultCode == Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE) {
                if (Application_Singleton.selectedshareCatalog != null) {
                    if (data.getSerializableExtra("supplier") != null) {
                        MultipleSuppliers multipleSuppliers = (MultipleSuppliers) data.getSerializableExtra("supplier");
                        if (multipleSuppliers.getRelation_id() != null) {
                            multipleSuppliers.setIs_supplier_approved(true);
                        } else {
                            multipleSuppliers.setIs_supplier_approved(false);
                        }
                        if (data.getStringExtra("action") != null) {
                            if (data.getStringExtra("action").equals(Fragment_CatalogsGallery.PURCHASE)) {
                                // purchase order pass
                                if (homeWishListAdapter != null)
                                    homeWishListAdapter.createPublicPurchaseOrder(Application_Singleton.selectedshareCatalog, multipleSuppliers.getCompany_id(), multipleSuppliers.getName(), multipleSuppliers.is_supplier_approved(), multipleSuppliers.getTrusted_seller());
                            }
                        }
                    }
                }
            }

            if (requestCode == Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE_RECENT
                    && resultCode == Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE) {
                if (Application_Singleton.selectedshareCatalog != null) {
                    if (data.getSerializableExtra("supplier") != null) {
                        MultipleSuppliers multipleSuppliers = (MultipleSuppliers) data.getSerializableExtra("supplier");
                        if (multipleSuppliers.getRelation_id() != null) {
                            multipleSuppliers.setIs_supplier_approved(true);
                        } else {
                            multipleSuppliers.setIs_supplier_approved(false);
                        }
                        if (data.getStringExtra("action") != null) {
                            if (data.getStringExtra("action").equals(Fragment_CatalogsGallery.PURCHASE)) {
                                // purchase order pass
                                if (recycler_view_recent.getAdapter() != null)
                                    ((RecentViewedAdapter) recycler_view_recent.getAdapter()).createPublicPurchaseOrder(Application_Singleton.selectedshareCatalog, multipleSuppliers.getCompany_id(), multipleSuppliers.getName(), multipleSuppliers.is_supplier_approved(), multipleSuppliers.getTrusted_seller());
                            }
                        }
                    }
                }
            }

            if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                if (params != null && params.size() > 0) {
                    if(bundle.getBoolean("clearall",false)) {
                        if (paramsClone != null) {
                            paramsClone.clear();
                            paramsClone = null;
                        }
                        updateHomePageRecommendation(paramsClone);
                    } else {
                        StaticFunctions.printHashmap(params);
                        updateHomePageRecommendation(params);
                    }
                }  else {
                    Log.d(TAG, "onActivityResult: Param Size < 0" );
                    if (paramsClone != null) {
                        paramsClone.clear();
                        paramsClone = null;
                    }
                    updateHomePageRecommendation(paramsClone);
                }
            }
            if (requestCode == Application_Singleton.STORY_VIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                notifyStoryAdapter();
                if (storyGroup != null && storyModels.size() > 0) {
                    if (Application_Singleton.StoryClickPostion + 1 < storyModels.size()) {
                        // Call Next Story
                        Intent storyIntent = new Intent(getActivity(), StoryActivity.class);
                        storyIntent.putExtra("story_id", storyModels.get(Application_Singleton.StoryClickPostion + 1).getId());
                        storyIntent.putExtra("story_start_position", storyModels.get(Application_Singleton.StoryClickPostion + 1).getCompletion_count() == storyModels.get(Application_Singleton.StoryClickPostion + 1).getCatalogs().size() ? 0 : storyModels.get(Application_Singleton.StoryClickPostion + 1).getCompletion_count());
                        this.startActivityForResult(storyIntent, Application_Singleton.STORY_VIEW_REQUEST_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                        Application_Singleton.StoryClickPostion = Application_Singleton.StoryClickPostion + 1;
                    }
                }
            } else {
                notifyStoryAdapter();
            }

            try {
                // MoneySmart ActivityResult Start

                if (requestCode == ASK_BACKGROUD_PROCESS) {
                    if (Application_Singleton.isMoneySmartEnable) {
                        // Autostart Permission Enable
                        moneySmart.edit().putBoolean("BACKGROUND_PROCESS", false).commit();
                        if (isRequiredServiceSms) {
                            askMoneySmartServiceSms();
                        } else {
                            Log.e(StaticFunctions.MONEYTAG, "onCreate: Init with userID ==>" + UserInfo.getInstance(getActivity()).getUserId());
                            MoneySmartInit.init(getActivity(), UserInfo.getInstance(getActivity()).getUserId(), true);
                            afterMononeySmartInitTask();
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getActivity(), "Money Smart Init Done", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }

                if (requestCode == ASK_SERVICE_SMS) {
                    if (Application_Singleton.isMoneySmartEnable) {
                        // Service SMSes Enable
                        moneySmart.edit().putBoolean("SMS_SERVICE", false).commit();
                        Log.e(StaticFunctions.MONEYTAG, "onCreate: Init with userID ==>" + UserInfo.getInstance(getActivity()).getUserId());
                        MoneySmartInit.init(getActivity(), UserInfo.getInstance(getActivity()).getUserId(), true);
                        afterMononeySmartInitTask();
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(getActivity(), "Money Smart Init Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // MoneySmart ActivityResult End
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 900) {
                if (grantResults.length > 0) {
                    if (Application_Singleton.isMoneySmartEnable) {
                        Map<String, Integer> perms = new HashMap<String, Integer>();
                        perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                        perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        if (perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                            if (isRequiredAutoStart) {
                                askMoneySmartBackgroundProcess();
                            } else {
                                // Service SMSes Enable
                                Log.e(StaticFunctions.MONEYTAG, "onCreate: Init with userID ==>" + UserInfo.getInstance(getActivity()).getUserId());
                                MoneySmartInit.init(getActivity(), UserInfo.getInstance(getActivity()).getUserId(), true);
                                afterMononeySmartInitTask();
                                moneySmart.edit().putBoolean("SMS_SERVICE", false).commit();
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(getActivity(), "Money Smart Init Done", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.e(TAG, "onRequestPermissionsResult: SMS Permission False Summary");
                            // Permission Denied
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // #################### MoneySmart Start  ###############################//

    public void initMoneySmart() {
        if (Application_Singleton.isMoneySmartEnable) {
            try {
                setMoneySmartFlags();
                if (Application_Singleton.isChangeCreditScrore && !UserInfo.getInstance(getActivity()).isAskSmsDialogShown()) {
                    showReadPermissionAskDialog(getActivity());
                } else {
                    Application_Singleton.isAskedFirstTimeAfterApply = true;
                    if (Application_Singleton.getInstance().checkSMSPermissionInit(getActivity())) {
                        if (Application_Singleton.getInstance().checkSMSPermissionInit(getActivity()) && !isRequiredAutoStart && !isRequiredServiceSms) {
                            Log.e(StaticFunctions.MONEYTAG, "onCreate: Init with userID ==>" + UserInfo.getInstance(getActivity()).getUserId());
                            MoneySmartInit.init(getActivity(), UserInfo.getInstance(getActivity()).getUserId(), true);
                            afterMononeySmartInitTask();
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(getActivity(), "Money Smart Init Done", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Application_Singleton.getInstance().checkSMSPermissionInit(getActivity()) && isRequiredAutoStart) {
                            // Sms permission there, and check autostart permission available
                            Log.e(StaticFunctions.MONEYTAG, "AutoStart:" + isRequiredAutoStart);
                            Log.e(StaticFunctions.MONEYTAG, "ServiceSms:" + isRequiredServiceSms);
                            if (moneySmart.getBoolean("BACKGROUND_PROCESS", true)) {
                                askMoneySmartBackgroundProcess();
                            }
                        } else {
                            if (moneySmart.getBoolean("SMS_SERVICE", true)) {
                                askMoneySmartServiceSms();
                            }

                        }
                    } else {
                        // Ask Permission
                        String[] permissions = {
                                "android.permission.READ_SMS",
                                "android.permission.RECEIVE_SMS"
                        };
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                            Log.e(TAG, "initMoneySmart: ask moneySmart Permission");
                            requestPermissions(permissions, 900);
                        } else {
                            try {
                                Snackbar snackbar = Snackbar
                                        .make(getView(), "Please Enable SMS Permission for Credit Rating", Snackbar.LENGTH_LONG)
                                        .setAction("SETTING", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                View snackBarView = snackbar.getView();
                                snackBarView.setAlpha(0.9f);
                                snackbar.addCallback(new Snackbar.Callback() {

                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {

                                    }

                                    @Override
                                    public void onShown(Snackbar snackbar) {
                                    }
                                });

                                snackbar.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setMoneySmartFlags() {
        String phoneManufacturer = Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "Honor".equalsIgnoreCase(phoneManufacturer)
                || "Letv".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)
                || "oppo".equalsIgnoreCase(phoneManufacturer) || "vivo".equalsIgnoreCase(phoneManufacturer)
                || "lenovo".equalsIgnoreCase(phoneManufacturer.toLowerCase())) {

            isRequiredAutoStart = true;
            isRequiredAutoStart = moneySmart.getBoolean("SMS_SERVICE", true);
        }

        if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)) {
            isRequiredServiceSms = true;
            isRequiredServiceSms = moneySmart.getBoolean("BACKGROUND_PROCESS", true);
        }

    }

    private void askMoneySmartBackgroundProcess() {
        String phoneManufacturer = Build.MANUFACTURER;
        //for background
        if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "Honor".equalsIgnoreCase(phoneManufacturer)
                || "Letv".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)
                || "oppo".equalsIgnoreCase(phoneManufacturer) || "vivo".equalsIgnoreCase(phoneManufacturer)
                || "lenovo".equalsIgnoreCase(phoneManufacturer.toLowerCase())) {
            String intentComponentName = "com.android.settings";
            String intentComponentActivityClassName = "com.android.settings.Settings";

            if ("xiaomi".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.miui.securitycenter";
                intentComponentActivityClassName = "com.miui.permcenter.autostart.AutoStartManagementActivity";
            } else if ("oppo".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.coloros.safecenter";
                intentComponentActivityClassName = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
            } else if ("vivo".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.vivo.permissionmanager";
                intentComponentActivityClassName = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
            } else if ("lenovo".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.android.settings";
                intentComponentActivityClassName = "com.android.settings.Settings";
            } else if ("Letv".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.letv.android.letvsafe";
                intentComponentActivityClassName = "com.letv.android.letvsafe.AutobootManageActivity";
            } else if ("Honor".equalsIgnoreCase(phoneManufacturer)) {
                intentComponentName = "com.huawei.systemmanager";
                intentComponentActivityClassName = "com.huawei.systemmanager.optimize.process.ProtectActivity";
            }


            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(intentComponentName, intentComponentActivityClassName));

            List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                Log.e(StaticFunctions.MONEYTAG, "askMoneySmartBackgroundProcess: ");
                //add ui
                // if (Application_Singleton.canUseCurrentAcitivity()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Credit Rating")
                        .setMessage("Please enable background process for more accurate credit rating")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(intent, ASK_BACKGROUD_PROCESS);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                //  }

            }
        }
    }

    private void askMoneySmartServiceSms() {
        String phoneManufacturer = Build.MANUFACTURER;
        //for service sms
        if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)) {
            if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)) {
                Log.e(StaticFunctions.MONEYTAG, "askMoneySmartServiceSms: ");
                // if (Application_Singleton.canUseCurrentAcitivity()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Credit Rating")
                        .setMessage("Please enable service sms for more accurate credit rating")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onDisplayPopupPermission();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                //}

            }
        }
    }

    private void onDisplayPopupPermission() {
        try {
            // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getActivity().getPackageName());
            localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(localIntent, ASK_SERVICE_SMS);


        } catch (Exception e) {
            try {
                // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", getActivity().getPackageName());
                localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(localIntent, ASK_SERVICE_SMS);


            } catch (Exception e1) {
                // Otherwise jump to application details
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, ASK_SERVICE_SMS);
            }
        }

    }

    private void afterMononeySmartInitTask() {
      /*  UserRegistrationIntentService.startActionRegisterUser(getActivity().getApplicationContext(), null, -1);
        InitializeDBIntentService.startActionInitializeDB(getActivity().getApplicationContext());
        ScrapeAndSendSMSService.startActionScrapeAndSendSMS(getActivity().getApplicationContext(), new LatLng(-90, -90));*/
    }

    public void showReadPermissionAskDialog(Context context) {
        String phoneManufacturer = Build.MANUFACTURER;
        boolean isAskwithBackground = false;
        if ("xiaomi".equalsIgnoreCase(phoneManufacturer) || "Honor".equalsIgnoreCase(phoneManufacturer)
                || "Letv".equalsIgnoreCase(phoneManufacturer) || "xiomi".equalsIgnoreCase(phoneManufacturer)
                || "oppo".equalsIgnoreCase(phoneManufacturer) || "vivo".equalsIgnoreCase(phoneManufacturer)
                || "lenovo".equalsIgnoreCase(phoneManufacturer.toLowerCase())) {

            isAskwithBackground = true;

        }

        String msg = getActivity().getResources().getString(R.string.moneysmart_ask_sms_permission);
        if (isAskwithBackground) {
            msg = getActivity().getResources().getString(R.string.moneysmart_ask_background_permission);
        }
        new MaterialDialog.Builder(getActivity())
                .content(msg)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        UserInfo.getInstance(getActivity()).setAskSmsDialogShown(true);
                        initMoneySmart();

                    }
                })
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UserInfo.getInstance(getActivity()).setAskSmsDialogShown(true);
                    }
                }).show();
    }


    // #######################   MoneySmart End  ######################### //


    public void updateBadge(final int count) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    if (count == 0) {
                        BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                    } else {

                        ActionItemBadge.update(getActivity(), menu.findItem(R.id.cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menu.findItem(R.id.cart).getActionView().startAnimation(shake);
    }


    public void getCartData(final @NonNull AppCompatActivity context) {
        final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        try {
            if (preferences.getString("cartId", "").equals("null") || preferences.getString("cartId", "").equals("")) {
                preferences.edit().putString("cartdata", "").commit();
            } else {
                String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/catalogwise/";
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                //showProgress();
                HttpManager.getInstance(context).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }


                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            CartCatalogModel cart_response = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                            if (cart_response != null) {
                                Log.d("CARTDATA", preferences.getString("cartdata", ""));
                                if (cart_response != null || cart_response.getItems() != null || cart_response.getItems().size() != 0) {
                                    StaticFunctions.saveCartData(context, cart_response);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGST() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "company_kyc", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (isAdded() && !isDetached()) {
                            PostKycGst[] resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst[].class);
                            if (resPostKycGst.length > 0 && resPostKycGst != null) {
                                UserInfo.getInstance(getActivity()).setKyc(Application_Singleton.gson.toJson(resPostKycGst[0]));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void getBankDetails() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "bank_details", ""), null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (isAdded() && !isDetached()) {
                            try {
                                Type type = new TypeToken<ArrayList<KYC_model>>() {
                                }.getType();
                                ArrayList<KYC_model> kyc_models = Application_Singleton.gson.fromJson(response, type);
                                if (kyc_models.size() > 0) {
                                    UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(kyc_models.get(0)));
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

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setApplozicMenu() {
        if (menu != null) {
           /* try {
                menu.findItem(R.id.action_unread_applozic).setVisible(false);
                int totalUnreadCount = new MessageDatabaseService(getActivity()).getTotalUnreadCount();
                Log.i(TAG, "setApplozicMenu: " + totalUnreadCount);
                if (totalUnreadCount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_unread_applozic), getResources().getDrawable(R.drawable.ic_chat_white_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_unread_applozic), getResources().getDrawable(R.drawable.ic_chat_white_24dp), ActionItemBadge.BadgeStyles.RED, totalUnreadCount);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }


    /**
     * AppLozic Unread count real Time
     */
    BroadcastReceiver unreadCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MobiComKitConstants.APPLOZIC_UNREAD_COUNT.equals(intent.getAction())) {
                setApplozicMenu();
            }
        }
    };

    public void registerUnReadCountReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MobiComKitConstants.APPLOZIC_UNREAD_COUNT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(unreadCountBroadcastReceiver, filter);
    }

    public void unregisterUnReadCountReceiver() {
        if (unreadCountBroadcastReceiver != null) {
            try {
                getActivity().unregisterReceiver(unreadCountBroadcastReceiver);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

    }


    // ############################## Home Page API Binding Start ####################################//

    public void getHomeData(int page, boolean isOldDataVarnish) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        if (page == 1 || isOldDataVarnish) {
            showProgress();
        }
        String url = URLConstants.USER_FEED_LIST;
        HashMap<String, String> param = new HashMap<>();
        param.put("page", String.valueOf(page));
        if (page > 1) {
            if (paramsClone != null) {
                param.putAll(paramsClone);
            }
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, param, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    Loading = false;
                    if (page == 1 || isOldDataVarnish) {
                        hideProgress();
                    }
                    if (isOldDataVarnish) {
                        removeOldData();
                    }
                    if (groupAdapter != null && page > 1) {
                        int last_group_postion = groupAdapter.getAdapterPosition(loadingGroup);
                        Group loading_item = groupAdapter.getGroup(last_group_postion);
                        if (loading_item.getItem(0) instanceof LoadingItem) {
                            Log.e(TAG, "onServerResponse: ====> INSTANCE OF LOADING");
                            groupAdapter.remove(loading_item);
                            groupAdapter.onItemRemoved(loading_item, last_group_postion);
                        }
                    }

                    try {
                        Type type = new TypeToken<ArrayList<HomeWrapperModel>>() {
                        }.getType();
                        ArrayList<HomeWrapperModel> temp = Application_Singleton.gson.fromJson(response, type);
                        if (temp != null && temp.size() > 0) {
                            if (page == 1) {
                                homeWrapperModelArrayList = new ArrayList<>();
                            }
                            homeWrapperModelArrayList.addAll(temp);
                            dataBindHomePage(temp, page);
                        }
                        if(page > 1 && isOldDataVarnish) {
                            try {
                                // After Filter scroll to second page position
                                if(page_group!=null && page_group.containsKey(2)) {
                                    ArrayList<Group> temp_list = page_group.get(2);
                                    int adapterPosition = groupAdapter.getAdapterPosition(temp_list.get(0));
                                    mRecyclerView.getLayoutManager().scrollToPosition(adapterPosition+2);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (temp.size() > 0 && page > 1) {
                            hasLoadedAllItems = true;
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
    }

    public void dataBindHomePage(ArrayList<HomeWrapperModel> temp, int page) {
        for (int i = 0; i < temp.size(); i++) {
            HomeWrapperModel homeWrapperModel = temp.get(i);
            String content_type = "";
            if (temp.get(i).getContent_type() != null) {
                content_type = temp.get(i).getContent_type();
            }
            if (content_type.equalsIgnoreCase("product_feeds")) {
                Section section;
                boolean isFirstProductFeed = false;
                if (homeWrapperModel.getContent_title() != null && !homeWrapperModel.getContent_title().isEmpty()) {
                    section = new Section();
                    home_sticky_filter = new StickyHeaderItem(getActivity(), Fragment_Home2.this, paramsClone);
                    section.setHeader(home_sticky_filter);
                    section.setHideWhenEmpty(false);
                    isFirstProductFeed = true;
                } else {
                    section = new Section();
                    section.setHideWhenEmpty(true);
                    isFirstProductFeed = false;
                }
                section.setFooter(new FooterSpaceItem(getActivity(), this));
                groupAdapter.add(section);
                if(isFirstProductFeed && homeWrapperModel.getData().size() == 0 ) {
                    section.add(new EmptyItem(getActivity(),this));
                } else {
                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        Response_catalogMini response_catalogMini = new Gson().fromJson(new Gson().toJson((homeWrapperModel.getData().get(j))), Response_catalogMini.class);
                        section.add(new BrowseCatalogItem(response_catalogMini, getActivity(), this));
                    }
                }

                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("carousel_filter")) {
                Section section = dataBindHeader(homeWrapperModel);
                section.setHideWhenEmpty(true);
                if (homeWrapperModel.getData().size() > 0) {
                    CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                    GroupAdapter carouselAdapter = new GroupAdapter();
                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        Response_Promotion response_promotion = new Gson().fromJson(new Gson().toJson(homeWrapperModel.getData().get(j)), Response_Promotion.class);
                        carouselAdapter.add(new CarouselFilterItem(response_promotion, getActivity(), this));
                    }
                    section.add(new CarouselGroup(carouselDecoration, carouselAdapter));
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("banners")) {
                Section section = dataBindHeader(homeWrapperModel);
                section.setHideWhenEmpty(true);
                if (homeWrapperModel.getData().size() > 0) {
                    HomeBanner homeBanner = new Gson().fromJson(new Gson().toJson((homeWrapperModel.getData().get(0))), HomeBanner.class);
                    section.add(new BannerItem(homeBanner, getActivity(), this));
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("home_category")) {

                Section section = dataBindHeader(homeWrapperModel);
                int span_grid = Integer.parseInt(temp.get(i).getSpan_grid());
                for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                    ResponseHomeCategories responseHomeCategories = new Gson().fromJson(new Gson().toJson((homeWrapperModel.getData().get(j))), ResponseHomeCategories.class);
                    section.add(new HomeCategoryItem(responseHomeCategories, getActivity(), this, span_grid));
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("home_banner")) {
                Section section = dataBindHeader(homeWrapperModel);
                Type type = new TypeToken<ArrayList<Response_Promotion>>() {
                }.getType();
                ArrayList<Response_Promotion> response_promotionArrayList = Application_Singleton.gson.fromJson(homeWrapperModel.getData(), type);
                section.add(new RotateBannerItem(response_promotionArrayList, getActivity(), this));
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("crousel_wishlist")) {
                getSectionPosition();
                if (homeWrapperModel.getData().size() > 0) {
                    Section section = dataBindHeader(homeWrapperModel);
                    section.setHideWhenEmpty(true);
                    int span_grid = Integer.parseInt(temp.get(i).getSpan_grid());
                    if (homeWrapperModel.getData().size() > 0) {
                        CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                        GroupAdapter carouselAdapter = new GroupAdapter();
                        for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                            Response_catalogMini response_catalogMini = new Gson().fromJson(new Gson().toJson((homeWrapperModel.getData().get(j))), Response_catalogMini.class);
                            carouselAdapter.add(new HomeWishListItem(response_catalogMini, getActivity(), this, span_grid));
                        }
                        section.add(new CarouselGroup(carouselDecoration, carouselAdapter));
                    }
                    storeGroup(page, section);
                } else {
                    getWishlist(getActivity());
                }


            } else if (content_type.equalsIgnoreCase("wbk_story")) {
                dataBindStory(homeWrapperModel);
            } else if (content_type.equalsIgnoreCase("home_public_brand")) {
                Section section = dataBindHeader(homeWrapperModel);
                section.setHideWhenEmpty(true);
                if (homeWrapperModel.getData().size() > 0) {
                    CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                    GroupAdapter carouselAdapter = new GroupAdapter();
                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        Response_Brands response_brands = new Gson().fromJson(new Gson().toJson(homeWrapperModel.getData().get(j)), Response_Brands.class);
                        carouselAdapter.add(new PublicBrandCarouselItem(response_brands, getActivity(), this));
                    }
                    Group group = new CarouselGroup(carouselDecoration, carouselAdapter, true, false, homeWrapperModel.getContent_title());
                    section.add(group);
                    CarouselWithBackGroundItem carouselWithBackGroundItem = (CarouselWithBackGroundItem) group.getItem(0);
                    carouselWithBackGroundItem.setOnSeeMoreClickListener(new CarouselWithBackGroundItem.OnSeeMoreClickListener() {
                        @Override
                        public void onSeeMoreClick() {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("type", "tab");
                            hashMap.put("page", "catalogs/brands");
                            new DeepLinkFunction(hashMap, getActivity());
                        }
                    });
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("product_feed_carousel")) {
                Section section = new Section();
                groupAdapter.add(section);
                section.setHideWhenEmpty(true);
                if (homeWrapperModel.getData().size() > 0) {
                    CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                    GroupAdapter carouselAdapter = new GroupAdapter();
                    HashMap<String, String> param = null;
                    if (homeWrapperModel.getSee_more_deep_link() != null) {
                        param = SplashScreen.getQueryString(Uri.parse(homeWrapperModel.getSee_more_deep_link()));
                    }

                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        Response_catalogMini response_catalogMini = new Gson().fromJson(new Gson().toJson(homeWrapperModel.getData().get(j)), Response_catalogMini.class);
                        carouselAdapter.add(new ProductItemAdapter(response_catalogMini, getActivity(), this, homeWrapperModel.getContent_title(), param, j));
                    }
                    Group group = new CarouselGroup(carouselDecoration, carouselAdapter, false, true, homeWrapperModel.getContent_title());
                    section.add(group);
                    CarouselProductWithSeeMoreItem carouselProductWithSeeMoreItem = (CarouselProductWithSeeMoreItem) group.getItem(0);
                    HashMap<String, String> finalParam = param;
                    carouselProductWithSeeMoreItem.setOnSeeMoreClickListener(new CarouselProductWithSeeMoreItem.OnSeeMoreClickListener() {
                        @Override
                        public void onSeeMoreClick() {
                            new DeepLinkFunction(finalParam, getActivity());
                        }
                    });
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("home_video_review")) {
                Section section = dataBindGradientHeader(homeWrapperModel);
                section.setHideWhenEmpty(true);
                int span_grid = Integer.parseInt(temp.get(i).getSpan_grid());
                if (homeWrapperModel.getData().size() > 0) {
                    CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                    GroupAdapter carouselAdapter = new GroupAdapter();
                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        Response_Promotion response_promotion = new Gson().fromJson(new Gson().toJson((homeWrapperModel.getData().get(j))), Response_Promotion.class);
                        carouselAdapter.add(new VideoFeedBackItem(response_promotion, getActivity(), this));
                    }
                    section.add(new CarouselGroup(carouselDecoration, carouselAdapter));
                }
                storeGroup(page, section);
            } else if (content_type.equalsIgnoreCase("home_image_review")) {
                homeWrapperModel.setContent_title("");
                Section section = dataBindGradientHeader(homeWrapperModel);
                Type type = new TypeToken<ArrayList<Response_Promotion>>() {
                }.getType();
                ArrayList<Response_Promotion> response_promotionArrayList = Application_Singleton.gson.fromJson(homeWrapperModel.getData(), type);
                section.add(new RotateBannerItem(response_promotionArrayList, getActivity(), this));
                storeGroup(page, section);
            } else if(content_type.equalsIgnoreCase("home_coupon_carousel")) {
                Section section = dataBindHeader(homeWrapperModel);
                section.setHideWhenEmpty(true);
                if (homeWrapperModel.getData().size() > 0) {
                    CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                    GroupAdapter carouselAdapter = new GroupAdapter();
                    for (int j = 0; j < homeWrapperModel.getData().size(); j++) {
                        ResponseCouponList responseCouponList= new Gson().fromJson(new Gson().toJson(homeWrapperModel.getData().get(j)), ResponseCouponList.class);
                        carouselAdapter.add(new CouponCarouselItem(responseCouponList, getActivity(), this));
                    }
                    section.add(new CarouselGroup(carouselDecoration, carouselAdapter));
                }
                storeGroup(page, section);
            }
        }
    }

    public Section dataBindHeader(HomeWrapperModel homeWrapperModel) {
        Section section;
        if (homeWrapperModel.getContent_title() != null && !homeWrapperModel.getContent_title().isEmpty()) {
            section = new Section();
            HeaderItem headerItem;
            if (homeWrapperModel.getSee_more_deep_link() != null && !homeWrapperModel.getSee_more_deep_link().isEmpty()) {
                headerItem = new HeaderItem(homeWrapperModel.getContent_title(), null, true);
                headerItem.setOnSeeMoreClickListener(new HeaderItem.OnSeeMoreClickListener() {
                    @Override
                    public void onSeeMoreClick() {
                        HashMap<String, String> param = SplashScreen.getQueryString(Uri.parse(homeWrapperModel.getSee_more_deep_link()));
                        new DeepLinkFunction(param, getActivity());
                    }
                });
            } else {
                headerItem = new HeaderItem(homeWrapperModel.getContent_title(), null, false);
            }

            section.setHeader(headerItem);

        } else {
            section = new Section();
        }
        section.setFooter(new FooterSpaceItem(getActivity(), this));
        if (homeWrapperModel.getContent_type().equalsIgnoreCase("crousel_wishlist")) {
            groupAdapter.add(wishlist_section_position, section);
        } else {
            groupAdapter.add(section);
        }
        return section;
    }

    public Section dataBindGradientHeader(HomeWrapperModel homeWrapperModel) {
        Section section;
        if (homeWrapperModel.getContent_title() != null && !homeWrapperModel.getContent_title().isEmpty()) {
            section = new Section();
            section.setHeader(new HeaderGradientItem(homeWrapperModel.getContent_title(), false));
        } else {
            section = new Section();
        }
        section.setFooter(new FooterSpaceItem(getActivity(), this));
        groupAdapter.add(section);
        return section;
    }

    public void dataBindStory(HomeWrapperModel temp) {
        storySection = new Section(new HeaderItem(temp.getContent_title(), null, false));
        groupAdapter.add(storySection);
        int span_grid = Integer.parseInt(temp.getSpan_grid());
        if (temp.getData().size() > 0) {
            CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(ContextCompat.getColor(getActivity(), R.color.white), betweenPadding);
            storyGroupAdapter = new GroupAdapter();
            storyModels = new ArrayList<>();
            for (int i = 0; i < temp.getData().size(); i++) {
                // Remove those stories who have not catalogs
                ResponseStoryModel responseStoryModel = new Gson().fromJson(new Gson().toJson((temp.getData().get(i))), ResponseStoryModel.class);
                if (responseStoryModel.getCatalogs().size() > 0)
                    storyModels.add(responseStoryModel);
            }
            Collections.shuffle(storyModels);
            for (int j = 0; j < storyModels.size(); j++) {
                storyGroupAdapter.add(new WishBookStoryItem(storyModels.get(j), getActivity(), this));
            }
            storyGroup = new ArrayList<>();
            CarouselGroup group = new CarouselGroup(carouselDecoration, storyGroupAdapter);
            storyGroup.add(group);
            storySection.addAll(storyGroup);
            notifyStoryAdapter();
            storeGroup(page, storySection);
        }
    }

    public void storeGroup(int page, Section section) {
        if (page_group == null)
            page_group = new HashMap<>();

        if (page_group.containsKey(page)) {
            ArrayList<Group> tempGroups = page_group.get(page);
            tempGroups.add(section);
            page_group.put(page, tempGroups);
        } else {
            ArrayList<Group> tempGroups = new ArrayList<>();
            tempGroups.add(section);
            page_group.put(page, tempGroups);
        }
    }

    private void notifyStoryAdapter() {
        if (storyGroup != null && storyModels.size() > 0) {
            if (PrefDatabaseUtils.getPrefStoryCompletion(getActivity()) != null) {
                HashMap<String, ArrayList<String>> responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefStoryCompletion(getActivity()), new TypeToken<HashMap<String, ArrayList<String>>>() {
                }.getType());
                for (int i = 0; i < storyModels.size(); i++) {
                    if (responseData.containsKey(storyModels.get(i).getId())) {
                        storyModels.get(i).setCompletion_count(responseData.get(storyModels.get(i).getId()).size());
                    }
                }
                storyGroupAdapter.notifyItemRangeChanged(0, storyModels.size());
            }
        }
    }

    private void getSectionPosition() {
        if (homeWrapperModelArrayList != null) {
            for (int homeIndex = 0; homeIndex < homeWrapperModelArrayList.size(); homeIndex++) {
                if (homeWrapperModelArrayList.get(homeIndex).getContent_type().equalsIgnoreCase("crousel_wishlist")) {
                    wishlist_section_position = homeIndex;
                }
            }
        }
    }

    public void openFilterScreen() {
        Intent intent = new Intent(getActivity(), ActivityFilter.class);
        if (paramsClone != null) {
            paramsClone.put("product_availability", "both");
            intent.putExtra("previousParam", paramsClone);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put("product_type", Constants.PRODUCT_TYPE_CAT);
            intent.putExtra("previousParam", param);
        }
        intent.putExtra("from_public", false);
        intent.putExtra("from_brand", true);
        Fragment_Home2.this.startActivityForResult(intent, 5000);
    }

    public void updateHomePageRecommendation(HashMap<String, String> params) {
        if (groupAdapter != null && page_group != null) {
            // clear Category
            try {
                if (page_group.containsKey(2)) {
                    paramsClone = params;
                    page = 2;
                    getHomeData(page, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeOldData() {
        homeWrapperModelArrayList.remove(page);
        Log.e(TAG, "removeOldData: ======>"+page );
        ArrayList<Group> temp_group = page_group.get(page);
        Log.e(TAG, "updateHomePageRecommendation: Size=====>" + temp_group.size());
        for (int i = 0; i < temp_group.size(); i++) {
            try {
                int adapterPosition = groupAdapter.getAdapterPosition(temp_group.get(i));
                Log.e(TAG, "updateHomePageRecommendation: =====>" + adapterPosition);
                if(adapterPosition > -1) {
                    Group temp = groupAdapter.getGroup(adapterPosition);
                    groupAdapter.remove(temp);
                    groupAdapter.onItemRemoved(temp, adapterPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        page_group.remove(page);
    }


    // ############################## Home Page API Binding End ####################################//

    public void showStockCheckDialog(Response_catalogMini[] response_catalog) {
        if (response_catalog.length > 0) {
            DialogFragment dialog = new DialogProductStockCheck();
            final ArrayList<Response_catalogMini> arrayList = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalog));
            Bundle bundle = new Bundle();
            bundle.putSerializable("catalog", arrayList);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "dialog");
        }
    }

    private void get15DaysCronStockCheck() {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HashMap<String, String> params = new HashMap<>();
            params.put("view_type", "mycatalogs");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date today = new Date();
            ArrayList<String> previous_date = new ArrayList<>();
            int no_of_loop = 60 / Constants.STOCK_CHECK_DAYS;

            if (UserInfo.getInstance(getActivity()).getLastLoggedIn() != null) {
                Date last_date = DateUtils.stringToDate(UserInfo.getInstance(getActivity()).getLastLoggedIn(), sdf);
                Date today_date = DateUtils.stringToDate(sdf.format(today.getTime()), sdf);
                long difference = today_date.getTime() - last_date.getTime();
                float daysBetween = (difference / (1000 * 60 * 60 * 24));
                int totalDays = Math.round(daysBetween);
                totalDays += 1;
                if (totalDays > Constants.STOCK_CHECK_DAYS) {
                    totalDays = Constants.STOCK_CHECK_DAYS;
                }
                Log.e(TAG, "get15DaysCronStockCheck: Diffrence" + difference + "  daysBetween: " + daysBetween + " totalDays: " + totalDays);
                for (int i = 0; i < totalDays; i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -i);
                    Log.e(TAG, "get15DaysCronStockCheck: inside loop of i:" + i);
                    for (int j = 0; j < no_of_loop; j++) {
                        cal.add(Calendar.DAY_OF_MONTH, -Constants.STOCK_CHECK_DAYS);
                        String temp = sdf.format(cal.getTime());
                        Log.e(TAG, "get15DaysCronStockCheck: adding date :" + temp + " to previous_date array of length:" + previous_date.size());
                        if (!previous_date.contains(temp)) {
                            previous_date.add(temp);
                        }
                    }
                }
            } else {
                UserInfo.getInstance(getActivity()).setLastLoggedIn();
            }
            Log.e(TAG, "get15DaysCronStockCheck: ====>" + StaticFunctions.ArrayListToString(previous_date, StaticFunctions.COMMASEPRATED));
            if (previous_date != null && previous_date.size() > 0) {
                params.put("created_at", StaticFunctions.ArrayListToString(previous_date, StaticFunctions.COMMASEPRATED));
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        if (isAdded() && !isDetached()) {
                            UserInfo.getInstance(getActivity()).setLastLoggedIn();
                            try {
                                Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                                if (response_catalog.length > 0) {
                                    checkIFAskedOnce(response_catalog);
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkIFAskedOnce(Response_catalogMini[] response_catalog) {
        if (PrefDatabaseUtils.getPrefKeepSellingStock(getActivity()) != null) {
            HashMap<String, String> responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefKeepSellingStock(getActivity()), new TypeToken<HashMap<String, String>>() {
            }.getType());
            ArrayList<Response_catalogMini> response_catalog_list = new ArrayList<>(Arrays.asList(response_catalog));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Response_catalogMini[] temp = new Response_catalogMini[response_catalog.length];
            temp = response_catalog.clone();
            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < responseData.size(); j++) {
                    if (responseData.containsKey(temp[i].getId())) {
                        String date = responseData.get(temp[i].getId());
                        if (date.equals(sdf.format(new Date()))) {
                            response_catalog_list.remove(i);
                        }
                    }
                }
            }
            response_catalog = (Response_catalogMini[]) response_catalog_list.toArray(new Response_catalogMini[response_catalog_list.size()]);
            showStockCheckDialog(response_catalog);
        } else {
            showStockCheckDialog(response_catalog);
        }
    }


    //  ################ Send Wishbook Analysis Data Start ################### //

    public void sendUserAttributes(Context context) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.USER_PROPERTIES);
        wishbookEvent.setEvent_names(WishbookEvent.USER_PROPERTIES);
        new WishbookTracker(getActivity(), wishbookEvent);
    }


    public void sendBannerClickAnalytics(HashMap<String, String> prop) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Banner_Clicked");
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(getActivity(), wishbookEvent);
    }

    //  ################ Send Wishbook Analysis Data End ################### //

    private void getWishlist(final Activity activity) {
        String url = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(5));
        params.put("offset", String.valueOf(0));
        url = URLConstants.userUrl(activity, "wishlist-catalog", "");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(HttpManager.METHOD.GET, url, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached() && !UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                        JsonArray response_catalog = Application_Singleton.gson.fromJson(response, JsonArray.class);
                        homeWrapperModelArrayList.get(wishlist_section_position).setData(response_catalog);
                        Section section = dataBindHeader(homeWrapperModelArrayList.get(wishlist_section_position));
                        section.setHideWhenEmpty(true);
                        if (homeWrapperModelArrayList.get(wishlist_section_position).getData().size() > 0) {
                            CarouselItemDecoration carouselDecoration = new CarouselItemDecoration(transparent, betweenPadding);
                            GroupAdapter carouselAdapter = new GroupAdapter();
                            for (int j = 0; j < homeWrapperModelArrayList.get(wishlist_section_position).getData().size(); j++) {
                                Response_catalogMini response_catalogMini = new Gson().fromJson(new Gson().toJson((homeWrapperModelArrayList.get(wishlist_section_position).getData().get(j))), Response_catalogMini.class);
                                if (!response_catalogMini.isBuyer_disabled() && !response_catalogMini.isSupplier_disabled()) {
                                    // Not Show Disable Catalog
                                    carouselAdapter.add(new HomeWishListItem(response_catalogMini, getActivity(), Fragment_Home2.this, 2));
                                }
                            }
                            section.add(new CarouselGroup(carouselDecoration, carouselAdapter));
                            storeGroup(1, section);
                        }
                    }

                } catch (JsonSyntaxException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                recycler_view_wishlist.setVisibility(View.GONE);
            }
        });
    }


}
