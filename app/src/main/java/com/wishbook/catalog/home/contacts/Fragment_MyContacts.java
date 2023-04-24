package com.wishbook.catalog.home.contacts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.MyContactAdapter;
import com.wishbook.catalog.commonmodels.Contact;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UploadContactsModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.dbmodel.ResponseCache;
import com.wishbook.catalog.commonmodels.postpatchmodels.CommmonFilterOptionModel;
import com.wishbook.catalog.home.contacts.add.Fragment_Invite;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



public class Fragment_MyContacts extends GATrackedFragment {

    private View v;
    private RecyclerView recyclerView;
    private MyContactAdapter contactAdapter;
    private String name;
    private List<Contact> contactList = new ArrayList<>();
    private TextView emptytext;

    private GetAllContacts gac;
    private ParseAllContactsLocally parseAsyncContacts;
    private FilterParseContacts filter;
    private RemoveDuplicateContacts removeDuplicateContacts;
    private LinearLayout linear_no_permission;
    private TextView btn_enable_permission;

    String type;


    private AppCompatButton btn_invite;
    private FloatingActionButton refresh;
    SearchView contactSearchView;

    public static LinearLayout select_all_container;
    private CheckBox select_all_contacts;

    //filter changes
    //for filter
    String filteredStatus="";
    public ArrayList<CommmonFilterOptionModel> filterDialogs = new ArrayList<CommmonFilterOptionModel>();


    public Fragment_MyContacts() {
        // Required empty public constructor
    }

    private List<MyContacts> wishbookcontactList = new ArrayList<>();
    Fragment_ContactsHolder frag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_importcontact, ga_container, true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        contactSearchView = (SearchView) v.findViewById(R.id.contact_search_view);
        contactSearchView.setIconifiedByDefault(false);
        emptytext = (TextView) v.findViewById(R.id.emptytext);
        btn_invite = (AppCompatButton) v.findViewById(R.id.btn_invite);
        linear_no_permission = v.findViewById(R.id.linear_no_permission);
        linear_no_permission.setVisibility(View.GONE);
        btn_enable_permission = v.findViewById(R.id.btn_enable_permission);
        refresh = (FloatingActionButton) v.findViewById(R.id.refresh);
        FastScroller fastScroller = (FastScroller) v.findViewById(R.id.fastscroll);

        if(getArguments()!=null){
            if(getArguments().getString("from")!=null){
                switch (getArguments().getString("from")){
                    case "allbuyers":
                        type="buyer";
                        break;
                    case "allsuppliers":
                        type="supplier";
                        break;
                }
            }
        }


        select_all_container = (LinearLayout) v.findViewById(R.id.select_all_container);
        select_all_contacts = (CheckBox) v.findViewById(R.id.select_all_contacts);

