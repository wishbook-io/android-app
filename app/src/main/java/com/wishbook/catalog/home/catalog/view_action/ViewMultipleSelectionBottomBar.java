package com.wishbook.catalog.home.catalog.view_action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.SelectSizeMultipleProductBottomSheet;
import com.wishbook.catalog.home.catalog.Fragment_ResellerMultipleProductShare;
import com.wishbook.catalog.home.catalog.share.Fragment_WhatsAppSelection;
import com.wishbook.catalog.home.catalog.social_share.BottomShareDialog;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Whatsappshare,OtherShare,AddToCart
 */
public class ViewMultipleSelectionBottomBar {

    ArrayList<Response_catalogMini> selected_response_catalogmini;
    Context mContext;
    Fragment fragment;
    StaticFunctions.SHARETYPE sharetype;
    TaskCompleteListener taskCompleteListener;
    boolean isShowFbPage = false;

    public ViewMultipleSelectionBottomBar(Context mContext, ArrayList<Response_catalogMini> selected_response_catalogminit, Fragment fragment) {
        this.selected_response_catalogmini = selected_response_catalogminit;
        this.mContext = mContext;
        this.fragment = fragment;

        if (PrefDatabaseUtils.getConfig(mContext) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(mContext), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("SHARE_FB_PAGE_FEATURE_IN_APP")
                        && data.get(i).getValue()!=null
                        && data.get(i).getValue().equalsIgnoreCase("1")) {
                    isShowFbPage = true;
                    break;
                }
            }
        }
    }

    public void whatsappShare() {
        sharetype = StaticFunctions.SHARETYPE.WHATSAPP;
        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(mContext).getCompanyGroupFlag(), CompanyGroupFlag.class);
        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {
            // Open Reseller Share Bottom Sheet
            openResaleMarginDialog(sharetype);
        } else {
            shareType(StaticFunctions.SHARETYPE.WHATSAPP);
        }
        //shareType(StaticFunctions.SHARETYPE.WHATSAPP);
        //copyMultipleProductDetails();

    }

    public void otherShare() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("fb", true);
        bundle.putBoolean("fbPage", isShowFbPage);
        bundle.putBoolean("gallery", true);
        bundle.putBoolean("other", true);
        BottomShareDialog bottomSheetDialog = BottomShareDialog.getInstance(bundle);
        bottomSheetDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Custom Bottom Sheet");

        bottomSheetDialog.setDismissListener(new BottomShareDialog.DismissListener() {
            @Override
            public void onDismiss(StaticFunctions.SHARETYPE type) {
                if(type !=null) {
                    sharetype = type;
                    if(sharetype == StaticFunctions.SHARETYPE.GALLERY) {
                        shareType(sharetype);
                    } else {
                        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(mContext).getCompanyGroupFlag(), CompanyGroupFlag.class);
                        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {
                            // Open Reseller Share Bottom Sheet
                            openResaleMarginDialog(sharetype);
                        } else {
                            shareType(sharetype);
                        }
                    }

                }
            }
        });

    }

    public void shareType(StaticFunctions.SHARETYPE sharetype) {
        if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 0) {
            ProductObj[] productObjs = new ProductObj[selected_response_catalogmini.size()];
            for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                ThumbnailObj thumbnailObj = new ThumbnailObj(selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium());
                productObjs[i] = new ProductObj(selected_response_catalogmini.get(i).getId(), selected_response_catalogmini.get(i).getSingle_piece_price_range());
                productObjs[i].setImage(thumbnailObj);
            }
            downloadShareImage(mContext, productObjs, sharetype, "");
        }
    }

    public void addToCart() {
        if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 0) {
            // differentiate size, and without size products

            ArrayList<Response_catalogMini> withSize = new ArrayList<>();
            ArrayList<Response_catalogMini> withoutSize = new ArrayList<>();
            for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                if (selected_response_catalogmini.get(i).getAvailable_sizes() != null &&
                        !selected_response_catalogmini.get(i).getAvailable_sizes().isEmpty()) {
                    withSize.add(selected_response_catalogmini.get(i));
                } else {
                    withoutSize.add(selected_response_catalogmini.get(i));
                }
            }

            if (withSize != null && withSize.size() > 0) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", withSize);
                SelectSizeMultipleProductBottomSheet bottomSheet = SelectSizeMultipleProductBottomSheet.newInstance(bundle);
                bottomSheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "SELECTSIZE");
                bottomSheet.setMultipleSizeAddToCartListener(new SelectSizeMultipleProductBottomSheet.MultipleSizeAddToCartListener() {
                    @Override
                    public void onSuccess(CartProductModel response) {
                        if(taskCompleteListener!=null) {
                            taskCompleteListener.onSuccessAddToCart(response);
                        }
                    }

                    @Override
                    public void onFailure() {
                        if(taskCompleteListener!=null) {
                            taskCompleteListener.onFailure();
                        }
                    }
                });

            }


            if (withoutSize != null && withoutSize.size() > 0) {
                ArrayList<ProductObj> productObjArrayList = new ArrayList<>();
                for (int i = 0; i < withoutSize.size(); i++) {
                    ProductObj p_temp = new ProductObj(withoutSize.get(i).getId(),
                            withoutSize.get(i).getSingle_piece_price(), "Nan");
                    p_temp.setCatalog_id(withoutSize.get(i).getCatalog_id());
                    productObjArrayList.add(p_temp);
                }
                CartHandler cartHandler = new CartHandler((AppCompatActivity) mContext);
                cartHandler.addMultipleProductToCartSinglePrice(productObjArrayList);
                cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                    @Override
                    public void onSuccess(CartProductModel response) {
                        if(taskCompleteListener!=null) {
                            taskCompleteListener.onSuccessAddToCart(response);
                        }
                    }

                    @Override
                    public void onFailure() {
                        if(taskCompleteListener!=null) {
                            taskCompleteListener.onFailure();
                        }
                    }
                });

            }
        }
    }

    void downloadShareImage(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype, String catalogname) {
        try {
            if (productObjs != null && productObjs.length > 0) {
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP && productObjs.length > 30) {
                    Fragment_WhatsAppSelection frag = new Fragment_WhatsAppSelection();
                    Application_Singleton.CONTAINER_TITLE = "WhatsApp (Pick 30 images to share)";
                    Bundle bundle = new Bundle();
                    bundle.putString("sharetext", "");
                    bundle.putString("type", "whatsapp");
                    frag.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = frag;
                    Intent intent = new Intent(context, OpenContainer.class);
                    intent.getIntExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                    context.startActivity(intent);
                    return;
                }
                try {
                    ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, null,productObjs,
                            sharetype, "", catalogname,
                            true,false);
                    if(sharetype == StaticFunctions.SHARETYPE.GALLERY) {
                        if(taskCompleteListener!=null) {
                            taskCompleteListener.onSuccess();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openResaleMarginDialog(StaticFunctions.SHARETYPE sharetype) {
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("data",selected_response_catalogmini);
        bundle1.putSerializable("share_type",sharetype);


        Fragment_ResellerMultipleProductShare resellerMultipleProductShareBottomSheet = new Fragment_ResellerMultipleProductShare();
        resellerMultipleProductShareBottomSheet.setCallbackListener(new Fragment_ResellerMultipleProductShare.CallbackListener() {
            @Override
            public void onSuccess() {
                if(taskCompleteListener!=null) {
                    taskCompleteListener.onSuccess();
                }
            }

            @Override
            public void onFailure() {

            }
        });
        resellerMultipleProductShareBottomSheet.setArguments(bundle1);
        Application_Singleton.CONTAINER_TITLE = "";
        Application_Singleton.CONTAINERFRAG = resellerMultipleProductShareBottomSheet;
        StaticFunctions.switchActivity((Activity) mContext, OpenContainer.class);
    }

    public interface TaskCompleteListener {
        void onSuccess();

        void onFailure();

        void onSuccessAddToCart(CartProductModel response);
    }

    public void setTaskCompleteListener(TaskCompleteListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
    }

}


