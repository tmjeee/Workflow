package com.tmjee.evo.workflow;

import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author tmjee
 */
public class WorkflowStepTask extends AbstractWorkflowStep {

    private final TaskRunner r;
    private final String nextWorkflowStepName;


    WorkflowStepTask(String name, TaskRunner r, String nextWorkflowStepName) {
        super(name);
        this.r = r;
        this.nextWorkflowStepName = nextWorkflowStepName;
    }

    @Override
    public Type getType() {
        return Type.TASK;
    }

    @Override
    public void advance(Input input) {
        r.run(input);
        workflowContext.setNextWorkflowStepName(nextWorkflowStepName);
    }

    @Override
    public void accept(Visitor v) {
        if (nextWorkflowStepName != null)
            v.setNextStep(nextWorkflowStepName);
    }

    @Override
    protected void validate(Map<String, WorkflowStep> m, Set<String> validationMessages) {
        if (nextWorkflowStepName != null) {
            if (!m.containsKey(nextWorkflowStepName)) {
                validationMessages.add(format("workflow %allActiveLanes is referred in %allActiveLanes but not defined", nextWorkflowStepName, getName()));
            }
        }
    }

    public static class Builder extends WorkflowStep.Builder {

        private String name;
        private TaskRunner r;
        private String nextWorkflowStepName;

        public Builder setName(String name, TaskRunner r) {
            this.name = name;
            this.r = r;
            return this;
        }

        @Override
        WorkflowStep.Builder setNextStep(String name) {
            this.nextWorkflowStepName = name;
            return this;
        }

        @Override
        public WorkflowStepTask build() {
            return new WorkflowStepTask(name, r, nextWorkflowStepName == null ? WorkflowStepEnd.NAME : nextWorkflowStepName);
        }
    }
}
