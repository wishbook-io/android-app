package com.wishbook.catalog.home.contacts.details;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brokers;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_grouptypes;
import com.wishbook.catalog.commonmodels.InviteContactsModel;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchBuyer;
import com.wishbook.catalog.commonmodels.postpatchmodels.RejectBuyer;
import com.wishbook.catalog.commonmodels.responses.Address;
import com.wishbook.catalog.commonmodels.responses.RequestAddSuppliers;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerFull;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_brokers;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.add.Fragment_Buyer_Rating;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.more.visits.Fragment_Visits;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orders.adapters.SalesOrderAdapterNew;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;


public class Fragment_BuyerDetails extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private Spinner spinner_brokers;
    private Spinner spinner_grouptypes;
    private EditText edit_paymentdur;
    private EditText edit_discount;
    private EditText edit_credit;
    private EditText edit_cash;
    private ImageView img_edit;
    private Button but_save;
    private Button but_cancel;
    private Button btn_reject;
    private Button btn_approve, btn_rating;
    private TextView broker_text;
    private AppCompatButton btn_order;
    private AppCompatButton btn_visits;
    private LinearLayout editcont;
    //private LinearLayout tabscont;
    //private ViewPager viewPager;
    private SimpleDraweeView profpic;
    private UserInfo userInfo;
    private boolean gps_enabled;
    private Response_BuyerFull response_buyer = null;

    //Broker
    private TextView txt_supplier_label, det_name;
    private EditText edit_brokerage;
    private LinearLayout linear_brokerage, linear_broker_user_container;
    private RelativeLayout select_broker_container;
    private FlexboxLayout flexbox_suppliers;
    private TextView btn_connect_supplier_broker;

    private FrameLayout frameRecyclerOrder;
    private RecyclerViewEmptySupport recyclerOrder;
    boolean isCatalogExpand, isOrderExpand, isAddCreditExpand;
    private ImageView arrow_img;
    private RelativeLayout relativeOrder;
    private CardView broker_details_card, broker_supplier_card, card_order_details;
    private CardView credit_details_card, set_discount_card, card_approve_reject;
    private RelativeLayout relative_add_credit;
    LinearLayout linear_add_credit_collapse, linear_add_credit_collapse1;
    private ImageView arrow_img2;
    private AppCompatButton btn_add_broker;


    private LinearLayout tabscont;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    String brokerId;


    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    CustomWatcher customWatcher[] = new CustomWatcher[4];



    public Fragment_BuyerDetails() {
        // Required empty public constructor
    }

    //todo
    // buyer and supplier details - instead of page, data is scrolling inside card holder/portlet
    //in catalogview order is not working
    //still on click of image catalog view is not coming
    //catalog name is not updating onchanging selected catalog
    //if any company doesn't have catalogs/selection then previously selected catalog should be cleared

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.details_buyer_new, ga_container, true);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        profpic = (SimpleDraweeView) v.findViewById(R.id.profpic);
        spinner_brokers = (Spinner) v.findViewById(R.id.spinner_brokers);
        editcont = (LinearLayout) v.findViewById(R.id.editcont);
        det_name = (TextView) v.findViewById(R.id.det_name);
        // tabscont = (LinearLayout) v.findViewById(R.id.tabscont);
        spinner_grouptypes = (Spinner) v.findViewById(R.id.spinner_grouptypes);
        edit_paymentdur = (EditText) v.findViewById(R.id.edit_paymentdur);
        edit_credit = (EditText) v.findViewById(R.id.edit_credit);
        edit_discount = (EditText) v.findViewById(R.id.edit_discount);
        edit_cash = (EditText) v.findViewById(R.id.edit_cash);
        but_save = (Button) v.findViewById(R.id.but_save);
        btn_reject = (Button) v.findViewById(R.id.btn_reject);
        btn_approve = (Button) v.findViewById(R.id.btn_approve);
        btn_rating = (Button) v.findViewById(R.id.btn_buyer_rating);
        // but_cancel = (Button) v.findViewById(R.id.but_cancel);
        btn_order = (AppCompatButton) v.findViewById(R.id.btn_create_order);
        btn_visits = (AppCompatButton) v.findViewById(R.id.btn_visits);
        broker_text = (TextView) v.findViewById(R.id.broker_text);
        // img_edit = (ImageView) v.findViewById(R.id.img_edit);
        //viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        arrow_img = (ImageView) v.findViewById(R.id.arrow_img);

        // Hide Permenatly
        v.findViewById(R.id.det_email).setVisibility(View.GONE);
        // v.findViewById(R.id.address).setVisibility(View.GONE);


        txt_supplier_label = (TextView) v.findViewById(R.id.txt_supplier_label);
        edit_brokerage = (EditText) v.findViewById(R.id.edit_brokerage);
        linear_brokerage = (LinearLayout) v.findViewById(R.id.linear_brokerage);
        linear_broker_user_container = (LinearLayout) v.findViewById(R.id.linear_broker_user_container);
        flexbox_suppliers = (FlexboxLayout) v.findViewById(R.id.flexbox_suppliers);
        btn_connect_supplier_broker = (TextView) v.findViewById(R.id.btn_connect_supplier_broker);

        card_order_details = (CardView) v.findViewById(R.id.card_order_details);
        select_broker_container = (RelativeLayout) v.findViewById(R.id.select_broker_container);

        relativeOrder = (RelativeLayout) v.findViewById(R.id.relative_order);
        frameRecyclerOrder = (FrameLayout) v.findViewById(R.id.frame_recycler_order);
        recyclerOrder = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_order);
        recyclerOrder.setEmptyView(v.findViewById(R.id.list_empty));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerOrder.setLayoutManager(layoutManager);
        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setNestedScrollingEnabled(false);
        isOrderExpand = false;
        arrow_img.setRotation(180);

        credit_details_card = v.findViewById(R.id.credit_details_card);
        set_discount_card = v.findViewById(R.id.set_discount_card);
        broker_details_card = (CardView) v.findViewById(R.id.broker_details_card);
        broker_supplier_card = (CardView) v.findViewById(R.id.broker_supplier_card);
        card_approve_reject = v.findViewById(R.id.card_approve_reject);

        relative_add_credit = v.findViewById(R.id.relative_add_credit);
        linear_add_credit_collapse = v.findViewById(R.id.linear_add_credit_collapse);
        linear_add_credit_collapse1 = v.findViewById(R.id.linear_add_credit_collapse1);
        linear_add_credit_collapse1.setVisibility(View.GONE);

        arrow_img2 = v.findViewById(R.id.arrow_img2);

        isAddCreditExpand = false;
        arrow_img2.setRotation(180);


        btn_add_broker = v.findViewById(R.id.btn_add_broker);

        //hide all before data comes
        credit_details_card.setVisibility(View.GONE);
        set_discount_card.setVisibility(View.GONE);
        broker_details_card.setVisibility(View.GONE);
        broker_supplier_card.setVisibility(View.GONE);


        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);


        tabscont = (LinearLayout) v.findViewById(R.id.tabscont);

        if (viewPager != null) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setupWithViewPager(viewPager);
        }

        relativeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOrderExpand) {
                    isOrderExpand = false;
                    frameRecyclerOrder.setVisibility(View.VISIBLE);
                    arrow_img.setRotation(180);
                } else {
                    isOrderExpand = true;
                    frameRecyclerOrder.setVisibility(View.GONE);
                    arrow_img.setRotation(0);
                }
            }
        });

        relative_add_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear_add_credit_collapse.getLayoutParams();

                if (isAddCreditExpand) {
                    isAddCreditExpand = false;
                    int padding = StaticFunctions.dpToPx(getActivity(), 16);
                    linear_add_credit_collapse1.setPadding(padding, padding, padding, padding);
                    linear_add_credit_collapse.setVisibility(View.VISIBLE);
                    arrow_img2.setRotation(180);
                } else {
                    int padding = StaticFunctions.dpToPx(getActivity(), 0);
                    linear_add_credit_collapse1.setPadding(padding, padding, padding, padding);
                    isAddCreditExpand = true;
                    linear_add_credit_collapse.setVisibility(View.GONE);
                    arrow_img2.setRotation(0);
                }
            }
        });

        select_broker_container.setVisibility(View.GONE);
        linear_brokerage.setVisibility(View.GONE);
        userInfo = UserInfo.getInstance(getActivity());
        if (userInfo.getGroupstatus().equals("2")) {
            editcont.setVisibility(View.GONE);
            //  img_edit.setEnabled(false);
            // btn_approve.setEnabled(false);
            // btn_reject.setEnabled(false);
            btn_approve.setVisibility(View.GONE);
            btn_reject.setVisibility(View.GONE);
            card_approve_reject.setVisibility(View.GONE);
            if (getArguments() != null) {
                if (getArguments().getString("isBuyerRejected", "no").equals("yes")) {
                    btn_order.setVisibility(View.GONE);
                    btn_visits.setVisibility(View.GONE);
                } else {
                    btn_order.setVisibility(View.VISIBLE);
                    btn_visits.setVisibility(View.VISIBLE);
                }
            }

        } else {
            editcont.setVisibility(View.VISIBLE);
            btn_order.setVisibility(View.GONE);
            btn_visits.setVisibility(View.GONE);
            btn_approve.setVisibility(View.VISIBLE);
            btn_reject.setVisibility(View.VISIBLE);
        }

        if (userInfo.getBroker() != null && userInfo.getBroker()) {
            linear_broker_user_container.setVisibility(View.VISIBLE);
            broker_supplier_card.setVisibility(View.VISIBLE);
            select_broker_container.setVisibility(View.GONE);

        } else {
            select_broker_container.setVisibility(View.VISIBLE);
            linear_broker_user_container.setVisibility(View.GONE);
            broker_supplier_card.setVisibility(View.GONE);
        }




       /* img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = edit_credit.isEnabled();
                setEditableFields(!enabled);
            }
        });*/

        btn_connect_supplier_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("type", "supplier")
                                .putExtra("toolbarTitle", "Select Supplier"),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                // Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                Bundle bundle = new Bundle();
                if (response_buyer != null) {
                    bundle.putString("buyerselected", response_buyer.getBuying_company().getName());
                    bundle.putString("buyer_selected_broker_id", response_buyer.getBroker_company());
                    bundle.putString("buyer_selected_company_id", String.valueOf(response_buyer.getBuying_company().getId()));
                    BuyersList buyersList = new BuyersList(String.valueOf(response_buyer.getBuying_company().getId()), response_buyer.getBuying_company().getName(), response_buyer.getBroker_company());
                    bundle.putSerializable("buyer", buyersList);
                    createOrderFrag.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                    Application_Singleton.CONTAINERFRAG = createOrderFrag;
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                } else {
                    Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_sales_order);
                    Application_Singleton.CONTAINERFRAG = createOrderFrag;
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                }
            }
        });


        initCall();
       /* btn_visits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE = "Visits";
                Application_Singleton.CONTAINERFRAG = new Fragment_Visits();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }
        });*/

      /*  but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditableFields(false);
            }
        });*/
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectBuyer rejectBuyer = new RejectBuyer(getArguments().getString("buyerid", ""), "rejected");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + getArguments().getString("buyerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Toast.makeText(getActivity(), "Buyer Rejected!", Toast.LENGTH_SHORT).show();
                            setEditableFields(false);
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                        //  progressDialog.dismiss();
                    }
                });
            }
        });
        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectBuyer rejectBuyer = new RejectBuyer(getArguments().getString("buyerid", ""), "approved");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + getArguments().getString("buyerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Toast.makeText(getActivity(), "Buyer approved!", Toast.LENGTH_SHORT).show();
                            setEditableFields(false);
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                     StaticFunctions.showResponseFailedDialog(error);
                        //  progressDialog.dismiss();
                    }
                });
            }
        });
        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                Gson gson = new Gson();
                PatchBuyer patchbuyer = new PatchBuyer();
                patchbuyer.setId(getArguments().getString("buyerid", ""));
                if (spinner_brokers.getCount() > 1) {
                    if (!((Response_brokers) spinner_brokers.getSelectedItem()).getCompany_id().equals("1")) {
                        patchbuyer.setBroker_company(((Response_brokers) spinner_brokers.getSelectedItem()).getCompany_id());
                    }
                } else {

                }
                if (response_buyer.getGroup_type().getName() != null && response_buyer.getGroup_type().getName().equals("Broker")) {

                    patchbuyer.setBrokerage_fees(edit_brokerage.getText().toString());

                }


                if (spinner_grouptypes.getSelectedItem() != null)
                    patchbuyer.setGroup_type(((Response_BuyerGroupType) spinner_grouptypes.getSelectedItem()).getId());

                /**
                 * https://wishbook.atlassian.net/browse/WB-2853
                 * Remove Relationship Discount
                 */
               // patchbuyer.setCash_discount(edit_cash.getText().toString());
               // patchbuyer.setDiscount(edit_discount.getText().toString());


                patchbuyer.setPayment_duration(edit_paymentdur.getText().toString());
                patchbuyer.setCredit_limit(edit_credit.getText().toString());
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + getArguments().getString("buyerid", "") + '/', gson.fromJson(gson.toJson(patchbuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        // setEditableFields(true);

                        getBuyer(getArguments().getString("buyerid", ""), false);
                        but_save.setVisibility(View.GONE);
                        // progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Buyer details updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });

        btn_visits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                gps_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                if (!gps_enabled) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.LightDialogTheme);
                    dialog.setMessage("Gps not enabled");
                    dialog.setPositiveButton("Open Location Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            getActivity().startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                }

                if (Activity_Home.pref.getString("currentmeet", "na").equals("na") && gps_enabled) {
                    Fragment_CheckInWithBuyer checkinDialogFrag = new Fragment_CheckInWithBuyer();
                    checkinDialogFrag.setCancelable(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", response_buyer.getId());
                    bundle.putString("company_id", String.valueOf(response_buyer.getBuying_company().getId()));
                    bundle.putString("company_name", response_buyer.getBuying_company().getName());
                    if (response_buyer.getBroker_company() != null) {
                        bundle.putString("broker_company", response_buyer.getBroker_company());
                    } else {
                        bundle.putString("broker_company", "");
                    }
                    checkinDialogFrag.setArguments(bundle);
                    checkinDialogFrag.show(getChildFragmentManager(), "checkin");
                } else {
                    if (!Activity_Home.pref.getString("currentmeet", "na").equals("na")) {
                        Toast.makeText(getActivity(), "You have currently one meeting running", Toast.LENGTH_LONG).show();
                        Fragment_Visits buyerDetailFrag = new Fragment_Visits();
                        Application_Singleton.CONTAINERFRAG = buyerDetailFrag;
                        Application_Singleton.CONTAINER_TITLE = "Visits";
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                    }
                }
            }
        });


        initSwipeRefresh(v);
        return v;
    }

    private void initCall() {
        if (getArguments() != null) {
            if (!getArguments().getString("buyerid", "").equals("")) {
                getBuyer(getArguments().getString("buyerid", ""), true);
            }
        }
        setEditableFields(true);
    }

    private void getBuyer(String id, final boolean isCache) {

        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + id + "/?expand=true", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    //Log.v("Buyer Response", response);
                    response_buyer = new Gson().fromJson(response, Response_BuyerFull.class);

                    //show all after data comes
                    /**
                     * https://wishbook.atlassian.net/browse/WB-2853
                     * Remove Relationship Discount
                     */
                    credit_details_card.setVisibility(View.GONE);
                    set_discount_card.setVisibility(View.GONE);
                    broker_details_card.setVisibility(View.GONE);
                    //broker_supplier_card.setVisibility(View.VISIBLE);


                    // change for bug #WB-850
                /*if (response_buyer.getBuying_person_name() != null) {
                    ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                    ((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuying_person_name());
                } else {
                    if (response_buyer.getBuying_company().getName() != null) {
                        ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                        ((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuyingCompany().getName());
                    } else {
                        ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.GONE);
                    }
                }*/


                    // change according bug #WB-1200
                    if (response_buyer != null) {
                        setBuyerFeedBackUI(response_buyer);
                        if (response_buyer.getBuying_person_name() != null) {
                            ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                            if (response_buyer.getBuying_company().getName() != null) {
                                if (response_buyer.getBuying_person_name().equals(response_buyer.getBuying_company().getName())) {
                                    String s = response_buyer.getBuying_person_name();
                                    ((TextView) v.findViewById(R.id.det_name)).setText(s);
                                } else {
                                    String s = response_buyer.getBuying_person_name() + " " + "(" + response_buyer.getBuying_company().getName() + ")";
                                    ((TextView) v.findViewById(R.id.det_name)).setText(s);
                                }
                            } else {
                                ((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuying_person_name());
                            }
                        } else {
                            if (response_buyer.getBuying_company().getName() != null) {
                                ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                                ((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuyingCompany().getName());
                            } else {
                                ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.GONE);
                            }
                        }

                        if (response_buyer.getBuyingCompany() != null && response_buyer.getBuyingCompany().getEmail() != null && !response_buyer.getBuyingCompany().getEmail().toLowerCase().contains("wishbook")) {

                            ((TextView) v.findViewById(R.id.det_email)).setText(response_buyer.getBuyingCompany().getEmail());
                        } else {
                            v.findViewById(R.id.det_email).setVisibility(View.GONE);
                        }

                        //((TextView) v.findViewById(R.id.det_name)).setText(response_buyer.getBuyingCompany().getName());

                        //((TextView) v.findViewById(R.id.det_num)).setText(response_buyer.getBuyingCompany().getPhone_number());
                        v.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_BUYER, ActionLogApi.ACTION_TYPE_CALL, String.valueOf(response_buyer.getBuyingCompany().getId()));
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_buyer.getBuyingCompany().getPhone_number()));
                                getActivity().startActivity(intent);
                            }
                        });
                        v.findViewById(R.id.chat_user).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_BUYER, ActionLogApi.ACTION_TYPE_CHAT, String.valueOf(response_buyer.getBuyingCompany().getId()));
                                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                                intent.putExtra(ConversationUIService.USER_ID, response_buyer.getBuying_company().getChat_user());
                                intent.putExtra(ConversationUIService.DISPLAY_NAME, response_buyer.getBuying_company().getName()); //put it for displaying the title.
                                intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
                                startActivity(intent);
                            }
                        });

                        v.findViewById(R.id.btn_call_wb_support).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Application_Singleton.trackEvent("callwbsupport","call","buyerDetail");
                                new ChatCallUtils(getActivity(),ChatCallUtils.CHAT_CALL_TYPE,null);
                            }
                        });


                        v.findViewById(R.id.det_email).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + response_buyer.getBuyingCompany().getEmail()));
                                getActivity().startActivity(intent);
                            }
                        });

                        if (response_buyer.getBuying_company().getThumbnail() != null && !response_buyer.getBuying_company().getThumbnail().equals("")) {

                            // StaticFunctions.loadImage(getActivity(),response_buyer.getBuying_company().getThumbnail(),profpic,R.drawable.uploadempty);
                            StaticFunctions.loadFresco(getActivity(), response_buyer.getBuying_company().getThumbnail(), profpic);
                            // Picasso.with(getActivity()).load(response_buyer.getBuying_company().getThumbnail()).into(profpic);
                        }
                        if (response_buyer.getStatus().equals("rejected")) {
                            card_order_details.setVisibility(View.GONE);
                            frameRecyclerOrder.setVisibility(View.GONE);
                            //tabscont.setVisibility(View.GONE);
                            editcont.setVisibility(View.GONE);
                            btn_approve.setVisibility(View.VISIBLE);
                            btn_reject.setVisibility(View.GONE);
                        } else {
                            card_order_details.setVisibility(View.VISIBLE);
                            frameRecyclerOrder.setVisibility(View.VISIBLE);
                            //tabscont.setVisibility(View.VISIBLE);
                            if (!userInfo.getGroupstatus().equals("2")) {
                                editcont.setVisibility(View.VISIBLE);
                                btn_reject.setVisibility(View.VISIBLE);
                                btn_approve.setVisibility(View.GONE);
                            }
                            edit_credit.setText(response_buyer.getCredit_limit());
                            edit_discount.setText(response_buyer.getDiscount());
                            edit_cash.setText(response_buyer.getCash_discount());
                            edit_paymentdur.setText(response_buyer.getPayment_duration());


                        }
                        brokerId = response_buyer.getBroker_company();
                        String groupId = null;
                        if (response_buyer.getGroup_type() != null) {
                            groupId = response_buyer.getGroup_type().getId();
                        }


                        //broker section start


                        if (userInfo.getBroker() != null && userInfo.getBroker()) {
                            Log.i("TAG", "onServerResponse:  User is Broker");
                            select_broker_container.setVisibility(View.GONE);
                            btn_add_broker.setVisibility(View.GONE);
                            if (select_broker_container.getVisibility() == View.GONE
                                    && btn_add_broker.getVisibility() == View.GONE
                                    && linear_brokerage.getVisibility() == View.GONE) {

                                broker_details_card.setVisibility(View.GONE);
                            }
                        } else {

                            if (response_buyer.getGroup_type() != null && response_buyer.getGroup_type().getName() != null && response_buyer.getGroup_type().getName().equals("Broker")) {

                                linear_brokerage.setVisibility(View.VISIBLE);
                                edit_brokerage.setText(response_buyer.getBrokerage_fees());
                                select_broker_container.setVisibility(View.GONE);
                                btn_add_broker.setVisibility(View.GONE);


                            } else {
                                linear_brokerage.setVisibility(View.GONE);
                                select_broker_container.setVisibility(View.VISIBLE);
                                btn_add_broker.setVisibility(View.VISIBLE);
                                Log.i("TAG", "onServerResponse:  User is Not Broker");
                                getbrokers(brokerId);
                            }

                        }

                        //broker section end


                        getGroups(groupId);
                        isChange();// change listener

                        if (!userInfo.getGroupstatus().equals("2")) {
                            card_order_details.setVisibility(View.VISIBLE);
                            frameRecyclerOrder.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            tabscont.setVisibility(View.GONE);
                            setOrderData(response_buyer);
                        } else {
                            // for display tab bar for saleman
                            card_order_details.setVisibility(View.GONE);
                            frameRecyclerOrder.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            setupViewPager(viewPager, response_buyer);
                        }

                        if (response_buyer != null && response_buyer.getBuying_company().getAddress() != null) {
                            setAddressNew(response_buyer.getBuying_company().getAddress());
                        }

                        btn_add_broker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(getActivity(), Activity_ConnectionList.class)
                                                .putExtra("toolbarTitle", "Select buyers to add as broker"),
                                        Application_Singleton.BROKER_ADD_REQUEST_CODE);
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void setEditableFields(boolean b) {

        spinner_brokers.setEnabled(b);
        //spinner_grouptypes.setEnabled(b);
        edit_paymentdur.setEnabled(b);
        edit_credit.setEnabled(b);
        edit_discount.setEnabled(b);
        edit_cash.setEnabled(b);


        //for broker
        edit_brokerage.setEnabled(b);
       /* if (b) {
            but_save.setVisibility(View.VISIBLE);
//            but_cancel.setVisibility(View.VISIBLE);
        } else {
            but_save.setVisibility(View.GONE);
            //           but_cancel.setVisibility(View.GONE);
        }*/
    }

    public void isChange() {

        edit_brokerage.addTextChangedListener(customWatcher[0] = new CustomWatcher(edit_brokerage));
        /**
         * https://wishbook.atlassian.net/browse/WB-2853
         * Remove Relationship Discount
         */
       // edit_cash.addTextChangedListener(customWatcher[1] = new CustomWatcher(edit_cash));
       // edit_discount.addTextChangedListener(customWatcher[2] = new CustomWatcher(edit_discount));
       // edit_paymentdur.addTextChangedListener(customWatcher[3] = new CustomWatcher(edit_paymentdur));
    }

    public void removeWatcher() {
        if (customWatcher != null && customWatcher.length > 0) {
            but_save.setVisibility(View.GONE);
            edit_brokerage.removeTextChangedListener(customWatcher[0]);
            /**
             * https://wishbook.atlassian.net/browse/WB-2853
             * Remove Relationship Discount
             */
           // edit_cash.removeTextChangedListener(customWatcher[1]);
            //edit_discount.removeTextChangedListener(customWatcher[2]);
            edit_paymentdur.removeTextChangedListener(customWatcher[3]);
        }
    }

    public class CustomWatcher implements TextWatcher {

        EditText editText;


        public CustomWatcher(EditText editText) {
            this.editText = editText;
        }


        public CustomWatcher() {

        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        @Override

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editText.isEnabled()) {
                but_save.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getGroups(final String id) {

        showProgress();

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Response_BuyerGroupType[] responseBuyerGroupTypes = Application_Singleton.gson.fromJson(response, Response_BuyerGroupType[].class);
                final SpinAdapter_grouptypes spinAdapter_grouptypes = new SpinAdapter_grouptypes((AppCompatActivity) getActivity(), R.layout.spinneritem, responseBuyerGroupTypes);
                spinner_grouptypes.setAdapter(spinAdapter_grouptypes);
                String defaultSelected = null;
                if (id != null) {
                    Log.e("TAG", "onServerResponse: id not null" + id);
                    for (Response_BuyerGroupType responseBuyerGroupType : responseBuyerGroupTypes) {
                        if (id.equals(responseBuyerGroupType.getId())) {
                            Log.e("TAG", "onServerResponse: in equal condition" + id);
                            defaultSelected = String.valueOf(spinAdapter_grouptypes.getPosition(responseBuyerGroupType));
                            spinner_grouptypes.setSelection(spinAdapter_grouptypes.getItemById(id));
                            Log.e("TAG", "onServerResponse: set selection");
                            break;
                        }
                    }
                }
                final String finalDefaultSelected = defaultSelected;
                spinner_grouptypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (finalDefaultSelected != null) {
                            if (Integer.parseInt(finalDefaultSelected) != i) {
                                but_save.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getbrokers(final String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brokerlist", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Response_brokers[] brokersdata = Application_Singleton.gson.fromJson(response, Response_brokers[].class);
                ArrayList<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                brokers.add(0, new Response_brokers("Select broker", "1", "1"));
                brokersdata = brokers.toArray(new Response_brokers[brokers.size()]);
                SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata);
                spinner_brokers.setAdapter(spinAdapter_brokers);


                if (brokers.size() < 2) {
                    select_broker_container.setVisibility(View.GONE);
                   /* broker_text.setVisibility(View.GONE);
                    spinner_brokers.setVisibility(View.GONE);*/
                } else {
                    select_broker_container.setVisibility(View.VISIBLE);
                   /* broker_text.setVisibility(View.VISIBLE);
                    spinner_brokers.setVisibility(View.VISIBLE);*/
                }

                String defaultSelected = "0";
                if (id != null) {
                    for (Response_brokers responseBrokers : brokersdata) {
                        if (id.equals(responseBrokers.getCompany_id())) {
                            defaultSelected = String.valueOf(spinAdapter_brokers.getPosition(responseBrokers));
                            spinner_brokers.setSelection(spinAdapter_brokers.getPosition(responseBrokers));
                        }
                    }
                }

                final String finalDefaultSelected = defaultSelected;
                spinner_brokers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (finalDefaultSelected != null) {
                            if (Integer.parseInt(finalDefaultSelected) != i) {
                                but_save.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void setOrderData(final Response_BuyerFull response_buyer) {
        Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
        if (response_buyer != null && response_buyer.getBuying_company() != null && response_buyer.getBuying_company().getBuying_order() != null) {
            String ordersList = new Gson().toJson(response_buyer.getBuying_company().getBuying_order());
            Type type = new TypeToken<Response_sellingorder[]>() {
            }.getType();
            Response_sellingorder[] response_sellingorders = new Gson().fromJson(ordersList, type);
            ArrayList<Response_sellingorder> list = new ArrayList<Response_sellingorder>(Arrays.asList(response_sellingorders));
            SalesOrderAdapterNew salesOrderAdapter = new SalesOrderAdapterNew(getActivity(), list);
            recyclerOrder.setAdapter(salesOrderAdapter);
            if (list.size() == 0) {
                // collapse for order size zero
                isOrderExpand = false;
                frameRecyclerOrder.setVisibility(View.GONE);
                arrow_img.setRotation(0);
            }
        }

    }

    private void setAddressNew(final Address address) {
        if (address != null) {
            String street = address.getStreet_address();
            if (street == null || street.equals("null")) {
                street = "";
            } else {
                street = street + ", ";
            }
            String city = address.getCity().getCity_name();
            if (city == null || city.equals("null")) {
                city = "";
            } else {
                city = city + ", ";
            }
            String state = address.getState().getState_name();
            if (state == null || state.equals("null")) {
                state = "";
            }
            ((TextView) v.findViewById(R.id.address)).setText(

                    city + state
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                v.findViewById(R.id.address).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
        }
    }


    private void setupViewPager(ViewPager viewPager, final Response_BuyerFull response_buyer) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
        if (response_buyer != null && response_buyer.getBuying_company() != null && response_buyer.getBuying_company().getBuying_order() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("orders", new Gson().toJson(response_buyer.getBuying_company().getBuying_order()));
            bundle.putString("type", "buyer");
            bDet_orders.setArguments(bundle);
        }
        adapter.addFragment(bDet_orders, "Orders");
        Fragment_Details_Meetings fragment_details_meetings = new Fragment_Details_Meetings();
        if (response_buyer != null && response_buyer.getBuying_company() != null) {
            Bundle bundle1 = new Bundle();
            bundle1.putString("buyerComapnyId", String.valueOf(response_buyer.getBuying_company().getId()));
            fragment_details_meetings.setArguments(bundle1);
        }
        adapter.addFragment(fragment_details_meetings, "Meetings");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            BuyersList suppier = (BuyersList) data.getSerializableExtra("buyer");
            flexbox_suppliers.removeAllViews();
            flexbox_suppliers.addView(txt_supplier_label);
            txt_supplier_label.setVisibility(View.VISIBLE);
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_suppliers, deleteableConfig);
            ArrayList<String> companies = new ArrayList<>();
            deleteableCloud.addChip(suppier.getCompany_name());
            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int i, String s) {
                    txt_supplier_label.setVisibility(View.GONE);
                    btn_connect_supplier_broker.setVisibility(View.VISIBLE);
                }
            });
            companies.add(suppier.getCompany_id());
            if (getArguments().getString("buyerid") != null) {
                String buyerId = getArguments().getString("buyerid");
                addSuppliers(buyerId, companies);
            }

        } else if (requestCode == Application_Singleton.BROKER_ADD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getbuyergroups();
        } else if(requestCode == 256 && resultCode == Activity.RESULT_OK){
            //refresh the page for changing feedback button
            onRefresh();
        }
    }

    public void addSuppliers(String buyerID, ArrayList<String> companies) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddSuppliers requestAddSuppliers = new RequestAddSuppliers();
        requestAddSuppliers.setSelling_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "add_suppliers", buyerID), new Gson().fromJson(new Gson().toJson(requestAddSuppliers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                txt_supplier_label.setVisibility(View.VISIBLE);
                btn_connect_supplier_broker.setVisibility(View.GONE);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    private void getbuyergroups() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                Type listOfProductObj = new TypeToken<ArrayList<GroupIDs>>() {
                }.getType();
                ArrayList<GroupIDs> buyergroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                String brokerGroupId = null;
                if (buyergroups != null && buyergroups.size() > 0) {
                    for (int i = 0; i < buyergroups.size(); i++) {
                        if (buyergroups.get(i).getName().equals("Broker")) {
                            brokerGroupId = buyergroups.get(i).getId();
                            break;
                        }
                    }
                }

                if (brokerGroupId != null) {
                    invite(brokerGroupId);
                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("sync response", error.getErrormessage());
            }
        });


    }

    private void invite(String groupID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        InviteContactsModel inviteContactsModel = new InviteContactsModel();
        inviteContactsModel.setGroup_type(groupID);
        inviteContactsModel.setRequest_type("Buyer");
        ArrayList<ArrayList<String>> invitees = new ArrayList<>();
        for (NameValues selectedContact : StaticFunctions.selectedContacts) {
            ArrayList<String> namevalue = new ArrayList<>();
            namevalue.add(selectedContact.getName());
            String phoneNumber1 = selectedContact.getPhone().replaceAll("\\D+", "");
            if (phoneNumber1.length() >= 10) {
                phoneNumber1 = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
            }
            namevalue.add(phoneNumber1);
            invitees.add(namevalue);
        }
        inviteContactsModel.setInvitee(invitees);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "contactsinvites", ""), new Gson().fromJson(new Gson().toJson(inviteContactsModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.selectedContacts.clear();
                Toast.makeText(getActivity(), "Added to your Network", Toast.LENGTH_SHORT).show();
                getbrokers(brokerId);

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(getActivity())
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }


    private void showBrokerDialog() {
        new MaterialDialog.Builder(getActivity())
                .content(getResources().getString(R.string.add_broker_popup))
                .positiveText(getResources().getString(R.string.add_broker_popup_addnow))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(getActivity(), Activity_ConnectionList.class)
                                        .putExtra("toolbarTitle", "Select buyers to add as broker"),
                                Application_Singleton.BROKER_ADD_REQUEST_CODE);
                    }
                })
                .negativeText(getResources().getString(R.string.add_broker_popup_later))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
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
                swipe_container.setRefreshing(false);
                removeWatcher();
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }

    public void setBuyerFeedBackUI(final Response_BuyerFull response_buyer) {
        if (response_buyer.getCredit_reference() != null) {
            if (response_buyer.getCredit_reference() != null && response_buyer.getCredit_reference().getId() == null) {
                // Show BtnRating Add
                btn_rating.setText(getResources().getString(R.string.buyer_add_feedback));
                btn_rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBuyerRating(Constants.CREDT_RATING_ADD_MODE,response_buyer);
                    }
                });
            } else {
                btn_rating.setText(getResources().getString(R.string.buyer_edit_feedback));
                // Show BtnRating with Edit
                btn_rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBuyerRating(Constants.CREDT_RATING_EDIT_MODE,response_buyer);
                    }
                });
            }
        }
    }

    public void openBuyerRating(int mode,Response_BuyerFull response_buyer) {
        try {
            Bundle data = new Bundle();
            data.putString("buyer_company_id", "" + response_buyer.getBuyingCompany().getId());
            data.putInt("mode", mode);
            data.putString("buyer_name", response_buyer.getBuyingCompany().getName());
            if (mode == Constants.CREDT_RATING_EDIT_MODE) {
                data.putString("id", response_buyer.getCredit_reference().getId());
            }
            Application_Singleton.CONTAINER_TITLE = response_buyer.getBuyingCompany().getName();
            Application_Singleton.TOOLBARSTYLE = "WHITE";
            Fragment_Buyer_Rating fragment_buyer_rating = new Fragment_Buyer_Rating();
            fragment_buyer_rating.setArguments(data);
            Application_Singleton.CONTAINERFRAG = fragment_buyer_rating;
            Fragment_BuyerDetails.this.startActivityForResult(new Intent(getActivity(), OpenContainer.class), 256);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
