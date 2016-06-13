package com.tmjee.evo.workflow;

import org.junit.Before;
import org.junit.Test;

import static com.tmjee.evo.workflow.WorkflowBuilder.when;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author tmjee
 */
public class WorkflowTest2 {

    private Workflow workflow;
    private Input input;

    @Before
    public void before() {
        workflow =
            new WorkflowBuilder()
                .decide("decision1",
                    when("opt1")
                        .doTask("task1", (i) -> { }),
                    when("opt2")
                        .doTask("task2", (i) -> { }))
                .doTask("task3", (i) -> { })
                .build();

        input = new Input.Builder().build();
    }

    @Test
    public void test() throws Exception {

        workflow.prettyPrintFlowDiagram();

        WorkflowStep step = null;
        input.setResult("opt1");

        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.DECISION);
        assertEquals(step.getName(), "decision1");
        step.advance(input);

        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task1");
        step.advance(input);

        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task3");
        step.advance(input);

        assertFalse(workflow.hasNextStep());
    }
}
