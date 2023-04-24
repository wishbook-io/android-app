package com.wishbook.catalog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by root on 22/5/17.
 */

public class WebPrintActivity extends Activity {

    private WebView myWebView;
    private AppCompatButton print;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.web);
        myWebView = (WebView) findViewById(R.id.myWebView);
        print = (AppCompatButton) findViewById(R.id.print);

        myWebView.loadUrl(
                "https://s3-eu-west-1.amazonaws.com/htmlpdfapi.production/free_html5_invoice_templates/example2/index.html");

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    printManager = (PrintManager) WebPrintActivity.this
                            .getSystemService(Context.PRINT_SERVICE);
                    PrintDocumentAdapter printAdapter =
                            myWebView.createPrintDocumentAdapter();

                    String jobName = getString(R.string.app_name) +
                            "Print Test";

                    printManager.print(jobName, printAdapter,
                            new PrintAttributes.Builder().build());
                }


            }
        });

    }
}
