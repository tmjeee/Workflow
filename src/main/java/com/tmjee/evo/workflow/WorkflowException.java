package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowException extends RuntimeException {
    public WorkflowException(String msg) {
        super(msg);
    }
    public WorkflowException(String msg, Throwable t) {
        super(msg, t);
    }
    public WorkflowException(Throwable t) {
        super(t);
    }
}
