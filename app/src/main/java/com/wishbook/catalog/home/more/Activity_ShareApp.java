package com.wishbook.catalog.home.more;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_ShareApp extends AppCompatActivity {

    @BindView(R.id.appbar)
    Toolbar toolbar;

    @BindView(R.id.btn_share_sms)
    AppCompatButton btn_share_sms;

    @BindView(R.id.btn_share_whatsapp)
    AppCompatButton btn_share_whatsapp;

    @BindView(R.id.btn_share_other)
    AppCompatButton btn_share_other;


    private Context mContext;
    private String TAG = "BranchLink";


    public enum SHARETYPE {
        WHATSAPP, SMS, OTHER
    }


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.fragment_share_app);
        mContext = Activity_ShareApp.this;
        ButterKnife.bind(this);
        setUpToolbar(mContext, toolbar);
        initView();
    }

    private void initView() {
        btn_share_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Share App", "Click", "SMS");
                shareSMS();

                /*LinkProperties lp = new LinkProperties()
                        .setChannel("All")
                        .setFeature("marketing")
                        .setCampaign("WB-Referral");

                ShareSheetStyle ss = new ShareSheetStyle(Activity_ShareApp.this, "Check this out!", "This stuff is awesome: ")
                        .setCopyUrlStyle(ContextCompat.getDrawable(Activity_ShareApp.this, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                        .setMoreOptionStyle(ContextCompat.getDrawable(Activity_ShareApp.this, android.R.drawable.ic_menu_search), "Show more")
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                        .setAsFullWidthStyle(true)
                        .setSharingTitle("Share With");

                BranchUniversalObject buo = new BranchUniversalObject()
                        .setContentDescription("App Link Share")
                        .setTitle("App Link Share");


                buo.showShareSheet(Activity_ShareApp.this, lp,  ss,  new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {

                        Log.d(TAG, "onShareLinkDialogLaunched");
                    }
                    @Override
                    public void onShareLinkDialogDismissed() {
                    }
                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                        Log.d(TAG, "onLinkShareResponse Link:"+ sharedLink+ " Shared Channel:"+sharedChannel +" Error:"+ error);
                    }
                    @Override
                    public void onChannelSelected(String channelName) {
                        Log.d(TAG, "onChannelSelected Selected Channel:"+channelName );
                    }
                });*/
            }
        });

        btn_share_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Share App", "Click", "WHATSAPP");
                shareFriends(SHARETYPE.WHATSAPP);
            }
        });

        btn_share_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.trackEvent("Share App", "Click", "OTHER");
                shareFriends(SHARETYPE.OTHER);
            }
        });
    }


    public void setUpToolbar(final Context context, Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.intro_text_color));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.intro_text_color), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void shareFriends(SHARETYPE type) {
        try {

            //String shareText = getString(R.string.share_app_msg) + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());

            String shareText = getString(R.string.share_app_msg) + Uri.parse(UserInfo.getInstance(this).getBranchRefLink());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Download Wishbook APP");
            intent.putExtra(Intent.EXTRA_TEXT,shareText);
            if (type.equals(SHARETYPE.WHATSAPP)) {
                intent.setPackage("com.whatsapp");
            }

            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            if (type.equals(SHARETYPE.WHATSAPP))
                Toast.makeText(mContext, "Whatsapp have not been installed", Toast.LENGTH_SHORT).show();
        }

    }

    public void shareSMS() {

        String shareText = getString(R.string.share_app_msg) + Uri.parse(UserInfo.getInstance(this).getBranchRefLink());

        Uri uri = Uri.parse("smsto:");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", shareText);
        it.putExtra(Intent.EXTRA_TEXT,
                shareText);
        startActivity(it);
    }


}
