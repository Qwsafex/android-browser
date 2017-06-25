package ru.spbau.shevchenko.browser;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SuppressWarnings("WeakerAccess")
public class URLUtils {
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final String DEFAULT_PROTOCOL = HTTP_PROTOCOL;
    private static final String YANDEX_SEARCH_URL = "https://yandex.ru/search/?text=";

    /**
     * Determines if given string is URL or search request and gives appropriate URL.
     * <p>
     * Adds protocol to the beginning of URL if needed.
     *
     * @param url user-typed URL
     * @return URL of site that user wanted or URL of search engine request
     */
    public static String cleanupUrl(String url) {
        if (url.startsWith(HTTP_PROTOCOL) || url.startsWith(HTTPS_PROTOCOL)) {
            return url;
        }
        if (url.contains(" ") || !url.contains(".")) {
            return yandexSearchUrl(url);
        } else {
            return DEFAULT_PROTOCOL + url;
        }
    }

    /**
     * Returns URL of Yandex.Search with given request
     *
     * @param request search request
     * @return URL of Yandex.Search with given request
     */
    public static String yandexSearchUrl(String request) {
        try {
            return YANDEX_SEARCH_URL + URLEncoder.encode(request, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("yaSearchUrl", "UNSUPPORTED ENCODING");
            return request;
        }
    }
}
