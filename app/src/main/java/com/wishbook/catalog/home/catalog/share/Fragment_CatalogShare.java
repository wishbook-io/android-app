package com.wishbook.catalog.home.catalog.share;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_buyergroups;
import com.wishbook.catalog.commonmodels.BuyerSeg;
import com.wishbook.catalog.commonmodels.postpatchmodels.PushObjectShare;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
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

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;


public class Fragment_CatalogShare extends GATrackedFragment {


    View dialog;

    private final String catID = "";
    private Spinner spinner_buyergroups;
    private EditText input_message;
    private CheckBox fullcatalog;
    private CheckBox changeprice;
    private CheckBox message;
    private RelativeLayout pricecontainer;
    private boolean changepriceFilter = false;
    private RadioGroup radiogroupprice;
    private RadioButton add_percentage;
    private RadioButton fixed_price;
    private boolean fixedRate = false;
    private AppCompatButton share_go;
    private EditText input_price;
    private TextView typeofShare;
    private SwitchCompat toggle;
    private TextView spinner_label;
    private LinearLayout group, single;
    //private CustomAutoCompleteTextView buyer_suggestion;
    AutoCompleteCommonAdapter adapter;
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private BuyersList buyer = null;
    private String type = "group";
    private EditText date;
    private boolean is_product_price_null = false;
    private boolean is_product_price_below100 = false;
    ArrayList<BuyerSeg> response_buyerGroups = new ArrayList<>();


    private CatalogMinified catalogMinified;

    TextView edit_buyername;
    TextInputLayout txt_input_buyer;
    private RelativeLayout price_change_container;
    private TextView old_price, new_price;
    private ScrollView scroll_container;
    private EditText input_percentage;

    private RadioGroup radiogroup_share_type;
    private LinearLayout linear_new_price_container, linear_fixed_container;
    private EditText input_new_price;
    private RadioButton radio_share_add_margin, radio_share_different_price;

    public Fragment_CatalogShare() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialog = inflater.inflate(R.layout.fragment_share_catalog_new, container, false);

        // share_cancel = (Button) dialog.findViewById(R.id.share_cancel);
        share_go = (AppCompatButton) dialog.findViewById(R.id.share_go);
        date = (EditText) dialog.findViewById(R.id.date);

        radiogroupprice = (RadioGroup) dialog.findViewById(R.id.radiogroupprice);
        radiogroup_share_type = (RadioGroup) dialog.findViewById(R.id.radiogroup_share_type);
        linear_new_price_container = (LinearLayout) dialog.findViewById(R.id.linear_new_price_container);
        linear_fixed_container = (LinearLayout) dialog.findViewById(R.id.linear_fixed_container);
        add_percentage = (RadioButton) dialog.findViewById(R.id.check_add);
        fixed_price = (RadioButton) dialog.findViewById(R.id.fixed_price);
        radio_share_add_margin = (RadioButton) dialog.findViewById(R.id.radio_share_add_margin);
        radio_share_different_price = (RadioButton) dialog.findViewById(R.id.radio_share_different_price);


        scroll_container = (ScrollView) dialog.findViewById(R.id.scroll_container);

        input_message = (EditText) dialog.findViewById(R.id.input_message);

        input_percentage = (EditText) dialog.findViewById(R.id.input_percentage);
        input_price = (EditText) dialog.findViewById(R.id.input_price);
        input_new_price = (EditText) dialog.findViewById(R.id.input_new_price);
        price_change_container = (RelativeLayout) dialog.findViewById(R.id.price_change_container);
        old_price = (TextView) dialog.findViewById(R.id.old_price);
        new_price = (TextView) dialog.findViewById(R.id.new_price);


