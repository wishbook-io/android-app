package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestProductBatchUpdate;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartStopHandler {

    public Activity context;
    StartStopDoneListener startStopDoneListener;

    public enum STARTSTOP {
        SINGLE_WITH_SIZE, SINGLE_WITHOUT_SIZE, FULL_WITH_SIZE
    }

    public StartStopHandler(Activity context) {
        this.context = context;
    }


    private void postBulkUpdateProductStartStop(Context context, RequestProductBatchUpdate requestProductBatchUpdate, final boolean isStartSelling) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "bulk-update-product-seller", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isStartSelling) {
                    if (startStopDoneListener != null) {
                        startStopDoneListener.onSuccessStart();
                    }
                } else {
                    if (startStopDoneListener != null) {
                        startStopDoneListener.onSuccessStop();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public interface StartStopDoneListener {
        void onSuccessStart();

        void onSuccessStop();

        void onError();
    }

    public void startStopHandler(Enum type, List<ProductObj> old_product, String product_id, ArrayList<String> selected_sizes, boolean isStartSelling) {

        if (type == STARTSTOP.SINGLE_WITH_SIZE) {


            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
            List<ProductObj> productObjs = new ArrayList<>();
            // Remove Other Parameter, send  needed parameter

            if (old_product == null) {
                // Patch for Bundle Id
                ProductObj productObj = new ProductObj();
                productObj.setProduct_id(product_id);
                if (selected_sizes != null && selected_sizes.size() > 0) {
                    productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(selected_sizes, StaticFunctions.COMMASEPRATED));
                    productObj.setIs_enable(true);
                } else {
                    productObj.setIs_enable(false);
                }
                productObjs.add(productObj);

            } else {
                for (int i = 0; i < old_product.size(); i++) {
                    ProductObj productObj = new ProductObj();
                    productObj.setProduct_id(old_product.get(i).getId());
                    if (old_product.get(i).getAvailable_sizes() != null && old_product.get(i).getAvailable_sizes().size() > 0) {
                        productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(old_product.get(i).getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
                        productObj.setIs_enable(true);
                    } else {
                        productObj.setIs_enable(false);
                    }
                    productObjs.add(productObj);
                }
            }

            requestProductBatchUpdate.setProducts(productObjs);


            Log.e("TAG", "SINGLE_WITH_SIZE =======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());

            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isStartSelling);

        } else if (type == STARTSTOP.SINGLE_WITHOUT_SIZE) {

            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
            List<ProductObj> productObjs = new ArrayList<>();



            if (old_product == null) {
                Log.e("TAG", "startStopHandler: Not in update Bundle" );
                // Patch for Bundle Id (not required to check available sizes)
                ProductObj productObj = new ProductObj();
                productObj.setProduct_id(product_id);
                productObj.setIs_enable(isStartSelling);
                productObjs.add(productObj);

            } else {
                Log.e("TAG", "startStopHandler: Single update " );
                for (int i = 0; i < old_product.size(); i++) {
                    ProductObj productObj = new ProductObj();
                    productObj.setProduct_id(old_product.get(i).getId());
                    productObj.setIs_enable(old_product.get(i).isIs_enable());
                    Log.e("TAG", "startStopHandler: Single update "+old_product.get(i).isIs_enable() );
                    productObjs.add(productObj);
                }
            }


            requestProductBatchUpdate.setProducts(productObjs);
            Log.e("TAG", "SINGLE_WITHOUT_SIZE =======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());
            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isStartSelling);

        } else if (type == STARTSTOP.FULL_WITH_SIZE) {
            // Patch for Bundle Id
            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();

            // Patch for Bundle Id
            List<ProductObj> request_product_obj = new ArrayList<>();
            ProductObj productObj = new ProductObj();
            productObj.setProduct_id(product_id);
            if (selected_sizes != null && selected_sizes.size() > 0) {
                productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(selected_sizes, StaticFunctions.COMMASEPRATED));
                productObj.setIs_enable(true);
            } else {
                productObj.setIs_enable(false);
            }

            request_product_obj.add(productObj);

            requestProductBatchUpdate.setProducts(request_product_obj);
            Log.e("TAG", "FULL WITH SIZE=======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());
            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isStartSelling);
        }

    }

    public void becomeASellerHandler(Enum type, List<ProductObj> old_product, String product_id, ArrayList<String> selected_sizes, boolean isStartSelling) {

        if (type == STARTSTOP.SINGLE_WITH_SIZE) {

            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
            List<ProductObj> productObjs = new ArrayList<>();
            // Remove Other Parameter, send  needed parameter
            if (old_product == null) {
                // Patch for Bundle Id
                ProductObj productObj = new ProductObj();
                productObj.setProduct_id(product_id);
                productObj.setIs_enable(true);
                productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(selected_sizes, StaticFunctions.COMMASEPRATED));
                productObjs.add(productObj);
            } else {
                for (int i = 0; i < old_product.size(); i++) {
                    ProductObj productObj = new ProductObj();
                    productObj.setProduct_id(old_product.get(i).getId());
                    productObj.setIs_enable(old_product.get(i).isIs_enable());
                    if(old_product.get(i).isIs_enable()) {
                        productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(old_product.get(i).getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
                    }
                    productObjs.add(productObj);
                }
            }

            requestProductBatchUpdate.setProducts(productObjs);


            Log.e("TAG", "SINGLE_WITH_SIZE =======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());

            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isStartSelling);

        } else if (type == STARTSTOP.FULL_WITH_SIZE) {
            // Patch for Bundle Id
            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();

            // Patch for Bundle Id
            List<ProductObj> request_product_obj = new ArrayList<>();
            ProductObj productObj = new ProductObj();
            productObj.setProduct_id(product_id);
            productObj.setIs_enable(true);
            productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(selected_sizes, StaticFunctions.COMMASEPRATED));

            request_product_obj.add(productObj);

            requestProductBatchUpdate.setProducts(request_product_obj);
            Log.e("TAG", "FULL WITH SIZE=======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());
            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isStartSelling);
        }

    }

    public void updateProduct(Enum type, List<ProductObj> old_product, boolean isEnable) {

        if (type == STARTSTOP.SINGLE_WITH_SIZE) {

            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
            List<ProductObj> productObjs = new ArrayList<>();
            // Remove Other Parameter, send  needed parameter

            for (int i = 0; i < old_product.size(); i++) {
                ProductObj productObj = new ProductObj();
                productObj.setProduct_id(old_product.get(i).getId());
                productObj.setSku(old_product.get(i).getSku());
                productObj.setMwp_price(old_product.get(i).getMwp_price());
                if (old_product.get(i).getAvailable_sizes() != null && old_product.get(i).getAvailable_sizes().size() > 0) {
                    productObj.setAvailable_size_string(StaticFunctions.ArrayListToString(old_product.get(i).getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
                    productObj.setIs_enable(true);
                } else {
                    productObj.setIs_enable(false);
                }
                productObjs.add(productObj);
            }

            requestProductBatchUpdate.setProducts(productObjs);


            Log.e("updateProduct", "SINGLE_WITH_SIZE_UPDATE =======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());

            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isEnable);

        } else if (type == STARTSTOP.SINGLE_WITHOUT_SIZE) {

            RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
            List<ProductObj> productObjs = new ArrayList<>();

            for (int i = 0; i < old_product.size(); i++) {
                ProductObj productObj = new ProductObj();
                productObj.setProduct_id(old_product.get(i).getId());
                productObj.setSku(old_product.get(i).getSku());
                productObj.setMwp_price(old_product.get(i).getMwp_price());
                productObj.setIs_enable(old_product.get(i).isIs_enable());
                Log.e("updateProduct", "startStopHandler: Single update "+old_product.get(i).isIs_enable() );
                productObjs.add(productObj);
            }

            requestProductBatchUpdate.setProducts(productObjs);
            Log.e("updateProduct", "SINGLE_WITHOUT_SIZE_UPDATE =======>" + Application_Singleton.gson.toJson(requestProductBatchUpdate).toString());
            postBulkUpdateProductStartStop(context, requestProductBatchUpdate, isEnable);

        }

    }

    public void setStartStopDoneListener(StartStopDoneListener startStopDoneListener) {
        this.startStopDoneListener = startStopDoneListener;
    }
}
