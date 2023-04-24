package com.wishbook.catalog.pendingTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerFull;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.details.Fragment_Details_Address;
import com.wishbook.catalog.home.contacts.details.Fragment_Details_Orders;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

public class BuyerDetailFrag extends GATrackedFragment {

    private View v;
    private TabLayout tabLayout;
    private Response_meeting response_meeting;
    private Response_BuyerFull response_buyer=null;
    private  ViewPager viewPager;
    private  Boolean finishActivity=false;
    public  BuyerDetailFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("timerservice"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("time");
            String buyer = intent.getStringExtra("buyer");
            if (buyer.equals(Application_Singleton.navselectedBuyer.getId())) {
                ((TextView) v.findViewById(R.id.timertex)).setText(getDurationString(Integer.parseInt(message)));
                if (!Activity_Home.pref.getString("currentmeet", "na").equals("na")) {
                    response_meeting = Application_Singleton.gson.fromJson(Activity_Home.pref.getString("currentmeet", "na"), Response_meeting.class);
                    v.findViewById(R.id.createorderlay).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.createorder1).setVisibility(View.GONE);
                }


            } else {
                ((TextView) v.findViewById(R.id.timertex)).setText("");
                v.findViewById(R.id.createorderlay).setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.buyerdetail, ga_container, true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);

        getBuyer(Application_Singleton.navselectedBuyer.getId());

        if(getArguments()!=null){
            finishActivity = getArguments().getBoolean("fromBuyerDirectly",false);
        }


        //final BuyerdetailState buyerdetailState = new BuyerdetailState();
        //buyerdetailState.setUpToolbar((AppCompatActivity) getActivity(), toolbar);
        v.findViewById(R.id.createorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                Bundle bundle = new Bundle();
                bundle.putString("buyerselected", Application_Singleton.navselectedBuyer.getBuying_company_name());
                bundle.putString("buyer_selected_broker_id",Application_Singleton.navselectedBuyer.getBroker_company());
                bundle.putString("buyer_selected_company_id",Application_Singleton.navselectedBuyer.getBuying_company());
                BuyersList buyersList = new BuyersList(Application_Singleton.navselectedBuyer.getBuying_company(),Application_Singleton.navselectedBuyer.getBuying_company_name(),Application_Singleton.navselectedBuyer.getBroker_company());
                bundle.putSerializable("buyer",buyersList);
                createOrderFrag.setArguments(bundle);

                Application_Singleton.CONTAINER_TITLE=getResources().getString(R.string.new_sales_order);
                Application_Singleton.CONTAINERFRAG=createOrderFrag;
                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);


                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, createOrderFrag).addToBackStack("createorder").commit();
            }
        });



        v.findViewById(R.id.createorder1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                Bundle bundle = new Bundle();
                bundle.putString("buyerselected", Application_Singleton.navselectedBuyer.getBuying_company_name());
                bundle.putString("buyer_selected_broker_id",Application_Singleton.navselectedBuyer.getBroker_company());
                bundle.putString("buyer_selected_company_id",Application_Singleton.navselectedBuyer.getBuying_company());
                BuyersList buyersList = new BuyersList(Application_Singleton.navselectedBuyer.getBuying_company(),Application_Singleton.navselectedBuyer.getBuying_company_name(),Application_Singleton.navselectedBuyer.getBroker_company());
                bundle.putSerializable("buyer",buyersList);
                createOrderFrag.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE=getResources().getString(R.string.new_sales_order);
                Application_Singleton.CONTAINERFRAG=createOrderFrag;
                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);




            }
        });
        v.findViewById(R.id.choutbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", "" + response_meeting.getId());
              //  params.put("end_datetime", "" + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)).format(new Date()));
                params.put("end_datetime", "" + DateUtils.currentUTC());
                params.put("end_lat", "" + response_meeting.getStart_lat());
                params.put("end_long", "" + response_meeting.getStart_long());
                params.put("status", "done");

                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(),"meetings_with_id",response_meeting.getId()), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        // Response_meeting response_meeting=(Response_meeting)Application_Singleton.gson.fromJson(response,Response_meeting.class);
                        Activity_Home.pref.edit().putString("currentmeet","na").apply();
                        Activity_Home.pref.edit().putString("currentmeetbuyer","na").apply();

                        Application_Singleton.selectedBuyer.clear();

                        if(finishActivity){
                            getActivity().finish();
                        }else{
                            getActivity().getSupportFragmentManager().popBackStack("buyerdet", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }

                            /*NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.userUrl(getActivity(),"meetings",""), null, new NetworkManager.customCallBack() {
                                @Override
                                public void onCompleted(int status, String response) {
                                    if (status == NetworkManager.RESPONSESUCCESS) {
                                        getActivity().getSupportFragmentManager().popBackStack("buyerdet", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }
                                }
                            });*/

                    }


                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });
        return v;
    }

    private void getBuyer(String id) {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"buyers","")+ id + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                response_buyer = new Gson().fromJson(response, Response_BuyerFull.class);

                if (response_buyer != null) {
                    if (viewPager != null) {
                        setupViewPager(viewPager, response_buyer);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                }
                ((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuyingCompany().getName());
                ((TextView) v.findViewById(R.id.det_email)).setText("Email: " + response_buyer.getBuyingCompany().getEmail());
                ((TextView) v.findViewById(R.id.det_num)).setText("Phone: " + response_buyer.getBuyingCompany().getPhone_number());


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }
    private String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

   /* private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new Fragment_Details_Orders(), "Orders");
        adapter.addFragment(new Fragment_Details_Address(), "Address");

        viewPager.setAdapter(adapter);
    }
*/
    private void setupViewPager(ViewPager viewPager, final Response_BuyerFull response_buyer) {
        try {
            Adapter adapter = new Adapter(getChildFragmentManager());
            Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
            if (response_buyer != null && response_buyer.getBuyingCompany() != null && response_buyer.getBuyingCompany().getBuying_order() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("orders", new Gson().toJson(response_buyer.getBuyingCompany().getBuying_order()));
                bundle.putString("type", "buyer");
                bDet_orders.setArguments(bundle);
            }
            adapter.addFragment(bDet_orders, "Orders");
            Fragment_Details_Address bDet_address = new Fragment_Details_Address();
            if (response_buyer != null && response_buyer.getBuyingCompany() != null) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("company", new Gson().toJson(response_buyer.getBuyingCompany()));
                bDet_address.setArguments(bundle1);
            }
            adapter.addFragment(bDet_address, "Address");
            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
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

    @Override
    public void onResume() {
        super.onResume();
      //  getBuyer(Application_Singleton.navselectedBuyer.getId());
    }
}
