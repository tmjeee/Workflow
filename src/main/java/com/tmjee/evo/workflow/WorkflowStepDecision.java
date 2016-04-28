package com.tmjee.evo.workflow;

import java.util.Map;

/**
 * @author tmjee
 */
public class WorkflowStepDecision implements WorkflowStep {


    private Map<String, WorkflowStep> possibilities;


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
        public Builder when(String condition, WorkflowStep workflowStep) {
            return this;
        }
        public Builder when(String condition, String workflowStepName) {
            return this;
        }
        public WorkflowStepDecision build() {
            return null;
        }
    }
}
