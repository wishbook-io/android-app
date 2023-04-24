package com.wishbook.catalog.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.home.adapters.CatalogsSharedAdapter;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.add.ActivityFilter;
import com.wishbook.catalog.home.catalog.details.SortBottomDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class Fragment_RecievedCatalogs extends GATrackedFragment implements
        Paginate.Callbacks,SortBottomDialog.SortBySelectListener, SwipeRefreshLayout.OnRefreshListener {
    int lastFirstVisiblePosition = 0;
    private View v;
    public RecyclerViewEmptySupport mRecyclerView;
    private HashMap<String, String> filterparams = new HashMap<>();
    private TextView filter_image;
    private CatalogsSharedAdapter catalogsSharedAdapter;
    CatalogMinified[] response_catalogs = new CatalogMinified[]{};
    ArrayList<CatalogMinified> respoCatalogMinifiedArrayList = new ArrayList<>();
    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    private String stock_filter = "";
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;

    private String filtertype = null;
    private String filtervalue = null;

    public int LIST_SIZE = 0;
    boolean isTrusted = false;
    private TextView txt_seller_near_me_subtext;
    private MaterialBadgeTextView badge_filter_count,badge_sort_count;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    private LinearLayout linear_sort, linear_search, linear_filter;

    public Fragment_RecievedCatalogs() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecyclerView != null) {
            lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
        Log.d("Position", String.valueOf(lastFirstVisiblePosition));
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
        inflater.inflate(R.layout.recentcatalogs, ga_container, true);

        if (UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
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
                StaticFunctions.registerClickListener(getActivity(), dialog,null);
            }
            return v;
        }


        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        filter_image = (TextView) v.findViewById(R.id.filter_image);
        linear_sort = v.findViewById(R.id.linear_sort);
        linear_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortBottom();
            }
        });

        /*
        Activity_Home.tabs.setVisibility(View.VISIBLE);
*/


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //final CarouselLayoutManager mLayoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL);

        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFilter();
            }
        });

        badge_filter_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_filter_count);
        badge_filter_count.setBadgeCount(0, true);

        badge_sort_count = (MaterialBadgeTextView) v.findViewById(R.id.badge_sort_count);
        badge_sort_count.setBadgeCount(0, true);




        mRecyclerView.setLayoutManager(mLayoutManager);

        // mLayoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));

        catalogsSharedAdapter = new CatalogsSharedAdapter((AppCompatActivity) getActivity(), respoCatalogMinifiedArrayList, "purchase", this);
        mRecyclerView.setAdapter(catalogsSharedAdapter);

        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        catalogsSharedAdapter.notifyDataSetChanged();

        initSwipeRefresh(v);
        initCall();
        CatalogHolder.toggleFloating(mRecyclerView,getActivity());

        return v;
    }

    public void initCall() {
        gettingFilterIfAny();
        if (Application_Singleton.deep_link_filter != null) {
            Log.i("TAG", "onCreateView: Deeep Link Filter Apply");
            if(getArguments()!=null){
                if(getArguments().getSerializable("deep_link_filter")!=null){
                    filterparams = (HashMap<String, String>) getArguments().getSerializable("deep_link_filter");
                }
            }

            if (filterparams != null) {
                if (filterparams.containsKey("trusted_seller")) {
                    if (filterparams.get("trusted_seller").equals("true")) {
                        updateFilter(true);
                        isTrusted = true;
                    }
                }
                respoCatalogMinifiedArrayList.clear();
                getReceivedCatalogs(filterparams, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);

            }
        } else if (isFilter) {
            getReceivedCatalogs(filterparams, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);
        } else {
            HashMap<String, String> params = null;
            getReceivedCatalogs(params, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);
        }


        // focus particular item
        if (getArguments() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(getArguments().getInt(Application_Singleton.adapterFocusPosition));
                }
            }, 200);

        }

    }

    private void gettingFilterIfAny() {
        if (getArguments() != null) {
            filtertype = getArguments().getString("filtertype", null);
            filtervalue = getArguments().getString("filtervalue", null);
        }
    }

    private void hideViews() {

        //  filter.animate().translationY(-filter.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        // filter.setVisibility(View.GONE);
        /*filter.animate().translationY(-filter.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                filter.setVisibility(View.GONE);
            }
        });*/
       /* Activity_Home.tabs.animate().translationY(Activity_Home.tabs.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Activity_Home.tabs.setVisibility(View.GONE);
            }
        });*/

     /*   TranslateAnimation animate = new TranslateAnimation(0,0,0,-filter.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        filter.startAnimation(animate);
        filter.setVisibility(View.GONE);

        TranslateAnimation animate1 = new TranslateAnimation(0,0,0,Activity_Home.tabs.getHeight());
        animate1.setDuration(500);
        animate1.setFillAfter(true);
        Activity_Home.tabs.startAnimation(animate1);
        Activity_Home.tabs.setVisibility(View.GONE);*/

       /* FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();


   */
    }

    private void showViews() {
        //    filter.setVisibility(View.VISIBLE);
        //   filter.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
       /* filter.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                filter.setVisibility(View.VISIBLE);
            }
        });*/
     /*   Activity_Home.tabs.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Activity_Home.tabs.setVisibility(View.VISIBLE);
            }
        });*/
    }

    private void toFilter() {
        /*Fragment_AddFilter addBuyergroupFragment = new Fragment_AddFilter();
        Bundle bundle = new Bundle();
        bundle.putBoolean("from_public", false);
        bundle.putBoolean("from_received",true);
        addBuyergroupFragment.setArguments(bundle);
        addBuyergroupFragment.setTargetFragment(Fragment_RecievedCatalogs.this, 1);
        addBuyergroupFragment.show(getActivity().getSupportFragmentManager(), "add");*/

        Intent intent = new Intent(getActivity(), ActivityFilter.class);
        intent.putExtra("from_public", false);
        intent.putExtra("from_received", true);
        if (filterparams != null) {
            intent.putExtra("previousParam", filterparams);
        }
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("parameters");
                    filterparams = null;
                    if (params != null) {
                        respoCatalogMinifiedArrayList.clear();
                        filterparams = params;
                        isFilter = true;
                        if (params.containsKey("trusted_seller")) {
                            isTrusted = true;
                            updateFilter(true);
                        } else {
                            isTrusted = false;
                            updateFilter(false);
                        }
                        getReceivedCatalogs(params, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);
                    }

                }
                break;
            //ResponseCode.Received_Catalogs
            case 9877:
                if (resultCode == Activity.RESULT_OK) {
                    getReceivedCatalogs(filterparams, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);
                }

        }
    }


    public void getReceivedCatalogs(HashMap<String, String> params, Context context, int limit, final int offset, Boolean progress, String stock_filter) {
        if (params == null) {
            params = new HashMap<>();
        }
        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                Log.d(getClass().getName(), "Visible");
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
                // showProgress();
            } else {
                Log.d(getClass().getName(), "Invisible");
                methodType = HttpManager.METHOD.GET;
            }
        } else {
            methodType = HttpManager.METHOD.GET;
        }
        String get_params = "";
        if (stock_filter.equals("")) {
            Log.i("TAG", "getReceivedCatalogs:  Null Para");
            params.put("limit", String.valueOf(limit));
            params.put("offset", String.valueOf(offset));
        } else if (stock_filter.equals("prebook")) {
            params.put("limit", String.valueOf(limit));
            params.put("offset", String.valueOf(offset));
            params.put("stock", "prebook");
        } else if (stock_filter.equals("instock")) {
            params.put("limit", String.valueOf(limit));
            params.put("offset", String.valueOf(offset));
            params.put("stock", "instock");
        } else if (stock_filter.equals("trusted")) {
            Log.i("TAG", "getReceivedCatalogs:  Null trusted Para");
            params.put("limit", String.valueOf(limit));
            params.put("offset", String.valueOf(offset));
            params.put("trusted_seller", "true");
        }

        if (offset == 0) {
            page = 0;
            hasLoadedAllItems = false;
            respoCatalogMinifiedArrayList.clear();
            if (catalogsSharedAdapter != null) {
                catalogsSharedAdapter.notifyDataSetChanged();
            }
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

            if (params.containsKey("ordering")) {
                filtercount = filtercount - 1;
            }

            if (params.containsKey("min_price") && params.containsKey("max_price")) {
                filtercount = filtercount - 1;
            }

            if(params.containsKey("ordering")){
                if(!params.get("ordering").equals("-id")){
                    badge_sort_count.setHighLightMode();
                } else {
                    badge_sort_count.setBadgeCount(0,true);
                }
            } else {
                badge_sort_count.setBadgeCount(0,true);
            }

            badge_filter_count.setBadgeCount(filtercount, true);
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        params.put("view_type", "myreceived");

        HttpManager.getInstance((Activity) context).request(methodType, URLConstants.companyUrl(getActivity(), "catalogs", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
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

                    Loading = false;

                    CatalogMinified[] response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified[].class);

                    if (isAdded() && !isDetached()) {
                        //  Debug.startMethodTracing("CACHE_ISSUE_SOLVE_FILE");
                        if (response_catalogs.length > 0) {
                            respoCatalogMinifiedArrayList = (ArrayList<CatalogMinified>) HttpManager.removeDuplicateIssue(offset, respoCatalogMinifiedArrayList, dataUpdated, LIMIT);

                            for (int i = 0; i < response_catalogs.length; i++) {
                                respoCatalogMinifiedArrayList.add(response_catalogs[i]);
                            }

                            page = (int) Math.ceil((double) respoCatalogMinifiedArrayList.size() / LIMIT);

                            if (response_catalogs.length % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }

                            if (respoCatalogMinifiedArrayList.size() <= LIMIT) {
                                catalogsSharedAdapter.notifyDataSetChanged();
                            } else {
                                try {
                                    catalogsSharedAdapter.notifyItemRangeChanged(offset, respoCatalogMinifiedArrayList.size() - 1);
                                } catch (Exception e) {
                                    catalogsSharedAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            hasLoadedAllItems = true;
                            catalogsSharedAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                if (getActivity() != null) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            }
        });
    }


    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    public ArrayList<CatalogMinified> filterPreBook(ArrayList<CatalogMinified> response) {
        ArrayList<CatalogMinified> res = new ArrayList();
        for (int i = 0; i < response.size(); i++) {
            if (response.get(i).getExp_desp_date() != null) {
                //Get Tommorrow Date
                Calendar calendar = Calendar.getInstance();
                Date tomorrow = calendar.getTime();


                //Format Date Arriving from Server
                String startDateString = response.get(i).getExp_desp_date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date startDate = null;
                try {
                    startDate = df.parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Check If date is greater than Tommorow
                if (startDate.after(tomorrow)) {
                    // add to the new filtered list
                    res.add(response.get(i));
                }


            }
        }


        return res;
    }

    public ArrayList<CatalogMinified> filterInStock(ArrayList<CatalogMinified> response) {
        ArrayList<CatalogMinified> res = new ArrayList();

        for (int i = 0; i < response.size(); i++) {
            if (response.get(i).getExp_desp_date() != null) {
                //Get Tommorrow Date
                Calendar calendar = Calendar.getInstance();
                Date tomorrow = calendar.getTime();


                //Format Date Arriving from Server
                String startDateString = response.get(i).getExp_desp_date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date startDate = null;
                try {
                    startDate = df.parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Check If date is greater than Tommorow
                if (startDate.after(tomorrow)) {
                    // add to the new filtered list
                } else {
                    res.add(response.get(i));
                }


            } else {
                res.add(response.get(i));
            }
        }


        return res;
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            getReceivedCatalogs(filterparams, getActivity(), LIMIT, page * LIMIT, false, stock_filter);
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

    private void updateFilter(boolean isFilter) {
       /* if (isFilter) {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less_padding_blue_fill));
            trusted_filter.setTextColor(Color.WHITE);
        } else {
            trusted_filter.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_edge_less));
            trusted_filter.setTextColor(getResources().getColor(R.color.color_primary));
        }*/
    }

    private void openSortBottom() {
        SortBottomDialog sortBottomDialog = null;
        if (filterparams != null) {
            if (filterparams.containsKey("ordering")) {
                sortBottomDialog = sortBottomDialog.newInstance(filterparams.get("ordering"));
            } else {
                sortBottomDialog = SortBottomDialog.newInstance(null);
            }
        } else {
            sortBottomDialog = SortBottomDialog.newInstance(null);
        }
        sortBottomDialog.show(getFragmentManager(), sortBottomDialog.getTag());
        sortBottomDialog.setSortBySelectListener(Fragment_RecievedCatalogs.this);
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
    public void onCheck(String check) {
        if (check != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("ordering", check);
            if (filterparams != null) {
                filterparams.putAll(params);
            } else {
                params.put("view_type", "public");
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(INITIAL_OFFSET));
                filterparams = params;
            }
            isFilter = true;
            getReceivedCatalogs(params, getActivity(), LIMIT, INITIAL_OFFSET, true, stock_filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

}