package com.wishbook.catalog.home.catalog.social_share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("ValidFragment")
public class BottomShareDialog  extends BottomSheetDialogFragment  {


    @BindView(R.id.btn_share_wishbook)
    RelativeLayout btn_share_wishbook;

    @BindView(R.id.btn_share_link)
    RelativeLayout btn_share_link;

    @BindView(R.id.btn_share_whatsapp)
    RelativeLayout btn_share_whatsapp;

    @BindView(R.id.btn_save_gallery)
    RelativeLayout btn_save_gallery;

    static boolean flag, flag_business_whatsapp;

    @BindView(R.id.btn_share_facebook)
    RelativeLayout btn_share_facebook;

    @BindView(R.id.btn_share_fbpage)
    RelativeLayout btn_share_fbpage;

    @BindView(R.id.btn_share_other)
    RelativeLayout btn_share_other;

    DismissListener dismissListener;
    @BindView(R.id.btn_share_whatsapp_business)
    RelativeLayout btn_share_whatsapp_business;



    public static BottomShareDialog getInstance(boolean flag, boolean flag2) {
        BottomShareDialog.flag=flag;
        flag_business_whatsapp = flag2;
        return new BottomShareDialog();
    }

    public static BottomShareDialog getInstance(Bundle bundle) {
        BottomShareDialog bottomShareDialog = new BottomShareDialog();
        if(bundle!=null)
            bottomShareDialog.setArguments(bundle);
        return bottomShareDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.share_bottomsheet, container, false);
        ButterKnife.bind(this, view);
        initListener();
        setVisibilityWhatsappBusiness(flag_business_whatsapp);
        setVisibility(flag);
        return view;
    }

    public void initListener(){
        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.FACEBOOK);
                }
            }
        });

        btn_share_wishbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.ONWISHBOOK);
                }
            }
        });

        btn_share_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.WHATSAPP);
                }
            }
        });

        btn_share_whatsapp_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (dismissListener != null) {
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS);
                }
            }
        });

        btn_share_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.OTHER);
                }
            }
        });

        btn_share_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.LINKSHARE);
                }
            }
        });

        btn_save_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.GALLERY);
                }
            }
        });

        btn_share_fbpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(dismissListener!=null){
                    dismissListener.onDismiss(StaticFunctions.SHARETYPE.FBPAGE);
                }
            }
        });
    }

    public interface DismissListener{
        void onDismiss(StaticFunctions.SHARETYPE type);
    }


    public void setDismissListener(DismissListener dismissListener){
        this.dismissListener = dismissListener;
    }


    public void setVisibility(boolean value){
        /**
         * WB-4404 Hide Share on Wishbook
         */

       /* if(value)
            btn_share_wishbook.setVisibility(View.VISIBLE);
        else
            btn_share_wishbook.setVisibility(View.GONE);*/

       btn_share_wishbook.setVisibility(View.GONE);

       if(getArguments()!=null) {
           btn_share_link.setVisibility(View.GONE);
           btn_share_whatsapp.setVisibility(View.GONE);
           btn_share_whatsapp_business.setVisibility(View.GONE);
           btn_share_facebook.setVisibility(View.GONE);
           btn_share_other.setVisibility(View.GONE);
           btn_save_gallery.setVisibility(View.GONE);
           if(getArguments().containsKey("whatsapp")) {
               if(getArguments().getBoolean("whatsapp")){
                   btn_share_whatsapp.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("whatsappb")) {
               if(getArguments().getBoolean("whatsappb")){
                   btn_share_whatsapp_business.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("fb")) {
               if(getArguments().getBoolean("fb")){
                   btn_share_facebook.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("fbPage")) {
               if(getArguments().getBoolean("fbPage")){
                   btn_share_fbpage.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("link")) {
               if(getArguments().getBoolean("link")){
                   btn_share_link.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("other")) {
               if(getArguments().getBoolean("other")){
                   btn_share_other.setVisibility(View.VISIBLE);
               }
           }

           if(getArguments().containsKey("gallery")) {
               if(getArguments().getBoolean("gallery")){
                   btn_save_gallery.setVisibility(View.VISIBLE);
               }
           }
       }
    }

    public void setVisibilityWhatsappBusiness(boolean value) {
        if (value)
            btn_share_whatsapp_business.setVisibility(View.VISIBLE);
        else
            btn_share_whatsapp_business.setVisibility(View.GONE);
    }


}