package hudson.plugins.resultscache;

import hudson.model.Result;

public class CachedResult {
    public Result result;
    public Number build_number;

    public CachedResult(Result result, Number build_number) {
        this.result = result;
        this.build_number = build_number;
    }
}
