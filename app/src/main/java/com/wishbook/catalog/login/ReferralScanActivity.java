package com.wishbook.catalog.login;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.Result;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReferralScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    @BindView(R.id.appbar)
    Toolbar toolbar;

    @BindView(R.id.txt_referral_ok)
    TextView txt_referral_ok;
    @BindView(R.id.input_mobile_scan)
    EditText input_mobile_scan;

    @BindView(R.id.btn_cancel)
    TextView btn_cancel;
    private ZXingScannerView mScannerView;
    public static int REQUEST_REGISTER_REFERRAL = 900;
    public static int RESPONSE_REGISTER_REFERRAL = 901;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.dialog_referral_scan);
        ButterKnife.bind(this);
        txt_referral_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input_mobile_scan.getText().toString().isEmpty()) {
                    if (input_mobile_scan.getText().toString().length() != 10) {
                        input_mobile_scan.requestFocus();
                        input_mobile_scan.setError("Invalid Mobile Number");
                        return;
                    }

                    if (!input_mobile_scan.getText().toString().isEmpty()) {
                        if (input_mobile_scan.getText().toString().startsWith("6") ||
                                input_mobile_scan.getText().toString().startsWith("7") ||
                                input_mobile_scan.getText().toString().startsWith("8") ||
                                input_mobile_scan.getText().toString().startsWith("9")) {
                        } else {
                            input_mobile_scan.requestFocus();
                            input_mobile_scan.setError("Invalid Mobile Number");
                            return;
                        }
                    }
                    showReferralDialog(input_mobile_scan.getText().toString());
                }
            }
        });
        setUpToolbar(toolbar);
        StaticFunctions.initializeAppsee();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        showReferralDialog(rawResult.getText());
    }

    public void setUpToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Register");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        toolbar.setTitleTextColor(getResources().getColor(R.color.intro_text_color));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.intro_text_color), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();

            }
        });
    }

    public void showReferralDialog(String phonenumber) {
        new MaterialDialog.Builder(ReferralScanActivity.this)
                .content("The number you have entered is "+phonenumber)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Intent intent = new Intent();
                        intent.putExtra("content", input_mobile_scan.getText().toString());
                        setResult(RESPONSE_REGISTER_REFERRAL, intent);
                        dialog.dismiss();
                        finish();
                    }
                })
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if(mScannerView!=null) {
                            mScannerView.setResultHandler(ReferralScanActivity.this);
                            mScannerView.startCamera();
                            mScannerView.setAutoFocus(true);
                        }

                    }
                })
                .show();
    }

}
