package com.wishbook.catalog.reseller;

import android.app.Dialog;
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
import android.widget.Toast;

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
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerAddDialog extends GATrackedDialogFragment {


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

    @BindView(R.id.btn_positive)
    LinearLayout btn_positive;

    ResellerAddAddressListener resellerAddAddressListener;


    public ResellerAddDialog() {
    }

    public static ResellerAddDialog newInstance(String string) {
        ResellerAddDialog f = new ResellerAddDialog();
        if (string != null) {
            Bundle args = new Bundle();
            f.setArguments(args);
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
        return view;
    }


    private void initView() {
        getStates(spinner_state, spinner_city);
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
                    if (((Response_States) spinner_state.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                        spinner_state.requestFocus();
                        Toast.makeText(getActivity(), "Please select State", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    if (((Response_Cities) spinner_city.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                        spinner_city.requestFocus();
                        Toast.makeText(getActivity(), "Please select City", Toast.LENGTH_SHORT).show();
                        return;
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
                callPostAddress(edit_address.getText().toString(),
                        edit_customer_name.getText().toString(),
                        edit_customer_phone.getText().toString(), edit_pincode.getText().toString());
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

    public interface ResellerAddAddressListener {
        void onAdd(boolean check);
    }

    public void setResellerAddAddressListener(ResellerAddAddressListener resellerAddAddressListener) {
        this.resellerAddAddressListener = resellerAddAddressListener;
    }


    public void callPostAddress(String address, String name, String phone_number, String pincode) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("street_address", address);
        params.put("pincode", pincode);
        params.put("city", cityId);
        params.put("state", stateId);
        params.put("phone_number", phone_number);
        params.put("country", "1");
        params.put("name", name);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "address", ""), params, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    dismiss();
                    if (resellerAddAddressListener != null) {
                        resellerAddAddressListener.onAdd(true);
                    }
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

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("AddAddress", "Exception", e);
        }
    }
}