package hudson.plugins.resultscache;

import jenkins.model.CauseOfInterruption;

class PluginCauseOfInterruption extends CauseOfInterruption {

    private static final long serialVersionUID = -3325429860618951647L;

    private final String jobHash;
    private final Integer buildNum;

    public PluginCauseOfInterruption(String jobHash, Integer buildNum) {
        this.jobHash = jobHash;
        this.buildNum = buildNum;
    }

    @Override
    public String getShortDescription() {
        return String.format(Constants.LOG_LINE_HEADER + "[INFO] This job (hash: %s) was interrupted because a SUCCESS result is cached from build number %s%n", jobHash, buildNum);
    }
}
