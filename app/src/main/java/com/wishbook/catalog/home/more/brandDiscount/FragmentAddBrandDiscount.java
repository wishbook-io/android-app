package com.wishbook.catalog.home.more.brandDiscount;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.RequestBrandDiscount;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscountExpand;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;
import lib.kingja.switchbutton.SwitchMultiButton;

public class FragmentAddBrandDiscount extends GATrackedFragment {

    private View v;
    @BindView(R.id.switch_mode_discount)
    SwitchMultiButton switch_mode_discount;

    @BindView(R.id.flexbox_buyer)
    FlexboxLayout flexbox_buyer;

    @BindView(R.id.btn_add_buyers)
    AppCompatButton btn_add_buyers;

    @BindView(R.id.flexbox_brands)
    FlexboxLayout flexbox_brands;

    @BindView(R.id.btn_add_brands)
    AppCompatButton btn_add_brands;

    @BindView(R.id.linear_brands)
    LinearLayout linear_brands;

    @BindView(R.id.linear_buyers)
    LinearLayout linear_buyers;

    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;

    @BindView(R.id.input_cash_discount)
    TextInputLayout input_cash_discount;

    @BindView(R.id.edit_cash_discount)
    EditText edit_cash_discount;

    @BindView(R.id.input_credit_discount)
    TextInputLayout input_credit_discount;

    @BindView(R.id.edit_credit_discount)
    EditText edit_credit_discount;


    final static int BRAND_REQUEST_CODE = 6000;
    final static int BUYER_REQUEST_CODE = 7000;

    private ArrayList<NameValues> selectedBrands;
    private ArrayList<NameValues> selectedBuyers;

    private boolean isCashDiscountValid;
    private boolean isCreditDiscountValid;
    private boolean isEditMode;

    private String discountID;

    private Toolbar toolbar;


    /**
     * Take Two Argument when it's editmode
     * getArguments().getBoolean("isEdit")
     * getArguments().getString("discount_id")
     */

