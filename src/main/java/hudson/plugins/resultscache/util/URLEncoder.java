// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import java.io.UnsupportedEncodingException;

/**
 * URL Encoder utility
 */
public class URLEncoder {

    protected URLEncoder() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * URL encodes the url using UTF-8
     * @param value
     * @return
     */
    public static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
