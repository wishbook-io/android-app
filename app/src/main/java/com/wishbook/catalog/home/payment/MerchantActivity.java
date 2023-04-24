package com.wishbook.catalog.home.payment;

/**
 * Created by root on 18/1/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.RequestPaytm;
import com.wishbook.catalog.commonmodels.responses.ResponseCheckSum;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchantapp);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getOrderId(this);
    }

    private void getOrderId(MerchantActivity merchantActivity) {
        String orderId = getIntent().getStringExtra("orderid");
        String order_amount = getIntent().getStringExtra("order_amount");
        if (orderId != null) {
            generatingCheckSum(orderId,order_amount);
        }
    }

    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public void onStartTransaction(String orderId, String order_amount,ResponseCheckSum responseCheckSum) {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        Resources res = getResources();
        paramMap.put("MID", BuildConfig.PAYTM_MERCHANT_ID);
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", UserInfo.getInstance(MerchantActivity.this).getUserId());
        paramMap.put("INDUSTRY_TYPE_ID", res.getString(R.string.industry_type_id));
        paramMap.put("CHANNEL_ID", res.getString(R.string.channel_id));
        paramMap.put("TXN_AMOUNT", order_amount);
        paramMap.put("WEBSITE", res.getString(R.string.website));
        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
       // paramMap.put("EMAIL", UserInfo.getInstance(MerchantActivity.this).getEmail());
       // paramMap.put("MOBILE_NO", UserInfo.getInstance(MerchantActivity.this).getMobile());
        paramMap.put( "CHECKSUMHASH" , responseCheckSum.getCHECKSUMHASH());
        PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant(
                URLConstants.GENERATE_CHECKSUM,
                URLConstants.VERIFY_CHECKSUM);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.d("Error", inErrorMessage);
                        setResult(Activity.RESULT_OK, new Intent().putExtra("response", inErrorMessage).putExtra("transaction", "failure"));
                        finish();
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        //Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                       *//* String response = "OrderID : " + inResponse.getString("ORDERID") + "\n"
                                + "STATUS : " + inResponse.getString("STATUS") + "\n"
                                + "GATEWAYNAME : " + inResponse.getString("GATEWAYNAME") + "\n"
                                + "TXNAMOUNT : " + inResponse.getString("TXNAMOUNT") + "\n";
                        new MaterialDialog.Builder(MerchantActivity.this).title("Transaction Successful")
                                .content(response).show();*//*
                        Intent intent = getIntent();
                        intent.putExtra("response", inResponse);
                        intent.putExtra("transaction", "Success");
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Log.d("ErrorLoadingWebPage", "");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.d("ClientAuthFailed", inErrorMessage);
                        setResult(Activity.RESULT_OK, new Intent().putExtra("response", inErrorMessage).putExtra("transaction", "Failure"));
                        finish();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.d("ErrorLoadingWebPage", inErrorMessage);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                        String inErrorMessage = "Transaction has been cancelled";
                        setResult(Activity.RESULT_OK, new Intent().putExtra("response", inErrorMessage).putExtra("transaction", "Failure"));
                        finish();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();

                        setResult(Activity.RESULT_OK, new Intent().putExtra("response", inResponse).putExtra("transaction", "Failure").putExtra("message", inErrorMessage));
                        finish();
                    }

                });
    }

    private void generatingCheckSum(final String orderId, final String order_amount) {
        //StaticFunctions.showProgressbar(MerchantActivity.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(MerchantActivity.this);
        Resources res = getResources();
        RequestPaytm requestPaytm = new RequestPaytm();
        requestPaytm.setMID(BuildConfig.PAYTM_MERCHANT_ID);
        requestPaytm.setORDER_ID(orderId);
        requestPaytm.setCUST_ID(UserInfo.getInstance(MerchantActivity.this).getUserId());
        requestPaytm.setINDUSTRY_TYPE_ID(res.getString(R.string.industry_type_id));
        requestPaytm.setCHANNEL_ID(res.getString(R.string.channel_id));
        requestPaytm.setTXN_AMOUNT(order_amount);
        requestPaytm.setWEBSITE(res.getString(R.string.website));
        requestPaytm.setCALLBACK_URL("https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
       // requestPaytm.setEMAIL(UserInfo.getInstance(MerchantActivity.this).getEmail()) ;
       // requestPaytm.setMOBILE_NO(UserInfo.getInstance(MerchantActivity.this).getMobile());

        HashMap<String, String> params = new HashMap<String, String>();
        String requestPaytmString = new Gson().toJson(requestPaytm);
        JsonObject jsonObject = new Gson().fromJson(requestPaytmString, JsonObject.class);
        HttpManager.getInstance(MerchantActivity.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.GENERATE_CHECKSUM, jsonObject, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ResponseCheckSum responseCheckSum = Application_Singleton.gson.fromJson(response, ResponseCheckSum.class);
                onStartTransaction(orderId, order_amount,responseCheckSum);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(MerchantActivity.this)
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
    */
}