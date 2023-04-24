package com.wishbook.catalog.home.address;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedDialogFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.PinCodeZone;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryAddressAddDialog extends GATrackedDialogFragment {


    Response_States[] allstates;
    Response_Cities[] allcities;
    private String State = "";
    private String City = "";
    private String stateId = "";
    private String cityId = "";

    @BindView(R.id.spinner_city)
    Spinner spinner_city;

    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    @BindView(R.id.edit_address)
    EditText edit_address;

    @BindView(R.id.edit_pincode)
    EditText edit_pincode;

    @BindView(R.id.edit_customer_phone)
    EditText edit_customer_phone;

    @BindView(R.id.edit_customer_name)
    EditText edit_customer_name;

    @BindView(R.id.btn_negative)
    LinearLayout btn_negative;

    @BindView(R.id.txt_dialog_title)
    TextView txt_dialog_title;

    @BindView(R.id.btn_positive)
    LinearLayout btn_positive;

    DeliveryAddAddressListener deliveryAddAddressListener;
    boolean isEditMode = false;
    String address_id = null;


    public DeliveryAddressAddDialog() {

    }

    public static DeliveryAddressAddDialog newInstance(Bundle bundle) {
        DeliveryAddressAddDialog f = new DeliveryAddressAddDialog();
        if (bundle != null) {
            f.setArguments(bundle);
        }
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reseller_add_new_address);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(getContext(), R.layout.dialog_reseller_add_new_address, null);
        ButterKnife.bind(this, view);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.dialog_reseller_add_new_address, null);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }


    private void initView() {
        if (getArguments() != null && getArguments().getBoolean("isEditMode") && getArguments().getString("address_id") != null) {
            isEditMode = true;
            address_id = getArguments().getString("address_id");
            txt_dialog_title.setText("Edit delivery address".toUpperCase());
            getAddress(getArguments().getString("address_id"));
        } else {
            txt_dialog_title.setText("Add new delivery address".toUpperCase());
            edit_customer_phone.setText(UserInfo.getInstance(getActivity()).getMobile());
            getStates(spinner_state, spinner_city);
        }
    }

    private void initListener() {
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ValidationUtils.isEmptyValidation(edit_customer_name)) {
                    edit_customer_name.requestFocus();
                    edit_customer_name.setError("Please Enter name");
                    return;
                }

                if (ValidationUtils.isEmptyValidation(edit_customer_phone)) {
                    edit_customer_phone.requestFocus();
                    edit_customer_phone.setError("Please Enter Phone number");
                    return;
                } else {
                    if (edit_customer_phone.getText().toString().length() != 10) {
                        edit_customer_phone.requestFocus();
                        edit_customer_phone.setError("Invalid Phone number");
                        return;
                    }
                }


                if (ValidationUtils.isEmptyValidation(edit_address)) {
                    edit_address.requestFocus();
                    edit_address.setError("Please Enter Address");
                    return;
                } else {
                    if (edit_address.getText().toString().length() < 15) {
                        edit_address.requestFocus();
                        edit_address.setError("Hmmm... How is your address this small? Please enter a valid address.");
                        return;
                    }
                }


                try {
                    if (((Response_States) spinner_state.getSelectedItem()) == null) {
                        Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
                        spinner_state.requestFocus();
                        return;
                    } else {
                        String state = ((Response_States) spinner_state.getSelectedItem()).getId();
                        String stateName = ((Response_States) spinner_state.getSelectedItem()).getState_name();
                        if (state != null && state.equals("")) {
                            Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
                            spinner_state.requestFocus();
                            return;
                        } else if (stateName.equals("-")) {
                            Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
                            spinner_state.requestFocus();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    if (((Response_Cities) spinner_city.getSelectedItem()) == null) {
                        Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
                        spinner_city.requestFocus();
                        return;
                    } else {
                        String city = ((Response_Cities) spinner_city.getSelectedItem()).getId();
                        String cityName = ((Response_Cities) spinner_city.getSelectedItem()).getCity_name();
                        if (city != null && city.equals("")) {
                            Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
                            spinner_city.requestFocus();
                            return;
                        } else if (cityName.equals("-")) {
                            Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
                            spinner_city.requestFocus();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (ValidationUtils.isEmptyValidation(edit_pincode)) {
                    edit_pincode.requestFocus();
                    edit_pincode.setError("Please enter valid pincode");
                    return;
                }

                if (edit_pincode.getText().toString().length() != 6) {
                    edit_pincode.requestFocus();
                    edit_pincode.setError("Please enter valid pincode");
                    return;
                }

                checkPinCode(getActivity(), edit_pincode.getText().toString(), ((Response_Cities) spinner_city.getSelectedItem()).getId());
            }
        });
    }

    private void getStates(final Spinner spinner_state, final Spinner spinner_city) {
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    if (allstates != null) {
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State(getActivity(), R.layout.spinneritem, R.id.spintext, allstates);
                        spinner_state.setAdapter(spinAdapter_state);
                    }
                    if (allstates != null) {
                        if (State != null) {
                            for (int i = 0; i < allstates.length; i++) {
                                if (State.equals(allstates[i].getState_name())) {
                                    spinner_state.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    Log.i("TAG", "onItemSelected:  State" + stateId);

                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                                if (allcities != null) {
                                    SpinAdapter_City spinAdapter_city = new SpinAdapter_City(getActivity(), R.layout.spinneritem, allcities);
                                    spinner_city.setAdapter(spinAdapter_city);
                                    if (State != null && State != "") {
                                        if (City != null) {
                                            for (int i = 0; i < allcities.length; i++) {
                                                if (City.equals(allcities[i].getCity_name())) {
                                                    spinner_city.setSelection(i);
                                                    break;
                                                }

                                            }
                                        }
                                    }
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = allcities[position].getId();
                HashMap<String, String> params = new HashMap<>();
                params.put("city", cityId);
                params.put("state", stateId);
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "companylist", ""), params, null, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface DeliveryAddAddressListener {
        void onAdd(String address_id);
    }

    public void setDeliveryAddAddressListener(DeliveryAddAddressListener deliveryAddAddressListener) {
        this.deliveryAddAddressListener = deliveryAddAddressListener;
    }


    public void getAddress(final String delivery_address_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", "") + delivery_address_id + "/", null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ShippingAddressResponse shippingAddressResponses = Application_Singleton.gson.fromJson(response, ShippingAddressResponse.class);
                    setData(shippingAddressResponses);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                try {
                    StaticFunctions.showResponseFailedDialog(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setData(ShippingAddressResponse addressResponse) {
        edit_customer_name.setText(addressResponse.getName());
        String phoneNumber = addressResponse.getPhone_number();
        if (phoneNumber != null && phoneNumber.length() >= 10) {
            String phoneNumbertrim = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
            edit_customer_phone.setText(phoneNumbertrim);
        } else {
            edit_customer_phone.setText(addressResponse.getPhone_number());
        }

        edit_address.setText(addressResponse.getStreet_address());
        if (addressResponse.getState() != null)
            State = addressResponse.getState().getState_name();
        if (addressResponse.getCity() != null)
            City = addressResponse.getCity().getCity_name();
        getStates(spinner_state, spinner_city);

        edit_pincode.setText(addressResponse.getPincode());

    }

    public void saveAddress(String address, String name, String phone_number, String pincode) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("street_address", address);
        params.put("pincode", pincode);
        params.put("city", cityId);
        params.put("state", stateId);
        params.put("phone_number", phone_number);
        params.put("country", "1");
        params.put("name", name);


        // Decide to post or patch
        String url;
        HttpManager.METHOD method = HttpManager.METHOD.POSTWITHPROGRESS;
        if (isEditMode) {
            method = HttpManager.METHOD.PATCHWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "address", "") + address_id + "/";
            final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
            progress_dialog.show();
            HttpManager.getInstance(getActivity()).requestPatch(method, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if(isAdded() && !isDetached()) {
                        if (progress_dialog != null) {
                            progress_dialog.dismiss();
                        }
                        try {
                            AddressResponse newAddress = Application_Singleton.gson.fromJson(response, AddressResponse.class);
                            dismiss();
                            if (deliveryAddAddressListener != null) {
                                deliveryAddAddressListener.onAdd(newAddress.getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } else {
            method = HttpManager.METHOD.POSTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "address", "");

            HttpManager.getInstance(getActivity()).request(method, url, params, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if(isAdded() && !isDetached()) {
                        AddressResponse newAddress = Application_Singleton.gson.fromJson(response, AddressResponse.class);
                        try {
                            dismiss();
                            if (deliveryAddAddressListener != null) {
                                deliveryAddAddressListener.onAdd(newAddress.getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * WB-4262 mapped city,state with pincode
     */
    public void checkPinCode(Context context, String pincode, final String selected_city) {
        final boolean[] isSamecity = {true};
        final String state_id = ((Response_States) spinner_state.getSelectedItem()).getId();
        final String city_id = ((Response_Cities) spinner_city.getSelectedItem()).getId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> params = new HashMap<>();
        params.put("pincode", pincode);
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getContext());
        progressDialog.show();
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.PINCODE_ZONE, params, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    final ArrayList<PinCodeZone> pinCodeZones = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<PinCodeZone>>() {
                    }.getType());

                    if (pinCodeZones != null && pinCodeZones.size() > 0) {
                        if (!pinCodeZones.get(0).getCity().equalsIgnoreCase(selected_city)) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(getActivity());
                            }
                            builder.setTitle("")
                                    .setMessage("We have updated the city since the pincode you entered is of a different city.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            isSamecity[0] = false;
                                            cityId = pinCodeZones.get(0).getCity();
                                            stateId = pinCodeZones.get(0).getState();
                                            saveAddress(edit_address.getText().toString(),
                                                    edit_customer_name.getText().toString(),
                                                    edit_customer_phone.getText().toString(), edit_pincode.getText().toString());
                                        }
                                    })
                                    .show();
                        } else {
                            // continue old state
                            saveAddress(edit_address.getText().toString(),
                                    edit_customer_name.getText().toString(),
                                    edit_customer_phone.getText().toString(), edit_pincode.getText().toString());
                        }
                    } else {
                        // continue old state
                        saveAddress(edit_address.getText().toString(),
                                edit_customer_name.getText().toString(),
                                edit_customer_phone.getText().toString(), edit_pincode.getText().toString());
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                saveAddress(edit_address.getText().toString(),
                        edit_customer_name.getText().toString(),
                        edit_customer_phone.getText().toString(), edit_pincode.getText().toString());

            }
        });
    }
}
