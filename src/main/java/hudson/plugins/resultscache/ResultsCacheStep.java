// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache;

import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;

public class ResultsCacheStep extends Step {

    private String hashParameters;

    @DataBoundConstructor
    public ResultsCacheStep(String hashParameters) {
        this.hashParameters = hashParameters;
    }

    public String getHashParameters() {
        return hashParameters;
    }

    public void setHashParameters(String hashParameters) {
        this.hashParameters = hashParameters;
    }

    @Override
    public StepExecution start(StepContext stepContext) {
        return new ExecutionImpl(stepContext, hashParameters);
    }

    private static class ExecutionImpl extends AbstractStepExecutionImpl {
        private String hashParameters;

        public ExecutionImpl(@Nonnull StepContext context, String hashParameters) {
            super(context);
            this.hashParameters = hashParameters;
        }

        @Override
        public boolean start() throws Exception {
            StepContext context = getContext();
            TaskListener listener = context.get(TaskListener.class);
            Run build = context.get(Run.class);
            ResultsCacheHelper.checkCacheOrExecute(build, listener, hashParameters);
            return false;
        }
    }

    /**
     * Descriptor for {@link ResultsCacheStep}.
     */
    @Extension(optional = true)
    public static class StepDescriptorImpl extends StepDescriptor {

        @Override
        public String getDisplayName() {
            return "ResultsCache";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getFunctionName() {
            return "resultsCache";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(Run.class);
        }
    }
}
