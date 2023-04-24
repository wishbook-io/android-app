package com.wishbook.catalog.home.banner;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.WishbookEvent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Common Fragment to show Web-view in app
 * required parameter banner_url - link of url to show in webview
 */
public class Fragment_BannerWebView extends GATrackedFragment {

    @BindView(R.id.banner_webview)
    WebView banner_webview;

    @BindView(R.id.btn_bottom)
    TextView btn_bottom;


    View view;
    String from = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.frag_banner_webview, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initLoadWebView();
        return view;
    }


    public void initView() {
        banner_webview.getSettings().setJavaScriptEnabled(true);
        banner_webview.getSettings().setSupportZoom(true);
        banner_webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //banner_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        banner_webview.getSettings().setDomStorageEnabled(true);
        banner_webview.getSettings().setDatabaseEnabled(true);
        banner_webview.getSettings().setAppCacheEnabled(true);
    }

    private void initLoadWebView() {
        if (getArguments() != null) {
            banner_webview.setWebViewClient(new WebViewClient());
            banner_webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                        return true;
                    } else {
                        try {
                            // Otherwise allow the OS to handle it
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            getActivity().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    hideProgress();
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    try {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error:" + description, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    //super.onReceivedSslError(view, handler, error);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.certificate_error_title));

                    StringBuilder messageBuilder = new StringBuilder();

                    switch (error.getPrimaryError()) {
                        case SslError.SSL_UNTRUSTED:
                            messageBuilder.append(getString(R.string.certificate_error_untrusted));
                            break;
                        case SslError.SSL_EXPIRED:
                            messageBuilder.append(getString(R.string.certificate_error_expired));
                            break;
                        case SslError.SSL_IDMISMATCH:
                            messageBuilder.append(getString(R.string.certificate_error_mismatched));
                            break;
                        case SslError.SSL_NOTYETVALID:
                            messageBuilder.append(getString(R.string.certificate_error_not_yet_valid));
                            break;
                        default:
                            messageBuilder.append(getString(R.string.certificate_error));
                            break;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        messageBuilder.append(System.lineSeparator());

                    } else {
                        messageBuilder.append(System.getProperty("line.separator"));
                    }

                    messageBuilder.append(getString(R.string.certificate_error_continue));
                    builder.setMessage(messageBuilder.toString());

                    builder.setPositiveButton(getString(R.string.prompt_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.promt_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                            getActivity().finish();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }


//                @Override
//                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    handler.proceed();
//                }
            });
            showProgress();
            banner_webview.loadUrl(getArguments().getString("banner_url"));

            if (getArguments().getString("banner_url").contains("resell-and-earn")) {
                from = getArguments().getString("from");
                sendResellEarnScreenEvent();
                btn_bottom.setVisibility(View.VISIBLE);
                btn_bottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("type", "catalog");
                        params.put("ctype", "public");
                        params.put("sell_full_catalog", "false");
                        params.put("ready_to_ship", "true");
                        new DeepLinkFunction(params, getActivity());
                    }
                });
            }
        }
    }

    // ##### Send Screen Analysis ###########
    private void sendResellEarnScreenEvent() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Resell_Earn_Screen");
        HashMap<String, String> param = new HashMap<>();
        param.put("visited", "true");
        param.put("from", from);
        wishbookEvent.setEvent_properties(param);
        new WishbookTracker(getActivity(), wishbookEvent);
    }
}
