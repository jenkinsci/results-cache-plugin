// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * REST client utility
 */
public class RestClientUtil {

    private int connectionTimeout;
    private int socketTimeout;

    /**
     * Constructor
     * @param connectionTimeout connectionTimeout in milliseconds
     * @param socketTimeout socketTimeout in milliseconds
     */
    public RestClientUtil(int connectionTimeout, int socketTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
    }

    /**
     * Executes a HTTP POST to the given url
     * @param urlStr URL to invoke
     * @return TRUE if it worked
     * @throws IOException there's a communication error
     */
    public boolean executePost(String urlStr) throws IOException {
        HttpURLConnection connection = createConnection(urlStr);
        connection.setRequestMethod("POST");

        try {
            return connection.getResponseCode() == 200;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Executes a HTTP GET to the given url
     * @param urlStr URL to invoke
     * @param defaultValue default value to return if the http response is not HTTP_OK
     * @return Http response content
     * @throws IOException there's a communication error
     */
    public String executeGet(String urlStr, String defaultValue) throws IOException {
        HttpURLConnection connection = createConnection(urlStr);

        try {
            if (connection.getResponseCode() == 200) {
                return IOUtils.toString(connection.getInputStream());
            } else {
                return defaultValue;
            }
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection createConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(socketTimeout);
        return connection;
    }
}