package ru.spbau.shevchenko.browser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private static final int TAB_POOL_SIZE = 5;
    private static final String PROTOCOL_SEPARATOR = "://";
    private static final String DEFAULT_PROTOCOL = "http://";
    TabPool tabPool;
    WebView activeTab = null;
    private UrlField urlField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String debugTag = "onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabPool = new TabPool(TAB_POOL_SIZE);

        Log.d(debugTag, "begin edit text set up");
        urlField = (UrlField) findViewById(R.id.url_field);
        urlField.setOnEditorActionListener(this);
        urlField.setOnDoneListener();
        String someStrings[] = {"https://meduza.io", "https://google.com"};
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
                ViewGroup webViewContainer = (ViewGroup) findViewById(R.id.web_view_layout);
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
}
