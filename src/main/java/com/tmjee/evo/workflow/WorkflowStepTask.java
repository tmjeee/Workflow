package com.tmjee.evo.workflow;

/**
 * @author tmjee
 */
public class WorkflowStepTask implements WorkflowStep{
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void advance(Input input) {

    }


    public static class Builder {
        public WorkflowStepTask build(String name, TaskRunner r) {
            return new WorkflowStepTask();
        }
    }
}
