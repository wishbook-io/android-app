package com.wishbook.catalog.home.orderNew.add;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.details.Activity_ComapnyCatalogs;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.adapters.MoreCatalogViewAdapterVersion2;
import com.wishbook.catalog.home.orderNew.adapters.SubProductAdapter2;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orderNew.details.Activity_PaymentOrder;
import com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_CreatePurchaseOrderVersion2 extends GATrackedFragment implements MoreCatalogViewAdapterVersion2.ViewChangeListener {


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

    @BindView(R.id.more_recyclerview)
    RecyclerView more_recyclerview;

    @BindView(R.id.btn_add_more_catalog)
    AppCompatButton btnAddMoreCatalog;


    @BindView(R.id.edit_order_requirement)
    EditText edit_order_requirement;


    @BindView(R.id.relative_discount_toggle)
    RelativeLayout relative_discount_toggle;

    @BindView(R.id.arrow_img)
    ImageView arrow_img;

    @BindView(R.id.txt_number_discount)
    TextView txt_number_discount;

    @BindView(R.id.linear_discount_frame)
    LinearLayout linear_discount_frame;


    public String sellerID = "";
    Boolean is_supplier_approved = true;
    Boolean from_public = false;
    boolean isFullCatalog = false;
    private String orderType;
    private String orderValue;
    ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem> list;
    ArrayList<CustomViewModel> viewModels;
    boolean isEditOrder = false;
    private boolean isTrustedSeller = false;
    private boolean isBrokerageOrder = false;
    private BuyersList buyer = null;
    private String brokerage_fees;

    private ArrayList<Response_catalogMini> catalog_arrays;
    private MoreCatalogViewAdapterVersion2 moreCatalogViewAdapterVersion2;
    boolean isDiscountExpand;

    Response_buyingorder cartOrder;

    Handler handler;
    Runnable runnable;

    public Fragment_CreatePurchaseOrderVersion2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragemnt_new_purchase_order_version2, ga_container, true);
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


        if (getArguments() != null) {
            if (getArguments().getBoolean("is_public", false)) {
                from_public = true;
                is_supplier_approved = getArguments().getBoolean("is_supplier_approved", true);
                if (getArguments().getString("full_catalog_order") != null) {
                    isFullCatalog = (getArguments().getString("full_catalog_order").equals("true")) ? true : false;
                }
                if (Application_Singleton.selectedshareCatalog != null) {
                    isTrustedSeller = Application_Singleton.selectedshareCatalog.getIs_trusted_seller();
                } else {
                    isTrustedSeller = false;
                }

                getCatalogMinifiedDetail(getArguments().getString("ordervalue"));
                //  getProductsByCatalog(getArguments().getString("ordervalue"), getArguments().getString("full_catalog_order"), false, 0, 1,editCatalogQuantity,inputCatalogQuantity);
                if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getSupplier() != null) {
                    getSellerDiscount(Application_Singleton.selectedshareCatalog.getSupplier());
                }

                getWishBookDiscount();
                return view;
            } else {
                try{
                    from_public = false;
                    if(Application_Singleton.selectedshareCatalog!=null){
                        is_supplier_approved = Application_Singleton.selectedshareCatalog.getIs_supplier_approved();
                    }
                    if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only() != null) {
                        isFullCatalog = (Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only().equals("true")) ? true : false;

                    }
                    getCatalogMinifiedDetail(getArguments().getString("ordervalue"));
                    if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getSupplier() != null) {
                        getSellerDiscount(Application_Singleton.selectedshareCatalog.getSupplier());
                    }
                    getWishBookDiscount();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return view;
    }

    public void initView() {
        linearSupplierName.setVisibility(View.VISIBLE);
        linearBuyerName.setVisibility(View.GONE);
        btnAddMoreCatalog.setText("+ Add more catalog of this supplier");
        inputOrderNumber.setVisibility(View.GONE);
        editOrderNumber.setVisibility(View.GONE);
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

            if (Application_Singleton.selectedshareCatalog.getEavdata() != null) {
                Log.i("TAG", "initView: Get Eav Data" + Application_Singleton.selectedshareCatalog.getEavdata().toString());

            }
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        //recyclerView.setLayoutManager(mLayoutManager);
        list = new ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem>();
        viewModels = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity().getApplicationContext());
        more_recyclerview.setLayoutManager(mLayoutManager1);
        more_recyclerview.setHasFixedSize(true);
        more_recyclerview.setNestedScrollingEnabled(false);
        // recyclerView.setNestedScrollingEnabled(false);


        /*editOrderNumber.addTextChangedListener(new TextWatcher() {
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
        });*/

        relative_discount_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isDiscountExpand) {
                        isDiscountExpand = false;
                        linear_discount_frame.setVisibility(View.VISIBLE);
                        arrow_img.setRotation(180);
                        int number_discount = 0;
                        if (linear_seller_discount.getVisibility() == View.VISIBLE)
                            number_discount++;

                        if (linear_wishbook_discount.getVisibility() == View.VISIBLE)
                            number_discount++;
                        String offer = "offer";
                        if (number_discount > 1) {
                            offer = "offers";
                        }
                        txt_number_discount.setText(String.format(getResources().getString(R.string.number_discount_available), number_discount, offer));


                    } else {
                        isDiscountExpand = true;
                        linear_discount_frame.setVisibility(View.GONE);
                        arrow_img.setRotation(0);

                        int number_discount = 0;
                        if (linear_seller_discount.getVisibility() == View.VISIBLE)
                            number_discount++;

                        if (linear_wishbook_discount.getVisibility() == View.VISIBLE)
                            number_discount++;
                        String offer = "offer";
                        if (number_discount > 1) {
                            offer = "offers";
                        }
                        txt_number_discount.setText(String.format(getResources().getString(R.string.number_discount_available), number_discount, offer));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.order)
    public void order() {
        /*if (isEmptyValidation(editOrderNumber)) {
            inputOrderNumber.setError(getResources().getString(R.string.empty_field));
            editOrderNumber.requestFocus();
            return;
        }*/

       /* if (Activity_Home.pref != null) {
            String kyc_gstin = Activity_Home.pref.getString("entered_gst", null);
            if (kyc_gstin != null) {
                if (!(kyc_gstin != null && !kyc_gstin.equals("null"))) {
                    showGSTPopup();
                    return;
                }
            }

        }*/

        if (isBrokerageOrder) {
            if (buyer == null) {
                edit_buyername.requestFocus();
                Toast.makeText(getActivity(), "Please enter buyer name", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Application_Singleton.trackEvent("NewPurchaseContinue", "Click", "NewPurchaseContinue");

        createOrder();
    }

    public void createOrder() {
        if (moreCatalogViewAdapterVersion2 != null && moreCatalogViewAdapterVersion2.getAllProducts() != null && moreCatalogViewAdapterVersion2.getAllProducts().size() > 0) {
            ArrayList<ProductQunatityRate> productQunatityRates = moreCatalogViewAdapterVersion2.getAllProducts();
            // String catalog = subProductAdapter.getCatId();
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


            SalesOrderCreate order = new SalesOrderCreate(null, sellingCompany, company, "Draft", null, productQunatityRates, is_supplier_approved);
            if (!edit_order_requirement.getText().toString().isEmpty()) {
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
                if (isBrokerageOrder) {
                    HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "purchaseorder", ""), jsonObject, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            if (isAdded() && !isDetached()) {
                                Gson gson = new Gson();
                                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                                Application_Singleton.selectedOrder = selectedOrder;

                                //Toast.makeText(getActivity(), "Purchase order placed successfully", Toast.LENGTH_SHORT).show();
                                configPurchaseOrder(isBrokerageOrder);
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });
                } else {
                    if (cartOrder != null) {
                        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.PUTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", cartOrder.getId()), jsonObject, headers, false, new HttpManager.customCallBack() {
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
                                Log.i("TAG", "onResponseFailed: " + error.getErrormessage());
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
                }

            } else {

                String url = null;
                try {
                    if (isBrokerageOrder) {
                        url = URLConstants.companyUrl(getActivity(), "brokerage_order", "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + "/";
                    } else {
                        url = URLConstants.companyUrl(getActivity(), "purchaseorder", "") + ((Response_buyingorder) Application_Singleton.selectedOrder).getId() + "/";
                    }
                }catch (Exception e){e.printStackTrace();}
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, jsonObject, headers, new HttpManager.customCallBack() {
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
                        StaticFunctions.showResponseFailedDialog(error);
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
        if (catalog_arrays != null && catalog_arrays.size() > 0) {
            for (int i = 0; i < catalog_arrays.size(); i++) {
                Response_catalogMini defaultCatalog = new Response_catalogMini(catalog_arrays.get(i).getId(), catalog_arrays.get(i).getTitle());
                temp.add(defaultCatalog);
            }
            Intent companyCatalogIntent = new Intent(getActivity(), Activity_ComapnyCatalogs.class);
            companyCatalogIntent.putExtra("companyId", Application_Singleton.selectedshareCatalog.getSupplier());
            companyCatalogIntent.putExtra("previousCatalog", temp);
            companyCatalogIntent.putExtra("isPurchase", true);
            startActivityForResult(companyCatalogIntent, Application_Singleton.ADD_CATALOG_REQUEST_CODE);
        }
    }


    public void getProductsByCatalog(final String id, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = null;
        if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.getView_permission() != null && Application_Singleton.selectedshareCatalog.getView_permission().equals(CatalogHolder.MYRECEIVED)) {
            url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", id) + "&view_type=" + CatalogHolder.MYRECEIVED;
        } else {
            url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", id);
        }
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
                CustomViewModel customViewModel = new CustomViewModel();
                customViewModel.setObject(response_catalog);
                customViewModel.setField1("1");
                customViewModel.setField5(true);
                catalog_arrays.get(position).setCustomViewModel(customViewModel);
                if (moreCatalogViewAdapterVersion2 == null) {
                    moreCatalogViewAdapterVersion2 = new MoreCatalogViewAdapterVersion2(getActivity(), Fragment_CreatePurchaseOrderVersion2.this, catalog_arrays);
                    more_recyclerview.setAdapter(moreCatalogViewAdapterVersion2);
                    moreCatalogViewAdapterVersion2.setViewChangeListener(Fragment_CreatePurchaseOrderVersion2.this);
                } else {
                    moreCatalogViewAdapterVersion2.notifyDataSetChanged();
                }

                changeTotal(position);
                if (position == 0) {
                    runnable = new Runnable() {

                        @Override
                        public void run() {
                            if (!isBrokerageOrder) {
                                setOrderCart();
                            }
                        }
                    };
                    handler = new Handler();
                    handler.postDelayed(runnable, 1500);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }

        });

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
                        if (response_seller[0].getDiscount() != null && Float.parseFloat(response_seller[0].getDiscount()) > 0) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            if (response_seller[0].getDiscount() != null)
                                if (Float.parseFloat(response_seller[0].getDiscount()) > 0) {
                                    txtOnCreditSupplier.setText(String.valueOf(Float.parseFloat(response_seller[0].getDiscount())) + "% off");
                                }
                        }
                        if (response_seller[0].getCash_discount() != null && Float.parseFloat(response_seller[0].getCash_discount()) > 0) {
                            linear_seller_discount.setVisibility(View.VISIBLE);
                            if (response_seller[0].getCash_discount() != null && Float.parseFloat(response_seller[0].getCash_discount()) > 0) {
                                txtOnCashSupplier.setText(String.valueOf(Float.parseFloat(response_seller[0].getCash_discount())) + "% off");
                            }

                        }
                    } else {
                        getBuyerDiscount(supplierId);
                    }
                    isDiscountExpand = true;
                    relative_discount_toggle.performClick();
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
                isDiscountExpand = true;
                relative_discount_toggle.performClick();

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
                        Fragment_CreatePurchaseOrderVersion2.this.startActivityForResult(intent, 5000);
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
        Log.e("TAG", "configPurchaseOrder: "+isBrokerageOrder );
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
                ArrayList<Response_catalogMini> arrayList = (ArrayList<Response_catalogMini>) data.getSerializableExtra("selectedCatalog");
                if (catalog_arrays == null) {
                    catalog_arrays = new ArrayList<>();
                }

                for (int i = 0; i < arrayList.size(); i++) {
                    catalog_arrays.add(arrayList.get(i));
                    getProductsByCatalog(arrayList.get(i).getId(), (catalog_arrays.size() - 1));
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

    private void getCatalogMinifiedDetail(final String catalogID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        String url = null;
        if (Application_Singleton.selectedshareCatalog != null && Application_Singleton.selectedshareCatalog.isFromReceived()) {
            url = URLConstants.companyUrl(getActivity(), "catalogs", "") + "/" + catalogID + "/?view_type=" + CatalogHolder.MYRECEIVED;
        } else {
            url = URLConstants.companyUrl(getActivity(), "catalogs", "") + "/" + catalogID;
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                final Response_catalogMini response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini.class);
                Log.e("TAG", "onServerResponse: " + response_catalogMinis.getThumbnail().getThumbnail_small());
                if (catalog_arrays == null) {
                    catalog_arrays = new ArrayList<Response_catalogMini>();
                    catalog_arrays.add(response_catalogMinis);

                }
                getProductsByCatalog(catalogID, 0);


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

    }

    public void changeTotal(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                totalprice.setText("Total Value : \u20B9 " + moreCatalogViewAdapterVersion2.getCurrentTotal());
            }
        }, 500);

    }

    public void changePrice(final int position) {
        totalprice.setText("Total Value : \u20B9 " + moreCatalogViewAdapterVersion2.getCurrentTotal());
    }


    @Override
    public void onRemoveView(int position) {
        moreCatalogViewAdapterVersion2.notifyDataSetChanged();
        changePrice(position);
    }

    @Override
    public void onEditQuantity(int position, int quantity) {
        try {
            if (moreCatalogViewAdapterVersion2 != null) {
                if (catalog_arrays.get(position).getCustomViewModel() != null) {
                    ArrayList<CatalogListItem> list = catalog_arrays.get(position).getCustomViewModel().getObjects();
                    if (list != null && list.size() > 0) {
                        SubProductAdapter2 subProductAdapter2 = (SubProductAdapter2) catalog_arrays.get(position).getCustomViewModel().getField4();
                        if (list.get(0).getFeedItemList().get(0).getSetDesign() != 0) {
                            int setQuantity = quantity * list.get(0).getFeedItemList().get(0).getSetDesign();
                            list.get(0).setFeedItemList(subProductAdapter2.setAllProductsQty(setQuantity, 0));
                        } else {
                            list.get(0).setFeedItemList(subProductAdapter2.setAllProductsQty(quantity, 0));
                        }
                        catalog_arrays.get(position).getCustomViewModel().setObjects(list);
                        subProductAdapter2.notifyDataSetChanged();
                        changePrice(position);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEditPkgType(String pkgtype, int position) {
        if (catalog_arrays.get(position).getCustomViewModel() != null) {
            ((CatalogListItem) catalog_arrays.get(position).getCustomViewModel().getObjects().get(0)).setFeedItemList(((SubProductAdapter2) catalog_arrays.get(position).getCustomViewModel().getField4()).setAllProductsPkgType(pkgtype, 0));
        }

    }

    public void setOrderCart() {
        try {
            if (moreCatalogViewAdapterVersion2 != null && moreCatalogViewAdapterVersion2.getAllProducts() != null && moreCatalogViewAdapterVersion2.getAllProducts().size() > 0) {
                ArrayList<ProductQunatityRate> productQunatityRates = moreCatalogViewAdapterVersion2.getAllProducts();
                // String catalog = subProductAdapter.getCatId();
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
                        if (buyer != null) {
                            company = buyer.getCompany_id();
                        }
                        broker_id = UserInfo.getInstance(getActivity()).getCompany_id();


                    }
                }

                SalesOrderCreate order = new SalesOrderCreate(null, sellingCompany, company, "Cart", null, productQunatityRates, is_supplier_approved);
                if (!edit_order_requirement.getText().toString().isEmpty()) {
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


                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "purchaseorder", ""), jsonObject, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        if (isAdded() && !isDetached()) {
                            cartOrder = Application_Singleton.gson.fromJson(response, Response_buyingorder.class);
                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        Log.i("TAG", "onResponseFailed: " + error.getErrormessage());
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });

            }
        } catch (Exception e) {

        }

    }


}
