package com.wishbook.catalog.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicConversationCreateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicommons.people.channel.Conversation;
import com.freshchat.consumer.sdk.ConversationOptions;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogEnquiry;
import com.wishbook.catalog.home.ApplogicContextBasedActivity;
import com.wishbook.catalog.home.contacts.Fragment_CatalogEnquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OpenContextBasedApplogicChat {

    private Context context;
    private ResponseCatalogEnquiry catalogEnquiry;
    private String conversion_type;
    private Fragment fragment;
    private String first_message;
    public static String BUYERTOSUPPLIER = "BUYERTOSUPPLIER";
    public static String SUPPLIERTOBUYER = "SUPPLIERTOBUYER";

    public static String TAG = OpenContextBasedApplogicChat.class.getSimpleName();

    public OpenContextBasedApplogicChat(Context context, ResponseCatalogEnquiry catalogEnquiry, String conversion_type, Fragment fragment, String first_message) {
        this.context = context;
        this.catalogEnquiry = catalogEnquiry;
        this.conversion_type = conversion_type;
        this.first_message = first_message;
        this.fragment = fragment;
        if(isAllowContextBasedChat(context)) {
            if (catalogEnquiry != null) {
                initChat();
            }
        } else {
            new ChatCallUtils(context, ChatCallUtils.WB_CHAT_TYPE, first_message);
        }

    }

    public OpenContextBasedApplogicChat(Context context, ResponseCatalogEnquiry catalogEnquiry, String conversion_type, Fragment fragment) {
        this.context = context;
        this.catalogEnquiry = catalogEnquiry;
        this.conversion_type = conversion_type;
        this.fragment = fragment;
        if(isAllowContextBasedChat(context)) {
            if (catalogEnquiry != null) {
                initChat();
            }
        } else {
            String msg = "";
            if(catalogEnquiry!=null && catalogEnquiry.getId()!=null)
                 msg = "Enquiry ID:" + catalogEnquiry.getId() +"\n Catalog name: "+catalogEnquiry.getCatalog_title();
            new ChatCallUtils(context, ChatCallUtils.WB_CHAT_TYPE, msg);
        }

    }

    public void initFreshChat(){
        //not in use
        FreshchatMessage msg = new FreshchatMessage();
        msg.setMessage("Hi From User (Test Ignore)");
        msg.setTag("enquiries");


        List<String> freshchat_tags = new ArrayList<>();
        freshchat_tags.add("enquiries");

        ConversationOptions convOptions = new ConversationOptions()
                .filterByTags(freshchat_tags, "Catalog Enquiry");

        Freshchat.showConversations(context, convOptions);
        Freshchat.setConversationBannerMessage(getApplicationContext(), "Banner Message");
        Freshchat.sendMessage(getApplicationContext(), msg);

    }


    public void initChat() {
        ApplozicClient.getInstance(context).setContextBasedChat(true);
        if (conversion_type.equals(SUPPLIERTOBUYER)) {
            try{
                if(catalogEnquiry.getApplogic_conversation_id()==null){
                    supportBackwardSupplierToBuyer();
                    return;
                } else {
                    if(Integer.parseInt(catalogEnquiry.getApplogic_conversation_id()) >0) {
                        Intent intent = new Intent(context, ApplogicContextBasedActivity.class);
                        intent.putExtra("takeOrder", true);
                        if (conversion_type.equals(SUPPLIERTOBUYER)) {
                            intent.putExtra(ConversationUIService.USER_ID, catalogEnquiry.getBuying_company_chat_user());//RECEIVER USERID
                            intent.putExtra(ConversationUIService.DISPLAY_NAME, "Buyer");
                        }
                        intent.putExtra(ConversationUIService.DEFAULT_TEXT, "");
                        intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                        intent.putExtra(ConversationUIService.CONVERSATION_ID, Integer.parseInt(catalogEnquiry.getApplogic_conversation_id()));
                        Log.d("CONVERSATION", "Supplier To Buyer =====>Conversation ID" + catalogEnquiry.getApplogic_conversation_id());
                        intent.putExtra("catalog_detail", catalogEnquiry);
                        intent.putExtra("conversion_type", conversion_type);
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, Fragment_CatalogEnquiry.ENQUIRY_REQUEST_CODE);
                        } else {
                            context.startActivity(intent);
                        }
                    } else {
                        supportBackwardSupplierToBuyer();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // Create Applozic Conversation ID
           final MaterialDialog progressDialog =  StaticFunctions.showProgressDialog(context,"Initialize chat..","Loading",true);
           progressDialog.show();
            ApplozicConversationCreateTask applozicConversationCreateTask = null;
            ApplozicConversationCreateTask.ConversationCreateListener conversationCreateListener = new ApplozicConversationCreateTask.ConversationCreateListener() {
                @Override
                public void onSuccess(Integer conversationId, Context context) {
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    if (!((Activity) context).isFinishing() && context != null) {
                        Log.e(TAG, "onSuccess: " + conversationId);
                        Intent intent = new Intent(context, ApplogicContextBasedActivity.class);
                        intent.putExtra("takeOrder", true);
                        if (conversion_type.equals(SUPPLIERTOBUYER)) {
                            intent.putExtra(ConversationUIService.USER_ID, catalogEnquiry.getBuying_company_chat_user());//RECEIVER USERID
                            if (catalogEnquiry.getBuyerName() != null)
                                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Buyer");
                            else
                                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Buyer");
                        } else {
                            // Buyer To Supplier
                            intent.putExtra(ConversationUIService.USER_ID, catalogEnquiry.getSelling_company_chat_user());//RECEIVER USERID
                            if (catalogEnquiry.getSellerName() != null)
                                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Supplier");
                            else
                                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Supplier");
                            if (first_message != null) {
                                Message message = new Message(catalogEnquiry.getSelling_company_chat_user(), first_message);
                                message.setConversationId(conversationId);

                                new MobiComMessageService(context, MessageIntentService.class).sendCustomMessage(message);
                            }
                        }

                        intent.putExtra(ConversationUIService.DEFAULT_TEXT, "");
                        intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                        intent.putExtra(ConversationUIService.CONVERSATION_ID, conversationId);
                        intent.putExtra("catalog_detail", catalogEnquiry);
                        intent.putExtra("conversion_type", conversion_type);

                        Log.d("CONVERSATION", "Buyer To Supplier =====>Conversation ID"+conversationId);
                        patchApplozicId(catalogEnquiry.getId(), String.valueOf(conversationId));
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, Fragment_CatalogEnquiry.ENQUIRY_REQUEST_CODE);
                        } else {
                            context.startActivity(intent);
                        }
                    }
                }
                @Override
                public void onFailure(Exception e, Context context) {
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            };
            Conversation conversation = buildConversation(catalogEnquiry); //From Step 1
            applozicConversationCreateTask = new ApplozicConversationCreateTask(context, conversationCreateListener, conversation);
            applozicConversationCreateTask.execute((Void) null);
        }


    }


    private Conversation buildConversation(ResponseCatalogEnquiry catalogEnquiry) {
        //Title and subtitles are required if you are enabling the view for particular context.
        /*TopicDetail topic = new TopicDetail();
        topic.setTitle(catalogEnquiry.getCatalog_title());//Your Topic title
        String price_range = catalogEnquiry.getPrice_range();
        if (price_range != null) {
            if (price_range.contains("-")) {
                String[] priceRangeMultiple = price_range.split("-");
                topic.setSubtitle(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc." + " , " + catalogEnquiry.getTotal_products() + " Designs");
            } else {
                String priceRangeSingle = price_range;
                topic.setSubtitle("\u20B9" + priceRangeSingle + "/Pc." + ", " + catalogEnquiry.getTotal_products() + " Designs");
            }
        }
        topic.setLink(catalogEnquiry.getThumbnail());*/

        //Create Conversation.

        Conversation conversation = new Conversation();

        //SET UserId for which you want to launch chat or conversation

        conversation.setTopicId(catalogEnquiry.getId() + 10);
        conversation.setUserId(catalogEnquiry.getBuying_company());
        //conversation.setTopicDetail(topic.getJson());
        Log.e(TAG, "buildConversation: " + catalogEnquiry.toString());
        return conversation;

    }


    private void patchApplozicId(String enquiryID, String appLozicConversationId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "catalog-enquiries", "") + enquiryID + '/';
        ResponseCatalogEnquiry patchEnquiry = new ResponseCatalogEnquiry();
        patchEnquiry.setApplogic_conversation_id(appLozicConversationId);
        HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchEnquiry), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {


            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void supportBackwardSupplierToBuyer() {
        Log.d(TAG, "supportBackwardSupplierToBuyer: ==>");
        final MaterialDialog progressDialog =  StaticFunctions.showProgressDialog(context,"Initialize chat..","Loading",true);
        progressDialog.show();
        ApplozicConversationCreateTask applozicConversationCreateTask = null;
        ApplozicConversationCreateTask.ConversationCreateListener conversationCreateListener = new ApplozicConversationCreateTask.ConversationCreateListener() {
            @Override
            public void onSuccess(Integer conversationId, Context context) {
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (!((Activity) context).isFinishing() && context != null) {
                    Intent intent = new Intent(context, ApplogicContextBasedActivity.class);
                    intent.putExtra("takeOrder", true);
                    if (conversion_type.equals(SUPPLIERTOBUYER)) {
                        intent.putExtra(ConversationUIService.USER_ID, catalogEnquiry.getBuying_company_chat_user());//RECEIVER USERID
                        intent.putExtra(ConversationUIService.DISPLAY_NAME, "Buyer");
                    }
                    intent.putExtra(ConversationUIService.DEFAULT_TEXT, "");
                    intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                    intent.putExtra(ConversationUIService.CONVERSATION_ID, conversationId);
                    intent.putExtra("catalog_detail", catalogEnquiry);
                    intent.putExtra("conversion_type", conversion_type);
                    if (fragment != null) {
                        fragment.startActivityForResult(intent, Fragment_CatalogEnquiry.ENQUIRY_REQUEST_CODE);
                    } else {
                        context.startActivity(intent);
                    }
                    patchApplozicId(catalogEnquiry.getId(), String.valueOf(conversationId));
                }
            }
            @Override
            public void onFailure(Exception e, Context context) {
                e.printStackTrace();
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        };
        Conversation conversation = buildConversation(catalogEnquiry); //From Step 1
        applozicConversationCreateTask = new ApplozicConversationCreateTask(context, conversationCreateListener, conversation);
        applozicConversationCreateTask.execute((Void) null);
    }

    public boolean isAllowContextBasedChat(Context context) {
        if (PrefDatabaseUtils.getConfig(context) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(context), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("CONTEXT_BASED_ENQUIRY_FEATURE_IN_APP")) {
                    try {
                        if(Integer.parseInt(data.get(i).getValue()) == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        return true;
    }

}
