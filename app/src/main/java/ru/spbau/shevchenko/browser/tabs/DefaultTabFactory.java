package ru.spbau.shevchenko.browser.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebSettings;

import ru.spbau.shevchenko.browser.Browser;
import ru.spbau.shevchenko.browser.CustomWebClient;

/**
 * Implementation of {@link TabFactory} that creates tabs that use {@link CustomWebClient} and
 * have default parameters (zoom enabled, JS enabled etc.)
 */
public class DefaultTabFactory implements TabFactory {
    private final Browser browser;

    public DefaultTabFactory(Browser browser) {
        this.browser = browser;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public Tab createTab(Context context) {
        Tab webView = new Tab(context);
        webView.setWebViewClient(new CustomWebClient(browser));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        return webView;
    }

}
