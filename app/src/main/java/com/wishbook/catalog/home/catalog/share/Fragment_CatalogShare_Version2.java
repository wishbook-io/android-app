package com.wishbook.catalog.home.catalog.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_buyergroups;
import com.wishbook.catalog.commonmodels.BuyerSeg;
import com.wishbook.catalog.commonmodels.postpatchmodels.PushObjectShare;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.appcompat.widget.AppCompatButton;

public class Fragment_CatalogShare_Version2 extends GATrackedFragment {


    View view;
    private String type = "group";
    private EditText date;
    private AppCompatButton share_go;
    private LinearLayout group, single;
    private TextView spinner_label;
    private Spinner spinner_buyergroups;
    TextView edit_buyername;
    TextInputLayout txt_input_buyer;

    // #### Start Variable Initialize #### //
    ArrayList<BuyerSeg> response_buyerGroups = new ArrayList<>();
    private BuyersList buyer = null;
    private String product_id;

    public Fragment_CatalogShare_Version2() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_share_catalog_new, container, false);
        share_go = (AppCompatButton) view.findViewById(R.id.share_go);
        date = (EditText) view.findViewById(R.id.date);
        spinner_buyergroups = (Spinner) view.findViewById(R.id.spinner_buyergroups);
        spinner_label = (TextView) view.findViewById(R.id.spinner_label);
        group = (LinearLayout) view.findViewById(R.id.group_share_layout);
        single = (LinearLayout) view.findViewById(R.id.single_share_layout);
        txt_input_buyer = (TextInputLayout) view.findViewById(R.id.input_buyername);
        edit_buyername = (TextView) view.findViewById(R.id.edit_buyername);
        final Calendar myCalendar = Calendar.getInstance();

        if (getArguments() != null && getArguments().getString("product_id") != null) {
            product_id = getArguments().getString("product_id");
        }

        if (date.getText().toString().equals("")) {
            updateLabel(myCalendar);
        }

        final LinearLayout buyer_container = (LinearLayout) view.findViewById(R.id.buyer_container);
        buyer_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        if (getArguments().getString("type") != null) {
            type = getArguments().getString("type");
            if (type.equals("group")) {
                group.setVisibility(View.VISIBLE);
                single.setVisibility(View.GONE);
                spinner_label.setText("Select buyers group");
                buyer = null;
            } else {
                type = "single";
                group.setVisibility(View.GONE);
                single.setVisibility(View.VISIBLE);
                spinner_label.setText("Search buyer");
            }
        }

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyergroups", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Type listOfProductObj = new TypeToken<ArrayList<BuyerSeg>>() {
                }.getType();

                response_buyerGroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                if (response_buyerGroups != null) {
                    if (response_buyerGroups.size() > 0) {
                        SpinAdapter_buyergroups spinAdapter_buyergroups = new SpinAdapter_buyergroups(getActivity(), R.layout.spinneritem, response_buyerGroups);
                        spinner_buyergroups.setAdapter(spinAdapter_buyergroups);
                        spinner_buyergroups.setSelection(response_buyerGroups.size() - 1);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

        if (product_id == null) {
            share_go.setEnabled(false);
        } else {
            share_go.setEnabled(true);
        }

        share_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("group")) {
                    if ((BuyerSeg) spinner_buyergroups.getSelectedItem() == null) {
                        Toast.makeText(getContext(), "Please select buyer group", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (type.equals("single")) {
                    if (buyer == null) {
                        Toast.makeText(getContext(), "Please select buyer", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                callShareAPI();


            }
        });
        return view;

    }


    private void updateLabel(Calendar myCalendar) {
        Date today = null;
        Date setDate = null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c.getTime());
        try {
            today = df.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String form = sdf.format(myCalendar.getTime());

        try {
            setDate = sdf.parse(form);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!setDate.before(today)) {
            date.setText(form);
        }
    }

    public void callShareAPI() {
        try {
            if (!((BuyerSeg) spinner_buyergroups.getSelectedItem()).getActive_buyers().equals("0") || buyer != null) {
                if (spinner_buyergroups.getSelectedItem() != null || buyer != null) {
                    try {
                        PushObjectShare pushObject = new PushObjectShare();
                        if (type.equals("group")) {
                            pushObject.setBuyer_segmentation(((BuyerSeg) spinner_buyergroups.getSelectedItem()).getId());
                        }
                        if (type.equals("single")) {
                            pushObject.setSingle_company_push((buyer.getCompany_id()));
                        }
                        pushObject.setCatalog(new String[]{product_id});
                        pushObject.setFull_catalog_orders_only("true");
                        pushObject.setSms("yes");
                        pushObject.setChange_price_add_amount(0);
                        pushObject.setPush_downstream("yes");
                        pushObject.setPush_type("buyers");
                        pushObject.setStatus("Delivered");
                        pushObject.setExp_desp_date(date.getText().toString());
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "pushes", ""), new Gson().fromJson(new Gson().toJson(pushObject), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                Toast.makeText(getActivity(), "Shared successfully", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "unable to select buyer group", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No Buyers in the selected Group", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Dialog CatalogShare Request code" + requestCode + "\n Response Code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                if (edit_buyername != null) {
                    edit_buyername.setText(buyer.getCompany_name());
                }
            }
        }
    }
}
