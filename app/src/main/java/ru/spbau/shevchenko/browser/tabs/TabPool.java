package ru.spbau.shevchenko.browser.tabs;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Object that manages pool of reusable tabs.
 * Task requirements said something about pool capacity, but I see no reason to have it, so there's no maximum amount of tabs in pool.
 */
public class TabPool {
    private final TabFactory tabFactory;
    private LinkedList<Tab> openTabs;
    private LinkedList<Tab> freeTabs;

    public TabPool(TabFactory tabFactory) {
        openTabs = new LinkedList<>();
        freeTabs = new LinkedList<>();
        this.tabFactory = tabFactory;
    }

    /**
     * Puts new {@link Tab} into corresponding pool.
     *
     * If there are tabs in a used-tabs-pool it takes one from there, else it creates new Tab.
     * @param url URL to load in new tab or empty string for empty tab
     * @param context a Context object used to access application assets
     * @return newly opened Tab
     */
    public Tab add(String url, Context context) {
        final String debugTab = "TabPool.add";
        Tab view;
        if (!freeTabs.isEmpty()) {
            Log.d(debugTab, "using old tab");
            view = freeTabs.getFirst();
            freeTabs.removeFirst();
        }
        else { // TODO: use capacity
            Log.d(debugTab, "creating new tab");
            view = tabFactory.createTab(context);
        }
        if (!url.isEmpty()) {
            view.loadUrl(url);
        }
        openTabs.add(view);
        return view;
    }

    /**
     * Returns all opened tabs.
     * @return all opened tabs
     */
    public List<Tab> getAll() {
        return openTabs;
    }

    public void closeAll(ArrayList<Integer> closedTabs) {
        // Indexes will change after remove so sort from biggest to lowest
        Collections.sort(closedTabs, Collections.<Integer>reverseOrder());
        for (int tabId : closedTabs) {
            close(tabId);
        }
    }

    /**
     * Checks if given tab is opened.
     * @param tab tab to check
     * @return true if given tab is in open-tabs-pool and false otherwise
     */
    public boolean contains(Tab tab) {
        return openTabs.contains(tab);
    }

    private boolean close(int tabId) {
        if (openTabs.size() >= tabId) {
            Tab tab = openTabs.get(tabId);
            tab.clear(); // to reset view and release resources
            freeTabs.add(tab);
            openTabs.remove(tabId);
            return true;
        }
        return false;
    }

}
