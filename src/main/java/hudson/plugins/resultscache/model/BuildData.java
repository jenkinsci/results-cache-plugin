// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.model;

import hudson.EnvVars;

/**
 * Represents a jenkins job data. It is used to create a hash to identify a jenkins job in the results cache
 */
public class BuildData {

    private String ciUrl;

    private String fullJobName;

    private EnvVars parameters;

    public BuildData(String ciUrl,
                      String fullJobName,
                      EnvVars parameters) {
        this.ciUrl = ciUrl;
        this.fullJobName = fullJobName;
        this.parameters = parameters;
    }

    public BuildData() {
        this.ciUrl = "";
        this.fullJobName = "";
        this.parameters = new EnvVars();
    }

    public String getCiUrl() {
        return ciUrl;
    }

    public void setCiUrl(String ciUrl) {
        this.ciUrl = ciUrl;
    }

    public String getFullJobName() {
        return fullJobName;
    }

    public void setFullJobName(String fullJobName) {
        this.fullJobName = fullJobName;
    }

    public EnvVars getParameters() {
        return parameters;
    }

    public void setParameters(EnvVars parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "BuildData{" +
                "ciUrl='" + ciUrl + '\'' +
                ", fullJobName='" + fullJobName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
