package ru.spbau.shevchenko.browser.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import ru.spbau.shevchenko.browser.Handler;
import ru.spbau.shevchenko.browser.MainActivity;
import ru.spbau.shevchenko.browser.R;

public class TabSelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ArrayList<Integer> closedTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_selector);

        closedTabs = new ArrayList<>();

        ArrayList<Parcelable> parcelableTabs = getIntent().getParcelableArrayListExtra(MainActivity.TAB_HEADERS_KEY);
        ArrayList<TabHeader> tabHeaders = new ArrayList<>();
        for (Parcelable parcelledTab : parcelableTabs) {
            tabHeaders.add((TabHeader) parcelledTab);
        }
        ListView tabList = (ListView) findViewById(R.id.tab_list);
        tabList.setAdapter(new TabAdapter(this, tabHeaders, tabCloseHandler));
        tabList.setOnItemClickListener(this);

        Button addTabButton = (Button) findViewById(R.id.add_tab_button);
        addTabButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        data.putExtra(MainActivity.TAB_ID_KEY, position);
        finish(data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_tab_button) {
            Intent data = new Intent();
            data.putExtra(MainActivity.NEW_TAB_EXTRA_KEY, true);
            finish(data);
        }
    }

    private void finish(Intent data) {
        finish(RESULT_OK, data);
    }


    @Override
    public void onBackPressed() {
        finish(RESULT_CANCELED, new Intent());
    }

    private void finish(int resultCode, Intent data) {
        data.putIntegerArrayListExtra(MainActivity.CLOSED_TABS_KEY, closedTabs);
        setResult(resultCode, data);
        finish();
    }

    private Handler<Integer> tabCloseHandler = new Handler<Integer>() {
        @Override
        public void handle(Integer id) {
            closedTabs.add(id);
        }
    };
}
