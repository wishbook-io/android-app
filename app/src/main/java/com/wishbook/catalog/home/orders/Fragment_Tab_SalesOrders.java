package com.wishbook.catalog.home.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.contacts.Fragment_BackOrder;
import com.wishbook.catalog.home.orders.adapters.SalesOrderAdapterNew;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class  Fragment_Tab_SalesOrders extends GATrackedFragment implements
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener{

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
    String from ="";
    String source= "";
    LinearLayout linear_select_date_range, linear_order_date_container;
    TextView txt_selected_date;
    String default_from_date, default_to_date;
    String order_date_range_type;

    public Fragment_Tab_SalesOrders() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment_Tab_SalesOrders(String filter,Boolean menuShow, String from) {
        // Required empty public constructor
        this.filter=filter;
        this.menuShow=menuShow;
        this.from = from;
    }

    @SuppressLint("ValidFragment")
    public Fragment_Tab_SalesOrders(String filter,Boolean menuShow, String from, String type) {
        // Required empty public constructor
        this.filter=filter;
        this.menuShow=menuShow;
        this.from = from;
        this.order_date_range_type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_salesordernotitle, container, false);

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
                StaticFunctions.registerClickListener(getActivity(), dialog, null);
            }
            return v;
        }
        if (getActivity() instanceof ActivityOrderHolder) {
            setHasOptionsMenu(true);
        }
        initTopBar();

        relativeProgress = (RelativeLayout) v.findViewById(R.id.relative_progress);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        ImageView menu = (ImageView) v.findViewById(R.id.menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        if (response_salesorder != null) {
            if (response_salesorder.length > 0) {

                list = filterList(response_salesorder, list, filterStatusList);

                backorder_allowedorderstatus.add("Accepted");
                backorder_allowedorderstatus.add("ordered");

                backorder_list = filterList(response_salesorder, backorder_list, backorder_allowedorderstatus);
            }
        }
        //salesOrderAdapter = new SalesOrderAdapter(getActivity(),list);


        if (menuShow) {
            menu.setVisibility(mRecyclerView.VISIBLE);
        } else {
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

        linear_select_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomOrderDateRange bottomOrderDateRange = BottomOrderDateRange.newInstance(null);
                bottomOrderDateRange.setDismissListener(new BottomOrderDateRange.DismissListener() {
                    @Override
                    public void onDismiss(String from_date, String to_date, String displayText) {
                        Log.e("TAG", "onDismiss: ======>"+from_date + "\n Display Text=====>"+displayText );
                        default_from_date = from_date;
                        default_to_date = to_date;
                        if(displayText.contains(";")) {
                            String[] split = displayText.split(";");
                            if(split.length == 2){
                                default_from_date = split[0];
                                default_to_date = split[1];
                                txt_selected_date.setText("Start "+split[0]+ " - "+ "End "+split[1]);
                            }
                        } else {
                            txt_selected_date.setText(displayText);
                        }
                        getSalesOrder(filter,false,"");
                    }
                });
                bottomOrderDateRange.show(getFragmentManager(),"SelectRange");
            }
        });

        final PopupMenu popupMenu = new PopupMenu(getActivity(), menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Backorder":
                        Fragment_BackOrder backorder = new Fragment_BackOrder();
                        Application_Singleton.CONTAINER_TITLE = "New back order";
                        Application_Singleton.CONTAINERFRAG = backorder;
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) backorder_list);
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


        initSwipeRefresh(v);



        if(filter!=null){
            if(filter.equalsIgnoreCase(ActivityOrderHolder.class.getSimpleName())){
                source = "Order Tab";
            }
        }

        WishbookTracker.sendScreenEvents(getActivity(),WishbookEvent.SELLER_EVENTS_CATEGORY,"SalesOrdersList_screen",source,null);
        return v;
    }

    private void getSalesOrder(String status,boolean isSearchOrder, String search_string) {
        showProgress1();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(),"salesorder","")+"?processing_status="+status;
        if(!isSearchOrder) {
            if(default_from_date!=null && default_to_date!=null) {
                String from_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT,default_from_date);
                String to_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT,default_to_date);
                url+= "&from_date="+from_date_string+"&to_date="+to_date_string;
            }
        } else {
            url = URLConstants.companyUrl(getActivity(),"salesorder","")+"?search="+search_string +"&processing_status="+status;
        }

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress1();
                Log.v("sync response", response);
                if(isAdded() && !isDetached()) {
                    Gson gson = new Gson();
                    response_salesorder = gson.fromJson(response, Response_sellingorder[].class);
                    list = new ArrayList<>(Arrays.asList(response_salesorder));
                    salesOrderAdapter = new SalesOrderAdapterNew(getActivity(),list,false);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_order_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                default_from_date =null;
                default_to_date = null;
                linear_searchview.setVisibility(View.VISIBLE);
                ((ActivityOrderHolder) getActivity()).toolbar.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.GONE);
                linear_order_date_container.setVisibility(View.GONE);
                return true;

            default:
                break;
        }

        return false;
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
        if(salesOrderAdapter!=null){
           getSalesOrder("placed",true,query);
        }
        return true;
    }

    private List<Response_sellingorder> filter(ArrayList<Response_sellingorder> models, String query) {
        final List<Response_sellingorder> filteredModelList = new ArrayList<>();
        try {
            query = query.toLowerCase();
            if (query.equals("") | query.isEmpty()) {
                filteredModelList.addAll(models);
                return filteredModelList;
            }
            for (Response_sellingorder model : models) {
                final String ordernumber = model.getOrder_number().toLowerCase();
                final String date = getformatedDate(model.getDate().toLowerCase());
                final String total_rate = model.getTotal_rate().toLowerCase();
                final String status = model.getProcessing_status().toLowerCase();
                final String ownstatus = model.getCustomer_status().toLowerCase();
                final String company = model.getCompany_name().toLowerCase();
                String regex = "\\d{4}-\\d{2}-\\d{2}";
                // if (!query.matches(regex)) {
                if (ordernumber.contains(query)) {
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
        } catch (Exception e) {
            e.printStackTrace();
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
                getSalesOrder(filter,false,"");
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
        linear_select_date_range = v.findViewById(R.id.linear_select_date_range);
        linear_order_date_container = v.findViewById(R.id.linear_order_date_container);
        txt_selected_date = v.findViewById(R.id.txt_selected_date);
        setDateRange();
        // Initializing a String Array
        String[] order_status = new String[]{
                "All",
                "Placed",
                "Dispatched",
                "Cancelled"
        };

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.order_spinner_item, order_status
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem_new);
        spinner.setAdapter(spinnerArrayAdapter);

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
                if (getActivity() instanceof ActivityOrderHolder)
                    ((ActivityOrderHolder) getActivity()).toolbar.setVisibility(View.VISIBLE);
                relative_top_bar.setVisibility(View.VISIBLE);
                linear_order_date_container.setVisibility(View.VISIBLE);
                linear_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
                searchView.clearFocus();
                spinner.setSelection(0);
                KeyboardUtils.hideKeyboard(getActivity());
            }
        });
        if(filter!=null) {
            if (filter.equals("placed")) {
                spinner.setSelection(0);
            }if(filter.equals("pending")){
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
                if(i == 0) {
                    filter = "placed";
                    getSalesOrder(filter,false,"");
                }if (i == 1) {
                    filter = "pending";
                    getSalesOrder(filter,false,"");
                } else if (i == 2) {
                    filter = "dispatch";
                    getSalesOrder(filter,false,"");
                } else if (i == 3) {
                    filter = "cancel";
                    getSalesOrder(filter,false,"");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setDateRange() {
        if (order_date_range_type != null) {
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            switch (order_date_range_type.toLowerCase()) {
                case "all orders":
                    txt_selected_date.setText("All");
                    default_from_date = null;
                    default_to_date = null;
                    break;
                case "today":
                    txt_selected_date.setText("Today");
                    default_from_date = formatter.format(today);
                    default_to_date = formatter.format(today);
                    break;
                case "yesterday": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(from_date);
                    txt_selected_date.setText("Yesterday");
                }
                break;
                case "this week": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    txt_selected_date.setText("Last 7 Days");
                }
                break;
                case "this month": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    txt_selected_date.setText("This Month");
                }
                break;
                default:
                    if(order_date_range_type.contains(";")) {
                        String[] split = order_date_range_type.split(";");
                        if(split.length == 2){
                            default_from_date = split[0];
                            default_to_date = split[1];
                            txt_selected_date.setText("Start "+split[0]+ " - "+ "End "+split[1]);
                        }
                    }
                    break;

            }
        } else {
            txt_selected_date.setText("All");
        }
    }

}
