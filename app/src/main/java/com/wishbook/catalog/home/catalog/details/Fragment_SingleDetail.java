package com.wishbook.catalog.home.catalog.details;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
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
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.Utils.widget.LoopViewPager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostPushUserId;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.DeliveryInfo;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.ResponseWishListAdd;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.SelectSizeBottomSheet;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.catalog.ResellerCatalogShareBottomSheet;
import com.wishbook.catalog.home.catalog.adapter.ProductThumbAdapter;
import com.wishbook.catalog.home.catalog.share.Fragment_WhatsAppSelection;
import com.wishbook.catalog.home.catalog.share.ProductSingleShareApi;
import com.wishbook.catalog.home.catalog.social_share.BottomShareDialog;
import com.wishbook.catalog.home.models.ProductObj;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class Fragment_SingleDetail extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {


    private View v;

    @BindView(R.id.product_img)
    SimpleDraweeView product_img;

    @BindView(R.id.linear_flow_button)
    LinearLayout linear_flow_button;

    @BindView(R.id.linear_dispatch_detail)
    LinearLayout linear_dispatch_detail;

    @BindView(R.id.txt_dispatch_value)
    TextView txt_dispatch_value;


    @BindView(R.id.relative_design)
    RelativeLayout relative_design;

    @BindView(R.id.txt_catalog_detail_number_design)
    TextView txt_catalog_detail_number_design;

    @BindView(R.id.txt_catalog_detail_label)
    TextView txt_catalog_detail_label;


    @BindView(R.id.txt_catalog_detail_label_design)
    TextView txt_catalog_detail_label_design;

    @BindView(R.id.txt_catalog_detail_category)
    TextView txt_catalog_detail_category;

    @BindView(R.id.txt_size_chart)
    TextView txt_size_chart;

    @BindView(R.id.txt_copy_details)
    TextView txt_copy_details;

    @BindView(R.id.linear_eav_data)
    LinearLayout linear_eav_data;

    @BindView(R.id.btn_support_chat)
    TextView btn_support_chat;

    @BindView(R.id.linear_size_container)
    LinearLayout linear_size_container;

    @BindView(R.id.txt_size_value)
    TextView txt_size_value;

    @BindView(R.id.linear_material)
    LinearLayout linear_material;


    @BindView(R.id.relative_size)
    RelativeLayout relative_size;

    @BindView(R.id.card_catalog_summary)
    LinearLayout card_catalog_summary;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    @BindView(R.id.relative_other_details)
    RelativeLayout relative_other_details;
    @BindView(R.id.txt_other)
    TextView txt_other;

    @BindView(R.id.linear_return_policy)
    LinearLayout linear_return_policy;
    @BindView(R.id.linear_delivery_time)
    LinearLayout linear_delivery_time;
    @BindView(R.id.txt_delivery_value)
    TextView txt_delivery_value;
    @BindView(R.id.txt_return_value)
    TextView txt_return_value;

    @BindView(R.id.txt_delivery_label)
    TextView txt_delivery_label;
    @BindView(R.id.txt_ready_to_dispatch)
    TextView txt_ready_to_dispatch;


    @BindView(R.id.relative_category)
    RelativeLayout relative_category;

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

    @BindView(R.id.btn_add_to_cart)
    AppCompatButton btn_add_to_cart;
    @BindView(R.id.btn_share_product)
    AppCompatButton btn_share_product;

    @BindView(R.id.relative_product_price)
    RelativeLayout relative_product_price;

    @BindView(R.id.txt_single_pc_price)
    TextView txt_single_pc_price;

   /* @BindView(R.id.relative_product_view_tax_link)
    RelativeLayout relative_product_view_tax_link;*/

    @BindView(R.id.txt_product_view_tax_link)
    TextView txt_product_view_tax_link;

    @BindView(R.id.relative_collection)
    RelativeLayout relative_collection;

    @BindView(R.id.txt_collection_name)
    TextView txt_collection_name;

    @BindView(R.id.txt_collection_name_subtext)
    TextView txt_collection_name_subtext;

    @BindView(R.id.txt_all_review)
    TextView txt_all_review;

    @BindView(R.id.txt_total_rating_review)
    TextView txt_total_rating_review;

    @BindView(R.id.txt_avg_rating)
    TextView txt_avg_rating;

    @BindView(R.id.rating_root_container)
    LinearLayout rating_root_container;

    // ### Product ID Show ### //
    @BindView(R.id.relative_product_id)
    RelativeLayout relative_product_id;

    @BindView(R.id.txt_product_id_label)
    TextView txt_product_id_label;

    @BindView(R.id.txt_product_id_value)
    TextView txt_product_id_value;

    @BindView(R.id.relative_viewpager_container)
    RelativeLayout relative_viewpager_container;

    @BindView(R.id.viewPager)
    LoopViewPager viewPager;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    @BindView(R.id.recyclerview_thumb_img)
    RecyclerView recyclerview_thumb_img;

    // ### Referral Banner
    @BindView(R.id.refer_earn_promotion_img_1)
    SimpleDraweeView refer_earn_promotion_img_1;


    // ### MWP Price
    @BindView(R.id.txt_single_mwp_price)
    TextView txt_single_mwp_price;

    @BindView(R.id.txt_single_clearance_discount)
    TextView txt_single_clearance_discount;

    @BindView(R.id.linear_collection_other_images)
    LinearLayout linear_collection_other_images;

    @BindView(R.id.txt_view_collection_title)
    TextView txt_view_collection_title;

    @BindView(R.id.flex_collection_other_images)
    FlexboxLayout flex_collection_other_images;

    @BindView(R.id.txt_extra_images)
    TextView txt_extra_images;

    @BindView(R.id.txt_material_detail)
    TextView txt_material_detail;

    @BindView(R.id.txt_product_disable_note)
    TextView txt_product_disable_note;

    boolean isSeeMore = false;
    Handler handler;
    Runnable runnable;
    private SwipeRefreshLayout swipe_container;
    boolean isAllowCache;


    private static final String HTML_TAG_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    String product_id;
    public Response_catalog response_catalog;
    SharedPreferences preferences;
    ProductMyDetail productMyDetail;
    boolean isGetMyDetails = false;
    int isMyCatalog = 0;

    boolean isSendAnalytics;
    String from = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_product_detail, ga_container, true);
        ButterKnife.bind(this, v);
        isAllowCache = true;
        preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        txt_catalog_detail_label.setText("Product details");
        initSwipeRefresh(v);
        initCall();
        return v;
    }

    public void initCall() {
        if (getArguments() != null && getArguments().getString("product_id") != null) {
            product_id = getArguments().getString("product_id");
            if (getArguments() != null && getArguments().getString("from") != null) {
                from = getArguments().getString("from");
            }
            callProductDetail();
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void callProductDetail() {
        final HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        String url;
        url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", product_id);
        HashMap<String, String> param = new HashMap<>();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, isAllowCache, new HttpManager.customCallBack() {
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
                        response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                        if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_medium() != null) {
                            StaticFunctions.loadFresco(getActivity(), response_catalog.getImage().getThumbnail_medium(), product_img);
                        }
                        getMyDetails(getActivity(), response_catalog.getId());
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
    }

    public void setDetailUI() {
        setCatalogSummary(response_catalog);
        initDeliveryInfoListner();
        setActionButton();
        sendProductAttributes(getActivity(), response_catalog);
        sendCatalogView();
        setAdditionalImg();
        initRatingView();

        if (response_catalog.getIs_addedto_wishlist() == null) {
            updateWishlist(false);
        } else {
            updateWishlist(true);
        }
        showPromotionBanner();
        if (response_catalog.getBundle_product_id() != null)
            getBundleExpandTrue(response_catalog.getBundle_product_id(), response_catalog.getCatalog_id());

        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            disableActionButton();
        }

        hideOrderDisableConfig();
    }

    public void setAdditionalImg() {
        recyclerview_thumb_img.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerview_thumb_img.setNestedScrollingEnabled(false);

        if (response_catalog.getPhotos().size() > 1) {
            product_img.setVisibility(View.GONE);
            recyclerview_thumb_img.setVisibility(View.VISIBLE);
            relative_viewpager_container.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            final ProductThumbAdapter productThumbAdapter = new ProductThumbAdapter(getActivity(), response_catalog.getPhotos());
            recyclerview_thumb_img.setAdapter(productThumbAdapter);
            productThumbAdapter.setOnThumbSelectorListener(new ProductThumbAdapter.OnThumbSelectorListener() {
                @Override
                public void onSelect(int position) {
                    viewPager.setCurrentItem(position);
                }
            });

            ProductPhotosPagerAdapter productPhotosPagerAdapter = new ProductPhotosPagerAdapter(getActivity(), response_catalog.getPhotos());
            viewPager.setAdapter(productPhotosPagerAdapter);
            viewPager.setCurrentItem(0, false); // set current item in the adapter to middle
            indicator.setViewPager(viewPager);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    productThumbAdapter.setSelected_position(i);
                    productThumbAdapter.notifyDataSetChanged();
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

        } else {
            product_img.setVisibility(View.VISIBLE);
            relative_viewpager_container.setVisibility(View.GONE);
            recyclerview_thumb_img.setVisibility(View.GONE);
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
        } else {
            rating_root_container.setVisibility(View.GONE);
        }

    }

    public void setCatalogSummary(final Response_catalog response_catalog) {
        if (response_catalog != null && (response_catalog.isSupplier_disabled())) {
            txt_product_disable_note.setVisibility(View.VISIBLE);
        } else {
            txt_product_disable_note.setVisibility(View.GONE);
        }
        relative_design.setVisibility(View.GONE);
        card_catalog_summary.setVisibility(View.VISIBLE);
        txt_other.setVisibility(View.GONE);
        relative_other_details.setVisibility(View.GONE);
        linear_size_container.setVisibility(View.GONE);
        relative_size.setVisibility(View.GONE);
        linear_collection_other_images.setVisibility(View.GONE);

        // ## Show Collection Name
        relative_collection.setVisibility(View.GONE);
        txt_collection_name.setText(response_catalog.getCatalog_title());
        txt_collection_name.setPaintFlags(txt_collection_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_collection_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_catalog.getBundle_product_id() != null && !response_catalog.getBundle_product_id().isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "Single Detail");
                    bundle.putString("product_id", response_catalog.getBundle_product_id());
                    new NavigationUtils().navigateDetailPage(getActivity(), bundle);
                }
            }
        });

        txt_collection_name_subtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_catalog.getBundle_product_id() != null && !response_catalog.getBundle_product_id().isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "Single Detail");
                    bundle.putString("product_id", response_catalog.getBundle_product_id());
                    new NavigationUtils().navigateDetailPage(getActivity(), bundle);
                }
            }
        });


        relative_product_price.setVisibility(View.VISIBLE);
        txt_single_pc_price.setText("\u20B9" + response_catalog.getSingle_piece_price());


        if (response_catalog.getSingle_discount() > 0) {

            txt_single_clearance_discount.setVisibility(View.VISIBLE);
            txt_single_clearance_discount.setText(Math.round(response_catalog.getSingle_discount()) + "% off");

            if (response_catalog.getMwp_single_price() > 0) {
                txt_single_mwp_price.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray));
                txt_single_mwp_price.setPaintFlags(txt_single_mwp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txt_single_mwp_price.setVisibility(View.VISIBLE);
                txt_single_mwp_price.setText("\u20B9" + response_catalog.getMwp_single_price());
            } else {
                txt_single_mwp_price.setVisibility(View.GONE);
            }
        } else {
            txt_single_clearance_discount.setVisibility(View.GONE);
        }

        txt_product_view_tax_link.setPaintFlags(txt_product_view_tax_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (!response_catalog.is_seller()) {
            // relative_product_view_tax_link.setVisibility(View.VISIBLE);
            txt_product_view_tax_link.setVisibility(View.VISIBLE);
            txt_product_view_tax_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openViewTaxDialog();
                }
            });
        } else {
            //  relative_product_view_tax_link.setVisibility(View.GONE);
            txt_product_view_tax_link.setVisibility(View.GONE);
        }


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
                                //txt_work.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                //txt_work.setText(valueString);
                                material_details.append(valueString + " | ");
                            }

                            if (response_catalog.getEavdatajson().get("work") == null && response_catalog.getEavdatajson().get("fabric") == null) {
                                linear_material.setVisibility(View.GONE);
                            }
                            break;

                        case "fabric":
                            if (response_catalog.getEavdatajson().get(key) != null && valueString != null) {
                               // txt_fabric.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                               // txt_fabric.setText(valueString);
                                material_details.append(valueString + " | ");
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
                                material_details.append(valueString + " | ");
                                //add linearlayout
                                if (!key.equalsIgnoreCase("gender")) {
                                    isanyother_eav = true;
                                    //addDynamicEav(key, valueString, linear_eav_data);
                                }
                            }
                            break;
                    }
                }

            }

        }

        if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
            linear_size_container.setVisibility(View.VISIBLE);
            relative_size.setVisibility(View.VISIBLE);
            txt_size_value.setText(response_catalog.getAvailable_sizes());

        } else {
            linear_size_container.setVisibility(View.GONE);
        }

        if (response_catalog.getId() != null && !response_catalog.getId().isEmpty()) {
            relative_product_id.setVisibility(View.GONE);
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                txt_product_id_label.setText("Set ID: ");
            } else {
                txt_product_id_label.setText("Product ID: ");
            }
            txt_product_id_value.setText(response_catalog.getId());
        } else {
            relative_product_id.setVisibility(View.GONE);
        }

        if (lengths != null && lengths.size() > 0) {
            material_details.append(StaticFunctions.ArrayListToString(lengths, StaticFunctions.COMMASEPRATED) + " | ");
            // txt_length.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            //relative_lengths.setVisibility(View.VISIBLE);

            //txt_length.setText(StaticFunctions.ArrayListToString(lengths, StaticFunctions.COMMASEPRATED));
        } else {
            //relative_lengths.setVisibility(View.GONE);
        }

        txt_material_detail.setText(material_details.toString());

        // modify for txt_catalog_detail_label_design:


        txt_catalog_detail_label_design.setText("Design no :");
        txt_catalog_detail_number_design.setText(String.valueOf(response_catalog.getTitle()));

        // set category name in catalog deatils section and more product from category(below similar products)
        if (response_catalog.getCategory_name() != null) {
            txt_catalog_detail_category.setText(response_catalog.getCategory_name());
        } else {
            relative_category.setVisibility(View.GONE);
        }

        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalog.getTitle() != null) {
                String temp = "<font color='#3a3a3a' size='16'>" + response_catalog.getTitle() + "</font><font color='#777777' size='16'>" + " From " + "</font><font color='#3a3a3a' size='16'>" + response_catalog.getCatalog_title() + "</font>";
                //txt_catalog_name.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
            }
        } else {
            if (response_catalog.getTitle() != null) {
                //txt_catalog_name.setText(response_catalog.getTitle());
            }
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
                } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN_SINGLE)) {
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


        try {
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
                    copyProductDetail(getActivity(), true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setActionButton() {
        if (response_catalog != null) {
            btn_add_to_cart.setVisibility(View.VISIBLE);
            if (response_catalog.is_owner()
                    || (response_catalog.is_seller() && response_catalog.is_currently_selling())
                    || !response_catalog.isSelling()) {
                btn_add_to_cart.setVisibility(View.GONE);
            }
            checkAlreadyAddedToCart();
            btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btn_add_to_cart.getText().toString().contains("GO")) {
                        new NavigationUtils().navigateMyCart(getActivity());
                    } else {
                        new CartHandler(((AppCompatActivity) getActivity())).new GetSize(getActivity(), response_catalog.getCategory(), new CartHandler.SizeGetCallbackListener() {
                            @Override
                            public void onSuccess(boolean isSizeMandatory) {
                                if (isSizeMandatory && response_catalog.getAvailable_sizes() == null) {
                                    new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                            .content("Product out of stock")
                                            .positiveText("OK")
                                            .cancelable(false)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    dialog.dismiss();
                                                    btn_add_to_cart.clearAnimation();
                                                    btn_add_to_cart.setRotationX(0);
                                                    btn_add_to_cart.setText("ADD TO CART");
                                                }
                                            })
                                            .show();
                                    btn_add_to_cart.clearAnimation();
                                    btn_add_to_cart.setRotationX(0);
                                    btn_add_to_cart.setText("ADD TO CART");
                                }
                                if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                                    SelectSizeBottomSheet bottomSheetDialog = SelectSizeBottomSheet.getInstance(response_catalog.getAvailable_sizes(), response_catalog.getSingle_piece_price(), "1", "product");
                                    bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Custom Bottom Sheet");
                                    bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                                        @Override
                                        public void onDismiss(HashMap<String, Integer> value) {
                                            if (value.size() > 0) {
                                                List<String> selected_sizes = new ArrayList<>(value.keySet());
                                                ArrayList<ProductObj> productObjs = new ArrayList<>();
                                                for (int j = 0; j < selected_sizes.size(); j++) {
                                                    if (selected_sizes.get(j) != null && !selected_sizes.get(j).isEmpty()) {
                                                        String note = "Size : " + selected_sizes.get(j);
                                                        ProductObj p_temp = new ProductObj(response_catalog.getId(),
                                                                response_catalog.getSingle_piece_price(), note);
                                                        p_temp.setQuantity(String.valueOf(value.get(selected_sizes.get(j))));
                                                        p_temp.setCatalog_id(response_catalog.getCatalog_id());
                                                        productObjs.add(p_temp);
                                                    }
                                                }
                                                prePareAddToCartRequest(productObjs);
                                            }
                                        }
                                    });
                                } else {
                                    prePareAddToCartRequest(null);
                                }
                            }

                            @Override
                            public void onFailure() {

                            }
                        });

                    }
                }
            });

            btn_share_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);
                    if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {

                        // Open Reseller Share Bottom Sheet
                        String from = "Single Product Detail";
                        ResellerCatalogShareBottomSheet resellerCatalogShareBottomSheet = ResellerCatalogShareBottomSheet.newInstance(response_catalog.getId(),from);
                        resellerCatalogShareBottomSheet.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Custom Bottom Sheet");

                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("whatsapp", true);
                        bundle.putBoolean("whatsappb", true);
                        bundle.putBoolean("fb", true);
                        bundle.putBoolean("gallery", true);
                        bundle.putBoolean("other", true);
                        BottomShareDialog bottomSheetDialog = BottomShareDialog.getInstance(bundle);
                        bottomSheetDialog.show((getActivity()).getSupportFragmentManager(), "Custom Bottom Sheet");
                        bottomSheetDialog.setDismissListener(new BottomShareDialog.DismissListener() {
                            @Override
                            public void onDismiss(StaticFunctions.SHARETYPE type) {
                                if (type != null) {
                                    ProductObj[] productObjs = new ProductObj[1];
                                    productObjs[0] = new ProductObj(response_catalog.getId(), response_catalog.getSingle_piece_price_range());
                                    productObjs[0].setImage(response_catalog.getImage());
                                    new ProductSingleShareApi(getActivity(), response_catalog.getId(), type, response_catalog);
                                }
                            }
                        });
                    }


                }
            });
        }
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
            refer_earn_promotion_img_1.setVisibility(View.VISIBLE);
            StaticFunctions.loadFresco(getActivity(), referral_banner_img, refer_earn_promotion_img_1);
        } else {
            refer_earn_promotion_img_1.setVisibility(View.GONE);
        }

        refer_earn_promotion_img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "refer_earn");
                param.put("from", "SingleProductDetail-In App Banner");
                new DeepLinkFunction(param, getActivity());
            }
        });

    }

    private void initDeliveryInfoListner() {

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

    private void checkStorePermission(Context context) {
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 1599);
        } else {
            new ProductSingleShareApi(getActivity(), response_catalog.getId(), StaticFunctions.SHARETYPE.GALLERY, response_catalog);
        }
    }

    @OnClick(R.id.btn_support_chat)
    public void clickSupportChat() {
        Application_Singleton.trackEvent("chatwbsupport", "chat", "productDetail");
        String msg = "";
        if (response_catalog != null && !response_catalog.getTitle().isEmpty())
            msg = "Product Enquiry Request:\n " + response_catalog.getTitle();
        new ChatCallUtils(getActivity(), ChatCallUtils.WB_CHAT_TYPE, msg);
    }

    @OnClick({R.id.fab_add_to_wishlist})
    public void addToWishlistClick() {
        if (response_catalog != null) {
            if (response_catalog.getIs_addedto_wishlist() == null) {
                callSaveWishlist(getActivity(), response_catalog.getId());
            } else {
                callRemoveWishlist(getActivity(), response_catalog.getIs_addedto_wishlist());
            }
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
                    ProductObj[] productObjs = new ProductObj[1];
                    productObjs[0] = new ProductObj(response_catalog.getId(), response_catalog.getSingle_piece_price_range());
                    productObjs[0].setImage(response_catalog.getImage());
                    downloadAndCopyDetails(getActivity(), productObjs, StaticFunctions.SHARETYPE.GALLERY, response_catalog.getCatalog_title());
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Write External Storage Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
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

    public void checkAlreadyAddedToCart() {
        btn_add_to_cart.setText("ADD TO CART");
        SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (!preferences.getString("cartdataProducts", "").equalsIgnoreCase("")) {
            Type cartType = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> saveCartData = new Gson().fromJson(preferences.getString("cartdataProducts", ""), cartType);
            if (saveCartData != null && saveCartData.size() > 0) {
                for (int j = 0; j < saveCartData.size(); j++) {
                    if (saveCartData.get(j).contains(response_catalog.getId())) {
                        btn_add_to_cart.setText("GO TO CART");
                        break;
                    }
                }
            } else {
                btn_add_to_cart.setText("ADD TO CART");
            }
        }
    }

    public void prePareAddToCartRequest(ArrayList<ProductObj> productObjs) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            // not start animation for android pie
            showProgress();
        } else {
            btn_add_to_cart.animate().rotationX(180).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        btn_add_to_cart.setText("GO TO CART");
                        btn_add_to_cart.setRotationX(0);
                        MenuItem v = (((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart));
                        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
                        v.getActionView().startAnimation(shake);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        CartHandler cartHandler = new CartHandler(((AppCompatActivity) getActivity()));
        if (productObjs != null && productObjs.size() > 0) {
            // for post
        } else {
            // For Without Size post
            productObjs = new ArrayList<>();
            ProductObj p_temp = new ProductObj(response_catalog.getId(),
                    response_catalog.getSingle_piece_price(), "Nan");
            p_temp.setCatalog_id(response_catalog.getCatalog_id());
            productObjs.add(p_temp);
        }
        cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
            @Override
            public void onSuccess(CartProductModel response) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    hideProgress();
                    Toast.makeText(getActivity(), "Product Added to cart", Toast.LENGTH_SHORT).show();
                }
                btn_add_to_cart.setText("GO TO CART");
                MenuItem v = (((OpenContainer) getActivity()).toolbar.getMenu().findItem(R.id.cart));
                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
                v.getActionView().startAnimation(shake);
                if (getActivity() instanceof OpenContainer) {
                    ((OpenContainer) getActivity()).updateBadge(Integer.parseInt(response.getTotal_cart_items()));
                }

            }

            @Override
            public void onFailure() {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    hideProgress();
                }
                btn_add_to_cart.clearAnimation();
                btn_add_to_cart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_add_to_cart.setText("ADD TO CART");
                    }
                }, 500);

            }
        });
        cartHandler.addMultipleProductToCartSinglePrice(productObjs);

    }

    void downloadAndCopyDetails(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype, String catalogname) {
        try {
            if (productObjs != null && productObjs.length > 0) {
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP && productObjs.length > 30) {
                    Fragment_WhatsAppSelection frag = new Fragment_WhatsAppSelection();
                    Application_Singleton.CONTAINER_TITLE = "WhatsApp (Pick 30 images to share)";
                    Bundle bundle = new Bundle();
                    bundle.putString("sharetext", "");
                    bundle.putString("type", "whatsapp");
                    frag.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = frag;
                    Intent intent = new Intent(context, OpenContainer.class);
                    intent.getIntExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                    startActivity(intent);
                    return;
                }
                try {
                    ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_SingleDetail.this,
                            productObjs, sharetype, "", catalogname, false,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (sharetype != null && sharetype != StaticFunctions.SHARETYPE.GALLERY)
                    copyProductDetail(context, true);
            } else {
                Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String copyProductDetail(Context context, boolean isCopy) {
        StringBuffer copy_details = new StringBuffer();
        String brand_name = "";
        if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null)
            brand_name = response_catalog.getBrand().getName();

        copy_details.append("\u26A1" + "Single piece " + response_catalog.getCategory_name() + " from " + brand_name + response_catalog.getCatalog_title() + " Collection" + "\u26A1" + "\n\n");

        ////////
        String id = "";
        id = "*Product ID*: " + (response_catalog.getId()) + "\n";

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
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_length() != null) {
            length.add("Dupatta Length: " + response_catalog.getEavdata().getDupatta_length());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_width() != null) {
            length.add("Dupatta Width: " + response_catalog.getEavdata().getDupatta_width());
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
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context, "Product description copied to clipboard", Toast.LENGTH_SHORT).show();
        }


        return copy_details.toString();

    }

    public void getMyDetails(Context context, String id) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            showProgress();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", id), null, headers, false, new HttpManager.customCallBack() {

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


                            setDetailUI();
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

    public void sendProductAttributes(Context context, Response_catalog response_catalog) {
        StaticFunctions.addProductViewCount(getActivity(), response_catalog.getId());
        if (!isSendAnalytics) {
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

    private void sendCatalogView() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        if (response_catalog.getView_permission() != null) {
            HashMap<String, String> params = new HashMap<String, String>();
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
                    Log.v("Done", "CatalogViewCount");
                }


                @Override
                public void onResponseFailed(ErrorString error) {
                    Log.e("TAG", "onResponseFailed: Error");
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
                        Log.v("sync response", response);
                        Log.v("Done", "Posting");

                    }


                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }
        }
    }

    public void disableActionButton() {
        // if user only manufacturer
        try {
            btn_add_to_cart.setVisibility(View.GONE);
            btn_share_product.setVisibility(View.GONE);
            linear_flow_button.setVisibility(View.GONE);
            if (btn_add_to_cart.getVisibility() == View.VISIBLE ||
                    btn_share_product.getVisibility() == View.VISIBLE) {
                linear_flow_button.setVisibility(View.VISIBLE);
            } else {
                linear_flow_button.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isContentIsValidHtml(String content) {
        Pattern pattern = Pattern.compile(HTML_TAG_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(content);
        return matcher.matches();
    }

    public class ProductPhotosPagerAdapter extends PagerAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<Photos> photos;

        public ProductPhotosPagerAdapter(Context context, ArrayList<Photos> photos) {
            this.context = context;
            this.photos = photos;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.product_detail_pager_item, container, false);
            SimpleDraweeView product_imageview = (SimpleDraweeView) itemView.findViewById(R.id.product_img);
            if (photos.get(position).getImage() != null && photos.get(position).getImage().getThumbnail_medium() != null)
                StaticFunctions.loadFresco(context, photos.get(position).getImage().getThumbnail_medium(), product_imageview);
            container.addView(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToPhotos(position);
                }
            });

            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int selectedProductPostion, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    public void navigateToPhotos(int position) {
        Intent photos_intent = new Intent(getActivity(), Activity_ProductPhotos.class);
        photos_intent.putExtra("photos", response_catalog.getPhotos());
        photos_intent.putExtra("position", position);
        startActivity(photos_intent);
    }

    public void getBundleExpandTrue(String bundle_id, String catalog_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getActivity());
        String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", bundle_id);
        HashMap<String, String> param = new HashMap<>();
        HttpManager.getInstance((Activity) getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        Response_catalog bundle_product_response = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                        linear_collection_other_images.setVisibility(View.VISIBLE);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View textView) {
                                Bundle bundle = new Bundle();
                                bundle.putString("from", "Single Detail");
                                bundle.putString("product_id", response_catalog.getBundle_product_id());
                                new NavigationUtils().navigateDetailPage(getActivity(), bundle);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(true);
                            }
                        };
                        SpannableString ss = null;
                        String s_link = response_catalog.getCatalog_title();
                        if (LocaleHelper.getLanguage(getActivity()).equalsIgnoreCase("hi")) {
                            ss = new SpannableString(response_catalog.getCatalog_title() + " " + getActivity().getResources().getString(R.string.view_other_collection_subtext));
                            ss.setSpan(clickableSpan, ss.toString().indexOf(s_link), ss.toString().indexOf(s_link) + s_link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            ss = new SpannableString(getActivity().getResources().getString(R.string.view_other_collection_subtext) + " " + response_catalog.getCatalog_title());
                            ss.setSpan(clickableSpan, ss.toString().indexOf(s_link), ss.toString().indexOf(s_link) + s_link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        txt_view_collection_title.setText(ss);
                        txt_view_collection_title.setMovementMethod(LinkMovementMethod.getInstance());
                        flex_collection_other_images.removeAllViews();
                        int show_max_thumb = bundle_product_response.getProduct().length;
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
                                    Bundle bundle = new Bundle();
                                    bundle.putString("product_id", bundle_product_response.getProduct()[finalI].getId());
                                    bundle.putString("from", "Public List");
                                    new NavigationUtils().navigateSingleProductDetailPage(getActivity(), bundle);
                                }
                            });
                            StaticFunctions.loadFresco(getActivity(), bundle_product_response.getProduct()[i].getImage().getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.product_img));
                            flex_collection_other_images.addView(view);
                        }
                        if (bundle_product_response.getProduct() != null && bundle_product_response.getProduct().length > 3) {
                            flex_collection_other_images.addView(txt_extra_images);
                            txt_extra_images.setVisibility(View.VISIBLE);
                            txt_extra_images.setText("+" + (bundle_product_response.getProduct().length - 3) + "\nDesigns");

                            txt_extra_images.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("view_type", "public");
                                    params.put("type", "product");
                                    params.put("ctype", "public");
                                    params.put("generic", "true");
                                    params.put("collection", "false");
                                    params.put("product_type", bundle_product_response.getProduct_type());
                                    params.put("page_title", bundle_product_response.getTitle());
                                    params.put("catalog_id", catalog_id);
                                    new DeepLinkFunction(params, getActivity());
                                }
                            });
                        } else {
                            txt_extra_images.setVisibility(View.GONE);
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

    public void openViewTaxDialog() {
        if (response_catalog != null) {
            DialogViewTaxShipping dialog = new DialogViewTaxShipping();
            Bundle bundle = new Bundle();
            bundle.putString("product_id", response_catalog.getId());
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "dialog");
        }
    }


    // ######################### WishList Operation Start ##################################
    public void callSaveWishlist(final Activity activity, String productId) {
        showProgress();
        Application_Singleton.trackEvent("Wishlist", "Save Wishlist", productId);
        String userid = UserInfo.getInstance(getActivity()).getUserId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HashMap<String, String> params = new HashMap<String, String>();
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
                    onRefresh();
                    int wishcount = UserInfo.getInstance(activity).getWishlistCount() + 1;
                    UserInfo.getInstance(activity).setWishlistCount(wishcount);
                    Toast.makeText(getActivity(), "Product successfully added to wishlist", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Product successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                    onRefresh();
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

    private void updateWishlist(boolean isAddedWishlist) {
        if (isAddedWishlist) {
            FloatingActionButton fab = getView().findViewById(R.id.fab_add_to_wishlist);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_fill_blue_24dp));
        } else {
            FloatingActionButton fab = getView().findViewById(R.id.fab_add_to_wishlist);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_blue_24dp));
        }
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

    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig() {
        // Hide Refer and Earn Banner
        // add catalog button hide from catalog level
        if (StaticFunctions.checkOrderDisableConfig(getActivity())) {
            btn_add_to_cart.setVisibility(View.GONE);
            refer_earn_promotion_img_1.setVisibility(View.GONE);
        }
    }


    // ########################## Start Send Wishbook Tracking Details #################################

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


}