        fullcatalog = (CheckBox) dialog.findViewById(R.id.fullcatalog);
        typeofShare = (TextView) dialog.findViewById(R.id.share_type);
        spinner_label = (TextView) dialog.findViewById(R.id.spinner_label);
        //toggle = (SwitchCompat) dialog.findViewById(R.id.toggle);
        group = (LinearLayout) dialog.findViewById(R.id.group_share_layout);
        single = (LinearLayout) dialog.findViewById(R.id.single_share_layout);
        txt_input_buyer = (TextInputLayout) dialog.findViewById(R.id.input_buyername);
        edit_buyername = (TextView) dialog.findViewById(R.id.edit_buyername);
        //buyer_suggestion = (CustomAutoCompleteTextView) dialog.findViewById(R.id.buyerautocomp);

        final LinearLayout buyer_container = (LinearLayout) dialog.findViewById(R.id.buyer_container);
        buyer_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        input_price.setError(null);
        input_new_price.setError(null);
        input_percentage.setError(null);

        radiogroup_share_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {

                if (checkedId == R.id.radio_share_different_price) {
                    linear_new_price_container.setVisibility(View.VISIBLE);
                    linear_fixed_container.setVisibility(View.GONE);
                    input_percentage.setText("");
                    input_percentage.setError(null);
                    input_price.setText("");
                    input_price.setError(null);
                } else if (checkedId == R.id.radio_share_add_margin) {
                    linear_new_price_container.setVisibility(View.GONE);
                    linear_fixed_container.setVisibility(View.VISIBLE);
                    input_new_price.setError(null);
                    input_new_price.setText("");
                }
            }
        });

        radiogroupprice.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (checkedId != R.id.check_add) {
                            fixedRate = true;
                            input_price.setEnabled(true);
                            input_price.requestFocus();
                            input_price.setSelection(input_price.getText().length());
                            input_percentage.setEnabled(false);
                            input_percentage.setText("");
                            input_percentage.setError(null);
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input_price, InputMethodManager.SHOW_IMPLICIT);


                        } else {

                            input_price.setEnabled(false);
                            input_price.setText("");
                            input_price.setError(null);
                            input_percentage.setEnabled(true);
                            input_percentage.requestFocus();
                            input_percentage.setSelection(input_percentage.getText().length());


                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input_percentage, InputMethodManager.SHOW_IMPLICIT);

                        }


                    }
                }
        );
        radiogroupprice.check(R.id.check_add);


        final Calendar myCalendar = Calendar.getInstance();


        if (Application_Singleton.shareCatalogHolder != null) {
            if (Application_Singleton.shareCatalogHolder instanceof CatalogMinified) {
                catalogMinified = (CatalogMinified) Application_Singleton.shareCatalogHolder;
                if (catalogMinified.getExp_desp_date() != null) {
                    checkDate(catalogMinified.getExp_desp_date());
                }
                is_product_price_null = catalogMinified.getIs_product_price_null();
                Log.e("TAG", "onCreateView:==> "+catalogMinified.getIs_product_price_null() );
                updateprice("0");
            }
        }


        if (is_product_price_null == true) {
            add_percentage.setVisibility(View.GONE);
        }


        if (date.getText().toString().equals("")) {
            updateLabel(myCalendar);
        }

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel(myCalendar);
            }

        };

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.HOUR_OF_DAY, myCalendar.getMinimum(Calendar.HOUR_OF_DAY));
                myCalendar.set(Calendar.MINUTE, myCalendar.getMinimum(Calendar.MINUTE));
                myCalendar.set(Calendar.SECOND, myCalendar.getMinimum(Calendar.SECOND));
                myCalendar.set(Calendar.MILLISECOND, myCalendar.getMinimum(Calendar.MILLISECOND));

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.LightDialogTheme, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                dialog.show();
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
     /*   toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = "group";
                    typeofShare.setText("Group Share");
                    group.setVisibility(View.VISIBLE);
                    single.setVisibility(View.GONE);
                    spinner_label.setText("Select buyers group");
                    buyer = null;
                } else {
                    type = "single";
                    typeofShare.setText("Single buyer share");
                    group.setVisibility(View.GONE);
                    single.setVisibility(View.VISIBLE);
                    spinner_label.setText("Search buyer");
                }
            }
        });*/


        message = (CheckBox) dialog.findViewById(R.id.message);


        spinner_buyergroups = (Spinner) dialog.findViewById(R.id.spinner_buyergroups);
     /*   share_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();
            }
        });*/
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
        input_percentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().equals("") && !charSequence.toString().equals("-")) {
                    fixedRate = false;
                    is_product_price_null = false;
                    try{
                        if (Integer.parseInt(charSequence.toString()) > 100 || Integer.parseInt(charSequence.toString()) < -100) {
                            input_percentage.requestFocus();
                            input_percentage.setError(getResources().getString(R.string.error_max_price_percentage));
                            return;

                        }
                        updateprice(charSequence.toString());
                    } catch (Exception e){
                        Toast.makeText(getActivity(),"Please enter valid number",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    if(input_percentage.getVisibility() == View.VISIBLE && linear_fixed_container.getVisibility() == View.VISIBLE){
                        is_product_price_null = true;
                        Log.e("TAG", "Product Price Null Percentage:==> ");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        input_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (input_price.getVisibility() == View.VISIBLE) {
                    if (!charSequence.toString().equals("") && !charSequence.toString().equals("-")) {
                        fixedRate = true;
                        is_product_price_null = false;
                        updateprice(charSequence.toString());
                    } else {
                        if(input_price.getVisibility() == View.VISIBLE && linear_fixed_container.getVisibility() == View.VISIBLE){
                            is_product_price_null = true;
                            Log.e("TAG", "Product Price Null input price:==> ");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input_new_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (linear_new_price_container.getVisibility() == View.VISIBLE) {
                    if (!input_new_price.getText().toString().isEmpty()) {
                        if (Integer.parseInt(charSequence.toString()) > 100) {
                            updateprice(charSequence.toString());
                        } else {
                            input_new_price.requestFocus();
                            input_new_price.setError(getResources().getString(R.string.error_min_price));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (Application_Singleton.shareCatalogHolder != null) {
            if (Application_Singleton.shareCatalogHolder instanceof CatalogMinified) {
                final CatalogMinified catalogMinified = (CatalogMinified) Application_Singleton.shareCatalogHolder;
                if (Boolean.parseBoolean(catalogMinified.getFull_catalog_orders_only())) {
                    fullcatalog.setChecked(true);
                } else {
                    fullcatalog.setChecked(false);
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

                        if(type.equals("single")){
                            if(buyer == null){
                                Toast.makeText(getContext(), "Please select buyer", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        if (radio_share_different_price.isChecked()) {
                            if (is_product_price_below100) {
                                input_new_price.requestFocus();
                                input_new_price.setError(getResources().getString(R.string.error_min_price));
                                return;
                            }

                        } else {
                            if (fixed_price.isChecked()) {
                            }

                            if (is_product_price_below100) {

                                if (fixedRate) {
                                    input_price.requestFocus();
                                    input_price.setError(getResources().getString(R.string.error_min_price));
                                    return;

                                } else {
                                    input_percentage.requestFocus();
                                    input_percentage.setError(getResources().getString(R.string.error_min_price));
                                    return;

                                }

                            }
                        }



                    }
                });


            }


        }
        return dialog;

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


    private void updateprice(String text) {
        if (catalogMinified != null && catalogMinified.getPrice_range() != null) {
            if (catalogMinified.getPrice_range().contains("-")) {
                String[] priceRangeMultiple = catalogMinified.getPrice_range().split("-");
                Float lowerPrice = 0.0f;
                Float upperPrice = 0.0f;


                old_price.setText("\u20B9 " + priceRangeMultiple[0] + " - " + "\u20B9 " + priceRangeMultiple[1]);

                if (radio_share_different_price.isChecked()) {
                    lowerPrice = Float.parseFloat(text);
                    upperPrice = Float.parseFloat(text);
                    if (lowerPrice < 101) {
                        is_product_price_below100 = true;
                        input_new_price.requestFocus();
                        input_new_price.setError(getResources().getString(R.string.error_min_price));
                    } else {
                        is_product_price_below100 = false;
                    }
                } else {
                    if (fixedRate) {
                        lowerPrice = Integer.parseInt(priceRangeMultiple[0]) + Float.parseFloat(text);
                        upperPrice = Integer.parseInt(priceRangeMultiple[1]) + Float.parseFloat(text);

                        if (lowerPrice < 101) {
                            is_product_price_below100 = true;
                            input_price.requestFocus();
                            input_price.setError(getResources().getString(R.string.error_min_price));
                        } else {
                            is_product_price_below100 = false;
                        }

                    } else {

                        lowerPrice = Integer.parseInt(priceRangeMultiple[0]) * (1 + (Float.parseFloat(text) / 100));
                        upperPrice = Integer.parseInt(priceRangeMultiple[1]) * (1 + (Float.parseFloat(text) / 100));

                        if (lowerPrice < 101) {
                            is_product_price_below100 = true;
                            input_percentage.requestFocus();
                            input_percentage.setError(getResources().getString(R.string.error_min_price));
                        } else {
                            is_product_price_below100 = false;
                        }
                    }
                }
                new_price.setText("\u20B9 " + String.format("%.2f", lowerPrice) + " - " + "\u20B9 " + String.format("%.2f", upperPrice));

            } else {
                Integer priceRangeSingle = Integer.parseInt(catalogMinified.getPrice_range());
                Float newPrice = 0.0f;
                old_price.setText("\u20B9 " + priceRangeSingle);

                if (radio_share_different_price.isChecked()) {
                    newPrice = Float.parseFloat(text);
                    if (newPrice < 101) {
                        is_product_price_below100 = true;
                        input_new_price.requestFocus();
                        input_new_price.setError(getResources().getString(R.string.error_min_price));
                    } else {
                        is_product_price_below100 = false;
                    }
                } else {
                    if (fixedRate) {

                        newPrice = priceRangeSingle + Float.parseFloat(text);
                        if (newPrice < 101) {
                            is_product_price_below100 = true;
                            input_price.requestFocus();
                            input_price.setError(getResources().getString(R.string.error_min_price));
                        } else {
                            is_product_price_below100 = false;
                        }

                    } else {
                        newPrice = priceRangeSingle * (1 + (Float.parseFloat(text) / 100));
                        if (newPrice < 101) {
                            is_product_price_below100 = true;
                            input_percentage.requestFocus();
                            input_percentage.setError(getResources().getString(R.string.error_min_price));
                        } else {
                            is_product_price_below100 = false;
                        }
                    }
                }


                //  Log.v("PriceUpdate","input_price"+ Integer.parseInt(charSequence.toString())+  "lowerPrice"+ (1 + (Integer.parseInt(charSequence.toString()) / 100) ));
                new_price.setText("\u20B9 " + String.format("%.2f", newPrice));

            }
        } else {
            is_product_price_null = true;
        }
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
            if (!is_product_price_null) {
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
                            pushObject.setCatalog(new String[]{catalogMinified.getId()});
                            pushObject.setFull_catalog_orders_only(String.valueOf(fullcatalog.isChecked()));
                            pushObject.setSms("yes");
                            pushObject.setChange_price_add_amount(Integer.parseInt(input_price.getText().toString()));
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
            } else {
                Toast.makeText(getActivity(), "Product prices are empty for this catalog . Please enter the price", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong try again", Toast.LENGTH_LONG).show();
        }
    }

    private void checkDate(String dateToCheck) {
        Date today = null;
        Date setDate = null;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c.getTime());
        try {
            today = df.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String form = dateToCheck;

        try {
            setDate = sdf.parse(form);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!setDate.before(today)) {
            date.setText(form);
        }
    }
}
