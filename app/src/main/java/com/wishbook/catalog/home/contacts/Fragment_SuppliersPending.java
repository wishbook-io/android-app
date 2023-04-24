package com.wishbook.catalog.home.contacts;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.wishbook.catalog.commonadapters.PendingSupplierAdapterMerge;
import com.wishbook.catalog.commonadapters.PendingSuppliersAdapter;
import com.wishbook.catalog.commonmodels.MergeContacts;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.add.Fragment_AddSupplier;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Fragment_SuppliersPending extends GATrackedFragment implements SearchView.OnQueryTextListener {

    private View v;
    private RecyclerViewEmptySupport recyclerView;
    private SearchView searchView;
    private ArrayList<MyContacts> contactList = new ArrayList<>();
    private ArrayList<MergeContacts> Allitems = new ArrayList<>();
    private ArrayList<MergeContacts> allContacts = new ArrayList<>();
    private ArrayList<Response_Suppliers> pendingSuppliersList = new ArrayList<>();
    private Response_Suppliers[] response_suppliers;
    private PendingSuppliersAdapter mAdapter;
    private ArrayList<MergeContacts> FilteredallContacts = new ArrayList<>();
    Response_BuyerGroupType[] responseBuyerGroupTypes;
    private PendingSupplierAdapterMerge mAdapterMerge;
    private Fragment_AddSupplier.Listener listener;
    private FloatingActionButton fabaddsupplier;

    public Fragment_SuppliersPending() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_pending_suppliers, ga_container, true);
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        searchView = (SearchView) v.findViewById(R.id.group_search);
        fabaddsupplier=(FloatingActionButton)v.findViewById(R.id.fabaddsupplier);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);

        fabaddsupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_AddSupplier addBuyerFragment = new Fragment_AddSupplier();
                addBuyerFragment.setListener(new Fragment_AddSupplier.Listener() {
                    @Override
                    public void onDismiss() {
                        getSuppliersList(getActivity(),contactList);
                    }
                });
                addBuyerFragment.show(getActivity().getSupportFragmentManager(), "addbuyer");
            }
        });

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

      //  Allitems.addAll(getData());
        Allitems.addAll(getData());

        if(Allitems.size()==0) {
            GetAllContacts gac = new GetAllContacts(getActivity());
            gac.execute();
        }else
        {
            mAdapterMerge = new PendingSupplierAdapterMerge((AppCompatActivity) getActivity(), Allitems,responseBuyerGroupTypes);
            recyclerView.setAdapter(mAdapterMerge);

        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v("onresume", "pendingSuppliers");

    }

    @Override
    public void onPause() {
        super.onPause();
        saveData(Allitems);
    }

    private void saveData(ArrayList<MergeContacts> allitems) {
        Activity_Home.pref.edit().putString("supplier_items","").apply();
        String items= new Gson().toJson(allitems);
        Activity_Home.pref.edit().putString("supplier_items",items).apply();

    }

    private ArrayList<MergeContacts> getData(){
        String supplier_items = Activity_Home.pref.getString("supplier_items","");
        ArrayList<MergeContacts> items = new ArrayList<>();
        if(supplier_items!="") {
            try {
                items =
                        new Gson().fromJson(supplier_items, new TypeToken<ArrayList<MergeContacts>>() {
                        }.getType());
            }
            catch (Exception e)
            {

            }
        }
        return items;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "pendingSuppliers");

    }

    private void getSuppliersList(Context mContext, final ArrayList<MyContacts> contactList) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext,"sellers_pending",""), null, headers, true, new HttpManager.customCallBack() {
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
                        response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                        pendingSuppliersList = new ArrayList<Response_Suppliers>(Arrays.asList(response_suppliers));
                /*mAdapter = new PendingSuppliersAdapter((AppCompatActivity) getActivity(), pendingSuppliersList);
                recyclerView.setAdapter(mAdapter);*/
                        if (pendingSuppliersList.size() > 0) {
                            // Allitems.add(new MergeContacts("","Supplier", "","","", true));
                            for (int i = 0; i < pendingSuppliersList.size(); i++) {
                                if (pendingSuppliersList.get(i).getSelling_company() != null) {
                                    Allitems.add(new MergeContacts(pendingSuppliersList.get(i).getSelling_company_phone_number(), StringUtils.capitalize(pendingSuppliersList.get(i).getSelling_company_name()), "", pendingSuppliersList.get(i).getStatus(), pendingSuppliersList.get(i).getId(), false));
                                } else {
                                    Allitems.add(new MergeContacts(pendingSuppliersList.get(i).getInvitee_phone_number(), StringUtils.capitalize(pendingSuppliersList.get(i).getInvitee_name()), "", pendingSuppliersList.get(i).getStatus(), pendingSuppliersList.get(i).getId(), false));
                                }
                            }
                            //  Allitems.add(new MergeContacts("", "Contacts", "", "", "", true));
                            for (int i = 0; i < contactList.size(); i++) {
                                Allitems.add(new MergeContacts(contactList.get(i).getPhone(), StringUtils.capitalize(contactList.get(i).getName()), contactList.get(i).getCompany_name(), "", "", false));
                            }
                            Collections.sort(Allitems);


                            mAdapterMerge = new PendingSupplierAdapterMerge((AppCompatActivity) getActivity(), Allitems, responseBuyerGroupTypes);
                            recyclerView.setAdapter(mAdapterMerge);
                            Log.v("APPROVED_BUYERS1", "" + response_suppliers.toString());
                        } else {
                            for (int i = 0; i < contactList.size(); i++) {
                                Allitems.add(new MergeContacts(contactList.get(i).getPhone(), StringUtils.capitalize(contactList.get(i).getName()), contactList.get(i).getCompany_name(), "", "", false));
                            }
                            Collections.sort(Allitems);
                            mAdapterMerge = new PendingSupplierAdapterMerge((AppCompatActivity) getActivity(), Allitems, responseBuyerGroupTypes);
                            recyclerView.setAdapter(mAdapterMerge);

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
        if(Allitems!=null) {
            if (Allitems.size() > 0) { // Check if the Original List and Constraint aren't null.
                FilteredallContacts.clear();
                newText = newText.toLowerCase();
                for (int i = 0; i < Allitems.size(); i++) {
                    try {
                        if (Allitems.get(i).getName()!= null) {
                            String status =Allitems.get(i).getStatus().replace("buyer_","").toLowerCase();
                            if (Allitems.get(i).getName().toLowerCase().toString().contains(newText.toLowerCase()) || Allitems.get(i).getPhone().toString().contains(newText) || status.contains(newText) ) {
                                FilteredallContacts.add(Allitems.get(i));
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                if(getUserVisibleHint() && getContext()!=null) {
                    mAdapterMerge = new PendingSupplierAdapterMerge((AppCompatActivity) getActivity(), FilteredallContacts, responseBuyerGroupTypes);
                    recyclerView.setAdapter(mAdapterMerge);
                }
            }
        }
        return false;
    }

   /* public void reloadData() {
        getSuppliersList();
    }*/

    private class GetAllContacts extends AsyncTask<Void, Void, ArrayList<MyContacts>> {

        private Context mContext;

        public GetAllContacts(Context mContext) {
            this.mContext = mContext;
        }


        @Override
        protected ArrayList<MyContacts> doInBackground(Void... params) {
            contactList.clear();
            Allitems.clear();
            contactList.addAll(StaticFunctions.getContactsFromPhone(mContext));
            return contactList;
        }

        @Override
        protected void onPostExecute(ArrayList<MyContacts> myContactses) {
            super.onPostExecute(myContactses);
            getSuppliersList(mContext,contactList);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

      /*  @Override
        protected ArrayList<MergeContacts> doInBackground(Void... params) {
            contactList.clear();
            Allitems.clear();
            StaticFunctions.getContactsFromPhone(mContext,contactList);
            Collections.sort(contactList, new Comparator<MyContacts>() {
                @Override
                public int compare(MyContacts lhs, MyContacts rhs) {
                    return lhs.getCompany_name().compareToIgnoreCase(rhs.getName());
                }
            });
           *//* Allitems.add(new MergeContacts("","Contacts", "","","", true));
            for(int i=0;i<contactList.size();i++)
            {
                Allitems.add(new MergeContacts(contactList.get(i).getPhone(), StringUtils.capitalize(contactList.get(i).getName()),contactList.get(i).getCompany_name(),"","",false));
            }*//*
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getSuppliersList(mContext);
        }*/



        }
    private MaterialDialog showProgress(Context activity) {
        return new MaterialDialog.Builder(activity)
                .title("Loading")
                .content("Please wait..")
                .cancelable(false)
                .progress(true, 0).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity_Home.pref.edit().putString("supplier_items","").apply();
    }
}
