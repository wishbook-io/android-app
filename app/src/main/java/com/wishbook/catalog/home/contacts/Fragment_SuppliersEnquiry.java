package com.wishbook.catalog.home.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.EndlessRecyclerViewScrollListener;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.EnquirySuppliersAdapters;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.CommmonFilterOptionModel;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.contacts.add.Fragment_AddSupplier;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_SuppliersEnquiry extends GATrackedFragment implements
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private ArrayList<Response_Suppliers> approvedSuppliersList = new ArrayList<>();
    private ArrayList<Response_Suppliers> allApprovedSuppliersList = new ArrayList<>();
    private ArrayList<Response_Suppliers> allApprovedFilteredSuppliersList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    private SearchView searchView;
    private Response_Suppliers[] response_suppliers;
    private EnquirySuppliersAdapters mAdapter;
    private FloatingActionButton fabaddsupplier;
    private String searchText = "";
    private EndlessRecyclerViewScrollListener scrollListener;


    //Pagination Variable
    public static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page = 0;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    Fragment_ContactsHolder frag;

    Spinner spinner;
    ImageView search_icon, img_searchclose, ic_filter;
    LinearLayout linear_searchview;
    RelativeLayout relative_top_bar;
    LinearLayout linear_filter;
    private String filter;

    public Fragment_SuppliersEnquiry() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment_SuppliersEnquiry(String filter) {
        this.filter = filter;
    }

    String filteredStatus = "";
    public ArrayList<CommmonFilterOptionModel> filterDialogs = new ArrayList<CommmonFilterOptionModel>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_approved_suppliers, ga_container, true);
        approvedSuppliersList = new ArrayList<>();
        linear_filter = v.findViewById(R.id.filter);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fabaddsupplier = (FloatingActionButton) v.findViewById(R.id.fabaddsupplier);


        if(filter!=null){
            linear_filter.setVisibility(View.VISIBLE);
        } else {
            linear_filter.setVisibility(View.GONE);
        }
        initSearch();
        initTopBar();
        fabaddsupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_AddSupplier addBuyerFragment = new Fragment_AddSupplier();
                addBuyerFragment.setListener(new Fragment_AddSupplier.Listener() {
                    @Override
                    public void onDismiss() {
                        getSuppliersList(searchText);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");
            }
        });
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));

        ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter));
        ic_filter.setEnabled(true);


        //filter changes
        //filterOptions
        filterDialogs.add(new CommmonFilterOptionModel(1, "Approved", false, "approved"));
        filterDialogs.add(new CommmonFilterOptionModel(2, "Pending", false, "supplier_pending"));
        filterDialogs.add(new CommmonFilterOptionModel(3, "Rejected", false, "rejected"));
        filterDialogs.add(new CommmonFilterOptionModel(4, "Pending References", false, "Pending References"));


        //restting filter icon
        ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter));


        if (UserInfo.getInstance(getActivity()).isGuest()) {
            frag.fab_invite.setVisibility(View.GONE);
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
                StaticFunctions.registerClickListener(getActivity(), dialog, null);
            }
            return v;
        }

        mAdapter = new EnquirySuppliersAdapters((AppCompatActivity) getActivity(), allApprovedSuppliersList);
        recyclerView.setAdapter(mAdapter);
        initSwipeRefresh(v);
        getSuppliersList(searchText);
        return v;
    }


    private void getSuppliersList(final String search) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (!filteredStatus.equals("")) {
            params.put("supplier_status", filteredStatus);
        }
        if (!search.equals("")) {
            params.put("search", search);
        }

        if (filter != null) {
            if (filter.equals("new")) {
                params.put("status", "enquiry");
            } else if (filter.equals("old")) {
                params.put("status", "created_enquiry");
                //params.put("buyer_status", "approved,rejected");
            }
        }
        showProgress();
        allApprovedSuppliersList.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Log.v("sync response", response);
                        Response_Suppliers[] response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                        allApprovedSuppliersList.clear();
                        if (response_suppliers.length > 0) {
                            for (int i = 0; i < response_suppliers.length; i++) {
                                allApprovedSuppliersList.add(response_suppliers[i]);
                            }
                        }
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final Boolean[] canRun = {true};
        searchText = newText.toLowerCase();
        if (newText.length() >= 2) {

            if (canRun[0]) {
                canRun[0] = false;
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canRun[0] = true;
                        Log.d("call to", searchText);
                        getSuppliersList(searchText);
                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            getSuppliersList(searchText);
        }

        return false;
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
                searchText = "";
                searchView.setQuery("", false);
                searchView.clearFocus();
                getSuppliersList(searchText);
                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void initSearch() {
        searchView = (SearchView) v.findViewById(R.id.search_view);
        ic_filter = v.findViewById(R.id.ic_filter);
        ic_filter.setVisibility(View.GONE);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
    }

    public void initTopBar() {
        spinner = v.findViewById(R.id.spinner);
        search_icon = v.findViewById(R.id.search_icon);
        img_searchclose = v.findViewById(R.id.img_searchclose);
        linear_searchview = v.findViewById(R.id.linear_searchview);
        relative_top_bar = v.findViewById(R.id.relative_top_bar);


        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relative_top_bar.setVisibility(View.GONE);
                linear_searchview.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);

            }
        });
        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
            }
        });


        if (filter != null) {
            if(filter.equals("all"))
                spinner.setSelection(0);
            else if (filter.equals("new")) {
                spinner.setSelection(1);
            } else if (filter.equals("old")) {
                spinner.setSelection(2);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if( i == 0){
                    filter = "all";
                    getSuppliersList(searchText);
                } else if (i == 1) {
                    filter = "new";
                    getSuppliersList(searchText);
                } else if (i == 2) {
                    filter = "old";
                    getSuppliersList(searchText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ic_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContactsCommonFilterDialog commonFilterDialog = new ContactsCommonFilterDialog();
                commonFilterDialog.setList(filterDialogs);
                commonFilterDialog.setListener(new ContactsCommonFilterDialog.FilterDismissListener() {
                    @Override
                    public void selectedIDs(ArrayList<Integer> ids) {
                        filteredStatus = "";
                        if (ids != null && ids.size() > 0) {
                            ic_filter.setBackground(ContextCompat.getDrawable(getContext(), R.color.white));
                            ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_filled));
                            for (Integer id : ids) {
                                filteredStatus = (filteredStatus + "," + filterDialogs.get(id - 1).getRequest_filter_name());
                                filterDialogs.get(id - 1).setSelected(true);
                            }
                            if (filteredStatus.length() > 0) {
                                filteredStatus = filteredStatus.substring(1);
                            }
                            getSuppliersList(searchText);
                        } else {
                            ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                            ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter));
                            for (int i = 0; i < filterDialogs.size(); i++) {
                                filterDialogs.get(i).setSelected(false);
                            }
                            getSuppliersList(searchText);
                        }
                    }
                });
                commonFilterDialog.show(getChildFragmentManager(), "filter_dialog");
            }
        });

    }
}
