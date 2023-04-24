package com.wishbook.catalog.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class CTNotificationIntentService extends IntentService {

    public final static String MAIN_ACTION = "com.clevertap.PUSH_EVENT";
    public final static String TYPE_BUTTON_CLICK = "com.clevertap.ACTION_BUTTON_CLICK";

    public CTNotificationIntentService() {
        super("CTNotificationIntentService");
        Log.e("TAG", "CTNotificationIntentService: " );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;

        String type = extras.getString("ct_type");
        if (type != null && TYPE_BUTTON_CLICK.equals(type)) {
            Log.v("TAG","CTNotificationIntentService handling " + TYPE_BUTTON_CLICK);
            handleActionButtonClick(extras);
        } else {
            Log.v("TAG","CTNotificationIntentService: unhandled intent "+intent.getAction());
        }
    }

    private void handleActionButtonClick(Bundle extras) {
        try {
            boolean autoCancel = extras.getBoolean("autoCancel", false);
            int notificationId = extras.getInt("notificationId", -1);
            String dl = extras.getString("dl");

            Context context = getApplicationContext();
            Intent launchIntent;
            if (dl != null) {
                launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dl));
            } else {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            }

            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            launchIntent.putExtras(extras);
            launchIntent.removeExtra("dl");

            if (autoCancel && notificationId > -1) {
                NotificationManager notificationManager =
                        (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
            }
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); // close the notification drawer
            startActivity(launchIntent);
        } catch (Throwable t) {
            Log.v("TAG","CTNotificationService: unable to process action button click:  "+ t.getLocalizedMessage());
        }
    }
}

