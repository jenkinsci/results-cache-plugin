// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * REST client utility
 */
public class RestClientUtil {

    private final int connectionTimeout;
    private final int socketTimeout;

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
     * Executes a HTTP JSON POST to the given url, with the provided json string
     * @param urlStr URL to invoke
     * @param body JSON request body, as string
     * @return TRUE if it worked
     * @throws IOException there's a communication error
     */
    public boolean executePost(String urlStr, String body) throws IOException {
        HttpURLConnection connection = createConnection(urlStr);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

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
                return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
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