package com.tmjee.evo.workflow;


import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author tmjee
 */
public class WorkflowBuilder {


    // ----------- static methods

    public static WhenCondition when(String cond) {
        return new WhenCondition(cond);
    }



    // ----------- instance methods

    private Param param = new Param();

    public WorkflowBuilder doTask(String name, TaskRunner r) {
        new TaskCondition(name, r).process(param);
        return this;
    }


    public WorkflowBuilder decide(String name, WhenCondition... whenConditions) {
        new DecideCondition(name, whenConditions).process(param);
        return this;
    }

    public Workflow build() {

        if (param.allBuilders.isEmpty()) {
            throw new WorkflowException("No workflow steps defined");
        }

        WorkflowStep start = new WorkflowStepStart(param.allBuilders.keySet().iterator().next());

        WorkflowStep end = new WorkflowStepEnd();

        Map<String, WorkflowStep> m = new LinkedHashMap<>();
        m.put(start.getName(), start);

        for(Map.Entry<String, WorkflowStep.Builder> e : param.allBuilders.entrySet()) {
            String name = e.getKey();
            WorkflowStep.Builder workflowStepBuilder = e.getValue();

            m.put(name, workflowStepBuilder.build());
        }

        m.put(end.getName(), end);

        Set<String> validationMessages = new HashSet<>();

        for (Map.Entry<String, WorkflowStep> e : m.entrySet()) {
            WorkflowStep workflowStep = e.getValue();
            if (workflowStep instanceof  AbstractWorkflowStep) {
                ((AbstractWorkflowStep)workflowStep).validate(Collections.unmodifiableMap(m), validationMessages);
            }
        }

        if(!validationMessages.isEmpty()) {
            throw new WorkflowException("failed with following validations \n"+
                validationMessages.stream().collect(Collectors.joining("\t\n")));
        }

        return new Workflow(m);
    }




    // ----------- inner classes

    static class Param {
        private WorkflowStep.Builder currentBuilder;
        private Map<String, WorkflowStep.Builder> allBuilders = new LinkedHashMap<>();

        void addWorkflowStepBuilder(String name, WorkflowStep.Builder b) {
           if (allBuilders.containsKey(name)) {
               throw new WorkflowException(format("workflow step %s already exists", name));
           } else {
               allBuilders.put(name, b);
           }
        }
    }

    public static abstract class Condition {
        abstract void process(Param param);
    }

    public static abstract class NamedCondition extends Condition {
        abstract String name();
    }

    public static class TaskCondition extends NamedCondition {
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
            if (param.currentBuilder != null) {
                param.currentBuilder.setNextStep(name);
            }
            param.currentBuilder = new WorkflowStepTask.Builder()
                .setName(name, r);
            param.addWorkflowStepBuilder(name, param.currentBuilder);
        }
    }

    public static class DecideCondition extends NamedCondition {
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
            if (param.currentBuilder != null) {
                param.currentBuilder.setNextStep(name);
            }
            param.currentBuilder = new WorkflowStepDecision.Builder()
                .setName(name);
            param.addWorkflowStepBuilder(name, param.currentBuilder);
            for (WhenCondition wc : whenConditions) {
                wc.process(param);
            }
        }

    }

    public static class WhenCondition extends Condition {

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
            WorkflowStep.Builder oldBuilder = param.currentBuilder;
            for (NamedCondition c : conditions) {
                param.currentBuilder.setNextStep(cond, c.name());
                c.process(param);
            }
            param.currentBuilder = oldBuilder;
        }
    }


}
