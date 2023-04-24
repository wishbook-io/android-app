package com.wishbook.catalog.home.more.myviewers_2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogViewers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_MyViewersDetail extends AppCompatActivity implements Paginate.Callbacks, Fragment_CatalogsGallery.UpdateUIBecomeSellerListener, AppBarLayout.OnOffsetChangedListener {

    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    @BindView(R.id.catalog_detail_container)
    LinearLayout catalog_detail_container;
    @BindView(R.id.catalog_img)
    SimpleDraweeView catalog_img;
    @BindView(R.id.txt_brand_name)
    TextView txt_brand_name;
    @BindView(R.id.txt_uploaded_value)
    TextView txt_uploaded_value;
    @BindView(R.id.txt_total_value)
    TextView txt_total_value;
    @BindView(R.id.txt_price_value)
    TextView txt_price_value;
    @BindView(R.id.txt_full_catalog)
    TextView txt_full_catalog;
    @BindView(R.id.txt_total_viewers)
    TextView txt_total_viewers;
    @BindView(R.id.btn_become_seller)
    AppCompatButton btn_become_seller;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.txt_become_seller_label)
    TextView txt_become_seller_label;
    @BindView(R.id.linear_total_viewers)
    LinearLayout linear_total_viewers;
    @BindView(R.id.toolbar)
    androidx.appcompat.widget.Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar_img)
    SimpleDraweeView toolbar_img;
    @BindView(R.id.toolbar_back)
    ImageView toolbar_back;
    @BindView(R.id.back_button)
    ImageView back_button;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.catalog_name)
    TextView catalog_name;
    //Pagination Variable
    Handler handler;
    Paginate paginate;
    int page;
    boolean brand_i_own;
    long CONTSANT_TIME = 5000;
    HashMap<String, String> paramsClone = null;
    ArrayList<ResponseCatalogViewers> allViewers = new ArrayList<>();
    ViewersAdapter adapter;
    boolean isAllowCache = true;
    Response_catalog response_catalog;
    private View view;
    private int mMaxScrollSize;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_MyViewersDetail.this;
        setContentView(R.layout.fragment_my_viewers_detail);
        ButterKnife.bind(this);
        initView();

        WishbookTracker.sendScreenEvents(mContext, WishbookEvent.SELLER_EVENTS_CATEGORY, "MyViewerDetails_page", "My Viewers List", null);
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_MyViewersDetail.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ViewersAdapter(Activity_MyViewersDetail.this, allViewers);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        if (getIntent().getStringExtra("catalog_id") != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            // txt_total_viewers.setText(getIntent().getStringExtra("total_viewers") + " Viewers");
            getViewerDetail(params);
        }

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appbar.addOnOffsetChangedListener(this);


        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
    }

    public void goBack(View v) {
        this.finish();
    }

    private void getViewerDetail(final HashMap<String, String> params) {
        try {
            if (params.containsKey("offset")) {
                if (params.get("offset").equals("0")) {
                    page = 0;
                    allViewers.clear();
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    hasLoadedAllItems = false;

                }
            } else {
                params.put("offset", "0");
                page = 0;
                allViewers.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                hasLoadedAllItems = false;
            }
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_MyViewersDetail.this);
            HttpManager.getInstance(Activity_MyViewersDetail.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_MyViewersDetail.this, "catalog-viewers", getIntent().getStringExtra("catalog_id")), params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (!isFinishing()) {
                            Loading = false;
                            final int offset = Integer.parseInt(params.get("offset"));
                            Type type = new TypeToken<ArrayList<ResponseCatalogViewers>>() {
                            }.getType();
                            ArrayList<ResponseCatalogViewers> temp = Application_Singleton.gson.fromJson(response, type);
                            if (temp.size() > 0) {
                                String c_name = getIntent().getStringExtra("catalog_name");

                                catalog_name.setText(c_name);
                                allViewers.addAll(temp);
                                brand_i_own = Boolean.parseBoolean(temp.get(0).getBrand_i_own());
                                getCatalogDetails2(getIntent().getStringExtra("product_id"));
                                page = (int) Math.ceil((double) allViewers.size() / LIMIT);
                                if (temp.size() % LIMIT != 0) {
                                    hasLoadedAllItems = true;
                                }
                                if (allViewers.size() <= LIMIT) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    try {
                                        mRecyclerView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyItemRangeChanged(offset, allViewers.size() - 1);
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

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getCatalogDetails2(String catalogID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_MyViewersDetail.this);
        HttpManager.getInstance(Activity_MyViewersDetail.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_MyViewersDetail.this, "catalogs_expand_true_id", catalogID), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (!isFinishing()) {
                        Type type = new TypeToken<Response_catalog>() {
                        }.getType();
                        response_catalog = Application_Singleton.gson.fromJson(response, type);

                        if (response_catalog.getImage() != null
                                && response_catalog.getImage().getThumbnail_small() != null
                                && !response_catalog.getImage().getThumbnail_small().isEmpty()) {
                            StaticFunctions.loadFresco(Activity_MyViewersDetail.this, response_catalog.getImage().getThumbnail_small(), catalog_img);
                        }
                        if (response_catalog != null) {
                            if (brand_i_own && !response_catalog.getSupplier_details().isI_am_selling_this()) {
                                if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_CAT)) {
                                    btn_become_seller.setVisibility(View.VISIBLE);
                                }
                                txt_become_seller_label.setText(String.format(getResources().getString(R.string.my_viewers_detail_subtext_1), response_catalog.getBrand()!=null ? response_catalog.getBrand().getName() : ""));
                                txt_become_seller_label.setVisibility(View.VISIBLE);
                            } else if (!brand_i_own && response_catalog.getSupplier_details().isI_am_selling_this()) {
                                btn_become_seller.setVisibility(View.GONE);
                                txt_become_seller_label.setText(String.format(getResources().getString(R.string.my_viewers_detail_subtext_2)));
                                txt_become_seller_label.setVisibility(View.VISIBLE);
                            } else {
                                linear_total_viewers.setVisibility(View.GONE);
                                btn_become_seller.setVisibility(View.GONE);
                                txt_become_seller_label.setVisibility(View.GONE);
                            }
                        }
                        catalog_detail_container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("from","My Viewer Detail");
                                bundle.putString("product_id",response_catalog.getId());
                                new NavigationUtils().navigateDetailPage(mContext,bundle);
                            }
                        });


                        if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null) {
                            txt_brand_name.setText(response_catalog.getBrand().getName());
                        }
                        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                            txt_total_value.setText(response_catalog.getNo_of_pcs_per_design() + "Pieces");
                        } else {
                            txt_total_value.setText(response_catalog.getTotal_products() + "Designs");
                        }

                        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                            if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("true")) {
                                txt_full_catalog.setText("Full Set");
                            } else {
                                txt_full_catalog.setText("Single piece available");
                            }
                        } else {
                            if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("true")) {
                                txt_full_catalog.setText("Only full catalog for sale");
                            } else {
                                txt_full_catalog.setText("Single piece available");
                            }
                        }

                        if (response_catalog.getBrand() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_CAT)) {
                            btn_become_seller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
                                    fragment_catalogsGallery.setUpdateUIBecomeSellerListener(Activity_MyViewersDetail.this);
                                    fragment_catalogsGallery.getBrandPermission(response_catalog, false, Activity_MyViewersDetail.this);
                                }
                            });
                        }

                        try {
                            Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(response_catalog.getCreated_at());
                            Date today = new Date();
                            int days = (int) ((today.getTime() - temp.getTime()) / (24 * 60 * 60 * 1000));
                            int weeks = days / 7;
                            if (days <= 45) {
                                // show days
                                if (days == 0) {
                                    txt_uploaded_value.setText("Today");
                                } else {
                                    txt_uploaded_value.setText(days + " days ago");
                                }
                            } else {
                                // show week
                                txt_uploaded_value.setText(weeks + " weeks ago");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String price_range = response_catalog.getPrice_range();
                        if (price_range != null) {
                            txt_price_value.setVisibility(View.VISIBLE);
                            if (price_range.contains("-")) {
                                String[] priceRangeMultiple = price_range.split("-");
                                txt_price_value.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                            } else {
                                String priceRangeSingle = price_range;
                                txt_price_value.setText("\u20B9" + priceRangeSingle + "/Pc."/*+ ", " + response_catalogMini.getTotal_products() + " Designs"*/);
                            }
                        } else {
                            txt_price_value.setVisibility(View.GONE);
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


    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(page * LIMIT));
                getViewerDetail(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                getViewerDetail(paramsClone);
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
    public void upDateBecomeSellerUI() {
        if (getIntent().getStringExtra("catalog_id") != null) {
            isAllowCache = false;
            getCatalogDetails2(getIntent().getStringExtra("product_id"));
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            //   txt_total_viewers.setText(getIntent().getStringExtra("total_viewers") + " Viewers");
            getViewerDetail(params);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        try {

            if (Math.abs(offset) == appBarLayout.getTotalScrollRange()) {
                toolbar.setVisibility(View.VISIBLE);
                String catalog_name = getIntent().getStringExtra("catalog_name");
                toolbar.setTitle(catalog_name);
                toolbar.setTitleTextColor(getResources().getColor(R.color.color_primary));
                toolbar_img.setVisibility(View.VISIBLE);
                StaticFunctions.loadFresco(Activity_MyViewersDetail.this, response_catalog.getImage().getThumbnail_small(), toolbar_img);
                setSupportActionBar(toolbar);
                toolbar_back.setVisibility(View.VISIBLE);
                collapsing_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.color_primary));
            } else if (offset == 0) {
                toolbar.setTitle("");
                toolbar_img.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                toolbar_back.setVisibility(View.GONE);
                collapsing_toolbar.setTitle("");
            } else {
                toolbar.setTitle("");
                toolbar_img.setVisibility(View.GONE);
                toolbar_back.setVisibility(View.GONE);
                collapsing_toolbar.setTitle("");
                toolbar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void requestNewdata() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewRecentData();
            }
        }, CONTSANT_TIME);
    }

    private void getNewRecentData() {
        try {
            if (!isFinishing()) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_MyViewersDetail.this);

                HttpManager.getInstance(Activity_MyViewersDetail.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_MyViewersDetail.this, "catalog-viewers", getIntent().getStringExtra("catalog_id")) + "?seconds=5", null, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Log.d("RESPONSEe", response);

                            if (!isFinishing()) {
                                Type type = new TypeToken<ArrayList<ResponseCatalogViewers>>() {
                                }.getType();
                                ArrayList<ResponseCatalogViewers> temp = Application_Singleton.gson.fromJson(response, type);
                                if (temp.size() > 0) {
                                    for (int i = 0; i < temp.size(); i++) {
                                        allViewers.add(i, temp.get(i));
                                        adapter.notifyItemInserted(i);
                                        mRecyclerView.scrollToPosition(0);

                                    }
                                    adapter.notifyDataSetChanged();

                                }
                                requestNewdata();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {
        try {
            handler.removeCallbacks(null);
            toolbar_img.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Fragment_CatalogsGallery.BECOME_SELLER_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(Activity_MyViewersDetail.this, "You are succesfully seller of this catalog", Toast.LENGTH_LONG).show();
            successDialog(false, Activity_MyViewersDetail.this, response_catalog);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //-requestNewdata();
        // ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);


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
                CatalogMinified selectedCatalog = new CatalogMinified(response_catalog.getId(), "catalog", response_catalog.isBuyer_disabled(), response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getView_permission());
                selectedCatalog.setIs_owner(response_catalog.is_owner());
                Application_Singleton.selectedshareCatalog = selectedCatalog;
                Application_Singleton.CONTAINER_TITLE = response_catalog.getTitle();
                Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                Intent intent = new Intent(context, OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.CATALOGAPP);
                context.startActivity(intent);
                context.finish();
            }

        });
    }
}
