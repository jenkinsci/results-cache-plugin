// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import hudson.EnvVars;
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
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

import static hudson.plugins.resultscache.ResultsCacheHelper.CACHED_RESULT_BUILD_NUM_ENV_VAR_NAME;

public class ResultsCacheBuildWrapper extends BuildWrapper {

    private boolean excludeMachineName;
    private String hashParameters;
    private final BuildConfig buildConfig;

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
        new ResultsCacheHelper(build, listener, buildConfig).checkCacheOrExecute();
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        String jobHash = new HashCalculator().calculate(build, buildConfig);
        return new MyEnvironment(jobHash);
    }

    private class MyEnvironment extends BuildWrapper.Environment {
        private final String jobHash;

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

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Enable " + Constants.DISPLAY_NAME + " for this job";
        }
    }

    @Extension
    public static class RunListenerImpl extends RunListener<AbstractBuild<?, ?>> {

        @Override
        public void onCompleted(AbstractBuild<?, ?> build, @Nonnull TaskListener listener) {
            build.getEnvironments().stream()
                    .filter(e -> e.getClass().getName().startsWith(ResultsCacheBuildWrapper.class.getName()))
                    .findFirst()
                    .ifPresent(e -> saveResultToCache(build, listener, ((MyEnvironment) e).getJobHash()));
        }

        private void saveResultToCache(AbstractBuild<?, ?> build, TaskListener listener, String jobHash) {
            try {
                int buildNum = calculateBuildNumber(build, listener);
                JobResult jobResult = new JobResult(build.getResult(), buildNum);
                postResultToCache(listener, jobHash, jobResult);
            } catch (IOException e) {
                LoggerUtil.info(listener, "Unable to get Environment%n");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private int calculateBuildNumber(AbstractBuild<?, ?> build, TaskListener listener) throws IOException, InterruptedException {
            if (build.getResult() == Result.SUCCESS) {
                EnvVars environment = build.getEnvironment(listener);
                return environment.containsKey(CACHED_RESULT_BUILD_NUM_ENV_VAR_NAME) ?
                        Integer.parseInt(environment.get(CACHED_RESULT_BUILD_NUM_ENV_VAR_NAME)) :
                        build.getNumber();
            }
            return build.getNumber();
        }

        private void postResultToCache(TaskListener listener, String jobHash, JobResult jobResult) {
            CacheServerComm cacheServer = new CacheServerComm(ResultsCacheHelper.getCacheServiceUrl(), ResultsCacheHelper.getTimeout());
            try {
                LoggerUtil.info(listener, "(Post Build) Sending build result for this job (result: %s :: build: %s :: hash: %s)%n",
                        jobResult.getResult(), jobResult.getBuild(), jobHash);
                cacheServer.postJobResult(jobHash, jobResult);
                LoggerUtil.info(listener, "(Update status: SUCCESS) Build result sent%n");
            } catch (IOException e) {
                LoggerUtil.warn(listener, "(Update status: FAILURE) Unable to connect with cache server. Exception: %s%n", e.getMessage());
            }
        }
    }
}
