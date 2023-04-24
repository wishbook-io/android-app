package com.wishbook.catalog.home.orders.add;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.PurchaseBackOrder;
import com.wishbook.catalog.commonmodels.responses.ProductsSelection;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_SelectionBrandwise;
import com.wishbook.catalog.home.adapters.SpinAdapter_ProdSel;
import com.wishbook.catalog.home.inventory.BrandsModel;
import com.wishbook.catalog.home.inventory.CatalogsModel;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.orders.adapters.BrandAdapterMySelection;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by root on 30/1/17.
 */
public class Fragment_PurchaseBackOrder extends GATrackedFragment {


    private View v;
    private EditText ordernumber;
    private TextView productssize;
    private RecyclerView recyclerview;
    private TextView totalproducts;
    private TextView totalprice;
    private ReceivedProductsAdapter receivedProductsAdapter;
    private TextView quatity;
    private Button screenshot;
    private Button cancel;
    //private String catID = "0";
    private Spinner spinner_sel;
    private Button orderBut;
    private String orderValue;
    private String isBackOrder;
    private LinearLayout full_catalog_quantity;
    private LinearLayout spinner_sel_lay;
    private Response_Selection[] response_selections = new Response_Selection[]{};
    public static SelectionPurchaseOrder order = new SelectionPurchaseOrder();
    private ProductObj[] productObjs = null;
    Response_SelectionBrandwise[] selectionBrandwise;
    private ArrayList<CatalogsModel> catalogs = new ArrayList<>();

