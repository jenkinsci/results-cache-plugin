// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import hudson.model.Result;
import hudson.plugins.resultscache.JobResult;
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
        uit.postJobResult(id, new JobResult(Result.SUCCESS, 5));

        Thread.sleep(1000);

        JobResult jobResult = uit.getCachedJobResult(id);
        Assert.assertEquals(Result.SUCCESS, jobResult.getResult());
        Assert.assertEquals(5, jobResult.getBuild().intValue());
    }
}
