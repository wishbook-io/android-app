package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Categories;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brands;
import com.wishbook.catalog.home.adapters.CatalogsSharedAdapter;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.models.Response_Followed_Brands;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.commonmodels.CategoryTree;

/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_AddFilter extends DialogFragment {

    public static CatalogsSharedAdapter catalogsSharedAdapter;
    private int min=0;
    private int max=100000;
    private Boolean is_disable=false;
    private Boolean is_supplier_approved=false;
    private Boolean from_brand=false;
    Spinner spinner_brands;
    private Boolean is_buyer_disabled=false;
    private Boolean supplier_disabled=false;
    private LinearLayout linear_showDisabled;
    private boolean show_trusted_seller = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.filter_dialog);
        setToolbar(dialog);
        AppCompatButton apply_filter = (AppCompatButton) dialog.findViewById(R.id.btn_apply);
        AppCompatButton discard = (AppCompatButton) dialog.findViewById(R.id.btn_discard);
        final EditText fabric = (EditText) dialog.findViewById(R.id.input_fabric);
        final EditText work = (EditText) dialog.findViewById(R.id.input_work);
        final EditText catalog_name = (EditText) dialog.findViewById(R.id.input_catalog_name);
        final LinearLayout public_supplier_container = (LinearLayout) dialog.findViewById(R.id.public_supplier_container);
         RadioGroup radiogroupprice = (RadioGroup) dialog.findViewById(R.id.radiogroup);
         RadioGroup radiogroupsupplier= (RadioGroup) dialog.findViewById(R.id.radioGroup_public);
       // final RangeSeekBar rangeSeekBar = (RangeSeekBar) dialog.findViewById(R.id.pricerangebar);
        final TextInputLayout spinner_brand_container = (TextInputLayout) dialog.findViewById(R.id.spinner_brand_container);
        final TextView show_only = (TextView) dialog.findViewById(R.id.show_only);
        final CheckBox checkBuyerDisable = (CheckBox) dialog.findViewById(R.id.check_buyer_disable);
        final CheckBox checkSupplierDisable = (CheckBox) dialog.findViewById(R.id.check_supplier_disable);
        final CheckBox  checkTrustedSeller= (CheckBox) dialog.findViewById(R.id.check_trusted_seller);
        linear_showDisabled = (LinearLayout) dialog.findViewById(R.id.linear_showDisabled);
        spinner_brands = (Spinner) dialog.findViewById(R.id.spinner_brands);

        final List<FileBean> mDatas = new ArrayList<>();
        final Spinner spinner_cat = (Spinner) dialog.findViewById(R.id.spinner_category);



     /*   rangeSeekBar.setRangeValues(100,10000);
        rangeSeekBar.setSelectedMinValue(100);
        rangeSeekBar.setSelectedMaxValue(10000);
        rangeSeekBar.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);


        rangeSeekBar.setNotifyWhileDragging(true);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                rangeSeekBar.resetSelectedValues();
                rangeSeekBar.setSelectedMaxValue(maxValue);
                rangeSeekBar.setSelectedMinValue(minValue);
                min = minValue;
                max = maxValue;
            }
        });*/

        getBrands();

        radiogroupprice.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                       if(checkedId==R.id.enabled_catalog){
                           is_disable=false;
                       }else{
                           is_disable=true;
                       }
                    }
                }
        );

        radiogroupsupplier.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId==R.id.supplier_approved_false){
                            is_supplier_approved=false;
                        }else{
                            is_supplier_approved=true;
                        }
                    }
                }
        );

        checkBuyerDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    is_buyer_disabled = true;
                } else{
                    is_buyer_disabled =false;
                }
            }
        });

        checkSupplierDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    supplier_disabled = true;
                } else{
                    supplier_disabled =false;
                }
            }
        });

        checkTrustedSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    show_trusted_seller = true;
                } else {
                    show_trusted_seller = false;
                }
            }
        });
        if(getArguments().getBoolean("from_public")){
            public_supplier_container.setVisibility(View.VISIBLE);
            radiogroupprice.setVisibility(View.GONE);
            show_only.setVisibility(View.GONE);
            linear_showDisabled.setVisibility(View.GONE);
            checkTrustedSeller.setVisibility(View.VISIBLE);
        }else{
            public_supplier_container.setVisibility(View.GONE);
        }

        if(getArguments().getBoolean("from_brand",false)){
            from_brand=true;
            spinner_brand_container.setVisibility(View.GONE);
        }else{
            spinner_brand_container.setVisibility(View.VISIBLE);
        }


        if(getArguments().getBoolean("from_received",false)){
            checkTrustedSeller.setVisibility(View.VISIBLE);
        }


        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HashMap<String, String> params = new HashMap<String, String>();
                if(spinner_cat.getSelectedItemPosition()!=0 && spinner_cat.getSelectedItem()!=null)
                {
                    params.put("category", "" + ((FileBean) (spinner_cat.getSelectedItem())).fileId);
                }
                if(!catalog_name.getText().toString().matches("")) {
                    params.put("title", "" + catalog_name.getText().toString());
                }
                if(!fabric.getText().toString().matches("")) {
                    params.put("fabric", "" + fabric.getText().toString());
                }
                if(!work.getText().toString().matches("")) {
                    params.put("work", "" + work.getText().toString());
                }
               /* if(min!=0) {
                    if (rangeSeekBar.getSelectedMinValue() != null) {
                        params.put("min_price", "" + min);
                    }
                }*/

                if(spinner_brands.getSelectedItem()!=null && !from_brand) {
                    if (spinner_brands.getSelectedItemPosition() != 0) {
                        params.put("brand", "" + ((Response_Brands) spinner_brands.getSelectedItem()).getId());
                    }
                }


                    /*if (rangeSeekBar.getSelectedMaxValue() != null) {
                        if(max == 10000){
                            params.put("max_price", "" + 100000);
                        } else {
                            params.put("max_price", "" + max);
                        }

                    }*/




                if(is_disable){
                    params.put("is_disable", String.valueOf(is_disable));
                }

                if(getArguments().getBoolean("from_public")){
                    params.put("is_supplier_approved", String.valueOf(is_supplier_approved));
                }

                if(is_buyer_disabled){
                    params.put("buyer_disabled",String.valueOf(is_buyer_disabled));
                }

                if(supplier_disabled){
                    params.put("supplier_disabled",String.valueOf(supplier_disabled));
                }

                if(show_trusted_seller) {
                    params.put("trusted_seller",String.valueOf(show_trusted_seller));
                }

                Intent i = new Intent()
                        .putExtra("parameters", params);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
              /*  HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.RECIEVED_CAT_APP, params, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        Intent i = new Intent()
                                .putExtra("response", response);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                         dismiss();

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
                });*/
              /*  recievedCatalogs.getReceivedCatalogs(params,getActivity());
                dismiss();*/
            }
        });

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"category","")+"?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

                CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                if (ct != null) {
                    if (ct.length > 0) {
                        if (ct[0].getId() != null) {
                            FileBean fileBean;
                            fileBean = new FileBean(0,-1,"All");
                            mDatas.add(fileBean);
                            for (int i = 0; i < ct.length; i++) {
                                CategoryTree ctitem = ct[i];
                                if (ctitem.getparent_category() == null) {
                                    fileBean = new FileBean(ctitem.getId(), -1, ctitem.getcategory_name());
                                    mDatas.add(fileBean);
                                } else {
                                    fileBean = new FileBean(ctitem.getId(), ctitem.getparent_category(), ctitem.getcategory_name());
                                    mDatas.add(fileBean);
                                }
                            }
                        }
                    }
                }
                SpinAdapter_Categories spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mDatas);
                spinner_cat.setAdapter(spinAdapter_categories);
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

                return dialog;
    }

    private void getBrands() {
        String url;
        if(getArguments().getBoolean("from_public")){
            url =  URLConstants.companyUrl(getActivity(),"brands","")+"?type=public";
        }else if (getArguments().getBoolean("from_followed_brand")){
            url = URLConstants.companyUrl(getActivity(),"brands-follow","");
        }else {
            url =  URLConstants.companyUrl(getActivity(),"brands","");
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                if(getArguments().getBoolean("from_followed_brand")) {
                    Response_Followed_Brands[] response_followed_brandses = Application_Singleton.gson.fromJson(response, Response_Followed_Brands[].class);
                    Response_Brands[] response_brands = new Response_Brands[response_followed_brandses.length];
                    for (int i = 0; i < response_followed_brandses.length; i++){
                        Response_Brands br =new Response_Brands(response_followed_brandses[i].getBrand(), response_followed_brandses[i].getBrand_name(), response_followed_brandses[i].getCompany(), response_followed_brandses[i].getId());
                        response_brands[i]= br;
                    }
                    ArrayList<Response_Brands> brands = new ArrayList<Response_Brands>(Arrays.asList(response_brands));
                    brands.add(0,new Response_Brands("Select Brand"));
                    Response_Brands[] response_brands_new = brands.toArray(new Response_Brands[brands.size()]);
                    if (response_followed_brandses.length > 0) {
                        if (response_followed_brandses[0].getBrand() != null) {
                            SpinAdapter_brands spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, response_brands_new);
                            spinner_brands.setAdapter(spinAdapter_brands);

                        }
                    }
                } else {
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    ArrayList<Response_Brands> brands = new ArrayList<Response_Brands>(Arrays.asList(response_brands));
                    brands.add(0,new Response_Brands("Select Brand"));
                    Response_Brands[] response_brands_new = brands.toArray(new Response_Brands[brands.size()]);
                    if (response_brands.length > 0) {
                        if (response_brands[0].getId() != null) {
                            // todo set filter brand spinner

                            SpinAdapter_brands spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, response_brands_new);
                            spinner_brands.setAdapter(spinAdapter_brands);

                        }
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void setToolbar(Dialog view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Filter");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


}
