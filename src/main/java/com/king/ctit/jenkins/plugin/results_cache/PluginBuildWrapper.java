// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache;

import com.king.ctit.jenkins.plugin.results_cache.util.CacheServerComm;
import com.king.ctit.jenkins.plugin.results_cache.util.HashCalculator;
import com.king.ctit.jenkins.plugin.results_cache.util.LoggerUtil;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.model.CauseOfInterruption;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

public class PluginBuildWrapper extends BuildWrapper {

    private String hashParameters;

    @DataBoundConstructor
    public PluginBuildWrapper(String hashParameters) {
        this.hashParameters = hashParameters;
    }

    public String getHashParameters() {
        return hashParameters;
    }

    public void setHashParameters(String hashParameters) {
        this.hashParameters = hashParameters;
    }

    @Override
    public void preCheckout(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        if (StringUtils.isEmpty(getCacheServiceUrl())) {
            LoggerUtil.warn(listener, "(Pre-Checkout) Unable to check cached result because 'cacheServiceUrl' is not defined %n");
        } else {
            String jobHash = new HashCalculator().calculate(build, hashParameters, listener);
            LoggerUtil.info(listener, "(Pre-Checkout) Checking cached result for this job (hash: %s) %n", jobHash);

            Result result = Result.NOT_BUILT;
            CacheServerComm cacheServer = new CacheServerComm(getCacheServiceUrl(), getTimeout());
            try {
                result = cacheServer.getCachedResult(jobHash);
                LoggerUtil.info(listener, "(Pre-Checkout) Cached result for this job (hash: %s) is %s %n", jobHash, result.toString());
            } catch (IOException e) {
                LoggerUtil.warn(listener, "(Pre-Checkout) Unable to get cached result for this job (hash: %s). Exception: %s %n", jobHash, e.getMessage());
            }

            if (result.equals(Result.SUCCESS)) {
                Executor executor = build.getExecutor();
                if (executor != null) {
                    if (executor.isActive()) {
                        createWorkspace(build.getWorkspace());
                        executor.interrupt(result, new CauseOfInterruption() {
                            @Override
                            public String getShortDescription() {
                                return String.format(Constants.LOG_LINE_HEADER + "[INFO] This job (hash: %s) was interrupted because a SUCCESS result is cached %n", jobHash);
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * Creates the supplied Workspace file path
     *
     * @param ws filepath to the workspace
     * @throws IOException          if the workspace could not be created
     * @throws InterruptedException if the process was interrupted
     */
    public void createWorkspace(FilePath ws) throws IOException, InterruptedException {
        if (ws != null) {
            ws.mkdirs();
        }
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        String jobHash = new HashCalculator().calculate(build, hashParameters, null);
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
    public static class PluginDescriptor extends BuildWrapperDescriptor {

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
    public static class PluginRunListener extends RunListener<AbstractBuild> {

        @Override
        public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
            build.getEnvironments().stream()
                    .filter(e -> e.getClass().getName().startsWith(PluginBuildWrapper.class.getName()))
                    .findFirst()
                    .ifPresent(e -> saveResultToCache(build, listener, ((MyEnvironment) e).getJobHash()));
        }

        private void saveResultToCache(AbstractBuild build, TaskListener listener, String jobHash) {
            CacheServerComm cacheServer = new CacheServerComm(PluginBuildWrapper.getCacheServiceUrl(), PluginBuildWrapper.getTimeout());
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

    private static String getCacheServiceUrl() {
        return PluginConfiguration.get().getCacheServiceUrl();
    }

    private static int getTimeout() {
        return PluginConfiguration.get().getTimeoutInt();
    }
}
