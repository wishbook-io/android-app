package com.wishbook.catalog.home.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.contacts.Fragment_BackOrder;
import com.wishbook.catalog.home.orders.adapters.SalesOrderAdapterNew;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Frgment_Tab_Brokerage_Orders extends GATrackedFragment implements
        SearchView.OnQueryTextListener ,SwipeRefreshLayout.OnRefreshListener{
    private String filter;
    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private FloatingActionButton orderfab;
    private SearchView searchView;
    //private SalesOrderAdapter salesOrderAdapter;
    private SalesOrderAdapterNew salesOrderAdapter;
    private ArrayList<Response_sellingorder> list = new ArrayList<>();
    private ArrayList<Response_sellingorder> backorder_list = new ArrayList<>();
    public ArrayList<String> backorder_allowedorderstatus = new ArrayList<>();
    private ArrayList<String> filterStatusList = new ArrayList<>();
    private Boolean menuShow = false;
    Response_sellingorder[] response_salesorder;
    RelativeLayout relativeProgress;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    Spinner spinner;
    ImageView search_icon,img_searchclose;
    LinearLayout linear_searchview;
    RelativeLayout relative_top_bar;

    public Frgment_Tab_Brokerage_Orders() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Frgment_Tab_Brokerage_Orders(String filter,Boolean menuShow) {
        // Required empty public constructor
        this.filter=filter;

        this.menuShow=menuShow;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_salesordernotitle, container, false);

        if(UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
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
                StaticFunctions.registerClickListener(getActivity(), dialog, null);
            }
            return v;
        }

        initTopBar();
        relativeProgress = (RelativeLayout) v.findViewById(R.id.relative_progress);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        ImageView menu = (ImageView) v.findViewById(R.id.menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        if(response_salesorder!=null  ) {
            if(response_salesorder.length>0) {

                list = filterList(response_salesorder, list, filterStatusList);

                backorder_allowedorderstatus.add("Accepted");
                backorder_allowedorderstatus.add("ordered");

                backorder_list = filterList(response_salesorder, backorder_list, backorder_allowedorderstatus);
            }
        }
        //salesOrderAdapter = new SalesOrderAdapter(getActivity(),list);
        initSwipeRefresh(v);

        if(menuShow)
        {
            menu.setVisibility(mRecyclerView.VISIBLE);
        }
        else
        {
            menu.setVisibility(mRecyclerView.GONE);
        }
        searchView = (SearchView) v.findViewById(R.id.search_view);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                }
            }
        });


        final PopupMenu popupMenu = new PopupMenu(getActivity(), menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Backorder" :
                        Fragment_BackOrder backorder=new Fragment_BackOrder();
                        Application_Singleton.CONTAINER_TITLE = "New back order";
                        Application_Singleton.CONTAINERFRAG=backorder;
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST",(Serializable)backorder_list);
                        backorder.setArguments(args);
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                        break;
                }
                return true;
            }
        });

        popupMenu.inflate(R.menu.menu_backorder);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });





        return v;
    }

    private void getBrokerageOrder(String status) {
        showProgress1();

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"brokerage_order","")+"?processing_status="+status, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress1();
                if(isAdded() && !isDetached()){
                    Log.v("sync response", response);
                    Log.v("res", "" + response);
                    Gson gson = new Gson();
                    response_salesorder = gson.fromJson(response, Response_sellingorder[].class);
                    list = new ArrayList<>(Arrays.asList(response_salesorder));
                    salesOrderAdapter = new SalesOrderAdapterNew(getActivity(),list,true);
                    mRecyclerView.setAdapter(salesOrderAdapter);


                    backorder_allowedorderstatus.add("Accepted");
                    backorder_allowedorderstatus.add("ordered");

                    backorder_list = filterList(response_salesorder, backorder_list, backorder_allowedorderstatus);
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress1();
            }
        });
    }


    private ArrayList<Response_sellingorder> filterList(Response_sellingorder[] response_sellingorders, ArrayList<Response_sellingorder> list, ArrayList<String> filterStatusList) {
        list= new ArrayList<Response_sellingorder>();
        for(int i=0;i<response_sellingorders.length;i++)
        {
            for(int j=0;j<filterStatusList.size();j++) {
                String status = response_sellingorders[i].getProcessing_status();
                if (filterStatusList.get(j).equals(status) || filterStatusList.get(j).toLowerCase().equals(status) ) {
                    list.add(response_sellingorders[i]);
                }
            }
        }

        return list;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(salesOrderAdapter!=null) {
            final List<Response_sellingorder> filteredModelList = filter(list, query);
            salesOrderAdapter.setData((ArrayList<Response_sellingorder>) filteredModelList);
        }

        return true;
    }

    private List<Response_sellingorder> filter(ArrayList<Response_sellingorder> models, String query) {
        query = query.toLowerCase();
        final List<Response_sellingorder> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
            return filteredModelList;
        }
        for (Response_sellingorder model : models) {
            final String ordernumber =model.getOrder_number().toLowerCase();
            final String date =getformatedDate(model.getDate().toLowerCase());
            final String total_rate = model.getTotal_rate().toLowerCase();
            final String status = model.getProcessing_status().toLowerCase();
            final String ownstatus = model.getCustomer_status().toLowerCase();
            final String company = model.getCompany_name().toLowerCase();
            String regex ="\\d{4}-\\d{2}-\\d{2}";
            // if (!query.matches(regex)) {
            if(ordernumber.contains(query)){
                filteredModelList.add(model);
            }

            /*if (date.contains(query)) {
                filteredModelList.add(model);
            }
            //  }
            else if (status.contains(query)) {
                filteredModelList.add(model);
            }

            else if (total_rate.contains(query)) {
                filteredModelList.add(model);

            }
            else if (ownstatus.contains(query)) {
                filteredModelList.add(model);
            }*/

            else if (company.contains(query)) {
                filteredModelList.add(model);

            }
        }
        return filteredModelList;
    }


    private String getformatedDate(String dat) {

        String format = "yyyy-MM-dd";
        DateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date).toLowerCase().toString();

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    public void showProgress1() {
        if(relativeProgress!=null){
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress1() {
        if(relativeProgress !=null) {
            relativeProgress.setVisibility(View.GONE);
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
                searchView.setQuery("",false);
                searchView.clearFocus();
                getBrokerageOrder(filter);
                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

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
                searchView.setFocusable(true);
                searchView.setIconifiedByDefault(false);
                searchView.requestFocus();
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
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
        if(filter!=null) {
            if(filter.equals("placed")){
                spinner.setSelection(0);
            } else if(filter.equals("pending")){
                spinner.setSelection(1);
            } else if(filter.equals("dispatch")) {
                spinner.setSelection(2);
            } else if(filter.equals("cancel")) {
                spinner.setSelection(3);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    filter = "placed";
                    getBrokerageOrder(filter);
                } else if (i == 1) {
                    filter = "pending";
                    getBrokerageOrder(filter);
                } else if (i == 2) {
                    filter = "dispatch";
                    getBrokerageOrder(filter);
                } else if (i == 3) {
                    filter = "cancel";
                    getBrokerageOrder(filter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