        fastScroller.setBubbleColor(ContextCompat.getColor(getContext(), R.color.color_primarysel));
        fastScroller.setHandleColor(ContextCompat.getColor(getContext(), R.color.color_primarysel));
        fastScroller.setBubbleTextAppearance(R.style.StyledScrollerTextAppearance);
        contactAdapter = new MyContactAdapter(getActivity(), contactList , getActivity().getSupportFragmentManager(),type);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactAdapter);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        contactSearchView.setVisibility(View.GONE);



        //getting Parent searchview
        frag = ((Fragment_ContactsHolder) this.getParentFragment());
        frag.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (contactList.size() > 0 && getUserVisibleHint() && getContext() != null) {
                   /* List<Contact> filteredModelList = filter(contactList, newText);
                    contactAdapter.setData(filteredModelList);*/
                    filter = new FilterParseContacts(getContext(), newText,false);
                    filter.execute(null, null);

                }
                return true;
            }
        });


        //filter changes
        //filterOptions
        filterDialogs.add(new CommmonFilterOptionModel(1,"Not in your Network",false,"in_network"));
        filterDialogs.add(new CommmonFilterOptionModel(2,"Not on Wishbook",false,"not_on_wishbook"));

        //restting filter icon
        frag.ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.color_primarysel));
        frag.ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_filter_white));
        frag.ic_filter.setEnabled(true);

        frag.btn_invite.setVisibility(View.VISIBLE);
        //hide Invite Button
        frag.fab_invite.setVisibility(View.INVISIBLE);

        //filter changes
        frag.ic_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContactsCommonFilterDialog commonFilterDialog = new ContactsCommonFilterDialog();
                commonFilterDialog.setList(filterDialogs);
                commonFilterDialog.setListener(new ContactsCommonFilterDialog.FilterDismissListener() {
                    @Override
                    public void selectedIDs(ArrayList<Integer> ids) {
                        filteredStatus="";
                        if(ids!=null && ids.size()>0) {

                            //changing filter icon
                            frag.ic_filter.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_edge_less_padding));
                            frag.ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_filter_filled));


                            //setting all ids to current data
                            for (Integer id : ids) {
                                filteredStatus = (filteredStatus+","+filterDialogs.get(id-1).getRequest_filter_name());
                                filterDialogs.get(id - 1).setSelected(true);
                            //    Toast.makeText(getContext(), filterDialogs.get(id - 1).getName(), Toast.LENGTH_SHORT).show();
                            }

                            //making filter status proper
                            if(filteredStatus.length()>0) {
                                filteredStatus = filteredStatus.substring(1);
                            }

                            //get Buyers
                           // getBuyersList(searchText);
                            filter = new FilterParseContacts(getContext(), filteredStatus,true);
                            filter.execute(null, null);

                        }else{

                            //restting filter icon
                            frag.ic_filter.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.color_primarysel));
                            frag.ic_filter.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_filter_white));


                            for(int i=0;i<filterDialogs.size();i++){
                                filterDialogs.get(i).setSelected(false);
                            }

                            //get Buyers
                           // getBuyersList(searchText);
                            filter = new FilterParseContacts(getContext(), filteredStatus,true);
                            filter.execute(null, null);
                        }
                    }
                });
                commonFilterDialog.show(getChildFragmentManager(),"filter_dialog");
            }
        });


        select_all_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_all_contacts.isChecked()) {
                    select_all_contacts.setChecked(false);
                } else {
                    select_all_contacts.setChecked(true);
                }
            }
        });

        select_all_contacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    contactAdapter.selectAllContacts();
                } else {
                    contactAdapter.removeSelectAllContacts();
                    select_all_container.setVisibility(View.GONE);
                }
            }
        });

        frag.btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserInfo.getInstance(getActivity()).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(getActivity(),"My Contacts");
                    return;
                }
                // StaticFunctions.selectedContacts = contactAdapter.getAllSelectedContacts();
                String typeBuyerSupplier="";
                if (StaticFunctions.selectedContacts.size() > 0) {
                    if(getArguments()!=null){
                        if(getArguments().getString("from")!=null){
                            switch (getArguments().getString("from")){
                                case "allbuyers":
                                    typeBuyerSupplier="buyer";
                                    break;
                                case "allsuppliers":
                                    typeBuyerSupplier="supplier";
                                    break;
                            }
                        }
                    }

                    sendUserInviteAnalytics(typeBuyerSupplier);

                    Fragment_Invite invite = new Fragment_Invite();
                    Bundle bundle = new Bundle();
                    bundle.putString("type",typeBuyerSupplier);
                    invite.setArguments(bundle);
                    invite.setListener(new Fragment_Invite.SuccessListener() {
                        @Override
                        public void OnSuccess() {
                            frag.searchView.setQuery("", false);
                            frag.searchView.clearFocus();
                            StaticFunctions.selectedContacts.clear();
                            if(getContext()!=null) {
                                contactAdapter.removeSelectAllContacts();
                                select_all_container.setVisibility(View.GONE);
                                frag.subtabs.getTabAt(1).select();
                            }
                        }

                        @Override
                        public void OnCancel() {

                        }
                    });
                    invite.show(getActivity().getSupportFragmentManager(), "invite");
                } else {
                    Toast.makeText(getActivity(), "Please select atleast one contact", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(UserInfo.getInstance(getActivity()).getContactStatus().equals("fetched")){
            //fecth from usercontacts
            if(UserInfo.getInstance(getActivity()).getWishBookContactStatus().equals("init")){
                // call onwishbook contacts
                parseAsyncContacts = new ParseAllContactsLocally(getContext());
                parseAsyncContacts.execute();
            } else if(UserInfo.getInstance(getActivity()).getWishBookContactStatus().equals("fetched")){
                // fetch store wishbook_contacts
                String cacheContacts = getCacheifExists("wishBook_contacts");
                if(cacheContacts != null) {
                    Log.i("Contact", "onCreateView: Else Cache Exist");
                    emptytext.setVisibility(View.INVISIBLE);
                    Gson gson = new Gson();
                    Type type1 = new TypeToken<List<Contact>>() {
                    }.getType();
                    contactList = gson.fromJson(cacheContacts, type1);
                    //  cacheContacts

                    contactAdapter = new MyContactAdapter(getActivity(), contactList, getActivity().getSupportFragmentManager(), type);
                    mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(contactAdapter);
                    fastScroller.setRecyclerView(recyclerView);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
                } else {
                    Log.i("Contact", "onCreateView: wishbook contact fetched but not contacts");
                }
            } else {
                // wishbook contacts error state
                Log.i("Contact", "onCreateView: wishbook contact fetch state");
            }
        }
        else if(UserInfo.getInstance(getActivity()).getContactStatus().equals("fetching")){
            //show progress
            emptytext.setVisibility(View.VISIBLE);

        }

        else if(UserInfo.getInstance(getActivity()).getContactStatus().equals("init")){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                    frag.btn_invite.setVisibility(View.INVISIBLE);
                    linear_no_permission.setVisibility(View.VISIBLE);
                } else {
                gac = new GetAllContacts(getContext());
                gac.execute();
            }

        }
        else {
            //handler error
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
              /*  ActivityCompat.requestPermissions((Activity) getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS}, 100);*/
            } else {
                gac = new GetAllContacts(getContext());
                gac.execute();
            }



        }


        /*if (UserInfo.getInstance(getActivity()).getcontacts().equals("") && !Application_Singleton.contactPermissionDenied) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i("Contact", "Contact Not Granted");
              *//*  ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.READ_CONTACTS}, 1);*//*
            } else {
                Log.i("Contact", "GAC Execute");
                gac = new GetAllContacts(getContext());
                gac.execute();
            }
        } else {
            if(Application_Singleton.isOnWishBookContactFetch) {
                String cacheContacts = getCacheifExists("wishBook_contacts");
                if(cacheContacts != null) {
                    Log.i("Contact", "onCreateView: Else Cache Exist");
                    emptytext.setVisibility(View.INVISIBLE);
                    Gson gson = new Gson();
                    Type type1 = new TypeToken<List<Contact>>() {
                    }.getType();
                    contactList = gson.fromJson(cacheContacts, type1);
                    //  cacheContacts

                    contactAdapter = new MyContactAdapter(getActivity(), contactList , getActivity().getSupportFragmentManager(),type);
                    mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(contactAdapter);
                    fastScroller.setRecyclerView(recyclerView);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
                } else {
                    Log.i("Contact", "ParseAllContact 2");
                    parseAsyncContacts = new ParseAllContactsLocally(getContext());
                    parseAsyncContacts.execute();
                }
            } else {
                Log.i("Contact", "ParseAllContact 1");
                parseAsyncContacts = new ParseAllContactsLocally(getContext());
                parseAsyncContacts.execute();
            }



           *//* Type type = new TypeToken<List<Contact>>() {
            }.getType();
            List<Contact> contactList1 = new Gson().fromJson(UserInfo.getInstance(getActivity()).getcontacts(), type);
            contactList.addAll(contactList1);
            // contactSearchView.setVisibility(View.VISIBLE);

            getWishbooksContact(getContext());*//*
        }*/
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                   /*  ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.READ_CONTACTS}, 1);*/
                } else {
                    gac = new GetAllContacts(getContext());
                    gac.execute();
                }
            }
        });
        btn_enable_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    Log.e("TAG", "onClick: ELSE=====>" );
                    // No explanation needed, we can request the permission.
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS},
                            100);
                } else {
                    Log.e("TAG", "onClick: =====>" );
                    Snackbar snackbar = Snackbar
                            .make(getView(), "Please Enable 'READ CONTACT' Permission from setting", Snackbar.LENGTH_LONG)
                            .setAction("SETTING", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    View snackBarView = snackbar.getView();
                    snackBarView.setAlpha(0.9f);
                    snackbar.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {

                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                        }
                    });

                    snackbar.show();
                }
            }
        });
        return v;
    }


    private List<Contact> filter(List<Contact> models, String query) {
        query = query.toLowerCase();
        List<Contact> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
            return filteredModelList;
        }

        /*for(Iterator<Contact> it = models.iterator(); it.hasNext();) {
            Contact s = it.next();
            String text = s.getContactName().toLowerCase();
            String numbertext = s.getContactNumber().toLowerCase();
            String regex = "\\d+";
            if(!query.matches(regex)) {
                if (text.contains(query)) {
                    filteredModelList.add(s);
                }
            }
            else if(!numbertext.matches(""))
            {
                if (numbertext.contains(query)) {
                    filteredModelList.add(s);
                }
            }

        }*/
        for (Contact model : models) {


            String text = model.getContactName().toLowerCase();
            String numbertext = model.getContactNumber().toLowerCase();
            String regex = "\\d+";
            if (!query.matches(regex)) {
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            } else if (!numbertext.matches("")) {
                if (numbertext.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }

        return filteredModelList;
    }

    private List<Contact> filterStatus(List<Contact> models, String status) {
        List<Contact> filteredModelList = new ArrayList<>();
        if(!status.equals("")) {
            String[] filter = status.split(",");
            if(filter.length>=2){
                filteredModelList.addAll(models);
                return filteredModelList;
            }else if(status.equals("not_on_wishbook")){
                for(Contact model : models){
                    if(!model.isWishbookContact){
                        filteredModelList.add(model);
                    }
                }
                return filteredModelList;
            }else if(status.equals("in_network")){
                for(Contact model : models){
                    if(model.isWishbookContact){
                        filteredModelList.add(model);
                    }
                }
                return filteredModelList;
            }


            return filteredModelList;
        }else{
            filteredModelList.addAll(models);
            return filteredModelList;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("MyCon", "onRequestPermissionsResult My Contacts: "+requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100) {
            if(grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     // Read Contact Permission Enable
                    linear_no_permission.setVisibility(View.GONE);
                    frag.btn_invite.setVisibility(View.VISIBLE);
                    gac = new GetAllContacts(getContext());
                    gac.execute();
                }
            }
        }
    }

    private class GetAllContacts extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public GetAllContacts(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptytext.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Contact :", "Getting Contacts from Phone");
            rtt(mContext);
            if (contactList.size() > 0) {
                Collections.sort(contactList, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact lhs, Contact rhs) {
                        return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("Contact :", "Contacts Arrived from Phone");
            emptytext.setVisibility(View.INVISIBLE);
            // contactSearchView.setVisibility(View.VISIBLE);
            //contactAdapter.notifyDataSetChanged();

           /* if (getUserVisibleHint() && getContext() != null) {
                //setting tutorial
                StaticFunctions.checkAndShowTutorial(getContext(), "mycontacts_tab_tut_shown", ((ViewGroup) Fragment_ContactsHolder.subtabs.getChildAt(0)).getChildAt(0), getResources().getString(R.string.mycontacts_tab_tutorial_text), "bottom");
            }*/
            Log.d("Contact :", "Getting Onwishbook from Server");
            getWishbooksContact(mContext);
        }
    }


    private class ParseAllContactsLocally extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;

        public ParseAllContactsLocally(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptytext.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("Contact :", "Getting Contacts from Local JSON");

            try {
                Type type = new TypeToken<List<Contact>>() {
                }.getType();
                if (getUserVisibleHint() && getActivity()!=null && mContext!=null) {
                    List<Contact> contactList1 = new Gson().fromJson(UserInfo.getInstance(mContext).getcontacts(), type);
                    Log.i("Contact", "doInBackground: ParseContacts ==> "+contactList1.size());
                    contactList.addAll(contactList1);
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d("Contact :", "Contacts arrived from Local JSON");

            if(result){
                emptytext.setVisibility(View.INVISIBLE);
                Log.d("Contact :", "Getting onwishbook from Server");
               // UserInfo.getInstance(getActivity()).setWishBookContactStatus("fetching");
                getWishbooksContact(mContext);
            }
        }
    }

    private class RemoveDuplicateContacts extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public RemoveDuplicateContacts(Context context) {
            Log.d("Contact :", "Remove Background Create");
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptytext.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Contact :", "Remove Background Start");
            if(wishbookcontactList!=null && wishbookcontactList.size()>0) {
                for (int j = (wishbookcontactList.size() - 1); j >= 0; j--) {
                    if (wishbookcontactList.get(j).getConnected_as().equals("")) {
                        checkRemoveContactAlreadyExist(wishbookcontactList.get(j).getPhone());
                        Contact contact = new Contact(wishbookcontactList.get(j).getName(), wishbookcontactList.get(j).getPhone(), false, true);
                        contact.setCompanyName(wishbookcontactList.get(j).getCompany_name());
                        contactList.add(0,contact);
                    }else{
                        checkRemoveContactAlreadyExist(wishbookcontactList.get(j).getPhone());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("Contact :", "Remove Background Post");
            if(getActivity()!=null){
                emptytext.setVisibility(View.INVISIBLE);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Contact>>() {}.getType();
                String contacts = gson.toJson(contactList, type);
                storePhoneContact(contacts);
                UserInfo.getInstance(getActivity()).setWishBookContactStatus("fetched");
                Application_Singleton.isOnWishBookContactFetch = true;
                contactAdapter.notifyDataSetChanged();
            }

        }
    }

    private class FilterParseContacts extends AsyncTask<List<Contact>, Void, List<Contact>> {

        private Context mContext;
        private String searchText;
        private Boolean isStatusFilter;

        public FilterParseContacts(Context context, String newtext,Boolean isStatusFilter) {
            mContext = context;
            this.searchText = newtext;
            this.isStatusFilter=isStatusFilter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptytext.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Contact> doInBackground(List<Contact>... params) {
            List<Contact> filteredModelList = new ArrayList<>();

            //search filtering
            if(!isStatusFilter) {
                if (contactList!=null && contactList.size() > 0 && getUserVisibleHint() && getContext() != null) {
                    filteredModelList = filter(contactList, searchText);
                }
            }
            //if filter through status
            if(isStatusFilter){
                if (contactList!=null && contactList.size() > 0 && getUserVisibleHint() && getContext() != null) {
                    filteredModelList = filterStatus(contactList, searchText);
                }
            }
            return filteredModelList;
        }

        @Override
        protected void onPostExecute(List<Contact> list) {
            super.onPostExecute(list);
            emptytext.setVisibility(View.INVISIBLE);
            contactAdapter.setData(list);
        }
    }


    private void rtt(Context mContext) {

        contactList.clear();
        HashSet<String> nums = new HashSet<>();
        if (mContext != null) {
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if(phones!=null){
                UserInfo.getInstance(mContext).setContactStatus("fetching");
                try{
                    while (phones.moveToNext()) {
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").trim();
                        if (phoneNumber.length() >= 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                        Contact ct = new Contact(name, phoneNumber, false, false);

                        String phoneNumber1 = phoneNumber.replaceAll("\\D+", "");
                        if (phoneNumber1.length() >= 10) {
                            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                            if (!nums.contains(phoneNumbertrim)) {
                                contactList.add(ct);
                            }
                            nums.add(phoneNumbertrim);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                phones.close();
            }

        }
        if (contactList.size() > 0) {
            Collections.sort(contactList, new Comparator<Contact>() {

                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                }
            });
        }
        if (mContext != null) {
            UserInfo.getInstance(mContext).setContacts(new Gson().toJson(contactList));
            UserInfo.getInstance(mContext).setContactStatus("fetched");
        }

    }

    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    public void getWishbooksContact(final Context context) {
        //showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        ArrayList<NameValues> cons = new ArrayList<>();
        UploadContactsModel uploadContactsModel = new UploadContactsModel(cons);
        Log.d("Contact :", "Making model contacts for wishbook");
        for (Contact contact : contactList) {
            cons.add(new NameValues(contact.getContactName(), contact.getContactNumber()));
        }
        uploadContactsModel.setContacts(cons);
        Log.d("Contact :", "Uploading model contacts for wishbook");
        if (context != null) {
            HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(context, "contacts_onwishbook", ""), new Gson().fromJson(new Gson().toJson(uploadContactsModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
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
                                Log.v("sync response", response);
                                Log.d("Contact :", "Response Onwishbook from Server");
                                if (getUserVisibleHint() && getContext() != null) {

                                    HashSet<String> nums = new HashSet<>();
                                    MyContacts[] myContacts = Application_Singleton.gson.fromJson(response, MyContacts[].class);
                                    wishbookcontactList.clear();

                                    for (MyContacts myContacts1 : myContacts) {
                                        if (myContacts1.getPhone().length() >= 10) {
                                            String phoneNumber1 = myContacts1.getPhone().replaceAll("\\D+", "");
                                            if (phoneNumber1.length() >= 10) {
                                                String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                                                if (!nums.contains(phoneNumbertrim)) {
                                                    wishbookcontactList.add(myContacts1);
                                                }
                                                nums.add(phoneNumbertrim);
                                            }
                                        }
                                    }

                               /* Collections.sort(wishbookcontactList, new Comparator<MyContacts>() {
                                    @Override
                                    public int compare(MyContacts lhs, MyContacts rhs) {
                                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                                    }
                                });*/
                                    if (wishbookcontactList.size() > 1) {
                                        UserInfo.getInstance(getActivity()).setwishContacts(new Gson().toJson(wishbookcontactList));
                                    }
                                    Log.d("Contact :", "After Checking Wishbook Contact");
                                    removeDuplicateContacts = new RemoveDuplicateContacts(context);
                                    removeDuplicateContacts.execute();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                                hideProgress();
                        }
                    }

            );
        }


    }

    private void checkRemoveContactAlreadyExist(String phone) {
        if(contactList!=null && contactList.size()>0) {
            for (int i = 0; i < contactList.size(); i++) {
                if (contactList.get(i).getContactNumber().equals(phone))
                    contactList.remove(i);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if(gac!=null){
            gac.cancel(true);
        }
        if(parseAsyncContacts!=null){
            parseAsyncContacts.cancel(true);
        }
        if(filter!=null){
            filter.cancel(true);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        StaticFunctions.selectedContacts.clear();
    }

    public void storePhoneContact(String result) {
        ResponseCache responseCache = new ResponseCache();
        responseCache.setKey("wishBook_contacts");
        responseCache.setResponse(result);

        PrefDatabaseUtils.setPrefWishbookContacts(getActivity(),new Gson().toJson(responseCache));
    }

    private String getCacheifExists(String md5) {

        ResponseCache res = new Gson().fromJson(PrefDatabaseUtils.getPrefWishbookContacts(getActivity()), new TypeToken<ResponseCache>() {
        }.getType());
        if (res != null) {
            return res.getResponse();
        }
        return null;
    }

    public void sendUserInviteAnalytics(String type) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Users_Invite");
        ArrayList<NameValues> nameValuesArrayList = new ArrayList<>();
        HashMap<String, String> prop = new HashMap<>();
        for (NameValues temp :
                StaticFunctions.selectedContacts) {
            NameValues t = new NameValues(temp.getName(), temp.getPhone());
            t.setType_contacts(type);
            nameValuesArrayList.add(t);
        }
        prop.put("list_of_contacts",Application_Singleton.gson.toJson(nameValuesArrayList));


        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }
}
