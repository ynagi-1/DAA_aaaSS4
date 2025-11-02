package smartcity.graph.topo;

import smartcity.model.Graph;
import smartcity.graph.dagsp.Metrics;
import java.util.*;

public class TopologicalSort implements Metrics {
    private long dfsCount;
    private long edgeRelaxations;
    private long kahnOperations;

    public List<Integer> topologicalSort(Graph graph) {
        reset();
        return dfsTopologicalSort(graph);
    }


    public List<Integer> dfsTopologicalSort(Graph graph) {
        int n = graph.getN();
        boolean[] visited = new boolean[n];
        boolean[] inStack = new boolean[n];
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (!dfsVisit(graph, i, visited, inStack, order)) {
                    throw new IllegalArgumentException("Graph contains cycles - topological sort not possible");
                }
            }
        }

        Collections.reverse(order);
        return order;
    }

    private boolean dfsVisit(Graph graph, int node, boolean[] visited, boolean[] inStack, List<Integer> order) {
        incrementDFSCount();

        if (inStack[node]) {
            return false; // Cycle detected
        }
        if (visited[node]) {
            return true;
        }

        visited[node] = true;
        inStack[node] = true;

        for (var edge : graph.getEdges(node)) {
            incrementEdgeRelaxation();
            int neighbor = edge.getTo();
            if (!dfsVisit(graph, neighbor, visited, inStack, order)) {
                return false;
            }
        }

        inStack[node] = false;
        order.add(node);
        return true;
    }


    public List<Integer> kahnTopologicalSort(Graph graph) {
        reset();
        int n = graph.getN();
        int[] inDegree = new int[n];


        for (int u = 0; u < n; u++) {
            for (var edge : graph.getEdges(u)) {
                incrementEdgeRelaxation();
                inDegree[edge.getTo()]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            incrementKahnOperation();
            int u = queue.poll();
            result.add(u);

            for (var edge : graph.getEdges(u)) {
                incrementEdgeRelaxation();
                int v = edge.getTo();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }


        if (result.size() != n) {
            throw new IllegalArgumentException("Graph has cycles - topological sort not possible. Only processed: " + result.size() + " of " + n + " nodes");
        }

        return result;
    }


    public List<Integer> getTaskOrderFromComponents(List<Integer> componentOrder,
                                                    List<List<Integer>> components) {
        List<Integer> taskOrder = new ArrayList<>();
        for (int compId : componentOrder) {
            List<Integer> componentTasks = components.get(compId);


            if (componentTasks.size() == 1) {
                taskOrder.add(componentTasks.get(0));
            } else {

                taskOrder.addAll(componentTasks);
            }
        }
        return taskOrder;
    }


    public boolean isDAG(Graph graph) {
        try {
            topologicalSort(graph);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public List<List<Integer>> findAllTopologicalOrders(Graph graph) {
        List<List<Integer>> allOrders = new ArrayList<>();
        int n = graph.getN();
        int[] inDegree = new int[n];


        for (int u = 0; u < n; u++) {
            for (var edge : graph.getEdges(u)) {
                inDegree[edge.getTo()]++;
            }
        }

        boolean[] visited = new boolean[n];
        List<Integer> currentOrder = new ArrayList<>();
        findAllTopologicalOrdersUtil(graph, inDegree, visited, currentOrder, allOrders);

        return allOrders;
    }

    private void findAllTopologicalOrdersUtil(Graph graph, int[] inDegree,
                                              boolean[] visited, List<Integer> currentOrder,
                                              List<List<Integer>> allOrders) {
        int n = graph.getN();
        boolean allVisited = true;

        for (int i = 0; i < n; i++) {
            if (!visited[i] && inDegree[i] == 0) {
                visited[i] = true;
                currentOrder.add(i);

                // Decrease in-degree of neighbors
                for (var edge : graph.getEdges(i)) {
                    inDegree[edge.getTo()]--;
                }


                findAllTopologicalOrdersUtil(graph, inDegree, visited, currentOrder, allOrders);


                visited[i] = false;
                currentOrder.remove(currentOrder.size() - 1);
                for (var edge : graph.getEdges(i)) {
                    inDegree[edge.getTo()]++;
                }

                allVisited = false;
            }
        }

        if (allVisited) {
            allOrders.add(new ArrayList<>(currentOrder));
        }
    }


    public int[] getVertexLevels(Graph graph) {
        List<Integer> topoOrder = topologicalSort(graph);
        int n = graph.getN();
        int[] levels = new int[n];
        Arrays.fill(levels, 0);


        for (int u : topoOrder) {
            for (var edge : graph.getEdges(u)) {
                int v = edge.getTo();
                levels[v] = Math.max(levels[v], levels[u] + 1);
            }
        }

        return levels;
    }


    public List<Integer> findSources(Graph graph) {
        int n = graph.getN();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (var edge : graph.getEdges(u)) {
                inDegree[edge.getTo()]++;
            }
        }

        List<Integer> sources = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                sources.add(i);
            }
        }
        return sources;
    }


    public List<Integer> findSinks(Graph graph) {
        int n = graph.getN();
        List<Integer> sinks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (graph.getEdges(i).isEmpty()) {
                sinks.add(i);
            }
        }
        return sinks;
    }


    @Override
    public void incrementDFSCount() {
        dfsCount++;
    }
    @Override
    public void incrementEdgeRelaxation() {
        edgeRelaxations++;
    }
    @Override
    public void incrementKahnOperation() {
        kahnOperations++;
    }
    @Override
    public long getDFSCount() {
        return dfsCount;
    }
    @Override
    public long getEdgeRelaxations() {
        return edgeRelaxations;
    }
    @Override
    public long getKahnOperations() {
        return kahnOperations;
    }
    @Override
    public void reset() {
        dfsCount = 0;
        edgeRelaxations = 0;
        kahnOperations = 0;
    }


    public void compareAlgorithms(Graph graph) {
        System.out.println("=== Topological Sort Algorithm Comparison ===");



        long startTime = System.nanoTime();
        List<Integer> dfsResult = dfsTopologicalSort(graph);
        long dfsTime = System.nanoTime() - startTime;

        System.out.println("DFS Algorithm:");
        System.out.println("  Time: " + dfsTime + " ns");
        System.out.println("  DFS visits: " + getDFSCount());
        System.out.println("  Edge relaxations: " + getEdgeRelaxations());
        System.out.println("  Result: " + dfsResult.subList(0, Math.min(10, dfsResult.size())) +
                (dfsResult.size() > 10 ? "..." : ""));

        reset();



        startTime = System.nanoTime();
        List<Integer> kahnResult = kahnTopologicalSort(graph);
        long kahnTime = System.nanoTime() - startTime;

        System.out.println("Kahn's Algorithm:");
        System.out.println("  Time: " + kahnTime + " ns");
        System.out.println("  Kahn operations: " + getKahnOperations());
        System.out.println("  Edge relaxations: " + getEdgeRelaxations());
        System.out.println("  Result: " + kahnResult.subList(0, Math.min(10, kahnResult.size())) +
                (kahnResult.size() > 10 ? "..." : ""));



        System.out.println("Validation: " + (isValidTopologicalOrder(graph, dfsResult) &&
                isValidTopologicalOrder(graph, kahnResult) ?
                "PASSED" : "FAILED"));
    }


    public boolean isValidTopologicalOrder(Graph graph, List<Integer> order) {
        int n = graph.getN();
        int[] position = new int[n];



        for (int i = 0; i < order.size(); i++) {
            position[order.get(i)] = i;
        }


        for (int u = 0; u < n; u++) {
            for (var edge : graph.getEdges(u)) {
                int v = edge.getTo();
                if (position[u] >= position[v]) {
                    return false;
                }
            }
        }

        return true;
    }
}