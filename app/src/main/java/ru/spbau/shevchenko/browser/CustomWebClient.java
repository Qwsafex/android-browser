package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebClient extends WebViewClient{
    private final Browser browser;

    public CustomWebClient(Browser browser) {
        this.browser = browser;
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        browser.onPageStarted(url);
        super.onPageStarted(view, url, favicon);
    }
}
