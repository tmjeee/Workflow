package com.tmjee.evo.workflow;

import org.junit.Before;
import org.junit.Test;

import static com.tmjee.evo.workflow.WorkflowBuilder.otherwise;
import static com.tmjee.evo.workflow.WorkflowBuilder.when;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author tmjee
 */
public class WorkflowTest1 {

    Workflow workflow;
    WorkflowStep step;
    Input i;

    @Before
    public void before() {
        workflow =
            new WorkflowBuilder()
                .doTask("task1", (i) -> {
                })
                .decide("decision1",
                    when("opt1")
                        .doTask("task2", (i) -> {}),
                    otherwise()
                        .doTask("task3", (i) -> {}))
                .build();

        i = new Input.Builder().build();
    }


    @Test
    public void test2() throws Exception {
        new WorkflowWriter().write(workflow, System.out);
    }

    @Test
    public void test() throws Exception {

        workflow.prettyPrintFlowDiagram();

        step = null;
        Input input = new Input.Builder().build();


        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task1");

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input.setResult("opt1"));
        assertEquals(step.getType(), WorkflowStep.Type.DECISION);
        assertEquals(step.getName(), "decision1");

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task2");

        assertFalse(workflow.hasNextStep());
    }
}
