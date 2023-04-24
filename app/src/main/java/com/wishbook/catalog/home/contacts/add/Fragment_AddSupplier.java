package com.wishbook.catalog.home.contacts.add;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_suppliertypes;
import com.wishbook.catalog.commonmodels.SellingCompany;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.responses.Countries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_AddSupplier extends DialogFragment {


    private EditText input_buyername;
    private EditText input_phone;
    private Spinner spinner_buyertype;
    private AppCompatButton btn_discard;
    private AppCompatButton btn_add;
    private Listener listener;
    private TextView countrycodes;
    private Countries[] countries;
    private String CountryId = "1";
    public interface Listener{
        void onDismiss();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.addsupplierdialog);

        if(getArguments()!=null)
        {
             Toast.makeText(getActivity(),"Select your buyer type and add supplier",Toast.LENGTH_LONG).show();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.COMPANY_URL + getArguments().getString("id") + '/', null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        Log.v("sync response", response);
                        SellingCompany company = Application_Singleton.gson.fromJson(response, SellingCompany.class);
                        if (company.getCountry() != null) {
                            input_buyername.setText(company.getName().toString());
                            input_phone.setText(company.getPhone_number().toString());
                            CountryId = company.getCountry();


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    Log.v("sync response", error.getErrormessage());
                }
            });

        }
        input_buyername=(EditText)dialog.findViewById(R.id.input_buyername);
        input_phone=(EditText)dialog.findViewById(R.id.input_mobile);
        countrycodes = (TextView) dialog.findViewById(R.id.countrycodes);
        getCountries(getActivity());
        spinner_buyertype=(Spinner)dialog.findViewById(R.id.spinner_buyertype);
        getbuyergroups();

        btn_discard=(AppCompatButton)dialog.findViewById(R.id.btn_discard);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_add=(AppCompatButton)dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_buyername.getText().toString().equals("") && !input_phone.getText().toString().equals("")
                        && input_phone.getText().length()==10) {
                    if(ValidationUtils.validatePhone(input_phone.getText().toString())) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("supplier_name", input_buyername.getText().toString());
                        params.put("supplier_number", input_phone.getText().toString());
                        params.put("country", CountryId);
                        params.put("group_type", ((GroupIDs) spinner_buyertype.getSelectedItem()).getId());

                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", ""), params, headers, true, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
                                    Log.v("sync response", response);
                                    Toast.makeText(getActivity(), "Supplier invited successfully", Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.onDismiss();
                                    }
                                    dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                dismiss();
                                Toast.makeText(getActivity(), "Supplier already invited", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        input_phone.setError(getActivity().getResources().getString(R.string.invalid_mobile));
                    }
                } else {
                    if (input_buyername.getText().toString().equals("")) {
                        input_buyername.setError(getActivity().getResources().getString(R.string.field_empty));
                    }
                    if (input_phone.getText().toString().equals("")) {
                        input_phone.setError(getActivity().getResources().getString(R.string.field_empty));
                    }
                    if(input_phone.getText().length()>10 || input_phone.getText().length()<10)
                    {
                        input_phone.setError(getActivity().getResources().getString(R.string.invalid_mobile));
                    }
                }
            }
        });

        countrycodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final PopupMenu popup = new PopupMenu(getActivity(), countrycodes);
                    if (countries != null && countries.length > 0) {

                        for (int i = 0; i < countries.length; i++) {
                            String country = countries[i].getName();
                            popup.getMenu().add(0, i, 0, country);

                        }
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                CountryId = countries[item.getItemId()].getId();
                                Log.v("country selected", "" + CountryId);
                                String country = countries[item.getItemId()].getName();
                                countrycodes.setText(country);
                                return true;
                            }
                        });
                        popup.show();

                    }
                }


        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void getbuyergroups() {


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"grouptype",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    Log.v("sync response", response);
                    Type listOfProductObj = new TypeToken<ArrayList<GroupIDs>>() {
                    }.getType();
                    ArrayList<GroupIDs> buyergroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                    SpinAdapter_suppliertypes spinAdapter_buyergroups = new SpinAdapter_suppliertypes(getActivity(), R.layout.spinneritem, buyergroups);
                    spinner_buyertype.setAdapter(spinAdapter_buyergroups);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("sync response", error.getErrormessage());
            }
        });



    }

    public void setListener(Listener listener) {
        this.listener=listener;
    }

    public void getCountries(final Context context) {
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.COUNTRIES, null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    countries = Application_Singleton.gson.fromJson(response, Countries[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }
}
