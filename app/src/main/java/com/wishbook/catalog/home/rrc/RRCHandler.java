package com.wishbook.catalog.home.rrc;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestRRC;
import com.wishbook.catalog.commonmodels.responses.RRCRequest;
import com.wishbook.catalog.commonmodels.responses.Rrc_items;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RRCHandler {

    public Activity context;


    public enum RRCREQUESTTYPE {
        REPLACEMENT, CANCELLATION, RETURN
    }

    HashMap<String, String> rrc_image_successcount;
    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogRRCImageUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;

    public RRCHandler(Activity context) {
        this.context = context;
    }


    public void callRequest(Enum request_type, String order_id, String reason, String invoice_id,
                            ArrayList<Rrc_items> rrc_items, boolean isEditmode, String rrc_editId, ArrayList<Image> rrcImageArrayList) {

        RequestRRC requestRRC = new RequestRRC();
        if (request_type == RRCREQUESTTYPE.REPLACEMENT)
            requestRRC.setRequest_type("replacement");
        else if (request_type == RRCREQUESTTYPE.CANCELLATION)
            requestRRC.setRequest_type("cancellation");
        else if (request_type == RRCREQUESTTYPE.RETURN)
            requestRRC.setRequest_type("return");

        requestRRC.setInvoice(invoice_id);
        requestRRC.setOrder(order_id);
        requestRRC.setRequest_reason_text(reason);
        requestRRC.setRrc_items(rrc_items);
/*
        {"id":265,"rrc_items":[{"id":377,"catalog_title":"Set Matching Paging","product_sku":"okok","shipping":28.6,"created":"2019-06-27T18:50:23.090556Z","modified":"2019-06-27T18:50:23.090587Z","qty":1,"rate":"1005.10","discount":"70.00","tax":"46.50","rrc":265,"order_item":20982}],"rrc_images":[],"requested_by":2536,"refund":null,"invoice_id":10547,"invoice":null,"images":["http://b2b.wishbook.io/media/__sized__/product_image/FB_IMG_1557343749996-thumbnail-215x300-90.jpg"],"created":"2019-06-27T18:50:23.072838Z","modified":"2019-06-27T18:50:23.097192Z","request_type":"replacement","request_status":"requested","request_reason_text":"Colour Mismatch","reject_reason":null,"replacement_order_id":null,"return_type":null,"total_qty":1,"total_rate":"1005.10","total_discount":"70.00","total_tax":"46.50","uniware_return_invoice_id":null,"uniware_reverse_pickup_code":null,"return_shipment_status":"requested","return_courier":null,"return_awb_no":null,"request_reason":null,"order":10288}

        */

        callReplacementRequest(requestRRC,isEditmode,rrc_editId,rrcImageArrayList);
    }


    public void callReplacementRequest(RequestRRC requestRRC, boolean isEditMode, String requestId, final ArrayList<Image> rrImageArrayList) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        String url = URLConstants.companyUrl(context, "rrc-requests", "");
        if(isEditMode) {
            method = HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(context, "rrc-requests", "")+requestId+"/";
        }
        Log.d("TAG","POST Request===>"+Application_Singleton.gson.toJson(requestRRC));
        Log.d("TAG", "callReplacementRequest: "+url);
        HttpManager.getInstance((Activity) context).requestwithObject(method, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestRRC), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    RRCRequest response_rrc = Application_Singleton.gson.fromJson(response, new TypeToken<RRCRequest>() {
                    }.getType());
                    if(rrImageArrayList!=null && rrImageArrayList.size() > 0 ) {
                        showUploadDialog();
                        for (int index = 0; index < rrImageArrayList.size(); index++) {
                            callRRCUploadImages(response_rrc.getId(),index,rrImageArrayList);
                        }
                    } else {
                        if(Application_Singleton.rrcHandlerMultipleListner!=null) {
                            for (RRCHandlerListner item:
                                    Application_Singleton.rrcHandlerMultipleListner) {
                                item.onSuccessRequest();
                            }
                        }
                    }
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


    public void callRRCCancel(RRCRequest oldData) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "rrc-requests", "") + oldData.getId() + "/";
        RequestRRC requestTemp = new RequestRRC();
        requestTemp.setRrc_items(oldData.getRrc_items());
        requestTemp.setOrder(oldData.getOrder());
        requestTemp.setInvoice(oldData.getInvoice());
        requestTemp.setRequest_status("cancel");
        requestTemp.setRequest_type(oldData.getRequest_type());
        Log.d("TAG","Cancel Request===>"+Application_Singleton.gson.toJson(requestTemp));
        HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(requestTemp), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(Application_Singleton.rrcHandlerMultipleListner!=null) {
                    for (RRCHandlerListner item:
                            Application_Singleton.rrcHandlerMultipleListner) {
                        item.onSuccessCancel();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void callRRCUploadImages(String rrcId, final int position, final ArrayList<Image> rrcImageArrayList) {
        HashMap params = new HashMap();
        params.put("rrc", rrcId);
        File outputrenamed = new File(rrcImageArrayList.get(position).getPath());
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HttpManager.getInstance(context).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(context, "rrc-requests-images", ""), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if(rrc_image_successcount == null) {
                        rrc_image_successcount = new HashMap<>();
                    }
                    rrc_image_successcount.put(String.valueOf(position),"true");
                    Log.e("TAG", "onServerResponse: Put Position"+position );
                    boolean isComplete = observalRRCImageSuccessCount(rrc_image_successcount,rrcImageArrayList);
                    if(isComplete) {
                        hideUploadDialog();
                        if(Application_Singleton.rrcHandlerMultipleListner!=null) {
                            for (RRCHandlerListner item:
                                    Application_Singleton.rrcHandlerMultipleListner) {
                                item.onSuccessRequest();
                            }
                        }
                    } else {
                        Log.e("TAG", "onServerResponse: is Complete False" );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(final ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void showUploadDialog() {
        try {
            //initializing loader
            dialogRRCImageUpload = new MaterialDialog.Builder(context).title("Uploading ..").build();
            dialogRRCImageUpload.setCancelable(false);
            dialogRRCImageUpload.show();
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
                            dialogRRCImageUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
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
        if (dialogRRCImageUpload != null) {
            handlerPostData.removeCallbacks(runnablePostData);
            dialogRRCImageUpload.dismiss();
        }

    }



    public interface RRCHandlerListner {
        void onSuccessRequest();

        void onSuccessCancel();
    }


    public void setRrcHandlerListner(RRCHandlerListner rrcHandlerListner) {
       // this.rrcHandlerListner = rrcHandlerListner;
        if(Application_Singleton.rrcHandlerMultipleListner == null) {
            Application_Singleton.rrcHandlerMultipleListner = new HashSet<>();
        }
        Application_Singleton.rrcHandlerMultipleListner.add(rrcHandlerListner);

    }

    private boolean observalRRCImageSuccessCount(HashMap<String, String> hashmap, ArrayList<Image> rrImageArrayList) {
        boolean isSuccess = false;
        if (hashmap != null && rrImageArrayList != null) {
            StaticFunctions.printHashmap(hashmap);
            for (int index = 0; index < rrImageArrayList.size(); index++) {
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
