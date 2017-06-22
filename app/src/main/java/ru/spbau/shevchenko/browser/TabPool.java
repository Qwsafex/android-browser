package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.webkit.WebView;

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
            view = new WebView(context);
        }
        view.loadUrl(url);
        openTabs.add(view);
        return view;
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
