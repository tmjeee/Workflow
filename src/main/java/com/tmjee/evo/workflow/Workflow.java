package com.tmjee.evo.workflow;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author tmjee
 */
public class Workflow {


    private final Map<String, WorkflowStep> m;

    private final WorkflowContext workflowContext;


    Workflow(Map<String, WorkflowStep> m) {
        this.m = Collections.unmodifiableMap(new LinkedHashMap<>(m));
        this.workflowContext = new WorkflowContext();
        for (WorkflowStep s : m.values()) {
            s.setWorkflowContext(workflowContext);
        }
    }


    public void prettyPrintFlowDiagram() {
        new FlowDiagramPrettyPrinter(m).print();
    }


    public boolean hasNextStep() {
        return (workflowContext.getCurrentWorkflowStepName() != null);
    }


    public WorkflowStep nextStep() {
        return m.get(workflowContext.getCurrentWorkflowStepName());
    }

}
