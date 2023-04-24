package com.wishbook.catalog.home.more;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.webkit.WebView;

import com.wishbook.catalog.R;

public class Activity_AboutUs_WebView extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_aboutus_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);
        webView = (WebView) findViewById(R.id.webview);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("show").equals("about_us")){
                webView.loadUrl("http://www.wishbook.io/about-us.html");
            }
            if(getIntent().getExtras().getString("show").equals("product_services")){
                webView.loadUrl("http://www.wishbook.io/product-services.html");
            }
            if(getIntent().getExtras().getString("show").equals("privacy_policy")){
                webView.loadUrl("http://www.wishbook.io/privacy-policy.html");
            }
            if(getIntent().getExtras().getString("show").equals("terms_of_use")){
                webView.loadUrl("http://www.wishbook.io/terms-of-use.html");
            }
            if(getIntent().getExtras().getString("show").equals("user_manual")){
                webView.loadUrl("http://www.wishbook.io/user-manual.html");
            }
        }
    }

}
