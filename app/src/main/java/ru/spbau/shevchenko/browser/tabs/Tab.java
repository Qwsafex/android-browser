package ru.spbau.shevchenko.browser.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Adds some useful methods to {@link WebView} and slightly changes some functionality.
 */
public class Tab extends WebView {
    private static final String BLANK_URL = "about:blank";

    public Tab(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
    }

    public Tab(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
    }

    public Tab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDrawingCacheEnabled(true);
    }

    public Bitmap getBitmap() {
        return getDrawingCache();
    }

    /**
     * Returns page URL.
     *
     * If it is "about:blank" returns null.
     * @return URL of page or null
     */
    @Override
    @Nullable
    public String getUrl() {
        String url = super.getUrl();
        if (url == null) {
            return null;
        }
        if (super.getUrl().equals(BLANK_URL)) {
            return null;
        }
        else {
            return super.getUrl();
        }
    }

    /**
     * Releases used resources and clears tab history.
     */
    public void clear() {
        loadUrl(BLANK_URL);
        clearHistory();
    }
}
