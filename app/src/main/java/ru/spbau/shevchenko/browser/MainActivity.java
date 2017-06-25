package ru.spbau.shevchenko.browser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, Browser, View.OnClickListener {
    public static final String TAB_ID_KEY = "tab_id";
    public static final String TAB_HEADERS_KEY = "tab_headers";
    public static final String NEW_TAB_EXTRA_KEY = "new_tab";
    public static final String CLOSED_TABS_KEY = "closed_tabs";

    private static final int TAB_POOL_SIZE = 5;
    private static final String PROTOCOL_SEPARATOR = "://";
    private static final String DEFAULT_PROTOCOL = "http://";
    private static final int STUB_CODE = 0;
    private static final int MAX_TAB_HEADER_LENGTH = 20;
    TabPool tabPool;
    WebView activeTab = null;
    private UrlField urlField;
    private ViewGroup webViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String debugTag = "onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabPool = new TabPool(this, TAB_POOL_SIZE);

        Log.d(debugTag, "begin edit text set up");
        urlField = (UrlField) findViewById(R.id.url_field);
        urlField.setOnEditorActionListener(this);
        urlField.setOnDoneListener();

        Button tabSelectorButton = (Button) findViewById(R.id.tab_selector_button);
        tabSelectorButton.setOnClickListener(this);

        Log.d(debugTag, "finish edit text set up");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        final String debugTag = "onEdAct";
        Log.d(debugTag, "entering");
        Log.d(debugTag, String.valueOf(actionId));
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String url = v.getText().toString();
            if (!url.contains(PROTOCOL_SEPARATOR)) {
                url = DEFAULT_PROTOCOL + url;
            }
            Log.d(debugTag, "handling DONE");
            if (activeTab == null) {
                ((Button) findViewById(R.id.tab_selector_button)).setText("1");
                Log.d(debugTag, "activeTab is null");
                activeTab = tabPool.add(url, this);
                webViewContainer = (ViewGroup) findViewById(R.id.web_view_layout);
                webViewContainer.addView(activeTab);
                Log.d(debugTag, "added activeTab to container");
            }
            else {
                Log.d(debugTag, "loading new URL to activeTab");
                activeTab.loadUrl(url);
            }
        }
        return false;
    }

    @Override
    public void onPageStarted(String url) {
        urlField.setText(url);
        urlField.dismissDropDown();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tab_selector_button) {
            Intent intent = new Intent(this, TabSelectorActivity.class);
            ArrayList<TabHeader>  tabHeaders = new ArrayList<>();
            int i = 0;
            for (WebView tab : tabPool.getAll()) {
                String tabHeader = tab.getUrl() == null ? "Empty tab" : tab.getTitle();
                if (tabHeader.length() > MAX_TAB_HEADER_LENGTH) {
                    tabHeader = tabHeader.substring(0, MAX_TAB_HEADER_LENGTH) + "...";
                }
                tabHeaders.add(new TabHeader(tabHeader, i));
                i++;
            }
            intent.putParcelableArrayListExtra(TAB_HEADERS_KEY, tabHeaders);
            startActivityForResult(intent, STUB_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String debugTag = "onActRes";
        Log.d(debugTag, "start");
        ArrayList<Integer> closedTabs = data.getIntegerArrayListExtra(CLOSED_TABS_KEY);
        if (closedTabs != null) {
            Log.d(debugTag, "Closed tabs" + closedTabs);
            tabPool.removeAll(closedTabs);
            if (!tabPool.contains(activeTab)) {
                if (!tabPool.getAll().isEmpty()) {
                    setActiveTab(tabPool.getAll().get(0));
                }
                else {
                    setActiveTab(null);
                }
            }
        }
        if (resultCode == RESULT_OK) {
            Log.d(debugTag, "RESULT_OK");
            boolean newTabCreated = data.getBooleanExtra(NEW_TAB_EXTRA_KEY, false);
            WebView newTab;
            if (newTabCreated) { // user created new tab
                Log.d(debugTag, "new tab created");
                newTab = tabPool.add("", this);
                urlField.setText("");
            }
            else { // user selected one of old tabs
                int tab_id = data.getIntExtra(TAB_ID_KEY, 0); // TODO: constant
                Log.d(debugTag, "switched to tab " + tab_id);
                newTab = tabPool.getAll().get(tab_id);
                urlField.setText(newTab.getUrl());
            }
            setActiveTab(newTab);
        }

        ((Button) findViewById(R.id.tab_selector_button)).setText(String.valueOf(tabPool.getAll().size()));
    }

    public void setActiveTab(WebView newActiveTab) {
        if (activeTab == newActiveTab) {
            return;
        }
        webViewContainer.removeAllViews();
        activeTab = newActiveTab;
        if (newActiveTab == null) {
            urlField.setText("");
            return;
        }
        webViewContainer.addView(activeTab);
        urlField.setText(activeTab.getUrl());
    }
}
