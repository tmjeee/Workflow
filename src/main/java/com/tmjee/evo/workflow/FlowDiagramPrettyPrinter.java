package com.tmjee.evo.workflow;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author tmjee
 */
public class FlowDiagramPrettyPrinter {

    private final Map<String, WorkflowStep> m;

    public FlowDiagramPrettyPrinter(Map<String, WorkflowStep> m) {
        this.m = m;
    }

    int lane =0;
    int i=0;
    Map<String, Node> nodesMap;
    Set<Integer> s = new TreeSet<>();

    public void print() {
        lane = 0;
        i = 0;
        List<WorkflowStep> workflowSteps = new ArrayList<>(m.values());

        nodesMap = new LinkedHashMap<>();

        for (WorkflowStep step : workflowSteps) {
            String name = step.getName();
            WorkflowStep.Type type = step.getType();

            step.accept(new WorkflowStep.Visitor() {
                @Override
                public void setNextStep(String workflowStepName) {
                    Path p = new Path(lane++, null);
                    addOutgoingPath(p, name);
                    addIncommingPath(p, workflowStepName);
                }

                @Override
                public void setNextStep(String cond, String workflowStepName) {
                    Path p = new Path(lane++, cond);
                    addOutgoingPath(p, name);
                    addIncommingPath(p, workflowStepName);
                }
            });
        }

        for (Node n : nodesMap.values()) {
            n.prettyPrint();
        }
    }

    private void addOutgoingPath(Path p, String workflowStepName) {
        findNode(workflowStepName, m.get(workflowStepName).getType()).addOutgoing(p);
    }

    private void addIncommingPath(Path p, String workflowStepName) {
        findNode(workflowStepName, m.get(workflowStepName).getType()).addIncomming(p);
    }

    private Node findNode(String workflowStepName, WorkflowStep.Type type) {
        if (!nodesMap.containsKey(workflowStepName)) {
            Node n = new Node(i++, workflowStepName, type);
            nodesMap.put(workflowStepName, n);
        }
        return nodesMap.get(workflowStepName);
    }


    class Node {
        int i;
        String name;
        WorkflowStep.Type type;
        List<Path> incommings;
        List<Path> outgoings;

        Node(int i, String name, WorkflowStep.Type type) {
            this.i = i;
            this.name = name;
            this.type = type;
            incommings = new ArrayList<>();
            outgoings = new ArrayList<>();
        }
        void addIncomming(Path p){
            incommings.add(p);
        }
        void addOutgoing(Path p) {
            outgoings.add(p);
        }
        void prettyPrint() {
            switch(type) {
                case TASK:
                    System.out.println(format("  +-------+ "   ));prettyPrint_Path();
                    System.out.print(  format("  |       | "   ));prettyPrint_OutcommingPath();
                    System.out.println(format("  |  %s   | ", i));prettyPrint_Path();
                    System.out.println(format("  |       | "   ));prettyPrint_IncommingPath();
                    System.out.println(format("  +-------+ "   ));prettyPrint_Path();
                                                                  prettyPrint_Path();
                                                                  prettyPrint_Path();
                    break;
                case DECISION:
                    System.out.println(format("   -----    "   ));prettyPrint_Path();
                    System.out.print(  format("  /     \\  "   ));prettyPrint_OutcommingPath();
                    System.out.println(format(" /   %s  \\ ", i));prettyPrint_Path();
                    System.out.println(format(" \\      /  "   ));prettyPrint_IncommingPath();
                    System.out.println(format("  \\    /   "   ));prettyPrint_Path();
                    System.out.println(format("    ---     "   ));prettyPrint_Path();
                                                                  prettyPrint_Path();
                    break;
            }

        }
        void prettyPrint_IncommingPath() {
            Set<Integer> o = incommings.stream().mapToInt((p) -> p.lane).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
            System.out.print("-<---");
            prettyPrint_commonPath(o);
        }

        void prettyPrint_OutcommingPath() {
            Set<Integer> o = incommings.stream().mapToInt((p) -> p.lane).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
            System.out.print("->---");
            prettyPrint_commonPath(o);
        }

        void prettyPrint_commonPath(Set<Integer> o) {
            for (int a = s.stream().max(Comparator.naturalOrder()).get(); a >= 0; a--) {
                if (s.contains(a)) {
                    System.out.print("--n--");
                } else if (o.contains(a)) {
                    System.out.print("--+--");
                } else {
                    System.out.print("-----");
                }
            }
        }

        void prettyPrint_Path() {
            System.out.print("     ");
            for (int a = s.stream().max(Comparator.naturalOrder()).get(); a >= 0; a--) {
                if (s.contains(a)) {
                    System.out.print(format("  |  "));
                } else {
                    System.out.print(format("     "));
                }
            }
        }
    }

    static class Path {
        int lane;
        String description;

        Path(int lane, String description) {
            this.lane = lane;
            this.description =description;
        }
    }
}
