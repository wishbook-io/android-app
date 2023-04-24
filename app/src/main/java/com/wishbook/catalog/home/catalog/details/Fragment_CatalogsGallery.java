package com.wishbook.catalog.home.catalog.details;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.OpenContextBasedApplogicChat;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonadapters.AllItemAdapterChoose;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.AddBrandDistributor;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.Response_BrandSell;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostPushUserId;
import com.wishbook.catalog.commonmodels.responses.BrandFollowed;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.DeliveryInfo;
import com.wishbook.catalog.commonmodels.responses.Eavdata;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogEnquiry;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.ResponseWishListAdd;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.ResellerCatalogShareBottomSheet;
import com.wishbook.catalog.home.catalog.StartStopHandler;
import com.wishbook.catalog.home.catalog.add.Activity_Become_Seller;
import com.wishbook.catalog.home.catalog.add.CatalogBottomSheetDialogFragment;
import com.wishbook.catalog.home.catalog.add.Dialog_ClearanceDiscount;
import com.wishbook.catalog.home.catalog.add.Fragment_AddAdditionalImages;
import com.wishbook.catalog.home.catalog.share.CatalogShareHolder;
import com.wishbook.catalog.home.catalog.share.Fragment_WhatsAppSelection;
import com.wishbook.catalog.home.catalog.social_share.BottomShareDialog;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.models.ThumbnailObj;
import com.wishbook.catalog.home.more.myviewers_2.Activity_MyViewersDetail;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_CatalogsGallery extends GATrackedFragment
        implements SwipeRefreshLayout.OnRefreshListener, AllItemAdapterChoose.GridSelectListener {

    public static final String PURCHASE = "purchase";
    public static final String BROKERAGE = "brokerage";
    public static final String SELL = "sell";
    public static final String CHATSUPPLIER = "chatSupplier";
    public static final String SENDENQUIRY = "sendenquiry";
    public static final String ALLACTION = "allaction";
    public static final String CHATENQUIRY = "chat_enquiry";
    public static final String ENABLE_LABEL = "Start Selling";
    public static final String DISABLE_LABEL = "Stop Selling";
    public static final int BECOME_SELLER_REQUEST = 1502;
    public static final int ADD_EDIT_SCREEN_SET = 1602;
    public static final String ASK_PERMISSION = "This brand is not yet on your \"Brand I Sell\" list. Do you want to add this brand and sell this catalog?";
    public Response_catalog response_catalog;
    AlertDialog dialog;
    MenuItem share_menu;
    SimpleDraweeView img_brand_logo, img_brand_logo_2;
    TextView txt_brand_name, txt_brand_name_2;
    LinearLayout relative_follow_brand;
    LinearLayout linear_flow_button;
    @BindView(R.id.linear_dispatch_detail)
    LinearLayout linear_dispatch_detail;
    @BindView(R.id.txt_dispatch_value)
    TextView txt_dispatch_value;
    /* btn_enable_catalog*/;
    @BindView(R.id.linear_single_seller_policy)
    LinearLayout linear_single_seller_policy;

    @BindView(R.id.txt_price)
    TextView txt_price;
    @BindView(R.id.txt_number_design)
    TextView txt_number_design;
    @BindView(R.id.txt_catalog_detail_number_design)
    TextView txt_catalog_detail_number_design;

    @BindView(R.id.txt_label_design)
    TextView txt_label_design;
    @BindView(R.id.txt_catalog_detail_label_design)
    TextView txt_catalog_detail_label_design;

    @BindView(R.id.txt_category)
    TextView txt_category;
    @BindView(R.id.txt_catalog_detail_category)
    TextView txt_catalog_detail_category;

    @BindView(R.id.relative_design)
    RelativeLayout relative_design;
    @BindView(R.id.txt_sold_by)
    TextView txt_sold_by;
    @BindView(R.id.img_trusted)
    ImageView img_trusted;
    @BindView(R.id.wishlogo)
    ImageView wishlogo;
    @BindView(R.id.txt_location)
    TextView txt_location;
    @BindView(R.id.linear_seller_rating)
    LinearLayout linear_seller_rating;
    @BindView(R.id.txt_rating)
    TextView txt_rating;
    @BindView(R.id.linear_size_container)
    LinearLayout linear_size_container;
    @BindView(R.id.txt_size_value)
    TextView txt_size_value;
    @BindView(R.id.linear_material)
    LinearLayout linear_material;
    @BindView(R.id.linear_seller)
    LinearLayout linear_seller;

    @BindView(R.id.card_catalog_summary)
    LinearLayout card_catalog_summary;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.relative_other_details)
    RelativeLayout relative_other_details;
    @BindView(R.id.txt_other)
    TextView txt_other;


    @BindView(R.id.relative_size)
    RelativeLayout relative_size;


    @BindView(R.id.list_empty1)
    TextView list_empty1;
    @BindView(R.id.btn_become_seller)
    TextView btn_become_seller;
    @BindView(R.id.single_supplier_details)
    LinearLayout single_supplier_details;
    @BindView(R.id.txt_become_seller_status)
    TextView txt_become_seller_status;
    @BindView(R.id.linear_brand_follow_container)
    FlexboxLayout linear_brand_follow_container;

    @BindView(R.id.linear_brand_follow_container_2)
    FlexboxLayout linear_brand_follow_container_2;

    @BindView(R.id.relative_become_seller)
    RelativeLayout relative_become_seller;
    @BindView(R.id.txt_see_all_seller)
    TextView txt_see_all_seller;
    @BindView(R.id.linear_txt_see_all_seller)
    LinearLayout linear_txt_see_all_seller;
    @BindView(R.id.relative_seller)
    RelativeLayout relative_seller;
    @BindView(R.id.linear_btn_catalog)
    LinearLayout linear_btn_catalog;

    @BindView(R.id.img_header_left_nav)
    ImageView img_header_left_nav;

    @BindView(R.id.img_header_right_nav)
    ImageView img_header_right_nav;


    @BindView(R.id.btn_enable_disable)
    TextView btn_enable_disable;
    @BindView(R.id.btn_sales_order1)
    TextView btn_sales_order1;
    @BindView(R.id.btn_enquiry_chat)
    AppCompatButton btn_enquiry_chat;
    @BindView(R.id.linear_return_policy)
    LinearLayout linear_return_policy;
    @BindView(R.id.linear_delivery_time)
    LinearLayout linear_delivery_time;
    @BindView(R.id.txt_delivery_value)
    TextView txt_delivery_value;
    @BindView(R.id.txt_return_value)
    TextView txt_return_value;
    @BindView(R.id.single_seller_details)
    LinearLayout single_seller_details;
    @BindView(R.id.relative_single_seller_location)
    RelativeLayout relative_single_seller_location;
    @BindView(R.id.multiple_seller)
    LinearLayout multiple_seller;
    @BindView(R.id.txt_single_piece_price)
    TextView txt_single_piece_price;
    @BindView(R.id.btn_broker)
    AppCompatButton btn_broker;
    @BindView(R.id.cart_info)
    TextView cart_info;
    @BindView(R.id.txt_wishlist_layout)
    LinearLayout txt_wishlist_layout;
    @BindView(R.id.relative_received_price)
    RelativeLayout relative_received_price;
    @BindView(R.id.relative_full_price)
    RelativeLayout relative_full_price;
    @BindView(R.id.relative_single_price)
    RelativeLayout relative_single_price;
    @BindView(R.id.txt_received_price)
    TextView txt_received_price;
    @BindView(R.id.txt_catalog_name)
    TextView txt_catalog_name;
    @BindView(R.id.relative_category)
    RelativeLayout relative_category;
    @BindView(R.id.recycler_products_header)
    RecyclerView mRecyclerViewHeader;
    @BindView(R.id.img_cover)
    SimpleDraweeView img_cover;
    @BindView(R.id.img_cover_disable)
    RelativeLayout img_cover_disable;
    @BindView(R.id.linear_share)
    LinearLayout linear_share;
    @BindView(R.id.linear_share_2)
    LinearLayout linear_share_2;
    @BindView(R.id.linear_add_product)
    LinearLayout linear_add_product;
    @BindView(R.id.img_disable)
    ImageView img_disable;
    @BindView(R.id.txt_products_header_empty_text)
    TextView txt_products_header_empty_text;
    @BindView(R.id.txt_delivery_label)
    TextView txt_delivery_label;
    @BindView(R.id.txt_ready_to_dispatch)
    TextView txt_ready_to_dispatch;
    @BindView(R.id.full_catalog_label)
    TextView full_catalog_label;
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
    // ## Screen Flow Button ######
    @BindView(R.id.linear_screen_owner_btn)
    LinearLayout linear_screen_owner_btn;

    // ### Seller Approval Layout variable #####
    @BindView(R.id.btn_edit_screen_set)
    AppCompatButton btn_edit_screen_set;
    @BindView(R.id.btn_add_screen_set)
    AppCompatButton btn_add_screen_set;
    @BindView(R.id.linear_color)
    LinearLayout linear_color;
    @BindView(R.id.txt_color_value)
    TextView txt_color_value;
    boolean visible_flag;

    @BindView(R.id.txt_size_chart)
    TextView txt_size_chart;

    @BindView(R.id.txt_copy_details)
    TextView txt_copy_details;

    // ### Similar Product ######
    @BindView(R.id.recycler_view_similar)
    RecyclerView recycler_view_similar;
    @BindView(R.id.linear_similar)
    LinearLayout linear_similar;
    @BindView(R.id.txt_similar_title)
    TextView txt_similar_title;
    @BindView(R.id.sectionMore)
    TextView sectionMore;

    @BindView(R.id.linear_eav_data)
    LinearLayout linear_eav_data;


    @BindView(R.id.btn_support_chat)
    TextView btn_support_chat;

    @BindView(R.id.txt_more_category)
    TextView txt_more_category;

    // ### Delivery Info ####
    @BindView(R.id.txt_delivery_cod_value)
    TextView txt_delivery_cod_value;

    @BindView(R.id.txt_delivery_shipping_options)
    TextView txt_delivery_shipping_options;

    @BindView(R.id.txt_delivery_delivery_value)
    TextView txt_delivery_delivery_value;

    @BindView(R.id.linear_delivery_info_container)
    LinearLayout linear_delivery_info_container;

    @BindView(R.id.linear_delivery_delivery)
    LinearLayout linear_delivery_delivery;
    @BindView(R.id.linear_delivery_shipping_option)
    LinearLayout linear_delivery_shipping_option;
    @BindView(R.id.linear_delivery_cod)
    LinearLayout linear_delivery_cod;

    @BindView(R.id.btn_check_delivery_info)
    AppCompatButton btn_check_delivery_info;

    @BindView(R.id.input_pincode)
    TextInputLayout input_pincode;

    @BindView(R.id.edit_pincode)
    EditText edit_pincode;

    @BindView(R.id.linear_delivery_root_container)
    LinearLayout linear_delivery_root_container;

    //  ##### Rating-Review  ##### //

    @BindView(R.id.txt_all_review)
    TextView txt_all_review;

    @BindView(R.id.txt_total_rating_review)
    TextView txt_total_rating_review;

    @BindView(R.id.txt_avg_rating)
    TextView txt_avg_rating;

    @BindView(R.id.rating_root_container)
    LinearLayout rating_root_container;

    @BindView(R.id.linear_rating_images)
    LinearLayout linear_rating_images;

    @BindView(R.id.txt_no_of_review_photos)
    TextView txt_no_of_review_photos;

    @BindView(R.id.flex_review_photos)
    FlexboxLayout flex_review_photos;

    @BindView(R.id.txt_extra_review_count)
    TextView txt_extra_review_count;


    // ### Product ID Show ### //
    @BindView(R.id.relative_product_id)
    RelativeLayout relative_product_id;

    @BindView(R.id.txt_product_id_label)
    TextView txt_product_id_label;

    @BindView(R.id.txt_product_id_value)
    TextView txt_product_id_value;

    // ### Referral Banner
    @BindView(R.id.refer_earn_promotion_img_2)
    SimpleDraweeView refer_earn_promotion_img_2;

    // ### MWP Price
    @BindView(R.id.txt_mwp_price)
    TextView txt_mwp_price;

    @BindView(R.id.txt_single_clearance_discount)
    TextView txt_single_clearance_discount;

    @BindView(R.id.txt_full_clearance_discount)
    TextView txt_full_clearance_discount;

    @BindView(R.id.txt_my_clerance_discount)
    TextView txt_my_clerance_discount;

    @BindView(R.id.linear_clearance_discount_root_container)
    LinearLayout linear_clearance_discount_root_container;

    @BindView(R.id.linear_clearance_discount)
    LinearLayout linear_clearance_discount;

    @BindView(R.id.txt_clearance_discount_label)
    TextView txt_clearance_discount_label;

    @BindView(R.id.txt_my_catalog_seller_note1)
    TextView txt_my_catalog_seller_note1;

    @BindView(R.id.txt_view_tax_link)
    TextView txt_view_tax_link;

    @BindView(R.id.btn_brand_follow)
    TextView btn_brand_follow;

    @BindView(R.id.txt_material_detail)
    TextView txt_material_detail;

    @BindView(R.id.linear_upper_action_bar)
    LinearLayout linear_upper_action_bar;

    @BindView(R.id.catalog_sticky_bar)
    LinearLayout catalog_sticky_bar;


    @BindView(R.id.img_share_2)
    ImageView img_share_2;

    @BindView(R.id.img_disable_2)
    ImageView img_disable_2;

    @BindView(R.id.linear_add_product_2)
    LinearLayout linear_add_product_2;

    @BindView(R.id.txt_wishlist_layout_2)
    LinearLayout txt_wishlist_layout_2;

    @BindView(R.id.wishlogo_2)
    ImageView wishlogo_2;


    boolean isSeeMore = false;
    Handler handler;
    Runnable runnable;
    Animation shake;
    private RecyclerViewEmptySupport
            mRecyclerView;
    private View v;
    private AllItemAdapterChoose allItemAdapter;
    private UserInfo userinfo;
    private String shareText;
    private UserInfo info;
    private AppCompatButton btn_send_enquiry, btn_purchase, btn_chat_supplier;
    private SwipeRefreshLayout swipe_container;
    private TextView txt_wishlist;
    private UpdateUIBecomeSellerListener updateUIBecomeSellerListener;
    private updateListEnableDisable updateListEnableDisable;

    String from = "";

    boolean isSendAnalytics;

    String product_id;

    boolean isWhatsappStepone, isWhatsappStepTwo;

    ProductMyDetail productMyDetail;
    AllItemAdapterChoose header_adapter;

    private static final String HTML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

    boolean isProductWithSize;
    ArrayList<String> system_sizes = new ArrayList<>();

    boolean isGetMyDetails = false;
    int isMyCatalog = 0;


    public Fragment_CatalogsGallery() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_catalog_detail, ga_container, true);
        ButterKnife.bind(this, v);
        try {
            shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getArguments() != null && getArguments().getString("from") != null) {
            from = getArguments().getString("from");
        }
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        list_empty1.setVisibility(View.GONE);

        //bottom flow layout
        linear_flow_button = (LinearLayout) v.findViewById(R.id.linear_flow_button);
        btn_send_enquiry = (AppCompatButton) v.findViewById(R.id.btn_send_enquiry);
        btn_purchase = (AppCompatButton) v.findViewById(R.id.btn_purchase);
        // btn_sales_order = (AppCompatButton) v.findViewById(R.id.btn_sales_order);
        btn_chat_supplier = (AppCompatButton) v.findViewById(R.id.btn_chat_supplier);
        //  btn_enable_catalog = (AppCompatButton) v.findViewById(R.id.btn_enable_catalog);

        // brand follow bar control initialize
        relative_follow_brand = v.findViewById(R.id.liner_follow_brand);
        img_brand_logo = (SimpleDraweeView) v.findViewById(R.id.img_brand_logo);
        img_brand_logo_2 = v.findViewById(R.id.img_brand_logo_2);
        txt_brand_name_2 = v.findViewById(R.id.txt_brand_name_2);
        txt_brand_name = (TextView) v.findViewById(R.id.txt_brand_name);
        btn_brand_follow = v.findViewById(R.id.btn_brand_follow);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        userinfo = UserInfo.getInstance(getActivity());
        txt_wishlist = v.findViewById(R.id.txt_wishlist);
        try {
            if (userinfo.getGroupstatus().equals("2")) {
                linear_share.setVisibility(View.GONE);
                linear_share_2.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info = UserInfo.getInstance(getActivity());
        initSwipeRefresh(v);
        return v;
    }

    public void initCall(boolean isShowCache) {

        if (getArguments() != null && getArguments().getString("product_id") != null) {
            product_id = getArguments().getString("product_id");
            showCatalogs(isShowCache);
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        if (isWhatsappStepone) {
            openShareDetailsDialog(StaticFunctions.SHARETYPE.WHATSAPP);
        }
        super.onResume();
        try {
            if (getArguments() != null && getArguments().getString("isRemoveCache") != null) {
                String isCache = getArguments().getString("isRemoveCache");
                if (isCache.equalsIgnoreCase("true")) {
                    initCall(false);
                } else {
                    initCall(true);
                }
            } else {
                initCall(true);
            }
            if (getActivity() instanceof OpenContainer) {
                if (getArguments() != null && getArguments().getString("product_id") != null) {
                    ((OpenContainer) getActivity()).toolbar.setTitle("Product ID: " + getArguments().getString("product_id"));
                }

                share_menu = ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.menu_share);
                if (userinfo.getGroupstatus().equals("2")) {
                    // Sales person
                    linear_share.setVisibility(View.GONE);
                    linear_share_2.setVisibility(View.GONE);
                } else {
                    if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                        if (getActivity() instanceof OpenContainer) {
                            // hide toolbar share option
                            linear_share.setVisibility(View.GONE);
                            linear_share_2.setVisibility(View.GONE);
                        }
                        try {
                            ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart).setVisible(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart).setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        linear_share.setVisibility(View.VISIBLE);
                        linear_share_2.setVisibility(View.VISIBLE);
                        visible_flag = true;
                    }
                }


            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void initRatingView() {
        if (response_catalog.getProduct_rating() != null && response_catalog.getProduct_rating().getAvg_rating() != null) {
            rating_root_container.setVisibility(View.VISIBLE);
            if (response_catalog.getProduct_rating() != null && response_catalog.getProduct_rating().getAvg_rating() != null) {
                txt_avg_rating.setText(response_catalog.getProduct_rating().getAvg_rating());
            }
            StringBuffer total_rating = new StringBuffer();
            if (Integer.parseInt(response_catalog.getProduct_rating().getTotal_rating()) > 0) {
                total_rating.append(response_catalog.getProduct_rating().getTotal_rating() + " rating ");
            }
            if (Integer.parseInt(response_catalog.getProduct_rating().getTotal_review()) > 0) {
                total_rating.append("& " + response_catalog.getProduct_rating().getTotal_review() + " reviews");
            }

            txt_total_rating_review.setText(total_rating);
            txt_total_rating_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id", response_catalog.getId());
                    bundle.putSerializable("product_rating", response_catalog.getProduct_rating());
                    Fragment_ProductAllReview productAllReview = new Fragment_ProductAllReview();
                    productAllReview.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "All reviews";
                    Application_Singleton.CONTAINERFRAG = productAllReview;
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                }
            });
            if (response_catalog.getProduct_rating().getReview_photos() != null && response_catalog.getProduct_rating().getReview_photos().size() > 0) {
                linear_rating_images.setVisibility(View.VISIBLE);
                txt_no_of_review_photos.setText("" + response_catalog.getProduct_rating().getReview_photos().size() + " " + "customer photos");
                flex_review_photos.removeAllViews();
                int show_max_thumb = response_catalog.getProduct_rating().getReview_photos().size();
                if (show_max_thumb > 3)
                    show_max_thumb = 3;

                for (int i = 0; i < show_max_thumb; i++) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.product_thumb_item, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, StaticFunctions.dpToPx(getActivity(), 6), 0);
                    view.setLayoutParams(params);
                    int finalI = i;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photos_intent = new Intent(getActivity(), Activity_ProductPhotos.class);
                            photos_intent.putExtra("review_photos", response_catalog.getProduct_rating().getReview_photos());
                            photos_intent.putExtra("position", finalI);
                            getActivity().startActivity(photos_intent);
                        }
                    });
                    StaticFunctions.loadFresco(getActivity(), response_catalog.getProduct_rating().getReview_photos().get(i).getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.product_img));
                    flex_review_photos.addView(view);
                }
                if (response_catalog.getProduct_rating().getReview_photos() != null && response_catalog.getProduct_rating().getReview_photos().size() > 3) {
                    flex_review_photos.addView(txt_extra_review_count);
                    txt_extra_review_count.setVisibility(View.VISIBLE);
                    txt_extra_review_count.setText("+" + (response_catalog.getProduct_rating().getReview_photos().size() - 3) + "\nmore");

                    txt_extra_review_count.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photos_intent = new Intent(getActivity(), Activity_ProductPhotos.class);
                            photos_intent.putExtra("review_photos", response_catalog.getProduct_rating().getReview_photos());
                            photos_intent.putExtra("position", (3 + 1));
                            getActivity().startActivity(photos_intent);
                        }
                    });
                } else {
                    txt_extra_review_count.setVisibility(View.GONE);
                }
            } else {
                linear_rating_images.setVisibility(View.GONE);
            }

        } else {
            rating_root_container.setVisibility(View.GONE);
        }
    }

    private void initDeliveryInfoListener() {
        if (response_catalog.is_owner() || response_catalog.is_seller()) {
            linear_delivery_root_container.setVisibility(View.GONE);
        }
        btn_check_delivery_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_pincode != null && edit_pincode.length() == 6)
                    getDeliveryInfo(getActivity(), edit_pincode.getText().toString(), response_catalog.getId());
                else {
                    // validation error
                    if (edit_pincode.getText().toString().isEmpty()) {
                        edit_pincode.setError("Please enter pincode");
                    } else {
                        edit_pincode.setError("Please enter valid pincode");
                    }

                }
            }
        });
    }

    public void showShareBottomSheet(final Context context, String product_type, final String product_id) {
        if (UserInfo.getInstance(context).isGuest()) {
            StaticFunctions.ShowRegisterDialog(context, "Product Detail");
            return;
        }

        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(context).getCompanyGroupFlag(), CompanyGroupFlag.class);
        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {

            // Open Reseller Share Bottom Sheet
            ResellerCatalogShareBottomSheet resellerCatalogShareBottomSheet = ResellerCatalogShareBottomSheet.newInstance(response_catalog.getId());
            resellerCatalogShareBottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "Custom Bottom Sheet");

        } else {
            if (UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                visible_flag = false;
            } else {
                visible_flag = true;
            }

            if (product_type != null && product_type.equals(Constants.PRODUCT_TYPE_SCREEN)) {
                visible_flag = false;
            }


            boolean whatsapp_business = StaticFunctions.appInstalledOrNot("com.whatsapp.w4b", context);

            BottomShareDialog bottomSheetDialog = BottomShareDialog.getInstance(visible_flag, whatsapp_business);
            bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Custom Bottom Sheet");
            bottomSheetDialog.setDismissListener(new BottomShareDialog.DismissListener() {
                @Override
                public void onDismiss(StaticFunctions.SHARETYPE type) {
                    if (type == StaticFunctions.SHARETYPE.FACEBOOK) {
                        fbShare(response_catalog, context, product_id);
                    } else if (type == StaticFunctions.SHARETYPE.WHATSAPP || type == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
                        whatsappShare(response_catalog, context, product_id, type);
                    } else if (type == StaticFunctions.SHARETYPE.ONWISHBOOK) {
                        wishbookShare(context, product_id);
                    } else if (type == StaticFunctions.SHARETYPE.GALLERY) {
                        checkStorePermission(context, response_catalog, product_id, true);
                    } else if (type == StaticFunctions.SHARETYPE.OTHER) {
                        otherShare(response_catalog, context, product_id, false);
                    } else if (type == StaticFunctions.SHARETYPE.LINKSHARE) {
                        linkShare(response_catalog, context, product_id);
                    }
                }
            });
        }


    }

    private void showCatalogs(boolean isShowCache) {
        //CRASHING HERE FIND SOME POINT
        String catalog = null;
        String catalog_type = "catalog";


        final HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());


        if (catalog_type != null && catalog_type.equals("catalog")) {

            showProgress();
            String url;
            url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", product_id);
            HashMap<String, String> param = new HashMap<>();
            param.put("show_all_products", "true");
            if (isMyCatalog > 0 && isMyCatalog == 1) {
                param.put("view_type", "mycatalogs");
            }
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, isShowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    onServerResponse(response, false);
                }

                @Override

                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (isAdded() && !isDetached()) {

                            actionsAfterCatalogDetailResponse(response);

                            //updateCatalogUI(response);
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } else {

            //selection not in use

        }
    }

    private void actionsAfterCatalogDetailResponse(String response) {
        response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
        if (response_catalog.getEavdatajson() != null) {
            response_catalog.setEavdata(Application_Singleton.gson.fromJson(response_catalog.getEavdatajson(), Eavdata.class));
        }
        Application_Singleton.setResponse_catalog(response_catalog);
        getSizeEav(getActivity(), response_catalog.getCategory());
        getMyDetails(getActivity(), response_catalog.getId()); //calling updateCatalogUI at the end
        sendProductAttributes(getActivity(), response_catalog);
    }

    private void updateCatalogUI() {
        enableCatalogUpperButton();
        btn_become_seller.setVisibility(View.GONE);
        btn_enable_disable.setVisibility(View.GONE);
        btn_purchase.setVisibility(View.GONE);
        btn_purchase.setEnabled(true);
        btn_send_enquiry.setVisibility(View.GONE);
        btn_chat_supplier.setVisibility(View.GONE);
        btn_sales_order1.setVisibility(View.GONE);
        btn_enquiry_chat.setVisibility(View.GONE);
        relative_full_price.setVisibility(View.VISIBLE);
        txt_view_tax_link.setPaintFlags(txt_view_tax_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (!response_catalog.is_seller()) {
            txt_view_tax_link.setVisibility(View.VISIBLE);
            txt_view_tax_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openViewTaxDialog();
                }
            });
        } else {
            txt_view_tax_link.setVisibility(View.GONE);
        }
        btn_purchase.setText("ADD TO CART");
        btn_purchase.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.orange));
        cart_info.setText("");
        try {
            int productCount = 0, fullCount = 0;
            boolean fullcatalog = false, singlepiece = false, both = false;
            StaticFunctions.loadFresco(getActivity(), response_catalog.getImage().getThumbnail_small(), img_cover);
            if (response_catalog.isSupplier_disabled()) {
                img_cover_disable.setVisibility(View.VISIBLE);
                img_cover.setAlpha(0.5f);
            } else {
                img_cover.setAlpha(1f);
                img_cover_disable.setVisibility(View.GONE);
            }

            img_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (allItemAdapter != null && allItemAdapter.getItemCount() > 0) {
                        allItemAdapter.coverImageClick();
                    }
                }
            });
            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                }.getType();
                ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
                if (saveCartData != null && saveCartData.size() > 0) {
                    for (int i = 0; i < saveCartData.size(); i++) {
                        if (saveCartData.get(i).getId().equals(response_catalog.getId())) {
                            if (saveCartData.get(i).getProducts() == null || saveCartData.get(i).getProducts().getId() == null || saveCartData.get(i).getProducts().getId().size() == 0) {
                                fullcatalog = true;
                                fullCount++;
                            } else {
                                both = true;
                            }
                        }
                    }

                    for (int i = 0; i < saveCartData.size(); i++) {

                        if (both && saveCartData.get(i).getId().equals(response_catalog.getId())) {

                            for (int j = 0; j < response_catalog.getProducts().size(); j++) {

                                if (saveCartData.get(i).getProducts().getId().contains(response_catalog.getProducts().get(j).getId())) {
                                    productCount = productCount + 1;
                                    singlepiece = true;
                                }
                            }
                        }
                    }
                    if (fullcatalog) {
                        cart_info.setVisibility(View.VISIBLE);
                        cart_info.setText("" + (fullCount) + " complete set of this catalog is added to your cart");
                        btn_purchase.setText("GO TO CART");
                    }

                    if (singlepiece) {
                        cart_info.setVisibility(View.VISIBLE);
                        if (productCount == 1) {
                            cart_info.setText("" + productCount + " individual design of this catalog is added to your cart");
                        } else {
                            cart_info.setText("" + productCount + " individual designs of this catalog are added to your cart");
                        }
                       /* if (Application_Singleton.isFromGallery != null && Application_Singleton.isFromGallery.equals(CatalogHolder.MYRECEIVED)) {
                            btn_purchase.setText("BUY NOW");
                        } else {
                            btn_purchase.setText("ADD TO CART");
                        }*/

                        btn_purchase.setText("ADD TO CART");
                    }

                    if (fullcatalog && both) {
                        cart_info.setVisibility(View.VISIBLE);
                        if (productCount == 1) {
                            cart_info.setText("" + (fullCount) + " complete set +" + productCount + " individual design of this catalog are added to your cart");
                        } else {
                            cart_info.setText("" + (fullCount) + " complete set +" + productCount + " individual designs of this catalog are added to your cart");
                        }
                        btn_purchase.setText("GO TO CART");
                    }

                } else {
                    /*if (Application_Singleton.isFromGallery != null && Application_Singleton.isFromGallery.equals(CatalogHolder.MYRECEIVED)) {
                        btn_purchase.setText("BUY NOW");
                    } else {
                        btn_purchase.setText("ADD TO CART");
                    }*/
                    btn_purchase.setText("ADD TO CART");
                    cart_info.setText("");
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
            if (response_catalog.getPush_user_id() != null) {


                btn_purchase.setVisibility(View.VISIBLE);


                if (response_catalog.getProduct_type() != null
                        && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    if (response_catalog.getPhotos() != null && response_catalog.getPhotos().size() > 0) {
                        linear_flow_button.setVisibility(View.GONE);
                        btn_chat_supplier.setVisibility(View.GONE);
                        btn_send_enquiry.setVisibility(View.GONE);
                        btn_purchase.setVisibility(View.GONE);
                        /**
                         * https://wishbook.atlassian.net/browse/WB-2843
                         * Remove Sales Order
                         */
                        btn_sales_order1.setVisibility(View.GONE);
                    } else {
                        linear_flow_button.setVisibility(View.GONE);
                    }

                } else {
                    if (response_catalog.getProducts() != null && response_catalog.getProducts().size() > 0) {
                        linear_flow_button.setVisibility(View.GONE);
                        btn_chat_supplier.setVisibility(View.GONE);
                        btn_send_enquiry.setVisibility(View.GONE);
                        btn_purchase.setVisibility(View.GONE);
                        /**
                         * https://wishbook.atlassian.net/browse/WB-2843
                         * Remove Sales Order
                         */
                        btn_sales_order1.setVisibility(View.GONE);
                    } else {
                        linear_flow_button.setVisibility(View.GONE);
                    }
                }


            }
        } else {
            showHideTopStickyBar();
            setCatalogFlags();
            setActionButton();
            setScreenButton();
        }


        if (userinfo.getGroupstatus().equals("2")) {
            linear_share.setVisibility(View.VISIBLE);
            linear_share_2.setVisibility(View.VISIBLE);
        } else {
            if (response_catalog.isBuyer_disabled() || response_catalog.getProducts().size() == 0) {
                if (response_catalog.getProduct_type() != null
                        && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    if (response_catalog.isBuyer_disabled() || response_catalog.getPhotos().size() == 0) {
                        disableShareButton();
                    } else {
                        enableShareButton();
                    }
                } else {
                    disableShareButton();
                }
            } else {
                if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                    if (response_catalog.getCompany().equals(UserInfo.getInstance(getContext()).getCompany_id())) {
                        // Show Own Catalog Share for Manufaturer
                        linear_share.setVisibility(View.VISIBLE);
                        linear_share_2.setVisibility(View.VISIBLE);
                    } else {
                        // Hide Public Catalog Share for Manufacturer
                        linear_share.setVisibility(View.GONE);
                        linear_share_2.setVisibility(View.GONE);
                    }
                } else {
                    linear_share.setVisibility(View.VISIBLE);
                    linear_share_2.setVisibility(View.VISIBLE);
                }

            }
        }


        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalog.is_owner()) {
                txt_wishlist_layout.setVisibility(View.VISIBLE);
                txt_wishlist_layout_2.setVisibility(View.VISIBLE);
                disableAddToWishlistButton();
            } else {
                linear_add_product.setVisibility(View.GONE);
                linear_add_product_2.setVisibility(View.GONE);
            }
        } else {
            if (response_catalog.is_owner()) {
                linear_add_product.setVisibility(View.VISIBLE);
                linear_add_product_2.setVisibility(View.VISIBLE);
                if (response_catalog.isBuyer_disabled()) {
                    linear_add_product.setClickable(false);
                    linear_add_product_2.setClickable(false);
                    img_disable.setBackground(getResources().getDrawable(R.drawable.btn_round_fill_purchase));
                    img_disable_2.setBackground(getResources().getDrawable(R.drawable.btn_round_fill_purchase));
                    ((TextView) v.findViewById(R.id.txt_add_product)).setTextColor(getResources().getColor(R.color.purchase_medium_gray));
                } else {

                }
            } else {
                linear_add_product.setVisibility(View.GONE);
                linear_add_product_2.setVisibility(View.GONE);
            }
        }
        brandUIUpdate(response_catalog);
        bottomBarUIUpdate(response_catalog);
        setCatalogSummary(response_catalog);
        initDeliveryInfoListener();
        initRatingView();

        if (response_catalog.getProduct_type() != null
                && response_catalog.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
            ArrayList<ProductObj> list = new ArrayList<>();
            for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                ProductObj productObj = new ProductObj(response_catalog.getPhotos().get(i).getId(),
                        response_catalog.getTitle(),
                        response_catalog.getTitle(),
                        StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATED),
                        StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATED), null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                productObj.setProduct_type(Constants.PRODUCT_TYPE_SCREEN);
                productObj.setEnabled(true);
                list.add(productObj);
            }
            allItemAdapter = new AllItemAdapterChoose((AppCompatActivity) getActivity(),
                    list, response_catalog.getCompany().equals(UserInfo.getInstance(getContext()).getCompany_id()),
                    response_catalog.is_seller(),
                    response_catalog.full_catalog_orders_only.equals("true"),
                    response_catalog.getCategory(), response_catalog.getProduct_type(), response_catalog.getFull_discount());
            allItemAdapter.setGridSelectListener(this);
            mRecyclerView.setAdapter(allItemAdapter);
            allItemAdapter.setProductsMyDetails(productMyDetail);
            //initTopDesignsRecyclerView(list);
            if (list.size() > 0) {
                list_empty1.setVisibility(View.GONE);
            } else {
                list_empty1.setVisibility(View.VISIBLE);
            }

        } else {
            ArrayList<ProductObj> list = response_catalog.getProducts();
            allItemAdapter = new AllItemAdapterChoose((AppCompatActivity) getActivity(),
                    list,
                    response_catalog.getCompany().equals(UserInfo.getInstance(getContext()).getCompany_id()),
                    response_catalog.is_seller(),
                    response_catalog.full_catalog_orders_only.equals("true"),
                    response_catalog.getCategory(), response_catalog.getProduct_type(), response_catalog.getFull_discount());
            allItemAdapter.setGridSelectListener(this);
            mRecyclerView.setAdapter(allItemAdapter);
            allItemAdapter.setProductsMyDetails(productMyDetail);
            // initTopDesignsRecyclerView(list);
            if (list.size() > 0) {
                list_empty1.setVisibility(View.GONE);
            } else {
                list_empty1.setVisibility(View.VISIBLE);
            }
            if (response_catalog.getView_permission() != null) {
                if (list != null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).getCatalog().setView_permission(response_catalog.getView_permission());
                        }
                    }
                }
            }
        }

        initSellerApproval();

        if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
            btn_broker.setVisibility(View.VISIBLE);


            if (response_catalog.getCompany().equals(UserInfo.getInstance(getContext()).getCompany_id())) {
                //my catalog
            } else if (response_catalog.getPush_user_id() != null) {
                //recv catalog
            } else {
                //public catalog


                            /*if (!response_catalog.getIs_supplier_approved()) {
                                btn_purchase.setText(getResources().getString(R.string.create_purchase_order));
                            }*/

            }

        } else {
            btn_broker.setVisibility(View.GONE);
        }


        //public catalog view Count
        postCatalogView();

        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            disableActionButton();
            try {
                ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart).setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       /* if(btn_enable_disable.getVisibility() == View.VISIBLE && btn_sales_order1.getVisibility() == View.VISIBLE) {
            linear_btn_catalog.setVisibility(View.VISIBLE);
        } else {
            linear_btn_catalog.setVisibility(View.GONE);
        }*/
    }


    private void postCatalogView() {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        if (response_catalog.getView_permission() != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            //params.put("catalog", catalog);
            params.put("product", response_catalog.getId());

            params.put("catalog_type", response_catalog.getView_permission());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POST, URLConstants.companyUrl(getActivity(), "catalog_view_count", ""), params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);
                    Log.v("Done", "CatalogViewCount");
                }


                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }


        if (StaticFunctions.isOnline(getActivity())) {
            if (response_catalog.getPush_user_id() != null) {

                ArrayList<String> pushid = new ArrayList<String>();
                ArrayList<String> pushProductid = new ArrayList<String>();
                pushid.add(response_catalog.getPush_user_id().toString());
                PostPushUserId postPushUserId = new PostPushUserId(pushid, pushProductid);


                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "syncactivitylog", ""), new Gson().fromJson(new Gson().toJson(postPushUserId), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("Done", "Posting");
                    }


                    @Override
                    public void onResponseFailed(ErrorString error) {
                    }
                });
            }


        } else {
            if (response_catalog.getPush_user_id() != null) {
                //added by abu
                ArrayList<String> pushid = new ArrayList<String>();
                ArrayList<String> pushProductid = new ArrayList<String>();
                pushid.add(response_catalog.getPush_user_id().toString());
                addPushId(pushid, pushProductid);

            }

        }
    }

    private void setCatalogFlags() {

        if (response_catalog.getView_permission().toLowerCase().equals("public")) {
            response_catalog.setIs_public(true);
        }
    }

    public void setActionButton() {


        if (response_catalog.getSupplier_details().getTotal_suppliers() > 1) {
            // For Multiple Seller
            if (response_catalog.getIs_supplier_approved() && !response_catalog.is_owner()) {
                btn_enquiry_chat.setVisibility(View.GONE);
            }

            /*if (response_catalog.is_public() && response_catalog.getEnquiry_id()==null) {
                btn_enquiry_chat.setVisibility(View.VISIBLE);
            }*/
            if (response_catalog.is_public()) {
                btn_enquiry_chat.setVisibility(View.GONE);
            }

            if (response_catalog.is_public() && response_catalog.getEnquiry_id() == null
                    && !response_catalog.is_owner()) {
                btn_send_enquiry.setVisibility(View.VISIBLE);
            } else {
                btn_send_enquiry.setVisibility(View.GONE);
            }

            if (response_catalog.getEnquiry_id() != null && response_catalog.getSupplier() != null && !response_catalog.getSupplier().equals(UserInfo.getInstance(getActivity()).getCompany_id())) {
                btn_chat_supplier.setVisibility(View.VISIBLE);
            }

            linear_txt_see_all_seller.setVisibility(View.VISIBLE);
            txt_see_all_seller.setVisibility(View.VISIBLE);
            txt_see_all_seller.setText(String.format(getResources().getString(R.string.seller_more), response_catalog.getSupplier_details().getTotal_suppliers()));
            single_supplier_details.setVisibility(View.GONE);
            multiple_seller.setVisibility(View.VISIBLE);


        } else {
            // Single Seller

            /**
             * https://wishbook.atlassian.net/browse/WB-2821
             * Change Seller name to n numbers
             */

            if (response_catalog.getProduct_type().equals(Constants.CATALOG_TYPE_CAT)) {
                txt_see_all_seller.setVisibility(View.VISIBLE);
                linear_txt_see_all_seller.setVisibility(View.VISIBLE);
                txt_see_all_seller.setText(getResources().getString(R.string.one_supplier));
                single_seller_details.setVisibility(View.GONE);
                multiple_seller.setVisibility(View.VISIBLE);
            } else {
                if (response_catalog.getSupplier_details().getTotal_suppliers() == 0) {
                    if (Application_Singleton.isFromGallery != null && Application_Singleton.isFromGallery.equals(CatalogHolder.MYRECEIVED)) {
                        // For received Catalog
                        single_supplier_details.setVisibility(View.GONE);
                    } else {
                        // For public Catalog
                        single_supplier_details.setVisibility(View.GONE);
                    }
                } else {
                    single_supplier_details.setVisibility(View.VISIBLE);
                }

                multiple_seller.setVisibility(View.GONE);
            }



           /* if (response_catalog.getIs_supplier_approved() && !response_catalog.is_owner()) {
                btn_chat_supplier.setVisibility(View.VISIBLE);
            }*/

            if (response_catalog.getEnquiry_id() != null && response_catalog.getSupplier() != null && !response_catalog.getSupplier().equals(UserInfo.getInstance(getActivity()).getCompany_id())) {
                btn_chat_supplier.setVisibility(View.VISIBLE);
            }

           /* if (response_catalog.is_public() && !response_catalog.getIs_supplier_approved()) {
                btn_send_enquiry.setVisibilit   y(View.VISIBLE);
            }*/

            if (response_catalog.is_public() && response_catalog.getEnquiry_id() == null
                    && !response_catalog.is_owner()
                    && response_catalog.getSupplier() != null && !response_catalog.getSupplier().equals(UserInfo.getInstance(getActivity()).getCompany_id())) {
                btn_send_enquiry.setVisibility(View.VISIBLE);
            } else {
                btn_send_enquiry.setVisibility(View.GONE);
            }

        }

        if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && response_catalog.getProduct().length == 1 && response_catalog.getFull_catalog_orders_only().equals("false")) {
            btn_purchase.setVisibility(View.GONE);
        } else {
            btn_purchase.setVisibility(View.VISIBLE);
        }
        if (response_catalog.is_owner()
                || (response_catalog.is_seller() && response_catalog.is_currently_selling())) {
            btn_purchase.setVisibility(View.GONE);
        }

        if (!response_catalog.isSelling()) {
            btn_purchase.setEnabled(false);
            btn_purchase.setText("Full Not Available");
            btn_purchase.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
        }

        if (response_catalog.is_seller() && response_catalog.is_currently_selling()) {
            /**
             * https://wishbook.atlassian.net/browse/WB-2843
             * Remove Sales Order Fun.
             */
            btn_sales_order1.setVisibility(View.GONE);
            btn_send_enquiry.setVisibility(View.GONE);
            btn_chat_supplier.setVisibility(View.GONE);
            linear_flow_button.setVisibility(View.GONE);
        }

        if (response_catalog.is_owner()) {
            //add product

            // don't display own company info in seller details
            single_supplier_details.setVisibility(View.GONE);
            multiple_seller.setVisibility(View.GONE);
            /* view_liner_seller.setVisibility(View.GONE);*/
        }


        if (response_catalog.is_seller() && response_catalog.is_currently_selling()) {
            // stop selling (disable catalog)
            btn_enable_disable.setVisibility(View.VISIBLE);
            btn_enable_disable.setText(DISABLE_LABEL);

        }

        Log.i("TAG", "setActionButton: Seller" + response_catalog.is_seller() + "\n Current Selling" + response_catalog.is_currently_selling() + "\n Iam Selling" + response_catalog.is_seller());

        if ((response_catalog.is_seller() || response_catalog.is_owner()) && !response_catalog.is_currently_selling()) {
            // resume selling (enable catalog)
            btn_enable_disable.setVisibility(View.VISIBLE);
            btn_enable_disable.setText(ENABLE_LABEL);
        }


        // ####### END  BOTTOM ACTION


        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            // not show  for retailer
            if (!response_catalog.is_owner() && !response_catalog.is_seller() && response_catalog.is_public()) {
                //  become a seller
                if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.CATALOG_TYPE_NON) || response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    btn_become_seller.setVisibility(View.GONE);
                } else {
                    btn_become_seller.setVisibility(View.VISIBLE);
                }
                txt_become_seller_status.setVisibility(View.GONE);
            }
        } else {
            btn_become_seller.setVisibility(View.GONE);
        }


        if (response_catalog.is_public() && response_catalog.is_seller()) {
            // show status of seller
            btn_become_seller.setVisibility(View.GONE);
            //view_become_seller.setVisibility(View.GONE);
            txt_become_seller_status.setVisibility(View.VISIBLE);


            if (response_catalog.is_currently_selling()) {
                multiple_seller.setVisibility(View.GONE);
                btn_send_enquiry.setVisibility(View.GONE);
                btn_enquiry_chat.setVisibility(View.GONE);
                txt_wishlist_layout.setVisibility(View.GONE);
                txt_wishlist_layout_2.setVisibility(View.GONE);
            }


            btn_broker.setVisibility(View.GONE);

            if (btn_enable_disable.getVisibility() == View.VISIBLE) {
                if (btn_enable_disable.getText().equals(ENABLE_LABEL)) {
                    txt_become_seller_status.setText(getResources().getString(R.string.you_were_selling));
                } else {
                    txt_become_seller_status.setText(getResources().getString(R.string.you_are_selling));
                }
            } else {
                txt_become_seller_status.setText(getResources().getString(R.string.you_are_selling));
            }

        }

        if (btn_purchase.getVisibility() == View.VISIBLE ||
                btn_chat_supplier.getVisibility() == View.VISIBLE ||
                btn_send_enquiry.getVisibility() == View.VISIBLE ||
                btn_enquiry_chat.getVisibility() == View.VISIBLE) {
            linear_flow_button.setVisibility(View.VISIBLE);

        }

       /* if(Application_Singleton.selectedshareCatalog!=null && (Application_Singleton.selectedshareCatalog.getBuyer_disabled() || Application_Singleton.selectedshareCatalog.getSupplier_disabled())){
            disableActionForDisableCatalog();
        }*/
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            if (response_catalog.is_owner()) {
                txt_wishlist_layout.setVisibility(View.GONE);
                txt_wishlist_layout_2.setVisibility(View.GONE);
            } else {
                if (response_catalog.is_seller() && response_catalog.is_currently_selling()) {
                    txt_wishlist_layout.setVisibility(View.GONE);
                    txt_wishlist_layout_2.setVisibility(View.GONE);
                } else {
                    if (response_catalog != null) {
                        if (response_catalog.getIs_addedto_wishlist() == null) {
                            updateWishlist(false);
                        } else {
                            updateWishlist(true);
                        }
                    }

                }
            }

        } else {
            txt_wishlist_layout.setVisibility(View.GONE);
            txt_wishlist_layout_2.setVisibility(View.GONE);
        }

        if (response_catalog.is_seller()
                && isIAmSellingAnyOneProduct()
                && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            linear_clearance_discount_root_container.setVisibility(View.VISIBLE);
            linear_clearance_discount.setVisibility(View.VISIBLE);
            linear_clearance_discount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (productMyDetail != null)
                        openClearanceDiscountDialog(response_catalog.getProduct(), productMyDetail.getCatalog_seller_id(), productMyDetail.getClearance_discount_percentage());
                }
            });
            // check seller is already given the discount
            if (productMyDetail != null && productMyDetail.getClearance_discount_percentage() > 0) {
                txt_clearance_discount_label.setText("Edit/Delete Clearance Discount");
            } else {
                txt_clearance_discount_label.setText("Offer Clearance Discount");
            }
        } else {
            linear_clearance_discount_root_container.setVisibility(View.GONE);
            linear_clearance_discount.setVisibility(View.GONE);
        }

        if (!response_catalog.is_owner()) {
            if (response_catalog != null && (response_catalog.isSupplier_disabled())) {
                disableActionForDisableCatalog();
            }
        } else {
            if (response_catalog != null && (response_catalog.isBuyer_disabled())) {
                disableActionForDisableCatalog();
            }
        }
    }

    public void setCatalogSummary(final Response_catalog response_catalog) {
        card_catalog_summary.setVisibility(View.VISIBLE);

        txt_other.setVisibility(View.GONE);
        relative_other_details.setVisibility(View.GONE);
        linear_size_container.setVisibility(View.GONE);
        relative_size.setVisibility(View.GONE);
        boolean isanyother_eav = false;
        linear_eav_data.removeAllViews();
        StringBuffer material_details = new StringBuffer();
        ArrayList<String> lengths = new ArrayList<>();

        if (response_catalog.getEavdatajson() != null) {

            for (Iterator iterator = response_catalog.getEavdatajson().keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                String valueString = null;
                if (response_catalog.getEavdatajson().get(key) instanceof JsonArray) {
                    valueString = StaticFunctions.JsonArrayToString((JsonArray) response_catalog.getEavdatajson().get(key), StaticFunctions.COMMASEPRATEDSPACE);
                } else {
                    valueString = response_catalog.getEavdatajson().get(key).toString().replaceAll("\"", "");
                }
                if (StaticFunctions.isLengthEav(key)) {
                    lengths.add(key + ": " + valueString);
                } else {
                    switch (key) {
                        case "work":
                            if (response_catalog.getEavdatajson().get(key) != null && valueString != null) {
                                //  txt_work.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                // txt_work.setText(valueString);
                                material_details.append(valueString + StaticFunctions.MATERIALDETAILSEPRATED);
                            }
                            if (response_catalog.getEavdatajson().get("work") == null && response_catalog.getEavdatajson().get("fabric") == null) {
                                linear_material.setVisibility(View.GONE);
                            }
                            break;

                        case "fabric":
                            if (response_catalog.getEavdatajson().get(key) != null && valueString != null) {
                                //  txt_fabric.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                //  txt_fabric.setText(valueString);
                                material_details.append(valueString + StaticFunctions.MATERIALDETAILSEPRATED);
                            }

                            if (response_catalog.getEavdatajson().get("work") == null && response_catalog.getEavdatajson().get("fabric") == null) {
                                linear_material.setVisibility(View.GONE);
                            }
                            break;

                        case "number_pcs_design_per_set":
                        case "gender":
                            //don't show
                            Log.e("EAV Type", "Key: " + key + "Value type: " + response_catalog.getEavdatajson().get(key) + " Dont show");
                            break;


                        case "other":
                            Log.e("EAV Type", "setCatalogSummary: Ohter" + valueString);
                            if (response_catalog.getEavdatajson().get(key) != null && valueString != null) {
                                // txt_material_see_more.setVisibility(View.VISIBLE);
                                isSeeMore = true;
                                if (isContentIsValidHtml(valueString)) {
                                    txt_other.setText(Html.fromHtml(valueString), TextView.BufferType.NORMAL);
                                } else {
                                    String temp = "<font color='#3a3a3a'>" + valueString + "</font>";
                                    txt_other.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
                                }
                                relative_other_details.setVisibility(View.VISIBLE);
                                txt_other.setVisibility(View.VISIBLE);
                            }

                            break;

                        case "size":
                            break;


                        default:
                            Log.e("EAV Type", "Key: " + key + "Value type: " + response_catalog.getEavdatajson().get(key));
                            if (key != null && valueString != null) {
                                material_details.append(valueString + StaticFunctions.MATERIALDETAILSEPRATED);
                                //add linearlayout
                                if (!key.equalsIgnoreCase("gender")) {
                                    isanyother_eav = true;
                                    //  addDynamicEav(key, valueString, linear_eav_data);
                                }
                            }
                            break;
                    }
                }

            }

        }


        if (response_catalog.getId() != null && !response_catalog.getId().isEmpty()) {
            relative_product_id.setVisibility(View.GONE);
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                txt_product_id_label.setText("Set ID: ");
            } else {
                txt_product_id_label.setText("Catalog ID: ");
            }
            txt_product_id_value.setText(response_catalog.getId());
        } else {
            relative_product_id.setVisibility(View.GONE);
        }


        if (lengths != null && lengths.size() > 0) {
            material_details.append(StaticFunctions.ArrayListToString(lengths, StaticFunctions.COMMASEPRATED) + StaticFunctions.MATERIALDETAILSEPRATED);
            // txt_length.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            //  relative_lengths.setVisibility(View.VISIBLE);

            //   txt_length.setText(StaticFunctions.ArrayListToString(lengths, StaticFunctions.COMMASEPRATED));
        } else {
            //  relative_lengths.setVisibility(View.GONE);
        }

        if (material_details != null && !material_details.toString().isEmpty()) {
            txt_material_detail.setText(material_details.substring(0, material_details.length() - StaticFunctions.MATERIALDETAILSEPRATED.length()));
        }


        if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
            linear_size_container.setVisibility(View.VISIBLE);
            relative_size.setVisibility(View.VISIBLE);
            txt_size_value.setText(response_catalog.getAvailable_sizes());

        } else {
            linear_size_container.setVisibility(View.GONE);
        }


        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalog.getSet_type_details() != null && !response_catalog.getSet_type_details().isEmpty()) {
                linear_color.setVisibility(View.VISIBLE);
                txt_color_value.setText(response_catalog.getSet_type_details());
            } else {
                linear_color.setVisibility(View.GONE);
            }
        }


        if (response_catalog.getPrice_range() != null) {
            if (response_catalog.getPrice_range().contains("-")) {
                String[] priceRangeMultiple = response_catalog.getPrice_range().split("-");
                txt_price.setText("\u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
            } else {
                txt_price.setText("\u20B9" + response_catalog.getPrice_range() + "/Pc.");
            }
        } else {
            txt_price.setVisibility(View.GONE);
        }

        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)
                || response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_NON)) {
            full_catalog_label.setText("Full Set :");
            if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_NON) && response_catalog.getProduct() != null && response_catalog.getProduct().length == 1) {
                full_catalog_label.setText("Price :");
                if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equalsIgnoreCase("false") && response_catalog.getSingle_piece_price_range() != null) {
                    txt_price.setText("\u20B9" + response_catalog.getSingle_piece_price_range() + "/Pc.");
                }
            }
        } else {
            full_catalog_label.setText("Full :");
            if (response_catalog.getProduct() != null && response_catalog.getProduct().length == 1) {
                full_catalog_label.setText("Price :");
                // changes done April 30 new mail (Jay Patel)
                if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equalsIgnoreCase("false") && response_catalog.getSingle_piece_price_range() != null) {
                    txt_price.setText("\u20B9" + response_catalog.getSingle_piece_price_range() + "/Pc.");
                }
            } else {
                if (!response_catalog.isSelling() && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    relative_full_price.setVisibility(View.GONE);
                }
            }

        }


        /**
         * Revert the logic for show singl pc price for product ==1
         */
       /* if(!response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && response_catalog.getProduct().length == 1) {
            boolean isFullCatalogOrderOnly = true;
            if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("false")) {
                isFullCatalogOrderOnly = false;
            }
            if(!isFullCatalogOrderOnly) {
                relative_full_price.setVisibility(View.GONE);
            }
        }*/

        if (!response_catalog.getFull_catalog_orders_only().equals("true")) {
            if (response_catalog.getSingle_piece_price_range() != null) {
                txt_single_piece_price.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                if (response_catalog.getSingle_piece_price_range() != null) {
                    String price_range = response_catalog.getSingle_piece_price_range();
                    if (price_range.contains("-")) {
                        String[] priceRangeMultiple = price_range.split("-");
                        txt_single_piece_price.setText("\u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
                    } else {
                        txt_single_piece_price.setText("\u20B9" + price_range + "/Pc.");
                    }
                }
            } else {
                txt_single_piece_price.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                txt_single_piece_price.setText(txt_price.getText().toString());
            }
        }


        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            relative_single_price.setVisibility(View.GONE);
        } else {
            if (response_catalog.getProduct().length > 1) {
                relative_single_price.setVisibility(View.VISIBLE);
            } else {
                relative_single_price.setVisibility(View.GONE);
            }
        }


        // MWP Price Set
        if (response_catalog.getMwp_single_price() > 0) {
            txt_mwp_price.setVisibility(View.VISIBLE);
            txt_mwp_price.setTextColor(getResources().getColor(R.color.purchase_medium_gray));
            txt_mwp_price.setPaintFlags(txt_mwp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String price_range = String.valueOf(response_catalog.getMwp_single_price());
            txt_mwp_price.setText("\u20B9" + price_range + "/Pc.");
        } else {
            txt_mwp_price.setVisibility(View.GONE);
            txt_mwp_price.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }


        // Clearance discount set for public catalog view
        /*if(response_catalog.getSingle_piece_price_range().contains("-")) {
            // show avg single price discount
        } else {
            response_catalog.setSingle_discount(Math.round ((1-Double.parseDouble(response_catalog.getSingle_piece_price_range())/retail_price) * 100));
        }*/
        if (response_catalog.getFull_catalog_orders_only().equals("false") && response_catalog.getSingle_discount() > 0) {
            txt_single_clearance_discount.setVisibility(View.VISIBLE);
            txt_single_clearance_discount.setText("" + Math.round(response_catalog.getSingle_discount()) + "% off");
        } else {
            txt_single_clearance_discount.setVisibility(View.GONE);
        }

    /*    if(response_catalog.getPrice_range().contains("-")) {
            // show avg single price discount

        } else {
            response_catalog.setFull_discount(Math.round ((1-Double.parseDouble(response_catalog.getPrice_range())/retail_price) * 100));
            //response_catalog.setFull_discount(Math.round(Double.parseDouble(response_catalog.getPrice_range())/retail_price*100));
        }*/
        if (response_catalog.getFull_discount() > 0) {
            txt_full_clearance_discount.setVisibility(View.VISIBLE);
            txt_full_clearance_discount.setText("" + Math.round(response_catalog.getFull_discount()) + "% off");
        } else {
            txt_full_clearance_discount.setVisibility(View.GONE);
        }
        setMyCatalogPrice();


        /**
         * Temp Condition
         */
        if (Application_Singleton.isFromGallery != null && Application_Singleton.isFromGallery.equals(CatalogHolder.MYRECEIVED)) {
            relative_received_price.setVisibility(View.VISIBLE);
            txt_received_price.setText(txt_price.getText());
            relative_full_price.setVisibility(View.GONE);
            relative_single_price.setVisibility(View.GONE);
        }


        /*if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("true")) {
            txt_single_full.setText(R.string.only_full_catalog_sale);
        } else {
            txt_single_full.setText(R.string.single_catalog_sale);
        }*/

        if (response_catalog.getProduct_type() != null
                && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalog.getProducts() != null) {
                txt_label_design.setText("Pieces :");
                txt_catalog_detail_label_design.setText("Pieces :");
                String set = "";

                if (response_catalog.getCatalog_multi_set_type() != null && response_catalog.getCatalog_multi_set_type().equalsIgnoreCase("color_set")) {
                    set = "Color";
                    if (Integer.parseInt(response_catalog.getNo_of_pcs_per_design()) == 1) {
                        set = "Color";
                    } else {
                        set = "Colors";
                    }
                } else if (response_catalog.getCatalog_multi_set_type() != null && response_catalog.getCatalog_multi_set_type().equalsIgnoreCase("size_set")) {
                    if (Integer.parseInt(response_catalog.getNo_of_pcs_per_design()) == 1) {
                        set = "Size";
                    } else {
                        set = "Sizes";
                    }
                }


                String extra_string = " (Set of " + response_catalog.getNo_of_pcs_per_design() +
                        " " + set + ")";
                txt_number_design.setText(extra_string);
                txt_catalog_detail_number_design.setText(String.valueOf(response_catalog.getNo_of_pcs_per_design()) + extra_string);
            }
        } else {
            if (response_catalog.getProducts() != null) {
                txt_number_design.setText(" (" + String.valueOf(response_catalog.getProducts().size()) + " Designs)");
                txt_catalog_detail_number_design.setText(String.valueOf(response_catalog.getProducts().size()));
            }
        }

        // set category name in catalog deatils section and more product from category(below similar products)
        if (response_catalog.getCategory_name() != null) {
            txt_category.setText(response_catalog.getCategory_name());
            txt_catalog_detail_category.setText(response_catalog.getCategory_name());
            txt_more_category.setVisibility(View.VISIBLE);
            txt_more_category.setText("More " + response_catalog.getCategory_name());
        } else {
            relative_category.setVisibility(View.GONE);
            txt_more_category.setVisibility(View.GONE);
        }

        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalog.getTitle() != null) {
                String temp = "<font color='#3a3a3a' size='16'>" + response_catalog.getTitle() + "</font><font color='#777777' size='16'>" + " From " + "</font><font color='#3a3a3a' size='16'>" + response_catalog.getCatalog_title() + "</font>";
                txt_catalog_name.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
            }
        } else {
            if (response_catalog.getTitle() != null) {
                txt_catalog_name.setText(response_catalog.getTitle());
            }
        }


        if (response_catalog.getSupplier_name() != null) {
            txt_sold_by.setText(response_catalog.getSupplier_name().toString());
        }

        if (response_catalog.is_trusted_seller()) {
            img_trusted.setVisibility(View.VISIBLE);
        } else {
            img_trusted.setVisibility(View.GONE);
        }

        if (response_catalog.getSupplier_details() != null) {
            String stateLocation = response_catalog.getSupplier_details().getState_name() != null ? response_catalog.getSupplier_details().getState_name() + ", " : "";
            String cityLocation = response_catalog.getSupplier_details().getCity_name() != null ? response_catalog.getSupplier_details().getCity_name() : "";
            txt_location.setText(stateLocation + cityLocation);


            if (response_catalog.getSupplier_details().getSeller_policy() != null && response_catalog.getSupplier_details().getSeller_policy().size() > 0) {
                ArrayList<ResponseSellerPolicy> policies = response_catalog.getSupplier_details().getSeller_policy();
                String return_policy_value_other_language = null;
                String return_policy_value_english_language = null;
                for (int i = 0; i < policies.size(); i++) {
                    if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                        linear_delivery_time.setVisibility(View.VISIBLE);
                        txt_delivery_value.setText(policies.get(i).getPolicy().toString());
                    } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                        if (policies.get(i).getLanguage() != null && policies.get(i).getLanguage().get(0).equals("en")) {
                            return_policy_value_english_language = policies.get(i).getPolicy();
                        } else if (policies.get(i).getLanguage() != null && LocaleHelper.getLanguage(getActivity()).equalsIgnoreCase(policies.get(i).getLanguage().get(0))) {
                            return_policy_value_other_language = policies.get(i).getPolicy();
                        }
                    }
                }
                if (return_policy_value_english_language != null || return_policy_value_other_language != null) {
                    linear_return_policy.setVisibility(View.VISIBLE);
                    if (return_policy_value_other_language != null)
                        txt_return_value.setText(return_policy_value_other_language);
                    else
                        txt_return_value.setText(return_policy_value_english_language);
                }
            } else {
                if (response_catalog.isReady_to_ship()) {
                    linear_delivery_time.setVisibility(View.GONE);
                    txt_delivery_label.setVisibility(View.INVISIBLE);
                    txt_delivery_value.setVisibility(View.INVISIBLE);
                } else {
                    linear_delivery_time.setVisibility(View.GONE);
                }
                linear_return_policy.setVisibility(View.GONE);
            }
        } else {
            if (response_catalog.isReady_to_ship()) {
                linear_delivery_time.setVisibility(View.GONE);
                txt_delivery_label.setVisibility(View.INVISIBLE);
                txt_delivery_value.setVisibility(View.INVISIBLE);
            } else {
                linear_delivery_time.setVisibility(View.GONE);
                linear_return_policy.setVisibility(View.GONE);
                linear_single_seller_policy.setVisibility(View.VISIBLE);
            }


            relative_single_seller_location.setVisibility(View.GONE);
        }
        boolean isSellerDispatchAvailable = false;

        if (response_catalog.getSupplier_details().getSeller_policy() != null && response_catalog.getSupplier_details().getSeller_policy().size() > 0) {
            ArrayList<ResponseSellerPolicy> policies = response_catalog.getSupplier_details().getSeller_policy();
            String return_policy_value_other_language = null;
            String return_policy_value_english_language = null;
            for (int i = 0; i < policies.size(); i++) {
                if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                    isSellerDispatchAvailable = true;
                    linear_delivery_time.setVisibility(View.VISIBLE);
                    txt_delivery_value.setText(policies.get(i).getPolicy().toString());
                    if (response_catalog.isReady_to_ship()) {
                        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                            txt_ready_to_dispatch.setVisibility(View.GONE);
                        } else {
                            txt_ready_to_dispatch.setVisibility(View.GONE);
                        }
                    } else {
                        txt_ready_to_dispatch.setVisibility(View.GONE);

                    }
                } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                    if (policies.get(i).getLanguage().size() > 0 && policies.get(i).getLanguage().get(0).equals("en")) {
                        return_policy_value_english_language = policies.get(i).getPolicy();
                    } else if (policies.get(i).getLanguage().size() > 0 && LocaleHelper.getLanguage(getActivity()).equalsIgnoreCase(policies.get(i).getLanguage().get(0))) {
                        return_policy_value_other_language = policies.get(i).getPolicy();
                    }
                }
            }

            if (return_policy_value_english_language != null || return_policy_value_other_language != null) {
                linear_return_policy.setVisibility(View.VISIBLE);
                if (return_policy_value_other_language != null)
                    txt_return_value.setText(return_policy_value_other_language);
                else
                    txt_return_value.setText(return_policy_value_english_language);
            }

            if (!isSellerDispatchAvailable) {
                if (response_catalog.isReady_to_ship()) {
                    if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                        txt_ready_to_dispatch.setVisibility(View.GONE);
                    } else {
                        linear_delivery_time.setVisibility(View.GONE);
                        txt_delivery_label.setVisibility(View.INVISIBLE);
                        txt_delivery_value.setVisibility(View.INVISIBLE);
                        txt_ready_to_dispatch.setVisibility(View.GONE);
                    }
                } else {
                    txt_ready_to_dispatch.setVisibility(View.GONE);

                }
            }
        } else {
            if (response_catalog.isReady_to_ship()) {
                linear_delivery_time.setVisibility(View.GONE);
                txt_delivery_label.setVisibility(View.INVISIBLE);
                txt_delivery_value.setVisibility(View.INVISIBLE);
            } else {
                linear_delivery_time.setVisibility(View.GONE);
            }
            linear_return_policy.setVisibility(View.GONE);
        }


        if (response_catalog.getDispatch_date() != null && !response_catalog.getDispatch_date().isEmpty()) {
            linear_dispatch_detail.setVisibility(View.VISIBLE);
            Date date1 = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                date1 = sdf.parse(response_catalog.getDispatch_date());
                Date current_date = new Date();
                if (date1.compareTo(current_date) > 0) {

                    // Before
                    String dispatch_client = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT1, response_catalog.getDispatch_date().toString());
                    txt_dispatch_value.setText(dispatch_client);
                } else if (date1.compareTo(current_date) < 0) {
                    // After
                    linear_dispatch_detail.setVisibility(View.GONE);
                } else if (date1.compareTo(current_date) == 0) {
                    linear_dispatch_detail.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            linear_dispatch_detail.setVisibility(View.GONE);
        }

        if (response_catalog.getSupplier_company_rating() != null) {
            if (response_catalog.getSupplier_company_rating().getSeller_score() != null) {
                float rating = Float.parseFloat(response_catalog.getSupplier_company_rating().getSeller_score());
                if (Math.round(rating) == 0) {
                    linear_seller_rating.setVisibility(View.GONE);
                } else {
                    if (Double.parseDouble(response_catalog.getSupplier_company_rating().getSeller_score()) <= 2) {
                        txt_rating.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rating_summary_red));
                    } else {
                        txt_rating.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rating_summary));
                    }
                    String rateString = String.format("%.1f", Double.parseDouble(response_catalog.getSupplier_company_rating().getSeller_score()));
                    txt_rating.setText(rateString);
                }
            } else {
                linear_seller_rating.setVisibility(View.GONE);
            }
        } else {
            linear_seller_rating.setVisibility(View.GONE);
        }


        try {
            single_supplier_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(getActivity()).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                    } else {
                     /*   Bundle bundle = new Bundle();
                        if (response_catalog.getSupplier() != null && response_catalog.getSupplier_id() != null) {
                            bundle.putString("sellerid", response_catalog.getSupplier_id());
                            bundle.putString("sellerCompanyid", response_catalog.getSupplier());
                            Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                            Application_Singleton.TOOLBARSTYLE = "WHITE";
                            Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                            supplier.setArguments(bundle);
                            Application_Singleton.CONTAINERFRAG = supplier;
                            Intent intent = new Intent(getActivity(), OpenContainer.class);
                            startActivityForResult(intent, ResponseCodes.Supplier_Approved);
                        } else if (response_catalog.getSupplier() != null) {
                            // for public details
                            bundle.putString("sellerid", response_catalog.getSupplier());
                            bundle.putBoolean("isHideAll", true);
                            Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                            Application_Singleton.TOOLBARSTYLE = "WHITE";
                            Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                            supplier.setArguments(bundle);
                            Application_Singleton.CONTAINERFRAG = supplier;
                            Intent intent = new Intent(getActivity(), OpenContainer.class);
                            startActivityForResult(intent, ResponseCodes.Supplier_Approved);
                        }*/
                    }
                }
            });


            if (btn_become_seller.getVisibility() == View.GONE
                    /*  && view_become_seller.getVisibility() == View.GONE*/
                    && txt_become_seller_status.getVisibility() == View.GONE
                    && linear_btn_catalog.getVisibility() == View.GONE) {
                relative_become_seller.setVisibility(View.GONE);
            }

            btn_become_seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                            && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
                        HashMap<String, String> param = new HashMap<String, String>();
                        param.put("type", "profile_update");
                        new DeepLinkFunction(param, getActivity());
                        return;
                    }

                    Application_Singleton.trackEvent("Catalog Detail", "Click", "Become Seller");
                    //startActivityForResult(new Intent(getActivity(), Activity_Become_Seller.class),500);
                    getBrandPermission(response_catalog, true, getActivity());
                }
            });

            linear_txt_see_all_seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* startActivityForResult(new Intent(getActivity(), ActivityMultipleSeller.class)
                            .putExtra("catalog_name", response_catalog.getTitle())
                            .putExtra("catalog_id", response_catalog.getId())
                            .putExtra("catalog_price", response_catalog.getPrice_range())
                            .putExtra("action", ALLACTION), Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE);*/
                }
            });


            if (relative_size.getVisibility() == View.GONE
                    && linear_color.getVisibility() == View.GONE && !isanyother_eav) {
                //view_material.setVisibility(View.GONE);
            }

            if (single_supplier_details.getVisibility() == View.GONE
                    && multiple_seller.getVisibility() == View.GONE
                    && linear_single_seller_policy.getVisibility() == View.GONE
                    && linear_dispatch_detail.getVisibility() == View.GONE) {
                //view_liner_seller.setVisibility(View.GONE);
            }

            txt_size_chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSizeChart();
                }
            });

            txt_copy_details.setText("Copy Details");

            txt_copy_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyProductDetail(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setMyCatalogPrice() {
        // For Show My Products
        if (response_catalog.is_seller()) {
            txt_my_catalog_seller_note1.setVisibility(View.VISIBLE);
            if (response_catalog.isI_am_selling_sell_full_catalog()) {
                txt_price.setText("\u20B9" + productMyDetail.getFull_price() + "/Pc.");
                txt_single_piece_price.setText("Not selling");
            } else {
                txt_price.setText("\u20B9" + productMyDetail.getFull_price() + "/Pc.");
                txt_single_piece_price.setText("\u20B9" + productMyDetail.getSingle_piece_price() + "/Pc.");
            }

        }


        // For Show My Products
        if (response_catalog.is_seller()) {
            txt_full_clearance_discount.setVisibility(View.GONE);
            txt_single_clearance_discount.setVisibility(View.GONE);
            txt_mwp_price.setVisibility(View.GONE);
            if (productMyDetail.getClearance_discount_percentage() > 0) {
                txt_my_clerance_discount.setVisibility(View.VISIBLE);
                txt_my_clerance_discount.setText("You are currently giving " + productMyDetail.getClearance_discount_percentage() + "% clerance discount");
            } else {
                txt_my_clerance_discount.setVisibility(View.GONE);
            }
        }
    }


    private boolean isIAmSellingAnyOneProduct() {
        boolean isSellingOneProduct = true;
        if (productMyDetail.isI_am_selling_sell_full_catalog()) {
            isSellingOneProduct = productMyDetail.isCurrently_selling();
        } else {
            ArrayList<Boolean> isAllProductNotSelling1 = new ArrayList<>();
            if (productMyDetail != null) {
                isAllProductNotSelling1 = new ArrayList<>();
                for (int i = 0; i < productMyDetail.getProducts().size(); i++) {
                    isAllProductNotSelling1.add(productMyDetail.getProducts().get(i).isCurrently_selling());
                }
                return isAllProductNotSelling1.contains(true);
            }
        }
        return isSellingOneProduct;
    }

    private void addPushId(ArrayList<String> pushid, ArrayList<String> pushProductid) {
        List pushIds = loadPushId();
        if (pushIds == null)
            pushIds = new ArrayList();
        PostPushUserId postPushUserId = new PostPushUserId(pushid, pushProductid);
        pushIds.add(postPushUserId);
        storePushId((ArrayList<String>) pushIds);

    }

    private ArrayList loadPushId() {
        SharedPreferences pref;
        List push_id;
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        if (pref.contains("Push_Id_List")) {
            String jsonFavorites = pref.getString("Push_Id_List", null);
            Gson gson = new Gson();
            PostPushUserId[] pushIds = gson.fromJson(jsonFavorites, PostPushUserId[].class);
            push_id = Arrays.asList(pushIds);
            push_id = new ArrayList(push_id);
            Set<String> hs = new HashSet<>();
            hs.addAll(push_id);
            push_id.clear();
            push_id.addAll(hs);
        } else
            return null;
        return (ArrayList) push_id;
    }

    private void storePushId(ArrayList<String> pushid) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        String jsonPushId = gson.toJson(pushid);
        editor.putString("Push_Id_List", jsonPushId);
        editor.commit();
    }

    private void showEnquirySelectDialog(final String supplierCompanyId, final String supplierName, final String supplierChatUSer, boolean isSellFullCatalog) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_enquiry_send, null);
        initDialogViewListener(alertLayout, supplierCompanyId, supplierName, supplierChatUSer, isSellFullCatalog);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        dialog = alert.create();
        dialog.show();
    }

    public void initDialogViewListener(View alertLayout, final String supplierCompanyId,
                                       final String supplierName, final String supplierChatUSer, final boolean isSellFullCatalog) {


        if (isSellFullCatalog) {
            alertLayout.findViewById(R.id.layout_single_pcs).setVisibility(View.GONE);
            alertLayout.findViewById(R.id.txt_only_sets).setVisibility(View.VISIBLE);
        }
        Application_Singleton.getInstance().trackScreenView("NewEnquiryDialog", getActivity());
        final EditText txt_only_sets = alertLayout.findViewById(R.id.txt_only_sets);
        final AppCompatCheckBox check_price = alertLayout.findViewById(R.id.check_price);
        final AppCompatCheckBox check_fabric = alertLayout.findViewById(R.id.check_fabric);
        final AppCompatCheckBox check_dispatch = alertLayout.findViewById(R.id.check_dispatch);
        final RadioButton radio_pcs = alertLayout.findViewById(R.id.radio_pcs);
        final RadioButton radio_sets = alertLayout.findViewById(R.id.radio_sets);
        final EditText txt_sets = alertLayout.findViewById(R.id.txt_sets);
        final EditText txt_pcs = alertLayout.findViewById(R.id.txt_pcs);
        LinearLayout btn_send = alertLayout.findViewById(R.id.btn_send);
        LinearLayout btn_cancel = alertLayout.findViewById(R.id.btn_cancel);
        radio_sets.setChecked(true);
        txt_pcs.setEnabled(false);
        radio_pcs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radio_sets.setChecked(false);
                    txt_pcs.setEnabled(true);
                    txt_sets.setEnabled(false);
                    txt_sets.setText("");
                }
            }
        });
        radio_sets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radio_pcs.setChecked(false);
                    txt_sets.setEnabled(true);
                    txt_pcs.setEnabled(false);
                    txt_pcs.setText("");
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = null;
                boolean isFirstCheck = false, isSecondCheck = false, isThirdCheck = false;
                ArrayList<String> temp;

                if (isSellFullCatalog) {
                    quantity = txt_only_sets.getText().toString();
                    if (quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
                        Toast.makeText(getActivity(), "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (check_price.isChecked() || check_fabric.isChecked() || check_dispatch.isChecked()) {
                            temp = new ArrayList<>();
                            if (check_price.isChecked()) {
                                temp.add(check_price.getText().toString());
                                isThirdCheck = true;
                            }
                            if (check_fabric.isChecked()) {
                                temp.add(check_fabric.getText().toString());
                                isSecondCheck = true;
                            }
                            if (check_dispatch.isChecked()) {
                                temp.add(check_dispatch.getText().toString());
                                isFirstCheck = true;
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please select at least one option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        callCatalogEnquiry(supplierCompanyId, supplierName, StaticFunctions.ArrayListToString(temp,
                                StaticFunctions.COMMASEPRATEDSPACE), supplierChatUSer, Constants.ENQUIRY_SET, quantity, isFirstCheck, isSecondCheck, isThirdCheck);
                    }
                } else {
                    String enquiryType = Constants.ENQUIRY_PIECES;
                    if (radio_pcs.isChecked()) {
                        quantity = txt_pcs.getText().toString();
                        enquiryType = Constants.ENQUIRY_PIECES;
                    }
                    if (radio_sets.isChecked()) {
                        quantity = txt_sets.getText().toString();
                        enquiryType = Constants.ENQUIRY_SET;
                    }
                    if (quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
                        Toast.makeText(getActivity(), "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (check_price.isChecked() || check_fabric.isChecked() || check_dispatch.isChecked()) {
                            temp = new ArrayList<>();
                            if (check_price.isChecked()) {
                                temp.add(check_price.getText().toString());
                                isThirdCheck = true;
                            }
                            if (check_fabric.isChecked()) {
                                temp.add(check_fabric.getText().toString());
                                isSecondCheck = true;
                            }
                            if (check_dispatch.isChecked()) {
                                temp.add(check_dispatch.getText().toString());
                                isFirstCheck = true;
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please select at least one option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Application_Singleton.getInstance().trackScreenView("NewEnquirySent", getActivity());
                        dialog.dismiss();
                        callCatalogEnquiry(supplierCompanyId, supplierName, StaticFunctions.ArrayListToString(temp,
                                StaticFunctions.COMMASEPRATEDSPACE), supplierChatUSer, enquiryType, quantity, isFirstCheck, isSecondCheck, isThirdCheck);

                    }

                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @OnClick({R.id.linear_share_2, R.id.linear_share})
    public void shareClick() {
        if (response_catalog != null && response_catalog.getProduct_type() != null) {
            showShareBottomSheet(getActivity(), response_catalog.getProduct_type(), response_catalog.getId());
        }
    }

    @OnClick({R.id.img_brand_logo, R.id.img_brand_logo_2, R.id.txt_brand_name_2, R.id.txt_brand_name})
    public void brandClick() {
        if (response_catalog != null && response_catalog.getBrand() != null && response_catalog.getBrand().getId() != null)
            StaticFunctions.hyperLinking1("brand", response_catalog.getBrand().getId(), getActivity(), response_catalog.getBrand().getName());
    }

    @OnClick({R.id.linear_add_product, R.id.linear_add_product_2})
    public void updateProductClick() {
        openUpdateCatalog();
    }

    @OnClick({R.id.txt_wishlist_layout, R.id.txt_wishlist_layout_2})
    public void wishlistClick() {
        if (UserInfo.getInstance(getActivity()).isGuest()) {
            StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
            return;
        }
        if (response_catalog != null) {
            if (response_catalog.getIs_addedto_wishlist() == null) {
                callSaveWishlist(getActivity(), response_catalog.getCatalog_id(), response_catalog.getId());
            } else {
                callRemoveWishlist(getActivity(), response_catalog.getIs_addedto_wishlist());
            }
        }
    }

    @OnClick(R.id.btn_support_chat)
    public void clickSupportChat() {
        Application_Singleton.trackEvent("chatwbsupport", "chat", "productDetail");
        String msg = "";
        if (response_catalog != null && !response_catalog.getTitle().isEmpty())
            msg = "Product Enquiry Request:\n " + response_catalog.getTitle();
        new ChatCallUtils(getActivity(), ChatCallUtils.WB_CHAT_TYPE, msg);
        // Freshchat.showConversations(getActivity());
    }

    @OnClick(R.id.txt_more_category)
    public void clickMoreCategory() {

        if (response_catalog != null) {
            HashMap<String, String> param = new HashMap<>();
            param.put("category", response_catalog.getCategory());
            param.put("type", "catalog");
            param.put("type", "catalog");
            param.put("ctype", "public");
            param.put("ready_to_ship", "true");
            param.put("catalog_type", Constants.CATALOG_TYPE_CAT);

            new DeepLinkFunction(param, getActivity());
        }

    }


    public void getBrandPermission(final Response_catalog response_catalog, final boolean isVisible, final Activity context) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        params.put("brand", response_catalog.getBrand().getId());
        HttpManager.getInstance(context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "brands_permission", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //showBrandEnableDurationDialog(response_catalog, isVisible, context);
                try {
                    openBecomeSellerUI(context, response_catalog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (error.getErrormessage().equalsIgnoreCase(ASK_PERMISSION)) {
                    new MaterialDialog.Builder(context)
                            .content(getResources().getString(R.string.content_ask_right))
                            .positiveText("YES")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    boolean isAskAllowSinglePrice = false;
                                    if (response_catalog.getSingle_piece_price_range() == null) {
                                        isAskAllowSinglePrice = true;
                                    }
                                    getBrandsISell(false, context);
                                    dialog.dismiss();
                                }

                            })
                            .negativeText("NO")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    try {
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();
                } else {
                    new MaterialDialog.Builder(context)
                            .content(error.getErrormessage())
                            .positiveText("YES")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    //  openBecomeSellerUI(context,response_catalog);
                                    dialog.dismiss();


                                }

                            })
                            .negativeText("NO")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    try {
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();
                }

            }
        });
    }

    private void getBrandsISell(boolean isAllowCache, Context context) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        MaterialDialog progrss_dialog = StaticFunctions.showProgress(getActivity());
        progrss_dialog.show();
        ;
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands_distributor", "") + "?expand=true", null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progrss_dialog != null) {
                        progrss_dialog.dismiss();
                    }
                    Response_BrandSell[] responseSell = Application_Singleton.gson.fromJson(response, Response_BrandSell[].class);
                    if (responseSell.length > 0) {
                        ArrayList<String> ids = new ArrayList<>();
                        if (responseSell != null && responseSell.length > 0) {
                            Response_Brands[] response_brands = responseSell[0].getBrands();
                            if (response_brands.length > 0) {
                                for (int i = 0; i < response_brands.length; i++) {
                                    ids.add(response_brands[i].getId());
                                }
                            }
                        }
                        ids.add(response_catalog.getBrand().getId());
                        patchBrandISell(responseSell[0].getId(), ids, context);
                    } else {
                        ArrayList<String> ids = new ArrayList<>();
                        ids.add(response_catalog.getBrand().getId());
                        patchBrandISell(null, ids, context);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progrss_dialog != null) {
                    progrss_dialog.dismiss();
                }
            }
        });

    }

    private void patchBrandISell(String brandISellId, ArrayList<String> ids, Context context) {
        AddBrandDistributor addBrandDistributor = new AddBrandDistributor(ids);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        String url;
        HttpManager.METHOD method;
        if (brandISellId != null) {
            url = URLConstants.companyUrl(getActivity(), "brands_distributor", "") + brandISellId + "/";
            method = HttpManager.METHOD.PATCHWITHPROGRESS;
        } else {
            url = URLConstants.companyUrl(getActivity(), "brands_distributor", "");
            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        }

        HttpManager.getInstance(getActivity()).methodPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, gson.fromJson(gson.toJson(addBrandDistributor), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(getActivity(), "Brand added Successfully", Toast.LENGTH_LONG).show();
                openBecomeSellerUI(context, response_catalog);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void openBecomeSellerUI(Context context, Response_catalog response_catalog) {
        boolean isAskAllowSinglePrice = false;
        if (response_catalog.getSingle_piece_price_range() == null) {
            isAskAllowSinglePrice = true;
        }
        if (context instanceof Activity_AddCatalog || context instanceof Activity_MyViewersDetail) {
            ((Activity) context).startActivityForResult(new Intent(context, Activity_Become_Seller.class)
                    .putExtra("catalog_id", response_catalog.getCatalog_id())
                    .putExtra("catalog_price", response_catalog.getMwp_price_range())
                    .putExtra("isAskAllowSinglePrice", isAskAllowSinglePrice)
                    .putExtra("products", response_catalog.getProduct())
                    .putExtra("category_id", response_catalog.getCategory())
                    .putExtra("product_id", response_catalog.getId())
                    .putExtra("brand_id", response_catalog.getBrand().getId()), BECOME_SELLER_REQUEST);
        } else {
            Fragment_CatalogsGallery.this.startActivityForResult(new Intent(getActivity(), Activity_Become_Seller.class)
                    .putExtra("catalog_id", response_catalog.getCatalog_id())
                    .putExtra("catalog_price", response_catalog.getMwp_price_range())
                    .putExtra("isAskAllowSinglePrice", isAskAllowSinglePrice)
                    .putExtra("products", response_catalog.getProduct())
                    .putExtra("category_id", response_catalog.getCategory())
                    .putExtra("product_id", response_catalog.getId())
                    .putExtra("brand_id", response_catalog.getBrand().getId()), BECOME_SELLER_REQUEST);
        }
    }

    private void brandUIUpdate(final Response_catalog response_catalog) {
        if (response_catalog.getBrand() != null) {
            linear_brand_follow_container.setVisibility(View.VISIBLE);
            linear_brand_follow_container_2.setVisibility(View.VISIBLE);
            txt_brand_name.setText(response_catalog.getBrand().getName());
            txt_brand_name_2.setText(response_catalog.getBrand().getName());
            updateFollowUI(response_catalog.getBrand().getIs_followed() != null ? true : false);
            if (response_catalog.getBrand().getImage().getThumbnail_small() != null &&
                    !response_catalog.getBrand().getImage().getThumbnail_small().isEmpty()) {
                StaticFunctions.loadFresco(getContext(), response_catalog.getBrand().getImage().getThumbnail_small(), img_brand_logo);
                StaticFunctions.loadFresco(getContext(), response_catalog.getBrand().getImage().getThumbnail_small(), img_brand_logo_2);
            }


            btn_brand_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (UserInfo.getInstance(getActivity()).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                        return;
                    }

                    if (response_catalog.getBrand().getIs_followed() == null) {
                        sendBrandFollowAnalytics();
                        Application_Singleton.trackEvent("BtnBrandFollow", "Click", "Follow");
                        callFollowBrand(response_catalog);
                    } else {
                        Application_Singleton.trackEvent("BtnBrandUnFollow", "Click", "Unfollow");
                        callUnfollowBrand(response_catalog);
                    }
                }
            });
        } else {
            relative_follow_brand.setVisibility(View.GONE);
            linear_brand_follow_container.setVisibility(View.GONE);
            linear_brand_follow_container_2.setVisibility(View.GONE);
        }
    }

    private void bottomBarUIUpdate(final Response_catalog response_catalog) {
        btn_send_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserInfo.getInstance(getActivity()).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                    return;
                }

                Application_Singleton.trackEvent("BtnSendInquiry", "Click", "Send Inquiry");
                if (response_catalog.getSupplier_details() != null) {
                    if (response_catalog.getSupplier_details().getTotal_suppliers() > 1) {
                        boolean isSellFullCatalog = false;
                        if (response_catalog.full_catalog_orders_only != null) {
                            if (response_catalog.full_catalog_orders_only.equals("true")) {
                                isSellFullCatalog = true;
                            }
                        }
                        showEnquirySelectDialog(response_catalog.getSupplier(), response_catalog.getSupplier_name(), response_catalog.getSupplier_chat_user(), isSellFullCatalog);
                        /*startActivityForResult(new Intent(getActivity(), ActivityMultipleSeller.class)
                                .putExtra("catalog_name", response_catalog.getTitle())
                                .putExtra("catalog_id", response_catalog.getId())
                                .putExtra("catalog_price", response_catalog.getPrice_range())
                                .putExtra("action", SENDENQUIRY), Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE);*/
                    } else {
                        boolean isSellFullCatalog = false;
                        if (response_catalog.full_catalog_orders_only != null) {
                            if (response_catalog.full_catalog_orders_only.equals("true")) {
                                isSellFullCatalog = true;
                            }
                        }
                        showEnquirySelectDialog(response_catalog.getSupplier(), response_catalog.getSupplier_name(), response_catalog.getSupplier_chat_user(), isSellFullCatalog);
                        //  showEnquireDialog(response_catalog, response_catalog.getSupplier());
                    }
                } else {
                    boolean isSellFullCatalog = false;
                    if (response_catalog.full_catalog_orders_only != null) {
                        if (response_catalog.full_catalog_orders_only.equals("true")) {
                            isSellFullCatalog = true;
                        }
                    }
                    showEnquirySelectDialog(response_catalog.getSupplier(), response_catalog.getSupplier_name(), response_catalog.getSupplier_chat_user(), isSellFullCatalog);
                    // showEnquireDialog(response_catalog, response_catalog.getSupplier());
                }


            }
        });

        btn_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_purchase.getText().toString().contains("GO")) {
                    new NavigationUtils().navigateMyCart(getActivity());
                } else {
                    try {
                        addTocart("Nan");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_catalog.getSupplier_details() != null) {
                    if (response_catalog.getSupplier_details().getTotal_suppliers() > 1) {
                        startActivityForResult(new Intent(getActivity(), ActivityMultipleSeller.class)
                                .putExtra("catalog_name", response_catalog.getTitle())
                                .putExtra("catalog_id", response_catalog.getId())
                                .putExtra("suppliers", response_catalog.getSuppliers())
                                .putExtra("catalog_price", response_catalog.getPrice_range())
                                .putExtra("action", BROKERAGE), Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE);
                    } else {
                        //createPurchaseBrokerOrder(response_catalog);
                    }
                } else {
                    //createPurchaseBrokerOrder(response_catalog);
                }
            }
        });


        btn_chat_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfo.getInstance(getActivity()).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                    return;
                }
                try {
                    generateCatalogEnquiryWithSupplier(response_catalog.getSupplier_name(), response_catalog.getSupplier(), response_catalog.getSupplier_chat_user(), response_catalog.getEnquiry_id(), response_catalog.getProduct_type());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        btn_enable_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_enable_disable.getText().equals(ENABLE_LABEL)) {
                    Application_Singleton.trackEvent("Catalog Detail", "Click", "Start Selling Again");
                } else {
                    Application_Singleton.trackEvent("Catalog Detail", "Click", "Stop Selling");
                }
                enableDisable(true, getActivity(), response_catalog, btn_enable_disable.getText().toString(), Fragment_CatalogsGallery.class.getSimpleName());
            }
        });

        btn_enquiry_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserInfo.getInstance(getActivity()).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                    return;
                }

                startActivityForResult(new Intent(getActivity(), ActivityMultipleSeller.class)
                        .putExtra("catalog_name", response_catalog.getTitle())
                        .putExtra("catalog_id", response_catalog.getId())
                        .putExtra("catalog_price", response_catalog.getPrice_range())
                        .putExtra("action", CHATENQUIRY), Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE);
            }
        });
    }


    public void addTocart(String supplier) {
        try {
            final CartHandler cartHandler = new CartHandler(((AppCompatActivity) getActivity()));
            List<Integer> q = new ArrayList<>();


            for (int i = 0; i < response_catalog.getProducts().size(); i++) {
                q.add(1);
            }
            int a[] = new int[q.size()];
            for (int i = 0; i < a.length; i++) {
                a[i] = q.get(i);
            }

            final ObjectAnimator anim = ObjectAnimator.ofFloat(btn_purchase, "rotationX", 0f, 180f);
            anim.setDuration(800);
            anim.setInterpolator(new LinearInterpolator());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                anim.setAutoCancel(true);
            }
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        super.onAnimationEnd(animation);
                        Log.e("TAG", "onAnimEnd: Called====>");
                        btn_purchase.setText("GO TO CART");
                        btn_purchase.setRotationX(0);
                        MenuItem v = (((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart));
                        v.getActionView().startAnimation(shake);
                    } catch (Exception e) {
                        e.printStackTrace();
                        btn_purchase.setRotationX(0);
                    }
                }
            });
            cartHandler.addCatalogToCart(response_catalog.getId(), "", Fragment_CatalogsGallery.this, "gallery", (AppCompatActivity) getActivity(), null, "Nan");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                showProgress();
            }
            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                @Override
                public void onSuccess(CartProductModel response) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        hideProgress();
                        Toast.makeText(getActivity(), "Product Added to cart", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("TAG", "onSuccess: Called====>");
                    try {
                        anim.cancel();
                        btn_purchase.clearAnimation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btn_purchase.setText("GO TO CART");
                    btn_purchase.setRotationX(0);
                }

                @Override
                public void onFailure() {
                    Log.e("TAG", "onFailure: Called====>");
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        hideProgress();
                    }

                    try {
                        anim.cancel();
                        btn_purchase.clearAnimation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btn_purchase.setText("ADD TO CART");
                    btn_purchase.setRotationX(0);
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                // Don't start animation in android pie. sometime phone not responding issue
            } else {
                anim.start();
            }

            try {
                Application_Singleton.trackEvent("Add to cart", "Click", "From Catalog");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCatalogDataBeforeEnableDisable(final boolean isVisible, final Activity context, final Response_catalog response_catalog_para, final String label, final String from) {
        final MaterialDialog progressDialog = StaticFunctions.showProgressDialog(context, "Loading", "Please wait...", true);
        progressDialog.show();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", response_catalog_para.getId());
        HashMap<String, String> param = new HashMap<>();
        param.put("view_type", "mycatalogs");
        param.put("show_all_products", "true");
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                getSizeEavForList(response_catalog.getCategory(), isVisible, context, response_catalog_para, label, from);

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    /**
     * This function used only for list page start/stop selling
     */

    private void getSizeEavForList(String category_id, final boolean isVisible, final Activity context, final Response_catalog response_catalog_para, final String label, final String from) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "enumvalues", "") + "?category=" + category_id + "&attribute_slug=" + "size";
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (!((Activity) context).isFinishing()) {
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            for (int i = 0; i < enumGroupResponses1.size(); i++) {
                                system_sizes.add(enumGroupResponses1.get(i).getValue());
                            }
                            isProductWithSize = true;
                        }
                    } else {
                        isProductWithSize = false;
                    }
                }

                getMyDetailsForList(isVisible, context, response_catalog_para, label, from);
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    /**
     * This function used only for list page start/stop selling
     */
    private void getMyDetailsForList(final boolean isVisible, final Activity context, final Response_catalog response_catalog_para, final String label, final String from) {

        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", response_catalog_para.getId()), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {

                        productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);
                        if (productMyDetail.isIs_owner()) {
                            response_catalog.setIs_owner(true);
                        }

                        if (productMyDetail.isI_am_selling_this()) {
                            response_catalog.setIs_seller(true);
                        }

                        if (productMyDetail.isCurrently_selling()) {
                            response_catalog.setIs_currently_selling(true);
                        }

                        if (productMyDetail.isI_am_selling_sell_full_catalog()) {
                            response_catalog.setI_am_selling_sell_full_catalog(true);
                        }

                        enableDisable(isVisible, context, response_catalog_para, label, from);


                    } catch (Exception e) {
                        e.printStackTrace();
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


    public void enableDisable(final boolean isVisible, final Activity context, final Response_catalog response_catalog, String label, final String from_call) {
        if (label.equalsIgnoreCase(ENABLE_LABEL)) {
            boolean isOldFlow;
            isOldFlow = openStartStopDialog(context, false);
            if (isOldFlow) {
                final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                        .customView(R.layout.dialog_enable_catalog, true)
                        .positiveText("Done")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                EditText edit_enable_duration = (EditText) dialog.findViewById(R.id.edit_enable_duration);
                                int disable_days = 45;
                                Date todayDate = new Date();
                                Calendar objectCalendar = Calendar.getInstance();
                                objectCalendar.setTime(todayDate);
                                if (!edit_enable_duration.getText().toString().isEmpty()) {
                                    disable_days = Integer.parseInt(edit_enable_duration.getText().toString().trim());
                                }
                                objectCalendar.add(Calendar.DATE, disable_days);
                                Date expire_days = new Date(objectCalendar.getTimeInMillis());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                String expireDateString = sdf.format(expire_days);
                                dialog.dismiss();
                                enableCatalog(expireDateString, context, isVisible, response_catalog, from_call);

                            }
                        })
                        .build();
                final EditText editText = materialDialog.getCustomView().findViewById(R.id.edit_enable_duration);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (editText.getText().toString().trim().isEmpty()) {
                            editText.setError("Duration can't be empty");
                            materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            int days = Integer.parseInt(editText.getText().toString().trim());
                            if (days < 10) {
                                editText.setError("Minimum enable duration greater than 10");
                                materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            } else {
                                materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                materialDialog.show();
            }
        } else {
            boolean isOldFlow;
            isOldFlow = openStartStopDialog(context, true);
            if (isOldFlow) {
                HashMap<String, String> params = new HashMap<String, String>();
                String type = "catalog";
                params.put(type, response_catalog.getId());
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                HttpManager.getInstance(context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_disable", response_catalog.getId()), null, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("CHECKED ENABLED", response);
                        if (isVisible) {
                            sendProductStateChangesAnalytics("Disable");
                            getActivity().setResult(Activity.RESULT_OK);
                            showCatalogs(false);
                        } else {
                            sendProductStateChangesAnalytics("Disable");
                            Toast.makeText(context, "Successfully Stoped Selling", Toast.LENGTH_SHORT).show();
                            if (from_call != null) {
                                if (from_call.equalsIgnoreCase(CatalogBottomSheetDialogFragment.class.getSimpleName())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("from", Fragment_BrowseCatalogs.class.getSimpleName());
                                    bundle.putString("product_id", response_catalog.getId());
                                    Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
                                    gallery.setArguments(bundle);
                                    Application_Singleton.CONTAINER_TITLE = "";
                                    Application_Singleton.CONTAINERFRAG = gallery;
                                    Intent intent = new Intent(context, OpenContainer.class);
                                    intent.putExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                                    context.startActivity(intent);
                                    context.finish();
                                } else {
                                    if (updateListEnableDisable != null) {
                                        getRefreshCatalogData(Application_Singleton.getCurrentActivity(), response_catalog.getId());
                                        updateListEnableDisable.successEnableDisable(false);

                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }

                });
            }
        }
    }

    private void createChatSupplier(String supplierChatUser, String supplierName, String supplierCompanyID, String enquiryID, String product_type) {
        /*Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, supplierChatUser);
        intent.putExtra(ConversationUIService.DISPLAY_NAME, supplierName); //put it for displaying the title.
        intent.putExtra(ConversationUIService.DEFAULT_TEXT, response_catalog.getTitle() + " :"); //put it for displaying the title.
        startActivity(intent);*/


        try {
            generateCatalogEnquiryWithSupplier(supplierName, supplierCompanyID, supplierChatUser, enquiryID, product_type);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void generateCatalogEnquiryWithSupplier(String supplierName, String supplierCompanyID, String supplierChatUser, String enquiryID, String product_type) {
        ResponseCatalogEnquiry responseCatalogEnquiry = new ResponseCatalogEnquiry(UserInfo.getInstance(getActivity()).getCompany_id(), response_catalog.getTitle(), enquiryID, response_catalog.getImage().getThumbnail_small(), response_catalog.getPrice_range(),
                String.valueOf(response_catalog.getProducts().size()), UserInfo.getInstance(getActivity()).getCompanyname());
        responseCatalogEnquiry.setSellerName(supplierName);
        responseCatalogEnquiry.setCatalog(response_catalog.getId());
        responseCatalogEnquiry.setBuyerName(UserInfo.getInstance(getActivity()).getCompanyname());
        responseCatalogEnquiry.setSelling_company(supplierCompanyID);
        responseCatalogEnquiry.setStatus(Constants.CREATED_ENQUIRY);
        responseCatalogEnquiry.setProduct_type(product_type);
        responseCatalogEnquiry.setProduct(response_catalog.getId());
        responseCatalogEnquiry.setSelling_company_chat_user(supplierChatUser);
        String first_message = null;
        if (!isAllowContextBasedChat(getActivity())) {
            if (enquiryID != null) {
                if (enquiryID != null)
                    first_message = "Enquiry ID:" + enquiryID + "\n Catalog name: " + response_catalog.getCatalog_title();
            }
        }
        new OpenContextBasedApplogicChat(getActivity(), responseCatalogEnquiry, OpenContextBasedApplogicChat.BUYERTOSUPPLIER, null, first_message);

    }

    public boolean isAllowContextBasedChat(Context context) {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("CONTEXT_BASED_ENQUIRY_FEATURE_IN_APP")) {
                    try {
                        if (Integer.parseInt(data.get(i).getValue()) == 0) {
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


    public void enableCatalog(String expireDateString, final Activity context, final boolean isVisible, final Response_catalog response_catalog, final String from_call) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HashMap<String, String> param = new HashMap<>();
        param.put("expire_date", expireDateString);
        HttpManager.getInstance(context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_enable", response_catalog.getId()), param, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("CHECKED DISABLED", response);
                sendProductStateChangesAnalytics("Enable");
                Toast.makeText(context, "Product has been enabled", Toast.LENGTH_LONG).show();
                if (isVisible) {
                    showCatalogs(false);
                    String supplier_approval_status = UserInfo.getInstance(context).getSupplierApprovalStatus();
                    if (supplier_approval_status == null) {
                        UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
                    }
                    successDialog(true, context, response_catalog);
                } else {
                    if (from_call != null && from_call.equalsIgnoreCase(Fragment_Catalogs.class.getSimpleName())) {
                        String supplier_approval_status = UserInfo.getInstance(context).getSupplierApprovalStatus();
                        if (supplier_approval_status == null) {
                            UserInfo.getInstance(context).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
                        }

                        if (updateListEnableDisable != null) {
                            getRefreshCatalogData(Application_Singleton.getCurrentActivity(), response_catalog.getId());
                            updateListEnableDisable.successEnableDisable(true);
                        }
                    } else {
                        String supplier_approval_status = UserInfo.getInstance(context).getSupplierApprovalStatus();
                        if (supplier_approval_status == null) {
                            UserInfo.getInstance(context).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
                        }
                        successDialog(false, context, response_catalog);
                    }

                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(getActivity())
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();

                            }

                        })
                        .show();

            }
        });
    }

    private void updateFollowUI(boolean isFollow) {

        if (UserInfo.getInstance(getContext()).getGroupstatus().equals("2")) {
            btn_brand_follow.setVisibility(View.GONE);
        } else {
            btn_brand_follow.setVisibility(View.VISIBLE);
        }

        if (response_catalog != null) {
            if (response_catalog.getBrand().getCompany_id() != null) {

                if (response_catalog.getBrand().getCompany_id().equals(UserInfo.getInstance(getContext()).getCompany_id())) {
                    btn_brand_follow.setVisibility(View.GONE);
                }
            }
        }


        if (isFollow) {
            //btn_brand_follow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            btn_brand_follow.setText(getResources().getString(R.string.btn_followed));
            btn_brand_follow.setTextColor(getResources().getColor(R.color.color_primary));
        } else {
            //btn_brand_follow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            btn_brand_follow.setText(getResources().getString(R.string.btn_follow));
            btn_brand_follow.setTextColor(getResources().getColor(R.color.color_primary));
        }
    }

    public void callSaveWishlist(final Activity activity, String catalogID, String productId) {
        showProgress();
        Application_Singleton.trackEvent("Wishlist", "Save Wishlist", catalogID);
        String userid = UserInfo.getInstance(getActivity()).getUserId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HashMap<String, String> params = new HashMap<String, String>();
        // params.put("catalog", catalogID);
        params.put("user", userid);
        params.put("product", productId);
        HttpManager.getInstance(activity).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.userUrl(activity, "wishlist-catalog", userid), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    StaticFunctions.addWishList(getActivity(), response_catalog.getId());
                    ResponseWishListAdd responseWishListAdd = Application_Singleton.gson.fromJson(response, ResponseWishListAdd.class);
                    sendAddToWishlistAnalyticsData(response_catalog);
                    updateWishlist(true);
                    response_catalog.setIs_addedto_wishlist(responseWishListAdd.getId());
                    showCatalogs(false);
                    int wishcount = UserInfo.getInstance(activity).getWishlistCount() + 1;
                    UserInfo.getInstance(activity).setWishlistCount(wishcount);
                    Toast.makeText(getActivity(), "Catalog successfully added to wishlist", Toast.LENGTH_SHORT).show();
                    StaticFunctions.updateStatics(getActivity());
                    updateToolbarWishlist();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void callRemoveWishlist(final Activity activity, String wishlistID) {
        showProgress();
        Application_Singleton.trackEvent("Wishlist", "Remove Wishlist", response_catalog.getId());
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).methodPost(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.userUrl(activity, "wishlist-catalog", "") + wishlistID + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    StaticFunctions.removeWishList(getActivity(), response_catalog.getId());
                    response_catalog.setIs_addedto_wishlist(null);
                    updateWishlist(false);
                    int wishcount = UserInfo.getInstance(activity).getWishlistCount() - 1;
                    UserInfo.getInstance(activity).setWishlistCount(wishcount);
                    Toast.makeText(getActivity(), "Catalog successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                    showCatalogs(false);
                    StaticFunctions.updateStatics(getActivity());
                    updateToolbarWishlist();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void callFollowBrand(final Response_catalog response_catalog) {
        Log.i("TAG", "callfollowBrand: Detail");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", response_catalog.getBrand().getId());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands-follow", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("Unfollow Cache response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("Unfollow response", response);
                BrandFollowed brandFollowed = Application_Singleton.gson.fromJson(response, BrandFollowed.class);
                updateFollowUI(true);

                initCall(false);
                response_catalog.getBrand().setIs_followed(brandFollowed.getId());
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void callUnfollowBrand(final Response_catalog response_catalog) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", response_catalog.getBrand().getId());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands-follow", response_catalog.getBrand().getIs_followed()), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // Log.v("Unfollow response", response);
                updateFollowUI(false);
                response_catalog.getBrand().setIs_followed(null);

                initCall(false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.i("TAG", "onResponseFailed: Error" + error);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult Gallery: Request Code" + requestCode + "\n Result Code=>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE
                && resultCode == Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE) {
            if (response_catalog != null) {
                if (data.getSerializableExtra("supplier") != null) {
                    MultipleSuppliers multipleSuppliers = (MultipleSuppliers) data.getSerializableExtra("supplier");
                    if (multipleSuppliers.getRelation_id() != null) {
                        multipleSuppliers.setIs_supplier_approved(true);
                    } else {
                        multipleSuppliers.setIs_supplier_approved(false);
                    }
                    if (data.getStringExtra("action") != null) {
                        if (data.getStringExtra("action").equals(PURCHASE)) {
                            // purchase order pass
                            addTocart(multipleSuppliers.getCompany_id());
                            // createPublicPurchaseOrder(response_catalog, multipleSuppliers.getCompany_id(), multipleSuppliers.getName(), multipleSuppliers.is_supplier_approved(), multipleSuppliers.getTrusted_seller());
                        } else if (data.getStringExtra("action").equals(SELL)) {
                            // sales order pass
                            //createSalesOrder(response_catalog, multipleSuppliers.getCompany_id());
                        } else if (data.getStringExtra("action").equals(BROKERAGE)) {
                            // brokerage order pass
                            // createPublicPurchaseBrokerOrder(response_catalog, multipleSuppliers.getCompany_id(), multipleSuppliers.getRelation_id(), multipleSuppliers.getName(), multipleSuppliers.is_supplier_approved(), multipleSuppliers.getTrusted_seller());
                        } else if (data.getStringExtra("action").equals(CHATSUPPLIER)) {
                            createChatSupplier(multipleSuppliers.getChat_user(), multipleSuppliers.getName(), multipleSuppliers.getCompany_id(), multipleSuppliers.getEnquiry_id(), response_catalog.getProduct_type());
                        } else if (data.getStringExtra("action").equals(SENDENQUIRY)) {
                            showEnquirySelectDialog(multipleSuppliers.getCompany_id(), multipleSuppliers.getName(), multipleSuppliers.getChat_user(), multipleSuppliers.isSell_full_catalog());
                            //  showEnquireDialog(response_catalog, multipleSuppliers.getCompany_id());
                        }
                    }

                }

            }
        } else if (requestCode == BECOME_SELLER_REQUEST && resultCode == Activity.RESULT_OK) {
            boolean isVisible = false;
            if (data.getBooleanExtra("isVisible", false)) {
                isVisible = data.getBooleanExtra("isVisible", false);
            }
            if (isVisible) {
                String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
                if (supplier_approval_status == null) {
                    UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
                }
                successDialog(true, getActivity(), response_catalog);
                txt_become_seller_status.setVisibility(View.VISIBLE);
                btn_become_seller.setVisibility(View.GONE);
                //Toast.makeText(context, "You are successful seller of this catalog", Toast.LENGTH_LONG).show();
                Response_catalog.Supplier_details supplier_details = response_catalog.getSupplier_details();
                supplier_details.setTotal_suppliers(response_catalog.getSupplier_details().getTotal_suppliers() + 1);
                supplier_details.setI_am_selling_this(true);
                response_catalog.setSupplier_details(supplier_details);
                showCatalogs(false);
            } else {
                Toast.makeText(getActivity(), "You are successfully seller of this catalog", Toast.LENGTH_LONG).show();
                String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
                if (supplier_approval_status == null) {
                    UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
                }
                successDialog(false, getActivity(), response_catalog);
                // showCatalogs(false);
            }
        } else if (requestCode == ADD_EDIT_SCREEN_SET && resultCode == Activity.RESULT_OK) {
            onRefresh();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Application_Singleton.isFromGallery = null;
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void successDialog(final boolean isVisible, final Activity context, final Response_catalog response_catalog) {
        final MaterialDialog materialDialog1 = new MaterialDialog.Builder(context)
                .customView(R.layout.succes_seller_dialog, true)
                .build();
        materialDialog1.getCustomView().findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog1.dismiss();
            }
        });
        materialDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        materialDialog1.getWindow().setDimAmount(0.8f);
        materialDialog1.setCanceledOnTouchOutside(true);
        materialDialog1.show();
        materialDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isVisible) {
                    if (updateUIBecomeSellerListener != null) {
                        updateUIBecomeSellerListener.upDateBecomeSellerUI();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", "Product List Page");
                        bundle.putString("product_id", response_catalog.getId());
                        new NavigationUtils().navigateDetailPage(context, bundle);
                    }

                }
            }
        });
    }

    public void setUpdateUIBecomeSellerListener(UpdateUIBecomeSellerListener updateUIBecomeSellerListener) {
        this.updateUIBecomeSellerListener = updateUIBecomeSellerListener;
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

                swipe_container.setRefreshing(false);
                initCall(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    private void updateWishlist(boolean isAddedWishlist) {
        if (isAddedWishlist) {
            wishlogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
            wishlogo.setBackground(getResources().getDrawable(R.drawable.circle_filled));
            wishlogo_2.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
            wishlogo_2.setBackground(getResources().getDrawable(R.drawable.circle_filled));
            txt_wishlist.setTextColor(getResources().getColor(R.color.color_primary));
            txt_wishlist.setText("Remove from Wishlist");
            txt_wishlist_layout.setVisibility(View.VISIBLE);
            txt_wishlist_layout_2.setVisibility(View.VISIBLE);
        } else {
            wishlogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_thick_border_bookmark_24px));
            wishlogo.setBackground(getResources().getDrawable(R.drawable.circle));
            wishlogo_2.setImageDrawable(getResources().getDrawable(R.drawable.ic_thick_border_bookmark_24px));
            wishlogo_2.setBackground(getResources().getDrawable(R.drawable.circle));
            txt_wishlist.setText("Add to Wishlist");
            txt_wishlist.setTextColor(getResources().getColor(R.color.color_primary));
            txt_wishlist_layout.setVisibility(View.VISIBLE);
            txt_wishlist_layout_2.setVisibility(View.VISIBLE);
        }
    }


    public void disableActionButton() {
        // if user only manufacturer


        try {

            btn_purchase.setVisibility(View.GONE);
            btn_chat_supplier.setVisibility(View.GONE);
            btn_send_enquiry.setVisibility(View.GONE);
            btn_enquiry_chat.setVisibility(View.GONE);

            btn_become_seller.setVisibility(View.GONE);


            btn_brand_follow.setVisibility(View.GONE);


            txt_wishlist_layout.setVisibility(View.GONE);
            txt_wishlist_layout_2.setVisibility(View.GONE);


            if (btn_purchase.getVisibility() == View.VISIBLE ||
                    btn_chat_supplier.getVisibility() == View.VISIBLE ||
                    btn_send_enquiry.getVisibility() == View.VISIBLE ||
                    btn_enquiry_chat.getVisibility() == View.VISIBLE) {
                linear_flow_button.setVisibility(View.VISIBLE);

            } else {
                linear_flow_button.setVisibility(View.GONE);
            }

            if (getActivity() instanceof OpenContainer) {
                // hide toolbar share option
                ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
            }
        } catch (Exception e) {
        }
    }

    public void disableActionForDisableCatalog() {
        if (getActivity() instanceof OpenContainer) {
            // hide toolbar share option
            // ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.menu_share).setVisible(false);
        }

        btn_purchase.setVisibility(View.GONE);
        btn_chat_supplier.setVisibility(View.GONE);
        btn_send_enquiry.setVisibility(View.GONE);
        btn_enquiry_chat.setVisibility(View.GONE);
        btn_become_seller.setVisibility(View.GONE);
        btn_broker.setVisibility(View.GONE);
        // txt_wishlist_layout.setVisibility(View.GONE);

        if (btn_purchase.getVisibility() == View.VISIBLE ||
                btn_chat_supplier.getVisibility() == View.VISIBLE ||
                btn_send_enquiry.getVisibility() == View.VISIBLE ||
                btn_enquiry_chat.getVisibility() == View.VISIBLE) {
            linear_flow_button.setVisibility(View.VISIBLE);

        } else {
            linear_flow_button.setVisibility(View.GONE);
        }
    }

    public void callCatalogEnquiry(String supplierCompanyID, final String supplierName, final String enquiryabout, final String supplierChatUser, final String item_type,
                                   final String item_quantity, final boolean isFirstCheck, final boolean isSecondCheck, final boolean isThirdCheck) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("enquiry_type", "Text");
        // params.put("catalog", response_catalog.getCatalog_id());
        params.put("product", response_catalog.getId());
        params.put("text", enquiryabout);
        params.put("selling_company", supplierCompanyID);
        params.put("buying_company", UserInfo.getInstance(getActivity()).getCompany_id());
        params.put("item_type", item_type);
        params.put("item_quantity", item_quantity);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalog-enquiries", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    btn_chat_supplier.setVisibility(View.VISIBLE);
                    btn_send_enquiry.setVisibility(View.GONE);
                    showCatalogs(false);
                    ResponseCatalogEnquiry responseCatalogEnquiry = Application_Singleton.gson.fromJson(response, ResponseCatalogEnquiry.class);
                    sendEnquiryAnalytics(responseCatalogEnquiry);
                    responseCatalogEnquiry.setSellerName(supplierName);
                    responseCatalogEnquiry.setBuyerName(UserInfo.getInstance(getActivity()).getCompanyname());
                    responseCatalogEnquiry.setSelling_company_chat_user(supplierChatUser);
                    boolean isQuantity = true;
                    if (item_type.equals(Constants.ENQUIRY_SET)) {
                        isQuantity = true;
                    } else {
                        isQuantity = false;
                    }
                    String qty = item_quantity;
                    String first_message = "";
                    if (isQuantity) {
                        String set = "set";
                        if (Integer.parseInt(qty) > 1) {
                            set = "sets";
                        }
                        first_message = String.format(getResources().getString(R.string.enquiry_first_message), "A Buyer", qty, set, response_catalog.getBrand() != null ? response_catalog.getBrand().getName() : "", response_catalog.getTitle());

                    } else {
                        String pc = "pcs";
                        if (Integer.parseInt(qty) > 1) {
                            pc = "pcs.";
                        }
                        first_message = String.format(getResources().getString(R.string.enquiry_first_message), "A Buyer", qty, pc, response_catalog.getBrand() != null ? response_catalog.getBrand().getName() : "", response_catalog.getTitle());
                    }

                    if (isFirstCheck) {
                        first_message = first_message.concat(" " + getActivity().getResources().getString(R.string.enquiry_message_option_1));
                    }

                    if (isSecondCheck) {
                        if (isFirstCheck) {
                            first_message = first_message.concat(", ");
                        }
                        first_message = first_message.concat(" " + getActivity().getResources().getString(R.string.enquiry_message_option_2));
                    }

                    if (isThirdCheck) {
                        if (isFirstCheck || isSecondCheck) {
                            first_message = first_message.concat(" and");
                        }
                        first_message = first_message.concat(" " + getActivity().getResources().getString(R.string.enquiry_message_option_3));
                    }

                    first_message = first_message.concat(".");
                    new OpenContextBasedApplogicChat(getActivity(), responseCatalogEnquiry, OpenContextBasedApplogicChat.BUYERTOSUPPLIER, null, first_message);
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    void fbShare(Response_catalog response_catalog, Context context, String product_id) {
        if (response_catalog == null) {
            getCatalogsData(product_id, context, "facebook");
        } else {
            boolean installed = appInstalledOrNot("com.facebook.katana", context);
            if (installed) {
                ProductObj[] productObjs = null;
                if (response_catalog.getProduct_type() != null
                        && response_catalog.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
                    String work = "";
                    String fabric = "";
                    if (response_catalog.getEavdata() != null) {
                        if (response_catalog.getEavdata().getWork() != null) {
                            work = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                        if (response_catalog.getEavdata().getFabric() != null) {
                            fabric = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                    }

                    ArrayList<ProductObj> list = new ArrayList<>();
                    for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                        ProductObj productObj = new ProductObj(response_catalog.getPhotos().get(i).getId(),
                                response_catalog.getTitle(),
                                response_catalog.getTitle(),
                                fabric,
                                work, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                        list.add(productObj);
                    }
                    productObjs = list.toArray(new ProductObj[list.size()]);
                } else {
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                }

                if (productObjs != null && productObjs.length > 0) {
                    try {
                        ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_CatalogsGallery.this,
                                productObjs, StaticFunctions.SHARETYPE.FACEBOOK, "WishBook", response_catalog.getTitle(), false, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Facebook is not installed on your phone", Toast.LENGTH_LONG).show();
            }
        }
    }

    void otherShare(Response_catalog response_catalog, Context context, String product_id, boolean isSaveGallery) {
        try {
            if (UserInfo.getInstance(context).isGuest()) {
                StaticFunctions.ShowRegisterDialog(getActivity(), "Product Detail");
                return;
            }
            if (response_catalog == null) {
                getCatalogsData(product_id, context, "other");
            } else {
                String fabric = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE));
                String work = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE));
                String other_details = "";
                if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getOther() != null && response_catalog.getEavdata().getOther().length() > 0) {
                    other_details = "Other Details: " + response_catalog.getEavdata().getOther();
                }
                String size = "";

                if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                    size = "Sizes: " + (response_catalog.getAvailable_sizes()) + "\n";
                }
                String striching = "";
                if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getStitching_type() != null) {
                    striching = "Stitching Details: " + response_catalog.getEavdata().getStitching_type();
                }

                String msg = "";
                if (response_catalog.getBrand() != null) {
                    msg = response_catalog.getTitle() + "\n" + "Brand: " + response_catalog.getBrand().getName() + "\n" + "Fabric: " + fabric + "\nWork: " + work + "\n" + size + "\n" + striching + "" + other_details;
                } else {
                    msg = response_catalog.getTitle() + "\n" + "Fabric: " + fabric + "\nWork: " + work + "\n" + size + "\n" + striching + "" + other_details;
                }

                Application_Singleton.trackEvent("Share Catalog", "Click", "Share Other");
                ProductObj[] productObjs = null;
                if (response_catalog.getProduct_type() != null
                        && response_catalog.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
                    String work_screen = "";
                    String fabric_screen = "";
                    if (response_catalog.getEavdata() != null) {
                        if (response_catalog.getEavdata().getWork() != null) {
                            work_screen = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                        if (response_catalog.getEavdata().getFabric() != null) {
                            fabric_screen = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                    }

                    ArrayList<ProductObj> list = new ArrayList<>();
                    for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                        ProductObj productObj = new ProductObj(response_catalog.getPhotos().get(i).getId(),
                                response_catalog.getTitle(),
                                response_catalog.getTitle(),
                                work_screen,
                                fabric_screen, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                        list.add(productObj);
                    }
                    productObjs = list.toArray(new ProductObj[list.size()]);
                } else {
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                }

                if (productObjs != null && productObjs.length > 0) {
                    try {
                        if (isSaveGallery) {
                            ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_CatalogsGallery.this,
                                    productObjs, StaticFunctions.SHARETYPE.GALLERY, msg, response_catalog.getTitle(), false, false);
                        } else {
                            ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_CatalogsGallery.this,
                                    productObjs, StaticFunctions.SHARETYPE.OTHER, msg, response_catalog.getTitle(), false, false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "No product to share", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void whatsappShare(Response_catalog response_catalog, Context context, String product_id, StaticFunctions.SHARETYPE sharetype) {
        isWhatsappStepone = false;
        try {
            if (response_catalog == null) {
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP)
                    getCatalogsData(product_id, context, "whatsapp");
                else
                    getCatalogsData(product_id, context, "whatsappb");
            } else {
                ProductObj[] productObjs = null;
                if (response_catalog.getProduct_type() != null
                        && response_catalog.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
                    String work = "";
                    String fabric = "";
                    if (response_catalog.getEavdata() != null) {
                        if (response_catalog.getEavdata().getWork() != null) {
                            work = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                        if (response_catalog.getEavdata().getFabric() != null) {
                            fabric = StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE);
                        }
                    }

                    ArrayList<ProductObj> list = new ArrayList<>();
                    for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                        ProductObj productObj = new ProductObj(response_catalog.getPhotos().get(i).getId(),
                                response_catalog.getTitle(),
                                response_catalog.getTitle(),
                                fabric,
                                work, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                        list.add(productObj);
                    }
                    productObjs = list.toArray(new ProductObj[list.size()]);
                } else {
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                }

                if (productObjs != null && productObjs.length > 0) {
                    if (response_catalog.getIs_product_price_null() == null) {
                        response_catalog.setIs_product_price_null(false);
                    }

                    if (!response_catalog.getIs_product_price_null()) {
                        ArrayList<String> price = new ArrayList<String>();
                        for (int i = 0; i < productObjs.length; i++) {
                            price.add(productObjs[i].getFinal_price());
                        }

                    }
                    String fabric = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE));
                    String work = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE));
                    String other_details = "";
                    if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getOther() != null && response_catalog.getEavdata().getOther().length() > 0) {
                        other_details = "Other Details: " + response_catalog.getEavdata().getOther();
                    }
                    String size = "";


                    if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                        size = "Sizes: " + (response_catalog.getAvailable_sizes()) + "\n";
                    }

                    String striching = "";
                    if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getStitching_type() != null) {
                        striching = "Stitching Details: " + response_catalog.getEavdata().getStitching_type();
                    }

                    String msg = "";
                    if (response_catalog.getBrand() != null) {
                        msg = response_catalog.getTitle() + "\n" + "Brand: " + response_catalog.getBrand().getName() + "\n" + "Fabric: " + fabric + "\nWork: " + work + "\n" + size + "\n" + striching + "" + other_details;
                    } else {
                        msg = response_catalog.getTitle() + "\n" + "Fabric: " + fabric + "\nWork: " + work + "\n" + size + "\n" + striching + "" + other_details;
                    }
                    msg = "";
                    if (productObjs.length > 30) {
                        Fragment_WhatsAppSelection frag = new Fragment_WhatsAppSelection();
                        Application_Singleton.CONTAINER_TITLE = "WhatsApp (Pick 30 images to share)";
                        Bundle bundle = new Bundle();
                        bundle.putString("sharetext", msg);
                        if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP)
                            bundle.putString("type", "whatsapp");
                        else
                            bundle.putString("type", "whatsappb");

                        frag.setArguments(bundle);
                        Application_Singleton.CONTAINERFRAG = frag;
                        Intent intent = new Intent(context, OpenContainer.class);
                        intent.getIntExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                        startActivity(intent);
                    } else {
                        try {
                            ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_CatalogsGallery.this,
                                    productObjs, sharetype, msg, response_catalog.getTitle(), false, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    isWhatsappStepone = true;
                } else {
                    Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void wishbookShare(Context context, String product_id) {
        Application_Singleton.trackEvent("Share Catalog", "Click", "Share Wishbook");
        Intent intent = new Intent(context, CatalogShareHolder.class);
        intent.putExtra("product_id", product_id);
        context.startActivity(intent);
    }

    public void linkShare(Response_catalog response_catalog, Context context, String product_id) {
        if (response_catalog == null) {
            getCatalogsData(product_id, context, "link");
        } else {
            String link = "https://app.wishbook.io/?type=product&id=" + response_catalog.getId();
            String share_msg = "";
            if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null && !response_catalog.getBrand().getName().isEmpty()) {
                share_msg = String.format(context.getResources().getString(R.string.share_catalog_link_msg), response_catalog.getTitle(), response_catalog.getBrand().getName());
            } else {
                share_msg = String.format(context.getResources().getString(R.string.share_catalog_link_without_brand_msg), response_catalog.getTitle());
            }

            try {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SUBJECT, "View Catalog");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,
                        share_msg + "\n" + Uri.parse(link));

                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
        }

    }

    private void getCatalogsData(final String id, final Context context, final String option) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_expand_true_id", id), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                if (option.equals("whatsapp")) {
                    whatsappShare(response_catalog, context, id, StaticFunctions.SHARETYPE.WHATSAPP);
                } else if (option.equals("facebook")) {
                    fbShare(response_catalog, context, id);
                } else if (option.equals("other")) {
                    otherShare(response_catalog, context, id, false);
                } else if (option.equals("gallery")) {
                    checkStorePermission(context, response_catalog, id, true);
                } else if (option.equals("whatsappb")) {
                    whatsappShare(response_catalog, context, id, StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS);
                } else if (option.equals("wishbook")) {
                    wishbookShare(context, id);
                } else {
                    linkShare(response_catalog, context, id);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
            }
        });

    }

    public void openUpdateCatalog() {
        Intent intent = new Intent(getActivity(), Activity_AddCatalog.class);
        intent.putExtra("catalog_type", Constants.PRODUCT_TYPE_CAT);
        intent.putExtra("isEditMode", true);
        intent.putExtra("product_id", response_catalog.getId());
        intent.putExtra("catalog_id", response_catalog.getCatalog_id());
        Fragment_CatalogsGallery.this.startActivityForResult(intent, ADD_EDIT_SCREEN_SET);
        WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Edit_screen", "Product Detail page", null);

       /* if (response_catalog != null && getActivity() instanceof OpenContainer) {
            Application_Singleton.CONTAINER_TITLE = "Add Products";
            Bundle bundle2 = new Bundle();
            bundle2.putString("catalog_id", response_catalog.getCatalog_id());
            bundle2.putString("view_permission", "public");
            bundle2.putString("catalog_price", "0");
            bundle2.putString("category_id", response_catalog.getCategory());
            if (response_catalog.full_catalog_orders_only != null) {
                bundle2.putBoolean("catalog_fullproduct", response_catalog.full_catalog_orders_only.equals("true"));
            }
            if (response_catalog.getSingle_piece_price() != null) {
                bundle2.putString("singlePcAddPrice", response_catalog.getSingle_piece_price());
            }

            if (response_catalog.getSingle_piece_price_percentage() != null) {
                bundle2.putString("singlePcAddPer", response_catalog.getSingle_piece_price_percentage());
            }
            ((OpenContainer) getActivity()).getCatalogOptionsFromServer((OpenContainer) getActivity(), bundle2, response_catalog.getCatalog_id(), response_catalog.getProduct_type());
        }*/
    }

    public void updateToolbarWishlist() {
        try {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                if (getActivity() instanceof OpenContainer) {
                    if (((OpenContainer) getActivity()).toolbar.getMenu() != null) {
                        int wishcount = UserInfo.getInstance(getActivity()).getWishlistCount();
                        if (wishcount == 0) {
                            BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                            ActionItemBadge.update(getActivity(), ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                        } else {
                            ActionItemBadge.update(getActivity(), ((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.action_wishlist), getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initSellerApproval() {
        String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
        if (supplier_approval_status != null) {
            if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_PENDING)) {
                linear_btn_catalog.setVisibility(View.GONE);
                relative_become_seller.setVisibility(View.GONE);
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
                linear_btn_catalog.setVisibility(View.GONE);
                relative_become_seller.setVisibility(View.GONE);
                linear_approval_status.setVisibility(View.VISIBLE);
                txt_approval_stage.setText(Html.fromHtml(getResources().getString(R.string.upload_seller_approval_stage3)), TextView.BufferType.NORMAL);
                linear_chat_call.setVisibility(View.VISIBLE);
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD)) {
                linear_btn_catalog.setVisibility(View.GONE);
                relative_become_seller.setVisibility(View.GONE);
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

    public void enableCatalogUpperButton() {

        ((ImageView) v.findViewById(R.id.wishlogo)).setBackground(getResources().getDrawable(R.drawable.circle));
        ((TextView) v.findViewById(R.id.txt_wishlist)).setTextColor(getResources().getColor(R.color.color_primary));

        ((ImageView) v.findViewById(R.id.img_disable)).setBackground(getResources().getDrawable(R.drawable.circle_filled));
        ((TextView) v.findViewById(R.id.txt_add_product)).setTextColor(getResources().getColor(R.color.color_primary));
        linear_add_product.setClickable(true);

        ((ImageView) v.findViewById(R.id.img_share)).setBackground(getResources().getDrawable(R.drawable.circle_filled));
        ((TextView) v.findViewById(R.id.txt_share)).setTextColor(getResources().getColor(R.color.color_primary));

        linear_share.setClickable(true);


        // Upper TopBar(sticky)

        ((ImageView) v.findViewById(R.id.wishlogo_2)).setBackground(getResources().getDrawable(R.drawable.circle));

        ((ImageView) v.findViewById(R.id.img_disable_2)).setBackground(getResources().getDrawable(R.drawable.circle_filled));
        linear_add_product_2.setClickable(true);

        ((ImageView) v.findViewById(R.id.img_share_2)).setBackground(getResources().getDrawable(R.drawable.circle_filled));

        linear_share_2.setClickable(true);


    }

    public void disableShareButton() {
        ((ImageView) v.findViewById(R.id.img_share)).setBackground(getResources().getDrawable(R.drawable.btn_round_fill_purchase));
        ((TextView) v.findViewById(R.id.txt_share)).setTextColor(getResources().getColor(R.color.purchase_medium_gray));
        linear_share.setClickable(false);

        // upper topbar(sticky)
        ((ImageView) v.findViewById(R.id.img_share_2)).setBackground(getResources().getDrawable(R.drawable.btn_round_fill_purchase));
        linear_share_2.setClickable(false);
    }

    public void enableShareButton() {
        ((ImageView) v.findViewById(R.id.img_share)).setBackground(getResources().getDrawable(R.drawable.circle_filled));
        ((TextView) v.findViewById(R.id.txt_share)).setTextColor(getResources().getColor(R.color.color_primary));
        linear_share.setClickable(true);

        // upper topbar(sticky)
        ((ImageView) v.findViewById(R.id.img_share_2)).setBackground(getResources().getDrawable(R.drawable.circle_filled));
        linear_share_2.setClickable(true);
    }

    public void disableAddToWishlistButton() {
        ((ImageView) v.findViewById(R.id.wishlogo)).setBackground(getResources().getDrawable(R.drawable.circle_round_meduim_grey));
        ((TextView) v.findViewById(R.id.txt_wishlist)).setTextColor(getResources().getColor(R.color.purchase_medium_gray));
        txt_wishlist_layout.setClickable(false);

        // upper topbar (sticky)
        ((ImageView) v.findViewById(R.id.wishlogo_2)).setBackground(getResources().getDrawable(R.drawable.circle_round_meduim_grey));
        txt_wishlist_layout_2.setClickable(false);
    }

    public void setScreenButton() {
        if (response_catalog != null
                && response_catalog.getProduct_type() != null
                && response_catalog.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)
                && response_catalog.is_owner()) {
            linear_screen_owner_btn.setVisibility(View.VISIBLE);
            btn_add_screen_set.setVisibility(View.VISIBLE);
            btn_edit_screen_set.setVisibility(View.VISIBLE);
        } else {
            linear_screen_owner_btn.setVisibility(View.GONE);
            btn_add_screen_set.setVisibility(View.GONE);
            btn_edit_screen_set.setVisibility(View.GONE);
        }


        btn_add_screen_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("product_id", response_catalog.getId());
                bundle.putString("catalog_id", response_catalog.getCatalog_id());
                bundle.putString("set_type", response_catalog.getCatalog_multi_set_type());
                bundle.putString("from", Fragment_CatalogsGallery.class.getSimpleName());
                bundle.putBoolean("isEdit", false);
                Fragment_Manage_Images fragment_manage_images = new Fragment_Manage_Images();
                fragment_manage_images.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Edit set";
                Application_Singleton.CONTAINERFRAG = fragment_manage_images;
                Intent shared_by_me_intent = new Intent(getActivity(), OpenContainer.class);
                startActivityForResult(shared_by_me_intent, ADD_EDIT_SCREEN_SET);
            }
        });

        btn_edit_screen_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("product_id", response_catalog.getId());
                bundle.putString("catalog_id", response_catalog.getCatalog_id());
                bundle.putString("set_type", response_catalog.getCatalog_multi_set_type());
                bundle.putString("from", Fragment_CatalogsGallery.class.getSimpleName());
                bundle.putBoolean("isEdit", true);
                Fragment_Manage_Images fragment_manage_images = new Fragment_Manage_Images();
                fragment_manage_images.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Add set";
                Application_Singleton.CONTAINERFRAG = fragment_manage_images;
                Intent shared_by_me_intent = new Intent(getActivity(), OpenContainer.class);
                startActivityForResult(shared_by_me_intent, ADD_EDIT_SCREEN_SET);
            }
        });
    }

    public interface UpdateUIBecomeSellerListener {
        public void upDateBecomeSellerUI();
    }

    public void sendEnquiryAnalytics(ResponseCatalogEnquiry responseCatalogEnquiry) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Enquiry_Placed");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("catalog_item_id", responseCatalogEnquiry.getProduct());
        prop.put("enquiry_id", responseCatalogEnquiry.getId());
        prop.put("selling_company_id", responseCatalogEnquiry.getSelling_company());
        prop.put("selling_company_name", responseCatalogEnquiry.getSellerName());
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendBrandFollowAnalytics() {
        if (response_catalog != null && response_catalog.getBrand() != null) {
            WishbookEvent wishbookEvent = new WishbookEvent();
            wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
            wishbookEvent.setEvent_names("Brand_Follow");
            HashMap<String, String> prop = new HashMap<>();
            prop.put("brand_id", response_catalog.getBrand().getId());
            prop.put("brand_name", response_catalog.getBrand().getName());
            wishbookEvent.setEvent_properties(prop);
            new WishbookTracker(getActivity(), wishbookEvent);
        }
    }

    public void sendAddToWishlistAnalyticsData(Response_catalog response_catalog) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_AddToWishlist");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", "Product Detail Page");
        prop.putAll(getProductAttributes(response_catalog));
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendProductAttributes(Context context, Response_catalog response_catalog) {
        StaticFunctions.addProductViewCount(getActivity(), response_catalog.getId());
        if (!isSendAnalytics) {
            UserInfo userInfo = UserInfo.getInstance(context);
            WishbookEvent wishbookEvent = new WishbookEvent();
            wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_ATTRIBUTES);
            wishbookEvent.setEvent_names("Product_View");
            HashMap<String, String> prop = new HashMap<>();
            if (from != null) {
                if (from.equalsIgnoreCase(Fragment_BrowseCatalogs.class.getSimpleName()))
                    prop.put("source", "Public List");
                else
                    prop.put("source", from);
            }

            prop.put("product_type", response_catalog.getProduct_type());
            prop.put("full_catalog", response_catalog.full_catalog_orders_only);
            if (response_catalog.getBrand() != null)
                prop.put("brand", response_catalog.getBrand().getName());
            else
                prop.put("brand", "No Brand");

            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                prop.put("num_items", String.valueOf(response_catalog.getNo_of_pcs_per_design()));
            } else {
                if (response_catalog.getProduct() != null)
                    prop.put("num_items", String.valueOf(response_catalog.getProduct().length));
            }
            prop.put("product_id", response_catalog.getId());
            prop.put("product_name", response_catalog.getTitle());
            if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_small() != null)
                prop.put("product_cover_image", response_catalog.getImage().getThumbnail_small());
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN))
                prop.put("set_type", response_catalog.getCatalog_multi_set_type());

            if (response_catalog.getPrice_range() != null) {
                prop.put("full_set_price", response_catalog.getPrice_range());
            }


            if (response_catalog.getSingle_piece_price_range() != null)
                prop.put("single_pc_price", response_catalog.getSingle_piece_price());

            if (response_catalog.getCategory_name() != null) {
                prop.put("category", response_catalog.getCategory_name());
            }

            if (response_catalog.getPrice_range() != null) {
                if (response_catalog.getPrice_range().contains("-")) {
                    String[] priceRangeMultiple = response_catalog.getPrice_range().split("-");
                    prop.put("lowest_price", priceRangeMultiple[0]);
                } else {
                    prop.put("lowest_price", response_catalog.getPrice_range());
                }
            }

            prop.put("product_view_count", String.valueOf(StaticFunctions.getProductViewCount(getActivity(), response_catalog.getId())));

            wishbookEvent.setEvent_properties(prop);
            new WishbookTracker(getActivity(), wishbookEvent);
            isSendAnalytics = true;
        }
    }

    public HashMap<String, String> getProductAttributes(Response_catalog response_catalog) {
        HashMap<String, String> prop = new HashMap<>();
        if (response_catalog != null) {
            prop.put("product_type", response_catalog.getProduct_type());
            prop.put("full_catalog", response_catalog.full_catalog_orders_only);
            if (response_catalog.getBrand() != null)
                prop.put("brand", response_catalog.getBrand().getName());
            else
                prop.put("brand", "No Brand");
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                prop.put("num_items", String.valueOf(response_catalog.getNo_of_pcs_per_design()));
            } else {
                if (response_catalog.getProduct() != null)
                    prop.put("num_items", String.valueOf(response_catalog.getProduct().length));
            }

            prop.put("product_id", response_catalog.getId());
            prop.put("product_name", response_catalog.getTitle());
            if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_small() != null)
                prop.put("product_cover_image", response_catalog.getImage().getThumbnail_small());
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN))
                prop.put("set_type", response_catalog.getCatalog_multi_set_type());
            if (response_catalog.full_catalog_orders_only.equals("true")) {
                prop.put("full_set_price", response_catalog.getPrice_range());
            } else {
                prop.put("full_set_price", response_catalog.getPrice_range());
                if (response_catalog.getSingle_piece_price_range() != null)
                    prop.put("single_pc_price", response_catalog.getSingle_piece_price_range());
            }


            if (response_catalog.getCategory_name() != null) {
                prop.put("category", response_catalog.getCategory_name());
            }

            if (response_catalog.getPrice_range() != null) {
                if (response_catalog.getPrice_range().contains("-")) {
                    String[] priceRangeMultiple = response_catalog.getPrice_range().split("-");
                    prop.put("lowest_price", priceRangeMultiple[0]);
                } else {
                    prop.put("lowest_price", response_catalog.getPrice_range());
                }
            }


        }
        return prop;
    }

    public void sendProductStateChangesAnalytics(String type) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_StateChange");
        HashMap<String, String> prop = new HashMap<>();
        prop.putAll(getProductAttributes(response_catalog));
        prop.put("status", type);
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getContext(), wishbookEvent);
    }

    public interface updateListEnableDisable {
        public void successEnableDisable(boolean isEnable);
    }

    public void setUpdateListEnableDisable(updateListEnableDisable updateListEnableDisable) {
        this.updateListEnableDisable = updateListEnableDisable;
    }

    public void getRefreshCatalogData(Context context, String responseCatalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_expand_true_id", responseCatalogID), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {


                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSimilarProduct(final Context context, String productID) {
        boolean is_to_show = true;
        if (response_catalog.getCompany().equals(UserInfo.getInstance(getContext()).getCompany_id()) || response_catalog.is_seller()) {
            is_to_show = false;
        }

        if (is_to_show) {
            try {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "product-recommendation", productID), null, headers, true, new HttpManager.customCallBack() {
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
                                txt_similar_title.setText("Similar Products");
                                linear_similar.setVisibility(View.VISIBLE);
                                setSimilarRecyclerView();
                                BrowseCatalogsAdapter adapter = new BrowseCatalogsAdapter((AppCompatActivity) context, responseSimilarProduct, Constants.PRODUCT_VIEW_GRID, 3, true);
                                recycler_view_similar.setAdapter(adapter);
                            } else {
                                linear_similar.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            linear_similar.setVisibility(View.GONE);
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

    }

    public void setSimilarRecyclerView() {
        recycler_view_similar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view_similar.setHasFixedSize(false);
        recycler_view_similar.setNestedScrollingEnabled(false);
    }

    private void checkStorePermission(Context context, Response_catalog response_catalog, String product_id, boolean isSaveGallery) {
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 1599);
        } else {
            otherShare(this.response_catalog, context, this.product_id, isSaveGallery);
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
                    otherShare(this.response_catalog, getActivity(), this.product_id, true);
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Write External Storage Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    public void openSizeChart() {
        final android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(getActivity());
        }
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_promotion_image_close, null);

        builder.setTitle("")
                .setView(dialogView);

        final ZoomableDraweeView imageView = (ZoomableDraweeView) dialogView.findViewById(R.id.img);
        ImageUtils.loadFrescoZoombable(getActivity(), "https://d21jr61lxgl795.cloudfront.net/promotion_image/size-chart.png", imageView);
        dialogView.findViewById(R.id.img);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        RelativeLayout relative_close = dialogView.findViewById(R.id.relative_close);
        relative_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void addDynamicEav(String label, String value, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_eav_item, null);

        LinearLayout linear_view = v.findViewById(R.id.linear_view);
        TextView txt_eav_label = v.findViewById(R.id.txt_eav_label);
        TextView txt_eav_value = v.findViewById(R.id.txt_eav_value);
        String label_1 = StaticFunctions.formatErrorTitle(label);
        if (label_1.equalsIgnoreCase("Stitching Type")) {
            label_1 = "Stitch";
        }
        txt_eav_label.setText(label_1 + ":");
        txt_eav_value.setText(value);

        root.addView(v);

    }

    public String copyProductDetail(boolean isCopy) {


        StringBuffer copy_details = new StringBuffer();


        int products = 0;
        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            products = Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
        } else {
            products = response_catalog.getProduct().length;
        }
        if (products > 1) {
            if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null) {
                if (!response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    if (response_catalog.getCatalog_type().equalsIgnoreCase("catalog")) {
                        copy_details.append("\u26A1" + response_catalog.getBrand().getName() + " - " + response_catalog.getTitle() + " " + response_catalog.getCategory_name() + " Collection" + "\u26A1" + "\n\n");
                        copy_details.append("*No of Designs*: " + response_catalog.getProduct().length + "\n");

                    } else if (response_catalog.getCatalog_type().equalsIgnoreCase("noncatalog")) {
                        copy_details.append("*" + response_catalog.getTitle() + " Collection of " + response_catalog.getNo_of_pcs() + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                    }
                }

            } else {
                if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    copy_details.append("*" + response_catalog.getTitle() + " Collection of " + response_catalog.getNo_of_pcs_per_design() + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                }
            }
        } else {
            String categoryname = response_catalog.getCategory_name();
            if (response_catalog.getCategory_name().equalsIgnoreCase("Sarees")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Kurtis")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Dress Materials")) {
                categoryname = categoryname.substring(0, response_catalog.getCategory_name().length() - 1);
            }
            copy_details.append(getResources().getString(R.string.unicode_fire) + " *" + "Hot selling " + categoryname + "* " + getResources().getString(R.string.unicode_fire) + "\n\n");
        }
        ////////
        String id = "";
        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            id = "*Set ID*: " + (response_catalog.getId()) + "\n";
        } else {
            id = "*Catalog ID*: " + (response_catalog.getId()) + "\n";
        }

        copy_details.append(id + "\n");

        /////////////
        String size = "";

        if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
            size = "*Available Sizes*: " + (response_catalog.getAvailable_sizes()) + "\n";
        }

        ////////////
        String striching = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getStitching_type() != null) {
            striching = "*Stitching Details*: " + response_catalog.getEavdata().getStitching_type();
        }
        if (!size.isEmpty()) {
            copy_details.append(size + "\n");
        }

        if (!striching.isEmpty()) {
            copy_details.append(striching + "\n");
        }

        String fabric = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE));
        String work = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE));
        copy_details.append("*Fabric*: " + fabric + "\n");
        copy_details.append("*Work*: " + work + "\n");

        ArrayList<String> length = new ArrayList<>();
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getTop() != null) {
            length.add("Top: " + response_catalog.getEavdata().getTop());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getBottom() != null) {
            length.add("Bottom: " + response_catalog.getEavdata().getBottom());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta() != null) {
            length.add("Dupatta: " + response_catalog.getEavdata().getDupatta());
        }
        if (length != null && length.size() > 0) {
            copy_details.append("*Lengths*: " + StaticFunctions.ArrayListToString(length, StaticFunctions.COMMASEPRATEDSPACE) + "\n");
        }

        String other_details = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getOther() != null && response_catalog.getEavdata().getOther().length() > 0) {
            other_details = "*Other Details*: " + response_catalog.getEavdata().getOther();
        }
        if (!other_details.isEmpty()) {
            copy_details.append(other_details + "\n");
        }

        copy_details.append("\n\n");
        copy_details.append("*COD Available*" + "\n");
        if (response_catalog.full_catalog_orders_only.equals("false")) {
            copy_details.append("*Single Piece Available*" + "\n");
        }
        if (isCopy) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
                Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        }
        return copy_details.toString();

    }


    public void openShareDetailsDialog(final StaticFunctions.SHARETYPE sharetype) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_whatsapp_steptwo, null);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));


        dialog.show();

        isWhatsappStepTwo = true;

        RelativeLayout relative_close = dialogView.findViewById(R.id.relative_close);
        relative_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        RelativeLayout relative_share = dialogView.findViewById(R.id.relative_share);
        relative_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWhatsappStepone = false;
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Product Details");
                intent.putExtra(Intent.EXTRA_TEXT, copyProductDetail(false));
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP) {
                    boolean installed = appInstalledOrNot("com.whatsapp", getContext());
                    if (installed) {
                        intent.setPackage("com.whatsapp");
                    } else {
                        Toast.makeText(getActivity(), "Whatsapp is not installed on your phone", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
                    boolean installed = appInstalledOrNot("com.whatsapp.w4b", getContext());
                    if (installed) {
                        intent.setPackage("com.whatsapp.w4b");
                    } else {
                        Toast.makeText(getActivity(), "Whatsapp Business is not installed on your phone", Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                startActivity(intent);
            }
        });
    }

    public boolean openStartStopDialog(final Context context, boolean isStopSelling) {
        boolean isOldFlow = false;
        if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            // Catalog-NonCatalog
            StartStopBottomDialog startStopBottomDialog = null;
            Bundle bundle = new Bundle();
            bundle.putSerializable("products", response_catalog.getProduct());
            bundle.putBoolean("isStopSelling", isStopSelling);
            bundle.putString("category_id", response_catalog.getCategory());
            bundle.putString("id", response_catalog.getId());
            if (!response_catalog.isI_am_selling_sell_full_catalog()) {
                if (isProductWithSize && system_sizes.size() > 0) {
                    bundle.putSerializable("type", StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE);
                    isOldFlow = false;
                } else {
                    bundle.putSerializable("type", StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE);
                    isOldFlow = false;
                }
            } else {
                if (isProductWithSize && system_sizes.size() > 0) {
                    bundle.putSerializable("type", StartStopHandler.STARTSTOP.FULL_WITH_SIZE);
                    isOldFlow = false;
                } else {
                    // Continue old ui
                    isOldFlow = true;
                }
            }


            bundle.putString("catalogname", response_catalog.getTitle());
            startStopBottomDialog = StartStopBottomDialog.newInstance(bundle);
            if (!isOldFlow) {
                startStopBottomDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), startStopBottomDialog.getTag());
                startStopBottomDialog.setStartStopDoneListener(new StartStopBottomDialog.StartStopDoneListener() {
                    @Override
                    public void onSuccessStart() {
                        // To Do refresh the page
                        Toast.makeText(context, "Product Successfully started", Toast.LENGTH_SHORT).show();
                        sendProductStateChangesAnalytics("Enable");
                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            showCatalogs(false);
                        }

                    }

                    @Override
                    public void onSuccessStop() {
                        // To Do refresh the page
                        Toast.makeText(context, "Product Successfully stopped", Toast.LENGTH_SHORT).show();
                        sendProductStateChangesAnalytics("Disable");
                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            showCatalogs(false);
                        }

                        // Product Stock check Dialog (15Days Cron)
                        if (updateListEnableDisable != null) {
                            getRefreshCatalogData(Application_Singleton.getCurrentActivity(), response_catalog.getId());
                            updateListEnableDisable.successEnableDisable(false);
                        }

                    }
                });
            }
        } else {
            // Set Matching case
            isOldFlow = true;
        }
        return isOldFlow;
    }

    public void getMyDetails(Context context, String catalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", catalogID), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {

                            productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);

                           /* if(header_adapter!=null) {
                                header_adapter.setProductsMyDetails(productMyDetail);
                            }

                            if(allItemAdapter!=null) {
                                allItemAdapter.setProductsMyDetails(productMyDetail);
                            }*/
                            isMyCatalog = 2;
                            if (productMyDetail.isIs_owner()) {
                                response_catalog.setIs_owner(true);
                                isMyCatalog = 1;
                            }

                            if (productMyDetail.isI_am_selling_this()) {
                                response_catalog.setIs_seller(true);
                                isMyCatalog = 1;
                            }

                            if (productMyDetail.isCurrently_selling()) {
                                response_catalog.setIs_currently_selling(true);
                            }

                            if (productMyDetail.isI_am_selling_sell_full_catalog()) {
                                response_catalog.setI_am_selling_sell_full_catalog(true);
                            }

                            updateCatalogUI();
                            showPromotionBanner();
                            getSimilarProduct(getActivity(), response_catalog.getId());

                            // Temporary function
                            hideOrderDisableConfig();

                            if (!isGetMyDetails) {
                                isGetMyDetails = true;
                                if (isMyCatalog > 0 && isMyCatalog == 1) {
                                    // refresh the page
                                    initCall(false);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSizeEav(final Context context, String category_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "enumvalues", "") + "?category=" + category_id + "&attribute_slug=" + "size";
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (!((Activity) context).isFinishing()) {
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            for (int i = 0; i < enumGroupResponses1.size(); i++) {
                                system_sizes.add(enumGroupResponses1.get(i).getValue());
                            }
                            isProductWithSize = true;
                        }
                    } else {
                        isProductWithSize = false;
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void getDeliveryInfo(Context context, String pincode, String product_id) {
        final MaterialDialog progressDialog = StaticFunctions.showProgressDialog(context, "Loading", "Please wait...", true);
        progressDialog.show();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> params = new HashMap<>();
        params.put("pincode", pincode);
        params.put("product", product_id);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.DELIVERY_INFO, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                final DeliveryInfo delivery_info = Application_Singleton.gson.fromJson(response, new TypeToken<DeliveryInfo>() {
                }.getType());
                if (delivery_info != null) {
                    linear_delivery_info_container.setVisibility(View.VISIBLE);
                    if (delivery_info.isDelivery())
                        txt_delivery_delivery_value.setText("Available");
                    else
                        txt_delivery_delivery_value.setText("Unavailable");

                    if (delivery_info.isCod_available()) {
                        txt_delivery_cod_value.setText("Available");
                    } else {
                        txt_delivery_cod_value.setText("Unavailable");
                    }

                    ArrayList<String> temp_shipping_method = new ArrayList<>();
                    if (delivery_info.getShipping_methods() != null) {
                        for (ResponseShipment ship :
                                delivery_info.getShipping_methods()) {
                            temp_shipping_method.add(ship.getShipping_method_name());
                        }
                    }
                    if (temp_shipping_method.size() > 0) {
                        txt_delivery_shipping_options.setVisibility(View.VISIBLE);
                        txt_delivery_shipping_options.setText(StaticFunctions.ArrayListToString(temp_shipping_method, StaticFunctions.COMMASEPRATED));
                    }
                } else {
                    linear_delivery_info_container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public boolean isContentIsValidHtml(String content) {
        Pattern pattern = Pattern.compile(HTML_TAG_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(content);
        return matcher.matches();
    }

    @Override
    public void addImage(int position, ProductObj productObj) {

        DialogFragment dialog = new Fragment_AddAdditionalImages();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", productObj);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "dialog");


        ((Fragment_AddAdditionalImages) dialog).setAdditionalImageChangeListener(new Fragment_AddAdditionalImages.AdditionalImageChangeListener() {
            @Override
            public void additionalImageChange() {
                onRefresh();
            }
        });

    }

    public void showPromotionBanner() {
        String referral_banner_img = "";
        if (PrefDatabaseUtils.getConfig(getActivity()) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(getActivity()), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("REFERRAL_BANNER_IN_APP")) {
                    referral_banner_img = data.get(i).getValue();
                    break;
                }
            }
        }
        if (referral_banner_img != null && !referral_banner_img.isEmpty()) {
            refer_earn_promotion_img_2.setVisibility(View.VISIBLE);
            StaticFunctions.loadFresco(getActivity(), referral_banner_img, refer_earn_promotion_img_2);
        } else {
            refer_earn_promotion_img_2.setVisibility(View.GONE);
        }

        refer_earn_promotion_img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "refer_earn");
                param.put("from", "CatalogDetail-In App Banner");
                new DeepLinkFunction(param, getActivity());
            }
        });
    }

    public void openClearanceDiscountDialog(ProductObj[] productObjs, String catalog_seller_id, double clearance_discount_percentage) {
        ArrayList<ProductObj> arrayList = new ArrayList<ProductObj>(Arrays.asList(productObjs));
        DialogFragment dialog = new Dialog_ClearanceDiscount();
        Bundle bundle = new Bundle();
        bundle.putSerializable("products", arrayList);
        bundle.putString("catalog_seller_id", catalog_seller_id);
        bundle.putDouble("my_clearance_discount_percentage", clearance_discount_percentage);
        bundle.putSerializable("product_my_detail", productMyDetail);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "dialog");

        ((Dialog_ClearanceDiscount) dialog).setClearanceDiscountChangeListener(new Dialog_ClearanceDiscount.ClearanceDiscountChangeListener() {
            @Override
            public void clearanceDiscountChange() {
                onRefresh();
            }
        });
    }

    public void openViewTaxDialog() {
        if (response_catalog != null) {
            DialogViewTaxShipping dialog = new DialogViewTaxShipping();
            Bundle bundle = new Bundle();
            bundle.putString("product_id", response_catalog.getId());
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "dialog");
        }
    }

    public void showHideTopStickyBar() {
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int maxDistance = linear_delivery_root_container.getHeight() + 170;
                int movement = nestedScrollView.getScrollY();
                if (movement >= 0 && movement <= maxDistance) {
                    catalog_sticky_bar.setVisibility(View.GONE);
                } else {
                    catalog_sticky_bar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig() {
        // Edit Catalog hide
        // Send Enquiry hide
        // Become a Seller hide
        // add catalog button hide from catalog level
        // Clearance discount hide
        if (StaticFunctions.checkOrderDisableConfig(getActivity())) {
            btn_purchase.setVisibility(View.GONE);
            btn_chat_supplier.setVisibility(View.GONE);
            btn_send_enquiry.setVisibility(View.GONE);
            btn_enquiry_chat.setVisibility(View.GONE);
            btn_become_seller.setVisibility(View.GONE);
            linear_btn_catalog.setVisibility(View.GONE);
            btn_enable_disable.setVisibility(View.GONE);

            refer_earn_promotion_img_2.setVisibility(View.GONE);

            linear_add_product.setVisibility(View.GONE);
            linear_add_product_2.setVisibility(View.GONE);


            linear_clearance_discount_root_container.setVisibility(View.GONE);
            linear_clearance_discount.setVisibility(View.GONE);



            if (btn_purchase.getVisibility() == View.VISIBLE ||
                    btn_chat_supplier.getVisibility() == View.VISIBLE ||
                    btn_send_enquiry.getVisibility() == View.VISIBLE ||
                    btn_enquiry_chat.getVisibility() == View.VISIBLE) {
                linear_flow_button.setVisibility(View.VISIBLE);
            } else {
                linear_flow_button.setVisibility(View.GONE);
            }

        }
    }

}
