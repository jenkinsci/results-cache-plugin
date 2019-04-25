// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * REST client utility
 */
public class RestClientUtil {

    private int connectionTimeout;
    private int socketTimeout;

    /**
     * Constructor
     * @param connectionTimeout
     * @param socketTimeout
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
        if (url.getProtocol().equalsIgnoreCase("https")) {
            disableSSLVerification(connection);
        }

        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(socketTimeout);
        return connection;
    }

    private void disableSSLVerification(HttpURLConnection conn) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            ((HttpsURLConnection) conn).setHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}