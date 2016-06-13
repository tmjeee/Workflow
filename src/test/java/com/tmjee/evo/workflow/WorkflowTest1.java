package com.tmjee.evo.workflow;

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

    @Test
    public void test() throws Exception {

        Workflow workflow =
            new WorkflowBuilder()
                .doTask("task1", (i) -> {
                })
                .decide("decision1",
                    when("opt1")
                        .doTask("task2", (i) -> {}),
                    otherwise()
                        .doTask("task3", (i) -> {}))
                .build();

        workflow.prettyPrintFlowDiagram();

        WorkflowStep step = null;
        Input input = new Input.Builder().build();


        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task1");
        step.advance(input.setResult("opt1"));

        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.DECISION);
        assertEquals(step.getName(), "decision1");
        step.advance(input);

        assertTrue(workflow.hasNextStep());
        step = workflow.nextStep();
        assertEquals(step.getType(), WorkflowStep.Type.TASK);
        assertEquals(step.getName(), "task2");
        step.advance(input);

        assertFalse(workflow.hasNextStep());
    }
}
