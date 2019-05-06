// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * This class tests the RestClientUtil class functionality.
 */
public class RestClientUtilTest {

    private RestClientUtil uit;

    @Before
    public void setup() {
        uit = new RestClientUtil(0, 0);
    }

    @Test
    public void test_http_get_request() throws IOException {
        String responseContent = uit.executeGet("http://www.google.com", "");
        Assert.assertTrue(responseContent.length() > 0);
    }

    @Test
    public void test_https_get_request() throws IOException {
        String responseContent = uit.executeGet("https://www.google.com", "");
        Assert.assertTrue(responseContent.length() > 0);
    }
}