package com.wishbook.catalog.home.sellerhub;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.SellerInvoiceResponse;
import com.wishbook.catalog.commonmodels.responses.SellerInvoice_images;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SellerInvoiceAPIHandler {

    public Activity context;

    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogImageUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;
    HashMap<String, String> seller_invoice_image_successcount;

    public static Set<SellerInvoiceAPIHandler.SellerInvoiceAPIListener> apiHandlerListener;


    public SellerInvoiceAPIHandler(Activity context) {
        this.context = context;
    }

    public void callSellerInvoiceImageUpload(SellerInvoiceResponse sellerInvoiceResponse) {
        ArrayList<SellerInvoice_images> toupload_imagesArrayList = new ArrayList<>();
        for (int i = 0; i < sellerInvoiceResponse.getImages().size(); i++) {
            if (sellerInvoiceResponse.getImages().get(i).getId() == null) {
                toupload_imagesArrayList.add(sellerInvoiceResponse.getImages().get(i));
            }
        }
        if (toupload_imagesArrayList != null & toupload_imagesArrayList.size() > 0) {
            String seller_invoice_id = sellerInvoiceResponse.getId();
            showUploadDialog();
            for (int position = 0; position < toupload_imagesArrayList.size(); position++) {
                HashMap params = new HashMap();
                params.put("seller_invoice", seller_invoice_id);
                File outputrenamed = new File(toupload_imagesArrayList.get(position).getPath());
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                int finalPosition = position;
                HttpManager.getInstance(context).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(context, "seller-invoice-images", ""), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {

                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            if (seller_invoice_image_successcount == null) {
                                seller_invoice_image_successcount = new HashMap<>();
                            }
                            seller_invoice_image_successcount.put(String.valueOf(finalPosition), "true");
                            Log.e("TAG", "onServerResponse: Put Position" + finalPosition);
                            boolean isComplete = observalInvoiceImageSuccessCount(seller_invoice_image_successcount, toupload_imagesArrayList);
                            if (isComplete) {
                                hideUploadDialog();
                                callSellerInvoiceUpdateCopy(seller_invoice_id);
                            } else {
                                Log.e("TAG", "onServerResponse: is Complete False");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseFailed(final ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                        hideUploadDialog();
                    }
                });

            }
        }
    }


    public void callSellerInvoiceUpdateCopy(String seller_invoice_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "seller-invoice", "") + seller_invoice_id + "/";
        HashMap<String, String> param = new HashMap<>();
        param.put("id", seller_invoice_id);
        param.put("update_invoice_copy", "true");
        HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(param), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (SellerInvoiceAPIHandler.apiHandlerListener != null) {
                    for (SellerInvoiceAPIHandler.SellerInvoiceAPIListener item :
                            SellerInvoiceAPIHandler.apiHandlerListener) {
                        item.onSuccessRequest();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void showUploadDialog() {
        try {
            //initializing loader
            dialogImageUpload = new MaterialDialog.Builder(context).title("Uploading ..").build();
            dialogImageUpload.setCancelable(false);
            dialogImageUpload.show();
            BeforeTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            handlerPostData = new Handler();
            runnablePostData = new Runnable() {
                public void run() {
                    if (handlerPostData != null) {
                        long AfterTime = System.currentTimeMillis();
                        long TotalRxAfterTest = TrafficStats.getTotalTxBytes();
                        double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                        if ((rxDiff != 0)) {
                            double rxBPS = (rxDiff / 1024); // total rx bytes per second.
                            dialogImageUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                            TotalTxBeforeTest = TotalRxAfterTest;
                        }

                        handlerPostData.postDelayed(this, 1000);
                    }
                }
            };
            handlerPostData.postDelayed(runnablePostData, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideUploadDialog() {
        if (dialogImageUpload != null) {
            handlerPostData.removeCallbacks(runnablePostData);
            dialogImageUpload.dismiss();
        }
    }

    public void setSellerInvoiceAPIListener(SellerInvoiceAPIHandler.SellerInvoiceAPIListener sellerInvoiceAPIListener) {
        if (SellerInvoiceAPIHandler.apiHandlerListener == null) {
            SellerInvoiceAPIHandler.apiHandlerListener = new HashSet<>();
        }
        SellerInvoiceAPIHandler.apiHandlerListener.add(sellerInvoiceAPIListener);

    }


    public interface SellerInvoiceAPIListener {
        void onSuccessRequest();

        void onSuccessCancel();

        void onError();
    }

    private boolean observalInvoiceImageSuccessCount(HashMap<String, String> hashmap, ArrayList<SellerInvoice_images> sellerInvoice_imagesArrayList) {
        boolean isSuccess = false;
        if (hashmap != null && sellerInvoice_imagesArrayList != null) {
            StaticFunctions.printHashmap(hashmap);
            for (int index = 0; index < sellerInvoice_imagesArrayList.size(); index++) {
                if (hashmap.containsKey(String.valueOf(index))) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    return isSuccess;
                }
            }
            return isSuccess;
        }
        return isSuccess;
    }
}
