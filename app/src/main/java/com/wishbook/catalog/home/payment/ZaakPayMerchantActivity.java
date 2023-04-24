package com.wishbook.catalog.home.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tech4 on 13/5/17.
 */


public class ZaakPayMerchantActivity extends AppCompatActivity {


    private String SUCCESS_URL =  "/zaakpay/success";
    private String FAILURE_URL =  "/zaakpay/failure";
    @BindView(R.id.zaakpay_webview)
    WebView zaakpay_webview;
    private static final String HASH_ALGORITHM = "HmacSHA256";

    String mContact, mEmail, mOrderID, mRedirecturl, mMerchantId;
    Integer mAmount;
    private String mTestUrl = "http://zaakpaystaging.centralindia.cloudapp.azure.com:8080/api/paymentTransact/V7";
    private String mLiveUrl = "https://api.zaakpay.com/api/paymentTransact/V7";
    private String mSecretKey;
    private String CURRENCY = "INR";
    private String mCompanyName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zaakpay_layout);
        ButterKnife.bind(this);
        StaticFunctions.initializeAppsee();

        zaakpay_webview.getSettings().setJavaScriptEnabled(true);

        //asssigning values
        assigningValues();

        try {
            postToServer();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void assigningValues() {
        mContact = UserInfo.getInstance(this).getMobile();
        mEmail = UserInfo.getInstance(this).getEmail();
        mCompanyName = UserInfo.getInstance(this).getCompanyname();
        //  mMerchantId = "b19e8f103bce406cbd3476431b6b7973";
        mMerchantId = BuildConfig.ZAAKPAY_MERCHANT_ID;

        //  mSecretKey = "0678056d96914a8583fb518caf42828a";
        mSecretKey = BuildConfig.ZAAKPAY_SEC_KEY;
        mOrderID = getIntent().getStringExtra("orderid");
        mAmount = (int) (Float.parseFloat(getIntent().getStringExtra("order_amount")) * 100);
        mRedirecturl = URLConstants.APP_URL + "/api/v1/zaakpay/response/";

    }


    private void postToServer() throws Exception {
        String checksumStr = "amount=" + this.mAmount +
                "&buyerEmail=" + this.mEmail +
                "&buyerFirstName=" + this.mCompanyName +
                "&buyerLastName=" + " " +
                "&buyerPhoneNumber=" + this.mContact +
                "&currency=" + this.CURRENCY +
                "&merchantIdentifier=" + this.mMerchantId +
                "&orderId=" + this.mOrderID +
                "&returnUrl=" + this.mRedirecturl + "&";

        //checksumStr = "amount=100&bankid=PNB&buyerAddress=Delhi&buyerCity=Delhi&buyerCountry=India&buyerEmail=aa@gmail.com&buyerFirstName=abc&buyerLastName=sharma&buyerPhoneNumber=9876543210&buyerPincode=123455&buyerState=Delhi&currency=INR&debitorcredit=credit&merchantIdentifier="+this.mMerchantId+"&merchantIpAddress=127.0.0.1&mode=0&orderId=abc12323123&product1Description=thisistestproduct&productDescription=toy&productInfo=amazing&purpose=1&returnUrl=http://localhost:51476response.asp&shipToAddress=Delhi&shipToCity=Delhi&shipToCountry=India&shipToFirstname=manu&shipToLastname=garg&shipToPhoneNumber=9871234093&shipToPincode=1212121&shipToState=Kurkshetra&showMobile=1&txnDate=2017-07-12&txnType=3&zpPayOption=1";
        //  String checksum = hashMac(checksumStr, mSecretKey);
        Log.d("Zaakpay", "checksumStr " + checksumStr);
        String checksum = calculateChecksum(mSecretKey, checksumStr);
        Log.d("Zaakpay", "checksum " + checksum);
        Log.d("Zaakpay", "mSecretKey " + mSecretKey);

        boolean verify = verifyChecksum(mSecretKey, checksumStr, checksum);

        Log.d("Zaakpay", "verify " + verify);

        if(BuildConfig.DEBUG){

            this.mLiveUrl = this.mTestUrl;
        }


        // String urlTOLoad = "https://test.mobikwikm.com/wallet?checksum=b841a6bf9047e2ebce7c6a7428eb78d817cdba3c4f97721ec7c913ddd84501d1&cell=9810019581&email=dhruv.test@gmail.com&amount="+order_amount+"&orderid="+orderId+"&redirecturl=http://test.com/handleresponse&mid=MBK9002";
        String loadurl = this.mLiveUrl + "?" + checksumStr.substring(0, checksumStr.length() - 1) + "&checksum=" + checksum;
      //  zaakpay_webview.loadUrl(loadurl);
        Log.d("Zaakpay", " url " + this.mLiveUrl);

        String params = "amount=" + URLEncoder.encode(this.mAmount + "", "UTF-8") +
                "&buyerEmail=" + URLEncoder.encode(this.mEmail, "UTF-8") +
                "&buyerFirstName=" + this.mCompanyName +
                "&buyerLastName=" + " " +
                "&buyerPhoneNumber=" + URLEncoder.encode(this.mContact, "UTF-8") +
                "&currency=" + URLEncoder.encode(this.CURRENCY, "UTF-8") +
                "&merchantIdentifier=" + URLEncoder.encode(this.mMerchantId, "UTF-8") +
                "&orderId=" + URLEncoder.encode(this.mOrderID, "UTF-8") +
                "&returnUrl=" + URLEncoder.encode(this.mRedirecturl, "UTF-8");


       /* Log.d("Zaakpay", "checksumStr " + params);
        String checksum =calculateChecksum(mSecretKey, params);
        Log.d("Zaakpay", "checksum " + checksum);
        Log.d("Zaakpay", "mSecretKey " + mSecretKey);


        boolean verify = verifyChecksum(mSecretKey, params, checksum);*/


        String postData = params + "&checksum=" + URLEncoder.encode(checksum, "UTF-8");
        Log.d("Zaakpay", "postdata " + postData);

        zaakpay_webview.postUrl(this.mLiveUrl, postData.getBytes());
        this.zaakpay_webview.setWebViewClient(new AppWebViewClient());
    }

    private static String toHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);

        byte[] arrayOfByte = bytes;
        int j = bytes.length;
        for (int i = 0; i < j; i++) {
            Byte b = Byte.valueOf(arrayOfByte[i]);
            String str = Integer.toHexString(b.byteValue());
            int len = str.length();
            if (len == 8) {
                buffer.append(str.substring(6));
            } else if (str.length() == 2) {
                buffer.append(str);
            } else {
                buffer.append("0" + str);
            }
        }
        return buffer.toString();
    }

    public static String calculateChecksum(String secretKey, String allParamValue) throws Exception {
        byte[] dataToEncryptByte = allParamValue.getBytes();
        Log.d("Zaakpay", "dataToEncryptByte " + allParamValue.getBytes());
        byte[] keyBytes = secretKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] checksumByte = mac.doFinal(dataToEncryptByte);
        Log.d("Zaakpay", "checksumByte " + checksumByte);

        String checksum = toHex(checksumByte);

        return checksum;
    }

    public static boolean verifyChecksum(String secretKey, String allParamVauleExceptChecksum, String checksumReceived) throws Exception {
        byte[] dataToEncryptByte = allParamVauleExceptChecksum.getBytes();
        byte[] keyBytes = secretKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] checksumCalculatedByte = mac.doFinal(dataToEncryptByte);
        String checksumCalculated = toHex(checksumCalculatedByte);
        if (checksumReceived.equals(checksumCalculated)) {
            return true;
        }
        return false;
    }


    public String hashMac(String text, String secretKey) throws SignatureException {
        try {
            /*Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);*/
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            Log.d("encode 1", Base64.encodeToString(sha256_HMAC.doFinal(text.getBytes()), Base64.DEFAULT));
            //   Log.d("encode 2", Base64.encodeBase64String(sha256_HMAC.doFinal(text.getBytes()));
            return toHexString(sha256_HMAC.doFinal(text.getBytes()));


            //   return toHexString(sha256_HMAC.doFinal(text.getBytes()));
            //   return Base64.encodeBase64String(sha256_HMAC.doFinal(text.getBytes()));
            //  return  checksumdata;
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("error building signature, no such algorithm in device HmacSHA256");
        } catch (InvalidKeyException e2) {
            throw new SignatureException("error building signature, invalid key HmacSHA256");
        }

    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            formatter.format("%02x", new Object[]{Byte.valueOf(bytes[i])});
        }
        return sb.toString();
    }

    private class AppWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            System.out.println("on page started url " + url);
            String response;
            //IF FAILED FROM SERVER
            if (url.contains(FAILURE_URL)) {
               /* response =
                        "OrderID : " + mOrderID + "\n"
                                + "Status : " + "Failure" + "\n"
                                + "Amount : " + mAmount + "\n";
                new MaterialDialog.Builder(MobiKwikMerchantActivity.this).title("Transaction Failure ").content(response).positiveText("Ok").show();
            */
                Intent intent = getIntent();
                intent.putExtra("mOrderID", mOrderID);
                intent.putExtra("mOrderAmount", String.valueOf(mAmount / 100));
                intent.putExtra("transaction", "Failure");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            //IF SUCCESS  FROM SERVER
            else if (url.contains(SUCCESS_URL)) {
               /* response =
                        "OrderID : " + mOrderID + "\n"
                        + "Status : " + "Success" + "\n"
                        + "Amount : " + mAmount + "\n";
                new MaterialDialog.Builder(MobiKwikMerchantActivity.this).title("Transaction Success ").content(response).positiveText("Ok").show();
*/
                Intent intent = getIntent();
                intent.putExtra("mOrderID", mOrderID);
                intent.putExtra("mOrderAmount", String.valueOf(mAmount / 100));
                intent.putExtra("transaction", "Success");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
