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
    private Input latestInput;


    Workflow(Map<String, WorkflowStep> m, WorkflowContext wc) {
        this.m = Collections.unmodifiableMap(new LinkedHashMap<>(m));
        this.workflowContext = wc;
        for (WorkflowStep s : m.values()) {
            s.setWorkflowContext(workflowContext);
        }
        workflowContext.setNextWorkflowStepName(WorkflowStepStart.NAME);
    }



    public void prettyPrintFlowDiagram() {
        new FlowDiagramPrettyPrinter(m).print();
    }


    public boolean hasNextStep() {
        WorkflowStep step = m.get(workflowContext.getNextWorkflowStepName());
        return ((AbstractWorkflowStep)step).hasNext();
    }


    public WorkflowStep next(Input input) {
        if (hasNextStep()) {
            latestInput = input;
            WorkflowStep step = m.get(workflowContext.getNextWorkflowStepName());
            ((AbstractWorkflowStep)step).next(input);
            step = m.get(workflowContext.getNextWorkflowStepName());
            return step;
        }
        throw new WorkflowException("No more workflow step");
    }


    // ==== internal package private methods

    Internals _internals() {
        return new Internals(m, workflowContext, latestInput);
    }

    static Workflow _internals(Internals internals) {
        Workflow workflow =  new Workflow(internals.m, internals.wc);
        workflow.latestInput = internals.i;
        return workflow;
    }

    static class Internals {
        final Map<String, WorkflowStep> m;
        final WorkflowContext wc;
        final Input i;
        Internals(Map<String, WorkflowStep> m, WorkflowContext wc, Input i) {
            this.m = m;
            this.i =i;
            this.wc = wc;
        }
    }
}
