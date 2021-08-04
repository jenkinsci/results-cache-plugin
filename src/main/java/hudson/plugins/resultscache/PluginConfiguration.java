// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

@Extension
public class PluginConfiguration extends GlobalConfiguration {

    private static final int DEFAULT_TIMEOUT = 1;
    public static PluginConfiguration get() {
        return GlobalConfiguration.all().get(PluginConfiguration.class);
    }

    public PluginConfiguration() {
        load();
    }

    private String cacheServiceUrl;
    private String timeout;

    public String getCacheServiceUrl() {
        return cacheServiceUrl;
    }

    public String getTimeout() {
        return timeout;
    }

    public int getTimeoutInt() {
        try {
            int t = Integer.parseInt(timeout);
            return t < 0 ? DEFAULT_TIMEOUT : t;
        } catch (Exception e) {
            return DEFAULT_TIMEOUT;
        }
    }

    @DataBoundSetter
    public void setCacheServiceUrl(String cacheServiceUrl) {
        this.cacheServiceUrl = cacheServiceUrl;
        save();
    }

    @DataBoundSetter
    public void setTimeout(String timeout) {
        if (StringUtils.isEmpty(timeout))
            timeout = String.valueOf(DEFAULT_TIMEOUT);
        this.timeout = timeout;
        save();
    }

    public FormValidation doCheckCacheServiceUrl(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a cacheServiceUrl.");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckTimeout(@QueryParameter String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                int tout = Integer.parseInt(value);
                if (tout < 0) {
                    return FormValidation.error("Please specify a positive number.");
                }
            } catch (NumberFormatException e) {
                return FormValidation.error("Please specify a number of seconds.");
            }
            return FormValidation.ok();
        } else {
            return FormValidation.warning("Value not specified. It will use default value: %d seconds.", DEFAULT_TIMEOUT);
        }
    }
}
