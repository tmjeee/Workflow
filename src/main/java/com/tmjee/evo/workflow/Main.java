package com.tmjee.evo.workflow;

import com.github.mdr.ascii.graph.Graph;
import scala.Tuple2;
import scala.collection.immutable.List;
import scala.collection.immutable.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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
                            .doTask("task4", ()->{}),
                        when("opt5")
                            .doGoTo("task1")
                    ),
                when("opt2")
                    .doTask("task5", ()->{})
            )
            .doTask("task6", ()->{})
            .build();



        /*
        Workflow workflow = new WorkflowBuilder()
            .doTask("task1", ()->{})
            .build();

        */


        /*
        Workflow workflow = new WorkflowBuilder()
            .decide("decision1",
                when("opt1").doTask("task1", ()->{}),
                when("opt2").doTask("task2", ()->{})
            )
            .build();
            */




        workflow.prettyPrintFlowDiagram();

        /*
        while(!workflow.hasNextStep()) {
            WorkflowStep step = workflow.nextStep();
            step.getName();
            step.getType();

            Input input = new Input();
            step.advance(input);
        }
        */
    }
}
