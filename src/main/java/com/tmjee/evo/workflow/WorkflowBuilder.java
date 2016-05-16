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
    private List<WorkflowStep.Builder> builderList = Collections.emptyList();

    public WorkflowBuilder doTask(String name, TaskRunner r) {
        builderList = new TaskCondition(name, r).process(builderList, param);
        return this;
    }

    public WorkflowBuilder decide(String name, WhenCondition... whenConditions) {
        builderList = new DecideCondition(name, whenConditions).process(builderList, param);
        return this;
    }

    public WorkflowBuilder doGoTo(String gotoName) {
        builderList = new GotoCondition(gotoName).process(builderList, param);
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
        private Map<String, WorkflowStep.Builder> allBuilders = new LinkedHashMap<>();

        void addWorkflowStepBuilder(String name, WorkflowStep.Builder b) {
           if (allBuilders.containsKey(name)) {
               throw new WorkflowException(format("workflow step %allActiveLanes already exists", name));
           } else {
               allBuilders.put(name, b);
           }
        }
    }

    public static abstract class Condition {
        abstract List<WorkflowStep.Builder> process(List<WorkflowStep.Builder> prevBuilders, Param param);
    }


    public static abstract class NamedCondition extends Condition {
        abstract String name();
    }

    public static class GotoCondition extends Condition {

        String cond;
        String gotoName;

        GotoCondition(String gotoName) {
            this.gotoName = gotoName;
        }
        GotoCondition(String cond, String gotoName) {
            this(gotoName);
            this.cond = cond;
        }

        @Override
        List<WorkflowStep.Builder> process(List<WorkflowStep.Builder> prevBuilders, Param param) {
            for(WorkflowStep.Builder prevBuilder : prevBuilders) {
                if (cond == null) {
                    prevBuilder.setNextStep(gotoName);
                } else {
                    prevBuilder.setNextStep(cond, gotoName);
                }
            }
            return Collections.emptyList();
        }
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
        List<WorkflowStep.Builder> process(List<WorkflowStep.Builder> prevBuilders, Param param) {
            WorkflowStepTask.Builder b = new WorkflowStepTask.Builder()
                .setName(name, r);
            for(WorkflowStep.Builder prevBuilder : prevBuilders) {
                prevBuilder.setNextStep(name);
            }
            param.addWorkflowStepBuilder(name, b);
            return Collections.unmodifiableList(Arrays.asList(b));
        }
    }

    public static class DecideCondition extends NamedCondition {
        private String name;
        private BaseWhenCondition[] whenConditions;

        DecideCondition(String name, BaseWhenCondition... whenConditions) {
            this.name = name;
            this.whenConditions = whenConditions;
        }

        @Override
        String name() {
            return name;
        }

        @Override
        List<WorkflowStep.Builder> process(List<WorkflowStep.Builder> prevBuilders, Param param) {
            WorkflowStepDecision.Builder b = new WorkflowStepDecision.Builder()
                .setName(name);
            for(WorkflowStep.Builder prevBuilder : prevBuilders) {
                prevBuilder.setNextStep(name);
            }
            param.addWorkflowStepBuilder(name, b);
            List<WorkflowStep.Builder> bs = Collections.unmodifiableList(Arrays.asList(b));
            List<WorkflowStep.Builder> total = new ArrayList<>(); // all builders that needs to be mapped to their next step
            for (BaseWhenCondition wc : whenConditions) {
                List<WorkflowStep.Builder> r = wc.process(bs, param);
                total.addAll(r);
            }
            return Collections.unmodifiableList(total);
        }
    }

    public static abstract class BaseWhenCondition extends Condition {

        List<Condition> conditions;
        String cond;

        BaseWhenCondition(String cond) {
            this.cond = cond;
            this.conditions = new ArrayList<>();
        }

    }

    public static abstract class GotoWhenCondition extends BaseWhenCondition {

        GotoWhenCondition(String cond) {
            super(cond);
        }

        public BaseWhenCondition doGoTo(String gotoName) {
            conditions.add(new GotoCondition(cond, gotoName));
            return this;
        }
    }

    public static class WhenCondition extends GotoWhenCondition {

        WhenCondition(String cond) {
            super(cond);
        }

        public WhenCondition doTask(String name, TaskRunner r) {
            conditions.add(new TaskCondition(name, r));
            return this;
        }

        public WhenCondition decide(String name, BaseWhenCondition... whenConditions) {
            conditions.add(new DecideCondition(name, whenConditions));
            return this;
        }


        @Override
        List<WorkflowStep.Builder> process(List<WorkflowStep.Builder> prevBuilders, Param param) {
            int a=0;
            List<WorkflowStep.Builder> temp = prevBuilders;
            for(Condition c: conditions) {
                if (a == 0) {
                    if (c instanceof NamedCondition) {
                        for (WorkflowStep.Builder prevBuilder : prevBuilders) {
                            NamedCondition n = (NamedCondition) c;
                            prevBuilder.setNextStep(cond, n.name());
                        }
                    }
                }
                temp = c.process(temp, param);
                a++;
            }
            return temp;
        }
    }
}
