// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import com.king.ctit.jenkins.plugin.results_cache.model.BuildData;
import hudson.EnvVars;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the BuildDataPreparer class functionality.
 */
public class BuildDataPreparerTest {

    private BuildDataPreparer uit;

    @Before
    public void setUp() {
        uit = new BuildDataPreparer();
    }

    @Test
    public void check_empty_parameters() {
        BuildData data = new BuildData();
        data.setCiUrl("https://my-jenkins-server.com/");
        data.setFullJobName("myfolder/myjob");

        String result = uit.prepare(data, "");
        Assert.assertEquals("https://my-jenkins-server.com/;myfolder/myjob;[]", result);
    }

    @Test
    public void check_not_empty_parameters() {
        BuildData data = new BuildData();
        data.setCiUrl("https://my-jenkins-server.com/");
        data.setFullJobName("myfolder/myjob");

        EnvVars parameters = new EnvVars();
        parameters.put("PARAM_1","VALUE_1");
        parameters.put("PARAM_2","VALUE_2");

        data.setParameters(parameters);

        String result = uit.prepare(data, "PARAM_1,PARAM_2");
        Assert.assertEquals("https://my-jenkins-server.com/;myfolder/myjob;[PARAM_1=VALUE_1,PARAM_2=VALUE_2]", result);
    }

    @Test
    public void check_not_empty_unsorted_parameters() {
        BuildData data = new BuildData();
        data.setCiUrl("https://my-jenkins-server.com/");
        data.setFullJobName("myfolder/myjob");

        EnvVars parameters = new EnvVars();
        parameters.put("PARAM_2","VALUE_2");
        parameters.put("PARAM_1","VALUE_1");

        data.setParameters(parameters);

        String result = uit.prepare(data, "PARAM_1,PARAM_2");
        Assert.assertEquals("https://my-jenkins-server.com/;myfolder/myjob;[PARAM_1=VALUE_1,PARAM_2=VALUE_2]", result);
    }

    @Test
    public void check_empty_hashable_parameters_use_all_parameters() {
        BuildData data = new BuildData();
        data.setCiUrl("https://my-jenkins-server.com/");
        data.setFullJobName("myfolder/myjob");

        EnvVars parameters = new EnvVars();
        parameters.put("PARAM_2","VALUE_2");
        parameters.put("PARAM_1","VALUE_1");

        data.setParameters(parameters);

        String result = uit.prepare(data, "");
        Assert.assertEquals("https://my-jenkins-server.com/;myfolder/myjob;[PARAM_1=VALUE_1,PARAM_2=VALUE_2]", result);
    }

    @Test
    public void check_only_hashable_parameters() {
        BuildData data = new BuildData();
        data.setCiUrl("https://my-jenkins-server.com/");
        data.setFullJobName("myfolder/myjob");

        EnvVars parameters = new EnvVars();
        parameters.put("PARAM_2","VALUE_2");
        parameters.put("PARAM_1","VALUE_1");

        data.setParameters(parameters);

        String result = uit.prepare(data, "PARAM_1");
        Assert.assertEquals("https://my-jenkins-server.com/;myfolder/myjob;[PARAM_1=VALUE_1]", result);
    }
}
