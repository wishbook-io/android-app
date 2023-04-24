package com.wishbook.catalog.home.contacts;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.PendingBuyersAdapter;
import com.wishbook.catalog.commonadapters.PendingBuyersAdapterMerge;
import com.wishbook.catalog.commonmodels.MergeContacts;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.add.Fragment_AddBuyer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



public class Fragment_BuyersPending extends GATrackedFragment implements SearchView.OnQueryTextListener {

    private View v,v2;
    private RecyclerViewEmptySupport recyclerView;
    private SearchView searchView;
    private ArrayList<MyContacts> contactList = new ArrayList<>();
    private ArrayList<MergeContacts> Allitems = new ArrayList<>();
    private ArrayList<MergeContacts> allContacts = new ArrayList<>();
    private ArrayList<MergeContacts> FilteredallContacts = new ArrayList<>();
    private List<Response_Buyer> pendingBuyersList;
    private PendingBuyersAdapter mAdapter;
    private PendingBuyersAdapterMerge mAdapterMerge;

    private FloatingActionButton fabaddbuyer;
    Response_BuyerGroupType[] responseBuyerGroupTypes;
    private Response_Buyer[] response_buyer;
    private Response_Buyer[] search_response_buyer;
    private ArrayList<Response_Buyer> response_buyers=new ArrayList<>();
    String searchText="";
    public Fragment_BuyersPending() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_buyers_pending, ga_container, true);
        pendingBuyersList = new ArrayList<>();



        fabaddbuyer = (FloatingActionButton) v.findViewById(R.id.fabaddbuyer);
        fabaddbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_AddBuyer addBuyerFragment = new Fragment_AddBuyer();
                addBuyerFragment.setListener(new Fragment_AddBuyer.Listener() {
                    @Override
                    public void onDismiss() {
                        getBuyersList(getActivity(),searchText, FilteredallContacts);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");

            }
        });
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        searchView = (SearchView) v.findViewById(R.id.group_search);
        searchView.setIconifiedByDefault(false);

        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //searchView.setOnQueryTextListener(this);
        //getting Parent searchview
        Fragment_ContactsHolder frag = ((Fragment_ContactsHolder)this.getParentFragment());
        frag.searchView.setOnQueryTextListener(this);
        //local_searchview.setOnQueryTextListener(this);

        mAdapterMerge = new PendingBuyersAdapterMerge((AppCompatActivity) getActivity(),Allitems,responseBuyerGroupTypes);
        recyclerView.setAdapter(mAdapterMerge);

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"grouptype",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response", response);
                    responseBuyerGroupTypes = Application_Singleton.gson.fromJson(response, Response_BuyerGroupType[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });



        /*EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public Boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (Allitems.size() > 0) {
                    List<MergeContacts> moreContacts = new ArrayList<>();
                    for (int i = page * 10 + 1; i < (page + 1) * 10; i++) {
                        moreContacts.add(Allitems.get(i));
                    }
                    final int curSize = mAdapterMerge.getItemCount();
                    allContacts.addAll(moreContacts);

                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapterMerge.notifyItemRangeInserted(curSize, allContacts.size() - 1);
                        }
                    });
                }
                return  true;
            }

            @Override
            public void stopLoading() {

            }
        };

        recyclerView.addOnScrollListener(scrollListener);
*/
        if(Allitems.size()>0) {
          //  Allitems.addAll(getData());
        }

        if(Allitems.size()==0) {
            GetAllContacts gac = new GetAllContacts(getActivity(),mAdapterMerge);
            gac.execute();
        }else
        {
            mAdapterMerge = new PendingBuyersAdapterMerge((AppCompatActivity) getActivity(),Allitems,responseBuyerGroupTypes);
            recyclerView.setAdapter(mAdapterMerge);

        }
        return v;
    }

    private MaterialDialog showProgress(Context activity) {
        return new MaterialDialog.Builder(activity)
                .title("Loading")
                .content("Please wait..")
                .cancelable(false)
                .progress(true, 0).build();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "pendingBuyers");

    }

    @Override
    public void onPause() {
        super.onPause();
     //   saveData(Allitems);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("onresume", "pendingBuyers");

    }

    private void getBuyersList(Context mContext, String search, final ArrayList<MergeContacts> filteredallContacts) {
        // http://127.0.0.1:8000/api/v1/companies/252/buyers/?limit=20&offset=60
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext,"buyers_pending","")+"&&search="+search, null, headers, true, new HttpManager.customCallBack() {
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
                    if (isAdded()) {
                        Gson gson = new Gson();
                        response_buyer = gson.fromJson(response, Response_Buyer[].class);
                        Allitems.clear();
                        if (response_buyer.length > 0) {
                            if (response_buyer[0].getId() != null) {
                                response_buyers = new ArrayList<Response_Buyer>(Arrays.asList(response_buyer));
                                //  mAdapter = new PendingBuyersAdapter((AppCompatActivity) getActivity(), response_buyers);

                                Log.v("PENDING_BUYERS1", "" + response_buyer.toString());

                                for (int i = 0; i < response_buyers.size(); i++) {
                                    if (response_buyers.get(i).getBuying_company() != null) {
                                        Allitems.add(new MergeContacts(response_buyers.get(i).getBuying_company_phone_number(), StringUtils.capitalize(response_buyers.get(i).getBuying_company_name()), "", response_buyers.get(i).getStatus(), response_buyers.get(i).getId(), false));
                                    } else {
                                        Allitems.add(new MergeContacts(response_buyers.get(i).getInvitee_phone_number(), StringUtils.capitalize(response_buyers.get(i).getInvitee_name()), "", response_buyers.get(i).getStatus(), response_buyers.get(i).getId(), false));
                                    }
                                }
                                if (filteredallContacts.size() > 0) {
                                    for (int i = 0; i < filteredallContacts.size(); i++) {
                                        Allitems.add(filteredallContacts.get(i));
                                    }
                                }

                                Collections.sort(Allitems);
                                mAdapterMerge = new PendingBuyersAdapterMerge((AppCompatActivity) getActivity(), Allitems, responseBuyerGroupTypes);
                                recyclerView.setAdapter(mAdapterMerge);

                                Log.d("AllItems", Allitems.toString());
                            }
                        } else {
                            if (filteredallContacts.size() > 0) {
                                for (int i = 0; i < filteredallContacts.size(); i++) {
                                    Allitems.add(filteredallContacts.get(i));
                                }
                            }
                            Collections.sort(Allitems);
                            mAdapterMerge = new PendingBuyersAdapterMerge((AppCompatActivity) getActivity(), Allitems, responseBuyerGroupTypes);
                            recyclerView.setAdapter(mAdapterMerge);

                            Log.d("AllItems", Allitems.toString());

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
    public boolean onQueryTextChange(final String newText) {
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
                        Log.d("call to",searchText);
                        if(Allitems!=null) {
                            if (Allitems.size() > 0) { // Check if the Original List and Constraint aren't null.
                                FilteredallContacts.clear();
                                searchText = newText.toLowerCase();
                                for (int i = 0; i < Allitems.size(); i++) {
                                    try {
                                        if (Allitems.get(i).getName() != null) {
                                            String status = Allitems.get(i).getStatus().replace("buyer_", "").toLowerCase();
                                            if (Allitems.get(i).getName().toLowerCase().toString().contains(searchText) || Allitems.get(i).getPhone().toString().contains(searchText) || status.contains(searchText)) {
                                                FilteredallContacts.add(Allitems.get(i));
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        } if (searchText != "") {
                            if(getUserVisibleHint() && getContext()!=null)
                            getBuyersList(getActivity(), searchText, FilteredallContacts);
                        }

                        // handleSearch(searchText,response_buyer);
                    }
                }, 1000);
            }
        }else{

            if(searchText=="") {
                if(getUserVisibleHint() && getContext()!=null) {
                    GetAllContacts gac = new GetAllContacts(getActivity(), mAdapterMerge);
                    gac.execute();
                }
            }
        }

        return false;
    }

    public void reloadData() {
        // getBuyersList();
    }

    private void saveData(ArrayList<MergeContacts> allitems) {
        Activity_Home.pref.edit().putString("buyer_items","").apply();
        String items= new Gson().toJson(allitems);
        Activity_Home.pref.edit().putString("buyer_items",items).apply();

    }

    private ArrayList<MergeContacts> getData(){
        String buyer_items = Activity_Home.pref.getString("buyer_items","");
        ArrayList<MergeContacts> items = new ArrayList<>();
        if(buyer_items!="") {
            try {
                items =
                        new Gson().fromJson(buyer_items, new TypeToken<ArrayList<MergeContacts>>() {
                        }.getType());
            }
            catch (Exception e)
            {

            }
        }
        return items;
    }


    private class GetAllContacts extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private PendingBuyersAdapterMerge mAdapterMerge;

        public GetAllContacts(Context mContext, PendingBuyersAdapterMerge mAdapterMerge) {
            this.mContext = mContext;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            contactList.clear();
            Allitems.clear();
            contactList.addAll(StaticFunctions.getContactsFromPhone(mContext));
            for(int i=0;i<contactList.size();i++)
            {
                Allitems.add(new MergeContacts(contactList.get(i).getPhone(), StringUtils.capitalize(contactList.get(i).getName()),contactList.get(i).getCompany_name(),"","",false));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //getBuyersList(mContext);
            Collections.sort(Allitems);

            mAdapterMerge = new PendingBuyersAdapterMerge((AppCompatActivity) mContext,Allitems,responseBuyerGroupTypes);
            recyclerView.setAdapter(mAdapterMerge);


            Log.d("AllItems",Allitems.toString());
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity_Home.pref.edit().putString("position_buyer_group","0");
        Activity_Home.pref.edit().putString("buyer_items","").apply();

    }



}
