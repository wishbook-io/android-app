package com.wishbook.catalog.Utils.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.freshchat.consumer.sdk.Freshchat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            setGcmToken(token);
            setOtherPluginToken(token);
            subscribeTopics(token);
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(GCMPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void setOtherPluginToken(String token) {
        Freshchat.getInstance(this).setPushRegistrationToken(token);
    }

    private void setGcmToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("gcmtoken",token).apply();
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic);
        }

    }


}