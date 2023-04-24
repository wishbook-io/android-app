package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;

import java.util.ArrayList;
import java.util.HashMap;


public class Fragment_BrandFollowedCatalogs extends GATrackedFragment implements Paginate.Callbacks {

    private View v;
    private BrowseCatalogsAdapter adapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private TextView filter_image;
    private AppCompatButton trusted_filter;
    AppCompatButton nearme_filter;
    private ProgressDialog progressDialog;
    HashMap<String, String> paramsClone = null;
    ArrayList<Response_catalogMini> allCatalogs = new ArrayList<>();
    ArrayList<Response_catalogMini> allCatalogsFiltered = new ArrayList<>();
    int lastFirstVisiblePosition = 0;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;
    private boolean isTrusted;

    private String filtertype = null;
    private String filtervalue = null;

    private TextView txt_seller_near_me_subtext;
    private MaterialBadgeTextView badge_filter_count, badge_sort_count;
    private RelativeLayout relative_filter;

    public Fragment_BrandFollowedCatalogs() {
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
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        filter_image = (TextView) v.findViewById(R.id.filter_image);
        relative_filter = v.findViewById(R.id.relative_filter);
        trusted_filter = (AppCompatButton) v.findViewById(R.id.trusted_filter);
        badge_filter_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_filter_count);
        badge_filter_count.setBadgeCount(0, true);

        badge_sort_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_sort_count);
        badge_sort_count.setBadgeCount(0, true);


        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFilter();
            }
        });

        nearme_filter = v.findViewById(R.id.nearme_filter);
        nearme_filter.setVisibility(View.GONE);

        try {
            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                nearme_filter.setVisibility(View.GONE);
            } else {
                nearme_filter.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        relative_filter.setVisibility(View.GONE);
        trusted_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTrusted) {
                    isTrusted = true;
                    updateFilter(true);
                    //trusted_filter.setTextColor(Color.parseColor("#ECC90B"));
                    HashMap<String, String> params = new HashMap<>();
                    if (paramsClone != null) {
                        params = paramsClone;
                        params.put("trusted_seller", "true");
                    } else {
                        params.put("trusted_seller", "true");
                        params.put("follow_brand", "true");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(INITIAL_OFFSET));
                    }
                    showCatalogs(params, true);
                } else {
                    isTrusted = false;
                    updateFilter(false);
                    HashMap<String, String> params = new HashMap<>();
                    if (paramsClone != null) {
                        params = paramsClone;
                    } else {
                        params.put("follow_brand", "true");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(INITIAL_OFFSET));
                    }
                    showCatalogs(params, true);
                }

            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new BrowseCatalogsAdapter((AppCompatActivity) getActivity(), allCatalogs);
        mRecyclerView.setAdapter(adapter);


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

        gettingFilterIfAny();

        HashMap<String, String> params = new HashMap<>();
        params.put("follow_brand", "true");
        params.put("limit", String.valueOf(LIMIT));
        params.put("offset", String.valueOf(INITIAL_OFFSET));
        showCatalogs(params, true);

        // focus particular item
        if (getArguments() != null) {
            lastFirstVisiblePosition = getArguments().getInt(Application_Singleton.adapterFocusPosition);
        }




        return v;
    }

    private void toFilter() {

        Intent intent = new Intent(getActivity(), ActivityFilter.class);
        if (paramsClone != null) {
            intent.putExtra("previousParam", paramsClone);
        }
        intent.putExtra("from_followed_brand", true);
        startActivityForResult(intent, 1);
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
    }

    private void showCatalogs(HashMap<String, String> params, Boolean progress) {


        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                Log.d(getClass().getName(), "Visible");
                // showProgress();
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
            } else {
                Log.d(getClass().getName(), "Invisible");
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

        //adding supplier company filter if needed
        if (filtertype != null && filtervalue != null) {
            params.put(filtertype, filtervalue);
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

            badge_filter_count.setBadgeCount(filtercount, true);
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        final HashMap<String, String> finalParams = params;
        HttpManager.getInstance(getActivity()).request(methodType, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                int offset = Integer.parseInt(finalParams.get("offset"));
                Log.v("sync response", response);
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

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                Log.i("TAG", "onActivityResult: Called Brand Followed");
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                    if (params != null) {
                        if (paramsClone != null) {
                            paramsClone.clear();
                        }
                        if (isTrusted) {
                            params.put("trusted_seller", "true");
                        }
                        params.put("follow_brand", "true");
                        params.put("limit", String.valueOf(LIMIT));
                        params.put("offset", String.valueOf(INITIAL_OFFSET));
                        paramsClone = params;
                        showCatalogs(params, true);
                    }

                }
                break;
        }
    }

    @Override
    public void onLoadMore() {
        Loading = true;
         /* int tempOffset;
        if(page==0){
            tempOffset=INITIAL_OFFSET;
        }else{
            tempOffset=page*LIMIT+1;
        }*/
        if (page > 0) {
            if (paramsClone == null) {
                HashMap<String, String> params = new HashMap<>();
                if (isTrusted) {
                    params.put("trusted_seller", "true");
                }
                params.put("follow_brand", "true");
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(page * LIMIT));
                showCatalogs(params, false);

            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                showCatalogs(paramsClone, false);
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

    private void updateFilter(boolean isFilter) {
        if (isFilter) {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            trusted_filter.setTextColor(Color.WHITE);
        } else {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            trusted_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }
    }
}
