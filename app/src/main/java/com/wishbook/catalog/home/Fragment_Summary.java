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
import android.text.InputFilter;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.facebook.common.internal.ImmutableList;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.LoopViewPager;
import com.wishbook.catalog.commonadapters.AllSectionAdapter;
import com.wishbook.catalog.commonadapters.BrokerDashboardAdapter;
import com.wishbook.catalog.commonadapters.CommonAnalyticsAdapter;
import com.wishbook.catalog.commonadapters.DeepLinkItemsAdapter;
import com.wishbook.catalog.commonadapters.HomeBrandAdapter1;
import com.wishbook.catalog.commonadapters.HomeCategoryItemsAdapter;
import com.wishbook.catalog.commonadapters.HomeSupplierSuggestionRatingAdapter;
import com.wishbook.catalog.commonadapters.HomeWishListAdapter;
import com.wishbook.catalog.commonadapters.MostSoldAdapter;
import com.wishbook.catalog.commonadapters.RecommendListAdapter;
import com.wishbook.catalog.commonadapters.ShareBYMeSummaryAdapter;
import com.wishbook.catalog.commonadapters.SuggestionContactsAdapter;
import com.wishbook.catalog.commonadapters.VideoFeedBackItemAdapter;
import com.wishbook.catalog.commonmodels.AllDataModel;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
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
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.ImageBanner;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;
import com.wishbook.catalog.commonmodels.responses.Response_Broker_Report;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_DashBoard;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.UserStats;
import com.wishbook.catalog.home.adapters.RecentViewedAdapter;
import com.wishbook.catalog.home.adapters.RecyclerViewSectionAdapter;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.Fragment_BrandFollowedCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_WishList;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.notifications.Fragment_Notifications;
import com.wishbook.catalog.home.notifications.models.NotificationModel;
import com.wishbook.catalog.rest.CampaignLogApi;
import com.wishbook.catalog.services.LocalService;
import com.wishbook.catalog.stories.StoryActivity;
import com.wishbook.catalog.stories.adapter.HomeStoriesAdapter;

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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.in.moneysmart.common.MoneySmartInit;
import co.in.moneysmart.common.util.PermissionsCheckHelper;
import me.relex.circleindicator.CircleIndicator;
import rx.Subscriber;


public class Fragment_Summary extends GATrackedFragment {
    private ArrayList<SummaryModel> data = new ArrayList<>();
    private RecyclerViewSectionAdapter recyclerViewSectionAdapter;

    private ArrayList<Summary_Sub> contactsData = new ArrayList<>();
    private ArrayList<Summary_Sub> buyersData = new ArrayList<>();
    private ArrayList<Summary_Sub> suppliersData = new ArrayList<>();
    private ArrayList<Summary_Sub> saleOrdersData = new ArrayList<>();
    private ArrayList<Summary_Sub> purchaseOrdersData = new ArrayList<>();


    private ArrayList<Summary_Sub> sharedByMe = new ArrayList<>();
    private ArrayList<Summary_Sub> catalogsData = new ArrayList<>();
    private ArrayList<Summary_Sub> salesData = new ArrayList<>();
    private ArrayList<Summary_Sub> brands = new ArrayList<>();
    private ArrayList<Summary_Sub> selection = new ArrayList<>();

    private AllSectionAdapter allItemAdapter;
    private AllSectionAdapter trustedItemAdapter;
    private ArrayList<AllDataModel> trustedSellerData;
    private ArrayList<AllDataModel> alldata;
    private ArrayList<AllDataModel> nonCatalogData;
    Response_BuyerGroupType[] responseBuyerGroupTypes;
    private ArrayList<CatalogMinified> sharedWithMe;
    private ArrayList<CatalogMinified> sharedWithMeYes;
    @BindView(R.id.recent_catalog_heading)
    LinearLayout recent_catalog_heading;
    private SharedPreferences pref;
    Dialog_CompanyType companyType = null;
    private UserInfo userinfo;
    private List<MyContacts> contactList = new ArrayList<>();
    private List<MyContacts> wishbookcontactList = new ArrayList<>();
    private ArrayList<MyContacts> supplierSuggestionList = new ArrayList<>();

    private ArrayList<AllDataModel> allDataMostSold;
    private MostSoldAdapter mostSoldAdapter;

    private ArrayList<AllDataModel> allDataFollowedbyMe;
    private AllSectionAdapter followedbyMeAdapter;

    boolean flag_recent = false;
    //private String first_banner_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/western_kurtis.png";
    private String first_banner_url = "https://d21jr61lxgl795.cloudfront.net/__sized__/promotion_image/Single_pc_select_1-thumbnail-1200x300.png";
    private String second_lehnga_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/printed_sarees2.jpg";
    private String third_lehnga_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/suits_1_8blYENG.png";
    private String forth_lehnga_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/festive_lehngas1.jpg";
    private String fifth_banner_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/Eid_banner.jpg";

    private String non_catalog_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/noncat_banner.jpg";
    private String all_brand_bg_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/banner_1.jpg";
    private String reseller_promotion_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/reseller_learn_how.jpg";
    private String refer_earn_promotion_url = "https://d21jr61lxgl795.cloudfront.net/promotion_image/Refer-and-Earn-2.jpg";

    //added

    Handler handler, handlerFeedback;

    Timer timerTopBanner, timerFeedback;


    @BindView(R.id.recycler_view)
    RecyclerViewEmptySupport recycler_view;
    @BindView(R.id.recycler_view1)
    RecyclerViewEmptySupport mRecyclerView;
    @BindView(R.id.appbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_recent)
    RecyclerView recycler_view_recent;
    @BindView(R.id.viewPager)
    LoopViewPager viewPager;

    @BindView(R.id.see_all_recent)
    LinearLayout see_all_recent;

    @BindView(R.id.banner_container)
    RelativeLayout banner_container;

    @BindView(R.id.feedback_container)
    RelativeLayout feedback_container;

    @BindView(R.id.txt_details)
    TextView txt_details;

    @BindView(R.id.btn_catalog_details)
    ImageView btn_catalog_details;
    @BindView(R.id.viewPager_feedback)
    ViewPager viewPagerFeedback;

    @BindView(R.id.linear_apply_rating)
    LinearLayout linear_apply_rating;

    @BindView(R.id.txt_apply_credit)
    TextView txt_apply_credit;


    MenuItem notificationItem;
    private Subscriber<Integer> subscriber;

    int currentPage = 0, currentPageFeedback = 0;

    private Boolean isCompanyPopUpShown = false;
    private boolean isKYCPopUpShown = false;
    @BindView(R.id.linear_broker_container)
    LinearLayout linear_broker_container;

    @BindView(R.id.recycler_view_brokerreport)
    RecyclerView recycler_view_brokerreport;
    private Response_Broker_Report[] broker_report;


    @BindView(R.id.recycler_view_most_sold)
    RecyclerView recycler_view_most_sold;
    @BindView(R.id.linear_most_sold_container)
    LinearLayout linear_most_sold_container;

    @BindView(R.id.recycler_view_followedbyme)
    RecyclerView recycler_view_followedbyme;


    private ArrayList<CatalogMinified> recentCatalog;


    @BindView(R.id.linear_stories)
    LinearLayout linear_stories;

    @BindView(R.id.recycler_view_stories)
    RecyclerView recycler_view_stories;


    @BindView(R.id.recycler_view_wishlist)
    RecyclerView recycler_view_wishlist;

    @BindView(R.id.recycler_view_non_catalog)
    RecyclerView recycler_view_non_catalog;


    @BindView(R.id.recycler_view_prebook_catalog)
    RecyclerView recycler_view_prebook_catalog;

    @BindView(R.id.recycler_view_received_catalog)
    RecyclerView recycler_view_received_catalog;

    @BindView(R.id.linear_wishlist)
    LinearLayout linear_wishlist;


    @BindView(R.id.second_banner_img)
    SimpleDraweeView second_banner_img;

    @BindView(R.id.third_banner_img)
    SimpleDraweeView third_banner_img;

    @BindView(R.id.forth_banner_img)
    SimpleDraweeView forth_banner_img;


    @BindView(R.id.reseller_banner_img)
    SimpleDraweeView reseller_banner_img;

    @BindView(R.id.reseller_banner_img1)
    SimpleDraweeView reseller_banner_img1;

    @BindView(R.id.refer_earn_promotion_img)
    SimpleDraweeView refer_earn_promotion_img;


    @BindView(R.id.recycler_view_deep_link)
    RecyclerView recyclerViewDeepLink;

