package com.tmjee.evo.workflow;

import static com.tmjee.evo.workflow.WorkflowBuilder.*;

/**
 * @author tmjee
 */
public class Main {



    public static void main(String[] args) throws Exception {

        Workflow workflow = new WorkflowBuilder()
            .doTask("task1", ()->{})
            .decide(
                "decision1",
                when("opt1")
                    .doTask("task2", ()->{})
                    .decide(
                        "decision2",
                        when("opt3")
                            .doTask("task3", ()->{}),
                        when("opt4")
                            .doTask("task4", ()->{})
                    ),
                when("opt2")
                    .doTask("task5", ()->{})
            )
            .doTask("task6", ()->{})
            .build();


        while(!workflow.hasNext()) {
            WorkflowStep step = workflow.next();
            step.getName();
            step.getType();

            Input input = new Input();
            step.advance(input);
        }
    }
}
