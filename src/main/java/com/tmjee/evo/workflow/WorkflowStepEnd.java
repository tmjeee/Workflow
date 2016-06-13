package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowStepEnd extends WorkflowStepTask {

    public static final String NAME  = "___end___";

    WorkflowStepEnd() {
        super(NAME, (i)->{}, null);
    }
}
