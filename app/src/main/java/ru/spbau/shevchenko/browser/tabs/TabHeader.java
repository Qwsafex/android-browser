package ru.spbau.shevchenko.browser.tabs;

import android.os.Parcel;
import android.os.Parcelable;

public class TabHeader implements Parcelable{
    public static final Parcelable.Creator<TabHeader> CREATOR
            = new Parcelable.Creator<TabHeader>() {
        public TabHeader createFromParcel(Parcel in) {
            return new TabHeader(in.readString(), in.readInt());
        }

        public TabHeader[] newArray(int size) {
            return new TabHeader[size];
        }
    };

    private final String url;
    private final int id;

    public TabHeader(String url, int id) {
        this.url = url;
        this.id = id;
    }

    @SuppressWarnings("WeakerAccess")
    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(id);
    }
}
