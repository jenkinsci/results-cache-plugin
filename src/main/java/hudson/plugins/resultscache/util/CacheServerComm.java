// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import hudson.model.Result;
import hudson.plugins.resultscache.JobResult;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class implements the communication with the Results Cache Service
 */
public class CacheServerComm {

    private static final String RESULT_KEY = "result";
    private static final String BUILD_KEY = "build";
    private static final String DEFAULT_RESPONSE_VALUE = createBodyStringFromJobResult(JobResult.EMPTY_RESULT);

    private final String baseUrl;
    private final RestClientUtil restClient;

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
     * Searches a job result in the cache. Returns {@link JobResult#EMPTY_RESULT}, if not found.
     * @param hash job hash to search
     * @return the cached job result
     * @throws IOException there's a communication problem with the cache service
     */
    public JobResult getCachedJobResult(String hash) throws IOException {
        String url = createRequestUrl(hash);
        String response = restClient.executeGet(url, DEFAULT_RESPONSE_VALUE);
        return createJobResultFromResponse(response);
    }

    /**
     * Adds or Updates a job result into the cache
     * @param hash job hash to add/update
     * @param jobResult job result. If null then {@link JobResult#EMPTY_RESULT}
     * @return TRUE if it worked
     * @throws IOException there's a communication problem with the cache service
     */
    public boolean postJobResult(String hash, JobResult jobResult) throws IOException {
        String url = createRequestUrl(hash);
        String body = createBodyStringFromJobResult(jobResult);
        return restClient.executePost(url, body);
    }

    /**
     * Normalizes the service base URL
     * @param url url to normalize
     * @return normalized url
     */
    private String getServiceBaseUrl(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Returns the request url using the build hash
     * @param hash build hash
     * @return request url
     */
    private String createRequestUrl(String hash) {
        return String.format("%s/job-results/v2/%s", baseUrl, URLEncoder.encode(hash));
    }

    /**
     * Creates a Job Result object from a response body string
     * @param response response body string
     * @return job result
     */
    private JobResult createJobResultFromResponse(String response) {
        JSONObject json = new JSONObject(response);
        Result result = Result.fromString(json.getString(RESULT_KEY));
        Integer build = json.has(BUILD_KEY) ? json.getInt(BUILD_KEY) : null;
        return new JobResult(result, build);
    }

    /**
     * Returns the json string from a Job Result
     * @param jobResult job result
     * @return json string
     */
    private static String createBodyStringFromJobResult(JobResult jobResult) {
        return new JSONObject()
                .put(RESULT_KEY, jobResult.getResult())
                .put(BUILD_KEY, jobResult.getBuild())
                .toString();
    }
}
