package com.wishbook.catalog.home.more.brandDiscount;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import com.google.gson.JsonSyntaxException;
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
import com.wishbook.catalog.commonmodels.responses.Brand;
import com.wishbook.catalog.commonmodels.responses.RequestBrandDiscount;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscountExpand;
import com.wishbook.catalog.home.models.Response_Brands;

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

public class FragmentAddBrandDiscountVersion2 extends GATrackedFragment {

    private View v;


    @BindView(R.id.flexbox_brands)
    FlexboxLayout flexbox_brands;

    @BindView(R.id.btn_add_brands)
    AppCompatButton btn_add_brands;

    @BindView(R.id.linear_brands)
    LinearLayout linear_brands;


    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;

    @BindView(R.id.input_cash_discount)
    TextInputLayout input_cash_discount;

    @BindView(R.id.edit_cash_discount)
    EditText edit_cash_discount;

    @BindView(R.id.input_single_pc_cash_discount)
    TextInputLayout input_single_pc_cash_discount;

    @BindView(R.id.edit_single_pc_cash_discount)
    EditText edit_single_pc_cash_discount;


    final static int BRAND_REQUEST_CODE = 6000;
    final static int BUYER_REQUEST_CODE = 7000;

    private ArrayList<NameValues> selectedBrands;

    private boolean isCashDiscountValid;
    private boolean isCreditDiscountValid;
    private boolean isSinglePcDiscountValid;
    private boolean isEditMode;

    private String discountID;

    private Toolbar toolbar;

    ArrayList<NameValues> appliedDiscount_brands1;

    private boolean isAllbrandApplied = false;

    private ResponseBrandDiscountExpand oldresponseDiscount;


    /**
     * Take Two Argument when it's editmode
     * getArguments().getBoolean("isEdit")
     * getArguments().getString("discount_id")
     */

