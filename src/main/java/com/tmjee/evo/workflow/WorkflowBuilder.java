package com.tmjee.evo.workflow;


/**
 * @author tmjee
 */
public class WorkflowBuilder {


    // ----------- static methods

    public static WhenCondition when(String cond) {
        return new WhenCondition();
    }



    // ----------- instance methods

    public WorkflowBuilder doTask(String name, TaskRunner r) {

        return this;
    }


    public WorkflowBuilder decide(String name, WhenCondition... whenConditions) {

        return this;
    }

    public Workflow build() {
        return null;
    }





    // ----------- inner classes

    public static class Condition {

    }

    public static class TaskCondition {

    }

    public static class DecideCondition {

    }

    public static class WhenCondition {

        public WhenCondition doTask(String name, TaskRunner r) {

            return this;
        }

        public WhenCondition decide(String name, WhenCondition... whenConditions) {

            return this;
        }
    }


}
