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
        workflowContext.setNextWorkflowStepName(WorkflowStepStart.NAME);
        if (hasNextStep()) {
            nextStep().advance(null);
        }
    }


    public void prettyPrintFlowDiagram() {
        new FlowDiagramPrettyPrinter(m).print();
    }


    public boolean hasNextStep() {
        return (workflowContext.getNextWorkflowStepName() != WorkflowStepEnd.NAME);
    }


    public WorkflowStep nextStep() {
        return m.get(workflowContext.getNextWorkflowStepName());
    }

}
