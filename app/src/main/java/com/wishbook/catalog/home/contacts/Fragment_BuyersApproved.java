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
import androidx.appcompat.widget.Toolbar;
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
import com.wishbook.catalog.commonadapters.ApprovedBuyersAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.contacts.add.Fragment_AddBuyer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Fragment_BuyersApproved extends GATrackedFragment implements
        SearchView.OnQueryTextListener, Paginate.Callbacks,SwipeRefreshLayout.OnRefreshListener {


    private View v;
    private RecyclerViewEmptySupport recyclerView;
    private SearchView local_SearchView;
    private ApprovedBuyersAdapter mAdapter;
    private FloatingActionButton fabaddbuyer;

    private ArrayList<Response_Buyer> response_buyers = new ArrayList<>();
    private ArrayList<Response_Buyer> response_buyersFilteredList = new ArrayList<>();
    private Toolbar toolbar;
    private String searchText = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    Fragment_AddBuyer addBuyerFragment;


    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page = 0;
    private HashMap<String, Response_Buyer> dataHashMap = new HashMap<>();

    Fragment_ContactsHolder frag ;

    private SwipeRefreshLayout swipe_container;
    private boolean isFilter;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    TextView list_empty1;


    public Fragment_BuyersApproved() {
        // Required empty public constructor
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
        local_SearchView = (SearchView) v.findViewById(R.id.group_search);
        list_empty1 = v.findViewById(R.id.list_empty1);
        list_empty1.setText(getResources().getString(R.string.empty_text_network));
        local_SearchView.setIconifiedByDefault(false);
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        fabaddbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_AddBuyer addBuyerFragment = new Fragment_AddBuyer();
                addBuyerFragment.setListener(new Fragment_AddBuyer.Listener() {
                    @Override
                    public void onDismiss() {
                        getBuyersList(LIMIT, INITIAL_OFFSET, true, searchText);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");

                Application_Singleton.trackEvent("Approved Buyer", "Click", "Floating Plus Button");
            }
        });
        if (paginate != null) {
            paginate.unbind();
        }

        //getting Parent searchview
       frag = ((Fragment_ContactsHolder) this.getParentFragment());
        frag.searchView.setOnQueryTextListener(this);

        //light the filter option as no use in buyers approved
        frag.ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_light));
        frag.ic_filter.setEnabled(false);

        frag.btn_invite.setVisibility(View.GONE);

        //Show Invite Button
        frag.fab_invite.setVisibility(View.VISIBLE);



        frag.fab_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuyerFragment = new Fragment_AddBuyer();
                addBuyerFragment.setListener(new Fragment_AddBuyer.Listener() {
                    @Override
                    public void onDismiss() {
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");
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
                StaticFunctions.registerClickListener(getActivity(), dialog,"Buyer_Approved");
            }
            return v;
        }





        //Network Tutorial
       /* if(!Application_Singleton.tutorial_pref.getBoolean("network_tutorial",false) && !UserInfo.getInstance(getContext()).getGroupstatus().equals("2")){
            Intent intent = new Intent(getActivity(), IntroActivity.class);
            ArrayList<AppIntroModel> models = new ArrayList<>();
            models.add(new AppIntroModel(getString(R.string.intro_network_title),getString(R.string.intro_network_desc),R.drawable.intro_network_new));
            intent.putParcelableArrayListExtra("list",models);
            startActivity(intent);
            Application_Singleton.tutorial_pref.edit().putBoolean("network_tutorial",true).apply();
        }*/


        mAdapter = new ApprovedBuyersAdapter(this, response_buyers);
        recyclerView.setAdapter(mAdapter);
        paginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        mAdapter.notifyDataSetChanged();
      /*  if(response_buyer!=null) {
            mAdapter = new ApprovedBuyersAdapter((AppCompatActivity) getActivity(), response_buyers);
            recyclerView.setAdapter(mAdapter);
        }*/
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("Buyers");

        toolbar.setVisibility(View.GONE);
       /* if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            toolbar.setVisibility(View.VISIBLE);
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }*/



         /*scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public Boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    getBuyersList(LIMIT, totalItemsCount, false, searchText);

                return true;
            }

             @Override
             public void stopLoading() {
             }
         };

        recyclerView.addOnScrollListener(scrollListener);*/


         initSwipeRefresh(v);

        getBuyersList(LIMIT, INITIAL_OFFSET, true, "");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v("onresume", "approved buyers");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "approved buyers");
    }

    private void getBuyersList(int limit, final int offset, Boolean progress, final String search) {

        Log.v("onCreate", "called approved buyers");

        HttpManager.METHOD methodType;
        if (progress) {
          //  showProgress();
            methodType = HttpManager.METHOD.GETWITHPROGRESS;
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        if (offset == 0) {
            page = 0;
            hasLoadedAllItems = false;
            response_buyers.clear();
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        //  final Boolean[] hasAlreadyLoadedCache = {false};

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(methodType, URLConstants.companyUrl(getActivity(), "buyers_approved", "") + "&&limit=" + limit + "&&offset=" + offset + "&&search=" + search, null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
                hideProgress();
                //   hasAlreadyLoadedCache[0] =true;
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    if (isAdded() && !isDetached()) {
                        Loading = false;

                        Log.d("Data arrived Range", offset + " " + (offset + LIMIT));

                /*if(!hasAlreadyLoadedCache[0]) {
                    page++;
                }
                else{
                    page=0;
                    hasLoadedAllItems=false;
                    response_buyers.clear();
                }*/


                        Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                        ArrayList<Response_Buyer> temp = new ArrayList<Response_Buyer>(Arrays.asList(response_buyer));

                        Log.d("sync searchText", searchText);
                        Log.v("buyerssss", "dataUpdated " + dataUpdated);

                        if (response_buyer.length > 0) {


                            //checking if data updated on 2nd page or more
                            response_buyers = (ArrayList<Response_Buyer>) HttpManager.removeDuplicateIssue(offset, response_buyers, temp, dataUpdated, LIMIT);

                            Log.d("buyerssss", "curr size: " + response_buyers.size());
                            Log.d("buyerssss", "offset: " + offset);
                            Log.d("buyerssss", "offset +limit: " + offset + LIMIT);




                      /*  if (dataUpdated && (offset + LIMIT) > LIMIT) {
                            if (response_buyers.size() > offset) {

                                Log.d("buyerssss", "1++ page ");
                              //  response_buyers.subList(offset, response_buyers.size()).clear();
                                for (int i = 0; i < response_buyer.length; i++) {
                                    response_buyers.set( i + offset, response_buyer[i]);
                                    Log.v("buyerssss", "replaced " + response_buyer[i].getBuying_company_name());
                                }



                            }
                        } else if (dataUpdated) {

                            Log.d("buyerssss", "1st page ");
                            response_buyers.clear();

                        }*/


                            for (int i = 0; i < response_buyer.length; i++) {
                                response_buyers.add(response_buyer[i]);
                                Log.v("buyerssss", "added " + response_buyer[i].getBuying_company_name());
                            }


                            page = (int) Math.ceil((double) response_buyers.size() / LIMIT);


                            Log.v("buyerssss", "page  " + page);

                            if (response_buyer.length % LIMIT != 0) {
                                hasLoadedAllItems = true;
                            }

                            if (response_buyers.size() <= LIMIT) {
                                mAdapter.notifyDataSetChanged();
                            } else {

                                Log.v("buyerssss", "notifyItemRangeChanged  " + offset + " " + offset + response_buyer.length);
                                //    mAdapter.notifyItemRangeChanged(offset, response_buyers.size() - 1);


                                mAdapter.notifyItemRangeChanged(offset, offset + response_buyer.length);

                            }


                        } else {
                            hasLoadedAllItems = true;
                            mAdapter.notifyDataSetChanged();
                        }
                /*    if (response_buyer.length > 0) {
                    Log.v("buyerssss", "currpage  "+page + "response_buyers.size() "+ response_buyers.size()+" LIMIT "+LIMIT);


                    if(dataUpdated && response_buyers.size() >= LIMIT){
                        if(response_buyers.size()> (page+1)*LIMIT) {
                            Log.v("buyerssss", "deleted from "+offset+" "+((page+1)*LIMIT -1) );
                            response_buyers.subList(offset,((page+1)*LIMIT -1)).clear();
                        }else if(offset > (response_buyers.size()-1) ){
                            for (int i = 0; i < response_buyer.length; i++) {
                                response_buyers.add(response_buyer[i]);
                                Log.v("buyerssss", "offset > (response_buyers.size()-1) added page 0"+ response_buyer[i].getBuying_company_name() );
                            }
                        }
                        else{
                            Log.v("buyerssss", "deleted2 from "+offset+" "+(response_buyers.size()-1) );
                            response_buyers.subList(offset,(response_buyers.size()-1)).clear();
                        }

                        for (int i = 0; i < response_buyer.length; i++) {
                            response_buyers.add(response_buyer[i]);
                            Log.v("buyerssss", "added page>0"+ response_buyer[i].getBuying_company_name() );
                        }

                    }
                    else if(dataUpdated){
                        response_buyers.clear();
                        for (int i = 0; i < response_buyer.length; i++) {
                            response_buyers.add(response_buyer[i]);
                            Log.v("buyerssss", "dataupdated true added page 0"+ response_buyer[i].getBuying_company_name() );
                        }
                    }else{

                        for (int i = 0; i < response_buyer.length; i++) {
                            response_buyers.add(response_buyer[i]);
                            Log.v("buyerssss", "added page 0"+ response_buyer[i].getBuying_company_name() );
                        }
                    }
                      *//*  for (int i = 0; i < response_buyer.length; i++) {
                            response_buyers.add(response_buyer[i]);
                            Log.v("buyerssss", "added "+ response_buyer[i].getBuying_company_name() );


                        }*//*
                    page = (int) Math.ceil((double)response_buyers.size()/LIMIT);
                    Log.v("buyerssss", "page  "+page );

                    if(response_buyers.size() > (page-2)*LIMIT  ) {


                    }

                    if (response_buyer.length % LIMIT != 0) {
                        hasLoadedAllItems = true;
                    }

                    if (response_buyers.size() <= LIMIT) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), response_buyers.size() - 1);
                    }


                } else {
                    hasLoadedAllItems = true;
                    mAdapter.notifyDataSetChanged();
                }

*/


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

    private void replace(int offset, Response_Buyer[] response_buyer) {

        //if data from server  is not actually previously added into arraylist
        //than we should possibly add the
        //data rather than replacing

        if (response_buyers.size() > offset) {

            response_buyers.subList(offset, response_buyers.size()).clear();

            for (int i = 0; i < response_buyer.length; i++) {
                response_buyers.add(response_buyer[i]);
            }

        } else {
            for (int i = offset; i < offset + response_buyer.length; i++) {
                response_buyers.add(response_buyer[i - offset]);
            }
        }
        Log.d("buyersss", response_buyers.size() + "");
    }


    @Override
    public boolean onQueryTextSubmit(String newText) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        final Response_Buyer[] response_buyer = response_buyers.toArray(new Response_Buyer[response_buyers.size()]);
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
                            getBuyersList(LIMIT, INITIAL_OFFSET, false, searchText);
                        }
                    }
                }, 1000);
            }
        }
        if (newText.length() == 0) {
            list_empty1.setText(getResources().getString(R.string.empty_text_network));
            if (getUserVisibleHint() && getContext() != null)
                getBuyersList(LIMIT, INITIAL_OFFSET, false, searchText);
        }

        return false;
    }

    /*public void handleSearch(String newText, Response_Buyer[] response_buyer){
        if(response_buyer!=null) {


            if (response_buyer.length > 0) { // Check if the Original List and Constraint aren't null.
                response_buyers.clear();
                searchText=newText;
                for (int i = 0; i < response_buyer.length; i++) {
                    try {
                        if (response_buyer[i].getBuying_company_name() != null) {
                            if (response_buyer[i].getBuying_company_name().toLowerCase().toString().contains(newText.toLowerCase()) || response_buyer[i].getBuying_company_phone_number().toString().contains(newText) || response_buyer[i].getGroup_type().toLowerCase().toString().contains(newText)) {
                                response_buyers.add(response_buyer[i]);
                                Log.d("Added",response_buyer[i].getBuying_company_name());
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                mAdapter.notifyDataSetChanged();

                Log.d("Called","This Search" + searchText );

                getBuyersList(LIMIT,INITIAL_OFFSET,false,searchText);
            }else{
                searchText = newText;
                getBuyersList(LIMIT,INITIAL_OFFSET,false,searchText);
            }
        }
    }
*/
    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            getBuyersList(LIMIT, page * LIMIT, false, searchText);
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
        if (addBuyerFragment != null) {
            addBuyerFragment.dismiss();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode== ResponseCodes.Buyers_Approved){
            isAllowCache = false;
            getBuyersList(LIMIT, INITIAL_OFFSET, true, "");
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
                getBuyersList(LIMIT, INITIAL_OFFSET, true, "");
                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

}
