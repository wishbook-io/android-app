package com.wishbook.catalog.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.freshchat.consumer.sdk.ConversationOptions;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatCallUtils {

    private Context context;
    private String type;
    private String msg;
    public static String CHAT_CALL_TYPE = "callwbsupport";
    public static String WB_CHAT_TYPE = "chatwbsupport";

    public ChatCallUtils(Context context, String type, String msg) {
        this.context = context;
        this.type = type;
        this.msg = msg;
        if (type.equals("callwbsupport")) {
            callwbsupport();
        } else if(type.equals("chatwbsupport")){
            chatwbsupport();
        }
    }

    public void callwbsupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(this.context)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chatwbsupport() {
        FreshchatMessage freshchatMessage = new FreshchatMessage();
        freshchatMessage.setMessage(this.msg);
        freshchatMessage.setTag("enquiries");

        List<String> freshchat_tags = new ArrayList<>();
        freshchat_tags.add("enquiries");

        ConversationOptions convOptions = new ConversationOptions()
                .filterByTags(freshchat_tags, "Enquiry");

        Freshchat.showConversations(context,convOptions);
        Freshchat.sendMessage(context, freshchatMessage);
    }
}
