// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.Collections;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Executor;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.model.TaskListener;
import hudson.plugins.resultscache.model.BuildConfig;
import hudson.plugins.resultscache.util.CacheServerComm;
import hudson.plugins.resultscache.util.HashCalculator;
import hudson.plugins.resultscache.util.LoggerUtil;
import jenkins.model.CauseOfInterruption;

public class ResultsCacheHelper {

    private final Run<?, ?> build;
    private final TaskListener listener;
    private final BuildConfig buildConfig;

    public ResultsCacheHelper(Run<?, ?> build, TaskListener listener, BuildConfig buildConfig) {
        this.build = build;
        this.listener = listener;
        this.buildConfig = buildConfig;
    }

    public void checkCacheOrExecute() throws IOException, InterruptedException {
        if (StringUtils.isEmpty(getCacheServiceUrl())) {
            LoggerUtil.warn(listener, "(Pre-Checkout) Unable to check cached result because 'cacheServiceUrl' is not defined %n");
        } else {
            String jobHash = new HashCalculator().calculate(build, buildConfig, listener);
            LoggerUtil.info(listener, "(Pre-Checkout) Checking cached result for this job (hash: %s) %n", jobHash);

            JobResult jobResult = getJobResultFromCacheServer(jobHash);
            processJobResult(jobHash, jobResult);
        }
    }

    /**
     * Retrieves a Job Result from the Cache Server
     * @param jobHash job hash to retrive from the cache server
     * @return found Job Result or {@link JobResult#EMPTY_RESULT} otherwise
     */
    private JobResult getJobResultFromCacheServer(String jobHash) {
        JobResult jobResult = JobResult.EMPTY_RESULT;
        CacheServerComm cacheServer = new CacheServerComm(getCacheServiceUrl(), getTimeout());
        try {
            jobResult = cacheServer.getCachedJobResult(jobHash);
            LoggerUtil.info(listener, "(Pre-Checkout) Cached result for this job (hash: %s) is %s; found on build number %s %n", jobHash, jobResult.getResult().toString(), jobResult.getBuild().toString());
        } catch (IOException e) {
            LoggerUtil.warn(listener, "(Pre-Checkout) Unable to get cached result for this job (hash: %s). Exception: %s %n", jobHash, e.getMessage());
        }
        return jobResult;
    }

    /**
     * Process the Job Result obtained from the cache on the current build
     * @param jobHash job hash (for logging purposes)
     * @param jobResult job result
     * @throws IOException
     * @throws InterruptedException
     */
    private void processJobResult(String jobHash, JobResult jobResult) throws IOException, InterruptedException {
        final Result result = jobResult.getResult();
        final Integer buildNum = jobResult.getBuild();
        if (Result.SUCCESS.equals(result)) {
            Executor executor = build.getExecutor();
            if (executor != null && executor.isActive()) {
                if (build instanceof AbstractBuild) {
                    createWorkspace(((AbstractBuild<?, ?>) build).getWorkspace());
                }
                ParametersAction pa = new ParametersAction(Collections.singletonList(new StringParameterValue("CACHED_RESULT_BUILD_NUM", buildNum.toString())), Collections.singleton("CACHED_RESULT_BUILD_NUM"));
                build.addAction(pa);
                executor.interrupt(result, new CauseOfInterruption() {
                    @Override
                    public String getShortDescription() {
                        return String.format(Constants.LOG_LINE_HEADER + "[INFO] This job (hash: %s) was interrupted because a SUCCESS result is cached from build number %s%n", jobHash, buildNum);
                    }
                });
            }
        }
    }

    public static String getCacheServiceUrl() {
        return PluginConfiguration.get().getCacheServiceUrl();
    }

    public static int getTimeout() {
        return PluginConfiguration.get().getTimeoutInt();
    }

    /**
     * Creates the supplied Workspace file path
     *
     * @param ws filepath to the workspace
     * @throws IOException          if the workspace could not be created
     * @throws InterruptedException if the process was interrupted
     */
    private static void createWorkspace(FilePath ws) throws IOException, InterruptedException {
        if (ws != null) {
            ws.mkdirs();
        }
    }
}
