// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import java.io.UnsupportedEncodingException;

/**
 * URL Encoder utility
 */
public final class URLEncoder {

    private URLEncoder() {
    }

    /**
     * URL encodes the url using UTF-8
     * @param value url to encode
     * @return encoded url
     */
    public static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
