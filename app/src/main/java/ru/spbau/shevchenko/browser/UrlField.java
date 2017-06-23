package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class UrlField extends AppCompatAutoCompleteTextView {
    private static final String YANDEX_SUGGGEST_URL = "http://suggest.yandex.ru/suggest-ff.cgi?part=";
    private RequestQueue requestQueue = null;

    private TextWatcher textChangedWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("onTextChanged", "entering");
            refreshAutoComplete(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private void refreshAutoComplete(String s) {
        final String debugTag = "refreshAC";

        if (s.length() < getThreshold()) return;

        Log.d(debugTag, "adding request");
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getContext());
        }
        JsonArrayRequest request = new JsonArrayRequest(YANDEX_SUGGGEST_URL + s,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(debugTag, "onResponse");
                        try {
                            JSONArray jsonResponseStrings = response.getJSONArray(1);
                            ArrayList<String> responseStrings = new ArrayList<>();
                            for (int i = 0; i < jsonResponseStrings.length(); i++) {
                                responseStrings.add(jsonResponseStrings.getString(i));
                            }
                            setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, responseStrings));
                        } catch (JSONException e) {
                            // TODO: do smth
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(request);
        /*
        final String words[][] = {{"zero"}, {"one"}, {"two"}, {"aaa_three", "aaa_3", "aaa_drei"}, {"aaaa_four", "aaaa_4", "aaaa_vier"}, {"aaaaa_five", "aaaaa_5", "aaaaa_funf"}};
        if (s.length() > 5)
            return;
        setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, words[s.length()]));
        */
    }

    public UrlField(Context context) {
        super(context);
        addTextChangedListener(textChangedWatcher);
    }

    public UrlField(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textChangedWatcher);
    }
    public void setOnDoneListener() {

    }

    public UrlField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(textChangedWatcher);
    }

}
