package com.wishbook.catalog.home.notifications.adapters;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.notifications.models.NotificationModel;
import com.wishbook.catalog.services.LocalService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by prane on 21-03-2016.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<NotificationModel> notificationModels;

    private final int IMAGE = 0, SIMPLE = 1;

    private String from = "Noticication";

    public NotificationsAdapter(AppCompatActivity mActivity, ArrayList<NotificationModel> notificationModels) {
        this.mActivity = mActivity;
        this.notificationModels = notificationModels;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case SIMPLE:
                View v1 = inflater.inflate(R.layout.notification_noimage, parent, false);
                viewHolder = new SimpleViewholder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.notification_with_img, parent, false);
                viewHolder = new ImageViewholder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case SIMPLE:
                SimpleViewholder view1 = (SimpleViewholder) holder;
                configureSimpleViewholder(view1, position);
                break;
            case IMAGE:
                ImageViewholder view2 = (ImageViewholder) holder;
                configureImageViewholder(view2, position);
                break;
        }

    }

    private void configureImageViewholder(final ImageViewholder view2, final int position) {

        if (notificationModels.get(position).getPush_type().equals("promotional")) {
            view2.getIdtitle().setText(Html.fromHtml(notificationModels.get(position).getTitle()));
        } else {
            view2.getIdtitle().setText(notificationModels.get(position).getName());
        }
        view2.getIdmessage().setText(Html.fromHtml(notificationModels.get(position).getMessage()));

        if (!notificationModels.get(position).getRead()) {
            view2.getBadge_item().setVisibility(View.VISIBLE);
        } else {
            view2.getBadge_item().setVisibility(View.GONE);
        }
        try {
            if (notificationModels.get(position).getImage() != null & !notificationModels.get(position).getImage().equals("")) {
                StaticFunctions.loadFresco(mActivity, notificationModels.get(position).getImage(), view2.getNotimg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        view2.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationModels.get(position).getTitle() != null) {
                    Application_Singleton.trackEvent("Notification", "Open", notificationModels.get(position).getTitle());
                }
                if (notificationModels.get(position).getPush_type().equals("catalog")) {
                    //table id catalog id.

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", "catalog");
                    if (notificationModels.get(position).getTable_id() != null
                            && !notificationModels.get(position).getTable_id().isEmpty()) {
                        if (notificationModels.get(position).getPush_id() != null && !notificationModels.get(position).getPush_id().isEmpty()) {
                            hashMap.put("push_id", notificationModels.get(position).getPush_id());
                        }
                        hashMap.put("id", notificationModels.get(position).getTable_id());
                    }

                    if (notificationModels.get(position).getOtherPara() != null) {
                        Type type = new TypeToken<HashMap<String, String>>() {
                        }.getType();
                        HashMap<String, String> otherParam = new Gson().fromJson(notificationModels.get(position).getOtherPara(), type);
                        for (Map.Entry<String, String> entry : otherParam.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        }
                        if (otherParam.containsKey("deep_link")) {
                            String url = otherParam.get("deep_link");
                            try {
                                Uri intentUri = Uri.parse(url);
                                otherParam.putAll(SplashScreen.getQueryString(intentUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        hashMap.putAll(otherParam);
                        hashMap.put("from_notification", "true");
                    }
                    hashMap.put("from", from);
                    if (notificationModels.get(position).getPush_type() != null) {
                        sendNotificationClickedAnalytics(notificationModels.get(position).getPush_type(), notificationModels.get(position).getOtherPara() != null ? notificationModels.get(position).getOtherPara() : null, notificationModels.get(position).getTitle(), notificationModels.get(position).getMessage());
                    }
                    new DeepLinkFunction(hashMap, mActivity);


                }
                if (notificationModels.get(position).getPush_type().equals("selection")) {

                    DataPasser.selectedID = notificationModels.get(position).getTable_id();
                    Bundle bundle = new Bundle();
                    bundle.putString("fromNotification", "yes");
                    Fragment_ProductsInSelection productsInSelection = new Fragment_ProductsInSelection();
                    productsInSelection.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Selection";
                    Application_Singleton.CONTAINERFRAG = productsInSelection;
                    StaticFunctions.switchActivity(mActivity, OpenContainer.class);

                    //notificationModels.remove(position);
                }

                if (notificationModels.get(position).getPush_type().equals("promotional")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (notificationModels.get(position).getOtherPara() != null) {
                        Type type = new TypeToken<HashMap<String, String>>() {
                        }.getType();
                        HashMap<String, String> otherParam = new Gson().fromJson(notificationModels.get(position).getOtherPara(), type);
                        for (Map.Entry<String, String> entry : otherParam.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        }
                        if (otherParam.containsKey("deep_link")) {
                            String url = otherParam.get("deep_link");
                            try {
                                Uri intentUri = Uri.parse(url);
                                otherParam.putAll(SplashScreen.getQueryString(intentUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        hashMap.putAll(otherParam);
                        new DeepLinkFunction(hashMap, mActivity);

                        if (otherParam.containsKey("deep_link")) {
                            if (otherParam.containsKey("deep_link") && otherParam.containsKey("type") && otherParam.get("type").equals("catalog") && otherParam.containsKey("id")) {

                            } else {
                                mActivity.finish();
                            }

                        }
                    }

                }
            }
        });

        if (notificationModels.get(position).getTitle() != null
                && notificationModels.get(position).getTitle().isEmpty()
                && notificationModels.get(position).getMessage() != null
                && notificationModels.get(position).getMessage().isEmpty()) {
            view2.getLinear_root().setVisibility(View.GONE);
            view2.itemView.setVisibility(View.GONE);
            view2.itemView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            view2.getLinear_root().setVisibility(View.VISIBLE);
            view2.itemView.setVisibility(View.VISIBLE);
        }

    }

    private void configureSimpleViewholder(SimpleViewholder view1, final int position) {

        view1.getTitle().setText(Html.fromHtml(notificationModels.get(position).getTitle()));
        view1.getMessage().setText(Html.fromHtml(notificationModels.get(position).getMessage()));
        if (!notificationModels.get(position).getRead()) {
            view1.getBadge_item().setVisibility(View.VISIBLE);
        } else {
            view1.getBadge_item().setVisibility(View.GONE);
        }
        view1.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (notificationModels.get(position).getTitle() != null) {
                    Application_Singleton.trackEvent("Notification", "Open", notificationModels.get(position).getTitle());
                }
                HashMap<String, String> hashMap = new HashMap<>();


                if (notificationModels.get(position).getPush_type().equals("buyer")) {
                    hashMap.put("type", "supplier");
                    hashMap.put("id", notificationModels.get(position).getTable_id());
                }
                if (notificationModels.get(position).getPush_type().equals("supplier")) {
                    hashMap.put("type", "buyer");
                    hashMap.put("id", notificationModels.get(position).getTable_id());
                }

                if (notificationModels.get(position).getPush_type().equals("supplier_enquiry")) {
                    hashMap.put("type", "received_enquiries");
                    hashMap.put("id", notificationModels.get(position).getTable_id());

                }

                if (notificationModels.get(position).getPush_type().equals("buyer_enquiry")) {
                    hashMap.put("type", "sent_enquiries");

                }

                if (notificationModels.get(position).getPush_type().equals("credit_reference_buyer_requested")) {
                    hashMap.put("type", "credit_reference_buyer_requested");
                    hashMap.put("id", notificationModels.get(position).getTable_id());
                }

                if (notificationModels.get(position).getPush_type().equals("promotional")) {
                    if (notificationModels.get(position).getTitle().contains("Sales Order")) {
                        hashMap.put("type", "sales");
                        hashMap.put("id", notificationModels.get(position).getTable_id());
                    } else if (notificationModels.get(position).getTitle().contains("Purchase Order")) {
                        hashMap.put("type", "purchase");
                        hashMap.put("id", notificationModels.get(position).getTable_id());
                    } else {
                        if (notificationModels.get(position).getOtherPara() != null) {
                            Type type = new TypeToken<HashMap<String, String>>() {
                            }.getType();
                            HashMap<String, String> otherParam = new Gson().fromJson(notificationModels.get(position).getOtherPara(), type);
                            for (Map.Entry<String, String> entry : otherParam.entrySet()) {
                                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            }
                            if (otherParam.containsKey("deep_link")) {
                                String url = otherParam.get("deep_link");
                                try {
                                    Uri intentUri = Uri.parse(url);
                                    otherParam.putAll(SplashScreen.getQueryString(intentUri));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            hashMap.putAll(otherParam);

                            new DeepLinkFunction(hashMap, mActivity);
                            if (otherParam.containsKey("deep_link")) {
                                if (otherParam.containsKey("deep_link") && otherParam.containsKey("type") && otherParam.get("type").equals("catalog") && otherParam.containsKey("id")) {

                                } else {
                                    mActivity.finish();
                                }

                            }
                            return;
                        } else {
                            hashMap.put("type", "promotional");
                            hashMap.put("page", notificationModels.get(position).getMessage());
                        }

                    }
                }

                if (notificationModels.get(position).getPush_type().equals("update")) {
                    hashMap.put("type", "update");
                }

                if (notificationModels.get(position).getPush_type().equals("catalog")) {
                    hashMap.put("type", "catalog");
                    if (notificationModels.get(position).getTable_id() != null
                            && !notificationModels.get(position).getTable_id().isEmpty()) {
                        hashMap.put("id", notificationModels.get(position).getTable_id());
                    }

                    if (notificationModels.get(position).getOtherPara() != null) {
                        Type type = new TypeToken<HashMap<String, String>>() {
                        }.getType();
                        HashMap<String, String> otherParam = new Gson().fromJson(notificationModels.get(position).getOtherPara(), type);
                        for (Map.Entry<String, String> entry : otherParam.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        }
                        if (otherParam.containsKey("deep_link")) {
                            String url = otherParam.get("deep_link");
                            try {
                                Uri intentUri = Uri.parse(url);
                                otherParam.putAll(SplashScreen.getQueryString(intentUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        hashMap.putAll(otherParam);
                        hashMap.put("from_notification", "true");
                    }
                }

                if (notificationModels.get(position).getPush_type().equals("catalogwise-enquiry-supplier")) {
                    hashMap.put("type", "catalogwise-enquiry-supplier");
                    hashMap.put("table_id", notificationModels.get(position).getTable_id());
                    hashMap.put("id", notificationModels.get(position).getTable_id());
                }

                if (notificationModels.get(position).getPush_type() != null) {
                    sendNotificationClickedAnalytics(notificationModels.get(position).getPush_type(), notificationModels.get(position).getOtherPara() != null ? notificationModels.get(position).getOtherPara() : null, notificationModels.get(position).getTitle(), notificationModels.get(position).getMessage());
                }
                new DeepLinkFunction(hashMap, mActivity);
                LocalService.notificationCounter.onNext(0);
            }


        });

        if (notificationModels.get(position).getTitle() != null
                && notificationModels.get(position).getTitle().isEmpty()
                && notificationModels.get(position).getMessage() != null
                && notificationModels.get(position).getMessage().isEmpty()) {
            view1.getLinear_root().setVisibility(View.GONE);
            view1.itemView.setVisibility(View.GONE);
            view1.itemView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            view1.getLinear_root().setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationModels.get(position).getImage().equals("")) {
            return SIMPLE;
        } else if (notificationModels.get(position).getTitle().contains("Sales Order")) {
            return SIMPLE;
        } else if (notificationModels.get(position).getPush_type().equals("promotional")) {
            if (notificationModels.get(position).getImage() != null && !notificationModels.get(position).getImage().equals("")) {
                return IMAGE;
            } else {
                return SIMPLE;
            }
        } else if (notificationModels.get(position).getPush_type().equals("update")) {
            return SIMPLE;
        } else {
            return IMAGE;
        }
    }


    @Override
    public int getItemCount() {
        return notificationModels.size();
    }


    /// ######## Send Notification Analyses  ############

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

    public void sendNotificationClickedAnalytics(String type, String otherPara, String title, String message) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Notification_Clicked");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", "Notification List");
        if (checkNotificationType(type, otherPara, title, message).size() > 0) {
            prop.putAll(checkNotificationType(type, otherPara, title, message));
        } else {
            prop.put("type", "other");
        }
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mActivity, wishbookEvent);
    }
}
