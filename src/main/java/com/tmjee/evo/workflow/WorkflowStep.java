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
    void setWorkflowContext(WorkflowContext workflowContext);
    void accept(Visitor v);

    abstract class Builder {
        Builder setNextStep(String name) { return this; };
        Builder setNextStep(String condition, String workflowStepName) { return this; };
        abstract WorkflowStep build();
    }

    interface Visitor {
        void setNextStep(String workflowStepName);
        void setNextStep(String cond, String workflowStepName);
    }
}
