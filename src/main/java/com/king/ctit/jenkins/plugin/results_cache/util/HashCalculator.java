// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package com.king.ctit.jenkins.plugin.results_cache.util;

import com.king.ctit.jenkins.plugin.results_cache.model.BuildData;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;

/**
 * This class calculates a hash from a Jenkins Job data to be used to identify it in the results cache
 */
public class HashCalculator {

    private BuildDataBuilder dataBuilder;
    private BuildDataPreparer dataPreparer;
    private HashGenerator hashGenerator;

    public HashCalculator() {
        this(new BuildDataBuilder(), new BuildDataPreparer(), new HashGenerator());
    }

    public HashCalculator(BuildDataBuilder dataBuilder, BuildDataPreparer dataPreparer, HashGenerator hashGenerator) {
        this.dataBuilder = dataBuilder;
        this.dataPreparer = dataPreparer;
        this.hashGenerator = hashGenerator;
    }

    /**
     * Calculates a job hash from a jenkins job and its configured hashableParameters
     * @param build jenkins job data
     * @param hashParameters configured job parameters to be used to calculate the job hash
     * @param listener task listener to write log traces
     * @return calculated job hash
     */
    public String calculate(AbstractBuild build, String hashParameters, TaskListener listener) {
        BuildData data = dataBuilder.build(build);
        String preparedData = dataPreparer.prepare(data, hashParameters);
        if (listener != null)
            LoggerUtil.info(listener, "(Hash calculation) Data used to create hash: %s\n", preparedData);
        return hashGenerator.getHash(preparedData);
    }
}
