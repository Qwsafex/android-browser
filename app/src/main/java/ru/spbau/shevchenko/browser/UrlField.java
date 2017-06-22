package ru.spbau.shevchenko.browser;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class UrlField extends EditText {
    public UrlField(Context context) {
        super(context);
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    }
}