    public FragmentAddBrandDiscountVersion2() {
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
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_add_discount_version2, ga_container, true);
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
                setHasOptionsMenu(true);
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().setResult(Activity.RESULT_CANCELED);
                        getActivity().finish();
                    }
                });
            }
        }

        if (!isEditMode) {
            // NameValues nameValues = new NameValues("All brands", "-1");
            // ArrayList<NameValues> temp = new ArrayList<>();
            // temp.add(nameValues);
            // createDynamicBrandChips(temp);
        }

        getAllUsedBrand();
        checkIsAllBrandApply();
    }

    public void initListener() {
        final percentageTextWatcher cashDiscountTextwatcher = new percentageTextWatcher(edit_cash_discount, input_cash_discount);
        final percentageTextWatcher singlePcDiscountTextwatcher = new percentageTextWatcher(edit_single_pc_cash_discount, input_single_pc_cash_discount);
        KeyboardUtils.hideKeyboard(getActivity());
        showPublicDiscount();
        edit_cash_discount.addTextChangedListener(cashDiscountTextwatcher);
        edit_single_pc_cash_discount.addTextChangedListener(singlePcDiscountTextwatcher);


        btn_add_brands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode) {
                    if(appliedDiscount_brands1!=null && appliedDiscount_brands1.size() > 0) {
                        ArrayList<NameValues> tempApplied = new ArrayList<>(appliedDiscount_brands1);
                        if(selectedBrands!=null){
                            for (int i = 0; i < selectedBrands.size(); i++) {
                                if (selectedBrands != null && selectedBrands.size() > 0 && tempApplied != null && tempApplied.size() > 0) {
                                    for (int j = 0; j < tempApplied.size(); j++) {
                                        if (selectedBrands.get(i).getPhone().equals(tempApplied.get(j).getPhone())) {
                                            appliedDiscount_brands1.remove(tempApplied.get(j));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Intent brandIntent = new Intent(getActivity(), ActivityBuyerGroupSelect.class);
                brandIntent.putExtra("type", "brand");
                brandIntent.putExtra("previouslySelected", selectedBrands);
                brandIntent.putExtra("applyDiscountBrands", appliedDiscount_brands1);
                brandIntent.putExtra("isAllbrandApplied", isAllbrandApplied);
                startActivityForResult(brandIntent, BRAND_REQUEST_CODE);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedBrands == null) {
                    Toast.makeText(getActivity(), "Please select any one brand", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (selectedBrands.size() == 0) {
                        Toast.makeText(getActivity(), "Please select any one brand", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (edit_cash_discount.getText().toString().isEmpty()) {
                    edit_cash_discount.setError("Discount can't be empty");
                    edit_cash_discount.requestFocus();
                    return;
                } else {
                    if ((Double.parseDouble(edit_cash_discount.getText().toString()) < 5)) {
                        edit_cash_discount.setError("Please enter discount more than 5% ");
                        edit_cash_discount.requestFocus();
                        return;
                    }


                    if ((Double.parseDouble(edit_cash_discount.getText().toString()) > 100)) {
                        edit_cash_discount.setError("Discount can't be more than 100");
                        edit_cash_discount.requestFocus();
                        return;
                    }
                }

                if (!edit_single_pc_cash_discount.getText().toString().trim().isEmpty()) {
                    try {
                        if ((Double.parseDouble(edit_single_pc_cash_discount.getText().toString().trim()) > 100)) {
                            edit_single_pc_cash_discount.setError("Discount can't be more than 100");
                            edit_single_pc_cash_discount.requestFocus();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (isEditMode) {
                    // Patch Data

                    // validate WB-4847  (User can't decrease the discount)
                    if(oldresponseDiscount!=null && oldresponseDiscount.getDiscount_type().equals(Constants.DISCOUNT_TYPE_PUBLIC)) {
                        double oldFullCatalogDiscount = Double.parseDouble(oldresponseDiscount.getCash_discount());
                        double oldSingleCatalogDiscount = oldresponseDiscount.getSingle_pcs_discount();

                        if(!edit_cash_discount.getText().toString().isEmpty() &&  Double.parseDouble(edit_cash_discount.getText().toString().trim()) < oldFullCatalogDiscount ) {
                            edit_cash_discount.setError("To reduce the discount, please talk to your account manager.");
                            edit_cash_discount.requestFocus();
                            return;
                        }

                        if(!edit_single_pc_cash_discount.getText().toString().isEmpty()) {
                            if( Double.parseDouble(edit_single_pc_cash_discount.getText().toString().trim()) < oldSingleCatalogDiscount ) {
                                edit_single_pc_cash_discount.setError("To reduce the discount, please talk to your account manager.");
                                edit_single_pc_cash_discount.requestFocus();
                                return;
                            }
                        } else if(oldSingleCatalogDiscount > 0) {
                            edit_single_pc_cash_discount.setError("To reduce the discount, please talk to your account manager.");
                            edit_single_pc_cash_discount.requestFocus();
                            return;
                        }

                    }

                    // public Discount
                    if (!edit_cash_discount.getText().toString().isEmpty() && selectedBrands != null && selectedBrands.size() > 0) {
                        patchDiscountRule(discountID, true, true, edit_cash_discount.getText().toString(), null, selectedBrands, null, null,edit_single_pc_cash_discount.getText().toString());
                    }
                } else {
                    if (!edit_cash_discount.getText().toString().isEmpty() && selectedBrands != null && selectedBrands.size() > 0) {
                        // Add Discount
                      //  askDiscountNameDialog();
                        postDiscountRule(true, true, edit_cash_discount.getText().toString(),
                                null, selectedBrands, null, "",
                                edit_single_pc_cash_discount.getText().toString());
                    }

                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isEditMode && discountID != null) {
            ((OpenContainer) getActivity()).setSupportActionBar(((OpenContainer) getActivity()).toolbar);
            setHasOptionsMenu(true);
        }

    }

    public void showPublicDiscount() {
        linear_brands.setVisibility(View.VISIBLE);
        input_cash_discount.setVisibility(View.VISIBLE);
        edit_cash_discount.setVisibility(View.VISIBLE);
        edit_cash_discount.setText("");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*if (isEditMode) {
            inflater.inflate(R.menu.menu_brandwise_dsicount, menu);
        }*/
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


    public void askDiscountNameDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("")
                .content("Enter the name for this discount rule").inputRangeRes(1, 50, R.color.color_primary)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                    }
                }).positiveText("Done")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            postDiscountRule(true, true, edit_cash_discount.getText().toString(),
                                    null, selectedBrands, null, dialog.getInputEditText().getText().toString(),
                                    edit_single_pc_cash_discount.getText().toString());

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

        } else if (requestCode == FragmentAddBrandDiscount.BRAND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra("brand") != null) {
                ArrayList<NameValues> temp = new ArrayList<>();
                temp = (ArrayList<NameValues>) data.getSerializableExtra("brand");
                createDynamicBrandChips(temp);
            }
        }


    }

    public void createDynamicBrandChips(ArrayList<NameValues> nameValues) {
        selectedBrands = new ArrayList<>(nameValues);
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
                    if(appliedDiscount_brands1!=null && appliedDiscount_brands1.size() > 0){
                        if(appliedDiscount_brands1.contains(selectedBrands.get(i))){
                            appliedDiscount_brands1.remove(appliedDiscount_brands1.indexOf(selectedBrands.get(i)));
                        }
                    }
                    selectedBrands.remove(i);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        flexbox_brands.addView(btn_add_brands);
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
            String percentageValue = charSequence.toString().trim();
            if (percentageValue.isEmpty()) {
                if(editText.getId() == R.id.edit_cash_discount) {
                    textInputLayout.setError("Discount can't be empty");
                    isValid = false;
                } else if (editText.getId() == R.id.edit_single_pc_cash_discount) {
                    isValid = true;
                }
            } else if (percentageValue.length() > 1 && Double.parseDouble(percentageValue) > 100) {
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
            } else if(editText.getId() == R.id.edit_single_pc_cash_discount) {
                isSinglePcDiscountValid = isValid;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    public void postDiscountRule(boolean isPublicDiscount, boolean isAllbrands, String cashDiscount,
                                 String creditDiscount, ArrayList<NameValues> brands,
                                 ArrayList<NameValues> buyerGroup, String discountName, String singlePcDiscount) {
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


        if (isPublicDiscount) {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PUBLIC);
            if (brands != null && brands.size() > 0) {
                if (brands.size() == 1) {
                    if (brands.get(0).getPhone().equals("-1")) {
                        // All Brands
                        requestBrandDiscount.setAll_brands(isAllbrands);
                    } else {
                        ArrayList<String> brandID = new ArrayList<>();
                        for (int i = 0; i < brands.size(); i++) {
                            brandID.add(brands.get(i).getPhone());
                        }
                        requestBrandDiscount.setBrands(brandID);
                    }
                } else {
                    ArrayList<String> brandID = new ArrayList<>();
                    for (int i = 0; i < brands.size(); i++) {
                        brandID.add(brands.get(i).getPhone());
                    }
                    requestBrandDiscount.setBrands(brandID);
                }
            }
            requestBrandDiscount.setCash_discount(cashDiscount);

            if(singlePcDiscount!=null && !singlePcDiscount.isEmpty()) {
                requestBrandDiscount.setSingle_pcs_discount(singlePcDiscount);
            } else {
                requestBrandDiscount.setSingle_pcs_discount("0");
            }

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
                hideProgress();
                if (isAdded() && !isDetached()) {
                    Type type = new TypeToken<ResponseBrandDiscountExpand>() {
                    }.getType();
                    oldresponseDiscount = Application_Singleton.gson.fromJson(response, type);
                    if (oldresponseDiscount.getDiscount_type().equals(Constants.DISCOUNT_TYPE_PUBLIC)) {
                        // Public Discount Set
                        edit_cash_discount.setText(String.valueOf(Float.parseFloat(oldresponseDiscount.getCash_discount())));
                        if(oldresponseDiscount.getSingle_pcs_discount() > 0) {
                            edit_single_pc_cash_discount.setText(String.valueOf(oldresponseDiscount.getSingle_pcs_discount()));
                        }
                        if (oldresponseDiscount.getAll_brands()) {
                            NameValues nameValues = new NameValues("All brands", "-1");
                            ArrayList<NameValues> temp = new ArrayList<>();
                            temp.add(nameValues);
                            createDynamicBrandChips(temp);
                        } else {
                            ArrayList<Brand> temp = oldresponseDiscount.getBrands();
                            ArrayList<NameValues> temp1 = new ArrayList<>();
                            for (int i = 0; i < temp.size(); i++) {
                                NameValues nameValues = new NameValues(temp.get(i).getName(), temp.get(i).getId());
                                temp1.add(nameValues);
                            }

                            createDynamicBrandChips(temp1);
                        }

                    } else {
                        // Private Discount Set

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
                hideProgress();
                if (isAdded() && !isDetached()) {
                    Toast.makeText(getActivity(), "Discount Rule Deleted Successfully", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void patchDiscountRule(String discountRuleID, boolean isPublicDiscount,
                                  boolean isAllbrands, String cashDiscount,
                                  String creditDiscount, ArrayList<NameValues> brands,
                                  ArrayList<NameValues> buyerGroup, String discountName, String singlePcDiscount) {
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


        if (isPublicDiscount) {
            requestBrandDiscount.setDiscount_type(Constants.DISCOUNT_TYPE_PUBLIC);
            if (brands != null && brands.size() > 0) {
                if (brands.size() == 1) {
                    if (brands.get(0).getPhone().equals("-1")) {
                        // All Brands
                        requestBrandDiscount.setAll_brands(isAllbrands);
                    } else {
                        ArrayList<String> brandID = new ArrayList<>();
                        for (int i = 0; i < brands.size(); i++) {
                            brandID.add(brands.get(i).getPhone());
                        }
                        requestBrandDiscount.setBrands(brandID);
                    }
                } else {
                    ArrayList<String> brandID = new ArrayList<>();
                    for (int i = 0; i < brands.size(); i++) {
                        brandID.add(brands.get(i).getPhone());
                    }
                    requestBrandDiscount.setBrands(brandID);
                }
            }
            requestBrandDiscount.setCash_discount(cashDiscount);
            if(singlePcDiscount!=null
                    && !singlePcDiscount.isEmpty()) {
                requestBrandDiscount.setSingle_pcs_discount(singlePcDiscount);
            }

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
                Toast.makeText(getActivity(), "Discount Rule Updated Successfully", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
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

    public void getAllUsedBrand() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "?type=brandwisediscount", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(getActivity());
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    hideProgress();
                    try {
                        Type type = new TypeToken<ArrayList<Response_Brands>>() {
                        }.getType();
                        ArrayList<Response_Brands> appliedDiscount_brands = Application_Singleton.gson.fromJson(response, type);
                        appliedDiscount_brands1 = new ArrayList<>();
                        if (appliedDiscount_brands.size() > 0) {
                            for (int i = 0; i < appliedDiscount_brands.size(); i++) {
                                NameValues temp = new NameValues(appliedDiscount_brands.get(i).getName(), appliedDiscount_brands.get(i).getId());
                                temp.setDiscount_rules(appliedDiscount_brands.get(i).getDiscount_rules());
                                appliedDiscount_brands1.add(temp);
                            }
                        }

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (isAdded() && !isDetached()) {
                    hideProgress();
                    StaticFunctions.showResponseFailedDialog(error);
                }
            }
        });
    }

    public void checkIsAllBrandApply() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.DISCOUNT_RULE + "?expand=true&all_brands=true ", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ArrayList<ResponseBrandDiscountExpand> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseBrandDiscountExpand>>() {
                    }.getType());

                    if (responseData.size() > 0) {
                        isAllbrandApplied = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


}