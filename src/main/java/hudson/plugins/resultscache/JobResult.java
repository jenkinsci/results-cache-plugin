package hudson.plugins.resultscache;

import hudson.model.Result;

import java.io.Serializable;

public class JobResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final JobResult EMPTY_RESULT = new JobResult(Result.NOT_BUILT, -1);

    private Result result;
    private Integer build;

    private JobResult() {}

    public JobResult(Result result, Integer build) {
        this.result = result == null ? Result.NOT_BUILT : result;
        this.build = build == null ? Integer.valueOf(-1) : build;
    }

    public Result getResult() { return result; }
    public Integer getBuild() { return build; }
}
