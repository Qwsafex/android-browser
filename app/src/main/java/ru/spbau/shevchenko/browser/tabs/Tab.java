package ru.spbau.shevchenko.browser.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Adds some useful methods to {@link WebView} and slightly changes some functionality.
 */
public class Tab extends WebView {
    private static final String BLANK_URL = "about:blank";
    private static final int MAX_SNAPSHOT_HEIGHT = 5000;
    private static final int MAX_SNAPSHOT_WIDTH = 5000;
    private Bitmap cachedSnapshot = null;

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

    /**
     * Returns Bitmap of this Tab screencapture.
     * <p>
     * For unknown reasons returned Bitmap may represent Tab's state at some point in past.
     *
     * @return screencapture of this Tab
     */
    public Bitmap getBitmap() {
        if (cachedSnapshot != null) {
            return cachedSnapshot;
        }
        int height = getWidth() > getContentHeight() ? getContentHeight() : getWidth();
        height = Math.min(height, MAX_SNAPSHOT_HEIGHT);
        int width = Math.min(getWidth(), MAX_SNAPSHOT_WIDTH);
        if (width == 0 || height == 0) {
            return null;
        }
        Bitmap snapshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        boolean enableCaching = false;
        if (getProgress() == 100) {
            enableCaching = true;
        }
        Canvas bitmapCanvas = new Canvas(snapshot);
        draw(bitmapCanvas);
        if (enableCaching) {
            cachedSnapshot = snapshot;
        }
        return snapshot;
    }

    /**
     * Returns page URL.
     * <p>
     * If it is "about:blank" returns null.
     *
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
        } else {
            return super.getUrl();
        }
    }

    /**
     * Releases used resources and clears tab history.
     */
    public void clear() {
        loadUrl(BLANK_URL);
        clearHistory();
        invalidateCachedSnapshot();
    }

    public void invalidateCachedSnapshot() {
        cachedSnapshot = null;
    }
}
