package com.wishbook.catalog.home.inventory.inwardstock;

/**
 * Created by root on 19/11/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Catalogs;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Products;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brands;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_warehouse;
import com.wishbook.catalog.commonmodels.postpatchmodels.InventoryAddStock;
import com.wishbook.catalog.commonmodels.postpatchmodels.InventoryManagmentPost;
import com.wishbook.catalog.commonmodels.postpatchmodels.OpeningAddStock;
import com.wishbook.catalog.commonmodels.postpatchmodels.OpeningStockManagmentPost;
import com.wishbook.catalog.commonmodels.responses.Response_Inventory;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.Response_warehouse;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.inventory.BrandsAdapter;
import com.wishbook.catalog.home.inventory.BrandsModel;
import com.wishbook.catalog.home.inventory.CatalogsModel;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 18/11/16.
 */
public class Fragment_Inward_Stock extends GATrackedFragment {

    public static String INVENTORY_TYPE;
    public static String LOCAL_INVENTORY_JSON;
    public static String INVENTORY_NAME;
    public static String POST_URL;

    @BindView(R.id.spinner_warehouse)
    Spinner spinner_warehouse;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.brand)
    Spinner brand;
    @BindView(R.id.catalog)
    Spinner catalog;
    @BindView(R.id.product)
    Spinner product;
    @BindView(R.id.quantity)
    TextView quantity;
    private ArrayList<Response_warehouse> response_warehouses = new ArrayList<>();
    private ArrayList<Response_Brands> brands = new ArrayList<>();
    private ArrayList<Response_Brands> brandsnew = new ArrayList<>();
    private ArrayList<Response_Brands> brandsnshow = new ArrayList<>();
    private ArrayList<Response_catalogMini> catalogs = new ArrayList<>();
    private ArrayList<Response_catalogMini> catalogsnew = new ArrayList<>();
    private ArrayList<ProductObj> products = new ArrayList<>();


    private ArrayList<BrandsModel> brandsModels = new ArrayList<>();
    private ArrayList<BrandsModel> brandsModelsAllInventory = new ArrayList<>();
    private ArrayList<CatalogsModel> catalogsModels = new ArrayList<>();

    @BindView(R.id.date_container)
    LinearLayout date_container;

    public static LinearLayout bottom_container;
    public static LinearLayout middle_container;
    public static AppCompatButton btn_save;

    @BindView(R.id.btn_scan)
    AppCompatButton btn_scan;


    @BindView(R.id.btn_add)
    AppCompatButton btn_add;

    @BindView(R.id.product_image)
    SimpleDraweeView product_image;


    @BindView(R.id.btn_addnscan)
    AppCompatButton btn_addnscan;

    @BindView(R.id.btn_cancel)
    AppCompatButton btn_cancel;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.radioGroup_catalog)
    RadioGroup radioGroup_catalog;
    @BindView(R.id.radio_Full_catalog)
    RadioButton radio_Full_catalog;
    @BindView(R.id.radio_product_catalog)
    RadioButton radio_product_catalog;
    @BindView(R.id.linear_product)
    LinearLayout linear_product;
    @BindView(R.id.txt_text_quantity)
    TextView txt_text_quantity;

    @BindView(R.id.recycler_view_all_inventory)
    RecyclerView recycler_view_all_inventory;
    ProductObj[] response_products_spinner = null;
    ProductObj[] response_product = null;
    private BrandsAdapter adapter;
    SpinAdapter_Products spinAdapter_product;
    SpinAdapter_warehouse spinAdapter_warehouse;

    private int year, month, day;
    Calendar myCalendar;
    private ArrayList<InventoryAddStock> inventoryAdds = new ArrayList<>();
    private InventoryAddStock[] inventoryAdd_array;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.inward_stock_layout, ga_container, true);
        ButterKnife.bind(this, v);
        bottom_container = (LinearLayout) v.findViewById(R.id.bottom_container);
        middle_container = (LinearLayout) v.findViewById(R.id.middle_container);
        btn_save = (AppCompatButton) v.findViewById(R.id.save);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.LayoutManager mAllLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view_all_inventory.setLayoutManager(mAllLayoutManager);

        radioGroup_catalog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.radio_Full_catalog){
                    radio_Full_catalog.setTextColor(getResources().getColor(R.color.white));
                    radio_product_catalog.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                    linear_product.setVisibility(View.GONE);
                    txt_text_quantity.setText("Set");
                } else {
                    radio_Full_catalog.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                    radio_product_catalog.setTextColor(getResources().getColor(R.color.white));
                    linear_product.setVisibility(View.VISIBLE);
                    txt_text_quantity.setText("Quantity");
                }
            }
        });
        radio_Full_catalog.setChecked(true);
        if (getArguments() != null) {
            INVENTORY_TYPE = getArguments().getString("inventory_type", "");
            LOCAL_INVENTORY_JSON = getArguments().getString("local_inventory_json", "na");
            INVENTORY_NAME = getArguments().getString("inventory_name", "");

            if (INVENTORY_NAME.equals("Opening Stock")) {
                POST_URL = URLConstants.SAVE_OPENING_STOCK;
            } else {
                POST_URL = URLConstants.SAVE_INVENTORY;
            }
        }

        getwarehouse();
        final Calendar myCalendar = Calendar.getInstance();

        if (date.getText().equals("")) {
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

        //getting all inventories from server
        getAllInventories();

        if (!Activity_Home.pref.getString(LOCAL_INVENTORY_JSON, "na").equals("na")) {
            Type type = new TypeToken<ArrayList<BrandsModel>>() {
            }.getType();
            Gson gson = new Gson();
            String json = Activity_Home.pref.getString(LOCAL_INVENTORY_JSON, "na");

            brandsModels = gson.fromJson(json, type);
            //set adapter here
            if (brandsModels.size() > 0) {
                for (int i = 0; i < brandsModels.size(); i++) {
                    brandsModels.get(i).setExpanded(true);
                }
            }

            adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
            recyclerView.setAdapter(adapter);
            bottom_container.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.VISIBLE);
        } else {
            bottom_container.setVisibility(View.GONE);
            btn_save.setVisibility(View.GONE);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .title("Remark")
                        .content("")
                        .positiveText("Save")
                        .negativeText("Save without remark")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if(spinAdapter_warehouse!=null && spinAdapter_warehouse.getCount() > 0) {
                                    postData(getActivity(), "");
                                }else {
                                    Toast.makeText(getActivity(),"There is no warehouse",Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .inputRangeRes(1, 200, R.color.material_500_red)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (!input.toString().equals("")) {
                                    if(spinAdapter_warehouse!=null && spinAdapter_warehouse.getCount() > 0) {
                                        postData(getActivity(), input.toString());
                                    } else {
                                        Toast.makeText(getActivity(),"There is no warehouse",Toast.LENGTH_SHORT).show();
                                    }

                                } else {

                                }
                            }
                        }).show();

            }
        });


        date_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.LightDialogTheme, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                middle_container.setVisibility(View.GONE);
            }
        });

        btn_addnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_Full_catalog.isChecked()){
                    if(spinAdapter_product!=null) {
                        if(spinAdapter_product.getAllItem() !=null && spinAdapter_product.getAllItem().length > 0 ){
                            ProductObj [] productObjs = spinAdapter_product.getAllItem();
                            if (!quantity.getText().toString().equals("0") && !quantity.getText().toString().equals("")) {
                                for (int i = 0;i<productObjs.length ;i++){
                                    bottom_container.setVisibility(View.VISIBLE);
                                    btn_save.setVisibility(View.VISIBLE);
                                    String title = ((ProductObj) product.getSelectedItem()).getTitle();
                                    String Id = ((ProductObj) product.getSelectedItem()).getId();
                                    int count = 0;
                                    ProductObj productObj = ((ProductObj) productObjs[i]);
                                    brandsModels = processResultForExpandableView(brandsModels, productObj, Integer.parseInt(quantity.getText().toString()));

                                    for (int j = 0; j < brandsModels.size(); j++) {
                                        brandsModels.get(j).setExpanded(true);
                                    }

                                    //set adapter here
                                    adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
                                    recyclerView.setAdapter(adapter);
                                }

                                //SCAN
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA}, 1);
                                } else {

                                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                                    startActivityForResult(i, 1);
                                }
                            } else {
                                quantity.setError("quantity cannnot be zero or empty");
                            }

                        }
                    }
                } else {
                    if (product.getSelectedItem() != null) {
                        if (!quantity.getText().toString().equals("0") && !quantity.getText().toString().equals("")) {
                            bottom_container.setVisibility(View.VISIBLE);
                            btn_save.setVisibility(View.VISIBLE);
                            String title = ((ProductObj) product.getSelectedItem()).getTitle();
                            String Id = ((ProductObj) product.getSelectedItem()).getId();
                            int count = 0;
                            ProductObj productObj = ((ProductObj) product.getSelectedItem());
                            brandsModels = processResultForExpandableView(brandsModels, productObj, Integer.parseInt(quantity.getText().toString()));

                            for (int i = 0; i < brandsModels.size(); i++) {
                                brandsModels.get(i).setExpanded(true);
                            }

                            //set adapter here
                            adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
                            recyclerView.setAdapter(adapter);
                            //SCAN
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA}, 1);
                            } else {

                                Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                                startActivityForResult(i, 1);
                            }
                            Toast.makeText(getActivity(), "Product Added Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            quantity.setError("quantity cannnot be zero or empty");
                        }
                    }else{
                        Toast.makeText(getContext(),"There is no product in selected catalog",Toast.LENGTH_LONG).show();
                    }
                }

            }

        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(radio_Full_catalog.isChecked()){
                    if(spinAdapter_product!=null) {
                        if(spinAdapter_product.getAllItem() !=null && spinAdapter_product.getAllItem().length > 0 ){
                            bottom_container.setVisibility(View.VISIBLE);
                            btn_save.setVisibility(View.VISIBLE);
                            ProductObj [] productObjs = spinAdapter_product.getAllItem();
                            if (!quantity.getText().toString().equals("0") && !quantity.getText().toString().equals("")) {
                                for (int i = 0;i<productObjs.length ;i++){
                                    ProductObj productObj = ((ProductObj) productObjs[i]);
                                    brandsModels = processResultForExpandableView(brandsModels, productObj, Integer.parseInt(quantity.getText().toString()));
                                    //set adapter here
                                    for (int j = 0; j < brandsModels.size(); j++) {
                                        brandsModels.get(j).setExpanded(true);
                                    }
                                    adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
                                    recyclerView.setAdapter(adapter);
                                }
                            } else {
                                quantity.setError("quantity cannnot be zero or empty");
                            }

                        }
                    }
                } else {
                    if (product.getSelectedItem() != null) {
                        if (!quantity.getText().toString().equals("0") && !quantity.getText().toString().equals("")) {
                            bottom_container.setVisibility(View.VISIBLE);
                            btn_save.setVisibility(View.VISIBLE);
                            ProductObj productObj = ((ProductObj) product.getSelectedItem());

                            brandsModels = processResultForExpandableView(brandsModels, productObj, Integer.parseInt(quantity.getText().toString()));
                            for (int i = 0; i < brandsModels.size(); i++) {
                                brandsModels.get(i).setExpanded(true);
                            }
                            //set adapter here
                            adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
                            recyclerView.setAdapter(adapter);

                            Toast.makeText(getActivity(), "Product Added Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            quantity.setError("quantity cannnot be zero or empty");
                        }
                    }else{
                        Toast.makeText(getContext(),"There is no product in selected catalog",Toast.LENGTH_LONG).show();
                    }
                }



            }
        });


        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {

                    Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                    startActivityForResult(i, 1);
                }
            }
        });

        getbrands();

        product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                quantity.setText("1");
                if (response_products_spinner != null) {
                    //StaticFunctions.loadImage(getActivity(), response_products_spinner[i].getImage().getThumbnail_medium(), product_image, R.drawable.uploadempty);
                    StaticFunctions.loadFresco(getActivity(), response_products_spinner[i].getImage().getThumbnail_medium(), product_image);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("onitemselected", "" + i);
                quantity.setText("1");
                if (brand.getSelectedItem() != null) {

                    getCatalogs(((Response_Brands) brand.getSelectedItem()).getId());
                } else {
                    quantity.setEnabled(false);
                    btn_add.setVisibility(View.GONE);
                    btn_addnscan.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        catalog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                quantity.setText("1");
                if (catalog.getSelectedItem() != null) {
                    getProducts(((Response_catalogMini) catalog.getSelectedItem()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return v;
    }

    private void getAllInventories() {
        //getting all inventories from server
        brandsModelsAllInventory.clear();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.INVENTORY + "?expand=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("Inventory Response", response);
                    final Response_Inventory[] response_inventories = Application_Singleton.gson.fromJson(response, Response_Inventory[].class);
                    //for each product make it to expandable product view

                    if(response_inventories.length > 0){
                        for (int i = 0; i < response_inventories.length; i++) {
                            //adding each product with expandable view i.e. Brands->Catalogs->Product (Tree Structure)
                            brandsModelsAllInventory = processResultForExpandableViewForAllInventory(brandsModelsAllInventory, response_inventories[i].getProduct(), response_inventories[i].getIn_stock());
                        }

                        //Setting all Close
                        for (int i = 0; i < brandsModelsAllInventory.size(); i++) {
                            brandsModelsAllInventory.get(i).setExpanded(true);
                        }
                        //set adapter here
                        BrandsAdapter adapter = new BrandsAdapter(getActivity(), brandsModelsAllInventory, true, Fragment_Inward_Stock.this);
                        recycler_view_all_inventory.setAdapter(adapter);

                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public ArrayList<BrandsModel> processResultForExpandableView(ArrayList<BrandsModel> brandsModels, ProductObj productObj,int quantity) {
          inventoryAdds = new ArrayList<>();
        String title = productObj.getTitle();
        String Id = productObj.getId();
        inventoryAdds.add(new InventoryAddStock(title, Id,quantity, INVENTORY_TYPE));
        Boolean brandFound = false;
        Boolean catalogFound = false;

        if (brandsModels.size() > 0) {
            for (int i = 0; i < brandsModels.size(); i++) {
                if (productObj.getCatalog().getBrand().getId().equals(brandsModels.get(i).getId())) {
                    brandFound = true;
                    catalogsModels = brandsModels.get(i).getCatalogs();
                    if (catalogsModels.size() > 0) {
                        for (int j = 0; j < catalogsModels.size(); j++) {
                            if (productObj.getCatalog().getId().equals(catalogsModels.get(j).getId())) {
                                catalogFound = true;
                                ArrayList<InventoryAddStock> stocks = new ArrayList<InventoryAddStock>();
                                stocks.addAll(catalogsModels.get(j).getProducts());
                                ArrayList<InventoryAddStock> alreadyStock = new ArrayList<InventoryAddStock>();
                                alreadyStock.add(inventoryAdds.get(inventoryAdds.size() - 1));
                                Boolean contains = false;
                                if (stocks.size() > 0) {
                                    for (int k = 0; k < stocks.size(); k++) {
                                        if (stocks.get(k).getProduct().equals(alreadyStock.get(0).getProduct())) {
                                            contains = true;
                                            int integer1 = catalogsModels.get(j).getProducts().get(k).getQuantity();
                                            int integer2 = inventoryAdds.get(inventoryAdds.size() - 1).getQuantity();
                                            int interger3 = integer1 + integer2;
                                            catalogsModels.get(j).getProducts().get(k).setQuantity(interger3);
                                        }
                                    }
                                }
                                if (contains.equals(false)) {
                                    stocks.add(inventoryAdds.get(inventoryAdds.size() - 1));
                                    catalogsModels.get(j).setProducts(stocks);
                                }
                                brandsModels.get(i).setCatalogs(catalogsModels);
                                addToLocal(brandsModels);
                                return brandsModels;
                            }
                        }
                    }
                    if (catalogFound.equals(false)) {
                        ArrayList<InventoryAddStock> addStocks = new ArrayList<InventoryAddStock>();
                        addStocks.add(inventoryAdds.get(inventoryAdds.size() - 1));
                        catalogsModels.add(new CatalogsModel(productObj.getCatalog().getTitle(), productObj.getCatalog().getId(), addStocks));
                        brandsModels.get(i).setCatalogs(catalogsModels);
                        addToLocal(brandsModels);
                        return brandsModels;
                    }
                }
            }
        } else {

        }

        if (brandFound.equals(false)) {
            ArrayList<InventoryAddStock> addStocksnew = new ArrayList<InventoryAddStock>();
            addStocksnew.add(inventoryAdds.get(inventoryAdds.size() - 1));
            catalogsModels = new ArrayList<>();
            catalogsModels.add(new CatalogsModel(productObj.getCatalog().getTitle(), productObj.getCatalog().getId(), addStocksnew));
            brandsModels.add(new BrandsModel(productObj.getCatalog().getBrand().getName(), productObj.getCatalog().getBrand().getId(), catalogsModels));
            addToLocal(brandsModels);
            return brandsModels;
        }

        return brandsModels;
    }

    private ArrayList<BrandsModel> processResultForExpandableViewForAllInventory(ArrayList<BrandsModel> brandsModels, ProductObj productObj, int quantity) {
        //do not add it to inventory arraylist
/*        inventoryAdds = new ArrayList<>();
        String title = ((ProductObj) product.getSelectedItem()).getTitle();
        String Id = ((ProductObj) product.getSelectedItem()).getId();
        inventoryAdds.add(new InventoryAddStock(title, Id, quantity, "Add"));*/




        Boolean brandFound = false;
        Boolean catalogFound = false;



        String title = productObj.getTitle();
        String Id = productObj.getId();

        if (brandsModels.size() > 0) {
            for (int i = 0; i < brandsModels.size(); i++) {
                if (productObj.getCatalog().getBrand().getId().equals(brandsModels.get(i).getId())) {
                    brandFound = true;
                    catalogsModels = brandsModels.get(i).getCatalogs();
                    if (catalogsModels.size() > 0) {
                        for (int j = 0; j < catalogsModels.size(); j++) {
                            if (productObj.getCatalog().getId().equals(catalogsModels.get(j).getId())) {
                                catalogFound = true;
                                ArrayList<InventoryAddStock> stocks = new ArrayList<InventoryAddStock>();
                                stocks.addAll(catalogsModels.get(j).getProducts());

                                ArrayList<InventoryAddStock> alreadyStock = new ArrayList<InventoryAddStock>();
                                alreadyStock.add(new InventoryAddStock(title, Id, quantity, INVENTORY_TYPE));

                                Boolean contains = false;
                                if (stocks.size() > 0) {
                                    for (int k = 0; k < stocks.size(); k++) {
                                        if (stocks.get(k).getProduct().equals(alreadyStock.get(0).getProduct())) {
                                            contains = true;
                                            int integer1 = catalogsModels.get(j).getProducts().get(k).getQuantity();
                                            int integer2 = 0;
                                            if(inventoryAdds.size() > 0)
                                            {
                                                integer2 = inventoryAdds.get(inventoryAdds.size() - 1).getQuantity();
                                            }
                                            int interger3 = integer1 + integer2;
                                            catalogsModels.get(j).getProducts().get(k).setQuantity(interger3);
                                        }
                                    }
                                }
                                if (contains.equals(false)) {
                                    stocks.add(new InventoryAddStock(title, Id, quantity, INVENTORY_TYPE));
                                    catalogsModels.get(j).setProducts(stocks);
                                }
                                brandsModels.get(i).setCatalogs(catalogsModels);
                                //addToLocal(brandsModels);
                                return brandsModels;
                            }
                        }
                    }
                    if (catalogFound.equals(false)) {
                        ArrayList<InventoryAddStock> addStocks = new ArrayList<InventoryAddStock>();
                        addStocks.add(new InventoryAddStock(title, Id, quantity, INVENTORY_TYPE));

                        catalogsModels.add(new CatalogsModel(productObj.getCatalog().getTitle(), productObj.getCatalog().getId(), addStocks));
                        brandsModels.get(i).setCatalogs(catalogsModels);
                        //addToLocal(brandsModels);
                        return brandsModels;
                    }
                }
            }
        } else {

        }

        if (brandFound.equals(false)) {
            ArrayList<InventoryAddStock> addStocksnew = new ArrayList<InventoryAddStock>();
            addStocksnew.add(new InventoryAddStock(title, Id, quantity, INVENTORY_TYPE));
            catalogsModels = new ArrayList<>();
            catalogsModels.add(new CatalogsModel(productObj.getCatalog().getTitle(), productObj.getCatalog().getId(), addStocksnew));
            brandsModels.add(new BrandsModel(productObj.getCatalog().getBrand().getName(), productObj.getCatalog().getBrand().getId(), catalogsModels));
            //addToLocal(brandsModels);
            return brandsModels;
        }

        return brandsModels;
    }

    public static void addToLocal(ArrayList<BrandsModel> brandsModels) {
        BrandsModel[] model = brandsModels.toArray(new BrandsModel[brandsModels.size()]);
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Activity_Home.pref.edit().putString(LOCAL_INVENTORY_JSON, json).commit();
    }

    private void getProducts(String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "products", id), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response ", response);
                    response_products_spinner = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                    // if (response_products_spinner.length > 0) {
                    spinAdapter_product = new SpinAdapter_Products(getActivity(), R.layout.spinneritem, response_products_spinner);
                    product.setAdapter(spinAdapter_product);
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();

            }
        });
    }


    private void getCatalogs(String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?brand=" + id + "", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    catalogs = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalog));
                    catalogsnew = new ArrayList<Response_catalogMini>();
                    for (int i = 0; i < catalogs.size(); i++) {
                        if (Integer.parseInt(catalogs.get(i).getTotal_products()) > 0) {
                            catalogsnew.add(catalogs.get(i));
                        }

                    }
                    Response_catalogMini[] catalogs = catalogsnew.toArray(new Response_catalogMini[catalogsnew.size()]);
                    SpinAdapter_Catalogs spinAdapter_catalogs = new SpinAdapter_Catalogs(getActivity(), R.layout.spinneritem, catalogs);
                    catalog.setAdapter(spinAdapter_catalogs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

    }

    private void getbrands() {
        quantity.setEnabled(true);
        btn_add.setVisibility(View.VISIBLE);
        btn_addnscan.setVisibility(View.VISIBLE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "?device=app/", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    if (response_brands.length > 0) {
                        if (response_brands[0].getId() != null) {
                            brands = new ArrayList<Response_Brands>(Arrays.asList(response_brands));
                            brandsnew.clear();
                            for (int i = 0; i < brands.size(); i++) {
                                if (brands.get(i).getTotal_catalogs() > 0) {
                                    brandsnew.add(brands.get(i));
                                }

                            }
                            Response_Brands[] brandarray = brandsnew.toArray(new Response_Brands[brandsnew.size()]);
                            SpinAdapter_brands spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, brandarray);
                            brand.setAdapter(spinAdapter_brands);

                        }
                    } else {
                        quantity.setEnabled(false);
                        btn_add.setVisibility(View.GONE);
                        btn_addnscan.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void postData(FragmentActivity activity, String remark) {

        inventoryAdds = new ArrayList<>();
        for (int i = 0; i < brandsModels.size(); i++) {
            for (int j = 0; j < brandsModels.get(i).getCatalogs().size(); j++) {
                for (int k = 0; k < brandsModels.get(i).getCatalogs().get(j).getProducts().size(); k++) {
                    inventoryAdds.add(brandsModels.get(i).getCatalogs().get(j).getProducts().get(k));
                }
            }
        }

        for (int i = 0; i < inventoryAdds.size(); i++) {
            if (inventoryAdds.get(i).getQuantity()==0) {
                new MaterialDialog.Builder(getActivity())
                        .title("Error")
                        .content("cannot be 0")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return;
            }

           /* if (inventoryAdds.get(i).getQuantity().equals("")) {
                new MaterialDialog.Builder(getActivity())
                        .title("Error")
                        .content("cannot be empty")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return;
            }*/
        }

        inventoryAdd_array = inventoryAdds.toArray(new InventoryAddStock[inventoryAdds.size()]);

        if (!INVENTORY_NAME.equals("Opening Stock")) {
            InventoryManagmentPost post = new InventoryManagmentPost();
            post.setDate(date.getText().toString().trim());
            post.setWarehouse(((Response_warehouse) spinner_warehouse.getSelectedItem()).getId());
            post.setRemark(remark);
            post.setInventoryadjustmentqty_set(inventoryAdd_array);
            postToServer(post);

        } else {
            OpeningStockManagmentPost post = new OpeningStockManagmentPost();
            post.setDate(date.getText().toString().trim());
            post.setWarehouse(((Response_warehouse) spinner_warehouse.getSelectedItem()).getId());
            post.setRemark(remark);
            ArrayList<OpeningAddStock> addArray = new ArrayList<>();
            for (InventoryAddStock addArr : inventoryAdd_array) {
                addArray.add(new OpeningAddStock(addArr.getProduct(), addArr.getProduct_title(), addArr.getQuantity()));
            }
            OpeningAddStock[] array = addArray.toArray(new OpeningAddStock[addArray.size()]);
            post.setOpeningstockqty_set(array);
            postToServerOpeningStock(post);

        }


    }

    private void postToServerOpeningStock(OpeningStockManagmentPost post) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, POST_URL, new Gson().fromJson(new Gson().toJson(post), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    //Activity_Home.pref.edit().putString("brandWiseData", "na").commit();
                    Activity_Home.pref.edit().putString(LOCAL_INVENTORY_JSON, "na").commit();
                    inventoryAdds = new ArrayList<>();
                    bottom_container.setVisibility(View.GONE);
                    btn_save.setVisibility(View.GONE);

                    new MaterialDialog.Builder(getActivity())
                            .title(INVENTORY_NAME)
                            .content("Added successfully")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    getActivity().finish();
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

    private void postToServer(InventoryManagmentPost post) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, POST_URL, new Gson().fromJson(new Gson().toJson(post), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    //Activity_Home.pref.edit().putString("brandWiseData", "na").commit();
                    Activity_Home.pref.edit().putString(LOCAL_INVENTORY_JSON, "na").commit();
                    inventoryAdds = new ArrayList<>();
                    bottom_container.setVisibility(View.GONE);
                    btn_save.setVisibility(View.GONE);

                    new MaterialDialog.Builder(getActivity())
                            .title(INVENTORY_NAME)
                            .content("Added successfully")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();

                                }
                            })
                            .show();
                    getActivity().finish();
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


    private void updateLabel(Calendar myCalendar) {
        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }

    private void getwarehouse() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "warehouse", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    Log.v("sync response", response);
                    Response_warehouse[] response_warehous = Application_Singleton.gson.fromJson(response, Response_warehouse[].class);
                    response_warehouses = new ArrayList<Response_warehouse>(Arrays.asList(response_warehous));
                    spinAdapter_warehouse = new SpinAdapter_warehouse(getActivity(), R.layout.spinneritem, response_warehouses);
                    spinner_warehouse.setAdapter(spinAdapter_warehouse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Log.v("sync response", error.getErrormessage());
            }
        });
    }

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {


               /* btn_save.setVisibility(View.VISIBLE);
                barcode.setText(data.getStringExtra("content").toString());*/

                if (spinner_warehouse.getSelectedItem() != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("expand", "true");
                    params.put("barcode", data.getStringExtra("content").toString());
                    params.put("warehouse", ((Response_warehouse) spinner_warehouse.getSelectedItem()).getId());
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    showProgress();
                    HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "productsonlywithoutcatalog", ""), params, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            hideProgress();
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                hideProgress();
                                response_product = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                                if (response_product.length > 0) {

                                    //StaticFunctions.loadImage(getActivity(), response_product[0].getImage().getThumbnail_medium(), product_image, R.drawable.uploadempty);
                                    StaticFunctions.loadFresco(getActivity(), response_product[0].getImage().getThumbnail_medium(), product_image);
                                    middle_container.setVisibility(View.VISIBLE);
                                    btn_save.setVisibility(View.VISIBLE);
                                    int brand_position = 0;
                                    int catalog_position = 0;
                                    int product_position = 0;
                                    for (int i = 0; i < brandsnew.size(); i++) {
                                        if (brandsnew.get(i).getName().equals(response_product[0].getCatalog().getBrand().getName())) {
                                            brand_position = i;
                                        }
                                    }
                                    for (int i = 0; i < catalogsnew.size(); i++) {
                                        if (catalogsnew.get(i).getTitle().equals(response_product[0].getCatalog().getTitle())) {
                                            catalog_position = i;
                                        }
                                    }
                                    for (int i = 0; i < products.size(); i++) {
                                        if (products.get(i).getTitle().equals(response_product[0].getTitle())) {
                                            product_position = i;
                                        }
                                    }

                                    brand.setSelection(brand_position);
                                    catalog.setSelection(catalog_position);
                                    product.setSelection(product_position);


                                    //ADDED BRANDWISE HERE


                                /*brand.setText(response_product[0].getCatalog().get(0).getBrand().getName());
                                catalog.setText(response_product[0].getCatalog().get(0).getTitle());
                                product.setText(response_product[0].getTitle());*/
                                } else {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Inventory")
                                            .content("Details not found")
                                            .positiveText("OK")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            hideProgress();
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });
                }
            }
        }


    }

    public ArrayList<BrandsModel> getBrandsModels() {
        return brandsModels;
    }

    public void setBrandsModels(ArrayList<BrandsModel> brandsModels) {
        this.brandsModels = brandsModels;
    }

    public void notifyBrandsChange() {
        for (int i = 0; i < brandsModels.size(); i++) {
            brandsModels.get(i).setExpanded(true);
        }
        adapter = new BrandsAdapter(getActivity(), brandsModels, false, Fragment_Inward_Stock.this);
        recyclerView.setAdapter(adapter);
        bottom_container.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
    }

}
