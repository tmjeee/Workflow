package com.tmjee.evo.workflow;

import org.junit.Before;
import org.junit.Test;

import static com.tmjee.evo.workflow.WorkflowBuilder.when;

/**
 * @author tmjee
 */
public class WorkflowTest8 {
    private Workflow workflow;
    private WorkflowStep step;
    private Input input;


    @Before
    public void before() {
        input = new Input.Builder().build();
        workflow =
            new WorkflowBuilder()
                .doTask("task1", (t)->{})
                .decide("decision1",
                    when("opt1").doTask("task2", (t)->{}),
                    when("opt2")
                        .doTask("task3", (t)->{})
                        .doTask("task4", (t)->{})
                        .doTask("task5", (t)->{})
                        .doGoTo("task1")
                )
                .build();
    }

    @Test
    public void test() throws Exception {
        workflow.prettyPrintFlowDiagram();
    }
}
