package com.wishbook.catalog.home.orders.add;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.ReceivedProductsAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_CatalogsMini;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Suppliers;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.adapters.SpinAdapter_ProdSel;
import com.wishbook.catalog.home.models.ProductObj;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Fragment_CreatePurchaseOrder extends GATrackedFragment {


    private View v;
    private Spinner spinner_supplier;
    private EditText ordernumber;
    private TextView catalogcost;
    private TextView productssize;
    private RecyclerView recyclerview;
    private TextView totalproducts;
    private TextView totalprice;
    private ReceivedProductsAdapter receivedProductsAdapter;
    private TextView quatity;
    private Button screenshot;
    private Button cancel;
    //private String catID = "0";
    private Spinner spinner_cat;
    private Spinner spinner_sel;
    private Button orderBut;
    private SpinAdapter_Suppliers spinAdapter_suppliers;
    private String orderType;
    private String orderValue;
    boolean isFirsttime = true;
    private LinearLayout spinner_sel_lay;
    private LinearLayout spinner_cat_lay;
    private LinearLayout label;
    private boolean isFirsttimesel = true;
    private LinearLayout subdetail;
    public String sellerID = "";
    private int quantity = 1;
    boolean isFullCatalog = false;
    private ProductObj[] productObjs = null;
    private ImageButton ibtn_selections;
    private ImageButton ibn_catalogs;
    private TextView selected_catalog, selected_selection;
    private LinearLayout catalog_container, supplier_container;
    private LinearLayout linearLayout10;

    Boolean is_supplier_approved = true;
    Boolean from_public = false;

    public Fragment_CreatePurchaseOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.purchase, ga_container, true);
        ibn_catalogs = (ImageButton) v.findViewById(R.id.ibn_catalogs);
        ibtn_selections = (ImageButton) v.findViewById(R.id.ibtn_selections);

        catalog_container = (LinearLayout) v.findViewById(R.id.catalog_container);
        supplier_container = (LinearLayout) v.findViewById(R.id.supplier_container);

        linearLayout10 = (LinearLayout) v.findViewById(R.id.linearLayout10);
        catalog_container.setVisibility(View.VISIBLE);
        linearLayout10.setVisibility(View.VISIBLE);
        initializeViews();
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });
        orderBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order();

            }
        });
        if (getArguments() != null) {
            if (!getArguments().getString("sellerid", "").equals("")) {
                sellerID = getArguments().getString("sellerid", "");
            }
        }


        quatity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (!quatity.getText().toString().equals("0") && !quatity.getText().toString().equals("")) {
                    if (receivedProductsAdapter != null) {
                        ArrayList<ProductObjectQuantity> productObjectQuantities = receivedProductsAdapter.getAllProducts(Integer.parseInt(quatity.getText().toString()));
                        receivedProductsAdapter = new ReceivedProductsAdapter((AppCompatActivity) getActivity(), isFullCatalog, productObjectQuantities, new ReceivedProductsAdapter.ProductChangeListener() {
                            @Override
                            public void onChange() {
                                totalproducts.setText("Total Quantity : " + receivedProductsAdapter.getCurrentTotalQuantity());
                                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                if (receivedProductsAdapter.getCurrentTotalQuantity() > 0) {
                                    //  order_summary.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        totalproducts.setText("Total Quantity : " + receivedProductsAdapter.getCurrentTotalQuantity());
                        totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                        recyclerview.setAdapter(receivedProductsAdapter);
                        if (receivedProductsAdapter.getCurrentTotalQuantity() > 0) {
                            //order_summary.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ibn_catalogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catalogView();
            }
        });
        ibtn_selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionView();
            }
        });
       /* if(!quatity.getText().toString().equals("0")) {
            quatity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (receivedProductsAdapter != null) {
                        if (s == null && s.toString().equals("")) {
                            quantity = 1;
                        } else {
                            try {
                                quantity = Integer.parseInt(s.toString());
                            } catch (NumberFormatException e) {
                                quantity = 1;
                            }
                        }
                        if (receivedProductsAdapter != null) {
                            receivedProductsAdapter.setMultiplier(quantity);
                        }

                    }
                }
            });
        }*/
  /*      label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerview.getVisibility() == View.VISIBLE) {
                    recyclerview.setVisibility(View.GONE);
                } else {
                    recyclerview.setVisibility(View.VISIBLE);
                }
            }
        });
*/

        if (getArguments() != null) {
            if (getArguments().getBoolean("is_public", false)) {
                from_public=true;
                is_supplier_approved = getArguments().getBoolean("is_supplier_approved",true);
                supplier_container.setVisibility(View.GONE);
                catalog_container.setVisibility(View.GONE);
                getProductsByCatalog(getArguments().getString("ordervalue"), getArguments().getString("full_catalog_order"), false);
                return v;
            }
        }
        getData(orderType);

        return v;
    }

    private void getData(String orderType) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        if (orderType != null) {
            if (orderType.equals("")) {
                getApprovedSuppliers(null, null);

            } /*else
            if (orderType.equals("catalog")) {
                HashMap<String, String> params = new HashMap<>();
                params.put("catalog", orderValue);
                String orderjson = new Gson().toJson(params);
                JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);

                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.GET_CATALOGSUPPLIER, jsonObject, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String selleingcompany = jsonObject.get("selling_company").getAsString();
                        getApprovedSuppliers(selleingcompany, null);
//                        SpinAdapter_Suppliers suppliers = (SpinAdapter_Suppliers) spinner_supplier.getAdapter();
//                        for (int i = 0; i < suppliers.getCount(); i++) {
//                            if (String.valueOf(suppliers.getItem(i).getSelling_company().getId()).equals(selleingcompany)) {
//                                spinner_supplier.setSelection(i);
//                            }
//                        }


                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
//                Log.v("error", error.getErrormessage());
                    }
                });

            } else if (orderType.equals("selection")) {

                HashMap<String, String> params = new HashMap<>();
                params.put("selection", orderValue);
                String orderjson = new Gson().toJson(params);
                JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);

                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.GET_SELECTIONSUPPLIER, jsonObject, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String selleingcompany = jsonObject.get("selling_company").getAsString();
                        getApprovedSuppliers(null, selleingcompany);
//                        SpinAdapter_Suppliers suppliers = (SpinAdapter_Suppliers) spinner_supplier.getAdapter();
//                        for (int i = 0; i < suppliers.getCount(); i++) {
//                            if (String.valueOf(suppliers.getItem(i).getSelling_company().getId()).equals(selleingcompany)) {
//                                spinner_supplier.setSelection(i);
//                            }
//                        }


                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
//                Log.v("error", error.getErrormessage());
                    }
                });

            }*/ else if (orderType.equals("catalog")) {
                /*HashMap<String, String> params = new HashMap<>();
                params.put("catalog", orderValue);
                String orderjson = new Gson().toJson(params);
                JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);*/
                showProgress();
                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs_supplier", orderValue), null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        hideProgress();
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        hideProgress();
                        Log.v("sync response", response);
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String selleingcompany = jsonObject.get("selling_company").getAsString();
                        getApprovedSuppliers(selleingcompany, null);
//                        SpinAdapter_Suppliers suppliers = (SpinAdapter_Suppliers) spinner_supplier.getAdapter();
//                        for (int i = 0; i < suppliers.getCount(); i++) {
//                            if (String.valueOf(suppliers.getItem(i).getSelling_company().getId()).equals(selleingcompany)) {
//                                spinner_supplier.setSelection(i);
//                            }
//                        }


                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
//                Log.v("error", error.getErrormessage());
                    }
                });

            } else if (orderType.equals("selection")) {

             /*   HashMap<String, String> params = new HashMap<>();
                params.put("selection", orderValue);
                String orderjson = new Gson().toJson(params);
                JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
*/
             showProgress();
                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "selections_supplier", orderValue), null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        hideProgress();
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        hideProgress();
                        Log.v("sync response", response);
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String selleingcompany = jsonObject.get("selling_company").getAsString();
                        getApprovedSuppliers(null, selleingcompany);
