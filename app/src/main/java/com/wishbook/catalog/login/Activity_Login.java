package com.wishbook.catalog.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatCallbackStatus;
import com.freshchat.consumer.sdk.UnreadCountCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BaseActivity;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;

import java.util.HashMap;


/**
 * Login Screen, it's contains two step
 * contains Fragment_Login_Version2 and Fragment_VerifyOtp
 * override onBackPressed to maintain back-stack
 */
public class Activity_Login extends BaseActivity {
    TextView badgeTextView;
    FloatingActionButton support_chat_fab;
    private static final int REQUESTPERMISSION = 345;
    private OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.adjustFontScale(getResources().getConfiguration());
        StaticFunctions.initializeAppsee();

        //AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        //Log.e(Application_Singleton.SMSTAG, "onCreate: APP Hash"+ appSignatureHashHelper.getAppSignatures());

        setContentView(R.layout.activity_login);
        onBackPressedListener = null;
        badgeTextView = (TextView) findViewById(R.id.badge_textview);
        support_chat_fab = (FloatingActionButton) findViewById(R.id.support_chat_fab);

        support_chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Freshchat.showConversations(getApplicationContext());
            }
        });

        Application_Singleton.unfortunateLogoutCalledCount = 0;
        Application_Singleton.unfortunateLogoutCalled = false;
        Application_Singleton.logoutCalled = false;

        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            new MaterialDialog.Builder(Activity_Login.this)
                    .title("Oops!!")
                    .content("Your login has failed. Please Re-Login")
                    .positiveText("Ok")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("isGuestUserLogin", false);
        if (getIntent() != null && getIntent().getBooleanExtra("isGuestUserLogin", false)) {
            bundle.putBoolean("isGuestUserLogin", true);
        }

        /*Fragment_login fragment_login = new Fragment_login();
        getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_login).commit();*/


        Fragment_Login_Version2 fragment_login = new Fragment_Login_Version2();
        fragment_login.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_login).commit();

      /*  Fragment_VerifyOtp fragment_login = new Fragment_VerifyOtp();
        getSupportFragmentManager().beginTransaction().replace(R.id.logincontainer, fragment_login).commit();*/
        //checkPermissions();

        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Login_screen");
        HashMap<String, String> prop = new HashMap<>();
        if (getIntent().getStringExtra("from") != null) {
            prop.put("source", getIntent().getStringExtra("from"));
        } else {
            prop.put("source", "onboarding");
        }
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(this, wishbookEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Freshchat.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int unreadCount) {
                if (unreadCount > 0) {
                    badgeTextView.setVisibility(View.VISIBLE);
                    badgeTextView.setText(Integer.toString(unreadCount));
                } else {
                    badgeTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTPERMISSION: {
                if (grantResults.length > 0) {

                } else {
                    Toast.makeText(this, "No Permissions Granted !", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TAG", "onActivityResult: Activity Login  request code" + requestCode + "\n Result Code==>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public interface OnBackPressedListener {
        public void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (Activity_Login.this.onBackPressedListener != null) {
            onBackPressedListener.doBack();
            return;
        }
        super.onBackPressed();
    }
}
