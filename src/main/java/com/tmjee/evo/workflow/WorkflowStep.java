package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public interface WorkflowStep {

    enum Type {
        TASK,
        DECISION
    };

    String getName();
    Type getType();
    void advance(Input input);

    public static class Builder {

    }
}
