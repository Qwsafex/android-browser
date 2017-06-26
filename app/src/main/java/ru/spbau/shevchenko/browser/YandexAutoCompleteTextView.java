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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Input field that takes autocomplete items from Yandex.Suggest.
 */
public class YandexAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    private static final String YANDEX_SUGGGEST_URL = "http://suggest.yandex.ru/suggest-ff.cgi?part=";
    private RequestQueue requestQueue = null;
    private boolean dontRefresh = false;

    /**
     * Set text without triggering autocomplete
     *
     * @param text text to set
     */
    public void setTextProgrammatically(CharSequence text) {
        dontRefresh = true;
        setText(text);
        dontRefresh = false;
    }

    public YandexAutoCompleteTextView(Context context) {
        super(context);
        addTextChangedListener(textChangedWatcher);
    }

    public YandexAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textChangedWatcher);
    }

    public YandexAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(textChangedWatcher);
    }

    private void refreshAutoComplete(final String s) {
        if (s.length() < getThreshold()) return;

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getContext());
        }
        try {
            JsonArrayRequest request = new JsonArrayRequest(YANDEX_SUGGGEST_URL + URLEncoder.encode(s, "utf-8"),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONArray jsonResponseStrings = response.getJSONArray(1);
                                ArrayList<String> responseStrings = new ArrayList<>();
                                for (int i = 0; i < jsonResponseStrings.length(); i++) {
                                    responseStrings.add(jsonResponseStrings.getString(i));
                                }
                                if (getText().toString().equals(s)) {
                                    setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, responseStrings));
                                    showDropDown();
                                }
                            } catch (JSONException e) {
                                // That's not critical, just log it
                                Log.e("Exception", "YandexAutoCompleteTextView.refreshAutoComplete " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            // That's not critical, just log it
            Log.e("Exception", "UNSUPPORTED ENCODING UTF-8");
        }
    }


    private TextWatcher textChangedWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!dontRefresh) {
                refreshAutoComplete(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
