package com.tmjee.evo.workflow;

import org.junit.Before;
import org.junit.Test;

import static com.tmjee.evo.workflow.WorkflowBuilder.when;

/**
 * @author tmjee
 */
public class WorkflowTest4 {

    private Workflow workflow;
    private WorkflowStep step;
    private Input input;


    @Before
    public void before() {
        input = new Input.Builder().build();
        workflow =
            new WorkflowBuilder()
                .doTask("task1", (t)->{})
                .doTask("task2", (t)->{})
                .build();
    }

    @Test
    public void test() throws Exception {
        workflow.prettyPrintFlowDiagram();
    }
}
