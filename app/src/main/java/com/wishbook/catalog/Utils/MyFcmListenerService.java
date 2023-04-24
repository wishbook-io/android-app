package com.wishbook.catalog.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.NotificationInfo;
import com.crashlytics.android.Crashlytics;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatNotificationConfig;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.notifications.models.NotificationModel;
import com.wishbook.catalog.login.Activity_Login;
import com.wishbook.catalog.services.LocalService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyFcmListenerService extends FirebaseMessagingService {
    PendingIntent pendingIntent;
    private static final String TAG = "MyGcmListenerService";
    int min_random = 1, max_random = 2000;

    // [START receive_message]

    /**
     * Override method Entry point to Notification received
     * @param data
     */
    @Override
    public void onMessageReceived(RemoteMessage data) {
        super.onMessageReceived(data);
        String from = data.getFrom();
        final Map message = data.getData();
        Log.d(TAG, "From: " + from);
        if (MobiComPushReceiver.isMobiComPushNotification(message)) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent().setAction(MobiComKitConstants.APPLOZIC_UNREAD_COUNT));
            Log.d("Applozic Data", "" + data);
            if (data != null) {
                if (data.getData().get("APPLOZIC_01") != null) {
                    if (createNotification(message)) {
                        return;
                    }
                }
            }
            MobiComPushReceiver.processMessageAsync(this, message);
            return;
        }

        Log.d(TAG, "FCM Message: MESSAGE" + message);
        Log.d(TAG, "FCM Message: FROM " + from);
        Log.d(TAG, "FCM Message: DATA " + data);
        if (Freshchat.isFreshchatNotification(data)) {
            String CHANNEL_ID = "com.wishbook.catalog.android";// The id of the channel.
            CharSequence name = getString(R.string.wishbook_channel);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            Freshchat.getInstance(this).handleFcmMessage(this, data);
            FreshchatNotificationConfig notificationConfig = new FreshchatNotificationConfig()
                    .setNotificationSoundEnabled(true)
                    .setSmallIcon(getNotificationIcon())
                    .launchActivityOnFinish(Activity_Home.class.getName())
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            Freshchat.getInstance(this).setNotificationConfig(notificationConfig);
            return;
        } else {
            try {
                if (data.getData().size() > 0) {
                    Bundle extras = new Bundle();
                    for (Map.Entry<String, String> entry : data.getData().entrySet()) {
                        extras.putString(entry.getKey(), entry.getValue());
                    }
                    NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);
                    if (info.fromCleverTap) {
                        CleverTapAPI.createNotification(getApplicationContext(), extras);
                        storeCleverTapNotification(data.getData());
                        return;
                    } else {

                    }
                }
            } catch (Throwable t) {
                Log.d(TAG, "Error parsing FCM message", t);
            }

            if (data.getData() != null) {
                Log.e(TAG, "onMessageReceived: Get Data Not Null" );
                if (data.getData().containsKey("message") && data.getData().get("message").equals("Test single notification")) {
                    sendNotification(data.getData().get("message"), "Test", "", "", "", "", "");
                } else if (data.getData().containsKey("push_type") && data.getData().get("push_type").equals("")) {

                } else {
                    storeNotification(message);
                }


                // [START_EXCLUDE]
                /**
                 * Production applications would usually process the message here.
                 * Eg: - Syncing with server.
                 *     - Store message in local database.
                 *     - Update UI.
                 */

                /**
                 * In some cases it may be useful to show a notification indicating to the user
                 * that a message was received.
                 */

                // Removed Push-id (change by jay)
                try {
                    if (data.getData().containsKey("push_type")) {
                        String message1 = "", fragment = "", id = "", title = "", image = "", pushid = "", otherPara = "";
                        message1 = StaticFunctions.checkNotificationMapKey(data.getData(), "message");
                        fragment = StaticFunctions.checkNotificationMapKey(data.getData(), "push_type");
                        id = StaticFunctions.checkNotificationMapKey(data.getData(), "table_id");
                        title = StaticFunctions.checkNotificationMapKey(data.getData(), "title");
                        image = StaticFunctions.checkNotificationMapKey(data.getData(), "image");
                        pushid = StaticFunctions.checkNotificationMapKey(data.getData(), "push_id");
                        otherPara = StaticFunctions.checkNotificationMapKey(data.getData(), "other_para");

                        if (data.getData().get("push_type").equals("buyer"))//supplierDetails ...... companyId:table_id
                        {
                            fragment = "supplier";
                        }
                        if (data.getData().get("push_type").equals("supplier"))//BuyerDetails ...... companyId:table_id
                        {
                            fragment = "buyer";
                        }

                        //Enquiries
                        if (data.getData().get("push_type").equals("buyer_enquiry"))//supplierDetails ...... companyId:table_id
                        {
                            fragment = "sent-enquiries";


                        }
                        if (data.getData().get("push_type").equals("supplier_enquiry"))//BuyerDetails ...... companyId:table_id
                        {
                            fragment = "received_enquiries";

                        }

                        if (data.getData().get("push_type").equals("promotional"))//Link to promotional
                        {
                            if (title.contains("Sales Order")) {
                                fragment = "sales";
                            } else if (title.contains("Purchase Order")) {
                                fragment = "purchase";
                                //sendNotification(data.getData().get("message"), "purchase", data.getData().get("table_id"), data.getData().get("title"), data.getData().get("image"), data.getData().get("push_id"), data.getData().get("other_para"));
                            } else {

                            }
                        }

                        try {
                            sendNotification(message1, fragment, id, title, image, pushid, otherPara);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    customLogException(e, data.getData());
                }


            }
            // [END_EXCLUDE]
        }

    }
    // [END receive_message]


    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String fragment, String id, String title, String image, String pushid, String otherPara) {
        try {
            SharedPreferences pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
            Intent intent = null;
            if (pref.getBoolean("isLoggedIn", false)) {
                intent = new Intent(this, Activity_Home.class);
            } else {
                intent = new Intent(this, Activity_Login.class);
            }

            if (fragment != null && !fragment.isEmpty()) {
                if (fragment.equals("inactive_user")) {
                    intent.putExtra("FRAGMENTS", "catalog");
                    intent.putExtra("ctype", "public");
                } else {
                    intent.putExtra("FRAGMENTS", fragment);
                }
            } else {
                intent.putExtra("FRAGMENTS", fragment);
            }
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("message", message);

            if (pushid != null && !pushid.isEmpty())
                intent.putExtra("push_id", pushid);

            if (otherPara != null && !otherPara.isEmpty())
                intent.putExtra("other_para", otherPara);


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = null;
            if (image != null && !image.isEmpty()) {
                new generatePictureStyleNotification1(this, title, message, image, otherPara, fragment);
            } else {
                String CHANNEL_ID = "com.wishbook.catalog.android";// The id of the channel.
                CharSequence name = getString(R.string.wishbook_channel);// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                if (title.isEmpty()) {
                    title = "Wishbook";
                }


                notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(Html.fromHtml(title))
                        .setContentText(Html.fromHtml(message))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setContentIntent(pendingIntent);

                if (otherPara != null) {
                    Type type = new TypeToken<HashMap<String, String>>() {
                    }.getType();
                    HashMap<String, String> otherParam = new Gson().fromJson(otherPara, type);
                    if (otherParam != null && !otherParam.isEmpty()) {
                        if (otherParam.containsKey("not_action_1")) {
                            if (fragment != null && fragment.equals("catalog") && otherParam.get("not_action_1").equals("catalog_view")) {
                                NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "VIEW CATALOG", pendingIntent).build();
                                notificationBuilder.addAction(action);
                            } else if (fragment != null && fragment.equals("inactive_user") && otherParam.get("not_action_1").equals("catalog_view")) {
                                NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "VIEW CATALOG", viewPublicCatalog(title)).build();
                                notificationBuilder.addAction(action);
                            }
                        }

                        if (otherParam.containsKey("not_action_2")) {
                            if (fragment != null && fragment.equals("inactive_user") && otherParam.get("not_action_2").equals("support_chat")) {
                                NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "SUPPORT CHAT", sendSupportChatIntent(title)).build();
                                notificationBuilder.addAction(action);
                            }
                        }
                    }
                }
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    notificationManager.createNotificationChannel(mChannel);
                    notificationBuilder.setChannelId(CHANNEL_ID);
                    notificationBuilder.setPriority(importance);
                }
                int notified_number = min_random + (int) (Math.random() * ((max_random - min_random) + 1));
                notificationManager.notify(notified_number/* ID of notification */, notificationBuilder.build());

                sendNotificationReceivedAnalytics(fragment, otherPara, title, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customLogException(e, null);
        }
    }

    /**
     *  Create a Picture style notification using download image AsyncTask task
     */
    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl, otherPara, fragment;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl, String otherPara, String fragment) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
            this.otherPara = otherPara;
            this.fragment = fragment;
        }


        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                return BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.applogo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            notifyPictureStyleNotification(mContext, title, message, otherPara, fragment, result);
        }
    }

    /**
     * Create a Picture style notification using fresco
     */
    public class generatePictureStyleNotification1 {
        private Context mContext;
        private String title, message, imageUrl, otherPara, fragment;

        public generatePictureStyleNotification1(Context context, String title, String message, String imageUrl, String otherPara, String fragment) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
            this.otherPara = otherPara;
            this.fragment = fragment;
            loadFrescoImage();
        }

        public void loadFrescoImage() {
            String brand = Build.BRAND;
            String model = Build.MODEL;
            if (brand != null && brand.toLowerCase().equals("motorola") && model != null && (model.equalsIgnoreCase("MotoG3") || model.equalsIgnoreCase("MotoG3-TE") || model.equalsIgnoreCase("Moto G (4)") || model.equalsIgnoreCase("XT1022"))) {
                new generatePictureStyleNotification(mContext, title, message, imageUrl, otherPara, fragment).execute();
            } else {
                DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(Uri.parse(imageUrl)), getApplicationContext());
                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                         @Override
                                         public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                                             notifyPictureStyleNotification(mContext, title, message, otherPara, fragment, bitmap);
                                         }

                                         @Override
                                         public void onFailureImpl(DataSource dataSource) {
                                             // No cleanup required here.
                                         }
                                     },
                        CallerThreadExecutor.getInstance());
            }
        }
    }

    public void notifyPictureStyleNotification(Context mContext, String title, String message, String otherPara, String fragment, Bitmap result) {
        try {
            String CHANNEL_ID = "com.wishbook.catalog.android";// The id of the channel.
            CharSequence name = getString(R.string.wishbook_channel);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = null;
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder notificationBuilder = null;

            notificationBuilder = new Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(Html.fromHtml(title))
                    .setContentText(Html.fromHtml(message))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(result)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(result));

            if (otherPara != null) {
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> otherParam = new Gson().fromJson(otherPara, type);
                if (otherParam != null && !otherParam.isEmpty()) {
                    if (otherParam.containsKey("not_action_1")) {
                        if (fragment != null && fragment.equals("catalog") && otherParam.get("not_action_1").equals("view")) {
                            notificationBuilder.addAction(0, "VIEW CATALOG", pendingIntent);
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(CHANNEL_ID);
                notif = notificationBuilder.build();
            } else {
                notif = notificationBuilder.build();
            }

            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
                notificationBuilder.setChannelId(CHANNEL_ID);
                notificationBuilder.setPriority(importance);
            }
            int notified_number = min_random + (int) (Math.random() * ((max_random - min_random) + 1));
            notificationManager.notify(notified_number, notif);
            sendNotificationReceivedAnalytics(fragment, otherPara, title, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_white_launcher : R.mipmap.ic_launcher;
    }

    private PendingIntent sendSupportChatIntent(String title) {
        Intent intent = null;
        SharedPreferences pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        if (pref.getBoolean("isLoggedIn", false)) {
            intent = new Intent(this, Activity_Home.class);
        } else {
            intent = new Intent(this, Activity_Login.class);
        }
        intent.putExtra("FRAGMENTS", "support_chat");
        intent.putExtra("title", title);

        intent.setAction("SUPPORT_CHAT");
        intent.addCategory("SUPPORT_CHAT");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent viewPublicCatalog(String title) {
        Intent intent = null;
        SharedPreferences pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        if (pref.getBoolean("isLoggedIn", false)) {
            intent = new Intent(this, Activity_Home.class);
        } else {
            intent = new Intent(this, Activity_Login.class);
        }
        intent.putExtra("FRAGMENTS", "catalog");
        intent.putExtra("title", title);
        intent.setAction("VIEW_CATALOG");
        intent.addCategory("VIEW_CATALOG");

        intent.putExtra("other_para", "{\"ctype\":\"public\"}");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public boolean createNotification(Map<String, String> data) {
        try {
            boolean isJson = StaticFunctions.isJSONValid(data.get("APPLOZIC_01").toString());
            if (isJson) {
                JSONObject applozic_conversation = new JSONObject(data.get("APPLOZIC_01").toString());
                if (applozic_conversation.has("message")) {
                    boolean isJson1 = StaticFunctions.isJSONValid(applozic_conversation.get("message").toString());
                    if (isJson1) {
                        JSONObject messageJson = null;
                        try {
                            messageJson = new JSONObject(applozic_conversation.get("message").toString());
                            if (messageJson.has("conversationId")) {
                                Log.e(TAG, "createNotification: ConversationID Not null");
                                sendNotification(messageJson.get("message").toString(), "catalogwise-enquiry-supplier", messageJson.get("conversationId").toString(), "", "", "", null);
                                if (!checkAlreadyCatalogEnquiry(messageJson.get("conversationId").toString())) {
                                    // If same enquiry has a notification so not show on notification Adapter

                                    Map<String, String> jsonNotif = new HashMap<>();
                                    jsonNotif.put("message", messageJson.get("message").toString());
                                    jsonNotif.put("push_type", "catalogwise-enquiry-supplier");
                                    jsonNotif.put("table_id", messageJson.get("conversationId").toString());
                                    storeNotification(jsonNotif);
                                    sendNotificationReceivedAnalytics("catalogwise-enquiry-supplier", "", "", messageJson.get("message").toString());

                                }

                                return true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Store Notification in Shared Preference  (PREF_GCM_NOTIFICATION)
     * @param data - Notification message map
     */
    private void storeNotification(Map<String, String> data) {
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTable_id(data.get("table_id"));
        notificationModel.setPush_type(data.get("push_type"));
        notificationModel.setPush_id(data.get("push_id"));
        notificationModel.setName(data.get("name"));
        notificationModel.setImage(data.get("image"));
        notificationModel.setNotId(data.get("notId"));
        notificationModel.setTitle(data.get("title"));
        notificationModel.setMessage(data.get("message"));
        notificationModel.setCompany_image(data.get("company_image"));
        notificationModel.setCollapse_key(data.get("collapse_key"));
        notificationModel.setRead(false);
        notificationModel.setOtherPara(data.get("other_para"));


        ArrayList<NotificationModel> responseData = null;
        if (PrefDatabaseUtils.getGCMNotificationData(this) != null) {
            responseData = new Gson().fromJson(PrefDatabaseUtils.getGCMNotificationData(this), new TypeToken<ArrayList<NotificationModel>>() {
            }.getType());

        } else {
            responseData = new ArrayList<>();
        }

        responseData.add(notificationModel);
        PrefDatabaseUtils.setGCMNotificationData(this, new Gson().toJson(responseData));
        if (responseData != null) {
            LocalService.notificationCounter.onNext(responseData.size());
        }
    }

    private boolean checkAlreadyCatalogEnquiry(String tableID) {
        ArrayList<NotificationModel> responseData = null;
        if (PrefDatabaseUtils.getGCMNotificationData(this) != null) {
            responseData = new Gson().fromJson(PrefDatabaseUtils.getGCMNotificationData(this), new TypeToken<ArrayList<NotificationModel>>() {
            }.getType());
            if (responseData != null && responseData.size() > 0) {
                for (int i = 0; i < responseData.size(); i++) {
                    if (responseData.get(i).getPush_type() != null && responseData.get(i).getTable_id() != null && responseData.get(i).getPush_type().equals("catalogwise-enquiry-supplier") && responseData.get(i).getTable_id().equals(tableID)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Send Non-Fatal error in Fabric Crashlytics
     * @param exception
     * @param map
     */
    private void customLogException(Exception exception, Map<String, String> map) {
        try {
            StringBuffer custom_message = new StringBuffer();
            custom_message.append("User Id===>" + UserInfo.getInstance(this).getUserId() + "\n");
            custom_message.append("Notification Exception==>" + exception.toString() + "\n");
            if (map != null) {
                custom_message.append("Notification Data====>" + map.toString());
            }
            Writer writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            String s = writer.toString();
            custom_message.append("Notification Print StackTrace====>" + s);
            Crashlytics.logException(new Exception(custom_message.toString()));
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "customLogException: ===>" + custom_message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Store FCM New Token
     * @param s
     */
    @Override
    public void onNewToken(String s) {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        String token = s;
        // if(!Application_Singleton.getInstance().isAppIsInBackground(this)) {
        Freshchat.getInstance(this).setPushRegistrationToken(token);
        sendRegistrationToServer(s);
        // }

    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("gcmtoken",token).apply();
    }


    public void sendNotificationReceivedAnalytics(String type, String otherPara, String title, String message) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Notification_Received");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", "Statusbar_Notification");
        if (checkNotificationType(type, otherPara, title, message).size() > 0) {
            prop.putAll(checkNotificationType(type, otherPara, title, message));
        } else {
            prop.put("type", "other");
        }

        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(this, wishbookEvent);
    }

    public HashMap<String, String> checkNotificationType(String type, String otherPara, String title, String message) {
        HashMap<String, String> prop = new HashMap<>();
        try {
            if (otherPara != null) {
                Type type_token = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> otherParam = new Gson().fromJson(otherPara, type_token);
                if (otherParam != null && otherPara.isEmpty() && otherParam.containsKey("deep_link")) {
                    String url = otherParam.get("deep_link");
                    try {
                        Uri intentUri = Uri.parse(url);
                        HashMap<String, String> query_string = SplashScreen.getQueryString(intentUri);
                        if ((query_string.containsKey("catalog") || query_string.containsKey("product"))) {
                            if (query_string.containsKey("id")) {
                                prop.put("type", "Buyer Side");
                                prop.put("property", "Marketing - Catalog");
                            } else {
                                prop.put("type", "Buyer Side");
                                prop.put("property", "Marketing - Product List");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (type != null && type.equalsIgnoreCase("catalog") && message != null && message.contains("just added")) {
                prop.put("type", "Buyer Side");
                prop.put("property", "Followed brand has new catalog");
            }

            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Sales Order")) {
                if (message != null && message.contains("Received")) {
                    prop.put("type", "Seller Side");
                    prop.put("property", "New Sales order received");
                } else {
                    prop.put("type", "Seller Side");
                    prop.put("property", "Order status update");
                }
            }
            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Purchase Order")) {
                prop.put("type", "Buyer Side");
                prop.put("property", "Order status update");
            }

            if (type != null && type.equalsIgnoreCase("credit_reference_buyer_requested")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Invited for Feedback");
            }

            if (type != null && type.equalsIgnoreCase("promotional") && title != null && title.contains("Supplier Status")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Seller approval status");
            }

            if (type != null && type.equalsIgnoreCase("catalogwise-enquiry-supplier")) {
                prop.put("type", "Seller Side");
                prop.put("property", "Leads received");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return prop;
    }


    /** Store CleverTap Notification in Shared Preference
     * nt  ////Notification Title, if absent or empty, fallback to the app name.
     * <p>
     * nm ////Notification Body, if absent or empty, ignore this notification.
     * <p>
     * wzrk_bp  ///  the value will be a URL of an image, that needs to be displayed in the notification.
     * <p>
     * wzrk_dl /// If present, this is a deep link that must be followed at the time of notification open.
     * @param data
     */

    public void storeCleverTapNotification(Map<String, String> data) {
        if(data!=null) {
            NotificationModel notificationModel = new NotificationModel();
            if(data.containsKey("nt")) {
                notificationModel.setTitle(data.get("nt"));
            }
            if(data.containsKey("nm"))
                notificationModel.setMessage(data.get("nm"));
            if(data.containsKey("wzrk_dl")){
                JsonObject innerObject = new JsonObject();
                innerObject.addProperty("deep_link", data.get("wzrk_dl"));
                notificationModel.setOtherPara(Application_Singleton.gson.toJson(innerObject));
            }

            if(data.containsKey("wzrk_bp")) {
                notificationModel.setImage(data.get("wzrk_bp"));
            }

            notificationModel.setPush_type("promotional");

            notificationModel.setRead(false);

            ArrayList<NotificationModel> responseData = null;
            if (PrefDatabaseUtils.getGCMNotificationData(this) != null) {
                responseData = new Gson().fromJson(PrefDatabaseUtils.getGCMNotificationData(this), new TypeToken<ArrayList<NotificationModel>>() {
                }.getType());

            } else {
                responseData = new ArrayList<>();
            }

            responseData.add(notificationModel);
            PrefDatabaseUtils.setGCMNotificationData(this, new Gson().toJson(responseData));
            //Log.e(TAG, "storeCleverTapNotification: Store Data===>"+Application_Singleton.gson.toJson(notificationModel) );
            if (responseData != null) {
                LocalService.notificationCounter.onNext(responseData.size());
            }
        }
    }
}