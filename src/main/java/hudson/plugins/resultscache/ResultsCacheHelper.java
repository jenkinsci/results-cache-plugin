// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.Optional;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Executor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.resultscache.model.BuildConfig;
import hudson.plugins.resultscache.util.CacheServerComm;
import hudson.plugins.resultscache.util.HashCalculator;
import hudson.plugins.resultscache.util.LoggerUtil;
import jenkins.model.CauseOfInterruption;

public class ResultsCacheHelper {

    public static void checkCacheOrExecute(Run build, TaskListener listener, BuildConfig buildConfig) throws IOException, InterruptedException {
        if (StringUtils.isEmpty(getCacheServiceUrl())) {
            LoggerUtil.warn(listener, "(Pre-Checkout) Unable to check cached result because 'cacheServiceUrl' is not defined %n");
        } else {
            String jobHash = new HashCalculator().calculate(build, buildConfig, listener);
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
                        if (build instanceof AbstractBuild) {
                            createWorkspace(((AbstractBuild)build).getWorkspace());
                        }
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
