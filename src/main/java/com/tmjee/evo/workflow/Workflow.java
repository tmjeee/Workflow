package com.tmjee.evo.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tmjee
 */
public class Workflow {

    private static final String START = "__________start___________";
    private static final String END   = "___________end____________";


    private Map<String, WorkflowStep> m = new HashMap<>();

    private String currentWorkflowStepName = "start";

    public void prettyPrintFlowDiagram() {
    }


    public boolean hasNext() {

        return false;
    }


    public WorkflowStep next() {

        return null;
    }

}
