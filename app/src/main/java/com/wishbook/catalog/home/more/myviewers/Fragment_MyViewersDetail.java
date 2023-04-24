package com.wishbook.catalog.home.more.myviewers;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogViewers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.more.myviewers_2.ViewersAdapter;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MyViewersDetail extends GATrackedFragment implements Paginate.Callbacks, Fragment_CatalogsGallery.UpdateUIBecomeSellerListener {

    private View view;

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

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    HashMap<String, String> paramsClone = null;
    ArrayList<ResponseCatalogViewers> allViewers = new ArrayList<>();
    ViewersAdapter adapter;
    boolean isAllowCache  = true;

    public Fragment_MyViewersDetail() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_my_viewers_detail, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ViewersAdapter(getActivity(), allViewers);
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


        if (getArguments().getString("catalog_id") != null) {
            getCatalogDetails2(getArguments().getString("catalog_id"));
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            txt_total_viewers.setText(getArguments().getString("total_viewers") + " Viewers");
            getViewerDetail(params);
        }
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
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalog-viewers", getArguments().getString("catalog_id")), params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Loading = false;
                        final int offset = Integer.parseInt(params.get("offset"));
                        Type type = new TypeToken<ArrayList<ResponseCatalogViewers>>() {
                        }.getType();
                        ArrayList<ResponseCatalogViewers> temp = Application_Singleton.gson.fromJson(response, type);
                        if (temp.size() > 0) {
                            allViewers.addAll(temp);
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
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getCatalogDetails2(String catalogID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", catalogID), null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    Type type = new TypeToken<Response_catalog>() {
                    }.getType();
                    final Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, type);

                    if (response_catalog.getThumbnail() != null
                            && response_catalog.getThumbnail().getThumbnail_small() != null
                            && !response_catalog.getThumbnail().getThumbnail_small().isEmpty()) {
                        StaticFunctions.loadFresco(getActivity(), response_catalog.getThumbnail().getThumbnail_small(), catalog_img);
                    }

                    catalog_detail_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                            Bundle bundle = new Bundle();
                            bundle.putString("from","My Viewer Detail");
                            bundle.putString("product_id",response_catalog.getId());
                            new NavigationUtils().navigateDetailPage(getActivity(),bundle);

                        }
                    });

                    boolean brand_i_own = getArguments().getBoolean("brand_i_own");
                    if(brand_i_own && !response_catalog.getSupplier_details().isI_am_selling_this()){
                        btn_become_seller.setVisibility(View.VISIBLE);
                        txt_become_seller_label.setText(String.format(getResources().getString(R.string.my_viewers_detail_subtext_1), response_catalog.getBrand().getName()));
                        txt_become_seller_label.setVisibility(View.VISIBLE);
                    } else if(!brand_i_own && response_catalog.getSupplier_details().isI_am_selling_this() ) {
                        btn_become_seller.setVisibility(View.GONE);
                        txt_become_seller_label.setText(String.format(getResources().getString(R.string.my_viewers_detail_subtext_2)));
                        txt_become_seller_label.setVisibility(View.VISIBLE);
                    } else {
                        linear_total_viewers.setVisibility(View.GONE);
                    }

                    if(response_catalog.getBrand()!=null && response_catalog.getBrand().getName()!=null)
                         txt_brand_name.setText(response_catalog.getBrand().getName());
                    txt_total_value.setText(response_catalog.getTotal_products() + "Designs");
                    if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("true")) {
                        txt_full_catalog.setText("Only full catalog for sale");
                    } else {
                        txt_full_catalog.setText("Single piece available");
                    }

                    btn_become_seller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
                            fragment_catalogsGallery.setUpdateUIBecomeSellerListener(Fragment_MyViewersDetail.this);
                            fragment_catalogsGallery.getBrandPermission(response_catalog, false, getActivity());
                        }
                    });
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
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
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
        if (getArguments().getString("catalog_id") != null) {
            isAllowCache = false;
            getCatalogDetails2(getArguments().getString("catalog_id"));
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(LIMIT));
            params.put("offset", String.valueOf(INITIAL_OFFSET));
            txt_total_viewers.setText(getArguments().getString("total_viewers") + " Viewers");
            getViewerDetail(params);
        }
    }
}
