// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import hudson.model.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

/**
 * This is an integration test. Cache server should be online to use it
 */
@Ignore
public class CacheServerCommTest {

    private CacheServerComm uit;

    @Before
    public void setUp() {
        uit = new CacheServerComm("http://localhost:9090/", 1);
    }

    @Test
    public void postAndGet() throws Exception {
        String id = UUID.randomUUID().toString();
        uit.postCachedResult(id, Result.SUCCESS);

        Thread.sleep(1000);

        Result result = uit.getCachedResult(id);
        Assert.assertEquals(Result.SUCCESS, result);
    }
}
