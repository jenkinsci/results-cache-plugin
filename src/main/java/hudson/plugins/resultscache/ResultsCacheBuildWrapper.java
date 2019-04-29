// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import javax.annotation.Nonnull;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.plugins.resultscache.model.BuildConfig;
import hudson.plugins.resultscache.util.CacheServerComm;
import hudson.plugins.resultscache.util.HashCalculator;
import hudson.plugins.resultscache.util.LoggerUtil;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

public class ResultsCacheBuildWrapper extends BuildWrapper {

    private boolean excludeMachineName;
    private String hashParameters;
    private BuildConfig buildConfig;

    @DataBoundConstructor
    public ResultsCacheBuildWrapper(boolean excludeMachineName, String hashParameters) {
        this.excludeMachineName = excludeMachineName;
        this.hashParameters = hashParameters;
        this.buildConfig = new BuildConfig(excludeMachineName, hashParameters);
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
    public void preCheckout(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        ResultsCacheHelper.checkCacheOrExecute(build, listener, buildConfig);
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        String jobHash = new HashCalculator().calculate(build, buildConfig);
        return new MyEnvironment(jobHash);
    }

    private class MyEnvironment extends BuildWrapper.Environment {
        private String jobHash;

        public MyEnvironment(String jobHash) {
            this.jobHash = jobHash;
        }

        public String getJobHash() {
            return jobHash;
        }

        @Override
        public boolean tearDown(AbstractBuild build, BuildListener listener) {
            return true;
        }
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Enable " + Constants.DISPLAY_NAME + " for this job";
        }
    }

    @Extension
    public static class RunListenerImpl extends RunListener<AbstractBuild> {

        @Override
        public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
            build.getEnvironments().stream()
                    .filter(e -> e.getClass().getName().startsWith(ResultsCacheBuildWrapper.class.getName()))
                    .findFirst()
                    .ifPresent(e -> saveResultToCache(build, listener, ((MyEnvironment) e).getJobHash()));
        }

        private void saveResultToCache(AbstractBuild build, TaskListener listener, String jobHash) {
            CacheServerComm cacheServer = new CacheServerComm(ResultsCacheHelper.getCacheServiceUrl(), ResultsCacheHelper.getTimeout());
            Result r = (null != build) ? build.getResult() : Result.NOT_BUILT;
            LoggerUtil.info(listener, "(Post Build) Sending build result for this job (result: %s :: hash: %s) %n", r, jobHash);

            try {
                cacheServer.postCachedResult(jobHash, r);
                LoggerUtil.info(listener, "(Update status: SUCCESS) Build result sent %n");
            } catch (IOException e) {
                LoggerUtil.warn(listener, "(Update status: FAILURE) Unable to connect with cache server. Exception: %s %n", e.getMessage());
            }
        }
    }
}
