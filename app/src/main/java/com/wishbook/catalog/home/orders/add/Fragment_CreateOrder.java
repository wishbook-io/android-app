package com.wishbook.catalog.home.orders.add;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonadapters.ProductslistOrderAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Catalogs;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Suppliers;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brokers;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQntRate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Request_CreateOrder;
import com.wishbook.catalog.commonmodels.responses.Request_CreateOrderCatalog;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_Selection_Detail;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_brokers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder_new;
import com.wishbook.catalog.home.adapters.SpinAdapter_ProdSel;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

public class Fragment_CreateOrder extends GATrackedFragment {


    private View v;
    private String orderType = null;
    private String orderValue = null;
    private Spinner spinner_productselections;
    private Spinner spinner_catalogs;
    private Spinner spinner_brokers;
    private Spinner spinner_supplier;

    private String buyerselected = null;
    private ImageButton ibtn_selections;
    private ImageButton ibn_catalogs;
    private RecyclerView mRecyclerView;
    private SpinAdapter_ProdSel spinAdapter_prodSel;

    private SpinAdapter_Suppliers spinAdapter_suppliers;

    private Button orderBut;
    private Button screenshot;
    private Button cancel;
    private String SelectionType;
    private TextView total_quantity;
    private TextView tex_quantity;
    private TextView tex_totalvalue;
    private ProductslistOrderAdapter productslistOrderAdapter;
    private EditText input_ordernum;
    private SharedPreferences pref;
    private UserInfo userInfo;
    private LinearLayout order_summary;
    private TextInputLayout catalogshow, selectionshow;
    private TextView selected_catalog, selected_selection;
    private LinearLayout supplier_container_main;
    ProductObj[] productObjs = null;
    String full_catalog_orders_only = "false";
    Response_brokers[] brokersdata = new Response_brokers[]{};
    Response_catalogMini[] response_catalog = new Response_catalogMini[]{};


    //new added
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private ArrayList<BuyersList> supplierslist = new ArrayList<>();
    private CustomAutoCompleteTextView buyer_select;
    private CustomAutoCompleteTextView supplier_select;
    AutoCompleteCommonAdapter adapter;
    private BuyersList buyer = null;
    private BuyersList supplier = null;

    private TextView others_button;
    private TextView others_supplier_text;
    private ProgressBar progress_bar;
    Boolean is_other_supplier = false;

