package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowStepDecision implements WorkflowStep {





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

    public static class Builder {
        public Builder when(String condition, TaskRunner r) {
            return this;
        }
        public Builder when(String condition, String taskName) {
            return this;
        }
    }
}
