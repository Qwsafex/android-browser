package ru.spbau.shevchenko.browser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private static final int TAB_POOL_SIZE = 5;
    TabPool tabPool;
    WebView activeTab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabPool = new TabPool(TAB_POOL_SIZE);

        EditText urlField = (EditText) findViewById(R.id.url_field);
        urlField.setOnEditorActionListener(this);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (activeTab == null) {
                activeTab = tabPool.add(v.getText().toString(), this);
                ViewGroup webViewContainer = (ViewGroup) findViewById(R.id.web_view_layout);
                webViewContainer.addView(activeTab);
            }
            else {
                activeTab.loadUrl(v.getText().toString());
            }
        }
        return false;
    }
}
