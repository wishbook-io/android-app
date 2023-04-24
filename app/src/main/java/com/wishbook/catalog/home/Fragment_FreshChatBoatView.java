package com.wishbook.catalog.home;

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
import android.widget.Toast;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_FreshChatBoatView extends GATrackedFragment {

    @BindView(R.id.webview)
    WebView webView;


    View view;
    String from = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_freshchat, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initLoadWebView();
        return view;
    }


    public void initView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);


    }

    private void initLoadWebView() {

        if(getArguments()!=null && getArguments().getString("url")!=null) {
            webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
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
                    if(getActivity() instanceof OpenContainer) {
                        ((OpenContainer) getActivity()).toolbar.setVisibility(View.VISIBLE);
                    }
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
            });
            showProgress();
            webView.loadUrl(getArguments().getString("url"));
        }

       // webView.loadUrl("file:///android_asset/freshboat.html"+"?sessionId="+UserInfo.getInstance(getActivity()).getKey()+"&userId="+UserInfo.getInstance(getActivity()).getUserId()+"&name="+UserInfo.getInstance(getActivity()).getUserName());
       // webView.loadUrl("file:///android_asset/bot.html" + "?sessionId="+UserInfo.getInstance(getActivity()).getKey()+"&userId="+UserInfo.getInstance(getActivity()).getUserId()+"&name="+UserInfo.getInstance(getActivity()).getUserName());
        //webView.loadUrl("file:///android_asset/fresh_chat_web.html");



    }


}
