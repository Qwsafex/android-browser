package ru.spbau.shevchenko.browser.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.spbau.shevchenko.browser.Function;
import ru.spbau.shevchenko.browser.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class TabAdapter extends BaseAdapter {
    private final List<TabHeader> tabHeaders;
    private final Context context;
    private final Function<Integer> closeHandler;
    private AdapterView.OnItemClickListener itemClickListener;

    TabAdapter(Context context, List<TabHeader> tabHeaders, Function<Integer> closeHandler) {
        this.tabHeaders = tabHeaders;
        this.context = context;
        this.closeHandler = closeHandler;
    }

    void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
        final TabHeader tabHeader = tabHeaders.get(position);
        ((ImageView) convertView.findViewById(R.id.tab_icon)).setImageBitmap(tabHeader.getIcon());

        ImageView screencaptureView = (ImageView) convertView.findViewById(R.id.tab_screencapture);
        if (tabHeader.getScreencapture() != null) {
            screencaptureView.setImageBitmap(tabHeader.getScreencapture());
            screencaptureView.setVisibility(View.VISIBLE);
        } else {
            screencaptureView.setImageBitmap(null);
            screencaptureView.setVisibility(View.GONE);
        }


        final View viewToClick = convertView;
        screencaptureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(null, viewToClick, position, position);
            }
        });
        ((TextView) convertView.findViewById(R.id.tab_url)).setText(tabHeader.getTitle());

        ImageButton closeTabButton = (ImageButton) convertView.findViewById(R.id.close_tab_button);
        closeTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeHandler.call(tabHeader.getId());
                tabHeaders.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
