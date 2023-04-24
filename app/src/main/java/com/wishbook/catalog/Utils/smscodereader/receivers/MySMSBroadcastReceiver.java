package com.wishbook.catalog.Utils.smscodereader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.wishbook.catalog.Application_Singleton;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private OTPReceiveListener otpListener;

    /**
     * @param otpListener
     */
    public void setOTPListener(OTPReceiveListener otpListener) {
        this.otpListener = otpListener;
    }


    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Application_Singleton.SMSTAG, "MySMSBroadcastReceiver onReceive: Call");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            Log.d(Application_Singleton.SMSTAG, "MySMSBroadcastReceiver onReceive: Success");
                            //This is the full message
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (otpListener != null) {
                                if (message != null) {
                                    String[] otp = message.split("\n");
                                    if (otp.length > 0) {
                                        otpListener.onOTPReceived(otp[0]);
                                    }
                                }
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            if (otpListener != null) {
                                otpListener.onOTPTimeOut();
                            }
                            break;

                        case CommonStatusCodes.API_NOT_CONNECTED:
                            Log.d(Application_Singleton.SMSTAG, "MySMSBroadcastReceiver onReceive: API NOT CONNECTED");
                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("API NOT CONNECTED");
                            }

                            break;

                        case CommonStatusCodes.NETWORK_ERROR:

                            Log.d(Application_Singleton.SMSTAG, "MySMSBroadcastReceiver onReceive: NETWORK ERROR");
                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("NETWORK ERROR");
                            }

                            break;

                        case CommonStatusCodes.ERROR:
                            Log.d(Application_Singleton.SMSTAG, "MySMSBroadcastReceiver onReceive:  ERROR");
                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("SOME THING WENT WRONG");
                            }

                            break;

                    }
                }

            }
        }
    }

    public interface OTPReceiveListener {

        void onOTPReceived(String otp);

        void onOTPTimeOut();

        void onOTPReceivedError(String error);
    }
}
