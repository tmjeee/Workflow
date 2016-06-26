package com.tmjee.evo.workflow;

import java.util.Map;
import java.util.Set;

/**
 * @author tmjee
 */
public abstract class AbstractWorkflowStep implements WorkflowStep {

    private final String name;

    protected WorkflowContext workflowContext;

    public AbstractWorkflowStep(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setWorkflowContext(WorkflowContext workflowContext) {
        this.workflowContext = workflowContext;
    }

    protected void validate(Map<String, WorkflowStep> m, Set<String> validationMessages) {
    }

    abstract boolean hasNext();

    abstract void next(Input input);
}
