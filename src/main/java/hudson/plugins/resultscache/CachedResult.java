package hudson.plugins.resultscache;

import hudson.model.Result;

import java.io.Serializable;

public class CachedResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Result result;
    private Integer buildNumber;

    public CachedResult(Result result, Integer buildNumber) {
        this.result = result;
        this.buildNumber = buildNumber;
    }

    public Result getCachedResult() { return result; }
    public Number getBuildNumber() { return buildNumber; }

    public void setCachedResult(Result result) { this.result = result; }
    public void setBuildNumber(Integer buildNumber) { this.buildNumber = buildNumber; }
}
