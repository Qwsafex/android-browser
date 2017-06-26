package ru.spbau.shevchenko.browser.tabs;

import android.graphics.Bitmap;

public class TabHeader {
    private final int id;
    private final String title;
    private final Bitmap icon;
    private final Bitmap screencapture;

    public TabHeader(int id, String title, Bitmap icon, Bitmap screencapture) {
        this.title = title;
        this.id = id;
        this.icon = icon;
        this.screencapture = screencapture;
    }

    @SuppressWarnings("WeakerAccess")
    public String getTitle() {
        return title;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("WeakerAccess")
    public Bitmap getScreencapture() {
        return screencapture;
    }

}
