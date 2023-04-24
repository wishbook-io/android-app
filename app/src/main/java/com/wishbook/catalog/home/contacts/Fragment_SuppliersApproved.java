package com.wishbook.catalog.home.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.EndlessRecyclerViewScrollListener;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.ApprovedSuppliersAdapters;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.contacts.add.Fragment_AddSupplier;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_SuppliersApproved extends GATrackedFragment implements
        SearchView.OnQueryTextListener, Paginate.Callbacks , SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private ArrayList<Response_Suppliers> approvedSuppliersList = new ArrayList<>();
    private ArrayList<Response_Suppliers> allApprovedSuppliersList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    private SearchView local_searchview;
    private Response_Suppliers[] response_suppliers;
    private ApprovedSuppliersAdapters mAdapter;
    private FloatingActionButton fabaddsupplier;
    private String searchText = "";
    private EndlessRecyclerViewScrollListener scrollListener;

    Fragment_AddSupplier addBuyerFragment;
    //Pagination Variable
    private static int LIMIT = 10;
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
    TextView list_empty1;

    public Fragment_SuppliersApproved() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_approved_suppliers, ga_container, true);




        approvedSuppliersList = new ArrayList<>();

        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fabaddsupplier = (FloatingActionButton) v.findViewById(R.id.fabaddsupplier);
        local_searchview = (SearchView) v.findViewById(R.id.group_search);
        local_searchview.setIconifiedByDefault(false);
        list_empty1 = v.findViewById(R.id.list_empty1);
        list_empty1.setText(getResources().getString(R.string.empty_text_network));
        fabaddsupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBuyerFragment = new Fragment_AddSupplier();
                addBuyerFragment.setListener(new Fragment_AddSupplier.Listener() {
                    @Override
                    public void onDismiss() {
                        getSuppliersList(LIMIT, INITIAL_OFFSET, true, searchText);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");
            }
        });
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));

        //getting Parent searchview
       frag= ((Fragment_ContactsHolder) this.getParentFragment());
        frag.searchView.setOnQueryTextListener(this);
        //local_searchview.setOnQueryTextListener(this);

        //light the filter option as no use in buyers approved
        frag.ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_light));
        frag.ic_filter.setEnabled(false);
        /* scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public Boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    getSuppliersList(LIMIT, totalItemsCount, false, searchText);
       //         Toast.makeText(getActivity(),totalItemsCount+"",Toast.LENGTH_LONG).show();

                return  true;
            }

            @Override
            public void stopLoading() {

            }
        };
        recyclerView.addOnScrollListener(scrollListener);*/
        //Show Invite Button
        frag.fab_invite.setVisibility(View.VISIBLE);
        frag.btn_invite.setVisibility(View.GONE);
        frag.fab_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuyerFragment = new Fragment_AddSupplier();
                addBuyerFragment.setListener(new Fragment_AddSupplier.Listener() {
                    @Override
                    public void onDismiss() {

                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addsupplier");

                Application_Singleton.trackEvent("Approved Supplier", "Click", "Floating Plus Button");
            }
        });

        if(UserInfo.getInstance(getActivity()).isGuest()) {
            frag.fab_invite.setVisibility(View.GONE);
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            frag.linear_contact_filter_bar.setVisibility(View.GONE);
            if(!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text),UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(),dialog);
            } else {
                View dialog = v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog,null);
            }
            return v;
        }

        //Network Tutorial
      /*  if(!Application_Singleton.tutorial_pref.getBoolean("network_tutorial",false)&& !UserInfo.getInstance(getContext()).getGroupstatus().equals("2")){
            Intent intent = new Intent(getActivity(), IntroActivity.class);
            ArrayList<AppIntroModel> models = new ArrayList<>();
            models.add(new AppIntroModel(getString(R.string.intro_network_title),getString(R.string.intro_network_desc),R.drawable.intro_network_new));
            intent.putParcelableArrayListExtra("list",models);
            startActivity(intent);
            Application_Singleton.tutorial_pref.edit().putBoolean("network_tutorial",true).apply();
        }*/


        mAdapter = new ApprovedSuppliersAdapters(this, allApprovedSuppliersList);
        recyclerView.setAdapter(mAdapter);
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        mAdapter.notifyDataSetChanged();

        initSwipeRefresh(v);
        getSuppliersList(LIMIT, INITIAL_OFFSET, true, searchText);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("onresume", "approvedSuppliers");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "approvedSuppliers");
    }

    private void getSuppliersList(int limit, final int offset, Boolean progress, final String search) {

        HttpManager.METHOD methodType;
        if (progress) {
            methodType = HttpManager.METHOD.GETWITHPROGRESS;
           // showProgress();
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (offset == 0) {
            page = 0;
            hasLoadedAllItems = false;
            allApprovedSuppliersList.clear();
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(methodType, URLConstants.companyUrl(getActivity(), "sellers_approved", "") + "&&limit=" + limit + "&&offset=" + offset + "&&search=" + search, null, headers, isAllowCache, new HttpManager.customCallBack() {
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
                    if (isAdded() && !isDetached()) {
                        Response_Suppliers[] response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                        Loading = false;

                        if (response_suppliers.length > 0) {


                            //checking if data updated on 2nd page or more
                            allApprovedSuppliersList = (ArrayList<Response_Suppliers>) HttpManager.removeDuplicateIssue(offset, allApprovedSuppliersList, dataUpdated, LIMIT);


                            for (int i = 0; i < response_suppliers.length; i++) {
                                allApprovedSuppliersList.add(response_suppliers[i]);
                            }


                            page = (int) Math.ceil((double) allApprovedSuppliersList.size() / LIMIT);

                            if (response_suppliers.length % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }

                            if (allApprovedSuppliersList.size() <= LIMIT) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                try {
                                    mAdapter.notifyItemRangeChanged(offset, allApprovedSuppliersList.size() - 1);
                                } catch (Exception e) {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            hasLoadedAllItems = true;
                            mAdapter.notifyDataSetChanged();
                        }
                        Log.v("APPROVED_BUYERS1", "" + response_suppliers.toString());
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final Boolean[] canRun = {true};
        searchText = newText.toLowerCase();
        if (newText.length() > 2) {

            if (canRun[0]) {
                canRun[0] = false;
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        canRun[0] = true;
                        Log.d("call to", searchText);
                        if (getUserVisibleHint() && getContext() != null){
                            list_empty1.setText("No Item to display");
                            getSuppliersList(LIMIT, INITIAL_OFFSET, false, searchText);
                        }

                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            list_empty1.setText(getResources().getString(R.string.empty_text_network));
            if (getUserVisibleHint() && getContext() != null)
                getSuppliersList(LIMIT, INITIAL_OFFSET, false, searchText);
        }
        /*if(response_suppliers!=null) {
            if (response_suppliers.length > 0) { // Check if the Original List and Constraint aren't null.

                approvedSuppliersList.clear();
                for (int i = 0; i < response_suppliers.length; i++) {
                    try {
                        if (response_suppliers[i].getInvitee() != null) {
                            if (response_suppliers[i].getSelling_company_name().toLowerCase().toString().contains(newText.toLowerCase()) || response_suppliers[i].getSelling_company_phone_number().toString().contains(newText)) {
                                approvedSuppliersList.add(response_suppliers[i]);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
               // Response_Suppliers[] supplierses = approvedSuppliersList.toArray(new Response_Suppliers[approvedSuppliersList.size()]);
                mAdapter = new ApprovedSuppliersAdapters((AppCompatActivity) getActivity(), approvedSuppliersList);
                recyclerView.setAdapter(mAdapter);
            }
        }*/

        return false;
    }

    @Override
    public void onLoadMore() {
        if (page > 0) {
            getSuppliersList(LIMIT, page * LIMIT, false, searchText);
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
    public void onPause() {
        super.onPause();
        if(addBuyerFragment!=null){
            addBuyerFragment.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode== ResponseCodes.Supplier_Approved){
            getSuppliersList(LIMIT, INITIAL_OFFSET, true, "");
        }
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
                searchText ="";
                if(frag!=null) {
                    frag.searchView.setQuery("",false);
                    frag.searchView.clearFocus();
                }
                getSuppliersList(LIMIT, INITIAL_OFFSET, true, searchText);
                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

}
