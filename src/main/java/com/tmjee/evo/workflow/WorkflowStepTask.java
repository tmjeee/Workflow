package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowStepTask implements WorkflowStep{

    private WorkflowContext workflowContext;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void advance(Input input) {

    }


    public static class Builder extends WorkflowStep.Builder {
        public Builder set(String name, TaskRunner r) {
            return this;
        }
        public WorkflowStepTask build() {
            return null;
        }
    }
}