    public FragmentAddBrandDiscount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_add_discount, container, false);
        ButterKnife.bind(this, v);
        initView();
        initListener();
        return v;
    }

    public void initView() {
        if (getArguments() != null) {
            if (getArguments().getBoolean("isEdit")) {
                isEditMode = true;
            }
            if (getArguments().getString("discount_id") != null) {
                discountID = getArguments().getString("discount_id");
            }
        }


        if (isEditMode && discountID != null) {
            getDiscountRule(discountID);
            if (getActivity() instanceof OpenContainer) {
                toolbar = ((OpenContainer) getActivity()).toolbar;
                toolbar.getMenu().clear();
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                setHasOptionsMenu(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().setResult(Activity.RESULT_CANCELED);
                        getActivity().finish();
                    }
                });
            }
        }
    }

    public void initListener() {
        final percentageTextWatcher cashDiscountTextwatcher = new percentageTextWatcher(edit_cash_discount, input_cash_discount);
        final percentageTextWatcher creditDiscountTextwatcher = new percentageTextWatcher(edit_credit_discount, input_credit_discount);
        switch_mode_discount.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String s) {
                if (position == 0) {
                    KeyboardUtils.hideKeyboard(getActivity());
                    showPublicDiscount();
                    edit_cash_discount.addTextChangedListener(cashDiscountTextwatcher);
                    edit_credit_discount.removeTextChangedListener(creditDiscountTextwatcher);
                } else {
                    KeyboardUtils.hideKeyboard(getActivity());
                    showPrivateDiscount();
                    edit_cash_discount.addTextChangedListener(cashDiscountTextwatcher);
                    edit_credit_discount.addTextChangedListener(creditDiscountTextwatcher);
                }
            }
        });

        btn_add_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent brandIntent = new Intent(getActivity(), ActivityBuyerGroupSelect.class);
                brandIntent.putExtra("type", "buyer_group");
                brandIntent.putExtra("previouslySelected",selectedBuyers);
                startActivityForResult(brandIntent, BUYER_REQUEST_CODE);
            }
        });


        btn_add_brands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent brandIntent = new Intent(getActivity(), ActivityBuyerGroupSelect.class);
                brandIntent.putExtra("type", "brand");
                brandIntent.putExtra("previouslySelected",selectedBrands);
                startActivityForResult(brandIntent, BRAND_REQUEST_CODE);
            }
        });

        switch_mode_discount.setSelectedTab(0);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode) {
                    // Patch Data
                    if (switch_mode_discount.getSelectedTab() == 0) {
                        // public Discount
                        if (isCashDiscountValid) {
                            patchDiscountRule(discountID,true, true, edit_cash_discount.getText().toString(), edit_credit_discount.getText().toString(), selectedBrands, selectedBuyers, null);
                        }
                    } else {
                        // Private Discount
                        if (isCashDiscountValid && isCreditDiscountValid) {
                            patchDiscountRule(discountID,false, true, edit_cash_discount.getText().toString(), edit_credit_discount.getText().toString(), selectedBrands, selectedBuyers, null);
                        }
                    }
                } else {
                    // Add Discount
                    askDiscountNameDialog();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    public void showPublicDiscount() {

        linear_brands.setVisibility(View.VISIBLE);
        linear_buyers.setVisibility(View.GONE);

        input_cash_discount.setVisibility(View.VISIBLE);
        edit_cash_discount.setVisibility(View.VISIBLE);

        input_credit_discount.setVisibility(View.GONE);
        edit_credit_discount.setVisibility(View.GONE);

        edit_credit_discount.setText("");
        edit_cash_discount.setText("");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isEditMode) {
            inflater.inflate(R.menu.menu_brandwise_dsicount, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                new MaterialDialog.Builder(getActivity())
                        .content("Do you want to delete this discount setting ?")
                        .negativeText("No")
                        .positiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                deleteDiscountRule(discountID);

                            }
                        })
                        .show();
                break;

        }
        return false;
    }

    public void showPrivateDiscount() {
        linear_brands.setVisibility(View.VISIBLE);
        linear_buyers.setVisibility(View.VISIBLE);

        input_cash_discount.setVisibility(View.VISIBLE);
        edit_cash_discount.setVisibility(View.VISIBLE);

        input_credit_discount.setVisibility(View.VISIBLE);
        edit_credit_discount.setVisibility(View.VISIBLE);
    }


    public void askDiscountNameDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("")
                .content("Enter the name of discount").inputRangeRes(1, 50, R.color.color_primary)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                    }
                }).positiveText("Done")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (switch_mode_discount.getSelectedTab() == 0) {
                            // public Discount
                            if (isCashDiscountValid) {
                                postDiscountRule(true, true, edit_cash_discount.getText().toString(), edit_credit_discount.getText().toString(), selectedBrands, selectedBuyers, dialog.getInputEditText().getText().toString());
                            }
                        } else {
                            // Private Discount
                            if (isCashDiscountValid && isCreditDiscountValid) {
                                postDiscountRule(false, true, edit_cash_discount.getText().toString(), edit_credit_discount.getText().toString(), selectedBrands, selectedBuyers, dialog.getInputEditText().getText().toString());
                            }
                        }
                    }
                })
                .negativeText("Cancel")
                .negativeColor(getResources().getColor(R.color.purchase_medium_gray))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FragmentAddBrandDiscount.BUYER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("buyer") != null) {
                /*if(selectedBuyers!=null && selectedBuyers.size() > 0) {
                    selectedBuyers.addAll((ArrayList<NameValues>) data.getSerializableExtra("buyer"));
                } else {
                    selectedBuyers = (ArrayList<NameValues>) data.getSerializableExtra("buyer");
                }*/
                selectedBuyers = (ArrayList<NameValues>) data.getSerializableExtra("buyer");
                flexbox_buyer.removeAllViews();
                ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                        .selectMode(ChipCloud.SelectMode.mandatory)
                        .checkedChipColor(Color.parseColor("#ddaa00"))
                        .checkedTextColor(Color.parseColor("#ffffff"))
                        .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                        .showClose(Color.parseColor("#a6a6a6"))
                        .useInsetPadding(true)
                        .uncheckedTextColor(Color.parseColor("#000000"));

                ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
                for (NameValues s : selectedBuyers) {
                    deleteableCloud.addChip(s.getName());
                }

                deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                    @Override
                    public void chipDeleted(int i, String s) {
                        try{
                            selectedBuyers.remove(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                flexbox_buyer.addView(btn_add_buyers);
            }
        } else if (requestCode == FragmentAddBrandDiscount.BRAND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("brand") != null) {
                /*if(selectedBrands!=null && selectedBrands.size() > 0) {
                    selectedBrands.addAll((ArrayList<NameValues>) data.getSerializableExtra("brand"));
                } else {
                    selectedBrands = (ArrayList<NameValues>) data.getSerializableExtra("brand");
                }*/
                selectedBrands = (ArrayList<NameValues>) data.getSerializableExtra("brand");
                flexbox_brands.removeAllViews();
                ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                        .selectMode(ChipCloud.SelectMode.mandatory)
                        .checkedChipColor(Color.parseColor("#ddaa00"))
                        .checkedTextColor(Color.parseColor("#ffffff"))
                        .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                        .showClose(Color.parseColor("#a6a6a6"))
                        .useInsetPadding(true)
                        .uncheckedTextColor(Color.parseColor("#000000"));

                ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_brands, deleteableConfig);
                for (NameValues s : selectedBrands) {
                    deleteableCloud.addChip(s.getName());
                }

                deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                    @Override
                    public void chipDeleted(int i, String s) {
                        try {
                            selectedBrands.remove(i-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                flexbox_brands.addView(btn_add_brands);
            }
        }



    }

    class percentageTextWatcher implements TextWatcher {

        EditText editText;
        TextInputLayout textInputLayout;
        boolean isValid;

        public percentageTextWatcher(EditText editText, TextInputLayout textInputLayout) {
            this.editText = editText;
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String percentageValue = charSequence.toString();
            if (percentageValue.isEmpty()) {
                textInputLayout.setError("Discount can't be empty");
                isValid = false;
            } else if (Double.parseDouble(percentageValue) > 100) {
                textInputLayout.setError("Discount can't be more than 100");
                isValid = false;
            } else {
                textInputLayout.setError(null);
                isValid = true;
            }

            if (editText.getId() == R.id.edit_cash_discount) {
                isCashDiscountValid = isValid;
            } else if (editText.getId() == R.id.edit_credit_discount) {
                isCreditDiscountValid = isValid;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    public void postDiscountRule(boolean isPublicDiscount, boolean isAllbrands, String cashDiscount, String creditDiscount, ArrayList<NameValues> brands, ArrayList<NameValues> buyerGroup, String discountName) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestBrandDiscount requestBrandDiscount = new RequestBrandDiscount();
        if (discountName != null) {
            requestBrandDiscount.setName(discountName);
        }

        if (buyerGroup != null && buyerGroup.size() > 0) {
            ArrayList<String> buyerGroupID = new ArrayList<>();
            for (int i = 0; i < buyerGroup.size(); i++) {
                buyerGroupID.add(buyerGroup.get(i).getPhone());
            }
            requestBrandDiscount.setBuyer_segmentations(buyerGroupID);
        }


        if(brands !=null && brands.size() > 0) {
            ArrayList<String> brandID = new ArrayList<>();
            for (int i = 0; i < brands.size(); i++) {
                brandID.add(brands.get(i).getPhone());
            }
            requestBrandDiscount.setBrands(brandID);
        }


        if (isPublicDiscount) {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PUBLIC);
            if (isAllbrands) {
                requestBrandDiscount.setAll_brands(isAllbrands);
            } else {
                // requestBrandDiscount.setBrands(brands);
            }
            requestBrandDiscount.setCash_discount(cashDiscount);

        } else {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PRIVATE);
            requestBrandDiscount.setCash_discount(cashDiscount);
            requestBrandDiscount.setCredit_discount(creditDiscount);
        }


        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.DISCOUNT_RULE, (Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestBrandDiscount), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                Toast.makeText(getActivity(), "Discount Rule Added", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void getDiscountRule(String discountRuleID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.DISCOUNT_RULE + discountRuleID + "/?expand=true";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Type type = new TypeToken<ResponseBrandDiscountExpand>() {
                        }.getType();
                        ResponseBrandDiscountExpand responseDiscount = Application_Singleton.gson.fromJson(response, type);
                        if (responseDiscount.getDiscount_type().equals(Constants.DISCOUNT_TYPE_PUBLIC)) {
                            // Public Discount Set
                            switch_mode_discount.setSelectedTab(0);
                            edit_cash_discount.setText(String.valueOf(Float.parseFloat(responseDiscount.getCash_discount())));
                        } else {
                            // Private Discount Set
                            switch_mode_discount.setSelectedTab(1);

                            edit_cash_discount.setText(String.valueOf(Float.parseFloat(responseDiscount.getCash_discount())));
                            edit_credit_discount.setText(String.valueOf(Float.parseFloat(responseDiscount.getCredit_discount())));

                            if (responseDiscount.getAll_brands()) {

                            } else {
                                ArrayList<NameValues> tempData = new ArrayList<>();
                                for (int i = 0; i < responseDiscount.getBrands().size(); i++) {
                                    tempData.add(new NameValues(responseDiscount.getBrands().get(i).getName(), responseDiscount.getBrands().get(i).getId()));
                                }

                                selectedBrands = tempData;
                                flexbox_brands.removeAllViews();
                                ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                                        .selectMode(ChipCloud.SelectMode.mandatory)
                                        .checkedChipColor(Color.parseColor("#ddaa00"))
                                        .checkedTextColor(Color.parseColor("#ffffff"))
                                        .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                                        .showClose(Color.parseColor("#a6a6a6"))
                                        .useInsetPadding(true)
                                        .uncheckedTextColor(Color.parseColor("#000000"));
                                ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_brands, deleteableConfig);
                                for (NameValues s : selectedBrands) {
                                    deleteableCloud.addChip(s.getName());
                                }

                                deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                                    @Override
                                    public void chipDeleted(int i, String s) {
                                        try {
                                            selectedBrands.remove(i - 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                flexbox_brands.addView(btn_add_brands);

                            }
                        }

                        if (responseDiscount.getBuyer_segmentations() != null && responseDiscount.getBuyer_segmentations().size() > 0) {
                            ArrayList<NameValues> tempData = new ArrayList<>();
                            for (int i = 0; i < responseDiscount.getBuyer_segmentations().size(); i++) {
                                tempData.add(new NameValues(responseDiscount.getBuyer_segmentations().get(i).getSegmentation_name(), responseDiscount.getBuyer_segmentations().get(i).getId()));
                            }
                            selectedBuyers = tempData;
                            flexbox_buyer.removeAllViews();
                            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                                    .selectMode(ChipCloud.SelectMode.mandatory)
                                    .checkedChipColor(Color.parseColor("#ddaa00"))
                                    .checkedTextColor(Color.parseColor("#ffffff"))
                                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                                    .showClose(Color.parseColor("#a6a6a6"))
                                    .useInsetPadding(true)
                                    .uncheckedTextColor(Color.parseColor("#000000"));

                            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
                            for (NameValues s : selectedBuyers) {
                                deleteableCloud.addChip(s.getName());
                            }

                            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                                @Override
                                public void chipDeleted(int i, String s) {
                                    try {
                                        selectedBuyers.remove(i - 1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            flexbox_buyer.addView(btn_add_buyers);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void deleteDiscountRule(String discountRuleID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.DISCOUNT_RULE + discountRuleID + "/";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.DELETEWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Toast.makeText(getActivity(), "Discount Rule Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void patchDiscountRule(String discountRuleID , boolean isPublicDiscount, boolean isAllbrands, String cashDiscount, String creditDiscount, ArrayList<NameValues> brands, ArrayList<NameValues> buyerGroup, String discountName) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestBrandDiscount requestBrandDiscount = new RequestBrandDiscount();
        requestBrandDiscount.setId(discountRuleID);
        if (discountName != null) {
            requestBrandDiscount.setName(discountName);
        }

        if (buyerGroup != null && buyerGroup.size() > 0) {
            ArrayList<String> buyerGroupID = new ArrayList<>();
            for (int i = 0; i < buyerGroup.size(); i++) {
                buyerGroupID.add(buyerGroup.get(i).getPhone());
            }
            requestBrandDiscount.setBuyer_segmentations(buyerGroupID);
        }


        if(brands !=null && brands.size() > 0) {
            ArrayList<String> brandID = new ArrayList<>();
            for (int i = 0; i < brands.size(); i++) {
                brandID.add(brands.get(i).getPhone());
            }
            requestBrandDiscount.setBrands(brandID);
        }


        if (isPublicDiscount) {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PUBLIC);
            if (isAllbrands) {
                requestBrandDiscount.setAll_brands(isAllbrands);
            } else {
                // requestBrandDiscount.setBrands(brands);
            }
            requestBrandDiscount.setCash_discount(cashDiscount);

        } else {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PRIVATE);
            requestBrandDiscount.setCash_discount(cashDiscount);
            requestBrandDiscount.setCredit_discount(creditDiscount);
        }
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.DISCOUNT_RULE + discountRuleID + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestBrandDiscount), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Toast.makeText(getActivity(), "Discount Rule Updated Successfully", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
}
