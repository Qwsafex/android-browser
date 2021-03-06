package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.shevchenko.browser.tabs.DefaultTabFactory;
import ru.spbau.shevchenko.browser.tabs.Tab;
import ru.spbau.shevchenko.browser.tabs.TabHeader;
import ru.spbau.shevchenko.browser.tabs.TabPool;
import ru.spbau.shevchenko.browser.tabs.TabSelectorActivity;

public class MainActivity extends AppCompatActivity implements Browser, View.OnClickListener {
    public static final String TAB_ID_KEY = "tab_id";
    public static final String NEW_TAB_EXTRA_KEY = "new_tab";
    public static final String CLOSED_TABS_KEY = "closed_tabs";

    private static final int TAB_SELECTOR_RESULT_CODE = 0;
    private static final int MAX_SCREENCAPTURE_HEIGHT = 200;
    private static final long PROGRESS_UPDATE_DELAY = 300;

    private Handler handler = new Handler();

    private TabPool tabPool;
    private Tab activeTab = null;
    private YandexAutoCompleteTextView urlField;
    private ViewGroup webViewContainer;
    private ImageButton refreshButton;
    private ProgressBar loadProgressBar;
    private Runnable displayProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (activeTab != null) {
                int progress = activeTab.getProgress();
                if (progress != 100) {
                    loadProgressBar.setVisibility(View.VISIBLE);
                    loadProgressBar.setProgress(progress);
                } else {
                    loadProgressBar.setVisibility(View.GONE);
                }
            }
            handler.postDelayed(displayProgressRunnable, PROGRESS_UPDATE_DELAY);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabPool = new TabPool(new DefaultTabFactory(this));

        webViewContainer = (ViewGroup) findViewById(R.id.web_view_layout);

        urlField = (YandexAutoCompleteTextView) findViewById(R.id.url_field);
        urlField.setOnEditorActionListener(urlFieldEditListener);
        urlField.setOnItemClickListener(urlFieldItemClickListener);

        findViewById(R.id.tab_selector_button).setOnClickListener(this);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(this);

        loadProgressBar = (ProgressBar) findViewById(R.id.page_loading_progress);
    }


    @Override
    public void onBackPressed() {
        if (activeTab != null && activeTab.getUrl() != null) {
            activeTab.goBack();
        }
    }

    @Override
    public void onPageStarted(String url) {
        if (activeTab != null) {
            activeTab.invalidateCachedSnapshot();
        }
        urlField.setTextProgrammatically(url);
        urlField.dismissDropDown();
        refreshButton.setVisibility(View.VISIBLE);
        // Hide keyboard
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_selector_button: {
                List<TabHeader> tabHeaders = new ArrayList<>();
                for (int i = 0; i < tabPool.getAll().size(); i++) {
                    Tab tab = tabPool.getAll().get(i);
                    String tabTitle = tab.getUrl() == null ? "Empty tab" : tab.getTitle();
                    Bitmap tabScreencapture = tab.getBitmap();
                    Bitmap icon = tab.getFavicon();
                    if (tab.getUrl() == null) {
                        icon = null;
                        tabScreencapture = null;
                    }
                    if (tabScreencapture != null) {
                        int dstWidth = (int) ((1.0 * MAX_SCREENCAPTURE_HEIGHT / tabScreencapture.getHeight())
                                * tabScreencapture.getWidth());
                        tabScreencapture = Bitmap.createScaledBitmap(tabScreencapture, dstWidth,
                                MAX_SCREENCAPTURE_HEIGHT, false);
                    }
                    tabHeaders.add(new TabHeader(i, tabTitle, icon, tabScreencapture));
                }
                TabSelectorActivity.startActivityForResult(this, tabHeaders, TAB_SELECTOR_RESULT_CODE);
                break;
            }
            case R.id.refresh_button: {
                if (activeTab != null) {
                    activeTab.reload();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != TAB_SELECTOR_RESULT_CODE) {
            return;
        }

        ArrayList<Integer> closedTabs = data.getIntegerArrayListExtra(CLOSED_TABS_KEY);
        if (closedTabs != null) {
            tabPool.closeAll(closedTabs);
            if (!tabPool.contains(activeTab)) {
                // If active tab was closed, set first open as active
                if (!tabPool.getAll().isEmpty()) {
                    setActiveTab(tabPool.getAll().get(0));
                } else {
                    setActiveTab(null);
                }
            }
        }
        if (resultCode == RESULT_OK) {
            boolean newTabCreated = data.getBooleanExtra(NEW_TAB_EXTRA_KEY, false);
            Tab newTab;
            if (newTabCreated) { // user created new tab
                newTab = tabPool.add("", this);
            } else { // user selected one of old tabs
                int tab_id = data.getIntExtra(TAB_ID_KEY, 0);
                newTab = tabPool.getAll().get(tab_id);
            }
            setActiveTab(newTab);
        }

        ((Button) findViewById(R.id.tab_selector_button)).setText(String.valueOf(tabPool.getAll().size()));
    }

    private void setActiveTab(Tab newActiveTab) {
        if (activeTab == newActiveTab) {
            return;
        }
        if (newActiveTab == null || newActiveTab.getUrl() == null) {
            refreshButton.setVisibility(View.GONE);
        } else {
            refreshButton.setVisibility(View.VISIBLE);
        }
        // remove old CustomWebView
        webViewContainer.removeAllViews();
        activeTab = newActiveTab;
        if (newActiveTab == null) {
            urlField.setTextProgrammatically("");
            return;
        }
        webViewContainer.addView(activeTab);
        urlField.setTextProgrammatically(activeTab.getUrl());
    }

    private void loadPage(String url) {
        if (url.isEmpty()) {
            return;
        }

        url = URLUtils.cleanupUrl(url);
        if (activeTab == null) {
            ((Button) findViewById(R.id.tab_selector_button)).setText("1");
            activeTab = tabPool.add(url, this);
            webViewContainer = (ViewGroup) findViewById(R.id.web_view_layout);
            webViewContainer.addView(activeTab);
            refreshButton.setVisibility(View.VISIBLE);
        } else {
            activeTab.loadUrl(url);
        }
        handler.postDelayed(displayProgressRunnable, PROGRESS_UPDATE_DELAY);
    }

    private TextView.OnEditorActionListener urlFieldEditListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadPage(v.getText().toString());
            }
            return false;
        }
    };
    private AdapterView.OnItemClickListener urlFieldItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            urlField.dismissDropDown();
            String url = (String) parent.getItemAtPosition(position);
            loadPage(url);
        }
    };

}
