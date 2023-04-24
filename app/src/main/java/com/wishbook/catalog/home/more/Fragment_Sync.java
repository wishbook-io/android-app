package com.wishbook.catalog.home.more;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wishbook.catalog.GATrackedFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.commonadapters.SyncAdapter;
import com.wishbook.catalog.commonmodels.SyncModel;
import com.wishbook.catalog.commonmodels.UserInfo;

public class Fragment_Sync extends GATrackedFragment  {

    private View v;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fabsync;
    private SyncAdapter syncAdapter;
    public static final String SHAREDWITHME = "Received Catalogs";
    public static final String SHAREDBYME = "Sent Catalogs";
    public static final String MYCATALOGS = "My Catalogs";
    public static final String MYSELECTIONS = "My Selections";
    public static final String CATEGORIES = "Categories";
    public static final String BUYERGROUPS = "Buyer Group";
    public static final String STATES = "State";
    public static final String MEETINGS = "Meeting";
    public static final String MEETINGREPORTS = "Meeting Report";
    public static final String DASHBOARD = "Dashboard";
    public static final String BRANDS = "Brands";
    public static final String MYBRANDS = "My Brands";
    public static final String GROUPTYPES = "Groups";
    public static final String APPROVEDBUYERS = "Approved Buyers";
    public static final String REJECTBUYERS = "Rejected Buyers";
    public static final String PENDINGBUYERS = "Pending Buyers";
    public static final String APPROVEDSUPPLIERS = "Approved Suppliers";
    public static final String REJECTSUPPLIERS= "Rejected Suppliers";
    public static final String PENDINGSUPPLIERS = "Pending Suppliers";
    public static final String SALESORDERS = "Sales Orders";
    public static final String PURCHASEORDERS = "Purchase Orders";


    public Fragment_Sync() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mode, container, false);
        toolbar=(Toolbar)v.findViewById(R.id.appbar);
        toolbar.setTitle("Sync");
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<SyncModel> syncs=new ArrayList<>();
        if(UserInfo.getInstance(getActivity()).getGroupstatus().equals("1")) {
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"dashboard",""), false, DASHBOARD));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"brands",""), false, BRANDS));
           // syncs.add(new SyncModel(URLConstants.GET_MYBRANDS_URL, false, MYBRANDS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"mycatalogs",""), false, MYCATALOGS));
            syncs.add(new SyncModel( URLConstants.companyUrl(getActivity(),"state",""), false, STATES));
            syncs.add(new SyncModel(URLConstants.RECIEVED_CAT_APP,false,SHAREDWITHME));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"selections_expand_false","")+"?type=my", false, MYSELECTIONS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"category","")+"?parent=10", false, CATEGORIES));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"grouptype",""), false, GROUPTYPES));
          //  syncs.add(new SyncModel(URLConstants.BUYERS, false, "Buyers"));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyers_approved",""), false, APPROVEDBUYERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyers_pending",""), false, PENDINGBUYERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyers_rejected",""), false, REJECTBUYERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"sellers_approved",""), false, APPROVEDSUPPLIERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"sellers_pending",""), false,PENDINGSUPPLIERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"sellers_rejected",""), false, REJECTSUPPLIERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyergroups",""), false, BUYERGROUPS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"purchaseorder",""), false, PURCHASEORDERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"salesorder",""), false, SALESORDERS));
            // syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"contacts_onwishbook",""),false,"Contacts"));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"mysent_catalog",""), false, SHAREDBYME));
            syncs.add(new SyncModel("", false, "Download Images"));
        }
        else{
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"brands",""), false, BRANDS));
          //  syncs.add(new SyncModel(URLConstants.GET_MYBRANDS_URL, false, MYBRANDS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"mycatalogs",""), false,MYCATALOGS));
            syncs.add(new SyncModel(URLConstants.RECIEVED_CAT_APP,false,SHAREDWITHME));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyers_approved",""), false, APPROVEDBUYERS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"buyers_pending",""), false, PENDINGBUYERS));
           // syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"grouptype",""), false, GROUPTYPES));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"state",""), false, STATES));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"selections_expand_false","")+"?type=my", false, MYSELECTIONS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"category","")+"?parent=10", false, CATEGORIES));
            syncs.add(new SyncModel(URLConstants.userUrl(getActivity(),"meetings",""), false, MEETINGS));
            syncs.add(new SyncModel(URLConstants.companyUrl(getActivity(),"salesorder",""), false, SALESORDERS));
            syncs.add(new SyncModel(URLConstants.userUrl(getActivity(),"meetingsreport",""), false, MEETINGREPORTS));
            syncs.add(new SyncModel("", false, "Download Images"));
        }
        syncAdapter=new SyncAdapter((AppCompatActivity) getActivity(),syncs);
        mRecyclerView.setAdapter(syncAdapter);
        fabsync=(FloatingActionButton)v.findViewById(R.id.fabaddbuyer);
        fabsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncAdapter.sync("false");
            }
        });
        toolbar.setVisibility(View.GONE);
        return v;
    }




}
