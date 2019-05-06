// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.plugins.resultscache.model.BuildData;
import jenkins.model.Jenkins;

/**
 * This class creates a BuildData instance from a Jenkins Job AbstractBuild instance
 */
public class BuildDataBuilder {

    /**
     * Builds a BuildData instance from a Jenkins Job AbstractBuild instance.
     * It takes the following data:
     * <ul>
     *     <li>Jenkins master URL</li>
     *     <li>Full job name</li>
     *     <li>parameters map: name and value</li>
     * </ul>
     *
     * @param build Jenkins Job AbstractBuild instance
     * @return BuildData instance from a Jenkins Job AbstractBuild instance
     */
    public BuildData build(Run build) {
        BuildData buildData = new BuildData();
        buildData.setCiUrl(Jenkins.getInstance().getRootUrl());
        buildData.setFullJobName(build.getParent().getFullName());

        ParametersAction paramsAction = build.getAction(ParametersAction.class);
        if (paramsAction != null) {
            EnvVars env = new EnvVars();
            for (ParameterValue value : paramsAction.getParameters()) {
                if (!value.isSensitive()) {
                    value.buildEnvironment(build, env);
                }
            }
            buildData.setParameters(env);
        }

        return buildData;
    }
}
