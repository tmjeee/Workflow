package com.tmjee.evo.workflow;

import org.junit.Before;
import org.junit.Test;

import static com.tmjee.evo.workflow.WorkflowBuilder.*;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author tmjee
 */
public class WorkflowTest3 {

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
                       when("opt2").doGoTo("task1")
                  )
                  .doTask("task3", (t)->{})
                  .build();
    }


    @Test
    public void test() throws Exception {

        workflow.prettyPrintFlowDiagram();

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task1");

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.DECISION);
        assertEquals(step.getName(), "decision1");

        input.setResult("opt2");
        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task1");

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.DECISION);
        assertEquals(step.getName(), "decision1");

        input.setResult("opt1");
        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task2");

        assertTrue(workflow.hasNextStep());
        step = workflow.next(input);
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task3");

        assertFalse(workflow.hasNextStep());















    }

}
