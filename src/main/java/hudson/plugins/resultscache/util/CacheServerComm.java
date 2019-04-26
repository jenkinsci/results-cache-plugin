// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import hudson.model.Result;

import java.io.IOException;

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
     * Searches a cached result from the cache. Returns NOT_BUILD if not found.
     * @param hash job hash to search
     * @return the cached result
     * @throws IOException there's a communication problem with the cache service
     */
    public Result getCachedResult(String hash) throws IOException {
        return Result.fromString(restClient.executeGet(new StringBuilder(baseUrl).append("/job-results/").append(URLEncoder.encode(hash)).toString(), Result.NOT_BUILT.toString()));
    }

    /**
     * Adds or Updates a result in the cache
     * @param hash job hash to add/update
     * @param result job result. If null then NOT_BUILT
     * @return TRUE if it worked
     * @throws IOException there's a communication problem with the cache service
     */
    public boolean postCachedResult(String hash, Result result) throws IOException {
        Result r = (null != result) ? result : Result.NOT_BUILT;

        return restClient.executePost(new StringBuilder(baseUrl).append("/job-results/").append(URLEncoder.encode(hash)).append("/")
                .append(URLEncoder.encode(r.toString())).toString());
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
