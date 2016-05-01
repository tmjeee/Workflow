package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowContext {

    private String currentWorkflowStepName;

    public String getCurrentWorkflowStepName() {
        return currentWorkflowStepName;
    }

    public void setCurrentWorkflowStepName(String name) {
        this.currentWorkflowStepName = name;
    }
}
