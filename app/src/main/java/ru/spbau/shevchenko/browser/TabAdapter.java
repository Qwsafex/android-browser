package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class TabAdapter extends BaseAdapter {
    private final ArrayList<TabHeader> tabHeaders;
    private final Context context;

    public TabAdapter(Context context, ArrayList<TabHeader> tabHeaders) {
        this.tabHeaders = tabHeaders;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabHeaders.size();
    }

    @Override
    public Object getItem(int position) {
        return tabHeaders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = messageInflater.inflate(R.layout.tab_item, null);
        }
        ((TextView) convertView.findViewById(R.id.tab_url)).setText(tabHeaders.get(position).getUrl());

        return convertView;
    }
}