    public static BrandAdapterMySelection adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.purchase_backorder, ga_container, true);
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


        //TODO remove duplicate code from quantity change on BackOrderSelection

        quatity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (!quatity.getText().toString().equals("0") && !quatity.getText().toString().equals("")) {

                    //TODO this is Duplicate Code

                    ArrayList<BrandsModel> brands = new ArrayList<>();
                    if (selectionBrandwise != null) {
                        if (selectionBrandwise.length > 0) {
                            ArrayList<ProductsSelection> products = new ArrayList<>();
                            for (int i = 0; i < selectionBrandwise.length; i++) {
                                Boolean addCatalog = true;
                                ArrayList<CatalogsModel> catalogs = new ArrayList<>();
                                for (int j = 0; j < selectionBrandwise[i].getCatalogs().length; j++) {
                                    addCatalog = true;
                                    CatalogsModel model = new CatalogsModel();
                                    model.setTitle(selectionBrandwise[i].getCatalogs()[j].getCatalog_name());
                                    model.setId(selectionBrandwise[i].getCatalogs()[j].getId());
                                    model.setExpanded(false);
                                    model.setProductsSelections(selectionBrandwise[i].getCatalogs()[j].getProducts());
                                    //Add Products into Order
                                    for (int k = 0; k < selectionBrandwise[i].getCatalogs()[j].getProducts().length; k++) {
                                        if (selectionBrandwise[i].getCatalogs()[j].getProducts()[k].getSelling_company().length < 1) {
                                            addCatalog = false;
                                        } else {
                                            selectionBrandwise[i].getCatalogs()[j].getProducts()[k].setQuantity(s.toString()
                                            );
                                            products.add(selectionBrandwise[i].getCatalogs()[j].getProducts()[k]);
                                        }

                                    }

                                    if (addCatalog == true) {
                                        catalogs.add(model);
                                    }

                                }
                                if (addCatalog == true) {
                                    brands.add(new BrandsModel(selectionBrandwise[i].getBrand_name(), selectionBrandwise[i].getId(), catalogs, false));

                                    brands.get(i).setExpanded(false);
                                }
                            }
                            order = new SelectionPurchaseOrder();
                            order.setProducts(products);
                            adapter = new BrandAdapterMySelection(getActivity(), brands, new BrandAdapterMySelection.ProductChangeListener() {
                                @Override
                                public void OnChange() {
                                    totalproducts.setText("Total Quantity : " + String.valueOf(getCurrentTotalQuantity()));
                                    totalprice.setText("Total Price : " + String.valueOf(getCurrentTotal()));
                                }
                            });
                            totalproducts.setText("Total Quantity : " + String.valueOf(getCurrentTotalQuantity()));
                            totalprice.setText("Total Price : " + String.valueOf(getCurrentTotal()));
                            recyclerview.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (orderValue != null) {
            getMySelections();
        }

        if (isBackOrder != null && !isBackOrder.equals("")) {
            getProductsBrandwise("", isBackOrder);
            full_catalog_quantity.setVisibility(View.GONE);
            spinner_sel_lay.setVisibility(View.GONE);
        }


        return v;
    }


    private void getMySelections() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + "?type=my", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String result, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", result);
                response_selections = Application_Singleton.gson.fromJson(result, Response_Selection[].class);

                SpinAdapter_ProdSel spinAdapter_prodSel = new SpinAdapter_ProdSel(getActivity(), R.layout.spinneritem, response_selections);


                spinner_sel.setAdapter(spinAdapter_prodSel);

                if (response_selections.length > 0) {

                    spinner_sel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            quatity.setText("1");
                            getProductsBrandwise(response_selections[position].getId(), "");

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                if (orderValue != null) {
                    SpinAdapter_ProdSel selections = (SpinAdapter_ProdSel) spinner_sel.getAdapter();
                    for (int i = 0; i < selections.getCount(); i++) {
                        if (selections.getItem(i).getId().equals(orderValue)) {
                            spinner_sel.setSelection(i);
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


    private void order() {
        if (ordernumber.getText().toString().equals("")) {
            ordernumber.setError("cannot be empty !");
            return;
        }
        if (quatity.getText().toString().equals("0")) {
            quatity.setError("cannot be 0 !");
            return;
        }
        if (order != null && order.getProducts() != null && order.getProducts().size() > 0) {
            Response_Selection selection = ((Response_Selection) (spinner_sel.getSelectedItem()));
            String selections = selection.getId();
            String company = UserInfo.getInstance(getActivity()).getCompany_id();
            PurchaseBackOrder create = new PurchaseBackOrder();
            if (!isBackOrder.equals("")) {
                ArrayList<String> backorders = new ArrayList<String>(Arrays.asList(isBackOrder.split(",")));
                create.setBackorders(backorders);
            }


            create.setCompany(company);
            create.setSelections(selections);
            create.setOrder_number(ordernumber.getText().toString());

            ArrayList<ProductItems> items = new ArrayList<>();
            for (int i = 0; i < Fragment_PurchaseBackOrder.order.getProducts().size(); i++) {
                items.add(new ProductItems(order.getProducts().get(i).getId(), order.getProducts().get(i).getPrice(), order.getProducts().get(i).getQuantity(), order.getProducts().get(i).getSelling_company_id()));
            }
            create.setItems(items);


            String orderjson = new Gson().toJson(create);
            Log.v("orderdetails", orderjson);
            JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);

            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "multiorder", ""), jsonObject, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    // onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);
                    Toast.makeText(getActivity(), "Purchase order placed successfully", Toast.LENGTH_SHORT).show();
                    try {
                        if (!isBackOrder.equals("")) {
                            HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "purchaseorder", ""), null);
                        }
                    } catch (Exception e) {

                    } finally {
                        getActivity().finish();
                    }


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
        spinner_sel = ((Spinner) v.findViewById(R.id.spinner_sel));
        orderBut = ((Button) v.findViewById(R.id.orderBut));
        Bundle filter = getArguments();
        if (filter != null) {
            orderValue = filter.getString("ordervalue", "");
            isBackOrder = filter.getString("fromSales", "");
        }

        ordernumber = ((EditText) v.findViewById(R.id.input_ordernum));
        screenshot = ((Button) v.findViewById(R.id.screenshot));
        full_catalog_quantity = ((LinearLayout) v.findViewById(R.id.full_catalog_quantity));
        spinner_sel_lay = ((LinearLayout) v.findViewById(R.id.spinner_sel_lay));
        cancel = ((Button) v.findViewById(R.id.cancel));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        quatity = ((TextView) v.findViewById(R.id.quatity));
        quatity.setText("1");
        totalproducts = ((TextView) v.findViewById(R.id.totalproducts));
        totalprice = ((TextView) v.findViewById(R.id.totalprice));
        productssize = ((TextView) v.findViewById(R.id.productssize));
        recyclerview = ((RecyclerView) v.findViewById(R.id.recyclerview));
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setNestedScrollingEnabled(false);

    }


    private void getProductsBrandwise(final String id, String ids) {
        {
            String url;
            if (!ids.equals("")) {
                url = URLConstants.companyUrl(getActivity(), "multiorder", "") + "?salesorder=" + ids;
            } else {
                url = URLConstants.companyUrl(getActivity(), "multiorder", "") + "?selection=" + id;
            }
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

                    selectionBrandwise = Application_Singleton.gson.fromJson(response, Response_SelectionBrandwise[].class);
                    ArrayList<BrandsModel> brands = new ArrayList<>();
                    if (selectionBrandwise.length > 0) {
                        ArrayList<ProductsSelection> products = new ArrayList<>();
                        for (int i = 0; i < selectionBrandwise.length; i++) {
                            Boolean addCatalog = true;
                            ArrayList<CatalogsModel> catalogs = new ArrayList<>();
                            for (int j = 0; j < selectionBrandwise[i].getCatalogs().length; j++) {
                                addCatalog = true;
                                CatalogsModel model = new CatalogsModel();
                                model.setTitle(selectionBrandwise[i].getCatalogs()[j].getCatalog_name());
                                model.setId(selectionBrandwise[i].getCatalogs()[j].getId());
                                model.setExpanded(false);
                                model.setProductsSelections(selectionBrandwise[i].getCatalogs()[j].getProducts());
                                //Add Products into Order
                                for (int k = 0; k < selectionBrandwise[i].getCatalogs()[j].getProducts().length; k++) {
                                    if (selectionBrandwise[i].getCatalogs()[j].getProducts()[k].getSelling_company().length < 1) {
                                        addCatalog = false;
                                    } else {
                                        selectionBrandwise[i].getCatalogs()[j].getProducts()[k].setQuantity("1");

                                        selectionBrandwise[i].getCatalogs()[j].getProducts()[k].setSelling_company_id(selectionBrandwise[i].getCatalogs()[j].getProducts()[k].getSelling_company()[0].getId());
                                        products.add(selectionBrandwise[i].getCatalogs()[j].getProducts()[k]);
                                    }

                                }

                                if (addCatalog == true) {
                                    catalogs.add(model);
                                }

                            }
                            if (addCatalog == true) {
                                brands.add(new BrandsModel(selectionBrandwise[i].getBrand_name(), selectionBrandwise[i].getId(), catalogs, false));

                                brands.get(i).setExpanded(false);
                            }
                        }
                        order = new SelectionPurchaseOrder();
                        order.setProducts(products);
                        adapter = new BrandAdapterMySelection(getActivity(), brands, new BrandAdapterMySelection.ProductChangeListener() {
                            @Override
                            public void OnChange() {
                                totalproducts.setText("Total Quantity : " + String.valueOf(getCurrentTotalQuantity()));
                                totalprice.setText("Total Price : " + String.valueOf(getCurrentTotal()));
                            }
                        });
                        totalproducts.setText("Total Quantity : " + String.valueOf(getCurrentTotalQuantity()));
                        totalprice.setText("Total Price : " + String.valueOf(getCurrentTotal()));
                        recyclerview.setAdapter(adapter);
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    new MaterialDialog.Builder(getActivity())
                            .title("Error")
                            .content(error.getErrormessage())
                            .positiveText("Ok")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            })
                            .show();
                }


            });

        }


    }


    public int getCurrentTotalQuantity() {
        int total = 0;
        try {

            for (ProductsSelection productobj : order.getProducts()) {
                total = total + Integer.parseInt(productobj.getQuantity());
            }


        } catch (Exception e) {
            Log.d("EXCETION", e.toString());
        }
        return total;
    }

    public float getCurrentTotal() {
        float total = 0;
        try {
            for (ProductsSelection productobj : order.getProducts()) {
                total = total + (Float.parseFloat(productobj.getPrice()) * Float.parseFloat(productobj.getQuantity()));
            }

        } catch (Exception e) {

        }
        return total;
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

