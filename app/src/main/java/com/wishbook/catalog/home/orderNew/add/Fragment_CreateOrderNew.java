package com.wishbook.catalog.home.orderNew.add;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonadapters.ReceivedProductsAdapterNew;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brokers;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQntRate;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CustomViewModel;
import com.wishbook.catalog.commonmodels.responses.Request_CreateOrderCatalog;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_brokers;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder_new;
import com.wishbook.catalog.home.catalog.details.Activity_ComapnyCatalogs;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.orderNew.adapters.MoreCatalogViewAdapter;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_CreateOrderNew extends GATrackedFragment {


    protected View view;

    //order details
    @BindView(R.id.txt_seller_name)
    TextView sellerName;
    @BindView(R.id.input_ordernum)
    TextInputLayout inputOrderNumber;
    @BindView(R.id.edit_ordernum)
    EditText editOrderNumber;
    @BindView(R.id.linear_supplier_name)
    LinearLayout linearSupplierName;
    @BindView(R.id.linear_buyer_name)
    LinearLayout linearBuyerName;
    //    @BindView(R.id.buyer_select)
//    CustomAutoCompleteTextView autoCompleteTextBuyer;
    @BindView(R.id.spinner_brokers)
    Spinner spinnerBroker;
    @BindView(R.id.txt_input_broker)
    TextInputLayout txtInputBroker;
   /* @BindView(R.id.txt_input_buyer)
    TextInputLayout txt_input_buyer;*/

    @BindView(R.id.input_buyername)
    TextInputLayout input_buyername;
    @BindView(R.id.edit_buyername)
    TextView edit_buyername;
    @BindView(R.id.buyer_container)
    LinearLayout buyer_container;


    // catalog details
    @BindView(R.id.linear_default_catalog_container)
    LinearLayout linearDefaultCatalogContainer;
    @BindView(R.id.txt_catalog_name)
    TextView txtCatalogName;
    @BindView(R.id.input_catalog_quantity)
    TextInputLayout inputCatalogQuantity;
    @BindView(R.id.edit_catalog_quantity)
    EditText editCatalogQuantity;
    @BindView(R.id.default_full_catalog_note)
    TextView default_full_catalog_note;
    @BindView(R.id.radiogroup_pkg_type)
    RadioGroup radioGroupPkgType;

    //discount details
    @BindView(R.id.discount_card)
    CardView discountCard;
    @BindView(R.id.linear_wishbook_discount)
    LinearLayout linear_wishbook_discount;
    @BindView(R.id.linear_seller_discount)
    LinearLayout linear_seller_discount;
    @BindView(R.id.txt_oncash_supplier)
    TextView txtOnCashSupplier;
    @BindView(R.id.txt_oncredit_supplier)
    TextView txtOnCreditSupplier;
    @BindView(R.id.txt_oncash_wishbook)
    TextView txtOnCashWishBook;
    @BindView(R.id.txt_oncredit_wishbook)
    TextView txtOnCreditWishBook;
    @BindView(R.id.txt_discount_terms)
    TextView txt_discount_terms;


    //brokerage section
    @BindView(R.id.relative_brokerage)
    RelativeLayout relative_brokerage;
    @BindView(R.id.txt_brokerage_value)
    TextView txt_brokerage_value;


    @BindView(R.id.order)
    AppCompatButton order;
    @BindView(R.id.totalprice)
    TextView totalprice;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.more_recyclerview)
    RecyclerView more_recyclerview;


    @BindView(R.id.btn_add_more_catalog)
    AppCompatButton btnAddMoreCatalog;
    @BindView(R.id.linear_more_catalog_container)
    LinearLayout linearMoreCatalogContainer;

    @BindView(R.id.edit_order_requirement)
            EditText edit_order_requirement;


    boolean isFullCatalog = false;
    private ProductObj[] productObjs = null;
    private ReceivedProductsAdapterNew receivedProductsAdapter;
    private String orderType;
    private String orderValue;
    private ArrayList<CatalogListItem> listItems = new ArrayList<>();
    ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem> list;
    private int moreCatalogCount = 0;
    ArrayList<CustomViewModel> viewModels;
    private MoreCatalogViewAdapter moreCatalogViewAdapter;
    boolean isEditOrder = false;
    private BuyersList buyer = null;
    AutoCompleteCommonAdapter adapter;
    Response_brokers[] brokersdata = new Response_brokers[]{};
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    Response_catalogMini[] response_catalogMinis = new Response_catalogMini[]{};
    UserInfo userInfo;
    private String buyerselected = null;
    private boolean isDefaultAvailable;

    public Fragment_CreateOrderNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_new_purchase_order, ga_container, true);
        ButterKnife.bind(this, view);
        Bundle filter = getArguments();
        userInfo = UserInfo.getInstance(getActivity());
        initView(view);
        if (filter != null) {
            if (filter.getSerializable("buyer") != null) {
                buyer = (BuyersList) filter.getSerializable("buyer");
                edit_buyername.setText(buyer.getCompany_name());
                edit_buyername.setEnabled(false);
                //autoCompleteTextBuyer.setEnabled(false);
                //autoCompleteTextBuyer.setText(buyer.getCompany_name());
                getBuyerDiscount(buyer.getCompany_id());
                if (buyer.getBroker_id() != null) {
                    if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                        spinnerBroker.setVisibility(View.GONE);
                        txtInputBroker.setVisibility(View.GONE);
                    } else {
                        getbrokers(buyer.getBroker_id());
                    }
                }

            } else {
                getbrokers("");
            }
            orderType = filter.getString("ordertype");
            orderValue = filter.getString("ordervalue");
            if (orderType != null) {
                if (orderType.equals("selections")) {
                    String SelectionType = filter.getString("selectiontype");
                  /*  if (SelectionType != null) {
                        selectionView(true);
                    } else {
                        selectionView(false);
                    }*/
                }
                if (orderValue != null) {
                    isDefaultAvailable = true;
                    getCatalogs(false);
                } else {
                    linearDefaultCatalogContainer.setVisibility(View.GONE);
                    isDefaultAvailable = false;
                }
            } else {
                linearDefaultCatalogContainer.setVisibility(View.GONE);
                isDefaultAvailable = false;
            }

        } else {


        }


        txt_brokerage_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrokerageDialog();
            }
        });



        editCatalogQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editCatalogQuantity.getText().toString().equals("0") && !editCatalogQuantity.getText().toString().equals("")) {
                    if (receivedProductsAdapter != null) {
                        if(list.get(0)!=null) {
                            if(list.get(0).getFeedItemList().get(0).getSetDesign()!=0){
                                list.get(0).setFeedItemList(receivedProductsAdapter.setAllProductsQty(Integer.parseInt(editCatalogQuantity.getText().toString())*list.get(0).getFeedItemList().get(0).getSetDesign(), 0));
                            } else {
                                list.get(0).setFeedItemList(receivedProductsAdapter.setAllProductsQty(Integer.parseInt(editCatalogQuantity.getText().toString()), 0));
                            }
                        }

                        receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                            @Override
                            public void onChange() {
                                recyclerView.setAdapter(receivedProductsAdapter);
                                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                receivedProductsAdapter.notifyDataSetChanged();

                            }
                        });
                        recyclerView.setAdapter(receivedProductsAdapter);
                        recyclerView.setHasFixedSize(false);
                        recyclerView.setNestedScrollingEnabled(false);
                        totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                    }
                } else {
                    if (editCatalogQuantity.getText().toString().trim().equals("0")) {
                        inputCatalogQuantity.setError(getString(R.string.minimum_quantity));
                        editCatalogQuantity.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateQuantity(editCatalogQuantity);
            }
        });

        return view;
    }

    public void initView(View view) {
        linearSupplierName.setVisibility(View.GONE);
        linearBuyerName.setVisibility(View.VISIBLE);
        edit_order_requirement.setVisibility(View.GONE);
        order.setText("Order");

        txt_discount_terms.setPaintFlags(txt_discount_terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final String url = "http://www.wishbook.io/offer-tnc/";
        txt_discount_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
            spinnerBroker.setVisibility(View.GONE);
            txtInputBroker.setVisibility(View.GONE);
        } else {
            List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
            brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
            final Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
            final SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
            spinnerBroker.setAdapter(spinAdapter_brokers);
        }


        //edit_buyername.setEnabled(false);
        // edit_buyername.setShowSoftInputOnFocus(false);
        buyer_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_buyername.getWindowToken(), 0);
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });

       /* autoCompleteTextBuyer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (buyer == null) {
                        if (autoCompleteTextBuyer.getText().toString().isEmpty()) {
                            txt_input_buyer.setErrorEnabled(true);
                            txt_input_buyer.setError(getResources().getString(R.string.empty_field));
                            autoCompleteTextBuyer.requestFocus();
                        } else {
                            txt_input_buyer.setErrorEnabled(true);
                            txt_input_buyer.setError("Buyer name is not in network");
                        }
                    } else {
                        txt_input_buyer.setErrorEnabled(false);
                        txt_input_buyer.setError(null);
                    }
                }
            }
        });
        autoCompleteTextBuyer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getArguments().getSerializable("buyer") == null)
                    buyer = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        autoCompleteTextBuyer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("onitemselected", "" + i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        autoCompleteTextBuyer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                buyer = (BuyersList) parent.getItemAtPosition(position);
                autoCompleteTextBuyer.setError(null);
                getBuyerDiscount(buyer.getCompany_id());
                //make spinner 0
                List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                spinnerBroker.setAdapter(spinAdapter_brokers);
                getbrokers(buyer.getBroker_id());
            }
        });*/

        /*adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, buyerslist, "buyerlist");
        autoCompleteTextBuyer.setAdapter(adapter);
        autoCompleteTextBuyer.setThreshold(1);
        autoCompleteTextBuyer.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.progress_bar));*/


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        list = new ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem>();
        viewModels = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity().getApplicationContext());
        more_recyclerview.setLayoutManager(mLayoutManager1);
        more_recyclerview.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        ((RadioButton) radioGroupPkgType.getChildAt(1)).setChecked(true);
        radioGroupPkgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.radio_default_boxed) {
                    list.get(0).setFeedItemList(receivedProductsAdapter.setAllProductsPkgType((getActivity().getResources().getString(R.string.package_type_box)), 0));

                } else {
                    list.get(0).setFeedItemList(receivedProductsAdapter.setAllProductsPkgType((getActivity().getResources().getString(R.string.package_type_naked)), 0));
                }
                receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                    @Override
                    public void onChange() {
                        recyclerView.setAdapter(receivedProductsAdapter);
                        totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                    }
                });
                recyclerView.setAdapter(receivedProductsAdapter);
                recyclerView.setHasFixedSize(false);
                recyclerView.setNestedScrollingEnabled(false);
                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
            }
        });

    }

    @OnClick(R.id.order)
    public void placeOrder() {
        if (isEmptyValidation(editOrderNumber)) {
            inputOrderNumber.setError(getResources().getString(R.string.empty_field));
            editOrderNumber.requestFocus();
            return;
        }

        if (linearDefaultCatalogContainer.getVisibility() == View.VISIBLE) {
            if (isEmptyValidation(editCatalogQuantity)) {
                inputCatalogQuantity.setError(getResources().getString(R.string.minimum_quantity));
                editCatalogQuantity.requestFocus();
                return;
            }

            if (!isCatalogValidation(editCatalogQuantity)) {
                inputCatalogQuantity.setError(getResources().getString(R.string.minimum_quantity));
                editCatalogQuantity.requestFocus();
                return;
            }
        }

        if (moreCatalogViewAdapter != null) {
            if (moreCatalogViewAdapter.getItemCount() > 0) {
                boolean isvalid = moreCatalogViewAdapter.validateItems();
                if (!isvalid)
                    return;
            }
        }
        createSalesOrder();
    }


    public void createSalesOrder() {
        if (receivedProductsAdapter != null && receivedProductsAdapter.getAllProducts() != null && receivedProductsAdapter.getAllProducts().size() > 0) {
            inputCatalogQuantity.setErrorEnabled(false);
            inputOrderNumber.setErrorEnabled(false);
            ArrayList<ProductQunatityRate> productQunatityRates = receivedProductsAdapter.getAllProducts();
            ArrayList<ProductQntRate> productQntRates = new ArrayList<>();
            for (ProductQunatityRate productQunatityRate : productQunatityRates) {
                ProductQntRate productQntRate = new ProductQntRate(productQunatityRate.getProduct(), String.valueOf(productQunatityRate.getQuantity()), String.valueOf(productQunatityRate.getRate()));
                productQntRate.setPacking_type(productQunatityRate.getPacking_type());
                productQntRates.add(productQntRate);
            }
            String catalog = receivedProductsAdapter.getCatId();
            Request_CreateOrderCatalog request_createOrder = new Request_CreateOrderCatalog();
            //changed due to deputed to
            if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                if (!userInfo.getDeputed_to().equals("") && !userInfo.getDeputed_to().equals("null") && !userInfo.getDeputed_to_name().equals("") && !userInfo.getDeputed_to_name().equals("null")) {
                /*if (is_other_supplier && supplier != null) {
                    request_createOrder.setSeller_company(supplier.getCompany_id());
                } else {
                    request_createOrder.setSeller_company(((Response_Suppliers) spinner_supplier.getSelectedItem()).getSelling_company());
                }*/
                    request_createOrder.setSeller_company(UserInfo.getInstance(getActivity()).getDeputed_to());
                } else {
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
            request_createOrder.setOrder_number(editOrderNumber.getText().toString());
            if(!edit_order_requirement.getText().toString().isEmpty()){
                request_createOrder.setNote(edit_order_requirement.getText().toString().trim());
            }
            request_createOrder.setcatalog(catalog);
            request_createOrder.setProcessing_status("Accepted");
            if (spinnerBroker != null) {
                int selected = spinnerBroker.getSelectedItemPosition();
                if (selected != 0) {
                    if (!((Response_brokers) spinnerBroker.getSelectedItem()).getId().equals(null)) {
                        request_createOrder.setBroker_company(((Response_brokers) spinnerBroker.getSelectedItem()).getCompany_id());
                        request_createOrder.setBrokerage_fees(((Response_brokers) spinnerBroker.getSelectedItem()).getBrokerage_fees());
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
                            startActivity(new Intent(getActivity(), Activity_OrderDetailsNew.class));
                            getActivity().finish();
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);

                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No products in your order", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_add_more_catalog)
    public void addMoreCatalog() {

        Application_Singleton.trackEvent("Create Sales Order", "Click", "Add more Catalog");
       /*
        if (moreCatalogViewAdapter != null) {
            if (moreCatalogViewAdapter.getItemCount() > 0) {
                boolean isvalid = moreCatalogViewAdapter.validateItems();
                if (!isvalid)
                    return;
            }
        }
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = vi.inflate(R.layout.add_more_catalog_item, null);
        moreCatalogCount = moreCatalogCount + 1;
        getCatalogs(true);*/


        ArrayList<Response_catalogMini> temp = new ArrayList<>();
        if(isDefaultAvailable) {
            // add default catalog
            if (getArguments().getString("ordervalue") != null) {
                if(Application_Singleton.selectedshareCatalog!=null){
                    if(Application_Singleton.selectedshareCatalog.getTitle()!=null){
                        Response_catalogMini defaultCatalog = new Response_catalogMini(getArguments().getString("ordervalue"), Application_Singleton.selectedshareCatalog.getTitle());
                        temp.add(defaultCatalog);
                    }
                }

            }
        }
        if (viewModels != null && viewModels.size() > 0) {
            for (int i = 0; i < viewModels.size(); i++) {
                temp.add((Response_catalogMini) viewModels.get(i).getObjects().get(0));
            }
        }
        Intent companyCatalogIntent = new Intent(getActivity(), Activity_ComapnyCatalogs.class);
        //companyCatalogIntent.putExtra("companyId", Application_Singleton.selectedshareCatalog.getSupplier());
        companyCatalogIntent.putExtra("previousCatalog", temp);
        companyCatalogIntent.putExtra("isPurchase",false);
        startActivityForResult(companyCatalogIntent, Application_Singleton.ADD_CATALOG_REQUEST_CODE);
    }

    public void getProductsByCatalog(final String id, final String isFullCatalogOnly, boolean isSelection, final int position, final int quantity, final EditText editText, final TextInputLayout textInputLayout) {
        if (!isSelection) {
            isFullCatalog = (isFullCatalogOnly.equals("true")) ? true : false;
            Log.i("TAG", "getProductsByCatalog: Sales ==>" + isFullCatalogOnly);
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

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    Log.v("sync response", response);
                    Log.i("TAG", "onServerResponse: " + position);
                    Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);

                    ArrayList<ProductObjectQuantity> list1 = new ArrayList<ProductObjectQuantity>();

                    boolean isKurtiSet = false;
                    int number_design_per_set = 1;
                    try{
                        if(response_catalog.getEavdata()!=null) {
                           /* if(response_catalog.getEavdata().getNumber_pcs_design_per_set()!=null) {
                                // for kurti
                                Eavdata eavdata = response_catalog.getEavdata();
                                int number_piece = Integer.parseInt(eavdata.getNumber_pcs_design_per_set());

                                if(number_piece > 1) {
                                    // changes required
                                    Log.i("TAG", "onServerResponse: Numver Piece greater");
                                    isKurtiSet = true;
                                    number_design_per_set = number_piece;
                                    textInputLayout.setHint("Enter Set");
                                    editText.setHint("Enter Set");

                                } else {
                                    Log.i("TAG", "onServerResponse: Numver Piece Else");
                                }
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if(isKurtiSet) {
                        for (ProductObj productObj : productObjs) {
                            int set_quantity = quantity* number_design_per_set;
                            ProductObjectQuantity productObjectQuantity = new ProductObjectQuantity(productObj, set_quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, getResources().getString(R.string.package_type_naked));
                            productObjectQuantity.setSetDesign(number_design_per_set);
                            list1.add(productObjectQuantity);
                        }
                    } else {
                        for (ProductObj productObj : productObjs) {
                            list1.add(new ProductObjectQuantity(productObj, quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, getResources().getString(R.string.package_type_naked)));
                        }
                    }


                    com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem item = new com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem(response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getTotal_products(), list1);
                    if (list != null) {
                        if (list.size() - 1 < position) {
                            list.add(position, item);
                        } else {
                            list.remove(position);
                            list.add(position, item);
                        }
                    }

                    Log.i("TAG", "Array Size: " + list.size() + "\n Position Add" + position);
                    receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, id, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                        @Override
                        public void onChange() {
                            recyclerView.setAdapter(receivedProductsAdapter);
                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                        }
                    });
                    Log.i("TAG", "onServerResponse: Before Set Adapter");
                    recyclerView.setAdapter(receivedProductsAdapter);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setNestedScrollingEnabled(false);
                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

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
                    ArrayList<ProductObjectQuantity> list1 = new ArrayList<ProductObjectQuantity>();
                    ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem> list = new ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem>();

                    for (ProductObj productObj : productObjs) {
                        list1.add(new ProductObjectQuantity(productObj, 1, Float.parseFloat(productObj.getFinal_price())));
                    }
                    // todo changes
                    //CatalogListItem item = new CatalogListItem(response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getTotal_products(), list1);
                    Log.i("TAG", "Array Size: " + list.size() + "\n Position Add" + position);
                  /*  if(list !=null){
                        if(list.size()-1 < position){
                            Log.i("TAG", "Array Size:IF ");
                            list.add(position,item);
                        } else{
                            Log.i("TAG", "Array Size:ELSE ");
                            list.remove(position);
                            list.add(position,item);
                        }
                    }
*/
                    receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, id, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                        @Override
                        public void onChange() {
                            recyclerView.setAdapter(receivedProductsAdapter);
                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                        }
                    });
                    recyclerView.setAdapter(receivedProductsAdapter);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setNestedScrollingEnabled(false);
                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }


            });

        }


    }

    private boolean isEmptyValidation(EditText editText) {
        if (editText.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    private boolean isCatalogValidation(EditText editText) {
        if (!editText.getText().toString().trim().isEmpty()) {
            if (Integer.parseInt(editText.getText().toString().trim()) < 0)
                return false;
        }
        return true;
    }

    private void getCatalogs(final boolean isSetSpinner) {
        if (response_catalogMinis.length < 1) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", ""), null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    Log.v("sync response", response);
                    response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    //removeDuplicate

                    setCatalogAdapter(isSetSpinner);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } else {
            setCatalogAdapter(isSetSpinner);
        }
    }

    private void setCatalogAdapter(boolean isSetSpinner) {
        if (!isSetSpinner) {
            if (orderType != null) {
                if ((orderType.equals("catalogs") || orderType.equals("catalog")) && orderValue != null) {
                    for (int i = 0; i < response_catalogMinis.length; i++) {
                        if (response_catalogMinis[i].getId().equals(orderValue)) {
                            txtCatalogName.setText(response_catalogMinis[i].getTitle().toString());
                            isFullCatalog = (response_catalogMinis[i].getFull_catalog_orders_only().equals("true") ? true : false);
                            if (isFullCatalog)
                                default_full_catalog_note.setVisibility(View.VISIBLE);
                            else
                                default_full_catalog_note.setVisibility(View.GONE);
                            getProductsByCatalog(response_catalogMinis[i].getId(), response_catalogMinis[i].getFull_catalog_orders_only(), false, 0, 1,editCatalogQuantity,inputCatalogQuantity);
                        }
                    }
                }
            }
        } else {
            final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>();
            if (response_catalogMinis.length > 0) {
                final int layoutPosition = moreCatalogCount;
                Log.i("TAG", "layout Position" + layoutPosition);
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
                Response_catalogMini response_catalogMini = new Response_catalogMini("0", "Select Catalog");
                catalogMinis.add(0, response_catalogMini);

                if (isDefaultAvailable) {
                    for (int j = 0; j < catalogMinis.size(); j++) {
                        if (catalogMinis.get(j).getId().equals(orderValue)) {
                            catalogMinis.remove(j);
                        }
                        if (moreCatalogViewAdapter != null) {
                            ArrayList<String> duplicate = moreCatalogViewAdapter.getField1();
                            for (int i = 0; i < duplicate.size(); i++) {
                                if (catalogMinis.get(j).getTitle().equals(duplicate.get(i))) {
                                    catalogMinis.remove(j);
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < catalogMinis.size(); j++) {
                        if (moreCatalogViewAdapter != null) {
                            ArrayList<String> duplicate = moreCatalogViewAdapter.getField1();
                            for (int i = 0; i < duplicate.size(); i++) {
                                if (catalogMinis.get(j).getTitle().equals(duplicate.get(i))) {
                                    catalogMinis.remove(j);
                                }
                            }
                        }
                    }
                }
                CustomViewModel customViewModel = new CustomViewModel(moreCatalogCount, catalogMinis);
                viewModels.add(customViewModel);
                if (moreCatalogViewAdapter == null) {
                    moreCatalogViewAdapter = new MoreCatalogViewAdapter(getActivity(), viewModels, Fragment_CreateOrderNew.this, new MoreCatalogViewAdapter.ViewChangeListener() {
                        @Override
                        public void onRemoveView(int position) {
                            try {
                                list.remove(position);
                                receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                    @Override
                                    public void onChange() {
                                        recyclerView.setAdapter(receivedProductsAdapter);

                                    }
                                });
                                recyclerView.setAdapter(receivedProductsAdapter);
                                recyclerView.setHasFixedSize(false);
                                recyclerView.setNestedScrollingEnabled(false);
                                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onEditQuantity(int position, int quantity) {
                            try {
                                if (receivedProductsAdapter != null) {
                                    if(list.get(position)!=null) {
                                        if(list.get(position).getFeedItemList().get(0).getSetDesign()!=0) {

                                            int setQuantity = quantity*list.get(position).getFeedItemList().get(0).getSetDesign();
                                            Log.i("TAG", "onEditQuantity: Create "+setQuantity);
                                            list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsQty(setQuantity, position));
                                        }else {
                                            list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsQty(quantity, position));
                                        }
                                    }
                                    receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                        @Override
                                        public void onChange() {
                                            recyclerView.setAdapter(receivedProductsAdapter);
                                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                                        }
                                    });
                                    recyclerView.setAdapter(receivedProductsAdapter);
                                    recyclerView.setHasFixedSize(false);
                                    recyclerView.setNestedScrollingEnabled(false);
                                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onEditPkgType(String pkgtype, int position) {
                            try {
                                if (receivedProductsAdapter != null) {
                                    list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsPkgType(pkgtype, position));
                                    receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                        @Override
                                        public void onChange() {
                                            recyclerView.setAdapter(receivedProductsAdapter);
                                            totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                                        }
                                    });
                                    recyclerView.setAdapter(receivedProductsAdapter);
                                    recyclerView.setHasFixedSize(false);
                                    recyclerView.setNestedScrollingEnabled(false);
                                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, isDefaultAvailable);
                    more_recyclerview.setAdapter(moreCatalogViewAdapter);
                    more_recyclerview.setNestedScrollingEnabled(false);
                    more_recyclerview.setHasFixedSize(false);
                } else {
                    moreCatalogViewAdapter.notifyItemInserted(viewModels.size());
                    more_recyclerview.setNestedScrollingEnabled(false);
                    more_recyclerview.setHasFixedSize(false);
                }
            } else {
                moreCatalogCount = moreCatalogCount - 1;
                Toast.makeText(getContext(), "This Supplier has no catalogs", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void getBuyerDiscount(String buyerCompanyId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + "/?company=" + buyerCompanyId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_Suppliers[] response_seller = new Gson().fromJson(response, Response_Suppliers[].class);
                if (response_seller != null && Application_Singleton.configResponse != null) {
                    if (response_seller.length > 0) {
                        Application_Singleton.selectedSupplier = response_seller[0];
                        if (response_seller[0].getDiscount() != null && !response_seller[0].getDiscount().equals("0.00")) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            discountCard.setVisibility(View.VISIBLE);
                            if (response_seller[0].getDiscount() != null && !response_seller[0].getDiscount().equals("0.00"))
                                txtOnCreditSupplier.setText(String.valueOf(Math.round(Float.parseFloat(response_seller[0].getDiscount()))) + "% off");
                        }
                        if (response_seller[0].getCash_discount() != null && !response_seller[0].getCash_discount().equals("0.00")) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            discountCard.setVisibility(View.VISIBLE);
                            if (response_seller[0].getCash_discount() != null && !response_seller[0].getDiscount().equals("0.00"))
                                txtOnCashSupplier.setText(String.valueOf(Math.round(Float.parseFloat(response_seller[0].getCash_discount()))) + "% off");
                        }
                    } else {
                        linear_seller_discount.setVisibility(View.GONE);
                    }
                } else {
                    discountCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Create Purchase request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000 && resultCode == 50) {
            // To Edit Order
            isEditOrder = true;
            setEnableFalse();

        }
        if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
        if (requestCode == 5000 && resultCode == 0) {
            getActivity().finish();
        }

        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                edit_buyername.setText(buyer.getCompany_name());
                getBuyerDiscount(buyer.getCompany_id());
                //make spinner 0
                if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                    spinnerBroker.setVisibility(View.GONE);
                    txtInputBroker.setVisibility(View.GONE);
                } else {
                    List<Response_brokers> brokers = new ArrayList<Response_brokers>(Arrays.asList(brokersdata));
                    brokers.add(0, new Response_brokers("Select Broker", "1", "1"));
                    Response_brokers[] brokersdata1 = brokers.toArray(new Response_brokers[brokers.size()]);
                    SpinAdapter_brokers spinAdapter_brokers = new SpinAdapter_brokers(getActivity(), R.layout.spinneritem, brokersdata1);
                    spinnerBroker.setAdapter(spinAdapter_brokers);
                    getbrokers(buyer.getBroker_id());
                }
            }
        }
        if (requestCode == Application_Singleton.ADD_CATALOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringArrayListExtra("selectedCatalog") != null) {
                ArrayList<Response_catalogMini> arrayList = (ArrayList<Response_catalogMini>) data.getSerializableExtra("selectedCatalog");
                for (int i = 0; i < arrayList.size(); i++) {
                    moreCatalogCount = moreCatalogCount + 1;
                    ArrayList<Response_catalogMini> tempList = new ArrayList<>();
                    tempList.add(arrayList.get(i));
                    CustomViewModel customViewModel = new CustomViewModel(moreCatalogCount, tempList);
                    viewModels.add(customViewModel);
                    if (moreCatalogViewAdapter == null) {
                        moreCatalogViewAdapter = new MoreCatalogViewAdapter(getActivity(), viewModels, Fragment_CreateOrderNew.this, new MoreCatalogViewAdapter.ViewChangeListener() {
                            @Override
                            public void onRemoveView(int position) {
                                try {
                                    list.remove(position);
                                    receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                        @Override
                                        public void onChange() {
                                            recyclerView.setAdapter(receivedProductsAdapter);

                                        }
                                    });
                                    recyclerView.setAdapter(receivedProductsAdapter);
                                    recyclerView.setHasFixedSize(false);
                                    recyclerView.setNestedScrollingEnabled(false);
                                    totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onEditQuantity(int position, int quantity) {
                                try {
                                    if (receivedProductsAdapter != null) {
                                        if(list.get(position)!=null) {
                                            if(list.get(position).getFeedItemList().get(0).getSetDesign()!=0) {
                                                int setQuantity = quantity*list.get(position).getFeedItemList().get(0).getSetDesign();
                                                list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsQty(setQuantity, position));
                                            }else {
                                                list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsQty(quantity, position));
                                            }
                                        }
                                        receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                            @Override
                                            public void onChange() {
                                                recyclerView.setAdapter(receivedProductsAdapter);
                                                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                                            }
                                        });
                                        recyclerView.setAdapter(receivedProductsAdapter);
                                        recyclerView.setHasFixedSize(false);
                                        recyclerView.setNestedScrollingEnabled(false);
                                        totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onEditPkgType(String pkgtype, int position) {
                                try {
                                    if (receivedProductsAdapter != null) {
                                        list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsPkgType(pkgtype, position));
                                        receivedProductsAdapter = new ReceivedProductsAdapterNew(getActivity(), list, isFullCatalog, new ReceivedProductsAdapterNew.ProductChangeListener() {
                                            @Override
                                            public void onChange() {
                                                recyclerView.setAdapter(receivedProductsAdapter);
                                                totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());

                                            }
                                        });
                                        recyclerView.setAdapter(receivedProductsAdapter);
                                        recyclerView.setHasFixedSize(false);
                                        recyclerView.setNestedScrollingEnabled(false);
                                        totalprice.setText("Total Value : \u20B9 " + receivedProductsAdapter.getCurrentTotal());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, isDefaultAvailable);
                        more_recyclerview.setAdapter(moreCatalogViewAdapter);
                        more_recyclerview.setNestedScrollingEnabled(false);
                        more_recyclerview.setHasFixedSize(false);
                    } else {
                        moreCatalogViewAdapter.notifyItemInserted(viewModels.size());
                        more_recyclerview.setNestedScrollingEnabled(false);
                        more_recyclerview.setHasFixedSize(false);
                    }
                }
            }

        }
    }


    public void showBrokerageDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("Add Brokerage")
                .content("Enter Brokerage")
                .inputType(InputType.TYPE_CLASS_NUMBER).inputRange(0, 3)
                .content("You can enter brokerage % only once. Any changes in the brokerage can be made by supplier only.\n")
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText().getText().toString().isEmpty()) {
                            dialog.getInputEditText().setError(getResources().getString(R.string.empty_field));
                            return;
                        }
                        String orderBrokerage = dialog.getInputEditText().getText().toString().trim();

                        ((Response_brokers) spinnerBroker.getSelectedItem()).setBrokerage_fees(orderBrokerage);

                        relative_brokerage.setVisibility(View.VISIBLE);
                        txt_brokerage_value.setText(orderBrokerage + "%");


                    }
                }).positiveText("Save")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        materialDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        materialDialog.getContentView().setTextColor(getResources().getColor(R.color.purchase_dark_gray));

    }


    public void setEnableFalse() {
        editOrderNumber.setEnabled(false);
    }

    private void getbrokers(final String id) {
        // if (id != null && id != "" && id != "null") {
        if (brokersdata.length < 1) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
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
                    spinnerBroker.setAdapter(spinAdapter_brokers);


                    if (id != null) {
                        for (int i = 0; i < brokers.size(); i++) {
                            if (id.equals(brokers.get(i).getCompany_id())) {
                                spinnerBroker.setSelection(i);
                                // brokers.get(i)
                                relative_brokerage.setVisibility(View.VISIBLE);
                                txt_brokerage_value.setText(brokers.get(i).getBrokerage_fees() + "%");
                            }
                        }
                    } else {
                        spinnerBroker.setSelection(0);
                        relative_brokerage.setVisibility(View.GONE);
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
            spinnerBroker.setAdapter(spinAdapter_brokers);

            if (id != null) {
                for (int i = 0; i < brokers.size(); i++) {
                    if (id.equals(brokers.get(i).getCompany_id())) {
                        spinnerBroker.setSelection(i);
                    }
                }
            } else {
                spinnerBroker.setSelection(0);
                relative_brokerage.setVisibility(View.GONE);
            }

        }

        spinnerBroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    if (brokersdata.length <= 1) {
                        spinnerBroker.setEnabled(false);
                        //txtInputBroker.setErrorEnabled(true);
                        //txtInputBroker.setError("No broker in your network");
                        //Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                        //txtInputBroker.startAnimation(shake);
                    } else {
                        spinnerBroker.setEnabled(true);
                        //txtInputBroker.setErrorEnabled(false);
                        //txtInputBroker.setError(null);
                    }
                } else {

                    if (brokersdata[i - 1] != null && brokersdata[i - 1].getBrokerage_fees() != null) {
                        relative_brokerage.setVisibility(View.VISIBLE);
                        txt_brokerage_value.setVisibility(View.VISIBLE);
                        txt_brokerage_value.setText(brokersdata[i - 1].getBrokerage_fees() + "%");
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //}
    }

    private void validateQuantity(EditText s) {
        if (!TextUtils.isEmpty(s.getText())) {
            inputCatalogQuantity.setError(null);
            if (s.getText().toString().equals("0")) {
                inputCatalogQuantity.setError(getString(R.string.minimum_quantity));
            }
        } else {
            inputCatalogQuantity.setError(getString(R.string.empty_field));
        }
    }


}
