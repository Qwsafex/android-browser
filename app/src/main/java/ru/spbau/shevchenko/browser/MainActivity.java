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
    public static final String TAB_ID_EXTRA_KEY = "tab_id";
    public static final String NEW_TAB_EXTRA_KEY = "new_tab";
    public static final String TAB_HEADERS_EXTRA_KEY = "tab_headers";

    private static final int TAB_POOL_SIZE = 5;
    private static final String PROTOCOL_SEPARATOR = "://";
    private static final String DEFAULT_PROTOCOL = "http://";
    private static final int STUB_CODE = 0;
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
                tabHeaders.add(new TabHeader(tab.getUrl(), i));
                i++;
            }
            intent.putParcelableArrayListExtra(TAB_HEADERS_EXTRA_KEY, tabHeaders);
            startActivityForResult(intent, STUB_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String debugTag = "onActRes";
        Log.d(debugTag, "start");
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
                int tab_id = data.getIntExtra(TAB_ID_EXTRA_KEY, 0); // TODO: constant
                Log.d(debugTag, "switched to tab " + tab_id);
                newTab = tabPool.getAll().get(tab_id);
                if (activeTab == newTab) {
                    return;
                }
                urlField.setText(newTab.getUrl());
            }
            activeTab = newTab;
            webViewContainer.removeAllViews();
            webViewContainer.addView(newTab);
        }
    }
}
