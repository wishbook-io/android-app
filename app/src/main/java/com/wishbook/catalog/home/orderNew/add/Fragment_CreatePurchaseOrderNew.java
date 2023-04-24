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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.wishbook.catalog.commonadapters.ReceivedProductsAdapterNew;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.InvoiceItemSet;
import com.wishbook.catalog.commonmodels.postpatchmodels.OrderInvoiceCreate;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.BuyerDiscountRequest;
import com.wishbook.catalog.commonmodels.responses.CustomViewModel;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.catalog.details.Activity_ComapnyCatalogs;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.orderNew.adapters.MoreCatalogViewAdapter;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orderNew.details.Activity_PaymentOrder;
import com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Fragment_CreatePurchaseOrderNew extends GATrackedFragment {


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

    @BindView(R.id.spinner_brokers)
    Spinner spinnerBroker;
    @BindView(R.id.txt_input_broker)
    TextInputLayout txtInputBroker;

    @BindView(R.id.input_buyername)
    TextInputLayout input_buyername;
    @BindView(R.id.edit_buyername)
    TextView edit_buyername;
    @BindView(R.id.buyer_container)
    LinearLayout buyer_container;


    // catalog details
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


    public String sellerID = "";
    Boolean is_supplier_approved = true;
    Boolean from_public = false;
    boolean isFullCatalog = false;
    private ProductObj[] productObjs = null;
    private ReceivedProductsAdapterNew receivedProductsAdapter;
    private String orderType;
    private String orderValue;
    private ArrayList<com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem> listItems = new ArrayList<>();
    ArrayList<CatalogListItem> list;
    private int moreCatalogCount = 0;
    ArrayList<CustomViewModel> viewModels;
    private MoreCatalogViewAdapter moreCatalogViewAdapter;
    boolean isEditOrder = false;
    private boolean isTrustedSeller = false;
    private boolean isBrokerageOrder = false;
    private BuyersList buyer = null;
    private String brokerage_fees;

    public Fragment_CreatePurchaseOrderNew() {
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
        if (filter != null) {
            orderType = filter.getString("ordertype", "");
            orderValue = filter.getString("ordervalue", "");
            isBrokerageOrder = filter.getBoolean("isBrokerageOrder", false);

        }
        initView();
        if (getArguments() != null) {
            if (!getArguments().getString("sellerid", "").equals("")) {
                sellerID = getArguments().getString("sellerid", "");
            }
        }
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

        if (getArguments() != null) {
            if (getArguments().getBoolean("is_public", false)) {
                from_public = true;
                is_supplier_approved = getArguments().getBoolean("is_supplier_approved", true);
                if (getArguments().getString("full_catalog_order") != null) {
                    isFullCatalog = (getArguments().getString("full_catalog_order").equals("true")) ? true : false;
                    if (isFullCatalog)
                        default_full_catalog_note.setVisibility(View.VISIBLE);
                    else
                        default_full_catalog_note.setVisibility(View.GONE);
                }
                getProductsByCatalog(getArguments().getString("ordervalue"), getArguments().getString("full_catalog_order"), false, 0, 1,editCatalogQuantity,inputCatalogQuantity);
                if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getSupplier() != null) {
                    getSellerDiscount(Application_Singleton.selectedshareCatalog.getSupplier());
                }

                getWishBookDiscount();
                return view;
            } else {
                from_public = false;
                is_supplier_approved = Application_Singleton.selectedshareCatalog.getIs_supplier_approved();
                if (Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only() != null) {
                    isFullCatalog = (Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only().equals("true")) ? true : false;
                    if (isFullCatalog)
                        default_full_catalog_note.setVisibility(View.VISIBLE);
                    else
                        default_full_catalog_note.setVisibility(View.GONE);
                }

                getProductsByCatalog(getArguments().getString("ordervalue"), Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only(), false, 0, 1,editCatalogQuantity,inputCatalogQuantity);
                if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getSupplier() != null) {
                    getSellerDiscount(Application_Singleton.selectedshareCatalog.getSupplier());
                }
                getWishBookDiscount();


            }
        }
        return view;
    }

    public void initView() {
        linearSupplierName.setVisibility(View.VISIBLE);
        linearBuyerName.setVisibility(View.GONE);

        if (isBrokerageOrder) {
            linearBuyerName.setVisibility(View.VISIBLE);
            txtInputBroker.setVisibility(View.GONE);
            buyer_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyboardUtils.hideKeyboard(getActivity());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_buyername.getWindowToken(), 0);
                    Log.i("TAG", "onClick: Supplier Id==>" + Application_Singleton.selectedshareCatalog.getSupplier_id());
                    startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                    .putExtra("type", "connected_buyers")
                                    .putExtra("selling_comapny_id", Application_Singleton.selectedshareCatalog.getSupplier())
                                    .putExtra("supplier_id", Application_Singleton.selectedshareCatalog.getSupplier_id()),
                            Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                    //startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class),Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                }
            });
        }
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


        if (Application_Singleton.selectedshareCatalog != null) {
            sellerName.setText(Application_Singleton.selectedshareCatalog.getSupplier_name());
            txtCatalogName.setText(Application_Singleton.selectedshareCatalog.getTitle());

            if(Application_Singleton.selectedshareCatalog.getEavdata()!=null) {
                Log.i("TAG", "initView: Get Eav Data"+Application_Singleton.selectedshareCatalog.getEavdata().toString());

            }
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        list = new ArrayList<CatalogListItem>();
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
        editOrderNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateOrderNumber(editOrderNumber);
            }
        });
    }

    @OnClick(R.id.order)
    public void order() {
        if (isEmptyValidation(editOrderNumber)) {
            inputOrderNumber.setError(getResources().getString(R.string.empty_field));
            editOrderNumber.requestFocus();
            return;
        }
        if (isEmptyValidation(editCatalogQuantity)) {
            inputCatalogQuantity.setError("cannot be 0 !");
            editCatalogQuantity.requestFocus();
            return;
        }

        if (!isCatalogValidation(editCatalogQuantity)) {
            inputCatalogQuantity.setError(getResources().getString(R.string.minimum_quantity));
            editCatalogQuantity.requestFocus();
            return;
        }


        if (moreCatalogViewAdapter != null) {
            if (moreCatalogViewAdapter.getItemCount() > 0) {
                boolean isvalid = moreCatalogViewAdapter.validateItems();
                if (!isvalid)
                    return;
            }
        }

        if(Activity_Home.pref !=null) {
            String kyc_gstin = Activity_Home.pref.getString("entered_gst", null);
            if(kyc_gstin!=null) {
                if (!(kyc_gstin != null && !kyc_gstin.equals("null"))) {
                    showGSTPopup();
                    return;
                }
            }

        }



        Application_Singleton.trackEvent("NewPurchaseContinue", "Click", "NewPurchaseContinue");

        createOrder();
    }

    public void createOrder() {
        if (receivedProductsAdapter != null && receivedProductsAdapter.getAllProducts() != null && receivedProductsAdapter.getAllProducts().size() > 0) {
            inputCatalogQuantity.setErrorEnabled(false);
            inputCatalogQuantity.setErrorEnabled(false);
            ArrayList<ProductQunatityRate> productQunatityRates = receivedProductsAdapter.getAllProducts();
            String catalog = receivedProductsAdapter.getCatId();
            String sellingCompany;
            if (from_public) {
                sellingCompany = getArguments().getString("selling_company");
            } else {
                sellingCompany = Application_Singleton.selectedshareCatalog.getSupplier();
                //sellingCompany = String.valueOf(((Response_Suppliers) spinner_supplier.getSelectedItem()).getSelling_company());
            }
            String company = UserInfo.getInstance(getActivity()).getCompany_id();
            String broker_id = "";
            if (isBrokerageOrder) {
                if (UserInfo.getInstance(getActivity()).getBroker()) {
                    company = buyer.getCompany_id();
                    broker_id = UserInfo.getInstance(getActivity()).getCompany_id();


                }
            }



            SalesOrderCreate order = new SalesOrderCreate(editOrderNumber.getText().toString(), sellingCompany, company, "Draft", catalog, productQunatityRates, is_supplier_approved);
            if(!edit_order_requirement.getText().toString().isEmpty()){
                order.setNote(edit_order_requirement.getText().toString().trim());
            }

            if (UserInfo.getInstance(getActivity()).getBroker()) {
                order.setBroker_company(broker_id);
                order.setBrokerage_fees(brokerage_fees);
            }

            String orderjson = new Gson().toJson(order);
            Log.v("orderdetails", orderjson);
            JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            if (!isEditOrder) {
                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", ""), jsonObject, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        Gson gson = new Gson();
                        final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                        Application_Singleton.selectedOrder = selectedOrder;
                        //Toast.makeText(getActivity(), "Purchase order placed successfully", Toast.LENGTH_SHORT).show();
                        configPurchaseOrder(isBrokerageOrder);
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            } else {
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        Gson gson = new Gson();
                        final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                        Application_Singleton.selectedOrder = selectedOrder;
                        //Toast.makeText(getActivity(), "Purchase order placed successfully", Toast.LENGTH_SHORT).show();
                        configPurchaseOrder(isBrokerageOrder);
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

        } else {
            Toast.makeText(getActivity(), "No Products Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_add_more_catalog)
    public void addMoreCatalog() {
        Application_Singleton.trackEvent("Create Purchase Order", "Click", "Add more Catalog");
        ArrayList<Response_catalogMini> temp = new ArrayList<>();
        // add default catalog
        if (getArguments().getString("ordervalue") != null) {
            Response_catalogMini defaultCatalog = new Response_catalogMini(getArguments().getString("ordervalue"), Application_Singleton.selectedshareCatalog.getTitle());
            temp.add(defaultCatalog);
        }
        if (viewModels != null && viewModels.size() > 0) {
            for (int i = 0; i < viewModels.size(); i++) {
                temp.add((Response_catalogMini) viewModels.get(i).getObjects().get(0));
            }
        }
        Intent companyCatalogIntent = new Intent(getActivity(), Activity_ComapnyCatalogs.class);
        companyCatalogIntent.putExtra("companyId", Application_Singleton.selectedshareCatalog.getSupplier());
        companyCatalogIntent.putExtra("previousCatalog", temp);
        companyCatalogIntent.putExtra("isPurchase",true);
        startActivityForResult(companyCatalogIntent, Application_Singleton.ADD_CATALOG_REQUEST_CODE);




       /* getCatalogs(Application_Singleton.selectedshareCatalog.getSupplier());*/
    }

    public void getProductsByCatalog(final String id, final String isFullCatalogOnly, boolean isSelection, final int position, final int quantity, final EditText editText, final TextInputLayout textInputLayout) {
        if (!isSelection) {
            isFullCatalog = (isFullCatalogOnly.equals("true")) ? true : false;
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
                    if (position == 0) {

                        isTrustedSeller = Application_Singleton.selectedshareCatalog.getIs_trusted_seller();
                        Log.i("TAG", "==============onServerResponse: Is Trusted Seller=========="+isTrustedSeller);
                    }
                    productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);

                    ArrayList<ProductObjectQuantity> list1 = new ArrayList<ProductObjectQuantity>();

                    boolean isKurtiSet = false;
                    int number_design_per_set = 1;
                    try {
                        if(response_catalog.getEavdata()!=null) {
                            /*if(response_catalog.getEavdata().getNumber_pcs_design_per_set()!=null) {
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(isKurtiSet) {
                        // for not kurti set
                        for (ProductObj productObj : productObjs) {
                            int set_quantity = quantity* number_design_per_set;
                            ProductObjectQuantity productObjectQuantity = new ProductObjectQuantity(productObj, set_quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, getResources().getString(R.string.package_type_naked));
                            productObjectQuantity.setSetDesign(number_design_per_set);
                            list1.add(productObjectQuantity);
                        }
                    } else {
                        // kurti set
                        for (ProductObj productObj : productObjs) {
                            list1.add(new ProductObjectQuantity(productObj, quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, getResources().getString(R.string.package_type_naked)));
                        }
                    }


                    CatalogListItem item = new CatalogListItem(response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getTotal_products(), list1);
                    if (list != null) {
                        if (list.size() - 1 < position) {
                            list.add(position, item);
                        } else {
                            list.remove(position);
                            list.add(position, item);
                        }
                    }
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
                    ArrayList<CatalogListItem> list = new ArrayList<CatalogListItem>();

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
            if (Integer.parseInt(editText.getText().toString().trim()) == 0) {
                return false;
            }

            if (Integer.parseInt(editText.getText().toString().trim()) < 0)
                return false;
        }
        return true;
    }

    private void getCatalogs(String companyId) {
     /*   Log.i("TAG", "===============getCatalogs: ================");
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?company=" + companyId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
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
                    CustomViewModel customViewModel = new CustomViewModel(moreCatalogCount, catalogMinis);
                    viewModels.add(customViewModel);

                    if (moreCatalogViewAdapter == null) {
                        moreCatalogViewAdapter = new MoreCatalogViewAdapter(getActivity(), viewModels, Fragment_CreatePurchaseOrderNew.this, new MoreCatalogViewAdapter.ViewChangeListener() {
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
                                        list.get(position).setFeedItemList(receivedProductsAdapter.setAllProductsQty(quantity, position));
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
                        });
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
                    Log.i("TAG", "addMoreCatalog: " + Application_Singleton.selectedshareCatalog.getSupplier());
                    Toast.makeText(getContext(), "This Supplier has no catalogs", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
            }
        });*/

    }

    public void getSellerDiscount(final String supplierId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + "/?company=" + supplierId, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("SellerD", response);
                final Response_Suppliers[] response_seller = new Gson().fromJson(response, Response_Suppliers[].class);
                if (isBrokerageOrder) {
                    if (response_seller.length > 0) {
                        relative_brokerage.setVisibility(View.VISIBLE);
                        brokerage_fees = response_seller[0].getBrokerage_fees();
                        txt_brokerage_value.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        txt_brokerage_value.setText(brokerage_fees + "%");
                    }
                }
                if (response_seller != null && Application_Singleton.configResponse != null) {
                    if (response_seller.length > 0) {
                        Application_Singleton.selectedSupplier = response_seller[0];
                        discountCard.setVisibility(View.VISIBLE);
                        if (response_seller[0].getDiscount() != null && !response_seller[0].getDiscount().equals("0.00")) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            if (response_seller[0].getDiscount() != null && !response_seller[0].getDiscount().equals("0.00"))
                                txtOnCreditSupplier.setText(String.valueOf(Float.parseFloat(response_seller[0].getDiscount())) + "% off");
                        }
                        if (response_seller[0].getCash_discount() != null && !response_seller[0].getCash_discount().equals("0.00")) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            if (response_seller[0].getCash_discount() != null && !response_seller[0].getCash_discount().equals("0.00")) {
                                txtOnCashSupplier.setText(String.valueOf(Float.parseFloat(response_seller[0].getCash_discount())) + "% off");
                            }

                        }
                    } else {
                        getBuyerDiscount(supplierId);
                    }
                } else {
                    discountCard.setVisibility(View.GONE);
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
            }
        });
    }

    public void getBuyerDiscount(String supplierId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.BUYER_DISCOUNT_URL + "?supplier=" + supplierId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                BuyerDiscountRequest buyerDiscountRequests[] = Application_Singleton.gson.fromJson(response, BuyerDiscountRequest[].class);
                if (buyerDiscountRequests.length > 0) {
                    for (BuyerDiscountRequest discount : buyerDiscountRequests) {
                        if (discount.getBuyer_type().equals(getResources().getString(R.string.buyer_public))) {
                            if (discount.getDiscount() != null && !discount.getDiscount().equals("0.00")) {
                                discountCard.setVisibility(View.VISIBLE);
                                linear_seller_discount.setVisibility(View.VISIBLE);
                                txtOnCreditSupplier.setText(String.valueOf(Float.parseFloat(discount.getDiscount())) + "% off");
                            }
                            if (discount.getCash_discount() != null && !discount.getCash_discount().equals("0.00")) {
                                discountCard.setVisibility(View.VISIBLE);
                                linear_seller_discount.setVisibility(View.VISIBLE);
                                txtOnCashSupplier.setText(String.valueOf(Float.parseFloat(discount.getCash_discount())) + "% off");
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

    public void getWishBookDiscount() {
        if (Application_Singleton.configResponse != null) {
            Log.i("TAG", "getWishBookDiscount: not null");
            discountCard.setVisibility(View.VISIBLE);
            for (int i = 0; i < Application_Singleton.configResponse.size(); i++) {
                if (Application_Singleton.configResponse.get(i).getKey().equals(getResources().getString(R.string.wb_credit_discount))) {
                    if (Application_Singleton.configResponse.get(i).getValue() != null && !Application_Singleton.configResponse.get(i).getValue().equals("0.00")) {
                        txtOnCreditWishBook.setText(Application_Singleton.configResponse.get(i).getValue() + "% CASHBACK");
                        linear_wishbook_discount.setVisibility(View.VISIBLE);
                    }
                }
                if (Application_Singleton.configResponse.get(i).getKey().equals(getResources().getString(R.string.wb_cash_discount))) {
                    if (Application_Singleton.configResponse.get(i).getValue() != null && !Application_Singleton.configResponse.get(i).getValue().equals("0.00")) {
                        if (isTrustedSeller) {
                            Log.i("TAG", "==============onServerResponse: Is Trusted Seller True==========");
                            txtOnCashWishBook.setVisibility(View.VISIBLE);
                            txtOnCashWishBook.setText(Application_Singleton.configResponse.get(i).getValue() + "% CASHBACK");
                        } else {
                            Log.i("TAG", "==============onServerResponse: Is Trusted Seller False==========");
                         /*  LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                   ViewGroup.LayoutParams.MATCH_PARENT,
                                   ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
                           txtOnCashWishBook.setLayoutParams(param);*/
                            txtOnCashWishBook.setVisibility(View.GONE);
                            for (int j = 0; j < Application_Singleton.configResponse.size(); j++) {
                                if (Application_Singleton.configResponse.get(j).getKey().equals(getResources().getString(R.string.wb_credit_discount))) {
                                    if (Application_Singleton.configResponse.get(j).getValue() != null && !Application_Singleton.configResponse.get(j).getValue().equals("0.00")) {
                                        txtOnCashWishBook.setVisibility(View.VISIBLE);
                                        txtOnCashWishBook.setText(Application_Singleton.configResponse.get(j).getValue() + "% CASHBACK");
                                    }
                                }
                            }

                        }
                        linear_wishbook_discount.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            linear_wishbook_discount.setVisibility(View.GONE);
        }
    }

    public void createInvoice(Response_buyingorder selectedOrder) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        OrderInvoiceCreate orderInvoiceCreate = new OrderInvoiceCreate();
        ArrayList<InvoiceItemSet> invoiceItemSets = new ArrayList<InvoiceItemSet>();
        if (selectedOrder.getCatalogs() != null && selectedOrder.getCatalogs().size() > 0) {
            for (int i = 0; i < selectedOrder.getCatalogs().size(); i++) {
                for (int j = 0; j < selectedOrder.getCatalogs().get(i).getProducts().size(); j++) {
                    invoiceItemSets.add(
                            new InvoiceItemSet(selectedOrder.getCatalogs().get(i).getProducts().get(j).getId(),
                                    Integer.parseInt(selectedOrder.getCatalogs().get(i).getProducts().get(j).getQuantity())));
                }
            }
            orderInvoiceCreate.setInvoiceitem_set(invoiceItemSets);
            orderInvoiceCreate.setOrder(selectedOrder.getId());
            Gson gson1 = new Gson();
            String orderjson = new Gson().toJson(orderInvoiceCreate);
            Log.v("Invoice", orderjson);
            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "create_invoice", ""), (gson1.fromJson(gson1.toJson(orderInvoiceCreate), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Invoice invoice = Application_Singleton.gson.fromJson(response, Invoice.class);
                    Application_Singleton.purchaseInvoice = invoice;
                    if (invoice != null) {
                        Intent intent = new Intent(getActivity(), Activity_PaymentOrder.class);
                        if (isBrokerageOrder) {
                            intent.putExtra("isBrokerageOrder", isBrokerageOrder);
                        }
                        Fragment_CreatePurchaseOrderNew.this.startActivityForResult(intent, 5000);
                        // getActivity().finish();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Invoice Creating Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void configPurchaseOrder(boolean isBrokerageOrder) {
        final Response_buyingorder selectedOrder1 = (Response_buyingorder) Application_Singleton.selectedOrder;
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        if (isBrokerageOrder) {
            url = "purchase_brokerage_orders_catalogwise";
        } else {
            url = "purchaseorders_catalogwise";
        }
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, selectedOrder1.getId()), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                //Log.v("cached response", response);
                //onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                Application_Singleton.selectedOrder = selectedOrder;
                createInvoice(selectedOrder);

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
            }
        }
        if (requestCode == Application_Singleton.ADD_CATALOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringArrayListExtra("selectedCatalog") != null) {
                Log.i("TAG", "onActivityResult: String List Not Null");
                ArrayList<Response_catalogMini> arrayList = (ArrayList<Response_catalogMini>) data.getSerializableExtra("selectedCatalog");
                for (int i = 0; i < arrayList.size(); i++) {
                    Log.i("TAG", "onActivityResult: Add Count" + i);
                    moreCatalogCount = moreCatalogCount + 1;
                    ArrayList<Response_catalogMini> tempList = new ArrayList<>();
                    tempList.add(arrayList.get(i));
                    CustomViewModel customViewModel = new CustomViewModel(moreCatalogCount, tempList);
                    viewModels.add(customViewModel);
                    if (moreCatalogViewAdapter == null) {
                        moreCatalogViewAdapter = new MoreCatalogViewAdapter(getActivity(), viewModels, Fragment_CreatePurchaseOrderNew.this, new MoreCatalogViewAdapter.ViewChangeListener() {
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
                        });
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

    public void setEnableFalse() {
        editOrderNumber.setEnabled(false);
        if (isBrokerageOrder) {
            buyer_container.setClickable(false);
            edit_buyername.setEnabled(false);
        }
    }

    private void showGSTPopup() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("To create GST compliant invoice, Please add your GST number")
                .content("Enter GST no.").inputRangeRes(15, 15, R.color.color_primary)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        HashMap<String, String> params = new HashMap<>();
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        params.put("gstin", input.toString());
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""), params, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {

                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);

                                // transfer_button.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "GST number added successfully", Toast.LENGTH_SHORT).show();
                                Activity_Home.pref.edit().putBoolean("kyc_gstin_popup", true).apply();
                                if (resPostKycGst.getGstin() != null) {
                                    Activity_Home.pref.edit().putString("kyc_gstin", resPostKycGst.getGstin()).apply();
                                    Activity_Home.pref.edit().putString("entered_gst", resPostKycGst.getGstin()).apply();
                                }

                                createOrder();

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

                                Log.d("ERRROR", error.toString());
                            }
                        });
                    }
                }).positiveText("Done")
                .negativeText("Add later")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Activity_Home.pref.edit().putBoolean("kyc_gstin_popup", true).apply();
                        dialog.dismiss();
                        createOrder();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
    }

    private void validateOrderNumber(EditText s) {
        if (!TextUtils.isEmpty(s.getText())) {
            inputOrderNumber.setError(null);
        } else {
            inputOrderNumber.setError(getString(R.string.empty_field));
        }
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
