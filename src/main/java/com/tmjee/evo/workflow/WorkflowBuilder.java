package com.tmjee.evo.workflow;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tmjee
 */
public class WorkflowBuilder {


    // ----------- static methods

    public static WhenCondition when(String cond) {
        return new WhenCondition(cond);
    }


    private Param param = new Param();

    // ----------- instance methods

    public WorkflowBuilder doTask(String name, TaskRunner r) {
        new TaskCondition(r).process(param);
        return this;
    }


    public WorkflowBuilder decide(String name, WhenCondition... whenConditions) {
        new DecideCondition().process(param);
        return this;
    }

    public Workflow build() {
        return null;
    }


    static class Param {
        WorkflowStep.Builder currentBuilder;
        Map<String, WorkflowStep.Builder> allBuilders;
    }



    // ----------- inner classes

    public abstract class Condition {
        abstract void process(Param param);
    }

    public abstract class NamedCondition extends Condition {
        abstract String name();
    }

    public class TaskCondition extends NamedCondition {
        TaskRunner r;
        String name;
        TaskCondition(String name, TaskRunner r) {
            this.name = name;
            this.r = r;
        }

        @Override
        String name() {
            return name;
        }

        @Override
        void process(Param param) {
            parent.setNext(name);
            currentBuilder = new WorkflowStepTask.Builder()
                .set(name, r);
        }

    }

    public class DecideCondition extends NamedCondition {
        private String name;
        private WhenCondition[] whenConditions;

        DecideCondition(String name, WhenCondition... whenConditions) {
            this.name = name;
            this.whenConditions = whenConditions;
        }

        @Override
        String name() {
            return name;
        }

        @Override
        void process(Param param) {
            parent.setNext(name);
            currentBuilder = new WorkflowStepDecision.Builder()
                .set(name);
            for (WhenCondition wc : whenConditions) {
                wc.process(currentBuilder);
            }
        }

    }

    public class WhenCondition extends Condition {

        List<NamedCondition> conditions;
        String cond;

        WhenCondition(String cond) {
            this.cond = cond;
            this.conditions = new ArrayList<>();
        }

        public WhenCondition doTask(String name, TaskRunner r) {
            conditions.add(new TaskCondition(name, r));
            return this;
        }

        public WhenCondition decide(String name, WhenCondition... whenConditions) {
            conditions.add(new DecideCondition(name, whenConditions));
            return this;
        }

        @Override
        void process(Param param) {
            for (NamedCondition c : conditions) {
                builder.setNext(cond, c.name());
                c.process(builder);
            }
        }
    }


}
