// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import java.io.IOException;
import hudson.model.Result;
import hudson.plugins.resultscache.CachedResult;
import org.json.JSONObject;

/**
 * This class implements the communication with the Results Cache Service
 */
public class CacheServerComm {

    private String baseUrl;
    private RestClientUtil restClient;

    /**
     * Constructor
     * @param cacheServiceUrl cache service base URL
     * @param timeout communication timeout
     */
    public CacheServerComm(String cacheServiceUrl, int timeout) {
        this.baseUrl = getServiceBaseUrl(cacheServiceUrl);
        this.restClient = new RestClientUtil(timeout * 1000, timeout * 1000);
    }

    /**
     * Searches a cached result from the cache. Returns NOT_BUILD for result, and -1 for build_number, if not found.
     * @param hash job hash to search
     * @return the cached result
     * @throws IOException there's a communication problem with the cache service
     */
    public CachedResult getCachedResult(String hash) throws IOException {
        String url = baseUrl + "/job-results/" + URLEncoder.encode(hash);
        String defaultValue = "{\"result\": " + Result.NOT_BUILT.toString() + ", \"build_number\": -1}";

        String response = restClient.executeGet(url, defaultValue);
        JSONObject json = new JSONObject(response);
        Result result = Result.fromString(json.getString("result"));
        Number build_number = json.getNumber("build_number");

        return new CachedResult(result, build_number);
    }

    /**
     * Adds or Updates a result in the cache
     * @param hash job hash to add/update
     * @param result job result. If null then NOT_BUILT
     * @param build_number job number - allows reference to the run the result refers to. If null then -1
     * @return TRUE if it worked
     * @throws IOException there's a communication problem with the cache service
     */
    public boolean postCachedResult(String hash, Result result, Number build_number) throws IOException {
        Result r = (null != result) ? result : Result.NOT_BUILT;
        Number num = (build_number != null) ? build_number : -1;

        String url = baseUrl + "/job-results/" + URLEncoder.encode(hash);
        String jsonInputString = "{\"result\": " + r + ", \"build_number\": " + num + "}";

        return restClient.executeJsonPost(url, jsonInputString);
    }

    /**
     * Normalizes the service base URL
     * @param url url to normalize
     * @return normalized url
     */
    private String getServiceBaseUrl(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
