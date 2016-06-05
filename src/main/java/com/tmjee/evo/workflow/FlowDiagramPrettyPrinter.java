package com.tmjee.evo.workflow;

import java.util.*;
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

    int laneIdCount =0; // keep count of Lane's id
    int nodeIdCount =0; // keep count of Node's id
    Map<String, Node> nodesMap;
    Set<Integer> allActiveLanes = new TreeSet<>();  // all active lanes

    public void print() {
        laneIdCount = 0;
        nodeIdCount = 0;

        List<WorkflowStep> workflowSteps = new ArrayList<>(m.values());
        List<String> workflowStepNames = m.values().stream().map((s)->s.getName()).collect(Collectors.toCollection(ArrayList<String>::new));

        nodesMap = new LinkedHashMap<>();

        for (WorkflowStep step : workflowSteps) {
            String name = step.getName();

            // accepts a Visitor, tell Visitor the next steps you have
            step.accept(new WorkflowStep.Visitor() {
                @Override
                public void setNextStep(String workflowStepName) {

                    int source = workflowStepNames.indexOf(name);
                    int dest = workflowStepNames.indexOf(workflowStepName);

                    int laneId = laneIdCount++;
                    Path p = new Path(laneId, null, (source<dest? Path.Direction.DOWN: Path.Direction.UP));
                    addOutgoingPath(p, name);
                    p = new Path(laneId, null, (dest<source?Path.Direction.DOWN:Path.Direction.UP));
                    addIncommingPath(p, workflowStepName);
                }

                @Override
                public void setNextStep(String cond, String workflowStepName) {

                    int source = workflowStepNames.indexOf(name);
                    int dest = workflowStepNames.indexOf(workflowStepName);

                    int laneId = laneIdCount++;
                    Path p = new Path(laneId, cond, (source<dest?Path.Direction.DOWN:Path.Direction.UP));
                    addOutgoingPath(p, name);
                    p = new Path(laneId, cond, (dest<source?Path.Direction.DOWN:Path.Direction.UP));
                    addIncommingPath(p, workflowStepName);
                }
            });
        }

        for (Node n : nodesMap.values()) {
            n.prettyPrint();
        }

        System.out.println("Legend:");
        for (Node n : nodesMap.values()) {
           System.out.println(format("\t%s - %s", n.i, n.name));
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
            Node n = new Node(nodeIdCount++, workflowStepName, type);
            nodesMap.put(workflowStepName, n);
        }
        return nodesMap.get(workflowStepName);
    }

    class Node {
        int i;  // id of current node (sequential)
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
                    //addOutgoingPaths(this);
                    System.out.print(format("  +-------+ "   ));prettyPrint_Path();printLine();
                    System.out.print(format("  |       | "   )); prettyPrint_OutgoingPath();printLine();
                    System.out.print(format("  |  %s    | ", i));prettyPrint_Path();printLine();
                    System.out.print(format("  |       | "   ));prettyPrint_IncommingPath();printLine();
                    System.out.print(format("  +-------+ "   ));prettyPrint_Path();printLine();
                    System.out.print(format("            "   ));prettyPrint_Path();printLine();
                    System.out.print(format("            "   ));prettyPrint_Path();printLine();
                    break;
                case DECISION:
                    System.out.print(format("    ----    "   ));prettyPrint_Path();printLine();
                    System.out.print(format("  /     \\   "   )); prettyPrint_OutgoingPath();printLine();
                    System.out.print(format(" /   %s   \\  ", i));prettyPrint_Path();printLine();
                    System.out.print(format(" \\       /  "   ));prettyPrint_IncommingPath();printLine();
                    System.out.print(format("  \\     /   "   ));prettyPrint_Path();printLine();
                    System.out.print(format("    ----    "   ));prettyPrint_Path();printLine();
                    System.out.print(format("            "   ));prettyPrint_Path();printLine();
                    break;
            }
        }
        void prettyPrint_IncommingPath() {
            Set<Integer> o = incommings.stream().mapToInt((p) -> p.lane).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);

            if (o.isEmpty()) {
                prettyPrint_Path();
                return;
            }

            int max = Math.max(
                allActiveLanes.stream().max(Comparator.naturalOrder()).orElse(-1),
                o.stream().max(Comparator.naturalOrder()).orElse(-1));

            Map<Integer, Path> m = incommings.stream().collect(Collectors.toMap((p)->p.lane, (p)->p));

            System.out.print("-<-");
            for (int a=0; a<=max;a++) {
                if (o.contains(a)) {
                    System.out.print("--+");
                    if (m.get(a).direction == Path.Direction.UP){
                        allActiveLanes.remove(a);
                    } else {
                        allActiveLanes.add(a);
                    }
                } else if (!o.isEmpty() && allActiveLanes.contains(a)) {
                    System.out.print("-\\/");
                } else if (o.isEmpty() && allActiveLanes.contains(a)) {
                    System.out.print("  |");
                } else if (!o.isEmpty()) {
                    System.out.print("---");
                } else {
                    System.out.print("   ");
                }
            }
        }

        void prettyPrint_OutgoingPath() {
            Set<Integer> o = outgoings.stream().mapToInt((p) -> p.lane).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
            if (o.isEmpty()) {
                prettyPrint_Path();
                return;
            }

            int max = Math.max(
                allActiveLanes.stream().max(Comparator.naturalOrder()).orElse(-1),
                o.stream().max(Comparator.naturalOrder()).orElse(-1));

            Map<Integer, Path> m = outgoings.stream().collect(Collectors.toMap((p)->p.lane, (p)->p));

            System.out.print("->-");

            for (int a=0; a<=max;a++) {
                if (o.contains(a)) { // outgoing occupying this lane
                    System.out.print("--+");
                    if (m.get(a).direction == Path.Direction.UP) {
                        allActiveLanes.remove(a);
                    } else {
                        allActiveLanes.add(a);
                    }
                } else if (!o.isEmpty() && allActiveLanes.contains(a)) { // has outgoing, this lane occupied by others
                    System.out.print("-/\\");
                } else if (o.isEmpty() && allActiveLanes.contains(a)) { // no outgoing occupying this lane, others are using this lane
                    System.out.print("  |");
                } else if (!o.isEmpty()) {  // only outgoing occupying this lane
                    System.out.print("---");
                } else {
                    System.out.print("   ");
                }
            }
        }

        void prettyPrint_Path() {
            System.out.print("   ");
            int max = allActiveLanes.stream().max(Comparator.naturalOrder()).orElse(-1);
            for (int a=0; a<=max; a++) {
                if (allActiveLanes.contains(a)) {
                    System.out.print(format("  |"));
                } else {
                    System.out.print(format("   "));
                }
            }
        }

        void printLine() {
            System.out.println();
        }
    }

    static class Path {
        enum Direction {
            UP,DOWN
        };
        int lane;
        String description;
        Direction direction;

        Path(int lane, String description, Direction direction) {
            this.lane = lane;
            this.description =description;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path = (Path) o;
            return lane == path.lane;
        }

        @Override
        public int hashCode() {
            return Objects.hash(lane);
        }
    }
}
