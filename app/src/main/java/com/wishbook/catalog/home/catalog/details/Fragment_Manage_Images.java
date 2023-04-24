package com.wishbook.catalog.home.catalog.details;

import android.app.Activity;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseProductUpload;
import com.wishbook.catalog.commonmodels.responses.ResponseProducts;
import com.wishbook.catalog.commonmodels.responses.ScreenSetModel;
import com.wishbook.catalog.home.catalog.adapter.AddScreenSetAdapter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Manage_Images extends GATrackedFragment {

    /**
     * Take product_id and catalog_id,set type in arguments, isEditMode
     */


    @BindView(R.id.recyclerview_screen_set)
    RecyclerView recyclerview_screen_set;

    @BindView(R.id.btn_add_set)
    AppCompatButton btn_add_set;

    @BindView(R.id.btn_continue)
    AppCompatButton btn_continue;

    // ## Variable Step 3 Initialize Start ####
    private ArrayList<ScreenSetModel> screenSetModels;
    private AddScreenSetAdapter addScreenSetAdapter;

    // ## Variable for isnewImages selected;
    private boolean isNewImageAdd;
    private boolean isNewProductAdd;


    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogCatalogUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;

    // ### Variable Argument
    String product_id;
    String catalog_id;
    boolean isColorSet;
    boolean isEdit;

    HashMap<Integer, Integer> screen_successcount;

    String from = "";

    public static String TAG = Fragment_Manage_Images.class.getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_add_screen_product, ga_container, true);
        ButterKnife.bind(this, v);
        setupToolbar();
        initView();
        return v;
    }


    protected void setupToolbar() {
        if (getActivity() instanceof OpenContainer) {
            ((OpenContainer) getActivity()).toolbar.setTitle("Add Images");
        }

    }

    private void initView() {
        if (getArguments().getString("product_id") != null) {
            product_id = getArguments().getString("product_id");
        }

        if (getArguments().getString("catalog_id") != null) {
            catalog_id = getArguments().getString("catalog_id");
        }

        if (getArguments().getString("set_type") != null) {
            if (getArguments().getString("set_type").equalsIgnoreCase("size_set")) {
                isColorSet = false;
            } else {
                isColorSet = true;
            }

        }

        if(getArguments().getString("from")!=null) {
            from = getArguments().getString("from");
        }

        if (getArguments().getBoolean("isEdit")) {
            isEdit = true;
        } else {
            isEdit = false;
        }
        screenSetModels = new ArrayList<>();
        addScreenSetAdapter = new AddScreenSetAdapter(getActivity(), screenSetModels, recyclerview_screen_set, Fragment_Manage_Images.this);
        recyclerview_screen_set.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview_screen_set.setAdapter(addScreenSetAdapter);
        recyclerview_screen_set.setNestedScrollingEnabled(false);
        if (isEdit) {
            if (product_id != null) {
                callGetAllScreenProduct(product_id);
            }
        } else {
            if (screenSetModels != null) {
                isNewProductAdd = true;
                ScreenSetModel screenSetModel = new ScreenSetModel();
                if (isColorSet) {
                    screenSetModel.setColor_set_type(true);
                } else {
                    screenSetModel.setColor_set_type(false);
                }
                screenSetModels.add(screenSetModel);
                addScreenSetAdapter.notifyItemInserted(screenSetModels.size() - 1);
            }
        }


        btn_add_set.setVisibility(View.GONE);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    if (validateScreen()) {
                        patchProduct(0);
                    }
                } else {
                    if (validateScreen()) {
                        if (isEdit) {
                            dialogCatalogUpload = new MaterialDialog.Builder(getActivity()).title("Updating Products ..").build();
                            dialogCatalogUpload.show();
                        } else {
                            dialogCatalogUpload = new MaterialDialog.Builder(getActivity()).title("Uploading Products ..").build();
                            dialogCatalogUpload.show();
                        }
                        try {
                            if (isNewProductAdd) {
                                Log.i(TAG, "onClick: =======> New Product Uploaded Start");
                                for (int i = 0; i < screenSetModels.size(); i++) {
                                    Log.i(TAG, "onClick: =======> New Product Uploaded Start==>" + i);
                                    uploadScreenProduct(i);
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });

        btn_add_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateScreen()) {
                    if (screenSetModels != null) {
                        isNewProductAdd = true;
                        ScreenSetModel screenSetModel = new ScreenSetModel();
                        if (isColorSet) {
                            screenSetModel.setColor_set_type(true);
                        } else {
                            screenSetModel.setColor_set_type(false);
                        }
                        screenSetModels.add(screenSetModel);
                        addScreenSetAdapter.notifyItemInserted(screenSetModels.size() - 1);
                    }
                }

            }
        });
    }


    private void callGetAllScreenProduct(String product_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "productsonlywithoutcatalog", "") + product_id;
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ResponseProducts responseProducts = Application_Singleton.gson.fromJson(response, new TypeToken<ResponseProducts>() {
                }.getType());
                ScreenSetModel screenSetModel = new ScreenSetModel();
                screenSetModel.setScreen_name(responseProducts.getTitle());
                screenSetModel.setColor_name(responseProducts.getSet_type_details());
                if (isColorSet) {
                    screenSetModel.setColor_set_type(true);
                } else {
                    screenSetModel.setColor_set_type(false);
                }
                if (responseProducts.getExpiry_date() != null) {
                    screenSetModel.setExpiry_date(calculateDays(responseProducts.getExpiry_date()));
                }




                if (responseProducts.getPhotos()!=null && responseProducts.getPhotos().size() > 0) {
                    ArrayList<Image> temp = new ArrayList<>();
                    for (int i = 0; i < responseProducts.getPhotos().size(); i++) {
                        Image image = new Image(responseProducts.getPhotos().get(i).getId(), responseProducts.getPhotos().get(i).getImage().getThumbnail_small());
                        temp.add(image);
                    }
                    screenSetModel.setImages(temp);
                }
                screenSetModels.add(screenSetModel);
                addScreenSetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(final ErrorString error) {
                if (isAdded() && !isDetached()) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            }
        });
    }


    private String calculateDays(String expiredate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String today_date = DateUtils.currentUTC();
        String expiry_date = expiredate;
        try {
            Date date1 = sdf.parse(today_date);
            Date date2 = sdf.parse(expiry_date);
            long diff = date2.getTime() - date1.getTime();
            System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void uploadScreenProduct(final int position) {
        HashMap params = new HashMap();
        params.put("title", screenSetModels.get(position).getScreen_name());
        params.put("sku", screenSetModels.get(position).getScreen_name());
        params.put("catalog", catalog_id);
        params.put("sort_order", String.valueOf(position + 1));
        params.put("product_type", "set");
        if (screenSetModels.get(position).getColor_name() != null && !screenSetModels.get(position).getColor_name().isEmpty())
            params.put("set_type_details", screenSetModels.get(position).getColor_name());

        int disable_days = 30;
        Date todayDate = new Date();
        Calendar objectCalendar = Calendar.getInstance();
        objectCalendar.setTime(todayDate);
        if (!screenSetModels.get(position).getExpiry_date().isEmpty()) {
            disable_days = Integer.parseInt(screenSetModels.get(position).getExpiry_date().toString().trim());
        }
        objectCalendar.add(Calendar.DATE, disable_days);
        Date expire_days = new Date(objectCalendar.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expireDateString = sdf.format(expire_days);
        params.put("expiry_date", expireDateString);


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        File outputrenamed = new File(addScreenSetAdapter.getScreenProductImage(position).get(0).path);
        HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "productsonly", catalog_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // hideUploadDialog();
                screen_successcount = new HashMap<>();
                screen_successcount.put(position, 0);
                checkISSuccess();
                try {
                    ResponseProductUpload productUpload = Application_Singleton.gson.fromJson(response, ResponseProductUpload.class);
                    uploadProductPhotos(productUpload.getId(), position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


    private void patchProduct(final int position) {

        if (isEdit) {
            dialogCatalogUpload = new MaterialDialog.Builder(getActivity()).title("Updating Products ..").build();
            dialogCatalogUpload.show();
        } else {
            dialogCatalogUpload = new MaterialDialog.Builder(getActivity()).title("Uploading Products ..").build();
            dialogCatalogUpload.show();
        }

        HashMap params = new HashMap();
        params.put("title", screenSetModels.get(position).getScreen_name());
        params.put("sku", "" + screenSetModels.get(position).getScreen_name());
        params.put("catalog", catalog_id);
        params.put("sort_order", String.valueOf((position + 1)));
        params.put("product_type", "set");
        if (screenSetModels.get(position).getColor_name() != null
                && !screenSetModels.get(position).getColor_name().isEmpty())
            params.put("set_type_details", screenSetModels.get(position).getColor_name());

        int disable_days = 30;
        Date todayDate = new Date();
        Calendar objectCalendar = Calendar.getInstance();
        objectCalendar.setTime(todayDate);
        if (!screenSetModels.get(position).getExpiry_date().isEmpty()) {
            disable_days = Integer.parseInt(screenSetModels.get(position).getExpiry_date().toString().trim());
        }
        objectCalendar.add(Calendar.DATE, disable_days);
        Date expire_days = new Date(objectCalendar.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expireDateString = sdf.format(expire_days);
        params.put("expiry_date", expireDateString);

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "productsonly", catalog_id) + product_id + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (validateScreen()) {
                    screen_successcount = new HashMap<>();
                    screen_successcount.put(position, 0);
                    if (isNewImageAdd) {
                        Log.i(TAG, "onClick: =======> New Images Uploaded");
                        uploadProductPhotos(product_id, 0);
                    } else {
                        Toast.makeText(getActivity(), "Screen Updated Successfully", Toast.LENGTH_SHORT).show();
                        if (handlerPostData != null) {
                            handlerPostData.removeCallbacks(runnablePostData);
                        }
                        hideUploadDialog();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (isAdded() && !isDetached()) {
                    hideUploadDialog();
                    StaticFunctions.showResponseFailedDialog(error);
                }
            }
        });
    }


    private void uploadProductPhotos(String product_id, final int position) {
        Log.i(TAG, "uploadProductPhotos: ====>" + position);
        final int[] counter = {0};

       /* getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogCatalogUpload == null) {
                    dialogCatalogUpload = new MaterialDialog.Builder(getActivity()).title("Uploading Images ..").build();
                }
                dialogCatalogUpload.setTitle("Uploading Images...");
                dialogCatalogUpload.setCancelable(false);
            }
        });*/

        BeforeTime = System.currentTimeMillis();
        TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
        handlerPostData = new Handler();
        runnablePostData = new Runnable() {
            public void run() {
                long AfterTime = System.currentTimeMillis();
                long TotalRxAfterTest = TrafficStats.getTotalTxBytes();
                double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                if ((rxDiff != 0)) {
                    double rxBPS = (rxDiff / 1024); // total rx bytes per second.
                    if (getContext() != null) {
                        Log.d("Internet speed", String.valueOf(rxBPS) + " KB/s. Total rx = ");
                    }
                    dialogCatalogUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                    TotalTxBeforeTest = TotalRxAfterTest;
                }
                handlerPostData.postDelayed(this, 1000);
            }
        };
        handlerPostData.postDelayed(runnablePostData, 1000);
        //dialogCatalogUpload.setTitle("Uploading Image : " + counter[0] + " / " + addScreenSetAdapter.getScreenProductImage(position).size());
        counter[0] = 0;
        HashMap params = new HashMap();
        for (int i = 0; i < addScreenSetAdapter.getScreenProductImage(position).size(); i++) {
            if (i == 0) {
                params.put("set_default", String.valueOf(true));
            } else {
                params.put("set_default", String.valueOf(false));
            }
            params.put("product", product_id);
            params.put("sort_order", String.valueOf((i + 1)));
            if (addScreenSetAdapter.getScreenProductImage(position).get(i).getPhoto_id() != null) {
                counter[0]++;
                screen_successcount.put(position, counter[0]);
                checkISSuccess();
                continue;
            }
            final File[] outputrenamed = {new File(addScreenSetAdapter.getScreenProductImage(position).get(i).path)};
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "products_photos", ""), params, headers, "image", "image/jpg", outputrenamed[0], false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    counter[0]++;
                    screen_successcount.put(position, counter[0]);
                    checkISSuccess();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.MULTIIMAGE_SCREEN_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            //KeyboardUtils.hideKeyboard(getActivity());
            clearFocus();
            isNewImageAdd = true;
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            boolean isInsertMode = false;
            if (addScreenSetAdapter != null && addScreenSetAdapter.getItemCount() == 0) {
                // Set Adapter
                isInsertMode = false;
            } else {
                // Insert Mode
                isInsertMode = true;
            }

            if (temp.size() > 0 && screenSetModels != null && screenSetModels.size() > 0) {
                ScreenSetModel set_model = screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION);
                ArrayList<Image> temp1;
                if (screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION).getImages() == null) {
                    temp1 = new ArrayList<>();
                } else {
                    temp1 = screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION).getImages();
                }
                temp1.addAll(temp);
                set_model.setImages(temp1);
                screenSetModels.set(AddScreenSetAdapter.EDIT_POSITION, set_model);
            }

            if (addScreenSetAdapter != null)
                if (addScreenSetAdapter != null) {
                    addScreenSetAdapter.notifyItemChanged(AddScreenSetAdapter.EDIT_POSITION);
                }
        }
    }


    public boolean validateScreen() {
        for (int i = 0; i < screenSetModels.size(); i++) {
            if (screenSetModels.get(i).getScreen_name() == null || screenSetModels.get(i).getScreen_name().isEmpty()) {
                Toast.makeText(getActivity(), "Please insert screen name/number", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (screenSetModels.get(i).isColor_set_type() && (screenSetModels.get(i).getColor_name() == null || screenSetModels.get(i).getColor_name().isEmpty())) {
                if (screenSetModels.get(i).isColor_set_type()) {
                    Toast.makeText(getActivity(), "Please enter color name", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            if (screenSetModels.get(i).getImages() == null || screenSetModels.get(i).getImages().size() == 0) {
                Toast.makeText(getActivity(), "Please add atleast one product", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (screenSetModels.get(i).getExpiry_date() == null || screenSetModels.get(i).getExpiry_date().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter enable duration", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                int days = Integer.parseInt(screenSetModels.get(i).getExpiry_date().trim());
                if (days < 10) {
                    Toast.makeText(getActivity(), "Minimum enable duration should be 10", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (days > 90) {
                    Toast.makeText(getActivity(), "Maximum enable duration should be 90", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }


        }
        return true;
    }

    public void hideUploadDialog() {
        if (dialogCatalogUpload != null && dialogCatalogUpload.isShowing()) {
            dialogCatalogUpload.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideUploadDialog();
        if (handlerPostData != null) {
            handlerPostData.removeCallbacks(runnablePostData);
        }
    }


    public void checkISSuccess() {
        boolean isSucess = false;
        if (screen_successcount != null && screenSetModels != null) {
            for (int i = 0; i < screenSetModels.size(); i++) {
                if (screen_successcount.containsKey(i)) {
                    if (screen_successcount.get(i).intValue() >= screenSetModels.get(i).getImages().size()) {
                        isSucess = true;
                    } else {
                        isSucess = false;
                    }
                }
            }
            if (isSucess) {
                Toast.makeText(getActivity(), "Screen Uploaded Successfully", Toast.LENGTH_SHORT).show();
                if (handlerPostData != null) {
                    handlerPostData.removeCallbacks(runnablePostData);
                }
                hideUploadDialog();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }


    public void clearFocus() {
        KeyboardUtils.hideKeyboard(getActivity());
        View currentFocus = (getActivity()).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
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
}
