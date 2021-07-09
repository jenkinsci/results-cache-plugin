package hudson.plugins.resultscache;

import hudson.model.Result;

import java.io.Serializable;

public class CachedResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Result result;
    private Number build_number;

    public CachedResult(Result result, Number build_number) {
        this.result = result;
        this.build_number = build_number;
    }

    public Result getCachedResult() { return result; }
    public Number getBuildNumber() { return build_number; }

    public void setCachedResult(Result result) { this.result = result; }
    public void setBuild_number(Number build_number) { this.build_number = build_number; }
}
