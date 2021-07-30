// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.model;

import java.io.Serializable;

/**
 * Represents a configuration for this plugin in a jenkins job.
 */
public class BuildConfig implements Serializable {

    private static final long serialVersionUID = 2725731030223676965L;

    private boolean excludeMachineName;
    private String hashParameters;

    public BuildConfig(String hashParameters) {
        this(false, hashParameters);
    }

    public BuildConfig(boolean excludeMachineName, String hashParameters) {
        this.excludeMachineName = excludeMachineName;
        this.hashParameters = hashParameters;
    }

    public boolean isExcludeMachineName() {
        return excludeMachineName;
    }

    public void setExcludeMachineName(boolean excludeMachineName) {
        this.excludeMachineName = excludeMachineName;
    }

    public String getHashParameters() {
        return hashParameters;
    }

    public void setHashParameters(String hashParameters) {
        this.hashParameters = hashParameters;
    }

    @Override
    public String toString() {
        return "BuildConfig{" +
               "excludeMachineName=" + excludeMachineName +
               ", hashParameters='" + hashParameters + '\'' +
               '}';
    }
}
