package com.tmjee.evo.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;

/**
 * @author tmjee
 */
public class WorkflowStepDecision extends AbstractWorkflowStep {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowStepDecision.class);

    private Map<String, String> possibilities;
    private String otherwiseStepName;

    public WorkflowStepDecision(String name, String otherwiseStepName, Map<String, String > possibilities) {
        super(name);
        this.possibilities = Collections.unmodifiableMap(new HashMap<>(possibilities));
        this.otherwiseStepName = otherwiseStepName;
    }


    @Override
    public Type getType() {
        return Type.DECISION;
    }

    @Override
    protected void validate(Map<String, WorkflowStep> m, Set<String> validationMessages) {
        Set<String> missingWorkflowSteps = new HashSet<>();
        for (String workflowStepName : possibilities.values()) {
            if (!m.containsKey(workflowStepName) && (otherwiseStepName == null)) {
               missingWorkflowSteps.add(workflowStepName);
            }
        }
        if (!missingWorkflowSteps.isEmpty()) {
            validationMessages.add(
                format(" workflow step names %allActiveLanes are referred in workflow step %allActiveLanes but are not defined", missingWorkflowSteps, getName()));
        }
    }

    @Override
    boolean hasNext() {
        return true;
    }

    @Override
    void next(Input input) {
        String result = input.getResult();
        if (possibilities.containsKey(result)) {
            workflowContext.setNextWorkflowStepName(possibilities.get(result));
        } else {
            if (otherwiseStepName != null) {
                workflowContext.setNextWorkflowStepName(otherwiseStepName);
            } else {
                LOG.warn(format("No option from decision %s matched result %s", getName(), result));
            }
        }
    }

    @Override
    public void accept(Visitor v) {
        for (Map.Entry<String, String> e: possibilities.entrySet()) {
            v.setNextStep(e.getKey(), e.getValue());
        }
        if (otherwiseStepName != null) {
            v.setNextStep(otherwiseStepName);
        }
    }


    public static class Builder extends WorkflowStep.Builder {
        private String name;
        private Map<String, String> conditionalNextSteps = new HashMap<>();
        private String otherwiseStepName;

        @Override
        WorkflowStep.Builder setNextStep(String condition, String workflowStepName) {
            if (condition != null) {
                conditionalNextSteps.put(condition, workflowStepName);
            } else {
                otherwiseStepName = workflowStepName;
            }
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
            return new WorkflowStepDecision(name, otherwiseStepName, conditionalNextSteps);
        }
    }
}
