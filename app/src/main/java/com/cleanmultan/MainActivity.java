package com.cleanmultan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cleanmultan.Utils.ApiUrl;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar progressbar;
    private BroadcastReceiver connectivityReceiver;
    private LinearLayout layoutNoInternet;
    private ImageView ivRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWebView = findViewById(R.id.webview);
        progressbar = findViewById(R.id.progressbar);
        layoutNoInternet = findViewById(R.id.layoutNoInternet);
        ivRetry = findViewById(R.id.ivRetry);

        connectivityReceiverCheck();
        loadWebsite();
    }

    private void connectivityReceiverCheck() {

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {

                    if (layoutNoInternet.isShown()) {
                        if (IsNetworkConnected()) {
                            layoutNoInternet.setVisibility(View.GONE);
                            mWebView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layoutNoInternet.setVisibility(View.GONE);
                        mWebView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mWebView.setVisibility(View.GONE);
                    layoutNoInternet.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Network not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {

                    if (layoutNoInternet.isShown()) {
                        if (IsNetworkConnected()) {
                            layoutNoInternet.setVisibility(View.GONE);
                            mWebView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layoutNoInternet.setVisibility(View.GONE);
                        mWebView.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutNoInternet.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    Toast.makeText(context, "Network Disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, filter);

    }

    private void loadWebsite() {

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new AppWebViewClients(progressbar));
        mWebView.loadUrl(ApiUrl.BaseUrl);


        //for reloading inside of webView
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Method stub for TODO automatic generation

                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE); // After loading the progress bar disappears
                } else {
                    progressbar.setVisibility(View.VISIBLE); // display progress bar when starting to load web page
                    progressbar.setProgress(newProgress); // Set progress value
                }

            }
        });

    }

    public class AppWebViewClients extends WebViewClient {
        private View progressView;

        public AppWebViewClients(View progressView) {
            this.progressView = progressView;
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }


    private boolean IsNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }
}