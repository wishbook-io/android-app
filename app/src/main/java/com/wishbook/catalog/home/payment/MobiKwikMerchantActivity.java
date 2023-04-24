package com.wishbook.catalog.home.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.security.InvalidKeyException;
import java.security.Key;
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

/*
As discussed, platform independent API's are attached  - redirection flow

        TEST ENVIRONMENT DETAILS

        Test url : https://test.mobikwik.com/wallet
        MID : MBK9002
        Secret key : ju6tygh7u7tdg554k098ujd5468o
        TEST CARD
        name : test
        card type : debit/credit
        card number : 4000000000000002
        cvv : 123
        card expiry : 12/2018
*/


public class MobiKwikMerchantActivity extends AppCompatActivity {


    @BindView(R.id.mobikwik_webview)
    WebView mobikwik_webview;
    private static final String HASH_ALGORITHM = "HmacSHA256";

    String mContact, mEmail, mAmount, mOrderID, mRedirecturl, mMerchantId;
    private String mTestUrl = "https://test.mobikwik.com/wallet";
    private String mLiveUrl = "https://walletapi.mobikwik.com/wallet";
    private String mSecretKey;
    private String SUCCESS_URL = URLConstants.APP_URL+"/api/v1/mobikwik/success";
    private String FAILURE_URL = URLConstants.APP_URL+"/api/v1/mobikwik/failure";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobikwik_layout);
        ButterKnife.bind(this);
        StaticFunctions.initializeAppsee();

        mobikwik_webview.getSettings().setJavaScriptEnabled(true);

        //asssigning values
        assigningValues();

        try {
            postToServer();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    public void assigningValues() {
        mContact = UserInfo.getInstance(this).getMobile();
        mEmail = UserInfo.getInstance(this).getEmail();
      //  mMerchantId = "MBK9002";
        mMerchantId = BuildConfig.MOBI_MERCHANT_ID;
     //   mSecretKey = "ju6tygh7u7tdg554k098ujd5468o";
        mSecretKey = BuildConfig.MOBI_SEC_KEY;
        mOrderID = getIntent().getStringExtra("orderid");
        mAmount = getIntent().getStringExtra("order_amount");
        mRedirecturl = URLConstants.APP_URL + "/api/v1/mobikwik/";
    }


    private void postToServer() throws SignatureException {
        String checksumStr = "'" + this.mContact + "''" + this.mEmail + "''" + this.mAmount + "''" + this.mOrderID + "''" + this.mRedirecturl + "''" + this.mMerchantId + "'";
        String checksum = hashMac(checksumStr, mSecretKey );

        // String urlTOLoad = "https://test.mobikwik.com/wallet?checksum=b841a6bf9047e2ebce7c6a7428eb78d817cdba3c4f97721ec7c913ddd84501d1&cell=9810019581&email=dhruv.test@gmail.com&amount="+order_amount+"&orderid="+orderId+"&redirecturl=http://test.com/handleresponse&mid=MBK9002";
        String loadurl = this.mLiveUrl + "?checksum=" + checksum + "&amount=" + this.mAmount + "&mid=" + this.mMerchantId + "&redirecturl=" + this.mRedirecturl + "&orderid=" + this.mOrderID + "&cell=" + this.mContact + "&email=" + this.mEmail;
        System.out.println("load url " + loadurl);

        mobikwik_webview.loadUrl(loadurl);
        this.mobikwik_webview.setWebViewClient(new AppWebViewClient());
    }


    public static String hashMac(String text, String secretKey) throws SignatureException {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            return toHexString(mac.doFinal(text.getBytes()));
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
            if (url.equals(FAILURE_URL)) {
               /* response =
                        "OrderID : " + mOrderID + "\n"
                                + "Status : " + "Failure" + "\n"
                                + "Amount : " + mAmount + "\n";
                new MaterialDialog.Builder(MobiKwikMerchantActivity.this).title("Transaction Failure ").content(response).positiveText("Ok").show();
            */
                Intent intent = getIntent();
                intent.putExtra("mOrderID", mOrderID);
                intent.putExtra("mOrderAmount", mAmount);
                intent.putExtra("transaction", "Failure");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            //IF SUCCESS  FROM SERVER
            else if (url.equals(SUCCESS_URL)) {
               /* response =
                        "OrderID : " + mOrderID + "\n"
                        + "Status : " + "Success" + "\n"
                        + "Amount : " + mAmount + "\n";
                new MaterialDialog.Builder(MobiKwikMerchantActivity.this).title("Transaction Success ").content(response).positiveText("Ok").show();
*/
                Intent intent = getIntent();
                intent.putExtra("mOrderID", mOrderID);
                intent.putExtra("mOrderAmount", mAmount);
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
