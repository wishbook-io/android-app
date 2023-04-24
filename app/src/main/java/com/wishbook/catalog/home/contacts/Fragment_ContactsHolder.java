package com.wishbook.catalog.home.contacts;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.Activity_Home;

import java.util.ArrayList;
import java.util.List;

public class Fragment_ContactsHolder extends GATrackedFragment {

    private View v;
    private TabLayout tabs;
    // public static CustomViewPager viewPager;
    private Toolbar toolbar;
    public static TabLayout subtabs;

    public SearchView searchView;
    public ImageView search_icon, ic_filter;
    public FloatingActionButton fab_invite;
    public AppCompatButton btn_invite;
    public LinearLayout linear_contact_filter_bar;

    int count = 0;


    public Fragment_ContactsHolder() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Activity_Home.support_chat_fab != null) {
            Activity_Home.support_chat_fab.setVisibility(View.VISIBLE);
            Activity_Home.support_chat_fab.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TAG", "onCreateView: Contact Holder");

        v = inflater.inflate(R.layout.new_layout_contacts_holder, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Contacts");
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        subtabs = (TabLayout) v.findViewById(R.id.subtabs);
        searchView = (SearchView) v.findViewById(R.id.search_view);
        search_icon = (ImageView) v.findViewById(R.id.search_icon);
        ic_filter = (ImageView) v.findViewById(R.id.ic_filter);
        fab_invite = (FloatingActionButton) v.findViewById(R.id.fab_invite);
        btn_invite = (AppCompatButton) v.findViewById(R.id.btn_invite);
        linear_contact_filter_bar = v.findViewById(R.id.linear_contact_filter_bar);
        setupViewPager(tabs, subtabs);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchView.getVisibility() == View.GONE) {
                    searchView.clearFocus();
                    searchView.setQuery("", false);
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                }
            }
        });

        return v;
    }


    private void setupViewPager(final TabLayout tabs, final TabLayout subtabs) {

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                    switch (tab.getPosition()) {
                        case 0:
                            Tab allbuyer = subtabs.newTab().setText("ALL").setTag("allbuyer");
                            Tab approvedbuyer = subtabs.newTab().setText("APPROVED").setTag("approvedbuyer");
                            subtabs.removeAllTabs();
                            subtabs.addTab(allbuyer);
                            subtabs.addTab(approvedbuyer);
                            try {
                                subtabs.getTabAt(UserInfo.getInstance(getActivity()).getlastsubtabselected()).select();
                            } catch (Exception e) {
                                subtabs.getTabAt(1).select();

                            }
                            break;
                    }
                } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                    switch (tab.getPosition()) {
                        case 0:
                            Tab allbuyer = subtabs.newTab().setText("ALL").setTag("allbuyer");
                            Tab approvedbuyer = subtabs.newTab().setText("MY NETWORK").setTag("approvedbuyer");
                            //  Tab enquirybuyer = subtabs.newTab().setText("Enquiry").setTag("enquirybuyer");
                            subtabs.removeAllTabs();
                            //  subtabs.addTab(enquirybuyer);
                            subtabs.addTab(allbuyer);
                            subtabs.addTab(approvedbuyer);
                            try {
                                subtabs.getTabAt(UserInfo.getInstance(getActivity()).getlastsubtabselected()).select();
                            } catch (Exception e) {
                                subtabs.getTabAt(1).select();

                            }

                            break;

                    }
                } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    switch (tab.getPosition()) {
                        case 0:
                            Tab allsupplier = subtabs.newTab().setText("ALL").setTag("allsupplier");
                            Tab approvedsupplier = subtabs.newTab().setText("MY NETWORK").setTag("approvedsupplier");
                            //   Tab enquirysupplier = subtabs.newTab().setText("Enquiry").setTag("enquirysupplier");
                            subtabs.removeAllTabs();
                            //   subtabs.addTab(enquirysupplier);
                            subtabs.addTab(allsupplier);
                            subtabs.addTab(approvedsupplier);
                            try {
                                subtabs.getTabAt(UserInfo.getInstance(getActivity()).getlastsubtabselected()).select();
                            } catch (Exception e) {
                                subtabs.getTabAt(1).select();

                            }


                            break;

                    }
                } else {
                    switch (tab.getPosition()) {
                        case 0:
                            Tab allbuyer = subtabs.newTab().setText("ALL").setTag("allbuyer");
                            Tab approvedbuyer = subtabs.newTab().setText("MY NETWORK").setTag("approvedbuyer");
                            //  Tab enquirybuyer = subtabs.newTab().setText("Enquiry").setTag("enquirybuyer");
                            subtabs.removeAllTabs();
                            //  subtabs.addTab(enquirybuyer);
                            subtabs.addTab(allbuyer);
                            subtabs.addTab(approvedbuyer);
                            try {
                                subtabs.getTabAt(UserInfo.getInstance(getActivity()).getlastsubtabselected()).select();
                            } catch (Exception e) {
                                subtabs.getTabAt(1).select();

                            }
                            break;
                        case 1:
                            Tab allsupplier = subtabs.newTab().setText("ALL").setTag("allsupplier");
                            Tab approvedsupplier = subtabs.newTab().setText("MY NETWORK").setTag("approvedsupplier");
                            subtabs.removeAllTabs();
                            subtabs.addTab(allsupplier);
                            subtabs.addTab(approvedsupplier);
                            try {
                                subtabs.getTabAt(UserInfo.getInstance(getActivity()).getlastsubtabselected()).select();
                            } catch (Exception e) {
                                subtabs.getTabAt(1).select();

                            }
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });


        subtabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                searchView.setVisibility(View.GONE);

                Log.v("onCreate", "onTabSelected " + tab + " " + tab.getTag());
                if (tab.getTag().equals("received_enquiry")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_BuyersEnquiry(), "enquiry_received").commit();
                }
                if (tab.getTag().equals("sent_enquiry")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_SuppliersEnquiry(), "enquiry_sent").commit();
                }
                if (tab.getTag().equals("allbuyer")) {
                    Fragment_MyContacts fragment_myContacts = new Fragment_MyContacts();
                    Bundle allBuyers = new Bundle();
                    allBuyers.putString("from", "allbuyers");
                    fragment_myContacts.setArguments(allBuyers);
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, fragment_myContacts, "all_contacts").commit();
                }
                if (tab.getTag().equals("approvedbuyer")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_BuyersApproved(), "buyers_approved").commit();
                }
                if (tab.getTag().equals("allsupplier")) {
                    Fragment_MyContacts fragment_myContacts = new Fragment_MyContacts();
                    Bundle allsupplier = new Bundle();
                    allsupplier.putString("from", "allsuppliers");
                    fragment_myContacts.setArguments(allsupplier);
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, fragment_myContacts, "all_contacts").commit();
                }
                if (tab.getTag().equals("approvedsupplier")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_SuppliersApproved(), "supplier_approved").commit();
                }
                if (tab.getTag().equals("enquirysupplier")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_SuppliersEnquiry(), "enquiry_sent").commit();
                }
                if (tab.getTag().equals("enquirybuyer")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.containercontacts, new Fragment_BuyersEnquiry(), "enquiry_received").commit();
                }

            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        //sales
        tabs.removeAllTabs();
        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("1")) {


            if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {

                Tab buyertab = tabs.newTab().setText("BUYERS").setTag("buyers");
                tabs.addTab(buyertab);


            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {

                Tab suppliertab = tabs.newTab().setText("SUPPLIERS").setTag("suppliers");
                tabs.addTab(suppliertab);
            } else {

                Tab buyertab = tabs.newTab().setText("BUYERS").setTag("buyers");
                tabs.addTab(buyertab);

                Tab suppliertab = tabs.newTab().setText("SUPPLIERS").setTag("suppliers");
                tabs.addTab(suppliertab);

            }


            try {
                tabs.getTabAt(UserInfo.getInstance(getActivity()).getlasttabselected()).select();
            } catch (Exception e) {
                tabs.getTabAt(0).select();
            }


        } else {
            Tab buyertab = tabs.newTab().setText("Buyers").setTag("buyers");
            tabs.addTab(buyertab);


            try {
                tabs.getTabAt(UserInfo.getInstance(getActivity()).getlasttabselected()).select();
            } catch (Exception e) {
                tabs.getTabAt(0).select();
            }


        }


        //setting inner tab from Home Landing page;
        // UserInfo.getInstance(getActivity()).settabselected(Application_Singleton.selectedInnerTabContacts);


    }

    @Override
    public void onPause() {
        super.onPause();
        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("1")) {
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("received_enquiry")) {
                UserInfo.getInstance(getActivity()).settabselected(0);
                UserInfo.getInstance(getActivity()).selastsubtabselected(0);
            }
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("sent_enquiry")) {
                UserInfo.getInstance(getActivity()).settabselected(0);
                UserInfo.getInstance(getActivity()).selastsubtabselected(1);
            }
           /* if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("enquirybuyer")) {
                UserInfo.getInstance(getActivity()).settabselected(1);
                UserInfo.getInstance(getActivity()).selastsubtabselected(0);
            }*/
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("allbuyer")) {
                UserInfo.getInstance(getActivity()).settabselected(1);
                UserInfo.getInstance(getActivity()).selastsubtabselected(0);
            }
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("approvedbuyer")) {
                UserInfo.getInstance(getActivity()).settabselected(1);
                UserInfo.getInstance(getActivity()).selastsubtabselected(1);
            }
            /*if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("enquirysupplier")) {
                UserInfo.getInstance(getActivity()).settabselected(2);
                UserInfo.getInstance(getActivity()).selastsubtabselected(0);
            }*/
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("allsupplier")) {
                UserInfo.getInstance(getActivity()).settabselected(2);
                UserInfo.getInstance(getActivity()).selastsubtabselected(0);
            }
            if (subtabs.getTabAt(subtabs.getSelectedTabPosition()).getTag().equals("approvedsupplier")) {
                UserInfo.getInstance(getActivity()).settabselected(2);
                UserInfo.getInstance(getActivity()).selastsubtabselected(1);
            }
        }
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<GATrackedFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(GATrackedFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public GATrackedFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}

