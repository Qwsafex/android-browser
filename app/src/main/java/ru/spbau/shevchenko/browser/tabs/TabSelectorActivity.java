package ru.spbau.shevchenko.browser.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.shevchenko.browser.Function;
import ru.spbau.shevchenko.browser.MainActivity;
import ru.spbau.shevchenko.browser.R;

public class TabSelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static void startActivityForResult(final Activity activity, final List<TabHeader> tabHeaders, int requestCode) {

        final Intent intent = new Intent(activity, TabSelectorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        DataHolder.setData(tabHeaders);

        activity.startActivityForResult(intent, requestCode);
    }

    private ArrayList<Integer> closedTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_selector);

        closedTabs = new ArrayList<>();

        List<TabHeader> tabHeaders = DataHolder.getData();
        ListView tabList = (ListView) findViewById(R.id.tab_list);
        tabList.setAdapter(new TabAdapter(this, tabHeaders, tabCloseHandler));
        tabList.setOnItemClickListener(this);

        ImageButton addTabButton = (ImageButton) findViewById(R.id.add_tab_button);
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

    @Override
    public void onBackPressed() {
        finish(RESULT_CANCELED, new Intent());
    }

    private void finish(Intent data) {
        finish(RESULT_OK, data);
    }


    /**
     * Puts closedTabs in intent before finishing.
     */
    private void finish(int resultCode, Intent data) {
        data.putIntegerArrayListExtra(MainActivity.CLOSED_TABS_KEY, closedTabs);
        setResult(resultCode, data);
        finish();
    }

    private Function<Integer> tabCloseHandler = new Function<Integer>() {
        @Override
        public void call(Integer id) {
            closedTabs.add(id);
        }
    };

    private enum DataHolder {
        INSTANCE;

        private List<TabHeader> tabHeaders;

        public static void setData(final List<TabHeader> tabHeaders) {
            INSTANCE.tabHeaders = tabHeaders;
        }

        public static List<TabHeader> getData() {
            final List<TabHeader> resultingList = INSTANCE.tabHeaders;
            INSTANCE.tabHeaders = null;
            return resultingList;
        }
    }
}
