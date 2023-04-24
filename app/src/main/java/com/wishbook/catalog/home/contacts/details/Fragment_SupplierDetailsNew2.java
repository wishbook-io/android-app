package com.wishbook.catalog.home.contacts.details;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchSeller;
import com.wishbook.catalog.commonmodels.postpatchmodels.RejectSeller;
import com.wishbook.catalog.commonmodels.responses.Address;
import com.wishbook.catalog.commonmodels.responses.RequestAddBuyers;
import com.wishbook.catalog.commonmodels.responses.ResponsePublicSeller;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;
import com.wishbook.catalog.commonmodels.responses.Response_BuyingCompany;
import com.wishbook.catalog.commonmodels.responses.Response_SellerFull;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;

public class Fragment_SupplierDetailsNew2 extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    // private RelativeLayout editcontainer;
    private Button updatepricebut;
    private Button but_cancel;
    private AppCompatButton btn_reject;
    private Button btn_approve;
    private Button but_create_order;
    private AppCompatButton but_save, btn_add_broker_per;
    private TextView btn_connect_buyer_broker;
    private SimpleDraweeView profpic;
    private EditText edit_fix_amount;
    private EditText edit_percentage_amount;
    private String supplierID, sellerCompanyid;
    //boolean isCatalogExpand, isOrderExpand;
    ImageView order_arrow_img, arrow_img;
    LinearLayout linear_catalog_container, linear_order_container,
            linear_broker_user_container, linear_feedback_request_view;
    FlexboxLayout flexbox_buyer;
    AppCompatButton btn_see_all, btn_add_buyers, btn_request_feedback;
    RelativeLayout relative_brokerage;
    TextView txt_brokerage_value, txt_buyer_label;

    private LinearLayout tabscont;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    public static List<NameValues> selectedBuyers = new ArrayList<>();
    List<NameValues> getConnectedBuyers = new ArrayList<>();
    private RadioGroup radiogroupprice;
    private CardView broker_buyer_card, card_add_margin, card_btn;
    private boolean isHideDetails = false;


    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    private LinearLayout linear_return_policy, linear_delivery_time;
    private TextView txt_delivery_value, txt_return_value;

    private String TAG = Fragment_SupplierDetailsNew2.class.getSimpleName();

    public Fragment_SupplierDetailsNew2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.details_supplier_new_2, ga_container, true);
        profpic = (SimpleDraweeView) v.findViewById(R.id.profpic);


        btn_approve = (Button) v.findViewById(R.id.btn_approve);
        linear_catalog_container = (LinearLayout) v.findViewById(R.id.linear_catalog_container);
        linear_order_container = (LinearLayout) v.findViewById(R.id.linear_order_container);
        linear_broker_user_container = (LinearLayout) v.findViewById(R.id.linear_broker_user_container);
        relative_brokerage = (RelativeLayout) v.findViewById(R.id.relative_brokerage);
        txt_brokerage_value = (TextView) v.findViewById(R.id.txt_brokerage_value);
        txt_buyer_label = (TextView) v.findViewById(R.id.txt_buyer_label);


        updatepricebut = (Button) v.findViewById(R.id.updatepricebut);
        edit_fix_amount = (EditText) v.findViewById(R.id.input_price);
        edit_percentage_amount = (EditText) v.findViewById(R.id.input_percentage);
        but_save = (AppCompatButton) v.findViewById(R.id.but_save);
        radiogroupprice = (RadioGroup) v.findViewById(R.id.radiogroupprice);


        // Hide Permenatly

        v.findViewById(R.id.det_email).setVisibility(View.GONE);
        //v.findViewById(R.id.address).setVisibility(View.GONE);

        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);


        tabscont = (LinearLayout) v.findViewById(R.id.tabscont);

        if (viewPager != null) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setupWithViewPager(viewPager);
        }

        linear_return_policy = v.findViewById(R.id.linear_return_policy);
        linear_delivery_time = v.findViewById(R.id.linear_delivery_time);
        txt_delivery_value = v.findViewById(R.id.txt_delivery_value);
        txt_return_value = v.findViewById(R.id.txt_return_value);

        card_btn = (CardView) v.findViewById(R.id.card_btn);

        btn_reject = (AppCompatButton) v.findViewById(R.id.btn_reject);
        but_create_order = (Button) v.findViewById(R.id.createorder);
        flexbox_buyer = (FlexboxLayout) v.findViewById(R.id.flexbox_buyer);
        btn_connect_buyer_broker = (TextView) v.findViewById(R.id.btn_connect_buyer_broker);
        btn_add_broker_per = (AppCompatButton) v.findViewById(R.id.btn_add_broker_per);
        btn_add_buyers = (AppCompatButton) v.findViewById(R.id.btn_add_buyers);
        btn_request_feedback = v.findViewById(R.id.btn_request_feedback);
        btn_see_all = (AppCompatButton) v.findViewById(R.id.btn_see_all);

        linear_feedback_request_view = v.findViewById(R.id.linear_feedback_request_view);
        btn_connect_buyer_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("bottom_action", "save"),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        btn_add_broker_per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrokerageDialog();
            }
        });


        btn_add_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("bottom_action", "save")
                                .putExtra("previouslyArray", (Serializable) getConnectedBuyers),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        btn_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("type", "connected_buyers")
                                .putExtra("selling_comapny_id", sellerCompanyid)
                                .putExtra("supplier_id", supplierID)
                                .putExtra("bottom_action", "delete"),
                        Application_Singleton.BUYER_REMOVE_REQUEST_CODE);
            }
        });




        radiogroupprice.check(R.id.check_add);
        edit_fix_amount.setEnabled(false);
        edit_percentage_amount.setSelection(1);
        edit_percentage_amount.setEnabled(true);


        broker_buyer_card = (CardView) v.findViewById(R.id.broker_buyer_card);
        card_add_margin = (CardView) v.findViewById(R.id.card_add_margin);

        //hide all before data comes
        card_add_margin.setVisibility(View.GONE);
        broker_buyer_card.setVisibility(View.GONE);
        linear_broker_user_container.setVisibility(View.GONE);


        radiogroupprice.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (checkedId != R.id.check_add) {
                            edit_fix_amount.setEnabled(true);
                            edit_fix_amount.requestFocus();
                            edit_fix_amount.setSelection(0);
                            edit_percentage_amount.setEnabled(false);
                            edit_percentage_amount.setText("0");
                            edit_percentage_amount.setError(null);
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_fix_amount, InputMethodManager.SHOW_IMPLICIT);


                        } else {

                            edit_fix_amount.setEnabled(false);
                            edit_fix_amount.setText("0");
                            edit_fix_amount.setError(null);
                            edit_percentage_amount.setEnabled(true);
                            edit_percentage_amount.requestFocus();
                            edit_percentage_amount.setSelection(0);


                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_percentage_amount, InputMethodManager.SHOW_IMPLICIT);

                        }


                    }
                }
        );


        initCall();


        // getSeller(getArguments().getString("sellerid",""));
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid", ""), "rejected");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Toast.makeText(getActivity(), "Supplier Removed!", Toast.LENGTH_SHORT).show();
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
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid", ""), "approved");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Toast.makeText(getActivity(), "Supplier approved!", Toast.LENGTH_SHORT).show();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // progressDialog.dismiss();
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
                PatchSeller patchSeller = new PatchSeller();
                patchSeller.setId(getArguments().getString("sellerid", ""));
                patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());


                if (radiogroupprice.getCheckedRadioButtonId() != R.id.check_add) {
                    Log.i("TAG", "onClick: Fixed Rate");
                    patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                    patchSeller.setPercentage_amount("0");
                } else {
                    Log.i("TAG", "onClick: percentage Rate");
                    patchSeller.setFix_amount("0");
                    patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());

                }


                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(patchSeller), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {

                        /**
                         *  https://wishbook.atlassian.net/browse/WB-2853
                         *  Remove Relationship
                         */
                        card_add_margin.setVisibility(View.GONE);
                      /*  if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                            // hide push margin for retailer
                            card_add_margin.setVisibility(View.GONE);
                        }
                        card_add_margin.setVisibility(View.VISIBLE);*/
                        // progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Supplier details updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                        //  progressDialog.dismiss();
                    }
                });
            }

        });

        initSwipeRefresh(v);
        return v;
    }

    private void initCall() {
        if (getArguments() != null) {
            if (!getArguments().getString("sellerid", "").equals("")) {
                supplierID = getArguments().getString("sellerid", "");
                sellerCompanyid = getArguments().getString("sellerCompanyid");
                Log.i(TAG, " Seller Company ID==>: " + sellerCompanyid);
                if (sellerCompanyid != null) {

                }
                if (getArguments().getBoolean("isHideAll")) {
                    isHideDetails = true;
                }

                if (isHideDetails) {
                    getPublicSellerDetail(getArguments().getString("sellerid", ""));
                } else {
                    getSeller(getArguments().getString("sellerid", ""));
                }

            }
        }
    }

    private void getSeller(String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        String url = URLConstants.companyUrl(getActivity(), "sellers", "") + id + "/?expand=true";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("Seller=>", response);
                if (isAdded() && !isDetached()) {
                    final Response_SellerFull response_seller = new Gson().fromJson(response, Response_SellerFull.class);
                    if (!isHideDetails) {
                        if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                            broker_buyer_card.setVisibility(View.VISIBLE); // show conncted broker
                            linear_broker_user_container.setVisibility(View.VISIBLE); // show brokerage value
                            if (response_seller.getSelling_company() != null) {
                                connectBuyerAsBroker(String.valueOf(response_seller.getSelling_company().getId()));
                            } else {
                                linear_broker_user_container.setVisibility(View.GONE); // hide brokerage value linear
                                broker_buyer_card.setVisibility(View.GONE); // hide connected buyer card
                            }
                        } else {
                            linear_broker_user_container.setVisibility(View.GONE); // hide brokerage value linear
                            broker_buyer_card.setVisibility(View.GONE); // hide connected buyer card
                        }
                    }

                    /**
                     *  https://wishbook.atlassian.net/browse/WB-2853
                     *  Remove Relationship
                     */
                    card_add_margin.setVisibility(View.GONE);
                    // broker_buyer_card.setVisibility(View.VISIBLE);
                    //linear_broker_user_container.setVisibility(View.VISIBLE);

                    setSupplierRatingRequestUI(response_seller);
                    if (viewPager != null) {
                        setupViewPager(response_seller);
                        //tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);
                    }

                    //setupOrder(response_seller);
                    setAddressNew(response_seller.getSelling_company().getAddress());
                    // ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getSelling_company().getName());

                    // change according bug #WB-1200
                    if (response_seller.getSupplier_person_name() != null) {
                        ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                        if (response_seller.getSelling_company().getName() != null) {
                            String s = response_seller.getSupplier_person_name() + " " + "(" + response_seller.getSelling_company().getName() + ")";
                            ((TextView) v.findViewById(R.id.det_name)).setText(s);
                        } else {
                            ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getSupplier_person_name());
                        }
                    } else {
                        if (response_seller.getSelling_company().getName() != null) {
                            ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.VISIBLE);
                            ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getSelling_company().getName());
                        } else {
                            ((TextView) v.findViewById(R.id.det_name)).setVisibility(View.GONE);
                        }
                    }
                    if (response_seller.getSelling_company().getEmail() != null) {
                        if (response_seller.getSelling_company().getEmail().toLowerCase().contains("wishbook")) {
                            v.findViewById(R.id.det_email).setVisibility(View.GONE);
                        } else {

                        }
                    }

                    if (response_seller.getSelling_company() != null && response_seller.getSelling_company().getSeller_policy() != null && response_seller.getSelling_company().getSeller_policy().size() > 0) {
                        ArrayList<ResponseSellerPolicy> policies = response_seller.getSelling_company().getSeller_policy();
                        for (int i = 0; i < policies.size(); i++) {
                            if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                                linear_delivery_time.setVisibility(View.VISIBLE);
                                txt_delivery_value.setText(policies.get(i).getPolicy().toString());
                            } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                                linear_return_policy.setVisibility(View.VISIBLE);
                                txt_return_value.setText(policies.get(i).getPolicy().toString());
                            }
                        }
                    }
                    if (response_seller.getSelling_company() != null && response_seller.getSelling_company().getEmail() != null && !response_seller.getSelling_company().getEmail().toLowerCase().contains("wishbook")) {

                        ((TextView) v.findViewById(R.id.det_email)).setText(response_seller.getSelling_company().getEmail());
                    } else {
                        v.findViewById(R.id.det_email).setVisibility(View.GONE);
                    }


                    //((TextView) v.findViewById(R.id.det_num)).setText(response_seller.getSelling_company().getPhone_number());
                    edit_fix_amount.setText(response_seller.getFix_amount());
                    edit_percentage_amount.setText(response_seller.getPercentage_amount());
                    if (response_seller.getSelling_company().getThumbnail() != null && !response_seller.getSelling_company().getThumbnail().equals("")) {
                        //StaticFunctions.loadImage(getActivity(),response_seller.getSelling_company().getThumbnail(),profpic,R.drawable.uploadempty);
                        StaticFunctions.loadFresco(getActivity(), response_seller.getSelling_company().getThumbnail(), profpic);
                        // Picasso.with(getActivity()).load(response_seller.getSelling_company().getThumbnail()).into(profpic);
                    }

                    if (response_seller.getStatus().equals("rejected")) {
                        btn_reject.setVisibility(View.GONE);
                        btn_approve.setVisibility(View.VISIBLE);
                        //change for supplier new UI
                        //cont_pricerule.setVisibility(View.GONE);
                        card_add_margin.setVisibility(View.GONE);

                        tabscont.setVisibility(View.GONE);

                    } else {
                        //change for supplier new UI
                        //cont_pricerule.setVisibility(View.VISIBLE);
                        /**
                         *  https://wishbook.atlassian.net/browse/WB-2853
                         *  Remove Relationship
                         */
                        card_add_margin.setVisibility(View.GONE);


                        tabscont.setVisibility(View.VISIBLE);
                        btn_reject.setVisibility(View.VISIBLE);
                        btn_approve.setVisibility(View.GONE);


                    }
                    if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                        if (!response_seller.getBrokerage_fees().equals("0.00")) {
                            relative_brokerage.setVisibility(View.VISIBLE);
                            txt_brokerage_value.setText(response_seller.getBrokerage_fees() + "%");
                            btn_add_broker_per.setVisibility(View.GONE);
                        } else {
                            relative_brokerage.setVisibility(View.GONE);
                            btn_add_broker_per.setVisibility(View.VISIBLE);
                        }
                    }
                    if(UserInfo.getInstance(getActivity()).isSuper_User()){
                        v.findViewById(R.id.btn_call).setVisibility(View.VISIBLE);
                    } else {
                        v.findViewById(R.id.btn_call).setVisibility(View.GONE);
                    }
                    v.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CALL, String.valueOf(response_seller.getSelling_company().getId()));
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_seller.getSelling_company().getPhone_number()));
                            getActivity().startActivity(intent);
                        }
                    });

                    v.findViewById(R.id.chat_user).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CHAT, String.valueOf(response_seller.getSelling_company().getId()));
                            Intent intent = new Intent(getActivity(), ConversationActivity.class);
                            intent.putExtra(ConversationUIService.USER_ID, response_seller.getSelling_company().getChat_user());
                            intent.putExtra(ConversationUIService.DISPLAY_NAME, response_seller.getSelling_company().getName()); //put it for displaying the title.
                            intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
                            startActivity(intent);
                        }
                    });

                    v.findViewById(R.id.btn_call_wb_support).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("callwbsupport","call","supplierDetail");
                            new ChatCallUtils(getActivity(),ChatCallUtils.CHAT_CALL_TYPE,null);
                        }
                    });

                    v.findViewById(R.id.btn_chat_wb_support).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("chatwbsupport","call","supplierDetail");
                            String msg = "Supplier Ref No #" + sellerCompanyid;
                            new ChatCallUtils(getActivity(),ChatCallUtils.WB_CHAT_TYPE,msg);
                        }
                    });

                    v.findViewById(R.id.det_email).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + response_seller.getSelling_company().getEmail()));
                            getActivity().startActivity(intent);
                        }
                    });
                    if (isHideDetails) {
                        card_add_margin.setVisibility(View.GONE);
                        broker_buyer_card.setVisibility(View.GONE);
                        card_btn.setVisibility(View.GONE);
                        linear_broker_user_container.setVisibility(View.GONE);
                        v.findViewById(R.id.linear_chat_call).setVisibility(View.GONE);
                        //v.findViewById(R.id.det_num).setVisibility(View.GONE);
                        v.findViewById(R.id.det_email).setVisibility(View.GONE);
                    }

                    if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                        // hide push margin for retailer
                        card_add_margin.setVisibility(View.GONE);
                    }else{
                       // but_save.setVisibility(View.VISIBLE);
                        isChange();
                    }

                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    private void getPublicSellerDetail(final String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "public_supplier_detail", id);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, isAllowCache, new HttpManager.customCallBack() {
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
                if (isAdded() && !isDetached()) {
                    final ResponsePublicSeller response_seller = new Gson().fromJson(response, ResponsePublicSeller.class);
                    if (viewPager != null) {
                        setupViewPager(response_seller, id);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);
                    }

                    //setupOrder(response_seller);
                    setAddressNew(response_seller.getAddress());
                    ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getName());

                    if (response_seller.getEmail() != null && !response_seller.getEmail().toLowerCase().contains("wishbook")) {
                        ((TextView) v.findViewById(R.id.det_email)).setText(response_seller.getEmail());
                    } else {
                        v.findViewById(R.id.det_email).setVisibility(View.GONE);

                    }


                    // ((TextView) v.findViewById(R.id.det_num)).setText(response_seller.getPhone_number());
                    if (response_seller.getThumbnail() != null && !response_seller.getThumbnail().equals("")) {
                        StaticFunctions.loadFresco(getActivity(), response_seller.getThumbnail(), profpic);
                    }

                    if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                        v.findViewById(R.id.btn_call).setVisibility(View.GONE);
                        v.findViewById(R.id.chat_user).setVisibility(View.GONE);
                        v.findViewById(R.id.det_email).setVisibility(View.GONE);
                    } else {
                        if(UserInfo.getInstance(getActivity()).isSuper_User()){
                            v.findViewById(R.id.btn_call).setVisibility(View.VISIBLE);
                        } else {
                            v.findViewById(R.id.btn_call).setVisibility(View.GONE);
                        }
                    }



                    v.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CALL, String.valueOf(id));
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_seller.getPhone_number()));
                            getActivity().startActivity(intent);
                        }
                    });

                    v.findViewById(R.id.chat_user).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CHAT, String.valueOf(id));
                            Intent intent = new Intent(getActivity(), ConversationActivity.class);
                            intent.putExtra(ConversationUIService.USER_ID, response_seller.getChat_admin_user());
                            intent.putExtra(ConversationUIService.DISPLAY_NAME, response_seller.getName()); //put it for displaying the title.
                            intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
                            startActivity(intent);
                        }
                    });

                    v.findViewById(R.id.btn_call_wb_support).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("callwbsupport","call","supplierDetail");
                            new ChatCallUtils(getActivity(),ChatCallUtils.CHAT_CALL_TYPE,null);
                        }
                    });

                    v.findViewById(R.id.btn_chat_wb_support).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Application_Singleton.trackEvent("chatwbsupport","call","supplierDetail");
                            String msg = "Supplier Ref No #" + id;
                            new ChatCallUtils(getActivity(),ChatCallUtils.WB_CHAT_TYPE,msg);
                        }
                    });
                    v.findViewById(R.id.det_email).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + response_seller.getEmail()));
                            getActivity().startActivity(intent);
                        }
                    });

                    if (response_seller.getSeller_policy() != null && response_seller.getSeller_policy().size() > 0) {
                        ArrayList<ResponseSellerPolicy> policies = response_seller.getSeller_policy();
                        for (int i = 0; i < policies.size(); i++) {
                            if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                                linear_delivery_time.setVisibility(View.VISIBLE);
                                txt_delivery_value.setText(policies.get(i).getPolicy().toString());
                            } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                                linear_return_policy.setVisibility(View.VISIBLE);
                                txt_return_value.setText(policies.get(i).getPolicy().toString());
                            }
                        }
                    }
                    if (isHideDetails) {
                        card_add_margin.setVisibility(View.GONE);
                        broker_buyer_card.setVisibility(View.GONE);
                        card_btn.setVisibility(View.GONE);
                        linear_broker_user_container.setVisibility(View.GONE);
                        //v.findViewById(R.id.det_num).setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Log.v("error response", error.getErrormessage());
            }
        });
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

    /**
     * https://wishbook.atlassian.net/browse/WB-2853
     * Remove Set Margin Feature
     */
    public void isChange() {
       // edit_fix_amount.addTextChangedListener(new CustomWatcher(edit_fix_amount));
       // edit_percentage_amount.addTextChangedListener(new CustomWatcher(edit_percentage_amount));
    }

    public class CustomWatcher implements TextWatcher {

        EditText editText;

        public CustomWatcher(EditText editText) {
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

    /**
     * https://wishbook.atlassian.net/browse/WB-2853
     * Not Show Orders Sellers
     * @param response_sellerFull
     * @param sellerCompanyid
     */
    private void setupViewPager(final ResponsePublicSeller response_sellerFull, String sellerCompanyid) {
        try{
            Adapter adapter = new Adapter(getChildFragmentManager());
           /* Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
            if (response_sellerFull.getSelling_order() != null
                    && response_sellerFull.getSelling_order().size() > 0) {

                String ordersList = new Gson().toJson(response_sellerFull.getSelling_order());
                Bundle bundle = new Bundle();
                bundle.putString("orders", ordersList);
                bundle.putString("type", "supplier");
                bDet_orders.setArguments(bundle);
            }*/
            Fragment_Details_Catalog fragment_details_catalog = new Fragment_Details_Catalog();
            if (sellerCompanyid != null) {
                Bundle bundle = new Bundle();
                bundle.putString("sellerCompanyid", String.valueOf(sellerCompanyid));
                fragment_details_catalog.setArguments(bundle);
            }
            adapter.addFragment(fragment_details_catalog, "Products");
            //adapter.addFragment(bDet_orders, "Orders");
            viewPager.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * https://wishbook.atlassian.net/browse/WB-2853
     * Not Show Orders Sellers
     * @param response_sellerFull
     */
    private void setupViewPager(final Response_SellerFull response_sellerFull) {
        Adapter adapter = new Adapter(getChildFragmentManager());
       /* Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
        if (response_sellerFull.getBuying_company().getSelling_order() != null
                && response_sellerFull.getBuying_company().getSelling_order().size() > 0) {

            String ordersList = new Gson().toJson(response_sellerFull.getBuying_company().getSelling_order());
            Bundle bundle = new Bundle();
            bundle.putString("orders", ordersList);
            bundle.putString("type", "supplier");
            bDet_orders.setArguments(bundle);
        }*/
        Fragment_Details_Catalog fragment_details_catalog = new Fragment_Details_Catalog();
        if (response_sellerFull != null && response_sellerFull.getSelling_company() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("sellerCompanyid", String.valueOf(response_sellerFull.getSelling_company().getId()));
            bundle.putBoolean("isAllowCache", isAllowCache);
            fragment_details_catalog.setArguments(bundle);
        }
        adapter.addFragment(fragment_details_catalog, "Products");
        //adapter.addFragment(bDet_orders, "Orders");
        viewPager.setAdapter(adapter);
    }

    private void setAddress(final Response_BuyingCompany response_buyingCompany) {
        if (response_buyingCompany != null) {
            if (response_buyingCompany.getBranches() != null) {
                if (response_buyingCompany.getBranches().size() > 0) {
                    String street = response_buyingCompany.getBranches().get(0).getStreetAddress();
                    if (street == null || street.equals("null")) {
                        street = "";
                    } else {
                        street = street + ",";
                    }
                    String city = response_buyingCompany.getBranches().get(0).getCity().getCityName();
                    if (city == null || city.equals("null")) {
                        city = "";
                    } else {
                        city = city + ",";
                    }
                    String state = response_buyingCompany.getBranches().get(0).getState().getStateName();
                    if (state == null || state.equals("null")) {
                        state = "";
                    }
                    ((TextView) v.findViewById(R.id.address)).setText(

                            street + city + state
                    );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        v.findViewById(R.id.address).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    }
                }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedBuyers = new ArrayList<>();
            selectedBuyers = (List<NameValues>) data.getSerializableExtra("buyer");
            txt_buyer_label.setVisibility(View.VISIBLE);
            flexbox_buyer.setVisibility(View.VISIBLE);
            btn_connect_buyer_broker.setVisibility(View.GONE);
            if (getConnectedBuyers != null) {
                getConnectedBuyers.addAll(selectedBuyers);
            }
            flexbox_buyer.removeAllViews();
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
            ArrayList<String> companies = new ArrayList<>();
            //getPhone equals comapnyid
            for (NameValues s : selectedBuyers) {
                companies.add(s.getPhone());
            }
            addChips(deleteableCloud);
            addBuyers(supplierID, companies);
        } else if (requestCode == Application_Singleton.BUYER_REMOVE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            connectBuyerAsBroker(sellerCompanyid);
        }
    }

    public void showBrokerageDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("Add Brokerage")
                .content("Enter Brokerage")
                .inputType(InputType.TYPE_CLASS_NUMBER).inputRange(0, 2)
                .content("You can enter brokerage % only once. Any changes in the brokerage can be made by supplier only.\n")
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText().getText().toString().isEmpty()) {
                            dialog.getInputEditText().setError(getResources().getString(R.string.empty_field));
                            return;
                        }
                        addBrokerage(dialog.getInputEditText().getText().toString().trim());
                    }
                }).positiveText("Save")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});

        materialDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        materialDialog.getContentView().setTextColor(getResources().getColor(R.color.purchase_dark_gray));

    }

    public void addBuyers(String supplierId, ArrayList<String> companies) {

        Log.i("TAG", "addBuyers Request: ==>" + companies + "\n Supplier id" + supplierId);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "add_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void addBrokerage(final String brokerage) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        PatchSeller patchSeller = new PatchSeller();
        patchSeller.setId(getArguments().getString("sellerid", ""));
        patchSeller.setBrokerage_fees(brokerage);
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + supplierID + '/', gson.fromJson(gson.toJson(patchSeller), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.i("TAG", "onServerResponse: " + response);
                relative_brokerage.setVisibility(View.VISIBLE);
                txt_brokerage_value.setText(brokerage + "%");
                btn_add_broker_per.setVisibility(View.GONE);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void connectBuyerAsBroker(String sellingComapnyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "get_connected_buyers_broker", sellingComapnyId), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                BuyersList[] buyers = Application_Singleton.gson.fromJson(response, BuyersList[].class);
                //Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                if (buyers.length > 0) {
                    txt_buyer_label.setVisibility(View.VISIBLE);
                    flexbox_buyer.setVisibility(View.VISIBLE);
                    btn_connect_buyer_broker.setVisibility(View.GONE);
                    ArrayList<BuyersList> response_buyers = new ArrayList<BuyersList>(Arrays.asList(buyers));
                    getConnectedBuyers = new ArrayList<NameValues>();
                    for (int i = 0; i < response_buyers.size(); i++) {
                        if (response_buyers.get(i).getCompany_name() != null) {
                            getConnectedBuyers.add(new NameValues(response_buyers.get(i).getCompany_name(), response_buyers.get(i).getCompany_id()));
                        } else {
                            getConnectedBuyers.add(new NameValues(response_buyers.get(i).getPhone_number(), response_buyers.get(i).getCompany_id()));
                        }
                    }
                    flexbox_buyer.removeAllViews();
                    ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                            .selectMode(ChipCloud.SelectMode.multi)
                            .checkedChipColor(Color.parseColor("#ddaa00"))
                            .checkedTextColor(Color.parseColor("#ffffff"))
                            .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                            .showClose(Color.parseColor("#a6a6a6"))
                            .useInsetPadding(true)
                            .uncheckedTextColor(Color.parseColor("#000000"));
                    ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
                    addChips(deleteableCloud);

                } else {
                    txt_buyer_label.setVisibility(View.GONE);
                    flexbox_buyer.setVisibility(View.GONE);
                    btn_connect_buyer_broker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
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

    private void addChips(ChipCloud deleteableCloud) {
        try {
            if (getConnectedBuyers.size() > 4) {
                for (int i = 0; i < 4; i++) {
                    deleteableCloud.addChip(getConnectedBuyers.get(i).getName());
                }
            } else {
                for (NameValues s : getConnectedBuyers) {
                    deleteableCloud.addChip(s.getName());
                    // companies.add(s.getPhone());
                }
            }
            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int i, String s) {
                    ArrayList<String> companie = new ArrayList<String>();
                    companie.add(getConnectedBuyers.get(i).getPhone());
                    removeConnectedBuyers(supplierID, companie);
                }
            });
            flexbox_buyer.addView(btn_add_buyers);
            flexbox_buyer.addView(btn_see_all);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeConnectedBuyers(String supplierId, ArrayList<String> companies) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "remove_connected_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }


    public void getSellerPolicy(String supplierCompanyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "seller-policy", supplierCompanyId), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (response != null) {
                    if (isAdded() && !isDetached()) {
                        ResponseSellerPolicy[] policies = Application_Singleton.gson.fromJson(response, ResponseSellerPolicy[].class);
                        for (int i = 0; i < policies.length; i++) {
                            if (policies[i].getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                                linear_delivery_time.setVisibility(View.VISIBLE);
                                txt_delivery_value.setText(policies[i].getPolicy().toString());
                            } else if (policies[i].getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                                linear_return_policy.setVisibility(View.VISIBLE);
                                txt_return_value.setText(policies[i].getPolicy().toString());
                            }
                        }
                    }

                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
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
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


    public void setSupplierRatingRequestUI(final Response_SellerFull responseSellerFull) {
        if(responseSellerFull.getCredit_reference()!=null){
            if(responseSellerFull.getCredit_reference().getId() == null){
                linear_feedback_request_view.setVisibility(View.VISIBLE);
                btn_request_feedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callRequestFeedback(String.valueOf(responseSellerFull.getSelling_company().getId()), responseSellerFull.getSelling_company().getName());
                    }
                });
            } else {
                linear_feedback_request_view.setVisibility(View.GONE);
            }

        } else {
            linear_feedback_request_view.setVisibility(View.GONE);
        }

    }

    public void callRequestFeedback(String sellerCompanyid, String sellerCompanyName) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("buying_company", UserInfo.getInstance(getActivity()).getCompany_id());
        params.put("selling_company", sellerCompanyid);
        params.put("selling_company_name", sellerCompanyName);
        params.put("buyer_requested", "true");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "add-credit-reference", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    linear_feedback_request_view.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Successfully requested for feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
