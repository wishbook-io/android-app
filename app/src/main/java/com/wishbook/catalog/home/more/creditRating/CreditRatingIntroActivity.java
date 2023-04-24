package com.wishbook.catalog.home.more.creditRating;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditRatingIntroActivity extends AppCompatActivity {

    @BindView(R.id.btn_accept)
    AppCompatButton btn_accept;

    @BindView(R.id.subtitle)
    TextView subtitle;

    @BindView(R.id.txt_credit_terms_condition)
    TextView txt_credit_terms_condition;

    @BindView(R.id.img_credit_banner)
    ImageView img_credit_banner;

    private CreditRatingIntroActivity mContext;

    private String from ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_rating_intro);
        mContext = this;
        ButterKnife.bind(this);
        subtitle.setVisibility(View.INVISIBLE);
        subtitle.setText(Html.fromHtml(getResources().getString(R.string.credit_rating_tutorial_sub_text)));
        if (UserInfo.getInstance(mContext).getLanguage().equals("en")) {
            img_credit_banner.setImageResource(R.drawable.credit_rate_eng);
        } else if (UserInfo.getInstance(mContext).getLanguage().equals("hi")) {
            img_credit_banner.setImageResource(R.drawable.credit_rate_hi);
        }
        initListener();
        initSpannable();


        if(getIntent()!=null && getIntent().getStringExtra("from")!=null) {
            from = getIntent().getStringExtra("from");
        }

    }

    private void initListener() {
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCreditRatingScan fragmentCreditRatingScan = new FragmentCreditRatingScan();
                Bundle bundle = new Bundle();
                bundle.putString("from",from);
                fragmentCreditRatingScan.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "Apply for credit rating";
                Application_Singleton.CONTAINERFRAG = fragmentCreditRatingScan;
                StaticFunctions.switchActivity(mContext, OpenContainer.class);
                finish();
            }
        });
    }


    private void initSpannable() {
        try {
            final String url = "https://www.wishbook.io/credit-tnc";
            SpannableString ss1 = new SpannableString(getResources().getString(R.string.terms_rating).toString());
            ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
            if (UserInfo.getInstance(mContext).getLanguage().equals("en")) {
                ss1.setSpan(clickableSpan1, ss1.toString().indexOf("terms and conditions"), ss1.toString().indexOf("terms and conditions") + 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (UserInfo.getInstance(mContext).getLanguage().equals("hi")) {
                ss1.setSpan(clickableSpan1, ss1.toString().indexOf("नियम"), ss1.toString().indexOf("नियम") + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            txt_credit_terms_condition.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(txt_credit_terms_condition, Linkify.WEB_URLS);
            txt_credit_terms_condition.setVisibility(View.VISIBLE);
            txt_credit_terms_condition.setText(ss1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
