package ru.spbau.shevchenko.browser;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class TabSelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_selector);

        ArrayList<Parcelable> parcelableTabs = getIntent().getParcelableArrayListExtra(MainActivity.TAB_HEADERS_EXTRA_KEY);
        ArrayList<TabHeader> tabHeaders = new ArrayList<>();
        for (Parcelable parcelledTab : parcelableTabs) {
            tabHeaders.add((TabHeader) parcelledTab);
        }
        ListView tabList = (ListView) findViewById(R.id.tab_list);
        tabList.setAdapter(new TabAdapter(this, tabHeaders));
        tabList.setOnItemClickListener(this);

        Button addTabButton = (Button) findViewById(R.id.add_tab_button);
        addTabButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        data.putExtra(MainActivity.TAB_ID_EXTRA_KEY, position);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        final String debugTag = "tabSelAdd";
        Log.d(debugTag, "start");
        if (v.getId() == R.id.add_tab_button) {
            Intent data = new Intent();
            data.putExtra(MainActivity.NEW_TAB_EXTRA_KEY, true);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
