package com.wishbook.catalog.Utils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/*

public class MyGcmListenerService extends GcmListenerService {
    PendingIntent pendingIntent;
    private static final String TAG = "MyGcmListenerService";

    */
/**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     *//*

    // [START receive_message]
    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        final String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        if (MobiComPushReceiver.isMobiComPushNotification(data)) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent().setAction(MobiComKitConstants.APPLOZIC_UNREAD_COUNT));
            Log.d("Applozic Data", "" + data);
            if (data != null) {
                if (data.get("APPLOZIC_01") != null) {
                    if (createNotification(data)) {
                        return;
                    }
                }
            }

            MobiComPushReceiver.processMessageAsync(this, data);
            return;
        }


        Log.d(TAG, "GCM Message: " + message);
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
            Freshchat.getInstance(this).handleGcmMessage(data);
            FreshchatNotificationConfig notificationConfig = new FreshchatNotificationConfig()
                    .setNotificationSoundEnabled(true)
                    .setSmallIcon(getNotificationIcon())
                    .launchActivityOnFinish(Activity_Home.class.getName())
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            Freshchat.getInstance(this).setNotificationConfig(notificationConfig);
            return;
        } else {
            if (data.getString("message", "").equals("Test single notification")) {
                sendNotification(message, "Test", "", "", "", "", "");
            } else if (data.getString("push_type", "").equals("")) {

            } else {
                storeNotification(data);
            }


            //AppLogic

            if (MobiComPushReceiver.isMobiComPushNotification(data)) {
                MobiComPushReceiver.processMessageAsync(this, data);

                Log.d("PUSHED", "Message Received");

                return;
            }


            // [START_EXCLUDE]
            */
/**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             *//*


            */
/**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             *//*


            // Removed Push-id (change by jay)
            if (data.getString("push_type", "").equals("catalog")) {
                sendNotification(message, "catalog", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));//catalog_FragmentGallery ...catalogId:table_id
            }
            if (data.getString("push_type", "").equals("credit_reference_buyer_requested")) {
                sendNotification(message, "credit_reference_buyer_requested", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));//catalog_FragmentGallery ...catalogId:table_id
            }

            if (data.getString("push_type", "").equals("selection")) {
                sendNotification(message, "selection", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));//selectionlist ...selectionId:table_id
            }

            if (data.getString("push_type", "").equals("buyer"))//supplierDetails ...... companyId:table_id
            {

                sendNotification(message, "supplier", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));

            }
            if (data.getString("push_type", "").equals("supplier"))//BuyerDetails ...... companyId:table_id
            {

                sendNotification(message, "buyer", data.getString("table_id", ""), "", data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));

            }

            //Enquiries
            if (data.getString("push_type", "").equals("buyer_enquiry"))//supplierDetails ...... companyId:table_id
            {

                sendNotification(message, "sent_enquiries", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));

            }
            if (data.getString("push_type", "").equals("supplier_enquiry"))//BuyerDetails ...... companyId:table_id
            {

                sendNotification(message, "received_enquiries", data.getString("table_id", ""), "", data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));

            }


            if (data.getString("push_type", "").equals("promotional"))//Link to promotional
            {
                if (data.getString("title", "").contains("Sales Order")) {
                    sendNotification(message, "sales", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));
                } else if (data.getString("title", "").contains("Purchase Order")) {
                    sendNotification(message, "purchase", data.getString("table_id", ""), data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));
                } else {
                    sendNotification(message, "promotional", "", data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));
                }
            }

            // For Inactive User
            if (data.getString("push_type", "").equals("inactive_user")) {
                sendNotification(message, "inactive_user", "", data.getString("title", ""), data.getString("image", ""), data.getString("push_id", ""), data.getString("other_para", ""));
            }
            if (data.getString("push_type", "").equals("update"))//Link to playstore
            {
                sendNotification(message, "update", "", "", "", data.getString("push_id", ""), data.getString("other_para", ""));
            }
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    */
/**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     *//*

    private void sendNotification(String message, String fragment, String id, String title, String image, String pushid, String otherPara) {
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

        if (pushid != null && !pushid.isEmpty())
            intent.putExtra("push_id", pushid);

        if (otherPara != null && !otherPara.isEmpty())
            intent.putExtra("other_para", otherPara);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
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
                    .setContentTitle(title)
                    .setContentText(message)
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
                      handleNotificationButton(otherParam.get("not_action_1"),notificationBuilder,title);
                    }

                    if (otherParam.containsKey("not_action_2")) {
                        handleNotificationButton(otherParam.get("not_action_2"),notificationBuilder,title);
                    }
                }
            }


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            notificationManager.notify(0 */
/* ID of notification *//*
, notificationBuilder.build());
        }


    }

    public void handleNotificationButton(String action_type, NotificationCompat.Builder notificationBuilder, String title) {
        NotificationCompat.Action notification_action = null;
        if (action_type.equals("catalog_view")) {
            notification_action = new NotificationCompat.Action.Builder(0, "VIEW CATALOG", viewPublicCatalog(title)).build();
            notificationBuilder.addAction(notification_action);
        } else if (action_type.equals("support_call")) {
            notification_action = new NotificationCompat.Action.Builder(0, "SUPPORT CALL", supportCall(title)).build();
            notificationBuilder.addAction(notification_action);
        } else if (action_type.equals("support_chat")) {
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "SUPPORT CHAT", sendSupportChatIntent(title)).build();
            notificationBuilder.addAction(action);
        }
    }

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
            Notification.Builder notificationBuilder = null;
            notificationBuilder = new Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(CHANNEL_ID);
                notif = notificationBuilder.build();
            } else {
                notif = notificationBuilder.build();
            }

            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(1, notif);
        } catch (Exception e) {

        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent supportCall(String title) {
        Intent intent = null;
        SharedPreferences pref = StaticFunctions.getAppSharedPreferences(getApplicationContext());
        if (pref.getBoolean("isLoggedIn", false)) {
            intent = new Intent(this, Activity_Home.class);
        } else {
            intent = new Intent(this, Activity_Login.class);
        }
        intent.putExtra("FRAGMENTS", "support_call");
        intent.putExtra("title", title);
        intent.setAction("SUPPORT_CALL");
        intent.addCategory("SUPPORT_CALL");
        intent.putExtra("other_para", "{\"number\":"+getResources().getString(R.string.call_wb_support_value_notifation)+"}");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public boolean createNotification(Bundle data) {
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
                                    Bundle bundle = new Bundle();
                                    bundle.putString("message", messageJson.get("message").toString());
                                    bundle.putString("push_type", "catalogwise-enquiry-supplier");
                                    bundle.putString("table_id", messageJson.get("conversationId").toString());
                                    storeNotification(bundle);
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

    private void storeNotification(Bundle data) {
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTable_id(data.getString("table_id", ""));
        notificationModel.setPush_type(data.getString("push_type", ""));
        notificationModel.setPush_id(data.getString("push_id", ""));
        notificationModel.setName(data.getString("name", ""));
        notificationModel.setImage(data.getString("image", ""));
        notificationModel.setNotId(data.getString("notId", ""));
        notificationModel.setTitle(data.getString("title", ""));
        notificationModel.setMessage(data.getString("message", ""));
        notificationModel.setCompany_image(data.getString("company_image", ""));
        notificationModel.setCollapse_key(data.getString("collapse_key", ""));
        notificationModel.setRead(false);
        notificationModel.setOtherPara(data.getString("other_para"));


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

}
*/
