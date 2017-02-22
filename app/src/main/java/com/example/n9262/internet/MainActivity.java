package com.example.n9262.internet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl("www.baidu.com");
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    //finished
                }else {
                    //loading...
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
