package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TabPool {
    private final Browser browser;
    private LinkedList<WebView> openTabs;
    private LinkedList<WebView> freeTabs;
    private int capacity;
    public TabPool(Browser browser, int poolSize) {
        this.browser = browser;
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
        if (!url.isEmpty()) {
            view.loadUrl(url);
        }
        openTabs.add(view);
        return view;
    }

    private WebView createDefaultWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new CustomWebClient(browser));
         webView.getSettings().setJavaScriptEnabled(true);
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

    public void removeAll(ArrayList<Integer> closedTabs) {
        // Indexes will change after remove so sort from biggest to lowest
        Collections.sort(closedTabs, Collections.<Integer>reverseOrder());
        for (int tabId : closedTabs) {
            openTabs.remove(tabId);
        }
    }

    public boolean contains(WebView tab) {
        return openTabs.contains(tab);
    }
}
