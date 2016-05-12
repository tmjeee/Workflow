package com.tmjee.evo.workflow;

import java.util.*;

import static java.lang.String.format;

/**
 * @author tmjee
 */
public class WorkflowStepDecision extends AbstractWorkflowStep {

    private Map<String, String> possibilities;

    public WorkflowStepDecision(String name, Map<String, String > possibilities) {
        super(name);
        this.possibilities = Collections.unmodifiableMap(new HashMap<>(possibilities));
    }


    @Override
    public Type getType() {
        return Type.DECISION;
    }

    @Override
    protected void validate(Map<String, WorkflowStep> m, Set<String> validationMessages) {
        Set<String> missingWorkflowSteps = new HashSet<>();
        for (String workflowStepName : possibilities.values()) {
            if (!m.containsKey(workflowStepName)) {
               missingWorkflowSteps.add(workflowStepName);
            }
        }
        if (!missingWorkflowSteps.isEmpty()) {
            validationMessages.add(
                format(" workflow step names %allActiveLanes are referred in workflow step %allActiveLanes but are not defined", missingWorkflowSteps, getName()));
        }
    }

    @Override
    public void advance(Input input) {

    }

    @Override
    public void accept(Visitor v) {
        for (Map.Entry<String, String> e: possibilities.entrySet()) {
            v.setNextStep(e.getKey(), e.getValue());
        }
    }


    public static class Builder extends WorkflowStep.Builder {
        private String name;
        private Map<String, String> conditionalNextSteps = new HashMap<>();

        @Override
        WorkflowStep.Builder setNextStep(String condition, String workflowStepName) {
            conditionalNextSteps.put(condition, workflowStepName);
            return this;
        }

        @Override
        WorkflowStep.Builder setNextStep(String name) {

            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public WorkflowStepDecision build() {
            return new WorkflowStepDecision(name, conditionalNextSteps);
        }
    }
}
