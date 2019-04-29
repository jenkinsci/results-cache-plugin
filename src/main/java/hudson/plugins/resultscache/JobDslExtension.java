// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class JobDslExtension extends ContextExtensionPoint {
    @DslExtensionMethod(context = WrapperContext.class)
    public Object resultsCache() {
        return resultsCache(false, "");
    }

    @DslExtensionMethod(context = WrapperContext.class)
    public Object resultsCache(String hashableProperties) {
        return resultsCache(false, hashableProperties);
    }

    @DslExtensionMethod(context = WrapperContext.class)
    public Object resultsCache(boolean excludeMachineName, String hashableProperties) {
        return new ResultsCacheBuildWrapper(excludeMachineName, hashableProperties);
    }
}
