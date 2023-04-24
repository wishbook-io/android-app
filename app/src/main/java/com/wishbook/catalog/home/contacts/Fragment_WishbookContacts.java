package com.wishbook.catalog.home.contacts;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.WishbookContactsAdapter;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UploadContactsModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.contacts.add.Fragment_Invite;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wishbook.catalog.home.contacts.Fragment_ContactsHolder.subtabs;

public class Fragment_WishbookContacts extends GATrackedFragment {

    private View v;
    private RecyclerView recyclerView;
    private WishbookContactsAdapter mAdapter;
    private String name;
    private List<MyContacts> contactList = new ArrayList<>();
    private TextView emptytext;
    private GetAllWishbookContacts gac;
    private AppCompatButton btn_invite;
    private List<MyContacts> wishbookcontactList = new ArrayList<>();
    private FloatingActionButton refresh;

    public Fragment_WishbookContacts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_importcontact, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        SearchView contactSearchView = (SearchView) v.findViewById(R.id.contact_search_view);
        contactSearchView.setIconifiedByDefault(false);
        emptytext = (TextView) v.findViewById(R.id.emptytext);
        btn_invite = (AppCompatButton) v.findViewById(R.id.btn_invite);
        //btn_invite.setVisibility(View.GONE);
        FastScroller fastScroller = (FastScroller) v.findViewById(R.id.fastscroll);
        fastScroller.setBubbleColor(R.color.color_primary);
        fastScroller.setHandleColor(R.color.color_primary);
        fastScroller.setBubbleTextAppearance(R.style.StyledScrollerTextAppearance);

        if (UserInfo.getInstance(getActivity()).getwishbookcontacts().equals("")) {
            gac = new GetAllWishbookContacts(getContext());
            gac.execute();
        } else {
            Type type = new TypeToken<List<MyContacts>>() {
            }.getType();

            List<MyContacts> contactList1 = new Gson().fromJson(UserInfo.getInstance(getActivity()).getwishbookcontacts(), type);
            wishbookcontactList.addAll(contactList1);
        }
        refresh = (FloatingActionButton) v.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishbookcontactList.clear();
                mAdapter = new WishbookContactsAdapter(getActivity(), wishbookcontactList);
                gac = new GetAllWishbookContacts(getContext());
                gac.execute();
            }
        });
        mAdapter = new WishbookContactsAdapter(getActivity(), wishbookcontactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fastScroller.setRecyclerView(recyclerView);
        contactSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<MyContacts> filteredModelList = filter(wishbookcontactList, newText);
                mAdapter.setData(filteredModelList);
                return true;
            }
        });

        if(getUserVisibleHint()) {
            //setting tutorial
            StaticFunctions.checkAndShowTutorial(getContext(), "onwishbook_tab_tut_shown", ((ViewGroup) subtabs.getChildAt(0)).getChildAt(1), getResources().getString(R.string.onwishbook_tab_tutorial_text), "bottom");
        }

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticFunctions.selectedContacts = mAdapter.getAllSelectedContacts();
                if (StaticFunctions.selectedContacts.size() > 0) {
                    new Fragment_Invite().show(getActivity().getSupportFragmentManager(), "invite");
                } else {
                    Toast.makeText(getActivity(), "Please select atleast one contact", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private List<MyContacts> filter(List<MyContacts> models, String query) {
        query = query.toLowerCase();
        final List<MyContacts> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
            return filteredModelList;
        }
        for (MyContacts model : models) {
            final String text = model.getName().toLowerCase();
            final String numbertext = model.getPhone().toLowerCase();
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


    private class GetAllWishbookContacts extends AsyncTask<Void, Void, Void> {

        Context mContext;

        public GetAllWishbookContacts(Context activity_home) {
            this.mContext= activity_home;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptytext.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            contactList.clear();
            if(Application_Singleton.reloadPhoneContacts) {
                contactList.addAll(StaticFunctions.reloadPhoneContacts(mContext));
                Application_Singleton.reloadPhoneContacts=false;
                Log.e("Contacts","Reloaded");
            }else{
                contactList.addAll(StaticFunctions.getContactsFromPhone(mContext));
                Log.e("Contacts ","Fetched From Local");
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            emptytext.setVisibility(View.INVISIBLE);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            ArrayList<NameValues> cons = new ArrayList<>();
            UploadContactsModel uploadContactsModel = new UploadContactsModel(cons);

            for (MyContacts contact : contactList) {
                cons.add(new NameValues(contact.getName(), contact.getPhone()));
            }
            uploadContactsModel.setContacts(cons);
            if(getActivity()!= null) {
                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(),"contacts_onwishbook",""), new Gson().fromJson(new Gson().toJson(uploadContactsModel), JsonObject.class), headers, true, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                Log.v("cached response", response);
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
                                    Log.v("sync response", response);
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
                                    Collections.sort(wishbookcontactList, new Comparator<MyContacts>() {
                                        @Override
                                        public int compare(MyContacts lhs, MyContacts rhs) {
                                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                                        }
                                    });
                                    if (wishbookcontactList.size() > 1) {
                                        UserInfo.getInstance(getActivity()).setwishContacts(new Gson().toJson(wishbookcontactList));
                                    }


                                    mAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {

                            }
                        }

                );
            }
            }
        }

    private void rtt() {
        contactList.clear();
        HashSet<String> nums = new HashSet<>();
        if(getActivity()!= null) {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            try{
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","").trim();
                    String phoneNumber1 = phoneNumber.replaceAll("\\D+", "");
                    if (phoneNumber1.length() >= 10) {
                        String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                        if (!nums.contains(phoneNumbertrim)) {
                            contactList.add(new MyContacts(phoneNumber, null, name, name, ""));
                        }
                        nums.add(phoneNumbertrim);
                    }

                }
            }
            catch (Exception e){

            }
            phones.close();
        }
        Collections.sort(contactList, new Comparator<MyContacts>() {

            @Override
            public int compare(MyContacts lhs, MyContacts rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

    }


    public static <T> void removeDuplicates(ArrayList<T> list) {
        int size = list.size();
        int out = 0;
        {
            final Set<T> encountered = new HashSet<T>();
            for (int in = 0; in < size; in++) {
                final T t = list.get(in);
                final boolean first = encountered.add(t);
                if (first) {
                    list.set(out++, t);
                }
            }
        }
        while (out < size) {
            list.remove(--size);
        }
    }

}
