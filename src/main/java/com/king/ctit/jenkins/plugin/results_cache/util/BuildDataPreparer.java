// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import com.king.ctit.jenkins.plugin.results_cache.model.BuildData;
import hudson.EnvVars;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class prepares a string from a BuildData instance. This string will be used to create a hash which identifies a Jenkins Job in the result cache.
 */
public class BuildDataPreparer {

    /**
     * Prepares a string from a BuildData instance.
     * It only takes the job parameters present in the hashParameters comma separated list. If empty it takes all the parameters.
     * Output string format: <pre>&lt;jenkins master URL&gt;;&lt;jenkins job full name&gt;[&lt;parameter name&gt;=&lt;parameter value&gt;...]</pre>
     * @param buildData buildData instance
     * @param hashParameters comma separated list to take from the job. If empty it takes all of them.
     * @return a string from a BuildData instance.
     */
    public String prepare(BuildData buildData, String hashParameters) {
        StringBuilder sb = new StringBuilder();

        // Job values
        sb.append(StringUtils.isEmpty(buildData.getCiUrl()) ? "" : buildData.getCiUrl()).append(";")
            .append(buildData.getFullJobName()).append(";");

        // Job parameters
        List<String> hashableParameters = Stream.of(hashParameters.split(",")).map(String::trim).filter(s -> !StringUtils.isEmpty(s)).map(String::toUpperCase).collect(Collectors.toList());

        sb.append("[");
        EnvVars parameters = buildData.getParameters();
        sb.append(
            parameters.entrySet().stream()
                .filter(entry -> hashableParameters.isEmpty() || hashableParameters.contains(entry.getKey().toUpperCase()))
                .map(entry -> new StringBuilder(entry.getKey()).append("=").append(entry.getValue()).toString())
                .collect(Collectors.joining(","))
        );
        sb.append("]");

        return sb.toString();
    }
}