    @BindView(R.id.recycler_view_category)
    RecyclerView recycler_view_category;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.wishlist_see_all)
    TextView wishlist_see_all;

    @BindView(R.id.card_apply_rating)
    CardView card_apply_rating;

    @BindView(R.id.clear_stroy_pref)
    AppCompatButton clear_stroy_pref;


    @BindView(R.id.recycler_view_brand)
    RecyclerView recycler_view_brand;

    @BindView(R.id.txt_brand_see_more)
    TextView txt_brand_see_more;

    @BindView(R.id.linear_public_brand)
    LinearLayout linear_public_brand;

    // ### Similar Product ######
    @BindView(R.id.recycler_view_recommded)
    RecyclerView recycler_view_recommded;
    @BindView(R.id.linear_user_recommded)
    LinearLayout linear_user_recommded;
    @BindView(R.id.txt_recommded_title)
    TextView txt_recommded_title;
    @BindView(R.id.sectionMore)
    TextView sectionMore;

    // #### Reseller Suggested Products #####///
    @BindView(R.id.linear_user_recommded_reseller)
    LinearLayout linear_user_recommded_reseller;
    @BindView(R.id.txt_recommded_title_reseller)
    TextView txt_recommded_title_reseller;
    @BindView(R.id.recycler_view_recommded_reseller)
    RecyclerView recycler_view_recommded_reseller;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    // ## SinglePc ###############
    @BindView(R.id.recycler_view_single_catalog1)
    RecyclerView recycler_view_single_catalog1;

    @BindView(R.id.recycler_view_single_noncatalog1)
    RecyclerView recycler_view_single_noncatalog1;

    @BindView(R.id.recycler_view_single_catalog)
    RecyclerView recycler_view_single_catalog;

    @BindView(R.id.recycler_view_single_noncatalog)
    RecyclerView recycler_view_single_noncatalog;

    @BindView(R.id.recycler_view_video_feedback)
    RecyclerView recycler_view_video_feedback;

    @BindView(R.id.video_feedback_container)
    LinearLayout video_feedback_container;

    // How To Resell start
    @BindView(R.id.linear_how_to_resell_container)
    LinearLayout linear_how_to_resell_container;

    @BindView(R.id.recycler_view_how_to_resell)
    RecyclerView recycler_view_how_to_resell;


    ActionBarDrawerToggle mDrawerToggle;

    private boolean isFirstTimeLoadSharedBy;

    private Menu menu;
    private String TAG = "Fragment_Summary";

    HomeWishListAdapter homeWishListAdapter;
    HomeStoriesAdapter homeStoriesAdapter;
    ArrayList<ResponseStoryModel> storyModels;

    // MoneySmart Need Flags,Variable
    private static int ASK_BACKGROUD_PROCESS = 500;
    private static int ASK_SERVICE_SMS = 600;
    private boolean isRequiredAutoStart, isRequiredServiceSms;
    private SharedPreferences moneySmart;
    private TextView txt_home_search;


    Handler company_rating_handler;
    Runnable company_rating_runnable;


    private RelativeLayout relative_brand_bg_container;
    private SimpleDraweeView img_allbrand_bg;
    private float realDx;


    LinearLayoutManager linearLayoutManagerHorizontal;
    // MoneySmart End

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Start");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vi = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) vi.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.summary, ga_container, true);
        Log.d(TAG, "onCreateView: Start");
        flag_recent = false;
        ButterKnife.bind(this, vi);
        initTopHomeSearch(vi);
        moneySmart = this.getActivity().getApplicationContext().getSharedPreferences("moneysmart", Context.MODE_PRIVATE);
        setHasOptionsMenu(true);

        data = new ArrayList<>();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setDrwaerToggle();
        toolbar.setTitle("Wishbook");
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        initRecyclerView(vi);
        initOtherComponentListener();

        relative_brand_bg_container = (RelativeLayout) vi.findViewById(R.id.brand_bg_container);
        img_allbrand_bg = vi.findViewById(R.id.img_allbrand_bg);
        StaticFunctions.loadFresco(getActivity(), all_brand_bg_url, img_allbrand_bg);


        //Starting notification

        /*
        for solving issue Android o
         */
        try {
            getActivity().startService(new Intent(getActivity(), LocalService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*
          Chnages done by Bhavik Gandhi Jun-29 2019
          Jira 5135 (Not show Wishbook stories to company type only reseller)
         */
        if (!StaticFunctions.isOnlyReseller(getActivity())) {
            getAllStoriesList();
        }

        getGroupsType();
        getHomeCategories();
        get15DaysCronStockCheck();
        getGST();
        getBankDetails();
        getUserStatstic();
        StaticFunctions.getAllCODVerficationPending(getActivity());


        getCartData((AppCompatActivity) getActivity());
        Log.d(TAG, "onCreateView: End");
        return vi;
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
        if (BuildConfig.DEBUG) {
            clear_stroy_pref.setVisibility(View.GONE);
        } else {
            clear_stroy_pref.setVisibility(View.GONE);
        }
        clear_stroy_pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefDatabaseUtils.setPrefStoryCompletion1(getActivity(), null);
            }
        });

        linear_apply_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put("type", "credit_rating");
                if (txt_apply_credit.getText().toString().equalsIgnoreCase("APPLY")) {
                    hashmap.put("isEdit", "false");
                } else {
                    hashmap.put("isEdit", "true");
                }
                hashmap.put("from", Fragment_Summary.class.getSimpleName());
                new DeepLinkFunction(hashmap, getActivity());
            }
        });


        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    Activity_Home.support_chat_fab.hide();
                } else {
                    Activity_Home.support_chat_fab.show();
                }
            }
        });


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

    public void initRecyclerView(View view) {
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewSectionAdapter = new RecyclerViewSectionAdapter((AppCompatActivity) getActivity(), data);
        recycler_view.setAdapter(recyclerViewSectionAdapter);
        recycler_view.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(view.findViewById(R.id.list_empty1));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        alldata = new ArrayList<>();
        allItemAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), alldata);
        mRecyclerView.setAdapter(allItemAdapter);


        linear_wishlist.setVisibility(View.GONE);
        recycler_view_wishlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_wishlist.setHasFixedSize(true);
        recycler_view_wishlist.setNestedScrollingEnabled(false);
        nonCatalogData = new ArrayList<>();


        recycler_view_brokerreport.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recycler_view_brokerreport.setNestedScrollingEnabled(false);
        recycler_view_brokerreport.setHasFixedSize(true);

        recycler_view_most_sold.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_most_sold.setHasFixedSize(true);
        recycler_view_most_sold.setNestedScrollingEnabled(false);
        allDataMostSold = new ArrayList<>();
        mostSoldAdapter = new MostSoldAdapter((AppCompatActivity) getActivity(), allDataMostSold);
        recycler_view_most_sold.setAdapter(mostSoldAdapter);

        recycler_view_followedbyme.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_followedbyme.setHasFixedSize(true);
        recycler_view_followedbyme.setNestedScrollingEnabled(false);


       /* recycler_view_trusted_seller.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_trusted_seller.setHasFixedSize(true);
        recycler_view_trusted_seller.setNestedScrollingEnabled(false);
        recycler_view_trusted_seller.setVisibility(View.GONE);*/
        trustedSellerData = new ArrayList<>();
        trustedItemAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), trustedSellerData);
        /* recycler_view_trusted_seller.setAdapter(trustedItemAdapter);*/

        recycler_view_stories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_stories.setHasFixedSize(true);
        recycler_view_stories.setNestedScrollingEnabled(false);
        recycler_view_stories.setVisibility(View.GONE);


        allDataFollowedbyMe = new ArrayList<>();
        followedbyMeAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), allDataFollowedbyMe);
        recycler_view_followedbyme.setAdapter(followedbyMeAdapter);


        recyclerViewDeepLink.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDeepLink.setHasFixedSize(true);
        recyclerViewDeepLink.setNestedScrollingEnabled(false);


        recycler_view_category.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recycler_view_category.setHasFixedSize(true);
        recycler_view_category.setNestedScrollingEnabled(false);


        recycler_view_non_catalog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_non_catalog.setHasFixedSize(true);
        recycler_view_non_catalog.setNestedScrollingEnabled(false);


        recycler_view_recent.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_recent.setHasFixedSize(true);
        recycler_view_recent.setNestedScrollingEnabled(false);


        recycler_view_prebook_catalog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_prebook_catalog.setHasFixedSize(true);
        recycler_view_prebook_catalog.setNestedScrollingEnabled(false);

        // it's for other user
        recycler_view_single_catalog1.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_single_catalog1.setHasFixedSize(true);
        recycler_view_single_catalog1.setNestedScrollingEnabled(false);

        // it's for online-reseller
        recycler_view_single_catalog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_single_catalog.setHasFixedSize(true);
        recycler_view_single_catalog.setNestedScrollingEnabled(false);

        // it's for online-reseller
        recycler_view_single_noncatalog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_single_noncatalog.setHasFixedSize(true);
        recycler_view_single_noncatalog.setNestedScrollingEnabled(false);

        // it's for other user
        recycler_view_single_noncatalog1.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_single_noncatalog1.setHasFixedSize(true);
        recycler_view_single_noncatalog1.setNestedScrollingEnabled(false);


        recycler_view_received_catalog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_received_catalog.setHasFixedSize(true);
        recycler_view_received_catalog.setNestedScrollingEnabled(false);

        recycler_view_video_feedback.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_video_feedback.setHasFixedSize(true);
        recycler_view_video_feedback.setNestedScrollingEnabled(false);

        recycler_view_how_to_resell.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_how_to_resell.setHasFixedSize(true);
        recycler_view_how_to_resell.setNestedScrollingEnabled(false);


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment_Summary", "onResume: Start");
        Activity_Home.support_chat_fab.setVisibility(View.VISIBLE);
        if (timerTopBanner != null) {
            timerTopBanner.cancel();
        }
        if (timerFeedback != null) {
            timerFeedback.cancel();
        }

        alldata.clear();

        if (menu != null) {
            setApplozicMenu();
        }
        registerUnReadCountReceiver();


        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            //Manufacturer
            card_apply_rating.setVisibility(View.GONE);
            bannerVisible(true);
            getManufactureDashBoard();
            getBanner();
            getPublicCatalog(null, 0);
            getPrebookingCatalog();
            getPublicCatalog("most_ordered", 0);
            getPublicCatalog("most_viewed", 1);
            // getTrustedSeller(0);
            getPublicBrand();

            Log.d("FLOW", "seller" + card_apply_rating.getVisibility());
            getFeedBackBanner();
            getNonCatalog();

            getSinglePcCatalog();
            getSinglePcNonCatalog();


            // for space issue in Manufacture login
            //getActivity().findViewById(R.id.recycler_view1).setVisibility(View.GONE);
            //getActivity().findViewById(R.id.list_empty1).setVisibility(View.GONE);
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {


            //Retailer
            getDashBoard(null);

            getBanner();
            getFeedBackBanner();

            getDeepLink();
            getPublicCatalog(null, 0);
            getPrebookingCatalog();
            getWishlist(getActivity());
            getPublicBrand();


            getPublicCatalog("most_ordered", 0);
            getPublicCatalog("most_viewed", 1);
            // getTrustedSeller(0);
            bannerVisible(true);
            getNonCatalog();
            getWishListProductList();
            getUserRecommendProduct(getActivity());

            getSinglePcCatalog();
            getSinglePcNonCatalog();
            getRecentCatalogData();

            if (!UserInfo.getInstance(getActivity()).isGuest()) {
                //getReceivedCatalog1();
                getFollowedBrand(0);
            }


        } else {
            Log.d("FLOW", "wholeseller" + card_apply_rating.getVisibility());
            //Wholesaler
            callDashboardWholesaler();

            getBanner();
            //getReceivedCatalog1();
            getFeedBackBanner();
            getDeepLink();
            getPublicCatalog(null, 0);
            getPublicBrand();
            getPrebookingCatalog();


            getFollowedBrand(0);
            getWishlist(getActivity());

            getPublicCatalog("most_ordered", 0);
            getPublicCatalog("most_viewed", 1);

            //  getTrustedSeller(0);
            //getReceivedCatalog(data,0);
            bannerVisible(true);
            getNonCatalog();
            getWishListProductList();
            getUserRecommendProduct(getActivity());

            getSinglePcCatalog();
            getSinglePcNonCatalog();
            getRecentCatalogData();


            /**
             * Hide Credit Rating Feature
             * Changes done by Bhavik Gandhi April-8 2019
             */

           /* if (UserInfo.getInstance(getActivity()).isCreditRatingApply()) {
                card_apply_rating.setVisibility(View.GONE);
                linear_apply_rating.setVisibility(View.GONE);
            } else {
                card_apply_rating.setVisibility(View.VISIBLE);
                linear_apply_rating.setVisibility(View.VISIBLE);
            }*/
        }

        getVideoFeedBack();

        getHowToResellVideo();


        if (!UserInfo.getInstance(getActivity()).isGuest() && UserInfo.getInstance(getActivity()).getBroker()) {
            //Broker
            getBrokerReport();

        }

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

        /**
         * Hide Credit Rating Feature
         * Changes done by Bhavik gandhi April-8 2019
         */
        card_apply_rating.setVisibility(View.GONE);
       /* if (StaticFunctions.isOnlyReseller(getActivity())) {
            card_apply_rating.setVisibility(View.GONE);
            linear_apply_rating.setVisibility(View.GONE);
        }*/

        if (getActivity() instanceof Activity_Home) {
            ((Activity_Home) getActivity()).setMenuAfterCompany();
        }


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

    // Region #################  Home Page API Calling Start ################  //


    private void getDashBoard(final Response_DashBoard response_seller) {
        // showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "buyer_dashboard", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    data.clear();
                    brands = new ArrayList<>();
                    if (isAdded() && !isDetached()) {


                        userinfo = UserInfo.getInstance(getActivity());
                        String Status = "";
                        Response_DashBoard response_dashBoards = new Gson().fromJson(response, Response_DashBoard.class);



               /*try {
                   JSONObject abc = new JSONObject(response);
                    JSONObject profile  = abc.getJSONObject("profile");
                    String value = profile.getString("brand");
                    Log.d("HERE",abc.toString() + value.toString());
                    Status="Not Added";
                } catch (Exception e) {
                    Log.d("You have already","added");
                    Status="Added";
                }*/


                        // Log.d("profile", response_dashBoards.getProfile().toString());

                /*if (response_dashBoards.getCatalogs() != null) {
                    if (response_dashBoards.getCatalogs().getTotal_shared_with_me() != null) {
                        if (Integer.parseInt(response_dashBoards.getCatalogs().getTotal_shared_with_me()) > 0) {
                            if (Activity_Home.pref.getString("FromSplashScreen", "yes").equals("yes")) {
                                Activity_Home.tabs.getTabAt(1).select();
                                Activity_Home.pref.edit().putString("FromSplashScreen", "no").apply();
                            }
                        }
                    }
                }*/

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
                                /**Hide credit rating feature
                                 * changes done by Bhavik gandhi April-8 2019
                                 */

                                /*if (!StaticFunctions.isOnlyReseller(getActivity())) {
                                    if (UserInfo.getInstance(getActivity()).getCreditScore().equals("null")) {
                                        // Add Mode Apply
                                        card_apply_rating.setVisibility(View.VISIBLE);
                                        linear_apply_rating.setVisibility(View.VISIBLE);
                                        txt_apply_credit.setText("APPLY");
                                    } else {
                                        // Edit Mode Apply
                                        if (Integer.parseInt(UserInfo.getInstance(getActivity()).getCreditScore()) > 0) {
                                            // CRIF Apply so, hide credit rating
                                            card_apply_rating.setVisibility(View.GONE);
                                            linear_apply_rating.setVisibility(View.GONE);

                                        } else {
                                            // CRIF not apply, so edit mode
                                            card_apply_rating.setVisibility(View.VISIBLE);
                                            linear_apply_rating.setVisibility(View.VISIBLE);
                                            txt_apply_credit.setText("EDIT");
                                        }


                                    }
                                }*/

                                Activity_Home.pref.edit().putString("entered_gst", kyc_gstin).apply();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                                if (profile.has("brand")) {
                                    brands.add(new Summary_Sub("no brands", new Fragment_AddBrand(), "Add Brand"));
                                    data.add(new SummaryModel("Brands", brands));
                                    recyclerViewSectionAdapter.notifyDataSetChanged();
                                }

                                //CATALOGS
                                if (response_seller != null && response_seller.getCatalogs() != null && response_seller.getCatalogs().getUploaded_catalog() != null) {
                                    catalogsData = new ArrayList<>();
                                    String uploadedcatalogs = response_seller.getCatalogs().getUploaded_catalog().equals("0") ? "no" : response_seller.getCatalogs().getUploaded_catalog();
                                    catalogsData.add(new Summary_Sub(uploadedcatalogs + " enable", new Fragment_AddCatalog(), "Add Catalog"));
                                    data.add(new SummaryModel("Products", catalogsData));
                                }
                            }
                    /*    //Pending Products in Selection
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

                    /*if (company_type_filled != null && !company_type_filled.equals("") && !company_type_filled.equals("null")) {
                        if (!company_type_filled.equals("true")) {
                            if (Activity_Home.pref.getString("flag_company_type", "false").equals("false")) {
                                if (companyType == null) {
                                    companyType = new Dialog_CompanyType(company_type_filled);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", company_type_filled);
                                    companyType.show(getActivity().getSupportFragmentManager(), "Company Type");
                                }
                            }
                        }
                    }*/


                   /* if (is_profile_set != null) {
                        if ((!is_profile_set && !Activity_Home.pref.getBoolean("is_profile_set", false) && !isCompanyPopUpShown) || (!company_type_filled.equals("true") && !Activity_Home.pref.getBoolean("is_profile_set", false) && !isCompanyPopUpShown)) {
                            companyType = new Dialog_CompanyType();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", company_type_filled);
                            companyType.show(getActivity().getSupportFragmentManager(), "Company Type");
                            isCompanyPopUpShown = true;
                        }
                    }*/

                  /*  if (!(kyc_gstin != null && !kyc_gstin.equals("null"))) {
                        if (!Activity_Home.pref.getBoolean("kyc_gstin_popup", false) && !isKYCPopUpShown) {
                            isKYCPopUpShown = true;
                            showGSTPopup();
                        }

                    }*/

                        }

                        Log.d("companytype", UserInfo.getInstance(getActivity()).getCompanyType());

                        if (!UserInfo.getInstance(getActivity()).isGuest()) {

                    /*    if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                            if (response_seller != null) {
                                buyersData = new ArrayList<>();
                                ArrayList<CommonSummaryList> commonSummaryListsBuyers = new ArrayList<CommonSummaryList>();
                                commonSummaryListsBuyers.add(new CommonSummaryList("Buyers", Integer.parseInt(response_seller.getContacts().getApproved_buyer()), Integer.parseInt(response_seller.getContacts().getPending_buyer()), Integer.parseInt(response_seller.getContacts().getTotal_buyer_enquiry())));
                                CommonSummaryAdapter commonSummaryAdapter = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsBuyers);
                                buyersData.add(new Summary_Sub(commonSummaryAdapter));
                                data.add(new SummaryModel("Buyers", buyersData));
                            }

                        }


                        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                            suppliersData = new ArrayList<Summary_Sub>();
                            ArrayList<CommonSummaryList> commonSummaryListsSupplier = new ArrayList<CommonSummaryList>();
                            commonSummaryListsSupplier.add(new CommonSummaryList("Suppliers", Integer.parseInt(response_dashBoards.getContacts().getApproved_supplier()), Integer.parseInt(response_dashBoards.getContacts().getPending_supplier()), Integer.parseInt(response_dashBoards.getContacts().getTotal_supplier_enquiry())));
                            CommonSummaryAdapter commonSummaryAdaptersupplier = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsSupplier);
                            suppliersData.add(new Summary_Sub(commonSummaryAdaptersupplier));
                            data.add(new SummaryModel("Suppliers", suppliersData));
                        }

                        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                            purchaseOrdersData = new ArrayList<Summary_Sub>();
                            ArrayList<CommonSummaryList> commonSummaryListsPurchase = new ArrayList<CommonSummaryList>();
                            commonSummaryListsPurchase.add(new CommonSummaryList("Purchase Orders", response_dashBoards.getOrderInfo().getPurchaseorder_pending(), response_dashBoards.getOrderInfo().getPurchaseorder_dispatched(), response_dashBoards.getOrderInfo().getPurchaseorder_cancelled()));
                            CommonSummaryAdapter commonSummaryAdapterPurchase = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsPurchase);
                            purchaseOrdersData.add(new Summary_Sub(commonSummaryAdapterPurchase));
                            data.add(new SummaryModel("Purchase Orders", purchaseOrdersData));
                        }

                        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                            if (response_seller != null) {
                                saleOrdersData = new ArrayList<Summary_Sub>();
                                ArrayList<CommonSummaryList> commonSummaryListsSales = new ArrayList<CommonSummaryList>();
                                commonSummaryListsSales.add(new CommonSummaryList("Sales Orders", response_seller.getOrderInfo().getSalesorder_pending(), response_seller.getOrderInfo().getSalesorder_dispatched(), response_seller.getOrderInfo().getSalesorder_cancelled()));
                                CommonSummaryAdapter commonSummaryAdapterSales = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsSales);
                                saleOrdersData.add(new Summary_Sub(commonSummaryAdapterSales));
                                data.add(new SummaryModel("Sales Orders", saleOrdersData));
                            }

                        }*/


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
                                HomeSupplierSuggestionRatingAdapter homeSupplierSuggestionRatingAdapter = new HomeSupplierSuggestionRatingAdapter(getActivity(), supplierSuggestionList, Fragment_Summary.this);
                                temp1.add(0, new Summary_Sub(homeSupplierSuggestionRatingAdapter));
                                data.add(new SummaryModel("Request feedback & improve credit rating", temp1));
                            }

                        }


                        recyclerViewSectionAdapter.notifyDataSetChanged();
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
        //showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "seller_dashboard", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
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
                                recyclerViewSectionAdapter.notifyDataSetChanged();
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

                          /*  if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                                buyersData = new ArrayList<>();
                                ArrayList<CommonSummaryList> commonSummaryListsBuyers = new ArrayList<CommonSummaryList>();
                                commonSummaryListsBuyers.add(new CommonSummaryList("Buyers", Integer.parseInt(response_dashBoards_seller.getContacts().getApproved_buyer()), Integer.parseInt(response_dashBoards_seller.getContacts().getPending_buyer()), Integer.parseInt(response_dashBoards_seller.getContacts().getTotal_buyer_enquiry())));
                                CommonSummaryAdapter commonSummaryAdapter = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsBuyers);
                                buyersData.add(new Summary_Sub(commonSummaryAdapter));

                                try {
                                    if (data.get(8).getHeaderTitle().equals("Buyers")) {
                                        data.remove(8);
                                        data.add(8, new SummaryModel("Buyers", buyersData));
                                    } else {
                                        data.add(8, new SummaryModel("Buyers", buyersData));
                                    }
                                } catch (Exception e) {
                                    data.add(new SummaryModel("Buyers", buyersData));
                                }
                            }

                            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                                saleOrdersData = new ArrayList<Summary_Sub>();
                                ArrayList<CommonSummaryList> commonSummaryListsSales = new ArrayList<CommonSummaryList>();
                                commonSummaryListsSales.add(new CommonSummaryList("Sales Orders", response_dashBoards_seller.getOrderInfo().getSalesorder_pending(), response_dashBoards_seller.getOrderInfo().getSalesorder_dispatched(), response_dashBoards_seller.getOrderInfo().getSalesorder_cancelled()));
                                CommonSummaryAdapter commonSummaryAdapterSales = new CommonSummaryAdapter((AppCompatActivity) getActivity(), commonSummaryListsSales);
                                saleOrdersData.add(new Summary_Sub(commonSummaryAdapterSales));

                                try {
                                    if (data.get(9).getHeaderTitle().equals("Sales Orders")) {
                                        data.remove(9);
                                        data.add(9, new SummaryModel("Sales Orders", saleOrdersData));
                                    } else {
                                        data.add(9, new SummaryModel("Sales Orders", saleOrdersData));
                                    }
                                } catch (Exception e) {
                                    data.add(new SummaryModel("Sales Orders", saleOrdersData));
                                }
                            }*/

                            }


                            //getReceivedCatalog(data,6);


                        }

                        recyclerViewSectionAdapter.notifyDataSetChanged();
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

    private void getDeepLink() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HashMap<String, String> param = new HashMap<>();
        param.put("visible_on", "Home");
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.PRODUCT_TAB_PROMOTION, param, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_Promotion[] deepLinkPromotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (deepLinkPromotion.length > 0) {
                            recyclerViewDeepLink.setVisibility(View.VISIBLE);
                            ArrayList<Response_Promotion> dePromotions = new ArrayList<Response_Promotion>(Arrays.asList(deepLinkPromotion));
                            DeepLinkItemsAdapter deepLinkItemsAdapter = new DeepLinkItemsAdapter(getActivity(), dePromotions, DeepLinkItemsAdapter.DEEPLINKPRICE);
                            recyclerViewDeepLink.setAdapter(deepLinkItemsAdapter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                recyclerViewDeepLink.setVisibility(View.GONE);
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

    public void getPublicBrand() {
        /**
         * Show Only Catalog Brands(Gaurav sir)
         * Changed Feb-6 2019 by Bhavik Gandhi
         */
        String url = URLConstants.PUBLIC_BRANDS_LIST + "?limit=" + 10 + "&offset=" + 0 + "&type=public" + "&product_type=catalog";
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        ArrayList<Response_Brands> response_brandsArrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_Brands>>() {
                        }.getType());
                        if (response_brandsArrayList.size() > 0) {
                            setListItem(response_brandsArrayList);
                            animateContentOnScroll();
                        } else {
                            linear_public_brand.setVisibility(View.GONE);
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

    private void setListItem(ArrayList<Response_Brands> listData) {
        HomeBrandAdapter1 adapterList = new HomeBrandAdapter1(getActivity(), listData, recycler_view_brand);
        LinearLayoutManager viewAdapter = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_brand.setAdapter(adapterList);
        recycler_view_brand.setLayoutManager(viewAdapter);
        txt_brand_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("type", "tab");
                hashMap.put("page", "catalogs/brands");
                new DeepLinkFunction(hashMap, getActivity());
            }
        });

    }

    private void animateContentOnScroll() {
        // set animation image on scroll here
        recycler_view_brand.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                realDx = realDx + dx;
                if (realDx <= 300 && realDx > 0) {
                    relative_brand_bg_container.setAlpha(1 - realDx / 500);
                }
            }
        });
    }

    public void getRecentCatalogData() {

        recycler_view_recent.setVisibility(View.VISIBLE);
        recent_catalog_heading.setVisibility(View.VISIBLE);
        see_all_recent.setVisibility(View.VISIBLE);

        String url = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(10));
        params.put("offset", String.valueOf(0));
        url = URLConstants.userUrl(getActivity(), "recent-catalog", "");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        try {
                            if (flag_recent) {
                                txt_details.setText("SEE MORE");
                                btn_catalog_details.setRotationX(0);
                            }
                            Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                            if (response_catalog.length > 0) {
                                final ArrayList<Response_catalogMini> arrayList = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalog));
                                final ArrayList<Response_catalogMini> arrayList1 = new ArrayList<>();
                                if (arrayList.size() > 3) {
                                    arrayList1.add(arrayList.get(0));
                                    arrayList1.add(arrayList.get(1));
                                    arrayList1.add(arrayList.get(2));
                                } else {
                                    arrayList1.addAll(arrayList);
                                    see_all_recent.setVisibility(View.GONE);
                                }


                                final RecentViewedAdapter recentViewedAdapter = new RecentViewedAdapter(arrayList1, getActivity(), Fragment_Summary.this);
                                final RecentViewedAdapter recentViewedAdapter2 = new RecentViewedAdapter(arrayList, getActivity(), Fragment_Summary.this);
                                recycler_view_recent.setAdapter(recentViewedAdapter);

                                see_all_recent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (flag_recent) {
                                            recycler_view_recent.removeAllViews();
                                            recycler_view_recent.setAdapter(recentViewedAdapter);
                                            btn_catalog_details.setRotationX(0);
                                            flag_recent = false;
                                            txt_details.setText("SEE MORE");

                                        } else {
                                            recycler_view_recent.removeAllViews();
                                            recycler_view_recent.setAdapter(recentViewedAdapter2);
                                            flag_recent = true;
                                            btn_catalog_details.setRotationX(180);
                                            txt_details.setText("SEE LESS");
                                        }
                                    }
                                });


                            } else {
                                recycler_view_recent.setVisibility(View.GONE);
                                recent_catalog_heading.setVisibility(View.GONE);
                                see_all_recent.setVisibility(View.GONE);
                            }

                            //showStockCheckDialog(response_catalog);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            recycler_view_recent.setVisibility(View.GONE);
                            recent_catalog_heading.setVisibility(View.GONE);
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
                recycler_view_wishlist.setVisibility(View.GONE);
            }
        });

    }

    public void getHomeCategories() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10&is_home_display=1", null, headers, true, new HttpManager.customCallBack() {
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
                                recycler_view_category.setVisibility(View.VISIBLE);
                                ArrayList<ResponseHomeCategories> responseHomeCategoriesArrayList = new ArrayList<ResponseHomeCategories>(Arrays.asList(response_catagories));
                                HomeCategoryItemsAdapter homeCategoryItemsAdapter = new HomeCategoryItemsAdapter(getActivity(), responseHomeCategoriesArrayList, HomeCategoryItemsAdapter.HOMEDEEPLINKCATEGORY);
                                recycler_view_category.setAdapter(homeCategoryItemsAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            recycler_view_category.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                recycler_view_category.setVisibility(View.GONE);
            }
        });

    }

    private void getBanner() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.BANNER_URL + "?language_code=" + UserInfo.getInstance(getActivity()).getLanguage(), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response ", response);
                    final Response_Promotion[] promotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (promotion.length > 0) {
                            //  banner_container.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.VISIBLE);
                            final ArrayList<ImageBanner> imageBanners = new ArrayList<ImageBanner>();
                            for (Response_Promotion response_promotion : promotion) {
                                imageBanners.add(response_promotion.getImage());
                            }

                            final MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(getContext(), imageBanners, promotion);
                            viewPager.setAdapter(myCustomPagerAdapter);
                            viewPager.setCurrentItem(0, false); // set current item in the adapter to middle
                            indicator.setViewPager(viewPager);
                            final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
                            final long PERIOD_MS = 7000; // time in milliseconds between successive task executions.
                            handler = new Handler();
                            if (timerTopBanner != null) {
                                timerTopBanner.cancel();
                                timerTopBanner = null;
                            }

                            final Runnable Update = new Runnable() {
                                public void run() {
                                    if (currentPage == imageBanners.size()) {
                                        currentPage = 0;
                                    }
                                    viewPager.setCurrentItem(currentPage++, true);
                                }
                            };

                            timerTopBanner = new Timer();
                            timerTopBanner.schedule(new TimerTask() { // task to be scheduled

                                @Override
                                public void run() {
                                    handler.post(Update);
                                }
                            }, DELAY_MS, PERIOD_MS);
                        } else {
                            viewPager.setVisibility(View.GONE);
                            //banner_container.setVisibility(View.GONE);
                        }

                    } else {
                        banner_container.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                banner_container.setVisibility(View.GONE);
            }
        });
    }

    private void getFeedBackBanner() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.FEEDBACK_URL + "?language_code=" + UserInfo.getInstance(getActivity()).getLanguage() + "&review_type=ImageReview", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_Promotion[] promotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (promotion.length > 0) {
                            feedback_container.setVisibility(View.VISIBLE);
                            ArrayList<ImageBanner> imageBanners = new ArrayList<ImageBanner>();
                            for (Response_Promotion response_promotion : promotion) {
                                imageBanners.add(response_promotion.getImage());
                            }

                            final MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(getContext(), imageBanners, promotion);
                            viewPagerFeedback.setAdapter(myCustomPagerAdapter);
                            viewPagerFeedback.setCurrentItem(viewPagerFeedback.getChildCount() * myCustomPagerAdapter.LOOPS_COUNT / 2, false); // set current item in the adapter to middle


                            /*After setting the adapter use the timerTopBanner */

                            final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
                            final long PERIOD_MS = 7000; // time in milliseconds between successive task executions.
                            handlerFeedback = new Handler();
                            final Runnable Update = new Runnable() {
                                public void run() {
                                /*if (currentPage == myCustomPagerAdapter.LOOPS_COUNT*) {
                                    currentPage = 0;
                                }*/
                                    //Log.d("BannerChange", "Called curr item:"+viewPager.getCurrentItem());
                                    currentPageFeedback = viewPagerFeedback.getCurrentItem() + 1;
                                    viewPagerFeedback.setCurrentItem(currentPageFeedback, true);
                                }
                            };

                            timerFeedback = new Timer(); // This will create a new Thread
                            timerFeedback.schedule(new TimerTask() { // task to be scheduled

                                @Override
                                public void run() {
                                    handlerFeedback.post(Update);
                                }
                            }, DELAY_MS, PERIOD_MS);


                        } else {
                            feedback_container.setVisibility(View.GONE);
                        }

                    } else {
                        feedback_container.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                feedback_container.setVisibility(View.GONE);
            }
        });
    }

    private void getVideoFeedBack() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.FEEDBACK_URL + "?language_code=" + UserInfo.getInstance(getActivity()).getLanguage() + "&review_type=VideoReview", null, headers, true, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_Promotion[] promotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (promotion.length > 0) {
                            video_feedback_container.setVisibility(View.VISIBLE);
                            ArrayList<Response_Promotion> promotionArrayList = new ArrayList<Response_Promotion>(Arrays.asList(promotion));
                            recycler_view_video_feedback.setVisibility(View.VISIBLE);
                            VideoFeedBackItemAdapter videoFeedBackItemAdapter = new VideoFeedBackItemAdapter(getActivity(), promotionArrayList, 0);
                            recycler_view_video_feedback.setAdapter(videoFeedBackItemAdapter);
                        } else {
                            video_feedback_container.setVisibility(View.GONE);
                            recycler_view_video_feedback.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                video_feedback_container.setVisibility(View.GONE);
                recycler_view_video_feedback.setVisibility(View.GONE);
            }
        });
    }

    private void getHowToResellVideo() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.FEEDBACK_URL + "?language_code=" + UserInfo.getInstance(getActivity()).getLanguage() + "&review_type=ResellVideo", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_Promotion[] promotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (promotion.length > 0) {
                            linear_how_to_resell_container.setVisibility(View.VISIBLE);
                            ArrayList<Response_Promotion> promotionArrayList = new ArrayList<Response_Promotion>(Arrays.asList(promotion));
                            recycler_view_how_to_resell.setVisibility(View.VISIBLE);
                            VideoFeedBackItemAdapter videoFeedBackItemAdapter = new VideoFeedBackItemAdapter(getActivity(), promotionArrayList, 0);
                            recycler_view_how_to_resell.setAdapter(videoFeedBackItemAdapter);
                        } else {
                            linear_how_to_resell_container.setVisibility(View.GONE);
                            recycler_view_how_to_resell.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                video_feedback_container.setVisibility(View.GONE);
                recycler_view_video_feedback.setVisibility(View.GONE);
            }
        });
    }

    private void getPublicCatalog(final String listType, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final HashMap<String, String> params = new HashMap<>();
        params.put("view_type", "public");
        params.put("type", "catalog");
        params.put("ctype", "public");
        params.put("limit", String.valueOf(10));
        params.put("ready_to_ship", "true");
        params.put("product_type", Constants.PRODUCT_TYPE_CAT);
        params.put("offset", String.valueOf(0));
        params.put("ordering", "-id");
        if (listType == null) {
            params.put("total_products_gte", "2");
        }

        if (listType != null) {
            params.put(listType, "true");
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.PUBLIC_CATALOG_LIST, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);


                    final ArrayList<Response_catalogMini> publicCatalogList = new ArrayList<>();
                    Response_catalogMini[] response_catalogs = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    if (isAdded() && !isDetached()) {
                        // if(UserInfo.getInstance(getActivity())!= null && !UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                        if (response_catalogs.length > 0) {
                            for (Response_catalogMini responseCatalogs : response_catalogs) {
                                publicCatalogList.add(responseCatalogs);
                            }

                            if (listType != null) {


                                String title = "";
                                if (listType.equals("most_viewed")) {
                                    title = "Most Viewed Catalogs (7 Days)";

                                } else {
                                    title = "Most Sold Catalogs (30 Days)";
                                }
                                //pending
                                HashMap<String, String> params = new HashMap<>();
                                params.put("view_type", "public");
                                params.put(listType, "true");
                                params.put("type", "catalog");
                                params.put("ctype", "public");
                                params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                                params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                                params.put("ordering", "-id");
                                if (listType == null) {
                                    params.put("sell_full_catalog", "true");
                                }
                                try {
                                    linear_most_sold_container.setVisibility(View.VISIBLE);
                                    recycler_view_most_sold.setVisibility(View.VISIBLE);
                                    if (allDataMostSold.size() > position && allDataMostSold.get(position).getHeaderTitle().equals(title)) {
                                        allDataMostSold.remove(position);
                                        allDataMostSold.add(position, new AllDataModel(title, publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                    } else {
                                        allDataMostSold.add(position, new AllDataModel(title, publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    allDataMostSold.add(new AllDataModel(title, publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                } catch (Exception e) {
                                    allDataMostSold.add(position, new AllDataModel(title, publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                }

                                mostSoldAdapter.notifyDataSetChanged();


                            } else {
                                try {
                                    if (alldata.size() > position && alldata.get(position).getHeaderTitle().equals("Launched Catalogs")) {
                                        alldata.remove(position);
                                        alldata.add(position, new AllDataModel("Launched Catalogs", publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                    } else {
                                        alldata.add(position, new AllDataModel("Launched Catalogs", publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    alldata.add(new AllDataModel("Launched Catalogs", publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                } catch (Exception e) {
                                    alldata.add(position, new AllDataModel("Launched Catalogs", publicCatalogList, new Fragment_BrowseProduct(), 3, params));
                                }
                            }
                        }
                        if (listType != null) {
                            mostSoldAdapter.notifyDataSetChanged();
                        } else {
                            allItemAdapter.notifyDataSetChanged();
                        }
                        //  }

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

    private void getNonCatalog() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("view_type", "public");
        params.put("limit", String.valueOf(10));
        params.put("offset", String.valueOf(0));
        params.put("ready_to_ship", "true");
        params.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.PUBLIC_CATALOG_LIST, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        ArrayList<Response_catalogMini> non_catalogs = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                        }.getType());
                        if (non_catalogs.size() > 0) {
                            recycler_view_non_catalog.setVisibility(View.VISIBLE);
                            recycler_view_single_noncatalog.setNestedScrollingEnabled(false);
                            final HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("type", "catalog");
                            hashMap.put("ctype", "public");
                            hashMap.put("ready_to_ship", "true");
                            params.put("ordering", "-id");
                            hashMap.put("catalog_type", Constants.CATALOG_TYPE_NON); // for deeplink purpose
                            if (non_catalogs.size() > 0) {
                                Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                                ArrayList<AllDataModel> prebook_dataList = new ArrayList<>();
                                prebook_dataList.add(0, new AllDataModel("Non-Catalog", non_catalogs, fragment_browseCatalogs, 3, hashMap));
                                AllSectionAdapter itemAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), prebook_dataList);
                                recycler_view_non_catalog.setAdapter(itemAdapter);
                            } else {
                                recycler_view_non_catalog.setVisibility(View.GONE);
                            }
                        } else {
                            recycler_view_non_catalog.setVisibility(View.GONE);
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

    public void getPrebookingCatalog() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("view_type", "public");
        params.put("limit", String.valueOf(10));
        params.put("offset", String.valueOf(0));
        params.put("sell_full_catalog", "true");
        params.put("product_type", Constants.CATALOG_TYPE_CAT);
        params.put("ready_to_ship", "false");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.PUBLIC_CATALOG_LIST, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("view_type", "public");
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("product_type", Constants.CATALOG_TYPE_CAT);
                        params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                        params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                        params.put("ready_to_ship", "false");
                        params.put("ordering", "-id");
                        params.put("sell_full_catalog", "true");
                        ArrayList<Response_catalogMini> pre_booking_arraylist = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                        }.getType());
                        if (pre_booking_arraylist.size() > 0) {
                            Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                            Bundle bundle = new Bundle();
                            bundle.putString("ready_to_ship", "false");
                            fragment_browseCatalogs.setArguments(bundle);
                            ArrayList<AllDataModel> prebook_dataList = new ArrayList<>();
                            prebook_dataList.add(0, new AllDataModel("Pre-Launch Catalogs", pre_booking_arraylist, fragment_browseCatalogs, 3, params));
                            AllSectionAdapter preBookAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), prebook_dataList);
                            recycler_view_prebook_catalog.setAdapter(preBookAdapter);
                        } else {
                            recycler_view_prebook_catalog.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        recycler_view_prebook_catalog.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getSinglePcCatalog() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("view_type", "public");
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("sell_full_catalog", "false");
        params.put("product_type", Constants.PRODUCT_TYPE_CAT);
        params.put("ready_to_ship", "true");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("view_type", "public");
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                        params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                        params.put("product_type", Constants.PRODUCT_TYPE_CAT);
                        params.put("ordering", "-id");
                        params.put("ready_to_ship", "true");
                        params.put("sell_full_catalog", "false");
                        ArrayList<Response_catalogMini> response_arrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                        }.getType());
                        if (response_arrayList.size() > 0) {
                            Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                            ArrayList<AllDataModel> prebook_dataList = new ArrayList<>();
                            prebook_dataList.add(0, new AllDataModel("Buy Single Piece (Catalog)", response_arrayList, fragment_browseCatalogs, 3, params));
                            AllSectionAdapter preBookAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), prebook_dataList);
                            if (StaticFunctions.isOnlyReseller(getActivity())) {
                                recycler_view_single_catalog.setAdapter(preBookAdapter);
                                recycler_view_single_catalog1.setVisibility(View.GONE);
                            } else {
                                recycler_view_single_catalog1.setAdapter(preBookAdapter);
                                recycler_view_single_catalog.setVisibility(View.GONE);
                            }

                        } else {
                            recycler_view_single_catalog.setVisibility(View.GONE);
                            recycler_view_single_catalog1.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        recycler_view_single_catalog.setVisibility(View.GONE);
                        recycler_view_single_catalog1.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getSinglePcNonCatalog() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("view_type", "public");
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("sell_full_catalog", "false");
        params.put("product_type", Constants.PRODUCT_TYPE_NON);
        params.put("ready_to_ship", "true");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("view_type", "public");
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
                        params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                        params.put("product_type", Constants.PRODUCT_TYPE_NON);
                        params.put("ordering", "-id");
                        params.put("ready_to_ship", "true");
                        params.put("sell_full_catalog", "false");
                        ArrayList<Response_catalogMini> response_arrayList = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                        }.getType());
                        if (response_arrayList.size() > 0) {
                            Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                            ArrayList<AllDataModel> prebook_dataList = new ArrayList<>();
                            prebook_dataList.add(0, new AllDataModel("Buy Single Piece (Non-Catalog)", response_arrayList, fragment_browseCatalogs, 3, params));
                            AllSectionAdapter preBookAdapter = new AllSectionAdapter((AppCompatActivity) getActivity(), prebook_dataList);
                            if (StaticFunctions.isOnlyReseller(getActivity())) {
                                recycler_view_single_noncatalog.setAdapter(preBookAdapter);
                                recycler_view_single_noncatalog1.setVisibility(View.GONE);
                            } else {
                                recycler_view_single_noncatalog1.setAdapter(preBookAdapter);
                                recycler_view_single_noncatalog.setVisibility(View.GONE);
                            }
                        } else {
                            recycler_view_single_noncatalog.setVisibility(View.GONE);
                            recycler_view_single_noncatalog1.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        recycler_view_single_noncatalog.setVisibility(View.GONE);
                        recycler_view_single_noncatalog1.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getFollowedBrand(final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("follow_brand", "true");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final ArrayList<Response_catalogMini> followedCatalogList = new ArrayList<>();
                    if (isAdded() && !isDetached()) {
                        Response_catalogMini[] response_catalogs = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        if (response_catalogs.length > 0) {
                            for (Response_catalogMini responseCatalogs : response_catalogs) {
                                followedCatalogList.add(responseCatalogs);
                            }
                            try {
                                if (allDataFollowedbyMe.get(position).getHeaderTitle().equals(getResources().getString(R.string.from_brands_you_follow))) {
                                    allDataFollowedbyMe.remove(position);
                                    allDataFollowedbyMe.add(position, new AllDataModel(getResources().getString(R.string.from_brands_you_follow), followedCatalogList, new Fragment_BrandFollowedCatalogs(), 3));
                                } else {
                                    allDataFollowedbyMe.add(position, new AllDataModel(getResources().getString(R.string.from_brands_you_follow), followedCatalogList, new Fragment_BrandFollowedCatalogs(), 3));
                                }

                            } catch (IndexOutOfBoundsException e) {
                                allDataFollowedbyMe.add(new AllDataModel(getResources().getString(R.string.from_brands_you_follow), followedCatalogList, new Fragment_BrandFollowedCatalogs(), 3));
                            } catch (Exception e) {
                                allDataFollowedbyMe.add(position, new AllDataModel(getResources().getString(R.string.from_brands_you_follow), followedCatalogList, new Fragment_BrandFollowedCatalogs(), 3));
                            }
                            followedbyMeAdapter.notifyDataSetChanged();
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

    public void getUserRecommendProduct(final Context context) {
        String userId = UserInfo.getInstance(getActivity()).getUserId();
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            if (userId != null) {
                HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.userUrl(context, "user-recommendation", userId), null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override

                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            ArrayList<Response_catalogMini> responseSimilarProduct = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<Response_catalogMini>>() {
                            }.getType());
                            if (responseSimilarProduct.size() > 0) {
                                if (StaticFunctions.isOnlyReseller(getActivity())) {
                                    setUserRecommendRecyclerView();
                                    RecommendListAdapter adapter = new RecommendListAdapter(context, responseSimilarProduct, RecommendListAdapter.SIMILAR, Fragment_Summary.this, 3);
                                    recycler_view_recommded_reseller.setAdapter(adapter);
                                } else {
                                    setUserRecommendRecyclerView();
                                    RecommendListAdapter adapter = new RecommendListAdapter(context, responseSimilarProduct, RecommendListAdapter.SIMILAR, Fragment_Summary.this, 3);
                                    recycler_view_recommded.setAdapter(adapter);
                                }

                            } else {
                                linear_user_recommded.setVisibility(View.GONE);
                                linear_user_recommded_reseller.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            linear_user_recommded.setVisibility(View.GONE);
                            linear_user_recommded_reseller.setVisibility(View.GONE);
                            e.printStackTrace();
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

    public void setUserRecommendRecyclerView() {
        if (StaticFunctions.isOnlyReseller(getActivity())) {
            txt_recommded_title_reseller.setText("Suggested Products");
            linear_user_recommded_reseller.setVisibility(View.VISIBLE);
            linear_user_recommded.setVisibility(View.GONE);
            recycler_view_recommded_reseller.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recycler_view_recommded_reseller.setHasFixedSize(true);
            recycler_view_recommded_reseller.setNestedScrollingEnabled(false);
        } else {
            txt_recommded_title.setText("Suggested Products");
            linear_user_recommded_reseller.setVisibility(View.GONE);
            linear_user_recommded.setVisibility(View.VISIBLE);
            recycler_view_recommded.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recycler_view_recommded.setHasFixedSize(true);
            recycler_view_recommded.setNestedScrollingEnabled(false);
        }

    }


    // ################# Home Page API Calling End ############################  //


    private void getReceivedCatalog(final int position) {
        sharedWithMe = new ArrayList<>();
        sharedWithMeYes = new ArrayList<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("view_type", "myreceived");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    CatalogMinified[] response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified[].class);
                    if (isAdded() && !isDetached()) {
                        sharedWithMe.clear();
                        sharedWithMeYes.clear();
                        int count = 0;
                        if (response_catalogs.length > 0) {
                            for (CatalogMinified resb : response_catalogs) {
                                sharedWithMeYes.add(resb);
                                count++;
                            }
                            if (sharedWithMe.size() == 0) {
                                try {


                                    if (alldata.get(position).getHeaderTitle().equals("Received Catalogs")) {
                                        alldata.remove(position);
                                        alldata.add(position, new AllDataModel("Received Catalogs", sharedWithMeYes, new Fragment_RecievedCatalogs(), 3));
                                    } else {
                                        alldata.add(position, new AllDataModel("Received Catalogs", sharedWithMeYes, new Fragment_RecievedCatalogs(), 3));
                                    }

                                } catch (Exception e) {
                                    alldata.add(new AllDataModel("Received Catalogs", sharedWithMeYes, new Fragment_RecievedCatalogs(), 3));
                                }
                            } else {
                                try {
                                    if (alldata.get(position).getHeaderTitle().equals("Received Catalogs")) {
                                        alldata.remove(position);
                                        alldata.add(position, new AllDataModel("Received Catalogs", sharedWithMe, new Fragment_RecievedCatalogs(), 3));
                                    } else {
                                        alldata.add(position, new AllDataModel("Received Catalogs", sharedWithMe, new Fragment_RecievedCatalogs(), 3));
                                    }

                                } catch (Exception e) {
                                    alldata.add(position, new AllDataModel("Received Catalogs", sharedWithMe, new Fragment_RecievedCatalogs(), 3));
                                }
                            }
                        } else {

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

    private void getReceivedCatalog1() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("view_type", "myreceived");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        ArrayList<CatalogMinified> received_arraylist = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<CatalogMinified>>() {
                        }.getType());

                        if (received_arraylist != null && received_arraylist.size() > 0) {
                            recycler_view_received_catalog.setVisibility(View.VISIBLE);
                            ArrayList<AllDataModel> received_data_model_list = new ArrayList<>();
                            received_data_model_list.add(0, new AllDataModel("Received Products", received_arraylist, new Fragment_RecievedCatalogs(), 3));
                            AllSectionAdapter received_adapter = new AllSectionAdapter((AppCompatActivity) getActivity(), received_data_model_list);
                            recycler_view_received_catalog.setAdapter(received_adapter);
                        } else {
                            recycler_view_received_catalog.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        recycler_view_received_catalog.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void getTrustedSeller(final int position) {
       /* HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(9));
        params.put("offset", String.valueOf(0));
        params.put("trusted_seller", "true");
        params.put("view_type", "public");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        final ArrayList<Response_catalogMini> trustedCatalogList = new ArrayList<>();
                        Response_catalogMini[] response_catalogs = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        if (response_catalogs != null && response_catalogs.length > 0) {
                            recycler_view_trusted_seller.setVisibility(View.VISIBLE);
                            for (Response_catalogMini responseCatalogs : response_catalogs) {
                                trustedCatalogList.add(responseCatalogs);
                            }
                            Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                            Bundle bundle = new Bundle();
                            bundle.putString("isTrustedFilter", "true");
                            fragment_browseCatalogs.setArguments(bundle);
                            try {
                                if (trustedSellerData.get(position).getHeaderTitle().equals(getResources().getString(R.string.from_trusted_seller))) {
                                    trustedSellerData.remove(position);
                                    trustedSellerData.add(position, new AllDataModel(getResources().getString(R.string.from_trusted_seller), trustedCatalogList, fragment_browseCatalogs, 3));
                                } else {
                                    trustedSellerData.add(position, new AllDataModel(getResources().getString(R.string.from_trusted_seller), trustedCatalogList, fragment_browseCatalogs, 3));
                                }
                            } catch (IndexOutOfBoundsException e) {
                                trustedSellerData.add(new AllDataModel(getResources().getString(R.string.from_trusted_seller), trustedCatalogList, fragment_browseCatalogs, 3));
                            } catch (Exception e) {
                                trustedSellerData.add(position, new AllDataModel(getResources().getString(R.string.from_trusted_seller), trustedCatalogList, fragment_browseCatalogs, 3));
                            }
                            trustedItemAdapter.notifyDataSetChanged();
                        } else {
                            recycler_view_trusted_seller.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });*/
    }

    private void showGSTPopup() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("To create GST compliant invoice, Please add your GST number")
                .content("Enter GST no.").inputRangeRes(15, 15, R.color.color_primary)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        HashMap<String, String> params = new HashMap<>();
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        params.put("gstin", input.toString());
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""), params, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
                                    PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);

                                    // transfer_button.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "GST number added successfully", Toast.LENGTH_SHORT).show();
                                    Activity_Home.pref.edit().putBoolean("kyc_gstin_popup", true).apply();
                                    if (resPostKycGst.getGstin() != null) {
                                        Activity_Home.pref.edit().putString("kyc_gstin", resPostKycGst.getGstin()).apply();
                                        Activity_Home.pref.edit().putString("entered_gst", resPostKycGst.getGstin()).apply();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {


                                Log.d("ERRROR", error.toString());
                            }
                        });
                    }
                }).positiveText("Done")
                .negativeText("Add later")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Activity_Home.pref.edit().putBoolean("kyc_gstin_popup", true).apply();
                        dialog.dismiss();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

    }

    private void getReceivedCatalog(final ArrayList<SummaryModel> data, final int position) {
        final ArrayList<Response_ShareStatus> shareStatusesList = new ArrayList<>();
        final ArrayList<Summary_Sub> sharedByMe = new ArrayList<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(10));
        params.put("offset", String.valueOf(0));
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "mysent_catalog", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("Shared By Me response", response);
                    Response_ShareStatus[] response_shareStatuses = Application_Singleton.gson.fromJson(response, Response_ShareStatus[].class);
                    if (response_shareStatuses.length > 0) {
                        isFirstTimeLoadSharedBy = true;
                        for (int i = 0; i < response_shareStatuses.length; i++) {
                            shareStatusesList.add(response_shareStatuses[i]);
                        }

                        ShareBYMeSummaryAdapter catalogsSharedAdapter = new ShareBYMeSummaryAdapter((AppCompatActivity) getActivity(), shareStatusesList);
                        sharedByMe.add(new Summary_Sub(catalogsSharedAdapter));
                   /* if (!data.contains(sharedByMe)) {
                        data.add(0, new SummaryModel("SharedByMe", sharedByMe));
                    }*/

                        try {

                            if (data.get(position).getHeaderTitle().equals("Shared by Me")) {
                                data.remove(position);
                                data.add(position, new SummaryModel("Shared by Me", sharedByMe));
                            } else {
                                data.add(position, new SummaryModel("Shared by Me", sharedByMe));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.add(new SummaryModel("Shared by Me", sharedByMe));
                        }

                        recyclerViewSectionAdapter.notifyDataSetChanged();
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

    private void getBrokerReport() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "brokerage_order", "") + "report/", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("Broker Reposrt response", response);
                    Gson gson = new Gson();
                    broker_report = gson.fromJson(response, Response_Broker_Report[].class);
                    if (broker_report.length > 0) {
                        Log.v("Broker Reposrt response", response);
                        recycler_view_brokerreport.setVisibility(View.VISIBLE);
                        linear_broker_container.setVisibility(View.VISIBLE);
                        ArrayList<Response_Broker_Report> broker_reports = new ArrayList<>(Arrays.asList(broker_report));
                        BrokerDashboardAdapter brokerDashboardAdapter = new BrokerDashboardAdapter(getActivity(), broker_reports);
                        recycler_view_brokerreport.setAdapter(brokerDashboardAdapter);
                    } else {
                        linear_broker_container.setVisibility(View.GONE);
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
            /*HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("view_type", "wishlist");
            Application_Singleton.deep_link_filter = hashmap;
            Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);*/

            Application_Singleton.CONTAINER_TITLE = "My Wishlist";
            Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
/*
            Intent updateStatistics = new Intent("com.wishbook.catalog.update-statistic");
            updateStatistics.putExtra("action", "updateStatistics");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(updateStatistics);*/
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

         /*   Intent updateStatistics = new Intent("com.wishbook.catalog.update-statistic");
            updateStatistics.putExtra("action", "updateStatistics");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(updateStatistics);*/
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<ImageBanner> images = new ArrayList<>();
        LayoutInflater layoutInflater;
        private Response_Promotion[] response_promotions;
        public int LOOPS_COUNT = 1000;

        public MyCustomPagerAdapter(Context context, ArrayList<ImageBanner> images, Response_Promotion[] promotion) {
            this.context = context;
            this.images = images;
            this.response_promotions = promotion;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);

            final int newposition = position % response_promotions.length;
            SimpleDraweeView imageView = (SimpleDraweeView) itemView.findViewById(R.id.imageView);
            if (images.get(newposition).getFull_size().contains(".gif")) {
                StaticFunctions.loadFresco(context, images.get(newposition).getFull_size(), imageView);
            } else {
                StaticFunctions.loadFresco(context, images.get(newposition).getBanner(), imageView);
            }

            container.addView(itemView);
            //listening to image click
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // int selectedTab = response_promotions[position].getLanding_page()-1;
                    //\ Activity_Home.tabs.getTabAt(selectedTab).select();
                    //users/contact/buyer
                    //user/contact/supplier

                    if (response_promotions[newposition].getLanding_page_type() != null) {
                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "Top Banner");
                        if (response_promotions[newposition].getLanding_page_type() != null) {
                            prop.put("landing_page_type", response_promotions[newposition].getLanding_page_type());
                        }

                        if (response_promotions[newposition].getLanding_page() != null) {
                            prop.put("landing_page", response_promotions[newposition].getLanding_page());
                        }

                        sendBannerClickAnalytics(prop);

                        new CampaignLogApi(getActivity(), response_promotions[newposition].getCampaign_name());
                        Application_Singleton singleton = new Application_Singleton();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("type", response_promotions[newposition].getLanding_page_type().toLowerCase());
                        if (!response_promotions[newposition].getLanding_page_type().equals("Webview")) {
                            hashMap.put("page", response_promotions[newposition].getLanding_page() != null ? response_promotions[newposition].getLanding_page().toLowerCase() : "");
                        } else {
                            hashMap.put("page", response_promotions[newposition].getLanding_page());
                        }
                        if (response_promotions[newposition].getLanding_page_type().toLowerCase().equals("deep_link")) {
                            if (response_promotions[newposition].getLanding_page() != null) {
                                if (response_promotions[newposition].getLanding_page().contains("/?")) {
                                    hashMap.putAll(SplashScreen.getQueryString(Uri.parse(response_promotions[newposition].getLanding_page())));
                                } else {
                                    String deep_link = response_promotions[newposition].getLanding_page().toLowerCase();
                                    for (final String entry : deep_link.split("&")) {
                                        final String[] parts = entry.split("=");
                                        hashMap.put(parts[0], parts[1]);
                                    }
                                }
                            }

                        }

                        new DeepLinkFunction(hashMap, context);


                    }
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void bannerVisible(boolean isVisible) {
        try {
            if (isVisible) {
                CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);
                if (companyGroupFlag != null && (companyGroupFlag.getRetailer() || companyGroupFlag.getOnline_retailer_reseller())) {
                    reseller_banner_img.setVisibility(View.VISIBLE);
                    StaticFunctions.loadFresco(getActivity(), reseller_promotion_url, reseller_banner_img);
                    reseller_banner_img1.setVisibility(View.VISIBLE);
                    StaticFunctions.loadFresco(getActivity(), reseller_promotion_url, reseller_banner_img1);
                    reseller_banner_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("Home Resell and Earn Top", "Click", "See More");
                            HashMap<String, String> param = new HashMap<>();
                            param.put("type", "resell_earn");
                            param.put("from", "Home-In App Banner");
                            new DeepLinkFunction(param, getActivity());
                        }
                    });

                    reseller_banner_img1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("Home Resell and Earn Bottom", "Click", "See More");
                            HashMap<String, String> param = new HashMap<>();
                            param.put("type", "resell_earn");
                            param.put("from", "Home-In App Banner");
                            new DeepLinkFunction(param, getActivity());

                        }
                    });

                } else {
                    reseller_banner_img.setVisibility(View.GONE);
                    reseller_banner_img1.setVisibility(View.GONE);
                }


                second_banner_img.setVisibility(View.GONE);
                StaticFunctions.loadFresco(getActivity(), second_lehnga_url, second_banner_img);
                second_banner_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Application_Singleton.trackEvent("Home Print Banner", "Click", "See More");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("work", "2140");
                        params.put("ready_to_ship", "true");
                        new DeepLinkFunction(params, getActivity());

                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "In-App Banner");
                        prop.put("banner_type", "Home Print Banner");
                        sendBannerClickAnalytics(prop);
                    }
                });


                third_banner_img.setVisibility(View.GONE);
                StaticFunctions.loadFresco(getActivity(), third_lehnga_url, third_banner_img);
                third_banner_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Application_Singleton.trackEvent("Home Suites Banner", "Click", "See More");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("category", "13");
                        params.put("ready_to_ship", "true");
                        new DeepLinkFunction(params, getActivity());

                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "In-App Banner");
                        prop.put("banner_type", "Home Suites Banner");
                        prop.putAll(params);
                        sendBannerClickAnalytics(prop);
                    }
                });


                forth_banner_img.setVisibility(View.GONE);
                StaticFunctions.loadFresco(getActivity(), forth_lehnga_url, forth_banner_img);
                forth_banner_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Application_Singleton.trackEvent("Home Wedding Banner", "Click", "See More");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("category", "14");
                        params.put("min_price", "1500");
                        params.put("max_price", "100000");
                        params.put("ready_to_ship", "true");
                        new DeepLinkFunction(params, getActivity());

                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "In-App Banner");
                        prop.put("banner_type", "Home Wedding Banner");
                        prop.putAll(params);
                        sendBannerClickAnalytics(prop);
                    }
                });


                // Get Referral_banner_img from server config
                String referral_banner_img_path = refer_earn_promotion_url;
                if (PrefDatabaseUtils.getConfig(getActivity()) != null) {
                    ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(getActivity()), new TypeToken<ArrayList<ConfigResponse>>() {
                    }.getType());
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getKey().equals("REFERRAL_BANNER_IN_APP")) {
                            referral_banner_img_path = data.get(i).getValue();
                            break;
                        }
                    }
                }

                refer_earn_promotion_img.setVisibility(View.VISIBLE);
                StaticFunctions.loadFresco(getActivity(), referral_banner_img_path, refer_earn_promotion_img);

                refer_earn_promotion_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Application_Singleton.trackEvent("Home Refer and Earn", "Click", "See More");
                        HashMap<String, String> param = new HashMap<>();
                        param.put("type", "refer_earn");
                        param.put("from", "Home-In App Banner");
                        new DeepLinkFunction(param, getActivity());
                    }
                });


            } else {
                second_banner_img.setVisibility(View.GONE);
                third_banner_img.setVisibility(View.GONE);
                forth_banner_img.setVisibility(View.GONE);
                reseller_banner_img.setVisibility(View.GONE);
                reseller_banner_img1.setVisibility(View.GONE);
                refer_earn_promotion_img.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        ArrayList<Response_catalogMini> arrayList = new ArrayList<>();
                        for (int i = 0; i < response_catalog.length; i++) {
                            if (!response_catalog[i].isBuyer_disabled() && !response_catalog[i].isSupplier_disabled())
                                arrayList.add(response_catalog[i]);
                        }
                        if (arrayList.size() > 0) {
                            linear_wishlist.setVisibility(View.VISIBLE);
                            homeWishListAdapter = new HomeWishListAdapter(getActivity(), arrayList, HomeWishListAdapter.DEEPLINKWISHBOOK, Fragment_Summary.this);
                            recycler_view_wishlist.setAdapter(homeWishListAdapter);
                            wishlist_see_all.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                                    Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                                }
                            });
                        } else {
                            linear_wishlist.setVisibility(View.GONE);
                            recycler_view_wishlist.setVisibility(View.GONE);
                        }

                    }

                } catch (JsonSyntaxException e) {
                    Log.e("WISH", "JsonSyntaxException: ");
                    e.printStackTrace();
                    linear_wishlist.setVisibility(View.GONE);
                    recycler_view_wishlist.setVisibility(View.GONE);
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
                        hideProgress();
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



                  /*  if (creditRatings.length > 0) {
                        UserInfo.getInstance(getActivity()).setCreditRating(true);
                    } else {
                        UserInfo.getInstance(getActivity()).setCreditRating(false);
                    }

                    if (UserInfo.getInstance(getActivity()).isCreditRatingApply()) {
                        card_apply_rating.setVisibility(View.GONE);
                        linear_apply_rating.setVisibility(View.GONE);
                    } else {
                        card_apply_rating.setVisibility(View.VISIBLE);
                        linear_apply_rating.setVisibility(View.VISIBLE);
                    }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    Log.v("error response", error.getErrormessage());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.e(TAG, "onActivityResult Summary: ");
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

            if (requestCode == Application_Singleton.STORY_VIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                notifyStoryAdapter();
                if (homeStoriesAdapter != null && storyModels.size() > 0) {
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

    private void getAllStoriesList() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.STORIES, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        try {
                            hideProgress();
                            Type type = new TypeToken<ArrayList<ResponseStoryModel>>() {
                            }.getType();

                            ArrayList<ResponseStoryModel> temp = Application_Singleton.gson.fromJson(response, type);
                            storyModels = new ArrayList<>();
                            for (int i = 0; i < temp.size(); i++) {
                                // Remove those stories who have not catalogs
                                if (temp.get(i).getCatalogs().size() > 0)
                                    storyModels.add(temp.get(i));
                            }
                            /**
                             * https://wishbook.atlassian.net/browse/WB-2922
                             * Randomize stories
                             */
                            Collections.shuffle(storyModels);
                            if (storyModels.size() > 0) {
                                recycler_view_stories.setVisibility(View.VISIBLE);
                                linear_stories.setVisibility(View.VISIBLE);
                                homeStoriesAdapter = new HomeStoriesAdapter(getActivity(), storyModels, HomeStoriesAdapter.STORY, Fragment_Summary.this);
                                recycler_view_stories.setAdapter(homeStoriesAdapter);
                                notifyStoryAdapter();
                            } else {
                                recycler_view_stories.setVisibility(View.GONE);
                                linear_stories.setVisibility(View.GONE);
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
                hideProgress();
            }
        });

    }

    private void notifyStoryAdapter() {
        if (homeStoriesAdapter != null && storyModels.size() > 0) {
            if (PrefDatabaseUtils.getPrefStoryCompletion(getActivity()) != null) {
                HashMap<String, ArrayList<String>> responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefStoryCompletion(getActivity()), new TypeToken<HashMap<String, ArrayList<String>>>() {
                }.getType());

                for (int i = 0; i < storyModels.size(); i++) {
                    if (responseData.containsKey(storyModels.get(i).getId())) {
                        storyModels.get(i).setCompletion_count(responseData.get(storyModels.get(i).getId()).size());
                    }
                }
                homeStoriesAdapter.notifyDataSetChanged();
            }
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
                            hideProgress();
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
                    hideProgress();
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
                    hideProgress();
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

}

