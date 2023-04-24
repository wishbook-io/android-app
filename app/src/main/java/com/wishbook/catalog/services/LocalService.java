package com.wishbook.catalog.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

import rx.subjects.PublishSubject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CheckInmodel;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

public class LocalService extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    private SharedPreferences pref;
    public static PublishSubject<Integer> notificationCounter = PublishSubject.create();

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        ctx = this;
        pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        startService();
    }

    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }

    private class mainTask extends TimerTask {
        public void run() {
            if (!pref.getString("currentmeet", "na").equals("na") && !pref.getString("currentmeetbuyer", "na").equals("na")) {
                boolean isExists = false;
                for (int i = 0; i < Application_Singleton.selectedBuyer.size(); i++) {
                    final Response_Buyer response_buyer = Application_Singleton.gson.fromJson(pref.getString("currentmeetbuyer", "na"), Response_Buyer.class);
                    if (Application_Singleton.selectedBuyer.get(i).getBuyer().getId().equals(response_buyer.getId())) {
                        isExists = true;
                        Application_Singleton.selectedBuyer.get(i).setTime(Application_Singleton.selectedBuyer.get(i).getTime() + 1);
                        Intent intent = new Intent("timerservice");
                        intent.putExtra("time", "" + Application_Singleton.selectedBuyer.get(i).getTime());
                        intent.putExtra("buyer", "" + Application_Singleton.selectedBuyer.get(i).getBuyer().getId());
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    }
                }
                if (!isExists) {
                    Application_Singleton.selectedBuyer.add(new CheckInmodel(Application_Singleton.gson.fromJson(pref.getString("currentmeetbuyer", "na"), Response_Buyer.class), "checkedin", 0));
                }
            }

        }
    }

    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }


}