package ru.spbau.shevchenko.browser.tabs;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class TabHeader implements Parcelable{
    public static final Parcelable.Creator<TabHeader> CREATOR
            = new Parcelable.Creator<TabHeader>() {
        public TabHeader createFromParcel(Parcel in) {
            return new TabHeader(in.readInt(), in.readString(),
                    (Bitmap) in.readParcelable(Bitmap.class.getClassLoader()),
                    (Bitmap) in.readParcelable(Bitmap.class.getClassLoader()));
        }

        public TabHeader[] newArray(int size) {
            return new TabHeader[size];
        }
    };

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

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Bitmap getScreencapture() {
        return screencapture;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeParcelable(icon, 0);
        dest.writeParcelable(screencapture, 0);
    }

    public Bitmap getIcon() {
        return icon;
    }
}
