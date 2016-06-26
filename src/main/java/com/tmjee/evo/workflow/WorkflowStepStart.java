package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowStepStart extends WorkflowStepTask {

    public static final String NAME = "___start___";

    WorkflowStepStart(String nextWorkflowStepName) {
        super(NAME, (i)->{}, nextWorkflowStepName);
    }

}
