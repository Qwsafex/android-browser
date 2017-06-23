package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TabPool {
    private LinkedList<WebView> openTabs;
    private LinkedList<WebView> freeTabs;
    private int capacity;
    public TabPool(int poolSize) {
        openTabs = new LinkedList<>();
        freeTabs = new LinkedList<>();
        capacity = poolSize;
    }
    public WebView add(String url, Context context) {
        WebView view;
        if (!freeTabs.isEmpty()) {
            view = freeTabs.getFirst();
            freeTabs.removeFirst();
        }
        else { // TODO: use capacity
            view = createDefaultWebView(context);
        }
        view.loadUrl(url);
        openTabs.add(view);
        return view;
    }

    private WebView createDefaultWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient());
        // webView.getSettings().setJavaScriptEnabled(true);
        return webView;
    }

    public List<WebView> getAll() {
        return openTabs;
    }
    public boolean remove(WebView view) {
        if (openTabs.contains(view)) {
            openTabs.removeFirstOccurrence(view);
            freeTabs.add(view);
            return true;
        }
        return false;
    }
}