//                        SpinAdapter_Suppliers suppliers = (SpinAdapter_Suppliers) spinner_supplier.getAdapter();
//                        for (int i = 0; i < suppliers.getCount(); i++) {
//                            if (String.valueOf(suppliers.getItem(i).getSelling_company().getId()).equals(selleingcompany)) {
//                                spinner_supplier.setSelection(i);
//                            }
//                        }


                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
//                Log.v("error", error.getErrormessage());
                    }
                });

            }
        } else {
            getApprovedSuppliers(null, null);
        }
    }


    private void order() {
        if (ordernumber.getText().toString().equals("")) {
            ordernumber.setError("cannot be empty !");
            Toast.makeText(getContext(),"Please enter order number",Toast.LENGTH_SHORT).show();
            return;
        }
        if (quatity.getText().toString().equals("0")) {
            quatity.setError("cannot be 0 !");
            Toast.makeText(getContext(),"Quantity cannot be zero",Toast.LENGTH_SHORT).show();

            return;
        }

        if (receivedProductsAdapter != null && receivedProductsAdapter.getAllProducts() != null && receivedProductsAdapter.getAllProducts().size() > 0) {
            ArrayList<ProductQunatityRate> productQunatityRates = receivedProductsAdapter.getAllProducts();
            String catalog = receivedProductsAdapter.getCatId();
            String sellingCompany;
            if(from_public){
                sellingCompany = getArguments().getString("selling_company");
            }else {
                 sellingCompany = String.valueOf(((Response_Suppliers) spinner_supplier.getSelectedItem()).getSelling_company());
            }
            String company = UserInfo.getInstance(getActivity()).getCompany_id();
          /*  String processing_status = "ordered";
            String customer_status = "finalized";*/
            SalesOrderCreate order = new SalesOrderCreate(ordernumber.getText().toString(), sellingCompany, company, catalog, productQunatityRates,is_supplier_approved);
            String orderjson = new Gson().toJson(order);
            Log.v("orderdetails", orderjson);
            JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);

            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", ""), jsonObject, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    // onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);
                    Toast.makeText(getActivity(), "Purchase order placed successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }


            });
        } else {
            Toast.makeText(getActivity(), "No Products Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        selected_catalog = (TextView) v.findViewById(R.id.selected_catalog);
        selected_selection = (TextView) v.findViewById(R.id.selected_selections);
        spinner_supplier = ((Spinner) v.findViewById(R.id.spinner_supplier));
        spinner_cat = ((Spinner) v.findViewById(R.id.spinner_cat));
        spinner_sel = ((Spinner) v.findViewById(R.id.spinner_sel));
        orderBut = ((Button) v.findViewById(R.id.orderBut));
        Bundle filter = getArguments();
        if (filter != null) {
            orderType = filter.getString("ordertype", "");
            orderValue = filter.getString("ordervalue", "");
        }
        ordernumber = ((EditText) v.findViewById(R.id.input_ordernum));
        subdetail = ((LinearLayout) v.findViewById(R.id.subdetail));
        label = ((LinearLayout) v.findViewById(R.id.label));
        spinner_cat_lay = ((LinearLayout) v.findViewById(R.id.spinner_cat_lay));
        spinner_sel_lay = ((LinearLayout) v.findViewById(R.id.spinner_sel_lay));
        screenshot = ((Button) v.findViewById(R.id.screenshot));
        cancel = ((Button) v.findViewById(R.id.cancel));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        quatity = ((TextView) v.findViewById(R.id.quatity));
        quatity.setText("1");
        catalogcost = ((TextView) v.findViewById(R.id.totalprice));
        totalproducts = ((TextView) v.findViewById(R.id.totalproducts));
        totalprice = ((TextView) v.findViewById(R.id.totalprice));
        productssize = ((TextView) v.findViewById(R.id.productssize));
        recyclerview = ((RecyclerView) v.findViewById(R.id.recyclerview));
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setNestedScrollingEnabled(false);
      /*  if(Application_Singleton.selectedshareCatalog!=null&&!orderType.equals("")&&!orderValue.equals("")) {
            catalogname.setText(Application_Singleton.selectedshareCatalog.getTitle());
        }*/
    }


    private void getProductsByCatalog(final String id, String isFullCatalogOnly, boolean isSelection) {
        if (!isSelection) {
            isFullCatalog = (isFullCatalogOnly.equals("true")) ? true : false;
            //  Log.v("fullproduct",String.valueOf(isFullCatalog));
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", id);
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                //todo full catalog products
                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    Log.v("sync response", response);
                    Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);

                    ArrayList<ProductObjectQuantity> list = new ArrayList<ProductObjectQuantity>();

                    for (ProductObj productObj : productObjs) {
                        list.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getFinal_price())));
                    }
                    productssize.setText("Products " + list.size());
                    receivedProductsAdapter = new ReceivedProductsAdapter((AppCompatActivity) getActivity(), list, id, isFullCatalog, new ReceivedProductsAdapter.ProductChangeListener() {
                        @Override
                        public void onChange() {
                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                            totalproducts.setText("Total Products Qty :" + receivedProductsAdapter.getCurrentTotalQuantity());
                        }
                    });
                    catalogcost.setText("\u20B9 " + receivedProductsAdapter.getCurrentTotal());
                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                    totalproducts.setText("Total Products Qty :" + receivedProductsAdapter.getCurrentTotalQuantity());
                    recyclerview.setAdapter(receivedProductsAdapter);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }


            });
        } else {

            String url = URLConstants.companyUrl(getActivity(), "products_selection", id);
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
                    Log.v("sync response", response);
                    ProductObj[] productObjs = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                    ArrayList<ProductObjectQuantity> list = new ArrayList<ProductObjectQuantity>();

                    for (ProductObj productObj : productObjs) {
                        list.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getFinal_price())));
                    }
                    productssize.setText("Products " + list.size());
                    receivedProductsAdapter = new ReceivedProductsAdapter((AppCompatActivity) getActivity(), list, id, false, new ReceivedProductsAdapter.ProductChangeListener() {
                        @Override
                        public void onChange() {
                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                            totalproducts.setText("Total Products Qty :" + receivedProductsAdapter.getCurrentTotalQuantity());
                        }
                    });
                    catalogcost.setText("\u20B9 " + receivedProductsAdapter.getCurrentTotal());
                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                    totalproducts.setText("Total Products Qty :" + receivedProductsAdapter.getCurrentTotalQuantity());
                    recyclerview.setAdapter(receivedProductsAdapter);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }


            });

        }


    }


    private void getApprovedSuppliers(final String catalogsellerID, final String selectionSellerID) {
        Log.v("purchasetest", "getapprovedsupplierscalled");
        final HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers_approved", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                Log.v("purchasetestcache", "getapprovedsupplierscalled");
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Log.v("purchasetestserver", "getapprovedsupplierscalled");
                Response_Suppliers[] response_suppliers = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                ArrayList<Response_Suppliers> response_supplierses = new ArrayList<Response_Suppliers>();
                String filter = null;
                if (catalogsellerID != null) {
                    filter = catalogsellerID;
                }
                if (selectionSellerID != null) {
                    filter = selectionSellerID;
                }
                for (Response_Suppliers supplier : response_suppliers) {
                    if (filter != null) {
                        if (String.valueOf(supplier.getSelling_company()).equals(filter)) {
                            response_supplierses.add(0, supplier);
                            filter = null;
                        } else {
                            response_supplierses.add(supplier);
                        }

                    } else {
                        response_supplierses.add(supplier);
                    }
                }
                spinAdapter_suppliers = new SpinAdapter_Suppliers(getActivity(), R.layout.spinneritem, response_supplierses);
                spinner_supplier.setAdapter(spinAdapter_suppliers);
                if (sellerID != null && sellerID != "") {
                    SpinAdapter_Suppliers suppliers = (SpinAdapter_Suppliers) spinner_supplier.getAdapter();
                    for (int i = 0; i < suppliers.getCount(); i++) {
                        if (suppliers.getItem(i).getId().equals(sellerID)) {
                            spinner_supplier.setSelection(i);
                        }
                    }
                }

                spinner_supplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Response_Suppliers supplier = spinAdapter_suppliers.getItem(position);
                        checkSelections(String.valueOf(supplier.getSelling_company()));

                        Log.v("purchasetest", "getcatalogs " + supplier.getSelling_company());
                        getCatalogs(String.valueOf(supplier.getSelling_company()));


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });


    }

    private void checkSelections(String sellerID) {

        String url = URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + "?type=push&&company=" + sellerID + "&&buyable=true";
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
                    ibtn_selections.setVisibility(View.VISIBLE);
                } else {
                    selected_selection.setVisibility(View.GONE);
                    ibtn_selections.setVisibility(View.GONE);
                    //catalogView();

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getSelections(String sellerID) {
        String url = URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + "?type=push&company=" + sellerID + "&buyable=true";
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

                SpinAdapter_ProdSel spinAdapter_prodSel = new SpinAdapter_ProdSel(getActivity(), R.layout.spinneritem, response_selections);

                spinner_sel.setAdapter(spinAdapter_prodSel);
                if (response_selections.length > 0) {
                    spinner_sel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            quatity.setText("1");
                            Response_Selection selection = ((Response_Selection) (spinner_sel.getSelectedItem()));
                            //  catalogname.setText(selection.getName());
                            getProductsByCatalog(selection.getId(), null, true);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                if (isFirsttimesel) {
                    isFirsttimesel = false;
                    if (orderValue != null) {
                        SpinAdapter_ProdSel selections = (SpinAdapter_ProdSel) spinner_sel.getAdapter();
                        for (int i = 0; i < selections.getCount(); i++) {
                            if (selections.getItem(i).getId().equals(orderValue)) {
                                spinner_sel.setSelection(i);
                            }
                        }
                    }
                }


               /* if (response_selections.length == 0) {
                    spinner_sel_lay.setVisibility(View.GONE);
                   // subdetail.setVisibility(View.GONE);

                } else {
                    spinner_sel_lay.setVisibility(View.VISIBLE);
                    subdetail.setVisibility(View.VISIBLE);
                }*/
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }


    private void getCatalogs(String companyId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?company=" + companyId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>();

                if (response_catalogMinis.length > 0) {
                    subdetail.setVisibility(View.VISIBLE);
                    catalog_container.setVisibility(View.VISIBLE);
                    linearLayout10.setVisibility(View.VISIBLE);
                    if (orderValue == null) {
                        Collections.addAll(catalogMinis, response_catalogMinis);
                    } else {
                        for (int i = 0; i < response_catalogMinis.length; i++) {
                            if (response_catalogMinis[i].getId().equals(orderValue)) {
                                catalogMinis.add(0, response_catalogMinis[i]);
                            } else {
                                catalogMinis.add(response_catalogMinis[i]);
                            }
                        }
                    }
                    final SpinAdapter_CatalogsMini spinAdapter_catalogsMini = new SpinAdapter_CatalogsMini(getActivity(), R.layout.spinneritem, catalogMinis);
                    spinner_cat.setAdapter(spinAdapter_catalogsMini);


                    spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.v("purchasetest", "getproducts");
                            quatity.setText("1");
                            getProductsByCatalog(catalogMinis.get(position).getId(), catalogMinis.get(position).getFull_catalog_orders_only(), false);
                            //  catalogname.setText(catalogMinis.get(position).getTitle());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


               /* if (response_catalogMinis.length == 0) {
                    spinner_cat_lay.setVisibility(View.GONE);
                    subdetail.setVisibility(View.GONE);
                } else {
                    spinner_cat_lay.setVisibility(View.VISIBLE);
                    subdetail.setVisibility(View.VISIBLE);
                }*/
                } else {
                    catalog_container.setVisibility(View.GONE);
                    linearLayout10.setVisibility(View.GONE);
                    subdetail.setVisibility(View.GONE);

                    Toast.makeText(getContext(),"This Supplier has no catalogs",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

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


    private void selectionView() {
        spinner_cat_lay.setVisibility(View.GONE);
        selected_catalog.setVisibility(View.GONE);
        spinner_sel_lay.setVisibility(View.VISIBLE);
        selected_selection.setVisibility(View.VISIBLE);
        if (spinner_supplier != null) {
            Response_Suppliers supplier = ((Response_Suppliers) spinner_supplier.getSelectedItem());
            if (supplier != null) {
                getSelections(String.valueOf(supplier.getSelling_company()));
            }
        }
    }

    private void catalogView() {
        spinner_cat_lay.setVisibility(View.VISIBLE);
        selected_catalog.setVisibility(View.VISIBLE);
        spinner_sel_lay.setVisibility(View.GONE);
        selected_selection.setVisibility(View.GONE);
        if (spinner_supplier != null) {
            Response_Suppliers supplier = ((Response_Suppliers) spinner_supplier.getSelectedItem());
            if (supplier != null) {
                getCatalogs(String.valueOf(supplier.getSelling_company()));
            }
        }
    }

}
