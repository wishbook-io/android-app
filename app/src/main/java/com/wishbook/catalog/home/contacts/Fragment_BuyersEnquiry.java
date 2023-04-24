package com.wishbook.catalog.home.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.wishbook.catalog.commonadapters.EnquiryBuyerAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.CommmonFilterOptionModel;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.home.contacts.add.Fragment_AddBuyer;
import com.wishbook.catalog.home.models.BuyersList;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_BuyersEnquiry extends GATrackedFragment implements
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {


    private View v;
    private RecyclerViewEmptySupport recyclerView;
    private SearchView searchView;
    private EnquiryBuyerAdapter mAdapter;
    private FloatingActionButton fabaddbuyer;

    private ArrayList<Response_Buyer> response_buyers = new ArrayList<>();
    private ArrayList<Response_Buyer> response_buyersFilteredList = new ArrayList<>();
    private Toolbar toolbar;
    private String searchText = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    Response_BuyerGroupType[] responseBuyerGroupTypes;

    //Pagination Variable
    public static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;


    //filter changes
    //for filter
    String filteredStatus = "";
    public ArrayList<CommmonFilterOptionModel> filterDialogs = new ArrayList<CommmonFilterOptionModel>();

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

    private String filter;
    private LinearLayout linear_filter;

    public Fragment_BuyersEnquiry() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment_BuyersEnquiry(String filter) {
        this.filter = filter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_buyers_approved, ga_container, true);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fabaddbuyer = (FloatingActionButton) v.findViewById(R.id.fabaddbuyer);
        linear_filter = v.findViewById(R.id.filter);
        if (filter != null) {
            linear_filter.setVisibility(View.VISIBLE);
        } else {
            linear_filter.setVisibility(View.GONE);
        }

        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        fabaddbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_AddBuyer addBuyerFragment = new Fragment_AddBuyer();
                addBuyerFragment.setListener(new Fragment_AddBuyer.Listener() {
                    @Override
                    public void onDismiss() {
                        getBuyersList(searchText);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");
            }
        });
        if (paginate != null) {
            paginate.unbind();
        }

        initSearch();
        initTopBar();
        getBuyerGroup();
        mAdapter = new EnquiryBuyerAdapter((AppCompatActivity) getActivity(), response_buyers, responseBuyerGroupTypes, this);
        recyclerView.setAdapter(mAdapter);

        //filter changes
        //filterOptions


        ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter));


        if (UserInfo.getInstance(getActivity()).isGuest()) {
            // frag.fab_invite.setVisibility(View.GONE);
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            if (!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text), UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(), dialog);
            } else {
                View dialog = v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(), dialog,null);
            }
            return v;
        }
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);
        toolbar.setTitle("Buyers");
        initSwipeRefresh(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.enquiry_received_filter, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO do sth here on dismiss
            }
        });

        popupWindow.showAsDropDown(v);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "approved buyers");
    }


    public void getBuyerGroup() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
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
                    responseBuyerGroupTypes = Application_Singleton.gson.fromJson(response, Response_BuyerGroupType[].class);
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

    private void getBuyersList(final String search) {
        showProgress();
        HashMap<String, String> params = new HashMap<String, String>();
        if (!filteredStatus.equals("")) {
            params.put("buyer_status", filteredStatus);
        }
        if (!search.equals("")) {
            params.put("search", search);
        }
        if (filter != null) {
            if (filter.equals("new")) {
                params.put("status", "enquiry");
            } else if (filter.equals("old")) {
                params.put("status", "created_enquiry");
                // params.put("buyer_status", "approved,rejected");
            }
        }
        response_buyers.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", ""), params, headers, isAllowCache, new HttpManager.customCallBack() {
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
                    Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                    Log.d("Got result for ", searchText);
                    response_buyers.clear();
                    if (response_buyer.length > 0) {
                        for (int i = 0; i < response_buyer.length; i++) {
                            response_buyers.add(response_buyer[i]);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    Log.v("APPROVED_BUYERS1", "" + response_buyer.toString());
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
                        getBuyersList(searchText);
                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            getBuyersList(searchText);
        }

        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String newText) {

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Frgament Buyer Enquiry request code" + requestCode + "\n Result Code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ResponseCodes.BuyerEnquiry) {
            getBuyersList(searchText);
        } else if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                BuyersList buyer = (BuyersList) data.getSerializableExtra("buyer");
                mAdapter.setBuyer(buyer);
                if (mAdapter.getTransferDialog() != null) {
                    if (mAdapter.getTransferDialog().isShowing()) {
                        TextView edit_buyername = (TextView) mAdapter.getTransferDialog().getCustomView().findViewById(R.id.edit_buyername);
                        edit_buyername.setText(buyer.getCompany_name());
                    }
                }

            }
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
                searchText = "";

                searchView.setQuery("", false);
                searchView.clearFocus();

                getBuyersList(searchText);
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
            if (filter.equals("new")) {
                spinner.setSelection(0);
            } else if (filter.equals("old")) {
                spinner.setSelection(1);
            }
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    filter = "new";
                    getBuyersList(searchText);
                } else if (i == 1) {
                    filter = "old";
                    getBuyersList(searchText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //filter changes
        ic_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialogs.clear();
                if (filter.equals("new")) {
                    filterDialogs.add(new CommmonFilterOptionModel(1, "References Filled", false, "References Filled"));
                    filterDialogs.add(new CommmonFilterOptionModel(2, "Pending", false, "supplier_pending"));
                    filterDialogs.add(new CommmonFilterOptionModel(3, "Pending References", false, "Pending References"));
                } else {
                    filterDialogs.add(new CommmonFilterOptionModel(1, "Approved", false, "approved"));
                    filterDialogs.add(new CommmonFilterOptionModel(2, "Rejected", false, "rejected"));
                    filterDialogs.add(new CommmonFilterOptionModel(3, "Transferred", false, "Transferred"));
                }
                ContactsCommonFilterDialog commonFilterDialog = new ContactsCommonFilterDialog();
                commonFilterDialog.setList(filterDialogs);
                commonFilterDialog.setListener(new ContactsCommonFilterDialog.FilterDismissListener() {
                    @Override
                    public void selectedIDs(ArrayList<Integer> ids) {
                        filteredStatus = "";
                        if (ids != null && ids.size() > 0) {
                            //changing filter icon
                            ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                            ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_filled));
                            for (Integer id : ids) {
                                filteredStatus = (filteredStatus + "," + filterDialogs.get(id - 1).getRequest_filter_name());
                                filterDialogs.get(id - 1).setSelected(true);
                            }

                            if (filteredStatus.length() > 0) {
                                filteredStatus = filteredStatus.substring(1);
                            }
                            getBuyersList(searchText);

                        } else {
                            //restting filter icon
                            ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                            ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter));
                            for (int i = 0; i < filterDialogs.size(); i++) {
                                filterDialogs.get(i).setSelected(false);
                            }

                            //get Buyers
                            getBuyersList(searchText);
                        }
                    }
                });
                commonFilterDialog.show(getChildFragmentManager(), "filter_dialog");
            }
        });

    }
}
