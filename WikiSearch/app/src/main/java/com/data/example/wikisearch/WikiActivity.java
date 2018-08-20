package com.data.example.wikisearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

public class WikiActivity extends Activity {

  public static final String URL    = "url";
  public static final String TITLE  = "title";

  private static final String TAG   = "WikiActivity";

  private WebView mWebView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "creating wiki activity");
    setContentView(R.layout.activity_wiki);

    Bundle extras = getIntent().getExtras();
    if (extras == null) return;

    mWebView              = findViewById(R.id.web_view);
    WebSettings settings  = mWebView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setJavaScriptCanOpenWindowsAutomatically(true);
    settings.setSupportMultipleWindows(true);

    mWebView.setWebViewClient(new HelloWebViewClient());
    mWebView.loadUrl(extras.getString(URL));
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
          if (mWebView.canGoBack()) {
            mWebView.goBack();
          } else {
            finish();
          }
          return true;
      }

    }
    return super.onKeyDown(keyCode, event);
  }

  private class HelloWebViewClient extends WebViewClient {

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

      return false;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {

      return shouldOverrideUrlLoading(view, url.getUrl().toString());
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      startSpinner();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      stopSpinner();
    }

    private void stopSpinner() {

      ProgressBar progressBar = findViewById(R.id.web_view_spinner);
      progressBar.setVisibility(View.GONE);
    }

    private void startSpinner() {

      ProgressBar progressBar = findViewById(R.id.web_view_spinner);
      progressBar.setVisibility(View.VISIBLE);
    }
  }
}
