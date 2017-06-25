package ru.spbau.shevchenko.browser.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ru.spbau.shevchenko.browser.Handler;
import ru.spbau.shevchenko.browser.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class TabAdapter extends BaseAdapter {
    private final ArrayList<TabHeader> tabHeaders;
    private final Context context;
    private final Handler<Integer> closeHandler;

    TabAdapter(Context context, ArrayList<TabHeader> tabHeaders, Handler<Integer> closeHandler) {
        this.tabHeaders = tabHeaders;
        this.context = context;
        this.closeHandler = closeHandler;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = messageInflater.inflate(R.layout.tab_item, null);
        }
        ((TextView) convertView.findViewById(R.id.tab_url)).setText(tabHeaders.get(position).getUrl());
        Button closeTabButton = (Button) convertView.findViewById(R.id.close_tab_button);
        closeTabButton.setTag(position);
        closeTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeHandler.handle(tabHeaders.get(position).getId());
                tabHeaders.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
