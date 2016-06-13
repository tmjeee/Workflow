package com.tmjee.evo.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tmjee
 */
public class WorkflowContext {

    private String nextWorkflowStepName;
    private Map<String, String> userSpace = new HashMap<>();

    String getNextWorkflowStepName() {
        return nextWorkflowStepName;
    }

    void setNextWorkflowStepName(String name) {
        this.nextWorkflowStepName = name;
    }

    public Map<String, String> getUserSpace() {
        return userSpace;
    }
}
