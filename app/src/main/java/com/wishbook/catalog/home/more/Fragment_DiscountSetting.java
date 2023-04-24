package com.wishbook.catalog.home.more;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.wishbook.catalog.commonmodels.responses.BuyerDiscountRequest;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_DiscountSetting extends GATrackedFragment {

    private View view;

    @BindView(R.id.edit_wholeseller_cd)
    EditText editWholesellerCd;
    @BindView(R.id.edit_wholeseller_crd)
    EditText editWholesellerCrd;
    @BindView(R.id.edit_wholeseller_dayscr)
    EditText editWholesellerDayscr;

    @BindView(R.id.edit_retailer_cd)
    EditText editRetailerCd;
    @BindView(R.id.edit_retailer_crd)
    EditText editRetailerCrd;
    @BindView(R.id.edit_retailer_dayscr)
    EditText editRetailerDayscr;

    @BindView(R.id.edit_broker_cd)
    EditText editBrokerCd;
    @BindView(R.id.edit_broker_crd)
    EditText editBrokerCrd;
    @BindView(R.id.edit_broker_dayscr)
    EditText editBrokerDayscr;

    @BindView(R.id.edit_public_cd)
    EditText editPublicCd;
    @BindView(R.id.edit_public_crd)
    EditText editPublicCrd;
    @BindView(R.id.edit_public_dayscr)
    EditText editPublicDayscr;

    @BindView(R.id.btn_submit)
    AppCompatButton btnSubmit;

    @BindView(R.id.btn_cancel)
    AppCompatButton btnCancel;


    BuyerDiscountRequest[] buyerDiscountRequests;
    private boolean isWholesaler, isRetailer, isBroker, isPublic;


    public Fragment_DiscountSetting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_discount_setting, ga_container, true);
        ButterKnife.bind(this, view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);
        fetchDiscount();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Application_Singleton.trackEvent("BtnGroupDiscountSetting", "Click", "BtnGroupDiscountSetting");

                String public_cash_discount = isEmpty(editPublicCd) ? "0.0" : editPublicCd.getText().toString().trim();
                String public_cr_discount = isEmpty(editPublicCrd) ? "0.0" : editPublicCrd.getText().toString().trim();
                String public_days = isEmpty(editPublicDayscr) ? "0.0" : editPublicDayscr.getText().toString().trim();

                String broker_cash_discount = isEmpty(editBrokerCd) ? "0.0" : editBrokerCd.getText().toString().trim();
                String broker_cr_discount = isEmpty(editBrokerCrd) ? "0.0" : editBrokerCrd.getText().toString().trim();
                String broker_days = isEmpty(editBrokerDayscr) ? "0.0" : editBrokerDayscr.getText().toString().trim();

                String wh_cash_discount = isEmpty(editWholesellerCd) ? "0.0" : editWholesellerCd.getText().toString();
                String wh_cr_discount = isEmpty(editWholesellerCrd) ? "0.0" : editWholesellerCrd.getText().toString().trim();
                String wh_days = isEmpty(editWholesellerDayscr) ? "0.0" : editWholesellerDayscr.getText().toString().trim();

                String re_cash_discount = isEmpty(editRetailerCd) ? "0.0" : editRetailerCd.getText().toString().trim();
                String re_cr_discount = isEmpty(editRetailerCrd) ? "0.0" : editRetailerCrd.getText().toString().trim();
                String re_days = isEmpty(editRetailerDayscr) ? "0.0" : editRetailerDayscr.getText().toString().trim();

                if (buyerDiscountRequests!=null && buyerDiscountRequests.length > 0) {
                    // Update Discount
                    for (BuyerDiscountRequest discount : buyerDiscountRequests) {
                        if (discount.getBuyer_type().equalsIgnoreCase(getResources().getString(R.string.buyer_public))) {
                            BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(discount.getId(), public_cash_discount, discount.getBuyer_type(), public_days, public_cr_discount);
                            updateDiscount(discountRequest);
                        }
                        if (discount.getBuyer_type().equalsIgnoreCase(getResources().getString(R.string.buyer_broker))) {
                            BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(discount.getId(), broker_cash_discount, discount.getBuyer_type(), broker_days, broker_cr_discount);
                            updateDiscount(discountRequest);
                        }
                        if (discount.getBuyer_type().equalsIgnoreCase(getResources().getString(R.string.buyer_wholesaler))) {
                            BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(discount.getId(), wh_cash_discount, discount.getBuyer_type(), wh_days, wh_cr_discount);
                            updateDiscount(discountRequest);
                        }
                        if (discount.getBuyer_type().equalsIgnoreCase(getResources().getString(R.string.buyer_retailer))) {
                            BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(discount.getId(), re_cash_discount, discount.getBuyer_type(), re_days, re_cr_discount);
                            updateDiscount(discountRequest);
                        }

                    }
                }

                // Create Discount
                if (!isWholesaler) {
                    BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(public_cash_discount, getResources().getString(R.string.buyer_public), public_days, public_cr_discount);
                    createDiscount(discountRequest);
                }
                if (!isBroker) {
                    BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(broker_cash_discount, getResources().getString(R.string.buyer_broker), broker_days, broker_cr_discount);
                    createDiscount(discountRequest);
                }
                if (!isWholesaler) {
                    BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(wh_cash_discount, getResources().getString(R.string.buyer_wholesaler), wh_days, wh_cr_discount);
                    createDiscount(discountRequest);
                }
                if (!isRetailer) {
                    BuyerDiscountRequest discountRequest = new BuyerDiscountRequest(re_cash_discount, getResources().getString(R.string.buyer_retailer), re_days, re_cr_discount);
                    createDiscount(discountRequest);
                }
                Toast.makeText(getActivity(), "Discount setting updated Sucessfully.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }

    private boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    public void fetchDiscount() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.BUYER_DISCOUNT_URL, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                buyerDiscountRequests = Application_Singleton.gson.fromJson(response, BuyerDiscountRequest[].class);
                for (BuyerDiscountRequest discount : buyerDiscountRequests) {
                    if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_wholesaler))) {
                        isWholesaler = true;
                        editWholesellerCd.setText(String.valueOf(Float.parseFloat(discount.getCash_discount())));
                        editWholesellerCrd.setText(String.valueOf(Float.parseFloat(discount.getDiscount())));
                        editWholesellerDayscr.setText(String.valueOf(Float.parseFloat(discount.getPayment_duration())));
                    }
                    if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_retailer))) {
                        isRetailer = true;
                        editRetailerCd.setText(String.valueOf(Float.parseFloat(discount.getCash_discount())));
                        editRetailerCrd.setText(String.valueOf(Float.parseFloat(discount.getDiscount())));
                        editRetailerDayscr.setText(String.valueOf(Float.parseFloat(discount.getPayment_duration())));
                    }

                    if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_broker))) {
                        isBroker = true;
                        editBrokerCd.setText(String.valueOf(Float.parseFloat(discount.getCash_discount())));
                        editBrokerCrd.setText(String.valueOf(Float.parseFloat(discount.getDiscount())));
                        editBrokerDayscr.setText(String.valueOf(Float.parseFloat(discount.getPayment_duration())));
                    }
                    if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_public))) {
                        isPublic = true;
                        editPublicCd.setText(String.valueOf(Float.parseFloat(discount.getCash_discount())));
                        editPublicCrd.setText(String.valueOf(Float.parseFloat(discount.getDiscount())));
                        editPublicDayscr.setText(String.valueOf(Float.parseFloat(discount.getPayment_duration())));
                    }

                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();

            }
        });
    }

    public void updateDiscount(BuyerDiscountRequest discount) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.BUYER_DISCOUNT_URL + discount.getId() + "/", Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(discount), JsonObject.class), headers, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void createDiscount(BuyerDiscountRequest discount) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new Gson().fromJson(
                Application_Singleton.gson.toJson(discount), new TypeToken<HashMap<String, String>>() {
                }.getType()
        );
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.BUYER_DISCOUNT_URL, params, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


}

