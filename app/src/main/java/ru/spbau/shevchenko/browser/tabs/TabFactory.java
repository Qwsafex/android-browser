package ru.spbau.shevchenko.browser.tabs;

import android.content.Context;

interface TabFactory {
    Tab createTab(Context context);
}
