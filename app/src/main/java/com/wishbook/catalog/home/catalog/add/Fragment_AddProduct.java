package com.wishbook.catalog.home.catalog.add;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AddProductAdapter;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseAddProduct;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_AddProduct extends GATrackedFragment {


    public static String catalog_price = "0";
    public static String public_price = "0";
    public static String view_permission = "private";
    public ArrayList<Image> image;
    public String fabric = "";
    public String work = "";
    int counter = 0;
    int productCount = 1;
    int productCountInitializer = 0;
    int previousLimit, newLimit;
    //Validate Customization
    //WP= Without Price
    //WS= Without SKU
    Boolean productsWS = false;
    Boolean productsWP = false;
    MaterialDialog dialogMaterial;
    int textViewDefineCount = 0;
    int recyclerViewCount = 0;
    Handler handler;
    long TotalTxBeforeTest;
    long TotalRxAfterTest;
    Runnable runnable;
    long BeforeTime;
    //  private MaterialDialog progressDialog;
    private SharedPreferences pref;
    // private Response_catalog catSelected = null;
    private CardView uploadcard;
    private RecyclerView recycler_view;
    private ArrayList<Image> items;
    private ArrayList<Image> remainItems;
    private AddProductAdapter addProductAdapter;
    private Toolbar toolbar;
    ;
    private EditText limit_product_selection;
    private ArrayList<Image> prods;
    private ArrayList<Image> prodsCLone;
    //private ArrayList<String> catalog_category=new ArrayList<>();
    private String catalog_category = "";
    private String catalog_brand = "";
    private String catalog_id = "";
    private boolean catalog_fullproduct;

    private TextView txt_single_pc_note;
    private CheckBox checkBoxProductWS;
    private String singlePCAddPer, singlePcAddPrice;
    private String catalog_type;
    private String category_id;
    boolean isSellerWithSize;
    ArrayList<String> system_sizes = new ArrayList<>();
    HashMap<Integer, Integer> product_successcount;
    int photo_counter = 0;
    private static String TAG = Fragment_AddProduct.class.getSimpleName();


    String from;

    public Fragment_AddProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);
        if (getArguments() != null) {
            //Changed By Abu
            catalog_id = getArguments().getString("catalog_id");
            /*     catalog_fullproduct=getArguments().getString("catalog_fullproduct");*/
            catalog_price = getArguments().getString("catalog_price", "0");
            public_price = getArguments().getString("public_price", "0");
            view_permission = getArguments().getString("view_permission", "private");
            productsWS = getArguments().getBoolean("productsWithoutSKU", false);
            productsWP = getArguments().getBoolean("productsWithoutPrice", false);
            productCountInitializer = getArguments().getInt("productsCount", 0);
            catalog_fullproduct = getArguments().getBoolean("catalog_fullproduct", false);
            fabric = getArguments().getString("fabric", "");
            work = getArguments().getString("work", "");
            catalog_type = getArguments().getString("catalog_type");
            category_id = getArguments().getString("category_id");

            if (getArguments().getString("from") != null) {
                from = getArguments().getString("from");
            }


        }

        // getCatalogOptionsFromServer();


        if (catalog_price.equals("")) {
            catalog_price = "";
        }

        previousLimit = 0;
        //initializing product count
        productCount = productCountInitializer + 1;


        limit_product_selection = v.findViewById(R.id.limit_product_selection);
        uploadcard = (CardView) v.findViewById(R.id.uploadcard);
        recycler_view = v.findViewById(R.id.recycler_view);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        items = new ArrayList<>();
        remainItems = new ArrayList<>();
        checkBoxProductWS = v.findViewById(R.id.productsWS);

        /**
         * Hide Single Pc. (Managed by server changes Dec-27 2018)
         */
      /*  txt_single_pc_note = v.findViewById(R.id.txt_single_pc_note);

        if (getArguments().getString("singlePcAddPer") != null) {
            singlePCAddPer = getArguments().getString("singlePcAddPer");
            txt_single_pc_note.setText(String.format(getResources().getString(R.string.add_catalog_single_pc_note_per), singlePCAddPer + "%"));
        } else if (getArguments().getString("singlePcAddPrice") != null) {
            singlePcAddPrice = getArguments().getString("singlePcAddPrice");
            txt_single_pc_note.setText(String.format(getResources().getString(R.string.add_catalog_single_pc_note_price), "\u20B9" + singlePcAddPrice));
        }*/
        addProductAdapter = new AddProductAdapter((AppCompatActivity) getActivity(),
                items, catalog_fullproduct, productsWS,
                productsWP, singlePCAddPer,
                singlePcAddPrice);
        addProductAdapter.setFragment(Fragment_AddProduct.this);
        recycler_view.setAdapter(addProductAdapter);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);

        if (category_id != null) {
            getSizeEav(category_id);
        }
        checkBoxProductWS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    productsWS = false;
                    addProductAdapter.showDesignNumber(productsWS);
                } else {
                    productsWS = true;
                    addProductAdapter.notifyDataSetChanged();
                    addProductAdapter.showDesignNumber(productsWS);
                }
            }
        });

        checkBoxProductWS.setChecked(true);
        uploadcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (limit_product_selection.getText().toString().equals("")) {
                    limit_product_selection.setError("Please enter no. of design");
                } else {
                    textViewDefineCount = Integer.parseInt(limit_product_selection.getText().toString());
                    if (addProductAdapter != null) {
                        recyclerViewCount = addProductAdapter.getItemCount();
                    }

                    if (textViewDefineCount - recyclerViewCount > 0) {
                        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, textViewDefineCount - recyclerViewCount);
                        startActivityForResult(intent, Constants.REQUEST_CODE);
                    } else {
                        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit), textViewDefineCount), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        v.findViewById(R.id.btn_continue)
                .setOnClickListener
                        (new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (limit_product_selection.getText().toString().isEmpty()) {
                                    limit_product_selection.setError("Please enter design");
                                    return;
                                }
                                textViewDefineCount = Integer.parseInt(limit_product_selection.getText().toString());
                                if (addProductAdapter != null) {
                                    recyclerViewCount = addProductAdapter.getItemCount();
                                }

                                if (textViewDefineCount - recyclerViewCount == 0) {
                                    boolean havePrice = true;
                                    boolean havePublicPrice = true;
                                    Image image;
                                    prods = new ArrayList<>();
                                    prods.addAll(addProductAdapter.getProducts());

                                    int size = prods.size();
                                    if (productsWS) {
                                        for (int i = 0; i < prods.size(); i++) {
                                            prods.get(i).setName(null);
                                        }
                                    }

                                    if (!productsWP) {
                                        if (prods.size() > 0) {
                                            for (int i = 0; i < prods.size(); i++) {
                                                if (!checkPriceValidation(prods.get(i).price)) {
                                                    havePrice = false;
                                                    if (prods.get(i).price.equals("") || prods.get(i).price.equals("0")) {
                                                        Toast.makeText(getActivity(), getResources().getString(R.string.error_null_price), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        if (catalog_type != null && catalog_type.equalsIgnoreCase(com.wishbook.catalog.Constants.PRODUCT_TYPE_NON)) {
                                                            if ((int) Double.parseDouble(prods.get(i).price) <= 70) {
                                                                Toast.makeText(getActivity(), "Price should be greater than 70", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            if ((int) Double.parseDouble(prods.get(i).price) <= 100) {
                                                                Toast.makeText(getActivity(), "Price should be greater than 100", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                    }
                                                    break;
                                                } else {
                                                    // prods.get(i).setPrice(prods.get(i).getPrice());

                                                }
                                                if (view_permission.equals("public")) {
                                                    if (!checkPriceValidation(prods.get(i).price)) {
                                                        // todo request focus when checkprice validation false
                                                        havePublicPrice = false;
                                                        if (prods.get(i).price.equals("") || prods.get(i).price.equals("0")) {
                                                            Toast.makeText(getActivity(), getResources().getString(R.string.error_null_price), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            if (catalog_type != null && catalog_type.equalsIgnoreCase(com.wishbook.catalog.Constants.PRODUCT_TYPE_NON)) {
                                                                if ((int) Double.parseDouble(prods.get(i).price) <= 70) {
                                                                    Toast.makeText(getActivity(), "Price should be greater than 70", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                if ((int) Double.parseDouble(prods.get(i).price) <= 100) {
                                                                    Toast.makeText(getActivity(), "Price should be greater than 100", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    } else {
                                                        prods.get(i).setPublic_price(prods.get(i).getPrice());
                                                    }
                                                }
                                            }
                                            if (view_permission.equals("public")) {
                                                if (havePrice == true && havePublicPrice == true) {

                                                    {
                                                        counter = 0;
                                                        uploader();
                                                    }
                                                } else {
                                                    Log.d("VALUE", public_price);


                                                }
                                            } else {
                                                if (havePrice) {

                                                    {
                                                        counter = 0;
                                                        uploader();
                                                    }
                                                } else {

                                                }

                                            }

                                        } else {
                                            Toast.makeText(getActivity(), "No products selected !", Toast.LENGTH_SHORT).show();
                                        }


                                    } else {
                                        if (prods.size() > 0) {
                                            counter = 0;
                                            uploader();
                                        } else {
                                            Toast.makeText(getActivity(), "No products to upload", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                } else {
                                    if (textViewDefineCount > recyclerViewCount) {
                                        //Remaining message
                                        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit), textViewDefineCount - recyclerViewCount), Toast.LENGTH_LONG).show();
                                    } else {
                                        // upload message
                                        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit1), textViewDefineCount), Toast.LENGTH_LONG).show();
                                    }

                                }
                            }


                        });

        sendScreeAnalytics();

        return v;
    }

    private void uploader() {
        Log.e("TAG", "uploader: Called");
        //initializing loader
        dialogMaterial = new MaterialDialog.Builder(getContext()).title("Uploading Images ..").build();
        dialogMaterial.setCancelable(false);
        dialogMaterial.show();
        product_successcount= new HashMap<>();

        BeforeTime = System.currentTimeMillis();

        TotalTxBeforeTest = TrafficStats.getTotalTxBytes();


        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                long AfterTime = System.currentTimeMillis();

                long TotalRxAfterTest = TrafficStats.getTotalTxBytes();


                double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                if ((rxDiff != 0)) {
                    double rxBPS = (rxDiff / 1024); // total rx bytes per second.

                    if (getContext() != null) {
                        Log.d("Internet speed", String.valueOf(rxBPS) + " KB/s. Total rx = ");
                    }
                    dialogMaterial.setContent("Uploading speed : " + (int) rxBPS + " KB/s");


                    TotalTxBeforeTest = TotalRxAfterTest;
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(runnable, 1000);



       /* progressDialog = new MaterialDialog.Builder(getActivity())
                .title("Uploading")
                .content(*//*"" + (counter + 1) + "/" + prodsCLone.size() +*//* " please wait..")
                .progress(true, 0).build();
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        prodsCLone = new ArrayList<>();
        prodsCLone.addAll(prods);

        //set Progress
        //int percentage = (counter * 100) / prodsCLone.size();
        //   dialogMaterial.setContent("Downloaded " + values[0]+"/"+values[1] +" images");
        // dialogMaterial.setProgress(percentage);
        dialogMaterial.setTitle("Uploading Image : " + counter + " / " + prodsCLone.size());


        addProductAdapter.removeAll();

        /*// for add product image into product-photos
        for (Image p :
                prodsCLone) {
            ArrayList<Image> temp;
            Image first_product_img = new Image();
            first_product_img.setName(p.getName());
            first_product_img.setPath(p.getPath());

            if (p.getMore_images() != null) {
                temp = p.getMore_images();
                temp.add(0, first_product_img);
                p.setMore_images(temp);
            }
        }*/


        for (int i = 0; i < prodsCLone.size(); i++) {
            final Image image = prodsCLone.get(i);

            prods.remove(image);
            File outputrenamed = new File(image.path);
            Log.v("selected image", outputrenamed.getAbsolutePath());
            if (outputrenamed.exists()) {
                try {
                    if (StaticFunctions.isOnline(getActivity())) {

                        //changed by abu
                        HashMap params = new HashMap();
                        if (productsWS) {
                            params.put("title", "");
                        } else {
                            params.put("title", image.name);
                        }

                        // params.put("brand", catalog_brand);
                        if (productsWS) {
                            params.put("sku", "");
                        } else {
                            params.put("sku", "" + image.name);
                        }
                        params.put("catalog", catalog_id);
                        if (productsWP) {
                            params.put("price", "");
                        } else {
                            params.put("price", image.price);
                        }
                        if (view_permission.equals("public")) {
                            if (productsWP) {
                                params.put("public_price", "");
                            } else {
                                params.put("public_price", image.public_price);
                            }
                        }
                        if (image.sort_order < 1) {
                            image.setSort_order(productCount);
                            params.put("sort_order", productCount);
                        } else {
                            params.put("sort_order", image.sort_order);
                        }

                        if (image.getFabric() != null) {
                            params.put("fabric", image.getFabric());
                        }
                        if (image.getWork() != null) {
                            params.put("work", image.getWork());
                        }

                        if (isSellerWithSize) {
                            if (image.getAvailable_sizes() != null && !image.getAvailable_sizes().isEmpty()) {
                                params.put("available_sizes", StaticFunctions.ArrayListToString(image.getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
                            }
                        }
                        productCount++;
                        final int finalI = i;
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "productsonly", catalog_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
                                    counter++;
                                    dialogMaterial.setTitle("Uploading Image : " + counter + " / " + prodsCLone.size());
                                    product_successcount.put(finalI, 0);
                                    limit_product_selection.setText("" + ((Integer.parseInt(limit_product_selection.getText().toString())) - 1));
                                    ResponseAddProduct responseAddProduct = new Gson().fromJson(response, ResponseAddProduct.class);
                                    try {
                                        uploadProductMorePhotos(responseAddProduct.getId(), finalI);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponseFailed(final ErrorString error) {
                                dialogMaterial.setCancelable(true);
                                dialogMaterial.dismiss();
                                new MaterialDialog.Builder(getActivity())
                                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                        .content(error.getErrormessage())
                                        .positiveText("OK")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(MaterialDialog dialog, DialogAction which) {

                                                dialog.dismiss();
                                                if (error.getErrormessage().equals("sku should be unique")) {
                                                    prods.add(image);
                                                }

                                                if (prods.size() > 0) {
                                                    addProductAdapter = new AddProductAdapter((AppCompatActivity) getActivity(), prods, catalog_fullproduct, productsWS, productsWP, singlePCAddPer, singlePcAddPrice);
                                                    addProductAdapter.setFragment(Fragment_AddProduct.this);
                                                    recycler_view.setAdapter(addProductAdapter);
                                                    //addProductAdapter.notifyDataSetChanged();
                                                }

                                            }

                                        })
                                        .show();

                            }
                        });

                    } else {
                        StaticFunctions.showNetworkAlert(getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * Send Product image with all other selected images
     *
     * @param product_id
     * @param position
     */
    private void uploadProductMorePhotos(String product_id, final int position) {
        HashMap params = new HashMap();
        photo_counter = 0;
        Log.e(TAG, "Request Post More Photos ======>" + photo_counter);
        if (prodsCLone.get(position).getMore_images() == null || prodsCLone.get(position).getMore_images().size() == 0) {
            Log.e(TAG, "Response Post More Photos NO IMAGES======>" + photo_counter);
            photo_counter++;
            product_successcount.put(position, photo_counter);
            ObservableProductUpload();
        } else {
            for (int i = 0; i < prodsCLone.get(position).getMore_images().size(); i++) {
                params.put("set_default", String.valueOf(false));
                params.put("product", product_id);
                params.put("sort_order", String.valueOf((i + 1)));
                File outputrenamed = new File(prodsCLone.get(position).getMore_images().get(i).path);
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "products_photos", product_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.e(TAG, "Response Post More Photos SUCCESS======>" + photo_counter);
                        photo_counter++;
                        product_successcount.put(position, photo_counter);
                        ObservableProductUpload();
                    }

                    @Override
                    public void onResponseFailed(final ErrorString error) {
                        if (isAdded() && !isDetached()) {
                            hideUploadDialog();
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
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            this.image = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
          /* if(Boolean.parseBoolean(catalog_fullproduct)){
               for (Image image : images) {
                   image.price=catalog_price;
               }
           }*/
            for (Image image : image) {
                if (image != null && image.getName() != null) {
                    image.setName(image.getName().replaceAll(".jpg", "").replaceAll(".png", "").replaceAll(".jpeg", ""));
                } else {
                    image.setName("");
                }
                image.setPrice(catalog_price);
                image.setPublic_price(public_price);
                if (!fabric.equals("")) {
                    image.setFabric(fabric);
                }
                if (!work.equals("")) {
                    image.setWork(work);
                }
                items.add(image);
            }
            addProductAdapter = new AddProductAdapter((AppCompatActivity) getActivity(), items, catalog_fullproduct, productsWS, productsWP, singlePCAddPer, singlePcAddPrice);
            addProductAdapter.setFragment(Fragment_AddProduct.this);
            recycler_view.setAdapter(addProductAdapter);
            recycler_view.setHasFixedSize(true);
            recycler_view.setNestedScrollingEnabled(false);
            if (addProductAdapter != null) {
                addProductAdapter.setSizes(system_sizes);
                addProductAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {

            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0 && image != null && image.size() > 0) {
                Image pre_image = image.get(AddProductAdapter.ADD_MORE_IMAGE_POSITION);
                ArrayList<Image> temp1;
                if (this.image.get(AddProductAdapter.ADD_MORE_IMAGE_POSITION).getMore_images() == null) {
                    temp1 = new ArrayList<>();
                } else {
                    temp1 = this.image.get(AddProductAdapter.ADD_MORE_IMAGE_POSITION).getMore_images();
                }
                temp1.addAll(temp);
                pre_image.setMore_images(temp1);
                this.image.set(AddProductAdapter.ADD_MORE_IMAGE_POSITION, pre_image);
                if (addProductAdapter != null) {
                    addProductAdapter.notifyItemChanged(AddProductAdapter.ADD_MORE_IMAGE_POSITION);
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean checkPriceValidation(String price) {
        if (price.toString().trim().isEmpty()) {
            return false;
        } else {
            double productPrice = Double.parseDouble(price);
            int validatePrice = 100;
            if (catalog_type != null && catalog_type.equalsIgnoreCase(com.wishbook.catalog.Constants.PRODUCT_TYPE_NON)) {
                validatePrice = 70;
            }
            if (productPrice > validatePrice)
                return true;
            else return false;
        }
    }

    private void sendScreeAnalytics() {
        HashMap<String, String> prop = new HashMap<>();
        String source = "";
        if (from != null) {
            if (from.equals(Fragment_CatalogsGallery.class.getSimpleName())) {
                source = "Product Detail";
            } else {
                source = from;
            }
        }
        prop.put("catalog_item_id", catalog_id);
        WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "Product_Add_screen", source, prop);
    }

    private void getSizeEav(String category_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getActivity());
        String url = URLConstants.companyUrl(getActivity(), "enumvalues", "") + "?category=" + category_id + "&attribute_slug=" + "size";
        final MaterialDialog progressdialog = StaticFunctions.showProgress(getActivity());
        progressdialog.show();
        HttpManager.getInstance((Activity) getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
                EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                if (enumGroupResponses != null) {
                    if (enumGroupResponses.length > 0) {
                        ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                        for (int i = 0; i < enumGroupResponses1.size(); i++) {
                            system_sizes.add(enumGroupResponses1.get(i).getValue());
                        }
                        isSellerWithSize = true;
                        if (addProductAdapter != null) {
                            addProductAdapter.setSizes(system_sizes);
                            addProductAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    isSellerWithSize = false;
                }


            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
            }
        });
    }

    public void ObservableProductUpload() {
        boolean isSuccess = false;
        if (product_successcount != null && prodsCLone != null) {
            for (int i = 0; i < prodsCLone.size(); i++) {
                if (product_successcount.containsKey(i)) {
                    int extra_image_size = 0;
                    if (prodsCLone.get(i).getMore_images() != null) {
                        extra_image_size = prodsCLone.get(i).getMore_images().size();
                    }
                    if (product_successcount.get(i).intValue() >= extra_image_size) {
                        Log.e("TAG", "ObservableProductUpload: true" + product_successcount.get(i).intValue() + "Size ===>" + extra_image_size);
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                        break;
                    }
                } else {
                    isSuccess = false;
                }
            }
            if (isSuccess) {
                dialogMaterial.dismiss();
                Toast.makeText(getActivity(), "Product Uploaded Successfully", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    public void hideUploadDialog() {
        if (dialogMaterial != null && dialogMaterial.isShowing()) {
            dialogMaterial.dismiss();
        }
    }

   /* private void sendProductAddAnalytics() {
        HashMap<String, String> prop = new HashMap<>();
        prop.put("product_id",);
        prop.put("product_name",);
        prop.put("product_type");
        prop.put("brand",);
        prop.put("price", )
        prop.put("catalog_item_id", catalog_id);
    }*/
}