    public Fragment_CreateOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_create_orders, ga_container, true);
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        supplier_container_main = (LinearLayout) v.findViewById(R.id.supplier_container_main);
        spinner_brokers = ((Spinner) v.findViewById(R.id.spinner_brokers));
        spinner_supplier = ((Spinner) v.findViewById(R.id.spinner_supplier));

        tex_quantity = ((TextView) v.findViewById(R.id.tex_quantity));
        total_quantity = ((TextView) v.findViewById(R.id.input_total_quantity));
        input_ordernum = ((EditText) v.findViewById(R.id.input_ordernum));
        tex_totalvalue = ((TextView) v.findViewById(R.id.tex_totalvalue));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        order_summary = (LinearLayout) v.findViewById(R.id.order_summary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        spinner_productselections = ((Spinner) v.findViewById(R.id.spinner_productselections));
        spinner_catalogs = ((Spinner) v.findViewById(R.id.spinner_catalogs));
        userInfo = UserInfo.getInstance(getActivity());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        orderBut = ((Button) v.findViewById(R.id.orderBut));
        screenshot = ((Button) v.findViewById(R.id.screenshot));
        cancel = ((Button) v.findViewById(R.id.cancel));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });
        catalogshow = (TextInputLayout) v.findViewById(R.id.catalogSpinner);
        selectionshow = (TextInputLayout) v.findViewById(R.id.selectionSpinner);
        selected_catalog = ((TextView) v.findViewById(R.id.selected_catalog));
        selected_selection = ((TextView) v.findViewById(R.id.selected_selections));
        buyer_select = (CustomAutoCompleteTextView) v.findViewById(R.id.buyer_select);
        others_button = (TextView) v.findViewById(R.id.others_button);
        others_supplier_text = (TextView) v.findViewById(R.id.others_supplier_text);
        progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);

        List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
        brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
        Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
        SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
        spinner_brokers.setAdapter(spinAdapter_brokers);

        final UserInfo userInfo = UserInfo.getInstance(getActivity());

        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
            supplier_container_main.setVisibility(View.VISIBLE);
            //Adding deputed supplier
            ArrayList<Response_Suppliers> response_supplierses = new ArrayList<Response_Suppliers>();
            response_supplierses.add(new Response_Suppliers(userInfo.getCompanyname(), userInfo.getCompany_id()));
            if (!userInfo.getDeputed_to().equals("") && !userInfo.getDeputed_to().equals("null") && !userInfo.getDeputed_to_name().equals("") && !userInfo.getDeputed_to_name().equals("null")) {
                response_supplierses.add(new Response_Suppliers(userInfo.getDeputed_to_name(), userInfo.getDeputed_to()));
                spinAdapter_suppliers = new SpinAdapter_Suppliers(getActivity(), R.layout.spinneritem, response_supplierses);
                spinner_supplier.setAdapter(spinAdapter_suppliers);
            }else{
                supplier_container_main.setVisibility(View.GONE);
            }
        } else {
            supplier_container_main.setVisibility(View.GONE);
        }

        buyer_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buyer = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        buyer_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("onitemselected", "" + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        buyer_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                buyer = (BuyersList) parent.getItemAtPosition(position);

                //make spinner 0
                List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                spinner_brokers.setAdapter(spinAdapter_brokers);

                getbrokers(buyer.getBroker_id());
            }
        });

        adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, buyerslist,"buyerlist");
        buyer_select.setAdapter(adapter);
        buyer_select.setThreshold(1);
        buyer_select.setLoadingIndicator(
                (android.widget.ProgressBar) v.findViewById(R.id.progress_bar));


        others_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("Select Buyer").positiveText("Done").negativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        supplier = null;
                        dialog.dismiss();
                    }
                }).customView(R.layout.buyer_select_view_temp, true).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (supplier != null) {
                            is_other_supplier = true;
                        } else {
                            Toast.makeText(getActivity(), "Please select buyer from suggested list", Toast.LENGTH_LONG).show();
                        }
                    }
                }).build();

                dialog.show();

                supplier_select = (CustomAutoCompleteTextView) dialog.getCustomView().findViewById(R.id.buyer_select);

                supplier_select.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        supplier = null;
                        is_other_supplier = false;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                supplier_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.v("onitemselected", "" + position);
                        supplier = (BuyersList) parent.getItemAtPosition(position);
                        if(supplier!=null){
                            spinner_supplier.setVisibility(View.GONE);
                            others_supplier_text.setVisibility(View.VISIBLE);
                            others_supplier_text.setText(supplier.getCompany_name());
                        }
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                });
                AutoCompleteCommonAdapter adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, supplierslist,"buyerlist_no_deputed");
                supplier_select.setAdapter(adapter);
                supplier_select.setThreshold(1);
                supplier_select.setLoadingIndicator(
                        (android.widget.ProgressBar) dialog.getCustomView().findViewById(R.id.progress_bar));

            }
        });


        total_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String quantity = total_quantity.getText().toString();

                if (!quantity.equals("0") && !quantity.equals("")) {
                    if (productslistOrderAdapter != null) {
                        ArrayList<ProductObjectQuantity> productObjectQuantities = productslistOrderAdapter.getAllProducts(Integer.parseInt(quantity));
                   /* for (ProductObj productObj : productObjs) {
                        try {
                            productObjectQuantities.add(new ProductObjectQuantity(productObj, Integer.parseInt(quantity), Float.parseFloat(productObj.getSelling_price())));
                        } catch (Exception e) {

                        }
                    }*/
                        productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), full_catalog_orders_only, productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                            @Override
                            public void onChange() {
                                tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                    order_summary.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                        tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                        mRecyclerView.setAdapter(productslistOrderAdapter);
                        if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                            order_summary.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ibn_catalogs = (ImageButton) v.findViewById(R.id.ibn_catalogs);
        ibtn_selections = (ImageButton) v.findViewById(R.id.ibtn_selections);

        final Bundle filter = getArguments();
        if (filter != null && filter.getString("selectiontype") != null) {
            selectionView(true);
        } else {
            selectionView(false);
        }

        ibn_catalogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catalogView(orderValue);
            }
        });
        ibtn_selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter != null && filter.getString("selectiontype") != null) {
                    selectionView(true);
                } else {
                    selectionView(false);
                }

            }
        });




        if (filter != null) {
            buyerselected = filter.getString("buyerselected");
            buyer_select.setText(buyerselected);
            orderType = filter.getString("ordertype");
            orderValue = filter.getString("ordervalue");
            if (orderType != null) {
                if (orderType.equals("selections")) {
                    String SelectionType = filter.getString("selectiontype");
                    if (SelectionType != null) {
                        selectionView(true);
                    } else {
                        selectionView(false);
                    }
                } else {
                    catalogView(orderValue);
                }
            } else {
                catalogView(orderValue);
            }

        }


       /* HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyerlist", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
               *//* buyer_list = Application_Singleton.gson.fromJson(response, BuyersList[].class);
                if (buyer_list.length > 0) {
                    try {
                        final SpinAdapter_buyers spinAdapter_buyers = new SpinAdapter_buyers(getActivity(), R.layout.spinneritem, buyer_list);
                        spinner_buyer.setAdapter(spinAdapter_buyers);
                        if (buyerselected != null) {
                            for (int i = 0; i < buyer_list.length; i++) {
                                if (buyer_list[i].getId().equals(buyerselected)) {
                                    spinner_buyer.setSelection(i);
                                }
                            }
                        }
                      *//**//*  getbrokers();*//**//*
                    } catch (Exception e) {

                    }
                }*//*

                buyerslist.clear();
                BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                if (buyers.length > 0) {
                    for (BuyersList buyerList : buyers) {
                        buyerslist.add(buyerList);
                    }
                    adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, buyerslist);

                    buyer_select.setAdapter(adapter);
                    buyer_select.setThreshold(1);

                    if (buyerselected != null) {
                        for (int i = 0; i < buyers.length; i++) {
                            if (buyers[i].getCompany_id().equals(buyerselected)) {
                                buyer_select.setText(buyers[i].getCompany_name());
                                buyer = buyers[i];
                                //make s
                                // pinner 0
                                List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                                brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                                Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                                SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                                spinner_brokers.setAdapter(spinAdapter_brokers);
                                progress_bar.setVisibility(View.GONE);

                                getbrokers(buyer.getBroker_id());

                            }
                        }
                    }
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
*/

        toolbar.setVisibility(View.GONE);


        orderBut.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (input_ordernum.getText().toString().equals("")) {
                                                input_ordernum.setError("cannot be empty !");
                                                Toast.makeText(getContext(),"Please enter order number",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (total_quantity.getText().toString().equals("0")) {
                                                total_quantity.setError("cannot be 0 !");
                                                Toast.makeText(getContext(),"Quantity cannot be 0",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            ArrayList<ProductQntRate> productQntRates = new ArrayList<ProductQntRate>();

                                            ProductslistOrderAdapter proadapt = (ProductslistOrderAdapter) mRecyclerView.getAdapter();
                                            if (proadapt != null) {
                                                ArrayList<ProductObjectQuantity> data = proadapt.getCurrentData();
                                                if (data != null) {
                                                    for (int i = 0; i < data.size(); i++) {
                                                        productQntRates.add(new ProductQntRate(data.get(i).getFeedItemList().getId(), "" + data.get(i).getQuantity(), "" + data.get(i).getFeedItemList().getSelling_price()));
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "No Products", Toast.LENGTH_LONG).show();
                                                return;
                                            }
//input_ordernum.getText().toString(), pref.getString("company_id", ""), "" + Application_Singleton.navselectedBuyer.getBuyingCompany().getId(), productQntRates, "ordered", "finalized"
                                            if (spinner_productselections.getVisibility() == View.VISIBLE) {

                                                Request_CreateOrder request_createOrder = new Request_CreateOrder();

                                                //changed due to deputed to
                                                if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                                                    if (!userInfo.getDeputed_to().equals("") && !userInfo.getDeputed_to().equals("null") && !userInfo.getDeputed_to_name().equals("") && !userInfo.getDeputed_to_name().equals("null")) {
                                                        if (is_other_supplier && supplier != null) {
                                                            request_createOrder.setSeller_company(supplier.getCompany_id());
                                                        } else {
                                                            request_createOrder.setSeller_company(((Response_Suppliers) spinner_supplier.getSelectedItem()).getSelling_company());
                                                        }
                                                    }else{
                                                        request_createOrder.setSeller_company(UserInfo.getInstance(getActivity()).getCompany_id());
                                                    }

                                                } else {
                                                    request_createOrder.setSeller_company(UserInfo.getInstance(getActivity()).getCompany_id());
                                                }


                                                if (buyer != null) {
                                                    request_createOrder.setCompany("" + buyer.getCompany_id());
                                                } else {
                                                    Toast.makeText(getActivity(), "Please select the buyer", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                request_createOrder.setItems(productQntRates);
                                                request_createOrder.setOrder_number(input_ordernum.getText().toString());
                                                request_createOrder.setSelection(((Response_Selection) spinner_productselections.getSelectedItem()).getId());
                                                request_createOrder.setProcessing_status("Accepted");
                                                if (spinner_brokers != null) {
                                                    int selected = spinner_brokers.getSelectedItemPosition();
                                                    if (selected != 0) {
                                                        /*  if(((BuyersList) spinner_buyer.getSelectedItem()).getBroker_id()!=null) {
                                                            request_createOrder.setBroker_company("" + ((BuyersList) spinner_buyer.getSelectedItem()).getBroker_id());
                                                        }
                                                        else */
                                                        if (!((Response_brokers) spinner_brokers.getSelectedItem()).getId().equals(null)) {
                                                            request_createOrder.setBroker_company(((Response_brokers) spinner_brokers.getSelectedItem()).getCompany_id());
                                                        }
                                                    }
                                                }
                                                String orderjson = new Gson().toJson(request_createOrder);
                                                Log.v("ordersent", orderjson);
                                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "salesorder", ""), new Gson().fromJson(orderjson, JsonObject.class)
                                                        , headers, false, new HttpManager.customCallBack() {
                                                            @Override
                                                            public void onCacheResponse(String response) {
                                                                Log.v("cached response", response);
                                                                // onServerResponse(response, false);
                                                            }

                                                            @Override
                                                            public void onServerResponse(String response, boolean dataUpdated) {
                                                                Log.v("sync response", response);

                                                                Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
                                                                //startActivity(new Intent(getActivity(), Activity_OrderDetails.class));
                                                                startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class));
                                                                getActivity().finish();


                                                            }

                                                            @Override
                                                            public void onResponseFailed(ErrorString error) {
                                                                StaticFunctions.showResponseFailedDialog(error);

                                                            }


                                                        });


                                            } else {
                                                if (input_ordernum.getText().toString().equals("")) {
                                                    input_ordernum.setError("cannot be empty !");
                                                    return;
                                                }
                                                if (total_quantity.getText().toString().equals("0")) {
                                                    total_quantity.setError("cannot be 0 !");
                                                    return;
                                                }

                                                Request_CreateOrderCatalog request_createOrder = new Request_CreateOrderCatalog();

                                                //changed due to deputed to
                                                if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                                                    if (!userInfo.getDeputed_to().equals("") && !userInfo.getDeputed_to().equals("null") && !userInfo.getDeputed_to_name().equals("") && !userInfo.getDeputed_to_name().equals("null")) {
                                                        if (is_other_supplier && supplier != null) {
                                                            request_createOrder.setSeller_company(supplier.getCompany_id());
                                                        } else {
                                                            request_createOrder.setSeller_company(((Response_Suppliers) spinner_supplier.getSelectedItem()).getSelling_company());
                                                        }
                                                    }else{
                                                        request_createOrder.setSeller_company(UserInfo.getInstance(getActivity()).getCompany_id());
                                                    }

                                                } else {
                                                    request_createOrder.setSeller_company(UserInfo.getInstance(getActivity()).getCompany_id());
                                                }


                                                if (buyer != null) {
                                                    request_createOrder.setCompany("" + buyer.getCompany_id());
                                                } else {
                                                    Toast.makeText(getActivity(), "Please select the buyer", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                request_createOrder.setItems(productQntRates);
                                                request_createOrder.setOrder_number(input_ordernum.getText().toString());
                                                request_createOrder.setcatalog(((Response_catalogMini) spinner_catalogs.getSelectedItem()).getId());
                                                request_createOrder.setProcessing_status("Accepted");
                                                if (spinner_brokers != null) {
                                                    int selected = spinner_brokers.getSelectedItemPosition();
                                                    if (selected != 0) {
                                                      /*  if(((BuyersList) spinner_buyer.getSelectedItem()).getBroker_id()!=null) {
                                                            request_createOrder.setBroker_company("" + ((BuyersList) spinner_buyer.getSelectedItem()).getBroker_id());
                                                        }
                                                        else */

                                                        if (!((Response_brokers) spinner_brokers.getSelectedItem()).getId().equals(null)) {
                                                            request_createOrder.setBroker_company(((Response_brokers) spinner_brokers.getSelectedItem()).getCompany_id());
                                                        }
                                                    }
                                                }
                                                String orderjson = new Gson().toJson(request_createOrder);
                                                Log.v("ordersent", orderjson);

                                                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "salesorder", ""), new Gson().fromJson(orderjson, JsonObject.class)
                                                        , headers, false, new HttpManager.customCallBack() {
                                                            @Override
                                                            public void onCacheResponse(String response) {
                                                                Log.v("cached response", response);
                                                                // onServerResponse(response, false);
                                                            }

                                                            @Override
                                                            public void onServerResponse(String response, boolean dataUpdated) {
                                                                String salesId = "";
                                                                Log.v("sync response", response);
                                                                Log.v("RESPONSE", response);
                                                                Gson gson = new Gson();
                                                                final Response_sellingorder_new selectedOrder = gson.fromJson(response, Response_sellingorder_new.class);
                                                                Application_Singleton.selectedOrder = selectedOrder;
                                                                Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
                                                                //startActivity(new Intent(getActivity(), Activity_OrderDetails.class));
                                                                startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class));
                                                                getActivity().finish();
                                                               /* JSONObject response_salesorder = null;
                                                                try {
                                                                    response_salesorder = new JSONObject(response);
                                                                    salesId = response_salesorder.getString("id");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                if (!Activity_Home.pref.getString("groupstatus", "0").equals("1")) {
                                                                    final String sales = salesId;
                                                                    if (!Activity_Home.pref.getString("currentmeet", "na").equals("na")) {
                                                                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                                                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.userUrl(getActivity(), "meetings", "") + "?status=pending", null, headers, true, new HttpManager.customCallBack() {
                                                                            @Override
                                                                            public void onCacheResponse(String response) {
                                                                                Log.v("cached response", response);
                                                                                onServerResponse(response, false);
                                                                            }

                                                                            @Override
                                                                            public void onServerResponse(String response, boolean dataUpdated) {
                                                                                Response_meeting[] response_meeting = Application_Singleton.gson.fromJson(response, Response_meeting[].class);
                                                                                String id = String.valueOf(buyer.getCompany_id());
                                                                                if (id.equals(response_meeting[0].getBuying_company_ref())) {
                                                                                    ArrayList<Integer> salesorder = new ArrayList<Integer>();
                                                                                    // salesorder = response_meeting[0].getSalesorder();
                                                                                    salesorder.add(Integer.valueOf(sales));

                                                                                    SalesOrderPatch selectionPatch = new SalesOrderPatch(Integer.valueOf(response_meeting[0].getId()), salesorder);

                                                                                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                                                                    Gson gson = new Gson();
                                                                                    HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "meetings_with_id", response_meeting[0].getId()), gson.fromJson(gson.toJson(selectionPatch), JsonObject.class), headers, new HttpManager.customCallBack() {
                                                                                        @Override
                                                                                        public void onCacheResponse(String response) {
                                                                                            onServerResponse(response, false);
                                                                                        }

                                                                                        @Override
                                                                                        public void onServerResponse(String response, boolean dataUpdated) {
                                                                                            Log.d("DONE", response);
                                                                                            Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
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
                                                                                            //  progressDialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
                                                                                    getActivity().finish();
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


                                                                    } else {
                                                                        Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
                                                                        getActivity().finish();
                                                                    }

                                                                } else {
                                                                    Toast.makeText(getActivity(), "Sales order placed successfully", Toast.LENGTH_SHORT).show();
                                                                    getActivity().finish();
                                                                }
*/

                                                            }

                                                            @Override
                                                            public void onResponseFailed(ErrorString error) {
                                                                StaticFunctions.showResponseFailedDialog(error);

                                                            }


                                                        });
                                            }
                                        }
                                    }
        );





        /*if(brokersdata!=null) {
         if(brokersdata.length<1) {
             getbrokers(null);
         }
        }*/
       /* spinner_buyer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BuyersList list = (BuyersList) spinner_buyer.getSelectedItem();
                getbrokers(list.getBroker_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/


        return v;
    }

    private void selectionView(Boolean type) {
        catalogshow.setVisibility(View.GONE);
        selected_catalog.setVisibility(View.GONE);
        selectionshow.setVisibility(View.VISIBLE);
        selected_selection.setVisibility(View.VISIBLE);
        chooseSelections(type);
    }

    private void catalogView(String orderValue) {
        catalogshow.setVisibility(View.VISIBLE);
        selected_catalog.setVisibility(View.VISIBLE);
        selectionshow.setVisibility(View.GONE);
        selected_selection.setVisibility(View.GONE);
        chooseCatalogs(orderValue);
    }

    private void getbrokers(final String id) {
        if (id != null && id != "" && id != "null") {
            if (brokersdata.length < 1) {
                showProgress();
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brokerlist", ""), null, headers, true, new HttpManager.customCallBack() {
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
                        brokersdata = Application_Singleton.gson.fromJson(response, Response_brokers[].class);
                        List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                        brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                        Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                        SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                        spinner_brokers.setAdapter(spinAdapter_brokers);


                        if (id != null) {
                            for (int i = 0; i < brokers.size(); i++) {
                                if (id.equals(brokers.get(i).getCompany_id())) {
                                    spinner_brokers.setSelection(i);
                                }
                            }
                        } else {
                            spinner_brokers.setSelection(0);
                        }

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
                    }
                });
            } else {
                List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                spinner_brokers.setAdapter(spinAdapter_brokers);

                if (id != null) {
                    for (int i = 0; i < brokers.size(); i++) {
                        if (id.equals(brokers.get(i).getCompany_id())) {
                            spinner_brokers.setSelection(i);
                        }
                    }
                } else {
                    spinner_brokers.setSelection(0);
                }

            }
        }
    }

    private void chooseSelections(final String orderValue) {


        spinner_productselections.setVisibility(View.VISIBLE);
        spinner_catalogs.setVisibility(View.GONE);


        String url = URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + "?buyable=true";
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String result, boolean dataUpdated) {
                hideProgress();

                Log.v("res", "" + result);
                Response_Selection[] response_selections = Application_Singleton.gson.fromJson(result, Response_Selection[].class);
                if (response_selections.length > 0) {
                    if (response_selections[0].getId() != null) {
                        spinAdapter_prodSel = new SpinAdapter_ProdSel(getActivity(), R.layout.spinneritem, response_selections);
                        spinner_productselections.setAdapter(spinAdapter_prodSel);
                        spinner_productselections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                total_quantity.setText("1");
                                Response_Selection selection = ((Response_Selection) (spinner_productselections.getSelectedItem()));
                                NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + selection.getId() + "/?expand=true", null, new NetworkManager.customCallBack() {
                                            @Override
                                            public void onCompleted(int status, String response) {
                                                if (response != null) {
                                                    Gson gson = new Gson();
                                                    Response_Selection_Detail response_productss = gson.fromJson(response, Response_Selection_Detail.class);
                                                    ProductObj[] response_products = response_productss.getProducts();
                                                    if (response_products.length > 0) {
                                                        if (response_products[0].getId() != null) {
                                                            ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                                            for (ProductObj productObj : response_products) {
                                                                try {
                                                                    productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                                                } catch (Exception e) {

                                                                }
                                                            }
                                                            productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), "", productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                                                @Override
                                                                public void onChange() {
                                                                    tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                                    tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                                    if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                                        order_summary.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            });
                                                            tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                            tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                            mRecyclerView.setAdapter(productslistOrderAdapter);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                );


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        for (int i = 0; i < response_selections.length; i++) {
                            if (response_selections[i].getId().equals(orderValue)) {
                                spinner_productselections.setSelection(i);
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }


        });


    }

    private void chooseCatalogs(final String orderValue) {
        spinner_catalogs.setVisibility(View.VISIBLE);
        spinner_productselections.setVisibility(View.GONE);

        if (response_catalog.length < 1) {
            String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();

                    response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    if (response_catalog.length > 0) {
                        if (response_catalog[0].getId() != null) {
                            SpinAdapter_Catalogs spinAdapter_catalogs = new SpinAdapter_Catalogs(getActivity(), R.layout.spinneritem, response_catalog);
                            int selectionID = 0;
                            if (orderType != null) {
                                if ((orderType.equals("catalogs") || orderType.equals("catalog")) && orderValue != null) {
                                    for (int i = 0; i < response_catalog.length; i++) {
                                        if (response_catalog[i].getId().equals(orderValue)) {
                                            selectionID = i;
                                        }
                                    }
                                }
                            }
                            spinner_catalogs.setAdapter(spinAdapter_catalogs);

                            spinner_catalogs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    total_quantity.setText("1");
                                    Response_catalogMini selection = ((Response_catalogMini) (spinner_catalogs.getSelectedItem()));
                                  //  full_catalog_orders_only = selection.getFull_catalog_orders_only();
                                    full_catalog_orders_only = "false";
                                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                    String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", selection.getId());


                                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                                        @Override
                                        public void onCacheResponse(String response) {
                                            Log.v("cached response", response);
                                            onServerResponse(response, false);
                                        }

                                        @Override
                                        public void onServerResponse(String response, boolean dataUpdated) {
                                            Log.v("sync response", response);
                                            Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                            productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                                            ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                            for (ProductObj productObj : productObjs) {
                                                try {
                                                    productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                                } catch (Exception e) {

                                                }
                                            }
                                            productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), full_catalog_orders_only, productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                                @Override
                                                public void onChange() {
                                                    tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                    tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                    if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                        order_summary.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });


                                            tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                            tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                            mRecyclerView.setAdapter(productslistOrderAdapter);
                                            if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                order_summary.setVisibility(View.VISIBLE);
                                            }

                                            //CHANGED BY ABU

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
                            if (selectionID != 0) {
                                spinner_catalogs.setSelection(selectionID);
                            }
                        }

                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();

                }


            });
        } else {
            if (response_catalog[0].getId() != null) {
                SpinAdapter_Catalogs spinAdapter_catalogs = new SpinAdapter_Catalogs(getActivity(), R.layout.spinneritem, response_catalog);
                int selectionID = 0;


                if (orderType != null) {
                    if ((orderType.equals("catalogs") || orderType.equals("catalog")) && orderValue != null) {
                        for (int i = 0; i < response_catalog.length; i++) {
                            if (response_catalog[i].getId().equals(orderValue)) {
                                selectionID = i;
                            }
                        }
                    }
                }

                spinner_catalogs.setAdapter(spinAdapter_catalogs);

                spinner_catalogs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        total_quantity.setText("1");
                        Response_catalogMini selection = ((Response_catalogMini) (spinner_catalogs.getSelectedItem()));
                        //full_catalog_orders_only = selection.getFull_catalog_orders_only();
                        full_catalog_orders_only ="false";
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", selection.getId());
                        total_quantity.setText("1");
                        showProgress();
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
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
                                Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                                ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                for (ProductObj productObj : productObjs) {
                                    try {
                                        productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                    } catch (Exception e) {

                                    }
                                }
                                productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), full_catalog_orders_only, productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                    @Override
                                    public void onChange() {
                                        tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                        tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                        if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                            order_summary.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });


                                tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                mRecyclerView.setAdapter(productslistOrderAdapter);
                                if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                    order_summary.setVisibility(View.VISIBLE);
                                }

                                //CHANGED BY ABU

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                hideProgress();
                            }


                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (selectionID != 0) {
                    spinner_catalogs.setSelection(selectionID);
                }
            }


        }

    }


    private void chooseSelections(Boolean type) {
        spinner_productselections.setVisibility(View.VISIBLE);
        spinner_catalogs.setVisibility(View.GONE);

        String get_params;
        if (type) {
            get_params = "?type=my";
        } else {
            get_params = "?buyable=true";
        }

        String url = URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + get_params;
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String result, boolean dataUpdated) {

                hideProgress();
                Log.v("res", "" + result);
                Response_Selection[] response_selections = Application_Singleton.gson.fromJson(result, Response_Selection[].class);
                if (response_selections.length > 0) {
                    ibtn_selections.setVisibility(View.VISIBLE);
                    if (response_selections[0].getId() != null) {
                        spinAdapter_prodSel = new SpinAdapter_ProdSel(getActivity(), R.layout.spinneritem, response_selections);
                        spinner_productselections.setAdapter(spinAdapter_prodSel);
                        if (orderType != null) {
                            if ((orderType.equals("selections") || orderType.equals("selection")) && orderValue != null) {
                                for (int i = 0; i < response_selections.length; i++) {
                                    if (response_selections[i].getId().equals(orderValue)) {
                                        spinner_productselections.setSelection(i);
                                    }
                                }
                            }
                        }
                        spinner_productselections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                total_quantity.setText("1");
                                Response_Selection selection = ((Response_Selection) (spinner_productselections.getSelectedItem()));
                                NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + selection.getId() + "/?expand=true", null, new NetworkManager.customCallBack() {
                                            @Override
                                            public void onCompleted(int status, String response) {
                                                Gson gson = new Gson();
                                                if (response != null) {
                                                    Response_Selection_Detail response_productss = gson.fromJson(response, Response_Selection_Detail.class);
                                                    ProductObj[] response_products = response_productss.getProducts();
                                                    if (response_products.length > 0) {
                                                        if (response_products[0].getId() != null) {
                                                            ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                                            for (ProductObj productObj : response_products) {
                                                                try {
                                                                    productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                                                } catch (Exception e) {

                                                                }
                                                            }
                                                            productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), "", productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                                                @Override
                                                                public void onChange() {
                                                                    tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                                    tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                                    if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                                        order_summary.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            });
                                                            if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                                order_summary.setVisibility(View.VISIBLE);
                                                            }

                                                            tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                            tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                            mRecyclerView.setAdapter(productslistOrderAdapter);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                );


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                } else {
                    selected_selection.setVisibility(View.GONE);
                    ibtn_selections.setVisibility(View.GONE);
                    catalogView(orderValue);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }


        });


    }

    private void chooseCatalogs() {
        spinner_catalogs.setVisibility(View.VISIBLE);
        spinner_productselections.setVisibility(View.GONE);

        if (response_catalog.length < 1) {
            String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    if (response_catalog.length > 0) {
                        if (response_catalog[0].getId() != null) {
                            SpinAdapter_Catalogs spinAdapter_catalogs = new SpinAdapter_Catalogs(getActivity(), R.layout.spinneritem, response_catalog);

                            spinner_catalogs.setAdapter(spinAdapter_catalogs);

                            spinner_catalogs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Response_catalogMini selection = ((Response_catalogMini) (spinner_catalogs.getSelectedItem()));
                                   // full_catalog_orders_only = selection.getFull_catalog_orders_only();
                                    full_catalog_orders_only = "false";
                                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                                    String url = URLConstants.companyUrl(getActivity(), "catalogs", "") + selection.getId() + "/?expand=true";

                                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                                        @Override
                                        public void onCacheResponse(String response) {
                                            Log.v("cached response", response);
                                            onServerResponse(response, false);
                                        }

                                        @Override
                                        public void onServerResponse(String response, boolean dataUpdated) {
                                            Log.v("sync response", response);
                                            Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                            productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                                            ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                            for (ProductObj productObj : productObjs) {
                                                try {
                                                    productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                                } catch (Exception e) {

                                                }
                                            }
                                            productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), full_catalog_orders_only, productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                                @Override
                                                public void onChange() {
                                                    tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                                    tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                                    if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                        order_summary.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                            if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                                order_summary.setVisibility(View.VISIBLE);
                                            }


                                            tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                            tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                            mRecyclerView.setAdapter(productslistOrderAdapter);

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

                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }


            });
        } else {
            if (response_catalog[0].getId() != null) {
                SpinAdapter_Catalogs spinAdapter_catalogs = new SpinAdapter_Catalogs(getActivity(), R.layout.spinneritem, response_catalog);

                spinner_catalogs.setAdapter(spinAdapter_catalogs);

                spinner_catalogs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Response_catalogMini selection = ((Response_catalogMini) (spinner_catalogs.getSelectedItem()));
                        // full_catalog_orders_only = selection.getFull_catalog_orders_only();
                        full_catalog_orders_only = "false";
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        String url = URLConstants.companyUrl(getActivity(), "catalogs", "") + selection.getId() + "/?expand=true";
                        showProgress();
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
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
                                Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                                ArrayList<ProductObjectQuantity> productObjectQuantities = new ArrayList<ProductObjectQuantity>();
                                for (ProductObj productObj : productObjs) {
                                    try {
                                        productObjectQuantities.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getSelling_price())));
                                    } catch (Exception e) {

                                    }
                                }
                                productslistOrderAdapter = new ProductslistOrderAdapter((AppCompatActivity) getActivity(), full_catalog_orders_only, productObjectQuantities, new ProductslistOrderAdapter.ProductChangeListener() {
                                    @Override
                                    public void onChange() {
                                        tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                        tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                        if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                            order_summary.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                if (productslistOrderAdapter.getCurrentTotalQuantity() > 0) {
                                    order_summary.setVisibility(View.VISIBLE);
                                }


                                tex_quantity.setText("Total Quantity : " + productslistOrderAdapter.getCurrentTotalQuantity());
                                tex_totalvalue.setText("Total Value : \u20B9 " + productslistOrderAdapter.getCurrentTotal());
                                mRecyclerView.setAdapter(productslistOrderAdapter);

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                hideProgress();
                            }


                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }


        }

    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(getActivity(), "Screenshot Captured", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }


}
